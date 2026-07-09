# Lab 21: Advanced SQL

## Focus
Comprehensive deep-dive into advanced Oracle SQL including window functions, recursive CTEs, PIVOT/UNPIVOT, MERGE, hierarchical queries, MODEL clause, pattern matching, SQL/JSON, and query tuning with Java 21+ implementations.

## Topics Covered
- Window functions (ROW_NUMBER, RANK, DENSE_RANK, NTILE, LAG/LEAD)
- Recursive and non-recursive CTEs
- PIVOT/UNPIVOT operations
- MERGE (upsert) statements
- Hierarchical queries with CONNECT BY
- MODEL clause for spreadsheet-like calculations
- Analytical SQL and pattern matching with MATCH_RECOGNIZE
- Temporal tables and time-based queries
- SQL/JSON and XML functions
- Query tuning with execution plans
- Index types (B-tree, bitmap, function-based, domain)
- Partitioning (range, list, hash, composite)
- Query hints and optimizer statistics
- SQL Plan Management (SPM) and SQL profiles
- Adaptive execution plans

## Prerequisites
- Java 21+ (records, sealed classes, virtual threads, pattern matching)
- Oracle Database or PostgreSQL with advanced features
- Basic SQL knowledge (SELECT, JOIN, GROUP BY)
- Familiarity with Maven builds
- Docker Desktop for containerized database

## Lab Structure
| Directory | Content |
|-----------|---------|
| src/main/java/com/databases/advsql/ | Production-ready Java implementations |
| src/test/java/com/databases/advsql/ | JUnit 5 unit tests |
| BENCHMARK/ | Performance benchmarks using JMH |
| CHALLENGE/ | Advanced challenge exercises |
| DIAGRAMS/ | Architecture and flow diagrams |
| MINI_PROJECT/ | Guided mini-project with requirements |
| REAL_WORLD_PROJECT/ | Full-scale project specification |
| SOLUTION/ | Exercise solutions |
| TESTS/ | Additional test scenarios and fixtures |

## Learning Objectives
By completing this lab, you will be able to:
- Write advanced analytical SQL queries using window functions
- Implement recursive CTEs for hierarchical and graph data
- Transform rows to columns and vice versa with PIVOT/UNPIVOT
- Perform upsert operations with MERGE
- Query hierarchical data with CONNECT BY
- Use MODEL clause for inter-row calculations
- Apply pattern matching with MATCH_RECOGNIZE
- Tune queries using execution plans and optimizer hints
- Design partitioning strategies for large tables
- Understand and apply SQL Plan Management