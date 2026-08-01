# Lab 04: Mock Interview — Senior Security/Protocol Engineer

**Role**: Senior Security Engineer | **Topic**: TLS 1.3 Handshake State Machine | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Implement a TLS 1.3-like handshake state machine. Before the code, walk me through the handshake flow and what changed from TLS 1.2."

**Candidate**: "The TLS 1.3 full handshake is two round trips from the client's perspective, and the client can even get to one round trip. Flow: the client sends **ClientHello** — supported versions, key shares (its ephemeral X25519 public keys, typically for multiple curves), supported cipher suites, and extensions. The server replies with **ServerHello** — its chosen version, key share, and cipher suite — then immediately with **EncryptedExtensions** (server's certificate requirements, ALPN, etc.), its **Certificate** and **CertificateVerify** (a signature over the whole transcript up to that point), and **Finished** — all encrypted under the handshake keys derived from the ECDHE shared secret. The client validates, derives application keys, and sends its own **Finished**. Then application data flows with 1-RTT keys. The one-round-trip path is **0-RTT**: if the client has a cached session (a PSK from a previous handshake), it can send application data *with the first flight* — the price is that 0-RTT data is replayable, so it must be idempotent or protected by a replay cache."

**Interviewer**: "What changed versus TLS 1.2 that makes the state machine fundamentally different?"

**Candidate**: "Three changes. First, **the handshake is a full transcript** — every message up to the current point is fed into a running hash (the Transcript-Hash), and CertificateVerify signs the transcript; Finished is a MAC over the transcript with the derived keys. This kills the 'message sequence flexibility' that made 1.2's state machine a spiderweb — in 1.3 the message order is fixed, and any deviation is a hard error. Second, **RSA key exchange is gone** — forward secrecy is mandatory: only (EC)DHE or PSK-based key agreement exists, and the server's signature is over the handshake, never a decryption oracle like 1.2's RSA key transport. Third, **the key schedule is a HKDF chain** — a single early secret splits into derived secrets for each stage: `early → handshake → application`, each with its own traffic keys, and the 0-RTT keys come from the early secret. Renegotiation is gone too — a client asking to renegotiate is an error, which removes a whole class of downgrade and truncation attacks."

**Interviewer**: "Walk me through the state machine states. What's the canonical transition set?"

**Candidate**: "Client side: `START → CLIENT_HELLO_SENT → (SERVER_HELLO_RECEIVED) → SERVER_FINISHED_RECEIVED → CONNECTED`. Server side: `START → CLIENT_HELLO_RECEIVED → SERVER_HELLO_SENT → (CLIENT_FINISHED_RECEIVED) → CONNECTED`. The important design property: each state transition is *guarded* — a message arriving in the wrong state triggers a fatal alert and closes the connection. The subtlety is the middle: between ServerHello and Finished, several encrypted messages arrive in a fixed sequence — Certificate, CertificateVerify, EncryptedExtensions (the order in the spec is EncryptedExtensions, Certificate, CertificateVerify, Finished) — and the implementation must accumulate the transcript hash *as each message lands*, because CertificateVerify validates the transcript up to itself, and Finished validates the transcript up to itself. And there's the 0-RTT path: the client has a `0RTT_APPLICATION` state between ClientHello and ServerHello where it can send early data, and the server has a corresponding `early data accepted` branch."

**Interviewer**: "How does the key schedule actually work — what's derived when?"

**Candidate**: "The HKDF chain: the **Early Secret** is HKDF-Extract(PSK or zeros, 0). From it: `binder_key` (binds the PSK to the handshake), `client_early_traffic_secret` (0-RTT keys), and the **Handshake Secret** = HKDF-Extract(ECDHE shared secret, early secret). The handshake secret derives `client_handshake_traffic_secret` and `server_handshake_traffic_secret`, which encrypt the server's certificate exchange and the client's Finished. Finally the **Master Secret** = HKDF-Extract(0, handshake secret), which derives `client_application_traffic_secret` and `server_application_traffic_secret` — the 1-RTT keys. Each traffic secret goes through HKDF-Expand-Label with the transcript hash to produce the actual per-direction keys. The demo implementation will reproduce this chain with real HKDF — that's the part where correctness is verifiable: two implementations agreeing on keys is proof the schedule is right."

**Interviewer**: "What are the failure modes and attacks the state machine must defend against — what are the real-world bugs here?"

**Candidate**: "The classic list. **Downgrade attacks**: a MITM strips the client's supported_versions and keyshares; defense is the server echoing the negotiated version inside the transcript and the `supported_versions` extension — plus the downgrade sentinel values in ServerHello's random field that old implementations check. **Version-downgrade to SSLv3-era** is mitigated by TLS 1.3 not even offering those. **Truncation**: an attacker drops the client's Finished and the connection just... stops — without the `close_notify` requirement, a client can't distinguish truncation from a dead connection; the spec says both sides must send close_notify, and applications must treat its absence as truncation. **0-RTT replay**: the classic — an attacker replays a captured 0-RTT request; defenses are idempotency at the application and a server-side replay cache keyed by the client's random value. **Handshake message reordering / injection**: the fixed message order plus transcript binding means any injected message corrupts the transcript and the Finished check fails — that's why the transcript hash IS the anti-injection mechanism. And the subtle one: **key usage errors** — a server that encrypts application data with the handshake keys (or forgets to switch keys after Finished) produces an implementation that *works* against itself but fails interop — the per-stage key derivation must be rigid."

**Interviewer**: "What about session resumption — PSKs? How does that fit the state machine?"

**Candidate**: "Resumption is a PSK handshake: the server issues a session ticket (encrypted with a server-side resumption key) in the post-handshake phase; the client stores it; the next handshake sends ClientHello with a `pre_shared_key` extension containing the ticket; the server either accepts (offers a PSK cipher suite) and derives keys from the PSK + fresh ECDHE (the **PSK+DHE** mode, which keeps forward secrecy) or falls back to a full handshake. The state machine gains two states: the client can skip directly from ClientHello to application-data via 0-RTT when the PSK is fresh, and the server can accept early data if its policy allows. The failure mode: **session tickets stolen** — a stolen ticket lets the thief resume and decrypt; hence ticket encryption keys must rotate, and tickets are bound to the client identity and have short lifetimes."

**Interviewer**: "How do you test a TLS handshake implementation?"

**Candidate**: "Three layers. **Test vectors**: the official TLS 1.3 test vectors give exact inputs and expected outputs for the key schedule — an implementation that derives the exact traffic secrets from the known transcript passes the strongest correctness test there is. **Interop**: run against OpenSSL's s_server/s_client, Go's crypto/tls, and BoringSSL — the interop matrix catches every version-skew bug. **Fuzz/negative testing**: a message-level fuzzer that feeds malformed handshake messages (bad record types, wrong-length CertificateVerify, reordered Finished) and asserts the implementation always fails with a valid alert, never crashes, and never completes a handshake with a corrupt transcript. The demo walkthrough takes the first and third: deterministic transcript fixtures plus negative-case assertions."

**Interviewer**: "What's the single most common production bug you see in TLS stacks?"

**Candidate**: "**Key confusion between stages**: deriving the client application traffic secret but using it to encrypt handshake records, or encrypting with the handshake secret after the switch should have happened — it manifests as 'works in my test harness, fails against real peers' because the transcript the keys are bound to includes the switch point. The fix is structural: the key derivation and the record-layer protection must be driven by the *same* stage variable, and a test that verifies keys change after each Finished. Second runner-up: failing to check the server's certificate hostname — the handshake can be perfect while the identity check is absent, which is exactly how MITM proxies still work against misconfigured clients."

---

## Wrap-Up

**What the interviewer is looking for**:
- The exact 1-RTT message flow and where encryption switches on
- Transcript binding as the core anti-injection mechanism
- The HKDF key schedule: early → handshake → master, and what each stage protects
- Fixed message ordering and fatal-alert semantics in the state machine
- 0-RTT replay hazards and downgrade defenses (sentinels, supported_versions)
- Test vectors and interop as the correctness strategy

**Common mistakes candidates make**:
- Confusing the handshake traffic keys with application traffic keys
- Forgetting CertificateVerify signs the *transcript*, not the certificate alone
- No handling of the close_notify truncation signal
- Allowing renegotiation-style messages (a 1.2-ism that's fatal in 1.3)
- Not validating the Finished MAC before sending application data
