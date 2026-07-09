# Theoretical Foundations of PL/SQL

## Block-Structured Language
PL/SQL is a block-structured language (derived from Ada). Each block defines a scope. Variables declared in a block are local to that block and are not visible to nested blocks (unless dynamically referenced).

## Variable Scope
Variables declared in an outer block are visible in nested blocks (unless shadowed by local declaration). The scope of a variable is from its declaration to the END of the enclosing block.

## Exception Propagation
Exceptions propagate outward:
1. Current block's EXCEPTION section is checked
2. Matching handler executes, or exception propagates
3. Outward through nested blocks, procedures, and to caller
4. Unhandled exceptions propagate back to the calling environment

## Cursor Theory
A cursor is a handle to a SQL statement result set. Implicit cursors (SELECT INTO, DML) are managed automatically by Oracle. Explicit cursors give the programmer control over the fetch cycle: OPEN, FETCH, CLOSE.

## Transaction Theory
Oracle's transaction model: every DML statement executes in a transaction. Changes are not visible to other sessions until COMMIT. ROLLBACK undoes all changes since the last COMMIT. A transaction ends with COMMIT, ROLLBACK, or DDL (auto-commit).

## Autonomous Transactions
An autonomous transaction is an independent transaction started by another transaction (the main transaction). Autonomous transactions COMMIT or ROLLBACK without affecting the main transaction state. Common uses: logging, audit trails, and retry handling.

## Bulk Operations Theory
BULK COLLECT fetches multiple rows at once (reducing SQL-to-PL/SQL context switches). FORALL sends multiple DML statements to SQL engine in one operation. The binding is done once per batch rather than once per row.

## PRAGMA Concepts
PRAGMA (compiler directive) provides instructions to the compiler:
- EXCEPTION_INIT: assign error number to exception name
- AUTONOMOUS_TRANSACTION: create independent subtransaction
- SERIALLY_REUSABLE: reset package state between calls
- RESTRICT_REFERENCES: declares purity level of functions

## Package Theory
Package state (global variables) persists per session. This enables object-oriented-like behavior: stateful services that maintain data between calls. However, concurrent access from multiple sessions is safe since each session has its own copy.

## Trigger Theory
Triggers implement event-condition-action rules. DML triggers execute before/after row or statement events. System triggers fire on DDL or database events. INSTEAD OF triggers on views handle DML on complex views that would otherwise be non-updatable.

## Mutating Table Problem
A mutating table is a table currently being modified by a DML statement. You cannot SELECT/UPDATE the mutating table from a row-level trigger. Solutions: use compound triggers, statement-level triggers, or temporary collections.

## Collection Theory
Collections are homogeneous sets of elements. Associative arrays (formerly index-by tables) are key-value stores. Nested tables are unbounded arrays — they can be sparse. VARRAYs (variable arrays) are bounded, dense arrays. Collections can be passed as parameters, stored in columns, and accessed via SQL.

## Dynamic SQL Theory
Dynamic SQL constructs and executes SQL at runtime. Use cases: dynamic table/column names, cursor variables with varying content, DDL operations. DBMS_SQL package provides deeper control than the native EXECUTE IMMEDIATE.

## PL/SQL Optimization Theory
The compiler performs: dead code elimination, constant propagation, loop unrolling, function inlining, code reordering. Optimization level 3 enables all transformations. Native compilation converts p-code to machine code for CPU-intensive logic.

## Deterministic Functions
DETERMINISTIC clause declares that a function returns the same value for the same arguments. This allows function result caching (not to be confused with result cache). It enables the optimizer to use the function in function-based indexes and parallel queries.