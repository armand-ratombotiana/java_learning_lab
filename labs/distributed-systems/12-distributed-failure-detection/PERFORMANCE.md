# Failure Detection: Performance

## Performance Characteristics

| Detector Type   | Detection Speed | False Positive | Network Overhead | CPU Cost |
|-----------------|-----------------|----------------|------------------|----------|
| Fixed Heartbeat | 1-5s            | High           | Low              | Minimal  |
| Phi-Accrual     | 2-10s           | Low            | Low              | Medium   |
| SWIM            | 1-3s            | Medium         | Low              | Medium   |
| Gossip          | 5-15s           | Medium         | Medium           | Low      |

## Tuning Parameters

### Phi-Accrual
- Phi threshold: higher = slower detection, fewer false positives
- Window size: larger = better adaptation, more memory
- Min samples: too few = inaccurate phi before convergence

### SWIM
- Ping interval: shorter = faster detection, more traffic
- Indirect probes: more = better accuracy, more overhead
- Suspicion rounds: more = fewer false positives, slower

## Bottlenecks
- Network bandwidth (heartbeat traffic in large clusters)
- CPU (phi computation, convergence calculations)
- Memory (heartbeat history storage)
