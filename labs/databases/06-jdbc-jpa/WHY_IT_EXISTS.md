# Why JDBC & JPA Exist

## JDBC: Standardized Database Access

Before JDBC (1997), each database had its own proprietary API. Switching databases meant rewriting all data access code. JDBC provided a unified interface that any database vendor could implement via a driver.

## JPA: Solving Object-Relational Impedance Mismatch

JDBC exposes raw SQL and tabular ResultSets. Application code mixes SQL strings with Java logic — tedious, error-prone, and not object-oriented. JPA bridges this gap:

```
Relational World:    tables, rows, columns, foreign keys
Java World:          classes, objects, fields, references
```

JPA automatically maps between these paradigms, eliminating boilerplate.

## Problems Solved by Hibernate

- **SQL generation**: Write once, generate optimized SQL per database
- **Caching**: First-level (session) and second-level (application) cache
- **Dirty checking**: Automatic detection of modified fields
- **Lazy loading**: Load data on demand, not eagerly
- **Transaction management**: Declarative transactions with `@Transactional`
- **Schema generation**: DDL from entity annotations

## Why Both Still Matter

JDBC remains essential for:
- Batch operations requiring low-level control
- Complex SQL that ORMs generate poorly
- Migration scripts and bulk data operations
- Direct database administration

JPA/Hibernate excels at:
- CRUD for business entities
- Complex object graphs with relationships
- Multi-tier caching
- Rapid application development
