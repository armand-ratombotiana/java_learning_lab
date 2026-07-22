# Production Scenarios: EBS Supply Chain

## Scenario 1: ATP Check Shows Incorrect Availability
**Context**: An e-commerce company uses EBS ATP to promise delivery dates to customers. During a flash sale, ATP showed 1000 units available for a popular item, but only 100 were actually in stock.
**Problem**: Customers ordered 800 units based on ATP promises. Only 100 could be fulfilled. 700 orders were delayed, causing customer complaints and penalty fees.
**Root Cause**: The ATP calculation was reading from a stale snapshot of `MTL_ONHAND_QUANTITIES`. The ATP refresh frequency was set to 6 hours. The flash sale triggered 10,000 orders in 1 hour, but the ATP had not refreshed. The snapshot was built at midnight and was 6 hours stale.
**Solution**: 1) Immediately stopped accepting orders for the affected item. 2) Adjusted ATP refresh frequency to 5 minutes for high-velocity items. 3) Implemented real-time ATP using `MRP_ATP_V` instead of snapshot tables. 4) Added a safety stock buffer: ATP shows 90% of actual on-hand for flash sale items. 5) Implemented order promising with allocation: reserve inventory at order entry.
**Lessons Learned**: ATP must be near real-time for high-volume sales. Use allocation-based order promising for flash sales. Implement safety stock buffers. Monitor ATP accuracy regularly.

## Scenario 2: Order Import Running Slowly
**Context**: A retailer imports 200,000 orders daily from their e-commerce platform to EBS OM.
**Problem**: Order Import concurrent program took 8 hours to process, delaying fulfillment by an entire day. Orders were queuing up faster than they could be imported.
**Root Cause**: Order Import was running sequentially without batch processing. The `OE_ORDER_HEADERS_ALL` table had missing indexes on `INTERFACE_HEADER_ID` and `PROCESS_FLAG`. The concurrent program was using single-threaded processing.
**Solution**: 1) Added missing indexes on `OE_INTERFACE_HEADERS_ALL(PROCESS_FLAG, INTERFACE_HEADER_ID)`. 2) Changed Order Import to run in batch mode: 10,000 orders per batch using `OE_IMPORT_PUB`. 3) Increased the number of Order Import workers from 1 to 5. 4) Scheduled import to run every 30 minutes instead of once daily. 5) Created partitioned interface tables by date for faster purging.
**Lessons Learned**: Tune Order Import with proper indexes and batch processing. Increase concurrent workers for parallel processing. Use incremental processing instead of batch-of-all. Monitor interface table growth and purge regularly.

## Scenario 3: Inventory Quantity Corrupted After Miscount Adjustment
**Context**: A physical inventory count was entered incorrectly in EBS INV.
**Problem**: After entering the count adjustment, `MTL_ONHAND_QUANTITIES` showed negative quantities for 200 SKUs. Orders could not be shipped. The warehouse could not determine actual stock levels.
**Root Cause**: The person entering the count entered "50" instead of "500" for a popular item. The system accepted the negative adjustment because `MTL_PARAMETERS.NEGATIVE_INVESTORY_REP_CODE` allowed negative inventory. The adjustment was made to the primary subinventory, cascading to all other subinventories.
**Solution**: 1) Froze all inventory transactions for the affected items. 2) Identified the incorrect adjustment from `MTL_ADJUSTMENTS` and `MTL_TRANSACTION_ACCOUNTING`. 3) Reversed the adjustment with a correcting entry: negative inventory adjustment of -450 units. 4) Re-counted the physical inventory for the affected items. 5) Disabled negative inventory balancing at the organization level. 6) Implemented approval workflow for inventory adjustments above a threshold.
**Lessons Learned**: Never allow negative inventory in production. Implement approval workflows for physical inventory adjustments. Train warehouse staff on proper count procedures. Audit inventory adjustments regularly.

## Scenario 4: Unauthorized PO Approval via Workflow Bypass
**Context**: A procurement manager discovered a purchase order for $500,000 that they did not approve.
**Problem**: The PO was in "Approved" status with no record of workflow approval. The requisition was created by a junior buyer and approved without proper authorization.
**Root Cause**: The workflow approval routing rule had a loophole: if the requisition total was spread across multiple distributions, each under $10,000, no approval was required. The buyer split the $500K requisition into 50 distributions of $10K each. The approval rule checked each distribution value instead of the total.
**Solution**: 1) Flagged and audited the unauthorized PO. 2) Implemented approval rule that checks total requisition value, not per-distribution. 3) Added hard limit: any requisition > $10K requires approval, regardless of distribution splitting. 4) Implemented Segregation of Duties: buyer cannot approve their own requisitions. 5) Added periodic audit of approved POs to detect split-order patterns.
**Lessons Learned**: Design approval rules to check total value, not per-line. Implement anti-splitting logic in workflow rules. Conduct periodic PO audits for split-order fraud. Implement SOD for procurement.
