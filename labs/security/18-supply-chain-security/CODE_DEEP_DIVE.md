# Code Deep Dive: 18-supply-chain-security

## SupplyChainValidator.java

### Purpose
Validates software supply chain integrity through SBOM generation, dependency scanning, signature verification, and provenance checking.

### Key Implementation Details

**Validation Pipeline**:
1. Generate CycloneDX SBOM from Maven dependencies
2. Scan dependencies against CVE database
3. Verify artifact signatures via Sigstore/Cosign
4. Check SLSA provenance attestations
5. Enforce policy rules (license compliance, allowed versions)

### Class Structure

The SupplyChainValidator implements:
- generateSbom(String projectPath) - CycloneDX SBOM generation
- scanVulnerabilities(Sbom sbom) - Dependency vulnerability scanning
- erifySignatures(BuildArtifact artifact) - Cosign/Sigstore verification
- checkSlsaProvenance(BuildAttestation attestation) - SLSA level check
- evaluatePolicy(Sbom sbom, Policy policy) - Policy enforcement

### SBOM Generation

- Parse Maven POM and resolve transitive dependencies
- Generate CycloneDX 1.4 JSON format
- Include component hashes for integrity
- Enumerate all direct and transitive dependencies
- Record dependency relationship tree

### Sigstore Verification

- Fetch certificate from Fulcio
- Verify certificate chain against Fulcio root
- Check Rekor transparency log inclusion
- Verify signature against public key from certificate
- Validate certificate identity matches expected issuer

### SLSA Provenance Verification

- Check provenance statement exists and is signed
- Verify builder identity
- Validate source repository reference
- Check build parameters for hermetic/reproducible builds
- Verify materials list completeness

### Policy Rules

- Version pinning: No floating versions (SNAPSHOT, LATEST)
- License compliance: Approved license list
- Known vulnerabilities: Zero tolerance for CRITICAL
- Age policy: Dependencies less than 7 days old flagged
- Signature requirement: All artifacts must be signed

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
