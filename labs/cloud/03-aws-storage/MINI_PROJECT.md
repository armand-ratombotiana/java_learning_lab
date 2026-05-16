# AWS Storage - Mini Project

## Project: Data Backup and Archive System

### Objective
Create automated backup system using S3 lifecycle policies and EBS snapshots.

### Architecture
```
EC2 → EBS Volumes → Daily Snapshots → S3 Glacier → S3 Deep Archive
```

### Steps

**Step 1: EBS Volume Setup**
1. Create EBS volume (50GB)
2. Attach to EC2
3. Create test data

**Step 2: CloudWatch Events**
1. Create Lambda function
2. Trigger daily at midnight
3. Create EBS snapshot
4. Tag snapshot with date

**Step 3: S3 Lifecycle Policy**
1. Create backup bucket
2. Configure lifecycle rules:
   - Standard-IA after 30 days
   - Glacier after 90 days
   - Deep Archive after 1 year
3. Enable versioning

**Step 4: Automation Script**
```python
import boto3
import datetime

def create_daily_snapshot():
    ec2 = boto3.client('ec2')
    
    # Create snapshot
    response = ec2.create_snapshot(
        VolumeId='vol-12345678',
        Description=f"Backup {datetime.date.today()}"
    )
    
    # Tag snapshot
    ec2.create_tags(
        Resources=[response['SnapshotId']],
        Tags=[
            {'Key': 'Backup', 'Value': 'Daily'},
            {'Key': 'Date', 'Value': str(datetime.date.today())}
        ]
    )
    
    return response['SnapshotId']

# Add to S3
def archive_to_s3():
    s3 = boto3.client('s3')
    s3.upload_file('backup.tar', 'backup-bucket', 'daily/backup.tar')
```

### Deliverables
1. Automated snapshot creation
2. Lifecycle policy configured
3. Documentation

### Estimated Time
60 minutes