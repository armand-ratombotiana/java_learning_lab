# AWS Storage - Exercises

## Exercise 1: S3 Bucket Creation
1. Create S3 bucket via Console
2. Upload a text file
3. Make object public
4. Access via browser

## Exercise 2: S3 Versioning
1. Enable versioning on bucket
2. Upload file, modify, upload again
3. View version history
4. Restore previous version

## Exercise 3: S3 Lifecycle Policy
1. Create lifecycle rule
2. Transition to Standard-IA after 30 days
3. Transition to Glacier after 90 days
4. Verify rule is active

## Exercise 4: S3 Cross-Region Replication
1. Create source and destination buckets
2. Enable versioning on both
3. Configure replication rule
4. Test by uploading to source

## Exercise 5: S3 Pre-signed URL
1. Generate pre-signed URL for private object
2. Set 15-minute expiration
3. Test URL access
4. Verify expiration works

## Exercise 6: EBS Volume Creation
1. Create 50GB gp3 volume
2. Attach to EC2 instance
3. Format and mount
4. Write test data

## Exercise 7: EBS Snapshot
1. Create snapshot of EBS volume
2. Create new volume from snapshot
3. Verify data integrity
4. Delete snapshot

## Exercise 8: EBS Encryption
1. Create encrypted volume
2. Attach to instance
3. Verify encryption
4. Copy unencrypted to encrypted

## Exercise 9: EFS Creation
1. Create EFS file system
2. Mount on EC2 instances
3. Create files
4. Verify multi-AZ access

## Exercise 10: EFS Performance Modes
1. Create EFS with General Purpose
2. Create with Max I/O
3. Compare performance
4. Test throughput limits

## Exercise 11: S3 Server-Side Encryption
1. Create bucket with SSE-S3
2. Create bucket with SSE-KMS
3. Upload encrypted objects
4. Verify encryption

## Exercise 12: EBS Elastic Volumes
1. Create 20GB volume
2. Resize to 50GB online
3. Expand file system
4. Verify new size

## Exercise 13: S3 Inventory
1. Configure S3 Inventory
2. Set daily report
3. Query generated report
4. Analyze inventory

## Exercise 14: S3 Select
1. Upload CSV/JSON to S3
2. Use S3 Select to query
3. Compare with downloading full file
4. Analyze cost savings

## Exercise 15: EFS Lifecycle Management
1. Enable lifecycle policy
2. Move to Infrequent Access
3. Verify automatic transition
4. Monitor storage costs

## Exercise 16: EBS Multi-Attach
1. Create io2 volume
2. Enable multi-attach
3. Attach to two instances
4. Test concurrent access

## Exercise 17: S3 Bucket Policy
1. Write policy for IP restriction
2. Require SSL (HTTPS)
3. Deny delete operations
4. Test policies

## Exercise 18: EFS Access Points
1. Create access point
2. Configure POSIX permissions
3. Mount using access point
4. Verify access control

## Exercise 19: S3 Batch Operations
1. Create batch job manifest
2. Copy objects between buckets
3. Replace tags on objects
4. Monitor job completion

## Exercise 20: S3 Glacier Vault
1. Upload objects to Glacier
2. Initiate retrieval job
3. Download after retrieval
4. Monitor vault inventory

## Exercise 21: EBS RAID Configuration
1. Create RAID 0 with 2 volumes
2. Compare performance to single
3. Test fault tolerance
4. Document performance gains

## Exercise 22: S3 Analytics
1. Configure storage analytics
2. Analyze access patterns
3. Get recommendations
4. Optimize storage class

## Exercise 23: EFS Replication
1. Create EFS file system
2. Configure backup to S3
3. Restore from backup
4. Test disaster recovery

## Exercise 24: S3 Object Lambda
1. Create S3 Object Lambda
2. Transform data on access
3. Test transformation
4. Monitor usage

## Exercise 25: Storage Gateway
1. Deploy Storage Gateway
2. Configure file gateway
3. Access on-premises
4. Sync with S3