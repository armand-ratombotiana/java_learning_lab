# Interview Questions: APEX Security

## Oracle-Specific Questions
- Explain APEX authentication schemes: how do you implement custom authentication vs LDAP/SSO/OAuth2?
- What is authorization in APEX and how do you implement role-based access control using authorization schemes?
- How does APEX session state protection prevent URL tampering and cross-site request forgery?
- Describe SQL injection prevention in APEX: what is Bind Variable usage and how does `V()` and `NV()` functions help?
- Explain CSRF protection in APEX: how does the checksum mechanism work?
- What is XSS prevention in APEX: how does the `SYS.APEX_ESCAPE` function and `HTF.ESCAPE_SC` work?
- How do you implement Virtual Private Database (VPD) with APEX for multi-tenant data isolation?
- Explain APEX Access Control and how to implement ACL for application-level security.

## Google Cloud / Technical
- OAuth2 with Google Identity Platform for APEX authentication
- Cloud IAM integration with APEX authorization schemes
- Security Command Center audit logging for APEX on OCI

## Microsoft / Azure
- Azure AD as an OAuth2 provider for APEX applications
- Azure Key Vault for storing APEX credentials and connection strings
- Azure Sentinel integration for APEX security event monitoring

## Amazon / AWS
- AWS Cognito for APEX user authentication
- AWS WAF protecting APEX against SQL injection and XSS
- AWS Secrets Manager for APEX database credentials

## Apple
- Apple Sign-In integration with APEX using OAuth2
- Data minimization and privacy-by-design in APEX applications

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 182 | Duplicate Emails | Easy | GROUP BY + HAVING |
| LC 196 | Delete Duplicate Emails | Easy | Self JOIN + DELETE |
| LC 197 | Rising Temperature | Easy | DATEDIFF JOIN |
| LC 262 | Trips and Users | Hard | JOIN + Filter + CASE |
| LC 570 | Managers with at Least 5 Direct Reports | Medium | Subquery + HAVING |
| LC 585 | Investments in 2016 | Medium | Self JOIN + Subquery |
| LC 608 | Tree Node | Medium | CASE + Subquery |
| LC 615 | Average Salary | Hard | CASE + AVG |
| LC 626 | Exchange Seats | Medium | CASE + LEAD/LAG |

## Production Scenarios
- Scenario 1: "Production incident — SQL injection vulnerability discovered via APEX URL parameter"
- Scenario 2: "Performance tuning — Authorization scheme calling slow PL/SQL function on every page render"
- Scenario 3: "Disaster recovery — Authentication scheme failure locks all users out of application"
- Scenario 4: "Security breach — Session hijacking through exposed session ID in URL"

## Interview Patterns & Tips
- Oracle prioritizes security expertise in APEX interviews
- Expect scenario-based questions about SQL injection and CSRF prevention
- OCP APEX Security certification covers authorization, authentication, and session protection
- Security-focused APEX roles pay $140K-$200K
- Be ready to explain end-to-end APEX security architecture
