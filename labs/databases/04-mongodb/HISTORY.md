# History: MongoDB

## Timeline

- **2007**: 10gen founded by Dwight Merriman, Eliot Horowitz, and Kevin Ryan
- **2009**: MongoDB 1.0 released as open source (AGPL license)
- **2010**: MongoDB 1.4 – geospatial indexing, MapReduce
- **2012**: MongoDB 2.2 – aggregation framework
- **2013**: 10gen renamed to MongoDB Inc.
- **2015**: MongoDB 3.0 – pluggable storage engine (WiredTiger)
- **2016**: MongoDB 3.4 – sharding improvements, zones
- **2017**: MongoDB 3.6 – change streams, causal consistency
- **2018**: MongoDB 4.0 – multi-document ACID transactions
- **2019**: MongoDB 4.2 – distributed transactions, on-demand materialized views
- **2020**: MongoDB 4.4 – hedged reads, mirror reads, refined sharding
- **2021**: MongoDB 5.0 – native time-series collections, versioned API
- **2022**: MongoDB 6.0 – queryable encryption, cluster-to-cluster sync
- **2023**: MongoDB 7.0 – columnar compression, query sampling

## Key Milestones

- **WiredTiger Engine**: Replaced MMAPv1, providing document-level concurrency and compression
- **ACID Transactions**: Cross-collection, cross-shard transactions made MongoDB suitable for financial applications
- **Change Streams**: Real-time data change notifications for event-driven architectures
- **Atlas (DBaaS)**: Managed cloud service with global cluster distribution

## Java Driver History
- 2.x: Synchronous only, complex API
- 3.x: New driver with reactive streams and async support
- 4.x: Simplified API, builders pattern
- 5.x: Improved POJO codec support, faster serialization
