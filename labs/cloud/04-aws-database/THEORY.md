# AWS Database - Theory

## Overview
AWS offers managed database services for relational, NoSQL, and in-memory workloads.

## 1. Amazon RDS (Relational Database Service)

### Supported Engines
- **MySQL**: Popular open-source RDBMS
- **PostgreSQL**: Advanced open-source RDBMS
- **MariaDB**: MySQL-compatible fork
- **Oracle**: Enterprise-grade RDBMS
- **SQL Server**: Microsoft enterprise DB
- **Aurora**: MySQL/PostgreSQL compatible cloud-native

### Key Features
- **Multi-AZ**: Synchronous replication for HA
- **Read Replicas**: Asynchronous replication for read scaling
- **Automated Backups**: Daily automated backups
- **Point-in-time Recovery**: Restore to any second
- **Performance Insights**: Query performance monitoring
- **Enhanced Monitoring**: 50+ metrics

### Aurora Features
- **Serverless**: Auto-scaling compute
- **Global Database**: Cross-region replication
- **Backtrack**: Restore to previous state
- **Multi-Master**: Multiple write nodes
- **Parallel Query**: Faster analytics

## 2. Amazon DynamoDB (NoSQL)

### What is DynamoDB?
- Fully managed NoSQL database
- Single-digit millisecond latency
- Serverless option available
- Multi-region active-active

### Data Model
- **Tables**: Collection of items
- **Items**: Single record
- **Attributes**: Key-value pairs
- **Partition Key**: Required, distributes data
- **Sort Key**: Optional, enables ordering

### Capacity Modes
- **Provisioned**: Pre-configured RCU/WCU
- **On-Demand**: Pay-per-request pricing

### Features
- **DAX**: In-memory cache (microsecond latency)
- **Streams**: Change data capture
- **TTL**: Auto expire items
- **GSI/LSI**: Secondary indexes
- **Transactions**: ACID support

## 3. Amazon ElastiCache (In-Memory)

### What is ElastiCache?
- Managed Redis or Memcached
- Sub-millisecond latency
- Caching and session store

### Redis vs Memcached
| Feature | Redis | Memcached |
|---------|-------|------------|
| Data Types | Multiple | String only |
| Persistence | Yes (RDB/AOF) | No |
| Replication | Master-replica | None |
| Clustering | Yes | Yes (sharding) |
| Pub/Sub | Yes | No |

### Use Cases
- Session storage
- Real-time analytics
- Leaderboards
- Caching
- Chat/messaging

### Features
- **Cluster Mode**: Horizontal scaling
- **Read Replicas**: Scale reads
- **Auto Failover**: High availability
- **Multi-AZ**: Availability
- **Backup/Restore**: Point-in-time