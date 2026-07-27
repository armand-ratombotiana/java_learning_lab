# SQL Problem Walkthrough: 03-subqueries

## Problem 1: Nth Highest Salary (LC SQL 177) — Oracle

### Interview Scenario
"Oracle interview: You're given an Employee table. Write a function to find the Nth highest salary from the table. If there are fewer than N distinct salaries, return NULL."

### The Problem
Table **Employee**: id (NUMBER PK), salary (NUMBER). Write an Oracle function `getNthHighestSalary(N IN NUMBER)` that returns the Nth highest distinct salary. If Nth highest doesn't exist, return NULL.

### Step 1: Understand Schema
- Single table, self-contained
- Need distinct salaries — duplicates shouldn't create separate ranks
- N = 1 means the highest salary
- N = 2 means the second highest distinct salary

Sample data:
| id | salary |
|----|--------|
| 1  | 100    |
| 2  | 200    |
| 3  | 300    |
| 4  | 200    |

N=2 → 200 (distinct salaries: 300, 200, 100)

### Step 2: Think Aloud
We can't use LIMIT/OFFSET (that's MySQL). In Oracle we have several approaches:

1. **Analytic DENSE_RANK**: Rank distinct salaries, filter where rank = N
2. **Subquery with ROWNUM**: Order by salary DESC, wrap, filter by ROWNUM
3. **Correlated subquery**: Count distinct salaries >= current, filter count = N
4. **FETCH FIRST with OFFSET**: Oracle 12c+ `OFFSET N-1 ROWS FETCH NEXT 1 ROWS ONLY`

Approach 1 (DENSE_RANK) handles ties correctly. Approach 4 is cleanest for 12c+.

### Step 3: Write the Query
```sql
-- Approach 1: DENSE_RANK
SELECT DISTINCT salary
  FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
      FROM employee
  )
 WHERE rnk = N;

-- Approach 2: OFFSET FETCH (Oracle 12c+)
SELECT DISTINCT salary
  FROM employee
 ORDER BY salary DESC
OFFSET N-1 ROWS FETCH NEXT 1 ROWS ONLY;
```

Oracle function:
```sql
CREATE OR REPLACE FUNCTION get_nth_highest_salary (n IN NUMBER)
  RETURN NUMBER
IS
  result NUMBER;
BEGIN
  SELECT DISTINCT salary
    INTO result
    FROM (
      SELECT salary,
             DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
        FROM employee
    )
   WHERE rnk = n;

  RETURN result;

EXCEPTION
  WHEN NO_DATA_FOUND THEN
    RETURN NULL;
END get_nth_highest_salary;
/
```

### Step 4: Execution Plan
```
| Id | Operation                     | Name                  |
|----|-------------------------------|-----------------------|
|  0 | SELECT STATEMENT              |                       |
|  1 |  SORT UNIQUE                  |                       |
|  2 |   VIEW                        |                       |
|  3 |    WINDOW SORT                |                       |
|  4 |     TABLE ACCESS FULL         | EMPLOYEE              |
```

The WINDOW SORT computes DENSE_RANK across all rows. Then SORT UNIQUE deduplicates (though DENSE_RANK already produces one row per rank, wrapping in DISTINCT adds an extra sort).

### Step 5: Optimize
```sql
CREATE INDEX emp_salary_idx ON employee(salary DESC);
```

Better approach — avoid DISTINCT by using MIN/MAX with DENSE_RANK:
```sql
SELECT MIN(salary) AS nth_salary
  FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
      FROM employee
  )
 WHERE rnk = N;
```

For very large tables, consider:
- Materialized view pre-computing salary distribution
- Histogram-based optimizations
- Using KEEP/DENSE_RANK FIRST/LAST:
```sql
SELECT MAX(salary) KEEP(DENSE_RANK LAST ORDER BY salary) FROM employee;
```

### Step 6: Test
Edge cases:
- **N = 0**: Invalid — should return NULL or raise exception
- **N > distinct salary count**: Returns NULL (NO_DATA_FOUND exception handled)
- **All salaries identical**: Only one distinct salary; N=1 returns it, N>1 returns NULL
- **NULL salaries**: DENSE_RANK treats NULLs as largest (DESC order). Need to exclude if not wanted.

```sql
-- Test harness
WITH salary_test AS (
  SELECT 100 AS salary FROM dual UNION ALL
  SELECT 200 FROM dual UNION ALL
  SELECT 300 FROM dual UNION ALL
  SELECT 200 FROM dual
)
SELECT salary,
       DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
  FROM salary_test;
-- 300 -> 1, 200 -> 2, 200 -> 2, 100 -> 3
```

### Company Evaluation
- **Oracle**: DENSE_RANK is idiomatic. Discuss function creation with exception handling.
- **Amazon**: Translate to Redshift — same DENSE_RANK works but use TOP N syntax.
- **Google**: In Spanner, use ARRAY_AGG(DISTINCT salary ORDER BY salary DESC)[OFFSET(N-1)].

---

## Problem 2: Delete Duplicate Emails (LC SQL 196) — Google

### Interview Scenario
"Google interview: The Person table has duplicate email addresses. You need to delete all duplicate emails, keeping only the one with the smallest id."

### The Problem
Table **Person**: id (NUMBER PK), email (VARCHAR2). Delete all duplicate emails, keeping only one row per unique email — the one with the smallest id. Do this in a single SQL statement. Return the remaining table with id and email.

### Step 1: Understand Schema
- id is unique (PK)
- email has duplicates
- We keep the smallest id for each email
- DELETE operation, not SELECT

Sample data:
| id | email            |
|----|------------------|
| 1  | john@example.com |
| 2  | bob@example.com  |
| 3  | john@example.com |

After deletion:
| id | email            |
|----|------------------|
| 1  | john@example.com |
| 2  | bob@example.com  |

### Step 2: Think Aloud
Can't use GROUP BY in a DELETE directly. Approaches:

1. **Self-join DELETE**: DELETE p1 FROM Person p1 JOIN Person p2 ON p1.email = p2.email AND p1.id > p2.id (MySQL style)
2. **Subquery with correlated DELETE**: DELETE WHERE id NOT IN (SELECT MIN(id) FROM Person GROUP BY email)
3. **ROW_NUMBER + DELETE**: Use an updatable view or CTAS approach

Oracle doesn't support DELETE with JOIN syntax. Use subquery approach.

### Step 3: Write the Query
```sql
-- Approach 1: NOT IN subquery
DELETE FROM person
 WHERE id NOT IN (
   SELECT MIN(id)
     FROM person
    GROUP BY email
 );

-- Approach 2: NOT EXISTS (safer with NULLs)
DELETE FROM person p1
 WHERE NOT EXISTS (
   SELECT 1
     FROM person p2
    WHERE p2.email = p1.email
    GROUP BY p2.email
   HAVING MIN(p2.id) = p1.id
 );

-- Approach 3: ROW_NUMBER in CTE (Oracle 12c+ updatable view)
DELETE FROM person
 WHERE ROWID IN (
   SELECT rid
     FROM (
       SELECT ROWID AS rid,
              ROW_NUMBER() OVER (PARTITION BY email ORDER BY id) AS rn
         FROM person
     )
    WHERE rn > 1
 );
```

### Step 4: Execution Plan
For Approach 1:
```
| Id | Operation          | Name   | Rows | Cost |
|----|--------------------|--------|------|------|
|  0 | DELETE STATEMENT   |        |      |      |
|  1 |  DELETE            | PERSON |      |      |
|  2 |   INDEX FULL SCAN  | PERSON |      |      |
```

Oracle will read the MIN(id) subquery, materialize it, then scan person to find rows whose id isn't in the kept set.

### Step 5: Optimize
```sql
CREATE INDEX person_email_idx ON person(email);
```

With this index, the GROUP BY email becomes an INDEX FULL SCAN instead of a TABLE FULL SCAN.

For massive tables, hash-partition by email and use partition-level deletion.

### Step 6: Test
Edge cases:
- **No duplicates**: No rows deleted (each email GROUP BY has count = 1, so MIN(id) = id)
- **All emails same**: Only the row with smallest id survives
- **NULL emails**: GROUP BY treats all NULLs as one group. NOT IN with subquery that might return NULL breaks. Approach 3 handles this best.
- **Large delete volume**: Consider disabling triggers, using batch deletion with LIMIT (via ROWNUM in a loop)

```sql
-- Verification query
SELECT email, COUNT(*), MIN(id) AS keep_id
  FROM person
 GROUP BY email
HAVING COUNT(*) > 1;
```

### Company Evaluation
- **Google**: Emphasize correctness — the interview tests understanding of subqueries in DML.
- **Oracle**: ROWID-based approach is most idiomatic for Oracle DML. Discuss flashback query to verify.
- **Amazon**: At DynamoDB scale, this would require a scan + conditional delete pattern.

---

## Problem 3: Not Boring Movies (LC SQL 620) — Amazon

### Interview Scenario
"Amazon Prime Video interview: The cinema table lists movies with their ratings and descriptions. Find all movies that have an odd-numbered ID and a description that is not 'boring'. Return results ordered by rating descending."

### The Problem
Table **Cinema**: id (NUMBER PK), movie (VARCHAR2), description (VARCHAR2), rating (NUMBER). Write a query to find movies with odd-numbered id AND description != 'boring'. Order by rating DESC.

### Step 1: Understand Schema
- id is the primary key
- description may be 'boring' or other values
- rating is numeric (higher is better)
- Need odd IDs only

Sample data:
| id | movie      | description | rating |
|----|------------|-------------|--------|
| 1  | War        | great 3D    | 8.9    |
| 2  | Science    | fiction     | 8.5    |
| 3  | Irish      | boring      | 6.2    |
| 4  | Ice song   | Fantacy     | 8.6    |

### Step 2: Think Aloud
Simple filtering: MOD(id, 2) = 1 for odd numbers. Two conditions in WHERE with AND. ORDER BY for sorting.

### Step 3: Write the Query
```sql
SELECT id,
       movie,
       description,
       rating
  FROM cinema
 WHERE MOD(id, 2) = 1
   AND description != 'boring'
 ORDER BY rating DESC;
```

Using BITAND for odd check:
```sql
SELECT id, movie, description, rating
  FROM cinema
 WHERE BITAND(id, 1) = 1
   AND description != 'boring'
 ORDER BY rating DESC;
```

### Step 4: Execution Plan
Full table scan expected. Since both conditions are non-selective (half the rows have odd IDs, most descriptions aren't 'boring'), Oracle will do a full scan. With a function-based index on MOD(id,2):

```sql
CREATE INDEX cinema_odd_idx ON cinema(MOD(id, 1));
-- But BITAND-based function index is better:
CREATE INDEX cinema_bitand_idx ON cinema(BITAND(id, 1));
```

### Step 5: Optimize
If this query is run frequently, consider:
- Virtual column for odd/even:
```sql
ALTER TABLE cinema ADD (is_odd AS (MOD(id, 2)));
CREATE INDEX cinema_is_odd_idx ON cinema(is_odd);
```
- Composite index on (is_odd, description, rating) for index-only access

### Step 6: Test
Edge cases:
- **NULL description**: `!= 'boring'` evaluates to NULL, row excluded. Use `NVL(description, 'unknown') != 'boring'` if needed.
- **All movies boring with odd IDs**: Returns empty set
- **Rating ties**: No secondary sort specified — could add movie name for deterministic order

```sql
-- Edge case test
WITH cinema_test AS (
  SELECT 1 AS id, 'Movie A' AS movie, 'boring' AS descr, 5.0 AS rating FROM dual UNION ALL
  SELECT 2, 'Movie B', 'exciting', 9.0 FROM dual UNION ALL
  SELECT 3, 'Movie C', NULL, 7.0 FROM dual
)
SELECT id, movie, descr, rating
  FROM cinema_test
 WHERE MOD(id, 2) = 1
   AND descr != 'boring'
 ORDER BY rating DESC;
-- Returns: Movie C (id=3, descr=NULL is excluded because NULL != 'boring' is UNKNOWN)
```

### Company Evaluation
- **Amazon**: Discuss content filtering at scale with DynamoDB or Elasticsearch.
- **Oracle**: MOD function and function-based indexes.
- **Microsoft**: T-SQL uses `%` operator: `id % 2 = 1`.
