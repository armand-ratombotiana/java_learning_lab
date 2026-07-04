# Reflection: Query Optimization

## Key Takeaways
- Measure before optimizing – not all queries need optimization
- Indexes are the most impactful optimization tool
- The N+1 problem is the #1 ORM performance issue
- EXPLAIN ANALYZE is the most important diagnostic tool
- Statistics are the foundation of good query plans (keep them fresh)
- More indexes ≠ better performance; each index has write overhead

## When to Optimize
- Production queries exceeding performance budget
- Before deploying new features with database access
- After significant data growth
- When CPU or I/O on database is persistently high

## When Not to Optimize
- Development queries (premature optimization)
- Queries running once per hour on small tables
- Before measuring actual impact
- When the bottleneck is elsewhere (network, application logic)
