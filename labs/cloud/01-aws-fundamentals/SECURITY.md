# Security — AWS Fundamentals

## IAM Security Best Practices

### Password Policy
```json
{
  "MinimumPasswordLength": 16,
  "RequireUppercase": true,
  "RequireLowercase": true,
  "RequireNumbers": true,
  "RequireSymbols": true,
  "MaxPasswordAge": 90,
  "PasswordReusePrevention": 24
}
```

### Least Privilege Pattern
```json
{
  "Effect": "Allow",
  "Action": [
    "s3:GetObject",
    "s3:ListBucket"
  ],
  "Resource": [
    "arn:aws:s3:::my-app-config",
    "arn:aws:s3:::my-app-config/*"
  ],
  "Condition": {
    "IpAddress": {"aws:SourceIp": "10.0.0.0/16"}
  }
}
```

### Audit Checklist
- [ ] Root user has MFA enabled
- [ ] No IAM users have access keys >90 days old
- [ ] All human users sign in with SSO + MFA
- [ ] No inline policies (use managed policies)
- [ ] Access Analyzer enabled for S3, IAM, Lambda

## S3 Security

### Encryption Options
```
SSE-S3: AES-256 managed by AWS (default)
  └── aws s3 cp file s3://bucket/ --sse AES256

SSE-KMS: Customer-managed KMS keys
  └── aws s3 cp file s3://bucket/ --sse aws:kms --sse-kms-key-id alias/my-key

SSE-C: Customer-provided keys (server-side, AWS doesn't store key)
  └── aws s3 cp file s3://bucket/ --sse-c AES256 --sse-c-key fileb://key.bin

Client-side: Encrypt before uploading
  └── Java crypto library → upload ciphertext
```

### Block Public Access
```powershell
aws s3control put-public-access-block --account-id 123456789012 `
    --public-access-block-configuration BlockPublicAcls=true,IgnorePublicAcls=true,`
    BlockPublicPolicy=true,RestrictPublicBuckets=true
```

## EC2 Security

### Security Group Rules
| Direction | Type | Port | Source | Purpose |
|-----------|------|------|--------|---------|
| Inbound | HTTP | 80 | 0.0.0.0/0 | Web traffic |
| Inbound | HTTPS | 443 | 0.0.0.0/0 | SSL traffic |
| Inbound | SSH | 22 | 10.0.0.0/8 | Internal SSH only |
| Outbound | All | All | 0.0.0.0/0 | Default outbound |

### Instance Hardening
- Use Amazon Linux 2023 (minimal attack surface, automatic patches)
- Enable IMDSv2 (session token required for metadata access)
- `aws ec2 modify-instance-metadata-options --instance-id i-xxx --http-tokens required`

## VPC Security

### Flow Logs
```powershell
aws ec2 create-flow-logs --resource-type VPC --resource-id vpc-xxx `
    --traffic-type ALL --log-group-name cloud-vpc-flow-logs `
    --deliver-logs-permission-arn arn:aws:iam::xxx:role/FlowLogsRole
```

### Network ACL Rules
```
Inbound:
  Rule 100: HTTP (80)  from 0.0.0.0/0  ALLOW
  Rule 200: SSH (22)   from 10.0.0.0/8 ALLOW
  Rule 300: Ephemeral  from 0.0.0.0/0  ALLOW
  Rule *:   All traffic                 DENY

Outbound:
  Rule 100: HTTP (80)  to 0.0.0.0/0   ALLOW
  Rule 200: HTTPS (443) to 0.0.0.0/0  ALLOW
  Rule 300: Ephemeral  to 0.0.0.0/0   ALLOW
  Rule *:   All traffic                DENY
```
