package com.net.security;

import java.util.*;
import java.security.*;
import java.security.cert.*;
import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.time.*;

public class CertificateGenerator {

    public static class X509Certificate {
        public final String subject;
        public final String issuer;
        public final BigInteger serialNumber;
        public final Instant notBefore;
        public final Instant notAfter;
        public final String signatureAlgorithm;
        public final Map<String, String> extensions;

        public X509Certificate(String subject, String issuer, String algorithm,
                               Duration validity, Map<String, String> extensions) {
            this.subject = subject;
            this.issuer = issuer;
            this.serialNumber = new BigInteger(64, new SecureRandom());
            this.notBefore = Instant.now();
            this.notAfter = Instant.now().plus(validity);
            this.signatureAlgorithm = algorithm;
            this.extensions = extensions;
        }

        public boolean isValid() {
            Instant now = Instant.now();
            return now.isAfter(notBefore) && now.isBefore(notAfter);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Certificate:\n");
            sb.append("  Subject: ").append(subject).append("\n");
            sb.append("  Issuer: ").append(issuer).append("\n");
            sb.append("  Serial: ").append(serialNumber).append("\n");
            sb.append("  Valid: ").append(notBefore).append(" to ").append(notAfter).append("\n");
            sb.append("  Algorithm: ").append(signatureAlgorithm).append("\n");
            sb.append("  Extensions:\n");
            extensions.forEach((k, v) -> sb.append("    ").append(k).append(": ").append(v).append("\n"));
            return sb.toString();
        }
    }

    public static class CertificateAuthority {
        private final String name;
        private final X509Certificate caCert;

        public CertificateAuthority(String name) {
            this.name = name;
            this.caCert = new X509Certificate("CN=" + name, "CN=" + name,
                "SHA256WithRSA", Duration.ofDays(3650),
                Map.of("CA", "TRUE", "Path Length", "1"));
            System.out.println("CA '" + name + "' initialized\n");
        }

        public X509Certificate signCertificate(String subject, Duration validity, Map<String, String> exts) {
            Map<String, String> allExts = new HashMap<>(exts);
            allExts.put("CA", "FALSE");
            X509Certificate cert = new X509Certificate("CN=" + subject, "CN=" + name,
                "SHA256WithRSA", validity, allExts);
            System.out.println("Signed certificate for " + subject);
            return cert;
        }

        public boolean verify(X509Certificate cert) {
            return cert.isValid() && cert.issuer.equals("CN=" + name);
        }
    }

    public static class CertificateStore {
        private final Map<String, X509Certificate> store = new HashMap<>();
        private final Map<String, PrivateKey> keys = new HashMap<>();

        public void addCertificate(String alias, X509Certificate cert) {
            store.put(alias, cert);
            System.out.println("Stored certificate: " + alias);
        }

        public X509Certificate getCertificate(String alias) {
            return store.get(alias);
        }

        public void printAll() {
            System.out.println("\n=== Certificate Store ===");
            store.forEach((alias, cert) -> System.out.println("  " + alias + ": " + cert.subject));
        }
    }

    public static void main(String[] args) {
        CertificateAuthority ca = new CertificateAuthority("MyRootCA");

        X509Certificate serverCert = ca.signCertificate("api.example.com",
            Duration.ofDays(365), Map.of("TLS Web Server", "TRUE"));
        System.out.println(serverCert);

        X509Certificate clientCert = ca.signCertificate("client-app",
            Duration.ofDays(90), Map.of("TLS Web Client", "TRUE"));

        System.out.println("Verify server cert: " + ca.verify(serverCert));
        System.out.println("Verify client cert: " + ca.verify(clientCert));

        CertificateStore store = new CertificateStore();
        store.addCertificate("server", serverCert);
        store.addCertificate("client", clientCert);
        store.printAll();
    }
}
