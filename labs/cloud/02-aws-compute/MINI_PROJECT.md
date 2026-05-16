# AWS Compute - Mini Project

## Project: Serverless Image Processing Pipeline

### Objective
Build an image processing pipeline using Lambda and S3.

### Architecture
```
S3 Upload → Lambda Trigger → Process Image → Save to S3 → SNS Notification
```

### Steps

**Step 1: Create S3 Buckets**
- Source bucket: `images-source-YOURNAME`
- Processed bucket: `images-processed-YOURNAME`

**Step 2: Create Lambda Function**
```python
import boto3
import json

s3 = boto3.client('s3')

def lambda_handler(event, context):
    # Get bucket and key from event
    bucket = event['Records'][0]['s3']['bucket']['name']
    key = event['Records'][0]['s3']['object']['key']
    
    # Get the image
    response = s3.get_object(Bucket=bucket, Key=key)
    
    # In production: Use PIL to resize/compress
    print(f"Processing {key} from {bucket}")
    
    # Copy to processed bucket
    copy_source = {'Bucket': bucket, 'Key': key}
    s3.copy_object(CopySource=copy_source, 
                   Bucket='images-processed-YOURNAME',
                   Key=f'processed-{key}')
    
    return {'statusCode': 200}
```

**Step 3: Configure S3 Trigger**
- Event type: s3:ObjectCreated:*
- Enable trigger

**Step 4: Add SNS Notification**
- Create SNS topic
- Publish message on completion

### Deliverables
1. Working pipeline
2. Test images processed
3. Architecture diagram

### Estimated Time
60-90 minutes