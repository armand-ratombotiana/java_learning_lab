# LEETCODE_SOLUTIONS — Advanced SQL

## Analytical SQL Solutions

| LeetCode Problem | Oracle Function | PostgreSQL Function | Difficulty |
|-----------------|----------------|-------------------|------------|
| 177 Nth Highest | DENSE_RANK | DENSE_RANK | Medium |
| 178 Rank Scores | DENSE_RANK | DENSE_RANK | Medium |
| 180 Consecutive Numbers | LEAD | LEAD | Medium |
| 184 Dept Highest Salary | ROW_NUMBER | ROW_NUMBER | Medium |
| 185 Top 3 Salaries | DENSE_RANK + PARTITION | DENSE_RANK + PARTITION | Hard |
| 550 Game Play IV | MIN, date diff | MIN, date arithmetic | Medium |
| 569 Median Salary | PERCENTILE_CONT / ROW_NUMBER | PERCENTILE_CONT | Hard |
| 601 Human Traffic | LEAD/LAG | LEAD/LAG | Hard |
| 1097 Game Play V | CTE + window | CTE + window | Hard |
| 1126 Active Businesses | AVG OVER | AVG OVER | Medium |

### Window Functions Cheat Sheet
```sql
-- Ranking: ROW_NUMBER, RANK, DENSE_RANK (no gaps)
-- Value: LEAD, LAG, FIRST_VALUE, LAST_VALUE, NTH_VALUE
-- Aggregate: SUM, AVG, COUNT with OVER
-- Distribution: NTILE, PERCENT_RANK, CUME_DIST
-- Stats: RATIO_TO_REPORT (Oracle), STDDEV, VARIANCE
```

### Complex Pattern: LeetCode 1097
```sql
-- Day 1 retention after install date
WITH installs AS (
    SELECT player_id, MIN(event_date) AS install_dt
    FROM Activity GROUP BY player_id
)
SELECT install_dt, COUNT(*) AS installs,
       ROUND(COUNT(CASE WHEN a.event_date = i.install_dt + 1 THEN 1 END)::numeric
             / COUNT(*), 2) AS Day1_retention
FROM installs i LEFT JOIN Activity a ON i.player_id = a.player_id
GROUP BY install_dt;
```
