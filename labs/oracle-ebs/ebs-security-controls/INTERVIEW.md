# Interview Questions: EBS Security Controls

## Oracle-Specific Questions
- Explain EBS security architecture: user management, responsibility, function security, data security, and VPD.
- How does Virtual Private Database (VPD) work in EBS? Explain `FND_MOBS` and how it enforces Multi-Org security.
- What is Function Security in EBS? How do you use `FND_FORM_FUNCTION` and `FND_MENU_ENTRIES` to control access?
- Explain Audit Trail in EBS: how do you enable auditing on tables using `FND_AUDIT` and `FND_AUDIT_TABLES`?
- How does EBS handle user password policies: password expiration, complexity, and account locking?
- What is the EBS Sign-On Audit and how do you configure it for compliance?
- Explain the EBS encryption capabilities: Transparent Data Encryption (TDE) and column-level encryption.
- How do you configure Single Sign-On (SSO) for EBS using Oracle Internet Directory (OID) or OAM?

## Google Cloud / Technical
- Cloud IAM integration with EBS for federated identity
- Google Workspace password sync with EBS security
- Cloud Audit Logs for EBS security monitoring

## Microsoft / Azure
- Azure AD as identity provider for EBS SSO
- Azure Sentinel for EBS security log analysis
- Azure Policy for EBS compliance enforcement

## Amazon / AWS
- AWS IAM roles for EBS cross-account access
- Amazon GuardDuty for EBS security threat detection
- AWS KMS for EBS data encryption key management

## Apple
- Apple device compliance for EBS mobile access
- EBS security alignment with Apple's supplier responsibility standards

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 182 | Duplicate Emails | Easy | GROUP BY |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 196 | Delete Duplicate Emails | Easy | Self JOIN |
| LC 197 | Rising Temperature | Easy | DATEDIFF |
| LC 262 | Trips and Users | Hard | JOIN + Filter |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN |
| LC 626 | Exchange Seats | Medium | CASE + LEAD |

## Production Scenarios
- Scenario 1: "Production incident — VPD policy blocks all users from EBS (ORA-28112)"
- Scenario 2: "Performance tuning — FND_AUDIT logging slowing down critical transactions"
- Scenario 3: "Disaster recovery — Encryption key lost, encrypted data inaccessible"
- Scenario 4: "Security breach — Shared EBS accounts enabling unauthorized access"

## Interview Patterns & Tips
- EBS security interviews cover VPD, function security, and audit architecture
- Expect scenario questions about Segregation of Duties in EBS
- OCP EBS Security certification covers FND security, VPD, and audit
- EBS Security specialists: $130K-$185K; Compliance consultants: $140K-$200K
