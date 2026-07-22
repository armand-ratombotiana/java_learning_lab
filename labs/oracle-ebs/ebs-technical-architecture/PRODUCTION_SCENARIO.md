# Production Scenarios: EBS Technical Architecture

## Scenario 1: OAF Page Throwing BC4J Exception After Patch
**Context**: A quarterly EBS patch was applied to the production environment.
**Problem**: A critical OAF page (AP Invoice Approval) started throwing: `BC4J Exception: JBO-27000 — Attribute <Amount> is not bound in the view object`. The AP team could not approve invoices, delaying payments.
**Root Cause**: The patch updated `xx_ap_invoices_VO.xml` view object definition, changing attribute bindings. The personalization layer had custom attribute bindings that were incompatible with the new view object. The `VO` xml file was overwritten by the patch.
**Solution**: 1) Identified the incompatible personalization: compared `MDS_METADATA` before and after patch. 2) Temporarily disabled the affected personalization via OAF Personalization page. 3) Re-created the personalization with correct attribute bindings matching the new VO. 4) Tested in staging before applying fix to production. 5) Implemented MDS metadata comparison tool for patch impact analysis.
**Lessons Learned**: Always test patches against personalized OAF pages. Version control OAF personalization metadata. Implement automated MDS comparison before and after patching. Maintain a personalization inventory with version dependencies.

## Scenario 2: Concurrent Program Waiting for 'Child' Lock
**Context**: A month-end concurrent program (GL Posting) was stuck in "Waiting for Child" status.
**Problem**: GL Posting did not complete after 12 hours. Other concurrent requests queued behind it. The period close was delayed.
**Root Cause**: The program was waiting for a child request that had already completed with errors. The parent program was checking the status of a child request that had been purged from `FND_CONCURRENT_REQUESTS`. The `REQUEST_ID` the parent was waiting for no longer existed.
**Solution**: 1) Identified the stuck request: `SELECT * FROM FND_CONCURRENT_REQUESTS WHERE PHASE_CODE = 'R' AND STATUS_CODE = 'W'`. 2) Re-ran the child request manually. 3) Updated the parent request status to terminate: `FND_CONCURRENT.SET_COMPLETED` with warning status. 4) Purged the orphaned request reference. 5) Implemented health check for running concurrent programs with alerting on stuck requests.
**Lessons Learned**: Monitor concurrent request wait chains. Implement timeouts for parent-child request dependencies. Alert on programs stuck in "Waiting for Child" longer than threshold. Maintain child request completion verification.

## Scenario 3: Workflow Engine Corrupted After Incomplete Upgrade
**Context**: An EBS workflow upgrade for AP invoice approval failed mid-deployment.
**Problem**: Workflow item types were partially updated. AP invoice approval workflows started failing with "WF_RULE_DATA_NOT_FOUND". New invoices were stuck in "WF_ERROR" status.
**Root Cause**: The `WF_LOCAL_TABLES` upgrade script failed due to insufficient tablespace. The activity attributes for APINV item type were not upgraded. Old workflow activities referenced invalid function names from the new version.
**Solution**: 1) Increased tablespace for WF_LOCAL_TABLES. 2) Re-ran the failed upgrade script manually. 3) Validated workflow item type: `SELECT * FROM WF_ITEM_TYPES WHERE NAME = 'APINV' and STATUS = 'CORRUPT'`. 4) Reloaded the workflow definition using `WF_DEFINITION.LOAD`. 5) Retried failed workflow items using `WF_ENGINE.HANDLE`.
**Lessons Learned**: Ensure adequate tablespace before workflow upgrades. Validate workflow item types after upgrade. Test workflow upgrade scripts in staging first. Maintain rollback procedures for workflow deployments.

## Scenario 4: OAF Personalization Exposing Hidden Fields
**Context**: A user discovered they could view sensitive supplier bank account information in the AP Invoice page.
**Problem**: The OAF page had a personalization that made the supplier bank account field visible, but the original page had it hidden. Any user with access to the personalization could unhide the field.
**Root Cause**: An OAF personalization was created to show the bank account field for debugging purposes and was never removed. The personalization was not restricted to specific users or roles. The field visibility was controlled by personalization, not by the underlying VO.
**Solution**: 1) Immediately removed the bank account field personalization. 2) Locked down OAF personalization access: only specific admin users can personalize. 3) Implemented field-level security in the view object, not in personalization. 4) Created an audit report of all OAF personalizations with sensitive fields. 5) Implemented quarterly personalization review process.
**Lessons Learned**: Never use personalization to control sensitive field visibility. Use view object attribute security instead. Restrict OAF personalization access to authorized users. Audit personalizations regularly for sensitive data exposure.
