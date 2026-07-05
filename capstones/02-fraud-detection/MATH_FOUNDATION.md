# Math Foundation: Fraud Detection

## Anomaly Score (Isolation Forest)
- s(x, n) = 2^(-E(h(x)) / c(n))
- Where E(h(x)) is average path length, c(n) is normalizing constant
- s close to 1 = anomaly, s < 0.5 = normal

## Velocity Features
- tx_count_1m = COUNT(tx in last 60 seconds)
- tx_count_1h = COUNT(tx in last 3600 seconds)
- amount_sum_1h = SUM(amount in last 3600 seconds)
- z_score_amount = (amount - mean_amount_30d) / std_amount_30d

## Geo-Distance (Haversine)
- d = 2R * arcsin(sqrt(sin^2(delta_lat/2) + cos(lat1)*cos(lat2)*sin^2(delta_lon/2)))
- R = 6371 km (Earth radius)

## Score Fusion
- final_score = w_rule * rule_score + w_ml * ml_score
- Default: w_rule = 0.4, w_ml = 0.6
- Decision: APPROVE if score < 40, REVIEW if 40-70, REJECT if > 70
