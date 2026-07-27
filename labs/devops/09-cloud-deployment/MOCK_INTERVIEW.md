# Cloud Deployment MOCK_INTERVIEW.md

## Scenario 1: Multi-Cloud Strategy
Your company wants to move from a single cloud provider to multi-cloud.

**Questions**:
1. Why would a company choose multi-cloud?
2. What are the challenges of multi-cloud?
3. How do you manage resources across clouds?
4. How do you handle networking between clouds?

**Expected approach**: Reasons: vendor lock-in avoidance, regional presence, cost optimization. Challenges: complexity, consistency, networking, IAM, skills. Management: Terraform/Pulumi for IaC, Kubernetes for portability, Consul for service discovery. Networking: VPN/Peering, Cloud Interconnect/Direct Connect, mesh gateways.

## Scenario 2: Cloud Migration
You need to migrate 200 VMs from on-premise to AWS.

**Questions**:
1. What migration strategy would you use?
2. How do you assess dependencies?
3. How do you migrate databases?
4. What's the rollback plan?

**Expected approach**: Assess: dependency mapping, VM rightsizing, TCO analysis. Strategy: rehost (lift-and-shift) first, then replatform/refactor. Database: DMS for live migration, or cutover with replication. Rollback: keep on-prem running, DNS cutover back, test plan. Use Migration Evaluator, MGN, or third-party tools.

## Scenario 3: Infrastructure as Code on Cloud
Your cloud infrastructure is managed manually (click-ops). You need to automate.

**Questions**:
1. How do you introduce IaC incrementally?
2. How do you manage state for existing resources?
3. How do you handle configuration drift?
4. How do you enforce tagging and compliance?

**Expected approach**: Start with new resources in Terraform, import existing resources. Use `terraform import`. Drift detection: `terraform plan` in CI, periodic plan, AWS Config/GCP Cloud Asset Inventory. Tagging: Terraform variables, enforce via policy (OPA, Sentinel, Service Control Policies).

## Scenario 4: Cloud Cost Optimization
Your cloud bill has doubled in the last quarter. You need to reduce costs.

**Questions**:
1. How do you analyze cloud costs?
2. What cost optimization strategies exist?
3. How do you implement cost awareness in engineering teams?
4. How do you handle reserved instances vs spot instances?

**Expected approach**: Cost analysis: AWS Cost Explorer, CloudHealth, native cost tools. Strategies: right-sizing, reserved instances, spot instances, auto-scaling, delete unused resources, storage lifecycle policies. Cost awareness: tagging, cost allocation, showback/chargeback, budget alerts. Spot: handle interruptions via graceful shutdown.

## Scenario 5: Cloud Security
Your cloud environment needs to comply with SOC2 and HIPAA.

**Questions**:
1. How do you secure cloud infrastructure?
2. How do you implement network security?
3. How do you handle data encryption?
4. How do you audit cloud security posture?

**Expected approach**: Network: VPC/network isolation, security groups, NACLs, private subnets. Encryption: KMS for at-rest, TLS for in-transit. IAM: least privilege, roles, policies, OIDC for K8s. Audit: CloudTrail, GuardDuty, GCP Cloud Security Command Center, Azure Security Center; 3rd party: Wiz, Lacework, Prisma Cloud.

## Key Cloud Deployment Interview Questions
1. Compare AWS, GCP, and Azure for containerized workloads.
2. What's the difference between IaaS, PaaS, FaaS, and CaaS?
3. How do you design a highly available architecture on a cloud?
4. Explain cloud networking: VPC, subnets, transit gateway, peering.
5. How do you handle cloud service limits and quotas?
6. What's the shared responsibility model?
7. Explain infrastructure as code vs configuration management.
8. How do you handle cloud provider outages?
9. What's the difference between vertical and horizontal scaling?
10. Explain cloud storage options: object, block, file.

## Whiteboard Challenge
Design a cloud-native, multi-region, highly available architecture for a SaaS application on AWS (or your preferred cloud). Include compute, storage, networking, database, CDN, monitoring, and disaster recovery.

## Follow-up
1. How would you estimate the monthly cost?
2. How would you implement compliance (SOC2, HIPAA)?
3. How would you handle data residency requirements?