# Interview Questions: APEX Interactive Grid

## Oracle-Specific Questions
- What is the difference between an Interactive Grid (IG) and an Interactive Report (IR) in APEX?
- How do you configure columns in an IG — what types of column formats are available?
- Explain master-detail IG implementation: how do you link a master IG to a detail IG?
- How does cell editing work in IG? What are the save options (row-level, cell-level, batch update)?
- How do you implement custom aggregations in IG (sum, average, count, median, custom)?
- What are IG dynamic actions and how do you trigger them on row selection, cell change, or toolbar button click?
- How do you handle large datasets in IG — pagination, virtual scrolling, and server-side processing?
- Explain IG's built-in chart view, control break, and group by features.

## Google Cloud / Technical
- IG data export to Google Sheets for offline analysis
- Google Looker Studio integration with APEX IG data sources
- Cloud Spanner vs APEX IG for real-time data grid analysis

## Microsoft / Azure
- Azure Analysis Services with data sourced from APEX IG
- Power BI integration with APEX IG data exports
- Excel Online integration with APEX IG for spreadsheet-like editing

## Amazon / AWS
- Amazon QuickSight dashboards fed from APEX IG data
- IG data export to S3 for archival and analytics
- AWS Lambda triggers on IG row updates via APEX REST

## Apple
- IG touch gestures for iPad users: swipe, pinch-zoom, long-press
- Numbers export format compatibility with IG data

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | LEFT JOIN |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | LAG / Self JOIN |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK + Filter |
| LC 571 | Find Median Given Frequency | Hard | Window + SUM OVER |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN + Aggregate |
| LC 615 | Average Salary | Hard | CASE + AVG |
| LC 618 | Students Report | Hard | PIVOT |
| LC 1097 | Game Play Analysis V | Hard | Window + LEAD |

## Production Scenarios
- Scenario 1: "Production incident — IG not saving edited rows, showing 'Session state protection violation'"
- Scenario 2: "Performance tuning — IG loading 100K rows freezes browser"
- Scenario 3: "Disaster recovery — IG configuration lost after application export/import"
- Scenario 4: "Security breach — IG exposing columns that should be hidden via authorization"

## Interview Patterns & Tips
- Oracle interviews evaluate IG vs IR decision-making skills
- Master-detail IG implementation is a common whiteboard exercise
- Expect questions on IG performance with large datasets
- IG expertise is key for OCP APEX certification
- Salary: $125K-$180K for APEX developers with IG specialization
