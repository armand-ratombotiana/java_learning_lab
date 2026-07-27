# Mock Interview — Multi-Cloud & Hybrid

## Format
- **Duration**: 45 minutes
- **Type**: Strategic + Architecture
- **Difficulty**: Professional/Architect

## Warm-Up (5 min)

Q1: What are the main drivers for adopting a multi-cloud strategy? What are the risks?

Q2: What is the difference between multi-cloud, hybrid cloud, and poly-cloud?

## Technical Questions (20 min)

### Question 1: Multi-Cloud Architecture (10 min)
A financial services company wants to adopt multi-cloud to reduce vendor lock-in and improve DR. Currently on AWS (us-east-1). They need to add GCP as a secondary provider.

**Design the multi-cloud architecture**:
- Application: Java microservices on Kubernetes
- Data: PostgreSQL, Redis, S3
- Traffic: Active-active or active-passive
- Identity federation
- Networking between clouds
- CI/CD across both clouds

### Question 2: Cloudflare as Multi-Cloud Gateway (10 min)
Your company wants to use Cloudflare as a unified edge for multi-cloud workloads. Workloads run on AWS (primary) and GCP (secondary).

**Design**: How does Cloudflare route traffic between the two clouds? How do you handle SSL, WAF, DDoS, and load balancing? What happens during failover?

## Behavioral Question (10 min)

**Question**: Tell me about a time you implemented or evaluated a multi-cloud strategy. What were the trade-offs you considered and what was the outcome?

## System Design Whiteboard (10 min)

**Problem**: Design a multi-cloud data replication strategy for an e-commerce platform:
- Primary: AWS DynamoDB Global Tables (us-east-1, eu-west-1)
- Secondary: GCP Cloud Spanner (us-central1, europe-west1)
- Data must be synchronized with < 5 minute delay
- Must handle conflicts
- Compliance: GDPR data stays in EU

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| Multi-Cloud Strategy | Strategic drivers, risk assessment | Knows the concept | No clear reasoning |
| Cross-Cloud Networking | Interconnects, VPN, SD-WAN, latency | Basic VPN | No networking plan |
| Identity Federation | SAML, OIDC, SCIM, centralized IdP | Knows federation | No identity strategy |
| Data Replication | CDC, conflict resolution, latency trade-offs | Basic cross-region | No replication |
| IaC Multi-Cloud | Terraform abstraction, modules per provider | One provider only | Separate codebases |

## Sample Solution Outline

### Multi-Cloud Architecture
- **K8s Abstraction**: EKS (AWS) + GKE (GCP) with standardized Helm charts
- **Terraform**: Modules with provider abstraction (different provider per env)
- **Traffic**: Active-passive: Cloudflare as global load balancer
  - Primary: AWS; health check fails → route to GCP
- **Data**:
  - PostgreSQL: AWS RDS primary, GCP Cloud SQL as cross-cloud replica (Debezium CDC)
  - Redis: ElastiCache (AWS) + Memorystore (GCP) — separate, no sync
  - S3 → Cloud Storage: Cross-cloud sync via Storage Transfer Service
- **Identity**: Okta/Azure AD as master IdP → SAML to AWS IAM Identity Center + GCP Workforce Identity
- **Secrets**: HashiCorp Vault with replication between clouds
- **CI/CD**: GitLab CI with matrix strategy (deploy to AWS and/or GCP)
- **Monitoring**: Datadog with unified dashboards across both clouds

### Cloudflare Multi-Cloud Gateway
- Cloudflare as DNS + CDN + WAF + Load Balancer
- Traffic routing: Cloudflare Load Balancer with origin pools (AWS pool, GCP pool)
  - Geo-steering: NA → AWS, EU → GCP
  - Failover: Health check on each origin → if AWS down, route to GCP
- SSL: Cloudflare Universal SSL or Custom SSL — terminate at edge, re-encrypt to origin
- WAF: Cloudflare WAF rules (OWASP, rate limiting, bot management)
- DDoS: Cloudflare L3/L4/L7 DDoS protection at edge
- Origin: Authenticated Origin Pulls (Cloudflare → AWS/GCP)
- Argo Smart Routing for optimal path between edge and origin

### Multi-Cloud Data Replication
- **DynamoDB → GCP**: 
  - DynamoDB Streams → Lambda → Pub/Sub → Dataflow → Cloud Spanner
  - Lambda processes CDC events, maps to Spanner schema
  - Conflict resolution: Last-writer-wins with timestamp
- **Spanner → DynamoDB**:
  - Spanner change streams → Pub/Sub → Dataflow → Kinesis → DynamoDB
- **Latency**: Target < 5 min (CDC + streaming achieves < 1 min typically)
- **GDPR**: Data tagged by region to stay in EU
  - AWS eu-west-1 → Spanner europe-west1 (direct)
  - No cross-region sync for EU data
- **Monitoring**: Track replication lag, conflict rate, error rate
- **Disaster recovery**: If primary fails, promote secondary with catch-up
