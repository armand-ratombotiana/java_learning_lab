# Mock Interview — AWS Networking

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Architecture
- **Difficulty**: Professional Level

## Warm-Up (5 min)

Q1: What is the difference between a Security Group and a Network ACL?

Q2: Explain VPC Peering vs Transit Gateway. When would you use each?

## Technical Questions (20 min)

### Question 1: Multi-VPC Architecture (10 min)
Your company has 3 AWS accounts: Production, Staging, and Shared Services (centralized logging, monitoring, AD). Each account has its own VPC. You need to enable connectivity between them securely.

**Design the network**: How do you connect the VPCs? What about internet access for private subnets? How do you control routing?

### Question 2: CloudFront + Route 53 (10 min)
Design a global content delivery architecture for a media streaming platform. Content is stored in S3 in us-east-1. Users are global (NA, EU, Asia). S3 is the origin.

**Design**: CloudFront distribution configuration, origin access control, Route 53 routing policy, SSL/TLS, geographic restrictions, price class selection.

## Behavioral Question (10 min)

**Question**: Describe a time when a network issue caused a production outage. How did you diagnose and fix it? What monitoring did you add?

## System Design Whiteboard (10 min)

**Problem**: Design a hybrid network connecting an on-premises data center to AWS. On-prem has:
- 500 servers in 2 racks (10.0.0.0/16)
- Existing MPLS connection to corporate HQ
- Must extend Active Directory to AWS

**Design**: Direct Connect, VPN backup, VPC design, subnet CIDRs, route tables, AD integration.

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| VPC | Complex multi-VPC, subnet design, routing | Basic VPC setup | Single subnet only |
| Hybrid | Direct Connect, VPN, BGP, failover | Knows Direct Connect exists | On-prem connection unclear |
| CDN | Origin access, geo-restriction, price class | Basic CloudFront | No CDN knowledge |
| DNS | Routing policies, failover, latency-based | Basic Route 53 | Simple A record only |

## Sample Solution Outline

### Multi-VPC Connectivity
- Transit Gateway in the Network (management) account
- Attach all VPCs to Transit Gateway
- Use RAM (Resource Access Manager) to share Transit Gateway across accounts
- Centralized egress via NAT Gateway in Shared Services VPC
- On-premises connectivity via Direct Connect to Shared Services
- Route tables: isolate environments (Production can't reach Staging)
- VPC Endpoints for S3, DynamoDB to avoid NAT costs

### CloudFront + Route 53
- One CloudFront distribution with S3 origin
- Origin Access Control (OAC) — restrict S3 bucket to CloudFront only
- Route 53: Latency-based routing to CloudFront (or just use CloudFront DNS)
- SSL: Custom SSL certificate (ACM in us-east-1) or CloudFront default
- Price class: Price Class 200 (NA + Europe + Asia) to balance cost and performance
- Geographic restrictions: Block based on content licensing requirements

### Hybrid Network
- Direct Connect: 1 Gbps connection to AWS Direct Connect location
- Private VIF to VPC (Transit Gateway)
- VPN backup: Site-to-Site VPN as backup (over internet)
- BGP: Advertise on-prem routes via Direct Connect; AWS routes via BGP
- AD: Deploy AD Connector or Managed Microsoft AD in AWS
- DNS: Route 53 Resolver for hybrid DNS resolution
- Security: Restrict on-prem traffic to specific VPCs via TGW route tables
