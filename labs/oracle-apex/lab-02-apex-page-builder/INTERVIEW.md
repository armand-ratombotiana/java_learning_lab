# Interview Questions: APEX Page Builder

## Oracle-Specific Questions
- Walk through the Page Designer interface: Left Pane, Central Pane, Property Editor — what is each used for?
- What is a Dynamic Action and how do you implement AJAX-based partial page refresh without a full page submit?
- Explain APEX processes: On Demand, On Submit, On Load — when does each execute in the page lifecycle?
- What are APEX validations and how do you implement client-side vs server-side validation?
- How do APEX branches work? Compare branching after submit vs branching to a different page conditionally.
- What is a computation in APEX and how does it differ from a process?
- How do you create a master-detail form using APEX Page Builder components?
- Explain the APEX item types: Text Field, Textarea, Select List, Popup LOV, Shuttle — when to use each?

## Google Cloud / Technical
- APEX Page Builder vs Google App Maker (deprecated) — comparing low-code page design tools
- Using APEX Dynamic Actions to build reactive UIs similar to Angular/React patterns
- Cloud Run deployment for APEX page rendering with containerized ORDS

## Microsoft / Azure
- Azure App Services for hosting APEX form applications
- Power Apps vs APEX Page Builder — comparing form creation capabilities
- Azure AD integration with APEX authentication for enterprise SSO

## Amazon / AWS
- APEX page performance on AWS EC2 vs OCI compute shapes
- Using AWS ALB with APEX for sticky sessions and SSL termination
- Amazon CloudFront CDN for static APEX assets

## Apple
- APEX responsive design principles for iOS Safari
- Touch-friendly form design with Universal Theme mobile templates

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 181 | Employees Earning More Than Managers | Easy | Self JOIN |
| LC 182 | Duplicate Emails | Easy | GROUP BY |
| LC 183 | Customers Who Never Order | Easy | LEFT JOIN |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 196 | Delete Duplicate Emails | Easy | Self JOIN |
| LC 197 | Rising Temperature | Easy | DATEDIFF JOIN |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN + Aggregate |
| LC 626 | Exchange Seats | Medium | CASE + UPDATE |

## Production Scenarios
- Scenario 1: "Production incident — Page rendering partially broken after component library update"
- Scenario 2: "Performance tuning — Dynamic Action firing multiple unnecessary AJAX calls on page load"
- Scenario 3: "Disaster recovery — Lost application page designs after database corruption"
- Scenario 4: "Security breach — SQL injection via improperly bound page item in process"

## Interview Patterns & Tips
- Oracle values form design expertise — demonstrate how you built complex enterprise forms
- Be ready to whiteboard the APEX page rendering lifecycle
- Senior APEX developers earn $130K-$190K; architects earn $170K-$240K
- OCP certification in Oracle APEX includes page builder proficiency
