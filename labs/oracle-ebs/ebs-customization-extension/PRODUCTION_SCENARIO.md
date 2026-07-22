# Production Scenarios: EBS Customization and Extension

## Scenario 1: Custom Workflow Fails After Upgrade
**Context**: A company upgraded EBS from R12.1.3 to R12.2.4. Custom workflow for capital expenditure approval stopped working.
**Problem**: Workflow item type "CAPEXREQ" failed with "WF_COMPLETE(activity, '#FUNCTION', result) — Function not found". New capital requests could not be approved, delaying $2M in projects.
**Root Cause**: The custom workflow referenced a PL/SQL function in `APPS.XX_CAPEX_WF_PKG` that was invalidated during the upgrade. The package body had been recompiled with `WRAP` and the signature changed. The upgrade also changed the workflow version, and the custom item type was not upgraded to match.
**Solution**: 1) Recompiled the invalid package: `ALTER PACKAGE XX_CAPEX_WF_PKG COMPILE BODY`. 2) Verified workflow function signatures matched. 3) Reloaded the custom workflow definition using `WF_DEFINITION.LOAD` with the updated item type. 4) Restarted errored workflows using `WF_ENGINE.HANDLE`. 5) Implemented pre-upgrade workflow compatibility checklist.
**Lessons Learned**: Test custom workflows in a cloned environment before upgrade. Maintain version control for workflow definitions. Document function signatures and dependencies. Create post-upgrade workflow validation suite.

## Scenario 2: Forms Personalization Causing Excessive DB Calls
**Context**: An AP Invoice forms personalization was added to validate supplier tax registration numbers.
**Problem**: After the personalization was deployed, the AP Invoice form took 5 seconds to open each record. The database showed 50,000 logical I/O per invoice query.
**Root Cause**: The Forms personalization executed a `WHEN-VALIDATE-RECORD` trigger that queried `AP_SUPPLIERS` for each invoice line. The query did not use bind variables and performed a full table scan. With 100 lines per invoice, it executed 100 queries per form load.
**Solution**: 1) Identified the personalization SQL in `V$SQL` — same query with different values caused hard parsing. 2) Rewrote the personalization to use bind variables: `SELECT tax_reg_num FROM ap_suppliers WHERE vendor_id = :vendor_id`. 3) Modified the trigger to fire on field exit, not record validation. 4) Added index on `AP_SUPPLIERS(VENDOR_ID, TAX_REG_NUM)`. 5) Cached supplier tax data in form-level variables.
**Lessons Learned**: Always use bind variables in Forms personalizations. Minimize DB calls per form navigation. Test personalization performance with large datasets. Use form-level caching for reference data.

## Scenario 3: FNDLOAD Export of Custom Components Lost
**Context**: A company was migrating custom components from DEV to PROD using FNDLOAD.
**Problem**: After migration, custom descriptive flexfields and value sets were missing in PROD. The FNDLOAD export file was empty for some components.
**Root Cause**: The FNDLOAD command used incorrect parameters: `FNDLOAD apps/apps 0 Y DOWNLOAD $FND_TOP/patch/115/import/affrmcus.lct XX_CUST.ldt FND_FORM_CUSTOM_RULES` — the file name (`XX_CUST.ldt`) was overwritten by a subsequent FNDLOAD run. Some components were exported with wrong `UPLOAD_MODE = 'REPLACE'` which skipped existing records.
**Solution**: 1) Re-exported all custom components with unique file names per component type. 2) Imported using `UPLOAD_MODE = 'MERGE'` to preserve existing records. 3) Validated FNDLOAD import using component count reports. 4) Created standardized FNDLOAD migration scripts with error handling. 5) Implemented version control for all FNDLOAD files.
**Lessons Learned**: Use unique file names for each FNDLOAD export. Use MERGE mode for imports. Validate import success with component counts. Version control all FNDLOAD files. Document exact FNDLOAD commands per component type.

## Scenario 4: Custom AME Rule Allowing Unauthorized Approvals
**Context**: An AME rule was created to allow a supervisor to approve purchase requisitions up to $10,000.
**Problem**: The supervisor could approve requisitions up to $1,000,000. An audit found a $500,000 requisition approved by the supervisor without proper authorization.
**Root Cause**: The AME rule had a condition that checked `REQUISITION_TOTAL <= 10000`, but the attribute was mapped to the wrong field (`REQ_HEADER_ID` instead of `REQ_TOTAL_AMOUNT`). The condition always evaluated to TRUE because `REQ_HEADER_ID` is always less than 10000. The rule was an "AND" condition with no upper limit.
**Solution**: 1) Immediately deactivated the incorrect AME rule. 2) Corrected the attribute mapping to `REQ_TOTAL_AMOUNT`. 3) Added an upper limit condition (`REQ_TOTAL_AMOUNT BETWEEN 0 AND 10000`). 4) Added a chain rule: if amount > $10K, require VP approval. 5) Implemented AME audit report that validates all rules monthly.
**Lessons Learned**: Validate AME attribute mappings carefully. Always use range conditions, not just upper bounds. Audit approval rules for correctness. Implement approval chain of escalating authority.
