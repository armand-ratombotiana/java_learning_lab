# GUIDE — TLS Deep

## Step 1: TLS 1.3 Handshake
```java
public record ClientHello(byte[] random, List<CipherSuite> cipherSuites, List<Extension> extensions) {}
public record ServerHello(byte[] random, CipherSuite selected, byte[] serverKeyShare) {}
```

## Step 2: Key Schedule
- Derive handshake secret from ECDHE key exchange
- Derive application traffic secret
- Implement HKDF extract-and-expand

## Step 3: Certificate Chain Validation
```java
CertificateValidator validator = new CertificateValidator(trustStore);
validator.validate(certChain); // builds chain, checks signatures, expiration
```

## Step 4: OCSP Stapling
- Server includes OCSP response in CertificateEntry extension
- Client validates stapled response freshness

## Step 5: Session Tickets & 0-RTT
- Server issues NewSessionTicket after handshake
- Client caches ticket and uses in subsequent connections

## Step 6: Exercises
1. Implement HKDF key derivation for TLS 1.3 traffic secrets
2. Build a certificate chain builder from a list of certificates
3. Create a 0-RTT early data handler with replay protection
