# Module 67: Multi-Cloud Architectures - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the "Lowest Common Denominator" problem in multi-cloud architectures?
**Answer**:
When an organization decides they must be able to instantly failover from AWS to Azure, they are forced to use technologies that run on both. This means they cannot use AWS DynamoDB or Azure Cosmos DB (highly optimized, proprietary managed services). Instead, they are forced to deploy open-source databases (like Apache Cassandra or PostgreSQL) inside Kubernetes clusters on bare VMs. 
The problem is that managing distributed databases manually is extremely difficult, costly, and requires specialized DBA talent. By avoiding vendor lock-in, the organization sacrifices the massive productivity gains provided by managed cloud services, settling for the "lowest common denominator" of raw compute and storage.

### Q2: Why are Egress costs a major factor in multi-cloud design?
**Answer**:
Cloud providers (AWS, GCP, Azure) typically do not charge you to upload data into their network (Ingress is free). However, they charge steep fees to transfer data *out* of their network to the internet or a competitor's cloud (Egress). 
If you design an architecture where the Web Server is in AWS and the Database is in GCP, every single database query incurs an Egress fee. At scale, this cross-cloud traffic can cost millions of dollars, completely erasing any minor cost savings gained by using cheaper compute in a different cloud. Compute and its dependent data must live in the same cloud region.

### Q3: Explain "Data Sovereignty".
**Answer**:
Data sovereignty refers to the fact that digital data is subject to the laws and governance structures of the country in which it is physically stored. For example, GDPR requires certain European citizen data to remain within the EU. 
If an application is hosted on a primary cloud provider that does not have a data center in a specific mandated country, a multi-cloud strategy is required to deploy a localized version of the application on a secondary cloud provider that *does* have a data center in that country to comply with the law.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Multi-Cloud Disaster Recovery
**Problem**: An interviewer proposes: "We run our core Java e-commerce platform entirely on AWS. If the AWS `us-east-1` region goes down entirely, we lose millions of dollars an hour. We want an Active-Passive Multi-Cloud architecture with GCP as the passive backup. Walk me through the infrastructure components needed to make a failover to GCP work in under 15 minutes."

**Solution Strategy**:
1. **Compute Abstraction**: Ensure the Java application is containerized (Docker) and deployed via Kubernetes (EKS in AWS, GKE in GCP). The exact same Helm charts or YAML manifests must be used for both.
2. **Infrastructure as Code (IaC)**: The entire GCP environment (VPCs, GKE clusters) must be defined in Terraform. If a disaster strikes, you run `terraform apply` to spin up the GCP environment instantly, rather than clicking through the UI.
3. **Data Replication**: This is the hardest part. You cannot wait for the outage to happen to move data. The primary database in AWS (e.g., PostgreSQL) must be continuously replicating its data to a standby database in GCP asynchronously over a secure VPN/Direct Connect link.
4. **DNS Failover**: Use a global DNS provider (like Cloudflare or Route53). Configure health checks on the AWS endpoint. If AWS fails, the DNS provider automatically changes the A-Record to point to the GCP Load Balancer.