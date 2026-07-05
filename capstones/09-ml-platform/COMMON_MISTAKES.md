# Common Mistakes: ML Platform

- **Training-serving skew**: Features computed differently during training vs serving. Use a centralized feature pipeline for both.
- **No feature validation**: Invalid feature values (nulls, outliers) crash serving. Validate features before prediction.
- **Manual model deployment**: Error-prone and slow. Automate promotion through staging -> production pipeline.
- **No A/B testing for models**: Deploying new model without comparison can hide regressions. Always A/B test.
- **Ignoring data drift**: Model accuracy degrades silently. Monitor feature distributions continuously.
- **No model rollback**: If new model fails, need ability to instantly revert to previous version.
- **Over-engineered pipeline**: Start simple (offline training + REST API) before adding batch, A/B testing, etc.
