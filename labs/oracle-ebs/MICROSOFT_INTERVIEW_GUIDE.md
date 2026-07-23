# Microsoft Interview Guide — Oracle EBS Academy

## Interview Process for EBS Roles

### Rounds
- **Round 1 – Phone Screen (45 min)**: Technical screening with a senior engineer. EBS architecture, PL/SQL, and data modeling. Expect a "live coding" problem — write a query on a shared screen.
- **Round 2 – Technical (1 hr)**: Two sub-rounds back-to-back: (a) PL/SQL coding — complex report or batch processing routine. (b) Design — EBS integration with Azure, data migration strategy, handling large-scale EBS deployments.
- **Round 3 – System Design (1 hr)**: Design a fault-tolerant integration between EBS and Dynamics 365 or Azure Data Lake. Must handle 99.9% uptime, async processing, and monitoring.
- **Round 4 – "AS Appropriate" (45 min)**: Manager or partner level. Growth mindset, customer obsession, handling ambiguity.
- **Timeline**: 2–4 weeks.

### EBS-Specific Expectations
- Microsoft has a hybrid on-prem/Azure strategy for EBS. Knowing Azure SQL DB, Azure Integration Services (Logic Apps, Service Bus), and Azure Data Factory for ETL is a differentiator.
- Microsoft values growth mindset — be ready to discuss a time you learned a new EBS module quickly.
- EBS experience integrated with Microsoft stack (Power BI for reporting, Power Automate for workflows, Azure AD for SSO) is highly valued.
- Certifications: Microsoft certs (Azure, Power Platform) are valued alongside OCP.

## Top Technical Problems by Lab

### Lab 01: EBS Architecture and Fundamentals

#### Problem: EBS Data Feed to Azure Data Lake
- **Difficulty/Frequency**: High / Very Frequent
- **Problem statement**: Design and implement an incremental data extraction pipeline from EBS to Azure Data Lake. The pipeline must extract GL balances, invoices, and purchase orders daily. Write the PL/SQL extraction component and describe the Azure integration.
- **Interview walkthrough**: Use materialized views or staging tables in EBS with a last-updated timestamp. Create a concurrent program that extracts changed records since last run. Write to a file on the application tier or directly to an Azure Blob via `UTL_FILE` or `BFILE`. For Azure side, use Azure Data Factory with a Copy Activity that reads from the EBS staging table via a gateway. For large tables, use watermark columns (last_update_date).
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE extract_ebs_incremental (
  p_extract_date     DATE,
  p_extract_type     VARCHAR2  -- 'GL', 'AP', 'PO'
) AS
  l_output  UTL_FILE.FILE_TYPE;
  l_filename VARCHAR2(100);
BEGIN
  l_filename := 'EXTRACT_' || p_extract_type || '_' ||
                TO_CHAR(p_extract_date, 'YYYYMMDD') || '.csv';
  l_output := UTL_FILE.FOPEN('EXTRACT_DIR', l_filename, 'W', 32767);

  IF p_extract_type = 'GL' THEN
    UTL_FILE.PUT_LINE(l_output,
      'LEDGER_ID,PERIOD_NAME,ACCOUNT,ACTUAL_FLAG,PERIOD_NET_DR,PERIOD_NET_CR,LAST_UPDATE_DATE');
    FOR rec IN (
      SELECT gls.ledger_id, gls.period_name,
             gcc.segment1 account,
             gls.actual_flag,
             gls.period_net_dr, gls.period_net_cr,
             gls.last_update_date
        FROM gl_balances gls,
             gl_code_combinations gcc
       WHERE gls.code_combination_id = gcc.code_combination_id
         AND gls.last_update_date >= p_extract_date - 1
         AND gls.last_update_date < p_extract_date
    ) LOOP
      UTL_FILE.PUT_LINE(l_output,
        rec.ledger_id || ',' || rec.period_name || ',' ||
        rec.account || ',' || rec.actual_flag || ',' ||
        TO_CHAR(rec.period_net_dr) || ',' ||
        TO_CHAR(rec.period_net_cr) || ',' ||
        TO_CHAR(rec.last_update_date, 'YYYY-MM-DD HH24:MI:SS'));
    END LOOP;
  ELSIF p_extract_type = 'AP' THEN
    FOR rec IN (
      SELECT ai.invoice_id, ai.invoice_num,
             ai.invoice_amount, ai.invoice_date,
             ai.last_update_date
        FROM ap_invoices_all ai
       WHERE ai.last_update_date >= p_extract_date - 1
         AND ai.last_update_date < p_extract_date
    ) LOOP
      UTL_FILE.PUT_LINE(l_output,
        rec.invoice_id || ',' || rec.invoice_num || ',' ||
        TO_CHAR(rec.invoice_amount) || ',' ||
        TO_CHAR(rec.invoice_date, 'YYYY-MM-DD') || ',' ||
        TO_CHAR(rec.last_update_date, 'YYYY-MM-DD HH24:MI:SS'));
    END LOOP;
  END IF;

  UTL_FILE.FCLOSE(l_output);

  -- Log extraction
  INSERT INTO custom_extract_log
  (extract_type, extract_date, filename, rows_extracted, created_date)
  VALUES (p_extract_type, p_extract_date, l_filename,
          (SELECT COUNT(*) FROM ...), SYSDATE);
  COMMIT;
END extract_ebs_incremental;
/
```
- **EBS-specific context**: `GL_BALANCES`, `GL_CODE_COMBINATIONS`, `AP_INVOICES_ALL`, `PO_HEADERS_ALL`, `UTL_FILE`, database directories, materialized views, `FND_FILE`.
- **What Microsoft evaluates**: Data engineering thinking, Azure awareness, PL/SQL with file handling, incremental extraction pattern.
- **Follow-ups**: How would you handle CDC (Change Data Capture) for EBS tables without last_update_date? How does Azure Data Factory connect to on-prem EBS? Explain the gateway vs managed VNet approach.

#### Problem: Azure AD SSO Integration with EBS
- **Difficulty/Frequency**: Medium / Frequent
- **Problem statement**: Your client wants to enable Single Sign-On (SSO) for EBS using Azure Active Directory. Describe the architecture — how would you integrate Azure AD as the identity provider for EBS login? What changes are required in `FND_USER` and the login flow?
- **Interview walkthrough**: EBS supports SSO via Oracle Access Manager (OAM) integration. For Azure AD, use SAML 2.0 or OIDC. The architecture: Azure AD → SAML Assertion → OAM (or custom login servlet) → EBS session. The user record must exist in `FND_USER` with a matching email or UPN. The SSO handler validates the SAML token, looks up `FND_USER`, and creates an `ICX_SESSIONS` record. Password not required — EBS bypasses password hash verification.
- **SQL/PLSQL/Java solution**:
```sql
-- Create SSO user mapping table
CREATE TABLE custom_sso_user_map (
  azure_upn         VARCHAR2(200) PRIMARY KEY,
  fnd_user_id       NUMBER NOT NULL,
  fnd_user_name     VARCHAR2(100),
  email_address     VARCHAR2(240),
  enabled_flag      VARCHAR2(1) DEFAULT 'Y',
  last_sso_login    DATE,
  created_by        NUMBER,
  creation_date     DATE DEFAULT SYSDATE,
  CONSTRAINT fk_fnd_user FOREIGN KEY (fnd_user_id)
    REFERENCES fnd_user (user_id)
);

-- Query to check user and create SSO session
CREATE OR REPLACE FUNCTION sso_validate_user (
  p_azure_upn  VARCHAR2
) RETURN NUMBER IS
  l_user_id  NUMBER;
  l_count    NUMBER;
BEGIN
  SELECT COUNT(*)
    INTO l_count
    FROM custom_sso_user_map mp
   WHERE mp.azure_upn = p_azure_upn
     AND mp.enabled_flag = 'Y';

  IF l_count = 0 THEN
    -- Auto-provision if Azure UPN matches FND_USER email
    SELECT user_id INTO l_user_id
      FROM fnd_user
     WHERE email_address = p_azure_upn
       AND end_date IS NULL;

    INSERT INTO custom_sso_user_map
    (azure_upn, fnd_user_id, fnd_user_name,
     email_address, enabled_flag, created_by)
    VALUES (p_azure_upn, l_user_id,
            (SELECT user_name FROM fnd_user WHERE user_id = l_user_id),
            p_azure_upn, 'Y', l_user_id);
  ELSE
    SELECT fnd_user_id INTO l_user_id
      FROM custom_sso_user_map
     WHERE azure_upn = p_azure_upn;
  END IF;

  UPDATE custom_sso_user_map
     SET last_sso_login = SYSDATE
   WHERE azure_upn = p_azure_upn;

  COMMIT;
  RETURN l_user_id;
END sso_validate_user;
/
```
- **EBS-specific context**: `FND_USER`, `ICX_SESSIONS`, `FND_LOGIN_RESP_FORMS`, `FND_SIGNON`, `FND_WEB_SEC`, `FND_SSO`, Oracle Access Manager (OAM) integration.
- **What Microsoft evaluates**: Azure AD knowledge + EBS security understanding. Hybrid identity architecture thinking.
- **Follow-ups**: How does EBS handle session timeouts with SSO? Explain SAML bearer flow vs artifact binding. How do you handle user deprovisioning when an employee leaves?

### Lab 07: Reporting (BI Publisher, XML)

#### Problem: BI Publisher Report — P&L Statement with Drill-Down
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: Create a BI Publisher report that generates a Profit & Loss statement by ledger and period. Include drill-down from natural account to individual transactions. Write the data model (SQL) and the XML template structure.
- **Interview walkthrough**: Data model uses `GL_BALANCES` with `GL_CODE_COMBINATIONS` for account hierarchy. For drill-down, use event triggers to call a separate data template. Use `XDO:LEVEL_BREAK` for grouping by account type (Revenue, Expense). For drilling, use `BI Publisher Bursting` to generate sub-reports by account.
- **SQL/PLSQL/Java solution**:
```sql
-- Main P&L data model
SELECT gcc.segment1 natural_account,
       gcc.segment2 department,
       gcc.account_type,
       flv.description account_type_desc,
       gls.period_name,
       gls.ledger_id,
       gls.currency_code,
       SUM(NVL(gls.begin_balance_dr, 0) -
           NVL(gls.begin_balance_cr, 0)) opening_balance,
       SUM(NVL(gls.period_net_dr, 0)) period_debits,
       SUM(NVL(gls.period_net_cr, 0)) period_credits,
       SUM(NVL(gls.begin_balance_dr, 0) -
           NVL(gls.begin_balance_cr, 0) +
           NVL(gls.period_net_dr, 0) -
           NVL(gls.period_net_cr, 0)) closing_balance
  FROM gl_balances gls,
       gl_code_combinations gcc,
       fnd_lookup_values_vl flv
 WHERE gls.code_combination_id = gcc.code_combination_id
   AND flv.lookup_type = 'ACCOUNT_TYPE'
   AND flv.lookup_code = gcc.account_type
   AND gls.ledger_id = :p_ledger_id
   AND gls.period_name = :p_period_name
   AND gls.actual_flag = 'A'
   AND gcc.account_type IN ('R', 'E')  -- Revenue, Expense
 GROUP BY gcc.segment1, gcc.segment2, gcc.account_type,
          flv.description, gls.period_name, gls.ledger_id, gls.currency_code
 ORDER BY gcc.account_type, gcc.segment1;

-- Drill-down: account details
SELECT gjh.je_header_id,
       gjh.name journal_name,
       gjh.doc_sequence_value journal_num,
       gjl.je_line_num,
       gcc.segment1 natural_account,
       gcc.segment2 department,
       gjl.description line_description,
       gjl.entered_dr,
       gjl.entered_cr,
       gjl.accounted_dr,
       gjl.accounted_cr
  FROM gl_je_headers gjh,
       gl_je_lines gjl,
       gl_code_combinations gcc
 WHERE gjh.je_header_id = gjl.je_header_id
   AND gjl.code_combination_id = gcc.code_combination_id
   AND gjh.ledger_id = :p_ledger_id
   AND gjh.period_name = :p_period_name
   AND gcc.segment1 = :p_account
 ORDER BY gjh.doc_sequence_value, gjl.je_line_num;
```
- **BI Publisher XML template**:
```xml
<?xml version="1.0" encoding="utf-8"?>
<report xmlns="http://xmlns.oracle.com/oxp/xml">
  <dataTemplate name="PNL_MAIN">
    <dataQuery>
      <sqlStatement>
        <!-- Main P&L query above -->
      </sqlStatement>
    </dataQuery>
    <dataStructure>
      <group name="G_ACCOUNT_TYPE" source="dataQuery">
        <element name="ACCOUNT_TYPE_DESC" value="account_type_desc"/>
        <group name="G_ACCOUNT" source="dataQuery">
          <element name="NATURAL_ACCOUNT" value="natural_account"/>
          <element name="OPENING_BALANCE" value="opening_balance"/>
          <element name="PERIOD_DEBITS" value="period_debits"/>
          <element name="PERIOD_CREDITS" value="period_credits"/>
          <element name="CLOSING_BALANCE" value="closing_balance"/>
        </group>
      </group>
    </dataStructure>
  </dataTemplate>
</report>
```
- **EBS-specific context**: `GL_BALANCES`, `GL_CODE_COMBINATIONS`, `GL_JE_HEADERS`, `GL_JE_LINES`, `FND_LOOKUP_VALUES_VL`, `XDO_DS` definitions, BI Publisher data templates, bursting.
- **What Microsoft evaluates**: Reporting depth, BI Publisher + SQL skills, ability to create user-facing analytical reports.
- **Follow-ups**: How does BI Publisher connect to EBS? Explain `XDO_DS` and `XDO_TEMPLATES` tables. How do you parameterize reports with LOV based on value sets? How does bursting work in EBS BI Publisher?

### Lab 08: Integrations

#### Problem: Azure Logic App — EBS Invoice Approval
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: Design an integration where an AP invoice approval notification is sent via email using Azure Logic Apps. When the approver responds (Approved/Rejected), the result must be written back to EBS workflow. Provide the PL/SQL for the EBS side and the integration pattern.
- **Interview walkthrough**: EBS workflow sends notification via Workflow Mailer. For Azure-based approval, configure the EBS Business Event System (BES) to publish an XML message to an Azure Service Bus queue. Azure Logic App picks up the message, constructs an approval email, waits for response, and sends the decision back to an EBS PL/SQL API (via REST or `FND_WF_PKG`).
- **SQL/PLSQL/Java solution**:
```sql
-- EBS side: Receive approval from Azure
CREATE OR REPLACE PROCEDURE receive_approval (
  p_invoice_id    IN NUMBER,
  p_approver      IN VARCHAR2,
  p_decision      IN VARCHAR2,  -- 'APPROVED', 'REJECTED'
  p_comments      IN VARCHAR2 DEFAULT NULL,
  p_message_id    IN VARCHAR2
) AS
  l_item_key      VARCHAR2(30);
  l_activity      VARCHAR2(30);
BEGIN
  -- Dedup check
  INSERT INTO custom_approval_log
  (message_id, invoice_id, decision, comments, received_date)
  VALUES (p_message_id, p_invoice_id, p_decision,
          p_comments, SYSDATE);

  -- Complete workflow activity
  SELECT item_key INTO l_item_key
    FROM wf_items
   WHERE item_type = 'APINV'
     AND row_id = p_invoice_id;

  IF p_decision = 'APPROVED' THEN
    l_activity := 'APPROVE';
  ELSE
    l_activity := 'REJECT';
  END IF;

  wf_engine.complete_activity(
    itemtype => 'APINV',
    itemkey  => l_item_key,
    actid    => l_activity,
    result   => p_decision
  );

  -- Update invoice approval status
  UPDATE ap_invoices_all
     SET attribute_category = 'AZURE_APPR',
         attribute1 = p_decision,
         attribute2 = p_approver,
         attribute3 = TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS')
   WHERE invoice_id = p_invoice_id;

  COMMIT;
END receive_approval;
/
```
- **EBS-specific context**: `WF_ENGINE`, `WF_ITEMS`, `WF_ACTIVITIES`, `AP_INVOICES_ALL`, Workflow Mailer, Business Event System (`FND_EVENT_QUEUE_PKG`), `FND_WF_PKG`.
- **What Microsoft evaluates**: Azure integration services knowledge (Logic Apps, Service Bus), EBS workflow extensibility, event-driven architecture.
- **Follow-ups**: How does the Workflow Mailer work? How do you create a Business Event subscription? How do you handle failed workflow notifications? Explain event key delivery vs guaranteed delivery.

## EBS-Specific Deep Dive Questions

- **EBS on Azure**: How does EBS run on Azure VMs? What are the key considerations? Storage account for concurrent manager logs, VNet peering for on-prem connectivity, Azure SQL Database vs SQL Server on VM for EBS.
- **Power BI + EBS**: How do you connect Power BI to EBS data? DirectQuery from Power BI to EBS database (risky — impacts performance). Better approach: Extract to Azure SQL DB or Synapse via ADF, then Power BI connects to that. Use of gateway and SQL authentication.
- **Power Automate + EBS**: How would you trigger a Power Automate flow when an EBS workflow notification is raised? Pattern: EBS Business Event → Azure Service Bus → Power Automate → Teams notification/email approval.
- **Growth Mindset**: Microsoft asks "tell me about a time you learned a new technology." For EBS, describe learning a new module (e.g., picked up Oracle HRMS in 2 weeks to handle payroll implementation).
- **Customer Obsession**: "A customer's EBS system is down during month-end close. What do you do?" — Own the outcome, communicate relentlessly, drive the fix.
- **Data Residency**: Microsoft works with global enterprises. How would you handle EBS data residency requirements across regions?

## Behavioral Questions (STAR)

- **Situation**: Customer's EBS system crashed during month-end close due to tablespace full in `APPS_TS_TX_DATA`.
  - **Task**: Restore service immediately and prevent recurrence.
  - **Action**: Added datafile to the tablespace using `ALTER TABLESPACE APPS_TS_TX_DATA ADD DATAFILE`. Created an automated tablespace monitoring concurrent program using `DBA_TABLESPACE_USAGE_METRICS` that alerts when > 85% full.
  - **Result**: Service restored in 12 minutes. Zero recurrence in 2 years.

- **Situation**: Business requested a complex Power BI dashboard for EBS financials but had no data warehouse.
  - **Task**: Deliver dashboard within 4 weeks.
  - **Action**: Created nightly extract of EBS financial data (GL, AP, AR) to Azure SQL Database using ADF. Built Power BI semantic model with fiscal calendar, account hierarchy. Used incremental refresh policies.
  - **Result**: Dashboard delivered in 3 weeks. CFO uses it daily. 40+ users.

- **Situation**: EBS custom OAF page for time entry was too slow for 500 concurrent users.
  - **Task**: Fix performance at application level.
  - **Action**: Profiled BC4J application module — found `ViewObject` fetching 50K rows unnecessarily. Set forward-only mode, added bind parameters, implemented server-side pagination. Reduced VO query from 5 joins to 2 by denormalizing.
  - **Result**: Page response improved from 25 seconds to 1.2 seconds.

- **Situation**: Cross-functional team needed to integrate EBS with Dynamics 365 Finance.
  - **Task**: Design real-time bi-directional sync for customer master data.
  - **Action**: Used Azure Service Bus for async messaging. EBS published customer changes via `BUS_EVENT_PUB_PKG` to Service Bus Topic. Dynamics 365 subscribed via Logic Apps. Conflict resolution via last-writer-wins with audit table.
  - **Result**: 99.9% sync reliability. Latency under 30 seconds.

- **Situation**: Annual security audit found EBS passwords failing complexity requirements.
  - **Task**: Remediate without disrupting users.
  - **Action**: Created `CUSTOM_PASSWORD_POLICY` function in PL/SQL that validates passwords against Azure AD password protection rules. Integrated with `FND_SIGNON.FORM_LOGIN` to enforce on next login. Emailed users with 30-day grace period.
  - **Result**: 100% compliance in next audit. Zero user complaints.

## Study Plan

| Priority | Labs |
|----------|------|
| Must | Lab 01 (Architecture), Lab 07 (Reporting), Lab 08 (Integrations), Lab 09 (Security) |
| Recommended | Lab 06 (Customization/OAF), Lab 03 (Financials), Lab 10 (Upgrade/Migration) |
| Additional | Lab 02 (System Admin), Lab 04 (Supply Chain) |
| Niche | Lab 05 (HRMS) |

### Lab 04: Supply Chain (INV, PO, OM)

#### Problem: Azure Data Factory Pipeline — EBS Inventory Snapshot
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: Design and implement a daily inventory snapshot pipeline from EBS to Azure SQL Database using Azure Data Factory. The PL/SQL component must create a daily fact table (daily on-hand by item and subinventory) that ADF will copy. Write the snapshot creation and the ADF pipeline configuration overview.
- **Interview walkthrough**: Create a materialized view or PL/SQL procedure that populates a custom INV_DAILY_SNAPSHOT table with on-hand quantities from MTL_ONHAND_QUANTITIES, item attributes from MTL_SYSTEM_ITEMS_B, and subinventory info. Use ADF with a Lookup + Copy Data activity to copy from EBS DB (via self-hosted IR) to Azure SQL DB. Use watermark column (snapshot_date) for incremental loading.
- **SQL/PLSQL/Java solution**:
```sql
CREATE TABLE custom_inv_daily_snapshot (
  snapshot_date         DATE NOT NULL,
  inventory_item_id     NUMBER NOT NULL,
  organization_id       NUMBER NOT NULL,
  subinventory_code     VARCHAR2(10),
  item_code             VARCHAR2(40),
  item_description      VARCHAR2(240),
  uom_code              VARCHAR2(3),
  onhand_quantity       NUMBER,
  reserved_quantity     NUMBER,
  available_quantity    NUMBER,
  last_txn_date         DATE,
  created_date          DATE DEFAULT SYSDATE,
  CONSTRAINT inv_snap_pk PRIMARY KEY (snapshot_date,
                                       inventory_item_id,
                                       organization_id,
                                       subinventory_code)
);

CREATE OR REPLACE PROCEDURE refresh_inv_snapshot AS
  l_snap_date DATE := TRUNC(SYSDATE);
BEGIN
  -- Clear today's snapshot (idempotent)
  DELETE FROM custom_inv_daily_snapshot
   WHERE snapshot_date = l_snap_date;

  -- Insert current on-hand snapshot
  INSERT INTO custom_inv_daily_snapshot
    (snapshot_date, inventory_item_id, organization_id,
     subinventory_code, item_code, item_description,
     uom_code, onhand_quantity, reserved_quantity,
     available_quantity, last_txn_date)
  SELECT l_snap_date,
         msib.inventory_item_id,
         moq.organization_id,
         moq.subinventory_code,
         msib.segment1 item_code,
         msib.description item_description,
         msib.primary_uom_code uom_code,
         SUM(moq.primary_transaction_quantity) onhand_qty,
         NVL((SELECT SUM(primary_transaction_quantity)
                FROM mtl_reservations mr
               WHERE mr.inventory_item_id = moq.inventory_item_id
                 AND mr.organization_id = moq.organization_id
                 AND mr.subinventory_code = moq.subinventory_code), 0) reserved_qty,
         SUM(moq.primary_transaction_quantity) -
         NVL((SELECT SUM(primary_transaction_quantity)
                FROM mtl_reservations mr
               WHERE mr.inventory_item_id = moq.inventory_item_id
                 AND mr.organization_id = moq.organization_id
                 AND mr.subinventory_code = moq.subinventory_code), 0) available_qty,
         (SELECT MAX(mmt.transaction_date)
            FROM mtl_material_transactions mmt
           WHERE mmt.inventory_item_id = moq.inventory_item_id
             AND mmt.organization_id = moq.organization_id) last_txn_date
    FROM mtl_onhand_quantities moq,
         mtl_system_items_b msib
   WHERE moq.inventory_item_id = msib.inventory_item_id
     AND moq.organization_id = msib.organization_id
     AND msib.inventory_asset_flag = 'Y'
   GROUP BY msib.segment1, msib.description,
            msib.primary_uom_code, msib.inventory_item_id,
            moq.organization_id, moq.subinventory_code;

  COMMIT;
  fnd_file.put_line(fnd_file.log,
    'Snapshot refreshed: ' || TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'));
END refresh_inv_snapshot;
/

-- ADF pipeline overview:
-- 1. Trigger: Scheduled daily at 2:00 AM
-- 2. Lookup: Get last refresh timestamp from control table
-- 3. PL/SQL execution: Execute refresh_inv_snapshot via Oracle function
-- 4. Copy Data: Source = EBS DB (SELECT * FROM custom_inv_daily_snapshot
--               WHERE snapshot_date = @pipeline().TriggerTime)
--               Sink = Azure SQL DB (upsert on snapshot_date + item + org)
-- 5. Log: Write execution status to ADF run table
```
- **EBS-specific context**: MTL_ONHAND_QUANTITIES, MTL_SYSTEM_ITEMS_B, MTL_RESERVATIONS, MTL_MATERIAL_TRANSACTIONS, ADF self-hosted IR, Azure SQL DB, incremental loading.
- **What Microsoft evaluates**: End-to-end data pipeline design, EBS + Azure integration, data warehousing concepts, idempotent snapshot logic.
- **Follow-ups**: How do you handle incremental loads for EBS data that has hard deletes? Explain ADF mapping data flows vs copy activity for EBS data. How do you monitor pipeline failures and alert?

## Tips

- **Microsoft stack awareness**: Even for EBS roles, Microsoft expects you to know Azure fundamentals. Azure SQL DB, ADF, Logic Apps, Power BI, Azure AD — these are differentiators.
- **Growth mindset stories**: Prepare a story about learning a new module or technology quickly. "I didn't know Oracle HRMS but after 2 weeks of reading docs and accessing `HR_API` tables, I configured the payroll."
- **Customer focus**: Microsoft asks "how do you prioritize features for the customer?" Frame your answer around customer impact — revenue, compliance, user productivity.
- **Power BI + EBS**: Be ready to discuss Power BI integration with EBS. Know DirectQuery limitations (no write-backs, performance impact).
- **Azure hybrid story**: EBS on Azure is a real scenario at Microsoft. Discuss how you'd manage hybrid identities, hybrid connectivity, and hybrid licensing.
- **Certifications**: Microsoft values Azure certs (AZ-900, DP-900) alongside OCP for hybrid roles.
