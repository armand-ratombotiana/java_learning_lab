# Performance — AWS Storage

## S3 Performance

### Request Rate Scaling
- **Prefix limit**: 3,500 PUT/COPY/POST/DELETE per prefix, 5,500 GET/HEAD per prefix
- **Scaling strategy**: Distribute keys across multiple prefixes
  - `logs/2024/01/01/file1.log` → prefix = `logs/` (limit applies)
  - `logs-2024-01-01/file1.log` → prefix = `logs-2024-01-01` (unused)
  - Better: hash prefix: `logs/a/2024/`, `logs/b/2024/`, ... `logs/f/2024/`

```java
// High-throughput key naming with hash prefix
String hash = String.format("%02x", key.hashCode() % 256);
String s3Key = hash + "/" + date + "/" + originalFile;
```

### Multipart Upload Performance
```
File size: 1GB
Part size: 50MB → 20 parts
Concurrent uploads: 5-10 parts in parallel
Total speedup: ~5-10x over single PUT
```

### S3 Transfer Acceleration
```
Uses CloudFront edge locations
Improves upload speeds by 50-500% for cross-region/long-distance clients
Adds $0.04/GB cost
Enable per-bucket: aws s3api put-bucket-accelerate-configuration
```

### S3 Select Performance
```
Filter 1TB CSV file for 10 matching rows:
  Without Select: download 1TB ($0.09/GB = $90)
  With Select: scan 1TB, return 10KB ($0.002/GB scanned = $2)

Speed: up to 400MB/s scan rate
Supports CSV, JSON, Parquet, and BSON
```

## EBS Performance

### gp3 Features
- 3000 IOPS baseline (included, any size volume)
- 125 MB/s throughput baseline
- Burst up to 16000 IOPS for 30 minutes
- Attach up to 28 volumes per Nitro instance
- Max throughput: 1000 MB/s (at 16000 IOPS, 256KB I/O size)

### io2 Block Express Performance
- 99.999% durability
- Up to 256K IOPS per volume
- Up to 4000 MB/s throughput
- <1ms latency
- For demanding Java DB workloads (Oracle, PostgreSQL, MySQL)

```java
// EBS-optimized configuration for Java DB
// Use io2 for production databases
CreateVolumeRequest request = CreateVolumeRequest.builder()
    .volumeType(VolumeType.IO2)
    .size(1000).iops(64000)
    .availabilityZone("us-east-1a")
    .build();
```

## EFS Performance

### Bursting Credits
```
Baseline: 50 MiB/s per TiB of storage
Burst: up to 100 MiB/s (for small FS)
Credit accumulation: unused throughput converts to credits
Burst duration depends on credit balance

Example: 100GB EFS
  Baseline: 5 MiB/s
  Burst: up to 100 MiB/s for ~30 minutes
  Recovery: 2 hours at idle to replenish credits
```

### Provisioned Throughput
```
Set explicit throughput regardless of storage size
1 MiB/s = $6/month
Recommended for: production Java workloads with consistent IO
```
