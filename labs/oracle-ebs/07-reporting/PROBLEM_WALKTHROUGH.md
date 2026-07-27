# Problem Walkthrough: Reporting

## Problem 1: BI Publisher Report for AP Aging — Company: Deloitte
### EBS Interview Scenario
"You're at Deloitte implementing EBS for a manufacturing client. The CFO wants a custom AP Aging report that shows invoice aging buckets by supplier category, with drill-down to invoice details. The standard Oracle AP Aging report does not support supplier categorization or drill-down. The report must be delivered via BI Publisher integrated with EBS."

### The Problem
Build a BI Publisher report that: (1) Queries AP_INVOICES_ALL and AP_PAYMENT_SCHEDULES_ALL for aging buckets (0-30, 31-60, 61-90, 90+ days), (2) Groups by supplier category (Raw Materials, MRO, Services, Utilities), (3) Provides drill-down from category to supplier to invoice details, (4) Handles multi-currency with spot-rate conversion to USD, (5) Includes aging based on due date, not invoice date. Must be deployed as an EBS concurrent program.

### Solution Walkthrough
- Step 1: Design the data model in BI Publisher using SQL query with bind variables
- Step 2: Create the report layout in RTF template with conditional formatting
- Step 3: Implement drill-down using BI Publisher hyperlinks with request parameters
- Step 4: Register the report as an EBS concurrent program with concurrent program executable
- Step 5: Configure parameters — Aging Date, Currency, Supplier Category
- Step 6: Set up bursting for email delivery to category managers
- Step 7: Implement currency conversion using GL_DAILY_RATES

### Code
```sql
-- AP Aging Report Data Model
SELECT pv.segment1 AS supplier_number,
       pv.vendor_name,
       pvsa.vendor_site_code,
       pv.category_code AS supplier_category,
       aia.invoice_id,
       aia.invoice_num,
       aia.invoice_date,
       aia.invoice_currency_code,
       aia.invoice_amount,
       apsa.due_date,
       apsa.amount_due_remaining,
       apsa.gross_due,
       apsa.discount_amount_remaining,
       ap_invoices_utility_pkg.get_approval_status(aia.invoice_id) AS approval_status,
       -- Aging buckets
       CASE
         WHEN SYSDATE - apsa.due_date <= 0 THEN 'CURRENT'
         WHEN SYSDATE - apsa.due_date BETWEEN 1 AND 30 THEN '0-30 Days'
         WHEN SYSDATE - apsa.due_date BETWEEN 31 AND 60 THEN '31-60 Days'
         WHEN SYSDATE - apsa.due_date BETWEEN 61 AND 90 THEN '61-90 Days'
         ELSE '90+ Days'
       END AS aging_bucket,
       -- Convert to USD
       apsa.amount_due_remaining *
         NVL(gdr.conversion_rate, 1) AS amount_due_usd
FROM   ap_suppliers pv,
       ap_supplier_sites_all pvsa,
       ap_invoices_all aia,
       ap_payment_schedules_all apsa,
       gl_daily_rates gdr
WHERE  pv.vendor_id = pvsa.vendor_id
AND    pv.vendor_id = aia.vendor_id
AND    aia.invoice_id = apsa.invoice_id
AND    aia.invoice_currency_code = gdr.from_currency(+)
AND    gdr.to_currency(+) = 'USD'
AND    gdr.conversion_date(+) = TRUNC(SYSDATE)
AND    pv.end_date_active IS NULL
AND    apsa.amount_due_remaining > 0
ORDER  BY pv.category_code, pv.vendor_name, apsa.due_date;

-- Register as concurrent program
BEGIN
  fnd_program_pkg.create_program(
    p_application_short_name => 'SQLAP',
    p_program_short_name     => 'XX_AP_AGING_REPORT',
    p_program_name           => 'AP Aging Report by Category',
    p_description            => 'Custom AP aging with supplier categorization and drill-down',
    p_executable_name        => 'XX_AP_AGING_REPORT',
    p_executable_method      => 'P',
    p_execution_file_name    => 'XX_AP_AGING_REPORT.rtf',
    p_execution_directory    => '$XX_REPORTS_TOP/reports/US',
    p_output_format          => 'PDF',
    p_enabled_flag           => 'Y'
  );
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: BI Publisher architecture, data model design, RTF template layout, EBS integration, bursting configuration.
- Deloitte: Report design methodology, user requirements gathering, report validation and UAT process.
- Accenture: Enterprise reporting strategy, BI Publisher dashboards, multi-dimensional analysis with Oracle OLAP.
- PwC: Financial reporting controls, report accuracy validation, audit trail for report distribution.
- Amazon: Cloud BI with QuickSight, EBS data export to S3 for serverless analytics, real-time dashboards.

---

## Problem 2: Oracle Reports (RDF) Migration — Company: Oracle
### EBS Interview Scenario
"You're at Oracle helping a banking client upgrade from EBS 12.1 to 12.2. They have 200+ custom Oracle Reports (RDF files) built with Oracle Reports 6i. After the upgrade, many reports fail with "REP-0001: Unable to open file" and "REP-0069: Error opening file for writing." The reports team can no longer modify them because Oracle Reports Builder is no longer supported."

### The Problem
Oracle Reports 6i is desupported in EBS 12.2. The RDF files are binary and incompatible with the 12.2 reports runtime. Some reports use deprecated features (matrix reports, PL/SQL in format triggers, and Web.ShowDocument calls). The client needs all 200+ reports migrated to BI Publisher, but only has a 6-week timeline. A 100% migration is impossible — need a hybrid approach.

### Solution Walkthrough
- Step 1: Inventory all 200+ RDF files — categorize by complexity (Simple = 1 SQL, Medium = complex layout, Complex = matrix/PLSQL triggers)
- Step 2: Identify reports that can run in Oracle Reports 12c compatibility mode
- Step 3: Convert simple RDFs to XML for BI Publisher using automated conversion tools
- Step 4: For complex reports — redesign in BI Publisher using same SQL queries
- Step 5: Rebuild matrix reports using BI Publisher cross-tab functionality
- Step 6: Replace Web.ShowDocument calls with BI Publisher hyperlinks
- Step 7: Test each report against baseline output using data comparison
- Step 8: Deploy BI Publisher reports as EBS concurrent programs

### Code
```sql
-- Inventory of custom Oracle Reports
SELECT fru.user_report_name,
       fru.user_report_description,
       fcpv.user_concurrent_program_name,
       fa.application_short_name,
       fe.executable_name,
       fe.execution_method_code,
       fe.execution_file_name
FROM   fnd_reports_utl fru,
       fnd_concurrent_programs_vl fcpv,
       fnd_application fa,
       fnd_executables fe
WHERE  fru.report_id = fcpv.concurrent_program_id
AND    fcpv.application_id = fa.application_id
AND    fcpv.executable_id = fe.executable_id
AND    fe.executable_name LIKE '%.rdf'
ORDER  BY fa.application_short_name, fe.executable_name;

-- Convert legacy RDF SQL to BI Publisher XML
-- Extract SQL from RDF (using Oracle Reports tools or manual extraction)
-- Example: Convert Oracle Reports query to BI Publisher data set

-- Original RDF SRW.DO_SQL statement:
-- SRW.DO_SQL('SELECT account, SUM(amount) FROM gl_balances GROUP BY account');

-- BI Publisher equivalent:
SELECT account,
       SUM(amount) AS total_amount
FROM   gl_balances
WHERE  period_name = :P_PERIOD_NAME
GROUP  BY account
ORDER  BY account;
```

### Company Evaluation
- Oracle: Oracle Reports 6i/12c architecture, BI Publisher migration pathway, deprecation roadmap.
- Deloitte: Legacy modernization methodology, risk assessment for report migration, testing strategy.
- Accenture: Large-scale report conversion factories, automated comparison tools, offshore delivery models.
- PwC: Report accuracy verification, audit trail for financial reports, SOX compliance for generated reports.
- Amazon: Modernization strategy — replacing Oracle Reports with QuickSight, Tableau, or custom React dashboards.

---

## Problem 3: XML Publisher Bursting for Invoice Distribution — Company: Accenture
### EBS Interview Scenario
"You're at Accenture implementing EBS for a utility company. They send 100,000 invoices per month to customers. Currently, invoices are printed and mailed — costing $50K/month in postage. The client wants to switch to electronic invoice delivery via email. They need the XML Publisher bursting engine to split invoices by customer email address."

### The Problem
Configure BI Publisher bursting to: (1) Generate a single PDF for each customer invoice from the Oracle AR Invoice Print program, (2) Burst (split) the output by customer email address, (3) Email each PDF as an attachment, (4) Handle customers with multiple email addresses, (5) Log delivery status to a custom table, (6) Retry failed deliveries 3 times, (7) Archive the bursted PDFs to a network drive.

### Solution Walkthrough
- Step 1: Create a BI Publisher data template with SQL joining AR invoices and customer email addresses
- Step 2: Design the RTF template for invoice layout
- Step 3: Create bursting control file (XML) with delivery rules
- Step 4: Configure bursting query to return customer_email as delivery key
- Step 5: Configure BI Publisher Sender profile for SMTP
- Step 6: Set up delivery processors — Email, File (archive)
- Step 7: Create a custom PL/SQL delivery handler for logging
- Step 8: Schedule the report as an EBS concurrent program

### Code
```xml
<!-- Bursting Control File -->
<?xml version="1.0" encoding="UTF-8"?>
<burstingDefinition xmlns="http://xmlns.oracle.com/oxp/xmlp">
  <delivery>
    <deliveryType>EMAIL</deliveryType>
    <fromAddress>invoices@utilitycompany.com</fromAddress>
    <toAddress>{customer_email}</toAddress>
    <ccAddress>accounting@utilitycompany.com</ccAddress>
    <subject>Your Utility Invoice {invoice_number}</subject>
    <message>Dear {customer_name}, please find attached your invoice.</message>
    <attachmentFileName>{invoice_number}.pdf</attachmentFileName>
  </delivery>
  
  <delivery>
    <deliveryType>FILE</deliveryType>
    <filePath>/archive/invoices/{year}/{month}</filePath>
    <fileName>{invoice_number}.pdf</fileName>
  </delivery>
</burstingDefinition>
```

```sql
-- Bursting delivery key query
SELECT rcta.trx_number AS invoice_number,
       rcta.trx_date AS invoice_date,
       hca.account_name AS customer_name,
       hcp.contact_point_text AS customer_email,
       rcta.attribute1 AS customer_id
FROM   ra_customer_trx_all rcta,
       hz_cust_accounts hca,
       hz_contact_points hcp,
       hz_cust_acct_sites_all cas,
       hz_party_sites hps
WHERE  rcta.customer_trx_id = :p_trx_id
AND    rcta.bill_to_customer_id = hca.cust_account_id
AND    hca.cust_account_id = cas.cust_account_id
AND    cas.party_site_id = hps.party_site_id
AND    hps.party_id = hcp.owner_table_id
AND    hcp.contact_point_type = 'EMAIL'
AND    hcp.primary_flag = 'Y'
AND    ROWNUM = 1;
```

### Company Evaluation
- Accenture: High-volume invoice processing, electronic invoice delivery, customer communication management.
- Oracle: BI Publisher bursting engine, delivery handlers, SMTP configuration, XML data templates.
- Deloitte: Business process reengineering for invoice-to-cash, cost reduction analysis, customer experience design.
- PwC: Invoice integrity controls, electronic signature compliance, SOX for automated invoice delivery.
- Amazon: Cloud invoice generation with Lambda, SES for email delivery, S3 for archival, CloudWatch monitoring.
