# Snowflake Data Engineer Interview Guide

## Interview Structure
- Recruiter Screen (30 min): Background, SQL experience, why Snowflake
- Technical Phone (45-60 min): SQL deep dive, warehouse concepts
- Systems Design (60 min): Data sharing, migration, zero-copy cloning
- Hiring Manager (45 min): Behavioral + data architecture decisions
- Bar Raiser (45 min): Leadership, conflict resolution

## Key Topics

### Architecture
- Virtual warehouses: XS-6XL, auto-suspend/resume, multi-cluster
- Micro-partitions: 50-500 MB, automatic pruning
- Cloud services layer: authentication, query optimization, metadata
- Separation of storage and compute

### SQL Patterns to Master
- Window functions: ROW_NUMBER, RANK, LAG/LEAD, FIRST_VALUE
- JSON: PARSE_JSON, FLATTEN, VARIANT type, DOT NOTATION
- Time-series: DATE_TRUNC, DATEDIFF, DATEADD
- MERGE INTO for UPSERT and CDC
- PIVOT/UNPIVOT for cross-tabulation

### Performance Optimization
- Clustering keys (up to 4 columns, automatic clustering available)
- Search optimization service for point lookups
- Materialized views vs views vs CTEs
- Query profiling with EXPLAIN and ACCOUNT_USAGE
- Caching layers: results (24h), metadata, warehouse data

### Key Features
- Time travel: AT/BEFORE with 1-90 day retention
- Zero-copy cloning: CREATE CLONE for dev/test
- Data sharing: reader accounts, listings, marketplace
- Tasks + streams for CDC automation
- Dynamic tables for declarative transformation
- External tables for querying data in place
- Iceberg tables for open-format compatibility

## Sample Questions
1. "Design a migration from Redshift to Snowflake"
2. "How would you handle CDC from a source database?"
3. "Design a cost-optimized Snowflake deployment"
4. "Explain how micro-partition pruning works"
5. "Design a multi-tenant data sharing architecture"

## Resources
- Snowflake documentation: SQL reference, best practices
- Snowflake blog: engineering deep dives
- Cognilytica SnowPro certification guide
