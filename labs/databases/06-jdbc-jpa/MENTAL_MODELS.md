# Mental Models: JDBC & JPA

## JDBC = Low-Level Plumbing
Think of JDBC as the water pipes in a building. It carries data efficiently but requires manual connection, valve turning (autocommit), leak checking (connection close), and pipe fitting (SQL strings).

## PreparedStatement = Parameterized Template
A PreparedStatement is like a form with blanks to fill in (parameters). The database compiles the template once and fills in values each time. Faster, secure against SQL injection.

## ResultSet = Database Cursor
Think of a ResultSet as a cursor positioned before the first row. You call `next()` to advance, then read columns by name or index. Like scrolling through a spreadsheet one row at a time.

## JPA Entity = Database Row as Object
An entity is a Java object whose fields map to database columns. When you modify an entity inside a transaction, Hibernate automatically generates UPDATE SQL on flush.

## EntityManager = Persistence Context
The EntityManager is a workspace. It tracks all loaded entities. When you flush, it compares snapshots and generates SQL for changes. Like a notepad where you jot down changes, then copy to the clean copy.

## @Transactional = Unit of Work
Everything inside @Transactional either succeeds or fails together. If an exception occurs, all changes roll back. Like an atomic operation — all or nothing.

## N+1 Problem = Shoe Store Query
You want to buy 5 pairs of shoes. Instead of asking "show me all shoes in my size" (1 query), you ask "show me the first shoe" (1 query), then "show me the second shoe" (1 query), etc. — 6 queries instead of 1. This is the N+1 query problem.
