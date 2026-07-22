# Production Scenarios: APEX Page Builder

## Scenario 1: Dynamic Action Fires Endlessly
**Context**: An e-commerce checkout page using APEX had a Dynamic Action that recalculated cart totals.
**Problem**: The Dynamic Action created an infinite AJAX loop, generating thousands of requests per minute. The ORDS connection pool exhausted and the application became unresponsive.
**Root Cause**: The Dynamic Action was set to fire "On Change" of a page item. The process updated that same item, triggering the DA again. No condition or guard was implemented to prevent re-entry.
**Solution**: 1) Added a static variable check in the DA condition using `apex.item(P8_TOTAL).setValue` only when value actually changed. 2) Changed DA event from "On Change" to a button click. 3) Implemented a debounce mechanism using JavaScript `setTimeout`. 4) Added `APEX_DEBUG.ERROR` logging to track DA execution count.
**Lessons Learned**: Always add re-entry guards to DAs that modify their triggering items. Use "Button Click" instead of "On Change" for critical calculations. Test DAs with concurrent users to identify infinite loops.

## Scenario 2: Branching Logic Sending Users to Wrong Page
**Context**: A loan application portal with multi-page wizard relied on APEX branches for navigation.
**Problem**: After completing step 3 of 5, users were randomly redirected to the beginning of the workflow, losing all entered data.
**Root Cause**: Multiple branches on the same page had overlapping conditions. Two branches with "Always" processing point evaluated to true simultaneously. The last branch in processing order won, redirecting to page 1 instead of page 4.
**Solution**: 1) Reviewed all branch conditions and reordered them by specificity — most specific first. 2) Changed branch conditions from "Always" to specific PL/SQL function returning BOOLEAN. 3) Added a branch guard: `IF :P4_WORKFLOW_STEP = 'STEP3' THEN RETURN TRUE; END IF;`. 4) Implemented workflow state tracking in a session-level item validated on each page.
**Lessons Learned**: Never use "Always" branches in wizard workflows. Order branches from most to least specific. Implement workflow state machine to validate navigation.

## Scenario 3: Validations Bypassed Due to Incorrect Execution Point
**Context**: A banking application had validation on a form to ensure transaction amounts did not exceed account balance.
**Problem**: A user submitted a transaction that exceeded their balance. The validation did not fire and the transaction was processed incorrectly.
**Root Cause**: The validation was defined as "On Submit - After Computations and Validations" instead of "On Submit - Before Computation and Validation". The process that committed the transaction executed before the validation check.
**Solution**: 1) Moved the validation to execute "On Submit - Before Computation and Validation". 2) Added a secondary validation in the database trigger as a safety net. 3) Implemented `APEX_VALIDATION.ADD_ERROR` for custom validation messages. 4) Added regression tests to verify validation order.
**Lessons Learned**: Understand the APEX process execution order. Always validate before processing. Implement database-level constraints as a defense-in-depth measure.

## Scenario 4: Page Computation Causing Data Truncation
**Context**: A CRM system used APEX computations to format phone numbers before insert.
**Problem**: Phone numbers with international codes (+1-555-123-4567) were stored as "1234567", losing the country code and extension.
**Root Cause**: A computation executed "After Submit" using `SUBSTR(:P_PHONE, -7)` to strip formatting, but the regex pattern did not account for country codes. The computation also ran before the actual insert, modifying the page item permanently.
**Solution**: 1) Replaced string manipulation with proper `REGEXP_REPLACE` to extract digits while preserving all numbers. 2) Changed the computation to execute only on page display, not on submit. 3) Stored raw phone in a hidden item and formatted display separately. 4) Added validation step to verify phone number completeness.
**Lessons Learned**: Be careful with computations that modify data. Separate display formatting from data storage. Always test with international formats.
