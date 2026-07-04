# Common Mistakes: Multi-Model & Polyglot

## Using Multiple DBs for Same Data Without Strategy
**Mistake**: Writing same data to MongoDB and PostgreSQL without clear purpose.
**Fix**: Define primary data store per entity type. Secondary stores are read-only replicas for specific queries.

## Ignoring Consistency Gaps
**Mistake**: Writing to PostgreSQL then immediately reading from MongoDB (eventual consistent) and getting stale data.
**Fix**: Use read-your-writes consistency or route reads to primary store until secondary is confirmed.

## Over-Engineering
**Mistake**: Using 5 database types for a simple CRUD app with 3 tables.
**Fix**: Start with one database (relational). Add specialized databases only when justified by performance or data model requirements.

## No Failover Strategy
**Mistake**: Redis cache being a hard dependency – app crashes when Redis is down.
**Fix**: Implement cache-aside pattern with fallback to primary database.

## Distributed Transaction Complexity
**Mistake**: Attempting 2PC across PostgreSQL, MongoDB, and Redis.
**Fix**: Use Saga pattern, eventual consistency, or avoid cross-database transactions.

## Not Monitoring Each Database
**Mistake**: Only monitoring the primary database.
**Fix**: Each database needs its own metric dashboards, alerting, and capacity planning.
