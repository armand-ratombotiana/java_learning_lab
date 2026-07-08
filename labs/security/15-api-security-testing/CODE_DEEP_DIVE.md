# Code Deep Dive: 15-api-security-testing

## SecurityTestRunner.java

### Purpose
Orchestrates automated security testing including SAST analysis, dependency scanning, and API fuzzing.

### Key Implementation Details

**SAST Integration**:
1. Load Java source files from project
2. Run SpotBugs with FindSecBugs plugin
3. Parse bug reports and filter security-related
4. Score findings by confidence and severity
5. Fail build if threshold exceeded

### Class Structure

The SecurityTestRunner implements:
- unSastAnalysis(String projectPath) - Static analysis
- unDependencyScan(String pomPath) - Dependency vulnerability check
- unFuzzingTest(String apiEndpoint) - API fuzzing
- generateSecurityReport() - Consolidated report

### Fuzzing Strategy

1. **Input Fuzzing**: Inject special characters, SQL injection payloads, XSS vectors
2. **Parameter Fuzzing**: Extra params, missing params, type confusion
3. **Header Manipulation**: Custom headers, cache headers, CORS headers
4. **Authentication Bypass**: JWT none algorithm, expired tokens, missing auth

### OWASP ZAP Integration

ZAP can be integrated via its REST API for automated DAST scanning:
- Spider scan for crawling
- Active scan for vulnerability detection
- AJAX spider for JavaScript-heavy apps
- Passive scanning for baseline analysis
- API scan for REST/GraphQL endpoints

### SAST Rule Configuration

Custom FindSecBugs rules can detect:
- Hardcoded credentials (password, secret, key patterns)
- Weak cryptography (DES, MD5, SHA1, ECB mode)
- SQL injection (unsafe string concatenation)
- Path traversal (unsanitized file paths)
- LDAP injection (unsanitized LDAP queries)
- Command injection (Runtime.exec, ProcessBuilder)
- XSS (unsafe output in templates)

### CI/CD Integration Pipeline

`yaml
security-test:
  stage: security
  script:
    - mvn com.github.spotbugs:spotbugs-maven-plugin:spotbugs
    - mvn org.owasp:dependency-check-maven:check
    - zap-api-scan.py -t https://staging.api -f openapi
  artifacts:
    reports:
      security: gl-security-report.json
`

### Test Data Generation

Fuzzing payloads are generated from multiple sources:
1. Generic injection patterns (SQL, XSS, command injection)
2. Protocol-specific malformations (HTTP, JSON, XML)
3. Business logic violations (negative amounts, future dates)
4. Edge cases (null, empty, very large inputs)
5. Encoding variations (Unicode, double encoding, mixed case)

### Reporting

The consolidated security report includes:
- Executive summary with risk score
- Vulnerability details with CVSS scores
- Affected endpoints and parameters
- Remediation recommendations
- Scan configuration and scope
- Historical comparison with previous scans
- False positive annotations

### Common Patterns

The codebase demonstrates several important security patterns:

1. **Defensive Programming**: Validate all inputs, assume nothing
2. **Separation of Concerns**: Security logic separated from business logic
3. **Fail Secure**: Default to denying access on any error
4. **Audit Trail**: Log all security-relevant events
5. **Minimize Attack Surface**: Only expose what is necessary

### Error Handling

Proper security error handling:
`java
try {
    securityOperation();
} catch (SecurityException e) {
    log.warn("Security operation failed: {}", e.getMessage());
    throw new AccessDeniedException("Access denied");
}
`

### Testing Considerations

Key testing scenarios for this code:
1. Valid authentication flow
2. Invalid credentials rejection
3. Expired token handling
4. Missing/invalid signature
5. Authorization boundary testing
6. Race condition testing
7. Error message information leakage
8. Performance under load

### Thread Safety

Thread safety is achieved through:
- Immutable objects where possible
- Thread-local storage for security context
- Concurrent collections for shared state
- Atomic operations for counters
- Reentrant locks for critical sections

### Configuration

Configuration loading follows this order:
1. Command line arguments (highest priority)
2. Environment variables
3. Application properties file
4. Profile-specific overrides
5. Default values (lowest priority)

This ensures flexibility across different environments while maintaining secure defaults.

### Security Hardening

Additional hardening measures applied:
1. Disable unnecessary features
2. Minimum permission service accounts
3. Resource limits on all operations
4. Timeout configuration for all external calls
5. Retry limits with exponential backoff
6. Input size limits
7. Rate limiting per client
8. IP allowlisting for admin functions

### Integration Points

This component integrates with:
- Spring Security filter chain
- Authentication providers
- User details service
- Token repository
- Audit event publisher
- Metrics exporter (Micrometer)
- Health indicators (Actuator)
- External identity providers
