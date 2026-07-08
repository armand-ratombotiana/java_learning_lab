# Data Governance Internals

## PII Scanner Pipeline
1. Scan catalog for tables/columns. 2. Apply column-name patterns (keyword list). 3. Read data sample (N rows). 4. Apply regex patterns to values. 5. Calculate confidence score. 6. Flag potential PII columns. 7. Route for human review. 8. Update metadata catalog with sensitivity tags.

## Dynamic Masking Implementation
Query intercept: proxy modifies SQL before execution. View-based: pre-defined views with masking per role. Policy engine: evaluate user's role + sensitivity of requested data. Apply masking function: substitute, null, encrypt. Return masked results transparently to user.

## GDPR Compliance Pipeline
Right to access: search all data stores for user record -> compile report. Right to erasure: locate all copies -> delete or anonymize -> confirm deletion -> retain deletion log. Right to portability: export data in machine-readable format. Data Protection Impact Assessment (DPIA): automated scanner for high-risk processing.

## Audit Log System
Capture: query events, access attempts, permission changes, data modifications. Store: immutable log (S3, Kafka). Index: for search and analysis. Retain: per compliance requirements (GDPR: 3 years, SOX: 7 years). Alert: on security-critical events (unusual access patterns, permission escalation).
