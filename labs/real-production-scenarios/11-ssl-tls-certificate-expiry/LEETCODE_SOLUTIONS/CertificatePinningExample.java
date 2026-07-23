package com.prod.solutions.ssltls;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HexFormat;
import java.util.List;
import java.util.ArrayList;

/**
 * Demonstrates certificate pinning: verifying that the server's
 * certificate matches a known fingerprint. This prevents MITM
 * attacks even if a CA is compromised.
 *
 * Note: Pinning has risks — if the server rotates its certificate
 * before the client is updated, the service becomes unavailable.
 * Use SPKI (Subject Public Key Info) pinning for better rotation
 * tolerance.
 */
public class CertificatePinningExample {

    static class CertificatePin {
        final String hostname;
        final String expectedFingerprint;
        final String algorithm;

        CertificatePin(String hostname, String expectedFingerprint, String algorithm) {
            this.hostname = hostname;
            this.expectedFingerprint = expectedFingerprint;
            this.algorithm = algorithm;
        }
    }

    static class PinningVerifier {
        private final List<CertificatePin> pins;

        PinningVerifier(List<CertificatePin> pins) {
            this.pins = pins;
        }

        boolean verifyPin(String hostname, byte[] certificateBytes) {
            CertificatePin pin = findPin(hostname);
            if (pin == null) {
                System.out.printf("  No pin configured for %s, skipping pinning check%n", hostname);
                return true;
            }

            try {
                MessageDigest md = MessageDigest.getInstance(pin.algorithm);
                byte[] fingerprint = md.digest(certificateBytes);
                String fingerprintHex = HexFormat.of().formatHex(fingerprint);

                boolean matches = fingerprintHex.equals(pin.expectedFingerprint);
                if (matches) {
                    System.out.printf("  PIN VERIFIED for %s%n", hostname);
                } else {
                    System.out.printf("  PIN MISMATCH for %s!%n", hostname);
                    System.out.printf("    Expected: %s%n", pin.expectedFingerprint);
                    System.out.printf("    Got:      %s%n", fingerprintHex);
                    System.out.println("  >>> Possible MITM attack or certificate rotation! <<<");
                }
                return matches;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        private CertificatePin findPin(String hostname) {
            return pins.stream()
                    .filter(p -> p.hostname.equals(hostname))
                    .findFirst()
                    .orElse(null);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Certificate Pinning Demo ===\n");

        List<CertificatePin> pins = new ArrayList<>();
        pins.add(new CertificatePin("api.example.com",
                "a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2", "SHA-256"));
        pins.add(new CertificatePin("backup-pin.example.com",
                "b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3", "SHA-256"));

        PinningVerifier verifier = new PinningVerifier(pins);

        // Simulated valid certificate
        System.out.println("--- Valid Certificate ---");
        byte[] validCert = new byte[256];
        new Random(42).nextBytes(validCert);
        verifier.verifyPin("api.example.com", validCert);

        // Simulated MITM certificate
        System.out.println("\n--- MITM Attack Simulation ---");
        byte[] mitmCert = new byte[256];
        new Random(99).nextBytes(mitmCert);
        verifier.verifyPin("api.example.com", mitmCert);

        // Backup pin
        System.out.println("\n--- Backup PIN (for cert rotation) ---");
        byte[] rotatedCert = new byte[256];
        new Random(42).nextBytes(rotatedCert);
        verifier.verifyPin("backup-pin.example.com", rotatedCert);

        System.out.printf("%nCertificate pinning:%n");
        System.out.println("  + Prevents MITM even with compromised CA");
        System.out.println("  - Risky if not managed properly (can cause outages)");
        System.out.println("  - Always have backup pins for rotation");
        System.out.println("  - Use SPKI pinning (not certificate pinning)");
    }
}

class Random extends java.util.Random {
    Random(long seed) { super(seed); }
}
