# Study Maps for Advanced SQL

## SQL Window Functions
- OVER() syntax and clauses (PARTITION BY, ORDER BY, ROWS/RANGE)
- ROW_NUMBER, RANK, DENSE_RANK, NTILE
- Aggregate window functions (SUM AVG, MIN, MAX, COUNT)
- LAG/LEAD, FIRST_VALUE/LAST_VALUE
- NTH_VALUE, CUME_DIST, PERCENT_RANK
- Ratio to Report: RATIO_TO_REPORT
- WINDOW clause (Oracle 21c+)

## Recursive CTEs
- Anchor and recursive members
- UNION ALL vs UNION
- LEVEL depth tracking
- CYCLE detection and prevention
- Path concatenation (SYS_CONNECT_BY_PATH equivalent)
- Graph traversal and materialized paths

## PIVOT/UNPIVOT
- PIVOT syntax: aggregate, FOR, IN
- Implicit GROUP BY
- UNPIVOT syntax
- Dynamic PIVOT (XML based)
- Multiple aggregate PIVOT
- Performance vs CASE expression

## MERGE
- MERGE syntax and semantics
- WHEN MATCHED / WHEN NOT MATCHED
- DELETE clause in MERGE
- Error logging in MERGE
- MERGE vs split INSERT+UPDATE
- Performance considerations

## Hierarchical Queries
- CONNECT BY PRIOR syntax
- START WITH (anchor)
- LEVEL pseudo-column
- SYS_CONNECT_BY_PATH
- CONNECT_BY_ISCYCLE, CONNECT_BY_ISLEAF
- ORDER SIBLINGS BY
- NOCYCLE and CYCLE detection

## MODEL Clause
- PARTITION BY, DIMENSION BY MEASURES, RULES
- Positional vs symbolic references
- UPSERT vs UPDATE rules
- AUTOMATIC ORDER vs SEQUENTIAL ORDER
- PRESENTV, PRESENTNNV, CV functions
- ITERATE iteration over rules

## MATCH_RECOGNIZE
- PARTITION BY, ORDER BY MEASURES
- PATTERN definition (row pattern)
- DEFINE clause (conditions per symbol)
- ONE ROW PER MATCH vs ALL ROWS PER MATCH
- PREV and NEXT in conditions
- Quantifiers (* + ? {m,n})

## Query Tuning
- Execution plan reading
- Cost-based optimizer fundamentals
- Cardinality estimation
- Join methods (NL, HJ, SM, CARTESIAN)
- Access paths (FTS, INDEX)
- Query hints
- SQL Tuning Advisor

## Indexes
- B-tree structure and usage
- Bitmap indexes and data warehouse use cases
- Function-based indexes
- Domain indexes (Oracle Text, Spatial)
- Invisible indexes
- Index compression
- Index monitoring and reorganization

## Partitioning
- Range, list, hash, composite
- Partition pruning
- Partition-wise joins
- Global and local indexes
- Partition exchange load
- Reference partitioning
- Interval partitioning

## SQL Plan Management
- SQL plan baselines
- Plan capture, selection, evolution
- SYS.SQL$TEXT and SYS.SQL$PLAN
- DBMS_SPM package
- Plan evolution (EVOLVE_SQL_PLAN_BASELINE)
- Fixed and accepted plans
- Loading plans from SQL Tuning Set

## Optimizer
- DBMS_STATS (GATHER, SET, DELETE, COPY)
- Histograms (frequency, height-balanced, hybrid, top-N)
- Dynamic sampling
- Multi-column statistics
- Expression statistics
- System statistics
- Optimizer parameters

## SQL/JSON and SQL/XML
- JSON_VALUE, JSON_QUERY, JSON_TABLE
- JSON_EXISTS, JSON_OBJECT
- SQL/JSON path expressions
- XMLELEMENT, XMLAGG, XMLTABLE
- DBMS_XMLGEN
- JSON datatype (Oracle 21c+/23c)