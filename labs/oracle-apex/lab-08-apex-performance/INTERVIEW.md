# Interview Questions: APEX Performance

## Oracle-Specific Questions
- Explain APEX caching strategies: page cache, region cache, and the `APEX_CACHE` package — when to use each?
- How do you optimize SQL queries in APEX? What tuning tools does APEX provide (Monitor Activity, APA)?
- How does APEX handle query optimization? What is the role of bind variables in APEX performance?
- What is the APEX Performance Advisor (APA) and how do you use it to identify slow pages?
- How do you optimize the Universal Theme for faster page loads — CSS/JS minification, template optimization?
- Explain APEX's built-in monitoring: `APEX_WORKSPACE_ACTIVITY_LOG`, `APEX_WORKSPACE_SESSION`, and page views analysis.
- How do you implement lazy loading in APEX using Dynamic Actions and partial page refresh?
- What are the common APEX performance bottlenecks and how do you address them?

## Google Cloud / Technical
- APEX page performance comparison: OCI vs Google Cloud Run
- Cloud CDN for APEX static content delivery
- Cloud Monitoring for APEX application performance metrics

## Microsoft / Azure
- Azure Front Door for APEX application acceleration
- Azure Monitor integration with APEX performance logs
- Azure Redis Cache for APEX session state offloading

## Amazon / AWS
- CloudFront CDN for APEX static assets
- CloudWatch monitoring of APEX application tier
- ElastiCache for APEX session caching

## Apple
- APEX performance on Safari — WebKit-specific optimizations
- iOS APEX web app performance with WKWebView

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | LAG |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK |
| LC 262 | Trips and Users | Hard | JOIN + CASE |
| LC 569 | Median Employee Salary | Hard | PERCENTILE_CONT |
| LC 571 | Find Median Given Frequency | Hard | Window + SUM |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN |
| LC 618 | Students Report | Hard | PIVOT |
| LC 1097 | Game Play Analysis V | Hard | LEAD + Window |

## Production Scenarios
- Scenario 1: "Production incident — APEX page 'Monitor Activity' shows 2000 page views/sec causing DB CPU at 98%"
- Scenario 2: "Performance tuning — APEX application slows down progressively over the day"
- Scenario 3: "Disaster recovery — Cached region returning stale data after schema changes"
- Scenario 4: "Security breach — Slow page loads enabling timing-based side channel attacks"

## Interview Patterns & Tips
- Oracle APEX performance interviews focus on query tuning and caching strategies
- Be ready to analyze a slow page using Monitor Activity and APA
- OCP APEX certification requires deep knowledge of APEX performance tools
- APEX performance architects earn $150K-$220K
- Expect a hands-on exercise: optimize a slow APEX page during the interview
