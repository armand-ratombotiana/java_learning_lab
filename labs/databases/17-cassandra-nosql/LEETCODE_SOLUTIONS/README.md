# LEETCODE_SOLUTIONS — Cassandra / NoSQL

## Wide-Column Database Solutions

| LeetCode Problem | Cassandra Data Model | CQL Query |
|-----------------|---------------------|-----------|
| 380 Randomized Set | Partition key + clustering | `SELECT * FROM sets WHERE id = ?` |
| 362 Hit Counter | Time-based partition | Per-hour partition with time UUID |
| 550 Game Play IV | Player activity table | `SELECT * FROM activity WHERE player_id = ?` |
| 184 Dept Highest | Partition by dept | `SELECT * FROM employees WHERE dept = ?` |

### Cassandra Table Design for LeetCode 184
```sql
CREATE TABLE employees_by_department (
    dept_id INT,
    emp_id UUID,
    name TEXT,
    salary DECIMAL,
    PRIMARY KEY (dept_id, salary, emp_id)
) WITH CLUSTERING ORDER BY (salary DESC, emp_id ASC);
```

### Key Differences from SQL
- No JOINs: denormalize data (LeetCode 175: store address in person table)
- No GROUP BY: use counter tables or Spark Analytics
- No window functions: sort at application level
- Eventual consistency: read-repair for accuracy
