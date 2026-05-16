# AWS Storage - Theory

## Overview
AWS provides comprehensive storage solutions for different use cases - object, block, and file storage.

## 1. Amazon S3 (Object Storage)

### What is S3?
- Object storage service
- Unlimited storage capacity
- 99.999999999% durability

### Storage Classes
- **Standard**: Frequent access, low latency
- **Intelligent-Tiering**: Auto-optimization based on access
- **Standard-IA**: Infrequent access, lower cost
- **Glacier**: Long-term archive, retrieval time 1-5 minutes to hours
- **Glacier Deep Archive**: 7-10 year retention, 12+ hour retrieval
- **One Zone-IA**: Single AZ, lower cost

### Key Features
- Versioning: Keep multiple versions
- Lifecycle Policies: Auto-transition between classes
- Cross-Region Replication: Automatic copying
- Server-Side Encryption: AES-256 or KMS
- Pre-signed URLs: Temporary access
- Static Website Hosting: HTML/CSS/JS

## 2. Amazon EBS (Block Storage)

### What is EBS?
- Block-level storage for EC2
- Persistent storage
- Single AZ

### Volume Types
- **gp3**: General purpose SSD, 3000 IOPS baseline
- **gp2**: General purpose SSD, up to 16000 IOPS
- **io2 Block Express**: High performance, 256000 IOPS
- **io1**: Provisioned IOPS, 64000 IOPS max
- **st1**: Throughput optimized HDD, 500 IOPS
- **sc1**: Cold HDD, lowest cost

### Use Cases
- Operating system boot volumes
- Databases
- Enterprise applications
- Big data analytics

### Features
- Snapshots: Point-in-time backups
- Encryption: AES-256, KMS
- Multi-attach: io2 volumes to multiple instances
- Elastic volumes: Resize without downtime

## 3. Amazon EFS (File Storage)

### What is EFS?
- Managed NFS (Network File System)
- Multi-AZ file storage
- Elastic capacity

### Performance Modes
- **General Purpose**: Web servers, CMS
- **Max I/O**: Big data, media processing

### Throughput Modes
- **Bursting**: Scales with file system size
- **Provisioned**: Fixed throughput

### Use Cases
- Web serving
- Content management
- Development tools
- Container storage
- Analytics workloads

### Features
- Lifecycle Management: Move to Infrequent Access
- Encryption: In transit and at rest
- Access Points: Simplified access control
- Performance: Low latency, high throughput

## Comparison

| Feature | S3 | EBS | EFS |
|---------|-----|-----|-----|
| Type | Object | Block | File |
| Access | HTTP/HTTPS | Block device | NFS |
| Multi-AZ | Yes (replication) | No | Yes |
| Scalability | Automatic | Manual resize | Automatic |