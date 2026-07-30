# Interview Questions — TLS Deep

## Beginner

Q: What does TLS 1.3 improve over TLS 1.2?
A: Reduced handshake (1-RTT vs 2-RTT), removed insecure features, 0-RTT support, simplified cipher suites.

Q: What is a cipher suite?
A: A set of cryptographic algorithms for key exchange, authentication, encryption, and integrity.

## Intermediate

Q: How does the TLS 1.3 handshake work in 1-RTT?
A: ClientHello + KeyShare -> ServerHello + EncryptedExtensions + Certificate + Finished -> Client Finished -> Application data.

Q: What is OCSP stapling?
A: Server includes time-stamped OCSP response in TLS handshake, proving certificate is not revoked without client contacting CA.

## Advanced

Q: How does session resumption work in TLS 1.3?
A: Server issues NewSessionTicket with PSK identity; client includes PSK in ClientHello; 0-RTT data possible if also includes early_data extension.

Q: What replay protections exist for TLS 1.3 0-RTT?
A: Server maintains anti-replay window, single-use tickets, monotonic ticket ages, idempotent early data only.
