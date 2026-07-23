# Amazon Interview Guide — Oracle Database Academy

## Interview Process for Database Roles
- **Rounds**: 6–8 rounds: Recruiter screen → Online assessment (SQL + data modeling) → 2x phone screens (SQL + system design) → On-site loop (4–5 rounds): 1x bar raiser, 2x SQL/coding, 1x system design, 1x data modeling.
- **Timeline**: 3–6 weeks. Amazon moves fast — expect interview scheduling within 1 week of screen.
- **DB-specific expectations**: Amazon emphasizes large-scale OLTP on RDS/Aurora, DynamoDB vs RDS trade-offs, data modeling for high-traffic systems, and database migration strategies (on-prem to AWS). Leadership principles (bias for action, dive deep) must be demonstrated in every answer. Be ready to discuss RDS Multi-AZ, Aurora auto-scaling, and Redshift for warehousing.

## Top SQL Problems by Pattern (5-6 patterns, 2-3 problems each)

### Pattern: Window Functions
#### Problem: Find Median Given Frequency of Numbers (LC SQL 571)
- **Difficulty/Frequency**: Hard / Very High at Amazon
- **Problem**: Given a table Numbers with columns num and frequency, find the median of all numbers considering frequencies.
- **Interview walkthrough**: Compute cumulative sums forward and backward. The median is the number(s) where the cumulative frequency crosses the midpoint. Use SUM OVER for running totals.
- **SQL solution**:
  ```sql
  WITH cumulative AS (
    SELECT num, frequency,
           SUM(frequency) OVER (ORDER BY num) AS cum_sum_asc,
           SUM(frequency) OVER (ORDER BY num DESC) AS cum_sum_desc
    FROM numbers
  )
  SELECT AVG(num) AS median
  FROM cumulative
  WHERE cum_sum_asc >= (SELECT SUM(frequency) FROM numbers) / 2
    AND cum_sum_desc >= (SELECT SUM(frequency) FROM numbers) / 2;
  ```
- **What Amazon evaluates**: Window functions with ordering; understanding of median calculation; multi-pass aggregation.
- **Follow-ups**: How would you handle even vs odd total frequency? What if the data is distributed across shards?

#### Problem: Market Analysis II (LC SQL 1159)
- **Difficulty/Frequency**: Hard / High at Amazon
- **Problem**: For each seller, find the item they sold second by order date. Null if fewer than 2 sales.
- **Interview walkthrough**: Use ROW_NUMBER() partitioned by seller_id, ordered by order_date. Join back to get item name. Filter rank = 2.
- **SQL solution**:
  ```sql
  WITH ranked_sales AS (
    SELECT seller_id, item_id, order_date,
           ROW_NUMBER() OVER (PARTITION BY seller_id ORDER BY order_date) AS rn
    FROM orders
  )
  SELECT rs.seller_id,
         CASE WHEN rs.rn = 2 THEN i.item_brand ELSE NULL END AS second_sold_item
  FROM ranked_sales rs
  JOIN items i ON rs.item_id = i.item_id
  WHERE rs.rn = 2;
  ```
- **What Amazon evaluates**: ROW_NUMBER logic; conditional output; handling missing rows.
- **Follow-ups**: What if there are ties in order_date? How would you use RANK or DENSE_RANK instead?

### Pattern: Joins and Subqueries
#### Problem: Report Contiguously Date Ranges (LC SQL 1225)
- **Difficulty/Frequency**: Hard / High at Amazon
- **Problem**: Given a table of login dates, find contiguous date ranges per user (all dates are present without gaps within each range).
- **Interview walkthrough**: Use ROW_NUMBER to assign groups. Subtract row number from date — contiguous dates have same difference. Then group by user and that difference.
- **SQL solution**:
  ```sql
  WITH numbered AS (
    SELECT user_id, login_date,
           login_date - ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY login_date) AS grp
    FROM logins
  )
  SELECT user_id,
         MIN(login_date) AS start_date,
         MAX(login_date) AS end_date
  FROM numbered
  GROUP BY user_id, grp
  ORDER BY user_id, start_date;
  ```
- **What Amazon evaluates**: Gaps-and-islands pattern; date arithmetic; window functions with GROUP BY.
- **Follow-ups**: How would you handle leap years or time components? How does this perform on a user table with 10M rows?

#### Problem: Page Recommendations (LC SQL 1917)
- **Difficulty/Frequency**: Medium / High at Amazon
- **Problem**: Recommend pages that a user's friends liked but the user has not liked.
- **Interview walkthrough**: Join users to their friends, then to likes. Exclude pages the user already liked.
- **SQL solution**:
  ```sql
  SELECT DISTINCT l1.user_id, l2.page_id AS recommended_page
  FROM friendship f
  JOIN likes l1 ON f.user1_id = l1.user_id OR f.user2_id = l1.user_id
  JOIN likes l2 ON (CASE WHEN f.user1_id = l1.user_id THEN f.user2_id ELSE f.user1_id END) = l2.user_id
  WHERE l2.page_id NOT IN (
    SELECT page_id FROM likes WHERE user_id = l1.user_id
  );
  ```
- **What Amazon evaluates**: Complex join logic; self-joins with OR conditions; generating recommendations using set theory.
- **Follow-ups**: How would you optimize this for a social network with 100M users? Partitioning strategy?

### Pattern: Aggregate Functions and Grouping
#### Problem: User Activity for the Past 30 Days (LC SQL 1141)
- **Difficulty/Frequency**: Easy / Very High at Amazon
- **Problem**: Find the daily active user count for each day over the last 30 days.
- **Interview walkthrough**: Filter dates within 30-day range. Group by activity_date. Count distinct users.
- **SQL solution**:
  ```sql
  SELECT activity_date AS day, COUNT(DISTINCT user_id) AS active_users
  FROM activity
  WHERE activity_date BETWEEN DATE '2019-07-27' - INTERVAL '29' DAY AND '2019-07-27'
  GROUP BY activity_date;
  ```
- **What Amazon evaluates**: Date filtering; GROUP BY with distinct counts; time-series reporting.
- **Follow-ups**: How would you handle time zones? What index supports this query?

#### Problem: Monthly Transactions II (LC SQL 1205)
- **Difficulty/Frequency**: Medium / High at Amazon
- **Problem**: Find the monthly transaction amounts, separating approved vs chargeback transactions.
- **Interview walkthrough**: UNION approved transactions with chargeback transactions. Pivot the data to show approved/chargeback columns side by side.
- **SQL solution**:
  ```sql
  WITH monthly_data AS (
    SELECT TO_CHAR(trans_date, 'YYYY-MM') AS month, country,
           COUNT(*) AS approved_count, SUM(amount) AS approved_amount,
           0 AS chargeback_count, 0 AS chargeback_amount
    FROM transactions WHERE state = 'approved'
    GROUP BY TO_CHAR(trans_date, 'YYYY-MM'), country
    UNION ALL
    SELECT TO_CHAR(c.trans_date, 'YYYY-MM') AS month, t.country,
           0, 0,
           COUNT(*) AS chargeback_count, SUM(t.amount) AS chargeback_amount
    FROM chargebacks c
    JOIN transactions t ON c.trans_id = t.id
    GROUP BY TO_CHAR(c.trans_date, 'YYYY-MM'), t.country
  )
  SELECT month, country,
         SUM(approved_count) AS approved_count, SUM(approved_amount) AS approved_amount,
         SUM(chargeback_count) AS chargeback_count, SUM(chargeback_amount) AS chargeback_amount
  FROM monthly_data
  GROUP BY month, country
  ORDER BY month;
  ```
- **What Amazon evaluates**: Complex aggregation; UNION of multiple fact sets; pivot-style reporting.
- **Follow-ups**: How would you rewrite using FULL OUTER JOIN? Which is more efficient for large datasets?

### Pattern: Subqueries and CTEs
#### Problem: Product Sales Analysis (LC SQL 1084)
- **Difficulty/Frequency**: Easy / High at Amazon
- **Problem**: Find all products that were sold in the first quarter of 2019 but not in the first quarter of 2020.
- **Interview walkthrough**: Use two subqueries or CTEs for each quarter. Anti-join using NOT IN or NOT EXISTS.
- **SQL solution**:
  ```sql
  SELECT DISTINCT p.product_id, p.product_name
  FROM product p
  JOIN sales s ON p.product_id = s.product_id
  WHERE s.sale_date BETWEEN '2019-01-01' AND '2019-03-31'
    AND p.product_id NOT IN (
      SELECT product_id FROM sales
      WHERE sale_date BETWEEN '2020-01-01' AND '2020-03-31'
    );
  ```
- **What Amazon evaluates**: Anti-join pattern; date range handling; business intelligence reporting.
- **Follow-ups**: NOT IN vs NOT EXISTS vs LEFT JOIN ... IS NULL — which is fastest in Oracle? What about NULL handling?

#### Problem: Second Highest Salary (LC SQL 176)
- **Difficulty/Frequency**: Medium / Very High at Amazon
- **Problem**: Write a SQL query to get the second highest salary from the Employee table. If there is no second highest, return null.
- **Interview walkthrough**: Use OFFSET 1 FETCH NEXT 1 ROW ONLY. Wrap in outer query to handle the null case.
- **SQL solution**:
  ```sql
  SELECT (
    SELECT DISTINCT salary
    FROM employee
    ORDER BY salary DESC
    OFFSET 1 ROWS FETCH NEXT 1 ROWS ONLY
  ) AS secondhighestsalary
  FROM dual;
  ```
- **What Amazon evaluates**: OFFSET/FETCH syntax; handling edge cases (no second salary); Oracle-specific dual table.
- **Follow-ups**: Rewrite using MAX() with a subquery. Which approach is more portable across SQL dialects?

### Pattern: Recursive CTEs and Hierarchical Queries
#### Problem: Number of Comments Per Post (LC SQL 1241)
- **Difficulty/Frequency**: Easy / Medium at Amazon
- **Problem**: Count comments per post, including child comments (replies to comments). Each row has a parent_id or NULL for top-level.
- **Interview walkthrough**: Recursively find all sub_ids under each post. Group by the root post id.
- **SQL solution**:
  ```sql
  WITH recursive all_comments AS (
    SELECT sub_id AS root_id, sub_id
    FROM submissions WHERE parent_id IS NULL
    UNION ALL
    SELECT ac.root_id, s.sub_id
    FROM all_comments ac
    JOIN submissions s ON ac.sub_id = s.parent_id
  )
  SELECT root_id AS post_id, COUNT(*) - 1 AS number_of_comments
  FROM all_comments
  GROUP BY root_id
  ORDER BY post_id;
  ```
- **What Amazon evaluates**: Recursive CTE for hierarchical data; root determination; self-referential tables.
- **Follow-ups**: How would this scale for a Reddit-style application with millions of nested comments? Alternative approaches?

## Database Architecture Deep Dive
- **Amazon RDS**: Multi-AZ deployments for high availability (synchronous standby in another AZ). Read replicas for read scaling (up to 5). Automated backups with point-in-time recovery (PITR) retention up to 35 days. Maintenance windows, automated patching, storage auto-scaling. Limitations — no SSH access to DB host, limited parameter group control compared to self-managed Oracle.
- **Amazon Aurora**: MySQL/PostgreSQL-compatible, not Oracle-compatible (DBA knowledge still converts). 6 copies of data across 3 AZs. Storage auto-scaling up to 128 TB. Aurora Replicas (up to 15) with sub-10ms replica lag. Fast database cloning for dev/test. Backtrack for point-in-time recovery without restoring. Compare Aurora vs RDS Oracle — Aurora is half the cost with better throughput but no native Oracle features (RAC, Data Guard, flashback queries).
- **DynamoDB vs RDS**: DynamoDB is NoSQL — key-value and document. Serverless, single-digit-millisecond latency at any scale. Auto-scaling throughput. Use DynamoDB for session state, shopping carts, user profiles. Use RDS for complex queries, joins, ACID transactions, reporting. Discuss migration trade-offs.
- **Redshift**: Columnar storage, massively parallel (MPP), leader-node + compute-node architecture. Dist keys and sort keys. Data compression. Spectrum for querying S3 directly. Workload management (WLM) queues. Compare Redshift vs Snowflake vs Oracle Exadata.
- **Database Migration Service (DMS)**: Homogeneous (Oracle to RDS Oracle) and heterogeneous (Oracle to Aurora). Ongoing replication using Oracle LogMiner or Direct Connect. Schema conversion using SCT. Limitations — LOB handling, unsupported data types.
- **RDS Proxy**: Connection pooling for serverless applications (Lambda). Reduces open connections to database by reusing connections. IAM authentication, TLS encryption.

## System Design (2-3 questions)
1. **Design the Amazon order database (OLTP) for 1 billion products, 300 million users, 10 million orders/day**
   - Database: RDS Multi-AZ with read replicas for product catalog queries. Shard by customer_id across multiple database clusters. Use DynamoDB for shopping cart (high throughput, low latency). Aurora for order history with auto-scaling storage. ElastiCache Redis for product inventory hot data. Discuss consistency trade-offs (eventual vs strong for inventory).

2. **Design a real-time fraud detection system using SQL and streaming**
   - Database: RDS for rules engine configuration. Kinesis for streaming transactions. Redshift for historical pattern analysis. Use window functions in SQL to flag transactions exceeding rolling averages per user. Discuss Lambda architecture (batch + speed layer) for fraud scoring.

3. **Design a data warehouse for Amazon Retail (10 PB) migrating from Oracle Exadata to Redshift**
   - Schema design: Star schema with fact tables for orders, shipments, returns. Dist keys on customer_id, sort keys on date. Use Redshift Spectrum for querying S3 raw logs. Compression (AZ64, ZSTD). Materialized views for common aggregations. Concurrency scaling for spiky workloads. Compare columnar storage vs Oracle row storage impact on query performance.

## Behavioral (5 STAR answers)
1. **S — Prime Day traffic spike caused database slowdown (Situation)**
   - T: During Prime Day 2022, a product catalog query degraded from 50ms to 5s under 10x traffic. Customers saw loading errors.
   - A: Ran performance insights, identified an inefficient JOIN missing an index. Created a composite index on (category_id, last_updated, status). Also implemented RDS Proxy to reduce connection storms and added an Aurora read replica for catalog queries.
   - R: Query latency returned to <60ms, sustained 400K requests/minute. Zero downtime. Documented in post-mortem as a runbook item for future events.

2. **S — Database migration from Oracle on-prem to Aurora PostgreSQL (Situation)**
   - T: A legacy Oracle 11g database needed migration to cloud. Schema had 500+ tables, 200 stored procedures, 30 triggers. No downtime allowed.
   - A: Used AWS SCT for schema conversion (rewrote PL/SQL to PL/pgSQL). AWS DMS with ongoing replication via LogMiner. Created a fallback plan using Oracle GoldenGate. Ran parallel testing for 4 weeks comparing query results.
   - R: Migration completed in 6 hours of cutover window. Zero data loss. 40% cost reduction. Application latency improved 25% due to Aurora's distributed storage.

3. **S — Data integrity issue in order processing pipeline (Situation)**
   - T: Duplicate orders were being recorded in RDS due to a bug in the service layer that retried failed API calls without idempotency.
   - A: Designed a database-level idempotency table using a unique constraint on (order_idempotency_key, customer_id). Added an ON CONFLICT DO NOTHING (PostgreSQL) pattern. Wrote a SQL reconciliation script to clean 50K existing duplicates.
   - R: Zero duplicate orders for 6 consecutive months. Feature adopted across 3 other teams.

4. **S — IAM database authentication rollout for compliance (Situation)**
   - T: Security audit required passwordless database authentication for all production databases. 150+ database instances needed migration.
   - A: Implemented RDS IAM database authentication. Created automation scripts using AWS CLI and Secrets Manager rotation. Wrote internal documentation and trained 20 teams on connection string updates.
   - R: Achieved compliance within 2 months. Eliminated hardcoded passwords. Reduced credential rotation time from 2 days to automated 0-day.

5. **S — Redshift query performance degradation after data growth (Situation)**
   - T: A dashboard query on Redshift that took 2 seconds started taking 15 minutes as the fact table grew to 50B rows.
   - A: Analyzed EXPLAIN plans, identified poor distribution (all nodes but data hash distribution misaligned). Rebuilt table with proper dist key (customer_id), sort key (transaction_date). Applied compression encoding (AZ64). Used materialized views for the specific dashboard aggregation.
   - R: Query returned to sub-second execution. Reduced Redshift cluster size by 40% due to better data distribution.

### Pattern: Performance Optimization in SQL
#### Problem: Find the Top 5 Most Expensive Orders with Customer Details (LC Style)
- **Difficulty/Frequency**: Medium / High at Amazon
- **Problem**: Retrieve the top 5 orders by total amount, including customer name. Optimize for a table with 10M+ orders.
- **Interview walkthrough**: Create an index on (total_amount DESC, order_id). Use subquery to reduce rows early. Join only on the filtered set.
- **SQL solution**:
  ```sql
  SELECT c.customer_name, o.order_id, o.total_amount
  FROM (
    SELECT order_id, customer_id, total_amount
    FROM orders
    ORDER BY total_amount DESC
    OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY
  ) o
  JOIN customers c ON o.customer_id = c.customer_id
  ORDER BY o.total_amount DESC;
  ```
- **What Amazon evaluates**: Optimization mindset — push filtering before join; understanding of indexes; execution plan reading.
- **Follow-ups**: What index would you create? How does the execution plan change with a large dataset? What if customers is also very large?

#### Problem: Find Products with Inventory Below Reorder Level (LC Style)
- **Difficulty/Frequency**: Easy / High at Amazon
- **Problem**: List products where current stock is below the reorder threshold. Include supplier info for automated reordering.
- **Interview walkthrough**: Simple WHERE filter with JOIN to suppliers. Consider index on (stock_quantity, reorder_level).
- **SQL solution**:
  ```sql
  SELECT p.product_id, p.product_name, p.stock_quantity, p.reorder_level, s.supplier_name, s.contact_email
  FROM products p
  JOIN suppliers s ON p.supplier_id = s.supplier_id
  WHERE p.stock_quantity < p.reorder_level
  ORDER BY p.stock_quantity ASC;
  ```
- **What Amazon evaluates**: Basic filtering with business logic; multi-table join; ordering for prioritization.
- **Follow-ups**: How would you automate reorder emails from this query? How to add lead time considerations?

### Pattern: Time-Series and Date-Based Analytics
#### Problem: Monthly Recurring Revenue Calculation (LC Style)
- **Difficulty/Frequency**: Hard / Very High at Amazon (subscription business)
- **Problem**: Calculate monthly recurring revenue (MRR) from a subscriptions table with start_date, end_date, and monthly_price. Subscriptions may be ongoing (end_date NULL).
- **Interview walkthrough**: Generate all months between subscription start and end (or current date). Join a calendar table or use recursive CTE to generate months. Multiply monthly_price by months active per customer.
- **SQL solution**:
  ```sql
  WITH months AS (
    SELECT DATE '2023-01-01' AS month_start
    FROM DUAL
    UNION ALL
    SELECT ADD_MONTHS(month_start, 1)
    FROM months
    WHERE month_start < DATE '2023-12-01'
  )
  SELECT TO_CHAR(m.month_start, 'YYYY-MM') AS month,
         SUM(s.monthly_price) AS mrr
  FROM subscriptions s
  JOIN months m ON m.month_start >= s.start_date
    AND (s.end_date IS NULL OR m.month_start < s.end_date)
  GROUP BY TO_CHAR(m.month_start, 'YYYY-MM')
  ORDER BY month;
  ```
- **What Amazon evaluates**: Date range intersection; recursive CTE for calendar generation; NULL handling for ongoing subscriptions; dollar-value aggregation.
- **Follow-ups**: How would you handle mid-month subscription changes? How to compute churn rate alongside MRR?

## Database Architecture Deep Dive (Extended)
- **Amazon RDS Deep Dive**: RDS manages the database as a service — automated provisioning, OS patching, automated backups (automated snapshots + transaction logs), storage auto-scaling (increase disk automatically when space is low). Multi-AZ deployments create a standby in another AZ with synchronous replication (Oracle uses Data Guard under the hood). Failover is automatic (no DNS changes needed — CNAME is updated). Read replicas use asynchronous replication (up to 5). Performance Insights for database load breakdown. Enhanced Monitoring for OS-level metrics. RDS Custom for Oracle — gives access to the host OS for advanced customization (custom parameter groups, advanced tuning).
- **Aurora Deep Dive**: MySQL/PostgreSQL compatible (not Oracle). Distributed storage system that auto-scales. 6 copies of data across 3 AZs with 4/6 consensus for writes (2 copies needed for writes). Automatic failover without replica lag. Backtrack (rewind to a specific time without restore). Fast database cloning (copy-on-write for dev/test). Aurora auto-scaling (storage grows automatically up to 128 TB). Aurora Serverless for variable workloads. Compare to Oracle RDS — Aurora has higher throughput but lacks Oracle-specific features (PL/SQL, flashback queries, Data Guard flexibility).
- **DynamoDB Deep Dive**: Managed NoSQL key-value/document database. Single-digit-millisecond latency at any scale. Auto-scaling throughput (reads/writes). DAX (DynamoDB Accelerator) for microsecond caching. Global tables for multi-region active-active replication. On-demand capacity for unpredictable workloads. Discuss data modeling for DynamoDB — single table design, composite keys (partition + sort key), secondary indexes (GSI, LSI), eventually consistent vs strongly consistent reads. Compare to RDS — DynamoDB excels at key-value lookups and high-throughput workloads; RDS excels at complex queries and transactions.
- **Redshift Deep Dive**: Columnar storage (compressed, sorted). Leader node + compute node architecture. Distribution styles (key, even, all). Sort keys (compound vs interleaved). Workload management (WLM queues, concurrency scaling). Materialized views. Spectrum (query S3 data directly). Auto-vacuum and auto-analyze. Compare to Snowflake — Redshift is MPP (massively parallel processing) with more manual tuning; Snowflake has automatic clustering and separation of compute/storage.
- **Database Migration Service Deep Dive**: Assessment (SCT for schema conversion, compatibility check). Full load (bulk copy). Ongoing replication (CDC using Oracle LogMiner or Direct Connect). Validation (row count comparison, data checksums). Cutover (stop writes to source, final sync, switch DNS). Rollback plan (reverse replication, switch back). Discuss common pitfalls — LOB handling, unsupported data types, object size limits, character set conversion.

## System Design (Extended — Additional Question)
4. **Design a real-time inventory management system for Amazon warehouses**
   - Database: DynamoDB for real-time inventory counters (product_id + warehouse_id as key, quantity as value). RDS for historical inventory data (for trend analysis and restocking predictions). Redshift for analytics (inventory turnover, stock-out rates). ElastiCache Redis for hot inventory during checkout lock. Discuss consistency model — strongly consistent reads during checkout (to prevent overselling), eventually consistent for catalog display. Timestamp-based reconciliation for distributed inventory updates.

## Behavioral (Extended — 3 Additional STAR Answers)
6. **S — Reducing Oracle RDS costs by 50% (Situation)**
   - T: Monthly RDS Oracle costs for a legacy application were $45K. Utilization was low (average CPU 10%).
   - A: Identified that the DB instance class was overprovisioned. Migrated from db.r5.8xlarge to db.r5.2xlarge. Switched from Multi-AZ to Single-AZ with automated backups (RPO acceptable was 5 minutes). Reserved instance pricing for 3-year term. Implemented auto-stop for non-production instances during weekends.
   - R: Monthly costs reduced to $18K (60% savings). Performance remained within SLA. Business adopted reserved instance calculator for all teams.

7. **S — Aurora global database setup for disaster recovery (Situation)**
   - T: A gaming company needed cross-region disaster recovery with RTO < 5 minutes and RPO < 1 second.
   - A: Implemented Aurora Global Database with primary in us-east-1 and secondary in eu-west-1. Configured failover routing using Route 53 health checks. Set up continuous backup to S3 for long-term retention. Wrote automation scripts for failover testing (monthly drills).
   - R: Achieved 30-second RTO and sub-second RPO during drills. DR testing became automated and predictable. Passed compliance audit.

8. **S — Resolving DynamoDB hot partition issue (Situation)**
   - T: A gaming leaderboard was using timestamp as partition key, causing all writes to one partition. Write throttling occurred during peak.
   - A: Changed the partition key design to use (game_id + hash_of_player_id). Implemented write sharding using a random suffix. Created a GSI for leaderboard queries sorted by score. Used DynamoDB adaptive capacity to automatically handle uneven access patterns.
   - R: Write throughput increased 10x. Hot partition eliminated. Leaderboard queries returned in < 10ms.

## Study Plan (Extended)
- **Weeks 1–2**: SQL fundamentals — SELECT, WHERE, Joins (INNER, LEFT, RIGHT, FULL, CROSS), subqueries (scalar, correlated, EXISTS), set operations. Practice 25 LeetCode SQL easy problems. Study AWS RDS documentation and pricing model. Set up a free tier RDS instance.
- **Weeks 3–4**: Advanced SQL — window functions (SUM, ROW_NUMBER, RANK, LAG/LEAD over various partitions), CTEs, recursive queries, PIVOT. Practice 20 LeetCode medium problems. Build RDS instances and practice querying real datasets (use AWS sample data).
- **Weeks 5–6**: Data modeling and normalization — star schema, snowflake, 3NF, denormalization for analytics. Learn OLTP vs OLAP design trade-offs. Study AWS Database Migration Service (DMS) and Schema Conversion Tool (SCT). Practice a mock migration from Oracle to Aurora.
- **Weeks 7–8**: Cloud database architecture — RDS Multi-AZ (HA), Aurora (distributed storage), Redshift (MPP warehouse), DynamoDB (NoSQL). Learn backup/restore strategies (automated snapshots, manual snapshots, cross-region copies). Understand RDS Proxy, Performance Insights, Enhanced Monitoring. Compare costs across services.
- **Weeks 9–10**: System design practice — design e-commerce database (orders, inventory, catalog), social media feed database, real-time analytics pipeline (Kinesis + Redshift), multi-tenant SaaS database. Draw architecture diagrams and discuss trade-offs. Practice 4 complete design problems with Amazon service selections.
- **Weeks 11–12**: Leadership principles integration. Prepare 2 STAR stories per principle: Customer Obsession, Dive Deep, Deliver Results, Ownership, Bias for Action, Invent and Simplify, Learn and Be Curious. Mock interviews daily with Amazon interviewers (use peers or paid services).
- **Ongoing**: Read AWS Database Blog daily. Practice LeetCode SQL in multiple dialects (Oracle + PostgreSQL). Review AWS re:Invent database sessions annually. Join AWS Database Study Group. Pursue AWS Database Specialty Certification.

### Pattern: Anti-Joins and Set Operations
#### Problem: Find Unmatched Orders (LC Style)
- **Difficulty/Frequency**: Medium / High at Amazon
- **Problem**: Find orders that have no corresponding payment record within 24 hours.
- **Interview walkthrough**: LEFT JOIN orders to payments where payment is NULL. Alternatively NOT EXISTS.
- **SQL solution**:
  ```sql
  SELECT o.order_id, o.order_date, o.total_amount
  FROM orders o
  WHERE NOT EXISTS (
    SELECT 1 FROM payments p
    WHERE p.order_id = o.order_id
      AND p.payment_date BETWEEN o.order_date AND o.order_date + INTERVAL '1' DAY
  )
  ORDER BY o.order_date;
  ```
- **What Amazon evaluates**: Anti-join pattern; date range join condition; understanding of when to use NOT EXISTS vs LEFT JOIN.
- **Follow-ups**: How would you find orders with multiple payments? Orders where payment amount doesn't match order total?

#### Problem: Employees Who Are Not Managers (LC SQL 610)
- **Difficulty/Frequency**: Easy / Medium at Amazon
- **Problem**: Find employees who are not managers (no one reports to them).
- **Interview walkthrough**: LEFT JOIN employee to itself on manager_id. Filter where manager side is NULL.
- **SQL solution**:
  ```sql
  SELECT e.name
  FROM employee e
  LEFT JOIN employee m ON e.id = m.managerId
  WHERE m.id IS NULL;
  ```
- **What Amazon evaluates**: Self-join anti-join; understanding of organizational data structures.
- **Follow-ups**: How would you find employees who are also managers (middle management)? Write using INTERSECT or EXISTS.

## Amazon LP Integration Guide
- **Customer Obsession**: Every system design answer must start with "What does the customer need?" For SQL problems, discuss how query performance impacts end-user experience.
- **Dive Deep**: When answering SQL questions, dive deep into execution plans, index strategies, and cost analysis. Show you understand not just the syntax but the engine behavior.
- **Deliver Results**: Quantify your results in behavioral answers. Use specific metrics — latency improvements (X ms to Y ms), cost savings ($X), uptime improvements (99.X%).
- **Bias for Action**: When faced with an ambiguous SQL problem, start coding a working solution immediately. Optimize later. Show willingness to make decisions with imperfect data.
- **Ownership**: In behavioral questions, use "I" not "we" statements. Take responsibility for both successes and failures. Show that you went above and beyond your role.
- **Invent and Simplify**: Propose creative but simple solutions. For example, use materialized views for dashboard queries instead of building a complex caching layer.

## Tips (Extended)
- **Leadership principles are mandatory**: Every answer must demonstrate one or more LPs. For SQL — show "Dive Deep" by explaining the query plan, statistics, and execution trade-offs. For system design — show "Customer Obsession" by discussing user experience impact and "Deliver Results" by discussing measurable outcomes. For behavioral — clearly label which LP you're demonstrating.
- **Bar raiser round**: This is the most important round. The bar raiser is not evaluating your technical depth — they evaluate whether Amazon should hire you based on leadership principles and bar standards. Be collaborative, acknowledge trade-offs, show willingness to learn. Do NOT get defensive when challenged. The bar raiser has veto power over the entire hiring committee.
- **Know AWS database services thoroughly**: Be ready to compare RDS vs Aurora (features, pricing, limitations), Aurora vs DynamoDB (use cases, consistency models), Redshift vs Athena vs EMR (when to use each), ElastiCache vs DAX (caching layers), DMS vs Database Migration Service. Know pricing models — on-demand vs reserved vs serverless vs spot.
- **Data modeling is tested heavily**: Design a normalized schema for a given use case, then discuss how you'd denormalize for read performance. Amazon interviewers love entities, attributes, relationships discussions. Always mention primary keys, foreign keys, indexes, unique constraints. Use crow's foot notation in whiteboarding.
- **Performance tuning in RDS**: Understand slow query log analysis (log_query_duration), explain plans (EXPLAIN in MySQL/Postgres), index tuning (B-tree, covering, filtered, partial), parameter group tuning (shared_buffers, work_mem, effective_cache_size for Postgres, or SGA_TARGET, PGA_AGGREGATE_TARGET for Oracle RDS). For Oracle RDS — SQL Tuning Advisor via OEM and Automatic Workload Repository (AWR).
- **DMS and migration**: Be ready to discuss a full migration strategy end-to-end: assessment (SCT schema conversion for schema compatibility, manual rewrite needed for unsupported features), full data load (DMS, parallel load, LOB handling), CDC (ongoing replication via Oracle LogMiner, PostgreSQL logical replication, MySQL binlog), cutover (stop source writes, final CDC sync, switch connection string to target), validation (row counts, checksums, sample query comparison), rollback planning (reverse CDC replication).
- **Read replicas and Multi-AZ**: Clearly explain the difference — Multi-AZ is HA (synchronous standby for Oracle RDS Data Guard, automatic failover within region, same endpoint), read replicas are for read scaling (asynchronous replication, different endpoint, can be cross-region). Aurora replicas serve both HA (fast failover with reader promotion — sub-30 second failover) and read scaling (up to 15 low-lag replicas, < 10ms lag typically).
- **Cross-region disaster recovery**: Discuss RDS cross-region automated backups (manual snapshots copied cross-region), Aurora Global Database (sub-second RPO across regions with 1-2 regional replicas), Redshift cross-region snapshots. Understand recovery regions (which region is your DR region) and failover testing (quarterly drills). Mention Route 53 DNS-based failover routing with health checks.
- **SQL dialect differences at Amazon**: Mention you adapt to the target RDS engine. Amazon uses MySQL, PostgreSQL, Oracle, MariaDB, and Aurora. Know key differences: Oracle PL/SQL vs PostgreSQL PL/pgSQL, MySQL LIMIT vs Oracle FETCH FIRST, PostgreSQL ARRAY_AGG vs Oracle LISTAGG. Amazon values engine-specific knowledge but also flexibility.
- **Database vs DynamoDB**: Know the decision framework. Use RDS/Aurora when you need: joins, complex queries, transactions (ACID), referential integrity, stored procedures, known schema. Use DynamoDB when you need: single-digit-millisecond latency at any scale, high throughput (millions of writes/sec), flexible schema, key-value or document data model, serverless scaling. Know the DynamoDB limits: 400KB item size, 1MB query result, 100 tables per account (soft limit).
- **Interview Preparation Checklist**: (1) Practice 30 LeetCode SQL problems in multiple dialects. (2) Build an end-to-end RDS + DynamoDB project in AWS (free tier). (3) Read AWS Database blogs and re:Invent session videos. (4) Prepare 2 STAR stories per Leadership Principle (16 LPs, focus on 8: Customer Obsession, Dive Deep, Deliver Results, Ownership, Bias for Action, Invent and Simplify, Learn and Be Curious, Insist on the Highest Standards). (5) Practice system design whiteboarding with AWS service choices.

## Additional Behavioral Practice Questions
1. Tell me about a time you designed a database for high availability on AWS.
2. Describe a situation where you reduced database costs by 50% or more.
3. How have you handled a database migration where you had to minimize downtime?
4. Tell me about a time you diagnosed a performance issue that puzzled the entire team.
5. Describe a situation where you automated a process that was previously manual and error-prone.
6. How have you championed a database best practice across multiple teams?
7. Tell me about a time you had to make a difficult trade-off between cost and performance.
8. Describe a situation where you dealt with a database security incident or vulnerability.

## Amazon-Specific Self-Study Questions
1. Compare Multi-AZ RDS with Aurora for high availability requirements.
2. When would you choose DynamoDB over RDS for a new application?
3. How does Redshift's columnar storage enable faster analytic queries?
4. Explain the difference between DynamoDB strongly consistent vs eventually consistent reads.
5. How does Aurora's distributed storage differ from RDS's EBS-backed storage?
6. When should you use RDS Proxy vs direct database connections?
7. How does DMS perform ongoing replication (CDC) from Oracle?
8. Compare Redshift Spectrum with Athena for querying data in S3.
