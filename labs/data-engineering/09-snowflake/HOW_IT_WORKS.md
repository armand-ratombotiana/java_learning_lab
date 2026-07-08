# How Snowflake Data Cloud Works

1. Client connects and authenticates via Services Layer
2. SQL is parsed, validated, and optimized using metadata statistics
3. Optimizer builds efficient execution plan with partition pruning
4. Plan executed on virtual warehouse nodes in parallel
5. Each node reads relevant micro-partitions in columnar format
6. Columnar data is decompressed and processed
7. Results aggregated and returned to client
8. Warehouse auto-suspends after configured idle period
9. Data continuously protected via encryption, Time Travel, and Fail-safe
