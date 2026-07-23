-- ============================================================================
-- LeetCode_Hard.sql
-- LeetCode SQL Solutions — Hard Problems (Oracle SQL + PostgreSQL syntax)
-- ============================================================================

-- ============================================================================
-- PROBLEM 185: Department Top Three Salaries
-- ============================================================================
-- Tables: Employee, Department
-- Task: Find employees in the top three unique salaries per department.

-- Oracle SQL:
SELECT Department, Employee, Salary
FROM (
    SELECT d.name AS Department, e.name AS Employee, e.salary AS Salary,
           DENSE_RANK() OVER (PARTITION BY e.departmentId ORDER BY e.salary DESC) AS dr
    FROM Employee e
    JOIN Department d ON e.departmentId = d.id
)
WHERE dr <= 3
ORDER BY Department, Salary DESC;

-- PostgreSQL (same syntax):
SELECT Department, Employee, Salary
FROM (
    SELECT d.name AS Department, e.name AS Employee, e.salary AS Salary,
           DENSE_RANK() OVER (PARTITION BY e.departmentId ORDER BY e.salary DESC) AS dr
    FROM Employee e
    JOIN Department d ON e.departmentId = d.id
) t
WHERE dr <= 3
ORDER BY Department, Salary DESC;

-- ============================================================================
-- PROBLEM 262: Trips and Users
-- ============================================================================
-- Tables: Trips, Users
-- Task: Find cancellation rate for unbanned users between Oct 1-3, 2013.

-- Oracle SQL:
SELECT t.request_at AS Day,
       ROUND(
           COUNT(CASE WHEN t.status IN ('cancelled_by_driver', 'cancelled_by_client') THEN 1 END)
           / COUNT(*)
       , 2) AS "Cancellation Rate"
FROM Trips t
JOIN Users c ON t.client_id = c.users_id AND c.banned = 'No'
JOIN Users d ON t.driver_id = d.users_id AND d.banned = 'No'
WHERE t.request_at BETWEEN '2013-10-01' AND '2013-10-03'
GROUP BY t.request_at
ORDER BY t.request_at;

-- PostgreSQL:
SELECT t.request_at AS Day,
       ROUND(
           SUM(CASE WHEN t.status LIKE 'cancelled%' THEN 1 ELSE 0 END)::numeric
           / COUNT(*)
       , 2) AS "Cancellation Rate"
FROM Trips t
JOIN Users c ON t.client_id = c.users_id AND c.banned = 'No'
JOIN Users d ON t.driver_id = d.users_id AND d.banned = 'No'
WHERE t.request_at BETWEEN '2013-10-01' AND '2013-10-03'
GROUP BY t.request_at
ORDER BY t.request_at;

-- ============================================================================
-- PROBLEM 569: Median Employee Salary
-- ============================================================================
-- Table: Employee (id, company, salary)
-- Task: Find the median salary for each company.

-- Oracle SQL:
WITH ranked AS (
    SELECT id, company, salary,
           ROW_NUMBER() OVER (PARTITION BY company ORDER BY salary, id) AS rn,
           COUNT(*) OVER (PARTITION BY company) AS cnt
    FROM Employee
)
SELECT id, company, salary
FROM ranked
WHERE rn BETWEEN cnt / 2 AND cnt / 2 + 1
ORDER BY company, salary;

-- PostgreSQL:
SELECT DISTINCT ON (company) id, company, salary
FROM (
    SELECT id, company, salary,
           PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY salary)
               OVER (PARTITION BY company) AS median
    FROM Employee
) t
ORDER BY company, salary;

-- ============================================================================
-- PROBLEM 571: Find Median Given Frequency of Numbers
-- ============================================================================
-- Table: Numbers (Number, Frequency)
-- Task: Find median given each number's frequency.

-- Oracle SQL:
WITH cumulative AS (
    SELECT Number, Frequency,
           SUM(Frequency) OVER (ORDER BY Number) AS cum_sum,
           SUM(Frequency) OVER () AS total_sum
    FROM Numbers
)
SELECT AVG(Number) AS median
FROM cumulative
WHERE cum_sum - Frequency <= total_sum / 2
  AND cum_sum >= total_sum / 2;

-- ============================================================================
-- PROBLEM 579: Find Cumulative Salary of an Employee
-- ============================================================================
-- (Covered in Medium but repeated for Hard categorization)
-- Task: Cumulative salary for each employee, excluding the most recent month.

-- Oracle SQL:
WITH ranked_months AS (
    SELECT id, month, salary,
           ROW_NUMBER() OVER (PARTITION BY id ORDER BY month DESC) AS rn
    FROM Employee
)
SELECT id, month,
       SUM(salary) OVER (PARTITION BY id ORDER BY month ROWS 2 PRECEDING) AS salary
FROM ranked_months
WHERE rn > 1
ORDER BY id, month DESC;

-- ============================================================================
-- PROBLEM 601: Human Traffic of Stadium
-- ============================================================================
-- Table: Stadium (id, visit_date, people)
-- Task: Find rows where three or more consecutive rows have 100+ people.

-- Oracle SQL:
WITH qualifying AS (
    SELECT id, visit_date, people
    FROM Stadium
    WHERE people >= 100
)
SELECT DISTINCT q.id, q.visit_date, q.people
FROM qualifying q
JOIN qualifying q2 ON q.id BETWEEN q2.id AND q2.id + 2
JOIN qualifying q3 ON q2.id = q3.id
GROUP BY q.id, q.visit_date, q.people
HAVING COUNT(CASE WHEN q2.id = q.id OR q2.id = q.id - 1 OR q2.id = q.id - 2 THEN 1 END) >= 3
ORDER BY q.visit_date;

-- Using LEAD/LAG:
SELECT id, TO_CHAR(visit_date, 'YYYY-MM-DD') AS visit_date, people
FROM (
    SELECT id, visit_date, people,
           LAG(people, 1) OVER (ORDER BY id) AS prev1,
           LAG(people, 2) OVER (ORDER BY id) AS prev2,
           LEAD(people, 1) OVER (ORDER BY id) AS next1,
           LEAD(people, 2) OVER (ORDER BY id) AS next2
    FROM Stadium
)
WHERE (people >= 100 AND prev1 >= 100 AND prev2 >= 100)
   OR (people >= 100 AND next1 >= 100 AND next2 >= 100)
   OR (people >= 100 AND prev1 >= 100 AND next1 >= 100)
ORDER BY visit_date;

-- ============================================================================
-- PROBLEM 615: Average Salary: Departments VS Company
-- ============================================================================
-- Tables: Salary, Employee, Department
-- Task: Compare each department's average salary to company average by month.

-- Oracle SQL:
WITH monthly_stats AS (
    SELECT DATE_TRUNC('month', s.pay_date) AS pay_month,
           e.department_id,
           AVG(s.amount) AS dept_avg,
           AVG(AVG(s.amount)) OVER (PARTITION BY DATE_TRUNC('month', s.pay_date)) AS company_avg
    FROM Salary s
    JOIN Employee e ON s.employee_id = e.employee_id
    GROUP BY DATE_TRUNC('month', s.pay_date), e.department_id
)
SELECT TO_CHAR(pay_month, 'YYYY-MM') AS pay_month,
       department_id,
       CASE
           WHEN dept_avg > company_avg THEN 'higher'
           WHEN dept_avg < company_avg THEN 'lower'
           ELSE 'same'
       END AS comparison
FROM monthly_stats
ORDER BY pay_month, department_id;

-- ============================================================================
-- PROBLEM 618: Students Report by Geography
-- ============================================================================
-- Table: Student (name, continent)
-- Task: Pivot student names by continent.

-- Oracle SQL:
SELECT 'America' AS continent,
       MAX(CASE WHEN continent = 'America' THEN name END) AS America,
       MAX(CASE WHEN continent = 'Asia' THEN name END) AS Asia,
       MAX(CASE WHEN continent = 'Europe' THEN name END) AS Europe
FROM (
    SELECT name, continent,
           ROW_NUMBER() OVER (PARTITION BY continent ORDER BY name) AS rn
    FROM Student
)
GROUP BY rn
ORDER BY rn;

-- ============================================================================
-- PROBLEM 1097: Game Play Analysis V
-- ============================================================================
-- Table: Activity (player_id, device_id, event_date, games_played)
-- Task: For each first login date, find the number of players who logged in
--       the next day and the fraction relative to total first logins on that day.

-- Oracle SQL:
WITH first_login AS (
    SELECT player_id, MIN(event_date) AS install_dt
    FROM Activity
    GROUP BY player_id
),
next_day AS (
    SELECT fl.install_dt,
           COUNT(DISTINCT fl.player_id) AS installs,
           COUNT(DISTINCT CASE WHEN a.event_date = fl.install_dt + 1 THEN fl.player_id END) AS day1_retention
    FROM first_login fl
    LEFT JOIN Activity a ON fl.player_id = a.player_id
    GROUP BY fl.install_dt
)
SELECT TO_CHAR(install_dt, 'YYYY-MM-DD') AS install_dt,
       installs,
       ROUND(day1_retention / installs, 2) AS Day1_retention
FROM next_day
ORDER BY install_dt;

-- PostgreSQL:
WITH first_login AS (
    SELECT player_id, MIN(event_date) AS install_dt
    FROM Activity
    GROUP BY player_id
),
next_day AS (
    SELECT fl.install_dt,
           COUNT(DISTINCT fl.player_id) AS installs,
           COUNT(DISTINCT CASE WHEN a.event_date = fl.install_dt + INTERVAL '1 day' THEN fl.player_id END) AS day1_retention
    FROM first_login fl
    LEFT JOIN Activity a ON fl.player_id = a.player_id
    GROUP BY fl.install_dt
)
SELECT install_dt::text, installs,
       ROUND(day1_retention::numeric / installs, 2) AS Day1_retention
FROM next_day
ORDER BY install_dt;

-- ============================================================================
-- PROBLEM 1126: Active Businesses
-- ============================================================================
-- Table: Events (business_id, event_type, occurences)
-- Task: Find businesses with event types above the average for that event type.

-- Oracle SQL:
WITH avg_occ AS (
    SELECT event_type, AVG(occurences) AS avg_occ
    FROM Events
    GROUP BY event_type
)
SELECT e.business_id
FROM Events e
JOIN avg_occ a ON e.event_type = a.event_type
WHERE e.occurences > a.avg_occ
GROUP BY e.business_id
HAVING COUNT(*) > 1;

-- ============================================================================
-- END OF HARD PROBLEMS
-- ============================================================================
