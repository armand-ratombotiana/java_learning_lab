# Architecture — AWS Fundamentals

## Three-Tier Web Application Architecture

```
                        ┌─────────┐
                        │ Route 53 │
                        │  DNS     │
                        └────┬─────┘
                             │
                        ┌────▼─────┐
                        │ CloudFront│
                        │  CDN     │
                        └────┬─────┘
                             │
                        ┌────▼─────┐
                        │   ALB    │
                        │ (HTTPS)  │
                        └────┬─────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
         ┌────▼───┐    ┌────▼───┐    ┌────▼───┐
         │EC2 App │    │EC2 App │    │EC2 App │
         │AZ-a    │    │AZ-b    │    │AZ-c    │
         └───┬────┘    └───┬────┘    └───┬────┘
              │              │              │
         ┌────▼──────────────▼──────────────▼────┐
         │      ElastiCache (Redis)              │
         │      Session state, cache             │
         └───────────────────────────────────────┘
              │
         ┌────▼──────────────────────────────────┐
         │      RDS Multi-AZ (Aurora)            │
         │      Primary in AZ-a, standby AZ-b    │
         └───────────────────────────────────────┘

         ┌───────────────────────────────────────┐
         │      S3 (Static assets, backups)      │
         └───────────────────────────────────────┘
```

## Architecture Decisions

### 1. Compute: EC2 vs Lambda
| Factor | EC2 | Lambda |
|--------|-----|--------|
| State | Stateful | Stateless |
| Duration | Long-running | 15 min max |
| Warm start | Always on | ~100ms cold start |
| Java startup | Fast (JVM always hot) | Slow (JVM cold start) |
| Cost | Predictable | Per-invocation |

### 2. Database: RDS vs DynamoDB
| Factor | RDS (Aurora) | DynamoDB |
|--------|-------------|----------|
| Schema | Relational (SQL) | NoSQL (key-value) |
| ACID | Full ACID | Single-item ACID |
| Scale | Up to 128TB | Unlimited |
| Use case | ORM, joins, transactions | Session, leaderboard, IoT |

### 3. Storage: EBS vs EFS vs S3
| Factor | EBS | EFS | S3 |
|--------|-----|-----|-----|
| Type | Block | File (NFS) | Object |
| Access | Single EC2 | Multiple EC2 | HTTP/API |
| Use case | DB volumes | Shared files | Backups, static |
| Max size | 16 TiB/volume | 8 TB/file | Unlimited |

## High Availability Architecture

```
ASG: min=2, max=10, across 3 AZs
├── Launch template: Amazon Linux 2023, Java 17, app JAR
├── Scaling: CPU > 70% for 5 min (scale up)
├── Scaling: CPU < 30% for 15 min (scale down)
└── Health check: ALB /health endpoint, 10s interval

RDS: Multi-AZ Aurora
├── Primary: us-east-1a
├── Standby: us-east-1b
└── Failover: automatic < 30s
```

## Disaster Recovery Strategy

```
RPO (Recovery Point Objective): 1 hour
RTO (Recovery Time Objective): 4 hours

Strategy: Backup & Restore (cheapest)
├── RDS: automated snapshots every 30 min
├── S3: cross-region replication to us-west-2
├── EC2: AMI backup daily
└── Recovery: CloudFormation template to rebuild in us-west-2
```
