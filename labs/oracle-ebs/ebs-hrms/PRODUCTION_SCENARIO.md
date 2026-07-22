# Production Scenarios: EBS HRMS

## Scenario 1: Payroll Run Fails Mid-Processing
**Context**: A bi-weekly payroll run for 5000 employees failed during the night before payday.
**Problem**: The payroll run processed 3000 employees successfully but failed at employee 3001. Partial results were committed. 2000 employees had no pay calculated. Payday was in 8 hours.
**Root Cause**: The payroll formula for employee 3001 had a divide-by-zero error: `(Regular_Amount / 0)` due to a missing value in the employee's element entry. The formula execution stopped. The payroll run did not have error handling to skip and continue.
**Solution**: 1) Corrected the payroll element entry for employee 3001: updated `PAY_ELEMENT_ENTRIES_VALUES` with the missing value. 2) Re-ran payroll for the remaining employees using payroll rollback: `PAY_ROLLBACK.ROLLBACK_RUN`. 3) Processed the remaining 2000 employees in a quick-pay run. 4) Successfully generated payment files before payroll deadline. 5) Implemented payroll pre-validation reports to catch data issues before run.
**Lessons Learned**: Run payroll pre-validation to catch data errors. Implement error handling in payroll formulas. Maintain rollback procedures for partial payroll runs. Keep a payroll runbook with emergency contacts.

## Scenario 2: SSHR Page Loading Slowly
**Context**: Employees using Self-Service HR (SSHR) to view their organization hierarchy experienced slow page loads.
**Problem**: SSHR pages took 30-60 seconds to load. The organization tree was displayed as a flat list instead of a hierarchy. Performance was worst for large departments.
**Root Cause**: The SSHR query for organization hierarchy used `CONNECT BY PRIOR` without proper indexing on `PER_ORG_STRUCTURE_ELEMENTS`. The hierarchy was being traversed from the top level for every employee. Temporary tablespace was exhausted for large departments.
**Solution**: 1) Added index on `PER_ORG_STRUCTURE_ELEMENTS(ORG_STRUCTURE_VERSION_ID, PARENT_ORGANIZATION_ID)`. 2) Rewrote the hierarchy query to use recursive CTE instead of CONNECT BY for better performance. 3) Implemented hierarchy caching using `PER_ORG_HIERARCHY` materialized view. 4) Increased temporary tablespace for SSHR schema. 5) Implemented lazy loading: expand hierarchy only on user click.
**Lessons Learned**: Index organization structure tables. Use recursive CTEs for better hierarchy performance. Cache frequently-used hierarchy data. Implement progressive loading for large trees.

## Scenario 3: Employee Data Corrupted After Bulk Data Load
**Context**: A company merged with another and loaded 3000 employee records via HRMS API.
**Problem**: After the data load, 150 employees had duplicate records. Some employees had incorrect assignment information, showing wrong department and manager assignments.
**Root Cause**: The bulk load script used `PER_ALL_PEOPLE_F` direct inserts instead of the HRMS API (`HR_PERSON_CREATE`). The API enforces business rules (e.g., effective dates, duplicate checks). Direct inserts bypassed all validation. The `PER_ALL_ASSIGNMENTS_F` data was not synchronized with `PER_ALL_PEOPLE_F`.
**Solution**: 1) Identified and merged duplicate records using `PER_ALL_PEOPLE_F` matching on national identifier. 2) Used `HR_PERSON_MERGE` API to consolidate duplicates. 3) Corrected assignment records using `HR_ASSIGNMENT_API`. 4) Re-loaded using proper HRMS API with validation. 5) Implemented data load validation scripts: pre-load checks, post-load reconciliation.
**Lessons Learned**: Always use HRMS APIs for employee data loads, never direct table inserts. Implement pre-load data validation. Run post-load reconciliation reports. Test bulk loads in a sandbox first.

## Scenario 4: Salary Data Exposed via Unauthorized Responsibility
**Context**: A regular employee discovered they could view salary information for all employees in their department.
**Problem**: By navigating to "View Person" in SSHR, the employee could see salary and compensation details for coworkers. This was reported to HR as a privacy violation.
**Root Cause**: The SSHR responsibility had "View all person details" access at the security profile level. The security profile was set to "View All" instead of "View Self". The HRMS security was configured with `PER_SECURITY_PROFILES.VIEW_ALL_FLAG = 'Y'`.
**Solution**: 1) Immediately revoked the SSHR responsibility from all employees. 2) Created a new security profile with `VIEW_ALL_FLAG = 'N'` and `SELF_VIEW_FLAG = 'Y'`. 3) Restricted salary and compensation fields to HR Manager responsibility only. 4) Implemented data security rules to hide salary element entries from SSHR. 5) Conducted audit of all HRMS responsibilities and security profiles.
**Lessons Learned**: HRMS security must follow least-privilege principles. Audit security profiles regularly. Never grant "View All" to self-service roles. Implement field-level security for compensation data.
