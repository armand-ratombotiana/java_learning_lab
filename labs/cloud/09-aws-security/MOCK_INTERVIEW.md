# Mock Interview — AWS Security

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Scenario
- **Difficulty**: Professional/Specialty

## Warm-Up (5 min)

Q1: Explain the principle of least privilege. How do you implement it in AWS IAM?

Q2: What is the difference between AWS WAF, AWS Shield, and AWS GuardDuty?

## Technical Questions (20 min)

### Question 1: IAM Policy Design (10 min)
Write an IAM policy for an application that needs:
- Read/write to S3 bucket `myapp-data` under prefix `/uploads/`
- Read only to DynamoDB table `user-profiles`
- Write to SQS queue `myapp-queue`
- Encrypt all S3 objects with KMS key `arn:aws:kms:us-east-1:123456789012:key/abc-123`
- Deny access to all other services

### Question 2: Security Incident Response (10 min)
GuardDuty alerts that an EC2 instance in your account is communicating with a known malicious IP address (crypto mining pool).

**Walk through your incident response plan**: Immediate actions, investigation, containment, eradication, recovery.

## Behavioral Question (10 min)

**Question**: Tell me about a time you discovered a security vulnerability or misconfiguration in your cloud infrastructure. How did you handle it?

## System Design Whiteboard (10 min)

**Problem**: Design a security monitoring and compliance framework for a 20-account AWS organization:
- Centralized logging and monitoring
- Automated compliance checks
- Incident alerting and response
- Multi-region (us-east-1, eu-west-1)

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| IAM Policies | Conditions, boundaries, service-linked roles | Basic managed policies | Static keys approach |
| Incident Response | Structured plan with IR framework | Some awareness | No process knowledge |
| Encryption | KMS, envelope, key rotation, HSM | Knows KMS exists | No encryption strategy |
| Monitoring | GuardDuty, Security Hub, Detective | Basic CloudTrail | No monitoring |

## Sample Solution Outline

### IAM Policy
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": ["s3:GetObject", "s3:PutObject"],
            "Resource": "arn:aws:s3:::myapp-data/uploads/*",
            "Condition": {
                "StringEquals": {
                    "s3:x-amz-server-side-encryption-aws-kms-key-id": "arn:aws:kms:us-east-1:123456789012:key/abc-123"
                }
            }
        },
        {
            "Effect": "Allow",
            "Action": ["dynamodb:GetItem", "dynamodb:Query"],
            "Resource": "arn:aws:dynamodb:us-east-1:123456789012:table/user-profiles"
        },
        {
            "Effect": "Allow",
            "Action": "sqs:SendMessage",
            "Resource": "arn:aws:sqs:us-east-1:123456789012:myapp-queue"
        },
        {
            "Effect": "Allow",
            "Action": ["kms:Decrypt", "kms:GenerateDataKey"],
            "Resource": "arn:aws:kms:us-east-1:123456789012:key/abc-123"
        },
        {
            "Effect": "Deny",
            "NotAction": ["s3:*", "dynamodb:*", "sqs:*", "kms:*"],
            "Resource": "*"
        }
    ]
}
```

### Incident Response (Based on NIST/SANS IR Framework)
1. **Preparation**: Pre-configured IR runbooks, IAM IR role, isolated forensic account
2. **Detection**: GuardDuty finding verified, severity determined
3. **Analysis**: CloudTrail confirms CryptoMiners activity
4. **Containment**: (1) Apply SG to isolate instance (no outbound); (2) Create snapshot for forensics; (3) Tag instance "compromised"
5. **Eradication**: (1) Terminate instance; (2) Rotate any keys the instance had access to; (3) Verify no other instances compromised
6. **Recovery**: (1) Launch replacement from known-good AMI; (2) Apply latest patches; (3) Restore from clean backup
7. **Post-mortem**: (1) Root cause analysis; (2) Implement preventive controls (SCP to prevent crypto mining services)
