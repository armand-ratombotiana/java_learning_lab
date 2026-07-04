# How Time and Ordering Works

## Lamport Clock

```java
public class LamportClock {
    private int counter = 0;
    
    public synchronized int tick() {
        // Increment on local event
        return ++counter;
    }
    
    public synchronized void update(int receivedTimestamp) {
        // On receiving message, take max and increment
        counter = Math.max(counter, receivedTimestamp) + 1;
    }
    
    public synchronized int getValue() {
        return counter;
    }
}
```

## Vector Clock

```java
public class VectorClock {
    private final Map<String, Integer> clock = new ConcurrentHashMap<>();
    
    public synchronized void increment(String processId) {
        clock.merge(processId, 1, Integer::sum);
    }
    
    public synchronized void merge(VectorClock other) {
        for (Map.Entry<String, Integer> entry : other.clock.entrySet()) {
            clock.merge(entry.getKey(), entry.getValue(), Math::max);
        }
    }
    
    public boolean happensBefore(VectorClock other) {
        boolean strictlyLess = false;
        for (String id : clock.keySet()) {
            int thisVal = clock.getOrDefault(id, 0);
            int otherVal = other.clock.getOrDefault(id, 0);
            if (thisVal > otherVal) return false;
            if (thisVal < otherVal) strictlyLess = true;
        }
        return strictlyLess;
    }
    
    public boolean isConcurrent(VectorClock other) {
        return !happensBefore(other) && !other.happensBefore(this);
    }
}
```
