# Code Deep Dive: 14-container-security

## ContainerSecurityScanner.java

### Purpose
Integrates container image vulnerability scanning into Java CI/CD pipelines, parsing scan results and enforcing security policies.

### Key Implementation Details

**Scanning Pipeline**:
1. Pull container image from registry
2. Run Trivy/Grype scanner
3. Parse JSON/XML scan results
4. Apply policy rules (severity thresholds, CVE blacklist)
5. Generate report and determine pass/fail

### Class Structure

The ContainerSecurityScanner implements:
- scanImage(String imageName, String imageTag) - Execute vulnerability scan
- parseScanResults(String rawOutput) - Parse scanner Output
- evaluatePolicy(ScanResult result, SecurityPolicy policy) - Policy evaluation
- generateReport(ScanResult result) - HTML/JSON report generation

### Policy Rules

- Severity threshold: Fail on any CRITICAL or HIGH
- CVE exception list: Known false positives
- Base image policy: Only approved base images
- Package policy: Prohibited packages/libraries
- Age policy: No vulnerabilities older than 30 days unpatched

### Security Context Configuration

`java
SecurityContext securityContext = new SecurityContext();
securityContext.setRunAsUser(10001);
securityContext.setReadOnlyRootFilesystem(true);
securityContext.setCapabilities(dropAllCapabilities());
securityContext.setSeccompProfile("runtime/default");
`

### Image Signing (Cosign)

- Sign: cosign sign --key key.pair image:tag
- Verify: cosign verify --key public-key image:tag
- Attest: cosign attest --predicate predicate.json image:tag
"@

System.Collections.Hashtable['15'] = @"
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

`java
ZapClient client = new ZapClient("localhost", 8080);
client.spiderScan("https://target.api");
client.activeScan("https://target.api");
List<Alert> alerts = client.getAlerts();
`

### Security Considerations

- Never test against production without authorization
- Rate limit scans to avoid DoS
- Handle false positives in reporting
- Signed commit for policy changes
- Scan results stored with timestamps

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
