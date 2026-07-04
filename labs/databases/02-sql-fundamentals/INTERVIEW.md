# Interview Prep: SQL

## Common Interview Questions

### Q1: "What's the difference between WHERE and HAVING?"
**Answer**: WHERE filters individual rows before GROUP BY. HAVING filters groups after GROUP BY. HAVING can use aggregate functions; WHERE cannot.

### Q2: "Explain the N+1 query problem and how to fix it."
**Answer**: One query fetches N parent rows, then N queries fetch children. Fix with JOIN FETCH, @EntityGraph, or batch fetching.

### Q3: "How do window functions differ from GROUP BY?"
**Answer**: Window functions compute over a partition of rows but do NOT collapse rows. Each row keeps its identity. GROUP BY collapses rows into aggregate groups.

### Q4: "What's a covering index?"
**Answer**: An index that contains all columns needed for a query, allowing Index Only Scan without visiting the table heap.

### Q5: "How would you paginate efficiently in SQL?"
**Answer**: Use keyset pagination (WHERE id > :lastId ORDER BY id LIMIT :size) instead of OFFSET/LIMIT.

### Q6: "Explain the difference between clustered and non-clustered indexes."
**Answer**: Clustered index determines physical row order (one per table). Non-clustered is a separate structure with pointers to rows.

## Whiteboard Challenge
Design a query to find employees who earn more than their department's average salary. Write the SQL.
