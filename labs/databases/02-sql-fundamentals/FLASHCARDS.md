# Flashcards: SQL Fundamentals

## Card 1
**Q**: What is the execution order of SQL clauses?
**A**: FROM → JOIN → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT

## Card 2
**Q**: How is NULL handled in WHERE conditions?
**A**: NULL comparisons always return false/unknown. Use IS NULL / IS NOT NULL.

## Card 3
**Q**: What does COALESCE(x, y, z) do?
**A**: Returns the first non-NULL argument.

## Card 4
**Q**: What's the difference between RANK and DENSE_RANK?
**A**: RANK skips numbers after ties; DENSE_RANK does not.

## Card 5
**Q**: When would you use HAVING vs WHERE?
**A**: WHERE filters rows before aggregation; HAVING filters groups after.

## Card 6
**Q**: What is a CTE?
**A**: Common Table Expression – a named temporary result set using WITH clause.

## Card 7
**Q**: How do you prevent SQL injection in JDBC?
**A**: Use PreparedStatement with parameters, never string concatenation.

## Card 8
**Q**: What's the difference between INNER and OUTER JOIN?
**A**: INNER returns only matching rows; OUTER retains non-matching rows from one/both tables.
