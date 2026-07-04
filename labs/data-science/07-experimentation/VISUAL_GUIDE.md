# Visual Guide to Experimentation

## A/B Test Flow

```
         ┌───────────────────────────────┐
         │   100,000 website visitors     │
         └───────────────┬───────────────┘
                         │
            ┌────────────┴────────────┐
            ▼                         ▼
    ┌───────────────┐       ┌────────────────┐
    │ Control (50K) │       │ Treatment (50K)│
    │  Old button   │       │  New button     │
    └───────┬───────┘       └───────┬────────┘
            │                       │
            ▼                       ▼
    ┌───────────────┐       ┌────────────────┐
    │  Clicks: 500  │       │  Clicks: 560    │
    │  Rate: 1.0%   │       │  Rate: 1.12%    │
    └───────────────┘       └────────────────┘
    
    Lift: +0.12pp (+12% relative)
    p-value: 0.038 (significant at α = 0.05)
    95% CI: [0.006%, 0.234%]
```

## Confidence Interval Visualization

```
Treatment ────●────
              │    │
Control ──●───┘
          │
          0%
          ↑
        MDE = 0.1%
        
CI excludes 0 → statistically significant
But lower bound is very small → may not be practically significant
```

## Power Curve

```
Power
 1.0 ┤                ╱
 0.8 ┤         ╱──────   target power
 0.6 ┤      ╱
 0.4 ┤   ╱
 0.2 ┤╱
 0.0 ┤────────────────
    0  5K  10K 15K 20K  Sample size per group
```
