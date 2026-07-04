# Debugging Causal Inference

## Propensity Score Model Doesn't Converge

**Symptom**: Logistic regression fails to fit.

**Diagnosis**: Perfect separation (a covariate perfectly predicts treatment).

```java
// Check for perfect separation
for (String col : data.booleanColumnNames()) {
    Table t = data.groupBy(col).mean("treatment");
    if (t.doubleColumn("mean(treatment)").min() == 0 ||
        t.doubleColumn("mean(treatment)").max() == 1) {
        System.out.println("Perfect separation: " + col);
    }
}
```

## ATE Estimate Is Implausibly Large

**Symptom**: ATE = $50,000 for a training program costing $2,000.

**Diagnosis**: Uncontrolled confounding, selection bias, or measurement error in the outcome.

```java
// Sensitivity check: add a fake confounder with varying strength
double[] confounderStrength = {0.1, 0.5, 1.0, 2.0};
for (double gamma : confounderStrength) {
    double adjustedATE = sensitivityAdjust(ate, gamma);
    System.out.printf("With confounder strength %.1f: ATE = %.0f%n", gamma, adjustedATE);
}
```

## Matching Produces Few Matches

**Symptom**: 1000 treated units matched to only 50 control units.

**Diagnosis**: Poor overlap — treated and control groups are very different.

```java
double[] prop = estimatePropensity(data);
double minTreated = min(prop.where(treatment == 1));
double maxControl = max(prop.where(treatment == 0));
if (minTreated > maxControl) {
    System.out.println("No common support — treated and control are completely separated");
}
```

## DiD Violates Parallel Trends

**Symptom**: DiD estimate is suspicious — check by plotting pre-trends.

```java
// Visual check: plot pre-treatment trends for both groups
// If lines are not parallel, DiD is invalid
plotTrend(treated.preTrend, "Treated");
plotTrend(control.preTrend, "Control");
// If trends diverge before treatment, the parallel trends assumption fails
```

## Checklist

- [ ] DAG drawn with all relevant variables
- [ ] All backdoor paths blocked by conditioning set
- [ ] No collider conditioning
- [ ] No mediator conditioning (if estimating total effect)
- [ ] Common support (overlap) checked
- [ ] Sensitivity analysis performed
- [ ] Multiple testing corrected
