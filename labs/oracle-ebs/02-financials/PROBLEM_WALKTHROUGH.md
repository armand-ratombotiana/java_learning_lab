# Problem Walkthrough: GL to Subledger Accounting Flow with Reconciliation

## Problem Statement

**Design and implement a complete General Ledger to Subledger accounting flow with automated reconciliation for a multinational manufacturing company running Oracle EBS R12.2 Financials.**

The client processes 200,000+ subledger transactions per month across AP, AR, and FA modules. During month-end close, the finance team manually reconciles subledger balances to GL balances using spreadsheets — a process that takes 5 full-time accountants 8 days. The CFO demands a 3-day close with automated reconciliation and drill-down from GL balance to source subledger transaction.

### Business Requirements
- Reduce month-end close from 8 days to 3 days
- Zero unreconciled differences between subledgers and GL
- Drill-down from GL journal line to source subledger transaction (AP invoice, AR receipt, FA addition)
- Support multi-currency (USD, EUR, JPY, GBP) with automated FX revaluation
- Compliance with SOX controls — audit trail for every accounting entry
- Real-time visibility into period close status via dashboard

### Technical Constraints
- Oracle EBS R12.2 Financials (AP, AR, GL, FA, CM)
- SLA (Subledger Accounting) must be the single accounting engine
- 50 legal entities across 12 countries
- 3,000+ natural account segments in the chart of accounts
- Period close must complete within 72 hours of period end

---

## Solution Architecture

### Step 1: Understand the Accounting Flow

```
Subledger Transaction
       │
       ▼
┌──────────────────┐
│   SLA Engine      │
│ (XLA tables)      │
│ - Event Model     │
│ - Accounting Rep  │
│ - Journal Lines   │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ GL_INTERFACE     │
│ (GL_I_LINES)     │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ GL_POST          │
│ (GL_JE_HEADERS   │
│  GL_JE_LINES)    │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ GL_BALANCES      │
│ Period Net DR/CR │
└────────┬─────────┘
         │
         ▼
   Reconciliation
   (SLA ↔ GL)
```

### Step 2: Configure Subledger Accounting (SLA)

SLA is the central accounting engine. Every subledger transaction generates accounting entries through SLA event model:

```sql
-- Query SLA accounting methods configured
SELECT lam.accounting_method_code,
       lam.accounting_method_name,
       lam.accounting_method_type,
       lam.enabled_flag,
       laam.application_id
FROM   xla_accounting_methods_b lam,
       xla_applicable_accounting_methods laam
WHERE  lam.accounting_method_id = laam.accounting_method_id
AND    lam.enabled_flag = 'Y';

-- Create a custom accounting method for intercompany
BEGIN
  XLA_ACCOUNTING_METHOD_PUB_PKG.CREATE_ACCOUNTING_METHOD(
    P_ACCOUNTING_METHOD_CODE   => 'GLOBAL_IC_ACCT',
    P_ACCOUNTING_METHOD_NAME   => 'Global Intercompany Accounting',
    P_ACCOUNTING_METHOD_TYPE   => 'E',
    P_APPLICATION_ID           => 200,  -- AP Application
    P_ENABLED_FLAG             => 'Y'
  );
END;
/
```

### Step 3: Configure Journal Line Rules

Define how subledger transactions map to GL accounts:

```sql
-- Define journal line type for AP Invoice accrual
BEGIN
  XLA_JOURNAL_LINES_TYPE_PUB_PKG.CREATE_LINE_TYPE(
    P_LINE_TYPE_CODE      => 'AP_ACCRUAL',
    P_LINE_TYPE_NAME      => 'AP Invoice Accrual',
    P_APPLICATION_ID      => 200,
    P_ACCOUNTING_METHOD_CODE => 'STANDARD_ACCRUAL',
    P_LINE_DEFINITION_CODE   => 'AP_ACCRUAL_LINE',
    P_ENABLED_FLAG        => 'Y',
    P_ACCOUNTING_FLAG     => 'DR',
    P_DESCRIPTION         => 'Standard AP Invoice accrual line type'
  );
END;
/

-- Define accounting attribute mapping
-- Map AP invoice header to GL segment values
BEGIN
  XLA_ACCT_ATTR_MAPPINGS_PKG.CREATE_MAPPING(
    P_MAPPING_CODE        => 'AP_SEG1_COMPANY',
    P_APPLICATION_ID      => 200,
    P_SOURCE_TABLE        => 'AP_INVOICES_ALL',
    P_SOURCE_COLUMN       => 'INVOICE_NUM',
    P_TARGET_SEGMENT      => 'SEGMENT1',
    P_TRANSFORMATION      => 'SUBSTR(INVOICE_NUM, 1, 3)',
    P_ENABLED_FLAG        => 'Y'
  );
END;
/
```

### Step 4: Implement the AP to GL Flow

#### 4.1 Create AP Invoice

```sql
-- AP Invoice creation (simplified)
DECLARE
  l_invoice_id       NUMBER;
  l_invoice_num      VARCHAR2(50);
  l_accounting_event NUMBER;
BEGIN
  -- Create invoice header
  SELECT ap_invoices_all_s.NEXTVAL INTO l_invoice_id FROM DUAL;
  l_invoice_num := 'INV-' || TO_CHAR(l_invoice_id);

  INSERT INTO ap_invoices_all (
    invoice_id, invoice_num, invoice_date,
    vendor_id, invoice_amount, invoice_currency_code,
    payment_status_flag, invoice_type_lookup_code,
    accounting_event_id, creation_date, created_by,
    last_update_date, last_updated_by
  ) VALUES (
    l_invoice_id, l_invoice_num, SYSDATE,
    1234, 15000.00, 'USD',
    'N', 'STANDARD',
    NULL, SYSDATE, 1, SYSDATE, 1
  );

  -- Create invoice line
  INSERT INTO ap_invoice_lines_all (
    invoice_id, line_number, line_type_lookup_code,
    amount, accounting_event_id, creation_date, created_by
  ) VALUES (
    l_invoice_id, 1, 'ITEM',
    15000.00, NULL, SYSDATE, 1
  );

  -- Create distribution
  INSERT INTO ap_invoice_distributions_all (
    invoice_id, line_number, dist_code_combination_id,
    amount, accounting_event_id, set_of_books_id,
    creation_date, created_by
  ) VALUES (
    l_invoice_id, 1, 12345,       -- code combination ID
    15000.00, NULL, 101,
    SYSDATE, 1
  );

  COMMIT;

  -- Validate and create accounting event
  l_accounting_event := AP_ACCOUNTING_EVENTS_PKG.CREATE_EVENT(
    p_invoice_id            => l_invoice_id,
    p_event_type_code       => 'INVOICE_VALIDATION',
    p_event_date            => SYSDATE,
    p_period_name           => 'JUL-26'
  );

  AP_ACCOUNTING_EVENTS_PKG.PROCESS_EVENT(
    p_accounting_event_id   => l_accounting_event
  );

  DBMS_OUTPUT.PUT_LINE('Created invoice: ' || l_invoice_num);
END;
/
```

#### 4.2 SLA Creates Accounting Entries

```sql
-- Query SLA accounting entries for the invoice
SELECT xah.accounting_event_id,
       xah.event_type_code,
       xah.accounting_date,
       xal.je_line_num,
       xal.code_combination_id,
       gcck.segment1 AS company,
       gcck.segment2 AS department,
       gcck.segment3 AS account,
       gcck.segment4 AS product,
       xal.accounted_dr,
       xal.accounted_cr,
       xal.currency_code,
       xal.entered_dr,
       xal.entered_cr,
       xal.description
FROM   xla_ae_headers xah,
       xla_ae_lines xal,
       gl_code_combinations_kfv gcck
WHERE  xah.ae_header_id = xal.ae_header_id
AND    xal.code_combination_id = gcck.code_combination_id
AND    xah.application_id = 200
AND    xah.accounting_event_id = l_accounting_event
ORDER  BY xal.je_line_num;
```

### Step 5: Transfer from SLA to GL Interface

```sql
-- Transfer SLA entries to GL interface
DECLARE
  l_request_id NUMBER;
  l_result     BOOLEAN;
BEGIN
  -- Run GL Interface transfer program
  l_request_id := FND_REQUEST.SUBMIT_REQUEST(
    application   => 'SQLAP',
    program       => 'APXTRAMTHX',
    description   => 'Transfer AP accounting to GL',
    start_time    => SYSDATE,
    sub_request   => FALSE,
    argument1     => 'JUL-26',      -- Period name
    argument2     => 'BOTH',        -- Detail/Summary
    argument3     => 'Y'            -- Post to GL
  );

  COMMIT;

  -- Check status
  DBMS_OUTPUT.PUT_LINE('GL Transfer Request: ' || l_request_id);
END;
/
```

### Step 6: Post Journals in GL

```sql
-- Create and post GL journal batch
DECLARE
  l_je_batch_id    NUMBER;
  l_je_header_id   NUMBER;
  l_status         VARCHAR2(10);
  l_message        VARCHAR2(2000);
BEGIN
  -- Create journal batch
  l_je_batch_id := GL_JE_BATCHES_PKG.CREATE_BATCH(
    p_name            => 'JUL-26 AP ACCRUAL BATCH',
    p_period_name     => 'JUL-26',
    p_set_of_books_id => 101,
    p_status          => 'U',
    p_actual_flag     => 'A',
    p_budget_version_id => NULL,
    p_running_total_dr => 15000,
    p_running_total_cr => 15000,
    p_default_effective_date => SYSDATE
  );

  -- Create journal header
  l_je_header_id := GL_JE_HEADERS_PKG.CREATE_HEADER(
    p_je_batch_id     => l_je_batch_id,
    p_name            => 'AP ACCRUAL JUL-26',
    p_period_name     => 'JUL-26',
    p_je_category     => 'Purchase Accrual',
    p_je_source       => 'Payables',
    p_set_of_books_id => 101,
    p_currency_code   => 'USD',
    p_status          => 'U',
    p_actual_flag     => 'A',
    p_running_total_dr => 15000,
    p_running_total_cr => 15000
  );

  COMMIT;

  -- Post the journal
  l_status := GL_JE_POSTING_PKG.POST(
    p_je_header_id  => l_je_header_id,
    p_set_of_books_id => 101,
    p_message       => l_message
  );

  DBMS_OUTPUT.PUT_LINE('Post status: ' || l_status);
  DBMS_OUTPUT.PUT_LINE('Message: ' || l_message);
END;
/
```

### Step 7: Implement Reconciliation Process

The core reconciliation compares subledger balances to GL balances at various levels:

#### 7.1 Subledger to GL Balance Comparison

```sql
CREATE OR REPLACE PACKAGE xx_gl_reconciliation_pkg AS

  TYPE recon_record IS RECORD (
    period_name        VARCHAR2(30),
    application_name   VARCHAR2(80),
    account_segment    VARCHAR2(50),
    subledger_balance  NUMBER,
    gl_balance         NUMBER,
    difference         NUMBER,
    recon_status       VARCHAR2(20)
  );

  TYPE recon_table IS TABLE OF recon_record;

  FUNCTION compare_ap_gl_balances(
    p_period_name    VARCHAR2,
    p_set_of_books_id NUMBER
  ) RETURN recon_table PIPELINED;

  FUNCTION compare_ar_gl_balances(
    p_period_name    VARCHAR2,
    p_set_of_books_id NUMBER
  ) RETURN recon_table PIPELINED;

  PROCEDURE run_period_reconciliation(
    p_period_name    VARCHAR2,
    p_set_of_books_id NUMBER
  );

END xx_gl_reconciliation_pkg;
/
```

#### 7.2 AP to GL Reconciliation

```sql
CREATE OR REPLACE PACKAGE BODY xx_gl_reconciliation_pkg AS

  FUNCTION compare_ap_gl_balances(
    p_period_name    VARCHAR2,
    p_set_of_books_id NUMBER
  ) RETURN recon_table PIPELINED IS

    CURSOR ap_balances_cur IS
      SELECT code_combination_id,
             SUM(NVL(debit_amount, 0) - NVL(credit_amount, 0)) AS subledger_balance
      FROM   ap_invoice_distributions_all aid,
             ap_invoices_all ai
      WHERE  aid.invoice_id = ai.invoice_id
      AND    ai.invoice_date >= (
                SELECT MIN(period_start_date)
                FROM   gl_periods
                WHERE  period_name = p_period_name
                AND    set_of_books_id = p_set_of_books_id
             )
      AND    ai.invoice_date <= (
                SELECT MAX(period_end_date)
                FROM   gl_periods
                WHERE  period_name = p_period_name
                AND    set_of_books_id = p_set_of_books_id
             )
      AND    ai.approval_status = 'APPROVED'
      GROUP  BY code_combination_id;

    l_rec recon_record;

  BEGIN
    FOR ap_rec IN ap_balances_cur LOOP
      l_rec.period_name := p_period_name;
      l_rec.application_name := 'Payables';
      l_rec.subledger_balance := ap_rec.subledger_balance;

      -- Get GL balance for same code combination
      BEGIN
        SELECT NVL(SUM(period_net_dr - period_net_cr), 0)
        INTO   l_rec.gl_balance
        FROM   gl_balances
        WHERE  code_combination_id = ap_rec.code_combination_id
        AND    period_name = p_period_name
        AND    actual_flag = 'A';
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          l_rec.gl_balance := 0;
      END;

      l_rec.difference := l_rec.subledger_balance - l_rec.gl_balance;

      IF NVL(l_rec.difference, 0) = 0 THEN
        l_rec.recon_status := 'MATCHED';
      ELSE
        l_rec.recon_status := 'UNMATCHED';
      END IF;

      PIPE ROW(l_rec);
    END LOOP;

    RETURN;
  END compare_ap_gl_balances;

  FUNCTION compare_ar_gl_balances(
    p_period_name    VARCHAR2,
    p_set_of_books_id NUMBER
  ) RETURN recon_table PIPELINED IS
    -- Similar logic for AR module
    l_rec recon_record;
    CURSOR ar_balances_cur IS
      SELECT code_combination_id,
             SUM(NVL(acctd_amount, 0)) AS subledger_balance
      FROM   ar_receivable_applications_all
      WHERE  gl_date BETWEEN (
                SELECT MIN(period_start_date)
                FROM   gl_periods
                WHERE  period_name = p_period_name
                AND    set_of_books_id = p_set_of_books_id
             ) AND (
                SELECT MAX(period_end_date)
                FROM   gl_periods
                WHERE  period_name = p_period_name
                AND    set_of_books_id = p_set_of_books_id
             )
      GROUP  BY code_combination_id;
  BEGIN
    FOR ar_rec IN ar_balances_cur LOOP
      l_rec.period_name := p_period_name;
      l_rec.application_name := 'Receivables';
      l_rec.subledger_balance := ar_rec.subledger_balance;

      BEGIN
        SELECT NVL(SUM(period_net_dr - period_net_cr), 0)
        INTO   l_rec.gl_balance
        FROM   gl_balances
        WHERE  code_combination_id = ar_rec.code_combination_id
        AND    period_name = p_period_name
        AND    actual_flag = 'A';
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          l_rec.gl_balance := 0;
      END;

      l_rec.difference := l_rec.subledger_balance - l_rec.gl_balance;

      IF NVL(l_rec.difference, 0) = 0 THEN
        l_rec.recon_status := 'MATCHED';
      ELSE
        l_rec.recon_status := 'UNMATCHED';
      END IF;

      PIPE ROW(l_rec);
    END LOOP;
    RETURN;
  END compare_ar_gl_balances;

  PROCEDURE run_period_reconciliation(
    p_period_name    VARCHAR2,
    p_set_of_books_id NUMBER
  ) IS
    l_match_count NUMBER := 0;
    l_unmatch_count NUMBER := 0;
    l_total_diff NUMBER := 0;
  BEGIN
    -- Log start
    INSERT INTO xx_gl_recon_log
      (period_name, set_of_books_id, start_time, status)
    VALUES (p_period_name, p_set_of_books_id, SYSDATE, 'IN_PROGRESS');

    -- AP reconciliation
    FOR ap_rec IN (
      SELECT * FROM TABLE(
        xx_gl_reconciliation_pkg.compare_ap_gl_balances(
          p_period_name, p_set_of_books_id
        )
      )
    ) LOOP
      INSERT INTO xx_gl_recon_results VALUES ap_rec;

      IF ap_rec.recon_status = 'MATCHED' THEN
        l_match_count := l_match_count + 1;
      ELSE
        l_unmatch_count := l_unmatch_count + 1;
        l_total_diff := l_total_diff + NVL(ap_rec.difference, 0);
      END IF;
    END LOOP;

    -- AR reconciliation
    FOR ar_rec IN (
      SELECT * FROM TABLE(
        xx_gl_reconciliation_pkg.compare_ar_gl_balances(
          p_period_name, p_set_of_books_id
        )
      )
    ) LOOP
      INSERT INTO xx_gl_recon_results VALUES ar_rec;

      IF ar_rec.recon_status = 'MATCHED' THEN
        l_match_count := l_match_count + 1;
      ELSE
        l_unmatch_count := l_unmatch_count + 1;
        l_total_diff := l_total_diff + NVL(ar_rec.difference, 0);
      END IF;
    END LOOP;

    -- Update log
    UPDATE xx_gl_recon_log
    SET end_time = SYSDATE,
        status = CASE WHEN l_unmatch_count = 0 THEN 'COMPLETE' ELSE 'COMPLETE_WITH_EXCEPTIONS' END,
        match_count = l_match_count,
        unmatch_count = l_unmatch_count,
        total_difference = l_total_diff
    WHERE period_name = p_period_name
    AND set_of_books_id = p_set_of_books_id;

    COMMIT;
  END run_period_reconciliation;

END xx_gl_reconciliation_pkg;
/
```

### Step 8: Drill-Down from GL to Subledger

Enable drill-down from a GL journal line to the source transaction:

```sql
-- GL to SLA drill-down query
SELECT xah.accounting_event_id,
       xah.event_type_code,
       xah.event_date,
       xah.entity_id,
       -- Source transaction identification
       CASE xah.event_type_code
         WHEN 'INVOICE_VALIDATION' THEN (
           SELECT ai.invoice_num
           FROM   ap_invoices_all ai
           WHERE  ai.invoice_id = xah.entity_id
         )
         WHEN 'PAYMENT_CLEARED' THEN (
           SELECT acp.check_number
           FROM   ap_checks_all acp
           WHERE  acp.check_id = xah.entity_id
         )
         WHEN 'RECEIPT_CASH' THEN (
           SELECT acr.receipt_number
           FROM   ar_cash_receipts_all acr
           WHERE  acr.cash_receipt_id = xah.entity_id
         )
       END AS source_document_number,
       xal.accounted_dr,
       xal.accounted_cr,
       xal.description
FROM   xla_ae_headers xah,
       xla_ae_lines xal
WHERE  xah.ae_header_id = xal.ae_header_id
AND    xah.application_id = xal.application_id
AND    xah.gl_transfer_flag = 'Y'
AND    EXISTS (
  SELECT 1
  FROM   gl_je_lines gjl
  WHERE  gjl.reference_1 = TO_CHAR(xah.ae_header_id)
  AND    gjl.je_header_id = :p_je_header_id
)
ORDER  BY xah.accounting_event_id, xal.je_line_num;
```

### Step 9: Multi-Currency Reconciliation

Handle reconciling transactions in multiple currencies:

```sql
CREATE OR REPLACE PROCEDURE xx_reconcile_multi_currency (
  p_period_name    VARCHAR2,
  p_set_of_books_id NUMBER
) IS
  CURSOR fx_diff_cur IS
    SELECT xal.currency_code,
           SUM(NVL(xal.entered_dr, 0) - NVL(xal.entered_cr, 0)) AS entered_balance,
           SUM(NVL(xal.accounted_dr, 0) - NVL(xal.accounted_cr, 0)) AS accounted_balance,
           ABS(SUM(NVL(xal.accounted_dr, 0) - NVL(xal.accounted_cr, 0))
               - SUM(NVL(xal.entered_dr, 0) - NVL(xal.entered_cr, 0))) AS fx_diff
    FROM   xla_ae_headers xah,
           xla_ae_lines xal,
           gl_periods gp
    WHERE  xah.ae_header_id = xal.ae_header_id
    AND    xah.accounting_date BETWEEN gp.period_start_date AND gp.period_end_date
    AND    gp.period_name = p_period_name
    AND    gp.set_of_books_id = p_set_of_books_id
    AND    xal.currency_code != 'USD'
    GROUP  BY xal.currency_code
    HAVING ABS(SUM(NVL(xal.accounted_dr, 0) - NVL(xal.accounted_cr, 0))
             - SUM(NVL(xal.entered_dr, 0) - NVL(xal.entered_cr, 0))) > 0.01;

BEGIN
  FOR rec IN fx_diff_cur LOOP
    INSERT INTO xx_fx_recon_results
      (period_name, currency_code, entered_balance,
       accounted_balance, fx_difference, recon_date)
    VALUES (p_period_name, rec.currency_code,
            rec.entered_balance, rec.accounted_balance,
            rec.fx_diff, SYSDATE);
  END LOOP;

  COMMIT;
END;
/
```

### Step 10: Close Dashboard and Monitoring

Create period close status monitoring:

```sql
-- Period close status dashboard
CREATE OR REPLACE VIEW xx_period_close_dashboard AS
SELECT gp.period_name,
       gp.period_year,
       gp.quarter_num,
       gp.period_type,
       gp.closing_status,
       (SELECT COUNT(*)
        FROM   gl_je_headers gjh
        WHERE  gjh.period_name = gp.period_name
        AND    gjh.status = 'P') AS posted_journals,
       (SELECT COUNT(*)
        FROM   gl_je_headers gjh
        WHERE  gjh.period_name = gp.period_name
        AND    gjh.status = 'U') AS unposted_journals,
       (SELECT COUNT(*)
        FROM   ap_invoices_all ai,
               gl_periods gp2
        WHERE  ai.invoice_date BETWEEN gp2.period_start_date AND gp2.period_end_date
        AND    gp2.period_name = gp.period_name
        AND    ai.approval_status = 'APPROVED'
        AND NOT EXISTS (
          SELECT 1
          FROM   xla_ae_headers xah
          WHERE  xah.entity_id = ai.invoice_id
          AND    xah.event_type_code = 'INVOICE_VALIDATION'
          AND    xah.gl_transfer_flag = 'Y'
        )) AS unaccounted_ap_invoices,
       (SELECT NVL(SUM(difference), 0)
        FROM   xx_gl_recon_results
        WHERE  period_name = gp.period_name) AS unreconciled_difference
FROM   gl_periods gp
WHERE  gp.set_of_books_id = 101
ORDER  BY gp.period_year DESC, gp.period_num DESC;
```

---

## Best Practices

### SLA Configuration
1. **Single accounting method per module**: Use one accounting method across all subledgers for consistency; create custom methods only for intercompany or specific legal requirements
2. **Journal line types**: Define line types for each unique accounting entry type (accrual, payment, cancellation, adjustment) to simplify reconciliation
3. **Accounting attributes**: Map source columns to GL segments at the earliest possible point; avoid transformations that lose audit trail

### Reconciliation
1. **Automate daily, not just at month-end**: Run lightweight reconciliation daily to identify issues early when they're easier to fix
2. **Three-way reconcile**: Match (1) subledger detail → (2) SLA accounting entries → (3) GL balances — never skip the SLA layer
3. **Tolerance thresholds**: Set small tolerances ($0.01) for general accounts; use higher tolerances ($100) for clearing accounts with known timing differences
4. **Exception handling**: Create specific exception categories (timing differences, FX rounding, intercompany mismatches) instead of a generic "unmatched" bucket

### Period Close
1. **Pre-close checklist**: Run validation programs 2 days before period end — identify and fix issues before close starts
2. **Parallel processing**: Run AP/AR/FA accounting creation in parallel; sequence only GL posting which requires serial execution
3. **SLA to GL transfer timing**: Run transfers during low-transaction windows; use incremental mode to avoid reprocessing already-transferred entries
4. **Close monitor**: Assign a close coordinator who monitors the dashboard; escalate exceptions that exceed 4 hours without resolution

### Common Pitfalls
1. **Orphaned SLA entries**: Transactions canceled after SLA accounting creation leave orphaned entries — implement cleanup jobs
2. **Period mismatch**: Subledger transaction date falls in a closed period but header date is in open period — configure date validation
3. **Rounding differences**: FX revaluation creates 0.01-0.03 rounding differences — configure tolerance accounts to absorb these
4. **Duplicate GL transfers**: Concurrent GL transfer programs submitted multiple times create duplicate journal entries — implement idempotency checks

### Performance Optimization
1. **SLA batch size**: Process SLA events in batches of 10,000 for optimal performance; larger batches cause undo segment contention
2. **GL_INTERFACE indexing**: Ensure GL_INTERFACE has indexes on (STATUS, SET_OF_BOOKS_ID, PERIOD_NAME) — this is the most queried table during close
3. **Parallel GL posting**: Use GL_POST_DETAIL program with multiple workers; test with increasing worker count until I/O becomes bottleneck
4. **Archive old periods**: Archive GL_BALANCES and SLA tables for closed periods to keep query performance consistent

## Audit and Compliance

```sql
-- SOX audit trail for all accounting changes
CREATE OR REPLACE VIEW xx_accounting_audit_trail AS
SELECT xah.accounting_event_id,
       xah.event_type_code,
       xah.event_status_code,
       xah.accounting_date,
       xah.entity_id,
       xah.entity_code,
       xal.je_line_num,
       xal.code_combination_id,
       gcck.concatenated_segments AS account_combination,
       xal.accounted_dr,
       xal.accounted_cr,
       xal.currency_code,
       xal.entered_dr,
       xal.entered_cr,
       xal.description,
       xah.created_by AS event_created_by,
       xah.creation_date AS event_created_date,
       xal.last_updated_by AS line_updated_by,
       xal.last_update_date AS line_updated_date
FROM   xla_ae_headers xah,
       xla_ae_lines xal,
       gl_code_combinations_kfv gcck
WHERE  xah.ae_header_id = xal.ae_header_id
AND    xal.code_combination_id = gcck.code_combination_id;
```

## Key Performance Metrics

| Metric | Target | Measurement Method |
|--------|--------|--------------------|
| GL to SL reconciliation | 100% match | Automated recon job |
| Close duration | < 72 hours | Close dashboard |
| SLA transfer latency | < 15 min per batch | SLA batch monitor |
| Unposted journals | 0 at close | GL period status |
| Drill-down response | < 5 seconds | XLA query |
| FX difference | < $100 total | Multi-currency recon |
| Audit trail completeness | 100% | XLA event count vs transaction count |
| Intercompany match | 100% | IC reconciliation engine |
