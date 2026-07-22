# Production Scenarios: EBS Setup and Configuration

## Scenario 1: AutoConfig Fails After OS Patching
**Context**: A Linux security patch was applied to EBS application tier servers.
**Problem**: AutoConfig failed with `TCL script error` when reconfiguring services. OHS and Forms services could not start. EBS was down for 8 hours.
**Root Cause**: The OS patch updated the TCL interpreter from 8.5 to 8.6, which broke AutoConfig's TCL scripts. AutoConfig uses embedded TCL for configuration file generation. The `adopmnctl` wrapper script was incompatible with TCL 8.6.
**Solution**: 1) Restored the previous TCL version from backup. 2) Re-ran AutoConfig successfully. 3) Pinned TCL version 8.5 in the OS package manager. 4) Created AutoConfig compatibility test: a script that runs `adautocfg.sh` in dry-run mode after any OS patch. 5) Documented the TCL version requirement in change management procedures.
**Lessons Learned**: Test OS patches in lower environments before production. Maintain version pinning for critical dependencies like TCL. Always run AutoConfig dry-run before full execution. Create post-patch validation checklists.

## Scenario 2: Profile Option '%' Causing Performance Issues
**Context**: EBS application became slow, with login taking 30 seconds.
**Problem**: Every page request was taking 3-5 seconds longer than normal. Database showed high logical I/O from queries against `FND_PROFILE_OPTION_VALUES`.
**Root Cause**: A profile option was set at the user level with the value `%`. This wildcard profile triggered full table scans every time `FND_PROFILE.VALUE` was called. The profile option was used on every page (e.g., `FND_COLOR_SCHEME`).
**Solution**: 1) Identified the problematic profile: `SELECT * FROM FND_PROFILE_OPTION_VALUES WHERE PROFILE_OPTION_VALUE LIKE '%'`. 2) Changed the profile option value from `%` to a specific value. 3) Cleared the profile cache: `FND_PROFILE.PUT_PROFILE`. 4) Optimized `FND_PROFILE_OPTION_VALUES` with an index on `(PROFILE_OPTION_ID, LEVEL_ID, LEVEL_VALUE)`. 5) Implemented profile value validation to reject wildcards.
**Lessons Learned**: Never use wildcard values in profile options. Index profile option value tables. Monitor `V$SQL` for FND_PROFILE queries. Implement profile value constraints.

## Scenario 3: Flexfield Structure Corrupted After Data Migration
**Context**: A company merged two EBS instances after an acquisition and migrated flexfield structures.
**Problem**: Key Flexfield (KFF) segments showed incorrect values. Accounting entries posted with wrong segment combinations, causing financial reconciliation failures.
**Root Cause**: The flexfield structure migration used FNDLOAD but did not account for different `ID_FLEX_STRUCTURE_CODE` values between instances. The segment qualifiers were not migrated. The cross-validation rules were lost.
**Solution**: 1) Restored flexfield structures from the pre-migration backup. 2) Re-exported KFF from source using FNDLOAD with correct parameters: `FNDLOAD apps/apps O Y DOWNLOAD $FND_TOP/patch/115/import/afffload.lct XX_GL_KFF.ldt FND_ID_FLEX_STRUCTURE`. 3) Re-imported with segment qualifiers and cross-validation rules. 4) Verified segment combinations using `GL_BALANCING_SEGMENT` validation. 5) Implemented standardized FNDLOAD migration scripts with parameter validation.
**Lessons Learned**: Always use FNDLOAD for flexfield migrations with version control. Validate segment qualifiers and cross-validation rules after migration. Test financial postings in a sandbox before production cutover. Document KFF/DFF structure dependencies.

## Scenario 4: Segregation of Duties Violation
**Context**: An audit of EBS security found that 5 users had access to both AP Invoice Approval and AP Payment Processing.
**Problem**: These users could create invoices and approve payments to themselves. The company was at risk of fraud. SOX compliance was violated.
**Root Cause**: During user provisioning, responsibilities were copied from an existing user who had both AP responsibilities. Role-based access control was not implemented. No SOD (Segregation of Duties) review was conducted.
**Solution**: 1) Immediately revoked conflicting responsibilities from the 5 users. 2) Implemented Oracle EBS SOD Controls using `FND_SOD` rules. 3) Created access request workflows with manager approval. 4) Implemented quarterly SOD review process. 5) Automated SOD violation detection with alerts using `FND_SOD_VIOLATIONS` table.
**Lessons Learned**: Implement SOD controls in EBS from day one. Use role-based access control, not copy-user provisioning. Conduct regular SOD audits. Automate SOD violation detection and reporting.
