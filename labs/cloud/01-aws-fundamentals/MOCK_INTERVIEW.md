# Mock Interview — AWS Fundamentals

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Behavioral
- **Difficulty**: Associate Level

## Warm-Up (5 min)

Q1: What is the AWS Shared Responsibility Model? Give an example of what the customer is responsible for vs what AWS is responsible for.

Q2: Explain the difference between a Region, an Availability Zone, and an Edge Location.

## Technical Questions (20 min)

### Question 1: EC2 Instance Design (10 min)
A startup needs to run a Java web application that receives variable traffic (500-5000 req/s). The application is stateless and connects to a PostgreSQL database. Design an EC2-based architecture that is cost-effective, scalable, and resilient.

**Consider**: Instance types, Auto Scaling, Load Balancer, purchasing options (On-Demand, Reserved, Spot), placement groups.

### Question 2: S3 + IAM (10 min)
Your application needs to generate presigned URLs for users to upload profile photos to S3. Users should only be able to upload to their own folder. Write an IAM policy that enforces this restriction. Explain how presigned URLs work.

## Behavioral Question (10 min)

**Question**: Tell me about a time you had to choose between multiple cloud providers or services for a project. What factors influenced your decision and what was the outcome?

Use STAR/LP format: Situation, Task, Action, Result.

## System Design Whiteboard (10 min)

**Problem**: Design a cost-effective architecture for hosting a static website with a contact form that sends emails. The site expects 100K monthly visitors. Use only AWS services.

**Expected elements**: S3, CloudFront, Route 53, Lambda + SES for the contact form.

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| EC2 Knowledge | Knows instance families, purchasing options, placement groups | Knows basic types and ASG | Can't differentiate instance types |
| IAM Depth | Writes policy from scratch, knows conditions | Understands IAM concepts | Basic understanding only |
| Architecture | Multi-AZ, cost optimization, security | Single AZ, basic setup | No HA consideration |
| Communication | Clear, structured, trade-off aware | Mostly clear | Rambling or vague |

## Sample Solution Outline

### EC2 Architecture
- ALB in front of Auto Scaling Group (min 2, max 10 across 2 AZs)
- t3.medium (burstable) or c6g.large (compute-optimized) — Graviton for cost savings
- Use Mixed Instances Policy (60% Reserved, 40% Spot) if steady-state with some variable
- Security Groups: ALB allows 443 from anywhere; EC2 allows traffic from ALB SG only
- RDS PostgreSQL Multi-AZ in private subnet

### S3 Presigned URL IAM
```json
{
    "Version": "2012-10-17",
    "Statement": [{
        "Effect": "Allow",
        "Action": "s3:PutObject",
        "Resource": "arn:aws:s3:::mybucket/\${cognito-identity.amazonaws.com:sub}/*",
        "Condition": {
            "StringLike": {"s3:x-amz-server-side-encryption": "AES256"}
        }
    }]
}
```
