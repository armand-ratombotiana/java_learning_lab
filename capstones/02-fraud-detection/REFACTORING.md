# Refactoring: Fraud Detection

## Current Pain Points
- Rule chain is hardcoded; adding/removing rules requires code change
- Feature extraction logic duplicated across rule engine and ML scorer
- No model versioning; deploying new model overwrites old one
- Score fusion weights are static, not adaptive

## Suggested Improvements
- Externalize rule configuration to YAML or database with hot-reload
- Extract feature computation into a shared FeatureService
- Implement model registry with A/B testing (route % traffic to new model)
- Make fusion weights adaptive based on recent precision/recall metrics
- Add explainability output (SHAP values) with each decision for auditor review
