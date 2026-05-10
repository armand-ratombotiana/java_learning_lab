# AWS Pedagogic Guide

## Teaching Strategy

### Phase 1: AWS Fundamentals (Hours 1-2)
**Goal**: Understand AWS basics

**Topics**:
- AWS global infrastructure
- Core services overview
- IAM and security
- AWS CLI basics

**Activities**:
1. Configure AWS CLI
2. Create IAM user
3. Explore console

**Exercises**:
- Exercise 1, 6

**Assessment**: CLI configured, access working

---

### Phase 2: Compute Services (Hours 3-4)
**Goal**: Master compute options

**Topics**:
- EC2 instances
- Lambda functions
- ECS containers
- Auto Scaling

**Activities**:
1. Launch EC2 instance
2. Create Lambda function
3. Configure ASG

**Exercises**:
- Exercise 3, 4

**Assessment**: Application runs

---

### Phase 3: Storage & Databases (Hours 5-6)
**Goal**: Understand data services

**Topics**:
- S3 object storage
- RDS databases
- DynamoDB
- ElastiCache

**Activities**:
1. Create S3 bucket
2. Set up RDS instance
3. Configure backup

**Exercises**:
- Exercise 1, 5, 9

**Assessment**: Data operations work

---

### Phase 4: Networking (Hours 7-8)
**Goal**: Master AWS networking

**Topics**:
- VPC setup
- Security groups
- Route tables
- Load balancers

**Activities**:
1. Create VPC
2. Configure security groups
3. Set up ALB

**Exercises**:
- Exercise 8

**Assessment**: Network configured

---

### Phase 5: Infrastructure as Code (Hours 9-10)
**Goal**: Use CDK and CloudFormation

**Topics**:
- CloudFormation basics
- CDK for Java
- Deployment automation

**Activities**:
1. Create CDK stack
2. Deploy infrastructure
3. Manage updates

**Exercises**:
- Exercise 8, 10

**Assessment**: Infrastructure deployed

---

## Teaching Techniques

### Code Review Questions
1. Which AWS service is best for this use case?
2. How would you secure this resource?
3. What monitoring is needed?
4. How would you optimize costs?

### Common Mistakes
| Mistake | Solution |
|---------|----------|
| Using access keys | Use IAM roles |
| Not enabling encryption | Enable at rest and in transit |
| No backup strategy | Configure automated backups |
| Open security groups | Use least privilege |
| No monitoring | Set up CloudWatch |

### Real-World Examples

**Example 1: Web Application**
- ALB + ECS Fargate
- RDS PostgreSQL
- ElastiCache Redis
- S3 for static assets
- CloudFront CDN

**Example 2: Serverless API**
- API Gateway + Lambda
- DynamoDB for storage
- X-Ray for tracing
- CloudWatch for logging

**Example 3: Batch Processing**
- Lambda for triggers
- ECS for processing
- S3 for input/output
- SNS for notifications

---

## Hands-On Projects

### Project 1: Web Application (6 hours)
Deploy web application:
- ECS Fargate cluster
- Application Load Balancer
- RDS PostgreSQL
- Auto Scaling

**Requirements**:
- Application accessible
- Database connected
- Auto scaling works

---

### Project 2: Serverless API (5 hours)
Create serverless API:
- API Gateway
- Lambda function
- DynamoDB table
- CloudWatch monitoring

**Requirements**:
- API responds correctly
- Data persists
- Monitoring works

---

### Project 3: Infrastructure as Code (6 hours)
Build complete infrastructure:
- VPC with public/private subnets
- ECS cluster with service
- RDS database
- Monitoring and alerting

**Requirements**:
- All resources deployed
- Can be destroyed and recreated
- Proper security configured

---

## Evaluation Criteria

### Service Selection (30%)
- Appropriate service choice
- Cost-effective solution
- Scalability considerations

### Implementation (40%)
- Proper configuration
- Security settings
- Monitoring enabled

### Best Practices (30%)
- IAM roles not users
- Encryption enabled
- Backup configured

---

## Resources

### Official Docs
- [AWS Documentation](https://docs.aws.amazon.com/)
- [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/)
- [CDK Documentation](https://docs.aws.amazon.com/cdk/)

### Tools
- AWS CLI
- AWS CDK
- SAM CLI
- LocalStack