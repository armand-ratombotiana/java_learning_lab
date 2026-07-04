# Observability - SECURITY

## Sensitive Data in Logs

### Never Log Sensitive Information
```java
// WRONG: Logging PII
log.info("User login: email={}, password={}", email, password);

// RIGHT: Log only necessary, non-sensitive data
log.info("User login: userId={}", userId);

// Mask sensitive data
log.info("Payment processed: card={}", maskCardNumber(cardNumber));
```

### Log Sanitization
```java
@Component
public class LogSanitizer {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern CARD_PATTERN = Pattern.compile("\\d{16}");

    public String sanitize(String message) {
        message = EMAIL_PATTERN.matcher(message).replaceAll("***@***");
        message = CARD_PATTERN.matcher(message).replaceAll("****-****-****-****");
        return message;
    }
}
```

## Access Control for Observability Tools

```yaml
# Grafana authentication
grafana:
  auth:
    oauth:
      client_id: ${OAUTH_CLIENT_ID}
      client_secret: ${OAUTH_CLIENT_SECRET}
    viewer_role: Viewer  # read-only by default
    editor_role: Editor  # for dashboard creators
    admin_role: Admin    # for infrastructure team
```

## Monitoring for Security Events

### Security Metrics to Track
- Failed login attempts (rate spike → brute force)
- 403/401 response rates (unauthorized access attempts)
- Suspicious parameter values (SQL injection, XSS attempts)
- Unusual request sizes (potential data exfiltration)
- Rate limit exceeded counts (potential DoS)

### Example Security Alert
```yaml
- alert: BruteForceAttempt
  expr: rate(http_requests_total{status="401"}[5m]) > 10
  labels:
    severity: warning
  annotations:
    summary: "Possible brute force attack - {{ $value }} 401/s"
```

## Trace Data Security

```java
// Never include sensitive data in span attributes
Span.current().setAttribute("user.email", email);  // WRONG
Span.current().setAttribute("user.id", userId);    // OK
```

## Compliance Considerations

- **GDPR**: Log retention limits, data anonymization
- **PCI-DSS**: No credit card data in logs or traces
- **HIPAA**: No protected health information in observability data
- **SOX**: Audit trails must be immutable and retained
