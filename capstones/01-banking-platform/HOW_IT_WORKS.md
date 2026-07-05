# How It Works: Banking Platform

1. Client sends a payment request to the Gateway Service (API gateway).
2. Gateway routes to Payment Service, which generates an idempotency key.
3. Payment Service calls Account Service to check balance.
4. Account Service places a hold on funds (soft-freeze).
5. Fraud Service evaluates the transaction in real-time using rule engine + ML model.
6. If cleared, Payment Service commits the transaction; Account Service adjusts balance.
7. Notification Service sends confirmation via email/SMS/push.
8. Transaction log is appended to the event store for audit.
