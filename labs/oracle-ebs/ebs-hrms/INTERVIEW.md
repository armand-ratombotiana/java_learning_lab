# Interview Questions: EBS HRMS

## Oracle-Specific Questions
- Explain the EBS HRMS architecture: business group, organization, position, job, grade, and person entities.
- How does the EBS payroll engine work? Explain element design, formula processing, and run results.
- How do you configure talent management in EBS: performance appraisals, succession planning, and career development?
- What is absence management in EBS and how do you configure accrual plans and absence types?
- Explain the HRMS data model: PER_ALL_PEOPLE_F, PAY_ELEMENT_ENTRIES_VALUES, PAY_RUN_RESULTS.
- How does the Oracle HRMS SSHR (Self-Service HR) work with OAF?
- How do you upgrade HRMS for legislative changes (e.g., country-specific statutory deductions)?
- What is the difference between Oracle HRMS and Oracle Fusion HCM Cloud?

## Google Cloud / Technical
- EBS HRMS integration with Google Workspace for employee data sync
- Google Chat bot for EBS HR self-service queries
- Cloud Identity integration with EBS HRMS for SSO

## Microsoft / Azure
- Azure AD for EBS HRMS identity management
- Dynamics 365 HR vs Oracle EBS HRMS
- Power Apps for EBS HR self-service portal

## Amazon / AWS
- EBS HRMS data warehouse on Amazon Redshift
- AWS Lambda for EBS HR event-driven integrations
- Amazon WorkMail integration with EBS HRMS

## Apple
- HR data privacy compliance: GDPR, CCPA in EBS HRMS
- Apple Business Manager integration with EBS HRMS user provisioning

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 181 | Employees Earning More Than Managers | Easy | Self JOIN |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK |
| LC 569 | Median Employee Salary | Hard | PERCENTILE_CONT |

## Production Scenarios
- Scenario 1: "Production incident — Payroll run fails mid-processing, partial results committed"
- Scenario 2: "Performance tuning — SSHR page loading slowly due to complex organization hierarchy"
- Scenario 3: "Disaster recovery — Employee data corrupted after bulk data load"
- Scenario 4: "Security breach — Salary data exposed via unauthorized HRMS responsibility"

## Interview Patterns & Tips
- Oracle HRMS interviews cover the complete employee lifecycle from hire to retire
- Payroll element design and formula processing are key technical topics
- OCP EBS HRMS certification: Covers HRMS foundation and payroll
- HRMS consultants: $110K-$160K; Payroll specialists: $120K-$175K
