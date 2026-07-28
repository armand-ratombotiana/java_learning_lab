# Experimental Design — Interview Questions

### Q1: Blocking
**Q**: When would you block vs not block in an experiment?

**A**: Block when you have a known source of variability that could obscure treatment effects (e.g., day of week, user segment, lab equipment). Blocking reduces residual variance by accounting for this variability. Don't block if the blocking factor doesn't explain substantial variance — you lose degrees of freedom for no benefit. Rule of thumb: if the blocking factor explains >10% of total variance, block.

### Q2: Factorial vs One-at-a-Time
**Q**: Compare factorial experiments with one-factor-at-a-time (OFAT) approaches.

**A**: Factorial detects interactions; OFAT cannot. For k factors each at 2 levels, factorial uses 2^k runs and estimates all main effects and interactions. OFAT requires 2k runs but cannot estimate interactions. Factorial is more efficient when interactions exist. OFAT is only appropriate when you're certain there are no interactions (rarely true in practice).

### Q3: Fractional Factorials and Aliasing
**Q**: Explain confounding/aliasing in fractional factorial designs.

**A**: In a 2^(k-p) design, some effects are aliased (cannot be distinguished). For example, in a 2^(3-1) design with I=ABC, the main effect A is aliased with the BC interaction. Resolution III: main effects aliased with 2-way interactions. Resolution IV: no main effects aliased with other mains or 2-way, but 2-ways aliased with each other. Resolution V: no main or 2-way aliased with each other.

### Q4: Sample Size for Factorial
**Q**: How many replicates do you need for a 2^3 factorial?

**A**: Depends on effect size and desired power. With 1 replicate (8 runs), you have no error df — can't estimate significance. With 2 replicates (16 runs), you have 8 df for error (assume 3-factor interaction is negligible) — enough to detect medium-to-large effects (Cohen's f > 0.4) at 80% power. For small effects, you may need 4-8 replicates.

### Q5: Response Surface vs Factorial
**Q**: When do you use response surface methodology (RSM) instead of factorial designs?

**A**: Factorial designs identify which factors matter and their first-order effects. RSM adds center points and axial points to estimate curvature (quadratic effects), enabling optimization of the response. Use factorial for screening (many factors, few important), then RSM for optimization (few factors, need to find optimum).

## Coding

### Q6: Generate block-randomized allocation
```java
public int[] blockRandomize(int blocks, int treatments, long seed) {
    Random rng = new Random(seed);
    int[] assignments = new int[blocks * treatments];
    for (int b = 0; b < blocks; b++) {
        for (int t = 0; t < treatments; t++) assignments[b * treatments + t] = t;
        // Shuffle this block
        for (int t = treatments - 1; t > 0; t--) {
            int k = rng.nextInt(t + 1);
            int offset = b * treatments;
            int tmp = assignments[offset + t];
            assignments[offset + t] = assignments[offset + k];
            assignments[offset + k] = tmp;
        }
    }
    return assignments;
}
```
