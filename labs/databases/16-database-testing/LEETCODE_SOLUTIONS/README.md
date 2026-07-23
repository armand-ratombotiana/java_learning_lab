# LEETCODE_SOLUTIONS — Database Testing

## Test-Driven SQL Solutions

| LeetCode Problem | Test Case Type | Edge Cases |
|-----------------|---------------|------------|
| 175 Combine Tables | LEFT JOIN | Person without address → null |
| 176 Second Highest | No data | Only one row → null |
| 178 Rank Scores | Ties | Same score → same rank, no gaps |
| 180 Consecutive Numbers | Boundary | First/last rows, no matches |
| 182 Duplicate Emails | No duplicates | Empty result, all unique |
| 183 Customers Never Order | All ordered | Empty result |
| 184 Dept Highest Salary | Empty department | No employees → no result |
| 197 Rising Temperature | Single row | No previous date → no result |

### Testing Approach for SQL
```sql
-- Test case 1: Basic functionality
INSERT INTO Employee VALUES (1, 'Alice', 5000, NULL);
INSERT INTO Employee VALUES (2, 'Bob', 6000, 1);
-- Expected: Bob (earns more than Alice)

-- Test case 2: Edge — no manager
SELECT * FROM Employee e LEFT JOIN Employee m ON e.managerId = m.id;
-- Expected: Alice shows with null manager

-- Test case 3: Empty table
TRUNCATE TABLE Employee;
SELECT name FROM Employee; -- Expected: empty result
```
