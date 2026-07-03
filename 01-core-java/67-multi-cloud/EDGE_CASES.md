# Module 67: Multi-Cloud Architectures - Edge Cases & Pitfalls

---

## Pitfall 1: Cross-Cloud Database Queries

### ❌ Wrong
Deploying the Web API layer in AWS (to use cheap compute) and the central Database in GCP (because the team prefers Cloud SQL), and having the AWS API query the GCP database for every user request.

### ✅ Correct
This introduces massive network latency (50ms+ per query instead of 1ms) and astronomical Egress costs. Compute and Data must be kept closely coupled within the same cloud provider and region to avoid the "Data Gravity" penalty.

---

## Pitfall 2: Lowest Common Denominator Architectures

### ❌ Wrong
Refusing to use AWS SQS, AWS DynamoDB, or Azure Functions simply because "we might move to another cloud provider someday," and instead spending 6 months building and maintaining a custom Kafka cluster and Cassandra cluster on raw VMs.

### ✅ Correct
Unless multi-cloud is a hard, immediate business requirement (e.g., for specific legal compliance or a massive SaaS platform), taking on the operational burden of managing complex stateful systems just to maintain vendor neutrality is usually a net-negative. For 95% of companies, embracing managed, proprietary cloud services significantly accelerates time-to-market.

---

## Pitfall 3: Fragmented Identity and Access Management (IAM)

### ❌ Wrong
Managing user access, database passwords, and API keys manually and separately in the AWS IAM console and the Azure IAM console. When a developer leaves the company, an admin forgets to revoke their Azure access, leading to a breach.

### ✅ Correct
In a multi-cloud environment, Identity must be federated. Use a centralized Identity Provider (IdP) like Okta or Azure AD, and configure AWS and GCP to trust that provider via SAML/OIDC. Use HashiCorp Vault as a cloud-agnostic secret manager so applications pull credentials uniformly regardless of which cloud they run in.