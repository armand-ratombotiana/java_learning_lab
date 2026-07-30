# Interview Questions — mTLS

## Beginner

Q: What is mutual TLS (mTLS)?
A: Both client and server present certificates to authenticate each other.

Q: How is mTLS different from regular TLS?
A: Regular TLS only authenticates server; mTLS authenticates both parties.

## Intermediate

Q: How does mTLS work in a service mesh (e.g., Istio)?
A: Sidecar proxy intercepts traffic, terminates mTLS, uses SPIFFE identities, rotates certificates automatically.

Q: What is a Certificate Revocation List (CRL)?
A: A list of revoked certificate serial numbers published by the CA, periodically downloaded by clients.

## Advanced

Q: How do you handle certificate revocation in a high-performance mTLS system?
A: OCSP stapling for server certificates, CRLite for compressed revocation, short-lived certificates (~24h) to avoid revocation.

Q: How does SPIFFE federation work across trust domains?
A: Each domain publishes a trust bundle; domains exchange bundles out-of-band; peers validate SVIDs using federated bundles.
