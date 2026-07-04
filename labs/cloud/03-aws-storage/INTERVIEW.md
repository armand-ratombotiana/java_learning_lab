# Interview Questions — AWS Storage

## Beginner

**Q1**: What's the difference between S3, EBS, and EFS?

**Q2**: Explain S3 storage classes.

**Q3**: Can you attach an EBS volume to multiple EC2 instances? Why or why not?

## Intermediate

**Q4**: How does S3 achieve 99.999999999% durability?

**Q5**: What's a presigned URL and when would you use it?

**Q6**: How do EBS snapshots work? Are they incremental?

**Q7**: What happens when an S3 object transitions from Standard to Glacier?

## Advanced

**Q8**: Design a storage strategy for a Java app that processes 10TB of data daily.

**Q9**: How does EFS scale throughput as your file system grows?

**Q10**: Your EBS volume is throttling at 3000 IOPS. What's wrong and how do you fix it?

**Q11**: Explain S3 consistency model changes from "eventual" to "strong" for new PUTs.

## Sample Answers

**A1**: S3 is object storage (API access, unlimited size, 11-9s durability). EBS is block storage (attached to single EC2, low latency). EFS is file storage (NFS, multi-EC2, auto-scaling). Think: block = hard drive, file = network drive, object = RESTful bucket.

**A2**: Standard (hot, frequent), Standard-IA (warm, infrequent), One Zone-IA (cheap, recreatable), Glacier Instant Retrieval (ms access, archival), Glacier Flexible (minutes retrieval), Glacier Deep Archive (cheapest, 12h retrieval), Intelligent-Tiering (auto-moves between tiers).

**A3**: No, EBS is single-attach (multi-attach io2 supports limited multi-attach for clustered DBs). For shared access across instances, use EFS.

**A4**: S3 stores objects on at least 3 devices across 3+ AZs. Each object has multiple checksums. Background verification processes constantly verify data integrity. Failed devices are auto-replaced from replicas.

**A5**: A presigned URL grants temporary access to an S3 object without IAM credentials. Use case: allow users to upload files to S3 through your app without giving them S3 write permissions. URL includes signature with expiration (minutes to hours).

## Key Topics for AWS Storage
- S3 storage classes and use cases
- Lifecycle policies for cost optimization
- EBS volume types (gp3, io2, st1, sc1)
- EBS snapshots and encryption
- EFS performance and throughput modes
- S3 security (block public access, encryption, bucket policies)
- VPC endpoints for S3/DynamoDB
