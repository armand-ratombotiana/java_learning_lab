# Production Scenarios: PL/SQL Advanced (Oracle Focus)

## Scenario 1: Pipelined Table Function Memory Exhaustion
**Context**: An ETL process used a pipelined table function to transform 10M rows of data.
**Problem**: The function consumed 8GB of PGA memory and caused ORA-04030 (out of process memory). The ETL job failed.
**Root Cause**: The pipelined function was implemented with `PIPE ROW` inside a loop that collected all rows in an array before piping: `COLLECT rows INTO array; FOR i IN 1..array.COUNT LOOP PIPE ROW(array(i)); END LOOP;`. This negated the benefit of pipelining — all data was still held in memory.
**Solution**: 1) Rewrote to use `PIPE ROW` immediately after processing each row: `LOOP FETCH cursor INTO rec; EXIT WHEN cursor%NOTFOUND; PIPE ROW(process(rec)); END LOOP;`. 2) Removed the intermediate collection array. 3) Used `LIMIT` clause in BULK COLLECT to fetch in batches of 1000. 4) Monitored PGA usage: `SELECT * FROM V$PGAST`. 5) Reduced memory from 8GB to 200MB.
**Lessons Learned**: Pipelined functions must pipe rows immediately, not buffer them. Use BULK COLLECT with LIMIT for batch processing. Monitor PGA memory for PL/SQL functions. Test with production data volumes.

## Scenario 2: Result Cache Returning Stale Data
**Context**: A frequently-called function used `RESULT_CACHE` to cache product pricing for performance.
**Problem**: After a price update, the function continued returning old prices for 10 minutes. Customers saw incorrect prices.
**Root Cause**: The function was declared with `RESULT_CACHE` but without `RELIES_ON(products)`. The result cache did not track dependency on the PRODUCTS table. When prices were updated, Oracle did not invalidate the cached result.
**Solution**: 1) Added `RELIES_ON(products)` to the function declaration. 2) Manually flushed the result cache after price updates: `DBMS_RESULT_CACHE.INVALIDATE('APP', 'GET_PRICE')`. 3) Set a cache timeout: `RESULT_CACHE(maxage => 60)` for maximum 60-second cache. 4) Verified cache invalidation: `SELECT * FROM V$RESULT_CACHE_OBJECTS`. 5) Implemented cache monitoring: hit ratio and invalidation count.
**Lessons Learned**: Always use `RELIES_ON` with result cache to track dependencies. Set maxage timeout as a safety measure. Test cache invalidation with data updates. Monitor result cache statistics.

## Scenario 3: AQ Queue Growing Unbounded
**Context**: Oracle Advanced Queuing was used for order processing between two applications.
**Problem**: The queue grew to 2M messages. Processing latency increased from 1 second to 30 minutes. Disk space was filling up.
**Root Cause**: The dequeuer (consumer) was stopped for 4 hours due to deployment. The enqueuer (producer) continued enqueueing at 500 msg/sec. Messages accumulated. When the dequeuer restarted, it fell behind and could not catch up.
**Solution**: 1) Stopped the enqueuer temporarily. 2) Purged 1M old messages: `DBMS_AQ.PURGE_QUEUE(queue_name => 'ORDER_QUEUE', purge_condition => '...', purge_options => DBMS_AQ.PURGE_MSG_BY_MSGID)`. 3) Increased the dequeue batch size from 10 to 100. 4) Increased the number of dequeue subscribers from 1 to 4. 5) Implemented queue monitoring with alerting at 10K messages.
**Lessons Learned**: Monitor queue depth and set alerts. Implement subscriber scaling for peak loads. Plan for consumer downtime with queue capacity. Use retention policies for message cleanup.

## Scenario 4: VPD Policy Function Blocking All Queries
**Context**: A new Virtual Private Database policy was deployed for row-level security.
**Problem**: After deployment, all queries on the ORDERS table returned zero rows. Even the application administrator could not see any data.
**Root Cause**: The VPD policy function had a bug: `IF user_context.tenant_id IS NULL THEN RETURN '1=0'; END IF;`. The session context was not being set before the query. For sessions without the context, the policy returned `1=0`, blocking all rows.
**Solution**: 1) Fixed the policy function: `IF user_context.tenant_id IS NULL THEN RETURN '1=1'; END IF;` — return all rows when no context is set. 2) Added a debugging function to test the policy: `SELECT COUNT(*) FROM orders WHERE SYS_CONTEXT('USERENV', 'CURRENT_USER') = 'APP'`. 3) Verified the session context was being set at login. 4) Added error handling in the policy function. 5) Staged VPD deployment: first test with a single user, then roll out.
**Lessons Learned**: Test VPD policies in a sandbox with real user sessions. Handle NULL context gracefully (return all rows). Add logging to VPD policy functions for debugging. Stage security policy deployments incrementally. Always have a bypass mechanism for emergencies.
