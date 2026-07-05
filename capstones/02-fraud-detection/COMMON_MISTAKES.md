# Common Mistakes: Fraud Detection

- **Rules too aggressive**: Over-blocking legitimate transactions (false positives) drives customer churn. Tune thresholds on historical data.
- **Ignoring feature freshness**: Stale velocity counts lead to incorrect decisions. Ensure TTL aligns with window size.
- **Blocking ML inference sync**: ML scoring adds latency. Always use async with fallback.
- **No feedback loop**: Without labeled outcomes, model quality degrades over time. Implement feedback pipeline from day one.
- **Sampling bias**: Training data that's 99% legitimate leads to models that predict "not fraud" for everything. Use undersampling or synthetic fraud generation.
- **Not tracking feature importance**: When a feature drifts (e.g., avg transaction amount increases seasonally), the model becomes less accurate.
