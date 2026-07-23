# Google Interview Guide — Oracle Database Academy

## Interview Process for Database Roles
- **Rounds**: 5–7 rounds: Recruiter screen → Technical phone screen (45 min, SQL + distributed systems) → On-site (4–5 rounds): 2x SQL/coding, 1x system design, 1x Googleyness/leadership, 1x data modeling or migrations.
- **Timeline**: 6–10 weeks. Google is methodical — each round is evaluated independently by separate hiring committees.
- **DB-specific expectations**: Google focuses on distributed database fundamentals — Spanner, Bigtable, Cloud SQL, BigQuery. Expect deep questions about consistency (external consistency, TrueTime), replication (Paxos/Raft), partitioning (range vs hash), and query optimization at planetary scale. SQL questions often involve complex analytics with large datasets. You must demonstrate algorithmic thinking in SQL.

## Top SQL Problems by Pattern (5-6 patterns, 2-3 problems each)

### Pattern: Window Functions
#### Problem: (LC SQL 185) Department Top Three Salaries
- **Difficulty/Frequency**: Hard / Very High at Google
- **Problem**: Find employees earning the top three salaries in each department, including ties.
- **Interview walkthrough**: Use DENSE_RANK() partitioned by departmentId, ordered by salary DESC. Filter rank <= 3. Join with Department. Google expects window function mastery and consideration for tie-breaking.
- **SQL solution**:
  ```sql
  SELECT d.name AS department, e.name AS employee, e.salary
  FROM (
    SELECT departmentId, name, salary,
           DENSE_RANK() OVER (PARTITION BY departmentId ORDER BY salary DESC) AS dr
    FROM employee
  ) e
  JOIN department d ON e.departmentId = d.id
  WHERE e.dr <= 3
  ORDER BY d.name, e.salary DESC;
  ```
- **What Google evaluates**: Window function fluency; handling partitions correctly; understanding DENSE_RANK vs RANK vs ROW_NUMBER.
- **Follow-ups**: How would this query be executed in a distributed database like Spanner? Where does the sorting happen? How does data partitioning affect the result?

#### Problem: Game Play Analysis IV (LC SQL 550)
- **Difficulty/Frequency**: Medium / High at Google
- **Problem**: Find the fraction of players who logged in again the day after their first login, rounded to 2 decimal places.
- **Interview walkthrough**: Find each player's first login date using MIN. Then use LEAD or self-join to check if the next login is date + 1. Count distinct players who satisfy this.
- **SQL solution**:
  ```sql
  WITH first_login AS (
    SELECT player_id, MIN(event_date) AS first_date
    FROM activity
    GROUP BY player_id
  ),
  consecutive AS (
    SELECT fl.player_id
    FROM first_login fl
    JOIN activity a ON fl.player_id = a.player_id AND a.event_date = fl.first_date + 1
  )
  SELECT ROUND(COUNT(DISTINCT c.player_id) / COUNT(DISTINCT fl.player_id), 2) AS fraction
  FROM first_login fl
  LEFT JOIN consecutive c ON fl.player_id = c.player_id;
  ```
- **What Google evaluates**: Stepwise problem decomposition; CTE chaining; date arithmetic; ratio computation.
- **Follow-ups**: How would you compute this using only window functions? What if the activity data is in BigQuery — changes in syntax?

### Pattern: Aggregation and Advanced Grouping
#### Problem: Restaurant Growth (LC SQL 1321)
- **Difficulty/Frequency**: Medium / Very High at Google
- **Problem**: Calculate the 7-day moving average of customer spending.
- **Interview walkthrough**: Self-join on a 7-day range. Group by each date. Compute average. Use aggregate window function for cleaner solution.
- **SQL solution**:
  ```sql
  WITH daily_sum AS (
    SELECT visited_on, SUM(amount) AS amount
    FROM customer
    GROUP BY visited_on
  )
  SELECT visited_on, amount,
         SUM(amount) OVER (ORDER BY visited_on ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) / 7.0 AS average_amount
  FROM daily_sum
  ORDER BY visited_on
  OFFSET 6 ROWS;
  ```
- **What Google evaluates**: Moving window aggregates; self-join alternative; date range handling; edge cases (insufficient data).
- **Follow-ups**: How would this execute in BigQuery? What if there are missing dates? How to handle leap years or variable-width windows?

#### Problem: Find Median Given Frequency of Numbers (LC SQL 571)
- **Difficulty/Frequency**: Hard / Very High at Google
- **Problem**: Find median from a table with number-frequency pairs.
- **Interview walkthrough**: Compute ascending and descending cumulative sums. Find where both sums cross the halfway point. Average the qualifying numbers.
- **SQL solution**:
  ```sql
  WITH freq AS (
    SELECT num, frequency,
           SUM(frequency) OVER (ORDER BY num) AS asc_sum,
           SUM(frequency) OVER (ORDER BY num DESC) AS desc_sum
    FROM numbers
  ),
  total AS (
    SELECT SUM(frequency) AS total_freq FROM numbers
  )
  SELECT AVG(num) AS median
  FROM freq, total
  WHERE asc_sum >= total_freq / 2.0
    AND desc_sum >= total_freq / 2.0;
  ```
- **What Google evaluates**: Understanding of median in frequency distributions; multi-pass window functions; set-cross join for global aggregates.
- **Follow-ups**: How would you implement this in a distributed system? What if the frequency data is in the billions?

### Pattern: Subqueries and Complex Joins
#### Problem: Capital Gain/Loss (LC SQL 1393)
- **Difficulty/Frequency**: Medium / High at Google
- **Problem**: Calculate capital gain/loss for each stock. Buy and sell operations are in the same table.
- **Interview walkthrough**: Use CASE to classify buy vs sell. Compute running sum partitioned by stock. Alternative: group by stock and sum with CASE.
- **SQL solution**:
  ```sql
  SELECT stock_name,
         SUM(CASE WHEN operation = 'Sell' THEN price ELSE -price END) AS capital_gain_loss
  FROM stocks
  GROUP BY stock_name;
  ```
- **What Google evaluates**: Conditional aggregation within GROUP BY; understanding of financial data modeling.
- **Follow-ups**: How would you detect data anomalies (multiple sells without buys)? How would you compute the realized gain using LAG?

#### Problem: Active Businesses (LC SQL 1126)
- **Difficulty/Frequency**: Medium / High at Google
- **Problem**: Find businesses that have at least 2 occurrences of an event type where the event count is above the average for that event type.
- **Interview walkthrough**: Compute per-event-type averages. Filter events above average. Count such events per business. Keep those with >= 2.
- **SQL solution**:
  ```sql
  WITH event_avg AS (
    SELECT event_type, AVG(occurences) AS avg_occ
    FROM events
    GROUP BY event_type
  ),
  flagged AS (
    SELECT e.business_id, e.event_type
    FROM events e
    JOIN event_avg ea ON e.event_type = ea.event_type
    WHERE e.occurences > ea.avg_occ
  )
  SELECT business_id
  FROM flagged
  GROUP BY business_id
  HAVING COUNT(*) >= 2;
  ```
- **What Google evaluates**: Multi-step aggregation; subqueries with JOIN; HAVING for post-filtering.
- **Follow-ups**: How would you optimize this for a data warehouse with 10B rows? Materialized views?

### Pattern: Recursive CTEs and Hierarchical Data
#### Problem: Friend Requests II (LC SQL 602)
- **Difficulty/Frequency**: Medium / Medium at Google
- **Problem**: Find the person with the most friends. Friend relationships are symmetric (if A accepts B, both are friends).
- **Interview walkthrough**: UNION ALL both directions of friendship. Count per person. Find the max.
- **SQL solution**:
  ```sql
  WITH all_friends AS (
    SELECT requester_id AS id FROM request_accepted
    UNION ALL
    SELECT accepter_id AS id FROM request_accepted
  )
  SELECT id, COUNT(*) AS num
  FROM all_friends
  GROUP BY id
  ORDER BY num DESC
  FETCH FIRST 1 ROW ONLY;
  ```
- **What Google evaluates**: Symmetric relationship handling; UNION ALL vs UNION; TOP-N queries.
- **Follow-ups**: How would you find the top 5 most connected users in a social graph? How does this scale to billions of edges?

#### Problem: Airport Flight Connections (LC Style)
- **Difficulty/Frequency**: Hard / Medium at Google
- **Problem**: Given flight routes between airports, find all reachable airports from a starting airport with at most K stops.
- **Interview walkthrough**: Use recursive CTE starting from the start airport. Join to routes. Track depth and stop when exceeding K.
- **SQL solution**:
  ```sql
  WITH reachable(dest, depth) AS (
    SELECT dest_airport, 1
    FROM routes
    WHERE src_airport = 'JFK'
    UNION ALL
    SELECT r.dest_airport, rch.depth + 1
    FROM reachable rch
    JOIN routes r ON rch.dest = r.src_airport
    WHERE rch.depth < 3
  )
  SELECT DISTINCT dest
  FROM reachable;
  ```
- **What Google evaluates**: Recursive traversal control; cycle prevention; depth-bounded graph queries.
- **Follow-ups**: How would you prevent cycles without infinite recursion? Compare with Oracle CONNECT BY NOCYCLE.

### Pattern: Data Cleaning and Transformation
#### Problem: Find the Start and End Number of Continuous Ranges (LC SQL 1285)
- **Difficulty/Frequency**: Medium / High at Google
- **Problem**: Given a table with log IDs, find the start and end of each consecutive ID range.
- **Interview walkthrough**: Use ROW_NUMBER to create groups. Group ID minus row number is constant within a consecutive range.
- **SQL solution**:
  ```sql
  WITH numbered AS (
    SELECT log_id,
           log_id - ROW_NUMBER() OVER (ORDER BY log_id) AS grp
    FROM logs
  )
  SELECT MIN(log_id) AS start_id, MAX(log_id) AS end_id
  FROM numbered
  GROUP BY grp
  ORDER BY start_id;
  ```
- **What Google evaluates**: Gaps-and-islands pattern; ROW_NUMBER utility; GROUP BY on computed column.
- **Follow-ups**: What if the data has duplicates? How would this work in a sharded database?

## Database Architecture Deep Dive
- **Google Cloud SQL**: Fully managed MySQL, PostgreSQL, SQL Server. High availability via regional persistence (zonal vs regional). Automated backups, failover replicas, cross-region replication. Cloud SQL Proxy for IAM-based auth. Storage auto-increase. Read replicas for read offloading. Compare to RDS — Cloud SQL is simpler (less tuning knobs), integrates better with GCP ecosystem (BigQuery, Dataflow, Cloud Run).
- **Cloud Spanner**: Globally distributed, strongly consistent, horizontally scalable relational database. Synchronous replication via Paxos. External consistency (global timestamps) using TrueTime. Interleaved tables for parent-child access locality. Change streams for CDC. Query via GoogleSQL (dialect 2) or PostgreSQL interface. NoSQL-style schema design with primary-key-based splits. Discuss hot-spotting and key design strategies.
- **BigQuery**: Serverless data warehouse. Columnar storage (Capacitor). Separated compute and storage (Colossus). Dremel query engine with tree architecture. BI Engine for acceleration. Clustering and partitioning for pruning. Materialized views. BigQuery Omni (multi-cloud). Compare to Oracle Exadata — BigQuery scales infinitely without manual tuning but lacks transactional guarantees (ACID limited to query-scope).
- **Cloud Bigtable**: NoSQL wide-column database based on HBase. For low-latency, high-throughput data (ad tech, IoT, time series). Single-row transactions. No multi-row ACID. Served via Cloud Bigtable client libraries. Compare to HBase and DynamoDB.
- **Memorystore**: Redis/Memcached for caching. Use for session caching, game leaderboards, real-time data. Discuss cache-aside, write-through patterns, and cache invalidation strategies.

## System Design (2-3 questions)
1. **Design Google Photos database for 1 billion users**
   - Database: Cloud Spanner for user metadata (user_id, albums, sharing). Multicast TrueTime for global consistency. Hash primary key on user_id to avoid hot-spotting. Bigtable for photo metadata (tags, EXIF) — high write throughput for photo uploads. BigQuery for analytics (popular tags, object detection metadata). Interleaved tables in Spanner for photos under each user for locality. Discuss consistency implications.

2. **Design a real-time Google Analytics query engine**
   - Database: Cloud Spanner for event metadata (account configurations, custom dimensions). BigQuery for event data ingestion via streaming inserts. Raw events in Bigtable with row key (account_id + reverse_timestamp). Use BigQuery slot reservations for query SLAs. Flink/Dataflow for stream processing. Discuss window functions for sessionization, deduplication of events, approximate COUNT DISTINCT (HyperLogLog++).

3. **Design a globally distributed payment ledger with external consistency**
   - Database: Cloud Spanner for the ledger with TrueTime-based commit timestamps. Shard by account_id hash. Use Spanner read-write transactions for money movement (atomic debit/credit). Change streams for downstream propagation to reporting (BigQuery). Discuss TrueTime guarantees, clock skew handling, and how external consistency is achieved across continents. Compare with Oracle RAC — Google spanner spans global WAN, RAC spans local datacenter.

## Behavioral (5 STAR answers)
1. **S — Spanner query latency spikes during peak traffic (Situation)**
   - T: A critical user-facing dashboard query on Spanner degraded from 50ms to 10s during peak hours, causing SLO violations.
   - A: Analyzed query execution plan, discovered full-table scan due to lack of a secondary index. Created a composite covering index. Also identified read-stall due to split overload — added a secondary split policy. Used Cloud Monitoring to set up proactive alerts.
   - R: Query latency returned to <30ms. SLO compliance reached 99.99%. Team adopted proactive capacity planning.

2. **S — Data migration from Oracle on-prem to Cloud Spanner (Situation)**
   - T: A financial application with 500 tables needed migration to Spanner for global scale. Schema included sequences, triggers, and stored procedures not supported in Spanner.
   - A: Used Schema Conversion Tool for initial mapping. Rewrote sequences as auto-generated keys (UUID/sequence generator table). Replaced triggers with application-level logic. Used Dataflow for parallel data export/import. Implemented dual-run validation for 2 weeks.
   - R: Successful migration with 30 min cutover window. 99.99% application compatibility. Reduced operational cost by 60%.

3. **S — Data corruption in BigQuery due to pipeline bug (Situation)**
   - T: An ETL pipeline introduced corrupted data into a critical revenue reporting table, affecting quarterly financial statements.
   - A: Used BigQuery time-travel (7-day window) to restore the table to the state before corruption. Wrote a SQL validation script to cross-reference with raw source data. Fixed the ETL pipeline (added schema validation, data quality checks on write).
   - R: Corrected $50M in misstated revenue. Implemented data quality framework now used by 10+ teams.

4. **S — Reducing BigQuery costs for a data science team (Situation)**
   - T: BigQuery costs for a single team were $200K/month. Queries scanned terabytes unnecessarily.
   - A: Implemented cluster and partition standards (date-partitioned tables, clustered on commonly filtered columns). Created a BI Engine reservation for dashboard queries. Set up query cost alerts and recommended performance-optimized SQL patterns (SELECT * eliminated, early filtering).
   - R: Costs reduced to $50K/month. Query performance improved 5x. Team trained on cost-optimized SQL.

5. **S — Cold start issue with Cloud SQL for a new product launch (Situation)**
   - T: Cloud SQL read replicas experienced 30-minute cold starts when scaling from 1 to 10 replicas during a product launch event.
   - A: Switched to Cloud SQL Enterprise Plus (faster replica provisioning). Pre-provisioned replicas and used connection pooling. Implemented Cloud SQL Proxy with IAM auth for connection management. Set up proactive replica scaling based on traffic predictions.
   - R: Replica provisioning reduced to 3 minutes. Launch handled 10x traffic with zero downtime.

### Pattern: BigQuery-Specific Features
#### Problem: Find Most Recent Event per User Using ARRAY_AGG (LC Style)
- **Difficulty/Frequency**: Medium / High at Google
- **Problem**: Find the most recent event for each user from a large event log table in BigQuery.
- **Interview walkthrough**: Use ARRAY_AGG with ORDER BY and LIMIT 1 within the aggregation. In BigQuery, you can ORDER BY within aggregation functions.
- **SQL solution** (BigQuery syntax):
  ```sql
  SELECT user_id,
         ARRAY_AGG(event_type ORDER BY event_timestamp DESC LIMIT 1)[OFFSET(0)] AS last_event,
         ARRAY_AGG(page_url ORDER BY event_timestamp DESC LIMIT 1)[OFFSET(0)] AS last_page
  FROM user_events
  GROUP BY user_id;
  ```
- **What Google evaluates**: BigQuery-specific ARRAY_AGG syntax; understanding of STRUCT and ARRAY types in GoogleSQL; capability to work with semi-structured data.
- **Follow-ups**: How would you extract multiple recent events (last 5) per user? Compare to using ROW_NUMBER approach — which is more efficient in BigQuery?

#### Problem: Unnest Arrays for Product Recommendations (LC Style)
- **Difficulty/Frequency**: Medium / Medium at Google
- **Problem**: Given a table with users and their viewed product IDs (stored as a string array), find the total views per product.
- **Interview walkthrough**: Use UNNEST to explode the array into individual rows. Then GROUP BY product_id. Count rows.
- **SQL solution** (BigQuery syntax):
  ```sql
  SELECT product_id, COUNT(*) AS total_views
  FROM users,
  UNNEST(SPLIT(viewed_product_ids, ',')) AS product_id
  GROUP BY product_id
  ORDER BY total_views DESC;
  ```
- **What Google evaluates**: UNNEST/FLATTEN for array data; lateral join syntax; cross join with comma in FROM clause.
- **Follow-ups**: How does UNNEST perform on large datasets (100M+ arrays)? How would you avoid CROSS JOIN explosion?

### Pattern: Approximate Aggregation at Scale
#### Problem: Approximate Distinct User Count per Day (LC Style)
- **Difficulty/Frequency**: Medium / High at Google
- **Problem**: Find the approximate number of distinct users per day for the last year. Exact count is too expensive.
- **Interview walkthrough**: Use APPROX_COUNT_DISTINCT (HyperLogLog++ algorithm). Group by day.
- **SQL solution** (BigQuery syntax):
  ```sql
  SELECT event_date,
         APPROX_COUNT_DISTINCT(user_id) AS approx_dau
  FROM user_events
  WHERE event_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 365 DAY)
  GROUP BY event_date
  ORDER BY event_date;
  ```
- **What Google evaluates**: Knowledge of approximate algorithms (HyperLogLog++ invented at Google); understanding of when exact counts are unnecessary; BigQuery's APPROX functions family.
- **Follow-ups**: What is the error bound of APPROX_COUNT_DISTINCT? When would you still need exact COUNT(DISTINCT)? What other approximate functions does BigQuery support (APPROX_TOP_COUNT, APPROX_QUANTILES)?

## Database Architecture Deep Dive (Extended)
- **Cloud SQL Deep Dive**: Fully managed MySQL, PostgreSQL, SQL Server. High availability (zonal vs regional). Automated backups (up to 365 days retention). Cross-region replication. Cloud SQL Proxy for IAM-based authentication. Private IP connectivity (VPC). Automated maintenance windows. Performance insights (query insights, execution plans). Limits: maximum 60 TB storage, 624 GB RAM per instance. Compare to RDS — Cloud SQL simpler, more integrated with GCP; fewer instance types than RDS.
- **Cloud Spanner Deep Dive**: Globally distributed relational database with external consistency (strongest consistency level available in a distributed database). TrueTime GPS atomic clocks ensure global commit timestamps. Paxos for synchronous replication. Interleaved tables for parent-child relationships (reduce JOINs). Hot-spotting prevention using hash keys. Secondary indexes (global, local, stored). Change streams for CDC. Spanner SQL (GoogleSQL or PG interface). Partitioned DML for bulk operations (without transaction guarantees). Spanner pricing: compute + storage + data transfer. Compare features to Oracle RAC — RAC requires high-speed interconnect and shared storage; Spanner works over WAN with no shared storage.
- **BigQuery Deep Dive**: Serverless data warehouse. Storage: Capacitor columnar format (high compression). Compute: Dremel query engine with multi-level serving tree (root -> mixers -> leaf nodes). Slot-based pricing (flat-rate reservations vs on-demand). Automatic slot scaling by edition. BI Engine for in-memory acceleration of dashboard queries. BigQuery Omni (run query on AWS/Azure blob storage). Materialized views (automatic incremental refresh). Partitioning by date (daily, monthly, yearly). Clustering (up to 4 columns). Time travel (7-day restore). Stored procedures, UDFs (JS, SQL), user-defined aggregate functions. BigQuery difference from Oracle: no indexes, no table design for OLTP, no transaction support beyond single-statement.

## System Design (Extended — Additional Question)
4. **Design a real-time anomaly detection system using Cloud Spanner and BigQuery**
   - Database: Cloud Spanner for real-time transaction data (payment events, user actions). Write transaction to Spanner, then stream via change streams to Pub/Sub, then to Dataflow for windowed aggregation. Use BigQuery for ML model training (fraud detection model). For real-time anomaly checks, use Spanner read-only transactions with stale reads for latency. Discuss consistency requirements — anomaly detection can tolerate seconds of staleness; payment settlement needs external consistency.

## Behavioral (Extended — 3 Additional STAR Answers)
6. **S — BigQuery query optimization for a ML training pipeline (Situation)**
   - T: A daily ML feature pipeline in BigQuery was running for 8 hours and consuming the full flat-rate slot reservation (2000 slots).
   - A: Analyzed query execution details. Identified that joins were broadcasting large tables (100GB dimension tables). Systematically added clustering on join keys. Replaced expensive self-joins with window functions. Moved to incremental materialized views for daily aggregations.
   - R: Pipeline completed in 45 minutes. Slot utilization dropped to 400 slots. Annual savings of $120K on slot reservations.

7. **S — Managing Spanner schema migrations with zero downtime (Situation)**
   - T: Needed to add a column with a NOT NULL constraint to a production Spanner table serving 50K QPS (queries per second).
   - A: Used Spanner's schema update DDL with IF NOT EXISTS pattern. First added the column as NULLABLE, then backfilled data using partitioned DML, then added the NOT NULL constraint. Ran schema change during low traffic window but Spanner's online DDL handled it without downtime.
   - R: Schema migration completed in 10 minutes with zero impact on production traffic. No downtime or errors.

8. **S — Cloud SQL failover during a regional outage (Situation)**
   - T: A GCP region experienced a 4-hour outage. Cloud SQL primary in us-central1 was down. Application needed failover to a different region.
   - A: Promoted the cross-region read replica in us-west1 to standalone instance. Updated application connection strings to point to the new primary. After region recovery, set up the original primary as a read replica of the new primary. Automated failover testing was added for future.
   - R: Application back online within 30 minutes. Data loss limited to < 5 seconds. Cross-region DR became the standard for all critical databases.

## Study Plan (Extended)
- **Weeks 1–2**: SQL fundamentals — all join types, subqueries (scalar, correlated, EXISTS), set operations (UNION, INTERSECT, EXCEPT), date/time functions (DATE_TRUNC, DATE_ADD, DATE_DIFF, EXTRACT). Practice 25 LeetCode SQL easy problems. Read Google Cloud SQL documentation thoroughly.
- **Weeks 3–4**: Advanced SQL — window functions (ROW_NUMBER, RANK, DENSE_RANK, LAG/LEAD, SUM/AVG OVER with ROWS/RANGE), recursive CTEs, pivot/unpivot, string functions (REGEXP_CONTAINS, REGEXP_EXTRACT, SPLIT), array functions (ARRAY_AGG, ARRAY_CONCAT, UNNEST). Practice 20 LeetCode medium problems. Study BigQuery SQL dialect differences from Oracle (no CONNECT BY, no MATCH_RECOGNIZE, no PIVOT).
- **Weeks 5–6**: Distributed databases — read the Spanner paper (2012, Spanner: TrueTime and External Consistency), BigQuery paper (Dremel: Interactive Analysis of Web-Scale Datasets), Bigtable paper (Bigtable: A Distributed Storage System for Structured Data). Understand CAP theorem, consistency models (strong, eventual, causal), Paxos/Raft basics, TrueTime details.
- **Weeks 7–8**: Cloud SQL, Cloud Spanner, BigQuery deep dives. Practice creating schemas, loading data (50-100GB via Cloud Storage transfer), running queries in the GCP console. Study BigQuery slot reservations, BI Engine accelerated queries, clustered tables, partitioning strategies.
- **Weeks 9–10**: System design with database focus. Practice designing globally distributed databases (Spanner), serverless data warehouses (BigQuery), real-time analytics pipelines (Pub/Sub + Dataflow + BigQuery), multi-model databases. Draw 4 architectures in detail with GCP-specific services.
- **Weeks 11–12**: Behavioral prep — write 10 STAR stories covering different Google attributes: Distributed Database Experience, Big Data Scale Handling, Cross-functional Collaboration, Shipping at Scale, Technical Decision Making, Mentorship. Do mock interviews with peers. Study Googliness scenarios (ethical dilemmas, conflict resolution).
- **Ongoing**: Read Google AI blog, Google Cloud database blog, Google Research blog. Watch Google I/O database sessions (yearly). Practice SQL on LeetCode weekly contests. Study Googliness scenarios. Pursue Google Cloud Database certifications (Professional Data Engineer, Professional Database Engineer).

### Pattern: Date and Time Intelligence
#### Problem: Count Days Spent in Each Trip (LC SQL 1605)
- **Difficulty/Frequency**: Easy / Medium at Google
- **Problem**: Given a table of trip check-in/check-out dates, find the number of days spent on each trip.
- **Interview walkthrough**: Simple date difference between checkout and checkin.
- **SQL solution** (BigQuery syntax):
  ```sql
  SELECT trip_id,
         DATE_DIFF(checkout_date, checkin_date, DAY) AS days_spent
  FROM trips
  ORDER BY days_spent DESC;
  ```
- **What Google evaluates**: DATE_DIFF usage; BigQuery date function knowledge; understanding of date arithmetic.
- **Follow-ups**: How would you handle trips that span across months and years? How would you find the average trip duration per user?

#### Problem: Bi-Weekly Budget Report Comparison (LC Style)
- **Difficulty/Frequency**: Medium / Medium at Google
- **Problem**: Compare this bi-weekly period's spending to the previous bi-weekly period for each department.
- **Interview walkthrough**: Calculate bi-weekly period using DATE_TRUNC and DATE_DIFF. Use LAG to get previous period spending.
- **SQL solution** (BigQuery syntax):
  ```sql
  WITH biweekly AS (
    SELECT department_id,
           DATE_TRUNC(spend_date, WEEK(MONDAY)) AS period_start,
           SUM(amount) AS total_spend
    FROM expenses
    GROUP BY department_id, period_start
  )
  SELECT department_id, period_start, total_spend,
         LAG(total_spend) OVER (PARTITION BY department_id ORDER BY period_start) AS prev_period_spend,
         total_spend - LAG(total_spend) OVER (PARTITION BY department_id ORDER BY period_start) AS difference
  FROM biweekly
  ORDER BY department_id, period_start;
  ```
- **What Google evaluates**: Date bucketing; LAG for period-over-period comparison; partition by dimension.
- **Follow-ups**: How would you compute the percentage change instead of absolute difference? How to handle periods with no spending?

## Tips (Extended)
- **Research papers matter fundamentally**: Read Spanner (2012, 2017 TrueTime and external consistency), BigQuery/Dremel (2010 — Dremel: Interactive Analysis of Web-Scale Datasets, 2020), Bigtable (2006, 2015), FlumeJava, MillWheel, MapReduce papers. Google interviewers love discussing these. Being able to explain TrueTime clock uncertainty (192us, but exposed as [earliest, latest]), external consistency via commit-wait (wait until TT.after(commit_timestamp)), and tree-architecture of Dremel (root server -> mixers -> leaf nodes with local columnar storage) sets you apart from 90% of candidates.
- **SQL dialect flexibility**: Google uses GoogleSQL (formerly F1 Query) for Spanner/BigQuery, which is similar to Oracle but has significant differences. Mention that you adapt to the dialect — Google values learning agility over rote memorization. Know that GoogleSQL supports ARRAY (ordered list), STRUCT (named tuple), PROTO (protocol buffers) types natively — concepts not present in Oracle SQL.
- **No trick questions**: Google interviews are fair — they test fundamentals at depth. If you don't know something, say "I don't know" and explain how you would find the answer. Do not bluff or guess. Intellectual honesty is a core Google value and is explicitly evaluated.
- **Distributed mindset**: Always consider how your SQL would execute on distributed infrastructure. Mention partition pruning (micro-partition metadata in Spanner, clustered column pruning in BigQuery), data locality (interleaved tables in Spanner for parent-child co-location), shuffle vs broadcast joins (BigQuery chooses automatically based on table sizes), data skew handling (hot-spot detection, range partition adjustments).
- **TrueTime discussion**: Be ready to explain how TrueTime exposes clock uncertainty (TT.now() returns [earliest, latest] where earliest <= absolute_time <= latest), how Spanner uses commit-wait to guarantee external consistency (wait until TT.after(commit_timestamp) to ensure all future reads see this write), and the trade-off of increased write latency (typically 2-10ms for commit-wait, compared to < 1ms for standard Oracle writes).
- **BigQuery slot management**: Understand flat-rate vs on-demand pricing (on-demand: up to 2000 slots burst, flat-rate: guaranteed slots). How to measure slot utilization (INFORMATION_SCHEMA.JOBS_BY_PROJECT, Cloud Monitoring dashboards). How to optimize queries to use fewer slots (less shuffle via clustered tables, better partitioning to prune more data, avoiding SELECT * to reduce bytes processed, using LIMIT responsibly, using APPROX_COUNT_DISTINCT instead of exact COUNT(DISTINCT) for large cardinality).
- **Googleyness**: Demonstrate humility (acknowledge what you don't know, and say how you'd learn it), intellectual curiosity (ask thoughtful questions about how Google's systems work — "How does Spanner handle zone failures?"), and ability to collaborate (describe how you included stakeholders in decisions, mentored junior engineers, or resolved technical disagreements).
- **No Oracle bashing**: If asked to compare, discuss Oracle's strengths (mature, feature-rich, robust for enterprise, 40+ years of optimization) but position Google Cloud databases as better suited for modern distributed workloads. Do not badmouth Oracle. Frame it as "Oracle is great for its era, but Spanner/BigQuery are built for a new era of global, internet-scale applications."
- **Algorithms in SQL**: Google sometimes asks algorithmic problems solved in SQL — Fibonacci sequence (recursive CTE), prime numbers (CROSS JOIN generate_series), BFS/DFS tree traversal (recursive CTE with LEFT JOIN). Practice recursive CTEs extensively as these are compatible with both Spanner and BigQuery. Practice converting Oracle CONNECT BY to recursive CTE patterns.
- **Prepare for "What is your experience with X?"**: Be honest. If you haven't used Spanner, say "I've studied the papers and the architecture deeply, and I've built analogous systems using Oracle RAC and custom application-level sharding. I'm eager to apply this knowledge to Spanner." Connect your Oracle experience to analogous concepts — RAC (shared disk, cache fusion) -> Spanner (shared-nothing, Paxos replication), Data Guard (async/sync replication) -> Spanner (Paxos, inter-zone replication), Partitioning (range/hash/list) -> Spanner interleaving and split points.
- **Interview Preparation Checklist**: (1) Read Spanner, BigQuery/Dremel, and Bigtable research papers. (2) Practice 30 LeetCode SQL problems in GoogleSQL/BigQuery dialect. (3) Create a free GCP trial and practice BigQuery queries with public datasets (50-100GB). (4) Study Cloud Spanner schema design best practices (interleaved tables, secondary indexes, key design for hot-spot prevention). (5) Prepare STAR stories for distributed systems, data migration at scale, and Googliness scenarios.

## Additional Behavioral Practice Questions
1. Tell me about a time you designed a globally distributed database system.
2. Describe a situation where you had to optimize a query that was running too long at scale.
3. How have you resolved a technical disagreement with a peer about architecture?
4. Tell me about a time you mentored someone who was struggling with a database concept.
5. Describe a time you had to make a decision with incomplete data and adjust course later.
6. How have you contributed to the broader engineering community (open source, talks, papers)?
7. Tell me about a situation where your database design had to scale to millions of users.
8. Describe a time you identified and fixed a systemic issue rather than a one-time bug.

## Google-Specific Self-Study Questions
1. Explain how Spanner's TrueTime enables external consistency.
2. How does BigQuery's columnar storage (Capacitor) differ from Oracle's row storage?
3. Compare Spanner's Paxos-based replication with Oracle Data Guard's redo transport.
4. How does BigQuery's tree architecture (Dremel) execute distributed queries?
5. Explain Spanner's interleaved tables — when would you use them vs regular JOINs?
6. How does BigQuery clustering differ from Oracle partitioning?
7. What is the role of the commit-wait in Spanner's write path?
8. Compare Cloud Spanner with Oracle RAC for global-scale OLTP workloads.

## Quick Reference — Google Cloud Database Services
- **Cloud SQL**: Managed MySQL, PostgreSQL, SQL Server — best for lift-and-shift migrations, standard OLTP, up to 60TB storage, automated backups, failover replicas
- **Cloud Spanner**: Globally distributed, strongly consistent, SQL relational — for global-scale OLTP with external consistency, automatic sharding, Paxos replication, interleaved tables
- **BigQuery**: Serverless data warehouse — columnar storage (Capacitor), tree-based query engine (Dremel), slot-based pricing, automatic scaling, BI Engine acceleration
- **Cloud Bigtable**: Low-latency NoSQL wide-column — for time-series, IoT, ad-tech, HBase-compatible API, single-row transactions, no multi-row ACID
- **Memorystore**: Managed Redis/Memcached — caching layer, sub-millisecond latency, for session state, leaderboards, real-time data
