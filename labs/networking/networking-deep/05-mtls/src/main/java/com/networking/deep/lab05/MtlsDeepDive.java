package com.networking.deep.lab05;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class MtlsDeepDive {

    public record CertificateRequest(List<String> acceptableCas, List<String> signatureAlgorithms) {}
    public record CertificateEntry(String subject, String issuer, String serialNumber, Instant notBefore, Instant notAfter, List<String> san) {}
    public record SpiffeId(String trustDomain, String namespace, String serviceAccount) {
        public String uri() { return "spiffe://" + trustDomain + "/ns/" + namespace + "/sa/" + serviceAccount; }
    }
    public record Svid(CertificateEntry cert, SpiffeId spiffeId, String trustBundle) {}

    public static class MtlsHandshake {
        public boolean authenticate(CertificateEntry serverCert, CertificateEntry clientCert, List<String> trustStore) {
            if (!trustStore.contains(serverCert.issuer()) || !trustStore.contains(clientCert.issuer())) {
                System.out.println("Untrusted issuer");
                return false;
            }
            var now = Instant.now();
            if (now.isBefore(serverCert.notBefore()) || now.isAfter(serverCert.notAfter()) ||
                now.isBefore(clientCert.notBefore()) || now.isAfter(clientCert.notAfter())) {
                System.out.println("Certificate expired");
                return false;
            }
            System.out.println("mTLS authentication successful");
            return true;
        }
    }

    public static class CertificateRevocation {
        private final Set<String> revoked = new ConcurrentSkipListSet<>();

        public void revoke(String serialNumber) { revoked.add(serialNumber); }

        public boolean checkCrl(String serialNumber) { return revoked.contains(serialNumber); }

        public boolean checkOcsp(String serialNumber) {
            return !revoked.contains(serialNumber);
        }
    }

    public static class SpiffeWorkload {
        private final SpiffeId spiffeId;
        private final Map<String, Svid> svids = new ConcurrentHashMap<>();

        public SpiffeWorkload(SpiffeId spiffeId) { this.spiffeId = spiffeId; }

        public Svid getSvid() {
            return svids.computeIfAbsent(spiffeId.uri(), k -> {
                var cert = new CertificateEntry(spiffeId.uri(), "SPIFFE-CA", "SVID-" + UUID.randomUUID().toString().substring(0, 8),
                    Instant.now(), Instant.now().plus(24, java.time.temporal.ChronoUnit.HOURS), List.of(spiffeId.uri()));
                return new Svid(cert, spiffeId, "trust-bundle-content");
            });
        }

        public boolean authenticatePeer(Svid peerSvid, List<String> trustedDomains) {
            var domain = peerSvid.spiffeId().trustDomain();
            if (!trustedDomains.contains(domain)) {
                System.out.println("Untrusted SPIFFE domain: " + domain);
                return false;
            }
            var now = Instant.now();
            var cert = peerSvid.cert();
            if (now.isBefore(cert.notBefore()) || now.isAfter(cert.notAfter())) {
                System.out.println("Peer SVID expired");
                return false;
            }
            System.out.println("Peer SPIFFE identity authenticated: " + peerSvid.spiffeId().uri());
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== mTLS Authentication ===");
        var serverCert = new CertificateEntry("server.example.com", "Internal-CA", "SN-001",
            Instant.now().minus(1, java.time.temporal.ChronoUnit.DAYS),
            Instant.now().plus(365, java.time.temporal.ChronoUnit.DAYS), List.of("server.example.com"));
        var clientCert = new CertificateEntry("client.example.com", "Internal-CA", "SN-002",
            Instant.now().minus(1, java.time.temporal.ChronoUnit.DAYS),
            Instant.now().plus(365, java.time.temporal.ChronoUnit.DAYS), List.of("client.example.com"));
        var appCert = new CertificateEntry("app.internal", "External-CA", "SN-003",
            Instant.now(), Instant.now().plus(30, java.time.temporal.ChronoUnit.DAYS), List.of("app.internal"));

        var mtls = new MtlsHandshake();
        System.out.println("Valid mTLS: " + mtls.authenticate(serverCert, clientCert, List.of("Internal-CA")));
        System.out.println("Invalid mTLS: " + mtls.authenticate(serverCert, appCert, List.of("Internal-CA")));

        System.out.println("\n=== Certificate Revocation ===");
        var crl = new CertificateRevocation();
        crl.revoke("SN-003");
        System.out.println("CRL check SN-001: " + crl.checkCrl("SN-001"));
        System.out.println("CRL check SN-003: " + crl.checkCrl("SN-003"));
        System.out.println("OCSP check SN-003: " + crl.checkOcsp("SN-003"));

        System.out.println("\n=== SPIFFE Workload Identity ===");
        var workload = new SpiffeWorkload(new SpiffeId("cluster.local", "default", "webapp"));
        var svid = workload.getSvid();
        System.out.println("SVID SPIFFE ID: " + svid.spiffeId().uri());
        System.out.println("SVID subject: " + svid.cert().subject());

        var peerWorkload = new SpiffeWorkload(new SpiffeId("cluster.local", "default", "database"));
        var peerSvid = peerWorkload.getSvid();
        System.out.println("Peer auth valid: " + workload.authenticatePeer(peerSvid, List.of("cluster.local")));
        System.out.println("Untrusted peer auth: " + workload.authenticatePeer(peerSvid, List.of("other.local")));
    }
}
