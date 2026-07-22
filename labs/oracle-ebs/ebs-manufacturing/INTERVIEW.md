# Interview Questions: EBS Manufacturing

## Oracle-Specific Questions
- Explain the EBS Work in Process (WIP) module: discrete jobs, flow schedules, and repetitive manufacturing.
- How does Material Requirements Planning (MRP) work in EBS? Explain the MRP explosion process from MPS to lower-level demands.
- What is the Bill of Material (BOM) structure: how do you define multi-level BOMs, alternate BOMs, and engineering BOMs?
- Explain Quality (QA) in EBS: collection plans, specifications, and quality results integration with WIP and INV.
- How does shop floor control work in EBS WIP: job release, operation completion, move transactions, and job close?
- What are the key manufacturing tables: WIP_DISCRETE_JOBS, BOM_BILL_OF_MATERIALS, MRP_FORECAST_DATES, QA_PLAN_RESULTS?
- How do you configure cost rollup in EBS Manufacturing?
- Explain the difference between standard costing and actual costing in EBS.

## Google Cloud / Technical
- EBS Manufacturing analytics with Google Looker
- Cloud IoT for factory floor machine integration with EBS WIP
- Vertex AI for EBS demand forecasting

## Microsoft / Azure
- Azure Digital Twins integration with EBS Manufacturing
- Dynamics 365 Supply Chain vs EBS Manufacturing
- Azure IoT Hub for EBS shop floor data collection

## Amazon / AWS
- AWS Panorama for EBS factory computer vision
- Amazon Forecast for EBS manufacturing demand planning
- AWS IoT SiteWise for EBS industrial equipment monitoring

## Apple
- iPad-based shop floor apps consuming EBS manufacturing APIs
- Manufacturing data security for Apple supplier responsibility

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
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN |

## Production Scenarios
- Scenario 1: "Production incident — WIP job completion failing, inventory not being updated"
- Scenario 2: "Performance tuning — MRP explosion taking 12 hours, missing schedule window"
- Scenario 3: "Disaster recovery — BOM structure corrupted after data migration"
- Scenario 4: "Security breach — Unauthorized changes to engineering BOM impacting production"

## Interview Patterns & Tips
- Oracle Manufacturing interviews cover WIP, BOM, MRP, and Costing
- MRP explosion logic and BOM multi-level structuring are frequent topics
- OCP EBS Manufacturing certification covers discrete and process manufacturing
- Manufacturing consultants: $115K-$170K; Solution Architects: $145K-$200K
