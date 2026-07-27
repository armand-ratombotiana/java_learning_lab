# Problem Walkthrough: Supply Chain

## Problem 1: Inventory Cycle Count — Company: Oracle
### EBS Interview Scenario
"You're at Oracle consulting for a chemical manufacturer running EBS Inventory. Their annual physical inventory reveals a $2M discrepancy between system quantities and actual counts. The VP of Supply Chain asks you to design a cycle counting program that prevents this going forward."

### The Problem
The client only performs physical inventory once per year with the entire plant shutdown. There is no cycle counting program. They have 50,000 SKUs across 3 warehouses. Analysis of the physical count shows that 80% of the discrepancies come from 20% of the SKUs (ABC analysis), specifically high-value, fast-moving raw materials. The warehouse team has no mobile scanning capability.

### Solution Walkthrough
- Step 1: Perform ABC classification of all SKUs based on annual dollar usage
- Step 2: Implement cycle counting using Oracle Inventory Cycle Counting module
- Step 3: Configure A items for monthly count, B items quarterly, C items annually
- Step 4: Create count schedules by subinventory with rotating assignments
- Step 5: Set up count approval workflow with tolerance limits per ABC class
- Step 6: Generate Cycle Count Request concurrent program scheduling
- Step 7: Integrate with handheld scanners using INV_MATERIAL_STATUS_API
- Step 8: Create discrepancy analysis report with root cause coding

### Code
```sql
-- ABC classification query
SELECT msib.inventory_item_id,
       msib.segment1 AS item_code,
       msib.description,
       SUM(mtla.transaction_quantity * mtla.transaction_cost) AS annual_usage_value,
       SUM(SUM(mtla.transaction_quantity * mtla.transaction_cost)) 
         OVER (ORDER BY SUM(mtla.transaction_quantity * mtla.transaction_cost) DESC) 
         / SUM(SUM(mtla.transaction_quantity * mtla.transaction_cost)) OVER () AS cum_percent
FROM   mtl_system_items_b msib,
       mtl_transaction_lt_account mtla
WHERE  msib.inventory_item_id = mtla.inventory_item_id
AND    msib.organization_id = mtla.organization_id
AND    mtla.transaction_date > SYSDATE - 365
AND    msib.organization_id = 101
GROUP  BY msib.inventory_item_id, msib.segment1, msib.description
ORDER  BY annual_usage_value DESC;

-- Create cycle counting schedule
BEGIN
  inv_cycle_count_api.create_schedule(
    p_organization_id   => 101,
    p_schedule_name     => 'A_ITEMS_MONTHLY',
    p_description       => 'Monthly count for Class A items',
    p_count_frequency   => 'MONTHLY',
    p_start_date        => SYSDATE,
    p_abc_class         => 'A',
    p_subinventory_from => 'RAW',
    p_subinventory_to   => 'RAW',
    p_assign_all_flag   => 'Y'
  );
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: Inventory module architecture — MTL tables, cycle counting APIs, ABC classification, subinventory management.
- Deloitte: Inventory management best practices, warehousing process design, count accuracy KPI methodology.
- Accenture: Multi-site inventory control, global supply chain visibility, RFID/mobile integration patterns.
- PwC: SOX inventory controls, physical count observation procedures, valuation audit.
- Amazon: WMS integration, Kiva robotics picking, real-time inventory visibility with AWS IoT.

---

## Problem 2: Purchase Order Approval Workflow — Company: Deloitte
### EBS Interview Scenario
"You're at Deloitte implementing EBS Procurement for a construction company. After go-live, the procurement team reports that urgent purchase orders are stuck in approval workflow for 3-4 days. The project managers are buying materials out-of-pocket to avoid delays. The CFO is furious."

### The Problem
The client configured a single sequential approval workflow for all POs regardless of amount or urgency. A PO for $500 office supplies requires the same approvals as a $500,000 equipment PO. There are no approval conditions, no delegation rules for vacation, and no escalation for stalled approvals. The workflow AMB_APPROVAL_LIST is processing POs in FIFO order.

### Solution Walkthrough
- Step 1: Review existing approval workflow in Workflow Builder (AMB_APPROVAL_LIST)
- Step 2: Analyze approval routing by PO type, amount, and category
- Step 3: Design multi-path approval workflow using AME (Approvals Management Engine)
- Step 4: Configure amount-based routing (<$5K = Manager, <$50K = Director, >$50K = VP+Finance)
- Step 5: Set up vacation delegation rules and approval escalation after 24 hours
- Step 6: Configure auto-approval for blanket releases under $1K
- Step 7: Implement approval delegation concurrent program to process daily
- Step 8: Create approval cycle time dashboard in Oracle BI Publisher

### Code
```sql
-- Check pending approvals
SELECT poh.segment1 AS po_number,
       poh.creation_date,
       (SYSDATE - poh.creation_date) * 24 AS hours_pending,
       pv.vendor_name,
       poh.authorization_status,
       poh.po_header_id,
       wri.item_type,
       wri.item_key
FROM   po_headers_all poh,
       po_vendors pv,
       wf_items wri
WHERE  poh.vendor_id = pv.vendor_id
AND    wri.item_key = TO_CHAR(poh.po_header_id)
AND    poh.authorization_status = 'IN PROCESS'
AND    poh.creation_date < SYSDATE - 1
ORDER  BY poh.creation_date;

-- Route based on amount using AME
-- Example: Create approval condition in AME (via API)
BEGIN
  ame_util.create_condition(
    p_condition_name       => 'PO_AMOUNT_GT_50K',
    p_condition_type       => 'SQL',
    p_condition_sql        => 'SELECT ''Y'' FROM DUAL WHERE :PO_AMOUNT > 50000',
    p_description          => 'Route to VP approval for POs over $50K',
    p_start_date           => SYSDATE
  );
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: PO approval workflow architecture, AME engine, WF tables (WF_ITEMS, WF_ITEM_ACTIVITY_STATUSES).
- Deloitte: Procurement process design, approval authority matrix, delegation and escalation rules.
- Accenture: Global procurement rollouts, multi-currency approval limits, supplier collaboration portal integration.
- PwC: Procurement SOX controls, approval authority verification, Purchasing card compliance.
- Amazon: AWS Step Functions for approval workflows, automated PO generation with ML-based budget prediction.

---

## Problem 3: WIP Job Closure — Company: Accenture
### EBS Interview Scenario
"You're at Accenture implementing EBS Manufacturing for an aerospace parts manufacturer. They cannot close WIP jobs for completed assemblies, causing $5M in WIP to sit on the balance sheet. The controller says this is impacting their quarterly financial statements."

### The Problem
The client's WIP jobs remain open even after assemblies are completed to inventory. The issue is that some components are being issued to WIP with negative quantities (returns) that are not processed in the correct sequence. The WIP completion transaction fails a validation rule — "Component issue quantity cannot exceed WIP job requirement." Additionally, phantom assemblies are not being automatically issued.

### Solution Walkthrough
- Step 1: Identify all open WIP jobs past their completion date
- Step 2: Run the WIP Job Close Validation concurrent program to find specific errors
- Step 3: Query WIP_TRANSACTIONS_INTERFACE for failed completions
- Step 4: Correct component issue sequencing — reverse and re-issue in correct order
- Step 5: Configure auto-issue for phantom assemblies in BOM
- Step 6: Run WIP Job Close program after corrections
- Step 7: Set up periodic WIP aging report to prevent future accumulation

### Code
```sql
-- Find open WIP jobs past due
SELECT we.wip_entity_name,
       we.creation_date,
       we.last_update_date,
       wdj.date_completed,
       wdj.status_type,
       wdj.quantity_completed,
       wdj.quantity_in_queue,
       wdj.quantity_in_process,
       wdj.quantity_scrapped
FROM   wip_entities we,
       wip_discrete_jobs wdj
WHERE  we.wip_entity_id = wdj.wip_entity_id
AND    wdj.status_type IN (1, 3, 4)  -- Released, Complete, Complete-No Charges
AND    wdj.date_completed IS NULL
AND    wdj.scheduled_completion_date < SYSDATE - 30
AND    wdj.organization_id = 101;

-- Correct component issues for a specific job
DECLARE
  l_txn_id NUMBER;
BEGIN
  -- Reverse the incorrect negative issue
  wip_job_interface_pkg.reverse_transaction(
    p_wip_entity_id   => 12345,
    p_operation_seq   => 10,
    p_organization_id => 101,
    p_transaction_id  => 67890,
    p_reason_code     => 'CORRECTION'
  );
  
  -- Re-issue components in correct sequence
  l_txn_id := wip_job_interface_pkg.create_wip_job_iface(
    p_wip_entity_id     => 12345,
    p_organization_id   => 101,
    p_operation_seq_num => 10,
    p_transaction_type  => 3,
    p_primary_quantity  => 5,
    p_inventory_item_id => 54321
  );
  
  wip_job_interface_pkg.process_wip_job_iface(p_batch_id => l_txn_id);
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: WIP module — WIP_ENTITIES, WIP_DISCRETE_JOBS, WIP_OPERATIONS, WIP_TRANSACTIONS_INTERFACE, BOM explosion.
- Accenture: Manufacturing process design, job-based vs flow manufacturing, engineer-to-order patterns.
- Deloitte: Implementation methodology for discrete manufacturing, period close integration, cost accounting.
- PwC: WIP valuation audit, inventory obsolescence testing, standard cost variance analysis.
- Amazon: Just-in-time manufacturing patterns, AWS IoT for shop floor tracking, ML-based production scheduling.
