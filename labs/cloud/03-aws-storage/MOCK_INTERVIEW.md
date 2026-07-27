# Mock Interview — AWS Storage

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Architecture
- **Difficulty**: Associate Level

## Warm-Up (5 min)

Q1: What is the difference between object storage (S3), block storage (EBS), and file storage (EFS)? When would you use each?

Q2: Explain S3 storage classes and the lifecycle policy that automatically transitions objects between them.

## Technical Questions (20 min)

### Question 1: S3 Data Lake Design (10 min)
A healthcare company wants to build a data lake on S3. Data includes:
- Patient records (must be encrypted at rest)
- Medical images (large files, 100MB-2GB each)
- Access logs (generated daily, accessed rarely after 30 days)
- Compliance requirements: retain data for 7 years

**Design the S3 architecture**: bucket structure, storage classes, lifecycle policies, encryption, access controls.

### Question 2: EBS Performance (10 min)
Your Java application running on EC2 has a database on EBS. Users report slow performance during peak hours (5s write latency vs 2ms normal). Current setup: gp2 volume, 500 GB, single EC2 instance.

**Diagnose and fix**: What metrics would you check? What changes would you make?

## Behavioral Question (10 min)

**Question**: Tell me about a time you had a data loss or corruption incident. How did you recover and what did you implement to prevent recurrence?

## System Design Whiteboard (10 min)

**Problem**: Design a backup strategy for a company running 50 EC2 instances, 5 RDS databases, and 10TB of EFS data. Requirements:
- Daily backups with 30-day retention
- Monthly snapshots with 12-month retention
- Cross-region DR backup
- Total backup cost < $500/month

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| S3 | Classes, lifecycle, encryption, policies deeply | Knows basics well | Confuses with EBS |
| EBS | gp2/gp3/io1/io2 differences, performance factors | Basic understanding | No performance awareness |
| Backup | Multi-tier, cross-region, cost optimization | Standard backup plan | Single region only |
| Encryption | SSE-S3 vs SSE-KMS vs SSE-C | Knows KMS exists | No encryption knowledge |

## Sample Solution Outline

### S3 Data Lake
- Bucket structure: `s3://data-lake-{env}/{domain}/{date}/`
- Intelligent-Tiering for active data, transition to Glacier after 30 days
- Expire after 7 years (compliance)
- SSE-KMS with Customer Managed Key (CMK) + bucket policies enforcing encryption
- Block Public Access, VPC endpoint for access, bucket policies scoped by prefix

### EBS Performance Fix
- Check CloudWatch: VolumeQueueLength, VolumeReadBytes, VolumeWriteBytes
- gp2 baseline 128 KB/s per GB = 500GB → ~64 MB/s
- Upgrade to gp3: baseline 3000 IOPS + 125 MB/s regardless of size
- Or switch to io2 Block Express for consistent sub-millisecond latency
- Consider EBS-optimized instance + Nitro for maximum throughput
- Separate data and log volumes
- Multi-volume RAID 0 for striping if single volume insufficient

### Backup Strategy
- EC2: AWS Backup with daily snapshots (30d retention), monthly (12mo)
- RDS: Automated snapshots (35d retention) + manual monthly snapshots
- EFS: AWS Backup with daily backup (30d retention)
- Cross-region: Copy snapshots to us-west-2 using AWS Backup cross-region
- S3: Versioning + Lifecycle + Cross-Region Replication for critical data
