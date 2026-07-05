# Internals: Banking Platform

## Service Breakdown
- **account-service**: Account CRUD, balance management, holds, ledger entries
- **payment-service**: Payment initiation, processing, idempotency, settlement
- **fraud-service**: Rule evaluation, ML scoring, anomaly detection
- **notification-service**: Templating, delivery, retry, delivery status tracking
- **gateway-service**: API routing, auth, rate limiting, request validation
- **user-service**: User registration, KYC, authentication, profile management
- **common**: Shared DTOs, event definitions, utility classes

## Core Data Flows
- Payment lifecycle: INITIATED -> PENDING_FRAUD -> PENDING_AUTH -> COMPLETED / FAILED
- Account holds: AVAILABLE_BALANCE - PENDING_HOLDS = EFFECTIVE_BALANCE
- Event topics: account.events, payment.events, fraud.events, notification.events

## Storage
- Each service has its own database instance (PostgreSQL per service)
- Event store (Kafka) for cross-service event propagation
- Redis for idempotency cache and rate limiting counters
