# Interview Questions: APEX Advanced Worksheets

## Oracle-Specific Questions
- Explain APEX Collections — how do you create, populate, and use collections for shopping carts or temporary data?
- What are APEX caching strategies? Compare page cache, region cache, and result cache.
- How do you manage credentials in APEX? What is the `APEX_CREDENTIAL` API and how do you use it for REST calls?
- How does APEX send email? Explain `APEX_MAIL` package configuration and SMTP server setup.
- How do you generate PDFs from APEX — compare BI Publisher, Apache FOP, and APEX Office Print (AOP)?
- How do you create ZIP files in PL/SQL using `APEX_ZIP` package for bulk file downloads?
- Explain APEX JET Charts — how do you create interactive charts and customize them?
- What are APEX Plugins? How do you create and use custom plugins for reusable components?

## Google Cloud / Technical
- APEX mail integration with Google Workspace SMTP relay
- Google Cloud Print alternatives for APEX PDF generation
- Cloud Storage for APEX file uploads and downloads

## Microsoft / Azure
- Azure Communication Services email integration with APEX
- SharePoint integration for APEX document storage
- Office 365 SMTP relay for APEX mail

## Amazon / AWS
- Amazon SES for APEX email delivery at scale
- S3 presigned URLs for APEX file downloads
- Lambda@Edge for APEX PDF generation offloading

## Apple
- AirPrint compatibility with APEX printing features
- iCloud integration for APEX file storage

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | LAG |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK |
| LC 262 | Trips and Users | Hard | JOIN + CASE |
| LC 569 | Median Employee Salary | Hard | PERCENTILE_CONT |
| LC 571 | Find Median Given Frequency | Hard | Window + SUM |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN |
| LC 618 | Students Report | Hard | PIVOT |

## Production Scenarios
- Scenario 1: "Production incident — APEX_MAIL queue backing up, emails not sending for 6 hours"
- Scenario 2: "Performance tuning — APEX JET Chart query taking 45 seconds to render dashboard"
- Scenario 3: "Disaster recovery — Collection data lost after session timeout for in-progress orders"
- Scenario 4: "Security breach — Cached report data contains PII accessible by unauthorized users"

## Interview Patterns & Tips
- Oracle values experience with APEX advanced features like collections, caching, and mail
- Expect questions about file handling (ZIP, PDF) and how to process large files in APEX
- Plugin development expertise is a differentiator
- Senior APEX roles with advanced feature expertise: $135K-$200K
