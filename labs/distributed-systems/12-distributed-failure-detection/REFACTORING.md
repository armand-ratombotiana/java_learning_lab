# Refactoring for Failure Detection

## From Fixed to Adaptive Detection

### Before (Fixed Timeout):
```java
public class FixedDetector {
    private static final long TIMEOUT = 5000; // 5 seconds fixed
    
    public boolean isFailed(long lastHeartbeat) {
        return System.currentTimeMillis() - lastHeartbeat > TIMEOUT;
    }
}
```

### After (Adaptive Phi-Accrual):
```java
public class AdaptiveDetector {
    private final PhiAccrualDetector detector;
    
    public boolean isFailed(long lastHeartbeat) {
        return detector.phi(currentNodeId()) > PHI_THRESHOLD;
    }
}
```
