# Production Scenarios: Query Optimization II (Oracle Advanced)

## Scenario 1: Adaptive Plan Causing Performance Oscillation
**Context**: A hybrid OLTP/OLAP query running on Oracle 19c with adaptive query optimization enabled.
**Problem**: The query ran in 2 seconds for the first 100 executions, then suddenly took 30 seconds. It alternated between fast and slow, causing inconsistent user experience.
**Root Cause**: Oracle's adaptive plan feature re-optimized the query after statistics changed. The first execution used a hash join (fast). After a statistics gather, the adaptive optimizer chose a nested loop join with a larger-than-expected cardinality estimate. The plan changed mid-execution, using a different join method for different rows.
**Solution**: 1) Identified the adaptive plan in `V$SQL`: `IS_OBSERVED = 'Y'`, `ADAPTIVE_CURSOR = 'Y'`. 2) Disabled adaptive plans for this query using `DBMS_SQLTUNE.ALTER_SQL_PROFILE` with `ADAPTIVE_PLAN` disabled. 3) Fixed the plan with SPM: created a baseline for the fast plan. 4) Analyzed cardinality estimates and fixed the underlying statistics. 5) Set `_optimizer_adaptive_plans=FALSE` at session level for this specific workload.
**Lessons Learned**: Adaptive plans can cause unpredictable performance. Use SPM to stabilize critical query plans. Monitor `V$SQL` for adaptive cursor changes. Test adaptive optimization in staging before production. Lock statistics for stable tables.

## Scenario 2: SPM Baseline Not Evolving
**Context**: A new, better execution plan was found by the optimizer but SPM did not adopt it.
**Problem**: A query continued using a 3-table hash join plan (10 seconds) even though a new plan with index nested loops (2 seconds) was available. The new plan was verified as faster by SQL Tuning Advisor.
**Root Cause**: SPM baseline evolution requires `DBMS_SPM.EVOLVE_SQL_PLAN_BASELINE` to be run. The automatic evolution (`AUTO_EVOLVE`) was disabled. The new plan was accepted by the advisor but not verified by SPM.
**Solution**: 1) Evolved the baseline manually: `DBMS_SPM.EVOLVE_SQL_PLAN_BASELINE(sql_handle => 'handle', plan_name => 'plan_name', verify => 'YES', commit => 'YES')`. 2) Enabled automatic evolution: `DBMS_SPM.CONFIGURE('auto_evolve', 'ON')`. 3) Verified the new plan was accepted: `SELECT * FROM DBA_SQL_PLAN_BASELINES WHERE SQL_HANDLE = 'handle'`. 4) Set nightly job to run automatic SPM evolution. 5) Monitored SPM evolution log for failures.
**Lessons Learned**: Enable automatic SPM evolution for ongoing plan improvement. Manually evolve baselines after SQL Tuning Advisor recommendations. Monitor SPM baseline evolution results. Use `VERIFY = 'YES'` to test new plans before acceptance.

## Scenario 3: SQL Tuning Advisor Recommending Wrong Fix
**Context**: SQL Tuning Advisor was run for a slow nightly batch query.
**Problem**: The advisor recommended adding an index. The index was created in production. The next night, the batch query was 5x slower. The index actually made the query worse.
**Root Cause**: The advisor recommended an index based on the query's access path analysis, but it did not consider the INSERT overhead. The batch job also inserted 1M rows — the new index slowed down inserts by 3x. Overall batch time increased from 1 hour to 3 hours.
**Solution**: 1) Dropped the recommended index. 2) Re-analyzed the query without the index. 3) Used a different approach: created a materialized view with pre-computed aggregates. 4) Evaluated index impact on all operations (SELECT, INSERT, UPDATE, DELETE) before implementing. 5) Tested advisor recommendations in staging with full workload simulation.
**Lessons Learned**: Evaluate SQL Tuning Advisor recommendations holistically — consider all operations, not just the tuned query. Test in staging with representative workloads. Add index impact analysis to deployment checklist. Monitor query performance before and after index changes.

## Scenario 4: Result Cache Poisoning
**Context**: Oracle Result Cache was enabled for a query that returns product prices.
**Problem**: After a price update, some users still saw old prices for 30 minutes. The result cache was not invalidated when the underlying data changed.
**Root Cause**: The `RESULT_CACHE_MODE` was set to `FORCE`, caching all query results regardless of table dependency tracking. The query used a complex view that Oracle could not track dependencies for. The cached result did not expire because the view's base table changes did not propagate to the cache.
**Solution**: 1) Set `RESULT_CACHE_MODE` to `MANUAL` and added `/*+ RESULT_CACHE */` hint only for appropriate queries. 2) Flushed the result cache: `DBMS_RESULT_CACHE.FLUSH`. 3) For the product prices query, added `RESULT_CACHE(maxage 300)` to limit cache lifetime to 5 minutes. 4) Implemented explicit cache invalidation after price updates. 5) Monitored `V$RESULT_CACHE_STATISTICS` for cache hit ratio and invalidation rate.
**Lessons Learned**: Use result cache selectively with manual hints. Set reasonable TTL for cached results. Implement explicit cache invalidation for critical queries. Monitor result cache statistics. Prefer SPM for plan stability and result cache for data caching separately.
