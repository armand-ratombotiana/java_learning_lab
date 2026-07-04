# Debugging Time Issues

## Clock Drift Detection
```java
public class ClockDriftDetector {
    public static boolean checkDrift(List<NodeClock> clocks, long maxDrift) {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (NodeClock clock : clocks) {
            min = Math.min(min, clock.getPhysicalTime());
            max = Math.max(max, clock.getPhysicalTime());
        }
        return (max - min) > maxDrift;
    }
}
```

## Causality Violation Detection
```java
public static boolean detectCausalViolation(List<Message> history) {
    for (int i = 0; i < history.size(); i++) {
        for (int j = i + 1; j < history.size(); j++) {
            if (history.get(i).clock.happensBefore(history.get(j).clock)) {
                // Violation if seen in wrong order
                if (history.indexOf(history.get(j)) < history.indexOf(history.get(i))) {
                    return true;
                }
            }
        }
    }
    return false;
}
```
