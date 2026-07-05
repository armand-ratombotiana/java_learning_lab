# Theory: Fraud Detection

## Real-Time Scoring Architecture
Fraud detection requires sub-second evaluation of transactions against rules and ML models. The system uses a two-stage pipeline: lightweight rule-based filters (Stage 1) that run synchronously, and heavyweight ML inference (Stage 2) that runs asynchronously with fallback.

## Rule Engine Design
Rules are organized as a chain-of-responsibility. Each rule receives a transaction context and returns a score + reason code. Rules are ordered by computational cost (cheapest first) so expensive checks only run after passing cheap filters.

## ML Model
Isolation Forest algorithm for anomaly detection: builds random forests where anomalies are isolated closer to the root. Features include transaction amount z-score, velocity (tx count in time window), geo-distance from last known location, and device fingerprint entropy.

## Stream Processing
Apache Kafka/Flink processes transactions as a continuous stream. Windowed aggregations compute velocity features. The system uses tumbling windows (1 min, 5 min, 1 hour) for different velocity scales.

## Feature Store
A Redis-backed feature store caches pre-computed features per user/device/card, enabling sub-millisecond feature retrieval during scoring.
