# Production Scenarios: R2DBC (Oracle Focus)

## Scenario 1: R2DBC Connection Pool Exhausted
**Context**: A reactive Spring Boot application using R2DBC with Oracle under high load.
**Problem**: After 30 minutes of peak traffic, `ConnectionPool` threw `IllegalStateException: Unable to acquire connection from pool — pool is exhausted`. The application became unresponsive.
**Root Cause**: The R2DBC connection pool size was set to 10. A downstream service slowdown caused reactive queries to take 5 seconds instead of 50ms. Each HTTP request required 3 database queries, so the pool of 10 was quickly saturated with waiting requests.
**Solution**: 1) Increased `connectionPool.maxSize` to 50. 2) Added `connectionPool.maxIdleTime=PT30S` to close idle connections. 3) Implemented `timeout` on the database client: `.timeout(Duration.ofSeconds(2))`. 4) Added backpressure in the WebFlux controller using `Flux.merge` with `maxConcurrency` limit. 5) Monitored `r2dbc:pool:*` Micrometer metrics for pool utilization.
**Lessons Learned**: Size reactive connection pool for concurrent reactive streams, not total users. Set timeouts on all database operations. Monitor pool utilization metrics. Use backpressure to limit concurrent database calls.

## Scenario 2: Oracle R2DBC Driver Incompatibility
**Context**: An application upgraded Oracle database from 19c to 23c.
**Problem**: After the upgrade, R2DBC SELECT queries returned "Protocol violation — unexpected packet type". Application crashed on startup.
**Root Cause**: The Oracle R2DBC driver version (0.1.0) did not support Oracle 23c's new database protocol version. Oracle 23c introduced changes to the TTC (Two-Task Common) protocol that broke the experimental R2DBC driver.
**Solution**: 1) Rolled back Oracle 23c to 19c temporarily. 2) Found that Oracle R2DBC driver was not yet certified for 23c. 3) Used the traditional JDBC driver as fallback for the Oracle 23c deployment. 4) Implemented driver version check against database version on startup. 5) Monitored Oracle R2DBC driver release notes for 23c support.
**Lessons Learned**: R2DBC for Oracle is still evolving — verify driver-database version compatibility. Keep a JDBC fallback path for production. Test driver compatibility in staging before upgrading. Monitor Oracle R2DBC driver releases and supported versions.

## Scenario 3: Reactive Transaction Rollback Not Working
**Context**: A reactive service transferring money between accounts using R2DBC transactions.
**Problem**: When a transfer failed (insufficient funds), the debit was not rolled back. The money was deducted from account A but not added to account B.
**Root Cause**: The R2DBC transaction was not properly propagated across reactive operators. The `@Transactional` annotation does not work with reactive types in Spring — it requires `@Transactional` on `Mono`/`Flux` methods using `TransactionalOperator`. The debit and credit operations were using separate database connections.
**Solution**: 1) Used `TransactionalOperator.create(tm)` and wrapped both operations in `transactionalOperator.transactional(flux)`. 2) Ensured both debit and credit used the same `Connection` by scoping them within the same `usingWhen`. 3) Added reactive rollback-on-error with `.onErrorResume`. 4) Implemented Saga pattern for distributed compensation as an alternative. 5) Added integration tests that verify rollback behavior.
**Lessons Learned**: `@Transactional` doesn't work with reactive types — use `TransactionalOperator`. Always test rollback scenarios. Consider Saga pattern for multi-operation transactions. Ensure reactive operators share the same connection context.

## Scenario 4: Backpressure Cause Slow Consumer to Lose Results
**Context**: A reactive streaming endpoint returned a large dataset (1M rows) from Oracle via R2DBC.
**Problem**: The client (a batch processor) was slower than the database producer. Results were dropping — the client received only 500K out of 1M rows.
**Root Cause**: The R2DBC driver's backpressure mechanism canceled the `Subscription` when the downstream `Subscriber` did not call `request(n)` fast enough. The default `request` strategy for `Flux` is unbounded (`Long.MAX_VALUE`), but when combined with `delayElements` or slow processing, the driver's internal buffer overflowed and canceled.
**Solution**: 1) Implemented explicit backpressure: consumer calls `request(100)` per batch. 2) Changed from `Flux` to `Flux.buffer(100)` to batch results. 3) Used `R2dbcEntityTemplate.select(query).all()` with `limit(100)` for paginated streaming. 4) Added `Preconditions.checkState` to verify row counts match. 5) Used `Flux.usingWhen` for proper resource cleanup.
**Lessons Learned**: Understand R2DBC backpressure semantics. Use explicit request(n) for slow consumers. Batch results to match consumer speed. Verify row counts in integration tests. Use `limit()` for server-side pagination in streams.
