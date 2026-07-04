# Visual Guide: Relational Database Concepts

## Entity Relationship Diagram (ERD) Notation

```
CUSTOMERS                   ORDERS                     PRODUCTS
+------------+             +------------+             +------------+
| PK id      |──1:N────>   | PK id      |             | PK id      |
| name       |             | FK cust_id |────N:M──── | name       |
| email      |             | order_date |             | price      |
+------------+             | total      |             +------------+
                           +------------+
                                    |
                                    | 1:N
                                    v
                              ORDER_ITEMS
                            +------------+
                            | PK id      |
                            | FK order_id|
                            | FK prod_id |
                            | quantity   |
                            | unit_price |
                            +------------+
```

## Normalization Flow

```
Unnormalized (repeating groups)
        ↓ 1NF (atomic columns)
First Normal Form
        ↓ 2NF (remove partial dependencies)
Second Normal Form
        ↓ 3NF (remove transitive dependencies)
Third Normal Form
        ↓ BCNF (every determinant is a key)
Boyce-Codd Normal Form
```

## ACID Transaction Flow

```
BEGIN TXN
    ↓
Read snapshot (isolation)
    ↓
Execute operations (atomicity)
    ↓
Validate constraints (consistency)
    ↓
Write WAL (durability)
    ↓
COMMIT → WAL flush → data pages updated
```

## JDBC Architecture

```
Java App
    ↓ JDBC API
DriverManager / DataSource
    ↓
JDBC Driver (Type 4)
    ↓ network protocol
Database Server
    ↓
Storage Engine
```

## JPA Entity State Transitions

```
                persist()
    NEW ──────────────────► MANAGED
     ▲                        │
     │                        │ find() / JPQL
     │                        │
     │                        ▼
     │                   DETACHED
     │                        │
     │                        │ remove()
     │                        ▼
     └─────────────────── REMOVED
```
