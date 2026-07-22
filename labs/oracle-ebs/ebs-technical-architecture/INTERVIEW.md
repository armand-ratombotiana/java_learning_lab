# Interview Questions: EBS Technical Architecture

## Oracle-Specific Questions
- Explain the Oracle Application Framework (OAF) architecture: how does BC4J (Business Components for Java) work in EBS?
- How do you create and deploy OAF personalizations and extensions?
- What is the concurrent program architecture? How do you develop and register executable and concurrent programs?
- Explain the EBS Forms architecture: how do Forms services communicate with the database via FNDLIBR?
- What is the Workflow (WF) architecture in EBS? How do workflow engine, item types, and activities interact?
- How does the EBS document sequence architecture work for numbering invoices, POs, and payments?
- Explain the EBS reporting architecture: XML Publisher (BI Publisher), Reports (RWP), and Discoverer.
- How does the EBS attachment framework work: FND_ATTACHED_DOCUMENTS and relationship to JDrives?

## Google Cloud / Technical
- Running EBS OAF extensions on Google Cloud Run
- Cloud SQL for EBS technical architecture migrations
- Google Pub/Sub for EBS workflow event integration

## Microsoft / Azure
- Azure DevOps CI/CD for EBS CEMLI deployments
- Azure App Service for EBS OAF extension hosting
- Azure Functions for EBS workflow event processing

## Amazon / AWS
- AWS CodePipeline for EBS customization deployment
- EBS OAF on AWS Elastic Beanstalk
- Amazon SQS for EBS concurrent manager event queue

## Apple
- EBS mobile architecture: iOS apps using OAF mobile extensions
- Mac-based EBS Forms development environment setup

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

## Production Scenarios
- Scenario 1: "Production incident — OAF page throwing 'BC4J Exception: JBO-27000' after patch"
- Scenario 2: "Performance tuning — Concurrent program waiting for 'Child' lock for hours"
- Scenario 3: "Disaster recovery — Workflow engine corrupted after incomplete upgrade"
- Scenario 4: "Security breach — OAF personalizations exposing hidden fields to users"

## Interview Patterns & Tips
- EBS Technical Architecture interviews focus on OAF, BC4J, concurrent programs, and Forms
- Expect whiteboarding of the EBS technology stack and how components communicate
- OCP EBS Technical certification covers OAF, Forms, and concurrent processing
- EBS Technical Architects: $140K-$200K; Lead Developers: $120K-$170K
