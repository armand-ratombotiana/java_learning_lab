# 15 — Cloud Cost Optimization — Theory

## Overview
Cloud cost optimization is the practice of reducing cloud spending while maintaining or improving performance. This lab covers reserved instances, savings plans, rightsizing, spot instances, and FinOps.

## 1. Reserved Instances (RI)

### RI Types
- **Standard RIs**: Best discount (up to 72%) for steady-state workloads
- **Convertible RIs**: Change attributes (instance family, OS, tenancy) with lower discount (up to 54%)
- **Scheduled RIs**: Time-window reservations for predictable workloads

### Payment Options
- **All Upfront**: Highest discount, pay entire term upfront
- **Partial Upfront**: Medium discount, smaller upfront + monthly payments
- **No Upfront**: Lowest discount, monthly payments only

### RI Attributes
- Instance family (m5, c5, r5)
- Region (us-east-1, eu-west-1)
- Tenancy (default, dedicated)
- OS (Linux, Windows, RHEL, SLES)

### RI Utilization
Track utilization via Cost Explorer or RI Utilization reports. Unused RIs can be sold on the RI Marketplace.

## 2. Savings Plans

### Plan Types
- **Compute Savings Plans**: Most flexible, up to 66% discount
  - Applies to any compute family, region, OS, or tenancy
  - Covers EC2, Fargate, Lambda
- **EC2 Instance Savings Plans**: Up to 72% discount
  - Applies to a specific instance family in a region
  - Covers EC2 only

### How Savings Plans Work
```
Commitment: $100/hour for 1-year
Usage: EC2 instance costs $50/hour
       Lambda costs $30/hour
       Fargate costs $20/hour
Total: $100/hour -> All covered at discounted rate
If usage < commitment: Pay difference at on-demand rate
If usage > commitment: Excess at on-demand rate
```

## 3. Rightsizing

### Rightsizing Process
1. **Collect**: Gather CPU, memory, network, disk utilization data
2. **Analyze**: Identify over-provisioned and under-provisioned resources
3. **Recommend**: Suggest optimal instance types and sizes
4. **Act**: Resize or change instance types
5. **Monitor**: Track savings and performance after changes

### Key Metrics
- **CPU Utilization**: Average and peak (target 40-70%)
- **Memory Utilization**: Peak usage and allocation
- **Network IO**: Bandwidth usage patterns
- **Disk IOPS and Throughput**: Storage performance needs

### Rightsizing Tools
- AWS Compute Optimizer
- Azure Advisor
- GCP Recommender
- CloudHealth, CloudCheckr (third-party)

## 4. Spot Instances

### How Spot Instances Work
Spot instances use AWS spare capacity at discounted rates (up to 90% off on-demand). Instances can be reclaimed with 2-minute warning if AWS needs the capacity.

### Spot Instance Lifecycle
```
Request -> Pending -> Fulfilled -> Running -> Interrupted -> Terminated
                                                |
                                    2-minute warning (rebalance recommendation)
```

### Spot Best Practices
- Use fault-tolerant workloads (stateless, checkpointing)
- Choose instance types that are less likely to be reclaimed (diverse types)
- Use Spot Fleet or EC2 Fleet for multiple instance types
- Implement graceful shutdown handlers (SQS notification, checkpoint to S3)

### Spot Use Cases
- Batch processing and data analytics
- CI/CD build agents
- Web servers behind ALB
- Machine learning training
- HPC workloads

## 5. FinOps

### FinOps Lifecycle
```
                 +-------------+
                 |   Inform    |
                 |  (Visibility)|
                 +------+------+
                        |
                 +------v------+
                 |  Optimize   |
                 |  (Efficiency)|
                 +------+------+
                        |
                 +------v------+
                 |  Operate    |
                 | (Governance)|
                 +-------------+
```

### FinOps Personas
- **Finance**: Budgeting, forecasting, chargebacks
- **Engineering**: Resource optimization, tagging
- **Operations**: Cost anomaly detection, governance
- **Executives**: Business value, investment decisions

### Cost Allocation
- **Tags**: Label resources with cost center, environment, team, project
- **Accounts**: Separate accounts per team/environment
- **Cost Categories**: AWS Cost Categories for grouping

### Cost Optimization Metrics
- **Unit Cost**: Cost per transaction, per user, per GB
- **Waste Percentage**: Unused or underutilized resources
- **Coverage Rate**: Reserved/Savings Plan coverage
- **Savings Opportunity**: Potential savings from recommendations
- **Cost Per Tag**: Spending by cost allocation tags

### Automating Cost Optimization
- Schedule non-production instances to stop at night
- Auto-delete stale EBS snapshots and unattached volumes
- Rightsize based on CloudWatch metrics with Lambda automation
- Enforce tagging policies with Config rules

## Key Takeaways
1. Reserved Instances and Savings Plans offer significant discounts for committed usage
2. Rightsizing ensures you only pay for what you need
3. Spot Instances provide deep discounts for fault-tolerant workloads
4. FinOps brings financial accountability to cloud spending
5. Automate cost optimization to continuously reduce waste
