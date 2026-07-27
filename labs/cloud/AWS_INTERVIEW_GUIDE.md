# AWS Interview Guide

## Overview
Comprehensive preparation guide for AWS technical interviews across all role types — Solutions Architect, Cloud Engineer, DevOps Engineer, Security Engineer.

## Role Types

| Role | Level | Focus |
|------|-------|-------|
| Solutions Architect (SA) | L4-L7 | Customer-facing, technical demos, architecture design, Well-Architected reviews |
| Cloud Engineer | L4-L6 | EC2, VPC, IAM, automation, operational excellence, migrations |
| DevOps Engineer | L5-L7 | CI/CD pipelines, IaC, container orchestration, monitoring |
| Security Engineer | L5-L7 | IAM, KMS, Shield, WAF, GuardDuty, Security Hub |
| Networking Engineer | L5-L7 | Direct Connect, VPN, Transit Gateway, Route 53, CloudFront |
| Data Engineer | L5-L7 | Redshift, EMR, Kinesis, Glue, Athena, QuickSight |

## Interview Process

| Stage | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, compensation, role alignment, LP intro |
| Technical Phone Screen | 45-60 min | Architecture design, service selection, whiteboard |
| Onsite Loop | 4-5 x 45 min | System design, LP behavioral, technical deep dive, coding, bar raiser |

## Core Technical Topics

### Compute
- **EC2**: Instance families (general, compute, memory, storage, GPU), purchasing options (on-demand, reserved, spot, savings plans), placement groups, user data, instance metadata, Nitro system
- **Lambda**: Execution model, cold starts, concurrency, reserved concurrency, provisioned concurrency, Lambda@Edge, SnapStart for Java, Lambda Power Tuning
- **ECS**: Cluster modes (EC2 vs Fargate), task definitions, services, Auto Scaling, service discovery, capacity providers
- **EKS**: Control plane vs data plane, node groups, Fargate profiles, add-ons, Cluster Autoscaler, Karpenter
- **Elastic Beanstalk**: Platform types, deployment policies (all-at-once, rolling, rolling with batch, immutable, blue/green)

### Storage
- **S3**: Storage classes, versioning, lifecycle policies, object lock, presigned URLs, multipart upload, S3 Select, S3 Object Lambda, access points
- **EBS**: Volume types (gp2, gp3, io1, io2, st1, sc1), snapshots, EBS multi-attach, EBS-optimized instances, encryption by default
- **EFS**: Performance modes (General Purpose, Max I/O), throughput modes (bursting, provisioned, elastic), lifecycle management, Access Points, EFS One Zone

### Database
- **RDS**: Multi-AZ, read replicas, cross-region read replicas, automated backups, snapshots, performance insights, Enhanced Monitoring
- **Aurora**: Aurora vs RDS, Aurora Serverless v2, Global Database, Aurora replicas, backtrack, Performance Insights
- **DynamoDB**: On-demand vs provisioned capacity, DAX, DynamoDB Streams, Global Tables, TTL, transactions, adaptive capacity, auto scaling, hot key mitigation
- **ElastiCache**: Redis vs Memcached, cluster mode, Redis AOF persistence, backup/restore, subnet group placement

### Networking
- **VPC**: CIDR, subnets, route tables, IGW, NAT Gateway, NAT Instance, VPC endpoints (Gateway, Interface), VPC peering, Transit Gateway, VPC Flow Logs, Security Groups, NACLs
- **Route 53**: Hosted zones, record types, routing policies (simple, weighted, latency, failover, geolocation, geoproximity, multivalue)
- **CloudFront**: Distributions, origins, behaviors, OAC/OAI, geo-restriction, Lambda@Edge, CloudFront Functions, real-time logs, field-level encryption
- **ELB**: ALB vs NLB vs GLB, target groups, stickiness, connection draining, cross-zone load balancing

### Security
- **IAM**: Users, groups, roles, policies (managed, inline, customer managed), policy conditions, permission boundaries, SCPs, role trust policies, service-linked roles
- **KMS**: CMK vs AWS managed, key rotation, key policies, grants, envelope encryption, HSM (CloudHSM), custom key store
- **WAF**: Web ACLs, rules, rate limiting, managed rule sets, IP sets, regex patterns, logging
- **Shield**: Standard vs Advanced, DDoS cost protection, DDoS response team
- **GuardDuty**: Findings, trusted IP lists, threat lists, suppression rules, auto-archiving
- **Security Hub**: Standards (CIS, PCI DSS, AWS Foundational), findings aggregation, cross-Region aggregation
- **CloudTrail**: Trail types, data events, Insights, Lake, log file validation

### Monitoring & Observability
- **CloudWatch**: Metrics, Logs, Logs Insights, Contributor Insights, composite alarms, anomaly detection, Synthetics canaries, embedded metric format
- **X-Ray**: Traces, segments, subsegments, sampling rules, annotations, service maps, X-Ray daemon

## 16 Leadership Principles

1. **Customer Obsession** — "Leaders start with the customer and work backwards"
2. **Ownership** — "Leaders never say 'that's not my job'"
3. **Invent and Simplify** — "Seek ways to simplify and innovate"
4. **Are Right, A Lot** — "Leaders have strong judgment and good instincts"
5. **Learn and Be Curious** — "Never stop learning"
6. **Hire and Develop the Best** — "Raise the performance bar with every hire"
7. **Insist on the Highest Standards** — "Continuously raise the bar"
8. **Think Big** — "A small mindset is a self-fulfilling prophecy"
9. **Bias for Action** — "Speed matters in business"
10. **Frugality** — "Accomplish more with less"
11. **Earn Trust** — "Listen, speak candidly, treat others respectfully"
12. **Dive Deep** — "Stay connected to details, audit frequently"
13. **Have Backbone; Disagree and Commit** — "Challenge decisions when you disagree"
14. **Deliver Results** — "Focus on key inputs, deliver with quality"
15. **Strive to be Earth's Best Employer** — "Empower everyone to be their best"
16. **Success and Scale Bring Broad Responsibility** — "Be humble, thoughtful, and impactful"

## Well-Architected Framework Pillars

| Pillar | Key Questions | Key Services |
|--------|---------------|--------------|
| Operational Excellence | How do you support operations? | CloudFormation, Config, CloudTrail, Systems Manager |
| Security | How do you protect data and systems? | IAM, KMS, Shield, WAF, GuardDuty, Security Hub |
| Reliability | How do you prevent and recover from failures? | RDS Multi-AZ, DynamoDB Global Tables, Route 53, Auto Scaling |
| Performance Efficiency | How do you use resources efficiently? | Lambda, Auto Scaling, CloudFront, ElastiCache |
| Cost Optimization | How do you avoid unnecessary costs? | Cost Explorer, Compute Optimizer, Savings Plans, S3 Lifecycle |
| Sustainability | How do you minimize environmental impact? | Graviton, serverless, region selection |

## Sample System Design Problems

1. **Design a video streaming platform** (Netflix-like)
   - S3 for storage, CloudFront for CDN, Lambda/ECS for transcoding, DynamoDB for metadata, ElastiCache for recommendations

2. **Design a real-time chat application**
   - API Gateway WebSocket, DynamoDB Streams, Lambda, SQS for offline messages

3. **Design an e-commerce platform**
   - CloudFront + S3 for static, ALB + ECS for services, DynamoDB for cart (high throughput), RDS Aurora for orders (ACID), ElastiCache for session state

4. **Design a serverless data lake**
   - S3 as data lake, Glue for catalog, Athena for queries, QuickSight for BI, Kinesis Firehose for ingestion

5. **Design a multi-region disaster recovery solution**
   - Route 53 failover routing, DynamoDB Global Tables, S3 CRR, RDS cross-region replicas, CloudFormation StackSets

## Preparation Resources

| Resource | Type | Cost |
|----------|------|------|
| AWS Docs & Whitepapers | Documentation | Free |
| AWS re:Invent Videos | Video | Free |
| A Cloud Guru / Pluralsight | Course | $35-50/mo |
| TutorialsDojo | Practice Exams | $15-20 |
| AWS Skill Builder | Learning Paths | $30/mo |
| Well-Architected Labs | Hands-on | Free |
| AWS Workshops | Hands-on | Free |

## Key Metrics to Know

| Service | Limit |
|---------|-------|
| S3 bucket size | Unlimited |
| S3 object size | 5TB |
| Lambda memory | 128MB-10GB |
| Lambda timeout | 15 min (900s) |
| Lambda deployment package | 250MB (unzipped) |
| EC2 instances per region | 20 (default, can increase) |
| VPC per region | 5 (default, can increase) |
| CloudFront distribution | 200 per account |
| Route 53 hosted zones | 500 per account |

## Interview Day Tips

1. **Listen carefully** — ask clarifying questions before diving into solutions
2. **Trade-offs** — always discuss pros and cons of your design choices
3. **Start simple** — begin with a basic architecture and iterate
4. **Name AWS services** — use correct service names and features
5. **Draw the architecture** — use the whiteboard (or paper for virtual)
6. **Quantify** — use metrics, numbers, cost estimates in your answers
7. **LP stories** — prepare 15+ STAR stories spanning 10+ LPs
8. **Failures** — own your mistakes and explain what you learned

---

*Last updated: July 2026*
