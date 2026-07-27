# SQL Problem Walkthrough: 05-window-functions

## Problem 1: Rank Scores (LC SQL 178) — Oracle

### Interview Scenario
"Oracle interview: The Scores table contains player scores. Rank them according to standard competition ranking — same score gets the same rank, and ranks are consecutive with no gaps."

### The Problem
Table **Scores**: id (NUMBER PK), score (NUMBER). Write a query to rank scores. If two scores tie, they get the same rank. After a tie, the next rank should be the next consecutive integer (no gaps). Return score and rank, ordered by score descending.

### Step 1: Understand Schema
- Single table, one column of interest (score)
- Standard competition ranking = DENSE_RANK
- Olympic ranking (with gaps) = RANK

Sample data:
| id | score |
|----|-------|
| 1  | 3.50  |
| 2  | 3.65  |
| 3  | 4.00  |
| 4  | 3.85  |
| 5  | 4.00  |
| 6  | 3.65  |

Expected:
| score | rank |
|-------|------|
| 4.00  | 1    |
| 4.00  | 1    |
| 3.85  | 2    |
| 3.65  | 3    |
| 3.65  | 3    |
| 3.50  | 4    |

### Step 2: Think Aloud
DENSE_RANK vs RANK: The problem says "consecutive with no gaps" → DENSE_RANK. If it said "skip ranks after ties" → RANK.

### Step 3: Write the Query
```sql
SELECT score,
       DENSE_RANK() OVER (ORDER BY score DESC) AS rank
  FROM scores
 ORDER BY score DESC;
```

Alternative using RANK for comparison:
```sql
SELECT score,
       RANK() OVER (ORDER BY score DESC) AS rank_with_gaps,
       DENSE_RANK() OVER (ORDER BY score DESC) AS rank_no_gaps
  FROM scores
 ORDER BY score DESC;
```

### Step 4: Execution Plan
```
| Id | Operation          | Name   | Cost |
|----|--------------------|--------|------|
|  0 | SELECT STATEMENT   |        |      |
|  1 |  WINDOW SORT       |        |      |
|  2 |   TABLE ACCESS FULL| SCORES |      |
```

Oracle performs a full table scan and sorts by score DESC in memory or on disk to compute the window function.

### Step 5: Optimize
```sql
CREATE INDEX scores_score_desc_idx ON scores(score DESC);
```

With this index, Oracle might avoid the SORT operation if the optimizer recognizes the index provides the required ordering. For tables with millions of rows, consider using the `ORDER BY score DESC` on the index directly.

### Step 6: Test
Edge cases:
- **Duplicate scores**: All get the same rank
- **All same score**: Everyone rank 1
- **Single row**: Rank 1
- **NULL scores**: DENSE_RANK treats NULLs as largest (DESC order). NULLs get rank 1 if present and sorted DESC.

```sql
-- Verify distinct ranks
WITH score_test AS (
  SELECT 3.5 AS score FROM dual UNION ALL
  SELECT 3.5 FROM dual UNION ALL
  SELECT 4.0 FROM dual UNION ALL
  SELECT 3.8 FROM dual
)
SELECT score,
       DENSE_RANK() OVER (ORDER BY score DESC) AS dr,
       RANK() OVER (ORDER BY score DESC) AS r,
       ROW_NUMBER() OVER (ORDER BY score DESC) AS rn
  FROM score_test;
```

### Company Evaluation
- **Oracle**: DENSE_RANK is the key function. Discuss WINDOW SORT performance.
- **Amazon**: Redshift supports the same syntax. Consider leader node compute limits.
- **Google**: Spanner — same syntax. Discuss distributed sort costs.

---

## Problem 2: Last Person to Fit in the Elevator (LC SQL 1204) — Amazon

### Interview Scenario
"Amazon interview: A Queue table lists people waiting for an elevator with their weight and turn order. The elevator has a weight limit of 1000 kg. Find the last person who can get on before the weight limit is exceeded."

### The Problem
Table **Queue**: person_id (NUMBER PK), person_name (VARCHAR2), weight (NUMBER), turn (NUMBER, order of entry). The elevator has a 1000 kg limit. Find the person_name of the last person who can fit without exceeding the limit, assuming everyone boards in turn order.

### Step 1: Understand Schema
- turn determines boarding order (1 = first)
- weight per person in kg
- Cumulative sum of weights must stay <= 1000
- Need the last person who qualifies (highest turn where running total <= 1000)

Sample data:
| person_id | person_name | weight | turn |
|-----------|-------------|--------|------|
| 1         | Alice       | 250    | 1    |
| 2         | Bob         | 300    | 2    |
| 3         | Carol       | 350    | 3    |
| 4         | Dave        | 400    | 4    |
| 5         | Eve         | 200    | 5    |

Cumulative: 250 (turn 1), 550 (2), 900 (3), 1300 (4) exceeds → Carol is last at turn 3.

### Step 2: Think Aloud
We need cumulative sum ordered by turn. Use SUM() OVER (ORDER BY turn ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) to compute running total. Then filter where cum_weight <= 1000, and take the last person (MAX turn).

### Step 3: Write the Query
```sql
SELECT person_name
  FROM (
    SELECT person_name,
           turn,
           SUM(weight) OVER (ORDER BY turn) AS cum_weight
      FROM queue
  )
 WHERE cum_weight <= 1000
 ORDER BY turn DESC
 FETCH FIRST 1 ROW ONLY;
```

Alternative using MAX turn:
```sql
SELECT person_name
  FROM (
    SELECT person_name,
           turn,
           SUM(weight) OVER (ORDER BY turn) AS cum_weight
      FROM queue
  )
 WHERE cum_weight <= 1000
   AND turn = (
     SELECT MAX(turn)
       FROM (
         SELECT turn,
                SUM(weight) OVER (ORDER BY turn) AS cum_weight
           FROM queue
       )
      WHERE cum_weight <= 1000
   );
```

### Step 4: Execution Plan
```
| Id | Operation                     | Name  | Cost |
|----|-------------------------------|-------|------|
|  0 | SELECT STATEMENT              |       |      |
|  1 |  VIEW                         |       |      |
|  2 |   WINDOW SORT                 |       |      |
|  3 |    TABLE ACCESS FULL          | QUEUE |      |
|  4 |  SORT ORDER BY STOP           |       |      |
```

Oracle's WINDOW SORT computes the cumulative sum. SORT ORDER BY STOP with FETCH FIRST 1 means Oracle stops sorting once it finds the first row in DESC order.

### Step 5: Optimize
```sql
CREATE INDEX queue_turn_idx ON queue(turn);
```

For a large queue:
- The WINDOW SUM requires either a sort or the data to be pre-ordered
- With an index on turn, Oracle can do an INDEX FULL SCAN (avoiding the sort)
- Consider setting workarea size appropriately for large sorts in memory

### Step 6: Test
Edge cases:
- **First person exceeds limit**: No one boards? Problem says "last person who can fit", returns NULL/empty.
- **Exact cumulative sum = 1000**: That person is included
- **All people fit**: The last person in the queue is returned
- **Single person**: Returned if weight <= 1000
- **Zero weight**: Should be allowed (unrealistic but valid)

```sql
-- Test edge case: exact limit
WITH queue_test AS (
  SELECT 1 AS person_id, 'A' AS person_name, 500 AS weight, 1 AS turn FROM dual UNION ALL
  SELECT 2, 'B', 500, 2 FROM dual UNION ALL
  SELECT 3, 'C', 1, 3 FROM dual
)
SELECT person_name
  FROM (
    SELECT person_name, turn,
           SUM(weight) OVER (ORDER BY turn) AS cum
      FROM queue_test
  )
 WHERE cum <= 1000
 ORDER BY turn DESC
 FETCH FIRST 1 ROW ONLY;
-- B at turn 2 (cum = 1000) is the last to fit
```

### Company Evaluation
- **Amazon**: Real-world elevator scheduling problem. Discussing running totals at scale in DynamoDB or Redshift.
- **Oracle**: SUM() OVER is the straightforward solution. Emphasize window function frame clause defaults (RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW).
- **Google**: Spanner supports the same syntax. Discuss distributed running totals challenges.

---

## Problem 3: Report Contiguous Dates (LC SQL 1225) — Microsoft

### Interview Scenario
"Microsoft interview: The Failed and Succeeded tables track daily task outcomes. Report the start and end date of each contiguous period of success and each contiguous period of failure."

### The Problem
Tables: **Succeeded** (success_date DATE PK, no duplicates), **Failed** (fail_date DATE PK, no duplicates). Both tables record dates, and they are mutually exclusive (a date can't be both).

Write a query to produce: period_state ('succeeded' or 'failed'), start_date, end_date for each contiguous date range. Order by start_date.

### Step 1: Understand Schema
- Succeeded: dates where tasks succeeded (no gaps in dates — every date in the range is recorded, but there are gaps between ranges)
- Failed: dates where tasks failed
- Together they partition a date range
- Need gaps-and-islands approach on dates

Sample data:
| success_date |
|--------------|
| 2019-01-01   |
| 2019-01-02   |
| 2019-01-03   |
| 2019-01-06   |

| fail_date |
|-----------|
| 2019-01-04 |
| 2019-01-05 |

Expected:
| period_state | start_date | end_date   |
|--------------|------------|------------|
| succeeded    | 2019-01-01 | 2019-01-03 |
| failed       | 2019-01-04 | 2019-01-05 |
| succeeded    | 2019-01-06 | 2019-01-06 |

### Step 2: Think Aloud
Combine both tables with UNION ALL, then apply gaps-and-islands using date difference with ROW_NUMBER.

Key technique: dates are sequential with no gaps inside a range. So (date - ROW_NUMBER() OVER(PARTITION BY state ORDER BY date)) gives a constant group identifier for each contiguous block.

### Step 3: Write the Query
```sql
WITH all_dates AS (
  SELECT success_date AS dt, 'succeeded' AS status
    FROM succeeded
  UNION ALL
  SELECT fail_date, 'failed'
    FROM failed
),
grouped AS (
  SELECT dt,
         status,
         dt - ROW_NUMBER() OVER (
           PARTITION BY status
           ORDER BY dt
         ) AS grp
    FROM all_dates
)
SELECT status AS period_state,
       MIN(dt) AS start_date,
       MAX(dt) AS end_date
  FROM grouped
 GROUP BY status, grp
 ORDER BY start_date;
```

### Step 4: Execution Plan
```
| Id | Operation             | Name      | Cost |
|----|-----------------------|-----------|------|
|  0 | SELECT STATEMENT      |           |      |
|  1 |  SORT GROUP BY        |           |      |
|  2 |   VIEW                | GROUPED   |      |
|  3 |    WINDOW SORT        |           |      |
|  4 |     VIEW              | ALL_DATES |      |
|  5 |      UNION-ALL        |           |      |
|  6 |       TABLE ACCESS FULL | SUCCEEDED |     |
|  7 |       TABLE ACCESS FULL | FAILED    |     |
```

### Step 5: Optimize
```sql
CREATE INDEX succeeded_date_idx ON succeeded(success_date);
CREATE INDEX failed_date_idx ON failed(fail_date);
```

For large date ranges (years of data), consider:
- Partitioning both tables by year
- Using DATE data type with proper indexing

### Step 6: Test
Edge cases:
- **Empty table**: No rows returned for that status
- **Single date in a range**: MIN = MAX = that date
- **Interleaved success/failure**: Alternating ranges
- **First date or last date is success/failure**: Works with the gaps-and-islands approach

```sql
-- Edge case: alternating days
WITH s AS (
  SELECT DATE '2019-01-01' AS d FROM dual UNION ALL
  SELECT DATE '2019-01-03' FROM dual UNION ALL
  SELECT DATE '2019-01-05' FROM dual
),
f AS (
  SELECT DATE '2019-01-02' AS d FROM dual UNION ALL
  SELECT DATE '2019-01-04' FROM dual
),
combined AS (
  SELECT d, 'succeeded' AS status FROM s
  UNION ALL
  SELECT d, 'failed' FROM f
),
g AS (
  SELECT d, status,
         d - ROW_NUMBER() OVER (PARTITION BY status ORDER BY d) AS grp
    FROM combined
)
SELECT status, MIN(d) AS start_d, MAX(d) AS end_d
  FROM g
 GROUP BY status, grp
 ORDER BY start_d;
-- Expected: 6 rows alternating
```

### Company Evaluation
- **Microsoft**: T-SQL uses same syntax. Discuss date arithmetic differences (DATEDIFF in T-SQL).
- **Oracle**: DATE arithmetic is natural (date - number = date). Emphasize date-to-row_number difference technique.
- **Amazon**: Discuss how this translates to Redshift's sort keys and distribution styles for large date-based tables.
