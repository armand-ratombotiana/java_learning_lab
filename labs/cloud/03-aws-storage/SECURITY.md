# Security — AWS Storage

## S3 Security

### Encryption at Rest
```java
// SSE-S3 (AES-256, AWS-managed keys)
PutObjectRequest req = PutObjectRequest.builder()
    .bucket("secure-bucket").key("doc.pdf")
    .sseAlgorithm(ServerSideEncryption.AES256)
    .build();

// SSE-KMS (customer-managed KMS keys)
PutObjectRequest req = PutObjectRequest.builder()
    .bucket("secure-bucket").key("doc.pdf")
    .sseAlgorithm(ServerSideEncryption.AWS_KMS)
    .ssekmsKeyId("arn:aws:kms:us-east-1:xxx:key/yyy")
    .build();
```

### Bucket Policy with Conditions
```json
{
  "Effect": "Allow",
  "Principal": {"AWS": "arn:aws:iam::xxx:role/app-role"},
  "Action": "s3:GetObject",
  "Resource": "arn:aws:s3:::app-data/*",
  "Condition": {
    "StringEquals": {"s3:x-amz-server-side-encryption": "AES256"},
    "Bool": {"aws:SecureTransport": "true"}
  }
}
```

### S3 Access Points
```powershell
# Create access point with restricted network
aws s3control create-access-point --account-id 123456789012 `
    --name prod-app --bucket app-data `
    --public-access-block-configuration BlockPublicAcls=true,IgnorePublicAcls=true,`
    BlockPublicPolicy=true,RestrictPublicBuckets=true `
    --vpc-configuration VpcId=vpc-xxx

# Access via access point ARN: arn:aws:s3:us-east-1:xxx:accesspoint/prod-app
```

## EBS Security

### Encryption by Default
```powershell
# Enable EBS encryption by default in region
aws ec2 enable-ebs-encryption-by-default --region us-east-1
```
All new EBS volumes are automatically encrypted with KMS (no application change needed).

### Snapshot Encryption
```powershell
# Copy snapshot to new KMS key
aws ec2 copy-snapshot --source-region us-east-1 `
    --source-snapshot-id snap-xxx `
    --encrypted --kms-key-id alias/backup-key `
    --destination-region us-west-2
```

## EFS Security

### Encryption at Rest
```powershell
# Create encrypted EFS file system
aws efs create-file-system --encrypted --kms-key-id alias/efs-key `
    --performance-mode generalPurpose --creation-token prod-efs
```

### EFS Access Points
```json
{
  "RootDirectory": {"Path": "/data/app", "CreationInfo": {
    "OwnerUid": 1001, "OwnerGid": 1001, "Permissions": "0755"
  }},
  "PosixUser": {"Uid": 1001, "Gid": 1001}
}
```
Each application gets isolated directory with correct permissions.
