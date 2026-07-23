# LEETCODE_SOLUTIONS — Query Optimization (Advanced)

## Advanced Performance Solutions

| LeetCode Problem | Optimized Approach | Expected Improvement |
|-----------------|-------------------|---------------------|
| 177 Nth Highest | DENSE_RANK vs LIMIT/OFFSET | 40% faster with window function |
| 178 Rank Scores | DENSE_RANK vs correlated subquery | 60% faster with window function |
| 180 Consecutive | LEAD vs triple self-JOIN | 50% faster, less I/O |
| 550 Game Play IV | CTE + JOIN vs multiple subqueries | 30% faster |
| 601 Human Traffic | LEAD/LAG vs complex JOIN | 45% faster |
| 1097 Game Play V | Window functions vs subqueries | 35% faster |

### Optimization Decision Tree
```
Is the query slow?
├── Is there an index on the WHERE columns?
│   ├── No  → Add index
│   └── Yes → Check execution plan
├── Is it a self-join?
│   └── Yes → Consider window functions (LEAD, LAG, RANK)
├── Is it a correlated subquery?
│   └── Yes → Convert to JOIN or CTE
└── Is it pagination?
    └── Yes → Use keyset pagination (not OFFSET)
```

### Example: Optimize LeetCode 180
```sql
-- Before: Triple self-JOIN (slow)
SELECT DISTINCT l1.num FROM Logs l1
JOIN Logs l2 ON l1.id = l2.id - 1 AND l1.num = l2.num
JOIN Logs l3 ON l1.id = l3.id - 2 AND l1.num = l3.num;

-- After: LEAD window function (fast)
SELECT DISTINCT num FROM (
    SELECT num, LEAD(num,1) OVER(ORDER BY id) n2, LEAD(num,2) OVER(ORDER BY id) n3
    FROM Logs
) WHERE num = n2 AND num = n3;
```
