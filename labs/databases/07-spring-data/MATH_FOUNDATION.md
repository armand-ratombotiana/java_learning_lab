# Math Foundation: Spring Data JPA

## Pagination Math
```
page = offset / pageSize
totalPages = ceil(totalElements / pageSize)
hasNext = (currentPage + 1) < totalPages
hasPrevious = currentPage > 0
```

## Query Complexity
For derived queries with N predicates:
- Method name length grows linearly with predicate count
- Worst-case JPQL generation: O(N)
- Execution: O(log N) for indexed lookups, O(N) for table scans

## Specification Composition
Composing specifications follows monoid properties:
- Identity: `Specification.where(null)`
- Associativity: `(a.and(b)).and(c) == a.and(b.and(c))`
- Idempotent `and()` with self: `spec.and(spec) != spec` (duplicates predicates)
