# Code Deep Dive: 20-incident-response-forensics

## IncidentResponseOrchestrator.java

### Purpose
Orchestrates incident response workflows including detection, containment, investigation, and evidence collection.

### Key Implementation Details

**Incident Response Workflow**:
1. Receive security alert from SIEM
2. Classify incident severity and type
3. Execute containment playbook based on classification
4. Collect forensic evidence (memory, disk, logs)
5. Analyze evidence and reconstruct timeline
6. Document findings with chain of custody

### Class Structure

The IncidentResponseOrchestrator implements:
- handleIncident(SecurityAlert alert) - Main incident handler
- classifyIncident(SecurityAlert alert) - Severity and type classification
- executeContainment(Incident incident) - Containment playbook
- collectEvidence(Incident incident) - Forensic evidence acquisition
- nalyzeEvidence(Evidence evidence) - Analysis and timeline
- generateReport(Incident incident) - Final IR report

### Containment Playbook

`json
{
  "playbook": {
    "type": "ransomware_detection",
    "steps": [
      {"action": "ISOLATE_HOST", "target": "affected_host", "timeout": 60},
      {"action": "SUSPEND_USER", "target": "affected_user", "timeout": 30},
      {"action": "BLOCK_IP", "target": "c2_address", "timeout": 10},
      {"action": "CAPTURE_MEMORY", "target": "affected_host"},
      {"action": "CAPTURE_DISK", "target": "affected_host"},
      {"action": "ROTATE_CREDENTIALS", "target": "compromised_accounts"}
    ]
  }
}
`

### Evidence Collection

- Memory: WinPMem/LiME for RAM capture
- Disk: dd/FTK Imager for bit-for-bit copy
- Network: tcpdump/Wireshark for packet capture
- Logs: Centralized log aggregation export
- Metadata: File timestamps, permissions, ownership

### Chain of Custody Record

`java
ChainOfCustodyRecord record = new ChainOfCustodyRecord(
    UUID.randomUUID(),        // evidenceId
    "server-01",              // source
    "System memory dump",     // description
    ZonedDateTime.now(),      // collectedAt
    "Incident Responder A",   // collector
    SHA256.of(dumpFile),      // hash
    ChainOfCustodyStatus.STORED
);
`

### Timeline Reconstruction

- Event correlation: Log events from multiple sources
- Time normalization: Convert to UTC with offset
- Sequence alignment: Order events with sub-second precision
- Gap identification: Detect missing log periods
- Root cause mapping: Link cause and effect events

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
