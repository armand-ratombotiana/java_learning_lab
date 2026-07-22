# Production Scenarios: APEX Performance

## Scenario 1: APEX DB CPU at 98% During Peak Hours
**Context**: A financial APEX application experienced severe slowdowns between 9 AM and 11 AM daily.
**Problem**: Database CPU was at 98%. APEX Monitor Activity showed 2000 page views/sec. Page load times increased from 2 seconds to 45 seconds. Users timed out.
**Root Cause**: An Interactive Report on the dashboard page was re-executing its SQL on every page refresh without caching. The query joined 8 tables and performed a full table scan on a 100M-row transaction table. The report had `Paginate = No` and returned all 100K rows.
**Solution**: 1) Identified the top SQL via `V$SQL` and `APEX_WORKSPACE_ACTIVITY_LOG`. 2) Added `MAX_ROWS = 1000` and pagination to the report. 3) Enabled region caching with 5-minute expiry. 4) Rewrote the query to use a materialized view with aggregates pre-computed. 5) Added index on the date column used in the WHERE clause. 6) Implemented bind variable usage to avoid hard parsing.
**Lessons Learned**: Always paginate large Interactive Reports. Use region caching aggressively. Monitor `V$SQL` for top SQL by CPU. Implement materialized views for dashboard queries. Use `APEX_ACTIVITY_LOG` to identify high-traffic pages.

## Scenario 2: Progressive APEX Slowness Throughout the Day
**Context**: An APEX order entry system worked fine in the morning but became progressively slower in the afternoon.
**Problem**: By 3 PM, page loads took 30+ seconds. Restarting ORDS temporarily fixed the issue, but it returned the next day.
**Root Cause**: ORDS connection pool was leaking connections. Each page view created a new database session but did not return it to the pool. `V$SESSION` showed 500+ inactive sessions from ORDS. The database `PROCESSES` parameter was hit, preventing new connections.
**Solution**: 1) Identified connection leak by monitoring `V$SESSION` over time, showing sessions increasing monotonically. 2) Patched ORDS to the latest version which fixed the connection leak. 3) Reduced `jdbc.MaxLimit` to 50 and implemented connection validation: `jdbc.validationQuery=SELECT 1 FROM DUAL`. 4) Added a scheduled job to kill idle ORDS sessions older than 30 minutes. 5) Implemented connection pool monitoring with alerts at 80% pool utilization.
**Lessons Learned**: Monitor connection pool usage trends. Implement connection validation queries. Keep ORDS updated for connection leak fixes. Set up proactive alerting on session counts.

## Scenario 3: Cached Region Returns Stale Data After Schema Change
**Context**: An inventory management APEX app displayed stock levels using a cached region with 1-hour expiry.
**Problem**: After a bulk inventory update, the report showed old stock levels for 55 minutes until cache expired. A customer order was confirmed based on stale data, causing overselling.
**Root Cause**: Region caching was configured with `Cache Timeout = 3600` seconds. No cache invalidation was triggered after data changes. The underlying table was updated directly via SQL*Loader without triggering cache purge.
**Solution**: 1) Implemented database trigger on the inventory table that calls `APEX_CACHE.PURGE_REGION_CACHE` via `UTL_HTTP` after INSERT/UPDATE/DELETE. 2) Reduced region cache timeout to 300 seconds as a safety net. 3) Added a "Refresh" button on the page that purges and reloads the region. 4) Implemented a post-update process that explicitly calls `APEX_CACHE.PURGE_REGION_CACHE`. 5) Added version number to the cached data — forcing re-cache when version changes.
**Lessons Learned**: Cache invalidation is as important as caching. Implement event-driven cache purging. Reduce cache TTL as a safety measure. Always provide a manual refresh option for cached data.

## Scenario 4: APEX Page Timing Side-Channel Attack
**Context**: A login page showed different response times for valid vs invalid usernames.
**Problem**: A penetration test discovered that the login page returned in 200ms for invalid usernames and 800ms for valid usernames (because it also loaded user preferences). This timing difference could be used to enumerate valid usernames.
**Root Cause**: The authentication process first checked if the username existed in the APEX users table, then attempted authentication. For valid usernames, it loaded user preferences before returning. This additional work increased response time by 600ms.
**Solution**: 1) Made all authentication attempts take the same time by adding artificial delay (`DBMS_LOCK.SLEEP(1)`) for invalid attempts to match valid ones. 2) Moved user preference loading to a lazy, post-authentication AJAX call. 3) Implemented constant-time comparison for credentials. 4) Added rate limiting to the login page (3 attempts per minute per IP). 5) Implemented CAPTCHA after failed attempts.
**Lessons Learned**: Never leak user existence through response timing. Make all authentication paths take the same time. Implement rate limiting and CAPTCHA on login pages. Conduct regular penetration testing on authentication flows.
