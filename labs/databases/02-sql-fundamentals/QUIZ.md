# Quiz: SQL Fundamentals

## Question 1
What is the order of execution for SQL clauses?

a) SELECT → FROM → WHERE → GROUP BY → ORDER BY
b) FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY
c) FROM → SELECT → WHERE → GROUP BY → ORDER BY
d) SELECT → FROM → GROUP BY → WHERE → ORDER BY

**Answer: b**

## Question 2
Which JOIN returns all rows from the left table and matching rows from the right?

a) INNER JOIN
b) RIGHT JOIN
c) LEFT JOIN
d) CROSS JOIN

**Answer: c**

## Question 3
What does `ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC)` return?

a) A sequential number per department, ordered by salary descending
b) The total number of rows in each department
c) A random number for each row
d) The average salary per department

**Answer: a**

## Question 4
What happens when you execute `SELECT * FROM employees WHERE department_id = NULL`?

a) Returns all employees with NULL department
b) Returns empty result
c) Returns all employees
d) Throws an error

**Answer: b (NULL comparisons always return false/unknown)**

## Question 5
What is the difference between UNION and UNION ALL?

a) UNION removes duplicates, UNION ALL keeps them
b) UNION ALL removes duplicates, UNION keeps them
c) UNION is faster than UNION ALL
d) No difference

**Answer: a**
