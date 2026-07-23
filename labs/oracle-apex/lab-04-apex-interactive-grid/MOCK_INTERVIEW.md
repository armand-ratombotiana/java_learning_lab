# Mock Interview: APEX Interactive Grid (Lab 04)

**Role:** Oracle APEX Developer (Mid-Level)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is an Interactive Grid and how is it different from an Interactive Report?

**Candidate:** An Interactive Grid is a modern, editable data grid component in Oracle APEX. Key differences from Interactive Report:

| Feature | Interactive Report | Interactive Grid |
|---------|-------------------|-----------------|
| Editable | Read-only | Yes (inline editing) |
| Data entry | Separate form required | In-grid editing |
| Performance | Scales better for large datasets | Optimized for edit operations |
| User experience | View-only with filters/sorts | Spreadsheet-like interaction |
| Master-detail | Separate pages needed | Same-page master-detail |
| Mobile | Responsive | Responsive (optimized) |
| When to use | Reporting, analysis | Data entry, CRUD operations |

Interactive Grid supports inline editing (single or multiple rows), copy/paste from Excel, cell validation, aggregation, and supports both static and dynamic SQL sources.

**Interviewer:** How do you make an Interactive Grid editable?

**Candidate:** Configure these attributes in Page Designer:
1. **Source > Allowed Operations:** Check "Insert", "Update", "Delete" as needed
2. **Source > Query Only:** Set to "No" (to allow DML operations)
3. **Columns > Edit:** Ensure each column's "Type" matches the database column type
4. **Processing tab:** Add an "Interactive Grid - Automatic Row Processing" process
5. Configure **Processing Options**: Return Key, Primary Key Column

The DML process auto-generates INSERT, UPDATE, DELETE statements based on the underlying table/query, provided the query is simple enough (single table with primary key).

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Explain how master-detail Interactive Grids work. Give a real example.

**Candidate:** Master-detail Interactive Grids display a one-to-many relationship on a single page. Example: Orders and Order Line Items.

**Implementation:**
1. Create a Master Grid region for `ORDERS` table (list of orders)
2. Create a Detail Grid region for `ORDER_ITEMS` table
3. In the Detail Grid's **Attributes > Master Detail > Master Region**: Select the master region
4. **Master Column:** Map `ORDER_ID` in detail grid to the master grid's `ORDER_ID`
5. When user selects a row in the master grid, the detail grid filters to show only items for that order

**Key settings:**
- Master Grid: Single Row Select = Yes
- Detail Grid: Foreign Key column + Link to Master Column
- When adding new master rows, the detail grid for the new row becomes available after save
- Detail Grid SQL: `SELECT * FROM order_items WHERE order_id = :master_order_id`

**Example use case:** Order management screen where operators can see the order header and edit line items in the same screen.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a complex inventory management page using Interactive Grids with the following requirements:
- Track products across multiple warehouses
- Show current stock, reserved stock, available stock
- Support stock adjustments with audit trail
- Real-time validation (can't reserve more than available)
- Bulk updates via copy-paste from Excel

**Candidate:** 

**Page architecture:**
```
Page: Inventory Management (PAGE 200)
│
├── Region: Warehouse Selection (Static Content)
│   └── P200_WAREHOUSE (Select List LOV from WAREHOUSES table)
│
├── Region: Current Stock (Interactive Grid) — "read-only summary"
│   └── Source SQL:
│       SELECT p.product_id, p.product_name, p.sku,
│              s.quantity_on_hand AS current_stock,
│              s.quantity_reserved AS reserved_stock,
│              s.quantity_on_hand - s.quantity_reserved AS available_stock,
│              s.reorder_level, s.reorder_quantity
│       FROM products p
│       JOIN stock_levels s ON p.product_id = s.product_id
│       WHERE s.warehouse_id = :P200_WAREHOUSE
│
├── Region: Stock Adjustments (Interactive Grid) — "editable"
│   └── Source SQL:
│       SELECT adjustment_id, product_id, adjustment_type, quantity,
│              reason_code, reference_doc, notes, created_at
│       FROM stock_adjustments
│       WHERE warehouse_id = :P200_WAREHOUSE
│       AND created_at >= SYSDATE - 30
│   └── Columns: Adjustment Type (Select List: ADDITION, REDUCTION, TRANSFER),
│       Quantity (Number), Reason (Select List), Notes (Textarea)
│
└── Dynamic Actions:
    ├── On P200_WAREHOUSE Change → Refresh both grids
    ├── On Stock Adjustment Save → Run PL/SQL validation (available stock check)
    └── On Validation Error → Display inline error, prevent save
```

**Validation logic (before grid submit):**
```sql
DECLARE
    v_available NUMBER;
BEGIN
    SELECT quantity_on_hand - quantity_reserved 
    INTO v_available 
    FROM stock_levels 
    WHERE product_id = :PRODUCT_ID AND warehouse_id = :P200_WAREHOUSE;
    
    IF :ADJUSTMENT_TYPE = 'REDUCTION' AND ABS(:QUANTITY) > v_available THEN
        APEX_ERROR.ADD_ERROR(
            p_message => 'Cannot reduce more than available stock (' || v_available || ' units)',
            p_display_location => APEX_ERROR.C_INLINE_WITH_FIELD,
            p_page_item => 'QUANTITY'
        );
    END IF;
END;
```

**Audit trail:**
```sql
CREATE OR REPLACE TRIGGER trg_stock_adjustment_audit
AFTER INSERT ON stock_adjustments
FOR EACH ROW
BEGIN
    INSERT INTO stock_audit_log(adjustment_id, product_id, warehouse_id, 
        old_qty, new_qty, adjusted_by, adjusted_at)
    VALUES (:NEW.adjustment_id, :NEW.product_id, :NEW.warehouse_id,
        (SELECT quantity_on_hand FROM stock_levels 
         WHERE product_id = :NEW.product_id AND warehouse_id = :NEW.warehouse_id),
        NULL, -- computed by stock update process
        NVL(v('APP_USER'), 'SYSTEM'), SYSTIMESTAMP);
END;
```

**Bulk Excel copy-paste:**
Interactive Grid supports pasting from Excel: users select the grid, paste data, and APEX validates row-by-row. Enable "Paste Enabled" in grid attributes. Each pasted row runs through the same validation process.

---

## Interviewer Feedback

**Strengths:** Deep understanding of Interactive Grid capabilities, practical validation and audit implementation  
**Areas to Improve:** Could discuss Interactive Grid's architecture for handling offline data entry  
**Verdict:** Strong Hire

---

*Lab 04 MOCK_INTERVIEW.md — Part of Oracle APEX Academy Interview Preparation*
