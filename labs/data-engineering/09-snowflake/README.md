# Snowflake Data Cloud

## Overview
Snowflake is a cloud-native data platform that separates storage and compute, enabling elastic scaling, concurrent workloads, and zero-copy cloning. This lab covers Snowflake's unique architecture, virtual warehouses, clustering, time travel, and data sharing.

## Key Concepts
- **Storage-Compute Separation**: Independent scaling of storage and compute resources
- **Virtual Warehouses**: Elastic compute clusters for query execution
- **Micro-Partitioning**: Automatic columnar storage and pruning (50-500 MB per partition)
- **Time Travel**: Query and restore historical data with configurable retention (1-90 days)
- **Zero-Copy Cloning**: Instant copy-on-write clones without additional storage cost
- **Data Sharing**: Secure sharing across Snowflake accounts without data movement

## Learning Objectives
1. Understand Snowflake's three-layer cloud-agnostic architecture
2. Configure and manage virtual warehouses of varying sizes
3. Implement clustering keys for query optimization on large tables
4. Use Time Travel for data recovery, auditing, and point-in-time analysis
5. Leverage Zero-Copy Cloning for dev/test environments
6. Share data securely across Snowflake accounts using Reader Accounts
