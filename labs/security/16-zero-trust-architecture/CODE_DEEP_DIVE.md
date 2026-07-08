# Code Deep Dive: 16-zero-trust-architecture

## AccessPolicyEngine.java

### Purpose
Core policy decision point evaluating access requests against zero trust policies using multiple signals.

### Key Implementation Details

**Access Decision Flow**:
1. Receive access request (subject, resource, action, context)
2. Collect signals: identity strength, device posture, location, behavior
3. Evaluate policies: match request against policy rules
4. Calculate trust score from weighted signals
5. Return decision (PERMIT, DENY, or STEP_UP)

### Class Structure

The AccessPolicyEngine implements:
- evaluate(AccessRequest request) - Main evaluation method
- collectSignals(AccessRequest request) - Gather context signals
- calculateTrustScore(Signal[] signals) - Weighted score calculation
- pplyPolicy(AccessRequest request, TrustScore score) - Policy matching

### Policy Definition Format

`json
{
  "policy": {
    "subject": {"role": "developer"},
    "resource": {"type": "api", "path": "/api/deployments/*"},
    "conditions": {
      "device_posture": {"os_version": ">= 14"},
      "location": {"country": "US"},
      "time": {"hours": "09:00-17:00"}
    },
    "action": "PERMIT",
    "mfa_required": true
  }
}
`

### Signal Weight Configuration

- Identity verification: weight 0.35
- Device posture: weight 0.25
- Behavior analysis: weight 0.20
- Location context: weight 0.10
- Time appropriateness: weight 0.10

### Continuous Session Monitoring

- Risk reassessment interval: 5 minutes
- High-risk action threshold: score > 75
- Step-up auth trigger: score 50-75
- Session termination: score > 90

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
