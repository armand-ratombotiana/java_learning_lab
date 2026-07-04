# Security: PostgreSQL

## Authentication Methods
```conf
# pg_hba.conf
# TYPE  DATABASE  USER       ADDRESS          METHOD
local   all       all                          peer
host    all       all        127.0.0.1/32      scram-sha-256
host    all       all        ::1/128           scram-sha-256
hostssl all       all        0.0.0.0/0         scram-sha-256
```

## SSL Configuration
```conf
# postgresql.conf
ssl = on
ssl_cert_file = 'server.crt'
ssl_key_file = 'server.key'
ssl_ca_file = 'root.crt'
ssl_min_protocol_version = 'TLSv1.3'
```

```java
// JDBC SSL connection
String url = "jdbc:postgresql://host:5432/db"
    + "?ssl=true"
    + "&sslmode=verify-full"
    + "&sslrootcert=root.crt"
    + "&sslcert=client.crt"
    + "&sslkey=client.key";
```

## Least Privilege Principle
```sql
-- Create application role
CREATE ROLE app_user WITH LOGIN PASSWORD 'strong_password';

-- Grant only needed permissions
GRANT CONNECT ON DATABASE myapp TO app_user;
GRANT USAGE ON SCHEMA app TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA app TO app_user;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA app TO app_user;

-- Revoke all on public schema
REVOKE ALL ON SCHEMA public FROM PUBLIC;
```

## Row-Level Security (RLS)
```sql
CREATE TABLE app.tenants (id BIGSERIAL PRIMARY KEY, name TEXT);

CREATE TABLE app.tenant_data (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT REFERENCES app.tenants(id),
    data TEXT
);

ALTER TABLE app.tenant_data ENABLE ROW LEVEL SECURITY;

CREATE POLICY tenant_isolation ON app.tenant_data
    USING (tenant_id = current_setting('app.tenant_id')::BIGINT);
```

```java
// Set tenant context in JDBC
try (Statement stmt = conn.createStatement()) {
    stmt.execute("SET app.tenant_id = '" + tenantId + "'");
}
```

## SQL Injection Prevention

```java
// WRONG: string concatenation
String sql = "SELECT * FROM users WHERE name = '" + userName + "'";

// RIGHT: parameterized query
String sql = "SELECT * FROM users WHERE name = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, userName);
```

## Encryption at Rest
```sql
-- pgcrypto extension
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Column-level encryption
INSERT INTO sensitive_data (ssn)
VALUES (pgp_sym_encrypt('123-45-6789', 'encryption_key'));

-- Decrypt
SELECT pgp_sym_decrypt(ssn, 'encryption_key') FROM sensitive_data;
```

## Audit Logging
```sql
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name TEXT,
    operation TEXT,
    old_data JSONB,
    new_data JSONB,
    changed_by TEXT,
    changed_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE OR REPLACE FUNCTION audit_trigger()
RETURNS trigger AS $$
BEGIN
    INSERT INTO audit_log (table_name, operation, old_data, new_data, changed_by)
    VALUES (TG_TABLE_NAME, TG_OP,
            row_to_json(OLD)::JSONB,
            row_to_json(NEW)::JSONB,
            current_user);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```
