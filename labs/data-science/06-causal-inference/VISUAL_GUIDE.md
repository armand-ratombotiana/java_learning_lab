# Visual Guide to Causal Inference

## DAG Library

```
Fork (confounding):           Collider (selection bias):
     Z                             X
    / \                            |
   ▼   ▼                           ▼
  X ──→ Y? (spurious)             Z ←── Y
                              Conditioning on Z opens path X → Z ← Y

Chain/Mediation:               Instrumental Variable:
  X ──→ M ──→ Y                 Z ──→ T ──→ Y
                                      ↑
                                 X (confounders)
```

## Common Causal Bias Diagrams

```
No confounding:                Confounding by Z:
  T ──→ Y                       Z
  (randomized)                 / \
                               ▼   ▼
                              T ──→ Y (biased)

Selection on collider:        Over-control of mediator:
    T ──→ Y                    T ──→ M ──→ Y
     \   /                     (don't condition on M!)
      ▼ ▼
       Z
  (conditioning on Z = bias)
```

## Before-After Comparison

```
Difference-in-Differences:
Outcome
    │ Treated: ───╲
    │            ╱ ╲
    │ Control: ──── ──
    │          pre  post
    └──────────────────→ Time
    
The treatment effect is the gap between treated/control change,
assuming parallel trends in the counterfactual.
```
