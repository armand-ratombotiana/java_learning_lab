# Debugging Failure Detectors

## Monitoring Phi Values
```java
public class PhiMonitor {
    public static void logPhiValues(Map<String, PhiAccrualDetector> detectors) {
        detectors.forEach((nodeId, detector) -> {
            double phi = detector.phi(nodeId);
            System.out.printf("Node %s: phi=%.2f %s%n", 
                nodeId, phi, 
                phi > 8 ? "FAILED" : phi > 3 ? "SUSPECT" : "ALIVE");
        });
    }
}
```

## Common Issues
- **False positives**: Check network latency, GC logs, increase threshold
- **False negatives**: Verify heartbeat sender, check timeout configuration
- **Detection storms**: Network issues cause cascading failure declarations
- **Split-brain during detection**: Implement quorum-based decisions
