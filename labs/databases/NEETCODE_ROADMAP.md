# Oracle Database — NEETCODE Roadmap

<div align="center">

![Roadmap](https://img.shields.io/badge/Interview_Roadmap-000000?style=for-the-badge)
![LeetCode](https://img.shields.io/badge/LeetCode_DB-FFA116?style=for-the-badge&logo=leetcode&logoColor=black)
![OCP](https://img.shields.io/badge/OCP_Integration-004d99?style=for-the-badge)

**Structured study plans mapping LeetCode problems, Oracle certification topics, and academy labs**

</div>

---

## Table of Contents

1. [LeetCode Database Section — Complete Problem Map](#1-leetcode-database-section--complete-problem-map)
2. [Oracle Certification Integration](#2-oracle-certification-integration)
3. [4-Week SQL Interview Crash Course](#3-4-week-sql-interview-crash-course)
4. [8-Week Oracle Database Professional](#4-8-week-oracle-database-professional)
5. [12-Week Oracle Database Expert](#5-12-week-oracle-database-expert)
6. [Company-Specific Prep Paths](#6-company-specific-prep-paths)

---

## 1. LeetCode Database Section — Complete Problem Map

### Easy (LC 175–1300+)

| LC # | Problem | Pattern | Lab | Difficulty |
|------|---------|---------|-----|------------|
| 175 | Combine Two Tables | LEFT JOIN, NULL handling | 02-SQL Fundamentals | ★☆☆☆☆ |
| 176 | Second Highest Salary | Subquery, window, OFFSET | 02-SQL Fundamentals | ★☆☆☆☆ |
| 177 | Nth Highest Salary | Function, window | 02-SQL Fundamentals | ★★☆☆☆ |
| 178 | Rank Scores | RANK, DENSE_RANK | 05-Advanced SQL | ★☆☆☆☆ |
| 179 | Largest Number | String functions, sorting | 02-SQL Fundamentals | ★★☆☆☆ |
| 181 | Employees Earning More Than Managers | Self JOIN | 02-SQL Fundamentals | ★☆☆☆☆ |
| 182 | Duplicate Emails | GROUP BY, HAVING | 02-SQL Fundamentals | ★☆☆☆☆ |
| 183 | Customers Who Never Order | LEFT JOIN, anti-join | 02-SQL Fundamentals | ★☆☆☆☆ |
| 184 | Department Highest Salary | Correlated subquery, window | 05-Advanced SQL | ★★☆☆☆ |
| 185 | Department Top Three Salaries | DENSE_RANK, window filtering | 05-Advanced SQL | ★★★☆☆ |
| 196 | Delete Duplicate Emails | Self JOIN, correlated delete | 02-SQL Fundamentals | ★★☆☆☆ |
| 197 | Rising Temperature | Self JOIN, LAG, date diff | 02-SQL Fundamentals | ★★☆☆☆ |
| 511 | Game Play Analysis I | GROUP BY, MIN | 02-SQL Fundamentals | ★☆☆☆☆ |
| 512 | Game Play Analysis II | ROW_NUMBER, correlated | 05-Advanced SQL | ★★☆☆☆ |
| 534 | Game Play Analysis III | SUM OVER, running total | 05-Advanced SQL | ★★☆☆☆ |
| 550 | Game Play Analysis IV | Window, date arithmetic | 05-Advanced SQL | ★★★☆☆ |
| 569 | Median Employee Salary | NTILE, window | 05-Advanced SQL | ★★★☆☆ |
| 570 | Managers with at Least 5 Direct Reports | GROUP BY, HAVING, subquery | 02-SQL Fundamentals | ★★☆☆☆ |
| 571 | Find Median Given Frequency of Numbers | Cumulative sum, window | 05-Advanced SQL | ★★★☆☆ |
| 577 | Employee Bonus | LEFT JOIN, COALESCE | 02-SQL Fundamentals | ★☆☆☆☆ |
| 578 | Get Highest Answer Rate Question | Aggregation, subquery | 02-SQL Fundamentals | ★★★☆☆ |
| 579 | Find Cumulative Salary of an Employee | Window, RANGE frame | 05-Advanced SQL | ★★★☆☆ |
| 580 | Count Student Number in Departments | LEFT JOIN, GROUP BY | 02-SQL Fundamentals | ★★☆☆☆ |
| 584 | Find Customer Referee | NULL handling, `<>` | 02-SQL Fundamentals | ★☆☆☆☆ |
| 585 | Investments in 2016 | Self JOIN, subquery | 02-SQL Fundamentals | ★★★☆☆ |
| 586 | Customer Placing the Largest Number of Orders | Aggregation, ORDER BY + LIMIT | 02-SQL Fundamentals | ★☆☆☆☆ |
| 595 | Big Countries | WHERE, OR | 02-SQL Fundamentals | ★☆☆☆☆ |
| 596 | Classes More Than 5 Students | GROUP BY, HAVING | 02-SQL Fundamentals | ★☆☆☆☆ |
| 597 | Friend Requests I: Overall Acceptance Rate | Aggregation, subquery | 02-SQL Fundamentals | ★★☆☆☆ |
| 601 | Human Traffic of Stadium | Self JOIN, window, CTE | 05-Advanced SQL | ★★★☆☆ |
| 602 | Friend Requests II: Who Has the Most Friends | UNION ALL, aggregation | 02-SQL Fundamentals | ★★★☆☆ |
| 603 | Consecutive Available Seats | Self JOIN, LAG/LEAD | 02-SQL Fundamentals | ★★☆☆☆ |
| 607 | Sales Person | Subquery, NOT IN | 02-SQL Fundamentals | ★★☆☆☆ |
| 608 | Tree Node | CASE, subquery, CONNECT BY | 05-Advanced SQL | ★★☆☆☆ |
| 610 | Triangle Judgement | CASE, COALESCE | 02-SQL Fundamentals | ★☆☆☆☆ |
| 612 | Shortest Distance in a Plane | Self JOIN, Euclidean distance | 02-SQL Fundamentals | ★★☆☆☆ |
| 613 | Shortest Distance in a Line | Self JOIN, ABS, MIN | 02-SQL Fundamentals | ★★☆☆☆ |
| 614 | Second Degree Follower | Self JOIN, GROUP BY | 02-SQL Fundamentals | ★★★☆☆ |
| 615 | Average Salary: Departments vs Company | Window AVG, date filter | 05-Advanced SQL | ★★★☆☆ |
| 618 | Students Report By Geography | PIVOT, ROW_NUMBER | 05-Advanced SQL | ★★★☆☆ |
| 619 | Biggest Single Number | GROUP BY, HAVING, MAX | 02-SQL Fundamentals | ★☆☆☆☆ |
| 620 | Not Boring Movies | MOD, WHERE, ORDER BY | 02-SQL Fundamentals | ★☆☆☆☆ |
| 626 | Exchange Seats | CASE, LEAD, LAG, subquery | 02-SQL Fundamentals | ★★☆☆☆ |
| 627 | Swap Salary | CASE / DECODE, UPDATE | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1045 | Customers Who Bought All Products | GROUP BY, HAVING, COUNT subquery | 02-SQL Fundamentals | ★★☆☆☆ |
| 1050 | Actors and Directors Who Cooperated At Least Three Times | GROUP BY, HAVING | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1068 | Product Sales Analysis I | INNER JOIN | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1069 | Product Sales Analysis II | JOIN, GROUP BY, SUM | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1070 | Product Sales Analysis III | ROW_NUMBER, window | 05-Advanced SQL | ★★☆☆☆ |
| 1075 | Project Employees I | JOIN, AVG, GROUP BY | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1076 | Project Employees II | GROUP BY, subquery, MAX | 02-SQL Fundamentals | ★★☆☆☆ |
| 1077 | Project Employees III | ROW_NUMBER, window | 05-Advanced SQL | ★★☆☆☆ |
| 1082 | Sales Analysis I | JOIN, GROUP BY, SUM | 02-SQL Fundamentals | ★★☆☆☆ |
| 1083 | Sales Analysis II | JOIN, GROUP BY, HAVING, subquery | 02-SQL Fundamentals | ★★★☆☆ |
| 1084 | Sales Analysis III | JOIN, GROUP BY, date functions | 02-SQL Fundamentals | ★★☆☆☆ |
| 1097 | Game Play Analysis V | Date arithmetic, window | 05-Advanced SQL | ★★★☆☆ |
| 1098 | Unpopular Books | JOIN, WHERE, date filter | 02-SQL Fundamentals | ★★☆☆☆ |
| 1107 | New Users Daily Count | Date functions, aggregation | 02-SQL Fundamentals | ★★☆☆☆ |
| 1112 | Highest Grade For Each Student | ROW_NUMBER, window | 05-Advanced SQL | ★★☆☆☆ |
| 1113 | Reported Posts | GROUP BY, date functions | 02-SQL Fundamentals | ★☆☆☆☆ |

### Medium (LC 1120–1900+)

| LC # | Problem | Pattern | Lab | Difficulty |
|------|---------|---------|-----|------------|
| 1126 | Active Businesses | HAVING with multiple conditions | 02-SQL Fundamentals | ★★★☆☆ |
| 1132 | Reported Posts II | CTE, aggregation, date filter | 05-Advanced SQL | ★★★☆☆ |
| 1141 | User Activity for the Past 30 Days I | Date truncation, GROUP BY | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1142 | User Activity for the Past 30 Days II | AVG of COUNT, subquery | 02-SQL Fundamentals | ★★☆☆☆ |
| 1148 | Article Views I | DISTINCT, ORDER BY | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1149 | Article Views II | Subquery, GROUP BY | 02-SQL Fundamentals | ★★☆☆☆ |
| 1158 | Market Analysis I | LEFT JOIN, aggregation | 02-SQL Fundamentals | ★★☆☆☆ |
| 1159 | Market Analysis II | Window, ROW_NUMBER | 05-Advanced SQL | ★★★☆☆ |
| 1164 | Product Price at a Given Date | CTE, window, date filter | 05-Advanced SQL | ★★★☆☆ |
| 1173 | Immediate Food Delivery I | Aggregation, date functions | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1174 | Immediate Food Delivery II | ROW_NUMBER, window | 05-Advanced SQL | ★★★☆☆ |
| 1179 | Reformat Department Table | PIVOT, aggregation | 05-Advanced SQL | ★★★☆☆ |
| 1193 | Monthly Transactions I | Date truncation, CASE, aggregation | 02-SQL Fundamentals | ★★☆☆☆ |
| 1204 | Last Person to Fit in the Bus | Running total, window, correlated | 05-Advanced SQL | ★★★☆☆ |
| 1205 | Monthly Transactions II | CTE, date aggregation | 05-Advanced SQL | ★★★☆☆ |
| 1211 | Queries Quality and Percentage | Aggregation, CASE, AVG | 02-SQL Fundamentals | ★★★☆☆ |
| 1212 | Team Scores in Football Tournament | UNION ALL, aggregation | 02-SQL Fundamentals | ★★★☆☆ |
| 1241 | Number of Comments per Post | LEFT JOIN, GROUP BY | 02-SQL Fundamentals | ★★☆☆☆ |
| 1251 | Average Selling Price | JOIN, date range, aggregation | 02-SQL Fundamentals | ★★★☆☆ |
| 1264 | Page Recommendations | UNION ALL, subquery, anti-join | 02-SQL Fundamentals | ★★★☆☆ |
| 1270 | All People Report to the Given Manager | Recursive CTE, CONNECT BY | 05-Advanced SQL | ★★★☆☆ |
| 1280 | Students and Examinations | CROSS JOIN, LEFT JOIN, GROUP BY | 02-SQL Fundamentals | ★★☆☆☆ |
| 1285 | Find the Start and End Number of Continuous Ranges | CTE, ROW_NUMBER, difference | 05-Advanced SQL | ★★★☆☆ |
| 1294 | Weather Type in Each Country | CASE, date filter, aggregation | 02-SQL Fundamentals | ★★☆☆☆ |
| 1303 | Find the Team Size | Subquery, JOIN | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1308 | Running Total for Different Genders | SUM OVER, running total | 05-Advanced SQL | ★★☆☆☆ |
| 1321 | Restaurant Growth | SUM OVER with range frame | 05-Advanced SQL | ★★★☆☆ |
| 1322 | Ads Performance | CASE, aggregation, GROUP BY | 02-SQL Fundamentals | ★★☆☆☆ |
| 1327 | List the Products Ordered in a Period | JOIN, date filter, GROUP BY | 02-SQL Fundamentals | ★★☆☆☆ |
| 1336 | Number of Transactions per Visit | JOIN, CTE, window | 05-Advanced SQL | ★★★☆☆ |
| 1341 | Movie Rating | UNION ALL, subquery, window | 05-Advanced SQL | ★★★☆☆ |
| 1350 | Students With Invalid Departments | LEFT JOIN, anti-join | 02-SQL Fundamentals | ★★☆☆☆ |
| 1355 | Activity Participants | Window, subquery | 05-Advanced SQL | ★★★☆☆ |
| 1364 | Number of Trusted Contacts of a Customer | LEFT JOIN, aggregation | 02-SQL Fundamentals | ★★★☆☆ |
| 1369 | Get the Second Most Recent Activity | ROW_NUMBER, window | 05-Advanced SQL | ★★★☆☆ |
| 1378 | Replace Employee ID With The Unique Identifier | LEFT JOIN | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1384 | Total Sales Amount by Year | Recursive CTE, date arithmetic | 05-Advanced SQL | ★★★☆☆ |
| 1393 | Capital Gain/Loss | CASE / DECODE, aggregation | 02-SQL Fundamentals | ★★☆☆☆ |
| 1398 | Customers Who Bought Products A and B but Not C | Subquery, NOT IN, EXISTS | 02-SQL Fundamentals | ★★★☆☆ |
| 1407 | Top Travellers | LEFT JOIN, GROUP BY, ORDER BY | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1412 | Find the Quiet Students in All Exams | Window, subquery, aggregation | 05-Advanced SQL | ★★★☆☆ |
| 1421 | NPV Queries | LEFT JOIN, COALESCE | 02-SQL Fundamentals | ★★☆☆☆ |
| 1435 | Create a Session Bar Chart | CASE, GROUP BY, GENERATE_SERIES | 02-SQL Fundamentals | ★★★☆☆ |
| 1440 | Evaluate Boolean Expression | CASE, JOIN, OR | 02-SQL Fundamentals | ★★☆☆☆ |
| 1445 | Apples & Oranges | CASE, aggregation, date comparison | 02-SQL Fundamentals | ★★★☆☆ |
| 1454 | Active Users | LAG, CTE, date difference | 05-Advanced SQL | ★★★☆☆ |

### Hard (LC 1460–2200+)

| LC # | Problem | Pattern | Lab | Difficulty |
|------|---------|---------|-----|------------|
| 1468 | Calculate Salaries | Window, aggregation, date | 05-Advanced SQL | ★★★★☆ |
| 1479 | Sales by Day of the Week | PIVOT, aggregation | 05-Advanced SQL | ★★★☆☆ |
| 1484 | Group Sold Products By The Date | LISTAGG, GROUP BY | 02-SQL Fundamentals | ★★☆☆☆ |
| 1495 | Friendly Movies Streamed Last Month | JOIN, date filter | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1501 | Countries You Can Safely Invest In | JOIN, aggregation, HAVING | 02-SQL Fundamentals | ★★★☆☆ |
| 1511 | Customer Order Frequency | JOIN, aggregation, grouping | 02-SQL Fundamentals | ★★★☆☆ |
| 1517 | Find Users With Valid E-Mails | REGEXP_LIKE | 02-SQL Fundamentals | ★★☆☆☆ |
| 1527 | Patients With a Condition | REGEXP_LIKE, LIKE | 02-SQL Fundamentals | ★★☆☆☆ |
| 1532 | The Most Recent Three Orders | ROW_NUMBER, window | 05-Advanced SQL | ★★★☆☆ |
| 1543 | Fix Product Name Format | TRIM, UPPER, LEFT, SUBSTR | 02-SQL Fundamentals | ★★☆☆☆ |
| 1549 | The Most Recent Orders for Each Product | ROW_NUMBER, window | 05-Advanced SQL | ★★★☆☆ |
| 1555 | Bank Account Summary | JOIN, aggregation, HAVING | 02-SQL Fundamentals | ★★★☆☆ |
| 1565 | Unique Orders and Customers Per Month | Date truncation, DISTINCT COUNT | 02-SQL Fundamentals | ★★☆☆☆ |
| 1571 | Warehouse Manager | JOIN, aggregation | 02-SQL Fundamentals | ★★★☆☆ |
| 1581 | Customer Who Visited but Did Not Make Any Transactions | LEFT JOIN, anti-join | 02-SQL Fundamentals | ★★☆☆☆ |
| 1587 | Bank Account Summary II | JOIN, GROUP BY, HAVING | 02-SQL Fundamentals | ★★☆☆☆ |
| 1596 | The Most Frequently Ordered Products for Each Customer | ROW_NUMBER, window | 05-Advanced SQL | ★★★☆☆ |
| 1607 | Sellers With No Sales | LEFT JOIN, anti-join | 02-SQL Fundamentals | ★★☆☆☆ |
| 1613 | Find the Missing IDs | Recursive CTE, NOT IN | 05-Advanced SQL | ★★★☆☆ |
| 1623 | All Valid Triplets | JOIN, aggregation | 02-SQL Fundamentals | ★★★☆☆ |
| 1633 | Percentage of Users Attended a Contest | JOIN, aggregation, subquery | 02-SQL Fundamentals | ★★★☆☆ |
| 1635 | Hopper Company Queries I | Recursive CTE, date series | 05-Advanced SQL | ★★★★☆ |
| 1645 | Hopper Company Queries II | Recursive CTE, date series | 05-Advanced SQL | ★★★★☆ |
| 1651 | Hopper Company Queries III | Recursive CTE, window | 05-Advanced SQL | ★★★★☆ |
| 1661 | Average Time of Process per Machine | JOIN, date functions, AVG | 02-SQL Fundamentals | ★★☆☆☆ |
| 1667 | Fix Names in a Table | String functions, SUBSTR | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1677 | Product's Worth Over Invoices | JOIN, aggregation | 02-SQL Fundamentals | ★★☆☆☆ |
| 1683 | Invalid Tweets | LENGTH | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1693 | Daily Leads and Partners | DISTINCT, GROUP BY, date | 02-SQL Fundamentals | ★★☆☆☆ |
| 1699 | Number of Calls Between Two Persons | LEAST, GREATEST, GROUP BY | 02-SQL Fundamentals | ★★★☆☆ |
| 1709 | Biggest Window Between Visits | LAG, date difference, window | 05-Advanced SQL | ★★★☆☆ |
| 1715 | Count Apples and Oranges | JOIN, aggregation | 02-SQL Fundamentals | ★★★☆☆ |
| 1729 | Find Followers Count | GROUP BY, ORDER BY | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1731 | The Number of Employees Which Report to Each Employee | Self JOIN, aggregation | 02-SQL Fundamentals | ★★☆☆☆ |
| 1741 | Find Total Time Spent by Each Employee | Date functions, SUM | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1747 | Leetflex Banned Accounts | Self JOIN, date overlap | 02-SQL Fundamentals | ★★★☆☆ |
| 1757 | Recyclable and Low Fat Products | WHERE | 02-SQL Fundamentals | ★☆☆☆☆ |
| 1767 | Find the Subtasks That Did Not Execute | Recursive CTE, anti-join | 05-Advanced SQL | ★★★★★ |
| 1777 | Product's Price for Each Store | PIVOT | 05-Advanced SQL | ★★★☆☆ |
| 1783 | Grand Slam Titles | UNION ALL, aggregation | 02-SQL Fundamentals | ★★★☆☆ |
| 1789 | Primary Department for Each Employee | UNION, boolean logic | 02-SQL Fundamentals | ★★★☆☆ |

### Oracle-Specific LeetCode Problems

| LC # | Problem | Oracle Feature | Lab |
|------|---------|---------------|-----|
| 1767 | Find the Subtasks That Did Not Execute | Recursive CTE, CONNECT BY | 23-PL/SQL Advanced |
| 1270 | All People Report to the Given Manager | Recursive CTE, CONNECT BY | 23-PL/SQL Advanced |
| 618 | Students Report By Geography | PIVOT | 21-Advanced SQL |
| 608 | Tree Node | CASE, CONNECT BY | 21-Advanced SQL |
| 179 | Largest Number | String comparison | 02-SQL Fundamentals |
| 626 | Exchange Seats | LEAD, LAG, CASE | 21-Advanced SQL |
| 601 | Human Traffic of Stadium | Self JOIN, CTE | 21-Advanced SQL |
| 571 | Find Median Given Frequency | Window, cumulative sum | 22-PL/SQL Foundations |

---

## 2. Oracle Certification Integration

### OCP 1Z0-071 (Oracle SQL) → Lab Mappings

| Exam Topic | Lab | LeetCode Problems |
|------------|-----|-------------------|
| SELECT statements | 02-SQL Fundamentals | 175, 181, 182, 183 |
| Restricting/sorting data | 02-SQL Fundamentals | 595, 620, 584 |
| Single-row functions | 02-SQL Fundamentals | 1667, 1683, 1527 |
| Conversion functions | 02-SQL Fundamentals | 610, 1393 |
| GROUP BY, HAVING | 02-SQL Fundamentals | 596, 570, 1045 |
| Joins | 02-SQL Fundamentals | 175, 181, 577 |
| Subqueries | 02-SQL Fundamentals | 176, 183, 607 |
| Set operators | 02-SQL Fundamentals | 182, 1965 |
| DML (INSERT, UPDATE, DELETE, MERGE) | 02-SQL Fundamentals | 196, 627 |
| DDL (CREATE, ALTER, DROP) | 02-SQL Fundamentals | — |
| Views, sequences, synonyms | 02-SQL Fundamentals | — |

### OCP 1Z0-082/083 (Oracle DBA) → Lab Mappings

| Exam Topic | Lab | Key Command |
|------------|-----|-------------|
| Oracle architecture | 16-Oracle Architecture | SGA, PGA, background processes |
| Instance management | 16-Oracle Architecture | `STARTUP`, `SHUTDOWN` |
| Storage structures | 13-Database Sharding | ASM, datafiles, tablespaces |
| Networking | 07-Spring Data | Listener, tnsnames.ora |
| Backup/recovery | 19-Backup Recovery | RMAN, `RESTORE`, `RECOVER` |
| Performance tuning | 11-Query Optimization | AWR, ADDM, SQL Tuning Advisor |
| Security | 20-Database Security | TDE, VPD, Audit Vault |
| Multitenant architecture | 16-Oracle Architecture | CDB, PDB operations |
| Data Guard | 14-Database Replication | `ALTER DATABASE SET STANDBY` |
| RAC | 14-Database Replication | Clusterware, Cache Fusion |

### OCM Practical Exam → Lab Focus

| Domain | Weight | Labs to Master |
|--------|--------|----------------|
| Database architecture & installation | 15% | 16-Oracle Architecture |
| Database configuration & manageability | 15% | 11-Query Optimization |
| Performance management | 20% | 15-Query Optimization |
| Backup & recovery | 20% | 19-Backup Recovery |
| Data management | 10% | 21-Advanced SQL |
| Data Guard & replication | 10% | 14-Database Replication |
| RAC & grid infrastructure | 10% | 14-Database Replication |

---

## 3. 4-Week SQL Interview Crash Course

**Goal:** Solve top 30 LeetCode SQL problems, one pattern per day

| Week | Day | Pattern | Problems | Reading |
|------|-----|---------|----------|---------|
| **1** | 1 | Basic SELECT + WHERE | 595, 1757 | Lab 02 |
| **1** | 2 | INNER JOIN | 181, 1068 | Lab 02 |
| **1** | 3 | LEFT JOIN + NULL | 175, 183, 577 | Lab 02 |
| **1** | 4 | Self JOIN | 181 (again), 570 | Lab 02 |
| **1** | 5 | GROUP BY + HAVING | 182, 596, 570 | Lab 02 |
| **1** | 6 | CASE + DECODE | 608, 627, 1393 | Lab 02 |
| **1** | 7 | Subqueries (IN, NOT IN) | 176, 183, 607 | Lab 02 |
| **2** | 8 | Correlated subqueries | 184, 585 | Lab 21 |
| **2** | 9 | EXISTS vs IN | 570, 1045 | Lab 21 |
| **2** | 10 | ROW_NUMBER + RANK | 176, 177, 178 | Lab 21 |
| **2** | 11 | DENSE_RANK + top-N | 184, 185 | Lab 21 |
| **2** | 12 | LAG + LEAD | 197, 180, 603 | Lab 21 |
| **2** | 13 | Running totals (SUM OVER) | 534, 1308 | Lab 21 |
| **2** | 14 | Moving averages | 1321 | Lab 21 |
| **3** | 15 | CTE basics | 184, 585 | Lab 21 |
| **3** | 16 | Multiple CTEs | 262, 601 | Lab 21 |
| **3** | 17 | Recursive CTE | 1270, 1613 | Lab 21 |
| **3** | 18 | Date functions | 197, 511 | Lab 02 |
| **3** | 19 | Date truncation | 1141, 1193 | Lab 02 |
| **3** | 20 | String functions | 1667, 1683 | Lab 02 |
| **3** | 21 | LISTAGG | 1484 | Lab 21 |
| **3** | 22 | PIVOT | 618, 1179 | Lab 21 |
| **4** | 23 | UNION / UNION ALL | 602, 1965 | Lab 02 |
| **4** | 24 | INTERSECT / MINUS | 1965 | Lab 02 |
| **4** | 25 | MERGE (UPSERT) | (scenario) | Lab 02 |
| **4** | 26 | Oracle CONNECT BY | 608, 1270 | Lab 21 |
| **4** | 27 | FLASHBACK | (scenario) | Lab 16 |
| **4** | 28 | Performance — EXPLAIN PLAN | — | Lab 11 |
| **4** | 29 | Performance — Index strategy | — | Lab 11 |
| **4** | 30 | Mock interview + review | — | All |

---

## 4. 8-Week Oracle Database Professional

**Goal:** SQL + PL/SQL + architecture — mid-level DBA role

| Week | Focus | Labs | LeetCode | Certification Topic |
|------|-------|------|----------|---------------------|
| 1 | SQL fundamentals | 01, 02 | 175–627 (Easy) | 1Z0-071: SELECT, filtering |
| 2 | Joins + set operations | 02 | 175–614 (Medium) | 1Z0-071: Joins, set operators |
| 3 | Subqueries + CTEs | 21 | 176–1270 | 1Z0-071: Subqueries |
| 4 | Window functions + advanced SQL | 21 | 178–1321 | 1Z0-071: Analytic functions |
| 5 | PL/SQL fundamentals | 22 | — | 1Z0-071: Anonymous blocks |
| 6 | PL/SQL cursors + exceptions | 22 | — | 1Z0-082: PL/SQL |
| 7 | PL/SQL procedures + packages | 23 | — | 1Z0-082: Stored code |
| 8 | Oracle architecture + instance mgmt | 16 | — | 1Z0-082: Memory, processes |

### Milestone: End of Week 8 — Ready for OCP 1Z0-071 + Junior DBA roles

---

## 5. 12-Week Oracle Database Expert

**Goal:** All topics + performance + HA — senior DBA / architect role

| Week | Focus | Labs | LeetCode | Certification Topic |
|------|-------|------|----------|---------------------|
| 1-2 | SQL mastery review | 01, 02, 21 | All Easy + Medium | 1Z0-071 review |
| 3 | Advanced SQL patterns | 21 | Hard (1384, 1635, 1767) | — |
| 4 | PL/SQL triggers + advanced | 23 | — | 1Z0-082: Triggers, packages |
| 5 | Indexing + query optimization | 11 | — | 1Z0-082: Indexes, statistics |
| 6 | Performance tuning | 15 | — | 1Z0-082: AWR, ADDM, ASH |
| 7 | Data modeling + normalization | 03, 12 | — | 1Z0-082: Data design |
| 8 | Backup + recovery | 19 | — | 1Z0-083: RMAN, Flashback |
| 9 | Security | 20 | — | 1Z0-083: TDE, VPD, Audit |
| 10 | RAC + Data Guard | 14 | — | 1Z0-083: HA architecture |
| 11 | Performance + scalability review | 11, 13, 15 | — | OCM domains 3, 4 |
| 12 | Final review + mock interview | All | Review weak spots | OCP 1Z0-082/083 |

### Milestone: End of Week 12 — Ready for OCP 1Z0-082/083 + Senior DBA / Database Architect roles

---

## 6. Company-Specific Prep Paths

### Oracle (Architecture + Deep SQL + Data Guard)

| Priority | Topic | Lab | Hours | Why |
|----------|-------|-----|-------|-----|
| ★★★★★ | Oracle Architecture (SGA, PGA, processes) | 16 | 8 | Core interview topic |
| ★★★★★ | RAC vs Data Guard trade-offs | 14 | 6 | Every Oracle interview |
| ★★★★☆ | Advanced SQL (CONNECT BY, MODEL, PIVOT) | 21 | 8 | Expected at senior levels |
| ★★★★☆ | PL/SQL performance (bulk collect, FORALL) | 23 | 6 | Performance-critical |
| ★★★★☆ | RMAN backup/recovery | 19 | 6 | Interview favorite |
| ★★★☆☆ | ASM + ACFS storage | 16 | 4 | DBA-specific |
| ★★★☆☆ | Flashback technologies | 19 | 4 | Common scenario Q |
| ★★☆☆☆ | Exadata + In-Memory | 16 | 3 | Senior role differentiator |

**LeetCode focus:** Hierarchical (CONNECT BY), window functions, PIVOT, recursive CTE

### Amazon (DynamoDB vs Oracle + Migration)

| Topic | Lab | Hours | Key Questions |
|-------|-----|-------|---------------|
| SQL advanced querying | 21 | 6 | LC 176, 177, 185, 262 |
| RDS Oracle vs DynamoDB | 17 | 4 | When to use NoSQL vs SQL |
| Database migration | 19 | 6 | DMS, GoldenGate, replication |
| Aurora vs Oracle | 14 | 4 | Compatibility, performance |
| Query optimization | 11 | 6 | Indexes, partitions, EXPLAIN |

**LeetCode focus:** Window functions, CTEs, complex joins. Amazon asks medium-hard SQL.
**Behavioral:** "Tell me about a time a slow query impacted customers."

### Google (Spanner + Distributed DB Concepts)

| Topic | Lab | Hours | Key Questions |
|-------|-----|-------|---------------|
| SQL + analytic functions | 21 | 6 | LC 178, 185, 1321 |
| Distributed SQL concepts | 14 | 4 | CAP theorem, Spanner |
| NoSQL (Bigtable, Firestore) | 12 | 4 | When to use each |
| Transactions (ACID vs BASE) | 14 | 4 | Spanner TrueTime |
| Schema design | 03 | 4 | Partitioning, interleaving |

**LeetCode focus:** Hard problems (1384, 1635, 1767). Google values creative solutions.
**System design:** "Design a globally distributed relational database."

### Microsoft (SQL Server vs Oracle)

| Topic | Lab | Hours | Key Questions |
|-------|-----|-------|---------------|
| SQL syntax differences | 02 | 4 | TOP vs ROWNUM, GETDATE vs SYSDATE |
| Migration SSMA | 19 | 4 | SQL Server → Oracle |
| Azure SQL vs Oracle | 02 | 4 | Managed DB comparison |
| T-SQL vs PL/SQL | 22 | 6 | Cursor differences, error handling |

**LeetCode focus:** All patterns. Microsoft asks balanced SQL + system design.

### Meta (Database Infrastructure)

| Topic | Lab | Hours | Key Questions |
|-------|-----|-------|---------------|
| SQL optimization | 11 | 6 | EXPLAIN, index strategy |
| Replication + sharding | 14 | 6 | MySQL-style vs Oracle |
| Performance at scale | 15 | 4 | Connection pooling, caching |

**LeetCode focus:** Medium problems (570, 180, 602). Meta emphasizes query correctness.

### Apple (Database Services)

| Topic | Lab | Hours | Key Questions |
|-------|-----|-------|---------------|
| Data privacy + security | 20 | 4 | VPD, TDE, audit |
| SQL proficiency | 02 | 6 | LC 175, 176, 181 |
| Core Data + FoundationDB | 12 | 4 | Apple-specific stacks |
| Query performance | 11 | 4 | Indexing, optimization |

**LeetCode focus:** Easy-medium with focus on correctness and edge cases.

---

> **Pro Tip:** The LeetCode Database section has ~180 problems as of 2025. Master the top 30–50 by pattern rather than grinding all 180. Focus especially on: window functions (LC 176, 177, 178, 184, 185), CTEs (LC 262, 1270), and date arithmetic (LC 197, 1321). For Oracle-specific roles, CONNECT BY (LC 608, 1270) and PIVOT (LC 618) are essential.
