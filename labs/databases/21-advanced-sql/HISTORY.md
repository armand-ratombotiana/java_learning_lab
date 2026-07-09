# History of Advanced SQL

## Pre-Relational Era (1960s-1970s)
Before SQL, data was accessed through hierarchical (IMS) and network (CODASYL) databases. Querying required navigating pointer chains. IBM's System R (1974) introduced SQL as a declarative query language.

## SQL Standardization
- SQL-86: First ANSI standard
- SQL-89: Minor revisions
- SQL-92: Major revision (entry, intermediate, full levels)
- SQL:1999: Recursive queries (WITH RECURSIVE), triggers, OLAP
- SQL:2003: Window functions, PIVOT, MERGE, XML
- SQL:2008: TRUNCATE, INSTEAD OF triggers
- SQL:2011: Temporal tables, enhanced window functions
- SQL:2016: JSON, polymorphic table functions
- SQL:2019: Multi-dimensional arrays, property graph queries
- SQL:2023: Property graph queries (SQL/PGQ)

## Oracle SQL Evolution
- Oracle 7 (1991): PL/SQL, stored procedures, snapshot (MV)
- Oracle 8 (1997): Partitioning, object-relational features
- Oracle 8i (1999): Function-based indexes, bitmap indexes
- Oracle 9i (2001): CASE expressions, MERGE, flashback query
- Oracle 10g (2003): MODEL clause, recycle bin, ASM
- Oracle 11g (2007): PIVOT/UNPIVOT, result cache, SQL Profile
- Oracle 12c (2013): MATCH_RECOGNIZE, adaptive plans, temporal
- Oracle 18c/19c: SQL macros, polymorphic table functions
- Oracle 21c: SQL/WINDOW, blockchain tables, GRAPH
- Oracle 23c: AI/ML in SQL, property graph queries, improved JSON

## The Rise of Analytical SQL
Before window functions, reporting required self-joins or multiple passes. Window functions enabled "SQL for analytics" — one query could compute detailed rows plus aggregates.

## PIVOT/UNPIVOT Origins
PIVOT emerged from the need to transform ETL data. Before PIVOT, CASE expressions did the work. Oracle 11g made it a first-class operation.

## MATCH_RECOGNIZE Origins
MATCH_RECOGNIZE was adopted from the stream processing community. It brings complex event processing (CEP) capabilities into SQL — pattern matching over ordered sequences.

## The Optimizer Journey
Rule-Based Optimizer (RBO) used hardcoded heuristics (path 1-15). Cost-Based Optimizer (CBO) uses statistics. Modern adaptive optimizers learn from actual execution and adjust plans on-the-fly.

## Partitioning History
Oracle 8 introduced range partitioning. 8i added hash, 9i added composite, 10g added list, 11g added reference and interval, 12c added interval-reference and automatic.

## Index History
B-trees were the first general-purpose index. Bitmap indexes came in Oracle 8i for warehousing. Function-based indexes enabled indexing expressions. Domain indexes extend to non-standard data types (text, spatial).

## SPM Origins
Before SPM, plan changes could cause unpredictable performance regression. SQL Plan Management (11g) captures plan baselines and only uses accepted plans. This gave DBAs control over query plan evolution.

## The Oracle Unique Value Proposition
- CONNECT BY (since Oracle 7) — hierarchical queries before SQL standard
- MODEL clause — spreadsheet calculations in SQL
- Flashback Query — temporal querying without temporal tables
- Materialized View Query Rewrite — transparent acceleration
- Bitmap indexes — unique to Oracle for data warehousing