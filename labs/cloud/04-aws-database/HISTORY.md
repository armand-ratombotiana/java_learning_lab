# History of AWS Database

## Timeline

| Year | Milestone |
|------|-----------|
| 2009 | **RDS** launches with MySQL, SQL Server, Oracle |
| 2010 | RDS Read Replicas for MySQL |
| 2011 | RDS Multi-AZ for high availability |
| 2012 | **DynamoDB** launches (single-digit ms latency at any scale) |
| 2013 | RDS PostgreSQL support; ElastiCache Redis |
| 2014 | **Aurora** preview (MySQL-compatible, 5x throughput) |
| 2015 | Aurora GA; DynamoDB TTL (auto-expire items) |
| 2016 | DynamoDB Global Tables (multi-region, active-active) |
| 2017 | RDS Aurora PostgreSQL GA; DynamoDB Accelerator (DAX) |
| 2018 | Aurora Serverless; DynamoDB On-Demand (pay-per-request) |
| 2019 | DynamoDB Transactions; Aurora Global Database |
| 2020 | DynamoDB CloudFormation enhancements; RDS Proxy |
| 2021 | Aurora Serverless v2; DynamoDB Standard-IA table class |
| 2022 | RDS Blue/Green deployments; DynamoDB Kinesis streaming |
| 2023 | Aurora I/O-Optimized (no I/O charges); DynamoDB Zero-ETL |
| 2024 | Aurora Limitless Database; DynamoDB multi-region strong consistency |

## Key Insight
Aurora is AWS's most transformative database innovation — it decouples compute from storage (like cloud-native DBs) while maintaining MySQL/PostgreSQL wire compatibility. The storage layer auto-scales, auto-repairs, and auto-replicates across 3 AZs.
