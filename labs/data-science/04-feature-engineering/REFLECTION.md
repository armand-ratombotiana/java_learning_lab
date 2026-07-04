# Reflection: Feature Engineering

## Key Takeaways

- Good features make simple models perform as well as complex ones
- Feature engineering encodes domain knowledge that the model cannot learn from raw data
- Every transform must be fit on training data only and applied consistently to test/serving
- Feature stores bridge the gap between experimentation and production

## Questions to Internalize

1. Can a domain expert look at my features and explain what they represent?
2. Am I creating features that use information not available at prediction time?
3. Is the performance gain from my engineered features real or an artifact of leakage?
4. If I had to serve these features in a production API with < 10ms latency, which ones would be feasible?

## Growth Areas

| Skill | Beginner | Proficient | Master |
|---|---|---|---|
| Encoding | One-hot all | Target encoding with CV | Learned embeddings |
| Transformations | Log/scale | Box-Cox, Yeo-Johnson | Adaptive transforms |
| Feature selection | Manual | LASSO, mutual info | AutoML feature selection |
| Automation | Manual per dataset | Pipeline builder | Deep Feature Synthesis |
