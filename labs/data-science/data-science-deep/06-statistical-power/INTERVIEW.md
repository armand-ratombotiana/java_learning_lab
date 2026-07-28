# Statistical Power — Interview Questions

### Q1: Why Power Matters
**Q**: What happens if you run an underpowered study?

**A**: (1) High false negative rate — you fail to detect real effects. (2) Effect size inflation among significant results (winners curse / Type M error). (3) Low reproducibility — significant results in underpowered studies are likely overestimates. (4) Wasted resources — the study can't answer the question regardless of outcome.

### Q2: Retrospective Power
**Q**: Should you compute power after seeing non-significant results? Why or why not?

**A**: No. Post-hoc power using the observed effect size is just a transformation of the p-value — it adds no information. If p > α, "observed power" will always be < 50%. Instead, use confidence intervals around the observed effect to determine if the study was informative. A wide CI including meaningful and null effects means the study was underpowered.

### Q3: Sample Size for Interactions
**Q**: Detecting an interaction typically requires much larger samples than detecting main effects. Why?

**A**: The standard error of an interaction effect is roughly twice that of a main effect (depends on design). With 2x the SE, you need ~4x the sample to achieve the same power for the same effect size. This is why many studies fail to replicate interactions. Recommendation: pre-register interactions and power for the interaction specifically.

### Q4: Multiple Testing and Power
**Q**: How does Bonferroni correction affect power?

**A**: Bonferroni reduces α to α/m, shifting the critical value to the right. For 10 tests, α = 0.005 per test. At n=100 per group, power for d=0.5 drops from ~0.86 (α=0.05) to ~0.55 (α=0.005). Alternatives: Benjamini-Hochberg (controls FDR, higher power), sequential rejection (Holm), or pre-registering primary vs secondary outcomes to avoid correcting all tests.

### Q5: Adaptive Design
**Q**: How do sample size re-estimation (adaptive designs) affect power?

**A**: Adaptive designs allow increasing n based on interim effect size estimates. If properly handled (preserving Type I error via alpha-spending or combination tests), they can improve power when the effect is smaller than anticipated. The efficiency gain is modest (~10-20% sample reduction) but can save studies that would otherwise be underpowered.

## Coding

### Q6: Power curve computation
```java
public record PowerCurve(int[] sampleSizes, double[] powerValues) {
    public static PowerCurve compute(double effectSize, double alpha) {
        int[] ns = IntStream.range(10, 1001).filter(n -> n % 10 == 0).toArray();
        double[] powers = new double[ns.length];
        for (int i = 0; i < ns.length; i++) {
            powers[i] = powerTwoSampleT(effectSize, ns[i], ns[i], alpha);
        }
        return new PowerCurve(ns, powers);
    }
}
```
