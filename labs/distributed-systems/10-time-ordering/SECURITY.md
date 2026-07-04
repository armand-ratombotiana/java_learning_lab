# Time and Ordering: Security

## Security Concerns
- Clock values can be manipulated by attackers
- Timestamp tampering can subvert ordering guarantees
- Vector clocks can be artificially inflated
- Time-based attacks (replay, causality violation)

## Best Practices
1. **Time synchronization security**: Authenticate NTP sources
2. **Clock values**: Validate they're within expected bounds
3. **Signed timestamps**: Prevent tampering
4. **Monotonic timelines**: Use hardware counters where possible
5. **Rate limiting**: Prevent vector clock inflation

```java
public class SecureVectorClock {
    private final HMacDigest signer;
    
    public Map<String, Integer> sign(Map<String, Integer> clock) {
        byte[] signature = signer.sign(serialize(clock));
        clock.put("_sig", Arrays.hashCode(signature));
        return clock;
    }
    
    public boolean verify(Map<String, Integer> clock) {
        int sig = clock.remove("_sig");
        byte[] computed = signer.sign(serialize(clock));
        return sig == Arrays.hashCode(computed);
    }
}
```
