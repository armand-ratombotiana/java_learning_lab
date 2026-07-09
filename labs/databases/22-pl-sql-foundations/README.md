# Lab 22: PL/SQL Foundations

## Focus
Comprehensive deep-dive into Oracle PL/SQL fundamentals including block structure, variables, types, control structures, cursors, exception handling, procedures, functions, packages, triggers, collections, bulk operations, dynamic SQL, and transactions — with Java implementations.

## Topics Covered
- PL/SQL overview and Oracle-specific language
- Block structure (DECLARE, BEGIN, EXCEPTION, END)
- Variables, types, and anchored declarations
- Conditional controls (IF, CASE) and loop controls (LOOP, WHILE, FOR)
- Cursors: implicit, explicit, CURSOR FOR loops, cursor variables
- Exception handling: predefined, user-defined, PRAGMA EXCEPTION_INIT
- Procedures and functions with parameters (IN/OUT/IN OUT)
- NOCOPY hint and DETERMINISTIC clause
- Packages, package state, overloaded subprograms
- Triggers (DML, INSTEAD OF, system), trigger predicates, mutating tables
- Collections: associative arrays, nested tables, VARRAYs
- Bulk operations: BULK COLLECT, FORALL, SAVE EXCEPTIONS
- Static SQL vs Dynamic SQL (EXECUTE IMMEDIATE, DBMS_SQL)
- Transaction management: COMMIT/ROLLBACK/SAVEPOINT/AUTONOMOUS_TRANSACTION
- PRAGMA SERIALLY_REUSABLE and optimization
- PL/SQL native compilation and PL/Scope

## Prerequisites
- Java 21+ for simulation/parser code
- Oracle Database (or Oracle XE) for PL/SQL execution
- Understanding of SQL (SELECT, INSERT, UPDATE, DELETE)
- Familiarity with basic programming constructs

## Lab Structure
| Directory | Content |
|-----------|---------|
| src/main/java/com/databases/plsql/ | PL/SQL Simulator and Parser |
| src/test/java/com/databases/plsql/ | JUnit 5 tests |
| BENCHMARK/ | Performance benchmarks |
| CHALLENGE/ | Advanced exercises |
| DIAGRAMS/ | Flow diagrams |
| MINI_PROJECT/ | Guided mini-project |
| REAL_WORLD_PROJECT/ | Full project specification |
| SOLUTION/ | Exercise solutions |
| TESTS/ | Additional test scenarios |

## Learning Objectives
- Write complex PL/SQL blocks and stored procedures
- Design and use packages with proper state management
- Handle exceptions gracefully with proper propagation
- Implement triggers for auditing and validation
- Optimize with bulk operations and native compilation
- Use dynamic SQL safely and effectively