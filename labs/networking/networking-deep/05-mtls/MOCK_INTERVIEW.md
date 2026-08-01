# Lab 05: Mock Interview — Senior Security Engineer (mTLS)

**Role**: Senior Security Engineer | **Topic**: Mutual TLS Certificate Verification | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Implement mutual TLS certificate verification. The client must verify the server and the server must verify the client. Walk me through the verification chain — what gets checked, in what order, and what the failure semantics are."

**Candidate**: "Let me separate the two directions, because the logic is symmetric but the *policy* differs. In one-way TLS the client verifies the server's certificate chain: leaf → intermediates → root, with signature verification at every link, validity windows, hostname match against the SNI, and revocation checking. mTLS adds the mirror: the server requests a client certificate during the handshake (CertificateRequest message), the client sends its certificate chain, and the server runs the *same* chain verification plus an application-level authorization check — is this certificate's identity allowed to call this service? The failure semantics are the interesting part: a chain failure is a hard handshake abort with a certificate_unknown or bad_certificate alert; an authorization failure happens *after* the TLS layer, at the application policy layer — the connection encrypts fine but the request is denied. A senior answer keeps those two layers distinct: TLS authentication (proves identity) and application authorization (decides what the identity may do)."

**Interviewer**: "Walk me through the chain verification algorithm itself — the actual steps."

**Candidate**: "Given the leaf certificate and the trust store: (1) build the chain — walk the certificate hierarchy from the leaf up to a trusted root, using each certificate's issuer name to find the next certificate in the store or in the presented chain; (2) verify every link: signature check — the parent's public key verifies the child's signature over the child's TBSCertificate (the signed portion); (3) check validity windows at the *current time* — notBefore/notAfter — for every certificate in the chain, not just the leaf; (4) check key usage and basic constraints — a CA certificate must have the CA bit set, intermediates must have proper pathLenConstraints, and the leaf must have the right key usages (serverAuth or clientAuth extended key usage, which is how the 'may this cert authenticate a server' rule is enforced); (5) check revocation — CRL or OCSP; (6) hostname/identity match for the leaf. The two classic subtleties: the order matters — checking signatures before validity means you can be fooled by a valid-signature-but-expired chain (still fail, just slower); and the *algorithm suite* must be pinned — a chain that validates only with MD5 or SHA-1 must be rejected regardless of signatures."

**Interviewer**: "Now the deep question: what does 'trust' mean here, and how do you build the chain correctly? Path building is harder than it sounds."

**Candidate**: "Trust means: the root CA's self-signed certificate is *preinstalled* in the trust store — it's the trust anchor. Path building starts at the leaf and repeatedly finds 'a certificate issued by the current certificate' — matching on the issuer DN. The difficulty: there can be multiple candidate parent chains (cross-signed roots — the same intermediate issued by two different roots), loops, and unanchored chains. The correct algorithm: build *all* candidate chains up to the configured roots, then pick the one that validates — with cross-signing, a chain that fails against one root may succeed against another, so the verifier must try the alternatives before declaring failure. The practical implementations (OpenSSL, Java's PKIX) do this; a naive 'follow the single issuer name' implementation breaks exactly when cross-signing is in play — which is common in real infrastructure (Let's Encrypt's cross-signed intermediates are the canonical example)."

**Interviewer**: "How do you handle revocation in mTLS? CRLs or OCSP, and what happens when the revocation service is unreachable?"

**Candidate**: "Two mechanisms: CRLs (signed lists of revoked serial numbers, fetched periodically) and OCSP (a real-time query to the issuer's responder: 'is serial X valid?'). CRL downsides: they're as stale as the fetch schedule, and the fetch itself can be blocked. OCSP downsides: a real-time dependency in the handshake path — and the classic failure mode: an attacker *blocks* OCSP traffic so every check fails, and a client configured 'fail-closed' turns an outage into a total handshake failure, while 'fail-open' (continue on OCSP failure) lets the attacker win by denial. The modern answer: **OCSP stapling** for server certs (the server fetches the OCSP response itself and staples it into the handshake, so the client doesn't need the network round trip) and, for the strongest assurance, **short-lived certificates** — certs with hours-to-days lifetimes that make revocation nearly moot. For mTLS client certificates specifically, I'd argue CRL/OCSP is only part of the story: the *authorization* layer (the mapping from cert identity to role/permission) is where revocation semantics actually bite, and it must consult the live identity source at request time — a revoked client whose cert is still within validity must be cut off at the authz layer within seconds, not at the next CRL fetch."

**Interviewer**: "What about the hostname verification for client certificates — client certs don't have DNS names. What do you match?"

**Candidate**: "Exactly — this is where mTLS differs from server-side TLS. A client certificate's identity comes from its **subject DN** and **SANs**: typically CN (common name) or a URI/email SAN. The matching rule is policy: some systems match the CN exactly; enterprise systems often map the cert to a directory identity via the UPN SAN or a custom OID; the important design point is that the *matching policy* must be explicit and reject ambiguous matches — a client cert with no SAN and a wildcard-ish CN should be rejected or explicitly handled, never silently accepted. And the deep practice: the *certificate identity* is not the *service identity* — the TLS layer authenticates the cert; the service then maps the cert identity to a role via its own directory or registry. The classic incident is the team that checks 'certificate validates' and skips 'identity is allowed' — that's an authentication gap, not a verification gap."

**Interviewer**: "How do you handle client certificate rotation and expiry in production? Cert renewal is where mTLS deployments fall apart."

**Candidate**: "The failure pattern: clients provisioned with a 1-year cert, and at month 13 the fleet starts failing with 'certificate expired' while the ops team scrambles. The answer is lifecycle automation: client certs with short lifetimes (30 days) *force* the rotation machinery to exist — short-lived certs are the load-bearing pattern; automated issuance via ACME-like flows (the cert is a *secret* that must be provisioned, rotated, and revoked programmatically); and the crucial operational detail: the client must handle **expiry-in-handshake** gracefully — a client that detects its cert is expiring within the threshold should warn and renew *before* the deadline, and a server that gets a client cert with an approaching expiry should log it (warning) while still accepting it. There's also the revocation-before-expiry case: identity compromise — the server's authz layer must be able to cut a specific client off without waiting for the CA. And the drift hazard: environments where every component silently accepts *any* client cert because the verification was disabled 'temporarily' during a migration — that's the thing an audit must hunt for."

**Interviewer**: "What's your test strategy for a verification chain?"

**Candidate**: "A fixture CA hierarchy in code — a root, an intermediate (with pathLenConstraints), and leaves with the right EKUs — generated with the JDK's keytool or BouncyCastle. Then a matrix of negative cases: expired leaf, expired intermediate, wrong EKU, revoked serial, unknown root, tampered signature, missing intermediate, chain-to-wrong-root via cross-signing. Each negative must produce the *correct* failure reason — because 'rejected' is not enough: the audit log must say *why* (expired vs untrusted vs revoked are different operational responses). Property tests: any certificate signed by the private key of a store root validates; any modification to the TBSCertificate invalidates the signature. And the integration test: the live handshake with a real TLS stack in both directions — client verifies server, server verifies client — which the walkthrough will simulate in code."

**Interviewer**: "Final question: what's the most common misconfiguration you see in real mTLS setups?"

**Candidate**: "**The trust anchor mismatch**: the server's trust store is missing the *client CA* (or vice versa), so everything validates 'locally' in tests against a different store than production — the first production client gets a certificate_unknown alert and the whole rollout stalls. It's embarrassingly common because teams test client-cert validation with *their own* CA in the store and never test with the *actual* client CA. The fix is the fixture hierarchy test run against the *same* store files that ship to production, plus a startup self-test: the server validates a known-good cert against its configured store at boot."

---

## Wrap-Up

**What the interviewer is looking for**:
- The full chain steps: path building, signature links, validity, EKU/KU, revocation, identity match
- The authentication-vs-authorization distinction in mTLS
- Cross-signing awareness in path building
- Revocation strategy depth: CRL/OCSP/stapling/short-lived certs, fail-open vs fail-closed
- Identity matching policy for client certs (subject/SAN, not DNS)
- Lifecycle reality: rotation automation, expiry handling, compromise cut-off

**Common mistakes candidates make**:
- Verifying only the leaf and skipping intermediates
- Forgetting EKU/basic-constraints checks
- Checking signature before validity with no algorithm pinning
- Treating revocation as optional or single-mechanism
- Confusing 'certificate is valid' with 'identity is authorized'
- No expiry-rotation lifecycle plan
