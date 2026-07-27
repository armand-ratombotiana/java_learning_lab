# Mock Interview — Cloud Cost Optimization

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Analytical
- **Difficulty**: Professional

## Warm-Up (5 min)

Q1: What is FinOps? Explain the three phases (Inform, Optimize, Operate).

Q2: Compare On-Demand, Reserved Instances, Savings Plans, and Spot Instances. When would you use each?

## Technical Questions (20 min)

### Question 1: Cost Analysis & Optimization (10 min)
Your monthly AWS bill is $150K distributed as: EC2 (45%), RDS (20%), Data Transfer (15%), S3 (10%), Others (10%).

**Analyze and optimize each category**:
- EC2: How do you identify waste? What's the first thing you'd do?
- RDS: What optimization options exist?
- Data Transfer: Where are the biggest savings opportunities?
- S3: Storage class optimization strategy

### Question 2: Reserved Instance Strategy (10 min)
Design a Reserved Instance purchasing strategy for a company with:
- 200 EC2 instances across 10 instance types
- 30 RDS instances (various engine types)
- Steady baseline (60% usage is predictable 24/7)
- Variable (30% follows business hours)
- Seasonal peaks (10% during holiday season, 2x normal)

**Calculate**: What % should be Reserved? What term? What payment option?

## Behavioral Question (10 min)

**Question**: Tell me about a time you identified and implemented significant cloud cost savings. What was your approach and how did you get stakeholder buy-in?

## System Design Whiteboard (10 min)

**Problem**: Design a cost governance framework for a company with:
- 5 AWS accounts (dev, staging, prod, shared, data)
- 10 product teams
- Monthly budget: $200K (hard limit)
- Must support chargeback and showback
- Automated enforcement

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| EC2 Optimization | RI, SP, spot, rightsizing, graviton | Knows RI exists | On-demand only |
| RDS Optimization | RI, instance sizing, Aurora, serverless | Basic RDS | No optimization |
| Data Transfer | CloudFront, Direct Connect, VPC endpoints | Some awareness | Not tracked |
| S3 Lifecycle | Intelligent-Tiering, Glacier, expiration | Basic lifecycle | No lifecycle |
| FinOps | Maturity model, governance, culture | Basic cost tracking | Only reactive |
| Governance | Budgets, alerts, policies, automation | Manual review | No governance |

## Sample Solution Outline

### Cost Analysis ($150K bill)

**EC2 ($67.5K — 45%)**:
- Identify waste: Compute Optimizer for underutilized instances (likely 30-40%)
- Rightsize: downsize oversized instances (e.g., m5.4xlarge → m5.2xlarge)
- Graviton: Migrate x86 to AWS Graviton (20-40% better price/performance)
- Purchasing: 3-year Compute Savings Plan for baseline (60% coverage, ~46% discount)
- Spot: Move non-critical, fault-tolerant workloads (30% of remaining, ~70% discount)
- Auto-scaling: Ensure right-sizing and schedule-based scaling (stop dev/test nights/weekends)
- Estimated savings: $27K/month (40%)

**RDS ($30K — 20%)**:
- Reserved Instances for steady-state databases (3-year, ~46% discount)
- Right-size instance classes (review CloudWatch metrics)
- Consider Aurora for better performance/price (especially if MySQL/PostgreSQL)
- Serverless for variable workloads
- Estimated savings: $12K/month (40%)

**Data Transfer ($22.5K — 15%)**:
- CloudFront for egress: reduced S3 data transfer costs
- VPC endpoints for S3/DynamoDB (no NAT Gateway data processing charges)
- Direct Connect for large data transfers (lower per-GB rate vs internet)
- Compress data before transfer
- Estimated savings: $9K/month (40%)

**S3 ($15K — 10%)**:
- Lifecycle policies: Standard → Standard-IA (30d) → Glacier (90d) → Deep Archive (365d)
- S3 Intelligent-Tiering for unknown patterns
- Delete incomplete multipart uploads and expired object versions
- Estimated savings: $6K/month (40%)

**Total estimated savings**: $54K/month (36% reduction)

### Reserved Instance Strategy
- **Baseline (60% — 120 instances)**: 3-year All Upfront Reserved Instances
  - Compute Savings Plan (flexible across instance families)
  - Covers predictable steady-state workload
  - ~46% discount vs on-demand
- **Variable (30% — 60 instances)**: 1-year Partial Upfront Reserved Instances
  - Covers business-hour predictable spikes
  - ~30% discount vs on-demand
- **Seasonal (10% — 20 instances)**: On-Demand + Spot
  - Can't predict exact timing
  - Use Spot Fleet for variety across instance types
  - ~70% discount vs on-demand
- **Review quarterly**: Adjust RI coverage as usage patterns evolve

### Cost Governance Framework
- **Account Structure**: AWS Organizations with SCPs
  - dev: budget $30K, no RI purchases allowed
  - staging: budget $20K, limited instance types
  - prod: budget $100K, reserved + spot
  - shared: budget $30K
  - data: budget $20K
- **Tags**: Mandatory tags (cost-center, environment, team, project) via AWS Tag Policies
- **Budgets**: AWS Budgets per team + per account, alert at 80% and 100%
- **Automated Actions**:
  - AWS Budget Action: Stop EC2 instances when dev budget exceeded
  - SCP: Deny expensive instance types (e.g., p3/p4) without VP approval
  - Config Rules: Auto-remediate untagged resources
- **Reporting**:
  - Monthly cost allocation report (by cost-center tag)
  - Infrastructure efficiency ratio (compute cost per transaction)
  - Weekly top-5 anomalies email
- **Process**:
  - Weekly: Cost review by team lead
  - Monthly: FinOps review with finance
  - Quarterly: RI/SP purchase planning
  - Annually: Cloud provider negotiation / discount review
