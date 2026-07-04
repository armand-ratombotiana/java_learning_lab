# Exercises: Relational Databases

## Exercise 1: Normalization
Given the following unnormalized table:
| order_id | customer_name | products |
|---|---|---|
| 1 | Alice | Widget(2), Gadget(1) |
| 2 | Bob | Doohickey(3) |

Normalize to 3NF. Write the SQL DDL.

## Exercise 2: JDBC Transaction
Write a JDBC method `transferFunds(Connection conn, long fromId, long toId, BigDecimal amount)` that safely transfers money between two accounts with proper transaction handling.

## Exercise 3: JPA Entity Mapping
Create JPA entities for a `Library` system with:
- **Book** (id, title, isbn, publishedYear)
- **Author** (id, name, birthYear)
- **Borrower** (id, name, email)
- **Loan** (id, book, borrower, loanDate, returnedDate)

Include proper relationships, cascade, and fetch strategies.

## Exercise 4: Constraint Design
Design a `CHECK` constraint that ensures:
- Order total must equal sum of (quantity × unit_price) for all items
- (Hint: This requires a trigger or application-level check – explain why)

## Exercise 5: ACID Analysis
For a banking application, identify which ACID property is violated in each scenario:
1. Transaction A reads balance = $100. Transaction B withdraws $80. A commits with old balance.
2. Server crashes after log write but before data page update. On restart, money is missing.
3. Two concurrent transfers cause a cycle that blocks both transactions.
4. UPDATE sets account_balance = -50, violating CHECK(account_balance >= 0).
