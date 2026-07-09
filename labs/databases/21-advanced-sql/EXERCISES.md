# Exercises: Advanced SQL

## Exercise 1: Window Functions
Given table `sales(region, salesperson, amount, sale_date)`, write queries:
a) Top 3 sales by amount per region with row numbers
b) Running total per region ordered by sale_date
c) Salesperson rank within region, dense rank overall
d) Each salesperson's sale amount as percentage of region total

## Exercise 2: Recursive CTEs
Given table `employees(emp_id, emp_name, mgr_id)`:
a) Full org chart with level numbers
b) All employees reachable from emp_id=100 (including indirect reports)
c) Depth of each employee (number of levels from CEO)
d) All ancestors of employee 120

## Exercise 3: PIVOT
Given `sales(dept, quarter, amount)`:
a) Pivot quarters as columns showing total sales per department
b) Unpivot the result back to rows
c) Pivot with two aggregates (SUM and AVG)
d) Dynamic pivot using XML

## Exercise 4: MERGE
1. Write MERGE to upsert product inventory: if product exists, update quantity; otherwise insert
2. Add DELETE clause to remove products with zero quantity
3. Add error logging
4. Compare performance: MERGE vs separate INSERT and UPDATE

## Exercise 5: Hierarchical Queries
1. Oracle CONNECT BY for org chart with LEVEL and path
2. Same query with recursive CTE
3. Find all leaf employees (no direct reports)
4. Find employee depth and breadth of org structure

## Exercise 6: MODEL Clause
Given monthly sales data, use MODEL to calculate:
1. Running total over months
2. Year-over-year growth rate
3. Moving 3-month average
4. What-if analysis: increase sales by 10% for specific regions

## Exercise 7: PATTERN Matching
Given stock_prices(symbol, trade_date, price), find:
1. Three consecutive up days (price > previous)
2. V-shaped pattern: down, down, up, up
3. W-shaped pattern (two V patterns)
4. Head and shoulders pattern

## Exercise 8: Performance Tuning
1. Generate execution plan for a given query
2. Identify the most expensive operation
3. Add index to improve performance
4. Compare before and after with EXPLAIN PLAN

## Exercise 9: Partitioning
1. Design partition scheme for 10B row sales table
2. Demonstrate partition pruning with EXPLAIN PLAN
3. Implement partition exchange load
4. Maintain global indexes after partition drop

## Exercise 10: SPM
1. Create plan baseline for a query
2. Load alternative plan from STS
3. Evolve a new plan
4. Fix a suboptimal plan