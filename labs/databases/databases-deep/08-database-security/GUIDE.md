# Database Security — Deep Dive Guide

## Encryption at Rest

| Layer         | Mechanism                                    |
|--------------|----------------------------------------------|
| Disk-level   | LUKS, BitLocker (whole disk)                  |
| Filesystem   | eCryptfs, fscrypt (per-directory)             |
| Database TDE | Transparent Data Encryption (SQL Server, Oracle) |
| Column-level | Application encrypts values before storing    |

## Encryption in Transit

- **TLS**: server certificate, optional client certificate
- **mTLS**: mutual TLS, both sides authenticate
- Connection strings: `sslmode=verify-full` (PostgreSQL)

## Row-Level Security (RLS)

PostgreSQL example:
```sql
CREATE POLICY user_policy ON orders
  USING (customer_id = current_setting('app.current_user_id')::INT);
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
```

## SQL Injection Prevention

| Method                | Secure |
|-----------------------|--------|
| String concatenation  | No     |
| Prepared statements   | Yes    |
| Stored procedures     | Yes    |
| ORM (Hibernate/JPA)   | Yes    |

## Audit Logging

PostgreSQL: `pgaudit` extension or trigger-based:
```sql
CREATE TABLE audit_log (op TEXT, table_name TEXT, old_data JSONB, new_data JSONB, ts TIMESTAMPTZ);
CREATE FUNCTION audit_trigger() RETURNS TRIGGER AS $$ BEGIN ... END $$ LANGUAGE plpgsql;
```