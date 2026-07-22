# Interview Questions: EBS Setup and Configuration

## Oracle-Specific Questions
- What is Rapid Install and what are the key configuration files it generates? Explain the `rapidinstall.cfg` contents.
- How does Multi-Org Access Control (MOAC) work? How do you configure operating units, ledgers, and business groups?
- Explain flexfields in EBS: Key Flexfields (KFF) vs Descriptive Flexfields (DFF) — how do you configure each?
- What are profile options in EBS? How do you set profile option levels (Site, Application, Responsibility, User)?
- How do you configure the EBS file system in R12.2? Explain APPL_TOP, COMMON_TOP, and the dual file system.
- What is AutoConfig and how does it manage EBS configuration files?
- Explain the EBS domain model in R12.2: how are product families, products, and modules organized?
- How do you configure EBS security: user creation, responsibility assignment, and data security rules?

## Google Cloud / Technical
- Automating EBS Rapid Install on Google Cloud Compute Engine
- Google Deployment Manager for EBS infrastructure as code
- Cloud DNS configuration for EBS load balancing

## Microsoft / Azure
- Azure Automation for EBS configuration management
- Azure Policy for EBS compliance configuration
- Azure Backup for EBS configuration backups

## Amazon / AWS
- AWS CloudFormation templates for EBS Rapid Install
- AWS Systems Manager Parameter Store for EBS configuration values
- Amazon FSx for EBS shared file system

## Apple
- Configuring EBS for Apple Business Manager integration
- Apple Configurator for EBS mobile access settings

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 181 | Employees Earning More Than Managers | Easy | Self JOIN |
| LC 182 | Duplicate Emails | Easy | GROUP BY |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 196 | Delete Duplicate Emails | Easy | Self JOIN |
| LC 197 | Rising Temperature | Easy | DATEDIFF |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN |

## Production Scenarios
- Scenario 1: "Production incident — AutoConfig fails after OS patching, configuration files corrupted"
- Scenario 2: "Performance tuning — Profile option '%' causing excessive database lookups"
- Scenario 3: "Disaster recovery — Flexfield structure corrupted after incomplete data migration"
- Scenario 4: "Security breach — Too many responsibilities assigned to a single user violating SOD"

## Interview Patterns & Tips
- Oracle EBS consultants must understand Rapid Install and AutoConfig deeply
- Flexfield configuration is a common interview whiteboard topic
- OCP EBS certification includes setup and configuration modules
- EBS functional consultants earn $110K-$160K; technical $120K-$175K
