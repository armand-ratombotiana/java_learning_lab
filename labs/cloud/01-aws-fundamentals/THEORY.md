# AWS Fundamentals - Theory

## Overview
Amazon Web Services (AWS) is the world's most comprehensive and widely adopted cloud platform, offering over 200 fully featured services from data centers globally.

## Core Concepts

### 1. EC2 (Elastic Compute Cloud)
- **What is EC2**: Virtual servers in the cloud
- **Instance Types**: General purpose, Compute optimized, Memory optimized, GPU optimized, Storage optimized
- **Pricing Models**: On-Demand, Reserved Instances, Savings Plans, Spot Instances
- **Key Features**: 
  - Auto Scaling
  - Load Balancing
  - Placement Groups

### 2. S3 (Simple Storage Service)
- **What is S3**: Object storage service
- **Storage Classes**: 
  - Standard
  - Intelligent-Tiering
  - Standard-IA (Infrequent Access)
  - Glacier (Archive)
- **Key Features**:
  - Versioning
  - Lifecycle policies
  - Cross-region replication
  - Encryption options

### 3. IAM (Identity and Access Management)
- **What is IAM**: Fine-grained access control
- **Components**:
  - Users: Individual accounts
  - Groups: Collection of users
  - Roles: Temporary permissions
  - Policies: JSON permission documents
- **Best Practices**:
  - Principle of least privilege
  - Enable MFA
  - Use IAM roles instead of access keys

### 4. VPC (Virtual Private Cloud)
- **What is VPC**: Isolated cloud network
- **Components**:
  - Subnets: Public and Private
  - Route Tables: Network traffic routing
  - Security Groups: Instance-level firewall
  - NACLs: Subnet-level firewall
  - Internet Gateway: VPC internet access
  - NAT Gateway: Private subnet internet access

## AWS Global Infrastructure
- **Regions**: Geographic areas (e.g., us-east-1, eu-west-1)
- **Availability Zones**: Isolated data centers within a region
- **Edge Locations**: CloudFront CDN endpoints

## AWS Shared Responsibility Model
- **AWS Responsibility**: Security OF the cloud (infrastructure)
- **Customer Responsibility**: Security IN the cloud (data, access, applications)

## Key Takeaways
1. AWS provides scalable, cost-effective cloud solutions
2. EC2 provides compute capacity; S3 provides scalable storage
3. IAM controls who can access what resources
4. VPC provides network isolation and security
5. Always follow security best practices