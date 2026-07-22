# Production Scenarios: APEX Advanced Worksheets

## Scenario 1: APEX Mail Queue Not Sending
**Context**: An e-commerce APEX application sends order confirmation emails via APEX_MAIL.
**Problem**: Emails stopped sending for 6 hours. 12,000 emails were stuck in `APEX_MAIL_QUEUE` with status "READY". Customers did not receive order confirmations, causing support tickets.
**Root Cause**: The SMTP server was changed without updating the APEX mail configuration. The `APEX_INSTANCE_ADMIN` parameter `SMTP_HOST_ADDRESS` still pointed to the old SMTP server, which was decommissioned. APEX mail background job was failing silently.
**Solution**: 1) Updated `APEX_INSTANCE_ADMIN.SET_PARAMETER('SMTP_HOST_ADDRESS', 'new-smtp.company.com')`. 2) Checked `APEX_MAIL_LOG` for SMTP connection errors. 3) Purged stuck queue entries and resent using `APEX_MAIL.PUSH_QUEUE`. 4) Added monitoring for `APEX_MAIL_QUEUE` — alert when queue size > 100. 5) Implemented a mail test procedure scheduled every 5 minutes.
**Lessons Learned**: Monitor APEX mail queue size proactively. Test SMTP connectivity automatically. Document SMTP server configuration changes. Implement mail delivery alerts.

## Scenario 2: APEX JET Chart Dashboard Taking 45 Seconds
**Context**: An executive dashboard with 15 JET charts displaying financial KPIs.
**Problem**: Dashboard took 45 seconds to load. Executives abandoned the page. The underlying queries were running full table scans on 50M-row fact tables.
**Root Cause**: Each chart query independently aggregated the same fact table. None of the queries used summary tables or materialized views. JET chart data was not cached at the region level.
**Solution**: 1) Created materialized views for each KPI with refresh on commit. 2) Replaced individual chart SQL queries to read from materialized views. 3) Enabled APEX region caching with a 5-minute expiry for dashboard charts. 4) Rewrote queries to use parallel execution hints (`/*+ PARALLEL(4) */`). 5) Implemented asynchronous chart loading using dynamic actions that load charts one at a time.
**Lessons Learned**: Use materialized views for dashboard KPI queries. Enable region caching for read-only data. Load charts asynchronously to improve perceived performance. Aggregate at the database level, not the application level.

## Scenario 3: Collection Data Lost During Order Processing
**Context**: A shopping cart used APEX Collections to store in-progress orders.
**Problem**: Users adding items to cart over 30 minutes lost all items when clicking "Checkout". The collection was empty.
**Root Cause**: APEX Collections are session-scoped and stored in `APEX_COLLECTIONS`. The session timeout was configured to 15 minutes. Users who browsed longer than 15 minutes had their session expired. The collection was deleted when the session was purged.
**Solution**: 1) Increased APEX session timeout to 60 minutes. 2) Changed collection storage from session-level to database-level using a custom `CART_ITEMS` table with `SESSION_ID` foreign key. 3) Added a persistent cart table that survives session timeout. 4) Implemented cart persistence with a "Save for Later" feature using `APEX_COLLECTION.CREATE_OR_TRUNCATE_COLLECTION`. 5) Added a "Cart expired" warning at 10 minutes before session timeout.
**Lessons Learned**: Understand APEX collection session boundaries. Use persistent tables for important transient data. Implement session timeout warnings. Don't rely on collections for critical, long-lived data.

## Scenario 4: Cached Report Exposes PII to Wrong User
**Context**: An HR dashboard cached employee salary data at the region level.
**Problem**: After User A viewed the dashboard and logged out, User B logged in and saw User A's salary data. The cached region was not cleared between user sessions.
**Root Cause**: Region caching was configured with "Cache by User = No" (shared cache). The cache key was based only on the page and region ID, not on the authenticated user. When User B loaded the page, they received the cached output from User A's request.
**Solution**: 1) Changed region cache to "Cache by User = Yes" which appends APP_USER to cache key. 2) Purged all existing region caches using `APEX_CACHE.PURGE_REGION_CACHE`. 3) Added user-level checks in the cached SQL to verify authorization. 4) Implemented cache invalidation on logout. 5) Sensitive data (salaries) moved to an uncached region loaded via AJAX after user verification.
**Lessons Learned**: Never share caches between users for sensitive data. Always set "Cache by User = Yes" for personalized content. Implement cache invalidation on authentication changes. Use separate cached and uncached regions for mixed-sensitivity pages.
