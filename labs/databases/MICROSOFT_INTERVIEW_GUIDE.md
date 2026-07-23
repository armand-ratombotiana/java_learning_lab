# Microsoft Interview Guide — Oracle Database Academy

## Interview Process for Database Roles
- **Rounds**: 5–6 rounds: Recruiter screen (30 min) → Technical screen (45-60 min, SQL + SQL Server) → On-site (4 rounds): 2x data platform/SQL coding, 1x system design, 1x behavioral + culture fit with hiring manager.
- **Timeline**: 4–8 weeks. Microsoft interview scheduling can be flexible; they often do virtual on-sites over two half-days.
- **DB-specific expectations**: Microsoft expects strong SQL Server knowledge — T-SQL, execution plans, indexing strategies, locking/isolation levels, high availability (Always On), SSIS/SSRS/SSAS. Your Oracle knowledge is highly valued because Microsoft wants you to translate between platforms. Expect comparison questions: Oracle vs SQL Server on features, syntax, architecture. Azure SQL Database, Azure Synapse, Cosmos DB are also tested.

## Top SQL Problems by Pattern (5-6 patterns, 2-3 problems each)

### Pattern: Window Functions
#### Problem: Rank Scores (LC SQL 178)
- **Difficulty/Frequency**: Medium / Very High at Microsoft
- **Problem**: Rank scores with no gaps in ranking.
- **Interview walkthrough**: Use DENSE_RANK in T-SQL. Order by score DESC. Select score and computed rank.
- **SQL solution**:
  ```sql
  SELECT score,
         DENSE_RANK() OVER (ORDER BY score DESC) AS rank
  FROM scores
  ORDER BY score DESC;
  ```
- **What Microsoft evaluates**: Window function syntax; T-SQL specific DENSE_RANK vs Oracle's identical function; understanding of ranking types.
- **Follow-ups**: Rewrite this using RANK() — what changes? In SQL Server, how does the OVER clause work without PARTITION BY? Compare T-SQL vs Oracle sort operators.

#### Problem: Last Person to Fit in the Bus (LC SQL 1204)
- **Difficulty/Frequency**: Medium / High at Microsoft
- **Problem**: Find last person who can board the bus without exceeding 1000 kg weight limit.
- **Interview walkthrough**: Use SUM OVER for running total. Filter where cumulative <= 1000. Get last person ordered by turn.
- **SQL solution**:
  ```sql
  SELECT TOP 1 person_name
  FROM (
    SELECT person_name, weight, turn,
           SUM(weight) OVER (ORDER BY turn) AS cumulative_weight
    FROM queue
  ) t
  WHERE cumulative_weight <= 1000
  ORDER BY turn DESC;
  ```
- **What Microsoft evaluates**: Running totals with window functions; TOP 1 (T-SQL) vs FETCH FIRST (Oracle); performance of window aggregate vs self-join.
- **Follow-ups**: How does SQL Server's SUM OVER execute compared to Oracle? What is the cursor-based alternative? How would you optimize with an index?

### Pattern: Joins and Subqueries
#### Problem: Customers Who Never Order (LC SQL 183)
- **Difficulty/Frequency**: Easy / Very High at Microsoft
- **Problem**: Find customers who have never placed any orders.
- **Interview walkthrough**: LEFT JOIN customers to orders where order id is null. Alternatively, NOT EXISTS subquery.
- **SQL solution**:
  ```sql
  SELECT c.name AS customers
  FROM customers c
  LEFT JOIN orders o ON c.id = o.customerId
  WHERE o.id IS NULL;
  ```
- **What Microsoft evaluates**: Anti-join logic; LEFT JOIN NULL pattern vs NOT EXISTS; T-SQL query execution.
- **Follow-ups**: Compare LEFT JOIN ... IS NULL vs NOT EXISTS execution in SQL Server. Which is better for large tables? What's the Oracle equivalent: MINUS?

#### Problem: Duplicate Emails (LC SQL 182)
- **Difficulty/Frequency**: Easy / High at Microsoft
- **Problem**: Find all duplicate email addresses.
- **Interview walkthrough**: GROUP BY email, HAVING COUNT(*) > 1.
- **SQL solution**:
  ```sql
  SELECT email
  FROM person
  GROUP BY email
  HAVING COUNT(*) > 1;
  ```
- **What Microsoft evaluates**: GROUP BY + HAVING; understanding of duplicate detection patterns.
- **Follow-ups**: If the table has 500M rows, what index would improve this? How would you delete duplicates while keeping one row? Use CTE with ROW_NUMBER.

### Pattern: Aggregate Functions and Advanced Grouping
#### Problem: Monthly Transactions I (LC SQL 1193)
- **Difficulty/Frequency**: Medium / High at Microsoft
- **Problem**: Count approved transactions and their total amount by month and country.
- **Interview walkthrough**: Use CASE inside SUM to count approved transactions. Filter only approved in WHERE. Group by TO_CHAR(trans_date, 'YYYY-MM') and country.
- **SQL solution**:
  ```sql
  SELECT TO_CHAR(trans_date, 'YYYY-MM') AS month, country,
         COUNT(*) AS trans_count,
         SUM(CASE WHEN state = 'approved' THEN 1 ELSE 0 END) AS approved_count,
         SUM(amount) AS trans_total_amount,
         SUM(CASE WHEN state = 'approved' THEN amount ELSE 0 END) AS approved_total_amount
  FROM transactions
  GROUP BY TO_CHAR(trans_date, 'YYYY-MM'), country;
  ```
- **What Microsoft evaluates**: Conditional aggregation; date formatting; pivot-like reporting with GROUP BY.
- **Follow-ups**: In SQL Server, how would you format the month? (FORMAT vs CONVERT). Compare Oracle's TO_CHAR with SQL Server's FORMAT. Performance impact of formatting in GROUP BY.

#### Problem: Product Sales Analysis III (LC SQL 1084)
- **Difficulty/Frequency**: Medium / Medium at Microsoft
- **Problem**: Find products that were sold in 2019 Q1 but not in 2020 Q1.
- **Interview walkthrough**: Two queries: one for 2019 Q1 sales, one for 2020 Q1 sales. Use LEFT JOIN with NULL check or NOT EXISTS.
- **SQL solution**:
  ```sql
  SELECT DISTINCT p.product_id, p.product_name
  FROM product p
  JOIN sales s ON p.product_id = s.product_id
  WHERE s.sale_date >= '2019-01-01' AND s.sale_date < '2019-04-01'
    AND NOT EXISTS (
      SELECT 1 FROM sales s2
      WHERE s2.product_id = p.product_id
        AND s2.sale_date >= '2020-01-01' AND s2.sale_date < '2020-04-01'
    );
  ```
- **What Microsoft evaluates**: NOT EXISTS anti-join; correlated subqueries; date range best practices (>= and < vs BETWEEN).
- **Follow-ups**: How does SQL Server optimize NOT EXISTS vs NOT IN vs LEFT JOIN NULL? What are the NULL implications?

### Pattern: Recursive CTEs and Hierarchical Queries
#### Problem: Find the Missing IDs (LC SQL 1613)
- **Difficulty/Frequency**: Medium / Medium at Microsoft
- **Problem**: Find all missing IDs between min and max customer_id.
- **Interview walkthrough**: Use recursive CTE to generate the ID sequence. LEFT ANTI-JOIN with the customer table.
- **SQL solution**:
  ```sql
  WITH cte AS (
    SELECT MIN(customer_id) AS n FROM customers
    UNION ALL
    SELECT n + 1 FROM cte
    WHERE n < (SELECT MAX(customer_id) FROM customers)
  )
  SELECT n AS ids
  FROM cte
  WHERE n NOT IN (SELECT customer_id FROM customers)
  OPTION (MAXRECURSION 0);
  ```
- **What Microsoft evaluates**: Recursive CTE (T-SQL standard); MAXRECURSION hint; anti-join for missing values.
- **Follow-ups**: Compare SQL Server's RECURSIVE CTE with Oracle's CONNECT BY. What is the default MAXRECURSION in SQL Server? How does SQL Server's stack depth compare?

#### Problem: All People Report to the Given Manager (LC SQL 1731)
- **Difficulty/Frequency**: Easy / Medium at Microsoft
- **Problem**: Find all employees who report to a given manager (directly or indirectly).
- **Interview walkthrough**: Recursive CTE starting with manager_id = 1. Union all employees whose manager_id is in the CTE.
- **SQL solution**:
  ```sql
  WITH emp_tree AS (
    SELECT employee_id
    FROM employees
    WHERE manager_id = 1
    UNION ALL
    SELECT e.employee_id
    FROM employees e
    JOIN emp_tree et ON e.manager_id = et.employee_id
  )
  SELECT employee_id FROM emp_tree;
  ```
- **What Microsoft evaluates**: Recursive CTE for org hierarchy; anchor and recursive member design; cycle avoidance.
- **Follow-ups**: How would you prevent infinite recursion from circular references? Compare with Oracle CONNECT BY NOCYCLE.

### Pattern: Set Operations and Data Comparison
#### Problem: Find Common Interests (LC SQL 1132)
- **Difficulty/Frequency**: Medium / Medium at Microsoft
- **Problem**: Find users who share common interests from multiple tables.
- **Interview walkthrough**: INTERSECT to find common interests across users.
- **SQL solution**:
  ```sql
  SELECT interest_type FROM user_1_interests
  INTERSECT
  SELECT interest_type FROM user_2_interests;
  ```
- **What Microsoft evaluates**: Set operations (INTERSECT, UNION, EXCEPT in T-SQL; MINUS in Oracle). T-SQL uses EXCEPT instead of MINUS.
- **Follow-ups**: How does SQL Server's EXCEPT differ from Oracle's MINUS? How does the engine evaluate EXCEPT vs NOT EXISTS? Compare performance.

## Database Architecture Deep Dive
- **SQL Server vs Oracle Architecture**: SQL Server uses OS threads (not processes like Oracle). Buffer pool (not buffer cache). Log writer flushes transaction log. Checkpoint writes dirty pages. Compare — Oracle SGA/PGA vs SQL Server memory architecture. SQL Server uses a single buffer pool; Oracle uses multiple pools (KEEP/RECYCLE/DEFAULT). SQL Server's lazy writer vs Oracle's DBWn.
- **Always On Availability Groups**: SQL Server HA/DR. Primary replicas + 1-8 secondary replicas. Synchronous (no data loss within same region) and asynchronous (cross-region). Readable secondaries. Listener for transparent failover. Compare to Oracle Data Guard + RAC. Discuss automatic failover, manual failover, forced failover. Comparison: Always On is simpler to manage (GUI), Data Guard has more options (Flashback, Snapshot Standby, Far Sync).
- **SQL Server Indexing**: Clustered indexes (table organization, leaf = data pages) vs non-clustered indexes (leaf = key columns + bookmark). Columnstore indexes for analytics. Filtered indexes. Included columns. Compare to Oracle indexes — Oracle has no clustered index concept (index-organized tables are similar). B-tree structure, bitmap indexes, function-based indexes (SQL Server has computed column indexes instead).
- **Azure SQL Database**: Platform as a Service (PaaS). Built on SQL Server engine. Hyperscale (up to 100 TB, log-based replication for fast scaling). Serverless (auto-pause, compute auto-scale). Geo-replication (readable secondaries in different regions). SQL Database elastic pools for multi-tenant. Compare to RDS — Azure SQL has higher compatibility (same engine) vs RDS which wraps multiple engines.
- **Azure Synapse Analytics**: Formerly SQL Data Warehouse. Massively parallel processing (MPP). Distribution (hash, round-robin, replicated). Clustered columnstore index. PolyBase for external data querying (Azure Blob, Data Lake). Synapse pipelines for ETL. Compare to Oracle Exadata — both use MPP-like architectures.
- **Locking and Blocking**: SQL Server lock modes (shared, exclusive, update, schema). Lock granularity (row, page, table, database). Lock escalation. Deadlock detection (periodic, victim selection). Isolation levels (READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ, SERIALIZABLE, SNAPSHOT). Compare to Oracle — Oracle's undo-based MVCC means readers don't block writers (SQL Server uses tempdb version store for similar behavior with READ COMMITTED SNAPSHOT).
- **T-SQL vs PL/SQL**: Cursors — DECLARE CURSOR FETCH syntax differences. Error handling — TRY...CATCH (T-SQL) vs EXCEPTION block (PL/SQL). Temp tables — #temp (session) vs ##temp (global) in T-SQL vs Oracle's global temporary tables. Table-valued functions vs Oracle's pipelined functions. MERGE statement differences.

## System Design (2-3 questions)
1. **Design a database for Microsoft Teams (100 million daily active users)**
   - Database: Azure SQL Database Hyperscale for message persistence (10 TB+). Shard by tenant_id. Use Azure Cosmos DB for chat metadata (low latency, multi-region writes). SQL Database geo-replication for disaster recovery. Full-text search via Azure Cognitive Search. Discuss rowstore vs columnstore for message history vs real-time chat.

2. **Design a migration from Oracle Exadata to Azure Synapse for a data warehouse (50 TB)**
   - Tools: Azure Data Factory for orchestration. PolyBase for external tables over Oracle. SSMA for Oracle to SQL Server schema conversion to T-SQL. Use Azure Blob Storage as staging. Redesign partitioning (date-based distribution) for Synapse MPP. Convert Oracle PL/SQL procedures to T-SQL stored procedures. Discuss dual-read validation strategy, cutover planning, fallback.

3. **Design a high-availability payment system database on Azure**
   - Database: Azure SQL Database Business Critical tier (99.995% SLA). Always On with 4 replicas, synchronous commit, automatic failover. Read scale-out with readable secondaries. Geo-replication with failover groups. Azure Traffic Manager for global routing. Discuss data consistency (XACT_ABORT), locking strategies, and deadlock prevention for payment transactions.

## Behavioral (5 STAR answers)
1. **S — SQL Server migration from on-prem to Azure SQL Database (Situation)**
   - T: Company had 20 on-prem SQL Server instances running critical applications. Infrastructure costs were high, and HA was manual.
   - A: Migrated 20 databases to Azure SQL Database using DMA (Data Migration Assistant) for assessment, DMS for migration. Set up elastic pools for dev/test databases. Implemented geo-replication for 3 business-critical databases. Created automation scripts for deployment using Azure DevOps.
   - R: 40% cost reduction. HA improved from 99.5% to 99.995%. Migration completed 2 weeks ahead of schedule.

2. **S — Deadlock crisis in order processing system (Situation)**
   - T: Order processing experienced 500+ deadlocks per hour during peak. Customers were retrying and getting charged multiple times.
   - A: Enabled READ_COMMITTED_SNAPSHOT to eliminate read-write deadlocks. Redesigned the order insert transaction to access tables in a consistent order (product first, then customer, then order). Created an index on foreign keys to reduce lock duration.
   - R: Deadlocks reduced from 500/hr to 0. Charge errors eliminated. System throughput doubled.

3. **S — Query performance degradation after index rebuild (Situation)**
   - T: After a routine index rebuild overnight, a critical dashboard query that previously took 2 seconds now took 2 minutes.
   - A: Discovered the clustered columnstore index compressed with the wrong order. Rebuilt the index with ORDER BY on date column. Also updated statistics and reviewed the execution plan for cardinality estimation issues.
   - R: Query returned to sub-2-second execution. Team implemented index rebuild validation checklist.

4. **S — Azure SQL Database DTU exhaustion during marketing campaign (Situation)**
   - T: A targeted email campaign caused DTU usage to spike to 100%, causing timeouts for all users.
   - A: Switched from DTU to vCore purchasing model for better burst capacity. Implemented Azure SQL Intelligent Insights for proactive alerting. Created a read replica for campaign target queries to isolate from OLTP. Used query store to identify the culprit query and added a covering index.
   - R: Campaign succeeded with zero user-facing issues. DTU exhaustion never recurred.

5. **S — Built a real-time reporting system using Azure Synapse (Situation)**
   - T: Business needed real-time (5-minute latency) sales reports. Legacy Oracle BI reports took 4 hours.
   - A: Architected Azure Synapse with streaming data via Event Hubs. Designed star schema with hash-distributed fact tables, replicated dimension tables. Used materialized views for common aggregations. Automated data loading with Synapse Pipelines.
   - R: Reports available within 3 minutes of transactions. 50x improvement over legacy system.

### Pattern: PIVOT/UNPIVOT and Conditional Aggregation
#### Problem: Product Sales Pivot by Year (LC Style)
- **Difficulty/Frequency**: Medium / High at Microsoft
- **Problem**: Given a sales table with product_id, year, and total_sales, pivot the data so each year becomes a column.
- **Interview walkthrough**: Use PIVOT in T-SQL. The pivot column is year, aggregated by SUM(total_sales).
- **SQL solution** (T-SQL syntax):
  ```sql
  SELECT product_id, [2020], [2021], [2022], [2023]
  FROM (
    SELECT product_id, year, total_sales
    FROM sales
  ) src
  PIVOT (
    SUM(total_sales) FOR year IN ([2020], [2021], [2022], [2023])
  ) pvt;
  ```
- **What Microsoft evaluates**: T-SQL PIVOT syntax; understanding of cross-tabulation; dynamic SQL alternatives.
- **Follow-ups**: How would you unpivot this result back? What if years are dynamic — how to build the PIVOT dynamically? Compare PIVOT with CASE-based conditional aggregation.

#### Problem: Employee Salary by Department (LC Style)
- **Difficulty/Frequency**: Medium / High at Microsoft
- **Problem**: Show department name, total salary, average salary, min salary, and max salary. Sort by total salary descending.
- **Interview walkthrough**: JOIN employee to department. GROUP BY department. Compute aggregates and order.
- **SQL solution** (T-SQL syntax):
  ```sql
  SELECT d.department_name,
         COUNT(e.employee_id) AS employee_count,
         SUM(e.salary) AS total_salary,
         AVG(e.salary) AS avg_salary,
         MIN(e.salary) AS min_salary,
         MAX(e.salary) AS max_salary
  FROM employees e
  JOIN departments d ON e.department_id = d.department_id
  GROUP BY d.department_name
  ORDER BY total_salary DESC;
  ```
- **What Microsoft evaluates**: Multi-table GROUP BY; multiple aggregate functions; ordering by computed column.
- **Follow-ups**: How would you include departments with zero employees using SQL Server? How would you compute the standard deviation of salaries?

### Pattern: String and Date Manipulation
#### Problem: Group Sold Products by the Date (LC SQL 1484)
- **Difficulty/Frequency**: Easy / Medium at Microsoft
- **Problem**: Group sold products by the date, listing product names comma-separated.
- **Interview walkthrough**: GROUP BY sell_date. Use STRING_AGG to concatenate distinct product names ordered alphabetically.
- **SQL solution** (T-SQL syntax):
  ```sql
  SELECT sell_date,
         COUNT(DISTINCT product) AS num_sold,
         STRING_AGG(DISTINCT product, ',') WITHIN GROUP (ORDER BY product) AS products
  FROM activities
  GROUP BY sell_date
  ORDER BY sell_date;
  ```
- **What Microsoft evaluates**: STRING_AGG for concatenation (SQL Server 2017+); WITHIN GROUP ordering; DISTINCT within aggregate.
- **Follow-ups**: Compare STRING_AGG (T-SQL) with LISTAGG (Oracle) and GROUP_CONCAT (MySQL). How would you limit the concatenated string length?

## Database Architecture Deep Dive (Extended)
- **SQL Server Indexing Deep Dive**: B-tree structure — root node, intermediate levels, leaf pages. Clustered index determines physical order (only one per table, leaf pages = data). Non-clustered index leaf pages contain key columns + bookmark (RID or clustering key). Include columns in non-clustered index to create covering indexes. Filtered indexes for subset of data. Columnstore indexes for analytical workloads — batch-mode execution (processes rows in batches for CPU efficiency). Index maintenance — fragmentation (internal/external), fill factor, page splits, rebuilding vs reorganizing.
- **Locking and Blocking Deep Dive**: Lock granularity — row (RID), key, page, extent, table, database. Lock modes — shared (S), exclusive (X), update (U), intent, schema (Sch-S, Sch-M). Lock escalation — SQL Server escalates from row to table at ~5000 locks. Deadlock detection — periodic (5-second cycle), chooses victim based on cost. Deadlock graph from system_health session or extended events. Compare to Oracle — Oracle uses UNDO for MVCC (readers don't block writers). SQL Server uses tempdb version store for READ_COMMITTED_SNAPSHOT and SNAPSHOT isolation (similar behavior).
- **Transaction Log Internals**: Write-ahead logging (WAL) — log is written before data pages. Virtual Log Files (VLFs) — log file is divided into VLF segments. Too many small VLFs cause performance degradation (log grows slowly). Log reader reads the log for replication, CDC, Always On. Log truncation on checkpoint. Log size management — auto-growth, manual shrink. Compare to Oracle redo logs — Oracle uses fixed-size online redo log groups; SQL Server uses a single transaction log file with auto-growth.
- **Always On Availability Groups Deep Dive**: Windows Server Failover Cluster (WSFC) as foundation. Primary replica sends log blocks to secondary replicas. Synchronous commit (data safety, higher latency) — transaction waits for secondary to harden log. Asynchronous commit (performance, potential data loss). Readable secondaries — can serve read-only queries, reporting. Listener for transparent client redirection. Automatic failover (requires synchronous commit on all replicas). Distributed Availability Groups for cross-DR (beyond WSFC limitations). Compare to Oracle Data Guard — Always On supports multiple secondaries (up to 8), readable secondaries standard, built-in listener.

## System Design (Extended — Additional Question)
4. **Design a multi-tenant SaaS database using Azure SQL Database Elastic Pools**
   - Database: Azure SQL Database elastic pools for multi-tenant database consolidation. Each tenant gets a database within the pool. Pool resource sharing prevents any single tenant from consuming all resources. Geo-replication for DR. Elastic jobs for tenant management (schema updates across all tenant databases). Sharding with Elastic Database Tools (shard map manager). Discuss tenant isolation vs shared pool economics. Compare single-tenant vs multi-tenant approaches.

## Behavioral (Extended — 3 Additional STAR Answers)
6. **S — Performance tuning for a SQL Server stored procedure (Situation)**
   - T: A critical month-end financial report stored procedure in SQL Server was timing out after 30 minutes. Business users could not close the books.
   - A: Analyzed the execution plan with STATISTICS IO — identified table spools and key lookups on a 50M-row table. Created a covering non-clustered index. Rewrote the cursor-based loop into a single set-based MERGE statement. Updated statistics with FULLSCAN.
   - R: Stored procedure completed in 4 minutes. Book closure completed on time. Set-based pattern documented and shared across team.

7. **S — SQL Server log file growth out of control (Situation)**
   - T: Transaction log file grew to 200GB, filling the disk. Replication was delayed due to log reader latency.
   - A: Identified that an index rebuild was running on a large table as a single transaction, causing log growth. Scheduled index maintenance in smaller batches. Switched to simple recovery model for non-critical databases. Increased log backup frequency from hourly to every 15 minutes.
   - R: Log file reduced to 5GB maintenance. Disk alerts eliminated. Replication lag dropped to under 1 minute.

8. **S — Azure SQL Database DTU exhaustion during monthly campaign (Situation)**
   - T: Monthly marketing campaigns caused Azure SQL Database DTU to spike to 100%, causing query timeouts.
   - A: Migrated from DTU to vCore model with auto-scaling. Implemented Read Scale-Out for reporting queries. Used Query Store for identifying performance regression. Added memory-optimized tables for session state (reduced blocking). Set up proactive auto-scale alerts.
   - R: Campaign traffic handled with zero timeouts. Cost increase was only 15% for 3x better performance.

## Study Plan (Extended)
- **Weeks 1–2**: SQL fundamentals with T-SQL focus. Master SELECT, JOINs (INNER, LEFT, RIGHT, FULL, CROSS APPLY), subqueries (scalar, correlated, EXISTS), set operations (UNION, INTERSECT, EXCEPT). Understand T-SQL vs Oracle differences (TOP vs FETCH FIRST, EXCEPT vs MINUS, no DUAL, no CONNECT BY). Practice 25 LeetCode SQL problems in T-SQL dialect.
- **Weeks 3–4**: Advanced T-SQL — window functions (OVER, PARTITION BY, ROWS/RANGE, ROW_NUMBER, RANK, DENSE_RANK, LAG/LEAD, FIRST_VALUE/LAST_VALUE), recursive CTEs, PIVOT/UNPIVOT, dynamic SQL (sp_executesql, QUOTENAME). Practice 20 medium LeetCode problems. Study SQL Server's execution plan operators and SSMS plan analysis.
- **Weeks 5–6**: SQL Server internals — data pages (8KB), extents (64KB), allocation maps (IAM, GAM, SGAM), B-tree structure and page splits, index internals (clustered vs non-clustered, include columns, filtered indexes), transaction log architecture (VLFs, log sequence numbers). Locking, blocking, deadlocks, lock escalation, isolation levels. Understand DMVs (sys.dm_exec_query_stats, sys.dm_db_index_usage_stats, sys.dm_os_wait_stats, sys.dm_db_index_physical_stats).
- **Weeks 7–8**: High availability and disaster recovery — Always On Availability Groups (synchronous/async commit, readable secondaries, automatic failover, distributed AG), failover cluster instances (FCI), log shipping, database mirroring (deprecated). Compare with Oracle RAC and Data Guard. Practice configuring HA in Azure Portal and ARM templates.
- **Weeks 9–10**: Azure database services — Azure SQL Database (DTU vs vCore, hyperscale, serverless), Azure SQL Managed Instance (full SQL Server compatibility), Azure Synapse (MPP, PolyBase, dedicated SQL pool), Cosmos DB (multi-model, multi-region writes, consistency levels). Understand pricing, scaling, migration tools (DMA, DMS, SSMA). Practice a mock migration from Oracle to Azure SQL Managed Instance.
- **Weeks 11–12**: Behavioral and system design prep. Write 10 STAR stories following Microsoft STAR+C (Challenge). Practice 4 database system design problems (e-commerce, SaaS multi-tenant, analytics, global payments). Mock interviews with Microsoft-style questions (deep dives into trade-offs).
- **Ongoing**: Read Microsoft SQL Server blogs (Bob Ward — SQL Server performance, Brent Ozar — DBA training, Paul Randal — internals). Follow Azure Database updates via Azure Updates RSS. Participate in SQL Server community events (SQL Saturday, PASS Data Community Summit). Earn Azure Database certifications (DP-300 Azure Database Administrator, DP-203 Data Engineer).

### Pattern: Dynamic SQL and Schema Exploration
#### Problem: Search Across All Columns (LC Style)
- **Difficulty/Frequency**: Hard / Medium at Microsoft
- **Problem**: Write a stored procedure to search for a keyword across all columns in a given table.
- **Interview walkthrough**: Query INFORMATION_SCHEMA.COLUMNS to get column list. Build dynamic SQL using sp_executesql. Use CONCAT to construct the WHERE clause with all columns.
- **SQL solution** (T-SQL syntax):
  ```sql
  CREATE PROCEDURE dbo.SearchAllColumns
    @SchemaName NVARCHAR(128),
    @TableName NVARCHAR(128),
    @SearchValue NVARCHAR(255)
  AS
  BEGIN
    DECLARE @sql NVARCHAR(MAX);
    
    SELECT @sql = STRING_AGG(
      CONCAT('CAST(', QUOTENAME(COLUMN_NAME), ' AS NVARCHAR(MAX)) LIKE ''%', @SearchValue, '%'''),
      ' OR '
    ) WITHIN GROUP (ORDER BY ORDINAL_POSITION)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @SchemaName AND TABLE_NAME = @TableName;
    
    SET @sql = CONCAT('SELECT * FROM ', QUOTENAME(@SchemaName), '.', QUOTENAME(@TableName), ' WHERE ', @sql);
    EXEC sp_executesql @sql;
  END;
  ```
- **What Microsoft evaluates**: Dynamic SQL generation; INFORMATION_SCHEMA queries; sp_executesql for safe execution; STRING_AGG with QUOTENAME for security.
- **Follow-ups**: How would you prevent SQL injection in this procedure? What are the performance implications of CAST on every column? When would you use Full-Text Search instead?

#### Problem: Row Count for All Tables in a Database (LC Style)
- **Difficulty/Frequency**: Easy / Medium at Microsoft
- **Problem**: Return a list of all tables in the current database with their row counts.
- **Interview walkthrough**: Query sys.tables. Use dynamic SQL to count each table's rows.
- **SQL solution** (T-SQL syntax):
  ```sql
  SELECT 
    t.name AS table_name,
    SUM(p.rows) AS row_count
  FROM sys.tables t
  JOIN sys.partitions p ON t.object_id = p.object_id
  WHERE p.index_id <= 1
  GROUP BY t.name
  ORDER BY row_count DESC;
  ```
- **What Microsoft evaluates**: Catalog views vs INFORMATION_SCHEMA; sys.partitions for approximate row counts; DMV knowledge.
- **Follow-ups**: How accurate are these row counts? How would you get exact counts for critical tables? Compare sys.dm_db_partition_stats with COUNT(*).

## Tips (Extended)
- **Cross-platform perspective**: Your Oracle knowledge is a major asset. Always frame answers with comparison: "In Oracle you use X, in SQL Server it's Y. The trade-off is..." Microsoft values engineers who can bridge platforms and help customers migrate from Oracle to SQL Server/Azure SQL.
- **SQL Server documentation**: Know the official Microsoft Docs inside out. If asked about a feature, navigate to the docs in your head. Microsoft engineers pride themselves on well-documented APIs and platform. Reference specific doc pages when discussing features.
- **Performance tuning in T-SQL**: Use Actual Execution Plan (SSMS or Azure Data Studio — right click, Show Actual Execution Plan), SET STATISTICS IO ON (see logical/physical reads), SET STATISTICS TIME ON (CPU and elapsed time), Query Store (persistent execution plan store with forced plan capability). Show familiarity with all diagnostic SET statements.
- **Azure certification helps significantly**: Having AZ-900 (Azure Fundamentals — entry level), DP-300 (Azure Database Administrator Associate — database-specific), or DP-203 (Azure Data Engineer Associate — data pipeline) demonstrates commitment to the Microsoft ecosystem. Azure certifications are highly valued in the interview process.
- **DMV proficiency**: Know key DMVs cold: sys.dm_exec_query_stats (query performance metrics), sys.dm_db_index_usage_stats (index seek/scan/lookup counts), sys.dm_os_wait_stats (cumulative wait statistics), sys.dm_db_index_physical_stats (fragmentation, page count), sys.dm_exec_sql_text (plan text from plan_handle), sys.dm_db_missing_index_details (suggested missing indexes), sys.dm_tran_locks (current lock info).
- **Always On vs RAC**: Be ready to discuss architectural differences thoroughly. Always On ships log blocks (write overhead only on primary's log I/O, less inter-node traffic). RAC uses cache fusion (data blocks transferred between node caches, high-speed interconnect required). Always On is active-passive (one primary processes writes, secondaries are readable but not writable). RAC is active-active (all nodes can process writes, cache coherence keeps block versions consistent). Always On is simpler to manage; RAC provides better resource utilization across all nodes.
- **Growth mindset**: Microsoft values "learn-it-all" over "know-it-all" (Satya Nadella's philosophy). When you don't know something, say "I don't know yet, but here's how I would learn about it." This is authentic Microsoft culture. Show curiosity and eagerness to learn.
- **Moneyball questions**: Expect data platform cost optimization questions. Know how to size Azure SQL Database (DTU purchasing model vs vCore model, Hyperscale for large databases, serverless for variable workloads), reserved instances (1 or 3 years, up to 60% discount), Azure Hybrid Benefit (using existing on-prem SQL Server licenses in Azure).
- **T-SQL vs Oracle SQL comprehensive comparison**: SELECT: TOP n (T-SQL) vs FETCH FIRST n ROWS ONLY (Oracle). Pagination: OFFSET FETCH (T-SQL 2012+) vs OFFSET FETCH (Oracle 12c+). SET operations: EXCEPT (T-SQL) vs MINUS (Oracle). String aggregation: STRING_AGG (T-SQL 2017+) vs LISTAGG (Oracle 11g+). Error handling: TRY...CATCH (T-SQL) vs EXCEPTION block (PL/SQL). Temp tables: #temp (session-scoped, T-SQL) vs global temporary tables (transaction-scoped, Oracle). Dynamic SQL: sp_executesql (T-SQL) vs EXECUTE IMMEDIATE (Oracle). Sequences: CREATE SEQUENCE (both, but RESET/CYCLE syntax differs). Hierarchical: recursive CTEs (T-SQL) vs CONNECT BY (Oracle).
- **Interview Preparation Checklist**: (1) Practice 30 LeetCode SQL problems in T-SQL dialect. (2) Read Microsoft Docs for Always On, Query Store, and Columnstore indexes. (3) Build a mini Azure SQL Database project (create tables, load data, monitor performance). (4) Prepare STAR stories that demonstrate Azure migration experience. (5) Study the T-SQL vs Oracle comparison table until you can explain each difference verbally. (6) Get DP-300 certification if possible. (7) Practice explaining execution plans to a non-technical audience.

## Additional Behavioral Practice Questions
1. Tell me about a time you migrated a legacy SQL Server database to Azure SQL.
2. Describe a situation where you resolved a complex deadlock issue.
3. How have you handled a high-severity outage in a production SQL Server?
4. Tell me about a time you improved query performance by more than 10x.
5. Describe a situation where you automated a manual DBA process using T-SQL.
6. How have you convinced developers to adopt parameterized queries and proper indexing?
7. Tell me about a cross-team collaboration that required significant communication effort.
8. Describe a situation where you had to learn a new Azure service quickly to solve a problem.

## SQL Server-Specific Self-Study Questions
1. How does SQL Server's buffer pool differ from Oracle's SGA/PGA?
2. Compare clustered vs non-clustered indexes. When to use each?
3. Explain the transaction log's role in ACID compliance.
4. Compare READ COMMITTED vs READ COMMITTED SNAPSHOT isolation.
5. How does SQL Server detect and handle deadlocks?
6. Temp table vs table variable — when to use each?
7. How does Query Store help with performance troubleshooting?
8. Always On synchronous vs asynchronous commit — trade-offs?
9. Recovery models: Full vs Simple vs Bulk-Logged — which for which workload?
10. Columnstore index vs traditional rowstore — performance characteristics?

## Quick Reference — Microsoft Data Platform
- **On-prem SQL Server**: Standard, Enterprise, Developer editions — Always On AG, FCI, Columnstore, In-Memory OLTP, PolyBase
- **Azure SQL Database**: Single database, elastic pool, managed instance, hyperscale (up to 100TB) — DTU or vCore pricing, serverless, geo-replication, auto-failover groups
- **Azure SQL Managed Instance**: Full SQL Server compatibility in cloud — no feature gaps, VNet integration, automated patching/backup, native SQL Agent, SSIS/SSRS/SSAS support
- **Azure Synapse Analytics**: MPP data warehouse — dedicated SQL pool, serverless SQL pool, Apache Spark integration, Synapse Pipelines, Link for real-time analytics
- **Cosmos DB**: Multi-model NoSQL — document, key-value, graph, column-family — multi-region writes, 5 consistency levels, auto-scaling throughput, < 10ms latency at P99

## Microsoft Interview Process — Key Takeaways
- **Cross-Platform Value**: Your Oracle experience is your superpower. Frame every answer to show how you bridge Oracle and SQL Server.
- **Growth Mindset**: Microsoft values "learn-it-all" over "know-it-all." Acknowledge what you don't know and show eagerness to learn.
- **Azure Ecosystem**: Demonstrate familiarity with Azure SQL, Azure Synapse, and Cosmos DB. Azure certifications (DP-300) are highly valued.
- **Performance Tuning**: Be ready to discuss execution plans, Query Store, waits stats, and DMVs in depth. Practice STATISTICS IO/TIME.
- **Enterprise Experience**: Microsoft customers are enterprises. Show understanding of HA/DR, compliance, security, licensing, and migration.
- **T-SQL vs Oracle Knowledge**: Know the syntax differences by heart. Microsoft interviewers specifically test your ability to translate between platforms.
- **Power Platform Integration**: Microsoft increasingly integrates SQL Server with Power BI, Power Apps, and Azure Logic Apps. Showing awareness of the broader Microsoft data ecosystem is valuable.
- **Customer-Focused**: Microsoft interviewers value customer empathy. Relate your database decisions to end-user impact and business outcomes.
- **Prepare for Product Questions**: Microsoft sometimes asks how you would improve a specific Azure database service. Read the latest Azure updates and have opinions about product direction.
- **Data Migration Expertise**: Microsoft values experience migrating from Oracle and other databases to SQL Server/Azure SQL. Be ready to discuss DMS, DMA, SSMA, and bulk copy (BCP) tools in detail.
- **Azure Hybrid Benefit**: Microsoft's licensing advantage — mention how customers can use existing on-prem SQL Server licenses to save up to 55% on Azure SQL. This shows business acumen.
- **ETL/SSIS experience**: If you have SSIS experience, highlight it heavily. SSIS is still the primary ETL tool in Microsoft enterprise shops. Integration services (SSIS packages), data flow tasks (source, transformation, destination), control flow (precedence constraints, containers), Azure-SSIS integration runtime (lift-and-shift SSIS to Azure), and data flows in Azure Data Factory (equivalent to SSIS in cloud) are all valuable skills.
