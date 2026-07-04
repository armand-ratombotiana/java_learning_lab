# Refactoring

## Before: Flat Table
```sql
CREATE TABLE sales_wide (transaction_id, customer_name, product_name, ...);
```
Problems: Redundancy, update anomalies, high storage.

## After: Star Schema
```sql
CREATE TABLE dim_customer (customer_key, name, ...);
CREATE TABLE fact_sales (customer_key, product_key, ..., amount);
```
