# Lab 10: Interview Questions

## FAANG-Level Questions

### Q1: Design an A/B testing platform for ML model experimentation.
**Answer**: Build a platform with: (1) Experiment configuration — model variants, traffic split, metrics, duration, (2) Assignment service — deterministic hashing for consistent user assignment, (3) Metrics pipeline — real-time event collection + batch aggregation, (4) Statistical engine — sequential testing with early stopping, (5) Dashboard — real-time results with confidence intervals and guardrail metrics.

### Q2: Explain the tradeoffs between A/B testing and multi-armed bandits.
**Answer**: A/B testing is simpler, gives unbiased estimate of treatment effect, and is preferred for definitive answers. MAB minimizes regret during experimentation by adaptively allocating more traffic to better variants. Use A/B for high-stakes decisions with sufficient traffic; use MAB for continuous optimization (e.g., which model serves a given user segment).

### Q3: How do you handle multiple comparison problems in ML experiments?
**Answer**: Use Bonferroni correction (divide α by number of comparisons) or Benjamini-Hochberg procedure (control false discovery rate). Pre-register primary and secondary metrics. Use a hierarchical testing approach: first test overall significance, then individual variants.

### Q4: How do you calculate sample size for an ML model A/B test?
**Answer**: n = (Z_α/2 + Z_β)² × 2σ² / δ² where Z_α/2 = 1.96 (for α=0.05), Z_β = 0.84 (for 80% power), σ² is the variance of the metric, and δ is the minimum detectable effect. For binary metrics (CTR), σ² = p(1-p). Use pilot study data to estimate variance.

## LeetCode / NeetCode References
- **Design a Recommendation System** — Multi-armed bandit for content selection
- **Random Pick with Weight (LeetCode 528)** — Weighted random selection in MAB
- **Design a Load Balancer** — Traffic splitting for A/B tests
