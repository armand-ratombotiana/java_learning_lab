# Failure Detection: Code Deep Dive

## Complete Phi-Accrual Detector

```java
import java.util.*;
import java.util.concurrent.*;

public class PhiAccrualDetector {
    private final ConcurrentHashMap<String, NodeState> states = new ConcurrentHashMap<>();
    private final double phiThreshold;
    private final long windowSize;
    private final long minSamples;
    
    public PhiAccrualDetector(double phiThreshold, long windowSize, long minSamples) {
        this.phiThreshold = phiThreshold;
        this.windowSize = windowSize;
        this.minSamples = minSamples;
    }
    
    public void heartbeat(String nodeId) {
        states.computeIfAbsent(nodeId, k -> new NodeState(nodeId))
              .recordHeartbeat(System.nanoTime());
    }
    
    public double phi(String nodeId) {
        NodeState state = states.get(nodeId);
        if (state == null) return 0.0;
        return state.computePhi();
    }
    
    public boolean isAvailable(String nodeId) {
        return phi(nodeId) < phiThreshold;
    }
    
    private class NodeState {
        private final String nodeId;
        private final LinkedList<Long> heartbeats = new LinkedList<>();
        private volatile long lastHeartbeat;
        
        NodeState(String nodeId) {
            this.nodeId = nodeId;
            this.lastHeartbeat = System.nanoTime();
        }
        
        synchronized void recordHeartbeat(long timestamp) {
            if (!heartbeats.isEmpty()) {
                long interval = timestamp - heartbeats.getLast();
                heartbeats.add(interval);
            } else {
                heartbeats.add(0L);
            }
            
            lastHeartbeat = timestamp;
            
            // Trim to window size
            while (heartbeats.size() > windowSize) {
                heartbeats.removeFirst();
            }
        }
        
        synchronized double computePhi() {
            if (heartbeats.size() < minSamples) return 0.0;
            
            double sum = 0, sumSq = 0;
            for (long interval : heartbeats) {
                sum += interval;
                sumSq += interval * interval;
            }
            
            double n = heartbeats.size();
            double mean = sum / n;
            double variance = (sumSq / n) - (mean * mean);
            double stdDev = Math.sqrt(Math.max(variance, 1e-9));
            
            long elapsed = System.nanoTime() - lastHeartbeat;
            double y = (elapsed - mean) / stdDev;
            
            // P(x > elapsed) = 1 - CDF(y)
            double cdf = 0.5 * (1 + erf(y / Math.sqrt(2)));
            double p = 1.0 - cdf;
            
            // phi = -log10(p)
            return Math.max(0, -Math.log10(Math.max(p, 1e-15)));
        }
        
        private double erf(double x) {
            // Approximation of error function
            double a1 = 0.254829592, a2 = -0.284496736;
            double a3 = 1.421413741, a4 = -1.453152027;
            double a5 = 1.061405429, p = 0.3275911;
            
            int sign = (x < 0) ? -1 : 1;
            x = Math.abs(x);
            
            double t = 1.0 / (1.0 + p * x);
            double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
            
            return sign * y;
        }
    }
}
```
