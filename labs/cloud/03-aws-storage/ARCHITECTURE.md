# Architecture — AWS Storage

## Data Lake Architecture

```
                         ┌──────────────┐
                         │  Data Sources│
                         └──────┬───────┘
                                │
                    ┌───────────┴───────────┐
                    │    S3 (Raw Bucket)    │
                    │   /ingest/yyyy/mm/dd  │
                    │   (Standard, 7 days)  │
                    └───────────┬───────────┘
                                │
                         Lambda (ETL)
                         (Java, Spark)
                                │
                    ┌───────────┴───────────┐
                    │  S3 (Curated Bucket)  │
                    │   /parquet/table_name │
                    │   (Standard-IA)       │
                    └───────────┬───────────┘
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
         ┌────▼────┐      ┌────▼────┐       ┌────▼────┐
         │Athena   │      │Redshift │       │EMR      │
         │(SQL)    │      │(DW)     │       │(Spark)  │
         └─────────┘      └─────────┘       └─────────┘

Lifecycle: Raw → Standard (7d) → IA (30d) → Glacier (1yr) → Deep Archive (7yr)
```

## Backup and Disaster Recovery

```
┌──────────────────────────────────────────────┐
│               Primary Region                  │
│  ┌────────────────────────────────────────┐  │
│  │ EBS Snapshot (every 6h) → S3           │  │
│  │ RDS Automated Backup (every 30min)     │  │
│  │ S3 CRR (cross-region replication)      │  │
│  │ DynamoDB PITR (continuous)             │  │
│  └────────────────────────────────────────┘  │
└──────────────────┬───────────────────────────┘
                   │
            S3 CRR (async)
                   │
┌──────────────────▼───────────────────────────┐
│               DR Region (us-west-2)           │
│  ┌────────────────────────────────────────┐  │
│  │ S3 bucket with replicated objects     │  │
│  │ (read-only, promote on failover)      │  │
│  │ EBS snapshots ready to restore        │  │
│  │ RDS snapshot ready for cross-region   │  │
│  └────────────────────────────────────────┘  │
└──────────────────────────────────────────────┘
```

## Multi-Tier Storage for Java App

```
┌──────────────────────────────────────────────────────────┐
│                  Java Application                         │
├──────────────────────────────────────────────────────────┤
│ Hot Tier (EBS gp3):                                      │
│   ├── /data/db          — PostgreSQL data files          │
│   ├── /data/redis       — Redis RDB/AOF (persistence)    │
│   └── /data/temp        — Application temp files         │
├──────────────────────────────────────────────────────────┤
│ Warm Tier (EFS):                                         │
│   ├── /shared/uploads   — User-uploaded content          │
│   └── /shared/configs   — Shared configuration files     │
├──────────────────────────────────────────────────────────┤
│ Cold Tier (S3):                                          │
│   ├── s3://app-assets   — Static files, images, CSS      │
│   ├── s3://app-logs     — Application logs (lifecycle)   │
│   └── s3://app-backups  — Database snapshots, archives   │
└──────────────────────────────────────────────────────────┘
```
