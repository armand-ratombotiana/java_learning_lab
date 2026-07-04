# Debugging ID Generation Issues

## Detecting Clock Drift
```java
public class ClockMonitor {
    private long lastNanoTime = System.nanoTime();
    private long lastMillis = System.currentTimeMillis();
    
    public boolean detectDrift() {
        long now = System.nanoTime();
        long elapsedNanos = now - lastNanoTime;
        long expectedMillis = lastMillis + elapsedNanos / 1_000_000;
        long actualMillis = System.currentTimeMillis();
        long drift = expectedMillis - actualMillis;
        
        if (Math.abs(drift) > 1000) {
            System.err.println("Clock drift detected: " + drift + "ms");
            return true;
        }
        return false;
    }
}
```

## Collision Detection
```java
// Maintain bloom filter of recent IDs
BloomFilter<Long> filter = BloomFilter.create(Funnels.longFunnel(), 1_000_000, 0.01);
```
