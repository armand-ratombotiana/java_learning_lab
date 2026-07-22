# Interview Questions: EBS Customization and Extension

## Oracle-Specific Questions
- What is CEMLI? Explain each component: Configuration, Extension, Modification, Localization, Integration.
- How do you use Oracle Workflow Builder to create custom approval workflows with business events?
- Explain Oracle Approvals Management (AME): how do you create approval rules, conditions, and hierarchies?
- How do you create custom Forms personalizations using Forms Personalization (Form Personalization Rules)?
- What is the OAF Extension methodology: how do you extend seeded VO, EO, and AM using BC4J?
- How do you use FNDLOAD to migrate CEMLI components between environments?
- Explain the EBS Integration Repository and how it publishes integration interfaces.
- How do you use Oracle SOA Suite with EBS for enterprise integrations?

## Google Cloud / Technical
- EBS customization to Google Cloud Tasks for async processing
- Apigee for EBS API management and customization
- Cloud Functions for EBS integration extensions

## Microsoft / Azure
- Azure Integration Services with EBS CEMLI
- Logic Apps custom connectors for EBS
- Service Bus for EBS asynchronous extensions

## Amazon / AWS
- EventBridge for EBS custom event routing
- Step Functions for EBS custom workflows
- AppSync as GraphQL layer for EBS data

## Apple
- iOS custom app integration with EBS via ORDS
- Custom EBS mobile approval flows for Apple devices

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | LAG |
| LC 181 | Employees Earning More Than Managers | Easy | Self JOIN |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK |
| LC 262 | Trips and Users | Hard | JOIN + Filter |
| LC 571 | Find Median Given Frequency | Hard | Window + SUM |

## Production Scenarios
- Scenario 1: "Production incident — Custom workflow failed after upgrade, approval chain broken"
- Scenario 2: "Performance tuning — Custom Forms personalization causing excessive DB calls"
- Scenario 3: "Disaster recovery — FNDLOAD export of custom components lost during migration"
- Scenario 4: "Security breach — Custom AME rule allowing unauthorized approvals"

## Interview Patterns & Tips
- EBS customization interviews focus on CEMLI methodology and tools like Workflow Builder and AME
- Expect scenario questions about extending seeded functionality without breaking patches
- OCP EBS Customization certification covers Forms, OAF, Workflow, and AME
- EBS technical consultants: $120K-$175K; Integration architects: $150K-$210K
