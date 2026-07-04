# Mental Models for Feature Engineering

## 1. The Feature Pyramid

```
         ┌──────────┐
         │  Target   │  What we're predicting
         └────┬─────┘
              │
    ┌─────────┴──────────┐
    │  High-Level        │  Domain aggregates, ratios, trends
    │  Synthesized       │  e.g., customer_ltv, avg_order_size
    └─────────┬──────────┘
              │
    ┌─────────┴──────────┐
    │  Interaction       │  Cross-product of raw features
    │  Features          │  e.g., age × income, region × product
    └─────────┬──────────┘
              │
    ┌─────────┴──────────┐
    │  Transformed       │  Log, sqrt, polynomial, binning
    │  Features          │  e.g., log(salary), age_squared
    └─────────┬──────────┘
              │
    ┌─────────┴──────────┐
    │  Raw Features      │  Directly from data source
    │                    │  e.g., age, salary, date
    └────────────────────┘
```

Higher-level features capture more signal but require more engineering effort and are harder to compute at inference time.

## 2. The Information Bottleneck

Every feature is an information channel between raw data and the model. The feature's capacity (how much information it carries) is limited by:
- **Granularity**: daily → weekly → monthly loses resolution
- **Noise**: a noisy sensor reading carries less signal
- **Missingness**: a 50% missing column can't carry full signal

## 3. The Garbage In, Garbage Out Corollary

No model can overcome bad features. A perfect gradient-boosted tree trained on junk features produces junk predictions. Feature engineering is the highest-leverage place to invest modeling effort.

## 4. Occam's Razor for Features

The simplest feature that captures the signal is the best. Complex features (high-dim interaction terms, deep embeddings) are harder to debug, explain, and maintain. Start simple, add complexity only when it improves validated performance.
