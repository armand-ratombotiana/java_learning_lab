# Failure Detection: Security

## Security Concerns
- False heartbeat injection (make dead node appear alive)
- Heartbeat suppression (make alive node appear dead)
- Denial of service via rapid heartbeat generation
- Spoofing node identity

## Best Practices
1. **Authenticate heartbeats** (verify node identity)
2. **Encrypt heartbeat messages** (prevent spoofing)
3. **Rate limit** heartbeat processing
4. **Cryptographically sign** heartbeat payloads
5. **Validate** heartbeat sequence numbers (prevent replay)

```java
public class SecureHeartbeat {
    private final Mac hmac;
    
    public Heartbeat createHeartbeat(String nodeId, long timestamp) {
        byte[] payload = serialize(nodeId, timestamp);
        byte[] signature = hmac.doFinal(payload);
        return new Heartbeat(nodeId, timestamp, signature);
    }
    
    public boolean verifyHeartbeat(Heartbeat hb) {
        byte[] payload = serialize(hb.nodeId, hb.timestamp);
        byte[] expected = hmac.doFinal(payload);
        return Arrays.equals(expected, hb.signature);
    }
}
```
