# Interview Questions: EBS Architecture

## Oracle-Specific Questions
- Explain the EBS R12.2 multi-tier architecture: desktop tier, application tier, database tier — what services run on each?
- What is Edition-Based Redefinition (EBR) and how does ADOP leverage it for online patching?
- Describe the concurrent manager architecture: how does the Internal Monitor Manager coordinate concurrent processing?
- How does Multi-Org Access Control (MOAC) work using Virtual Private Database (VPD) and `FND_MOBS`?
- Explain the EBS file system structure in R12.2: APPL_TOP, COMMON_TOP, INST_TOP — what is in each?
- What is the role of Oracle HTTP Server (OHS) and OC4J in the EBS architecture?
- How does the EBS database tier handle connections via FNDSM and FNDLIBR?
- Explain the EBS technology stack versions: database 19c, Forms 12c, OAF 12.2, Java 8.

## Google Cloud / Technical
- Migrating EBS architecture to Google Cloud Compute Engine
- Cloud SQL proxy for EBS database connections
- Google Cloud Load Balancing for EBS web tier

## Microsoft / Azure
- Deploying EBS on Azure VMs — reference architecture for high availability
- Azure SQL Database vs Oracle Database for EBS workloads
- Azure Site Recovery for EBS disaster recovery

## Amazon / AWS
- EBS on AWS — EC2 placement groups for low-latency RAC interconnect
- AWS Direct Connect for on-premise EBS application access
- Amazon MQ for EBS integration messaging

## Apple
- EBS mobile applications security architecture
- Mac-based EBS Forms access via Java Web Start

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | Self JOIN |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 262 | Trips and Users | Hard | JOIN + Filter |
| LC 585 | Investments in 2016 | Medium | Self JOIN + Subquery |

## Production Scenarios
- Scenario 1: "Production incident — EBS application tier services (OHS, Forms) crash after patch"
- Scenario 2: "Performance tuning — Concurrent manager requests queuing up, not processing"
- Scenario 3: "Disaster recovery — EBS database corrupt after failed ADOP cutover"
- Scenario 4: "Security breach — EBS port 8000 exposed to internet in multi-tier architecture"

## Interview Patterns & Tips
- Oracle EBS architecture interviews focus on R12.2 key concepts: EBR, ADOP, MOAC
- Expect deep questions about concurrent processing and forms server architecture
- OCP EBS certification covers EBS system administrator's guide
- EBS architects earn $140K-$200K; senior consultants $120K-$170K
