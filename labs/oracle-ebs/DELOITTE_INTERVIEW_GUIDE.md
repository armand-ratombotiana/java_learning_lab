# Deloitte Interview Guide — Oracle EBS Academy

## Interview Process for EBS Roles

### Rounds
- **Round 1 – Partner/Manager Screen (45 min)**: Consulting methodology (AIM/OUM), project experience, client-facing skills, case discussion on implementation challenges. Expect "tell me about a time you handled a difficult client."
- **Round 2 – Technical Round (1 hr)**: Hands-on PL/SQL, EBS configuration — system admin, financials, supply chain. Questions about setups vs customizations. Whiteboard an integration architecture.
- **Round 3 – Functional Round (1 hr)**: Business process mapping — Order-to-Cash, Procure-to-Pay, Record-to-Report. How do you translate business requirements into EBS configurations? Functional design document walkthrough.
- **Round 4 – Cultural Fit (45 min)**: Deloitte values, teamwork, travel availability, client management, work-life balance expectations.
- **Timeline**: 2–3 weeks.

### EBS-Specific Expectations
- Deloitte is a systems integrator — they value hands-on configuration skill as much as development. "Can you set up a ledger or configure an AP approval workflow?"
- Consulting experience managing client expectations, writing MD.50/BR.100 (AIM deliverables) is highly valued.
- EBS experience across multiple modules is preferred (Financials + SCM + HRMS).
- Certifications are a plus but not mandatory. Industry experience (manufacturing, retail, public sector) matters.
- Deloitte emphasizes the ability to "own the track" — lead a workstream with minimal supervision.

## Top Technical Problems by Lab

### Lab 01: EBS Architecture and Fundamentals

#### Problem: Multi-Org Setup in a New Implementation
- **Difficulty/Frequency**: High / Very Frequent
- **Problem statement**: You are implementing EBS for a newly acquired subsidiary. Walk through the steps to set up multiple operating units under a single legal entity. Include the configuration steps, the tables populated, and how to verify the setup is correct.
- **Interview walkthrough**: Start with Legal Entity setup in `XLE_ENTITIES`. Then define Operating Units in `HR_OPERATING_UNITS`. Link them via `ORG_ORGANIZATION_DEFINITIONS` and `GL_LEDGER_LEV_LE_VAL`. Explain that a single Legal Entity can own multiple OUs but each OU maps to one Legal Entity. Accounting setup uses Ledger → Legal Entity → OU mapping via `GL_LEDGERS`. Walk through MOAC profile options.
- **SQL/PLSQL/Java solution**:
```sql
-- Verify Legal Entity configuration
SELECT xle.name legal_entity_name,
       xle.legal_entity_id,
       xle.legal_entity_registration_number,
       hou.name operating_unit_name,
       hou.organization_id org_id
  FROM xle_entity_profiles xle,
       hr_operating_units hou,
       gl_ledger_lev_lev_val gll
 WHERE gll.legal_entity_id = xle.legal_entity_id
   AND gll.legal_entity_id = hou.legal_entity_id
   AND hou.organization_id = gll.organization_id
 ORDER BY xle.name;

-- Check MOAC profile assignment
SELECT fpov.profile_option_name,
       fpov.profile_option_value,
       fu.user_name
  FROM fnd_profile_option_values fpov,
       fnd_profile_options fpo,
       fnd_user fu
 WHERE fpov.profile_option_id = fpo.profile_option_id
   AND fpov.level_value = fu.user_id (+)
   AND fpov.level_id = 10001  -- User
   AND fpo.profile_option_name IN ('MO_SECURITY_PROFILE_ID',
                                   'MFG_ORGANIZATION_ID');

-- Verify OU access grants
SELECT fu.user_name,
       hou.name operating_unit,
       foa.access_level
  FROM fnd_user fu,
       hr_operating_units hou,
       fnd_organization_access foa
 WHERE foa.user_id = fu.user_id
   AND foa.organization_id = hou.organization_id
   AND fu.user_name = 'AP_USER';
```
- **EBS-specific context**: `XLE_ENTITIES`, `HR_OPERATING_UNITS`, `ORG_ORGANIZATION_DEFINITIONS`, `GL_LEDGERS`, `GL_LEDGER_LEV_LE_VAL`, MO: Security Profile, `FND_ORGANIZATION_ACCESS`.
- **What Deloitte evaluates**: Configuration knowledge — not just querying but understanding the setup steps. Can you lead a multi-org implementation workstream?
- **Follow-ups**: What happens if you change a Legal Entity to a different Ledger? How do you handle intercompany transactions between OUs? Explain reporting hierarchy setup.

#### Problem: Profile Options — Configuration Audit
- **Difficulty/Frequency**: Medium / Frequent
- **Problem statement**: A client reports that users are seeing incorrect application behavior after implementing a new module. They suspect incorrect profile option settings. Write a diagnostic query to show all profile options with different values across site, application, responsibility, and user levels. Then explain the profile option hierarchy and how to resolve conflicts.
- **Interview walkthrough**: Profile option levels: Site (10004), Application (10003), Responsibility (10002), User (10001). Use `FND_PROFILE_OPTIONS`, `FND_PROFILE_OPTION_VALUES`. The lookup for level IDs is in `FND_LOOKUPS` for lookup type `LEVEL`. Explain that the lowest-level value wins (User overrides Responsibility, etc.).
- **SQL/PLSQL/Java solution**:
```sql
SET LINESIZE 200
COL user_name FORMAT A20
COL resp_name FORMAT A30
COL app_name FORMAT A20
COL value_at_level FORMAT A30

SELECT fpo.profile_option_name,
       DECODE(fpov.level_id,
              10004, 'Site',
              10003, 'Application',
              10002, 'Responsibility',
              10001, 'User') level_type,
       CASE
         WHEN fpov.level_id = 10001 THEN fu.user_name
         WHEN fpov.level_id = 10002 THEN frt.responsibility_name
         WHEN fpov.level_id = 10003 THEN fat.application_name
         ELSE 'Site'
       END level_value_name,
       fpov.profile_option_value
  FROM fnd_profile_options_vl fpo,
       fnd_profile_option_values fpov,
       fnd_user fu,
       fnd_responsibility_tl frt,
       fnd_application_tl fat
 WHERE fpov.profile_option_id = fpo.profile_option_id
   AND fpov.level_value = fu.user_id (+)
   AND fpov.level_value = frt.responsibility_id (+)
   AND fpov.level_value = fat.application_id (+)
   AND fpo.profile_option_name LIKE 'MO%'
 ORDER BY fpo.profile_option_name, fpov.level_id;
```
- **EBS-specific context**: `FND_PROFILE_OPTIONS`, `FND_PROFILE_OPTION_VALUES`, `FND_PROFILE_OPTIONS_VL`, `FND_LOOKUPS` (lookup_type = `LEVEL`).
- **What Deloitte evaluates**: Systematic diagnostic approach, understanding of profile option precedence, ability to troubleshoot client-reported issues.
- **Follow-ups**: How would you change a profile at the site level via SQL? What is the impact of setting `MO: Operating Unit` vs using MOAC profiles? Explain `FND_GLOBAL` profile caching.

### Lab 03: Financials (GL, AP, AR)

#### Problem: Procure-to-Pay Process — Invoice Matching Exception
- **Difficulty/Frequency**: High / Very Frequent
- **Problem statement**: A client's AP team reports that 30% of invoices are failing match exception during month-end. They need a report that shows all unmatched invoices with PO information, receipt quantities, and the discrepancy amount. Write the SQL. Then design a process improvement recommendation.
- **Interview walkthrough**: Use `AP_INVOICES_ALL` joined with `AP_INVOICE_DISTRIBUTIONS_ALL` for distribution-level matching. Link to `PO_DISTRIBUTIONS_ALL` and `PO_HEADERS_ALL`. For receipt info, use `RCV_TRANSACTIONS` and `RCPT_SHIPMENT_HEADERS`. Match exception occurs when invoice quantity > received quantity or price > PO price (within tolerance).
- **SQL/PLSQL/Java solution**:
```sql
SELECT ai.invoice_id,
       ai.invoice_num,
       ai.invoice_amount,
       ai.invoice_date,
       pha.segment1 po_number,
       pha.po_header_id,
       aid.po_distribution_id,
       aid.amount invoice_dist_amount,
       NVL(pd.amount, 0) po_dist_amount,
       (aid.amount - NVL(pd.amount, 0)) discrepancy,
       NVL(rt.transaction_quantity, 0) received_qty,
       aid.quantity_invoiced - NVL(rt.transaction_quantity, 0) qty_discrepancy
  FROM ap_invoices_all ai,
       ap_invoice_distributions_all aid,
       po_distributions_all pd,
       po_headers_all pha,
       (SELECT rsh.shipment_header_id,
               rt.transaction_id,
               rt.quantity transaction_quantity,
               rt.po_distribution_id
          FROM rcv_transactions rt,
               rcv_shipment_headers rsh
         WHERE rt.shipment_header_id = rsh.shipment_header_id
           AND rt.transaction_type = 'RECEIVE') rt
 WHERE ai.invoice_id = aid.invoice_id
   AND aid.po_distribution_id = pd.po_distribution_id
   AND pd.po_header_id = pha.po_header_id
   AND aid.po_distribution_id = rt.po_distribution_id (+)
   AND ai.invoice_date BETWEEN '01-JAN-2024' AND '31-JAN-2024'
   AND (aid.amount - NVL(pd.amount, 0) <> 0
        OR aid.quantity_invoiced <> NVL(rt.transaction_quantity, 0))
 ORDER BY ai.creation_date;
```
- **EBS-specific context**: `AP_INVOICES_ALL`, `AP_INVOICE_DISTRIBUTIONS_ALL`, `PO_DISTRIBUTIONS_ALL`, `PO_HEADERS_ALL`, `RCV_TRANSACTIONS`, `RCPT_SHIPMENT_HEADERS`, match tolerance setup in `PO_APPROVED_DOC_LIMITS`.
- **What Deloitte evaluates**: Process understanding + technical querying. Can you diagnose a business problem and propose a fix? Consulting mindset.
- **Follow-ups**: How would you automate exception resolution? What setups affect match tolerance (organization level, supplier site level)? Explain 2-way vs 3-way matching.

#### Problem: AR Receipt Application — Unapplied Receipts
- **Difficulty/Frequency**: Medium / Frequent
- **Problem statement**: A client has $2M in unapplied receipts sitting in AR. Write a PL/SQL routine that automatically applies unapplied cash receipts to open invoices for a given customer, using receipt-on-account logic where exact matches exist.
- **Interview walkthrough**: Unapplied receipts are in `AR_RECEIVABLE_APPLICATIONS_ALL` where `status = 'UNAPP'` or `APPLIED_TO_RECEIVABLE_ID` is NULL. Use `AR_RECEIPT_METHODS` for receipt method. The auto-application logic matches by customer ID, amount, and invoice date. Use `AR_CASH_RECEIPT_HISTORY` for audit. Apply via `AR_CASH_RECEIPT_PKG` or direct insert into `AR_RECEIVABLE_APPLICATIONS_ALL`.
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE auto_apply_receipts (
  p_customer_id   IN NUMBER,
  p_batch_limit   IN NUMBER DEFAULT 100
) AS
  CURSOR unapplied_cr IS
    SELECT crh.cash_receipt_id,
           crh.amount receipt_amount,
           crh.receipt_date,
           crh.customer_id
      FROM ar_cash_receipts_all crh
     WHERE crh.customer_id = p_customer_id
       AND crh.status = 'UNAPP'
       AND crh.amount > 0
       AND ROWNUM <= p_batch_limit;

  CURSOR open_invoices (p_cust_id NUMBER, p_amount NUMBER) IS
    SELECT ra.customer_trx_id,
           ra.amount_due_original - ra.amount_due_remaining open_amount,
           ra.trx_number
      FROM ra_customer_trx_all ra
     WHERE ra.customer_id = p_cust_id
       AND ra.amount_due_remaining > 0
       AND ra.amount_due_remaining <= p_amount
       AND ra.status_trx = 'OP'
     ORDER BY ra.trx_date;

  PROCEDURE apply_receipt (
    p_receipt_id   NUMBER,
    p_trx_id       NUMBER,
    p_amount       NUMBER
  ) IS
  BEGIN
    INSERT INTO ar_receivable_applications_all
    (cash_receipt_id, customer_trx_id, amount_applied,
     status, application_date, created_by, creation_date)
    VALUES
    (p_receipt_id, p_trx_id, p_amount,
     'APP', SYSDATE, fnd_global.user_id, SYSDATE);
    COMMIT;
  END apply_receipt;
BEGIN
  FOR cr IN unapplied_cr LOOP
    FOR inv IN open_invoices(cr.customer_id, cr.receipt_amount) LOOP
      apply_receipt(cr.cash_receipt_id, inv.customer_trx_id, inv.open_amount);
      EXIT WHEN inv.open_amount >= cr.receipt_amount;
    END LOOP;
  END LOOP;
EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
    fnd_file.put_line(fnd_file.log, 'Auto-apply error: ' || SQLERRM);
    RAISE;
END auto_apply_receipts;
/
```
- **EBS-specific context**: `AR_CASH_RECEIPTS_ALL`, `AR_RECEIVABLE_APPLICATIONS_ALL`, `RA_CUSTOMER_TRX_ALL`, `AR_RECEIPT_METHODS`, `AR_CASH_RECEIPT_HISTORY`, receipt statuses.
- **What Deloitte evaluates**: Can you build a business automation? Receivables processing knowledge, PL/SQL proficiency, error handling.
- **Follow-ups**: How do you handle short payments vs overpayments? Explain receipt-on-account vs on-account credit memo application. What is the role of remittance banks?

### Lab 04: Supply Chain (INV, PO, OM)

#### Problem: Inventory Valuation Report — Period End
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: Write a concurrent program that generates an inventory valuation report at period end. Include item, subinventory, on-hand quantity at standard cost, and highlight items where actual cost differs from standard by more than 10%.
- **Interview walkthrough**: Use `MTL_ONHAND_QUANTITIES` for quantities, `MTL_SYSTEM_ITEMS_B` for item/cost info, `CST_ITEM_COSTS` for cost details. `MTL_MATERIAL_TRANSACTIONS` and `MTL_TRANSACTION_ACCOUNTS` for actual cost. Compare standard cost (`CST_ITEM_COSTS.cost_type_id = 1` for Standard) vs actual cost from recent transactions.
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE inv_valuation_report (
  p_organization_id  IN NUMBER,
  p_period_name      IN VARCHAR2
) AS
  CURSOR c_val IS
    SELECT msib.segment1 item_code,
           msib.description,
           msib.primary_uom_code uom,
           msib.organization_id,
           mp.organization_code,
           SUM(moq.transaction_quantity) on_hand_qty,
           MAX(cic.item_cost) standard_cost,
           SUM(moq.transaction_quantity) * MAX(cic.item_cost) valuation,
           CASE
             WHEN MAX(cic.item_cost) = 0 THEN 0
             ELSE ROUND(
               (AVG(mta.cost) - MAX(cic.item_cost))
               / MAX(cic.item_cost) * 100, 2
             )
           END cost_variance_pct
      FROM mtl_system_items_b msib,
           mtl_onhand_quantities moq,
           cst_item_costs cic,
           mtl_parameters mp,
           (SELECT mta.inventory_item_id,
                   mta.organization_id,
                   mta.actual_cost cost
              FROM mtl_transaction_accounts mta
             WHERE mta.transaction_date >= (
               SELECT MIN(period_start_date)
                 FROM gl_period_statuses
                WHERE period_name = p_period_name
                  AND application_id = 101  -- INV
             )) mta
     WHERE msib.inventory_item_id = moq.inventory_item_id
       AND msib.organization_id = moq.organization_id
       AND msib.inventory_item_id = cic.inventory_item_id (+)
       AND msib.organization_id = cic.organization_id (+)
       AND cic.cost_type_id (+) = 1  -- Standard cost
       AND msib.organization_id = mp.organization_id
       AND msib.inventory_item_id = mta.inventory_item_id (+)
       AND msib.organization_id = mta.organization_id (+)
       AND msib.organization_id = p_organization_id
       AND msib.inventory_asset_flag = 'Y'
     GROUP BY msib.segment1, msib.description, msib.primary_uom_code,
              msib.organization_id, mp.organization_code
     ORDER BY msib.segment1;

  l_line NUMBER := 0;
BEGIN
  fnd_file.put_line(fnd_file.output,
    'Item,Desc,UOM,Org,OnHandQty,StdCost,Valuation,CostVar%');
  FOR rec IN c_val LOOP
    fnd_file.put_line(fnd_file.output,
      rec.item_code || ',' || rec.description || ',' ||
      rec.uom || ',' || rec.organization_code || ',' ||
      TO_CHAR(rec.on_hand_qty) || ',' ||
      TO_CHAR(rec.standard_cost) || ',' ||
      TO_CHAR(rec.valuation) || ',' ||
      TO_CHAR(NVL(rec.cost_variance_pct, 0)));
    l_line := l_line + 1;
  END LOOP;
  fnd_file.put_line(fnd_file.output, 'Total lines: ' || l_line);
END inv_valuation_report;
/
```
- **EBS-specific context**: `MTL_ONHAND_QUANTITIES`, `MTL_SYSTEM_ITEMS_B`, `CST_ITEM_COSTS`, `MTL_MATERIAL_TRANSACTIONS`, `MTL_TRANSACTION_ACCOUNTS`, `MTL_PARAMETERS`, cost types (Standard, Average, FIFO).
- **What Deloitte evaluates**: Inventory module depth, cost accounting knowledge, ability to build a client-facing report.
- **Follow-ups**: Explain periodic vs perpetual inventory. How does EBS handle average cost vs standard cost? What are cost layers? How do you recalculate costs?

## EBS-Specific Deep Dive Questions

- **Implementation Methodology (AIM/OUM)**: Walk through the phases — Definition (MD.50), Operations Analysis (OA.60), System Design (BR.100/BR.120), Build & Test (TE.40/TE.50), Transition (TA.20). What are the key deliverables in each phase?
- **Client Management**: How do you handle scope creep during an EBS implementation? Describe a time you managed a difficult stakeholder insisting on a customization that would break the upgrade path.
- **Data Migration**: Strategies for migrating data from legacy systems to EBS. Use of `FND_LOAD` vs `AD_LOAD` vs SQL*Loader. Handling open item data conversion for AP and AR.
- **Testing Strategy**: Unit testing (TE.40), System Integration Testing (SIT), User Acceptance Testing (UAT). How many test cycles? How do you manage test data? Use of `FND_TEST` templates.
- **Cutover Planning**: The go-live sequence — what modules go first? When is the last data migration? Rollback strategy. Examples from real projects.
- **Customization vs Configuration**: When do you customize vs configure? What is the "Oracle hairball" problem? How do you keep customizations upgrade-safe?

## Behavioral Questions (STAR)

- **Situation**: Client demanded a custom workflow that would require extensive customization in AP invoice approval.
  - **Task**: Deliver within budget without compromising upgrade path.
  - **Action**: Used OOB (Out of Box) AP workflow with `AP_WF_APPROVAL` instead of customization. Configured approval groups, set up hierarchy rules in `FND_WF_NOTIFICATION`, and extended a single attribute to meet the unique requirement.
  - **Result**: Delivered at 60% of original estimate. Zero upgrade impact in R12.2 migration.

- **Situation**: Global rollout with 8 countries each with unique tax and regulatory requirements.
  - **Task**: Design a standardized EBS template that works across all countries.
  - **Action**: Created a core financials template with country-specific extensions using MOAC. Ledger per country, common chart of accounts with country-specific segments. Used `XLE_TAX_REGIME` configurations.
  - **Result**: Go-live in 6 months (vs 12-month estimate). Template reused for later rollouts.

- **Situation**: Go-live day — inventory transactions stopped processing.
  - **Task**: Diagnose and fix in < 2 hours.
  - **Action**: Identified `MTL_TRANSACTIONS_INTERFACE` stuck due to missing `INV_TXN_MANAGER` configuration. Reset the manager using `INVCONC` calls from backend, purged stuck interface records, and restarted the transaction manager.
  - **Result**: Transactions processed within 30 minutes. Go-live on schedule.

- **Situation**: Offshore development team delivered OAF pages with significant quality issues.
  - **Task**: Improve quality with no timeline extension.
  - **Action**: Set up code review checklists using OAF best practices, created automated PL/SQL validation scripts, and implemented weekly knowledge transfer sessions.
  - **Result**: Defect rate dropped from 40% to 8%. Team became self-sufficient.

- **Situation**: Client wanted to close books in 5 days but GL reconciliation took 3 days alone.
  - **Task**: Automate GL reconciliation.
  - **Action**: Built custom `GL_RECON` concurrent program using `GL_DAILY_BALANCES` and `AP_GL_TRANSACTIONS`. Automated suspense account clearance with `GL_SUSPENSE` hooks. Created dashboard in OAF.
  - **Result**: Reconciliation completed overnight. Books closed in 4 days.

## Study Plan

| Priority | Labs |
|----------|------|
| Must | Lab 01 (Architecture), Lab 02 (System Admin), Lab 03 (Financials), Lab 04 (Supply Chain) |
| Recommended | Lab 07 (Reporting), Lab 08 (Integrations), Lab 05 (HRMS) |
| Additional | Lab 06 (Customization/OAF), Lab 09 (Security) |
| Niche | Lab 10 (Upgrade/Migration) |

### Lab 05: HRMS

#### Problem: Employee Hire Process — Checklist Automation
- **Difficulty/Frequency**: Medium / Frequent
- **Problem statement**: A client needs an automated new hire checklist that creates the employee record in PER_ALL_PEOPLE_F, assigns the position, sets up supervisor hierarchy, creates the user account in FND_USER, and triggers the onboarding workflow. Write the PL/SQL procedure.
- **Interview walkthrough**: Use PER_ALL_PEOPLE_PKG.CREATE_EMPLOYEE for person record, PER_ALL_ASSIGNMENTS_F for position/assignment, PER_POSITIONS for position data. Link to FND_USER for the EBS login account. Use HR_GENERAL for date-effective tracking. The workflow can be initiated via WF_ENGINE.
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE auto_hire_employee (
  p_first_name       IN VARCHAR2,
  p_last_name        IN VARCHAR2,
  p_email            IN VARCHAR2,
  p_position_id      IN NUMBER,
  p_organization_id  IN NUMBER,
  p_effective_date   IN DATE,
  p_supervisor_id    IN NUMBER DEFAULT NULL
) AS
  l_person_id    NUMBER;
  l_assignment_id NUMBER;
  l_user_id      NUMBER;
BEGIN
  -- Create person
  l_person_id := per_all_people_pkg.create_employee(
    p_first_name       => p_first_name,
    p_last_name        => p_last_name,
    p_email_address    => p_email,
    p_effective_date   => p_effective_date,
    p_employee_number  => per_employee_number_s.NEXTVAL,
    p_organization_id  => p_organization_id
  );

  -- Create assignment
  l_assignment_id := per_all_assignments_pkg.create_assignment(
    p_person_id       => l_person_id,
    p_effective_date  => p_effective_date,
    p_position_id     => p_position_id,
    p_supervisor_id   => p_supervisor_id
  );

  -- Create EBS user
  l_user_id := fnd_user_pkg.createuser(
    x_user_name    => SUBSTR(p_first_name, 1, 1) || p_last_name,
    x_password     => 'TempPassword123!',
    x_employee_id  => l_person_id,
    x_description  => p_first_name || ' ' || p_last_name
  );

  -- Launch onboarding workflow
  wf_engine.start_process(
    item_type => 'HRONBOARD',
    item_key  => 'HR_HIRE_' || l_person_id
  );

  COMMIT;
EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
    fnd_file.put_line(fnd_file.log,
      'Hire failed: ' || SQLERRM);
    RAISE;
END auto_hire_employee;
/
```
- **EBS-specific context**: PER_ALL_PEOPLE_F, PER_ALL_ASSIGNMENTS_F, PER_POSITIONS, PER_ALL_PEOPLE_PKG, PER_ALL_ASSIGNMENTS_PKG, FND_USER_PKG, WF_ENGINE, HR_GENERAL.
- **What Deloitte evaluates**: HRMS configuration expertise, cross-module integration (HR to FND to Workflow), understanding of date-effective model.
- **Follow-ups**: How do you handle back-dated hires? Explain the HR date-effective model. How does supervisor hierarchy work in EBS? What is the difference between PER_ALL_PEOPLE_F and PER_ALL_PEOPLE_F?

### Lab 07: Reporting (BI Publisher, XML)

#### Problem: Financial Dashboard with Multi-Currency
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: A global client needs a BI Publisher report showing financial KPIs (Revenue, Expenses, Profit) by operating unit in both local and reporting currency. Write the data model with currency translation logic and explain the BI Publisher layout approach.
- **Interview walkthrough**: Use GL_DAILY_BALANCES for amounts in functional currency. For reporting currency, use GL_DAILY_TRANSLATED_RATES or GL_DAILY_BALANCES with translated_flag. Join with FND_CURRENCIES for currency codes. Use BI Publisher with two currency columns. For the layout, use RTF template with conditional formatting based on currency.
- **SQL/PLSQL/Java solution**:
```sql
SELECT hou.name operating_unit,
       gl.period_name,
       gl.ledger_id,
       gcc.segment1 natural_account,
       gcc.account_type,
       flv.description account_type_name,
       gl.currency_code,
       SUM(NVL(gl.period_net_dr, 0)) period_dr_func,
       SUM(NVL(gl.period_net_cr, 0)) period_cr_func,
       SUM(NVL(gl.period_net_dr, 0) - NVL(gl.period_net_cr, 0)) period_net_func,
       gdr.conversion_rate,
       (SUM(NVL(gl.period_net_dr, 0) - NVL(gl.period_net_cr, 0))
        * NVL(gdr.conversion_rate, 1)) period_net_reporting
  FROM gl_balances gl,
       gl_code_combinations gcc,
       fnd_lookup_values_vl flv,
       hr_operating_units hou,
       gl_translation_rates gdr,
       gl_sets_of_books gsob
 WHERE gl.code_combination_id = gcc.code_combination_id
   AND flv.lookup_type = 'ACCOUNT_TYPE'
   AND flv.lookup_code = gcc.account_type
   AND gl.ledger_id = hou.set_of_books_id
   AND gl.ledger_id = gsob.set_of_books_id
   AND gdr.from_currency = gl.currency_code
   AND gdr.to_currency = 'USD'
   AND gdr.period_name = gl.period_name
   AND gl.actual_flag = 'A'
   AND gl.period_name = :p_period
 GROUP BY hou.name, gl.period_name, gl.ledger_id,
          gcc.segment1, gcc.account_type,
          flv.description, gl.currency_code, gdr.conversion_rate;
```
- **EBS-specific context**: GL_BALANCES, GL_TRANSLATION_RATES, GL_CODE_COMBINATIONS, FND_CURRENCIES, GL_SETS_OF_BOOKS, translation and revaluation rules.
- **What Deloitte evaluates**: Multi-currency financial reporting, BI Publisher design, understanding of global consolidation requirements.
- **Follow-ups**: How does EBS handle currency translation in R12? Explain the difference between revaluation and translation. How do you report on translated balances in GL_DAILY_BALANCES?

## Tips

- **Consulting mindset**: Deloitte interviews focus heavily on client delivery stories. Frame every answer with "the client needed… so I… which resulted in…"
- **Configuration over customization**: Always propose OOB solutions first. If customization is unavoidable, wrap it with a "safe" pattern using `CUSTOM_` prefix and `FND_LOAD` hooks.
- **AIM methodology**: Memorize the AIM deliverables — ask about BR.100 (Business Requirement), MD.50 (Module Design), TE.40 (Test Script), CV.40 (Conversion). This distinguishes you from pure developers.
- **Industry experience**: If you have manufacturing, retail, or public sector experience, emphasize it. Deloitte services all verticals.
- **Data migration**: Be ready to talk about conversion strategies — open AP items, open AR balances, inventory opening balances. Use of `AP_INVOICES_INTERFACE` and `MTL_TRANSACTIONS_INTERFACE`.
- **Soft skills**: Deloitte values teamwork and client management equally with technical skill. Prepare stories about difficult client situations, cross-team collaboration, and mentoring.
