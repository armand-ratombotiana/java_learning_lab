# Interview Questions: EBS Financials

## Oracle-Specific Questions
- Explain the EBS General Ledger architecture: how do balancing segments, management segments, and secondary segments work?
- How does Subledger Accounting (SLA) work? What is the difference between SLA and the old XLA architecture?
- Describe the AP invoice lifecycle: from invoice entry, validation, approval to payment.
- How does AR receipt processing work — automatic receipt, receipt on account, and bill receivable?
- What is GL consolidation and how do you transfer balances between ledgers in EBS?
- Explain the Payment Process Request (PPR) flow in EBS R12.2. How does it differ from the old payment batches?
- How do you configure EBS Financials for multi-currency and foreign currency revaluation?
- What are the key tables in EBS Financials: GL_BALANCES, AP_INVOICES_ALL, AR_CASH_RECEIPTS_ALL, XLA_AE_HEADERS?

## Google Cloud / Technical
- EBS Financials reporting with Google Looker via BI Publisher
- Google Workspace integration for AP approval workflows
- Cloud Storage for EBS financial reports archival

## Microsoft / Azure
- Azure SQL Database financial reporting vs EBS GL
- Power BI integration with EBS GL, AP, AR data
- Dynamics 365 Finance vs EBS Financials comparison

## Amazon / AWS
- AWS QuickSight dashboards for EBS financial KPIs
- Amazon S3 for EBS financial report storage
- AWS Lambda for EBS financial data validation

## Apple
- Financial data privacy: Sarbanes-Oxley compliance for APEX/Apple
- Mobile approval workflows for AP invoices on iOS

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | LAG |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK |
| LC 262 | Trips and Users | Hard | JOIN + CASE |
| LC 569 | Median Employee Salary | Hard | PERCENTILE_CONT |
| LC 571 | Find Median Given Frequency | Hard | Window + SUM |
| LC 615 | Average Salary | Hard | CASE + AVG |

## Production Scenarios
- Scenario 1: "Production incident — GL period close fails due to unposted SLA journal entries"
- Scenario 2: "Performance tuning — AP Invoice Validation taking hours for large batches"
- Scenario 3: "Disaster recovery — Corrupted GL_BALANCES after incomplete consolidation"
- Scenario 4: "Security breach — Payment file intercepted during PPR processing"

## Interview Patterns & Tips
- Oracle EBS Financials interviews focus on SLA, GL consolidation, and the AP/AR lifecycle
- Expect scenario-based questions about period-end close and financial reporting
- OCP EBS Financials certification covers GL, AP, AR fundamentals
- EBS Financials consultants: $120K-$180K; managers: $150K-$210K
- Understanding of Oracle SLA architecture is critical for senior roles
