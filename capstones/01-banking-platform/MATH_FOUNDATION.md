# Math Foundation: Banking Platform

## Balance Calculations
- Available = SUM(all credits) - SUM(all debits)
- Pending = SUM(holds not yet settled)
- Effective = Available - Pending

## Fraud Scoring (logistic regression)
- P(fraud | features) = 1 / (1 + e^(-z))
- z = w0 + w1*x1 + w2*x2 + ... + wn*xn
- Features: transaction amount, velocity, geo-distance, device fingerprint, time since last tx

## Idempotency
- Idempotency key = HMAC-SHA256(user_id + amount + timestamp + nonce)
- TTL: 24 hours

## Rate Limiting
- Token bucket algorithm: capacity = 100 requests/min per user
- Refill rate = 100/60 tokens per second
