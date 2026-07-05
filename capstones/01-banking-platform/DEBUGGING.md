# Debugging: Banking Platform

## Common Issues

### Payment stuck in PENDING_FRAUD
- Check Fraud Service health and latency
- Verify Kafka topic `fraud.requests` has consumers
- Check if ML model inference timed out (default: 2s)
- Look for dead-letter queue in fraud service

### Balance doesn't match transaction history
- Replay events from last snapshot
- Check for orphaned holds (holds without corresponding settlement)
- Verify idempotency cache hasn't expired prematurely

### Notification not delivered
- Check notification-service logs for delivery status
- Verify email/SMS provider API keys
- Check rate limit on notification provider side

### High latency on payment endpoint
- Profile Account Service query: missing index on ledger table?
- Check Fraud Service: are rules evaluated in order of cost?
- Connection pool exhaustion on database?
