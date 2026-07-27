# Data Engineering Behavioral Interview Guide

> Behavioral questions specific to Data Engineering roles, with STAR framework responses for pipeline failures, data quality incidents, SLA negotiations, stakeholder management, and scalability challenges.

---

## 1. Data Pipeline Failures

### Common Scenarios
- Pipeline fails mid-run due to source schema change
- Data source goes down mid-extract
- Corrupted data causing transformation errors
- Resource exhaustion (OOM, disk full) on Spark job
- Deadlock in database update
- Network partition during data transfer

### STAR Templates

**Situation:** "Our nightly ETL pipeline failed at 2 AM, and the downstream dashboard would be empty for the morning business review."

**Task:** "Restore the pipeline and ensure data freshness for the 8 AM executive meeting."

**Action:**
1. **Immediate:** Assessed the error log - source database schema changed (column dropped)
2. **Workaround:** Modified the ingestion query to use `SELECT * EXCEPT(dropped_col)` via a hotfix
3. **Root cause:** Implemented schema drift detection with alerting
4. **Prevention:** Added pre-check stage that validates source schema matches expectations
5. **Communication:** Updated stakeholders every 30 minutes on ETA

**Result:** "Pipeline back online by 6 AM, data refreshed before the meeting. Implemented automated schema drift detection that caught 3 subsequent schema changes before they could break the pipeline."

**What interviewers look for:**
- Calm under pressure
- Systematic debugging approach
- Communication with stakeholders
- Long-term prevention mindset

### Additional Pipeline Failure Stories

**When the Spark job OOM'd:**
- **S:** Processing 2TB of clickstream data, job crashed after 3 hours
- **T:** Complete processing within the remaining 4-hour window
- **A:**
  - Analyzed Spark UI: data skew on partition key
  - Implemented salting for the skewed key
  - Increased shuffle partitions from 200 to 2000
  - Switched from `groupBy` to `reduceByKey` + `combineByKey`
  - Applied bucketing for future runs
- **R:** Job completed in 2.5 hours; partition skew eliminated for future runs

---

## 2. Data Quality Incidents

### Common Scenarios
- Duplicate records in data warehouse
- Missing data due to failed CDC capture
- Incorrect aggregation in transformation
- Data type truncation (loss of precision)
- NULL handling errors in business logic
- Off-by-one errors in date filters

### STAR Templates

**Situation:** "We discovered that the quarterly revenue report showed inflated numbers because of double-counting orders from re-synced data."

**Task:** "Identify all affected records, correct the data, and prevent recurrence."

**Action:**
1. **Impact analysis:** Queried to find all affected tables and confirmed the root cause (pipeline retry without dedup)
2. **Correction:** Ran dedup job using `ROW_NUMBER()` over natural keys to remove 47K duplicates
3. **Verification:** Compared pre/post row counts with source system, ran reconciliation report
4. **Root cause fix:** Added idempotency check (upsert instead of insert, transaction monitoring)
5. **Monitoring:** Added data quality metrics: row count diff, revenue sanity check, freshness SLA alerts

**Result:** "Clean data restored within 4 hours. Implemented data quality checks that prevented 3 similar incidents in the next quarter. Established weekly data quality review meetings."

**What interviewers look for:**
- Systematic impact analysis
- Data reconciliation methodology
- Monitoring mindset
- Collaboration with business users

### Data Quality Monitoring Framework
```
Freshness: "Data is no older than X hours"
Volume: "Row count within 10% of expected"
Schema: "Columns match expected types"
Uniqueness: "No duplicate natural keys"
Null ratio: "Critical columns not NULL > 99%"
Referential integrity: "FKs match valid PKs"
Range: "Values within expected bounds"
Distribution: "Statistical distribution hasn't shifted"
```

---

## 3. SLA Negotiations

### Common Scenarios
- Stakeholder wants real-time data on a batch-only system
- Marketing needs data at 7 AM, but source finishes at 7 AM
- Data team asked to support 10x more consumers without resources
- Compliance requires 1-year data retention but storage budget is limited

### STAR Templates

**Situation:** "The analytics team wanted sub-5-minute data freshness for all dashboards, but we had a batch-only infrastructure with hourly refreshes."

**Task:** "Design a solution that meets the most critical needs while being pragmatic about infrastructure."

**Action:**
1. **Prioritization:** Worked with stakeholders to categorize dashboards by latency needs (real-time, hourly, daily)
2. **Tiered SLA proposal:**
   - Tier 1 (5 min): Only 3 critical dashboards (revenue, fraud, operations)
   - Tier 2 (1 hour): 12 operational dashboards
   - Tier 3 (daily): Rest of the 50+ dashboards
3. **Implementation:** Built streaming pipeline for Tier 1 (Kafka + Flink), optimized batch for Tier 2 (every hour), kept Tier 3 on daily schedule
4. **Cost trade-off:** Documented cost difference per tier
5. **Monitoring:** SLA dashboards showing data freshness per tier

**Result:** "85% of dashboard needs met without full infrastructure overhaul. Tier 1 within budget because only 3 dashboards needed real-time. Tier 2 improved from 24h to 1h with minimal cost."

**What interviewers look for:**
- Business prioritization
- Technical trade-off communication
- Data-driven SLA definitions
- Collaboration across teams

### SLA Scenarios to Prepare

**Scenario 1:** "Data must be available at 6 AM but batch window is 4 AM - 8 AM"
- Approach: Prioritize critical tables, incremental processing for long-running tables
- Buffer: 2 hours for retries

**Scenario 2:** "Query performance SLA is 5 seconds, but dataset has 10B rows"
- Approach: Pre-aggregation, materialized views, caching layer
- Partition for query pruning

**Scenario 3:** "Data retention: Keep 7 years for compliance, 90 days for analytics"
- Approach: Hot tier (SSD), warm tier (standard), cold tier (archive/glacier)
- Lifecycle automation

---

## 4. Stakeholder Management

### Common Scenarios
- Conflicting priorities between data science and engineering teams
- Non-technical manager wants unreasonable deadlines
- Data team is a bottleneck for multiple product teams
- Convincing leadership to invest in data quality infrastructure

### STAR Templates

**Situation:** "Three product teams all needed new data pipelines at the same time, but our data team had capacity for only one major project per quarter."

**Task:** "Prioritize work and manage expectations without alienating stakeholders."

**Action:**
1. **Discovery:** Understood each team's needs, timelines, and business impact
2. **Impact matrix:** Ranked by revenue impact, user count, strategic alignment
3. **Transparency:** Shared the prioritization framework with all stakeholders
4. **Capacity modeling:** Showed current utilization and what was realistically achievable
5. **Compromise:** Offered to build self-service tools for the lower-priority teams
6. **Re-evaluation:** Monthly review of priorities

**Result:** "Highest-impact project delivered on time. Self-service tool empowered 2 teams to build their own pipelines (with templates and support). Reduced data team bottleneck by 60%."

**What interviewers look for:**
- Data-driven prioritization
- Transparency and communication
- Empowerment through self-service
- Business acumen

### Stakeholder Types in DE
- **Data Analysts:** Need clean, timely data with documentation
- **Data Scientists:** Need feature-rich, flexible datasets
- **Product Managers:** Need data to measure product KPIs
- **Engineering Teams:** Need data infrastructure to build on
- **Compliance:** Need audit trails, data lineage, retention policies
- **Executives:** Need high-level metrics, dashboards, ROI

---

## 5. Scalability Challenges

### Common Scenarios
- Pipeline designed for 100GB/day now needs 10TB/day
- Spark job that took 1 hour now takes 10 hours as data grew
- Table with 10M rows now has 10B rows - queries time out
- Single-region deployment needs to expand to multi-region

### STAR Templates

**Situation:** "Our Spark ETL pipeline was designed for 500GB of daily data but was now processing 5TB/day. The 2-hour pipeline was taking 14+ hours and missing SLA windows."

**Task:** "Redesign the pipeline to handle 10x scale and still meet the 4-hour SLA window."

**Action:**
1. **Profiling:** Analyzed Spark UI to find bottlenecks
   - Data skew on customer_id partition
   - Too many small files (100K+ files in input)
   - Shuffle spills to disk
2. **Optimizations:**
   - Salting for skewed keys (added random suffix)
   - Input coalesce to reduce file count (128MB file size target)
   - Increased shuffle partitions from 200 to 2000
   - AQE enabled for automatic partition coalescing
   - Broadcast join for dimension tables
3. **Architecture changes:**
   - Incremental processing instead of full refresh
   - Partitioned output by date for faster downstream queries
4. **Testing:** Ran parallel runs on staging with full data volume

**Result:** "Pipeline completed in 2.5 hours on 5TB data (faster than original 2 hours on 500GB). Reduced compute cost by 40% due to efficient resource utilization."

**What interviewers look for:**
- Systematic profiling
- Knowledge of Spark internals
- Cost-consciousness
- Measurement-driven approach

### Scalability Anti-Patterns
| Problem | Symptom | Fix |
|---------|---------|-----|
| Data skew | Some tasks take 10x longer | Salting, range partitioning |
| Small files | Slow metadata operations | Coalesce, auto-compaction |
| Full refresh | Long runs, waste | Incremental processing |
| No partitioning | Full table scans | Partition by date/region |
| Cartesian product | OOM, infinite loops | Verify join conditions |
| Single thread | Under-utilized cluster | Parallelize, partition |

---

## 6. Technical Disagreement

### Common Scenarios
- You believe streaming is wrong for a use case; team wants streaming
- You want to rewrite the pipeline; team says "if it ain't broke"
- Disagreement on data modeling approach (star vs data vault)
- Technology choice debate (Airflow vs Dagster, Spark vs Flink)

### STAR Templates

**Situation:** "The team wanted to use Spark Streaming for a real-time fraud detection pipeline, but I believed Apache Flink was a better fit due to exactly-once semantics and event-time handling."

**Task:** "Make the right technical decision while maintaining team cohesion."

**Action:**
1. **Research:** Built a comparison matrix: latency, state management, exactly-once, watermarking, operational complexity
2. **PoC:** Built a proof-of-concept in both frameworks on the same dataset
3. **Metrics:** Compared throughput, p99 latency, state size, resource usage
4. **Trade-off:** Acknowledged team's comfort with Spark but showed Flink's advantages
5. **Decision:** Team agreed to use Flink for the streaming component, Spark for batch processing
6. **Implementation:** I led Flink training and pair-programming sessions for the team

**Result:** "Flink pipeline achieved 99.99% accuracy for fraud detection with 200ms latency. Team upskilled in Flink. Spark used for daily reconciliation."

**What interviewers look for:**
- Data-driven decision making
- Respect for team's perspective
- PoC and measurable comparison
- Willingness to teach and learn

---

## 7. Leadership and Mentoring

### Common Scenarios
- Junior engineer makes a mistake causing data loss
- Onboarding new team members to complex pipelines
- Building data engineering best practices from scratch
- Leading incident response without authority

### STAR Templates

**Situation:** "A junior engineer accidentally deployed a pipeline with a logic error that corrupted 6 months of aggregated data."

**Task:** "Fix the data, mentor the engineer, and prevent recurrence."

**Action:**
1. **Fix:** Immediately rolled back to known good version, triggered backfill from source
2. **Blameless post-mortem:** Focused on process gaps, not individual blame
3. **Mentoring:** Paired with the engineer to understand their thought process and teach testing strategies
4. **Process improvements:**
   - Added mandatory code review for all pipeline logic
   - Implemented CI/CD with schema validation and row-count tests
   - Created sandbox environment for testing
5. **Documentation:** Wrote "Pipeline Deployment Guide" with checklists

**Result:** "Data restored within 6 hours. Team adopted mandatory code review. Junior engineer grew confident and became a code reviewer within 3 months. Zero similar incidents in 18 months."

---

## 8. Cross-functional Projects

### Common Scenarios
- Working with ML team to build feature engineering pipeline
- Collaborating with DevOps on data infrastructure
- Partnering with finance on cost optimization
- Unifying data from acquired company

### STAR Templates

**Situation:** "Company acquired a startup with a completely different data stack. We needed to unify their data into our warehouse within 3 months."

**Task:** "Lead the data migration and integration without disrupting either company's operations."

**Action:**
1. **Discovery:** Audited their stack (MySQL + PostgreSQL + custom analytics)
2. **Mapping:** Created schema mapping between their system and ours
3. **Migration:** Built parallel pipeline - dual write to old system and new warehouse
4. **Validation:** Reconciliation queries comparing old vs new data
5. **Cutover:** Phased migration (1 team at a time), with rollback plan
6. **Retirement:** After 1 month of dual run, decommissioned old system

**Result:** "Migration completed 2 weeks early. Zero data loss. Dual write detected and fixed 3 schema mapping errors before they reached production."

---

## 9. Common Behavioral Questions by Company

### Amazon LP-focused Questions
1. "Tell me about a time you had to deliver a project under a tight deadline" (Deliver Results)
2. "Describe a data quality issue and how you fixed it" (Insist on Highest Standards)
3. "How did you convince a team to adopt a new technology?" (Have Backbone; Disagree and Commit)
4. "Tell me about a time you went beyond your role" (Ownership)
5. "Describe a time you failed and what you learned" (Learn and Be Curious)

### Meta Behavioral Questions
1. "How did you handle a data pipeline that kept failing?" (Move Fast)
2. "Describe prioritizing between multiple stakeholders" (Focus on Impact)
3. "How did you use data to make a social impact?" (Build Social Value)
4. "Tell me about giving difficult feedback on a data model" (Be Direct and Respect)

### Google Googleyness Questions
1. "How do you handle ambiguity when requirements are unclear?"
2. "Describe a time you had to learn a completely new technology"
3. "How did you influence a decision without authority?"
4. "Tell me about a project that failed and what you learned"

### Snowflake Behavioral Questions
1. "How do you approach performance optimization in a data warehouse?"
2. "Describe a migration project you led"
3. "How do you stay updated with data engineering trends?"
4. "Tell me about a time you had to simplify a complex data problem"

### Databricks Behavioral Questions
1. "How did you optimize a Spark job that was running slowly?"
2. "Describe a time you used the medallion architecture"
3. "How do you ensure data quality in streaming pipelines?"
4. "Tell me about contributing to open source or internal communities"
