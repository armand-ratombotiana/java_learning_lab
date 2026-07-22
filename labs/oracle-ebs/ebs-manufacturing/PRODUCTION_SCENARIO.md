# Production Scenarios: EBS Manufacturing

## Scenario 1: WIP Job Completion Fails, Inventory Not Updated
**Context**: An electronics manufacturer uses EBS WIP to track production of circuit boards.
**Problem**: WIP job completion transactions failed with "INV_QUANTITY_TREE_PUB failure" error. Completed units were not moved to finished goods inventory. Production line was stopped for 3 hours.
**Root Cause**: The `MTL_ONHAND_QUANTITIES` for the finished goods subinventory had a corrupted quantity tree. The material status was set to "Block" for the completion subinventory. The `MTL_MATERIAL_STATUSES` table had an incorrect status assignment.
**Solution**: 1) Identified the error: `SELECT * FROM MTL_TRANSACTION_INTERFACE WHERE PROCESS_FLAG = 'E' AND ERROR_MESSAGE LIKE '%INV_QUANTITY_TREE_PUB%'`. 2) Rebuilt the quantity tree using `INV_QTY_TREE_PUB.PROCESS_CHANGES`. 3) Corrected the material status using `MTL_MATERIAL_STATUSES_PUB.UPDATE_ROW`. 4) Re-processed failed transactions from `MTL_TRANSACTIONS_INTERFACE`. 5) Implemented nightly validation of quantity tree integrity.
**Lessons Learned**: Monitor MTL_TRANSACTION_INTERFACE for errors proactively. Validate material statuses periodically. Maintain quantity tree rebuild procedures. Implement automated recovery for transaction interface errors.

## Scenario 2: MRP Explosion Taking 12 Hours
**Context**: A discrete manufacturer runs daily MRP to plan production for 50,000 SKUs.
**Problem**: MRP explosion took 12 hours, exceeding the overnight batch window. Production planners did not have updated plans when they arrived in the morning.
**Root Cause**: MRP was regenerating all items every night instead of using net-change. The `MRP_FORECAST_DATES` table had no index on `FORECAST_ITEM_ID`. The BOM explosion was processing all levels for all items without parallel execution.
**Solution**: 1) Identified bottleneck through `V$SESSION_WAITS` — full table scans on `MRP_FORECAST_DATES`. 2) Added index on `MRP_FORECAST_DATES(FORECAST_ITEM_ID, FORECAST_DATE)`. 3) Changed MRP to use net-change planning (only items with changes since last run). 4) Increased MRP planner parallel workers from 2 to 8. 5) Partitioned MRP tables by plan ID.
**Lessons Learned**: Use net-change MRP instead of full regeneration. Index MRP tables properly. Tune MRP parallel processing. Monitor MRP execution time and set alerts for breaches.

## Scenario 3: BOM Structure Corrupted After Migration
**Context**: A company migrated BOM data from a legacy system to EBS using APIs.
**Problem**: After migration, multi-level BOMs showed incorrect component quantities and missing items at lower levels. Production used wrong component quantities, causing material shortages.
**Root Cause**: The migration script used `BOM_BOMPUB` API but did not correctly handle the `BOM_IMPORT_NET` logic for multi-level BOMs. Component quantities were imported with `COMPONENT_QUANTITY = 0` instead of the correct value. The BOM explosion did not validate imported data.
**Solution**: 1) Froze production for BOM-affected items. 2) Used `BOM_BILL_OF_MATERIALS_API` to query and identify incorrect quantities. 3) Corrected component quantities using `BOM_BOMPUB.UPDATE_BOM`. 4) Re-imported BOMs with proper validation: `BOM_IMPORT_NET.CHECK_BOM_VALIDITY`. 5) Implemented pre- and post-migration BOM comparison scripts.
**Lessons Learned**: Validate imported BOM data before making it live. Use `BOM_IMPORT_NET` for multi-level import validation. Implement BOM comparison reports. Test BOM import in sandbox with full explosion.

## Scenario 4: Unauthorized Engineering BOM Changes
**Context**: An engineer changed the BOM for a high-volume product to substitute a cheaper component.
**Problem**: The cheaper component did not meet quality specifications. 10,000 units were manufactured with the wrong part before the change was discovered. $500K in rework cost.
**Root Cause**: The engineer had direct access to update production BOMs. No change control or approval workflow was enforced for engineering BOM changes. The change was made to the production BOM, not the engineering BOM. No ECO (Engineering Change Order) was created.
**Solution**: 1) Immediately reversed the BOM change and quarantined affected units. 2) Implemented ECO workflow: all BOM changes must go through `ENG_ECOS` with approval routing. 3) Segregated Engineering BOM from Production BOM. 4) Created approval workflow for BOM changes with quality and manufacturing review. 5) Implemented BOM change audit trail: `ENG_REVISED_ITEMS` and `ENG_CHANGE_ORDERS`.
**Lessons Learned**: Never allow direct changes to production BOMs. Implement ECO workflow for all BOM changes. Segregate engineering and production BOMs. Audit BOM changes weekly. Enforce change control for all product structures.
