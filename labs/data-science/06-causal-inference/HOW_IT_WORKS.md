# How Causal Inference Works

## The Three-Layered Hierarchy (Pearl)

| Layer | Query | Example |
|---|---|---|
| 1. Association | P(Y|X) | "Do customers who see an ad buy more?" |
| 2. Intervention | P(Y|do(X)) | "If I show this ad to everyone, will they buy more?" |
| 3. Counterfactual | P(Y_x | X=x', Y=y') | "Would this customer who bought have bought less if they hadn't seen the ad?" |

## Methods by Data Type

### Randomized Experiment
$$ \tau = E[Y|T=1] - E[Y|T=0] $$

```java
// Simple RCT analysis
double meanTreated = data.where(data.booleanColumn("treatment"))
    .doubleColumn("outcome").mean();
double meanControl = data.where(data.booleanColumn("treatment").not())
    .doubleColumn("outcome").mean();
double ate = meanTreated - meanControl;  // Average Treatment Effect
```

### Propensity Score Matching
$$ e(X) = P(T=1|X) $$

Estimate the probability of treatment given covariates, then match treated/control units with similar propensity scores.

### Difference-in-Differences
$$ \tau = (E[Y_{post}|T=1] - E[Y_{pre}|T=1]) - (E[Y_{post}|T=0] - E[Y_{pre}|T=0]) $$

### Instrumental Variables
Uses a variable Z that affects treatment T but has no direct effect on outcome Y (except through T).

```
Z (instrument) ──→ T (treatment) ──→ Y (outcome)
                          ↑
                     X (confounders)
```

## Causal Graph d-Separation

Two sets of variables are causally unrelated given a conditioning set S if all paths between them are blocked by S. Rules:
1. Chain A → B → C: B blocks the path (condition on B → A and C are independent)
2. Fork A ← B → C: B blocks the path (condition on B → A and C are independent)
3. Collider A → B ← C: B opens the path (condition on B → A and C become dependent)
