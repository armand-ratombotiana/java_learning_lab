# AWS Storage - Code Deep Dive

## S3 Operations

```python
import boto3

s3 = boto3.client('s3')

# Upload file
s3.upload_file('local.txt', 'bucket-name', 'key.txt')

# Download file
s3.download_file('bucket-name', 'key.txt', 'local.txt')

# Upload with metadata
s3.put_object(
    Bucket='bucket-name',
    Key='data.json',
    Body='{"name": "test"}',
    ContentType='application/json',
    Metadata={'department': 'engineering'}
)

# List objects
response = s3.list_objects_v2(Bucket='bucket-name')
for obj in response.get('Contents', []):
    print(f"Key: {obj['Key']}, Size: {obj['Size']}")

# Generate pre-signed URL
url = s3.generate_presigned_url(
    'get_object',
    Params={'Bucket': 'bucket-name', 'Key': 'file.txt'},
    ExpiresIn=3600
)

# Lifecycle configuration
s3.put_bucket_lifecycle_configuration(
    Bucket='bucket-name',
    LifecycleConfiguration={
        'Rules': [
            {
                'ID': 'Move to Glacier',
                'Status': 'Enabled',
                'Transitions': [
                    {'Days': 30, 'StorageClass': 'GLACIER'},
                    {'Days': 365, 'StorageClass': 'DEEP_ARCHIVE'}
                ],
                'Expiration': {'Days': 2555}
            }
        ]
    }
)

# Versioning
s3.put_bucket_versioning(
    Bucket='bucket-name',
    VersioningConfiguration={'Status': 'Enabled'}
)

# Cross-region replication
s3.put_bucket_replication(
    Bucket='source-bucket',
    ReplicationConfiguration={
        'Role': 'arn:aws:iam::123456789:role/replication-role',
        'Rules': [{
            'ID': 'replicate-all',
            'Status': 'Enabled',
            'Destination': {
                'Bucket': 'arn:aws:s3:::dest-bucket'
            }
        }]
    }
)
```

## EBS Operations

```python
import boto3

ec2 = boto3.client('ec2')

# Create volume
response = ec2.create_volume(
    Size=100,
    AvailabilityZone='us-east-1a',
    VolumeType='gp3',
    TagSpecifications=[{'ResourceType': 'volume', 'Tags': [{'Key': 'Name', 'Value': 'data-volume'}]}]
)

volume_id = response['VolumeId']

# Attach volume
ec2.attach_volume(
    VolumeId=volume_id,
    InstanceId='i-1234567890abcdef0',
    Device='/dev/sdf'
)

# Create snapshot
response = ec2.create_snapshot(
    VolumeId=volume_id,
    Description='Daily backup'
)

# Create volume from snapshot
ec2.create_volume(
    Size=100,
    AvailabilityZone='us-east-1a',
    VolumeType='gp3',
    SnapshotId='snap-12345678'
)

# Modify volume
ec2.modify_volume(
    VolumeId=volume_id,
    VolumeType='io2',
    Size=200
)

# Create encrypted volume
ec2.create_volume(
    Size=50,
    AvailabilityZone='us-east-1a',
    Encrypted=True,
    KmsKeyId='arn:aws:kms:us-east-1:123456789:key/key-id'
)
```

## EFS Operations

```python
import boto3

efs = boto3.client('efs')

# Create file system
response = efs.create_file_system(
    CreationToken='my-efs',
    PerformanceMode='generalPurpose',
    ThroughputMode='bursting',
    Tags=[{'Key': 'Name', 'Value': 'my-efs'}]
)

file_system_id = response['FileSystemId']

# Create mount target
efs.create_mount_target(
    FileSystemId=file_system_id,
    SubnetId='subnet-12345678',
    SecurityGroups=['sg-12345678']
)

# Describe file system
response = efs.describe_file_systems()
for fs in response['FileSystems']:
    print(f"ID: {fs['FileSystemId']}, Size: {fs['SizeInBytes']['Value']}")

# Create access point
efs.create_access_point(
    FileSystemId=file_system_id,
    PosixUser={'Uid': 1000, 'Gid': 1000},
    RootDirectory={'Path': '/share', 'CreationInfo': {'OwnerUid': '1000', 'OwnerGid': '1000', 'Permissions': '755'}}
)

# Enable lifecycle management
efs.put_lifecycle_configuration(
    FileSystemId=file_system_id,
    LifecyclePolicies=[{'TransitionToIA': 'AFTER_30_DAYS'}]
)
```

## Terraform Storage Example

```hcl
# S3 Bucket
resource "aws_s3_bucket" "data" {
  bucket = "my-data-bucket"
}

resource "aws_s3_bucket_versioning" "data" {
  bucket = aws_s3_bucket.data.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "data" {
  bucket = aws_s3_bucket.data.id
  rule {
    id     = "archive"
    status = "Enabled"
    transition {
      days          = 30
      storage_class = "GLACIER"
    }
  }
}

# EBS Volume
resource "aws_ebs_volume" "data" {
  availability_zone = "us-east-1a"
  size             = 100
  type             = "gp3"
  encrypted        = true
}

# EFS File System
resource "aws_efs_file_system" "data" {
  throughput_mode = "bursting"
  performance_mode = "generalPurpose"
}
```