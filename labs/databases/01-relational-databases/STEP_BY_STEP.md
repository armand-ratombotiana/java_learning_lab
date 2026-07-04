# Step by Step: Designing a Relational Database Schema

## Goal
Design a schema for an e-commerce system with customers, products, orders, and order items.

## Step 1: Identify Entities
List the real-world objects:
- Customer
- Product
- Order
- OrderItem (line item)

## Step 2: Define Relationships
- Customer → Order: 1:N (one customer has many orders)
- Order → OrderItem: 1:N (one order has many items)
- Product → OrderItem: 1:N (one product appears in many order items)

## Step 3: Create Tables with Keys

```sql
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(320) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    stock_qty INTEGER NOT NULL DEFAULT 0 CHECK (stock_qty >= 0)
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    total DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    UNIQUE(order_id, product_id)
);
```

## Step 4: Add Indexes for Performance

```sql
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_order_date ON orders(order_date);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
```

## Step 5: Apply Normalization (Verify 3NF)

| Table | Candidate Key | No Partial Dep? | No Transitive Dep? |
|---|---|---|---|
|customers| id | N/A | N/A |
|products| id | N/A | N/A |
|orders| id | N/A | N/A |
|order_items| id, (order_id,product_id) | Yes | Yes |

## Step 6: Create JPA Entities

```java
// See CODE_DEEP_DIVE.md for full entity classes
```

## Step 7: Verify with Test Data

```sql
INSERT INTO customers (name, email) VALUES ('Alice', 'alice@example.com');
INSERT INTO products (name, price, stock_qty) VALUES ('Widget', 9.99, 100);
INSERT INTO orders (customer_id) VALUES (1);
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
VALUES (1, 1, 2, 9.99);
```

## Step 8: Query with JOIN

```sql
SELECT c.name, o.id AS order_id, p.name AS product, oi.quantity
FROM customers c
JOIN orders o ON c.id = o.customer_id
JOIN order_items oi ON o.id = oi.order_id
JOIN products p ON oi.product_id = p.id
WHERE c.email = 'alice@example.com';
```
