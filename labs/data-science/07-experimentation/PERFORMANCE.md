# Performance in Experimentation

## 1. Real-Time p-Value Computation

For large-scale experiments with millions of users, computing p-values from scratch each time is wasteful.

**Optimization**: Maintain running statistics.

```java
public class RunningStatTracker {
    private long n1, n2;
    private double sum1, sum2, sumSq1, sumSq2;
    
    public void addControl(double value) {
        n1++; sum1 += value; sumSq1 += value * value;
    }
    public void addTreatment(double value) {
        n2++; sum2 += value; sumSq2 += value * value;
    }
    
    public double getPValue() {
        double mean1 = sum1 / n1, mean2 = sum2 / n2;
        double var1 = (sumSq1 - sum1 * sum1 / n1) / (n1 - 1);
        double var2 = (sumSq2 - sum2 * sum2 / n2) / (n2 - 1);
        double se = Math.sqrt(var1/n1 + var2/n2);
        double t = (mean2 - mean1) / se;
        return 2 * (1 - tCDF(Math.abs(t), Math.min(n1, n2) - 1));
    }
}
```

## 2. Large-Scale Experimentation Platforms

Companies run thousands of concurrent experiments. Each user may be in multiple experiments (as long as they're on non-overlapping dimensions).

**Optimization**: Use hashing-based assignment (no database lookup).

```java
// O(1) assignment: hash user_id + experiment_id → treatment/control
public boolean isInTreatment(String userId, String experimentId) {
    int hash = murmur3_32(userId + ":" + experimentId);
    return Math.abs(hash) % 100 < 50;  // 50/50 split
}
```

## 3. Bootstrapped Confidence Intervals

Full bootstrap (10K resamples on 1M users) is expensive.

**Optimization**: Use analytical CIs (based on Normal approximation) for primary analysis. Use bootstrap only for non-standard metrics.

## 4. CUPED Computation

CUPED requires computing Cov(Y, X) and Var(X) on the full dataset. For large datasets, pre-compute on a daily rollup.

## Memory for Experiment Logs

1M users × 1K experiments × 1 assignment bit ≈ 125MB (bit-packed). Store experiment assignments in a compressed bitmap (RoaringBitmap).
