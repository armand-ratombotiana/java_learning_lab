# Flashcards: Fraud Detection

Front: What is an Isolation Forest? | Back: An unsupervised anomaly detection algorithm that isolates anomalies by randomly partitioning data; anomalies have shorter average path lengths.

Front: What is score fusion? | Back: Weighted combination of rule engine score and ML model score to produce a final fraud probability.

Front: What is a velocity check? | Back: Detecting anomalous transaction frequency within a time window (e.g., 5+ transactions in 1 minute).

Front: What is the chain-of-responsibility pattern in rules? | Back: Rules are evaluated in sequence; each rule can short-circuit the chain with a definitive REJECT.

Front: What is concept drift? | Back: When fraud patterns change over time, degrading model accuracy. Mitigated by continuous retraining on new labeled data.

Front: What is a feature store? | Back: A centralized cache (Redis) for pre-computed features to avoid recomputation during scoring.
