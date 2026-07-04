# How Failure Detection Works

## Simple Heartbeat Detector

```java
public class HeartbeatFailureDetector {
    private final Map<String, Long> lastHeartbeats = new ConcurrentHashMap<>();
    private final long timeoutMs;
    private final long checkIntervalMs;
    
    public HeartbeatFailureDetector(long timeoutMs, long checkIntervalMs) {
        this.timeoutMs = timeoutMs;
        this.checkIntervalMs = checkIntervalMs;
        startChecker();
    }
    
    public void receiveHeartbeat(String nodeId) {
        lastHeartbeats.put(nodeId, System.currentTimeMillis());
    }
    
    private void startChecker() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            lastHeartbeats.forEach((nodeId, lastHeartbeat) -> {
                if (now - lastHeartbeat > timeoutMs) {
                    markFailed(nodeId);
                }
            });
        }, checkIntervalMs, checkIntervalMs, TimeUnit.MILLISECONDS);
    }
    
    private void markFailed(String nodeId) {
        System.out.println("Node " + nodeId + " declared failed");
        // Trigger recovery actions
    }
}
```

## Phi-Accrual Detector

```java
public class PhiAccrualFailureDetector {
    private final Map<String, List<Long>> heartbeats = new ConcurrentHashMap<>();
    private final long initialPause = 1000L;
    private final double phiThreshold = 8.0;
    
    public void reportHeartbeat(String nodeId) {
        long now = System.nanoTime();
        heartbeats.computeIfAbsent(nodeId, k -> new ArrayList<>()).add(now);
        // Keep only last N for memory efficiency
        List<Long> list = heartbeats.get(nodeId);
        if (list.size() > 1000) list.remove(0);
    }
    
    public double computePhi(String nodeId) {
        List<Long> intervals = computeIntervals(nodeId);
        if (intervals.size() < 2) return 0.0;
        
        double mean = intervals.stream().mapToLong(Long::longValue).average().orElse(0);
        double variance = intervals.stream()
            .mapToDouble(i -> Math.pow(i - mean, 2))
            .average().orElse(0);
        double stdDev = Math.sqrt(variance);
        
        long now = System.nanoTime();
        long lastHeartbeat = heartbeats.get(nodeId).get(
            heartbeats.get(nodeId).size() - 1);
        long elapsed = now - lastHeartbeat;
        
        double phi = -Math.log10(1 - normalCdf((elapsed - mean) / stdDev));
        return phi;
    }
    
    public boolean isFailed(String nodeId) {
        return computePhi(nodeId) > phiThreshold;
    }
    
    private List<Long> computeIntervals(String nodeId) {
        List<Long> timestamps = heartbeats.get(nodeId);
        List<Long> intervals = new ArrayList<>();
        for (int i = 1; i < timestamps.size(); i++) {
            intervals.add(timestamps.get(i) - timestamps.get(i - 1));
        }
        return intervals;
    }
    
    private double normalCdf(double x) {
        return 0.5 * (1 + erf(x / Math.sqrt(2)));
    }
}
```
