# Oracle APEX — NeetCode Roadmap

<div align="center">

![NeetCode](https://img.shields.io/badge/NeetCode_Roadmap-000000?style=for-the-badge)
![SQL](https://img.shields.io/badge/SQL_Prep-CC2927?style=for-the-badge&logo=leetcode&logoColor=white)
![APEX](https://img.shields.io/badge/APEX_Mapped-F80000?style=for-the-badge&logo=oracle&logoColor=white)

**SQL interview problems mapped to APEX labs + 4-week prep study plan**

</div>

---

## Table of Contents

1. [NeetCode SQL Problem List → Lab Mapping](#neetcode-sql-problem-list--lab-mapping)
2. [LeetCode Database Section — Categorized by Pattern](#leetcode-database-section--categorized-by-pattern)
3. [4-Week APEX Interview Study Plan](#4-week-apex-interview-study-plan)
4. [Company-Specific SQL Focus](#company-specific-sql-focus)
5. [APEX-Specific Preparation Path](#apex-specific-preparation-path)

---

## NeetCode SQL Problem List → Lab Mapping

### Lab 01 — APEX Getting Started

NeetCode problems that reinforce foundational concepts taught in Lab 01:

| NeetCode Problem | Pattern | Why It Maps to Lab 01 |
|-----------------|---------|----------------------|
| 175. Combine Two Tables | JOIN | Intro to relational data — first APEX apps connect two tables |
| 183. Customers Who Never Order | LEFT JOIN | Understanding FK relationships in data modeling |
| 181. Employees Earning More Than Managers | Self-JOIN | Hierarchical data — org charts in APEX navigation |

**Focus:** Understand how queries become APEX region sources.

### Lab 02 — APEX Page Builder

| NeetCode Problem | Pattern | Why It Maps to Lab 02 |
|-----------------|---------|----------------------|
| 176. Second Highest Salary | Subquery | Page items sourced from single-value queries (LOV, display) |
| 197. Rising Temperature | Date JOIN | Date-range filtering in APEX forms |
| 610. Triangle Judgement | CASE WHEN | Conditional logic in APEX page computations |

**Focus:** How SQL results map to page items and regions.

### Lab 03 — SQL Workshop

| NeetCode Problem | Pattern | Why It Maps to Lab 03 |
|-----------------|---------|----------------------|
| 180. Consecutive Numbers | LEAD/LAG, Self-JOIN | Window functions in report sources |
| 178. Rank Scores | DENSE_RANK | Ranking in leaderboards and dashboards |
| 184. Department Highest Salary | Window + JOIN | Departmental reports with partitioned ranking |
| 185. Department Top Three Salaries | DENSE_RANK | Filtered window results for top-N reports |
| 550. Game Play Analysis IV | CTE + Agg | CTEs for complex report logic |
| 626. Exchange Seats | LEAD/LAG + CASE | Row transformation patterns |
| 601. Human Traffic of Stadium | Window + CONSECUTIVE | Pattern detection queries |
| 1179. Reformat Department Table | PIVOT | Cross-tabulation in SQL Workshop |
| 1270. All People Report to Manager | Recursive CTE | Hierarchical employee reports |
| 1341. Movie Rating | RANK + UNION | Multi-criteria ranking queries |

**Focus:** SQL Workshop is the SQL playground — these problems directly test the queries you write for APEX region sources.

### Lab 04 — Interactive Grid / Reports

| NeetCode Problem | Pattern | Why It Maps to Lab 04 |
|-----------------|---------|----------------------|
| 177. Nth Highest Salary | Window (OFFSET) | Pagination and ranking in Interactive Grid |
| 534. Game Play Analysis III | SUM OVER | Running totals in Interactive Grid |
| 570. Managers With 5 Reports | GROUP BY HAVING | Aggregate filtering for report summaries |
| 602. Friend Requests II | UNION ALL + GROUP BY | Combined data source reports |
| 1045. Customers Who Bought All Products | HAVING COUNT | Complex having conditions in reports |
| 1070. Product Sales Analysis III | ROW_NUMBER | Window-based deduplication for reports |
| 1097. Game Play Analysis V | CTE + LEAD | Retention analysis in charts |
| 1158. Market Analysis I | CASE + GROUP BY | Conditional aggregation in reports |
| 1164. Product Price at a Date | CTE + COALESCE | As-of date reporting |
| 1174. Immediate Food Delivery II | ROW_NUMBER + AVG | First-row-per-group patterns |
| 1193. Monthly Transactions | DATE + CASE | Monthly trend reports |
| 1204. Last Person to Fit in Bus | SUM OVER | Running sum for cumulative reports |
| 1321. Restaurant Growth | SUM OVER + AVG OVER | Moving averages in dashboards |
| 1364. Number of Trusted Contacts | LEFT JOIN + GROUP BY | Multi-table summary reports |
| 1384. Total Sales Amount | PIVOT | Matrix/cross-tab reports in APEX |
| 1393. Capital Gain/Loss | CASE + SUM | Conditional financial calculations |
| 1532. Most Recent Orders | ROW_NUMBER | Latest-value-per-group in grids |
| 1635. Hopper Company Queries I | CTE + GENERATE_SERIES | Date series reports |
| 1699. Number of Calls Between Persons | LEAST/GREATEST | Pair deduplication |
| 1777. Product's Price for Each Store | PIVOT | Store-by-store comparison |

**Focus:** Interactive Grids are the backbone of APEX data entry — master multi-table queries and window functions.

### Lab 05 — Security

| NeetCode Problem | Pattern | Why It Maps to Lab 05 |
|-----------------|---------|----------------------|
| 262. Trips and Users | JOIN + CASE + WHERE | Data filtering based on user roles — VPD concept |
| 574. Winning Candidate | JOIN + GROUP BY | Authorization check queries |
| 607. Sales Person | Multiple JOINs + NOT IN | Permission-based data visibility |
| 182. Duplicate Emails | GROUP BY HAVING | Data validation rules |
| 596. Classes More Than 5 Students | GROUP BY HAVING | Security threshold checks |

**Focus:** Security in APEX is about filtering data per user — these problems train your WHERE-clause authorization muscle.

### Lab 06 — RESTful Services

| NeetCode Problem | Pattern | Why It Maps to Lab 06 |
|-----------------|---------|----------------------|
| 608. Tree Node | CASE + subquery | JSON-style hierarchical data structures |
| 1148. Article Views I | DISTINCT + WHERE | API endpoint data filtering |
| 1517. Find Users With Valid E-Mails | REGEXP_LIKE | Data validation for API inputs |
| 1965. Employees With Missing Information | FULL OUTER JOIN | Data reconciliation across systems |

**Focus:** REST APIs return clean, formatted data — practice shaping query output.

### Lab 07 — Advanced Components

| NeetCode Problem | Pattern | Why It Maps to Lab 07 |
|-----------------|---------|----------------------|
| 602. Friend Requests II | UNION ALL + GROUP BY | Combined feeds in faceted search |
| 1809. Ad-Free Sessions | LEFT JOIN + NOT EXISTS | Report filtering |
| 1459. Rectangles Area | Self-JOIN | Pairs processing in tree components |

**Focus:** Complex data relationships for advanced APEX components.

### Lab 08 — Performance

| NeetCode Problem | Pattern | Why It Maps to Lab 08 |
|-----------------|---------|----------------------|
| 180. Consecutive Numbers | LEAD window | Window function performance |
| 262. Trips and Users | Multi-JOIN + filtering | JOIN optimization |
| 185. Department Top Three Salaries | DENSE_RANK | Optimizing window queries |
| 1205. Monthly Transactions II | UNION ALL + dedup | Set operation performance |

**Focus:** Writing efficient SQL — these problems teach you to optimize queries for APEX performance.

---

## LeetCode Database Section — Categorized by Pattern

### JOIN Patterns (Master these first)

| Pattern | Must-Solve | Lab | Company |
|---------|-----------|-----|---------|
| Basic INNER JOIN | 175, 181, 197 | 03 | All |
| LEFT JOIN / ANTI-JOIN | 183, 196, 2084 | 03 | Oracle, Amazon, MS |
| Self-JOIN | 180, 181, 612 | 03 | Oracle, Google |
| Multiple JOINs | 184, 185, 570, 607 | 03-04 | Oracle, Amazon |
| FULL OUTER JOIN | 1965, 1440 | 03 | Oracle, MS |
| CROSS JOIN | 610, 1651 | 03 | Google |

### Subquery Patterns

| Pattern | Must-Solve | Lab | Company |
|---------|-----------|-----|---------|
| Scalar subquery | 176, 197 | 03 | Oracle, Amazon |
| Correlated subquery | 184, 185 | 03-04 | Oracle, Google |
| EXISTS / NOT EXISTS | 183, 607, 1821 | 03 | Oracle, Amazon |
| IN / NOT IN | 574, 607 | 03 | All |

### Window Function Patterns

| Pattern | Must-Solve | Lab | Company |
|---------|-----------|-----|---------|
| ROW_NUMBER | 1070, 1174, 1532 | 04 | Oracle, Google |
| RANK / DENSE_RANK | 178, 184, 185 | 04 | Oracle, Amazon, MS |
| LEAD / LAG | 180, 197, 601 | 03 | Oracle, Google |
| SUM OVER | 534, 1204, 1321 | 04 | Oracle, Google |

### Aggregate Patterns

| Pattern | Must-Solve | Lab | Company |
|---------|-----------|-----|---------|
| GROUP BY + HAVING | 182, 570, 596 | 03 | All |
| CASE + SUM | 1393, 1179 | 04 | Oracle |
| PIVOT | 1179, 1384, 1777 | 04 | Oracle, MS |
| LISTAGG / GROUP_CONCAT | 1484 | 04 | Oracle |

### CTE Patterns

| Pattern | Must-Solve | Lab | Company |
|---------|-----------|-----|---------|
| Simple CTE | 180, 550, 1097 | 03-04 | All |
| Recursive CTE | 1270, 1767, 2159 | 03 | Oracle, Google |
| Multiple CTEs | 1174, 1321, 1635 | 04 | Google, Oracle |

---

## 4-Week APEX Interview Study Plan

### Week 1: SQL Foundations + APEX Basics

| Day | SQL Practice (2 hrs) | APEX Practice (1 hr) | Problems |
|-----|---------------------|---------------------|----------|
| 1 | JOINs (INNER, LEFT) | Lab 01: Create app, explore builder | LC 175, 183 |
| 2 | Self-JOIN, subqueries | Lab 02: Build pages, items, regions | LC 181, 176 |
| 3 | GROUP BY / HAVING | Lab 03: SQL Workshop, report source | LC 182, 596 |
| 4 | Basic window functions | Build a report with ranking | LC 178, 184 |
| 5 | CTEs | Create master-detail pages | LC 180, 550 |
| 6 | Review + weak areas | Redo labs with your own data | Redo week problems |
| 7 | Mock assessment | Full 10-problem SQL practice | Mixed easy/medium |

### Week 2: Advanced SQL + APEX Core

| Day | SQL Practice (2 hrs) | APEX Practice (1 hr) | Problems |
|-----|---------------------|---------------------|----------|
| 1 | DENSE_RANK, ROW_NUMBER | Lab 04: Interactive Grid setup | LC 185, 1070 |
| 2 | LEAD/LAG, FIRST_VALUE | IG master-detail, aggregation | LC 601, 197 |
| 3 | SUM OVER (running totals) | Dashboards, charts, summaries | LC 534, 1204 |
| 4 | Recursive CTEs | Lab 07: Tree region, faceted search | LC 1270, 1767 |
| 5 | PIVOT, UNPIVOT | Report crosstab, data transformation | LC 1179, 1384 |
| 6 | Complex CASE + aggregation | Conditional formatting in IG | LC 1393, 1158 |
| 7 | Mock assessment | Medium SQL in 45-min timed session | Mixed medium |

### Week 3: Security, REST + Performance

| Day | SQL/Tech Practice (2 hrs) | APEX Practice (1 hr) | Problems |
|-----|--------------------------|---------------------|----------|
| 1 | Multi-table JOINs + filtering | Lab 05: Auth/authorization schemes | LC 262, 574 |
| 2 | Anti-join, NOT EXISTS | Implement VPD, row-level security | LC 607, 2084 |
| 3 | Regular expressions | Lab 06: Create REST endpoints in ORDS | LC 1517, 1148 |
| 4 | FULL OUTER JOIN, UNION | Web Source Module, consume API | LC 1965, 602 |
| 5 | Execution plan reading | Lab 08: Debug mode analysis | Analyze slow queries |
| 6 | Index tuning, hints | Caching strategies, pagination | LC 180 (optimized) |
| 7 | Performance-focused mock | Tune a slow APEX page (given scenario) | Mixed hard |

### Week 4: System Design + Mock Interviews

| Day | System Design (2 hrs) | Interview Prep (1 hr) | Focus |
|-----|----------------------|---------------------|-------|
| 1 | Design APEX multi-tenant app | Behavioral STAR stories | Oracle specific |
| 2 | APEX on OCI/Azure/AWS | Salary negotiation | Cloud deployment |
| 3 | Security architecture | Questions to ask | Security focus |
| 4 | HA + DR for APEX | Company research | High availability |
| 5 | Migration + integration | Panel interview prep | Integration patterns |
| 6 | Full mock interview | Whiteboard session | All topics |
| 7 | Review + confidence | Rest / light review | Weak areas |

---

## Company-Specific SQL Focus

### Oracle (Complex SQL, PL/SQL, APEX)

| Priority | Topic | Problems | Why |
|----------|-------|----------|-----|
| 1 | Hierarchical queries | CONNECT BY, Recursive CTE | Oracle-developed, APEX tree regions |
| 2 | PL/SQL patterns | Cursors, collections, bulk | Core Oracle development in APEX |
| 3 | Window functions | DENSE_RANK, LEAD, SUM OVER | Report building |
| 4 | Performance tuning | Execution plans, hints | APEX app optimization |
| 5 | PIVOT / UNPIVOT | Cross-tabulation data | Interactive Grid advanced features |

**Oracle interview SQL pattern:**
```sql
-- Oracle-specific: Hierarchical + Analytics
SELECT LEVEL, employee_id, last_name,
       CONNECT_BY_ISLEAF AS is_leaf,
       SYS_CONNECT_BY_PATH(last_name, '/') AS path,
       DENSE_RANK() OVER (PARTITION BY department_id
            ORDER BY salary DESC) AS dept_rank
FROM employees
START WITH manager_id IS NULL
CONNECT BY PRIOR employee_id = manager_id
ORDER SIBLINGS BY last_name;
```

### Google (Scalable DB Design, Complex Queries)

| Priority | Topic | Problems | Why |
|----------|-------|----------|-----|
| 1 | Scalable schema design | Normalization vs denormalization | Google-scale data |
| 2 | Complex CTEs | Multiple CTEs, recursive | Analytical queries |
| 3 | Window function mastery | FIRST/LAST_VALUE, NTILE | Data analysis at scale |
| 4 | Date/time series | Gap detection, retention | User analytics queries |
| 5 | Cloud databases | Spanner vs Oracle tradeoffs | Cloud infrastructure roles |

**Google interview SQL pattern:**
```sql
-- Google-style: Retention cohort analysis
WITH first_login AS (
    SELECT player_id, MIN(event_date) AS first_date
    FROM activity GROUP BY player_id
),
daily_retention AS (
    SELECT a.player_id, a.event_date, fl.first_date,
           a.event_date - fl.first_date AS day_since_first
    FROM activity a JOIN first_login fl ON a.player_id = fl.player_id
)
SELECT day_since_first,
       COUNT(DISTINCT player_id) AS retained_users
FROM daily_retention
GROUP BY day_since_first
ORDER BY day_since_first;
```

### Amazon (DynamoDB vs SQL, Cost Optimization)

| Priority | Topic | Problems | Why |
|----------|-------|----------|-----|
| 1 | Query optimization | Minimize full scans | Cost-per-query focus |
| 2 | Data modeling | RDS vs DynamoDB decisions | AWS-native role |
| 3 | Partitioning | Range/hash/list | Large-scale data mgmt |
| 4 | Anti-JOIN patterns | NOT EXISTS, LEFT JOIN NULL | Data quality checks |
| 5 | Aggregation | GROUP BY with complex CASE | Business reporting |

### Microsoft (SQL Server Patterns, Azure)

| Priority | Topic | Problems | Why |
|----------|-------|----------|-----|
| 1 | T-SQL differences | TOP vs ROWNUM, GETDATE vs SYSDATE | SQL Server familiarity |
| 2 | Date/time functions | DATEDIFF, DATEADD patterns | T-SQL patterns |
| 3 | PIVOT | Cross-tabulation | SQL Server pivot similarity |
| 4 | Sorting | ORDER BY with CASE | Custom sort orders |
| 5 | Window functions | T-SQL window syntax | MS SQL Server interviews |

**Microsoft-style SQL pattern:**
```sql
-- T-SQL style for MS interview
SELECT TOP 3 WITH TIES
    e.department_id, e.last_name, e.salary
FROM employees e
ORDER BY ROW_NUMBER() OVER (
    PARTITION BY e.department_id
    ORDER BY e.salary DESC
);
```

---

## APEX-Specific Preparation Path

### What LeetCode SQL Problems to Focus

| Priority | Problem Set | Count | Target Time | Purpose |
|----------|------------|-------|-------------|---------|
| P0 | Company-freq Easy (Oracle, Google, Amazon) | 15 | 2-5 min each | Warm-up, fundamentals |
| P1 | Window functions + CTE Medium | 20 | 10-20 min each | APEX report patterns |
| P2 | GROUP BY + CASE Medium | 10 | 10-15 min each | Report aggregation |
| P3 | Recursive CTE + Hard problems | 5 | 20-30 min each | APEX tree/hierarchy |
| P4 | Performance-focused rewrites | 5 | 15-20 min each | Optimization mindset |

### What System Design Topics to Study

| Topic | APEX Relevance | Study Hours |
|-------|---------------|-------------|
| APEX architecture (rendering, session) | Directly tested | 5 |
| Oracle DB architecture (SGA, PGA, CDB/PDB) | Directly tested | 4 |
| REST API design (ORDS, Web Sources) | Integration questions | 3 |
| Security (auth, authz, VPD, encryption) | High-frequency | 4 |
| Performance (caching, tuning, indexing) | High-frequency | 3 |
| Cloud deployment (OCI, AWS, Azure) | Architecture roles | 3 |
| HA/DR (Data Guard, RAC, failover) | Senior roles | 2 |
| Low-code platform comparison | Broad architecture | 2 |

### Quick Reference: Problem → Lab → Company Mapping

| LeetCode Problem | APEX Lab | Company | Why Important |
|-----------------|----------|---------|---------------|
| 175. Combine Two Tables | Lab 03 | All | Simplest JOIN — APEX report foundation |
| 178. Rank Scores | Lab 04 | Oracle, Amazon, MS | DENSE_RANK = leaderboard reports |
| 180. Consecutive Numbers | Lab 03 | Oracle, Google | LEAD/LAG = trend analysis in APEX |
| 184. Dept Highest Salary | Lab 04 | Oracle, Google, Amazon | Window + JOIN = most common IG pattern |
| 185. Dept Top Three | Lab 04 | Oracle, Google, Amazon | Top-N per group = dashboard staple |
| 550. Game Play Analysis IV | Lab 03 | Google, Amazon | CTE + date = user retention analytics |
| 570. Managers With 5 Reports | Lab 04 | Oracle, Amazon | GROUP BY HAVING = aggregation audit |
| 601. Human Traffic | Lab 03 | Oracle, Google | Complex window = performance reports |
| 626. Exchange Seats | Lab 03 | Oracle, Google, Amazon | LEAD/LAG + CASE = row transformation |
| 1270. All People Report | Lab 03 | Oracle, Google | Recursive CTE = org chart in APEX |
| 1321. Restaurant Growth | Lab 04 | Oracle, Google | Moving average = financial dashboards |
| 1767. Find Subtasks | Lab 03 | Oracle | Recursive CTE = BOM/task hierarchy |

---

<div align="center">

**"Every LeetCode SQL problem you solve is an APEX report you're learning to build. Start with JOINs, master windows, and the rest follows."**

---

[Back to Top](#oracle-apex--neetcode-roadmap)

</div>
