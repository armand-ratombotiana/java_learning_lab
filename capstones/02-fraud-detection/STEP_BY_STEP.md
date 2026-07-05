# Step by Step: Fraud Detection

## Transaction Scoring Flow

1. Transaction event published to Kafka topic `raw.transactions`
2. `TransactionIngestor` reads event, validates schema, assigns UUID
3. `FeatureExtractor` queries Redis for: user 1h velocity count, device fingerprint, last known geo
4. Features computed: amount z-score (from 30d user history), geo-distance from last tx, device trust score
5. `RuleEngine` runs rules sequentially:
   - AmountRule: amount > $10,000? score += 30
   - VelocityRule: tx count in 1m > 5? score += 40
   - BlacklistRule: device/account blacklisted? score = 100 (REJECT)
   - GeoAnomalyRule: distance > 500mi from last tx in < 1h? score += 50
   - DeviceTrustRule: new device, no history? score += 20
6. If rule score < 90, submit to `MLScorer` asynchronously
7. `MLScorer` runs Isolation Forest: anomaly score 0.87
8. `DecisionMaker` combines: final = 0.4 * 35 + 0.6 * 87 = 66.2 -> REVIEW
9. Decision published to `fraud.decisions` topic
10. If later confirmed fraud, feedback loop updates training dataset
