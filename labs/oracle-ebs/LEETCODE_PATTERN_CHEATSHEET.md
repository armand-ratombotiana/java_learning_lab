# LeetCode Pattern Cheatsheet — Oracle EBS

<div align="center">

![Oracle EBS](https://img.shields.io/badge/Oracle_EBS-F80000?style=for-the-badge)
![SQL Patterns](https://img.shields.io/badge/SQL_Patterns-005C5C?style=for-the-badge)
![LeetCode](https://img.shields.io/badge/LeetCode-FFA116?style=for-the-badge)

**SQL + technical patterns mapped to EBS modules — interview-ready templates**

</div>

---

## Table of Contents

1. [SQL Patterns for EBS](#sql-patterns-for-ebs)
2. [PL/SQL Patterns](#plsql-patterns)
3. [Data Migration Patterns](#data-migration-patterns)
4. [Performance Tuning Patterns](#performance-tuning-patterns)
5. [LeetCode SQL Problem Map by Functional Area](#leetcode-sql-problem-map-by-functional-area)
6. [Java Patterns for EBS](#java-patterns-for-ebs)

---

## SQL Patterns for EBS

### Multi-Table Joins in EBS Schema

Pattern: EBS uses `_ALL` tables with `ORG_ID` for multi-org. Joins typically involve FND, base table, and translation (TL) tables.

```sql
-- Standard EBS multi-table join: Invoice + Supplier + GL + Pay Terms
SELECT ai.invoice_num,
       ap.vendor_name,
       gcc.code_combination || ' - ' || gcc.description AS account,
       ait.name AS term_name,
       aia.amount
FROM   ap_invoices_all ai
       JOIN ap_suppliers ap ON ap.vendor_id = ai.vendor_id
       JOIN ap_invoice_lines_all ail ON ail.invoice_id = ai.invoice_id
       JOIN gl_code_combinations gcc ON gcc.code_combination_id = ail.dist_code_combination_id
       JOIN ap_terms_tl ait ON ait.term_id = ai.terms_id AND ait.language = 'US'
WHERE  ai.org_id = nvl(fnd_profile.value('ORG_ID'), ai.org_id)
       AND ai.invoice_date >= ADD_MONTHS(SYSDATE, -6);
```

**LeetCode Reference:** LC 175 (Combine Two Tables), LC 181 (Employees Earning More), LC 1378 (Replace Employee ID)

**Company Frequency:** Oracle (high), Deloitte (high), Accenture (high)

---

### Concurrent Program Patterns

Pattern: FND concurrent programs read from FND_CONCURRENT_REQUESTS, FND_CONC_PP_ACTIONS, and FND_RUN_INTERFACES.

```sql
-- Find all concurrent requests for a given program in the last 30 days
SELECT fcr.request_id,
       fcp.user_concurrent_program_name,
       fcr.phase_code,          -- P=pending, R=running, C=completed
       fcr.status_code,          -- Normal, Error, Warning, Terminated
       fcr.argument_text,
       fu.user_name AS requested_by,
       fcr.request_date,
       fcr.actual_start_date,
       fcr.actual_completion_date,
       ROUND((fcr.actual_completion_date - fcr.actual_start_date) * 24 * 60, 2) AS duration_min
FROM   fnd_concurrent_requests fcr
       JOIN fnd_concurrent_programs_vl fcp ON fcp.concurrent_program_id = fcr.concurrent_program_id
       JOIN fnd_user fu ON fu.user_id = fcr.requested_by
WHERE  fcr.request_date >= SYSDATE - 30
ORDER BY fcr.request_date DESC;
```

**LeetCode Reference:** LC 197 (Rising Temperature — date arithmetic), LC 1393 (Capital Gain/Loss — aggregation)

**Company Frequency:** Oracle (high), TCS (medium), Infosys (medium)

---

### FND Tables Patterns

```sql
-- User + Responsibility + Menu hierarchy
SELECT fu.user_name,
       frt.responsibility_name,
       fmt.menu_name,
       fme.prompt,
       ff.function_name,
       ff.description AS function_desc
FROM   fnd_user fu
       JOIN fnd_user_resp_groups_direct furg ON furg.user_id = fu.user_id
       JOIN fnd_responsibility_tl frt ON frt.responsibility_id = furg.responsibility_id
       JOIN fnd_menu_entries_vl fme ON fme.menu_id = frt.menu_id
       JOIN fnd_form_functions_ll ff ON ff.function_id = fme.function_id
WHERE  fu.end_date IS NULL
       AND frt.language = 'US'
ORDER BY fu.user_name, frt.responsibility_name;
```

**LeetCode Reference:** LC 1179 (Reformat Department Table — pivot), LC 1321 (Restaurant Growth — window functions)

**Company Frequency:** Oracle (high), PwC (high), EY (medium)

---

### Inventory / MFG / MRP Patterns

```sql
-- On-hand quantity with item master and subinventory
SELECT msi.segment1 AS item_number,
       msi.description,
       msib.subinventory_code,
       msib.lot_number,
       msib.quantity_onhand,
       msib.primary_unit_of_measure,
       mic.inventory_item_status_code
FROM   mtl_onhand_quantities_detail msib
       JOIN mtl_system_items_b msi ON msi.inventory_item_id = msib.inventory_item_id
       JOIN mtl_item_categories mic ON mic.inventory_item_id = msi.inventory_item_id
WHERE  msib.organization_id = 101
       AND msib.quantity_onhand > 0
ORDER BY msi.segment1;
```

**LeetCode Reference:** LC 620 (Not Boring Movies — filtering), LC 626 (Exchange Seats — CASE switching)

**Company Frequency:** Accenture (high), Deloitte (high), KPMG (medium)

---

## PL/SQL Patterns

### Multi-Org Architecture (MOAC)

Pattern: VPD policy using `FND_MOBS` and `ORG_ID` context. Always scope queries by `ORG_ID`.

```sql
-- MOAC-safe query pattern
PROCEDURE get_ap_aging(p_vendor_id IN NUMBER) IS
   CURSOR c_invoices IS
      SELECT ai.invoice_num,
             ai.invoice_date,
             ai.invoice_amount,
             SYSDATE - ai.invoice_date AS days_outstanding
      FROM   ap_invoices_all ai
      WHERE  ai.vendor_id = p_vendor_id
             AND ai.org_id IN (SELECT org_id FROM fnd_mobs); -- MOAC scope
BEGIN
   FOR rec IN c_invoices LOOP
      dbms_output.put_line(rec.invoice_num || ' - ' || rec.days_outstanding);
   END LOOP;
END;
```

**LeetCode Reference:** LC 1693 (Daily Leads — grouping), LC 1741 (Total Time — aggregation)

**Company Frequency:** Oracle (high), Accenture (high), Infosys (medium)

---

### Concurrent Manager Patterns

```sql
-- Concurrent program with parameter validation
PROCEDURE inv_reorder_report(errbuf       OUT VARCHAR2,
                              retcode      OUT VARCHAR2,
                              p_org_id     IN  NUMBER,
                              p_category   IN  VARCHAR2) IS
   CURSOR c_items IS
      SELECT msi.segment1,
             msi.description,
             msib.quantity_onhand,
             msi.min_minmax_quantity,
             msi.max_minmax_quantity
      FROM   mtl_system_items_b msi
             JOIN mtl_onhand_quantities msib ON msib.inventory_item_id = msi.inventory_item_id
      WHERE  msi.organization_id = p_org_id
             AND (p_category IS NULL OR msi.item_type = p_category);
BEGIN
   fnd_file.put_line(fnd_file.output, 'ITEM,QTY_ONHAND,MIN,MAX,REORDER');
   FOR rec IN c_items LOOP
      IF rec.quantity_onhand < rec.min_minmax_quantity THEN
         fnd_file.put_line(fnd_file.output,
            rec.segment1 || ',' || rec.quantity_onhand || ',' ||
            rec.min_minmax_quantity || ',' || rec.max_minmax_quantity || ',YES');
      END IF;
   END LOOP;
   retcode := 0;
EXCEPTION
   WHEN OTHERS THEN
      retcode := 2;
      errbuf := SQLERRM;
END;
```

**LeetCode Reference:** LC 182 (Duplicate Emails — grouping with HAVING), LC 596 (Classes > 5 Students)

**Company Frequency:** Oracle (high), Deloitte (high), TCS (medium)

---

### Autonomous Transaction Patterns

```sql
-- EBS standard: PRAGMA AUTONOMOUS_TRANSACTION for logging
PROCEDURE log_request(p_request_id NUMBER) IS
   PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
   INSERT INTO custom_debug_log(request_id, log_timestamp, message)
   VALUES (p_request_id, SYSDATE, 'Processing started');
   COMMIT;
END;
```

**LeetCode Reference:** LC 196 (Delete Duplicate Emails — subquery), LC 197 (Rising Temperature)

**Company Frequency:** All companies (high)

---

## Data Migration Patterns

### SQL\*Loader Pattern mapped to LeetCode

```sql
-- Control file for GL balance migration
-- LeetCode pattern: LC 177 (Nth Highest — ranking), LC 178 (Rank Scores)
OPTIONS (DIRECT=TRUE, ROWS=10000)
LOAD DATA
INFILE 'gl_balances.csv'
INTO TABLE GL_BALANCES
APPEND
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
(
   ledger_id        CHAR(10),
   period_name      CHAR(15),
   code_combination_id CHAR(20),
   actual_flag      CHAR(1),
   period_net_dr    EXPRESSION "TO_NUMBER(:period_net_dr)",
   period_net_cr    EXPRESSION "TO_NUMBER(:period_net_cr)"
)
```

### FNDLOAD Pattern

```bash
# FNDLOAD: Download and upload FND objects
# LeetCode pattern: LC 627 (Swap Salary — UPDATE CASE)
FNDLOAD apps/$APPS_PASS 0 Y DOWNLOAD $FND_TOP/patch/115/import/afscprof.lct
  CUSTOM_PROFILE_FILE.ldt FND_PROFILE_OPTION_VALUES PROFILE_OPTION_NAME "CUSTOM_PROFILE"

FNDLOAD apps/$APPS_PASS 0 Y UPLOAD $FND_TOP/patch/115/import/afscprof.lct
  CUSTOM_PROFILE_FILE.ldt
```

### ADi / AOL Pattern

```bash
# Upload value sets — Journal source, category
# LeetCode pattern: LC 1965 (Missing Info — FULL OUTER JOIN)
FNDLOAD apps/$APPS_PASS 0 Y UPLOAD $FND_TOP/patch/115/import/afffload.lct
  flex_value_set.ldt
```

**LeetCode Reference for Migration:** LC 1978 (Employees with Missing Info), LC 184 (Department Highest Salary), LC 601 (Human Traffic)

**Company Frequency:** Accenture (high), Infosys (high), Deloitte (high), Wipro (medium)

---

## Performance Tuning Patterns

### EBS Query Optimization

```sql
-- BEFORE: Unfiltered + function-based WHERE
SELECT ai.invoice_num, ai.invoice_amount, aps.vendor_name
FROM   ap_invoices_all ai, ap_suppliers aps
WHERE  ai.vendor_id = aps.vendor_id
       AND UPPER(aps.vendor_name) LIKE '%ORACLE%';   -- Full table scan on AP_SUPPLIERS

-- AFTER: Sargable predicate
SELECT ai.invoice_num, ai.invoice_amount, aps.vendor_name
FROM   ap_invoices_all ai
       JOIN ap_suppliers aps ON aps.vendor_id = ai.vendor_id
WHERE  aps.vendor_name LIKE '%Oracle%' ESCAPE '\';

-- Index strategy: create function-based index
-- CREATE INDEX idx_ap_supplier_name_upper ON ap_suppliers(UPPER(vendor_name));
```

**LeetCode Reference:** LC 180 (Consecutive Numbers — self JOIN), LC 185 (Department Top 3 — window + JOIN)

**Company Frequency:** Oracle (high), Amazon (high), Microsoft (high)

---

### Table Partitioning Patterns

```sql
-- Interval partitioning for GL_BALANCES
CREATE TABLE gl_balances_interval (
   ledger_id            NUMBER(15),
   period_name          VARCHAR2(15),
   actual_flag          VARCHAR2(1),
   period_net_dr        NUMBER,
   period_net_cr        NUMBER
)
PARTITION BY RANGE (period_name)
INTERVAL (NUMTOYMINTERVAL(1, 'MONTH'))
(
   PARTITION p_jan2025 VALUES LESS THAN ('FEB-25')
);

-- Local index for partition pruning
CREATE INDEX idx_gl_balances_period_local ON gl_balances_interval(period_name) LOCAL;
```

**LeetCode Reference:** LC 1890 (Latest Login — date filter + aggregation), LC 1364 (Number of Trusted Contacts)

**Company Frequency:** Amazon (high), Microsoft (high), Oracle (medium)

---

### Execution Plan Analysis

```sql
-- EBS SQL tuning: Find full table scans and cartesian joins
SELECT sql_id, child_number, plan_hash_value, executions,
       buffer_gets / NULLIF(executions, 0) AS buffer_gets_per_exec,
       elapsed_time / NULLIF(executions, 0) AS elapsed_ms,
       sql_text
FROM   v$sql
WHERE  sql_text LIKE '%AP_INVOICES_ALL%'
       AND executions > 100
ORDER BY buffer_gets_per_exec DESC;

-- LeetCode: Any problem involving 3+ tables and conditional aggregation
```

**LeetCode Reference:** LC 262 (Trips and Users — complex multi-table), LC 608 (Tree Node — CASE + subquery)

**Company Frequency:** All companies (high)

---

## LeetCode SQL Problem Map by Functional Area

### HRMS — Employee-Related SQL

| LeetCode | Problem | EBS Context | Difficulty |
|----------|---------|-------------|-----------|
| 175 | Combine Two Tables | PER_ALL_PEOPLE_F + PER_ASSIGNMENTS_F join | Easy |
| 176 | Second Highest Salary | PAY_RUN_RESULTS — find second highest salary | Easy |
| 177 | Nth Highest Salary | Ranking employees by salary grade | Medium |
| 178 | Rank Scores | PY_PAYROLL_ACTIONS ranking | Medium |
| 181 | Employees Earning More | Manager > employee (PER_ASSIGNMENTS_F hierarchy) | Easy |
| 184 | Department Highest Salary | Department-wise max element entry | Medium |
| 570 | Managers with 5 Reports | Org hierarchy in PER_ALL_ORGANIZATION_UNITS | Medium |
| 585 | Investments in 2016 | Insurance = element entries | Medium |
| 615 | Average Salary by Department | PAY_RUN_RESULTS by department | Hard |
| 618 | Students Geographical Report | Location-based HR reporting | Hard |

### Financials — Aggregation / Window Functions

| LeetCode | Problem | EBS Context | Difficulty |
|----------|---------|-------------|-----------|
| 197 | Rising Temperature | GL period close comparison | Easy |
| 569 | Median Employee Salary | Median GL balance calculation | Hard |
| 571 | Find Median Given Frequency | Financial median over CCID | Hard |
| 579 | Cumulative Salary | SLA cumulative distribution amounts | Hard |
| 601 | Human Traffic of Stadium | Consecutive high-transaction periods | Hard |
| 602 | Friend Requests II | Intercompany transaction analysis | Medium |
| 1070 | Product Sales Analysis III | GL period-to-date balances | Medium |
| 1097 | Game Play Analysis V | AR revenue recognition patterns | Hard |
| 1127 | User Purchase Platform | AP multi-currency payment analysis | Hard |
| 1174 | Immediate Food Delivery | Payment schedule aging analysis | Medium |
| 1193 | Monthly Transactions I | AP/AR monthly period close | Medium |
| 1204 | Last Person to Board | Payment run cutoff logic | Medium |
| 1212 | Team Scores in Tournament | Intercompany eliminations | Medium |
| 1285 | Find the Start and End Number | GL period range contiguous periods | Medium |
| 1308 | Running Total for Different Genders | GL running balance | Medium |
| 1321 | Restaurant Growth | SLA cumulative balance trend | Medium |
| 1384 | Total Sales Amount by Year | GL annual balance rollforward | Hard |
| 1393 | Capital Gain/Loss | Asset gain/loss (FA) calculation | Medium |
| 1412 | Hard: Find Top-scoring Students | AR credit scoring | Hard |
| 1783 | Grand Slam Titles | Consolidation elimination entries | Medium |
| 1890 | Latest Login | Latest period close date | Easy |
| 2041 | Accepted Candidates | Approval hierarchy in AP holds | Medium |

### Supply Chain — Inventory / Date-Range SQL

| LeetCode | Problem | EBS Context | Difficulty |
|----------|---------|-------------|-----------|
| 180 | Consecutive Numbers | ATP consecutive supply check | Medium |
| 182 | Duplicate Emails | Duplicate supplier/inventory items | Easy |
| 183 | Customers Who Never Order | Items with zero on-hand quantity | Easy |
| 196 | Delete Duplicate Emails | Deduplicate MTL_SYSTEM_ITEMS_B | Easy |
| 262 | Trips and Users | Shipping carrier performance | Hard |
| 534 | Game Play Analysis III | ATP cumulative available | Medium |
| 550 | Game Play Analysis IV | Order fulfillment rate | Medium |
| 574 | Winning Candidate | Top-selling supplier/item | Medium |
| 578 | Get Highest Answer Rate | PO line acceptance rate | Medium |
| 1045 | Customers Who Bought All | Customers who ordered all product lines | Medium |
| 1077 | Project Employees III | Vendor performance scoring | Medium |
| 1355 | Activity Participants | Order line allocation to shipping | Medium |
| 1364 | Number of Trusted Contacts | Supplier trust/approval scoring | Medium |
| 1501 | Countries You Can Safely Invt | Country-based item sourcing compliance | Medium |
| 1555 | Bank Account Summary | AR cash receipt reconciliation | Medium |
| 1596 | Most Frequently Ordered | Fast-moving inventory analysis | Medium |
| 1613 | Find Lost Customers | Lost / no-order customers (AR aging) | Medium |
| 1699 | Number of Calls Between Persons | Inter-org transfers | Medium |
| 1741 | Find Total Time Spent | Manufacturing shop floor time tracking | Easy |
| 1789 | Primary Department | Primary warehouse assignment | Easy |
| 1811 | Find Interview Candidates | Supplier audit / compliance | Medium |
| 1831 | Maximum Transaction Each Day | Daily shipment maximum volumes | Medium |
| 1933 | Check if String is Acronym | Item catalog number validation | Easy |
| 1939 | Users That Actively Request | Frequent reorder patterns | Medium |
| 1972 | First and Last Call On Same Day | First/last transaction date per item | Hard |
| 1978 | Employees Whose Manager Left | Orphaned supplier/inventory records | Easy |
| 2020 | Number of Accounts That Did Not Stream | Items with no transaction in period | Medium |
| 2051 | Category of Each Category | Multi-level item categorization | Medium |

### Manufacturing — MRP / WIP / BOM

| LeetCode | Problem | EBS Context | Difficulty |
|----------|---------|-------------|-----------|
| 596 | Classes > 5 Students | BOM — top-level assemblies with > 5 components | Easy |
| 626 | Exchange Seats | WIP operation sequence swapping | Medium |
| 627 | Swap Salary | BOM component quantity update | Easy |
| 1132 | Reported Posts II | Quality inspection pass/fail reporting | Medium |
| 1141 | User Activity Past 30 Days | MRP demand last 30 days | Easy |
| 1142 | User Activity Past 30 Days II | MRP planning horizon | Easy |
| 1158 | Market Analysis I | WIP completion vs target | Medium |
| 1164 | Product Price at Date | BOM cost roll-up by effective date | Medium |
| 1179 | Reformat Department Table | WIP job pivot by operation | Easy |
| 1241 | Number of Comments per Post | ECO (engineering change order) comments | Easy |
| 1264 | Page Recommendations | Component substitute recommendations | Medium |
| 1270 | All People Report to Manager | BOM multi-level explosion | Medium |
| 1322 | Ads Performance | Manufacturing yield by line | Medium |
| 1336 | Number of Transactions per Visit | Shop floor transaction frequency | Hard |
| 1341 | Movie Rating | Vendor quality rating aggregation | Medium |
| 1350 | Students With Invalid Departments | WIP entities with invalid jobs | Easy |
| 1369 | Get Second Most Recent Activity | Second operation completed in routing | Hard |
| 1421 | NPV Queries | Standard cost vs actual variance | Medium |
| 1440 | Evaluate Boolean Expression | BOM optional vs mandatory components | Medium |
| 1445 | Apples and Oranges | FG / WIP variance reporting | Medium |
| 1459 | Rectangles Area | Shop floor bin location capacity | Medium |
| 1468 | Calculate Salaries | Payroll by manufacturing department | Medium |
| 1501 | Countries You Can Safely Invest | Country of origin / sourcing compliance | Medium |
| 1532 | Most Recent Order | Latest MRP suggestion | Medium |

---

## Java Patterns for EBS

### OAF Framework — BC4J Application Module

```java
// OAF: View Object with bind parameters
public class InvoiceVOImpl extends ViewObjectImpl {
    public void initQuery(Long vendorId, Date fromDate) {
        setWhereClauseParams(null);
        setWhereClause("VENDOR_ID = :1 AND INVOICE_DATE >= :2");
        setWhereClauseParam(0, vendorId);
        setWhereClauseParam(1, fromDate);
        executeQuery();
    }
}
```

**LeetCode Reference:** LC 146 (LRU Cache — data structure), LC 173 (BST Iterator — iterator pattern)

**Company Frequency:** Oracle (high), Deloitte (high), TCS (medium)

---

### OAF Page Controller Pattern

```java
// Controller: processFormRequest for POST operations
public class InvoiceProcessCO extends ControllerImpl {
    @Override
    protected void processFormRequest(OAPageContext pageContext, OAWebBean webBean) {
        String action = pageContext.getParameter("EVENT");
        if ("APPROVE".equals(action)) {
            TransactionEvent event = new TransactionEvent();
            event.setEntityId(pageContext.getParameter("InvoiceId"));
            event.setStatus("APPROVED");
            // Call AM method
            ApplicationModule am = pageContext.getApplicationModule(webBean);
            am.invokeMethod("approveInvoice", event);
        }
    }
}
```

**LeetCode Reference:** LC 232 (Queue with Stacks — delegation), LC 355 (Design Twitter — event system)

**Company Frequency:** Oracle (high), Infosys (medium), Accenture (high)

---

### EBS Java API Patterns

```java
// EBS concurrent program in Java
public class InventoryRevaluation extends JtR12Process {
    @Override
    public void doWork() {
        setPhase("Running revaluation...");
        String orgId = getParameter("ORG_ID");
        String itemId = getParameter("ITEM_ID");

        // FND API call
        InvReleaseAPI.createRevaluation(
            orgId,
            itemId,
            getParameter("NEW_COST")
        );

        writeLog("Revaluation completed for item " + itemId);
    }
}
```

**LeetCode Reference:** LC 208 (Trie — API design), LC 380 (Insert/Delete/Random — set operations)

**Company Frequency:** Oracle (high), Microsoft (medium), Deloitte (medium)

---

### Entity Object Patterns

```java
// BC4J Entity Object with EBS validation
public class InvoiceEOImpl extends EntityImpl {
    @Override
    protected void validateEntity() {
        String invoiceNum = (String) getAttribute("InvoiceNum");
        if (invoiceNum != null && invoiceNum.length() > 50) {
            throw new OAException("Invoice number exceeds 50 characters",
                OAException.ERROR);
        }
        // Check duplicate
        InvoiceVOImpl vo = getInvoiceVO();
        vo.initQuery(invoiceNum);
        if (vo.hasNext()) {
            throw new OAException("Duplicate invoice number",
                OAException.ERROR);
        }
    }
}
```

**LeetCode Reference:** LC 353 (Design Snake Game — validation), LC 460 (LFU Cache — constraints)

**Company Frequency:** Oracle (high), Accenture (high), PwC (medium)

---

## Quick Reference: EBS SQL Keywords to LeetCode Mapping

| EBS Pattern | LeetCode Technique | Example Problems |
|-------------|-------------------|------------------|
| `_ALL` tables with ORG_ID | WHERE + OR clause | 175, 181 |
| FND profile values | Scalar subquery in SELECT | 176, 177 |
| MOAC / VPD | IN clause + subquery | 183, 184 |
| Translation (TL) JOIN | Multi-table JOIN with language filter | 175 (French match) |
| Effective-dated tables (`_F`) | BETWEEN + date range | 197, 601 |
| Concurrent request status | CASE + DECODE | 626, 627 |
| GL period ranges | LEAD/LAG + date diff | 1321, 180 |
| BOM explosion | Recursive CTE | 1270, 570 |
| ATP cumulative available | SUM OVER (ORDER BY date) | 534, 1308 |
| MRP supply/demand netting | FULL OUTER JOIN + COALESCE | 196, 1978 |
