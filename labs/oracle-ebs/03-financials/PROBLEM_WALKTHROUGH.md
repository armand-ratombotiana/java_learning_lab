# Problem Walkthrough: Financials

## Problem 1: AP Invoice Holds — Company: Deloitte
### EBS Interview Scenario
"You're at Deloitte implementing EBS Financials for a manufacturing company. During user acceptance testing, the AP team reports that 30% of supplier invoices are being placed on "Payables Invoice Holds" with no clear reason. The AP manager is concerned about supplier relationships and ask you to resolve."

### The Problem
The client configured automatic invoice validation with strict tolerances. However, they did not configure matching rules correctly for purchase order receipts. Suppliers are invoicing based on delivered quantities, but the system is matching against ordered quantities. Additionally, the "Price Variance" tolerance is set to 0%, causing any minor price difference to trigger a hold.

### Solution Walkthrough
- Step 1: Query AP_HOLDS_ALL to identify distribution of hold reasons
- Step 2: Review PO matching setup in Purchasing Options
- Step 3: Adjust matching tolerances — price variance to 5%, quantity variance to 10%
- Step 4: Configure receipt matching rules to use "Received Quantity" vs "Ordered Quantity"
- Step 5: Create a custom AP hold release concurrent program for batch processing
- Step 6: Set up workflow notification for automatic hold management
- Step 7: Document the matching rules for user training

### Code
```sql
-- Analyze hold reasons
SELECT h.hold_reason,
       h.hold_type,
       COUNT(*) AS hold_count,
       SUM(i.invoice_amount) AS total_hold_amount
FROM   ap_holds_all h,
       ap_invoices_all i
WHERE  h.invoice_id = i.invoice_id
AND    h.release_flag IS NULL
AND    i.invoice_date > SYSDATE - 30
GROUP  BY h.hold_reason, h.hold_type
ORDER  BY hold_count DESC;

-- Release specific holds in batch
DECLARE
  l_release_result VARCHAR2(200);
BEGIN
  FOR rec IN (
    SELECT invoice_id, line_number, hold_reason
    FROM   ap_holds_all
    WHERE  hold_reason = 'PRICE VARIANCE'
    AND    release_flag IS NULL
    AND    ROWNUM <= 100
  ) LOOP
    l_release_result := ap_hold_release_pkg.release_hold(
      p_invoice_id   => rec.invoice_id,
      p_line_number  => rec.line_number,
      p_hold_reason  => rec.hold_reason,
      p_release_date => SYSDATE
    );
  END LOOP;
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: AP schema deep dive — AP_HOLDS_ALL, AP_INVOICES_ALL, AP_PAYMENT_SCHEDULES_ALL, matching engine internals.
- Deloitte: Implementation methodology for invoice-to-pay process, tolerance configuration workshops, and UAT defect triage.
- Accenture: Large-scale AP shared services deployment, multi-org matching rules, supplier portal integration.
- PwC: SOX compliance for invoice holds, approval limits, segregation of duties in AP/payables processes.
- Amazon: Automation of hold resolution with machine learning, exception-based workflow management.

---

## Problem 2: GL Period Close — Company: Oracle
### EBS Interview Scenario
"You're at Oracle providing support to a retail client. They cannot close the GL period for December. The "GL.POST" program completes successfully, but the "GL.CLOSE" program fails with "Encumbrance balances are not in balance." The client needs to close before the SEC filing deadline in 3 days."

### The Problem
A custom encumbrance journal was posted for a purchase order that was subsequently cancelled. The encumbrance reversal was not processed correctly, leaving orphaned encumbrance balances in GL_BALANCES. The GL_CLOSE program validates that encumbrance debits equal credits before allowing period close. The mismatch is $12,500.

### Solution Walkthrough
- Step 1: Identify the unbalanced encumbrance entries in GL_BALANCES
- Step 2: Trace the orphaned encumbrance to the cancelled PO
- Step 3: Query GL_JE_HEADERS and GL_JE_LINES for the original encumbrance journal
- Step 4: Create a reversing journal entry to clear the orphaned encumbrance
- Step 5: Verify encumbrance balance using GL_ENCUMBRANCE_BALANCES_PKG
- Step 6: Re-run GL_CLOSE validation
- Step 7: Implement preventive controls to auto-reverse encumbrances on PO cancellation

### Code
```sql
-- Find unbalanced encumbrance balances
SELECT gb.period_name,
       gb.code_combination_id,
       gb.period_net_dr_beq,
       gb.period_net_cr_beq,
       gb.period_net_dr_beq - gb.period_net_cr_beq AS imbalance
FROM   gl_balances gb
WHERE  gb.period_name = 'DEC-24'
AND    gb.actual_flag = 'E'
AND    gb.period_net_dr_beq != gb.period_net_cr_beq;

-- Create reversal journal
DECLARE
  l_je_header_id NUMBER;
BEGIN
  l_je_header_id := gl_journal_entries_pkg.create_je_header(
    p_set_of_books_id => 101,
    p_journal_category => 'Encumbrance Reversal',
    p_journal_source   => 'Manual',
    p_period_name      => 'DEC-24',
    p_currency_code    => 'USD',
    p_document_date    => SYSDATE,
    p_je_source        => 'Manual'
  );
  
  gl_journal_entries_pkg.create_je_line(
    p_je_header_id       => l_je_header_id,
    p_code_combination_id => 12345,
    p_line_number         => 1,
    p_entered_dr          => 12500,
    p_accounted_dr        => 12500,
    p_description         => 'Reversal of orphaned encumbrance for cancelled PO 7654'
  );
  
  gl_journal_entries_pkg.create_je_line(
    p_je_header_id       => l_je_header_id,
    p_code_combination_id => 12345,
    p_line_number         => 2,
    p_entered_cr          => 12500,
    p_accounted_cr        => 12500,
    p_description         => 'Reversal of orphaned encumbrance for cancelled PO 7654'
  );
  
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: GL schema — GL_BALANCES, GL_JE_HEADERS, GL_JE_LINES, encumbrance accounting, period close validation programs.
- Deloitte: Period-close methodology, pre-close checklists, close automation using GL Journal import and auto-reversal.
- Accenture: Global consolidation, multi-period close, intercompany eliminations for large enterprises.
- PwC: SEC compliance, financial statement audit, period-close controls, journal entry testing.
- Amazon: Automation of period close with AWS Lambda, CloudWatch monitoring for close checklists.

---

## Problem 3: AR Receipt Reconciliation — Company: Accenture
### EBS Interview Scenario
"You're at Accenture implementing EBS for a telecommunications company. They process 50,000 customer payments per day via lockbox. After go-live, the AR team finds that 5% of payments are not matching to invoices, ending up in "Unapplied Receipts." The customer service team is overwhelmed with calls."

### The Problem
The lockbox configuration is using a "Customer Name" match, but customer names in the remittance data do not always match the customer names in EBS (e.g., "AT&T" vs "ATT" vs "AT and T"). Additionally, invoice numbers in the remittance file include leading zeros while EBS stores them without formatting.

### Solution Walkthrough
- Step 1: Review lockbox configuration in AR_LOCKBOX_PAYMENT_METHODS
- Step 2: Analyze unapplied receipts in AR_RECEIVABLE_APPLICATIONS_ALL
- Step 3: Create a custom AutoLockbox validation program to normalize customer names
- Step 4: Implement fuzzy matching logic using UTL_MATCH.JARO_WINKLER
- Step 5: Configure lockbox preprocessing to strip leading zeros from invoice numbers
- Step 6: Set up workflow notification for receipts that require manual intervention
- Step 7: Create a daily reconciliation report

### Code
```sql
-- Find unapplied receipts
SELECT rct.receipt_number,
       rct.amount,
       rct.receipt_date,
       cust.customer_name,
       rcta.status
FROM   ar_cash_receipts_all rct,
       ar_receivable_applications_all rcta,
       hz_cust_accounts cust
WHERE  rct.cash_receipt_id = rcta.cash_receipt_id(+)
AND    rct.pay_from_customer = cust.cust_account_id
AND    rcta.status = 'UNAPP'
AND    rct.receipt_date > SYSDATE - 7
ORDER  BY rct.receipt_date DESC;

-- Normalize customer name for matching
CREATE OR REPLACE FUNCTION normalize_customer_name (
  p_name IN VARCHAR2
) RETURN VARCHAR2 IS
  l_normalized VARCHAR2(100);
BEGIN
  l_normalized := UPPER(p_name);
  l_normalized := REGEXP_REPLACE(l_normalized, '[^A-Z0-9 ]', '');
  l_normalized := REGEXP_REPLACE(l_normalized, '\s+', ' ');
  l_normalized := REPLACE(l_normalized, ' AND ', ' ');
  l_normalized := REPLACE(l_normalized, ' INCORPORATED', ' INC');
  l_normalized := TRIM(l_normalized);
  RETURN l_normalized;
END;
/
```

### Company Evaluation
- Oracle: AutoLockbox configuration, AR tables, receipt matching algorithms, UTL_MATCH functions.
- Accenture: Large-volume transaction processing, shared services AR, customer data cleanup methodology.
- Deloitte: Testing methodology for lockbox integration, user acceptance testing with realistic remittance files.
- PwC: Cash application controls, SOX compliance for AR, audit trail for receipt adjustments.
- Amazon: Cloud-based lockbox processing with S3 and Lambda, AI/ML for intelligent receipt matching.
