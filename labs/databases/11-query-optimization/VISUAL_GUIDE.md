# Visual Guide: Query Execution Plans

```
EXPLAIN ANALYZE SELECT * FROM orders WHERE customer_id = 42;

Before Index:
Seq Scan on orders (cost=0.00..483.00 rows=50 width=100)
  Filter: (customer_id = 42)
  Rows Removed by Filter: 50000
  Planning Time: 0.15 ms
  Execution Time: 45.32 ms

After CREATE INDEX ON orders(customer_id):
Bitmap Heap Scan on orders (cost=4.43..40.89 rows=50 width=100)
  Recheck Cond: (customer_id = 42)
  -> Bitmap Index Scan on idx_orders_customer (cost=0.00..4.42 rows=50)
     Index Cond: (customer_id = 42)
  Planning Time: 0.18 ms
  Execution Time: 0.32 ms  ← 140x faster!

N+1 Problem Visualization:
                    ┌─────────┐
        1 query ───→│  100 users  │
                    └────┬────┘
        100 queries      │
         ┌───────────────┼───────────────┐
         ▼               ▼               ▼
    ┌─────────┐    ┌─────────┐      ┌─────────┐
    │ orders 1│    │ orders 2│      │ orders N│
    └─────────┘    └─────────┘      └─────────┘

With JOIN FETCH:
    ┌───────────────────────────────────┐
    │SELECT * FROM users JOIN orders... │  ← 1 query
    └───────────────────────────────────┘
```
