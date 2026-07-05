# Refactoring: Banking Platform

## Current Pain Points
- Duplicate validation logic across Account Service and Payment Service
- Hardcoded fraud rule thresholds in RuleEngine
- Synchronous ML scoring blocks payment processing
- No circuit breaker for downstream service calls
- Monolithic event handler in Notification Service

## Suggested Improvements
- Extract cross-cutting validation into a shared library (common module)
- Move fraud rule configuration to external config server or feature flags
- Implement async ML scoring with CompletableFuture + timeout
- Add Resilience4j circuit breaker on all inter-service HTTP calls
- Split Notification Service handlers per channel (email, SMS, push)
- Implement database per service with shared view layer for reporting
