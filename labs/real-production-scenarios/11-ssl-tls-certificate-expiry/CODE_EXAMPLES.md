# Lab 11 — SSL/TLS Certificate Expiry: Code Examples

## Certificate Expiry Checker

```java
import javax.net.ssl.*;
import java.security.cert.*;
import java.net.*;
import java.time.*;
import java.time.format.*;

public class CertificateChecker {
    private final String host;
    private final int port;

    public CertificateChecker(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public CertInfo check() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new TrustAllManager()}, null);
        SSLSocketFactory factory = sslContext.getSocketFactory();

        try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
            socket.startHandshake();
            Certificate[] certs = socket.getSession().getPeerCertificates();
            X509Certificate cert = (X509Certificate) certs[0];

            return new CertInfo(
                host,
                cert.getSubjectX500Principal().getName(),
                cert.getIssuerX500Principal().getName(),
                cert.getNotBefore().toInstant(),
                cert.getNotAfter().toInstant(),
                cert.getSigAlgName(),
                cert.getSerialNumber().toString(),
                Duration.between(Instant.now(), cert.getNotAfter().toInstant()).toDays(),
                cert.getSubjectAlternativeNames()
            );
        }
    }

    private static class TrustAllManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] c, String a) {}
        public void checkServerTrusted(X509Certificate[] c, String a) {}
        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    }

    public record CertInfo(String host, String subject, String issuer,
                           Instant notBefore, Instant notAfter, String sigAlg,
                           String serial, long daysUntilExpiry,
                           java.util.Collection<java.util.List<?>> subjectAltNames) {}

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: CertificateChecker <host> [port]");
            System.exit(1);
        }
        String host = args[0];
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 443;

        CertificateChecker checker = new CertificateChecker(host, port);
        CertInfo info = checker.check();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        System.out.println("=== TLS Certificate Info ===");
        System.out.println("Host: " + info.host());
        System.out.println("Subject: " + info.subject());
        System.out.println("Issuer: " + info.issuer());
        System.out.println("Not Before: " + fmt.format(info.notBefore().atZone(ZoneId.systemDefault())));
        System.out.println("Not After: " + fmt.format(info.notAfter().atZone(ZoneId.systemDefault())));
        System.out.println("Signature Algorithm: " + info.sigAlg());
        System.out.println("Serial: " + info.serial());
        System.out.println("Days until expiry: " + info.daysUntilExpiry());

        if (info.daysUntilExpiry() < 0) {
            System.err.println("CRITICAL: Certificate has EXPIRED!");
            System.exit(2);
        } else if (info.daysUntilExpiry() < 30) {
            System.out.println("WARNING: Certificate expires in " + info.daysUntilExpiry() + " days");
        } else {
            System.out.println("OK: Certificate valid for " + info.daysUntilExpiry() + " more days");
        }
    }
}
```

## Certificate Expiry Prometheus Exporter

```java
import io.prometheus.client.*;
import io.prometheus.client.exporter.HTTPServer;
import java.util.*;

public class CertificateMetricsExporter {
    static final Gauge certDaysToExpiry = Gauge.build()
        .name("tls_certificate_days_until_expiry")
        .help("Days until TLS certificate expires")
        .labelNames("host", "subject")
        .register();

    static final Gauge certExpired = Gauge.build()
        .name("tls_certificate_expired")
        .help("Whether TLS certificate has expired (1 = expired)")
        .labelNames("host")
        .register();

    private final List<String> hosts;

    public CertificateMetricsExporter(List<String> hosts) {
        this.hosts = hosts;
    }

    public void update() {
        for (String host : hosts) {
            try {
                CertificateChecker checker = new CertificateChecker(host, 443);
                CertificateChecker.CertInfo info = checker.check();
                certDaysToExpiry.labels(host, info.subject()).set(info.daysUntilExpiry());
                certExpired.labels(host).set(info.daysUntilExpiry() < 0 ? 1 : 0);
            } catch (Exception e) {
                certDaysToExpiry.labels(host, "unknown").set(-1);
                certExpired.labels(host).set(1);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<String> hosts = List.of("example.com", "api.example.com", "app.example.com");
        CertificateMetricsExporter exporter = new CertificateMetricsExporter(hosts);
        HTTPServer server = new HTTPServer(9090);

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                exporter.update();
            }
        }, 0, 3600000); // Every hour

        System.out.println("Certificate metrics exporter running on :9090");
    }
}
```
