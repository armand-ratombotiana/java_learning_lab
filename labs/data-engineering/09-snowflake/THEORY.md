# Snowflake Data Cloud Theory

## Architecture Layers
Snowflake's three-layer architecture: Storage Layer (compressed columnar data in S3/Azure/GCS with micro-partitioning), Compute Layer (virtual warehouses as elastic clusters of VMs), and Services Layer (authentication, metadata, optimizer, security). Each layer scales independently.

## Virtual Warehouse Sizing
X-Small (1 credit/hr) through 5X-Large (256 credits/hr). Larger warehouses have more memory and CPU for complex queries. Multi-cluster warehouses auto-scale horizontally for concurrency.

## Micro-Partitioning
Data automatically divided into 50-500 MB micro-partitions. Columnar storage within partitions. Automatic metadata collection (min, max, null count, distinct count) enables pruning — eliminating irrelevant partitions at query time without manual indexing.

## Time Travel & Fail-safe
Standard: 1 day Time Travel. Enterprise: 90 days. Fail-safe: additional 7 days (Snowflake-managed recovery only). AT/BEFORE syntax for point-in-time queries. UNDROP for table recovery.

## Zero-Copy Cloning
Creates metadata-only snapshot pointing to same storage fragments. Copy-on-write: only new/modified data consumes additional storage. Metadata-only operation completes in seconds regardless of table size.
