# Interview Questions: APEX Getting Started

## Oracle-Specific Questions
- Explain the APEX architecture: how does the APEX engine render a page from the database?
- What is the APEX workspace and how do you manage multiple workspaces in a single instance?
- Describe the page rendering lifecycle: from URL request to HTML response — what processes fire and in what order?
- How does APEX session state management work? Compare with traditional web frameworks.
- What are shared components and how do you use application-level items, lists of values, and authentication schemes?
- Explain the difference between a page, a region, and a component in APEX. How do you choose the right region type?
- What APEX versions have you worked with? What changed between 19.x, 21.x, and 23.x?
- How does APEX handle multi-language support and globalization?

## Google Cloud / Technical
- Deploying APEX on Oracle Cloud Infrastructure (OCI) vs running APEX on Google Cloud SQL with ORDS
- APEX low-code vs Google AppSheet — when would you choose each?
- Using APEX REST APIs to integrate with Google Workspace (Sheets, Calendar, Drive)

## Microsoft / Azure
- Running APEX on Azure Virtual Machines with Oracle Database on Azure
- Azure SQL Database vs Oracle APEX for low-code application development
- Azure Logic Apps integration with APEX RESTful services

## Amazon / AWS
- Hosting APEX on EC2 with RDS Oracle — architecture best practices
- AWS DMS for migrating APEX backend database to RDS Oracle
- APEX application deployment using Amazon S3 for static file storage

## Apple
- Data privacy considerations when building APEX apps that handle PII
- Mobile Safari compatibility with APEX Universal Theme

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | Window Function |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | Self JOIN |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | Window Function |
| LC 262 | Trips and Users | Hard | JOIN + Filter |

## Production Scenarios
- Scenario 1: "Production incident — APEX application returns 'Application not available' after workspace export/import"
- Scenario 2: "Performance tuning — APEX page loading slowly due to inefficient SQL in Interactive Report region"
- Scenario 3: "Disaster recovery — Recovering APEX application from backup when metadata corruption occurs"
- Scenario 4: "Security breach — Unauthorized access to APEX application due to misconfigured authentication scheme"

## Interview Patterns & Tips
- Oracle hiring managers look for hands-on APEX experience with real enterprise applications, not just demo apps
- Oracle interviews focus on understanding the APEX lifecycle deeply — from workspace setup to deployment
- APEX roles at Oracle pay $120K-$180K for Senior Developer, $150K-$220K for Lead/Architect
- Certifications: Oracle APEX Cloud Developer Certified Professional is highly valued
