# History of AWS Storage

## Timeline

| Year | Milestone |
|------|-----------|
| 2006 | **S3** launches (first AWS service besides SQS) — REST-based object storage |
| 2008 | **EBS** launches — persistent block storage for EC2 |
| 2009 | S3 adds versioning, bucket policies |
| 2010 | EBS adds Provisioned IOPS; S3 adds multipart upload |
| 2012 | **Glacier** launches (archive storage, $0.004/GB/month) |
| 2014 | **EFS** preview — NFSv4.1 shared file system |
| 2015 | S3 Standard-IA; EBS encryption at rest |
| 2016 | EFS GA; S3 Transfer Acceleration |
| 2017 | S3 Select; Glacier Select; EBS Elastic Volumes (modify without detach) |
| 2018 | S3 Intelligent-Tiering; EBS gp2 → gp3 (baseline IOPS independent of size) |
| 2019 | S3 Access Points; EFS Infrequent Access |
| 2020 | S3 Object Lambda; EBS io2 Block Express (256K IOPS) |
| 2021 | S3 Glacier Instant Retrieval (millisecond access for archive) |
| 2022 | S3 Express One Zone (10x faster than S3 Standard) |
| 2023 | EBS gp3 3000 IOPS baseline (no gp2 migration needed) |
| 2024 | S3 Tables (Apache Iceberg format) for analytics |

## Key Insight
S3 was the second AWS service ever launched (2006) after SQS. The durability promise of "eleven 9s" remains unchanged — the architecture was that well-designed from day one.
