# Meta Interview Guide — Oracle Database Academy

## Interview Process for Database Roles
- **Rounds**: 5–7 rounds: Recruiter screen → Technical phone screen (45 min, SQL + data modeling) → On-site (4 rounds): 2x SQL/coding, 1x system design, 1x behavioral (Meta leadership principles).
- **Timeline**: 3–5 weeks. Meta moves fast — expect decisions within 1 week of on-site.
- **DB-specific expectations**: Meta operates at extreme scale — MySQL and custom databases (TAO, Scuba, Presto, Velox). SQL problems are heavy on analytics, user growth metrics, product funnel analysis. Expect complex window functions, ratio computations, time-series analysis, and percentile calculations. Data modeling for social graph features (news feed, notifications, friend recommendations) is heavily tested.

## Top SQL Problems by Pattern (5-6 patterns, 2-3 problems each)

### Pattern: Window Functions
#### Problem: User Activity for the Past 30 Days II (LC SQL 1142)
- **Difficulty/Frequency**: Easy / Very High at Meta
- **Problem**: Find the average number of sessions per user for each day over the last 30 days.
- **Interview walkthrough**: Filter last 30 days. Group by day. Count sessions, divide by distinct users.
- **SQL solution**:
  ```sql
  SELECT activity_date AS day,
         COUNT(session_id) / NULLIF(COUNT(DISTINCT user_id), 0) AS avg_sessions_per_user
  FROM activity
  WHERE activity_date BETWEEN DATE '2019-07-27' - INTERVAL '29' DAY AND '2019-07-27'
  GROUP BY activity_date;
  ```
- **What Meta evaluates**: Date range filtering; division with NULLIF to avoid divide-by-zero; GROUP BY accuracy.
- **Follow-ups**: How would you calculate daily active users (DAU)? Weekly active users (WAU)? Compare DAU/WAU/MAU ratios — what do they tell about engagement?

#### Problem: Friend Requests II (LC SQL 602)
- **Difficulty/Frequency**: Medium / Very High at Meta
- **Problem**: Find the person with the most friends.
- **Interview walkthrough**: UNION ALL both sides of the friendship. Group by person. Count. Order descending and limit 1.
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
- **What Meta evaluates**: Symmetric friendship modeling; UNION ALL for combining directionality; TOP-N query.
- **Follow-ups**: How would you find the top 10 most connected users? How does Meta's graph TAO database handle this query differently than a relational database?

#### Problem: Active Users (LC SQL 1141)
- **Difficulty/Frequency**: Medium / Very High at Meta (growth analytics is Meta core)
- **Problem**: Find users who logged in on at least 5 consecutive days.
- **Interview walkthrough**: Use LAG to find previous 4 login dates. Check if the difference is exactly 1 day for 4 consecutive rows.
- **SQL solution**:
  ```sql
  WITH daily_users AS (
    SELECT DISTINCT user_id, login_date
    FROM logins
  ),
  lagged AS (
    SELECT user_id, login_date,
           LAG(login_date, 4) OVER (PARTITION BY user_id ORDER BY login_date) AS lag4
    FROM daily_users
  )
  SELECT DISTINCT user_id
  FROM lagged
  WHERE login_date - lag4 = 4;
  ```
- **What Meta evaluates**: LAG with offset; date difference for consecutive detection; DISTINCT deduplication.
- **Follow-ups**: How would you find streaks of any length N? How does Meta model user retention cohorts?

### Pattern: Aggregation and Ratio Computation
#### Problem: Confirmation Rate (LC SQL 1934)
- **Difficulty/Frequency**: Medium / Very High at Meta
- **Problem**: Find the confirmation rate of each user (number of confirmed actions / total actions).
- **Interview walkthrough**: LEFT JOIN signups with confirmations. GROUP BY user_id. Compute AVG of a CASE expression converting confirmations to 1.
- **SQL solution**:
  ```sql
  SELECT s.user_id,
         ROUND(AVG(CASE WHEN c.action = 'confirmed' THEN 1.0 ELSE 0.0 END), 2) AS confirmation_rate
  FROM signups s
  LEFT JOIN confirmations c ON s.user_id = c.user_id
  GROUP BY s.user_id;
  ```
- **What Meta evaluates**: Rate computation with JOIN; AVG of boolean expression; LEFT JOIN to preserve users with no actions.
- **Follow-ups**: How would you calculate the 7-day rolling confirmation rate? How would you identify users with abnormally low confirmation rates?

#### Problem: Reported Posts II (LC SQL 1132)
- **Difficulty/Frequency**: Medium / High at Meta
- **Problem**: Find the average number of reports per post per day for each action date.
- **Interview walkthrough**: Join posts with reports. Group by report date. Count distinct reports divided by distinct posts. Use window AVG over time.
- **SQL solution**:
  ```sql
  SELECT r.report_date,
         COUNT(DISTINCT r.post_id) AS reported_posts,
         COUNT(DISTINCT r.post_id) / NULLIF(COUNT(DISTINCT p.post_id), 0) AS report_ratio
  FROM actions p
  LEFT JOIN reports r ON p.post_id = r.post_id
  GROUP BY r.report_date
  ORDER BY r.report_date;
  ```
- **What Meta evaluates**: Multi-table aggregation; DISTINCT counts for ratio; NULLIF for safety.
- **Follow-ups**: How would you identify spam campaigns where one user reports many posts? Window function for rolling average report rates.

### Pattern: Subqueries and CTEs
#### Problem: Market Analysis I (LC SQL 1158)
- **Difficulty/Frequency**: Medium / Very High at Meta
- **Problem**: For each user, find how many orders they placed in 2019 and the earliest order date.
- **Interview walkthrough**: LEFT JOIN users to orders. GROUP BY user_id. Use COUNT with date filter and MIN for earliest.
- **SQL solution**:
  ```sql
  SELECT u.user_id AS seller_id,
         COUNT(CASE WHEN YEAR(o.order_date) = 2019 THEN o.order_id END) AS num_orders,
         MIN(CASE WHEN YEAR(o.order_date) = 2019 THEN o.order_date END) AS earliest_order
  FROM users u
  LEFT JOIN orders o ON u.user_id = o.seller_id AND YEAR(o.order_date) = 2019
  GROUP BY u.user_id;
  ```
- **What Meta evaluates**: Conditional aggregation; LEFT JOIN with additional ON condition vs WHERE; GROUP BY with multiple aggregates.
- **Follow-ups**: How would you rank users by order recency, frequency, and monetary value (RFM analysis)? Window function percentile.

#### Problem: Ads Performance (LC SQL 1322)
- **Difficulty/Frequency**: Medium / High at Meta (ads is core revenue)
- **Problem**: Find the click-through rate (CTR) for each ad group.
- **Interview walkthrough**: Join ads with actions. Use conditional count for clicks and views. Compute rate.
- **SQL solution**:
  ```sql
  SELECT a.ad_id,
         CASE WHEN SUM(CASE WHEN a.action = 'Clicked' THEN 1 ELSE 0 END) = 0 THEN 0.0
              ELSE ROUND(1.0 * SUM(CASE WHEN a.action = 'Clicked' THEN 1 ELSE 0 END)
                   / NULLIF(SUM(CASE WHEN a.action IN ('Clicked', 'Viewed') THEN 1 ELSE 0 END), 0), 2)
         END AS ctr
  FROM ads a
  GROUP BY a.ad_id
  ORDER BY ctr DESC;
  ```
- **What Meta evaluates**: Conditional aggregation for metrics; careful handling of division by zero; ad metrics computation.
- **Follow-ups**: How would you compute CTR by device, by country, by time of day? How does Presto optimize this at Meta scale?

### Pattern: Advanced Analytics and Percentiles
#### Problem: Median Employee Salary (LC SQL 569)
- **Difficulty/Frequency**: Hard / Very High at Meta
- **Problem**: Find the median salary for each company. For even number of salaries, use the lower median (the first of the two middle values).
- **Interview walkthrough**: Use ROW_NUMBER with ASC and DESC ordering. Find where row counts cross the midpoint.
- **SQL solution**:
  ```sql
  WITH salary_rows AS (
    SELECT company, salary,
           ROW_NUMBER() OVER (PARTITION BY company ORDER BY salary) AS rn_asc,
           ROW_NUMBER() OVER (PARTITION BY company ORDER BY salary DESC) AS rn_desc
    FROM employee
  )
  SELECT company, AVG(salary) AS median_salary
  FROM salary_rows
  WHERE rn_asc BETWEEN rn_desc - 1 AND rn_desc + 1
  GROUP BY company;
  ```
- **What Meta evaluates**: Advanced median computation; symmetric row numbering; understanding of statistical measures in SQL.
- **Follow-ups**: How would you compute percentile_50 exactly? How would you compute percentiles 25, 50, 75 together? Meta uses approximate percentile algorithms at scale.

#### Problem: Find the Quiet Students (LC SQL 1412)
- **Difficulty/Frequency**: Hard / Medium at Meta
- **Problem**: Find students who took exams but never scored in the top or bottom 10%.
- **Interview walkthrough**: Use PERCENT_RANK() to compute relative rank. Filter where rank is between 0.1 and 0.9. Ensure they took at least one exam.
- **SQL solution**:
  ```sql
  WITH exam_percentiles AS (
    SELECT student_id, exam_id, score,
           PERCENT_RANK() OVER (PARTITION BY exam_id ORDER BY score) AS pct_rank
    FROM exam
  ),
  quiet_students AS (
    SELECT student_id
    FROM exam_percentiles
    GROUP BY student_id
    HAVING MAX(pct_rank) < 0.9 AND MIN(pct_rank) > 0.1
  )
  SELECT s.student_id, s.student_name
  FROM student s
  JOIN quiet_students qs ON s.student_id = qs.student_id
  ORDER BY s.student_id;
  ```
- **What Meta evaluates**: PERCENT_RANK window function; aggregation on window function output; percentile-based filtering.
- **Follow-ups**: How would you find students in the top 5% per exam? How does PERCENT_RANK differ from CUME_DIST?

### Pattern: String and Date Functions
#### Problem: Fix Names in a Table (LC SQL 1667)
- **Difficulty/Frequency**: Easy / Medium at Meta
- **Problem**: Fix the capitalization of names: first letter uppercase, rest lowercase.
- **Interview walkthrough**: Use SUBSTR/UPPER/LOWER functions. Combine with CONCAT or ||
- **SQL solution**:
  ```sql
  SELECT user_id,
         UPPER(SUBSTR(name, 1, 1)) || LOWER(SUBSTR(name, 2)) AS name
  FROM users
  ORDER BY user_id;
  ```
- **What Meta evaluates**: String manipulation functions; Oracle-specific dual pipe concatenation vs CONCAT.
- **Follow-ups**: How would you handle multi-word names (Mary Ann)? What if names have apostrophes? Oracle vs Presto string functions?

## Database Architecture Deep Dive
- **MySQL at Meta**: Meta runs one of the largest MySQL fleets globally. Logical partitioning for sharding. InnoDB storage engine. Custom modifications for Meta's scale. Discuss how Meta uses MySQL with custom proxies (ProxySQL-like) for connection management, automatic failover, read/write splitting. Compare MySQL vs Oracle — MySQL lacks many Oracle features (materialized views, flashback queries, advanced partitioning) but is simpler, open-source, and optimized for web workloads.
- **TAO (The Associations and Objects)**: Meta's custom graph database for social graph. Objects (users, pages, photos) and Associations (edges between objects — friends, likes, comments). In-memory cache layer over MySQL. Read-optimized, eventually consistent. Sharded by object ID. Compare to relational — TAO handles 10x more reads than writes, uses ID-based lookups instead of joins.
- **Scuba (In-Memory Database)**: Meta's real-time analytics database. Columnar, in-memory, for operational analytics. Sub-second query latency on billion-row datasets. Used for product metrics, bug analysis, spam detection. Discuss how Scuba ingests data via Scribe and provides real-time aggregation.
- **Presto/Trino**: Distributed SQL query engine for analytics. Used at Meta for querying across multiple data sources (Hive tables, MySQL, specialized stores). Connector architecture. Velox native engine for vectorized execution. Compare to Oracle — Presto is not a database but a query engine (no ACID transaction support). Presto can join a MySQL table with a S3 Parquet file.
- **Data Warehouse (Hive + ORC/Parquet)**: Meta's data warehouse on HDFS. Partitioned tables by date. ORC columnar format for compression and predicate pushdown. Spark for ETL. Compare to Oracle Exadata — Meta uses open-source components with custom optimization.
- **Replication and Scaling**: Asynchronous replication (MySQL binlog). Semi-sync replication for reduced data loss. Read replicas. Sharding by user_id hash. Virtual IP + VIP management for failover. Compare to Oracle Data Guard and RAC.

## System Design (2-3 questions)
1. **Design the Facebook News Feed database**
   - Database: TAO for social graph (who are your friends, what pages they like). MySQL for post content storage. Scuba for real-time engagement metrics (likes, shares, comments per post). Presto for analytics querying. Shard by user_id. Use ranking algorithms (EdgeRank/ML-based) to score and order feed items. Discuss caching strategy (LRU cache for top stories, TAO cache for graph edges).

2. **Design Instagram's photo storage and analytics system**
   - Database: MySQL for user metadata (user_id, username, bio). Photos/thumbnails stored in object storage (xStore/Haystack). Comments/likes in MySQL sharded by photo_id. Hashtag index in TAO for graph traversal. Analytics on Scuba for real-time engagement dashboards. Discuss timestamp-ordered keys for photo queries, denormalized likes count on photo table, eventual consistency for like counts.

3. **Design Meta's ad targeting database**
   - Database: User attributes (age, location, interests) in MySQL with secondary indexes for targeting queries. Ad impressions/clicks in Scuba for real-time pacing and budget management. Ad campaigns in MySQL with transactional guarantees for billing. Use Presto for ML model feature extraction. Discuss aggregation tables for pre-computed audience sizes. Lambda architecture for near-real-time bid optimization.

## Behavioral (5 STAR answers)
1. **S — News Feed ranking system regression after model update (Situation)**
   - T: A machine learning model update for News Feed ranking caused a 5% decrease in user engagement. Users complained about irrelevant content.
   - A: Rolled back the model immediately. Analyzed Scuba dashboards to identify the segment (new users < 30 days). Fixed the training data imbalance. Implemented A/B testing with holdout groups. Wrote a SQL validation pipeline to monitor key metrics post-deployment.
   - R: Engagement returned to baseline in 2 days. New model with fix improved engagement by 8%. Validation pipeline adopted by 3 other ML teams.

2. **S — MySQL replication lag during viral event (Situation)**
   - T: A celebrity post went viral, causing 15+ minutes of replication lag on read replicas. Users saw stale data.
   - A: Increased replica count from 3 to 8. Upgraded to larger instance types. Enabled semi-sync replication. Implemented read-from-primary for critical queries that require up-to-date data. Added monitoring alerts for replication lag > 30 seconds.
   - R: Replication lag reduced to < 5 seconds during peak. Zero stale data incidents in next viral event.

3. **S — Reducing query latency for Ads Manager reporting (Situation)**
   - T: Ads Manager reports took 2+ minutes to load for advertisers with large campaigns (10M+ events).
   - A: Built pre-aggregated materialized tables in MySQL for common time ranges (daily, weekly, monthly). Switched from row-based to columnar storage for fact tables. Used Presto for full-scan queries. Implemented paginated loading of report sections.
   - R: Report load time reduced from 120 seconds to 3 seconds. Customer satisfaction scores improved 40%.

4. **S — Database capacity planning for user growth in emerging markets (Situation)**
   - T: User growth in India and Brazil was 300% YoY. Database capacity would be exceeded in 3 months.
   - A: Sharded the user table by country code + user_id hash. Added new MySQL clusters in regional data centers (Mumbai, Sao Paulo). Implemented local read replicas for low-latency reads. Used cross-region async replication for global features.
   - R: Handled 3x user growth without issues. Read latency reduced by 60% in local regions.

5. **S — Fixing data inconsistency between MySQL and cache layer (Situation)**
   - T: A bug in cache invalidation caused TAO cache to show stale friend counts. Users reported seeing wrong friend numbers.
   - A: Identified the race condition where cache was updated before MySQL commit completed. Rewrote the invalidation logic to use MySQL binlog-based invalidation (read from binlog, invalidate after commit). Added consistency checks running every hour. Implemented repair job for affected users.
   - R: Inconsistency eliminated. Repair job fixed 2M user profiles within 2 hours.

### Pattern: Funnel and Conversion Analysis
#### Problem: User Registration Funnel Analysis (LC Style)
- **Difficulty/Frequency**: Hard / Very High at Meta
- **Problem**: Given event logs for user onboarding (page_viewed, signed_up, completed_profile, first_content), compute the funnel conversion rate between each step.
- **Interview walkthrough**: Create a CTE with each event per user. Use LEAD to detect the next step. Count users at each stage transition.
- **SQL solution**:
  ```sql
  WITH user_funnel AS (
    SELECT user_id,
           MAX(CASE WHEN event_name = 'page_visited' THEN 1 ELSE 0 END) AS visited_page,
           MAX(CASE WHEN event_name = 'signed_up' THEN 1 ELSE 0 END) AS signed_up,
           MAX(CASE WHEN event_name = 'completed_profile' THEN 1 ELSE 0 END) AS completed_profile,
           MAX(CASE WHEN event_name = 'first_content' THEN 1 ELSE 0 END) AS created_content
    FROM user_events
    GROUP BY user_id
  )
  SELECT 'page_visited' AS step, COUNT(*) AS users, 100.0 AS pct_of_total FROM user_funnel
  UNION ALL
  SELECT 'signed_up', COUNT(*), ROUND(100.0 * COUNT(*) / MAX(total), 1)
  FROM user_funnel, (SELECT COUNT(*) AS total FROM user_funnel WHERE visited_page = 1) t
  WHERE signed_up = 1
  UNION ALL
  SELECT 'completed_profile', COUNT(*), ROUND(100.0 * COUNT(*) / MAX(total), 1)
  FROM user_funnel, (SELECT COUNT(*) AS total FROM user_funnel WHERE visited_page = 1) t
  WHERE completed_profile = 1
  UNION ALL
  SELECT 'first_content', COUNT(*), ROUND(100.0 * COUNT(*) / MAX(total), 1)
  FROM user_funnel, (SELECT COUNT(*) AS total FROM user_funnel WHERE visited_page = 1) t
  WHERE created_content = 1;
  ```
- **What Meta evaluates**: Funnel computation; conditional aggregation; cross-join for global totals; analytical reporting.
- **Follow-ups**: How would you compute per-cohort funnel conversion (by signup date)? How would you detect where users drop off most? A/B test for funnel improvements.

#### Problem: Retention Cohort Analysis (LC Style)
- **Difficulty/Frequency**: Hard / Very High at Meta
- **Problem**: Calculate the retention rate for each cohort (users who signed up in a given month) for weeks 1, 2, 4, 8 post-signup.
- **Interview walkthrough**: Determine cohort month from signup_date. Count users active in each week after signup. Compute percentage.
- **SQL solution**:
  ```sql
  WITH cohorts AS (
    SELECT user_id, DATE_TRUNC('month', signup_date) AS cohort_month
    FROM users
  ),
  activity AS (
    SELECT c.user_id, c.cohort_month,
           FLOOR(DATEDIFF('day', c.cohort_month, a.activity_date) / 7) AS week_num
    FROM cohorts c
    JOIN user_activity a ON c.user_id = a.user_id
    WHERE a.activity_date >= c.cohort_month
      AND week_num IN (0, 1, 3, 7)
  )
  SELECT cohort_month, week_num, COUNT(DISTINCT user_id) AS active_users
  FROM activity
  GROUP BY cohort_month, week_num
  ORDER BY cohort_month, week_num;
  ```
- **What Meta evaluates**: Cohort date bucketing; retention week computation; large-scale aggregation.
- **Follow-ups**: How would you compute the retention table (cohort_month as rows, weeks as columns)? How do you define active users (e.g., 7-day window)?

### Pattern: Social Graph Queries
#### Problem: Mutual Friends Count (LC Style)
- **Difficulty/Frequency**: Hard / Very High at Meta
- **Problem**: For each pair of friends, count the number of mutual friends they share.
- **Interview walkthrough**: Self-join friendship table to find friends of each friend. Intersect the two friend sets. Count common ones.
- **SQL solution**:
  ```sql
  WITH all_edges AS (
    SELECT user1_id AS user_id, user2_id AS friend_id FROM friendship
    UNION ALL
    SELECT user2_id, user1_id FROM friendship
  )
  SELECT a.user_id AS user_a, b.user_id AS user_b,
         COUNT(*) AS mutual_friend_count
  FROM all_edges a
  JOIN all_edges b ON a.user_id < b.user_id AND a.friend_id = b.friend_id
  WHERE EXISTS (
    SELECT 1 FROM all_edges c
    WHERE c.user_id = a.user_id AND c.friend_id = b.user_id
  )
  GROUP BY a.user_id, b.user_id;
  ```
- **What Meta evaluates**: Graph traversal in SQL; self-joins with symmetric relationships; intersection counting.
- **Follow-ups**: How does TAO optimize mutual friends queries differently? What if the friendship table has 10B rows?

## Database Architecture Deep Dive (Extended)
- **MySQL at Meta Deep Dive**: Meta runs one of the largest MySQL fleets globally (thousands of instances, petabytes of data). Custom MySQL forks with patches for Meta's scale. InnoDB storage engine with custom optimizations. Logical partitioning via application-level sharding (user_id hash -> shard mapping). ProxySQL for connection pooling and read/write splitting. Semi-sync replication for data durability. Custom automated failover tooling (no manual intervention). Compare to Oracle — MySQL simpler, open-source, optimized for web OLTP, but lacks advanced Oracle features (materialized views, flashback, advanced partitioning, analytic functions natively before 8.0).
- **TAO Deep Dive**: TAO (The Associations and Objects) is a distributed graph database built over MySQL. Objects (nodes) — user, page, photo, comment. Associations (edges) — friend, like, share, comment_on. Both are stored as key-value pairs where the key is (object_id, association_type, id_type, time). TAO provides a read-optimized cache layer (multiple layers of cache in front of MySQL). Read throughput 10x+ write throughput. No complex queries — only point lookups, range scans on edges, inverse edges. Compare to relational — a graph traversal that would take multiple SQL joins is one TAO request.
- **Scuba Deep Dive**: In-memory columnar database for real-time analytics. Data is column-compressed in memory (typically 10:1 compression). Sub-second query latency. Continuous data ingestion via Scribe (Meta's pub/sub system). Used for operational analytics — product metrics, spam detection, abuse prevention, bug diagnosis. Supports timeseries aggregations, top-N, filtering, grouping. Discuss how Scuba differs from Oracle — Scuba is in-memory only, no ACID transactions, no persistence guaranteed, optimized for real-time exploratory analysis.
- **Presto/Trino Deep Dive**: Distributed SQL query engine (not a database). Connector architecture — query across Hive tables, MySQL, Cassandra, Kafka, etc. At Meta, Presto queries Hive (HDFS) tables for data science and analytics. Features: ANSI SQL support, window functions, approximate aggregation, geospatial functions, UDF support. Velox (C++ execution engine) for vectorized processing. Compare to Oracle — Presto is for federated queries and analytics on object storage; Oracle is for transactional workloads and structured data.

## System Design (Extended — Additional Question)
4. **Design Facebook Messenger database**
   - Database: MySQL for user metadata (profile, contacts). Messages stored in custom log-based storage (HBase-like, write-optimized for append pattern). Message indexes in MySQL sharded by thread_id. Delivery status in Scuba for real-time tracking. End-to-end encryption key management in separate secure storage. Discuss write-optimized log storage (append-only for message delivery), sharding by thread_id for locality, and caching of recent messages.

## Behavioral (Extended — 3 Additional STAR Answers)
6. **S — Rebuilding user engagement metrics pipeline at scale (Situation)**
   - T: The user engagement metrics pipeline (DAU, WAU, MAU) built on Hive was taking 6+ hours to run, producing stale data that product teams couldn't trust.
   - A: Rebuilt the pipeline using Presto for faster query execution. Partitioned input tables by (date, country). Used incremental processing — only read changed partitions daily. Cached intermediate aggregations in materialized views. Added data quality checks (row count validation, anomaly detection).
   - R: Pipeline completed in 15 minutes. Data accuracy validated at 99.9%. Product teams adopted daily engagement data for real-time decisions.

7. **S — Handling a 10x traffic spike from a viral Reels feature (Situation)**
   - T: A new Reels feature went viral unexpectedly, causing 10x traffic to the video storage and metadata database (MySQL).
   - A: Scaled read capacity by adding 20 read replicas across 3 data centers. Enabled read-from-replica for all video metadata queries. Throttled non-critical writes (like aggressive prefetch jobs). Used TAO cache for object metadata (reduced MySQL load by 80%).
   - R: Zero downtime during the spike. Feature continued to grow 5x more without infrastructure issues.

8. **S — Reducing data storage costs by 40% (Situation)**
   - T: Data storage costs were growing at 20% month-over-month. Old logs and deprecated features consumed petabytes without value.
   - A: Implemented a data lifecycle policy — move cold data to cheaper HDFS cold storage (60-day retention on hot, 180-day on warm, then delete). Compressed old data with ZSTD (additional 3:1 compression). Decommissioned 20 deprecated feature tables. Automated the lifecycle with scheduled jobs.
   - R: Storage costs reduced 40% month-over-month. No data needed to be restored for at least 12 months.

## Study Plan (Extended)
- **Weeks 1–2**: SQL fundamentals — SELECT, JOINs (INNER, LEFT, RIGHT, no FULL OUTER in MySQL), subqueries (scalar, correlated, EXISTS), UNION/UNION ALL. Practice 25 LeetCode SQL easy/medium problems. Focus on Meta's favorite patterns: date filtering, aggregation, ratio computation, product analytics.
- **Weeks 3–4**: Advanced window functions — ROW_NUMBER, RANK, DENSE_RANK, LAG/LEAD (MySQL 8.0+), SUM/AVG OVER, PERCENT_RANK, NTILE, FIRST_VALUE/LAST_VALUE. Practice 20 LeetCode medium/hard problems. Study MySQL 8.0 window function documentation.
- **Weeks 5–6**: Data modeling for social applications — entities (user, post, comment, like, friend, page, group). Normalization vs denormalization (user friend count as denormalized column). Handle high write throughput (like button — millions of likes per minute) and read throughput (news feed — read-heavy). Study Instagram and Facebook database designs (engineering blog posts).
- **Weeks 7–8**: Distributed databases at scale — TAO (graph DB), Scuba (in-memory analytics), Presto/Trino (federated SQL), MySQL sharding (logical shards per user range). Read Meta engineering blog posts. Understand trade-offs: consistency vs availability (eventual consistency for non-critical), SQL vs NoSQL (MySQL + TAO hybrid), row vs columnar (Scuba columnar).
- **Weeks 9–10**: System design — practice designing 4 Meta-scale systems (News Feed, Messenger, Ads, Search). Each design should discuss database choice, sharding strategy, caching (TAO, Memcached), replication (semi-sync MySQL), and failure handling.
- **Weeks 11–12**: Behavioral prep — write 10 STAR stories using Meta's values (Move Fast, Focus on Impact, Be Open, Build Social Value, Differentiate Through Quality). Practice mock interviews with Meta-style problem solving. Emphasize impact metrics (DAU, MAU, engagement %, latency improvements).
- **Ongoing**: Read Meta Engineering blog. Follow Meta Data team publications (research paper releases). Practice LeetCode SQL weekly contest. Join Meta career preparation groups. Study MySQL 8.0+ new features (window functions, CTE, JSON, histograms).

### Pattern: Data Integrity and Deduplication
#### Problem: Detect and Remove Duplicate Rows (LC Style)
- **Difficulty/Frequency**: Easy / Very High at Meta
- **Problem**: Find and delete duplicate rows from a user_actions table where all columns except id are identical.
- **Interview walkthrough**: Use ROW_NUMBER partitioned by all non-id columns. Identify duplicates where rn > 1. Delete using CTE.
- **SQL solution**:
  ```sql
  WITH duplicate_ids AS (
    SELECT id,
           ROW_NUMBER() OVER (PARTITION BY user_id, action_type, action_date ORDER BY id) AS rn
    FROM user_actions
  )
  DELETE FROM user_actions
  WHERE id IN (SELECT id FROM duplicate_ids WHERE rn > 1);
  ```
- **What Meta evaluates**: Deduplication pattern; ROW_NUMBER with partition; DELETE using CTE subquery.
- **Follow-ups**: How would you keep the most recent record instead of the earliest? How would you prevent duplicates from being inserted in the first place (unique constraints, idempotency keys)?

#### Problem: Consecutive Available Seats (LC SQL 603)
- **Difficulty/Frequency**: Easy / Medium at Meta
- **Problem**: Find all seat numbers that are available and consecutive (at least 2 consecutive empty seats).
- **Interview walkthrough**: Self-join on seat number difference = 1 where both are available. Use DISTINCT.
- **SQL solution**:
  ```sql
  SELECT DISTINCT c1.seat_id
  FROM cinema c1
  JOIN cinema c2 ON ABS(c1.seat_id - c2.seat_id) = 1
    AND c1.free = 1 AND c2.free = 1
  ORDER BY c1.seat_id;
  ```
- **What Meta evaluates**: Self-joins for adjacency detection; ABS for unordered pair detection; DISTINCT to avoid duplicates.
- **Follow-ups**: How would you find groups of 3 or more consecutive seats? How would you find the longest chain of consecutive empty seats?

## Tips (Extended)
- **Move fast, break things mindset**: Meta values speed over perfection. When solving SQL, show agility — write the straightforward solution first, then optimize. Don't spend 20 minutes planning the perfect query. "A good query today beats a perfect query tomorrow." Show you can iterate quickly.
- **Data-driven decision making**: Every answer should reference data and metrics. Meta loves metrics — talk about DAU (daily active users), MAU (monthly active users), CTR (click-through rate), retention rates (Day 1/7/30), LTV (lifetime value), ARPU (average revenue per user). Show you understand what these metrics mean and how they relate to business outcomes.
- **Scale obsession**: Always mention how your solution performs at Facebook scale (2B+ monthly users, petabytes of data, millions of writes per second, thousands of shards). Even for a simple SQL, discuss sharding (by user_id hash), caching (TAO, Memcached), replication (semi-sync MySQL), materialized views (for dashboard queries), data partitioning (by date for time-series data).
- **Product thinking**: Meta interviews blend engineering with product sense. For system design, ask clarifying questions about the product use case (what kind of feed — ranked? chronological? Stories or posts?), user goals (engagement, connection, entertainment), and success metrics (retention, time spent, DAU) before proposing technical solutions.
- **MySQL knowledge is key**: While you know Oracle, Meta uses MySQL and its forks extensively. Learn the differences — MySQL does not have FULL OUTER JOIN (workaround with UNION), materialized views (use triggers + summary tables), analytic functions (pre-8.0 — no window functions), PIVOT (use CASE), or CONNECT BY (use recursive CTE or nested set model). MySQL uses LIMIT n OFFSET m not FETCH FIRST.
- **Engagement metrics expertise**: Be fluent in growth metrics — retention curves (Day 1: 60%, Day 7: 30%, Day 30: 15% is typical for social apps), cohort analysis (by signup month, by country, by acquisition channel), funnel conversion (registration -> profile completion -> first post -> invite a friend), A/B testing (p-value < 0.05, statistical significance, power = 0.8, minimum detectable effect).
- **No office politics talk**: Meta values direct communication and "be open." When discussing behavioral questions, focus on impact and outcomes, not process or politics. Avoid describing situations where stakeholders disagreed and you navigated politics — focus on how you directly solved the problem and delivered results.
- **TAO knowledge**: If you mention TAO in system design, you'll impress. Understand that TAO is read-optimized (cache-first with multiple cache layers), uses ID-based lookups (not SQL joins — each lookup is O(1) single-row fetch), supports graph traversal (follow edges, inverse edges, time-based range queries), and runs over MySQL for durability. Know why joins are expensive at Meta scale (data distributed across thousands of nodes, no distributed join support — each table lives on different shards).
- **Ambiguity tolerance**: Meta problems are deliberately ambiguous. Ask clarifying questions about data volume (millions? billions?), latency requirements (real-time? near real-time? batch?), consistency needs (strong for payments, eventual for likes), and access patterns (read-heavy? write-heavy? balanced?). This is a key evaluation criterion — they want to see you define the problem before solving it.
- **Time management**: For SQL coding rounds, aim to solve each problem in 10-15 minutes. Meta rounds are 45 minutes with 2-3 problems. Move fast, but explain your approach before coding. If you make a syntax mistake, that's okay — Meta cares about algorithmic approach over exact syntax. Focus on logic and pattern recognition.
