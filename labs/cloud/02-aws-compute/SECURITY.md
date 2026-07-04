# Security — AWS Compute

## Lambda Security

### Execution Role Best Practices
```json
{
  "Effect": "Allow",
  "Action": [
    "dynamodb:GetItem",
    "dynamodb:PutItem",
    "dynamodb:UpdateItem",
    "dynamodb:DeleteItem"
  ],
  "Resource": "arn:aws:dynamodb:us-east-1:xxx:table/orders"
}
```
- **Least privilege**: Only actions needed, only specific resources
- **One role per function**: Different functions, different permissions
- **Avoid wildcards**: Use specific table ARNs, not `*`

### Environment Variables
```powershell
# Encrypt environment variables with KMS
aws lambda update-function-configuration --function-name my-function `
    --kms-key-arn arn:aws:kms:us-east-1:xxx:key/xxx
```

### VPC Security
- Lambda in VPC → ENI created in your VPC
- All traffic goes through VPC (no internet unless NAT)
- Security group controls Lambda → RDS, Lambda → EC2 traffic
- Use VPC endpoints for SQS, S3, DynamoDB (no NAT needed)

## ECS Security

### Task IAM Role vs Task Execution Role
```
Task Execution Role (used by ECS agent):
  - Pull images from ECR
  - Write logs to CloudWatch
  - Get secrets from Secrets Manager

Task Role (used by application code):
  - Access DynamoDB, S3, SQS
  - Specific to application needs
```

### Secrets Injection
```json
{
  "secrets": [
    {
      "name": "DB_PASSWORD",
      "valueFrom": "arn:aws:secretsmanager:us-east-1:xxx:secret:db-pass"
    }
  ]
}
```

## EC2 Security

### Instance Metadata Service v2
```powershell
# Require IMDSv2 (session token required)
aws ec2 modify-instance-metadata-options --instance-id i-xxx `
    --http-tokens required --http-endpoint enabled
```

### SSH Key Rotation
```powershell
# Add new public key
aws ec2 import-key-pair --key-name new-key `
    --public-key-material fileb://new-key.pub

# Remove old key from authorized_keys
aws ec2-instance-connect send-ssh-public-key --instance-id i-xxx `
    --instance-os-user ec2-user --ssh-public-key file://new-key.pub
```

### Security Group Tightening
```
Lambda → RDS: Allow port 3306 from Lambda SG only
ECS → RDS: Allow port 3306 from ECS tasks SG only
ALB → ECS: Allow port 8080 from ALB SG only
Admin → EC2: Allow port 22 from corporate IP only
```
