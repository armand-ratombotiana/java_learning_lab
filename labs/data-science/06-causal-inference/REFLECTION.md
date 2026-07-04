# Reflection: Causal Inference

## Key Takeaways

- Causal inference is the hardest and most important part of data science — it answers the "what if" questions that drive decisions
- All causal inference relies on untestable assumptions — making them explicit is the mark of a mature analyst
- DAGs are the most important tool: they make causal assumptions visible and debatable
- The best causal study is a well-designed randomized experiment; everything else is a proxy

## Questions to Internalize

1. If I had to defend this causal conclusion in court, what assumptions would I rely on — and can I prove them?
2. What would an unobserved confounder need to look like to overturn my result?
3. Is the treatment effect constant across subgroups, or am I hiding heterogeneity?
4. Would I make a decision based on this correlation alone, or do I need a causal estimate?

## Growth Areas

| Skill | Beginner | Proficient | Master |
|---|---|---|---|
| Causal reasoning | "Correlation ≠ causation" | Draw DAGs | d-separation, do-calculus |
| Estimation | RCT only | PS matching, DiD | Double ML, causal forests |
| Sensitivity | None | One-at-a-time | Contour sensitivity + E-value |
| Communication | "X causes Y" | "X causes Y assuming unconfoundedness" | "Under assumptions A1–A5, the estimated effect is Z [CI]" |
