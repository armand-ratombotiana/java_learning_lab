# Oracle Interview Guide — Oracle EBS Academy

## Interview Process for EBS Roles

### Rounds
- **Round 1 – Phone Screen (45 min)**: Resume deep-dive, EBS architecture overview, PL/SQL fundamentals. Expect questions on R12 vs 11i differences.
- **Round 2 – Technical (1 hr)**: Live coding in PL/SQL and Java. Design a concurrent program, write a complex report query, explain table structure (FND, INV, PO, GL).
- **Round 3 – Architecture & Design (1 hr)**: Multi-org architecture, MOAC, profile options, flexfields, AOL concepts. Whiteboard a custom OAF page integrated with workflows.
- **Round 4 – Managerial (45 min)**: Project leadership, upgrade planning, offshore coordination, Oracle methodology (AIM/OUM).
- **Timeline**: 2–4 weeks total.

### EBS-Specific Expectations
- Deep product knowledge: Oracle expects you to know internals — FND tables, concurrent manager internals, INV transaction types, GL accounting flows.
- Must speak confidently about R12.2 Online Patching (ADOP), Edition-Based Redefinition (EBR).
- Experience with Oracle's AIM (Application Implementation Methodology) is a strong plus.
- Oracle values product certifications (OCP, OCM) heavily.

## Top Technical Problems by Lab

### Lab 01: EBS Architecture and Fundamentals

#### Problem: Multi-Org Access Control (MOAC) Query
- **Difficulty/Frequency**: High / Very Frequent
- **Problem statement**: Write a query that retrieves all open purchase orders across multiple operating units for a given user, applying MOAC security profiles. Show how MOAC filters data transparently.
- **Interview walkthrough**: Start by explaining MOAC — users are assigned a MOAC profile (MO: Security Profile) that determines which Operating Units (OU) they can access. The profile is stored in `FND_PROFILE_OPTIONS_VALUES` or `FND_SESSION_INFO`. The key is that EBS uses `MO_GLOBAL` package to set `ORG_ID` context. Explain that `PO_HEADERS_ALL` contains all OUs data and `PO_HEADERS` is a synonym that respects MOAC. Show how to query `PO_HEADERS` directly (MOAC applies automatically) vs bypassing MOAC via `PO_HEADERS_ALL`.
- **SQL/PLSQL/Java solution**:
```sql
-- Query respecting MOAC (uses synonym)
SELECT pha.segment1 po_number,
       pha.po_header_id,
       pha.org_id,
       pha.authorization_status,
       pha.creation_date
  FROM po_headers pha
 WHERE pha.creation_date >= SYSDATE - 30
   AND pha.authorization_status = 'INCOMPLETE'
 ORDER BY pha.creation_date DESC;

-- Bypass MOAC — requires explicit ORG_ID filter
SELECT pha.segment1 po_number,
       pha.org_id,
       pha.authorization_status
  FROM po_headers_all pha
 WHERE pha.org_id IN (
           SELECT fspv.profile_option_value
             FROM fnd_profile_options_vl fpo,
                  fnd_profile_option_values fspv
            WHERE fpo.profile_option_name = 'MO_SECURITY_PROFILE_ID'
              AND fspv.profile_option_id = fpo.profile_option_id
              AND fspv.level_id = 10001  -- User level
              AND fspv.level_value = fnd_global.user_id
       )
   AND pha.creation_date >= SYSDATE - 30;
```
- **EBS-specific context**: `FND_PROFILE_OPTIONS_VALUES`, `MO_GLOBAL`, `PO_HEADERS_ALL` vs `PO_HEADERS` synonyms, `ORG_ID`, `MULTI_ORG` views.
- **What Oracle evaluates**: Understanding of multi-org architecture, security by org, synonyms vs base tables, performance implications.
- **Follow-ups**: How would you troubleshoot MOAC not working? How does MO_GLOBAL.INIT set the context? What happens in GL vs PO multi-org differences?

#### Problem: Concurrent Program Registration
- **Difficulty/Frequency**: Medium / Frequent
- **Problem statement**: Walk through the steps to register a new concurrent program in Oracle EBS that accepts three parameters (date range, inventory item, organization code) and writes output to a log file and an output file. Provide the table inserts and the execution logic.
- **Interview walkthrough**: Explain AOL (Application Object Library) registration — concurrent programs are registered in `FND_CONCURRENT_PROGRAMS`, `FND_EXECUTABLES`, and `FND_COMPILED_CODE_FILES`. Parameters are defined in `FND_DESCRIPTIVE_FLEXS` and `FND_CONCURRENT_PROGRAM_SERIAL`. The concurrent program can be SQL*Plus, PL/SQL stored procedure, Java, or host. Cover the steps: create executable, register program, define parameters, attach to request group, test via SRS.
- **SQL/PLSQL/Java solution**:
```sql
-- 1. Register executable (SQL*Plus script)
BEGIN
  fnd_conc_executable_apis.create_executable(
    p_executable_short_name => 'CE_GL_RECON',
    p_executable_name       => 'CE_GL Reconciliation Report',
    p_execution_method_code => 'S',
    p_execution_file_name   => 'ceglrec.rex',
    p_submit_flag           => 'N'
  );
END;
/

-- 2. Register concurrent program
BEGIN
  fnd_conc_program_apis.create_program(
    p_program_short_name      => 'CE_GL_RECON',
    p_program_name            => 'CE-GL Reconciliation',
    p_executable_name         => 'CE_GL_RECON',
    p_executable_short_name   => 'CE_GL_RECON',
    p_application_id          => 201,  -- SQLAP
    p_enabled_flag            => 'Y',
    p_output_file_type        => 'HTML',
    p_execution_method_code   => 'S'
  );
END;
/

-- 3. Define parameters in FND_DESCRIPTIVE_FLEXS
INSERT INTO fnd_descriptive_flexs
(application_id, descriptive_flex_context_code, ...)
VALUES (201, 'CE_GL_RECON_PARAMS', ...);
```
- **EBS-specific context**: `FND_CONCURRENT_PROGRAMS`, `FND_EXECUTABLES`, `FND_CONCURRENT_PROCESSES`, `FND_RUN_REQUESTS`, AOL registrations, `CONCSUB` for submission.
- **What Oracle evaluates**: Understanding of the full program lifecycle — registration, execution, output management, parameter passing via `FND_GLOBAL`.
- **Follow-ups**: How do you handle concurrent program incompatibility rules? How does the concurrent manager pick up the request? Explain running a concurrent program via `FND_REQUEST.SUBMIT_REQUEST`.

### Lab 02: System Administration and Security

#### Problem: User and Role Management — FND APIs
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: Write a PL/SQL procedure that creates a new EBS user, assigns them to a responsibility, and grants data security access to two operating units. Include proper error handling and audit logging.
- **Interview walkthrough**: Use `FND_USER_PKG` for user creation, `FND_RESPONSIBILITY` for responsibility assignment, and `FND_GRANTS` or `FND_ORGANIZATION_ACCESS` for OU access. Emphasize password encryption (EBS stores hashed passwords in `FND_USER.encrypted_foundation_password`).
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE create_ebs_user (
  p_user_name    IN VARCHAR2,
  p_password     IN VARCHAR2,
  p_description  IN VARCHAR2,
  p_resp_key     IN VARCHAR2,
  p_ou_ids       IN SYS.ODCINUMBERLIST
) AS
  l_user_id         NUMBER;
  l_resp_id         NUMBER;
  l_appl_id         NUMBER;
  l_enc_pwd         VARCHAR2(100);
BEGIN
  -- Create user
  l_user_id := fnd_user_pkg.createuser(
    x_user_name          => p_user_name,
    x_password           => p_password,
    x_employee_id        => NULL,
    x_description        => p_description,
    x_unencrypted_password => fnd_web_sec.encrypt(p_password)
  );

  -- Get responsibility info
  SELECT responsibility_id, application_id
    INTO l_resp_id, l_appl_id
    FROM fnd_responsibility_vl
   WHERE responsibility_key = p_resp_key;

  -- Assign responsibility
  fnd_user_resp_groups_api.insert_assignment(
    p_user_id     => l_user_id,
    p_resp_id     => l_resp_id,
    p_appl_id     => l_appl_id,
    p_start_date  => SYSDATE,
    p_end_date    => NULL
  );

  -- Grant OU access
  FOR i IN 1..p_ou_ids.COUNT LOOP
    INSERT INTO fnd_organization_access
    (user_id, organization_id, access_level)
    VALUES (l_user_id, p_ou_ids(i), 'FULL');
  END LOOP;

  -- Audit log
  INSERT INTO custom_audit_log
  (action, user_name, created_by, creation_date)
  VALUES ('CREATE_USER', p_user_name, fnd_global.user_id, SYSDATE);

  COMMIT;
EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
    fnd_file.put_line(fnd_file.log, 'Error: ' || SQLERRM);
    RAISE;
END create_ebs_user;
/
```
- **EBS-specific context**: `FND_USER`, `FND_USER_PKG`, `FND_RESPONSIBILITY_VL`, `FND_ORGANIZATION_ACCESS`, `FND_RESPONSIBILITY`, `FND_GRANTS`, `FND_PROFILE_OPTIONS_VALUES`.
- **What Oracle evaluates**: Security understanding, user lifecycle, responsibility assignment, encryption basics, error handling.
- **Follow-ups**: How does password encryption work in EBS R12? Explain function-based security vs data security. How do you audit login failures?

### Lab 03: Financials (GL, AP, AR)

#### Problem: GL Account Balancing — Trial Balance Report
- **Difficulty/Frequency**: High / Very Frequent
- **Problem statement**: Write a PL/SQL concurrent program that generates a period-end trial balance for a given ledger and period, with beginning balance, debits, credits, and ending balance per natural account segment. Handle the GL period status and currency translation.
- **Interview walkthrough**: Use `GL_BALANCES` and `GL_CODE_COMBINATIONS` (as `GL_CODE_COMBINATIONS`). Understand that `GL_BALANCES` stores period balances by period name. Join with `FND_PERIODS` for period info. Handle translated balances via `GL_DAILY_BALANCES` if needed.
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE trial_balance_report (
  p_ledger_id  IN NUMBER,
  p_period     IN VARCHAR2
) AS
  CURSOR c_tb IS
    SELECT gcc.segment1 natural_account,
           SUM(NVL(gb.begin_balance_dr, 0) -
               NVL(gb.begin_balance_cr, 0)) opening_balance,
           SUM(NVL(gb.period_net_dr, 0)) period_debits,
           SUM(NVL(gb.period_net_cr, 0)) period_credits,
           SUM(NVL(gb.begin_balance_dr, 0) -
               NVL(gb.begin_balance_cr, 0) +
               NVL(gb.period_net_dr, 0) -
               NVL(gb.period_net_cr, 0)) closing_balance
      FROM gl_balances gb,
           gl_code_combinations gcc
     WHERE gb.code_combination_id = gcc.code_combination_id
       AND gb.ledger_id = p_ledger_id
       AND gb.period_name = p_period
       AND gb.actual_flag = 'A'      -- Actual amounts
       AND gb.budget_version_id IS NULL
       AND gb.encumbrance_type_id IS NULL
     GROUP BY gcc.segment1
     ORDER BY gcc.segment1;
  l_net_balance NUMBER;
BEGIN
  fnd_file.put_line(fnd_file.output, 'Natural Account,Opening,Debits,Credits,Closing');
  FOR rec IN c_tb LOOP
    fnd_file.put_line(fnd_file.output,
      rec.natural_account || ',' ||
      TO_CHAR(rec.opening_balance) || ',' ||
      TO_CHAR(rec.period_debits) || ',' ||
      TO_CHAR(rec.period_credits) || ',' ||
      TO_CHAR(rec.closing_balance));
  END LOOP;
END trial_balance_report;
/
```
- **EBS-specific context**: `GL_BALANCES`, `GL_CODE_COMBINATIONS`, `GL_DAILY_BALANCES`, `GL_PERIOD_STATUSES`, `FND_PERIODS`, flexfield segment qualifiers.
- **What Oracle evaluates**: GL schema understanding, period close, balance types (actual/budget/encumbrance), currency translation basics.
- **Follow-ups**: How do you handle multi-currency trial balance? Explain `GL_DAILY_BALANCES` vs `GL_BALANCES`. How does EBS calculate translated balances?

#### Problem: AP Invoice Hold and Validation
- **Difficulty/Frequency**: Medium / Frequent
- **Problem statement**: A user complains that an invoice is stuck on "Held" status with no visible hold name. Write the SQL to diagnose why the invoice is on hold, identify the hold type, and write a PL/SQL block to release the hold if certain conditions are met.
- **Interview walkthrough**: AP holds are stored in `AP_HOLDS_ALL`. Each hold has a type like `AMOUNT`, `MATCH`, `POLICY`, `TAX`. Find the hold in `AP_HOLDS_ALL`, check `AP_HOLD_CODES` for description. To release, use `AP_HOLDS_PKG.RELEASE_HOLD` or update the `release_flag`. Invoice distribution matching is in `AP_INVOICE_DISTRIBUTIONS_ALL` with `PO_DISTRIBUTIONS_ALL`.
- **SQL/PLSQL/Java solution**:
```sql
-- Diagnose holds on invoice 12345
SELECT ah.invoice_id,
       ah.hold_reason,
       ah.hold_date,
       ahc.hold_code_name,
       ahc.hold_description,
       ah.release_flag
  FROM ap_holds_all ah,
       ap_hold_codes_tl ahc
 WHERE ah.hold_code_id = ahc.hold_code_id
   AND ah.hold_lookup_code = ahc.hold_lookup_code
   AND ah.invoice_id = 12345;

-- Release matching hold if PO match > 5% tolerance
DECLARE
  CURSOR c_hold IS
    SELECT ah.hold_id, ah.hold_code_id
      FROM ap_holds_all ah
     WHERE ah.invoice_id = 12345
       AND ah.release_flag IS NULL
       AND ROWNUM = 1;
  l_tolerance_percent NUMBER := 5.0;
  l_match_tolerance   NUMBER;
BEGIN
  FOR h IN c_hold LOOP
    SELECT ABS(NVL(ai.invoice_amount, 0) -
               NVL(poa.po_amount, 0)) / NULLIF(poa.po_amount, 0) * 100
      INTO l_match_tolerance
      FROM ap_invoices_all ai,
           (SELECT SUM(NVL(pd.amount, 0)) po_amount
              FROM po_distributions_all pd,
                   ap_invoice_distributions_all aid
             WHERE aid.invoice_id = 12345
               AND aid.po_distribution_id = pd.po_distribution_id) poa
     WHERE ai.invoice_id = 12345;

    IF l_match_tolerance <= l_tolerance_percent THEN
      ap_holds_pkg.release_hold(
        p_hold_id     => h.hold_id,
        p_release_reason => 'Auto-release within tolerance'
      );
      COMMIT;
      fnd_file.put_line(fnd_file.log, 'Hold released: ' || h.hold_id);
    ELSE
      fnd_file.put_line(fnd_file.log, 'Hold NOT released: tolerance exceeded ' ||
                                      l_match_tolerance || '%');
    END IF;
  END LOOP;
END;
/
```
- **EBS-specific context**: `AP_HOLDS_ALL`, `AP_HOLD_CODES`, `AP_HOLDS_PKG`, `AP_INVOICES_ALL`, `PO_DISTRIBUTIONS_ALL`, `AP_INVOICE_DISTRIBUTIONS_ALL`.
- **What Oracle evaluates**: Payables schema, hold management, tolerance calculation, release logic.
- **Follow-ups**: How does three-way matching work? What are distributor-level holds vs header-level holds? Explain AP approval workflow.

## EBS-Specific Deep Dive Questions

- **Product Architecture**: Explain the R12.2 architecture — database tier, application tier (forms, OHS, OAF), desktop tier. How does OHS route requests to forms servlet vs OAF? What is the role of `APPS` schema?
- **Online Patching (ADOP)**: Walk through the ADOP cycle (`prepare`, `cutover`, `finalize`, `cleanup`). Explain Edition-Based Redefinition (EBR) — how editions work, cross-edition triggers, `FND_VIEWS`, invalidations.
- **Concurrent Managers**: Internal vs conflict resolution manager. How does the coordinator assign requests? Explain PSM (Program Specific Manager) and transaction managers.
- **Upgrade Paths**: 11i to R12 migration — what changes (multi-org, globalization, ledger architecture). R12.1 to R12.2 — ADOP, workflow changes, OHS upgrade. Downtime minimization during upgrades.
- **Flexfields**: Descriptive vs Key flexfields. How are segment values stored (FND_FLEX_VALUES, FND_ID_FLEXS)? How do you add a segment dynamically?
- **Workflow**: Business Event System (BES) vs Oracle Workflow. How does the Workflow Mailer work? How to monitor workflow failures?
- **Performance**: Explain how EBS uses FND_HASH and FND_GLOBAL for session management. Tuning concurrent requests — how many managers? Which tables are hotspots?

## Behavioral Questions (STAR)

- **Situation**: Ledger consolidation for a global rollout with 15 operating units across 4 currencies.
  - **Task**: Ledger close cycle from 18 days to 5 days.
  - **Action**: Implemented automated intercompany reconciliation concurrent program, created GL cross-validation rules, and trained AP/AR teams on EBS R12 period close process. Used `GL_RECON_AUTO` program with custom matching thresholds.
  - **Result**: Close cycle reduced to 6 days; intercompany discrepancies dropped 92%.

- **Situation**: Production concurrent manager bottleneck during month-end close.
  - **Task**: Reduce concurrent request queue wait times exceeding 4 hours.
  - **Action**: Analyzed `FND_CONCURRENT_REQUESTS` and `FND_CONCURRENT_PROCESSES` to identify contention. Reconfigured managers — created target managers for GL/AP programs, enabled incompatible rules, tuned `INV_TXN_MANAGER`.
  - **Result**: Queue wait times reduced to under 15 minutes. Month-end close completed in 3 days vs previous 7.

- **Situation**: Client upgrade from 11i to R12.2 with minimal downtime.
  - **Task**: Plan upgrade with < 4 hours of downtime.
  - **Action**: Designed cutover strategy using ADOP with zero-downtime patching, edition-based redefinition, pre-upgrade patch cycle, and parallel testing of R12.2 on a staged environment.
  - **Result**: Downtime was 2.5 hours. Post-go-live incidents reduced 80% vs prior upgrades.

- **Situation**: Custom OAF page for inventory approval was running slow (> 30 seconds).
  - **Task**: Reduce page load time to under 3 seconds.
  - **Action**: Profiled OAF page — identified N+1 query pattern in the AM (Application Module). Added view object caching, batch fetches, and replaced `VOImpl` with `ViewObject` SQL mode. Created DB index on `MTL_SYSTEM_ITEMS_B` by organization.
  - **Result**: Page loaded in 1.2 seconds. User satisfaction improved.

- **Situation**: Security audit found 8 orphaned EBS accounts with sysadmin privileges.
  - **Task**: Remediate accounts immediately and create preventive framework.
  - **Action**: Created PL/SQL audit script that cross-references `FND_USER` with HR employees, `FND_RESPONSIBILITY` assignments. Set up automated deactivation via a monthly concurrent program.
  - **Result**: Zero open audit findings in next cycle. Automated report created in FND.

## Study Plan

| Priority | Labs |
|----------|------|
| Must | Lab 01 (Architecture), Lab 02 (System Admin), Lab 03 (Financials), Lab 09 (Security) |
| Recommended | Lab 05 (HRMS), Lab 06 (Customization/OAF) |
| Additional | Lab 04 (Supply Chain), Lab 07 (Reporting), Lab 08 (Integrations) |
| Niche | Lab 10 (Upgrade/Migration) |

### Lab 10: Upgrade / Migration

#### Problem: R12.2 ADOP Cutover Planning
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: You are leading an upgrade from R12.1 to R12.2. Design the ADOP cutover plan. Write the PL/SQL to validate that all edition-enabled objects are valid before cutover, identify invalid objects, and automate the finalize phase checks.
- **Interview walkthrough**: ADOP cycle: prepare, apply, finalize, cleanup. Before cutover, validate edition views using `FND_VIEWS`, check `DBA_OBJECTS` for invalid objects in the patch edition. Use `ADOP_PHASE_CTRL` for phase management. Key tables: `FND_EDITION_OBJECTS`, `FND_VIEWS_AUDIT`. Use `ADOP_UTILITIES_PKG` for validation.
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE validate_adop_cutover (
  p_edition_name  IN VARCHAR2
) AS
  l_invalid_count NUMBER;
BEGIN
  -- Switch to patch edition
  EXECUTE IMMEDIATE 'ALTER SESSION SET EDITION = ' || p_edition_name;

  -- Check invalid objects
  SELECT COUNT(*)
    INTO l_invalid_count
    FROM dba_objects
   WHERE edition_name = p_edition_name
     AND status = 'INVALID'
     AND owner = 'APPS';

  IF l_invalid_count > 0 THEN
    fnd_file.put_line(fnd_file.log,
      'WARNING: ' || l_invalid_count || ' invalid objects found.');
    FOR rec IN (
      SELECT object_name, object_type
        FROM dba_objects
       WHERE edition_name = p_edition_name
         AND status = 'INVALID'
         AND owner = 'APPS'
    ) LOOP
      fnd_file.put_line(fnd_file.log,
        rec.object_type || ': ' || rec.object_name);
    END LOOP;
  ELSE
    fnd_file.put_line(fnd_file.log, 'All edition objects are valid.');
  END IF;

  -- Check cross-edition triggers
  FOR rec IN (
    SELECT trigger_name, table_name, status
      FROM dba_triggers
     WHERE edition_name = p_edition_name
       AND trigger_type LIKE '%CROSS%'
       AND status = 'DISABLED'
  ) LOOP
    fnd_file.put_line(fnd_file.log,
      'Cross-edition trigger DISABLED: ' || rec.trigger_name);
  END LOOP;

  -- Validate FND views
  FOR rec IN (
    SELECT view_name, status
      FROM fnd_views_audit
     WHERE status = 'N'  -- Not valid
  ) LOOP
    fnd_file.put_line(fnd_file.log,
      'FND view invalid: ' || rec.view_name);
  END LOOP;

  -- Cutover readiness summary
  IF l_invalid_count = 0 THEN
    fnd_file.put_line(fnd_file.output, 'CUTOVER_READY: YES');
  ELSE
    fnd_file.put_line(fnd_file.output, 'CUTOVER_READY: NO');
  END IF;
END validate_adop_cutover;
/
```
- **EBS-specific context**: ADOP phases, FND_EDITION_OBJECTS, FND_VIEWS_AUDIT, DBA_OBJECTS, DBA_TRIGGERS, ADOP_PHASE_CTRL, ADOP_UTILITIES_PKG, edition-based redefinition (EBR).
- **What Oracle evaluates**: Deep upgrade expertise, ADOP internals, EBR knowledge, cutover risk management.
- **Follow-ups**: How does ADOP handle online patching for data patches vs code patches? What are forward-flow and reverse-flow cross-edition triggers? How do you roll back an ADOP cycle?

## Tips

- **Know your meta data**: Oracle interviewers frequently ask about `FND_TABLES`, `FND_VIEWS`, `FND_COLUMNS`, `FND_LOOKUPS`. Be prepared to query these for troubleshooting.
- **ADOP is mandatory**: If you claim R12.2 experience, be ready to describe every step of ADOP and EBR.
- **Certifications matter**: Oracle values OCP (Oracle Certified Professional) and OCM. They may ask about certification study paths.
- **OAF/ADF**: Oracle's strategic direction is toward cloud (Fusion), but they still need EBS OAF experts. Know the MVC pattern — `OAApplicationModule`, `OAViewObject`, `OADataBean`.
- **Concurrent programming**: Be prepared to write a concurrent program from scratch — registration, parameters, `FND_FILE` output, request completion.
- **Tablespace management**: Oracle EBS DBAs manage tablespaces heavily. Know `APPS.TS_TX_DATA` vs `APPS.TS_SUMMARY`.
