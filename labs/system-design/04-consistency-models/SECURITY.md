# Consistency Models - SECURITY

## Consistency and Security

### Consistency Attacks
- **Fork consistency**: Malicious node shows different data to different clients
- **Timing attacks**: Clock skew used to influence conflict resolution
- **Stale data attacks**: Exploiting inconsistency windows for replay attacks

### Fork Consistency Prevention
```java
// Use cryptographic certificates for consistency verification
public class SignedValue {
    private String value;
    private long timestamp;
    private byte[] signature;  // signed by trusted node

    public boolean verify(PublicKey key) {
        // Verify that this value was produced by a trusted node
        return Crypto.verify(value + timestamp, signature, key);
    }
}
```

## Consistency in Multi-Tenant Systems

### Tenant Isolation
```java
// Each tenant's data must maintain internal consistency
// Cross-tenant consistency is optional

public class TenantAwareStore {
    public void write(String tenantId, String key, String value) {
        // Only replicate within tenant's consistency domain
        String consistencyGroup = tenantConsistency.get(tenantId);
        consistentStore(consistencyGroup).write(key, value);
    }
}
```

## Audit Logs and Consistency

### Consistent Audit Trail
All audit events must be in order. Use a single log or distributed consensus.

```java
// Event sourcing provides consistent audit history
public class AuditLog {
    private final KafkaTemplate<String, AuditEvent> kafka;

    public void record(AuditEvent event) {
        // Same partition = ordered events
        kafka.send("audit-log", event.aggregateId(), event);
    }
}
```

## Side-Channel Attacks via Inconsistency

### Timing Side Channels
```java
// WRONG: Timing differs based on data state
if (cache.contains(key)) {
    return fastCacheRead(key);   // fast
} else {
    return slowDbRead(key);      // slow
}

// RIGHT: Consistent timing
CompletableFuture<String> cacheResult = cache.getAsync(key);
CompletableFuture<String> dbResult = db.getAsync(key);
return CompletableFuture.anyOf(cacheResult, dbResult);
```

## Secure Replication

### TLS for Replication Traffic
```yaml
# All replication traffic must be encrypted
replication:
  ssl: true
  cert-auth: true
  cipher: TLS_AES_256_GCM_SHA384
```

### Replica Authentication
```java
// mTLS between replicas
SSLContext ssl = SSLContext.builder()
    .keyManager(keyManager)
    .trustManager(trustManager)
    .build();

// Each replica must present certificate
// Only known replicas can join the cluster
```
