# Why AWS Storage Matters

## Business Impact
- **Durability**: S3 99.999999999% — lose one object per 10 trillion stored
- **Scalability**: EBS scales to 64 TB per volume; S3 scales to any size
- **Cost tiers**: Hot (Standard), Warm (IA), Cold (Glacier), Frozen (Deep Archive) — pay only for access
- **Backup**: Automated lifecycle policies reduce manual backup overhead

## Technical Impact
- **Block**: EBS io2 at 256K IOPS for database performance
- **File**: EFS scales throughput as files grow (bursting or provisioned)
- **Object**: S3 event notifications trigger Lambda for processing pipelines
- **Hybrid**: Storage Gateway bridges on-premise file servers to S3

## For Java Developers
- S3 TransferManager for async multipart uploads
- EBS snapshots for database backup automation
- EFS for shared file locks across clustered Java apps
- S3 Select to filter CSV/JSON without downloading entire objects
