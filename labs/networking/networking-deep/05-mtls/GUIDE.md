# GUIDE — mTLS

## Step 1: mTLS Handshake
```java
public record CertificateRequest(List<String> acceptableCas, List<String> signatureAlgorithms) {}
```
Server sends CertificateRequest; client sends Certificate + CertificateVerify.

## Step 2: Mutual Certificate Validation
```java
MutualAuthValidator validator = new MutualAuthValidator(serverTrustStore, clientTrustStore);
validator.validate(serverCertChain, clientCertChain);
```

## Step 3: Certificate Revocation
- Download and parse CRL from distribution point
- Query OCSP responder
- Cache revocation status with TTL

## Step 4: Service Mesh mTLS
```java
SpiffeWorkload workload = new SpiffeWorkload("spiffe://cluster.local/ns/default/sa/webapp");
Svid svid = workload.getSvid();
```

## Step 5: SPIFFE Identity
- Build X.509 SVID with SPIFFE ID in SAN
- Establish trust with trust bundle
- Validate peer SVID

## Step 6: Exercises
1. Implement an OCSP response validator with freshness checks
2. Build a SPIFFE trust domain federation simulator
3. Create a service mesh proxy with mTLS termination
