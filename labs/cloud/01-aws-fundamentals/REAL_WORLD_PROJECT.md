# AWS Fundamentals - Real World Project

## Project: Multi-Tier Web Application Architecture

### Objective
Design and deploy a production-ready web application infrastructure with multiple tiers, proper security, and high availability.

### Architecture Overview
```
Internet → CloudFront → ALB → EC2 Auto Scaling Group → RDS (Multi-AZ)
           → S3 (Static Assets) → ElastiCache
```

### Requirements

**Tier 1: Network Layer**
1. Create VPC with CIDR 10.0.0.0/16
2. Create 2 public subnets in different AZs
3. Create 2 private subnets in different AZs
4. Configure NAT Gateway in public subnet
5. Set up Internet Gateway
6. Configure route tables

**Tier 2: Compute Layer**
1. Create IAM role for EC2 with S3 read access
2. Create launch template for web servers
3. Create Auto Scaling Group (min: 2, max: 4)
4. Configure Application Load Balancer
5. Set up target groups with health checks

**Tier 3: Database Layer**
1. Create RDS subnet group
2. Launch MySQL RDS instance (Multi-AZ)
3. Configure database credentials in Secrets Manager
4. Set up read replica in second AZ

**Tier 4: Storage Layer**
1. Create S3 bucket for application assets
2. Enable versioning
3. Configure lifecycle policies
4. Set up cross-region replication (optional)

**Tier 5: Security Layer**
1. Create security groups:
   - ALB Security Group (HTTP/HTTPS from internet)
   - EC2 Security Group (HTTP/HTTPS from ALB)
   - RDS Security Group (MySQL from EC2)
2. Implement IAM roles with least privilege
3. Enable VPC Flow Logs
4. Set up CloudTrail

**Tier 6: Monitoring**
1. Create CloudWatch dashboard
2. Set up alarms for:
   - CPU utilization > 80%
   - CPU utilization < 20%
   - ALB target response time > 2s
   - RDS connections > 80%
3. Configure notification via SNS

### Implementation Steps

**Week 1: Network Setup**
- Create VPC and subnets
- Configure routing
- Set up NAT Gateway

**Week 2: Security Configuration**
- Create security groups
- Set up IAM roles and policies
- Configure VPC Flow Logs

**Week 3: Compute Layer**
- Create launch template
- Set up Auto Scaling Group
- Configure ALB

**Week 4: Database and Storage**
- Launch RDS instance
- Configure S3 buckets
- Set up lifecycle policies

**Week 5: Monitoring and Alerts**
- Configure CloudWatch
- Create dashboards
- Set up alarms

**Week 6: Documentation and Testing**
- Document architecture
- Test failover scenarios
- Document runbooks

### Tools Used
- AWS Console / AWS CLI
- Terraform (optional)
- CloudWatch
- Systems Manager Session Manager

### Deliverables
1. Complete architecture diagram
2. Terraform/CloudFormation templates
3. Runbook for common operations
4. Cost estimation
5. Security assessment

### Success Criteria
- Website accessible from internet
- Auto scaling works under load
- Failover testing passes
- All security groups properly configured
- Monitoring alerts functional

### Estimated Time
6-8 weeks (part-time)