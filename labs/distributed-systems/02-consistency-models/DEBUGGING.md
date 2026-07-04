# Debugging Consistency Issues

## Common Bugs

### Stale Read Detection
```java
public class ConsistencyChecker {
    public static boolean checkMonotonicRead(List<ReadResult> history) {
        long lastVersion = 0;
        for (ReadResult r : history) {
            if (r.version < lastVersion) {
                System.err.println("Monotonic read violation at " + r.timestamp);
                return false;
            }
            lastVersion = r.version;
        }
        return true;
    }
}
```

### Causal Violation Detection
```java
public static boolean checkCausalConsistency(List<Event> events) {
    for (Event e1 : events) {
        for (Event e2 : events) {
            if (e1.causes(e2) && e2.timestamp < e1.timestamp) {
                System.err.println("Causal violation: " + e1 + " causes " + e2);
                return false;
            }
        }
    }
    return true;
}
```
