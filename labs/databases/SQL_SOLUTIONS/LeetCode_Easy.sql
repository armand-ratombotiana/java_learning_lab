-- ============================================================================
-- LeetCode_Easy.sql
-- LeetCode SQL Solutions — Easy Problems (Oracle SQL + PostgreSQL syntax)
-- Problems 175-650 range, organized by difficulty
-- Each problem includes both Oracle and PostgreSQL syntax where different
-- ============================================================================

-- ============================================================================
-- PROBLEM 175: Combine Two Tables
-- ============================================================================
-- Table: Person (personId, firstName, lastName)
-- Table: Address (addressId, personId, city, state)
-- Task: Write a solution to report the first name, last name, city, and state of each person in the Person table.
--       If the address of a person is not in the Address table, report null instead.

-- Oracle SQL:
SELECT p.firstName, p.lastName, a.city, a.state
FROM Person p
LEFT JOIN Address a ON p.personId = a.personId;

-- PostgreSQL (same syntax):
SELECT p.firstName, p.lastName, a.city, a.state
FROM Person p
LEFT JOIN Address a ON p.personId = a.personId;

-- ============================================================================
-- PROBLEM 176: Second Highest Salary
-- ============================================================================
-- Table: Employee (id, salary)
-- Task: Find the second highest distinct salary. If there is no second highest, return null.

-- Oracle SQL (12c+):
SELECT NVL((
    SELECT DISTINCT salary FROM Employee
    ORDER BY salary DESC
    OFFSET 1 ROW FETCH NEXT 1 ROW ONLY
), NULL) AS SecondHighestSalary
FROM DUAL;

-- Oracle SQL (pre-12c):
SELECT NVL(MIN(salary), NULL) AS SecondHighestSalary
FROM (
    SELECT DISTINCT salary
    FROM Employee
    WHERE salary < (SELECT MAX(salary) FROM Employee)
);

-- PostgreSQL:
SELECT (
    SELECT DISTINCT salary FROM Employee
    ORDER BY salary DESC
    LIMIT 1 OFFSET 1
) AS SecondHighestSalary;

-- ============================================================================
-- PROBLEM 181: Employees Earning More Than Their Managers
-- ============================================================================
-- Table: Employee (id, name, salary, managerId)
-- Task: Find employees who earn more than their managers.

-- Oracle SQL:
SELECT e.name AS Employee
FROM Employee e
JOIN Employee m ON e.managerId = m.id
WHERE e.salary > m.salary;

-- PostgreSQL (same syntax):
SELECT e.name AS Employee
FROM Employee e
JOIN Employee m ON e.managerId = m.id
WHERE e.salary > m.salary;

-- ============================================================================
-- PROBLEM 182: Duplicate Emails
-- ============================================================================
-- Table: Person (id, email)
-- Task: Find all duplicate emails.

-- Oracle SQL:
SELECT email AS Email
FROM Person
GROUP BY email
HAVING COUNT(*) > 1;

-- PostgreSQL (same syntax):
SELECT email AS Email
FROM Person
GROUP BY email
HAVING COUNT(*) > 1;

-- ============================================================================
-- PROBLEM 183: Customers Who Never Order
-- ============================================================================
-- Table: Customers (id, name), Orders (id, customerId)
-- Task: Find all customers who never order anything.

-- Oracle SQL:
SELECT c.name AS Customers
FROM Customers c
LEFT JOIN Orders o ON c.id = o.customerId
WHERE o.id IS NULL;

-- PostgreSQL (same syntax):
SELECT c.name AS Customers
FROM Customers c
LEFT JOIN Orders o ON c.id = o.customerId
WHERE o.id IS NULL;

-- ============================================================================
-- PROBLEM 196: Delete Duplicate Emails
-- ============================================================================
-- Table: Person (id, email)
-- Task: Delete duplicate emails, keeping only the one with the smallest id.

-- Oracle SQL:
DELETE FROM Person
WHERE id NOT IN (
    SELECT MIN(id) FROM Person GROUP BY email
);

-- PostgreSQL:
DELETE FROM Person p1
USING Person p2
WHERE p1.email = p2.email
  AND p1.id > p2.id;

-- ============================================================================
-- PROBLEM 197: Rising Temperature
-- ============================================================================
-- Table: Weather (id, recordDate, temperature)
-- Task: Find all dates' temperatures higher than the previous date.

-- Oracle SQL:
SELECT w1.id
FROM Weather w1
JOIN Weather w2 ON w1.recordDate = w2.recordDate + 1
WHERE w1.temperature > w2.temperature;

-- PostgreSQL:
SELECT w1.id
FROM Weather w1
JOIN Weather w2 ON w1.recordDate = w2.recordDate + INTERVAL '1 day'
WHERE w1.temperature > w2.temperature;

-- ============================================================================
-- PROBLEM 511: Game Play Analysis I
-- ============================================================================
-- Table: Activity (player_id, device_id, event_date, games_played)
-- Task: Find the first login date for each player.

-- Oracle SQL:
SELECT player_id, MIN(event_date) AS first_login
FROM Activity
GROUP BY player_id;

-- PostgreSQL (same syntax):
SELECT player_id, MIN(event_date) AS first_login
FROM Activity
GROUP BY player_id;

-- ============================================================================
-- PROBLEM 512: Game Play Analysis II
-- ============================================================================
-- Task: For each player, find the device they first logged into.

-- Oracle SQL:
SELECT player_id, device_id
FROM (
    SELECT player_id, device_id,
           ROW_NUMBER() OVER (PARTITION BY player_id ORDER BY event_date) AS rn
    FROM Activity
)
WHERE rn = 1;

-- PostgreSQL (same syntax):
SELECT DISTINCT ON (player_id) player_id, device_id
FROM Activity
ORDER BY player_id, event_date;

-- ============================================================================
-- PROBLEM 577: Employee Bonus
-- ============================================================================
-- Table: Employee (empId, name, supervisor, salary)
-- Table: Bonus (empId, bonus)
-- Task: Find each employee's bonus less than 1000 or no bonus.

-- Oracle SQL:
SELECT e.name, b.bonus
FROM Employee e
LEFT JOIN Bonus b ON e.empId = b.empId
WHERE NVL(b.bonus, 0) < 1000;

-- PostgreSQL:
SELECT e.name, b.bonus
FROM Employee e
LEFT JOIN Bonus b ON e.empId = b.empId
WHERE COALESCE(b.bonus, 0) < 1000;

-- ============================================================================
-- PROBLEM 584: Find Customer Referee
-- ============================================================================
-- Table: Customer (id, name, referee_id)
-- Task: Find customers whose referee is not 2 or null.

-- Oracle SQL:
SELECT name
FROM Customer
WHERE NVL(referee_id, 0) != 2;

-- PostgreSQL:
SELECT name
FROM Customer
WHERE COALESCE(referee_id, 0) != 2;

-- Alternative (both):
SELECT name FROM Customer WHERE referee_id != 2 OR referee_id IS NULL;

-- ============================================================================
-- PROBLEM 586: Customer Placing the Largest Number of Orders
-- ============================================================================
-- Table: Orders (order_number, customer_number)
-- Task: Find the customer who has placed the largest number of orders.

-- Oracle SQL:
SELECT customer_number
FROM Orders
GROUP BY customer_number
ORDER BY COUNT(*) DESC
FETCH FIRST 1 ROW ONLY;

-- PostgreSQL:
SELECT customer_number
FROM Orders
GROUP BY customer_number
ORDER BY COUNT(*) DESC
LIMIT 1;

-- ============================================================================
-- PROBLEM 595: Big Countries
-- ============================================================================
-- Table: World (name, continent, area, population, gdp)
-- Task: Find countries that are big by area (>= 3M km2) or population (>= 25M).

-- Oracle SQL / PostgreSQL:
SELECT name, population, area
FROM World
WHERE area >= 3000000 OR population >= 25000000;

-- ============================================================================
-- PROBLEM 596: Classes More Than 5 Students
-- ============================================================================
-- Table: Courses (student, class)
-- Task: Find classes with at least 5 students.

-- Oracle SQL:
SELECT class
FROM Courses
GROUP BY class
HAVING COUNT(*) >= 5;

-- PostgreSQL (same syntax):
SELECT class
FROM Courses
GROUP BY class
HAVING COUNT(*) >= 5;

-- ============================================================================
-- PROBLEM 597: Friend Requests I: Overall Acceptance Rate
-- ============================================================================
-- Table: FriendRequest (sender_id, send_to_id, request_date)
-- Table: RequestAccepted (requester_id, accepter_id, accept_date)
-- Task: Find the overall acceptance rate of friend requests.

-- Oracle SQL:
SELECT NVL(ROUND(
    (SELECT COUNT(*) FROM RequestAccepted) /
    NULLIF((SELECT COUNT(*) FROM FriendRequest), 0)
, 2), 0) AS accept_rate
FROM DUAL;

-- PostgreSQL:
SELECT COALESCE(ROUND(
    (SELECT COUNT(*)::numeric FROM RequestAccepted) /
    NULLIF((SELECT COUNT(*) FROM FriendRequest), 0)
, 2), 0) AS accept_rate;

-- ============================================================================
-- PROBLEM 603: Consecutive Available Seats
-- ============================================================================
-- Table: Cinema (seat_id, free)
-- Task: Find all consecutive available seats.

-- Oracle SQL:
SELECT DISTINCT c1.seat_id
FROM Cinema c1
JOIN Cinema c2 ON ABS(c1.seat_id - c2.seat_id) = 1
WHERE c1.free = 1 AND c2.free = 1
ORDER BY c1.seat_id;

-- PostgreSQL (same syntax):
SELECT DISTINCT c1.seat_id
FROM Cinema c1
JOIN Cinema c2 ON ABS(c1.seat_id - c2.seat_id) = 1
WHERE c1.free = 1 AND c2.free = 1
ORDER BY c1.seat_id;

-- ============================================================================
-- PROBLEM 607: Sales Person
-- ============================================================================
-- Tables: SalesPerson, Company, Orders
-- Task: Find sales people with no orders to RED company.

-- Oracle SQL:
SELECT s.name
FROM SalesPerson s
WHERE s.sales_id NOT IN (
    SELECT o.sales_id
    FROM Orders o
    JOIN Company c ON o.com_id = c.com_id
    WHERE c.name = 'RED'
);

-- PostgreSQL (same syntax):
SELECT s.name
FROM SalesPerson s
WHERE s.sales_id NOT IN (
    SELECT o.sales_id
    FROM Orders o
    JOIN Company c ON o.com_id = c.com_id
    WHERE c.name = 'RED'
);

-- ============================================================================
-- PROBLEM 610: Triangle Judgement
-- ============================================================================
-- Table: Triangle (x, y, z)
-- Task: Determine if three line segments can form a triangle.

-- Oracle SQL:
SELECT x, y, z,
       CASE WHEN x + y > z AND x + z > y AND y + z > x THEN 'Yes' ELSE 'No' END AS triangle
FROM Triangle;

-- PostgreSQL (same syntax):
SELECT x, y, z,
       CASE WHEN x + y > z AND x + z > y AND y + z > x THEN 'Yes' ELSE 'No' END AS triangle
FROM Triangle;

-- ============================================================================
-- PROBLEM 619: Biggest Single Number
-- ============================================================================
-- Table: MyNumbers (num)
-- Task: Find the biggest number that appears only once.

-- Oracle SQL:
SELECT NVL(MAX(num), NULL) AS num
FROM (
    SELECT num FROM MyNumbers
    GROUP BY num
    HAVING COUNT(*) = 1
);

-- PostgreSQL:
SELECT MAX(num) AS num
FROM (
    SELECT num FROM MyNumbers
    GROUP BY num
    HAVING COUNT(*) = 1
) t;

-- ============================================================================
-- PROBLEM 620: Not Boring Movies
-- ============================================================================
-- Table: Cinema (id, movie, description, rating)
-- Task: Find movies with odd ID and description != 'boring', ordered by rating.

-- Oracle SQL / PostgreSQL:
SELECT id, movie, description, rating
FROM Cinema
WHERE MOD(id, 2) = 1
  AND description != 'boring'
ORDER BY rating DESC;

-- ============================================================================
-- PROBLEM 627: Swap Salary
-- ============================================================================
-- Table: Salary (id, name, sex, salary)
-- Task: Swap all 'f' and 'm' values in the sex column.

-- Oracle SQL:
UPDATE Salary
SET sex = CASE WHEN sex = 'm' THEN 'f' ELSE 'm' END;

-- PostgreSQL (same syntax):
UPDATE Salary
SET sex = CASE WHEN sex = 'm' THEN 'f' ELSE 'm' END;

-- ============================================================================
-- PROBLEM 1050: Actors and Directors Who Cooperated At Least Three Times
-- ============================================================================
-- Table: ActorDirector (actor_id, director_id, timestamp)
-- Task: Find pairs of actors/directors who cooperated at least 3 times.

-- Oracle SQL / PostgreSQL:
SELECT actor_id, director_id
FROM ActorDirector
GROUP BY actor_id, director_id
HAVING COUNT(*) >= 3;

-- ============================================================================
-- PROBLEM 1068: Product Sales Analysis I
-- ============================================================================
-- Tables: Sales, Product
-- Task: Show product name, year, and price for each sale.

-- Oracle SQL / PostgreSQL:
SELECT p.product_name, s.year, s.price
FROM Sales s
JOIN Product p ON s.product_id = p.product_id;

-- ============================================================================
-- END OF EASY PROBLEMS
-- ============================================================================
