# Code Deep Dive: 12-webauthn-passkeys

## WebAuthnRegistrationService.java

### Purpose
Handles WebAuthn credential registration including challenge generation, attestation verification, and credential storage.

### Key Implementation Details

**Registration Flow**:
1. Generate random challenge (32 bytes via SecureRandom)
2. Build PublicKeyCredentialCreationOptions with RP info
3. Client creates credential via WebAuthn API
4. Server verifies attestation and stores public key
5. Associate credential with user account

### Class Structure

The WebAuthnRegistrationService implements:
- startRegistration(String userId, String userName) - Creates registration options
- completeRegistration(RegistrationRequest request) - Verifies attestation, stores credential
- erifyAttestation(AttestationObject attestation, byte[] challenge) - Verifies authenticator response
- storeCredential(Credential credential) - Persists public key and metadata

### COSE Key Parsing

COSE (CBOR Object Signing and Encryption) key format:
- Key type: EC2 (P-256, P-384), OKP (Ed25519), RSA
- Algorithm: ES256 (ECDSA w/ SHA-256), RS256, EdDSA
- Key parameters: x-coordinate, y-coordinate, curve

### Attestation Verification

- None: Skip attestation (accept self-attestation)
- Basic: Verify certificate chain against trusted root
- Self: Verify using credential public key
- ECDAA: Verify using issuer public key (anonymity-preserving)
- Privacy CA: Verify using privacy CA certificate

### Security Considerations

- Challenge entropy: 256 bits minimum
- Origin validation: Match against allowed RP origins
- Credential backup: Flag for passkey eligibility
- User verification: Preference-based (discouraged, preferred, required)

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
