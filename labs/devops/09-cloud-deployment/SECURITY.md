# Cloud Deployment Security

## Identity and Access Management
- **AWS IAM**: Least-privilege policies; IRSA for K8s; IAM roles for EC2.
- **GCP IAM**: Workload Identity; service account impersonation; organization policies.
- **Azure AD**: Managed AAD integration; RBAC for AKS; managed identities.

## Network Security
- **VPC/Network**: Private subnets for workloads; NAT gateways for outbound traffic.
- **Security groups**: Minimum required ingress/egress rules.
- **NACLs**: Stateless subnet-level traffic filtering.
- **VPC endpoints**: Private access to S3, DynamoDB, ECR without internet gateway.
- **Cloud WAF**: AWS WAF, Cloud Armor (GCP), Azure WAF for application protection.

## Data Security
- **Encryption at rest**: S3 SSE, EBS encryption, RDS encryption, KMS/Cloud KMS.
- **Encryption in transit**: TLS everywhere; ACM/SSL certs for load balancers.
- **Secrets management**: AWS Secrets Manager, GCP Secret Manager, Azure Key Vault.
- **Backup encryption**: Encrypted backups and snapshots.

## Compliance
- **Certifications**: SOC2, HIPAA, PCI-DSS, FedRAMP (varies by provider and service).
- **Audit logging**: CloudTrail (AWS), Cloud Audit (GCP), Activity Logs (Azure).
- **Guardrails**: Service control policies (AWS), Organization policies (GCP), Azure Policy.
- **Compliance frameworks**: CIS benchmarks for cloud foundations.
