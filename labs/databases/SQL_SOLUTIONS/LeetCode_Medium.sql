-- ============================================================================
-- LeetCode_Medium.sql
-- LeetCode SQL Solutions — Medium Problems (Oracle SQL + PostgreSQL syntax)
-- ============================================================================

-- ============================================================================
-- PROBLEM 177: Nth Highest Salary
-- ============================================================================
-- Task: Write a function to find the Nth highest salary.

-- Oracle SQL (using DENSE_RANK):
SELECT NVL(salary, NULL) AS NthHighestSal
FROM (
    SELECT DISTINCT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS dr
    FROM Employee
)
WHERE dr = :N;

-- Oracle SQL (using OFFSET/FETCH):
SELECT NVL((
    SELECT DISTINCT salary FROM Employee
    ORDER BY salary DESC
    OFFSET :N - 1 ROWS FETCH NEXT 1 ROW ONLY
), NULL) AS NthHighestSal
FROM DUAL;

-- PostgreSQL:
CREATE OR REPLACE FUNCTION NthHighestSalary(N INT)
RETURNS INT AS $$
BEGIN
    RETURN (
        SELECT DISTINCT salary FROM Employee
        ORDER BY salary DESC
        LIMIT 1 OFFSET N - 1
    );
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- PROBLEM 178: Rank Scores
-- ============================================================================
-- Table: Scores (id, score)
-- Task: Rank scores with ties getting same rank, no gaps.

-- Oracle SQL / PostgreSQL:
SELECT score,
       DENSE_RANK() OVER (ORDER BY score DESC) AS rank
FROM Scores
ORDER BY score DESC;

-- ============================================================================
-- PROBLEM 180: Consecutive Numbers
-- ============================================================================
-- Table: Logs (id, num)
-- Task: Find all numbers that appear at least three times consecutively.

-- Oracle SQL:
SELECT DISTINCT l1.num AS ConsecutiveNums
FROM Logs l1
JOIN Logs l2 ON l1.id = l2.id - 1 AND l1.num = l2.num
JOIN Logs l3 ON l1.id = l3.id - 2 AND l1.num = l3.num;

-- Using LEAD:
SELECT DISTINCT num AS ConsecutiveNums
FROM (
    SELECT num,
           LEAD(num, 1) OVER (ORDER BY id) AS next1,
           LEAD(num, 2) OVER (ORDER BY id) AS next2
    FROM Logs
)
WHERE num = next1 AND num = next2;

-- PostgreSQL (same syntax — both work):
SELECT DISTINCT num AS ConsecutiveNums
FROM (
    SELECT num,
           LEAD(num, 1) OVER (ORDER BY id) AS next1,
           LEAD(num, 2) OVER (ORDER BY id) AS next2
    FROM Logs
) t
WHERE num = next1 AND num = next2;

-- ============================================================================
-- PROBLEM 184: Department Highest Salary
-- ============================================================================
-- Tables: Employee, Department
-- Task: Find employees with the highest salary in each department.

-- Oracle SQL:
SELECT d.name AS Department, e.name AS Employee, e.salary AS Salary
FROM Employee e
JOIN Department d ON e.departmentId = d.id
WHERE (e.departmentId, e.salary) IN (
    SELECT departmentId, MAX(salary)
    FROM Employee
    GROUP BY departmentId
);

-- Using DENSE_RANK:
SELECT Department, Employee, Salary
FROM (
    SELECT d.name AS Department, e.name AS Employee, e.salary AS Salary,
           DENSE_RANK() OVER (PARTITION BY e.departmentId ORDER BY e.salary DESC) AS dr
    FROM Employee e
    JOIN Department d ON e.departmentId = d.id
)
WHERE dr = 1;

-- PostgreSQL (same syntax — both work):
SELECT Department, Employee, Salary
FROM (
    SELECT d.name AS Department, e.name AS Employee, e.salary AS Salary,
           DENSE_RANK() OVER (PARTITION BY e.departmentId ORDER BY e.salary DESC) AS dr
    FROM Employee e
    JOIN Department d ON e.departmentId = d.id
) t
WHERE dr = 1;

-- ============================================================================
-- PROBLEM 550: Game Play Analysis IV
-- ============================================================================
-- Table: Activity (player_id, device_id, event_date, games_played)
-- Task: Find the fraction of players who logged in again the day after their first login.

-- Oracle SQL:
SELECT ROUND(COUNT(DISTINCT a2.player_id) / COUNT(DISTINCT a1.player_id), 2) AS fraction
FROM Activity a1
LEFT JOIN Activity a2
    ON a1.player_id = a2.player_id
    AND a2.event_date = a1.first_date + 1
CROSS JOIN (
    SELECT player_id, MIN(event_date) AS first_date
    FROM Activity
    GROUP BY player_id
) first_login
WHERE a1.player_id = first_login.player_id
  AND a1.event_date = first_login.first_date;

-- Better approach:
WITH first_login AS (
    SELECT player_id, MIN(event_date) AS first_date
    FROM Activity
    GROUP BY player_id
)
SELECT ROUND(
    COUNT(DISTINCT CASE WHEN a.event_date = fl.first_date + 1 THEN a.player_id END)
    / COUNT(DISTINCT fl.player_id)
, 2) AS fraction
FROM first_login fl
LEFT JOIN Activity a ON fl.player_id = a.player_id;

-- PostgreSQL:
WITH first_login AS (
    SELECT player_id, MIN(event_date) AS first_date
    FROM Activity
    GROUP BY player_id
)
SELECT ROUND(
    COUNT(DISTINCT CASE WHEN a.event_date = fl.first_date + INTERVAL '1 day' THEN a.player_id END)::numeric
    / COUNT(DISTINCT fl.player_id)
, 2) AS fraction
FROM first_login fl
LEFT JOIN Activity a ON fl.player_id = a.player_id;

-- ============================================================================
-- PROBLEM 570: Managers with at Least 5 Direct Reports
-- ============================================================================
-- Table: Employee (id, name, department, managerId)
-- Task: Find managers with at least 5 direct reports.

-- Oracle SQL / PostgreSQL:
SELECT e.name
FROM Employee e
JOIN Employee m ON e.id = m.managerId
GROUP BY e.id, e.name
HAVING COUNT(m.id) >= 5;

-- ============================================================================
-- PROBLEM 574: Winning Candidate
-- ============================================================================
-- Tables: Candidate, Vote
-- Task: Find the candidate who received the most votes.

-- Oracle SQL:
SELECT c.name
FROM Candidate c
JOIN (
    SELECT candidateId, COUNT(*) AS cnt
    FROM Vote
    GROUP BY candidateId
    ORDER BY cnt DESC
    FETCH FIRST 1 ROW ONLY
) v ON c.id = v.candidateId;

-- PostgreSQL:
SELECT c.name
FROM Candidate c
JOIN Vote v ON c.id = v.candidateId
GROUP BY c.id, c.name
ORDER BY COUNT(*) DESC
LIMIT 1;

-- ============================================================================
-- PROBLEM 578: Get Highest Answer Rate Question
-- ============================================================================
-- Table: SurveyLog (id, action, question_id, answer_id, q_num, timestamp)
-- Task: Find the question with the highest answer rate.

-- Oracle SQL:
SELECT question_id AS survey_log
FROM (
    SELECT question_id,
           ROUND(
               SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END) /
               NULLIF(SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END), 0)
           , 4) AS answer_rate
    FROM SurveyLog
    GROUP BY question_id
    ORDER BY answer_rate DESC
)
FETCH FIRST 1 ROW ONLY;

-- ============================================================================
-- PROBLEM 579: Find Cumulative Salary of an Employee
-- ============================================================================
-- Table: Employee (id, month, salary)
-- Task: Find cumulative salary for each employee for each month (excluding max).

-- Oracle SQL:
SELECT id, month,
       SUM(salary) OVER (PARTITION BY id ORDER BY month ROWS 2 PRECEDING) AS salary
FROM Employee
WHERE (id, month) NOT IN (
    SELECT id, MAX(month) FROM Employee GROUP BY id
)
ORDER BY id, month DESC;

-- ============================================================================
-- PROBLEM 585: Investments in 2016
-- ============================================================================
-- Table: Insurance (pid, tiv_2015, tiv_2016, lat, lon)
-- Task: Find total 2016 investment for policyholders whose 2015 is same as another
--       and whose location is unique.

-- Oracle SQL:
SELECT ROUND(SUM(tiv_2016), 2) AS tiv_2016
FROM Insurance
WHERE tiv_2015 IN (
    SELECT tiv_2015 FROM Insurance GROUP BY tiv_2015 HAVING COUNT(*) > 1
)
AND (lat, lon) IN (
    SELECT lat, lon FROM Insurance GROUP BY lat, lon HAVING COUNT(*) = 1
);

-- ============================================================================
-- PROBLEM 602: Friend Requests II: Who Has the Most Friends
-- ============================================================================
-- Table: RequestAccepted (requester_id, accepter_id, accept_date)
-- Task: Find the person with the most friends.

-- Oracle SQL:
WITH all_friends AS (
    SELECT requester_id AS user_id FROM RequestAccepted
    UNION ALL
    SELECT accepter_id AS user_id FROM RequestAccepted
)
SELECT user_id, COUNT(*) AS friends_count
FROM all_friends
GROUP BY user_id
ORDER BY friends_count DESC
FETCH FIRST 1 ROW ONLY;

-- PostgreSQL:
WITH all_friends AS (
    SELECT requester_id AS user_id FROM RequestAccepted
    UNION ALL
    SELECT accepter_id AS user_id FROM RequestAccepted
)
SELECT user_id, COUNT(*) AS friends_count
FROM all_friends
GROUP BY user_id
ORDER BY friends_count DESC
LIMIT 1;

-- ============================================================================
-- PROBLEM 608: Tree Node
-- ============================================================================
-- Table: Tree (id, p_id)
-- Task: Categorize each node as Root, Inner, or Leaf.

-- Oracle SQL / PostgreSQL:
SELECT id,
       CASE
           WHEN p_id IS NULL THEN 'Root'
           WHEN id IN (SELECT p_id FROM Tree WHERE p_id IS NOT NULL) THEN 'Inner'
           ELSE 'Leaf'
       END AS type
FROM Tree
ORDER BY id;

-- ============================================================================
-- PROBLEM 612: Shortest Distance in a Plane
-- ============================================================================
-- Table: Point2D (x, y)
-- Task: Find the shortest distance between any two points.

-- Oracle SQL:
SELECT ROUND(MIN(SQRT(POWER(p1.x - p2.x, 2) + POWER(p1.y - p2.y, 2))), 2) AS shortest
FROM Point2D p1
JOIN Point2D p2 ON (p1.x, p1.y) != (p2.x, p2.y);

-- ============================================================================
-- PROBLEM 613: Shortest Distance in a Line
-- ============================================================================
-- Table: Point (x)
-- Task: Find the shortest distance between any two points.

-- Oracle SQL:
SELECT MIN(ABS(p1.x - p2.x)) AS shortest
FROM Point p1
JOIN Point p2 ON p1.x != p2.x;

-- Using LEAD:
SELECT MIN(diff) AS shortest
FROM (
    SELECT x - LAG(x) OVER (ORDER BY x) AS diff
    FROM Point
);

-- ============================================================================
-- PROBLEM 614: Second Degree Follower
-- ============================================================================
-- Table: Follow (followee, follower)
-- Task: Find second-degree followers (those who follow someone followed by others).

-- Oracle SQL:
SELECT f1.followee AS follower, COUNT(DISTINCT f1.follower) AS num
FROM Follow f1
WHERE f1.followee IN (
    SELECT DISTINCT follower FROM Follow
)
GROUP BY f1.followee
ORDER BY f1.followee;

-- ============================================================================
-- END OF MEDIUM PROBLEMS
-- ============================================================================
