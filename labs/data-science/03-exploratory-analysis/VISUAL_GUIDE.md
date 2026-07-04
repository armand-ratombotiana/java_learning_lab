# Visual Guide to EDA

## EDA Visual Playbook

```
Univariate Numeric           Bivariate Numeric
┌──────────────────┐        ┌──────────────────┐
│  ████████████     │        │  ⋅ ⋅    ⋅         │
│  ███████████████  │        │    ⋅ ⋅⋅  ⋅        │
│  ████████████████ │        │  ⋅   ⋅⋅ ⋅  ⋅⋅    │
│  ████████████     │        │      ⋅   ⋅  ⋅⋅   │
│  ██████           │        │          ⋅   ⋅   │
│  ██               │        └──────────────────┘
│  ═══            │        Histogram + KDE       Scatter plot
│  35    50    70  │
│  Histogram / Box  │
└──────────────────┘
```

## EDA Output Template

For each column, produce:

```
Column: salary
Type: numeric
Missing: 850/50000 (1.7%)
Unique: 48902
Min: 15000       Max: 450000
Mean: 68200      Median: 62000
Std: 28500       IQR: 38000
Skew: 2.1 (right-skewed)   Kurtosis: 12.4 (heavy tails)
Outliers (IQR): 1875 (3.75%)
```

## Visual Pattern Library

| Pattern | Look | Interpretation |
|---|---|---|
| Bell curve | ╰━━━╯ | Normal distribution |
| Bimodal | ╰━━╮╭━━╯ | Two populations mixed |
| Long right tail | ╰━━╮╲ | Positive skew, log transform candidate |
| Uniform | ═══════ | No clustering, flat distribution |
| Truncated | ─]━━━━ | Censored or bounded data |
| Heavy tails | ╰──╮──╯ | More outliers than normal |
| Periodic | ╱╲╱╲╱╲ | Seasonality in time series |
