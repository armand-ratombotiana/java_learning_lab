# Reflection: Connection Pooling

## Key Takeaways
- Connection pooling is not optional for production applications – it's mandatory
- HikariCP is the gold standard: fastest, most reliable, best defaults
- Pool sizing is counterintuitive – bigger is not better
- Monitoring is essential; tune based on real metrics, not guesswork
- Connection leaks are the most common pool-related bug

## When to Use a Connection Pool
- Every application that connects to a relational database
- Even development environments benefit from pooling
- Batch jobs (with appropriately sized pools)

## When Pooling May Not Apply
- In-memory databases (H2 in-mem) – connection overhead is negligible
- Some serverless environments (AWS Lambda) use external pool proxies
- Connection pooling is handled by middleware/PAAS (e.g., PgBouncer)
