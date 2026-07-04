# Theory: Query Optimization

## Index Types

| Type | Use Case | Structure |
|---|---|---|
| B-Tree | Equality + range queries, sorting | Balanced tree, O(log N) |
| Hash | Equality only | Hash table, O(1) |
| GiST | Full-text, geometric, range types | Generalized search tree |
| GIN | Array, JSONB, full-text | Inverted index |
| BRIN | Large tables with natural ordering | Block range index |
| Covering | Index-only scans | Includes INCLUDE columns |

## EXPLAIN Plan Reading

```
Seq Scan on users (cost=0.00..1000.00 rows=10000 width=100)
  Filter: (age > 30)
```
- **cost**: Estimated cost (startup..total) in arbitrary units
- **rows**: Estimated rows returned
- **width**: Average row width in bytes
- **Seq Scan**: Full table scan (table sequential scan)
- **Index Scan**: Index lookup then heap access
- **Index Only Scan**: All needed data in index (no heap visit)

## N+1 Problem
Occurs when code executes one query for the parent entity and N queries for each child collection. In JPA:
```java
// 1 query for departments + 10 queries for employees = 11 queries
List<Department> depts = departmentRepository.findAll();
for (Department d : depts) {
    d.getEmployees().size(); // triggers lazy load per department
}
```

## Query Cost Factors
- **Rows scanned**: Full scan vs index scan vs index-only scan
- **Join methods**: Nested Loop, Hash Join, Merge Join
- **Sorting**: In-memory vs disk-based sort
- **Data locality**: Sequential vs random I/O
