# Debugging Experiments

## Imbalanced Sample Sizes

**Symptom**: Treatment group has 60K users, control has 40K.

**Diagnosis**: Randomization bug — the random function may not be uniform.

```java
// Debug: check the randomization function
Random rng = new Random(seed);
int treatmentCount = 0;
for (User u : users) {
    boolean isTreatment = rng.nextBoolean();
    // Log the decision
    // ...
}
// If not ~50/50, the RNG might be biased or the assignment function is wrong
```

## Significant Result That Doesn't Replicate

**Symptom**: Experiment 1 shows significant lift. Experiment 2 (replication) shows flat.

**Diagnosis**: 
- First result was a false positive (p-hacking, peeking)
- Novelty effect wore off
- Metric definition changed between experiments
- Sample composition changed (e.g., different user segment)

## High Variance Metric

**Symptom**: p-value fluctuates wildly with each new batch of users.

**Diagnosis**: The metric has high variance, making it hard to detect effects.

```java
// Fix: use CUPED to reduce variance
double cov = covariance(preMetric, metric);
double var = variance(preMetric);
double theta = cov / var;
double cupedMetric = metric - theta * (preMetric - mean(preMetric));
```

## Conversion Rate Drops in Both Groups

**Symptom**: Both treatment and control show lower conversion.

**Diagnosis**: External event (holiday, outage, competitor action) affects all users.

```java
// Check: compare to holdout (users not in any experiment)
double holdoutRate = computeRate(holdoutUsers);
double controlRate = computeRate(controlUsers);
if (controlRate < holdoutRate) {
    System.out.println("The experiment itself may be affecting control users");
    // Possible: the experiment infrastructure (cookies, redirects) is slowing down all users
}
```

## Checklist

- [ ] Randomization is uniform (50/50 split ± 1%)
- [ ] Sample size meets power requirements
- [ ] No peeking — analysis performed at planned time
- [ ] Multiple testing correction applied
- [ ] Pre-experiment segments are balanced
- [ ] No interference between concurrent experiments
- [ ] Results are practically (not just statistically) significant
