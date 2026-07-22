# Interview Questions: EBS Upgrade and Migration

## Oracle-Specific Questions
- Explain the ADOP (Application Online Patching) cycle: prepare, apply, finalize, cutover, cleanup phases.
- How does Edition-Based Redefinition (EBR) support zero-downtime patching in R12.2?
- What are the pre-requisite checks for upgrading from R12.1 to R12.2? List the certification matrix.
- How do you migrate EBS from on-premise to Oracle Cloud Infrastructure (OCI)?
- Explain the EBS upgrade path: 11i → R12.1 → R12.2. What are the key considerations for each step?
- What is the EBS Technology Stack: what versions of database, Forms, and Java are certified with R12.2?
- How do you clone an EBS environment using `adcfgclone.pl`? What are the post-clone steps?
- Explain the EBS Cloud Migration options: OCI, AWS, Azure — what are the architecture differences?

## Google Cloud / Technical
- Migrating EBS to Google Cloud Compute Engine — reference architecture
- Cloud Storage for EBS migration data transfer
- Migrate for Compute Engine (formerly Velostrata) for EBS lift-and-shift

## Microsoft / Azure
- EBS on Azure: certified VMs, storage, and networking configuration
- Azure Database Migration Service for EBS to PostgreSQL
- Azure Site Recovery for EBS disaster recovery migration

## Amazon / AWS
- AWS Database Migration Service (DMS) for EBS database migration
- EBS on AWS Quick Start reference deployment
- Server Migration Service for EBS application tier

## Apple
- Data privacy during EBS cloud migration — GDPR compliance
- Secure data transfer for EBS migration to Apple suppliers

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | LAG |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK |
| LC 262 | Trips and Users | Hard | JOIN + Filter |
| LC 569 | Median Employee Salary | Hard | PERCENTILE_CONT |

## Production Scenarios
- Scenario 1: "Production incident — ADOP cutover fails, database edition switch incomplete"
- Scenario 2: "Performance tuning — Post-upgrade performance degradation due to stale statistics"
- Scenario 3: "Disaster recovery — Migration to cloud fails, on-premise rollback required"
- Scenario 4: "Security breach — Migration data intercepted during cloud transfer"

## Interview Patterns & Tips
- EBS upgrade interviews focus on ADOP, EBR, and R12.2 architecture
- Expect detailed questions about the ADOP lifecycle and rollback procedures
- OCP EBS Upgrade certification covers upgrade planning, execution, and post-upgrade validation
- Upgrade consultants: $130K-$190K; Migration architects: $150K-$220K
- Cloud migration experience (OCI/AWS/Azure) is highly valued
