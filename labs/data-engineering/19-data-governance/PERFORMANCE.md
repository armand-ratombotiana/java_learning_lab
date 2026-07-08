# Performance Optimization for Data Governance

## PII Scanning
Sample-based scanning for large tables; parallel scanning across partitions; incremental scanning (only new columns)

## Masking
Pre-compute masked views for frequent access patterns; cache masking rules per role; optimize masking UDFs

## Audit Logging
Async audit log writes (non-blocking); batch log aggregation; partition logs by date for efficient querying

## RBAC Evaluation
Cache resolved permissions per user session; optimize role hierarchy traversal; index permission lookups
