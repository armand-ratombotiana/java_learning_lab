# Mental Models for AWS Storage

## 1. The Storage Pyramid

```
                    ┌──────────┐
                    │  Cache   │  Ephemeral, instance store
                   ┌┴──────────┴┐
                   │   Block    │  EBS — single attachment, low latency
                  ┌┴────────────┴┐
                  │    File     │  EFS — shared, NFS protocol, multi-EC2
                 ┌┴──────────────┴┐
                 │    Object     │  S3 — API-accessible, global, unlimited
                ┌┴────────────────┴┐
                │    Archive      │  Glacier — cold, retrieval takes minutes/hours
```

## 2. The Iceberg Analogy (S3 Storage Classes)

```
Standard:    ████████████████████████████  (hot, instant access)
Standard-IA: ██████████░░░░░░░░░░░░░░░░  (warm, cheaper storage, retrieval cost)
One Zone-IA: ████░░░░░░░░░░░░░░░░░░░░░░  (low durability, lowest cost for access)
Glacier IR:  ████████████████████████░░  (frozen, ms retrieval, 3-5 min access)
Glacier:     ██████░░░░░░░░░░░░░░░░░░░░  (frozen, 1-5 min retrieval)
Deep Archive:██░░░░░░░░░░░░░░░░░░░░░░░░  (deep frozen, 12h retrieval)
```

## 3. The Block vs File vs Object Table

| Property | Block (EBS) | File (EFS) | Object (S3) |
|----------|-------------|------------|-------------|
| Unit | Block (512B-16KB) | File (bytes-GB) | Object (bytes-5TB) |
| Access | EC2 only (attach) | NFS mount (multi-EC2) | HTTP REST (anywhere) |
| Modify | In-place random writes | In-place | Rewrite entire object |
| Consistency | Strong | Strong | Read-after-write for new puts |
| Use case | DB, OS | Shared content, logs | Data lakes, backups, static |

## 4. The Lifecycle Conveyor Belt

```
Object uploaded → Standard (30 days) → Standard-IA (90 days) →
Glacier IR (365 days) → Glacier (1095 days) → Deep Archive (2555 days) → Expire/Delete

Each transition reduces storage cost but increases retrieval time and cost.
Lifecycle policy: automatic, per-prefix or per-tag filter.
```
