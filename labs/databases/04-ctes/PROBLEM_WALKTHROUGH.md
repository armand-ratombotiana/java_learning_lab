# SQL Problem Walkthrough: 04-ctes

## Problem 1: Find the Start and End Number of Continuous Ranges (LC SQL 1285) — Google

### Interview Scenario
"Google interview: The Logs table contains an id column with some gaps. Find the start and end of each contiguous range of ids. Return the result as (start_id, end_id) ordered by start_id."

### The Problem
Table **Logs**: log_id (NUMBER PK, with gaps). Write a query to find the start and end of each consecutive group of ids. A consecutive group is a set of ids where each id is exactly 1 more than the previous.

Example: [1, 2, 3, 7, 8, 10] → groups (1,3), (7,8), (10,10)

### Step 1: Understand Schema
- Single column table
- Values are integers, may have gaps
- Need to identify "islands" of consecutive values
- Classic gaps-and-islands problem

Sample data:
| log_id |
|--------|
| 1      |
| 2      |
| 3      |
| 7      |
| 8      |
| 10     |

Expected result:
| start_id | end_id |
|----------|--------|
| 1        | 3      |
| 7        | 8      |
| 10       | 10     |

### Step 2: Think Aloud
This is a classic gaps-and-islands problem solved with window functions and CTEs.

**Key insight**: If we subtract ROW_NUMBER() from the id, consecutive groups produce the same difference. For example:
- ID 1 - ROW_NUM 1 = 0
- ID 2 - ROW_NUM 2 = 0
- ID 3 - ROW_NUM 3 = 0
- ID 7 - ROW_NUM 4 = 3
- ID 8 - ROW_NUM 5 = 3
- ID 10 - ROW_NUM 6 = 4

We GROUP BY that difference, then MIN and MAX give the range boundaries.

### Step 3: Write the Query
```sql
WITH ordered_logs AS (
  SELECT log_id,
         ROW_NUMBER() OVER (ORDER BY log_id) AS rn
    FROM logs
),
grouped_logs AS (
  SELECT log_id,
         log_id - rn AS grp
    FROM ordered_logs
)
SELECT MIN(log_id) AS start_id,
       MAX(log_id) AS end_id
  FROM grouped_logs
 GROUP BY grp
 ORDER BY start_id;
```

Compact version:
```sql
WITH cte AS (
  SELECT log_id,
         log_id - ROW_NUMBER() OVER (ORDER BY log_id) AS grp
    FROM logs
)
SELECT MIN(log_id) AS start_id,
       MAX(log_id) AS end_id
  FROM cte
 GROUP BY grp
 ORDER BY start_id;
```

### Step 4: Execution Plan
```
| Id | Operation              | Name | Rows | Cost |
|----|------------------------|------|------|------|
|  0 | SELECT STATEMENT       |      |      |      |
|  1 |  SORT GROUP BY         |      |      |      |
|  2 |   VIEW                 |      |      |      |
|  3 |    WINDOW SORT         |      |      |      |
|  4 |     TABLE ACCESS FULL  | LOGS |      |      |
```

The WINDOW SORT (step 3) is the heaviest operation. It sorts all log_ids to compute ROW_NUMBER. The GROUP BY then aggregates.

### Step 5: Optimize
```sql
CREATE INDEX logs_id_idx ON logs(log_id);
```

With the index, Oracle can do an INDEX FULL SCAN instead of TABLE FULL SCAN, reading only the index blocks (smaller than the table).

For very large tables, consider:
- Materialized view for pre-computed ranges
- PL/SQL procedural approach using cursor loops (avoid sorting everything in memory)

### Step 6: Test
Edge cases:
- **Single row**: Returns (log_id, log_id)
- **All consecutive**: One group from MIN to MAX
- **No rows**: Empty result set
- **All gaps (every other number)**: Each id is its own group
- **NULL or negative values**: ROW_NUMBER assigns sequential numbers regardless

```sql
-- Comprehensive test
WITH logs_test AS (
  SELECT 1 AS log_id FROM dual UNION ALL
  SELECT 2 FROM dual UNION ALL
  SELECT 3 FROM dual UNION ALL
  SELECT 7 FROM dual UNION ALL
  SELECT 8 FROM dual UNION ALL
  SELECT 10 FROM dual
),
cte AS (
  SELECT log_id,
         log_id - ROW_NUMBER() OVER (ORDER BY log_id) AS grp
    FROM logs_test
)
SELECT MIN(log_id) AS start_id,
       MAX(log_id) AS end_id
  FROM cte
 GROUP BY grp
 ORDER BY start_id;
```

### Company Evaluation
- **Google**: Gaps-and-islands is a favorite. Discuss analytical functions and CTE optimization.
- **Oracle**: Emphasize WINDOW SORT performance and parallel query for large tables.
- **Amazon**: Redshift superblocks — efficient for sequential scans but WINDOW functions use memory.

---

## Problem 2: Consecutive Available Seats (LC SQL 603) — Amazon

### Interview Scenario
"Amazon interview: The Cinema table shows seat availability. Find all seat numbers that are available and have at least one consecutive available seat next to them (N+1 or N-1 also available)."

### The Problem
Table **Cinema**: seat_id (NUMBER PK, auto-increment), free (NUMBER, 1 = free, 0 = occupied). Find all seat_ids that are free AND have at least one adjacent seat (seat_id ± 1) that is also free. Return seat_id ordered by seat_id.

### Step 1: Understand Schema
- seat_id is sequential
- free is a boolean flag (1 = available, 0 = taken)
- Need consecutive available seats — not single isolated seats

Sample data:
| seat_id | free |
|---------|------|
| 1       | 1    |
| 2       | 0    |
| 3       | 1    |
| 4       | 1    |
| 5       | 1    |

Result: seat_ids 3, 4, 5 (seat 1 is isolated, seat 2 is taken)

### Step 2: Think Aloud
Multiple approaches:
1. **Self-join**: Join cinema c1 to cinema c2 on ABS(c1.seat_id - c2.seat_id) = 1
2. **Subquery with EXISTS**: Check for any adjacent free seat
3. **LAG/LEAD window functions**: Check if previous or next seat is free

LAG/LEAD is the most elegant and performant.

### Step 3: Write the Query
```sql
-- Approach 1: LAG/LEAD (best)
SELECT seat_id
  FROM (
    SELECT seat_id,
           free,
           LAG(free) OVER (ORDER BY seat_id) AS prev_free,
           LEAD(free) OVER (ORDER BY seat_id) AS next_free
      FROM cinema
  )
 WHERE free = 1
   AND (prev_free = 1 OR next_free = 1)
 ORDER BY seat_id;

-- Approach 2: Self-join
SELECT DISTINCT c1.seat_id
  FROM cinema c1
  JOIN cinema c2
    ON ABS(c1.seat_id - c2.seat_id) = 1
 WHERE c1.free = 1
   AND c2.free = 1
 ORDER BY c1.seat_id;

-- Approach 3: EXISTS subquery
SELECT seat_id
  FROM cinema c1
 WHERE free = 1
   AND EXISTS (
     SELECT 1
       FROM cinema c2
      WHERE c2.free = 1
        AND ABS(c2.seat_id - c1.seat_id) = 1
   )
 ORDER BY seat_id;
```

### Step 4: Execution Plan
For the LAG/LEAD approach:
```
| Id | Operation           | Name   | Rows | Cost |
|----|---------------------|--------|------|------|
|  0 | SELECT STATEMENT    |        |      |      |
|  1 |  VIEW               |        |      |      |
|  2 |   WINDOW SORT       |        |      |      |
|  3 |    TABLE ACCESS FULL| CINEMA |      |      |
```

For the self-join approach:
```
| Id | Operation            | Name   | Cost |
|----|----------------------|--------|------|
|  0 | SELECT STATEMENT     |        |      |
|  1 |  HASH UNIQUE         |        |      |
|  2 |   NESTED LOOPS       |        |      |
|  3 |    TABLE ACCESS FULL | CINEMA |      |
|  4 |    INDEX RANGE SCAN  | CINEMA |      |
```

### Step 5: Optimize
```sql
CREATE INDEX cinema_free_seat_idx ON cinema(free, seat_id);
```

With this index, Oracle can quickly find free seats and scan adjacent ones. The EXISTS approach benefits most because it probes the index for each candidate row.

For very large theaters (100K+ seats), consider partitioning by section.

### Step 6: Test
Edge cases:
- **All seats free**: Every seat returned except edge seats with only one neighbor? Actually edges with only one free neighbor are included if that one neighbor is free.
- **No consecutive free seats**: Empty result set
- **Single seat**: LAG and LEAD return NULL — condition fails, seat excluded (correct — no neighbor)
- **First seat**: LAG returns NULL — included only if next_free = 1
- **Last seat**: LEAD returns NULL — included only if prev_free = 1

```sql
-- Edge case test
WITH seat_test AS (
  SELECT 1 AS seat_id, 1 AS free FROM dual UNION ALL
  SELECT 2, 1 FROM dual UNION ALL
  SELECT 3, 0 FROM dual UNION ALL
  SELECT 4, 0 FROM dual UNION ALL
  SELECT 5, 1 FROM dual
)
SELECT seat_id
  FROM (
    SELECT seat_id, free,
           LAG(free) OVER (ORDER BY seat_id) AS prev,
           LEAD(free) OVER (ORDER BY seat_id) AS nxt
      FROM seat_test
  )
 WHERE free = 1 AND (prev = 1 OR nxt = 1);
-- Returns seat_id 1 (next=1), 2 (prev=1), 5 (null neighbor? no, returns nothing for 5)
```

### Company Evaluation
- **Amazon**: Discuss DynamoDB equivalent — scan with adjacent key checks, or use GSI on free status.
- **Oracle**: LAG/LEAD window functions are the most efficient. Compare with self-join costs.
- **Google**: Spanner uses same syntax. Discuss distributed execution.

---

## Problem 3: Find the Quiet Students in All Exams (LC SQL 1412) — Microsoft

### Interview Scenario
"Microsoft interview: You have Student and Exam tables. A 'quiet' student is one who never scored the highest or lowest score in any exam they took. Find all quiet students."

### The Problem
Tables: **Student** (student_id NUMBER PK, student_name VARCHAR2), **Exam** (exam_id NUMBER, student_id NUMBER, score NUMBER, PK on (exam_id, student_id)).

Find all students who never scored the maximum OR minimum score in any exam they took. Return student_id, student_name ordered by student_id.

### Step 1: Understand Schema
- Exam has one row per student per exam
- For each exam, there is a max score and a min score
- A 'quiet' student never received either the max or min in any exam
- A student who took zero exams is not 'quiet'? The problem is ambiguous — typically they must have taken at least one exam but never been at extremes.

Sample data:
| student_id | student_name |
|------------|--------------|
| 1          | Alice        |
| 2          | Bob          |
| 3          | Carol        |

| exam_id | student_id | score |
|---------|------------|-------|
| 1       | 1          | 90    |
| 1       | 2          | 80    |
| 1       | 3          | 70    |
| 2       | 1          | 60    |
| 2       | 2          | 75    |
| 2       | 3          | 85    |

In exam 1: Alice has max, Carol has min → Bob is quiet (neither)
In exam 2: Carol has max, Alice has min → Bob is quiet (neither)
Result: Bob (student_id 2)

### Step 2: Think Aloud
Approach using window functions in a CTE:
1. For each exam, compute MIN(score) and MAX(score) per exam_id
2. Flag students who are either max or min (they are "not quiet")
3. Find students who were NEVER flagged across all exams they took

Alternatively, use a subquery to find all students who are quiet.

### Step 3: Write the Query
```sql
WITH exam_stats AS (
  SELECT exam_id,
         student_id,
         score,
         MIN(score) OVER (PARTITION BY exam_id) AS min_score,
         MAX(score) OVER (PARTITION BY exam_id) AS max_score
    FROM exam
),
not_quiet AS (
  SELECT DISTINCT student_id
    FROM exam_stats
   WHERE score = min_score
      OR score = max_score
)
SELECT s.student_id,
       s.student_name
  FROM student s
 WHERE s.student_id NOT IN (
   SELECT student_id FROM not_quiet
 )
   AND EXISTS (
     SELECT 1
       FROM exam e
      WHERE e.student_id = s.student_id
   )
 ORDER BY s.student_id;
```

Compact version:
```sql
WITH stats AS (
  SELECT student_id,
         score,
         MIN(score) OVER (PARTITION BY exam_id) AS min_s,
         MAX(score) OVER (PARTITION BY exam_id) AS max_s
    FROM exam
),
quiet AS (
  SELECT student_id
    FROM stats
   GROUP BY student_id
  HAVING SUM(CASE WHEN score IN (min_s, max_s) THEN 1 ELSE 0 END) = 0
)
SELECT s.student_id, s.student_name
  FROM student s
  JOIN quiet q ON s.student_id = q.student_id
 ORDER BY s.student_id;
```

### Step 4: Execution Plan
```
| Id | Operation                | Name    | Cost |
|----|--------------------------|---------|------|
|  0 | SELECT STATEMENT         |         |      |
|  1 |  HASH GROUP BY           |         |      |
|  2 |   VIEW                   | STATS   |      |
|  3 |    WINDOW SORT           |         |      |
|  4 |     TABLE ACCESS FULL    | EXAM    |      |
|  5 |  TABLE ACCESS FULL       | STUDENT |      |
```

### Step 5: Optimize
```sql
CREATE INDEX exam_exam_id_score_idx ON exam(exam_id, score);
```

Covering index allows the WINDOW SORT to use the index for ordering and avoids table access.

For massive exam data:
- Partition exam by exam_id for partition-wise MIN/MAX
- Use materialized view to track per-exam min/max scores

### Step 6: Test
Edge cases:
- **Student takes 1 exam and scores in the middle**: Quiet (neither min nor max)
- **Student takes 1 exam and scores min or max**: Not quiet
- **Student takes no exams**: Excluded (the EXISTS check)
- **All students are extremists**: Returns empty set
- **All students have the same score**: All scores are both min AND max — all students are NOT quiet

```sql
-- Test with single-exam student
WITH exam_test AS (
  SELECT 1 AS exam_id, 1 AS student_id, 80 AS score FROM dual UNION ALL
  SELECT 1, 2, 75 FROM dual UNION ALL
  SELECT 1, 3, 95 FROM dual
)
SELECT student_id
  FROM exam_test
 GROUP BY student_id
HAVING SUM(CASE
             WHEN score = MIN(score) OVER (PARTITION BY exam_id)
               OR score = MAX(score) OVER (PARTITION BY exam_id)
             THEN 1 ELSE 0
           END) = 0;
```

### Company Evaluation
- **Microsoft**: T-SQL uses same syntax. Discuss table-valued functions vs CTEs.
- **Oracle**: Emphasize analytic functions and the HAVING SUM(CASE) pattern.
- **Amazon**: Scalability — with millions of students and exams, focus on partition-aware query design.
