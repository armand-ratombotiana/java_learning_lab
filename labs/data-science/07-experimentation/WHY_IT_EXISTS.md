# Why Experimentation Exists

Experimentation exists because observational data cannot reliably answer causal questions. A/B testing (randomized controlled trials) is the gold standard for establishing causation because randomization breaks the link between treatment and confounders.

## The Gap It Bridges

- **Observational data**: $P(Y|X)$ — biased by confounding
- **RCT**: $P(Y|do(X))$ — unbiased estimate of causal effect (if properly randomized)

Randomization ensures that treatment and control groups are exchangeable except for the treatment itself. Any observed difference in outcomes can be attributed to the treatment.

```java
// In an A/B test, randomization means:
// P(confounders | treatment) ≈ P(confounders | control)
// So any difference in outcomes is causal

// Bad: comparing self-selected groups
double convRateTreated = usersOptedIn.stream()
    .filter(u -> u.converted).count() / usersOptedIn.size();  // BIASED

// Good: comparing randomly assigned groups
double convRateTreatment = treatmentGroup.stream()
    .filter(u -> u.converted).count() / treatmentGroup.size();  // UNBIASED
```
