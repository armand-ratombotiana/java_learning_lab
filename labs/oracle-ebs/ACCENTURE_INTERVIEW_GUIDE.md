# Accenture Interview Guide — Oracle EBS Academy

## Interview Process for EBS Roles

### Rounds
- **Round 1 – Screening (45 min)**: Experience verification, EBS modules worked on, team size, client industry. Quick technical questions on basic PL/SQL and EBS architecture.
- **Round 2 – Technical Assessment (1 hr)**: Deep PL/SQL coding test — write a procedure, a function, a trigger. EBS tables and APIs — FND, AP, AR, GL. Frequently includes a performance tuning exercise (e.g., explain query plan).
- **Round 3 – Solution Architecture (1 hr)**: Whiteboard a solution for a complex client requirement. Could involve multi-module integration, data migration strategy, or custom OAF + workflow design.
- **Round 4 – HR/Leadership (45 min)**: Accenture values — Stewardship, Best People, Client Value Creation, One Global Network, Respect for the Individual. Behavioral questions tied to these.
- **Timeline**: 1–3 weeks. Accenture often takes longer due to project alignment.

### EBS-Specific Expectations
- Accenture is a massive SI — they need people who can "hit the ground running." Deep hands-on configuration + development skills expected.
- Experience with large-scale transformations (brownfield, greenfield) is a differentiator.
- Knowledge of Oracle's Cloud strategy — Accenture pushes clients toward Oracle Cloud (Fusion) but still maintains large EBS install base.
- Accenture heavily values certifications — OCP, OCM, PMP, TOGAF.
- Project experience in at least 2 full lifecycle implementations is the bar.

## Top Technical Problems by Lab

### Lab 01: EBS Architecture and Fundamentals

#### Problem: EBS R12.2 Architecture — OHS and Forms Servlet
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: A client reports that Oracle Forms sessions are timing out after 1 hour of inactivity, but OAF pages stay connected. Explain the architecture difference between Forms and OAF, identify where the timeout is configured, and write the SQL to diagnose active Forms sessions.
- **Interview walkthrough**: Forms uses `f60sqm` or `FNDRSSUB` (depending on architecture) and the Forms Listener Servlet. OAF runs on OHS with JSF lifecycle managed. Forms timeout is in `appsweb.cfg` (`sessionTimeout` parameter). OAF timeout is in OHS config (`httpd.conf`, `Timeout` directive). Active sessions can be seen in `FND_LOGIN_RESP_FORMS` and `ICX_SESSIONS`.
- **SQL/PLSQL/Java solution**:
```sql
-- Active Forms sessions
SELECT fu.user_name,
       flrf.forms_name,
       flrf.start_time,
       flrf.session_id,
       icx.serial_number,
       icx.last_activity_date,
       ROUND((SYSDATE - icx.last_activity_date) * 24 * 60, 1) idle_minutes
  FROM fnd_login_resp_forms flrf,
       fnd_user fu,
       icx_sessions icx
 WHERE flrf.user_id = fu.user_id
   AND flrf.session_id = icx.session_id
   AND flrf.end_time IS NULL
   AND icx.last_activity_date < SYSDATE - (1 / 24)  -- idle > 1 hr
 ORDER BY idle_minutes DESC;

-- Check forms timeout setting
SELECT name, value, default_value
  FROM fnd_profile_options_vl fpo,
       fnd_profile_option_values fpov
 WHERE fpov.profile_option_id = fpo.profile_option_id
   AND fpo.profile_option_name = 'ICX_FORMS_TIMEOUT';
```
- **EBS-specific context**: `FND_LOGIN_RESP_FORMS`, `ICX_SESSIONS`, `FND_SESSION_INFO`, `appsweb.cfg`, `FND_WEB_CONFIG`, `ICX_FORMS_TIMEOUT` profile option.
- **What Accenture evaluates**: Architecture breadth — can you troubleshoot across Forms and OAF? Understanding of EBS web tier components.
- **Follow-ups**: How does EBS handle session failover? What happens when `ICX_SESSIONS` is purged? Explain load balancing for EBS — which tier do you scale?

#### Problem: Value Set Validation — Flexfield Security
- **Difficulty/Frequency**: Medium / Frequent
- **Problem statement**: A client has a custom descriptive flexfield (DFF) with a dependent value set that shows incorrect values. Diagnose the issue — check the value set definition, verify the dependency structure, and fix it. Write the queries to debug and provide the correction.
- **Interview walkthrough**: DFFs are defined in `FND_DESCRIPTIVE_FLEXS`, `FND_DESCRIPTIVE_FLEXS_VL`, and `FND_DESCRIPTIVE_FLEX_CONTEXTS`. Value sets are in `FND_FLEX_VALIDATION_SETS`. Dependent values are linked via `FND_FLEX_VALUES` where `parent_flex_value_id` is set. The issue could be missing value set mapping or incorrect `include_flag`.
- **SQL/PLSQL/Java solution**:
```sql
-- Check value set definition
SELECT fvs.flex_validation_set_id,
       fvs.flex_validation_set_name,
       fvs.validation_type,
       fvs.value_set_definition
  FROM fnd_flex_validation_sets fvs
 WHERE fvs.flex_validation_set_name = 'CUSTOM_DFF_VS';

-- View all values in the set
SELECT ffv.flex_value_id,
       ffv.flex_value,
       ffv.description,
       ffv.parent_flex_value_id,
       ffv.parent_flex_value
  FROM fnd_flex_values ffv
 WHERE ffv.flex_value_set_id = (
     SELECT flex_validation_set_id
       FROM fnd_flex_validation_sets
      WHERE flex_validation_set_name = 'CUSTOM_DFF_VS'
   )
 ORDER BY ffv.flex_value;

-- Check dependency: what values are children of 'CORP'
SELECT ffv.flex_value child_value,
       ffv.description child_desc,
       ffv.parent_flex_value parent_value
  FROM fnd_flex_values ffv
 WHERE ffv.flex_value_set_id = (
     SELECT flex_validation_set_id
       FROM fnd_flex_validation_sets
      WHERE flex_validation_set_name = 'CUSTOM_DFF_VS'
   )
   AND ffv.parent_flex_value = 'CORP';

-- Fix: re-link orphaned values
UPDATE fnd_flex_values
   SET parent_flex_value_id = (
       SELECT flex_value_id
         FROM fnd_flex_values
        WHERE flex_value = 'CORP'
          AND flex_value_set_id = ffvs.flex_value_set_id
          AND ROWNUM = 1
   )
  WHERE flex_value_set_id = (
      SELECT flex_validation_set_id
        FROM fnd_flex_validation_sets
       WHERE flex_validation_set_name = 'CUSTOM_DFF_VS'
    )
    AND parent_flex_value_id IS NULL
    AND hierarchy_level = 2;
```
- **EBS-specific context**: `FND_DESCRIPTIVE_FLEXS`, `FND_FLEX_VALIDATION_SETS`, `FND_FLEX_VALUES`, `FND_FLEX_VALUE_CHILDREN`, `FND_ID_FLEXS` for key flexfields.
- **What Accenture evaluates**: Flexfield troubleshooting, value set hierarchy understanding, ability to fix configuration data.
- **Follow-ups**: What is the difference between Independent, Dependent, and Table-validated value sets? How does flexfield security work? What is `FND_FLEX_SERVER` and how does C-level validation work?

### Lab 03: Financials (GL, AP, AR)

#### Problem: GL Period Close — Suspense Account Clearance
- **Difficulty/Frequency**: High / Very Frequent
- **Problem statement**: During month-end close, the GL suspense account has a $500K balance from unidentifiable transactions. Write a PL/SQL block to analyze the source of suspense entries by module (AP, AR, FA, INV), identify the root cause, and clear the suspense to the proper GL accounts.
- **Interview walkthrough**: Suspense accounts are defined in `GL_SUSPENSE_ACCOUNTS` or via account type in `GL_CODE_COMBINATIONS`. Suspense entries originate from AP (unmatched invoices), AR (unapplied receipts), INV (transaction cost errors). Query `GL_JE_LINES` or `GL_IMPORT_REFERENCES` grouped by source. Use `GL_INTERFACE` to create clearing entries.
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE suspense_clearance (
  p_period_name     IN VARCHAR2,
  p_ledger_id       IN NUMBER
) AS
  CURSOR c_suspense IS
    SELECT gcc.code_combination_id,
           gcc.segment1 || '-' || gcc.segment2 account,
           gls.period_net_dr - gls.period_net_cr balance,
           gls.source_code
      FROM gl_balances gls,
           gl_code_combinations gcc
     WHERE gls.code_combination_id = gcc.code_combination_id
       AND gls.period_name = p_period_name
       AND gls.ledger_id = p_ledger_id
       AND gcc.account_type = 'S'  -- Suspense
       AND ABS(gls.period_net_dr - gls.period_net_cr) > 0;

  PROCEDURE clear_by_source (
    p_ccid    NUMBER,
    p_amount  NUMBER,
    p_source  VARCHAR2
  ) IS
  BEGIN
    INSERT INTO gl_interface
    (source_name, group_id, status, ledger_id,
     je_source_name, je_category_name, code_combination_id,
     period_name, entered_dr, entered_cr, actual_flag,
     user_je_source_name, attribute1)
    VALUES
    ('SUSPENSE_CLR', gid_seq.NEXTVAL, 'NEW', p_ledger_id,
     'SUSPENSE_CLR', 'SUSPENSE_CLR', p_ccid,
     p_period_name,
     DECODE(SIGN(p_amount), -1, -p_amount, 0),  -- DR if negative
     DECODE(SIGN(p_amount), 1, p_amount, 0),    -- CR if positive
     'A', p_source, PERIOD_CLOSE_PKG.GET_CLOSE_STATUS);
  END clear_by_source;
BEGIN
  FOR rec IN c_suspense LOOP
    fnd_file.put_line(fnd_file.log,
      'Clearing suspense: ' || rec.account || ' Amount: ' ||
      TO_CHAR(rec.balance) || ' Source: ' || rec.source_code);
    clear_by_source(rec.code_combination_id, rec.balance, rec.source_code);
  END LOOP;
  COMMIT;
  fnd_file.put_line(fnd_file.log, 'Suspense clearance complete');
END suspense_clearance;
/
```
- **EBS-specific context**: `GL_BALANCES`, `GL_CODE_COMBINATIONS`, `GL_JE_LINES`, `GL_IMPORT_REFERENCES`, `GL_INTERFACE`, `GL_SUSPENSE_ACCOUNTS`, `GL_SOURCES` (for source codes).
- **What Accenture evaluates**: Month-end close expertise, ability to automate manual processes, GL architecture knowledge.
- **Follow-ups**: What is the purpose of `GL_IMPORT_REFERENCES`? How do you prevent suspense transactions? What are sub-ledger accounting (SLA) rules? Explain `XLA_TRANSACTIONS_GT`.

### Lab 04: Supply Chain (INV, PO, OM)

#### Problem: Order Management — ATP Check and Backorder
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: A client's sales order processing is slow because ATP (Available-to-Promise) checks are timing out during peak hours. Write a PL/SQL routine to evaluate on-hand inventory vs backorders, and provide a solution to optimize ATP performance or implement allocation rules.
- **Interview walkthrough**: ATP uses `MSC_ATP` which checks `MTL_ONHAND_QUANTITIES`, `MTL_RESERVATIONS`, and supply/demand from `MRP_FORECAST_DATES`. For EBS Order Management, `OE_ORDER_LINES_ALL` holds sales order lines. Backorders are lines where `flow_status_code = 'AWAITING_DELIVERY'` and `open_flag = 'Y'`. ATP performance can be improved by using `MSC_ATP_SNAP` snapshot tables or custom ATP based on `MTL_ONHAND_QUANTITIES` for simple cases.
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE atp_check_and_backorder (
  p_organization_id  IN NUMBER,
  p_min_onhand_qty   IN NUMBER DEFAULT 0
) AS
  CURSOR c_backorder IS
    SELECT ool.line_id,
           ool.ordered_item,
           ool.ordered_quantity,
           ool.shipped_quantity,
           (ool.ordered_quantity - NVL(ool.shipped_quantity, 0)) backorder_qty,
           msib.description,
           msib.inventory_item_id
      FROM oe_order_lines_all ool,
           mtl_system_items_b msib
     WHERE ool.inventory_item_id = msib.inventory_item_id
       AND msib.organization_id = p_organization_id
       AND ool.flow_status_code = 'AWAITING_DELIVERY'
       AND ool.open_flag = 'Y'
       AND (ool.ordered_quantity - NVL(ool.shipped_quantity, 0)) > 0;

  FUNCTION get_onhand_qty (p_item_id NUMBER, p_org_id NUMBER)
    RETURN NUMBER IS
    l_qty NUMBER;
  BEGIN
    SELECT SUM(primary_transaction_quantity)
      INTO l_qty
      FROM mtl_onhand_quantities
     WHERE inventory_item_id = p_item_id
       AND organization_id = p_org_id;
    RETURN NVL(l_qty, 0);
  END get_onhand_qty;

  PROCEDURE allocate_inventory (
    p_line_id      NUMBER,
    p_item_id      NUMBER,
    p_allocation   NUMBER
  ) IS
  BEGIN
    oe_reservation_pub.create_reservation(
      p_api_version_number  => 1.0,
      p_init_msg_list       => 'T',
      p_commit              => 'T',
      p_inventory_item_id   => p_item_id,
      p_organization_id     => p_organization_id,
      p_demand_source_line_id => p_line_id,
      p_demand_source_type_id => 2,  -- Sales Order
      p_quantity            => p_allocation
    );
    COMMIT;
  END allocate_inventory;

BEGIN
  FOR bo IN c_backorder LOOP
    DECLARE
      l_onhand NUMBER := get_onhand_qty(bo.inventory_item_id,
                                         p_organization_id);
    BEGIN
      IF l_onhand >= bo.backorder_qty THEN
        allocate_inventory(bo.line_id, bo.inventory_item_id,
                           bo.backorder_qty);
        fnd_file.put_line(fnd_file.log,
          'Allocated ' || bo.backorder_qty || ' of ' ||
          bo.ordered_item || ' from ATP');
      ELSE
        fnd_file.put_line(fnd_file.log,
          'Insufficient ATP for ' || bo.ordered_item ||
          ' (need: ' || bo.backorder_qty || ', have: ' || l_onhand || ')');
      END IF;
    END;
  END LOOP;
END atp_check_and_backorder;
/
```
- **EBS-specific context**: `OE_ORDER_LINES_ALL`, `MTL_ONHAND_QUANTITIES`, `MTL_RESERVATIONS`, `OE_RESERVATION_PUB`, `MSC_ATP`, `MTL_SUPPLY`, `MRP_FORECAST_DATES`.
- **What Accenture evaluates**: Supply chain process depth, OM + INV integration, ATP understanding, performance optimization.
- **Follow-ups**: How does EBS calculate ATP across multiple organizations? Explain backward consumption vs forward consumption. What are ATP rules and how do they affect allocation?

### Lab 05: HRMS

#### Problem: Payroll Run — Salary Element Entry Validation
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: A client's payroll run failed because salary element entries were incorrectly structured for 200 employees. Write a PL/SQL validation routine that checks PAY_ELEMENT_ENTRY_VALUES for common errors (missing input values, incorrect effective dates, invalid element links) before payroll is processed.
- **Interview walkthrough**: HRMS payroll uses PAY_ELEMENT_ENTRY_VALUES, PAY_ELEMENT_TYPES_F, PAY_RUN_RESULTS. The validation should check: all required input values are present, effective dates are within element link dates, basic amount ranges are respected. Use PAY_ELEMENT_ENTRY_PKG for correction.
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE validate_payroll_entries (
  p_payroll_id    IN NUMBER,
  p_effective_date IN DATE
) AS
  CURSOR c_entries IS
    SELECT peev.element_entry_id,
           peev.element_type_id,
           petf.element_name,
           peev.assignment_id,
           peev.effective_start_date,
           peev.effective_end_date,
           peev.input_value_id,
           peev.value
      FROM pay_element_entry_values_f peev,
           pay_element_types_f petf,
           per_all_assignments_f paaf
     WHERE peev.element_type_id = petf.element_type_id
       AND peev.assignment_id = paaf.assignment_id
       AND paaf.payroll_id = p_payroll_id
       AND p_effective_date BETWEEN peev.effective_start_date
                                AND peev.effective_end_date
       AND peev.value IS NULL;

  PROCEDURE fix_entry (p_entry_id NUMBER, p_value VARCHAR2) IS
  BEGIN
    pay_element_entry_pkg.update_value(
      p_element_entry_id => p_entry_id,
      p_input_value_id   => NULL,
      p_value            => p_value,
      p_effective_date   => p_effective_date
    );
  END fix_entry;

  l_fix_count NUMBER := 0;
BEGIN
  FOR rec IN c_entries LOOP
    fix_entry(rec.element_entry_id, '0');
    l_fix_count := l_fix_count + 1;
    fnd_file.put_line(fnd_file.log,
      'Fixed NULL value for element ' || rec.element_name ||
      ' assignment ' || rec.assignment_id);
  END LOOP;
  fnd_file.put_line(fnd_file.log, 'Total fixes: ' || l_fix_count);
  COMMIT;
END validate_payroll_entries;
/
```
- **EBS-specific context**: PAY_ELEMENT_ENTRY_VALUES_F, PAY_ELEMENT_TYPES_F, PAY_ELEMENT_ENTRY_PKG, PER_ALL_ASSIGNMENTS_F, PAY_PAYROLL_ACTIONS, PAY_RUN_RESULTS.
- **What Accenture evaluates**: HRMS data model knowledge, payroll processing understanding, ability to validate and fix data proactively.
- **Follow-ups**: How does element eligibility work? Explain PAY_INPUT_VALUES_F and element links. What is the difference between recurring and non-recurring elements? How do balances work in payroll?

## EBS-Specific Deep Dive Questions

- **Implementation Methodology**: Accenture uses its own delivery methodology (Accenture Delivery Suite). How does it compare with AIM/OUM? What are the key work products?
- **Large Program Management**: How do you manage a 50+ person EBS implementation? Governance structure — steering committee, workstream leads, weekly status reporting. How do you handle SI (System Integration) across 12+ modules?
- **Data Conversion Strategy**: Accenture does massive data migrations. Walk through the conversion plan for converting 50M+ records from a legacy ERP into EBS. ETL strategy, validation rules, error handling. Use of `FND_LOAD`, `AD_LOAD`, SQL*Loader, OIC (Oracle Integration Cloud).
- **Customization Risk Management**: Accenture has strong governance around customizations. Explain the CEMLI (Configuration, Extension, Modification, Localization, Integration) tracking methodology. How do you decide what goes into the "Do Not Customize" list?
- **Testing at Scale**: Test strategy for a 50-module EBS implementation — how many test cycles? Data-driven testing with `FND_TEST`. Automation using Oracle Application Testing Suite (OATS) or Selenium for OAF pages.
- **OAF Personalization vs Extension**: When do you personalize vs extend vs customize? How do personalizations impact upgrades?

## Behavioral Questions (STAR)

- **Situation**: Two different clients running the same EBS version but needing completely different business processes for procurement.
  - **Task**: Design a single solution that satisfies both without forking the code.
  - **Action**: Leveraged MOAC with OU-specific purchasing rules (`PO_PURCHASING_OPTIONS` per OU), created OU-specific document sequences, and used profile option `PO: Force Document Style` for conditional behavior.
  - **Result**: Single instance supporting both clients. Project delivered under budget by $200K.

- **Situation**: Critical payroll issue during HRMS go-live — salary calculations wrong for 15% of employees.
  - **Task**: Fix within 24 hours.
  - **Action**: Identified that `PAY_ELEMENT_ENTRY_VALUES` had incorrect input values due to a conversion mapping error. Wrote PL/SQL correction script using `PAY_ELEMENT_ENTRY_PKG.UPDATE_VALUE`. Validated via `PAY_PAYROLL_ACTIONS` reruns.
  - **Result**: Fixed in 14 hours. Payroll ran correctly with no financial impact.

- **Situation**: Client wanted all custom reports in BI Publisher within 3 months.
  - **Task**: Migrate 200+ XML Publisher / Oracle Reports to BI Publisher.
  - **Action**: Created a conversion template using `XDO_DS` definitions, automated XML data template generation from existing SQLs, and trained client team on BI Publisher layout design.
  - **Result**: All 215 reports migrated in 10 weeks. Report performance improved 40%.

- **Situation**: Custom OAF page for expense reporting was failing intermittently.
  - **Task**: Diagnose and resolve.
  - **Action**: Found that the `OAApplicationModule` was not handling database connection exceptions properly for `AP_EXPENSE_REPORT_HEADERS`. Added proper `TransactionScope` management and `OADBTransaction` rollback logic.
  - **Result**: Zero failures after fix. Root cause documented in knowledge base.

- **Situation**: Offshore team was delivering poor-quality integration code.
  - **Task**: Improve quality without extensive rework.
  - **Action**: Implemented automated code review using custom PL/SQL scripts that checked against coding standards (exception handling, `FND_FILE` usage, `G_MISS` handling). Established peer review checklist aligned with Accenture coding standards.
  - **Result**: Quality score improved from 62% to 94% in 2 sprints.

## Study Plan

| Priority | Labs |
|----------|------|
| Must | Lab 01 (Architecture), Lab 03 (Financials), Lab 04 (Supply Chain), Lab 08 (Integrations) |
| Recommended | Lab 05 (HRMS), Lab 06 (Customization/OAF), Lab 07 (Reporting) |
| Additional | Lab 02 (System Admin), Lab 09 (Security) |
| Niche | Lab 10 (Upgrade/Migration) |

### Lab 07: Reporting (BI Publisher, XML)

#### Problem: BI Publisher Bursting — Invoice Distribution by Region
- **Difficulty/Frequency**: Medium / Frequent
- **Problem statement**: Configure BI Publisher bursting for a monthly AP aging report that must be sent to regional finance managers — each receives only their region's data. Write the data template and bursting control file.
- **Interview walkthrough**: BI Publisher bursting splits one report into multiple outputs based on a key (region). The data model extracts AP aging data from AP_INVOICES_ALL and AP_PAYMENT_SCHEDULES_ALL. The bursting control XML defines delivery per region key using XDO bursting. The key column maps to email via FND_USER or custom contact list.
- **SQL/PLSQL/Java solution**:
```sql
-- Data template for AP Aging with region key
SELECT ai.invoice_id,
       ai.invoice_num,
       ai.invoice_date,
       ai.invoice_amount,
       ai.payment_status_flag,
       NVL(aps.amount_due, 0) amount_due,
       NVL(aps.amount_due_remaining, 0) amount_remaining,
       ROUND(SYSDATE - NVL(ai.invoice_date, SYSDATE)) aging_days,
       CASE
         WHEN ROUND(SYSDATE - ai.invoice_date) BETWEEN 0 AND 30 THEN '0-30'
         WHEN ROUND(SYSDATE - ai.invoice_date) BETWEEN 31 AND 60 THEN '31-60'
         WHEN ROUND(SYSDATE - ai.invoice_date) BETWEEN 61 AND 90 THEN '61-90'
         ELSE '90+'
       END aging_bucket,
       pv.vendor_name,
       pv.vendor_id,
       hou.name operating_unit,
       hou.organization_id region_key  -- Bursting key
  FROM ap_invoices_all ai,
       ap_payment_schedules_all aps,
       po_vendors pv,
       hr_operating_units hou
 WHERE ai.invoice_id = aps.invoice_id (+)
   AND ai.vendor_id = pv.vendor_id
   AND ai.org_id = hou.organization_id
   AND ai.payment_status_flag <> 'P'
 ORDER BY hou.name, pv.vendor_name;

-- Bursting control XML
/*
<?xml version="1.0" encoding="UTF-8"?>
<xdo:burst xmlns:xdo="http://xmlns.oracle.com/oxp/xdo">
  <xdo:burst-definition>
    <xdo:split-by>REGION_KEY</xdo:split-by>
    <xdo:delivery>
      <xdo:email from="ap-team@company.com"
                 to="{concat(region_key, '@company.com')}"
                 subject="AP Aging Report - {OPERATING_UNIT}"
                 body="Attached AP aging report for {OPERATING_UNIT}"/>
    </xdo:delivery>
    <xdo:output file-name="AP_AGING_{OPERATING_UNIT}_{SYSDATE}.pdf"/>
  </xdo:burst-definition>
</xdo:burst>
*/
```
- **EBS-specific context**: AP_INVOICES_ALL, AP_PAYMENT_SCHEDULES_ALL, PO_VENDORS, HR_OPERATING_UNITS, BI Publisher bursting, XDO_META_TABLES, FND_SRS_RUNS.
- **What Accenture evaluates**: BI Publisher advanced features (bursting), understanding of distribution/permissions by region, report automation.
- **Follow-ups**: How does BI Publisher bursting handle large volumes? What happens if a delivery fails? How do you secure bursted reports? Explain XDO_DS template registration.

## Tips

- **Full lifecycle stories**: Accenture values end-to-end implementation experience. Emphasize Design → Build → Test → Deploy.
- **CEMLI knowledge**: Be comfortable explaining CEMLI methodology. Accenture uses it to track every customization beyond configuration.
- **System Integration (SI)**: Accenture does massive SI programs. Be ready to talk about integration between EBS and third-party systems (WMS, TMS, Salesforce, SAP).
- **OIC and Cloud**: Even for EBS roles, Accenture expects awareness of Oracle Cloud (Fusion) and OIC (Oracle Integration Cloud) because they cross-sell.
- **Offshore model**: Accenture leverages global delivery. Discuss offshore coordination, knowledge transfer, and quality control.
- **Certifications stack**: Accenture employees are encouraged to stack certifications. OCP + PMP + TOGAF is common.
