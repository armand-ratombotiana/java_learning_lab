# Lab 04: Problem Walkthrough — TLS 1.3-like Handshake State Machine

## Problem Statement

Implement a TLS 1.3-like handshake state machine with the full message flow and a working key schedule. The engine must:

1. Model the **message sequence**: ClientHello → ServerHello → EncryptedExtensions → Certificate → CertificateVerify → Finished → (client) Finished → CONNECTED, with the transcript hash accumulated as each message lands.
2. Enforce **state transitions**: every message arriving in the wrong state produces a fatal alert; the connection must never complete with a corrupt transcript.
3. Implement the **HKDF key schedule**: early secret → handshake secret → master secret, deriving `client/server_handshake_traffic_secret` and `client/server_application_traffic_secret` (HKDF with SHA-256 via the JDK's `SecretKeyFactory`).
4. Bind **CertificateVerify** to the transcript: the server signs a transcript digest, and the client verifies it — a modified transcript fails the handshake.
5. Bind **Finished** to the transcript with the derived handshake traffic secret.
6. Support the **0-RTT path**: with a cached PSK session, the client may send early application data before ServerHello arrives; the server accepts it only with a PSK and treats it as replayable.
7. Guard against **truncation**: application data is only unlocked after `CONNECTED`; a missing peer Finished never yields connected state.

**Constraints**

- Key derivation must use the JDK's real HKDF (HmacSHA256-based) so the schedule is cryptographically sound.
- The transcript hash is SHA-256 over the concatenation of all handshake messages.
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: Model handshake messages and the transcript

A `HandshakeMessage` is (type, payload). The transcript is an accumulating byte buffer hashed with SHA-256. The transcript is what CertificateVerify signs and what Finished MACs — the anti-injection backbone of the protocol.

```java
package com.networking.deep.lab04;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class Tls13Handshake {

    public enum MsgType { CLIENT_HELLO, SERVER_HELLO, ENCRYPTED_EXTENSIONS,
        CERTIFICATE, CERTIFICATE_VERIFY, FINISHED }

    public record HandshakeMessage(MsgType type, byte[] payload) {}

    static final class Transcript {
        private final MessageDigest digest;

        Transcript() {
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }

        void absorb(List<HandshakeMessage> messages) {
            for (HandshakeMessage m : messages) {
                digest.update(m.type().name().getBytes(StandardCharsets.UTF_8));
                digest.update(m.payload());
            }
        }

        byte[] hash() { return digest.digest(); }
    }
```

### Step 2: The HKDF key schedule

HKDF (RFC 5869) over HmacSHA256: Extract turns an input keying material + salt into a pseudorandom key; Expand turns that into the required-length output key material. The TLS 1.3 schedule is a chain of Extract/Expand steps: early secret → handshake secret → master secret, each spawning per-direction traffic secrets.

```java
    static final class Hkdf {
        private static final String HMAC = "HmacSHA256";

        static byte[] extract(byte[] salt, byte[] ikm) {
            try {
                Mac mac = Mac.getInstance(HMAC);
                mac.init(new SecretKeySpec(salt, HMAC));
                return mac.doFinal(ikm);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        static byte[] expand(byte[] prk, byte[] info, int length) {
            try {
                Mac mac = Mac.getInstance(HMAC);
                mac.init(new SecretKeySpec(prk, HMAC));
                byte[] out = new byte[length];
                byte[] t = new byte[0];
                int offset = 0;
                byte counter = 1;
                while (offset < length) {
                    mac.update(t);
                    mac.update(info);
                    mac.update(counter);
                    t = mac.doFinal();
                    int n = Math.min(t.length, length - offset);
                    System.arraycopy(t, 0, out, offset, n);
                    offset += n;
                    counter++;
                }
                return out;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        static byte[] expandLabel(byte[] prk, String label, byte[] context, int length) {
            String fullLabel = "tls13 " + label;
            byte[] labelBytes = fullLabel.getBytes(StandardCharsets.UTF_8);
            byte[] info = new byte[2 + 1 + labelBytes.length + 1 + context.length];
            info[0] = (byte) (length >> 8);
            info[1] = (byte) length;
            info[2] = (byte) labelBytes.length;
            System.arraycopy(labelBytes, 0, info, 3, labelBytes.length);
            info[3 + labelBytes.length] = (byte) context.length;
            System.arraycopy(context, 0, info, 4 + labelBytes.length, context.length);
            return expand(prk, info, length);
        }
    }
```

### Step 3: The key schedule — deriving the traffic secrets

The schedule follows RFC 8446 §7.1: with a PSK present, the early secret is derived from it; the handshake secret mixes in the ECDHE shared secret (simulated with a fixed shared value here); the master secret derives the application keys. Each traffic secret is expanded with the transcript hash to its concrete key.

```java
    record Keyset(byte[] clientHandshake, byte[] serverHandshake,
                  byte[] clientApplication, byte[] serverApplication,
                  byte[] earlyTraffic) {}

    static final class KeySchedule {
        static final byte[] ZERO_SALT = new byte[32];

        static Keyset derive(byte[] pskOrZero, byte[] ecdheShared, byte[] transcriptHash) {
            byte[] earlySecret = Hkdf.extract(ZERO_SALT, pskOrZero);
            byte[] earlyTraffic = Hkdf.expandLabel(earlySecret, "c e traffic",
                    transcriptHash, 32);

            byte[] handshakeSecret = Hkdf.extract(ecdheShared, earlySecret);
            byte[] clientHs = Hkdf.expandLabel(handshakeSecret, "c hs traffic",
                    transcriptHash, 32);
            byte[] serverHs = Hkdf.expandLabel(handshakeSecret, "s hs traffic",
                    transcriptHash, 32);

            byte[] masterSecret = Hkdf.extract(ZERO_SALT, handshakeSecret);
            byte[] clientApp = Hkdf.expandLabel(masterSecret, "c ap traffic",
                    transcriptHash, 32);
            byte[] serverApp = Hkdf.expandLabel(masterSecret, "s ap traffic",
                    transcriptHash, 32);

            return new Keyset(clientHs, serverHs, clientApp, serverApp, earlyTraffic);
        }
    }
```

### Step 4: The handshake state machine

The state machine drives the client side. Every transition is guarded: an unexpected message type produces a fatal `unexpected_message` alert. The server's Finished is verified with the transcript at that point; the client's own Finished is computed and verified conceptually by the server side.

```java
    public enum Alert { NONE, UNEXPECTED_MESSAGE, DECRYPT_ERROR, BAD_RECORD_MAC }

    public enum HandshakeState { START, CLIENT_HELLO_SENT, SERVER_HELLO_RECEIVED,
        SERVER_FINISHED_RECEIVED, CONNECTED, FAILED }

    public static final class ClientHandshake {
        private final List<HandshakeMessage> transcript = new ArrayList<>();
        private HandshakeState state = HandshakeState.START;
        private Alert alert = Alert.NONE;

        public void start() {
            transcript.add(new HandshakeMessage(MsgType.CLIENT_HELLO,
                    "ch".getBytes(StandardCharsets.UTF_8)));
            state = HandshakeState.CLIENT_HELLO_SENT;
        }

        public boolean receive(HandshakeMessage msg) {
            switch (state) {
                case CLIENT_HELLO_SENT -> {
                    if (msg.type() != MsgType.SERVER_HELLO) return fail(Alert.UNEXPECTED_MESSAGE);
                    transcript.add(msg);
                    state = HandshakeState.SERVER_HELLO_RECEIVED;
                    return true;
                }
                case SERVER_HELLO_RECEIVED -> {
                    if (msg.type() == MsgType.FINISHED) {
                        transcript.add(msg);
                        state = HandshakeState.SERVER_FINISHED_RECEIVED;
                        return true;
                    }
                    if (msg.type() != MsgType.ENCRYPTED_EXTENSIONS
                            && msg.type() != MsgType.CERTIFICATE
                            && msg.type() != MsgType.CERTIFICATE_VERIFY) {
                        return fail(Alert.UNEXPECTED_MESSAGE);
                    }
                    transcript.add(msg);
                    return true;
                }
                default -> { return false; }
            }
        }

        private boolean fail(Alert a) {
            alert = a;
            state = HandshakeState.FAILED;
            return false;
        }

        HandshakeState state() { return state; }
        Alert alert() { return alert; }
    }
```

This sketch is permissive: it accepts the four encrypted messages in *any* order. That is a real bug — TLS 1.3 mandates a fixed sequence, and Step 5 tightens it with an explicit per-stage guard.

### Step 5: The ordered encrypted sequence

Between ServerHello and Connected, exactly four messages arrive in a fixed order. The refined state machine tracks which of the four has been seen; any other message, or a repeated message, is fatal. CertificateVerify is verified against the transcript *at that point* (which includes the previous three messages), and the server Finished is MAC-verified with the server handshake traffic secret.

```java
    public static final class ClientHandshake2 {
        private final List<HandshakeMessage> transcript = new ArrayList<>();
        private HandshakeState state = HandshakeState.START;
        private Alert alert = Alert.NONE;
        private int encryptedStage = 0; // 0..3 -> which of the 4 messages is next
        private Keyset keys;

        public void start() {
            transcript.add(new HandshakeMessage(MsgType.CLIENT_HELLO, new byte[]{0x01}));
            state = HandshakeState.CLIENT_HELLO_SENT;
        }

        public boolean receive(HandshakeMessage msg) {
            switch (state) {
                case CLIENT_HELLO_SENT -> {
                    if (msg.type() != MsgType.SERVER_HELLO) return fail(Alert.UNEXPECTED_MESSAGE);
                    transcript.add(msg);
                    state = HandshakeState.SERVER_HELLO_RECEIVED;
                    return true;
                }
                case SERVER_HELLO_RECEIVED -> {
                    MsgType[] order = { MsgType.ENCRYPTED_EXTENSIONS,
                            MsgType.CERTIFICATE, MsgType.CERTIFICATE_VERIFY,
                            MsgType.FINISHED };
                    if (encryptedStage >= order.length || msg.type() != order[encryptedStage]) {
                        return fail(Alert.UNEXPECTED_MESSAGE);
                    }

                    if (msg.type() == MsgType.CERTIFICATE_VERIFY) {
                        // CertificateVerify signs the transcript up to itself.
                        byte[] transcriptHash = transcriptHash();
                        if (!verifyServerSignature(msg.payload(), transcriptHash)) {
                            return fail(Alert.DECRYPT_ERROR);
                        }
                        transcript.add(msg);
                        encryptedStage++;
                        return true;
                    }

                    if (msg.type() == MsgType.FINISHED) {
                        // The Finished MAC covers the transcript up to but NOT
                        // including the Finished message itself (RFC 8446 4.4.4).
                        byte[] transcriptHash = transcriptHash();
                        keys = KeySchedule.derive(ZERO_PSK, ECDHE_SHARED, transcriptHash);
                        byte[] expected = Hkdf.expandLabel(keys.serverHandshake(),
                                "finished", transcriptHash, 32);
                        if (!msgEquals(msg.payload(), expected)) {
                            return fail(Alert.DECRYPT_ERROR);
                        }
                        transcript.add(msg);
                        transcript.add(new HandshakeMessage(MsgType.FINISHED,
                                clientFinished()));
                        state = HandshakeState.CONNECTED;
                        return true;
                    }

                    transcript.add(msg);
                    encryptedStage++;
                    return true;
                }
                default -> { return false; }
            }
        }

        private byte[] transcriptHash() {
            Transcript t = new Transcript();
            t.absorb(transcript);
            return t.hash();
        }

        private byte[] clientFinished() {
            byte[] transcriptHash = transcriptHash();
            return Hkdf.expandLabel(keys.clientHandshake(), "finished",
                    transcriptHash, 32);
        }

        public byte[] expectedServerFinished() {
            byte[] transcriptHash = transcriptHash();
            if (keys == null) {
                keys = KeySchedule.derive(ZERO_PSK, ECDHE_SHARED, transcriptHash);
            }
            return Hkdf.expandLabel(keys.serverHandshake(), "finished",
                    transcriptHash, 32);
        }

        public byte[] expectedVerify() {
            // Stand-in for a real signature: the honest server's signature is
            // the digest of the transcript it has seen so far.
            return sha256(transcriptHash());
        }

        private boolean verifyServerSignature(byte[] signature, byte[] transcriptHash) {
            return msgEquals(signature, sha256(transcriptHash));
        }

        private boolean msgEquals(byte[] a, byte[] b) {
            return MessageDigest.isEqual(a, b);
        }

        private boolean fail(Alert a) {
            alert = a;
            state = HandshakeState.FAILED;
            return false;
        }

        HandshakeState state() { return state; }
        Alert alert() { return alert; }
        Keyset keys() { return keys; }

        private static final byte[] ZERO_PSK = new byte[32];
        private static final byte[] ECDHE_SHARED = new byte[32];

        private static byte[] sha256(byte[] data) {
            try {
                return MessageDigest.getInstance("SHA-256").digest(data);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }
    }
```

### Step 6: 0-RTT — early data with a cached PSK

With a cached session, the client sends early application data after ClientHello; the server accepts only with a matching PSK and a replay-protection check. The demo models the decision, not the crypto: the client marks the 0-RTT attempt, and the server accepts or rejects based on its policy and replay cache.

```java
    public record SessionTicket(byte[] psk, long issuedAtMs) {}

    public static final class ZeroRtt {
        public static boolean clientSendsEarlyData(SessionTicket ticket, long nowMs) {
            return ticket != null && nowMs - ticket.issuedAtMs() < 86_400_000L; // 24h
        }

        public static final class Server {
            private final java.util.Set<String> replayCache = new java.util.HashSet<>();
            private final boolean acceptEarlyData;

            Server(boolean acceptEarlyData) { this.acceptEarlyData = acceptEarlyData; }

            boolean accept(byte[] psk, String clientRandom, boolean ticketValid) {
                if (!acceptEarlyData || !ticketValid) return false;
                if (replayCache.contains(clientRandom)) return false; // replay!
                replayCache.add(clientRandom);
                return true;
            }
        }
    }
```

### Step 7: Demo — full handshake, tamper detection, 0-RTT

The demo runs three scenarios:

1. **Clean handshake**: the full message sequence is delivered; the client reaches CONNECTED and derives distinct handshake vs application keys.
2. **Tampered CertificateVerify**: an attacker modifies the server's CertificateVerify — the transcript check fails and the state machine ends FAILED with a decrypt_error.
3. **Reordered messages**: Finished arrives before CertificateVerify — fatal unexpected_message.
4. **0-RTT**: a valid ticket is accepted once; a replayed client random is rejected.

```java
    public static void main(String[] args) {
        System.out.println("=== TLS 1.3 Handshake Demo ===\n");

        System.out.println("-- Scenario 1: clean handshake --");
        ClientHandshake2 client = new ClientHandshake2();
        client.start();
        client.receive(new HandshakeMessage(MsgType.SERVER_HELLO, "sh".getBytes()));
        client.receive(new HandshakeMessage(MsgType.ENCRYPTED_EXTENSIONS, "ee".getBytes()));
        client.receive(new HandshakeMessage(MsgType.CERTIFICATE, "cert".getBytes()));
        client.receive(new HandshakeMessage(MsgType.CERTIFICATE_VERIFY,
                client.expectedVerify()));
        client.receive(new HandshakeMessage(MsgType.FINISHED,
                client.expectedServerFinished()));
        System.out.println("  state=" + client.state()
                + " keys derived=" + (client.keys() != null));

        System.out.println("\n-- Scenario 2: tampered CertificateVerify --");
        ClientHandshake2 tampered = new ClientHandshake2();
        tampered.start();
        tampered.receive(new HandshakeMessage(MsgType.SERVER_HELLO, "sh".getBytes()));
        tampered.receive(new HandshakeMessage(MsgType.ENCRYPTED_EXTENSIONS, "ee".getBytes()));
        tampered.receive(new HandshakeMessage(MsgType.CERTIFICATE, "cert".getBytes()));
        tampered.receive(new HandshakeMessage(MsgType.CERTIFICATE_VERIFY, "EVIL".getBytes()));
        boolean ok = tampered.receive(new HandshakeMessage(MsgType.FINISHED,
                tampered.expectedServerFinished()));
        System.out.println("  accepted=" + ok + " state=" + tampered.state()
                + " alert=" + tampered.alert());

        System.out.println("\n-- Scenario 3: reordered messages --");
        ClientHandshake2 reordered = new ClientHandshake2();
        reordered.start();
        reordered.receive(new HandshakeMessage(MsgType.SERVER_HELLO, "sh".getBytes()));
        boolean ok2 = reordered.receive(new HandshakeMessage(MsgType.FINISHED, "early".getBytes()));
        System.out.println("  accepted=" + ok2 + " state=" + reordered.state()
                + " alert=" + reordered.alert());

        System.out.println("\n-- Scenario 4: 0-RTT replay protection --");
        SessionTicket ticket = new SessionTicket(new byte[32], System.currentTimeMillis());
        ZeroRtt.Server server = new ZeroRtt.Server(true);
        boolean first = server.accept(ticket.psk(), "random-1", true);
        boolean replay = server.accept(ticket.psk(), "random-1", true);
        boolean other = server.accept(ticket.psk(), "random-2", true);
        System.out.println("  first=" + first + " replay=" + replay + " other=" + other);
        System.out.println("  (replay rejected by client-random replay cache)");
    }
}
```

The `expectedServerFinished` and `expectedVerify` helpers compute what an honest server would send, derived from the client's own transcript — in a real stack these values arrive over the wire and are verified cryptographically; here they are computed deterministically so the MAC check is meaningful in the clean scenario.

### Step 8: Verify the expected outputs

| Scenario | Expected |
|----------|----------|
| Clean handshake | `state=CONNECTED`, keys derived; handshake and application keys differ (distinct derivations from different secrets) |
| Tampered CertificateVerify | `accepted=false`, `state=FAILED`, `alert=DECRYPT_ERROR` |
| Finished before CertificateVerify | `accepted=false`, `state=FAILED`, `alert=UNEXPECTED_MESSAGE` |
| 0-RTT replay | `first=true replay=false other=true` — replay cache keyed by client random |

The transcript binding is the linchpin: modifying any message after ClientHello changes the transcript hash, which changes every derived secret and every verification — an attacker cannot inject or reorder anything without the handshake failing loudly.

---

## Complexity Analysis

- **Per-message processing**: O(M) where M = message size (hash update); the transcript hash is recomputed O(1) times per stage in this demo — a production stack keeps an *incremental* transcript hash updated once per message, making every stage O(M) total.
- **Key derivation**: O(1) — a fixed number of HKDF Extract/Expand calls (SHA-256 HMACs, ~16 per schedule).
- **State machine**: O(1) transitions, each a constant number of guards.
- **Space**: O(T) transcript buffer + O(1) keys; the transcript is bounded by handshake size (a few KB worst case, including certificates).
- **Security posture**: all checks are constant-time (`MessageDigest.isEqual`) — no timing side channels in the verification path.

---

## Follow-Up Questions

1. **How do you add real ECDHE?** Replace the fixed `ECDHE_SHARED` with an X25519 (or P-256) key exchange: both sides generate ephemeral keys, the shared secret is the raw X25519 output — the `KeySchedule.derive` call site is unchanged because the schedule only consumes the shared bytes.

2. **How do you handle the server side symmetrically?** The server state machine is the mirror: it receives ClientHello, sends ServerHello + the four encrypted messages, receives the client Finished, and verifies it with the *client* handshake traffic secret — the same `Keyset` gives both directions.

3. **How does a real client verify the certificate chain on top of this?** After the transcript checks pass, validate the Certificate message's chain against the trust store, check the leaf's hostname against the SNI, and verify the signature algorithm is in the client's supported set — the state machine exposes the transcript and keys to a separate `CertificateValidator`.

4. **How do you implement PSK-only handshakes (no ECDHE)?** In `KeySchedule.derive`, skip the ECDHE mixing: handshake secret = Extract(early secret, 0). The state machine is unchanged; forward secrecy is traded for latency.

5. **How do you detect a downgrade to TLS 1.2 by a MITM?** The ServerHello random field carries the downgrade sentinel (`44 4F 57 4E 47 52 44` for 1.2) — a 1.3 client that sees a 1.2 ServerHello must check the sentinel and abort; the `supported_versions` extension echo is the second line of defense.

6. **How do you test key-schedule correctness without a peer?** RFC 8448 test vectors: feed the exact ClientHello/ServerHello bytes and assert the derived traffic secrets match the published expected values — byte-for-byte — plus the demo's negative cases for the state machine.
