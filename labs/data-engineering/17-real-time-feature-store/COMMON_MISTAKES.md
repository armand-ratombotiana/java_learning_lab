# Common Mistakes with Real-Time Feature Store

1. No Point-in-Time Join: Joining features to labels without temporal alignment causes data leakage
2. Training-Serving Skew: Using different transformation code for training and serving
3. Large Online Store: Storing all features online when only recent values are needed; use TTL
4. Slow Materialization: Materializing full feature table every run instead of incremental
5. No Validation: Not validating online/offline consistency leads to silent serving failures
