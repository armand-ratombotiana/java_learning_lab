# Solution: Automated SSL/TLS Certificate Management

## Step 1: Immediate Certificate Issuance (Cloudflare Origin CA)

Since Let's Encrypt rate limits were exhausted, use Cloudflare Origin CA as an emergency backup:

```bash
# Generate Cloudflare Origin CA certificate via API
curl -X POST https://api.cloudflare.com/client/v4/certificates \
  -H "Authorization: Bearer $CLOUDFLARE_API_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "hostnames": ["*.acmecorp.com", "acmecorp.com"],
    "request_type": "origin-rsa",
    "requested_validity": 15
  }'

# Install on NGINX
sudo cp origin-cert.pem /etc/ssl/certs/acmecorp-origin.pem
sudo cp origin-key.pem /etc/ssl/private/acmecorp-origin.key
```

## Step 2: Configure Automated Renewal with certbot

### Systemd Service

```ini
# /etc/systemd/system/certbot-renewal.service
[Unit]
Description=Certbot Renewal
Documentation=https://certbot.eff.org/docs/

[Service]
Type=oneshot
ExecStart=/usr/bin/certbot renew --quiet --no-self-upgrade
ExecStartPost=/bin/systemctl reload nginx
```

### Systemd Timer

```ini
# /etc/systemd/system/certbot-renewal.timer
[Unit]
Description=Daily Certbot Renewal Timer

[Timer]
OnCalendar=daily
Persistent=true
RandomizedDelaySec=3600

[Install]
WantedBy=timers.target
```

### Verification

```bash
sudo systemctl enable certbot-renewal.timer
sudo systemctl start certbot-renewal.timer
sudo certbot renew --dry-run
```

## Step 3: Java Code — Certificate Expiry Monitoring Utility

```java
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;

public class CertificateExpiryMonitor {

    private static final int WARN_DAYS = 30;
    private static final int CRITICAL_DAYS = 14;

    public static class CertificateCheckResult {
        private final String hostname;
        private final X509Certificate cert;
        private final long daysToExpiry;
        private final Severity severity;

        public enum Severity { OK, WARN, CRITICAL, EXPIRED }

        public CertificateCheckResult(String hostname, X509Certificate cert) {
            this.hostname = hostname;
            this.cert = cert;
            long diff = cert.getNotAfter().toInstant().toEpochMilli()
                - Instant.now().toEpochMilli();
            this.daysToExpiry = Duration.ofMillis(diff).toDays();
            this.severity = computeSeverity();
        }

        private Severity computeSeverity() {
            if (daysToExpiry <= 0) return Severity.EXPIRED;
            if (daysToExpiry <= CRITICAL_DAYS) return Severity.CRITICAL;
            if (daysToExpiry <= WARN_DAYS) return Severity.WARN;
            return Severity.OK;
        }

        public String getHostname() { return hostname; }
        public long getDaysToExpiry() { return daysToExpiry; }
        public Severity getSeverity() { return severity; }

        @Override
        public String toString() {
            return String.format("[%s] %s: %d days to expiry (severity=%s)",
                severity, hostname, daysToExpiry, severity);
        }
    }

    public CertificateCheckResult checkCertificate(String hostname, int port) {
        try {
            SSLContext context = SSLContext.getInstance("TLSv1.3");
            context.init(null, null, null);
            SSLSocketFactory factory = context.getSocketFactory();

            try (SSLSocket socket = (SSLSocket) factory.createSocket(hostname, port)) {
                socket.setSoTimeout(5000);
                socket.startHandshake();
                SSLSession session = socket.getSession();
                X509Certificate cert = (X509Certificate) session.getPeerCertificates()[0];
                return new CertificateCheckResult(hostname, cert);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to check certificate for " + hostname, e);
        }
    }

    public void checkCertificates(String[] endpoints) {
        for (String endpoint : endpoints) {
            String[] parts = endpoint.split(":");
            String hostname = parts[0];
            int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 443;
            CertificateCheckResult result = checkCertificate(hostname, port);
            System.out.println(result);
            if (result.getSeverity() == CertificateCheckResult.Severity.EXPIRED) {
                throw new RuntimeException("EXPIRED certificate: " + hostname);
            }
        }
    }

    public static void main(String[] args) {
        CertificateExpiryMonitor monitor = new CertificateExpiryMonitor();
        String[] endpoints = {
            "api.acmecorp.com",
            "www.acmecorp.com",
            "app.acmecorp.com:8443"
        };
        monitor.checkCertificates(endpoints);
    }
}
```

## Step 4: Prometheus blackbox_exporter Configuration

```yaml
# /etc/prometheus/blackbox.yml
modules:
  http_2xx:
    prober: http
    http:
      valid_http_versions: ["HTTP/1.1", "HTTP/2"]
      valid_status_codes: [200]
      follow_redirects: true
      preferred_ip_protocol: "ip4"

  ssl_certificate_expiry:
    prober: http
    http:
      valid_ssl: true
      fail_if_not_ssl: true
      preferred_ip_protocol: "ip4"
```

## Step 5: Prometheus Alert Rules

```yaml
# /etc/prometheus/rules/ssl.yml
groups:
  - name: ssl_certificates
    rules:
      - alert: SSLCertExpiringSoon
        expr: probe_ssl_earliest_cert_expiry{job="blackbox"} - time() < 2592000
        for: 1h
        labels:
          severity: warning
          team: sre
        annotations:
          summary: "SSL certificate for {{ $labels.instance }} expires in {{ $value | humanizeDuration }}"
          runbook: "https://runbook.acmecorp.com/ssl-renewal"

      - alert: SSLCertExpiringCritical
        expr: probe_ssl_earliest_cert_expiry{job="blackbox"} - time() < 1209600
        for: 5m
        labels:
          severity: critical
          team: sre
        annotations:
          summary: "SSL certificate for {{ $labels.instance }} expires in less than 14 days"
          runbook: "https://runbook.acmecorp.com/ssl-renewal"

      - alert: SSLCertExpired
        expr: probe_ssl_earliest_cert_expiry{job="blackbox"} - time() < 0
        for: 0m
        labels:
          severity: critical
          team: sre
        annotations:
          summary: "SSL certificate for {{ $labels.instance }} has EXPIRED"
          runbook: "https://runbook.acmecorp.com/ssl-expired"
```

## Step 6: Java Code — Let's Encrypt ACME Client with Rate Limit Awareness

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class LetEncryptRateLimitChecker {

    private static final String ACME_DIRECTORY = "https://acme-v02.api.letsencrypt.org/directory";
    private final HttpClient httpClient;

    public LetEncryptRateLimitChecker() {
        this.httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    public static class RateLimitStatus {
        private final int certificatesIssued;
        private final int certificatesAllowed;
        private final long resetEpoch;

        public RateLimitStatus(int issued, int allowed, long reset) {
            this.certificatesIssued = issued;
            this.certificatesAllowed = allowed;
            this.resetEpoch = reset;
        }

        public boolean isExhausted() {
            return certificatesIssued >= certificatesAllowed;
        }

        public int getRemaining() {
            return certificatesAllowed - certificatesIssued;
        }

        public long getResetEpoch() { return resetEpoch; }

        @Override
        public String toString() {
            return String.format("Rate limit: %d/%d used, reset at %d",
                certificatesIssued, certificatesAllowed, resetEpoch);
        }
    }

    public RateLimitStatus checkRateLimit(String domain) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ACME_DIRECTORY))
                .method("REPHRASE", HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<Void> response = httpClient.send(request,
                HttpResponse.BodyHandlers.discarding());

            int issued = Integer.parseInt(
                response.headers().firstValue("Rephrase-Certificates-Issued")
                    .orElse("0"));
            int allowed = Integer.parseInt(
                response.headers().firstValue("Rephrase-Certificates-Allowed")
                    .orElse("50"));
            long reset = Long.parseLong(
                response.headers().firstValue("Rephrase-Certificates-Reset")
                    .orElse("0"));

            return new RateLimitStatus(issued, allowed, reset);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check Let's Encrypt rate limits", e);
        }
    }

    public static void main(String[] args) {
        LetEncryptRateLimitChecker checker = new LetEncryptRateLimitChecker();
        RateLimitStatus status = checker.checkRateLimit("acmecorp.com");
        System.out.println("Rate limit status: " + status);

        if (status.isExhausted()) {
            System.err.println("WARNING: Rate limit exhausted! Use Cloudflare Origin CA as fallback.");
            System.err.println("Remaining certs: " + status.getRemaining());
            System.err.println("Rate limit resets at epoch: " + status.getResetEpoch());
        }
    }
}
```

## Step 7: Multi-CA Strategy Implementation

```java
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class CertificateAuthorityManager {

    public enum CertificateAuthority {
        LETS_ENCRYPT("Let's Encrypt", "ISRG Root X1", 90),
        CLOUDFLARE("Cloudflare", "Cloudflare Origin CA", 5475),
        GOOGLE_TRUST("Google Trust Services", "GTS Root R1", 365),
        MICROSOFT("Microsoft", "Microsoft RSA Root CA", 365);

        private final String name;
        private final String rootCertName;
        private final int validityDays;

        CertificateAuthority(String name, String rootCertName, int validityDays) {
            this.name = name;
            this.rootCertName = rootCertName;
            this.validityDays = validityDays;
        }

        public String getName() { return name; }
        public String getRootCertName() { return rootCertName; }
        public int getValidityDays() { return validityDays; }
    }

    public static class MultiCACertificate {
        private final String domain;
        private final List<CertificateAuthority> authorities;

        public MultiCACertificate(String domain) {
            this.domain = domain;
            this.authorities = new ArrayList<>();
        }

        public void addAuthority(CertificateAuthority ca) {
            authorities.add(ca);
        }

        public List<CertificateAuthority> getAuthorities() {
            return authorities;
        }

        public boolean hasValidCertificate() {
            return !authorities.isEmpty();
        }

        @Override
        public String toString() {
            return String.format("Certificate for %s: %d CAs configured",
                domain, authorities.size());
        }
    }

    public MultiCACertificate configureMultiCA(String domain) {
        MultiCACertificate multiCA = new MultiCACertificate(domain);
        multiCA.addAuthority(CertificateAuthority.LETS_ENCRYPT);
        multiCA.addAuthority(CertificateAuthority.CLOUDFLARE);
        multiCA.addAuthority(CertificateAuthority.GOOGLE_TRUST);
        multiCA.addAuthority(CertificateAuthority.MICROSOFT);
        return multiCA;
    }

    public static void main(String[] args) {
        CertificateAuthorityManager manager = new CertificateAuthorityManager();
        MultiCACertificate cert = manager.configureMultiCA("acmecorp.com");
        System.out.println(cert);
        cert.getAuthorities().forEach(ca ->
            System.out.printf("  - %s (validity: %d days, root: %s)%n",
                ca.getName(), ca.getValidityDays(), ca.getRootCertName()));
    }
}
```

## Verification Commands

```bash
# Check certificate dates
openssl s_client -connect api.acmecorp.com:443 -servername api.acmecorp.com \
  2>&1 | openssl x509 -noout -dates -subject -issuer

# Verify certificate chain
openssl s_client -connect api.acmecorp.com:443 -servername api.acmecorp.com \
  2>&1 | openssl x509 -noout -ocsp_uri

# Check OCSP response
openssl ocsp -url http://r3.o.lencr.org \
  -issuer /path/to/issuer.pem -cert /path/to/cert.pem

# Check Certificate Transparency logs
openssl s_client -connect api.acmecorp.com:443 -serveless api.acmecorp.com \
  2>&1 | grep -E "signed_certificate_timestamp"

# Verify Let's Encrypt renewal
sudo certbot renew --dry-run

# Check Prometheus alert
curl -s http://localhost:9090/api/v1/alerts | jq '.data.alerts[] | select(.labels.alertname=="SSLCertExpiringSoon")'
```
