# Production Scenarios: APEX Security

## Scenario 1: SQL Injection via Interactive Report URL Parameter
**Context**: A healthcare APEX application used Interactive Reports to display patient records.
**Problem**: A penetration test revealed that the `IR_FILTER` URL parameter could be manipulated to inject SQL. By appending `' OR 1=1 --`, an attacker could see all patients across all organizations.
**Root Cause**: The Interactive Report source used string concatenation instead of bind variables: `SELECT * FROM patients WHERE org_id = '` || :P_ORG_ID || `'`. The IR filter passed user input directly into the WHERE clause without sanitization.
**Solution**: 1) Rewrote all IR source queries to use bind variables: `SELECT * FROM patients WHERE org_id = :P_ORG_ID`. 2) Removed all string concatenation in dynamic SQL replaced by `DBMS_SQL` with bind variables. 3) Implemented `APEX_UTIL.PURGE_SESSION` for session hardening. 4) Enabled Session State Protection on all page items. 5) Conducted weekly automated SQL injection scans.
**Lessons Learned**: Never use string concatenation in APEX SQL sources. Always use bind variables — they are not just for performance but for security. Enable Session State Protection on every application. Train all developers on SQL injection prevention patterns.

## Scenario 2: Authorization Function Called 500 Times Per Page
**Context**: A multi-tenant APEX application with 50 different authorization roles.
**Problem**: Page load times increased from 1 second to 15 seconds after adding authorization schemes. Each page region had its own authorization check.
**Root Cause**: Each region's authorization scheme called `IS_USER_AUTHORIZED(p_role)` which querried `APEX_WORKSPACE_APEX_USERS` for every region. A page with 25 regions and 10 reports resulted in 35+ authorization calls per page load. The function was not cached using `RESULT_CACHE`.
**Solution**: 1) Created a single "Page Authorization" process that runs once and sets a global application item. 2) Changed region authorization to reference the pre-computed application item. 3) Implemented `APEX_AUTHORIZATION.IS_AUTHORIZED` with result caching. 4) Reduced authorization granularity by combining similar roles. 5) Added caching via `APEX_AUTHORIZATION.RESULT_CACHE` directive.
**Lessons Learned**: Authorization checks are expensive — cache results aggressively. Use application-level items for role storage instead of per-region calls. Review authorization scheme performance during load testing.

## Scenario 3: Authentication Failure Locks All Users Out
**Context**: An APEX application using LDAP authentication for 5000 corporate users.
**Problem**: On Monday morning, all users received "Authentication Failed" errors. No one could access the system.
**Root Cause**: The LDAP server certificate had expired over the weekend. APEX authentication scheme used SSL with strict certificate verification (`verify="required"`). The ORDS configuration had `security.verifySSL` enabled. When the certificate expired, all LDAP bind requests failed.
**Solution**: 1) Emergency workaround: temporarily disabled SSL verification in ORDS. 2) Renewed the LDAP server certificate. 3) Added a fallback authentication scheme using local APEX users for emergency access. 4) Configured certificate expiry monitoring with alerting. 5) Implemented a health check endpoint that tests LDAP authentication and alerts on failure.
**Lessons Learned**: Always have a fallback authentication method. Monitor certificate expiry dates. Implement automated health checks that test authentication end-to-end. Document emergency access procedures.

## Scenario 4: Session Hijacking via Exposed Session ID in URL
**Context**: A government APEX application used URL-based session state (`f?p=101:1:1234567890`).
**Problem**: An employee shared a screenshot on social media that included the URL with the session ID. Another person used that session ID to access the application and view classified data.
**Root Cause**: APEX was configured to use URL-based session state passing (`f?p=App:Page:Session`). The session ID was in the browser address bar. Session State Protection was not enabled. The application did not verify IP address or user-agent consistency.
**Solution**: 1) Changed application to use Cookie-based session state (set `f?p=101:1:::NO:::`). 2) Enabled Session State Protection on all items. 3) Implemented IP address and user-agent binding for sessions via `APEX_SESSION.ATTRIBUTE`. 4) Set short session timeouts (15 minutes idle, 4 hours absolute). 5) Enabled `APEX_PUBLIC_USER` restrictions.
**Lessons Learned**: Never use URL-based session state for sensitive applications. Always enable Session State Protection. Bind sessions to client attributes. Train users not to share screenshots containing URLs.
