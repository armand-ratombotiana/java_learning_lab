# Flyway - Projects

This document contains two complete projects demonstrating Flyway: a mini-project for learning database migrations and a real-world project implementing production-grade schema version control.

## Project 1: Flyway Basics Mini-Project

### Overview

This mini-project demonstrates fundamental Flyway concepts including migration scripts, versioned and repeatable migrations, and baseline operations. It serves as a learning starting point for database version control.

### Project Structure

```
flyway-basics/
├── pom.xml
├── src/
│   └── main/
│       └── resources/
│           └── db/
│               └── migration/
└── docker/
    └── init.sql
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>flyway-basics</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
        <flyway.version>10.4.1</flyway.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.flywaydb</groupId>
                <artifactId>flyway-maven-plugin</artifactId>
                <version>${flyway.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### V1__Create_users_table.sql

```sql
-- V1__Create_users_table.sql
-- Create the users table

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(is_active);
```

### V2__Add_organization_column.sql

```sql
-- V2__Add_organization_column.sql
-- Add organization_id to users

ALTER TABLE users 
ADD COLUMN organization_id INTEGER REFERENCES organizations(id);

CREATE INDEX idx_users_organization ON users(organization_id);
```

### V3__Create_orders_table.sql

```sql
-- V3__Create_orders_table.sql
-- Create the orders table

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    order_number VARCHAR(20) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL,
    shipping_address TEXT,
    billing_address TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_number ON orders(order_number);
```

### V4__Create_order_items_table.sql

```sql
-- V4__Create_order_items_table.sql
-- Create order items

CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES orders(id),
    product_id INTEGER NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);
```

### R__Seed_data.sql

```sql
-- R__Seed_data.sql
-- Seed initial data (re-runs on change)

INSERT INTO users (username, email, password_hash, first_name, last_name) VALUES
('admin', 'admin@example.com', '$2a$10$xxx', 'Admin', 'User'),
('demo', 'demo@example.com', '$2a$10$xxx', 'Demo', 'User')
ON CONFLICT (username) DO UPDATE SET
    email = EXCLUDED.email,
    updated_at = CURRENT_TIMESTAMP;
```

### FlywayLab.java

```java
package com.learning.flyway;

import org.flywaydb.core.Flyway;

public class FlywayLab {
    
    public static void main(String[] args) {
        System.out.println("=== Flyway Basics Lab ===\n");
        
        // Migration types
        System.out.println("1. Migration Types:");
        System.out.println("   - Versioned: V1__Description.sql (applied once in order)");
        System.out.println("   - Repeatable: R__Description.sql (re-applied when checksums differ)");
        
        System.out.println("\n2. Versioned Migrations:");
        System.out.println("   V1__Create_users_table.sql");
        System.out.println("   V2__Add_organization_column.sql");
        System.out.println("   V3__Create_orders_table.sql");
        System.out.println("   V4__Create_order_items_table.sql");
        
        System.out.println("\n3. Repeatable Migrations:");
        System.out.println("   R__Seed_data.sql (re-runs when modified)");
        
        System.out.println("\n4. Configuration:");
        System.out.println("   flyway.url=jdbc:postgresql://localhost:5432/db");
        System.out.println("   flyway.locations=classpath:db/migration");
        System.out.println("   flyway.baseline-on-migrate=true");
        
        // Simulate migration info
        System.out.println("\n5. Migration Info:");
        showMigrationInfo();
        
        System.out.println("\n6. Commands:");
        System.out.println("   mvn flyway:migrate");
        System.out.println("   mvn flyway:clean");
        System.out.println("   mvn flyway:info");
        System.out.println("   mvn flyway:validate");
        System.out.println("   mvn flyway:repair");
        
        System.out.println("\n=== Lab Complete ===");
    }
    
    private static void showMigrationInfo() {
        String[][] migrations = {
            {"V1", "Create users table", "SUCCESS", "2024-01-01 10:00:00"},
            {"V2", "Add organization column", "SUCCESS", "2024-01-01 10:00:01"},
            {"V3", "Create orders table", "SUCCESS", "2024-01-01 10:00:02"},
            {"V4", "Create order items table", "SUCCESS", "2024-01-01 10:00:03"},
            {"R1", "Seed data", "SUCCESS", "2024-01-01 10:00:04"}
        };
        
        System.out.println("   Version | Description           | State    | Applied At");
        System.out.println("   --------|----------------------|----------|------------");
        for (String[] m : migrations) {
            System.out.printf("   %-7s | %-20s | %-8s | %s%n",
                m[0], m[1], m[2], m[3]);
        }
    }
}
```

### Build and Run Instructions

```bash
cd flyway-basics

# Configure database
mvn flyway:info -Dflyway.url=jdbc:postgresql://localhost:5432/mydb

# Run migrations
mvn flyway:migrate

# Check migration status
mvn flyway:info
```

## Project 2: Production Database Migration

### Overview

This real-world project implements comprehensive production database migrations with multi-environment support, rollback strategies, and team collaboration workflows.

### Project Structure

```
flyway-production/
├── pom.xml
├── src/
│   └── main/
│       └── resources/
│           └── db/
│               └── migration/
└── config/
    ├── application.yml
    └── flyway.conf
```

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/orders
    username: postgres
    password: ${DB_PASSWORD}

flyway:
  enabled: true
  locations: classpath:db/migration,db.migration/prod
  baseline-on-migrate: true
  baseline-version: 1.0.0
  validate-on-migrate: true
  out-of-order: false
  ignore-missing-migrations: false
  ignore-future-migrations: true
  clean-disabled: true
```

### V5__Add_payment_tables.sql

```sql
-- V5__Add_payment_tables.sql
-- Add payment-related tables

CREATE TABLE payment_methods (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    type VARCHAR(20) NOT NULL,
    provider VARCHAR(20) NOT NULL,
    token VARCHAR(255) NOT NULL,
    last_four VARCHAR(4),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_methods_user ON payment_methods(user_id);
CREATE INDEX idx_payment_methods_token ON payment_methods(token);

CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES orders(id),
    payment_method_id INTEGER REFERENCES payment_methods(id),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(20) NOT NULL,
    transaction_id VARCHAR(100),
    gateway_response JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_transaction ON payments(transaction_id);

-- Add foreign key after payments is created
ALTER TABLE payments 
ADD CONSTRAINT fk_payments_payment_method 
FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id);
```

### V6__Add_shipping_tables.sql

```sql
-- V6__Add_shipping_tables.sql
-- Add shipping-related tables

CREATE TABLE shipments (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES orders(id),
    carrier VARCHAR(50) NOT NULL,
    tracking_number VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    shipping_address TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shipments_order ON shipments(order_id);
CREATE INDEX idx_shipments_tracking ON shipments(tracking_number);
CREATE INDEX idx_shipments_status ON shipments(status);

CREATE TABLE shipment_items (
    id SERIAL PRIMARY KEY,
    shipment_id INTEGER NOT NULL REFERENCES shipments(id),
    order_item_id INTEGER NOT NULL REFERENCES order_items(id),
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shipment_items_shipment ON shipment_items(shipment_id);
CREATE INDEX idx_shipment_items_order_item ON shipment_items(order_item_id);
```

### V7__Add_audit_tables.sql

```sql
-- V7__Add_audit_tables.sql
-- Add audit trail for compliance

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    record_id INTEGER NOT NULL,
    action VARCHAR(10) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    changed_by INTEGER REFERENCES users(id),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address INET
);

CREATE INDEX idx_audit_logs_table ON audit_logs(table_name, record_id);
CREATE INDEX idx_audit_logs_changed_at ON audit_logs(changed_at);

-- Create trigger function
CREATE OR REPLACE FUNCTION audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_logs(table_name, record_id, action, new_values, changed_by)
        VALUES (TG_TABLE_NAME, NEW.id, 'INSERT', to_jsonb(NEW), current_setting('app.user_id', TRUE));
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_logs(table_name, record_id, action, old_values, new_values, changed_by)
        VALUES (TG_TABLE_NAME, OLD.id, 'UPDATE', to_jsonb(OLD), to_jsonb(NEW), current_setting('app.user_id', TRUE));
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit_logs(table_name, record_id, action, old_values, changed_by)
        VALUES (TG_TABLE_NAME, OLD.id, 'DELETE', to_jsonb(OLD), current_setting('app.user_id', TRUE));
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Apply trigger to sensitive tables
CREATE TRIGGER audit_users
AFTER INSERT OR UPDATE OR DELETE ON users
FOR EACH ROW EXECUTE FUNCTION audit_trigger();
```

### V8__Add_indexes_performance.sql

```sql
-- V8__Add_indexes_performance.sql
-- Performance indexes for common queries

-- Composite index for order search
CREATE INDEX idx_orders_user_status 
ON orders(user_id, status) WHERE status IN ('PENDING', 'PROCESSING');

-- Partial index for active users
CREATE INDEX idx_users_active_email 
ON users(email) WHERE is_active = TRUE;

-- Index on orders total for analytics
CREATE INDEX idx_orders_total ON orders(total_amount);

-- Covering index for order items
CREATE INDEX idx_order_items_order_product 
ON order_items(order_id, product_id) INCLUDE (quantity, unit_price);

-- GIN index for JSONB gateway_response
ALTER TABLE payments ADD CONSTRAINT payments_gateway_response_json 
CHECK (jsonb_typeof(gateway_response) IN ('object', 'null'));
```

### U5__Drop_payment_tables.sql (Undo Migration)

```sql
-- U5__Drop_payment_tables.sql
-- Undo V5: Remove payment tables

ALTER TABLE payments DROP CONSTRAINT fk_payments_payment_method;

DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS payment_methods CASCADE;
```

### FlywayMigrator.java

```java
package com.learning.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FlywayConfiguration;
import org.flywaydb.core.api.exception.FlywayValidateException;

import java.util.Map;

public class FlywayMigrator {
    
    public static void main(String[] args) {
        System.out.println("=== Flyway Production Migration ===\n");
        
        // Configuration for different environments
        System.out.println("1. Multi-Environment Configuration:");
        
        Map<String, String> configs = Map.of(
            "dev", "locations: classpath:db/migration",
            "test", "locations: classpath:db/migration,db/migration/test",
            "prod", "locations: classpath:db/migration,db/migration/prod"
        );
        
        configs.forEach((env, locations) -> 
            System.out.println("   " + env + ": " + locations));
        
        // Migration strategies
        System.out.println("\n2. Migration Strategies:");
        System.out.println("   - Versioned: Sequential apply (V1, V2, V3...)");
        System.out.println("   - Repeatable: Re-apply on checksum change");
        System.out.println("   - Undo: U5__Description.sql reverses V5");
        
        // Best practices
        System.out.println("\n3. Best Practices:");
        System.out.println("   - Always use transactions");
        System.out.println("   - Include ROLLBACK in scripts");
        System.out.println("   - Test in dev before prod");
        System.out.println("   - Use baseline for existing databases");
        
        // CI/CD integration
        System.out.println("\n4. CI/CD Integration:");
        System.out.println("   mvn flyway:migrate -Dflyway.password=${DB_PASSWORD}");
        
        System.out.println("\n=== Operations Complete ===");
    }
    
    public void migrate() {
        Flyway.configure()
            .dataSource(
                "jdbc:postgresql://localhost:5432/mydb",
                "postgres",
                System.getenv("DB_PASSWORD")
            )
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .load()
            .migrate();
    }
    
    public void validate() {
        try {
            Flyway.configure()
                .dataSource(
                    "jdbc:postgresql://localhost:5432/mydb",
                    "postgres",
                    System.getenv("DB_PASSWORD")
                )
                .load()
                .validate();
            System.out.println("Validation passed!");
        } catch (FlywayValidateException e) {
            System.out.println("Validation failed: " + e.getMessage());
        }
    }
}
```

### Build and Run Instructions

```bash
cd flyway-production

# Run migrations
mvn flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/mydb

# Verify migrations
mvn flyway:info

# Validate schema
mvn flyway:validate

# Repair broken migrations
mvn flyway:repair

# Clean (WARNING: drops all tables!)
mvn flyway:clean
```

### Docker Compose Setup

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: orders
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
  
  flyway:
    image: flyway/flyway:latest
    depends_on:
      - postgres
    command: migrate -url=jdbc:postgresql://postgres:5432/orders -user=postgres -password=postgres
```

## Summary

These two projects demonstrate:

1. **Mini-Project**: Basic Flyway setup with versioned and repeatable migrations
2. **Production Project**: Complete production migrations with multi-environment support, undo migrations, and audit trail

Flyway enables reliable database schema version control for teams, with clear migration history and rollback capabilities.