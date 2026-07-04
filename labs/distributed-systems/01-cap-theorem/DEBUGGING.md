# Debugging CAP Theorem Issues

## Identifying CAP Problems

### Symptoms of CP Violation
- Clients read stale data after writes
- Different nodes return different values for same key
- Write confirms but read returns old value

### Symptoms of AP Violation
- Service returns 503 during network issues
- Timeouts under normal load
- Clients experience downtime during deployment

## Debugging Tools

```java
public class CAPDebugger {
    public static void checkConsistency(String key, List<Node> nodes) {
        Set<Object> values = new HashSet<>();
        for (Node n : nodes) {
            try {
                Object val = n.read(key);
                values.add(val);
            } catch (Exception e) {
                System.out.println(n.id() + " unavailable");
            }
        }
        if (values.size() > 1) {
            System.out.println("INCONSISTENT: " + values);
        }
    }
    
    public static void checkAvailability(List<Node> nodes) {
        for (Node n : nodes) {
            try {
                n.ping();
                System.out.println(n.id() + " available");
            } catch (Exception e) {
                System.out.println(n.id() + " DOWN");
            }
        }
    }
}
```
