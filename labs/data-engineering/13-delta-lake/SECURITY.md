# Security for Delta Lake

## File Access
- IAM policies for S3/ADLS/GCS bucket access per medallion layer
- Bucket policies restricting access to Bronze/Silver/Gold

## Column-Level Security
```sql
CREATE VIEW safe_orders AS
SELECT id, amount, CONCAT(LEFT(card_last4,2),'**') AS masked_card
FROM delta.`/path`
```

## Encryption
- Server-side encryption (SSE-S3/SSE-KMS)
- Client-side encryption for sensitive PII columns
- Audit logging for all table access via Spark listener
