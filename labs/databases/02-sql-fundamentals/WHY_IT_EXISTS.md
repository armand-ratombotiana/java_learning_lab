# Why SQL Fundamentals Exist

SQL (Structured Query Language) exists as a **declarative** interface to query and manipulate relational data.

## Why Not Just Use Programming Language APIs?
- **Ad-hoc queries**: Analysts need to ask data questions without writing Java code
- **Set-based operations**: SQL operates on sets, not records – more efficient
- **Portability**: SQL is standardized across all RDBMS vendors
- **Optimization**: The database optimizer can optimize declarative SQL better than procedural code

## Why Java Developers Need SQL
- JDBC executes SQL directly
- JPA generates SQL – understanding it prevents surprises
- Query optimization requires reading EXPLAIN plans
- Stored procedures, views, and triggers are SQL artifacts
- Performance debugging starts with slow query logs
