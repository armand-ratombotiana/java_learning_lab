package com.networking.deep.lab04;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class TlsDeepDive {

    public record CipherSuite(String name, int id, String keyExchange, String auth, String encryption, String prf) {}
    public record ClientHello(int version, byte[] random, List<CipherSuite> supported, List<String> extensions) {}
    public record ServerHello(int version, byte[] random, CipherSuite selected, byte[] serverKeyShare) {}
    public record CertificateEntry(String subject, String issuer, Instant notBefore, Instant notAfter, List<String> san, byte[] ocspResponse) {}
    public record NewSessionTicket(byte[] ticket, long lifetimeSec, int ageAdd) {}

    public static final List<CipherSuite> TLS13_CIPHER_SUITES = List.of(
        new CipherSuite("TLS_AES_128_GCM_SHA256", 0x1301, "ECDHE", "RSA", "AES-128-GCM", "SHA-256"),
        new CipherSuite("TLS_AES_256_GCM_SHA384", 0x1302, "ECDHE", "RSA", "AES-256-GCM", "SHA-384"),
        new CipherSuite("TLS_CHACHA20_POLY1305_SHA256", 0x1303, "ECDHE", "RSA", "CHACHA20-POLY1305", "SHA-256")
    );

    public static class TlsHandshake {
        private final Random rand = new Random();

        public record HandshakeResult(ClientHello clientHello, ServerHello serverHello, byte[] masterSecret) {}

        public HandshakeResult perform(boolean use0Rtt) {
            var clientHello = new ClientHello(0x0304, randomBytes(32), TLS13_CIPHER_SUITES, List.of("key_share", "supported_versions", "psk_key_exchange_modes"));
            var selected = clientHello.supported().get(rand.nextInt(clientHello.supported().size()));
            var serverHello = new ServerHello(0x0304, randomBytes(32), selected, randomBytes(32));

            byte[] clientRandom = clientHello.random();
            byte[] serverRandom = serverHello.random();
            byte[] psk = use0Rtt ? randomBytes(16) : null;

            byte[] masterSecret = deriveSecret(clientRandom, serverRandom, psk);
            return new HandshakeResult(clientHello, serverHello, masterSecret);
        }

        private byte[] randomBytes(int len) { var b = new byte[len]; rand.nextBytes(b); return b; }

        private byte[] deriveSecret(byte[] clientRandom, byte[] serverRandom, byte[] psk) {
            var combined = new byte[(clientRandom.length + serverRandom.length + (psk != null ? psk.length : 0))];
            System.arraycopy(clientRandom, 0, combined, 0, clientRandom.length);
            System.arraycopy(serverRandom, 0, combined, clientRandom.length, serverRandom.length);
            if (psk != null) System.arraycopy(psk, 0, combined, clientRandom.length + serverRandom.length, psk.length);
            return combined;
        }
    }

    public static class CertificateValidator {
        private final List<String> trustStore = new CopyOnWriteArrayList<>(List.of("CA-Root-1", "CA-Root-2"));

        public boolean validateChain(List<CertificateEntry> chain) {
            if (chain.isEmpty()) return false;
            var leaf = chain.get(0);
            if (Instant.now().isBefore(leaf.notBefore()) || Instant.now().isAfter(leaf.notAfter())) {
                System.out.println("Certificate expired or not yet valid");
                return false;
            }
            var last = chain.get(chain.size() - 1);
            boolean trusted = trustStore.contains(last.issuer());
            System.out.println("Chain length: " + chain.size() + ", trusted: " + trusted);
            return trusted;
        }
    }

    public static class SessionTicketManager {
        private final Map<String, NewSessionTicket> tickets = new ConcurrentHashMap<>();

        public NewSessionTicket issueTicket() {
            var ticket = new NewSessionTicket(randomBytes(32), 7200, new Random().nextInt(100000));
            tickets.put(Base64.getEncoder().encodeToString(ticket.ticket()), ticket);
            return ticket;
        }

        public boolean resume(String encodedTicket) {
            return tickets.containsKey(encodedTicket);
        }

        private byte[] randomBytes(int len) { var b = new byte[len]; new Random().nextBytes(b); return b; }
    }

    public static void main(String[] args) {
        System.out.println("=== TLS 1.3 Handshake ===");
        var handshake = new TlsHandshake();
        var result = handshake.perform(false);
        System.out.println("ClientHello: " + result.clientHello().supported().size() + " cipher suites");
        System.out.println("Selected: " + result.serverHello().selected().name());

        var zeroRttResult = handshake.perform(true);
        System.out.println("0-RTT handshake completed, master secret length: " + zeroRttResult.masterSecret().length);

        System.out.println("\n=== Certificate Validation ===");
        var validator = new CertificateValidator();
        var chain = List.of(
            new CertificateEntry("www.example.com", "Example CA", Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                Instant.now().plus(300, java.time.temporal.ChronoUnit.DAYS), List.of("www.example.com"), new byte[0]),
            new CertificateEntry("Example CA", "CA-Root-1", Instant.now().minus(365, java.time.temporal.ChronoUnit.DAYS),
                Instant.now().plus(365 * 3, java.time.temporal.ChronoUnit.DAYS), List.of(), new byte[0])
        );
        System.out.println("Chain valid: " + validator.validateChain(chain));

        System.out.println("\n=== Session Tickets ===");
        var tickets = new SessionTicketManager();
        var ticket = tickets.issueTicket();
        var encoded = Base64.getEncoder().encodeToString(ticket.ticket());
        System.out.println("Ticket issued, resume: " + tickets.resume(encoded));
        System.out.println("Unknown ticket resume: " + tickets.resume("fake-ticket"));
    }
}
