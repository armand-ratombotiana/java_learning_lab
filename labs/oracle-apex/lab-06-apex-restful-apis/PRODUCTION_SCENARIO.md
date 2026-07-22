# Production Scenarios: APEX RESTful APIs

## Scenario 1: ORDS REST API Returns 503 Under Load
**Context**: A mobile banking app consumed 15 ORDS REST endpoints from APEX. During peak hours (9-11 AM), 30% of requests returned HTTP 503.
**Problem**: ORDS connection pool was exhausted. APEX_WS sessions were queuing. New connections timed out waiting for a database connection.
**Root Cause**: ORDS was configured with `jdbc.InitialLimit=5` and `jdbc.MaxLimit=25` in the connection pool. The mobile app was making 200+ concurrent requests, exceeding the pool size. ORDS default request timeout was 30 seconds.
**Solution**: 1) Increased pool size to `jdbc.InitialLimit=10`, `jdbc.MaxLimit=100` in `settings.xml`. 2) Implemented connection pool monitoring with `APEX_ORDS_CONNECTION_POOL` views. 3) Added rate limiting at the ORDS level using `security.requestValidationFunction`. 4) Implemented client-side retry with exponential backoff in the mobile app. 5) Added response caching for read-only endpoints using ORDS Cache (`cache.mode=CONNECTION`).
**Lessons Learned**: Always size ORDS connection pools based on expected concurrent users. Monitor pool exhaustion metrics. Implement rate limiting and client-side retry. Cache read-only responses aggressively.

## Scenario 2: REST API Endpoint Taking 30 Seconds
**Context**: An ORDS GET endpoint returned a list of orders with line items for a customer.
**Problem**: The endpoint took 30+ seconds for customers with >5000 orders. Mobile app timeouts at 10 seconds caused the UI to show errors.
**Root Cause**: The ORDS AutoREST handler generated N+1 queries: 1 query for orders + N queries for line items (one per order). No pagination was implemented. The endpoint returned all orders for the customer at once.
**Solution**: 1) Rewrote the ORDS handler with a single SQL query using JSON_ARRAYAGG: `SELECT JSON_OBJECT('orders' VALUE JSON_ARRAYAGG(...))`. 2) Implemented server-side pagination using `_page` and `_size` parameters. 3) Added `WHERE ROWNUM` limit in the SQL query. 4) Created a materialized view for the customer orders aggregate. 5) Added response compression via ORDS `compression.enabled=true`.
**Lessons Learned**: Avoid AutoREST for complex queries with related data. Always use server-side pagination. Use JSON generation functions (JSON_OBJECT, JSON_ARRAYAGG) for efficient JSON construction in SQL. Implement response compression.

## Scenario 3: ORDS Configuration Lost After Server Reboot
**Context**: A production ORDS instance was rebooted for OS patching.
**Problem**: After reboot, all REST endpoints returned 404. ORDS was running but the RESTful service definitions were missing.
**Root Cause**: ORDS RESTful service definitions were stored in the `ORDS` metadata schema. The ORDS deployment configuration pointed to the wrong database service after reboot. The `ords.war` file was redeployed from a backup that did not contain the latest REST endpoints.
**Solution**: 1) Re-exported RESTful service definitions from the development instance using `ORDS.DELETE_SERVICE` and `ORDS.DEPLOY`. 2) Restored the ORDS metadata from RMAN backup. 3) Automated ORDS deployment using `ords config map` and version-controlled configuration. 4) Implemented regular ORDS metadata exports to a `.zip` file. 5) Created a startup script that validates ORDS service availability after reboot.
**Lessons Learned**: Version control ORDS configuration files. Automate ORDS deployment with scripts. Implement post-reboot validation checks. Store ORDS metadata in a separate, backed-up tablespace.

## Scenario 4: OAuth2 Token Leaked in API Response Headers
**Context**: A REST API used OAuth2 client credentials flow for authentication between APEX and a third-party system.
**Problem**: An API response included the access token in the response body as a debugging field. A developer committed a log file containing the token to a public GitHub repository.
**Root Cause**: The ORDS handler included `APEX_JSON.WRITE('access_token', l_token)` for debugging. The `debug` parameter was left as a query parameter that triggered verbose logging. The token was also logged to `APEX_WORKSPACE_ACCESS_LOG` with full payload.
**Solution**: 1) Removed all token values from API responses immediately. 2) Rotated the compromised client credentials. 3) Implemented `APEX_JSON.WRITE` only when `:DEBUG = 'Y'` and restricted debug mode to internal IPs. 4) Added logging configuration to mask tokens: `APEX_WEB_SERVICE.LOG_RESPONSE_MASK`. 5) Implemented automated credential scanning in CI/CD pipeline.
**Lessons Learned**: Never include tokens in API responses. Mask sensitive data in all logs. Implement debug mode with IP whitelists. Use automated secret scanning in repositories.
