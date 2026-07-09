# Recursive CTEs — SQL Code Deep Dive

## Basic Recursive CTE
```sql
WITH RECURSIVE employee_tree AS (
  -- Anchor: top-level managers
  SELECT employee_id, first_name, last_name, manager_id, 1 AS level
  FROM employees
  WHERE manager_id IS NULL
  UNION ALL
  -- Recursive: direct reports
  SELECT e.employee_id, e.first_name, e.last_name, e.manager_id, t.level + 1
  FROM employees e
  JOIN employee_tree t ON e.manager_id = t.employee_id
)
SELECT employee_id, level, first_name, last_name
FROM employee_tree
ORDER BY level, last_name;
```

## Organ Chart with Path
```sql
WITH RECURSIVE org_chart AS (
  SELECT employee_id, first_name || ' ' || last_name AS full_name,
         CAST(first_name AS VARCHAR(4000)) AS path, 1 AS depth,
         manager_id, 0 AS sort_order
  FROM employees WHERE manager_id IS NULL
  UNION ALL
  SELECT e.employee_id, e.first_name || ' ' || e.last_name,
         oc.path || ' / ' || e.first_name, oc.depth + 1,
         e.manager_id, oc.sort_order * 10 + ROW_NUMBER() OVER (ORDER BY e.last_name)
  FROM employees e
  JOIN org_chart oc ON e.manager_id = oc.employee_id
)
SELECT LPAD(' ', (depth-1)*2) || full_name AS name, full_name, path, depth
FROM org_chart
ORDER BY sort_order;
```

## Date Dimension (Non-Recursive CTE Example)
```sql
WITH RECURSIVE dates AS (
  SELECT DATE '2024-01-01' AS dt
  UNION ALL
  SELECT dt + INTERVAL '1' DAY FROM dates WHERE dt < DATE '2024-12-31'
)
SELECT dt, EXTRACT(YEAR FROM dt) AS yr, EXTRACT(MONTH FROM dt) AS mo,
       TO_CHAR(dt, 'Day') AS day_name, EXTRACT(DOW FROM dt) AS dow
FROM dates;
```

## Topological Sort (Bill of Materials)
```sql
WITH RECURSIVE bom AS (
  SELECT part_id, part_name, quantity, 1 AS level,
         CAST(quantity AS INTEGER) AS total_qty,
         CAST(part_name AS VARCHAR(100)) AS bom_path
  FROM parts WHERE part_id = 'ROOT'
  UNION ALL
  SELECT c.part_id, c.part_name, c.quantity, b.level + 1,
         b.total_qty * c.quantity,
         b.bom_path || ' > ' || c.part_name
  FROM parts c
  JOIN bom b ON b.part_id = c.parent_part_id
)
SELECT * FROM bom ORDER BY level, bom_path;
```

## Graph Reachability
```sql
WITH RECURSIVE reachable AS (
  SELECT start_node, end_node, 1 AS hops, CAST(start_node || '->' || end_node AS VARCHAR(200)) AS path
  FROM edges WHERE start_node = 'A'
  UNION ALL
  SELECT r.start_node, e.end_node, r.hops + 1, r.path || '->' || e.end_node
  FROM reachable r
  JOIN edges e ON r.end_node = e.start_node
  WHERE r.hops < 10
)
CYCLE start_node, end_node SET is_cycle TO 'Y' DEFAULT 'N'
SELECT DISTINCT start_node, end_node, hops, path FROM reachable
WHERE NOT cycle ORDER BY distance;
```

## CYCLE Detection
```sql
WITH RECURSIVE graph_traversal AS (
  SELECT node_id, parent_id, 1 AS depth,
         CAST('' AS VARCHAR(4000)) AS cycle_detection
  FROM graph WHERE parent_id IS NULL
  UNION ALL
  SELECT g.node_id, g.parent_id, t.depth + 1,
         CASE WHEN t.cycle_detection LIKE '%' || g.node_id || '%' THEN 'CYCLE' ELSE '' END
  FROM graph g
  JOIN graph_traversal t ON g.parent_id = t.node_id
  WHERE t.cycle_detection IS NULL OR t.cycle_detection = ''
)
CYCLE node_id SET is_cycle TO 1 DEFAULT 0
SELECT * FROM graph_traversal;
```

## Hierarchical Aggregation
```sql
WITH RECURSIVE totals AS (
  SELECT dept_id, dept_name, NULL AS parent_dept_id, budget, budget AS total_budget
  FROM departments WHERE parent_dept_id IS NULL
  UNION ALL
  SELECT d.dept_id, d.dept_name, d.parent_dept_id, d.budget, t.total_budget + d.budget
  FROM departments d
  JOIN totals t ON d.parent_dept_id = t.dept_id
)
SELECT dept_id, dept_name, budget, total_budget FROM totals;
```

## Fibonacci with Recursive CTE
```sql
WITH RECURSIVE fibonacci(n, fib_n, fib_n1) AS (
  SELECT 1, 0, 1 FROM dual
  UNION ALL
  SELECT n + 1, fib_n1, fib_n + fib_n1
  FROM fibonacci WHERE n < 20
)
SELECT n, fib_n AS fibonacci_number FROM fibonacci;
```

## Prime Numbers with Recursive CTE
```sql
WITH RECURSIVE numbers AS (
  SELECT 2 AS n FROM dual
  UNION ALL
  SELECT n + 1 FROM numbers WHERE n < 100
),
primes AS (
  SELECT n FROM numbers n1
  WHERE NOT EXISTS (
    SELECT 1 FROM numbers n2
    WHERE n2.n < n1.n AND n2.n > 1 AND n1.n % n2.n = 0
  )
)
SELECT * FROM primes ORDER BY n;
```

## Working with Intervals
```sql
WITH RECURSIVE date_range AS (
  SELECT DATE '2024-01-01' AS date_val
  UNION ALL
  SELECT DATE '2024-01-01' + INTERVAL '1' DAY
  FROM date_range
  WHERE date_val < DATE '2024-01-31'
)
SELECT date_val, COUNT(s.sale_id) AS daily_sales
FROM date_range dr
LEFT JOIN sales s ON s.sale_date = dr.date_val
GROUP BY dr.date_val
ORDER BY dr.date_val;
```