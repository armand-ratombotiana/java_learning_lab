# Mock Interview: Experimental Design

**Interviewer**: We want to optimize a landing page with 5 factors (headline, image, button text, page layout, font size). Each has 2 levels. What's your experimental approach?

**Candidate**: A full 2^5 factorial requires 32 runs. If we can run all 32, great — we get all main effects and interactions. But if running 32 is too expensive (e.g., each run requires significant traffic), I'd use a 2^(5-2) fractional factorial in 8 runs — Resolution III, which estimates all 5 main effects but aliases them with 2-way interactions. If budget allows 16 runs, a 2^(5-1) Resolution V design separates all mains and 2-way interactions.

**Interviewer**: How do you handle the fact that user traffic varies by day of week?

**Candidate**: I'd block on day of week. Each day is a block containing a complete set of treatments (or fraction if too many). Within each block, treatments are randomized. This way the day effect is absorbed by the block term, reducing residual variance and increasing precision for treatment effect estimates. I'd include block as a categorical factor in the ANOVA.

**Interviewer**: What if a treatment combination performs terribly — can we stop that condition early?

**Candidate**: This is a multi-arm bandit problem. I'd use Thompson sampling or upper confidence bound (UCB) to dynamically allocate more traffic to promising arms. However, this introduces adaptivity that invalidates standard p-values and confidence intervals. If the goal is inference (proving which factor matters), I'd stick with fixed design and use sequential testing with alpha-spending boundaries. If the goal is purely optimization (find the best combination), I'd use bandit allocation.

**Interviewer**: Let's code. Implement a function to generate a randomized block design.

**Candidate**:
```java
public int[][] blockRandomize(int blocks, int treatments) {
    int[][] design = new int[blocks][treatments];
    Random rng = new Random();
    for (int b = 0; b < blocks; b++) {
        for (int t = 0; t < treatments; t++) design[b][t] = t;
        for (int t = treatments - 1; t > 0; t--) {
            int k = rng.nextInt(t + 1);
            int tmp = design[b][t];
            design[b][t] = design[b][k];
            design[b][k] = tmp;
        }
    }
    return design;
}
```

**Interviewer**: How would you analyze the results?

**Candidate**: Fit a linear model: response ~ block + treatment1 + treatment2 + ... + treatment1:treatment2 + ... . I'd use Type III SS for unbalanced designs. Check residuals (normality, constant variance). Plot interaction plots. For significant effects, compute effect sizes and confidence intervals. If the design is fractional, report the alias structure and interpret with caution.
