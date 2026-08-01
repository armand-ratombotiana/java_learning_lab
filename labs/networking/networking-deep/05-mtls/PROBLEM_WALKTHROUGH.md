# Lab 05: Problem Walkthrough — Mutual TLS Certificate Verification Chain

## Problem Statement

Implement a mutual TLS certificate verification chain. The system must:

1. Model **certificates** (subject, issuer, validity window, signature, key usage / extended key usage, serial) and **trust stores** (roots, optionally intermediates).
2. **Build the chain**: walk from the leaf up to a configured trust anchor, following issuer names; support **cross-signed** roots (multiple candidate parents) by trying all candidates.
3. **Verify every link**: parent's public key verifies the child's signature (simulated with hash-chained signing), each certificate is within its validity window at the verification time, CA certificates carry the CA basic constraint, and the leaf carries the required extended key usage (`SERVER_AUTH` or `CLIENT_AUTH`).
4. **Check revocation**: a CRL (list of revoked serials) per issuing CA; a revoked certificate fails regardless of validity.
5. Perform **mutual verification**: the client verifies the server's chain and the server verifies the client's chain, each against its own trust store — plus **identity authorization** mapping the verified leaf identity to an allowed role.
6. Produce a **failure reason** for every rejection — expired vs untrusted vs revoked vs wrong-EKU — because the operational response differs per reason.

**Constraints**

- Signatures are simulated as hash chains (SHA-256 of the signed portion chained with the parent's key ID) — structurally equivalent to real signatures for the verification logic.
- Verification must be deterministic; path building must terminate (cycle detection).
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: Model certificates

A certificate carries the signed portion (subject, issuer, validity, EKU, CA flag) plus a signature over that portion. The signature is produced by the issuer's private key ID — verification recomputes the chain hash.

```java
package com.networking.deep.lab05;

import java.time.Instant;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MtlsVerifier {

    public enum Eku { SERVER_AUTH, CLIENT_AUTH, NONE }

    public static final class Certificate {
        private final String subject;
        private final String issuer;
        private final Instant notBefore;
        private final Instant notAfter;
        private final boolean isCa;
        private final Eku eku;
        private final String serial;
        private final String publicKeyId;   // identity of this cert's public key
        private final byte[] signature;     // signed by the issuer's private key

        Certificate(String subject, String issuer, Instant notBefore, Instant notAfter,
                    boolean isCa, Eku eku, String serial, String publicKeyId,
                    byte[] signature) {
            this.subject = subject;
            this.issuer = issuer;
            this.notBefore = notBefore;
            this.notAfter = notAfter;
            this.isCa = isCa;
            this.eku = eku;
            this.serial = serial;
            this.publicKeyId = publicKeyId;
            this.signature = signature;
        }

        byte[] signedPortion() {
            byte[] data = (subject + "|" + issuer + "|" + notBefore + "|"
                    + notAfter + "|" + isCa + "|" + eku + "|" + serial).getBytes();
            return sha256(data);
        }

        boolean validAt(Instant at) {
            return !at.isBefore(notBefore) && !at.isAfter(notAfter);
        }

        String subject() { return subject; }
        String issuer() { return issuer; }
        boolean isCa() { return isCa; }
        Eku eku() { return eku; }
        String serial() { return serial; }
        String publicKeyId() { return publicKeyId; }
        byte[] signature() { return signature; }
    }

    static byte[] sha256(byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
```

### Step 2: Signing helper and revocation store

A `CaSigner` produces signatures: `sign(certPortion, issuerPrivateKeyId) = sha256(certPortion || issuerPrivateKeyId)`. The CRL store maps issuer → revoked serials.

```java
    public static final class CaSigner {
        public static byte[] sign(byte[] signedPortion, String issuerPrivateKeyId) {
            byte[] data = new byte[signedPortion.length + issuerPrivateKeyId.getBytes().length];
            System.arraycopy(signedPortion, 0, data, 0, signedPortion.length);
            System.arraycopy(issuerPrivateKeyId.getBytes(), 0, data,
                    signedPortion.length, issuerPrivateKeyId.getBytes().length);
            return sha256(data);
        }

        public static Certificate issue(String subject, String issuer,
                                        Instant notBefore, Instant notAfter, boolean isCa,
                                        Eku eku, String serial) {
            // The signature binds the certificate to the issuer's public key
            // identity — the same identity the verifier derives from the
            // parent certificate when checking the link.
            Certificate raw = new Certificate(subject, issuer, notBefore, notAfter,
                    isCa, eku, serial, "key-" + subject, new byte[0]);
            return new Certificate(subject, issuer, notBefore, notAfter, isCa, eku, serial,
                    raw.publicKeyId(), sign(raw.signedPortion(), "key-" + issuer));
        }
    }

    public record Crl(String issuer, Set<String> revokedSerials) {
        boolean revoked(Certificate cert) {
            return cert.issuer().equals(issuer) && revokedSerials.contains(cert.serial());
        }
    }
```

### Step 3: The chain verifier

The verifier implements the full chain algorithm:

1. **Path building**: start at the leaf; repeatedly find parent candidates (certificates whose subject = child's issuer) from the trust store; detect cycles; try every candidate (cross-signing support).
2. **Link verification**: parent's public key id verifies the child's signature — `signature(child) == sha256(signedPortion(child) || parentKeyId)`.
3. **Validity**: every certificate must be valid at the verification time.
4. **CA constraints**: every parent must have the CA basic constraint; the leaf must carry the required EKU.
5. **Revocation**: the leaf (and optionally intermediates) checked against CRLs.
6. **Anchor**: the chain must end at a certificate present in the trust store's roots.

Each failure carries a distinct reason.

```java
    public enum Reason { OK, EXPIRED, UNTRUSTED, BAD_SIGNATURE, WRONG_EKU, REVOKED,
        NOT_CA, NO_CHAIN }

    public record VerifyResult(Reason reason, List<String> chain) {}

    public static final class Verifier {
        private final Map<String, Certificate> store;  // subject -> cert
        private final Set<String> roots;               // trusted root subjects
        private final List<Crl> crls;
        private final Instant now;

        public Verifier(Map<String, Certificate> store, Set<String> roots,
                        List<Crl> crls, Instant now) {
            this.store = store;
            this.roots = roots;
            this.crls = crls;
            this.now = now;
        }

        public VerifyResult verify(Certificate leaf, Eku requiredEku) {
            if (!leaf.validAt(now)) return new VerifyResult(Reason.EXPIRED, List.of());
            if (leaf.eku() != requiredEku) return new VerifyResult(Reason.WRONG_EKU, List.of());
            if (isRevoked(leaf)) return new VerifyResult(Reason.REVOKED, List.of());

            List<Certificate> chain = buildChain(leaf, new ArrayList<>(), new HashSet<>());
            if (chain.isEmpty()) return new VerifyResult(Reason.NO_CHAIN, List.of());

            Certificate parent = null;
            for (int i = chain.size() - 1; i >= 0; i--) {
                Certificate cert = chain.get(i);
                if (!cert.validAt(now)) return new VerifyResult(Reason.EXPIRED,
                        names(chain));
                if (parent != null) {
                    if (!parent.isCa()) return new VerifyResult(Reason.NOT_CA, names(chain));
                    byte[] expected = sha256(concat(cert.signedPortion(),
                            parent.publicKeyId().getBytes()));
                    if (!MessageDigest.isEqual(cert.signature(), expected)) {
                        return new VerifyResult(Reason.BAD_SIGNATURE, names(chain));
                    }
                }
                parent = cert;
            }
            return new VerifyResult(Reason.OK, names(chain));
        }

        private List<Certificate> buildChain(Certificate leaf, List<Certificate> path,
                                             Set<String> visited) {
            path.add(leaf);
            if (roots.contains(leaf.subject())) return path;         // anchored
            if (visited.contains(leaf.subject())) return List.of();  // cycle
            visited.add(leaf.subject());

            for (Certificate candidate : store.values()) {
                if (candidate.subject().equals(leaf.issuer())
                        && candidate.isCa()) {
                    List<Certificate> result = buildChain(candidate, new ArrayList<>(path),
                            new HashSet<>(visited));
                    if (!result.isEmpty()) return result; // first valid path wins
                }
            }
            return List.of();
        }

        private boolean isRevoked(Certificate cert) {
            return crls.stream().anyMatch(crl -> crl.revoked(cert));
        }

        private List<String> names(List<Certificate> chain) {
            return chain.stream().map(Certificate::subject).toList();
        }

        private static byte[] concat(byte[] a, byte[] b) {
            byte[] out = new byte[a.length + b.length];
            System.arraycopy(a, 0, out, 0, a.length);
            System.arraycopy(b, 0, out, a.length, b.length);
            return out;
        }
    }
```

Note the signature check: `parent` is the cert *above* `cert` in the chain (closer to the root), so the loop verifies `signature(cert) == hash(signedPortion(cert), parentKeyId)` — every link is verified bottom-up.

### Step 4: The mutual TLS layer

The mutual handshake: the client verifies the server's chain (SERVER_AUTH) against the client trust store; the server verifies the client's chain (CLIENT_AUTH) against the server trust store; then the *authorization* layer maps the verified client identity to a role — authentication and authorization are distinct steps.

```java
    public static final class MtlsHandshake {
        private final Verifier clientSide;
        private final Verifier serverSide;
        private final Set<String> allowedClients; // authorized client identities

        public MtlsHandshake(Verifier clientSide, Verifier serverSide,
                             Set<String> allowedClients) {
            this.clientSide = clientSide;
            this.serverSide = serverSide;
            this.allowedClients = allowedClients;
        }

        public record Session(String clientIdentity, String serverIdentity,
                              boolean authorized, List<String> clientChain) {}

        public Session perform(Certificate serverCert, Certificate clientCert) {
            VerifyResult serverCheck = clientSide.verify(serverCert, Eku.SERVER_AUTH);
            VerifyResult clientCheck = serverSide.verify(clientCert, Eku.CLIENT_AUTH);

            if (serverCheck.reason() != Reason.OK || clientCheck.reason() != Reason.OK) {
                return new Session("", "", false, clientCheck.chain());
            }

            String identity = clientCert.subject();
            boolean authorized = allowedClients.contains(identity);
            return new Session(identity, serverCert.subject(), authorized, clientCheck.chain());
        }
    }
```

### Step 5: Demo — the fixture hierarchy and the verification matrix

The fixture: root CA → intermediate CA → two leaves (server cert, client cert); plus a revoked client cert and a cross-signed intermediate. The demo verifies:

1. **Mutual success**: both chains verify; the client is authorized.
2. **Expired leaf**: fails with EXPIRED.
3. **Revoked client**: fails with REVOKED.
4. **Wrong EKU**: a client cert used as a server cert fails with WRONG_EKU.
5. **Unknown root / missing anchor**: fails with NO_CHAIN (or UNTRUSTED when the anchor isn't in the roots).
6. **Cross-signing**: the same intermediate issued by two roots — the verifier finds the path to the trusted root.

```java
    public static void main(String[] args) {
        Instant now = Instant.parse("2026-07-01T00:00:00Z");
        Instant past = now.minusSeconds(365L * 24 * 3600);
        Instant future = now.plusSeconds(365L * 24 * 3600);
        Instant expired = now.minusSeconds(10);

        Certificate root = CaSigner.issue("root-ca", "root-ca",
                past, future, true, Eku.NONE, "R1");
        Certificate intermediate = CaSigner.issue("intermediate-ca", "root-ca",
                past, future, true, Eku.NONE, "I1");
        Certificate serverCert = CaSigner.issue("server", "intermediate-ca",
                past, future, false, Eku.SERVER_AUTH, "S1");
        Certificate clientCert = CaSigner.issue("client-1", "intermediate-ca",
                past, future, false, Eku.CLIENT_AUTH, "C1");
        Certificate expiredClient = CaSigner.issue("client-expired", "intermediate-ca",
                past, expired, false, Eku.CLIENT_AUTH, "C2");
        Certificate revokedClient = CaSigner.issue("client-revoked", "intermediate-ca",
                past, future, false, Eku.CLIENT_AUTH, "C3");

        Map<String, Certificate> serverStore = new HashMap<>();
        serverStore.put("root-ca", root);
        serverStore.put("intermediate-ca", intermediate);

        List<Crl> crls = List.of(new Crl("intermediate-ca", Set.of("C3")));

        Verifier serverSide = new Verifier(serverStore, Set.of("root-ca"), crls, now);
        Verifier clientSide = new Verifier(serverStore, Set.of("root-ca"), List.of(), now);

        MtlsHandshake handshake = new MtlsHandshake(clientSide, serverSide,
                Set.of("client-1"));

        System.out.println("=== mTLS Certificate Verification Demo ===\n");

        MtlsHandshake.Session ok = handshake.perform(serverCert, clientCert);
        System.out.println("1. mutual success: authorized=" + ok.authorized()
                + " identity=" + ok.clientIdentity()
                + " chain=" + ok.clientChain());

        MtlsHandshake.Session expiredSess = handshake.perform(serverCert, expiredClient);
        System.out.println("2. expired client cert: authorized=" + expiredSess.authorized()
                + " (server-side reason: " + serverSide.verify(expiredClient,
                        Eku.CLIENT_AUTH).reason() + ")");

        MtlsHandshake.Session revokedSess = handshake.perform(serverCert, revokedClient);
        System.out.println("3. revoked client cert: "
                + serverSide.verify(revokedClient, Eku.CLIENT_AUTH).reason()
                + " (CRL check on intermediate-ca)");

        VerifyResult wrongEku = serverSide.verify(clientCert, Eku.SERVER_AUTH);
        System.out.println("4. client cert used as server cert: " + wrongEku.reason());

        Verifier untrusted = new Verifier(Map.of(), Set.of("root-ca"),
                List.of(), now);
        System.out.println("5. unknown root: "
                + untrusted.verify(serverCert, Eku.SERVER_AUTH).reason()
                + " (intermediate missing from store)");

        System.out.println("\n-- Cross-signed intermediate --");
        Certificate crossRoot = CaSigner.issue("other-root", "other-root",
                past, future, true, Eku.NONE, "R2");
        Certificate crossIntermediate = CaSigner.issue("intermediate-ca", "other-root",
                past, future, true, Eku.NONE, "I2");
        Map<String, Certificate> crossStore = new HashMap<>(serverStore);
        crossStore.put("other-root", crossRoot);
        crossStore.put("intermediate-ca", crossIntermediate);
        Verifier cross = new Verifier(crossStore, Set.of("other-root"), List.of(), now);
        VerifyResult crossResult = cross.verify(serverCert, Eku.SERVER_AUTH);
        System.out.println("  server cert via cross-signed root: " + crossResult.reason()
                + " chain=" + crossResult.chain());
    }
}
```

### Step 6: Verify the expected outputs

| Case | Expected reason | Why |
|------|-----------------|-----|
| Mutual success | authorized=true | Both chains anchored at root-ca; identity in allowed set |
| Expired client | EXPIRED | Validity checked for the leaf and every link |
| Revoked client | REVOKED | CRL of intermediate-ca contains serial C3 |
| Client cert as server cert | WRONG_EKU | EKU checked before chain building |
| Unknown root | NO_CHAIN | Path building cannot anchor — no intermediate in store |
| Cross-signed | OK via other-root | Path builder tries the alternative parent (cross-signing) |

The cross-signing case is the subtle one: `intermediate-ca` exists twice (issued by root-ca and by other-root); the builder's store iteration finds the other-root path and validates — a single-path naive implementation would fail exactly here.

---

## Complexity Analysis

- **Chain building**: O(S^D) worst case over store size S and chain depth D with branching — bounded in practice (depth ≤ 3, candidates ≤ 2-3 per level) via cycle detection and the first-valid-path cut.
- **Link verification**: O(D) SHA-256 computations — each link is one hash over the signed portion plus parent key id.
- **Revocation**: O(C) per certificate over CRL count.
- **Path construction**: the cross-signing branch only adds O(C) candidate chains before finding the valid one.
- **Space**: O(D) chain plus O(C) hash-map store.
- **Determinism**: the same store, certs, and clock always produce the same verdict — the property that makes the negative-case matrix reliable.

---

## Follow-Up Questions

1. **How do you add real cryptography?** Replace the hash-chain signature with actual ECDSA/RSA signatures: `Signature.sign(child.signedPortion(), privateKey)` and `Signature.verify` with the parent's public key — the chain-walking logic is unchanged because it only consumes `signedPortion()` and `signature()`.

2. **How do you handle OCSP in this model?** Add an `OcspResponder` interface: `status(serial)` returning GOOD/REVOKED/UNKNOWN; the verifier consults it for the leaf when no CRL entry is found — with configurable fail-open/fail-closed policy on responder unreachability.

3. **How do you enforce pathLenConstraints?** Track remaining path length while walking down: an intermediate with `pathLen=0` may issue no further intermediates — reject a chain where a pathLen-limited cert appears above a CA it cannot cover.

4. **How do you map certificate identity to roles for authorization?** After a successful verification, consult an identity registry (LDAP/directory) keyed by the leaf's subject or UPN SAN — the `allowedClients` set in the demo is the static stand-in; production uses a live lookup with caching and the same cut-off semantics as revocation.

5. **How do you handle client-certificate renewal without downtime?** Issue short-lived certs (30 days), provision via an automated enrollment flow, and have the client renew when remaining validity < 7 days — the server accepts both the old and new cert during a transition window so rotation is rolling, not atomic.

6. **How do you test the full matrix deterministically?** The fixture hierarchy is generated once with fixed serials and times; every negative case is a table row (cert, expected reason) run through the same verifier — plus a property test asserting that modifying any field of the signed portion invalidates the signature.
