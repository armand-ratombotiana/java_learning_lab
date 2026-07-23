package com.prod.solutions.ssltls;

import javax.net.ssl.*;
import java.net.URI;
import java.net.http.*;
import java.security.cert.X509Certificate;

/**
 * Demonstrates an HTTPS client that validates the server certificate
 * chain, hostname, and expiry. This is the correct way to make
 * HTTPS connections with proper certificate validation.
 *
 * BUG: Many applications disable certificate validation for convenience
 * (e.g., trust-all SSLContext). This example shows the right way.
 */
public class HTTPSCertificateExample {

    public static void main(String[] args) {
        System.out.println("=== HTTPS Certificate Validation Demo ===\n");

        // Test with a valid HTTPS endpoint
        testConnection("https://google.com");

        // Test with an expired certificate simulation
        testConnection("https://expired.example.com");

        System.out.println("\nKey takeaways:");
        System.out.println("  - NEVER disable certificate validation in production");
        System.out.println("  - ALWAYS validate hostname matches SAN in certificate");
        System.out.println("  - Implement proper certificate pinning for critical services");
        System.out.println("  - Monitor certificate expiry and alert before expiration");
    }

    static void testConnection(String url) {
        System.out.printf("--- Testing: %s ---%n", url);
        try {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(java.time.Duration.ofSeconds(5))
                    .GET()
                    .build();

            long start = System.nanoTime();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            long elapsed = (System.nanoTime() - start) / 1_000_000;

            System.out.printf("  Status: %d%n", response.statusCode());
            System.out.printf("  Time: %d ms%n", elapsed);
            System.out.printf("  TLS version: %s%n", response.version());
            System.out.println("  Certificate: VALID");

        } catch (javax.net.ssl.SSLHandshakeException e) {
            System.out.println("  TLS HANDSHAKE FAILED: " + e.getMessage());
            System.out.println("  Certificate: INVALID or EXPIRED");
        } catch (java.net.http.HttpConnectTimeoutException e) {
            System.out.println("  CONNECTION TIMEOUT");
        } catch (Exception e) {
            System.out.printf("  ERROR: %s%n", e.getMessage());
        }
    }
}
