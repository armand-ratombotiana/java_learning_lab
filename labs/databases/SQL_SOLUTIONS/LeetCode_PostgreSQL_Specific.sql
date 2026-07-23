-- ============================================================================
-- LeetCode_PostgreSQL_Specific.sql
-- PostgreSQL-Specific SQL Features for LeetCode-style problems
-- Demonstrates PostgreSQL-only syntax: DISTINCT ON, GENERATE_SERIES, ARRAY, etc.
-- ============================================================================

-- ============================================================================
-- PostgreSQL DISTINCT ON
-- ============================================================================

-- Problem: Find the most recent order for each customer
SELECT DISTINCT ON (customer_id) id, customer_id, order_date, total_amount
FROM orders
ORDER BY customer_id, order_date DESC;

-- Problem: Find the highest-paid employee per department
SELECT DISTINCT ON (department_id) employee_id, name, department_id, salary
FROM employee
ORDER BY department_id, salary DESC;

-- ============================================================================
-- PostgreSQL GENERATE_SERIES
-- ============================================================================

-- Problem: Generate all dates between two dates
SELECT generate_series(
    '2024-01-01'::date,
    '2024-01-31'::date,
    '1 day'::interval
) AS date_sequence;

-- Problem: Fill missing dates in a time series
WITH all_dates AS (
    SELECT generate_series(
        MIN(order_date), MAX(order_date), '1 day'::interval
    )::date AS date
    FROM orders
)
SELECT ad.date, COALESCE(SUM(o.total_amount), 0) AS daily_total
FROM all_dates ad
LEFT JOIN orders o ON ad.date = o.order_date
GROUP BY ad.date
ORDER BY ad.date;

-- Problem: Generate a series of numbers (like for ranking or pivoting)
SELECT generate_series(1, 10) AS numbers;

-- ============================================================================
-- PostgreSQL ARRAY and Array Functions
-- ============================================================================

-- Problem: Aggregate values into an array
SELECT department_id,
       ARRAY_AGG(employee_name ORDER BY hire_date) AS employees
FROM employee
GROUP BY department_id;

-- Problem: Check if a value is in an array
SELECT *
FROM products
WHERE 'Electronics' = ANY(categories);

-- Problem: Unnest an array (convert array to rows)
WITH product_tags AS (
    SELECT product_id, ARRAY['sale', 'new', 'popular'] AS tags
    FROM products
    WHERE product_id = 1
)
SELECT product_id, unnest(tags) AS tag
FROM product_tags;

-- Problem: Count array elements
SELECT id, name, ARRAY_LENGTH(tags, 1) AS tag_count
FROM products;

-- ============================================================================
-- PostgreSQL JSON/JSONB Functions
-- ============================================================================

-- Problem: Query JSONB data
SELECT id, name, data->>'email' AS email,
       data->>'role' AS role
FROM users
WHERE data @> '{"active": true}';

-- Problem: Update a JSONB field
UPDATE users
SET data = jsonb_set(data, '{last_login}', '"2024-01-15T10:30:00Z"')
WHERE id = 1;

-- Problem: Extract keys from JSONB
SELECT id, jsonb_object_keys(data) AS field_name
FROM users
WHERE id = 1;

-- ============================================================================
-- PostgreSQL Full-Text Search
-- ============================================================================

-- Problem: Search for documents containing specific words
SELECT id, title, body,
       ts_rank(to_tsvector('english', body), plainto_tsquery('english', 'database performance')) AS rank
FROM documents
WHERE to_tsvector('english', body) @@ plainto_tsquery('english', 'database performance')
ORDER BY rank DESC;

-- Problem: Highlight search terms
SELECT id, title,
       ts_headline('english', body, plainto_tsquery('english', 'optimization'),
                   'StartSel = <mark>, StopSel = </mark>') AS highlighted
FROM documents
WHERE to_tsvector('english', body) @@ plainto_tsquery('english', 'optimization');

-- ============================================================================
-- PostgreSQL Window Functions (Advanced)
-- ============================================================================

-- Problem: Filter rows using window function result
WITH salary_ranks AS (
    SELECT name, salary, department_id,
           NTILE(4) OVER (PARTITION BY department_id ORDER BY salary DESC) AS quartile
    FROM employee
)
SELECT name, salary, department_id
FROM salary_ranks
WHERE quartile = 1; -- Top quartile per department

-- Problem: Dense ranking with ties
SELECT score,
       DENSE_RANK() OVER (ORDER BY score DESC) AS rank
FROM scores
ORDER BY score DESC;

-- Problem: Find gaps in a sequence
WITH numbered AS (
    SELECT id,
           LEAD(id) OVER (ORDER BY id) AS next_id
    FROM sequence_table
)
SELECT id AS gap_start, next_id - 1 AS gap_end
FROM numbered
WHERE next_id - id > 1;

-- ============================================================================
-- PostgreSQL Recursive CTE
-- ============================================================================

-- Problem: Generate Fibonacci sequence
WITH RECURSIVE fibonacci(a, b, n) AS (
    SELECT 0::bigint AS a, 1::bigint AS b, 1 AS n
    UNION ALL
    SELECT b, a + b, n + 1
    FROM fibonacci
    WHERE n < 20
)
SELECT n, a AS fibonacci_number
FROM fibonacci;

-- Problem: Generate calendar hierarchy
WITH RECURSIVE dates AS (
    SELECT '2024-01-01'::date AS date
    UNION ALL
    SELECT date + 1
    FROM dates
    WHERE date < '2024-01-31'
)
SELECT date,
       EXTRACT(DOW FROM date) AS day_of_week,
       TO_CHAR(date, 'Day') AS day_name
FROM dates;

-- ============================================================================
-- PostgreSQL Range Types
-- ============================================================================

-- Problem: Find overlapping date ranges
SELECT r1.room_id, r1.booking_id, r1.check_in, r1.check_out
FROM bookings r1
JOIN bookings r2 ON r1.room_id = r2.room_id
    AND r1.booking_id != r2.booking_id
    AND daterange(r1.check_in, r1.check_out, '[]') && daterange(r2.check_in, r2.check_out, '[]')
WHERE r1.check_in < r2.check_in;

-- Problem: Query using range containment
SELECT *
FROM bookings
WHERE daterange(check_in, check_out, '[]') @> '2024-06-15'::date;

-- ============================================================================
-- PostgreSQL Materialized Views
-- ============================================================================

-- Problem: Create and refresh a materialized view for reporting
CREATE MATERIALIZED VIEW monthly_sales_summary AS
SELECT DATE_TRUNC('month', order_date) AS month,
       product_category,
       SUM(amount) AS total_sales,
       COUNT(*) AS order_count,
       AVG(amount) AS avg_order_value
FROM orders
JOIN products ON orders.product_id = products.id
GROUP BY DATE_TRUNC('month', order_date), product_category
WITH DATA;

-- Refresh materialized view
REFRESH MATERIALIZED VIEW CONCURRENTLY monthly_sales_summary;

-- ============================================================================
-- PostgreSQL Full Outer Join (for completeness)
-- ============================================================================

-- Problem: Compare employee lists between two departments
SELECT COALESCE(d1.employee_id, d2.employee_id) AS employee_id,
       CASE WHEN d1.employee_id IS NOT NULL AND d2.employee_id IS NOT NULL THEN 'Both'
            WHEN d1.employee_id IS NOT NULL THEN 'Dept A only'
            ELSE 'Dept B only'
       END AS status
FROM dept_a_employees d1
FULL OUTER JOIN dept_b_employees d2 ON d1.employee_id = d2.employee_id
ORDER BY employee_id;

-- ============================================================================
-- PostgreSQL LATERAL Joins
-- ============================================================================

-- Problem: Find top 2 products per category
SELECT c.category_name, p.product_name, p.price
FROM categories c
LEFT JOIN LATERAL (
    SELECT product_name, price
    FROM products
    WHERE category_id = c.id
    ORDER BY price DESC
    LIMIT 2
) p ON TRUE
WHERE c.active = true
ORDER BY c.category_name, p.price DESC;

-- ============================================================================
-- PostgreSQL ON CONFLICT (UPSERT)
-- ============================================================================

-- Problem: Insert or update on conflict
INSERT INTO users (id, email, name, updated_at)
VALUES (1, 'john@example.com', 'John Doe', NOW())
ON CONFLICT (id) DO UPDATE
SET email = EXCLUDED.email,
    name = EXCLUDED.name,
    updated_at = EXCLUDED.updated_at;

-- Problem: Do nothing on conflict
INSERT INTO audit_log (id, action, timestamp)
VALUES (123, 'login', NOW())
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- END OF POSTGRESQL-SPECIFIC SOLUTIONS
-- ============================================================================
