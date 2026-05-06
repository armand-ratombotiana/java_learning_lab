DO $$
DECLARE
    i INT;
    schema_name TEXT;
BEGIN
    FOR i IN 1..2000 LOOP
        schema_name := 'tenant_' || lpad(i::text, 4, '0');

        EXECUTE format('CREATE SCHEMA IF NOT EXISTS %I', schema_name);

        EXECUTE format('
            CREATE TABLE IF NOT EXISTS %I.users(
                id BIGSERIAL PRIMARY KEY,
                username VARCHAR(255),
                email VARCHAR(255),
                created_at TIMESTAMP DEFAULT now()
            )', schema_name);

        EXECUTE format('
            CREATE TABLE IF NOT EXISTS %I.roles(
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(100)
            )', schema_name);

        EXECUTE format('
            CREATE TABLE IF NOT EXISTS %I.audit_logs(
                id BIGSERIAL PRIMARY KEY,
                action VARCHAR(255),
                created_at TIMESTAMP DEFAULT now()
            )', schema_name);
    END LOOP;
END $$;
