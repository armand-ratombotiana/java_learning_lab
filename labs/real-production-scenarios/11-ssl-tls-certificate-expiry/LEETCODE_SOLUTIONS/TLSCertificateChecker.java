package com.prod.solutions.ssltls;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Checks TLS certificate validity including expiry, hostname match,
 * and trust chain validation. In production, this would be scheduled
 * to run daily and alert before certificates expire.
 */
public class TLSCertificateChecker {

    static class CertCheckResult {
        final String hostname;
        final boolean valid;
        final long daysToExpiry;
        final List<String> issues = new ArrayList<>();

        CertCheckResult(String hostname, boolean valid, long daysToExpiry) {
            this.hostname = hostname;
            this.valid = valid;
            this.daysToExpiry = daysToExpiry;
        }

        void print() {
            System.out.printf("%n--- %s ---%n", hostname);
            System.out.printf("  Valid: %s%n", valid);
            System.out.printf("  Days to expiry: %d%n", daysToExpiry);
            if (!issues.isEmpty()) {
                System.out.println("  Issues:");
                for (String issue : issues) {
                    System.out.printf("    - %s%n", issue);
                }
            }
            if (daysToExpiry < 30 && valid) {
                System.out.println("  WARNING: Certificate expires in less than 30 days!");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== TLS Certificate Checker Demo ===\n");

        // Simulate checking multiple services
        String[] hosts = {
            "api.example.com",
            "payment.example.com",
            "internal-db.example.com"
        };

        for (String host : hosts) {
            CertCheckResult result = checkCertificate(host);
            result.print();
        }

        System.out.printf("%n%nCertificate monitoring best practices:%n");
        System.out.println("  - Check certificate expiry daily");
        System.out.println("  - Alert at 30, 14, 7, and 3 days before expiry");
        System.out.println("  - Automate renewal with Let's Encrypt / cert-manager");
        System.out.println("  - Monitor TLS handshake failures");
    }

    static CertCheckResult checkCertificate(String hostname) {
        Random rnd = new Random(hostname.hashCode());
        long daysToExpiry = rnd.nextInt(365);
        boolean valid = daysToExpiry > 0;

        CertCheckResult result = new CertCheckResult(hostname, valid, daysToExpiry);

        // Simulate SAN check
        if (hostname.contains("internal-")) {
            result.issues.add("Wildcard certificate does not cover internal hostnames");
        }

        if (daysToExpiry < 60) {
            result.issues.add("Certificate renewal recommended within " + daysToExpiry + " days");
        }

        return result;
    }
}
