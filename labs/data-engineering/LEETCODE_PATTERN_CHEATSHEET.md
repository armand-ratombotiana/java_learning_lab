# Data Engineering LeetCode Pattern Cheatsheet

> SQL and Python LeetCode patterns relevant to Data Engineering interviews, organized by company frequency.

---

## 1. SQL Window Functions (High Frequency)

### Rank / Dense_Rank / Row_Number
| Problem | Company | Frequency |
|---------|---------|-----------|
| Department Top Three Salaries | Amazon, Meta, Snowflake | Very High |
| Rank Scores | Apple, Google | High |
| Find Median Given Frequency | Databricks | Medium |
| Employee Earning More Than Manager | All | High |

**Pattern:**
```sql
-- Top N per group
SELECT * FROM (
  SELECT *,
    ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC) AS rn
  FROM employees
) WHERE rn <= 3;
```

### Moving Average / Running Total
| Problem | Company | Frequency |
|---------|---------|-----------|
| Restaurant Growth | Snowflake, Amazon | High |
| Last 15 Minutes of Orders | Databricks | Medium |
| Running Total by Account | Meta | High |

**Pattern:**
```sql
-- 7-day moving average
SELECT visited_on, amount,
  AVG(amount) OVER (ORDER BY visited_on ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) AS avg_7d
FROM (
  SELECT visited_on, SUM(amount) AS amount
  FROM customer
  GROUP BY visited_on
) daily;
```

### Lag / Lead
| Problem | Company | Frequency |
|---------|---------|-----------|
| Month-over-Month Change | Amazon, Snowflake | Very High |
| Consecutive Numbers | Meta, Google | High |
| Rising Temperature | All | High |

**Pattern:**
```sql
-- YoY comparison
SELECT year, revenue,
  LAG(revenue) OVER (ORDER BY year) AS prev_year_revenue,
  (revenue - LAG(revenue) OVER (ORDER BY year)) / LAG(revenue) OVER (ORDER BY year) * 100 AS yoy_growth
FROM annual_revenue;
```

### First/Last Value
| Problem | Company | Frequency |
|---------|---------|-----------|
| First Purchase Date | Amazon, Shopify | High |
| User Retention Analysis | Meta | Very High |
| Sessionization | Snowflake, Confluent | Medium |

**Pattern:**
```sql
-- First and last purchase per customer
SELECT customer_id,
  MIN(order_date) AS first_purchase,
  MAX(order_date) AS last_purchase,
  DATEDIFF('day', MIN(order_date), MAX(order_date)) AS customer_lifetime_days
FROM orders
GROUP BY customer_id;
```

---

## 2. SQL Joins (High Frequency)

### Self Join for Graph/Relationships
| Problem | Company | Frequency |
|---------|---------|-----------|
| Find Mutual Friends | Meta | Very High |
| Second Degree Connections | LinkedIn, Meta | Medium |
| Employee Hierarchy | Microsoft, Amazon | High |

**Pattern:**
```sql
-- Mutual friends count
SELECT a.user_id, b.friend_id, COUNT(*) AS mutuals
FROM friendships a
JOIN friendships b ON a.friend_id = b.friend_id
WHERE a.user_id != b.friend_id
GROUP BY 1, 2;
```

### Anti-join / Not Exists
| Problem | Company | Frequency |
|---------|---------|-----------|
| Customers Who Never Order | All | Very High |
| Products Not Sold | Amazon | High |
| Inactive Users | Meta | Medium |

```sql
-- Customers with no orders
SELECT * FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id
WHERE o.customer_id IS NULL;
```

### Cross Join for Date Series
| Problem | Company | Frequency |
|---------|---------|-----------|
| Daily Active Users by Date | Meta | High |
| Funnel Analysis | Snowflake, Databricks | Medium |

```sql
-- Fill missing dates with 0
WITH dates AS (
  SELECT DISTINCT date FROM calendar
  WHERE date BETWEEN '2024-01-01' AND '2024-01-31'
),
users AS (
  SELECT DISTINCT user_id FROM activity
)
SELECT d.date, u.user_id, COALESCE(a.count, 0) AS daily_count
FROM dates d
CROSS JOIN users u
LEFT JOIN activity a ON d.date = a.date AND u.user_id = a.user_id;
```

---

## 3. SQL Aggregation Patterns (Very High Frequency)

### Funnel Analysis
| Problem | Company | Frequency |
|---------|---------|-----------|
| Product Funnel Conversion | Amazon, Meta | Very High |
| Ad Campaign Funnel | Google, Meta | High |
| Newsletter Conversion | All | Medium |

**Pattern:**
```sql
WITH steps AS (
  SELECT user_id,
    MAX(CASE WHEN event = 'landing' THEN 1 ELSE 0 END) AS step1,
    MAX(CASE WHEN event = 'signup' THEN 1 ELSE 0 END) AS step2,
    MAX(CASE WHEN event = 'purchase' THEN 1 ELSE 0 END) AS step3
  FROM events GROUP BY user_id
)
SELECT
  'landing' AS step, SUM(step1) AS users FROM steps
UNION ALL
SELECT 'signup', SUM(step2) FROM steps WHERE step1 = 1
UNION ALL
SELECT 'purchase', SUM(step3) FROM steps WHERE step2 = 1;
```

### Pivot / Unpivot
| Problem | Company | Frequency |
|---------|---------|-----------|
| Convert Rows to Columns | Amazon | High |
| Quarterly Sales | Snowflake | Medium |

**Pattern:**
```sql
SELECT date,
  MAX(CASE WHEN metric = 'views' THEN value END) AS views,
  MAX(CASE WHEN metric = 'clicks' THEN value END) AS clicks,
  MAX(CASE WHEN metric = 'conversions' THEN value END) AS conversions
FROM events
GROUP BY date;
```

### Conditional Aggregation
```sql
SELECT
  COUNT(*) AS total,
  COUNT(DISTINCT user_id) AS unique_users,
  SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) AS completed,
  AVG(CASE WHEN status = 'completed' THEN duration ELSE NULL END) AS avg_completed_duration
FROM orders;
```

---

## 4. SQL Deduplication (Medium-High Frequency)

### Keep Latest Row
| Problem | Company | Frequency |
|---------|---------|-----------|
| Latest Login | Meta | High |
| Most Recent Order Status | Amazon | High |
| Latest Employee Salary | Databricks | Medium |

**Pattern:**
```sql
WITH ranked AS (
  SELECT *,
    ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY login_time DESC) AS rn
  FROM logins
)
SELECT * FROM ranked WHERE rn = 1;
```

### Remove Duplicates (Delete)
```sql
DELETE FROM employees WHERE id NOT IN (
  SELECT MIN(id) FROM employees GROUP BY email
);
```

---

## 5. SQL Time Series (Medium Frequency)

### Date Bucketing
| Problem | Company | Frequency |
|---------|---------|-----------|
| Monthly User Growth | Meta | High |
| Weekly Revenue Report | Amazon | High |
| Daily New Users | All | Medium |

**Pattern:**
```sql
SELECT
  DATE_TRUNC('week', order_date) AS week_start,
  SUM(revenue) AS weekly_revenue,
  COUNT(DISTINCT customer_id) AS paying_customers
FROM orders
GROUP BY 1
ORDER BY 1;
```

### Sessionization
**Pattern:**
```sql
WITH user_pages AS (
  SELECT user_id, page, timestamp,
    CASE WHEN DATEDIFF('minute', LAG(timestamp) OVER (PARTITION BY user_id ORDER BY timestamp), timestamp) > 30
      THEN 1 ELSE 0 END AS is_new_session
  FROM page_views
),
session_ids AS (
  SELECT *,
    SUM(is_new_session) OVER (PARTITION BY user_id ORDER BY timestamp) AS session_id
  FROM user_pages
)
SELECT user_id, session_id,
  MIN(timestamp) AS session_start,
  MAX(timestamp) AS session_end,
  COUNT(*) AS pages_viewed
FROM session_ids
GROUP BY 1, 2;
```

---

## 6. Python Data Processing (Medium Frequency)

### Group By Aggregate
```
Input:  [("user1", "page1"), ("user1", "page2"), ("user2", "page1")]
Output: {"user1": 2, "user2": 1}
```
```python
from collections import Counter
counts = Counter(user for user, page in events)
```

### Merge Intervals
```
Input:  [[1,3], [2,6], [8,10], [15,18]]
Output: [[1,6], [8,10], [15,18]]
```
```python
def merge(intervals):
    intervals.sort(key=lambda x: x[0])
    merged = [intervals[0]]
    for start, end in intervals[1:]:
        if start <= merged[-1][1]:
            merged[-1][1] = max(merged[-1][1], end)
        else:
            merged.append([start, end])
    return merged
```

### Running Median
```python
import heapq
class RunningMedian:
    def __init__(self):
        self.small = []  # max heap
        self.large = []  # min heap

    def add_num(self, num):
        heapq.heappush(self.small, -num)
        if self.small and self.large and -self.small[0] > self.large[0]:
            heapq.heappush(self.large, -heapq.heappop(self.small))
        if len(self.small) > len(self.large) + 1:
            heapq.heappush(self.large, -heapq.heappop(self.small))
        elif len(self.large) > len(self.small):
            heapq.heappush(self.small, -heapq.heappop(self.large))

    def find_median(self):
        if len(self.small) > len(self.large):
            return -self.small[0]
        return (-self.small[0] + self.large[0]) / 2
```

---

## 7. Schema Design Problems (DE-specific)

### Design a Star Schema
**Prompt:** Design star schema for e-commerce:
- Fact: orders (order_id_fk, customer_id_fk, product_id_fk, date_id_fk, quantity, unit_price)
- Dim: customers, products, dates

### Design Slowly Changing Dimension
**Prompt:** Design SCD Type 2 for customer address tracking:
```sql
CREATE TABLE dim_customer (
  customer_sk INT PRIMARY KEY,
  customer_id INT,
  name VARCHAR,
  address VARCHAR,
  effective_date DATE,
  end_date DATE,
  is_current BOOLEAN
);
```

### Design Event Table
**Prompt:** Track user events for analytics:
```sql
CREATE TABLE user_events (
  event_id BIGINT,
  user_id INT,
  event_type VARCHAR,
  event_time TIMESTAMP,
  properties VARIANT, -- JSON/半構造データ
  client_info VARIANT, -- user-agent, ip, device
  PRIMARY KEY (event_id)
)
PARTITION BY DATE(event_time);
```

---

## 8. Company Frequency Reference

### Amazon (Highest Frequency)
| Problem | Type | Frequency |
|---------|------|-----------|
| Department Top 3 Salaries | Window | Very High |
| Monthly Revenue by Product | Window | Very High |
| Customer Order History | Self join | High |
| Running Total for Sales | Window | High |
| Funnel Analysis | Aggregation | High |
| Top K Frequent Elements | Python | Medium |

### Meta/Facebook (Highest Frequency)
| Problem | Type | Frequency |
|---------|------|-----------|
| Friends Connection Strength | Self join | Very High |
| User Retention Cohorts | Window | Very High |
| Session Duration | Window | High |
| Page Likes Analytics | Aggregation | High |
| Ad Campaign Funnel | Aggregation | High |
| Mutual Friends | Self join | Very High |

### Snowflake (High Frequency)
| Problem | Type | Frequency |
|---------|------|-----------|
| Window Function Heavy | Window | Very High |
| JSON/半構造データ Parsing | LATERAL FLATTEN | Very High |
| Time Travel Queries | AT/BEFORE | Medium |
| MERGE Patterns | DML | High |
| Performance Tuning | EXPLAIN | Medium |

### Google (High Frequency)
| Problem | Type | Frequency |
|---------|------|-----------|
| Moving Average | Window | High |
| Median Finding | Percentile | Medium |
| Cohort Analysis | Window | High |
| Date Series Generation | UNNEST | Medium |
| Pivot Queries | CASE | Medium |

### Databricks (High Frequency)
| Problem | Type | Frequency |
|---------|------|-----------|
| Delta Lake MERGE | DML | Very High |
| Window Functions | Window | High |
| Schema Evolution | ALTER | Medium |
| ZORDER Optimization | CLUSTER | Medium |

---

## 9. Quick Reference: SQL Functions by Warehouse

| Function | Snowflake | BigQuery | Redshift | Databricks |
|----------|-----------|----------|----------|------------|
| JSON parse | PARSE_JSON | JSON_EXTRACT | JSON_EXTRACT_PATH_TEXT | from_json |
| Flatten | LATERAL FLATTEN | UNNEST | - | explode |
| Date trunc | DATE_TRUNC | DATE_TRUNC | DATE_TRUNC | DATE_TRUNC |
| Date diff | DATEDIFF | DATE_DIFF | DATEDIFF | DATEDIFF |
| Window | ROWS/RANGE | ROWS/RANGE | ROWS/RANGE | ROWS/RANGE |
| Median | MEDIAN | PERCENTILE_CONT | MEDIAN | PERCENTILE |
| Merge | MERGE INTO | MERGE INTO | MERGE | MERGE INTO |
| Sample | SAMPLE | TABLESAMPLE | TABLESAMPLE | TABLESAMPLE |
| Pivot | PIVOT | PIVOT | - | PIVOT |
| Array | ARRAY_AGG | ARRAY_AGG | LISTAGG | COLLECT_LIST |
