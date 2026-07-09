# Architecture of PL/SQL

## PL/SQL Engine Architecture
The PL/SQL engine is embedded in the Oracle Database server (and Oracle Forms). It compiles PL/SQL code into native or interpreted p-code. The engine processes:
1. Declarative section: variable/type allocation
2. Executable section: SQL and procedural statements
3. Exception section: error handling

## Block Structure
Every PL/SQL block follows: DECLARE (optional), BEGIN (required), EXCEPTION (optional), END (required). Anonymous blocks are unnamed; named blocks become procedures, functions, packages, or triggers.

## Compilation Process
1. Syntax check: parse PL/SQL text
2. Semantic check: resolve references
3. P-code generation: generate PL/SQL bytecode (DIANA - Descriptive Intermediate Attributed Notation for Ada)
4. Native compilation: optionally compile to C then to machine code

## Package Architecture
Packages consist of:
- **Specification** (public API): lists types, constants, variables, procedures, functions
- **Body** (implementation): actual code, private items, initialization

Package state is per-session. Public variables persist for the session duration.

## PL/SQL and SQL Engine Interaction
PL/SQL sends SQL statements to the SQL engine. The SQL engine returns results. Context switches occur when control moves between PL/SQL and SQL engines — this is the main performance bottleneck that bulk operations address.

## Trigger Architecture
Triggers are PL/SQL blocks stored in the database and automatically executed when events occur:
- DML triggers: BEFORE/AFTER/INSTEAD OF on INSERT/UPDATE/DELETE
- System triggers: BEFORE/AFTER on DDL or database events

## PL/SQL Optimizer
PL/SQL compiler has optimization levels (0/1/2/3) controlling:
- Inlining of subprograms
- Dead code elimination
- Constant folding
- Loop unrolling
- Ordering of boolean expressions

## PL/Scope Architecture
PL/Scope collects compile-time data about identifiers in PL/SQL code:
- Type identifiers
- Declaration locations
- Usage patterns (read/write/reference)
- Scope visibility chains

## Native Compilation Architecture
PL/SQL native compilation converts DIANA to C code, compiles with C compiler, and links as shared library. Execution bypasses PL/SQL interpreter for direct machine code execution.

## Collection Memory Architecture
- Associative arrays (index-by tables): stored in session memory (UGA), accessed by BINARY_INTEGER or VARCHAR2 keys
- Nested tables: stored in tablespaces, accessible via SQL
- VARRAYs: stored inline in table columns if small, as LOB otherwise
- Collection memory is allocated from the PGA or UGA depending on type

## Transaction and Autonomous Transaction
AUTONOMOUS_TRANSACTION pragma creates a separate transaction in its own session context. The autonomous transaction commits/rollbacks independently while the main transaction remains pending. This is useful for logging, audit trails, and error logging.

## PRAGMA EXCEPTION_INIT
Associates a user-defined Oracle error number with a custom exception name, allowing explicit handling of otherwise unnamed system errors.

## PRAGMA SERIALLY_REUSABLE
Marks a package as reusable across calls. State is reset between calls to the same session's call, preventing package state from persisting — useful for stateless utility packages.

## NOCOPY Hint
Passes IN OUT parameters by reference instead of by value. This avoids copying large data structures (collections, records) but risks the "subprogram ends with uncommitted changes to the actual parameter" phenomenon.