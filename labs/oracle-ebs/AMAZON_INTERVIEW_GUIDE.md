# Amazon Interview Guide — Oracle EBS Academy

## Interview Process for EBS Roles

### Rounds
- **Round 1 – Phone Screen (45 min)**: Leadership Principles (LPs) focused. "Tell me about a time you delivered under tight deadlines" coupled with basic EBS technical questions. Expect "bar raiser" style questions.
- **Round 2 – Technical Deep Dive (1 hr)**: Write a PL/SQL solution to a complex data problem — inventory allocation algorithm, billing reconciliation. Must handle large data volumes (Amazon scale thinking). Data structures and algorithms with EBS context.
- **Round 3 – Systems Design (1 hr)**: Whiteboard a scalable integration between EBS and a custom fulfillment system. Handle high throughput (100K+ transactions/day). Design for fault tolerance, idempotency, async processing.
- **Round 4 – Bar Raiser (1 hr)**: LPs — "Have Backbone; Disagree and Commit", "Bias for Action", "Deliver Results". No technical questions, purely behavioral. The bar raiser has veto power.
- **Timeline**: 3–5 weeks. Amazon is thorough and slow.

### EBS-Specific Expectations
- Amazon cares about scale — your EBS experience must demonstrate handling large transaction volumes, complex integrations, and automation.
- "Amazon-scale thinking" — every answer should consider performance at volume. How would your PL/SQL handle 10M rows? What happens when the concurrent manager queue has 5,000 requests?
- EBS at Amazon is often used alongside custom systems (FC, WMS, TMS). Integration patterns (SQS, SNS, Lambda + EBS) are highly valued.
- OCP certification is irrelevant at Amazon. Results matter.

## Top Technical Problems by Lab

### Lab 01: EBS Architecture and Fundamentals

#### Problem: High-Volume Concurrent Request Submission
- **Difficulty/Frequency**: High / Very Frequent
- **Problem statement**: You need to submit 10,000 concurrent requests nightly to regenerate picking lists across 12 fulfillment centers. The current bottleneck is sequential submission taking 6 hours. Design and implement a batch submission mechanism that completes in under 15 minutes.
- **Interview walkthrough**: Explain `FND_REQUEST.SUBMIT_REQUEST` — normally it's synchronous (waits for completion) or fire-and-forget. For high volume, use parallel submission via `DBMS_SCHEDULER` or concurrent programs that submit child requests. Use `FND_CONC_CLONE` for identical request sets. Manage request thread limits via `FND_CONCURRENT_PROCESSES`. Use `FND_TRACE` for performance diagnostics. Consider table partitioning on `FND_CONCURRENT_REQUESTS` for large volumes.
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE batch_submit_requests (
  p_request_count    IN NUMBER,
  p_program_name     IN VARCHAR2,
  p_parallel_threads IN NUMBER DEFAULT 10
) AS
  TYPE request_id_tab IS TABLE OF NUMBER INDEX BY PLS_INTEGER;
  l_request_ids  request_id_tab;
  l_req_id       NUMBER;

  PROCEDURE submit_single (
    p_thread_num IN NUMBER
  ) IS
    l_req NUMBER;
  BEGIN
    FOR i IN 1..p_request_count LOOP
      l_req := fnd_request.submit_request(
        application   => 'INV',
        program       => p_program_name,
        start_time    => NULL,
        sub_request   => FALSE,
        argument1     => TO_CHAR(i)
      );
      IF l_req > 0 THEN
        INSERT INTO req_batch_log (request_id, thread, status)
        VALUES (l_req, p_thread_num, 'SUBMITTED');
      END IF;
    END LOOP;
    COMMIT;
  END submit_single;

BEGIN
  -- Parallel dispatch using DBMS_SCHEDULER
  FOR t IN 1..p_parallel_threads LOOP
    DBMS_SCHEDULER.create_job(
      job_name   => 'BATCH_REQ_' || t,
      job_type   => 'PLSQL_BLOCK',
      job_action => 'BEGIN batch_submit_single(' || t || '); END;',
      enabled    => TRUE
    );
  END LOOP;

  -- Wait for all threads to complete
  WHILE (SELECT COUNT(*) FROM user_scheduler_jobs
          WHERE job_name LIKE 'BATCH_REQ_%' AND state = 'RUNNING') > 0
  LOOP
    DBMS_LOCK.sleep(10);
  END LOOP;
END batch_submit_requests;
/
```
- **EBS-specific context**: `FND_REQUEST.SUBMIT_REQUEST`, `FND_CONCURRENT_REQUESTS`, `FND_CONC_CLONE`, `FND_CONCURRENT_PROCESSES`, `FND_CONCURRENT_PROGRAMS`, concurrent manager work shifts.
- **What Amazon evaluates**: Scalability thinking, parallel processing, batch optimization. Can you handle EBS at Amazon scale?
- **Follow-ups**: How does the concurrent manager handle request priority? What is a program-specific manager and when would you use one? How do you prevent "runaway" processes?

#### Problem: Idempotent EBS Integration — Inventory Feed
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: Amazon's fulfillment system sends inventory adjustments to EBS every 5 minutes. The integration must be idempotent — if the same message is delivered twice, inventory counts must not be double-counted. Design the solution with PL/SQL.
- **Interview walkthrough**: Idempotency via deduplication — use `MTL_TRANSACTIONS_INTERFACE` with a unique transaction reference (`transaction_reference`, `source_line_id`). Create a log table (`custom_inv_dedup_log`) with a unique constraint on `message_id`. Before inserting into the interface, check if `message_id` exists. If duplicates arrive, log and skip. Use `INV_TXN_MANAGER` to process the interface.
- **SQL/PLSQL/Java solution**:
```sql
CREATE TABLE custom_inv_dedup_log (
  message_id        VARCHAR2(100) PRIMARY KEY,
  source_system     VARCHAR2(30),
  inventory_item_id NUMBER,
  adjustment_qty    NUMBER,
  received_date     DATE,
  processed_flag    VARCHAR2(1) DEFAULT 'N',
  created_date      DATE DEFAULT SYSDATE
);

CREATE UNIQUE INDEX dedup_idx
  ON custom_inv_dedup_log (message_id);

CREATE OR REPLACE PROCEDURE process_inv_feed (
  p_message_id       VARCHAR2,
  p_item_id          NUMBER,
  p_organization_id  NUMBER,
  p_subinventory     VARCHAR2,
  p_adjustment_qty   NUMBER,
  p_transaction_type VARCHAR2,
  p_source           VARCHAR2
) AS
  PRAGMA AUTONOMOUS_TRANSACTION;
  l_count NUMBER;
BEGIN
  -- Idempotency check
  SELECT COUNT(*)
    INTO l_count
    FROM custom_inv_dedup_log
   WHERE message_id = p_message_id;

  IF l_count > 0 THEN
    fnd_file.put_line(fnd_file.log,
      'Dedup: skipping ' || p_message_id);
    RETURN;
  END IF;

  -- Insert into MTL interface
  INSERT INTO mtl_transactions_interface
  (transaction_interface_id, source_code, source_line_id,
   transaction_type_id, transaction_quantity,
   inventory_item_id, organization_id, subinventory_code,
   transaction_date, transaction_uom, transaction_reference)
  VALUES
  (mtl_transactions_interface_s.NEXTVAL,
   p_source, p_message_id,
   DECODE(p_transaction_type, 'ADJUST', 162, 182),
   p_adjustment_qty, p_item_id, p_organization_id,
   p_subinventory, SYSDATE,
   'Ea', p_message_id);

  -- Log dedup key
  INSERT INTO custom_inv_dedup_log
  (message_id, source_system, inventory_item_id,
   adjustment_qty, received_date)
  VALUES
  (p_message_id, p_source, p_item_id,
   p_adjustment_qty, SYSDATE);

  COMMIT;
EXCEPTION
  WHEN DUP_VAL_ON_INDEX THEN
    ROLLBACK;
    fnd_file.put_line(fnd_file.log,
      'Dup msg ' || p_message_id || ' - skipped');
END process_inv_feed;
/
```
- **EBS-specific context**: `MTL_TRANSACTIONS_INTERFACE`, `INV_TXN_MANAGER`, `MTL_MATERIAL_TRANSACTIONS`, `MTL_ONHAND_QUANTITIES`, transaction types.
- **What Amazon evaluates**: Idempotency, fault tolerance, integration design, autonomous transactions for logging.
- **Follow-ups**: How do you handle exactly-once delivery for financial transactions (AR/AP)? What happens if the INV_TXN_MANAGER crashes mid-batch? Explain XA transactions in EBS.

### Lab 04: Supply Chain (INV, PO, OM)

#### Problem: Inventory Allocation Algorithm — Fair Share
- **Difficulty/Frequency**: High / Very Frequent
- **Problem statement**: Amazon's fulfillment network has 15 warehouses. When inventory is constrained, implement a fair-share allocation algorithm that distributes available stock across sales orders based on order priority and demand date. Write the PL/SQL allocation routine.
- **Interview walkthrough**: Use `MTL_ONHAND_QUANTITIES` for available stock. Use `OE_ORDER_LINES_ALL` for demand. Implement a priority queue: P1 (same-day), P2 (next-day), P3 (standard). Allocate iteratively — for each priority level, allocate proportionally by order quantity. Reservation via `OE_RESERVATION_PUB`. Use `MTL_RESERVATIONS` to track committed stock.
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE fair_share_allocate (
  p_item_id          IN NUMBER,
  p_organization_id  IN NUMBER,
  p_ship_set_id      IN NUMBER DEFAULT NULL
) AS
  l_onhand_qty NUMBER;
  l_remaining   NUMBER;

  CURSOR c_demand (p_priority VARCHAR2) IS
    SELECT ool.line_id, ool.ordered_item,
           ool.ordered_quantity - NVL(ool.shipped_quantity, 0) demand_qty,
           ool.flow_status_code,
           ool.request_date
      FROM oe_order_lines_all ool
     WHERE ool.inventory_item_id = p_item_id
       AND ool.open_flag = 'Y'
       AND ool.flow_status_code = 'AWAITING_DELIVERY'
       AND (ool.ordered_quantity - NVL(ool.shipped_quantity, 0)) > 0
       AND (p_ship_set_id IS NULL OR ool.ship_set_id = p_ship_set_id)
       AND ool.attribute_category = p_priority
     ORDER BY ool.request_date;

  PROCEDURE reserve_stock (
    p_line_id  NUMBER,
    p_qty      NUMBER
  ) IS
  BEGIN
    oe_reservation_pub.create_reservation(
      p_api_version_number     => 1.0,
      p_init_msg_list          => 'T',
      p_commit                 => 'T',
      p_inventory_item_id      => p_item_id,
      p_organization_id        => p_organization_id,
      p_demand_source_line_id  => p_line_id,
      p_demand_source_type_id  => 2,
      p_quantity               => p_qty
    );
  END reserve_stock;

BEGIN
  SELECT SUM(primary_transaction_quantity)
    INTO l_onhand_qty
    FROM mtl_onhand_quantities
   WHERE inventory_item_id = p_item_id
     AND organization_id = p_organization_id;

  l_remaining := NVL(l_onhand_qty, 0);

  -- Priority-based allocation: P1, P2, P3
  FOR priority IN ('P1', 'P2', 'P3') LOOP
    EXIT WHEN l_remaining <= 0;
    FOR d IN c_demand(priority) LOOP
      EXIT WHEN l_remaining <= 0;
      DECLARE
        l_alloc NUMBER := LEAST(d.demand_qty, l_remaining);
      BEGIN
        IF l_alloc > 0 THEN
          reserve_stock(d.line_id, l_alloc);
          l_remaining := l_remaining - l_alloc;
        END IF;
      END;
    END LOOP;
  END LOOP;

  COMMIT;
END fair_share_allocate;
/
```
- **EBS-specific context**: `MTL_ONHAND_QUANTITIES`, `MTL_RESERVATIONS`, `OE_ORDER_LINES_ALL`, `OE_RESERVATION_PUB`, `WSH_DELIVERY_DETAILS` (shipping), picking rules.
- **What Amazon evaluates**: Algorithm design, iterative approach, priority-based allocation, reservation API knowledge.
- **Follow-ups**: How would you handle negative on-hand quantities? What is ATP by item on a specific date? Explain backward and forward ATP consumption rules.

### Lab 06: Customization / OAF

#### Problem: OAF Page Performance — Million-Row Order List
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: An OAF page listing sales orders takes 45+ seconds to load for users who manage 50K+ orders. The current implementation uses a single table VO with no pagination or search optimization. Redesign the page and write the key configuration changes and PL/SQL needed.
- **Interview walkthrough**: Profile the AM (Application Module) — check `ViewObject` query, `maxFetchSize`, `setForwardOnly`. Move to `ViewObject` SQL mode with query bind parameters. Implement search filters: `AS_REGIONS=SEARCH` pattern. Use `OADBTransaction` prepared statements with bind variables. Add pagination with `setRangeSize`. For search, use `ViewCriteria` with indexed columns.
- **SQL/PLSQL/Java solution**:
```sql
-- Create efficient VO query (SQL mode)
SELECT ool.line_id,
       ool.order_number,
       ool.ordered_item,
       ool.ordered_quantity,
       ool.unit_selling_price,
       ool.flow_status_code,
       ool.request_date,
       ool.ship_to_org_id,
       ool.booked_flag
  FROM oe_order_lines_all ool
 WHERE (:p_order_number IS NULL OR ool.order_number = :p_order_number)
   AND (:p_item IS NULL OR ool.ordered_item LIKE :p_item || '%')
   AND (:p_status IS NULL OR ool.flow_status_code = :p_status)
   AND (:p_from_date IS NULL OR ool.request_date >= :p_from_date)
   AND (:p_to_date IS NULL OR ool.request_date <= :p_to_date)
 ORDER BY ool.request_date DESC;

-- Create index to support
CREATE INDEX oe_order_lines_search_idx
  ON oe_order_lines_all (order_number, ordered_item,
                          flow_status_code, request_date)
  LOCAL;  -- Partition-aware if table is partitioned

-- AM code snippet (Java)
/*
OAViewObjectImpl vo = (OAViewObjectImpl) getOrdersVO();
vo.setMaxFetchSize(-1);  // Disable row limit
vo.setForwardOnly(true); // Read-only forward scroll
vo.setRangeSize(100);    // Fetch 100 rows per page

// Bind parameters
vo.setWhereClauseParams(null);
vo.setWhereClauseParam(0, searchOrderNum);
vo.setWhereClauseParam(1, searchItem);
vo.setWhereClauseParam(2, searchStatus);
vo.setWhereClauseParam(3, fromDate);
vo.setWhereClauseParam(4, toDate);
vo.executeQuery();
*/
```
- **EBS-specific context**: `OAApplicationModule`, `OAViewObject`, `OADBTransaction`, `ViewCriteria`, `setRangeSize`, `setForwardOnly`, BC4J framework, MDS (MetaData Services).
- **What Amazon evaluates**: Performance under scale, OAF architecture understanding, indexing strategy, pagination.
- **Follow-ups**: Explain BC4J transaction management. How does EBS OAF handle connection pooling? What is the difference between Entity Object and View Object? How does MDS personalize pages?

## EBS-Specific Deep Dive Questions

- **Cloud Migration Strategy**: Amazon runs on AWS. How would you migrate EBS from on-premise to AWS EC2/RDS? What changes in architecture? How do you handle licensing? Use of AWS Direct Connect, load balancers, multi-AZ RDS for EBS.
- **EBS and AWS Integration**: Patterns for integrating EBS with AWS services — SQS for request queueing, Lambda for event-driven processing (e.g., invoice approval via SNS→Lambda→EBS API), S3 for report storage. How does EBS's concurrent manager compare to AWS Batch?
- **Scale Challenges**: Amazon processes transactions at a scale that breaks OOB EBS. How do you architect for 100K+ concurrent users? Table partitioning on `FND_CONCURRENT_REQUESTS`, `MTL_TRANSACTIONS_INTERFACE`. Use of `DBMS_PARALLEL_EXECUTE` for batch jobs.
- **Resilience and Fault Tolerance**: How do you ensure EBS availability in a multi-AZ setup? What happens when the EBS application tier fails mid-transaction? How do you handle concurrent manager failover?
- **Automation at Amazon**: Amazon automates everything. How would you automate EBS patching? Explain automated deployment of EBS patches using ADOP with CI/CD pipeline.
- **Data Engineering**: Amazon is data-driven. How would you build a data pipeline from EBS to Amazon Redshift? Use of GoldenGate, materialized views, or custom batch extract from EBS tables to S3.

## Behavioral Questions (STAR)

- **Situation**: Month-end close took 8 days because of manual GL reconciliation across 5 countries.
  - **Task**: Automate to close in 2 days.
  - **Action**: Designed and built a custom concurrent program that used `GL_DAILY_BALANCES` and `XLA_TRANSACTIONS_GT` to auto-reconcile subledgers to GL. Program ran nightly during close cycle.
  - **Result**: Close cycle reduced to 2.5 days. Eliminated $1.2M in carrying costs from delayed close.

- **Situation**: OAF custom page for fulfillment exception handling timed out for warehouse teams.
  - **Task**: Reduce page load from 60 seconds to under 3 seconds.
  - **Action**: Switched from entity-based VO to SQL-based VO with bind variables. Replaced nested loop query with hash join. Added index on `WSH_DELIVERY_DETAILS(status_code, warehouse_id)`.
  - **Result**: Page load dropped to 0.8 seconds. Warehouse productivity improved 30%.

- **Situation**: EBS concurrent manager queue had 3,500 requests backed up during peak.
  - **Task**: Clear the backlog without data loss.
  - **Action**: Temporarily added 20 target managers for high-priority programs using `FND_CONCURRENT_PROCESSES`. Modified `FND_CONCURRENT_REQUESTS` phase from "Running" to "Standby" for non-critical requests. Re-queued stuck requests.
  - **Result**: Backlog cleared in 2 hours. Zero data loss.

- **Situation**: Inventory discrepancy between EBS and Amazon FC systems reached $5M.
  - **Task**: Reconcile and prevent recurrence.
  - **Action**: Built automated reconciliation using `MTL_ONHAND_QUANTITIES` vs FC feeds via `MTL_TRANSACTIONS_INTERFACE`. Set up real-time alerting for discrepancies > 1%. Created idempotent processing layer.
  - **Result**: Discrepancy reduced to < 0.1%. Reconciliation automated end-to-end.

- **Situation**: Legacy EBS 11i upgrade to R12.2 with 10TB database.
  - **Task**: Upgrade with < 6 hours downtime.
  - **Action**: Used incremental approach — upgraded DB first to 12c then EBS to R12.2. Used ADOP with zero-downtime patching. Performed pre-upgrade analysis with `ADUPDTE.sql` and custom scripts.
  - **Result**: Downtime was 4.2 hours. Zero post-upgrade critical issues.

## Study Plan

| Priority | Labs |
|----------|------|
| Must | Lab 01 (Architecture), Lab 04 (Supply Chain), Lab 06 (OAF), Lab 08 (Integrations) |
| Recommended | Lab 07 (Reporting), Lab 09 (Security), Lab 10 (Upgrade/Migration) |
| Additional | Lab 03 (Financials), Lab 02 (System Admin) |
| Niche | Lab 05 (HRMS) |

### Lab 03: Financials (GL, AP, AR)

#### Problem: Auto-Reconciliation — Bank Statement Processing at Scale
- **Difficulty/Frequency**: High / Frequent
- **Problem statement**: Amazon receives 50,000+ bank transactions daily across 20+ bank accounts. Current EBS bank reconciliation is manual. Write a PL/SQL program that auto-matches bank statement lines to EBS AR receipts and AP payments, and flags unmatched items for review. Handle high volume with batch processing.
- **Interview walkthrough**: Bank statements are in CE_STATEMENT_HEADERS and CE_STATEMENT_LINES. AR receipts in AR_CASH_RECEIPTS_ALL. AP payments in AP_CHECKS_ALL or AP_PAYMENTS_ALL. Match on amount, date range, reference number. Use CE_ACCOUNTING for reconciliation status. For high volume, use PL/SQL bulk collect and FORALL with LIMIT 1000. Use CE_AUTO_RECONCILE_PKG for creating CE matches.
- **SQL/PLSQL/Java solution**:
```sql
CREATE OR REPLACE PROCEDURE auto_reconcile_bank_stmts (
  p_batch_size      IN NUMBER DEFAULT 1000,
  p_date_range_days IN NUMBER DEFAULT 3
) AS
  TYPE stmt_rec_type IS RECORD (
    stmt_line_id NUMBER,
    amount NUMBER,
    trx_date DATE,
    reference VARCHAR2(100)
  );
  TYPE stmt_tab IS TABLE OF stmt_rec_type;
  l_stmt_lines stmt_tab;

  CURSOR c_unmatched IS
    SELECT csl.statement_line_id,
           csl.trx_amount,
           csl.gl_date,
           csl.reference
      FROM ce_statement_lines csl
     WHERE csl.status = 'NEW'
       AND csl.gl_date >= SYSDATE - p_date_range_days
       AND NOT EXISTS (
           SELECT 1 FROM ce_auto_reconcile_log
            WHERE stmt_line_id = csl.statement_line_id
              AND status = 'MATCHED'
       );

  PROCEDURE match_to_receipt (
    p_stmt_line stmt_rec_type
  ) IS
    l_match_found BOOLEAN := FALSE;
  BEGIN
    FOR rec IN (
      SELECT cash_receipt_id, amount, receipt_date
        FROM ar_cash_receipts_all
       WHERE ABS(amount - p_stmt_line.amount) < 0.01
         AND receipt_date BETWEEN p_stmt_line.trx_date - 1
                               AND p_stmt_line.trx_date + 1
         AND status = 'UNAPP'
         AND ROWNUM <= 1
    ) LOOP
      ce_auto_reconcile_pkg.create_reconciliation(
        p_statement_line_id => p_stmt_line.stmt_line_id,
        p_source_id         => rec.cash_receipt_id,
        p_source_type       => 'CASH_RECEIPT'
      );
      INSERT INTO ce_auto_reconcile_log
      (stmt_line_id, source_type, source_id, amount, status, created_date)
      VALUES (p_stmt_line.stmt_line_id, 'CASH_RECEIPT',
              rec.cash_receipt_id, rec.amount, 'MATCHED', SYSDATE);
      l_match_found := TRUE;
    END LOOP;

    IF NOT l_match_found THEN
      INSERT INTO ce_auto_reconcile_log
      (stmt_line_id, source_type, source_id, amount, status, created_date)
      VALUES (p_stmt_line.stmt_line_id, 'NONE', NULL,
              p_stmt_line.amount, 'UNMATCHED', SYSDATE);
    END IF;
  END match_to_receipt;

BEGIN
  OPEN c_unmatched;
  LOOP
    FETCH c_unmatched BULK COLLECT INTO l_stmt_lines LIMIT p_batch_size;
    EXIT WHEN l_stmt_lines.COUNT = 0;
    
    FORALL i IN 1..l_stmt_lines.COUNT
      match_to_receipt(l_stmt_lines(i));
    
    COMMIT;
  END LOOP;
  CLOSE c_unmatched;
END auto_reconcile_bank_stmts;
/
```
- **EBS-specific context**: CE_STATEMENT_LINES, CE_STATEMENT_HEADERS, AR_CASH_RECEIPTS_ALL, AP_CHECKS_ALL, CE_AUTO_RECONCILE_PKG, CE_ACCOUNTING, CE_LOOKUPS.
- **What Amazon evaluates**: High-volume batch processing, bulk collect/FORALL, bank reconciliation process knowledge, performance at scale.
- **Follow-ups**: How do you handle partial matches (e.g., one bank line matches two receipts)? Explain the CE reconciliation aging process. How does EBS handle multi-currency bank reconciliation?

## Tips

- **Think big**: Amazon expects "Day 1" thinking. When presented with a problem, ask "how does this scale?" or "how do we automate this?"
- **Leadership Principles**: Know the 16 LPs. Frequently used ones for EBS roles: "Bias for Action" (quickly resolve issues), "Deliver Results" (automate close cycle), "Have Backbone" (push back on unnecessary customization).
- **Automation is key**: Amazon hates manual processes. Every EBS interview answer should include automation. "I wrote a concurrent program to..." is a good start.
- **Performance at volume**: Always discuss performance. Mention indexing strategy, partitioning, query tuning — EBS at Amazon scale.
- **No certifications talk**: Amazon doesn't care about OCP. Talk about results, not credentials.
- **Integration mindset**: Amazon uses EBS alongside dozens of custom systems. Be ready to discuss integration patterns, messaging, and APIs.
