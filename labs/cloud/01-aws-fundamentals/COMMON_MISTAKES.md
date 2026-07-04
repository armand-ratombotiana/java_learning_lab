# Common Mistakes — AWS Fundamentals

## EC2 Mistakes

### 1. Leaving Instances Running
**Mistake**: Launching t3.large for testing, forgetting to terminate.
**Cost**: ~$30/month for one forgotten instance.
**Fix**: Set up billing alerts + instance scheduler to auto-stop at night.

### 2. Using Default VPC for Production
**Mistake**: Deploying production workloads in the "default VPC."
**Issue**: No network isolation, no private subnets, no NAT.
**Fix**: Always create a custom VPC with proper subnet topology.

### 3. Opening Ports to 0.0.0.0/0
**Mistake**: SSH (port 22) open to all IPs.
**Risk**: Brute-force attacks, crypto-mining malware.
**Fix**: Restrict SSH to specific IP ranges or use SSM Session Manager.

### 4. Terminating Instead of Stopping
**Mistake**: Running `terminate-instances` when meaning `stop-instances`.
**Consequence**: EBS root volume deleted, data lost.
**Fix**: Set "Delete on Termination" = false for root volume; double-check command.

## S3 Mistakes

### 1. Public Bucket with Sensitive Data
**Mistake**: Bucket policy allows `s3:GetObject` for `Principal: "*"`.
**Risk**: Anyone on the internet can download objects.
**Fix**: Use S3 Block Public Access; audit with IAM Access Analyzer.

### 2. No Versioning
**Mistake**: Creating buckets without enabling versioning.
**Risk**: Accidental deletes or overwrites are permanent.
**Fix**: Enable versioning on all buckets; lifecycle rules to clean up old versions.

### 3. Cross-Region Replication Without Proper IAM
**Mistake**: Setting up CRR but source bucket can't write to destination bucket.
**Symptom**: Objects never replicate, no error message.
**Fix**: Verify IAM role has `s3:ReplicateObject` and `s3:ReplicateDelete`.

## IAM Mistakes

### 1. Using Root User for Daily Tasks
**Mistake**: Logging in as root user for routine operations.
**Risk**: No MFA enforcement, no access keys rotation, no audit trail.
**Fix**: Create admin IAM user, enable MFA, don't use root except for account settings.

### 2. Overly Permissive Policies
**Mistake**: Using `"Action": "*"` and `"Resource": "*"`.
**Risk**: Any compromised credential has full account access.
**Fix**: Follow least privilege — specify exact actions and resources.

### 3. Hardcoding Credentials in Code
**Mistake**: Writing `accessKey` and `secretKey` in application.properties.
**Risk**: Credentials committed to Git, leaked to CI logs.
**Fix**: Use IAM roles (EC2 instance profile), environment variables, or Secrets Manager.

## VPC Mistakes

### 1. Mismatched CIDR for Peering
**Mistake**: Two VPCs with overlapping CIDR ranges.
**Issue**: VPC peering fails; can't connect.
**Fix**: Plan CIDR allocation carefully — different ranges per VPC/environment.

### 2. Forgetting NAT Gateway for Private Subnets
**Mistake**: Private subnet instances cannot reach internet (for updates, Docker pull).
**Fix**: Add NAT Gateway in public subnet, update private route table.
**Cost**: ~$32/month per NAT Gateway — use NAT instance for dev.

### 3. Security Group Limits
**Mistake**: Adding 60+ rules to a single security group.
**Issue**: AWS limit is 60 inbound + 60 outbound rules.
**Fix**: Use multiple security groups; leverage NACLs for broad rules.
