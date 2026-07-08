# Debugging Data Quality Engineering

## High Null Rate Unexpectedly
Check for schema drift; source system schema change may have renamed column

## Duplicates After Restart
Check batch_id or watermark distribution; restart may reprocess data

## Referential Integrity Failures
Join failing keys with LEFT ANTI to source; check for deleted or updated source records

## Freshness SLA Misses
Check pipeline lag; upstream delay or resource contention may slow processing
