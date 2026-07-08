# Network Security - Theory

## TLS/SSL Protocol

TLS provides:
- **Encryption**: Data is encrypted in transit
- **Authentication**: Server (and optionally client) identity verified
- **Integrity**: Data cannot be modified without detection

## TLS Handshake (1.3)
```
Client                          Server
   |                               |
   |--- ClientHello + Key Share -->|
   |<-- ServerHello + Cert + Finished --|
   |--- Finished + App Data ------>|
   |<== Encrypted Communication ==>|
```

## Certificate Chain
```
Root CA (self-signed, in trust store)
  +-- Intermediate CA (signed by Root)
       +-- Server Certificate (signed by Intermediate)
            Subject: CN=example.com
            Issuer: CN=Intermediate CA
            Validity: 2024-2025
```

## mTLS (Mutual TLS)
Both client and server present certificates to verify identity.
```java
// mTLS client setup
public class MutualTlsClient {
    public static SSLContext createMutualTlsContext(
            String keyStorePath, String trustStorePath,
            String keyStorePass, String trustStorePass) throws Exception {

        // Load client certificate (used to identify client)
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(keyStorePath), keyStorePass.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keyStore, keyStorePass.toCharArray());

        // Load server CA (used to verify server)
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        trustStore.load(new FileInputStream(trustStorePath), trustStorePass.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        return sslContext;
    }
}
```

## Java KeyStore Management
```java
public class CertificateManager {
    // Generate self-signed certificate
    public static KeyStore createSelfSignedCert(String alias, String dn,
            String password) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        X509Certificate cert = generateCertificate(dn, pair, 365);
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(null, null);
        keystore.setKeyEntry(alias, pair.getPrivate(), password.toCharArray(),
            new Certificate[]{cert});
        return keystore;
    }

    private static X509Certificate generateCertificate(String dn,
            KeyPair pair, int days) throws Exception {
        // Use Bouncy Castle or JDK internal APIs
        // Returns X509Certificate
        throw new UnsupportedOperationException("Use keytool or Bouncy Castle");
    }

    // Export certificate
    public static void exportCert(KeyStore keystore, String alias,
            String outputPath) throws Exception {
        Certificate cert = keystore.getCertificate(alias);
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(cert.getEncoded());
        }
    }
}
```

## Network Policies (Kubernetes)
```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: api-network-policy
spec:
  podSelector:
    matchLabels:
      app: api-service
  policyTypes:
    - Ingress
    - Egress
  ingress:
    - from:
        - podSelector:
            matchLabels:
              app: gateway
      ports:
        - protocol: TCP
          port: 8080
  egress:
    - to:
        - podSelector:
            matchLabels:
              app: database
      ports:
        - protocol: TCP
          port: 5432
```

## Java Firewall Rules
```java
// Programmatic firewall rules (conceptual)
public class SimpleFirewall {
    private final List<FirewallRule> rules = new ArrayList<>();

    record FirewallRule(String srcIp, String dstIp, int port, String protocol, Action action) {}
    enum Action { ALLOW, DENY }

    public void addRule(FirewallRule rule) {
        rules.add(rule);
    }

    public boolean isAllowed(String srcIp, String dstIp, int port, String protocol) {
        return rules.stream()
            .filter(r -> matches(r, srcIp, dstIp, port, protocol))
            .findFirst()
            .map(r -> r.action == Action.ALLOW)
            .orElse(false); // Default deny
    }

    private boolean matches(FirewallRule r, String srcIp, String dstIp,
            int port, String protocol) {
        return (r.srcIp.equals("*") || r.srcIp.equals(srcIp))
            && (r.dstIp.equals("*") || r.dstIp.equals(dstIp))
            && r.port == port
            && r.protocol.equals(protocol);
    }
}
```
