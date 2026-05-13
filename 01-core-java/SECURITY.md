# Security in Core Java

## Fundamental Security Practices

### 1. Input Validation
- Never trust user input
- Validate on server side (client-side is insufficient)
- Use whitelist over blacklist validation
- Sanitize before using in queries or commands

### 2. Data Protection
- Encrypt sensitive data at rest
- Use TLS for data in transit
- Never log sensitive information
- Mask PII in logs and error messages

### 3. Access Control
- Follow principle of least privilege
- Use proper access modifiers
- Avoid public fields
- Implement proper authentication

### 4. Secure Coding Patterns
- Use PreparedStatement for SQL
- Avoid XML External Entity (XXE) attacks
- Validate file paths to prevent path traversal
- Use secure random number generators

## Common Vulnerabilities

- SQL Injection
- XSS (Cross-Site Scripting)
- Deserialization vulnerabilities
- Insecure deserialization
- Using broken cryptography

## Security Libraries

- Bouncy Castle for cryptography
- OWASP Java Encoder for output encoding
- Google Guava for secure utilities