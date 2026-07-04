# Security in Experimentation

## 1. Experiment Contamination

If users can detect they're in an experiment, they may change their behavior (Hawthorne effect) or exploit the experiment for personal gain.

**Mitigation**: Blinding — users should not know which variant they see. In online experiments, this is usually automatic (different CSS is invisible to users).

## 2. Holdout Groups as Attack Surface

If specific users are knowingly kept as holdouts (denied a valuable feature), they may have a complaint.

**Mitigation**: Explain that testing is essential for improvement. Use minimal holdout sizes.

## 3. Experiment Data Privacy

Experiment logs tie user identities to experiment assignments and outcomes. This data must be protected like any other PII.

```java
// Pseudonymize experiment assignments
public class ExperimentLog {
    private String pseudonymizedUserId;  // hashed, not raw
    private int experimentId;
    private int group;  // 0=control, 1=treatment
    private double metricValue;  // aggregated, not per-event
}
```

## 4. Metric Manipulation

If users or bots discover that a metric triggers a particular treatment, they can game the system.

**Example**: A bot that clicks on the treatment button to prove it's "better."

**Mitigation**: Use bot detection, cap user contributions, monitor for anomalous patterns.

## 5. Discrimination via Experimentation

If experiments show that a feature works better for one demographic group and worse for another, shipping the feature creates algorithmic discrimination.

**Mitigation**: Pre-register subgroup analyses. Monitor fairness metrics. If a feature harms a protected group, don't ship it even if the overall effect is positive.

## 6. Transparency in Reporting

Publish experiment results (including null and negative results) to prevent publication bias. Implement an internal experiment registry that records all experiments, not just the successful ones.
