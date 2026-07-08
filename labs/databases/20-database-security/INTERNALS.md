# Internals: Database Security

## Internal Architecture

### RBAC Engine Internals
Role-Based Access Control maps users to roles and roles to permissions.

**Data Flow:**
1. User request arrives with user ID
2. System looks up user's assigned roles
3. Each role is resolved to its permission set
4. For each permission, check if resource+action matches requested operation
5. If any permission matches: GRANT access
6. If no permission matches: DENY access

**Permission Model:**
```java
Permission = (resource, action)
Resource hierarchy: * (all), table/* (all in table), table/column
Action hierarchy: *, admin, read, write, delete
```

### Audit Logging Internals
Thread-safe event queue with bounded size and query capabilities.

**Event Structure:**
```java
record AuditEvent(long id, String timestamp, String userId, String action,
    String resource, String result, String details, long latencyMs)
```

**Storage:** ConcurrentLinkedQueue with max capacity. Oldest events evicted when limit reached.

**Query:** In-memory stream filtering by userId, action, or time range.

### Encryption Architecture
- **At Rest:** TDE (Transparent Data Encryption) with per-node keys
- **In Transit:** TLS 1.3 with mutual certificate validation
- **Key Management:** HSM or cloud KMS integration
- **Key Rotation:** Automatic 30-day rotation schedule

### Row-Level Security
Implemented via database policies that filter rows based on user context:
```sql
CREATE POLICY tenant_isolation ON orders
    USING (tenant_id = current_setting('app.current_tenant'));
```

### Audit Trail Integrity
- Events are sequenced with monotonically increasing IDs
- Tamper-evident log using hash chaining
- Write-once, append-only storage for critical events
- Periodic log verification and signing

### Incident Detection
- Real-time anomaly detection on access patterns
- Rate limit violation alerts
- Failed authentication monitoring
- Unusual data volume transfer detection
