# Interview Questions: PL/SQL Foundations (Oracle Focus)

## Oracle-Specific Questions
- Explain the PL/SQL block structure: DECLARE, BEGIN, EXCEPTION, END — what is the execution order?
- What are the PL/SQL data types: VARCHAR2, NUMBER, DATE, BOOLEAN, BINARY_INTEGER, PLS_INTEGER — when to use each?
- Explain PL/SQL cursors: implicit vs explicit cursors, cursor FOR loops, and REF CURSORs.
- How does exception handling work in PL/SQL? Explain predefined exceptions, user-defined exceptions, and `EXCEPTION_INIT`.
- What are PL/SQL procedures vs functions — what are the key differences?
- Explain PL/SQL packages: why use package specs vs bodies? How do package variables maintain state?
- What are PL/SQL triggers? Explain DML triggers, INSTEAD OF triggers, and system triggers.
- Explain PL/SQL collections: associative arrays, nested tables, VARRAYs — when to use each?
- How does bulk processing work? Explain `BULK COLLECT` and `FORALL` with `LIMIT` clause.
- What is dynamic SQL in PL/SQL? Explain `EXECUTE IMMEDIATE` and `DBMS_SQL` — when to use each?
- How do PL/SQL transactions work? Explain COMMIT, ROLLBACK, SAVEPOINT, and autonomous transactions (`PRAGMA AUTONOMOUS_TRANSACTION`).

## Google Cloud / Technical
- PL/SQL to Cloud SQL PostgreSQL PL/pgSQL migration
- Cloud Functions vs Oracle stored procedures
- Oracle PL/SQL vs PostgreSQL functions for business logic

## Microsoft / Azure
- T-SQL stored procedures vs Oracle PL/SQL
- Azure SQL managed instances vs Oracle for stored procedures
- SQL Server CLR integration vs Oracle Java stored procedures

## Amazon / AWS
- RDS Oracle PL/SQL — limitations on AWS
- Aurora PostgreSQL PL/pgSQL as PL/SQL alternative
- AWS Lambda vs Oracle PL/SQL for business logic

## Apple
- PL/SQL for Apple enterprise backend services
- Secure PL/SQL coding practices

## LeetCode-Style Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| Cursor | Process rows in batches | Medium | BULK COLLECT |
| Function | Calculate running total | Medium | Analytic Function |
| Procedure | ETL pipeline step | Medium | Error Logging |
| Trigger | Audit trail | Medium | OLD/NEW |
| Package | State management | Medium | Package Variables |

## Production Scenarios
- Scenario 1: "PL/SQL procedure taking 8 hours — rewrite with BULK COLLECT"
- Scenario 2: "Trigger causing mutating table error — restructure with compound trigger"
- Scenario 3: "Package state corrupted between sessions — stateful package issue"
- Scenario 4: "Dynamic SQL injection via user input — EXECUTE IMMEDIATE vulnerability"

## Interview Patterns & Tips
- PL/SQL interviews focus on bulk operations, packages, and performance
- Oracle expects deep understanding of collection types and when to use each
- OCP PL/SQL certification covers Developer and Advanced PL/SQL
- PL/SQL developers: $100K-$160K; Senior PL/SQL: $130K-$190K
- Testing PL/SQL with utPLSQL is a valuable additional skill
