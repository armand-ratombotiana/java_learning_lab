# Common Mistakes — AWS Storage

## S3 Mistakes

### 1. No Versioning on Critical Buckets
**Risk**: Accidental delete or overwrite → permanent data loss.
**Fix**: Enable versioning on all buckets with important data.

### 2. Public ACL on Private Objects
**Risk**: Bucket ACL allows `Everyone` read access.
**Fix**: Use S3 Block Public Access; audit with IAM Access Analyzer.

### 3. Glacier Retrieve Costs for Frequent Access
**Mistake**: Storing actively-used data in Glacier (thinks "it's cheap").
**Issue**: Every retrieval costs $0.01/GB (more expensive than Standard for frequent access).
**Fix**: Use lifecycle rules: Standard → IA → Glacier as access frequency drops.

### 4. NOT using multipart for files >100MB
**Issue**: Single PUT limited to 5GB; slow for large files.
**Fix**: SDK automatically uses multipart for >8MB (configurable).

## EBS Mistakes

### 1. Root Volume Delete on Termination = True
**Risk**: Terminating instance deletes all data on root volume.
**Fix**: Set `DeleteOnTermination = false`; snapshot before termination.

### 2. No EBS Snapshots in Backup Plan
**Risk**: Volume corruption or accidental deletion → no recovery.
**Fix**: Automated snapshot schedule (e.g., every 6 hours, retain 7 days).

### 3. gp3 with Insufficient IOPS for Database
**Mistake**: Using default gp3 IOPS (3000) for production database.
**Effect**: High latency during peak load, throttling (WritePending).
**Fix**: Monitor EBS metrics; right-size IOPS or use io2.

### 4. Cross-AZ EBS Attachment
**Mistake**: Trying to attach EBS volume to instance in different AZ.
**Fix**: EBS is AZ-specific. Use snapshots to migrate across AZs.

## EFS Mistakes

### 1. Forgetting Security Group Rules
**Mistake**: EFS security group doesn't allow NFS (port 2049) from EC2 SG.
**Effect**: Mount hangs or "Connection timed out".
**Fix**: Inbound rule: NFS port 2049 from EC2 security group.

### 2. Using Bursting Mode for High Throughput
**Mistake**: 100GB EFS with burst mode — only 5 MiB/s baseline.
**Effect**: Throughput-starved applications.
**Fix**: Provisioned throughput for consistent performance; or grow filesystem.
