# Interview Questions: APEX RESTful APIs

## Oracle-Specific Questions
- How do you create RESTful Services in APEX using ORDS? Explain resource templates, handlers, and URI patterns.
- What pagination strategies are available for ORDS REST APIs? Explain `_page`, `_size`, `_offset` parameters.
- How do you implement OAuth2 authentication for ORDS services? Describe client credentials and authorization code flows.
- Explain the `APEX_WEB_SERVICE` package — how do you consume external REST APIs from APEX?
- How does `APEX_JSON` help in parsing and generating JSON in PL/SQL?
- What are ORDS AutoREST and how do you expose a table or view as a REST endpoint?
- How do you secure ORDS endpoints with privilege definitions and roles?
- Compare ORDS REST vs APEX RESTful Services — when would you use each?

## Google Cloud / Technical
- APEX REST APIs consumed by Google Cloud Functions for serverless processing
- Google Apigee API management for APEX REST services
- Cloud Endpoints for securing ORDS APIs

## Microsoft / Azure
- Azure API Management for APEX REST API gateway and rate limiting
- Azure Logic Apps consuming APEX REST endpoints
- Azure Functions as webhook receivers for APEX events

## Amazon / AWS
- API Gateway proxying to ORDS REST endpoints
- AWS Lambda functions triggered by APEX REST calls
- AppSync GraphQL wrapping ORDS REST APIs

## Apple
- Building iOS apps that consume APEX REST APIs with URLSession
- Apple's App Transport Security (ATS) compliance for ORDS endpoints

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 181 | Employees Earning More Than Managers | Easy | Self JOIN |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK |
| LC 262 | Trips and Users | Hard | JOIN + Filter |
| LC 569 | Median Employee Salary | Hard | PERCENTILE_CONT |
| LC 571 | Find Median Given Frequency | Hard | Window + SUM |

## Production Scenarios
- Scenario 1: "Production incident — ORDS REST API returning 503 Service Unavailable under load"
- Scenario 2: "Performance tuning — REST API endpoint taking 30 seconds due to N+1 queries"
- Scenario 3: "Disaster recovery — ORDS configuration lost after server reboot"
- Scenario 4: "Security breach — OAuth2 token leaked in REST API response headers"

## Interview Patterns & Tips
- Oracle REST API interviews focus on ORDS architecture and security
- Be ready to design a REST API using ORDS with pagination and filtering
- OCP certification includes ORDS and RESTful Services module
- REST API developer roles at Oracle: $130K-$195K
- Expect questions on JSON parsing performance in PL/SQL
