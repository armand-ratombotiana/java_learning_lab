# Math Foundation for Pipeline Architecture

## Pipeline Latency
```
TotalLatency = Σ(StageLatency_i) + Σ(Overhead_i)
Serial: L = L1 + L2 + L3 + ... + Ln
Parallel: L = max(L1, L2, L3, ..., Ln)
```

## Throughput Calculation
```
Serial Throughput = 1 / (Σ StageLatency)
Parallel Throughput = N / (max StageLatency)
Where N = number of parallel branches
```

## Pipeline Stages Count
```
OptimalStages ≈ √(TotalProcessingTime / OverheadPerStage)
Too few: coarse-grained, hard to reuse
Too many: overhead dominates
Rule of thumb: 3-10 stages per pipeline
```

## Error Rate in Pipelines
```
OverallSuccessRate = Π(StageSuccessRate_i)
Example: 5 stages at 99% each = 0.99^5 = 95.1% overall
With error handling recovery: much higher effective rate
```

```java
public class PipelineMath {
    public long calculateSerialLatency(List<Long> stageLatencies) {
        return stageLatencies.stream().mapToLong(l -> l).sum();
    }

    public long calculateParallelLatency(List<Long> stageLatencies) {
        return stageLatencies.stream().mapToLong(l -> l).max().orElse(0);
    }

    public double overallSuccessRate(double stageRate, int stages) {
        return Math.pow(stageRate, stages);
    }
}
```
