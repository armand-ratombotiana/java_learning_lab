# Flashcards: ML Platform

Front: What is a feature store? | Back: A centralized repository for pre-computed features, serving both online (Redis, low latency) and offline (Parquet, batch) ML pipelines.

Front: What is training-serving skew? | Back: A mismatch between features computed during training vs serving, causing model performance degradation in production.

Front: What is concept drift? | Back: When the statistical properties of the target variable change over time, degrading model accuracy. Detected via monitoring metrics.

Front: What is PSI? | Back: Population Stability Index — measures how much a feature's distribution has shifted between training time and current serving time.

Front: What is a canary deployment? | Back: Gradually routing traffic to a new model version (1% -> 5% -> 50% -> 100%) with automated rollback on metric degradation.

Front: What is A/B testing in ML? | Back: Comparing two model versions by routing traffic to both and measuring business metrics to determine the winner.
