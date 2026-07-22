# Production Scenarios: APEX Interactive Grid

## Scenario 1: Interactive Grid Save Fails with Session State Protection Error
**Context**: A logistics company used an IG for tracking shipment status updates in real-time.
**Problem**: Dispatchers could edit rows but received "Session state protection violation" when clicking Save. The error appeared randomly for 20% of users.
**Root Cause**: Session State Protection (SSP) was enabled at the application level. The IG columns touched by the edit process included checksum validation. When the checksum page item (`WWV_FLOW.G_AJAX_ID` checksum) was not generated during AJAX calls, the validation failed.
**Solution**: 1) Identified that the IG process was a "Multi Row Update" process type without proper checksum handling. 2) Changed the IG process to use "Interactive Grid - Update Row" with `APEX_IG` package instead of custom PL/SQL. 3) Added `APEX_PAGE.GET_URL` checksum for the affected page items. 4) Whitelisted specific IG columns for SSP bypass using `APEX_UTIL.SET_SESSION_STATE_PROTECTION`.
**Lessons Learned**: Understand SSP implications for IG AJAX updates. Use built-in IG processes instead of custom PL/SQL. Test IG saves with SSP enabled in QA before production deployment.

## Scenario 2: Interactive Grid Loads 100K Rows — Browser Freezes
**Context**: An insurance claims system used IG to display all claims for a given month.
**Problem**: When a claims adjuster selected "Last Month", the IG loaded 100,000+ rows. The browser tab became unresponsive for 30+ seconds.
**Root Cause**: IG was configured with "Rows per page = All" and pagination was disabled. The default IG query did not include filters. No WHERE clause limited the result set.
**Solution**: 1) Set "Maximum Rows per Page" to 50 in IG attributes. 2) Enabled pagination with "Show Pagination Bar". 3) Added a required date range filter with `P_DATE_FROM` and `P_DATE_TO` page items. 4) Implemented IG server-side pagination using `APEX_IG` with proper OFFSET/LIMIT. 5) Added an initial filter on the IG SQL query: `WHERE created_date >= SYSDATE - 7`.
**Lessons Learned**: Never allow "Rows per Page = All" in production IGs. Always enforce filters on large datasets. Use server-side pagination for grids with >10K rows. Set reasonable defaults for initial data load.

## Scenario 3: Master-Detail IG Orphaned Rows
**Context**: A project management app used master-detail IG for projects and tasks.
**Problem**: When a project was deleted from the master IG, the associated tasks in the detail IG remained orphaned. No foreign key cascade was applied.
**Root Cause**: The master IG delete process was implemented as a custom PL/SQL process that only deleted from the PROJECTS table. The detail IG's TASKS table had no ON DELETE CASCADE constraint. No validation checked for existing tasks before deletion.
**Solution**: 1) Modified the delete process to also delete associated tasks: `DELETE FROM tasks WHERE project_id = :PROJECT_ID`. 2) Added a foreign key constraint with `ON DELETE CASCADE` on TASKS.PROJECT_ID. 3) Implemented a confirmation dialog in the master IG before deletion, showing the count of affected child rows. 4) Added an undo capability using Flashback Query.
**Lessons Learned**: Always handle cascading deletes in master-detail relationships. Implement database-level foreign keys in addition to application-level logic. Show users the impact of delete operations before confirmation.

## Scenario 4: Interactive Grid Column Formatting Shows Wrong Data Type
**Context**: A financial IG displayed monetary amounts with 6 decimal places instead of 2.
**Problem**: Transaction amounts appeared as "150.000000" instead of "$150.00". CFO complained about unprofessional formatting.
**Root Cause**: The IG column was defined as NUMBER with "Format Mask" set to the Oracle default precision. No currency formatting was applied. The underlying column in the database was `NUMBER(18,6)` and IG inherited the full precision.
**Solution**: 1) Set the IG column format mask to `$999,999,990.00`. 2) Updated the SQL query to `ROUND(amount, 2)` for display. 3) Created a number template format in Shared Components for reuse. 4) Added conditional formatting for negative values (red text).
**Lessons Learned**: Always define format masks for financial columns in IG. Use number/date templates in shared components for consistency. Test formatting with edge cases (negative, zero, large numbers).
