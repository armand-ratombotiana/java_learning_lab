# Mental Models for Causal Inference

## 1. The Fork (Confounder)

```
     Z (confounder)
    / \
   ▼   ▼
  X ──→ Y (non-causal correlation)
```

Z causes both X and Y → X and Y are correlated even if no causal link. Controlling for Z (conditioning/stratifying) breaks the spurious correlation.

## 2. The Collider (Selection Bias)

```
  X ──→ Z ←── Y
```

X and Y both cause Z. Conditioning on Z (e.g., "only look at admitted students") induces a non-causal correlation between X and Y. This is how Berkson's paradox and selection bias arise.

## 3. The Mediator (Causal Path)

```
  X ──→ M ──→ Y
```

X causes Y through M. If you control for M, you block the causal path (over-control bias). Never adjust for a mediator when estimating the total effect of X on Y.

## 4. The Fundamental Problem

We can never observe both Y(1) and Y(0) for the same unit. The causal effect is always a counterfactual: "What would have happened to this treated unit if it had not been treated?"

```
Unit:    Y(1)    Y(0)    Causal Effect
A:       10      ?       ?
B:       ?       8       ?
We observe only one column. The missing column is counterfactual.
```

## 5. The ID Refrain

When working with causal questions, repeat: *"Intervene vs. Observe — do vs. see."* The graph helps you reason about when $P(Y|do(X)) = P(Y|X)$ and when you need to adjust for covariates.
