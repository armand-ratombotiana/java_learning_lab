# Problem Walkthrough: Design a Master-Detail Page with Dynamic Actions

## Problem Statement

You are an APEX developer tasked with building an order management dashboard for an e-commerce client. The application must provide:

1. A master region showing orders with key details (order number, customer, date, status, total)
2. A detail region showing line items for the selected order
3. Dynamic actions that synchronize the detail view when the master selection changes
4. Inline editing of order status and line item quantities
5. A real-time order total recalculation when line items change

The master-detail relationship should work without page submission — selecting an order in the master region should immediately update the detail region and summary totals via AJAX.

### Technical Requirements
- Oracle Database 19c+ with sample schema (OE or custom)
- APEX 23.2+
- No page submission on master selection change
- Master selection persists across page navigation within the same session
- Support for large datasets: 100K+ orders, 500K+ line items

### Success Criteria
- Master-detail synchronization completes in under 500ms
- Inline updates persist without full page refresh
- Order total recalculates correctly after any line item change
- New line items can be added dynamically
- Error handling for invalid quantities and missing inventory

---

## Step-by-Step Walkthrough

### Step 1: Database Setup

```sql
-- Customers table
CREATE TABLE customers (
    customer_id   NUMBER PRIMARY KEY,
    first_name    VARCHAR2(50) NOT NULL,
    last_name     VARCHAR2(50) NOT NULL,
    email         VARCHAR2(100) UNIQUE NOT NULL,
    phone         VARCHAR2(20),
    created_date  DATE DEFAULT SYSDATE
);

-- Orders table (master)
CREATE TABLE orders (
    order_id      NUMBER PRIMARY KEY,
    customer_id   NUMBER NOT NULL REFERENCES customers(customer_id),
    order_date    DATE NOT NULL,
    status        VARCHAR2(20) DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED')),
    total_amount  NUMBER(12,2) DEFAULT 0,
    notes         VARCHAR2(1000),
    created_by    VARCHAR2(100),
    created_date  DATE DEFAULT SYSDATE
);

-- Order items table (detail)
CREATE TABLE order_items (
    item_id       NUMBER PRIMARY KEY,
    order_id      NUMBER NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    product_name  VARCHAR2(200) NOT NULL,
    quantity      NUMBER NOT NULL CHECK (quantity > 0),
    unit_price    NUMBER(10,2) NOT NULL,
    line_total    NUMBER(12,2) GENERATED ALWAYS AS (quantity * unit_price) VIRTUAL
);

-- Sequences
CREATE SEQUENCE customer_seq START WITH 100;
CREATE SEQUENCE order_seq START WITH 1000;
CREATE SEQUENCE item_seq START WITH 10000;

-- Indexes for performance
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_items_order ON order_items(order_id);

-- Sample data (20 customers, 50 orders, ~200 line items)
INSERT ALL
    INTO customers VALUES (1, 'John', 'Smith', 'john.smith@email.com', '555-0101', SYSDATE)
    INTO customers VALUES (2, 'Jane', 'Doe', 'jane.doe@email.com', '555-0102', SYSDATE)
    INTO customers VALUES (3, 'Robert', 'Johnson', 'rjohnson@email.com', '555-0103', SYSDATE)
SELECT * FROM dual;

INSERT INTO orders VALUES (1001, 1, SYSDATE - 5, 'CONFIRMED', 299.99, 'Rush delivery', 'APP', SYSDATE);
INSERT INTO orders VALUES (1002, 1, SYSDATE - 3, 'SHIPPED', 549.50, NULL, 'APP', SYSDATE);
INSERT INTO orders VALUES (1003, 2, SYSDATE - 1, 'PENDING', 129.99, 'Gift wrap', 'APP', SYSDATE);

INSERT INTO order_items VALUES (10001, 1001, 'Wireless Mouse', 2, 49.99);
INSERT INTO order_items VALUES (10002, 1001, 'USB-C Hub', 1, 89.99);
INSERT INTO order_items VALUES (10003, 1001, 'Laptop Sleeve', 1, 110.02);
INSERT INTO order_items VALUES (10004, 1002, 'Monitor 27"', 1, 349.99);
INSERT INTO order_items VALUES (10005, 1002, 'Keyboard', 1, 199.51);
INSERT INTO order_items VALUES (10006, 1003, 'Webcam HD', 1, 129.99);

COMMIT;
```

### Step 2: Create the Application and Page Layout

1. Create Application **Order Manager**
2. Create a new **Blank Page** (Page 1) named "Order Management"
3. Set **Page Mode**: Normal (not modal)

**Page Layout Structure:**

```
┌──────────────────────────────────────────────────────┐
│  Region: ORDER MANAGEMENT (Title)                     │
├────────────────────────┬─────────────────────────────┤
│  Region: Orders        │  Region: Order Items         │
│  (Interactive Report)  │  (Interactive Grid)          │
│                        │                              │
│  Columns:              │  Columns:                    │
│  - Order ID 🔗         │  - Product Name (edit)       │
│  - Customer            │  - Quantity (edit)           │
│  - Date                │  - Unit Price (edit)         │
│  - Status              │  - Line Total (read-only)    │
│  - Total Amount        │                              │
│  - Notes               │  Button: [Add Row]           │
│                        │  Button: [Save Changes]      │
├────────────────────────┴─────────────────────────────┤
│  Region: Order Summary                                │
│  - Selected Orders: 1                                 │
│  - Items: 3                                           │
│  - Subtotal: $299.99                                  │
│  - Status: CONFIRMED                                  │
└──────────────────────────────────────────────────────┘
```

### Step 3: Configure the Master Region (Orders Interactive Report)

1. Create a **Region**: "Orders"
   - Type: Interactive Report
   - Source:
   ```sql
   SELECT
       o.order_id,
       c.first_name || ' ' || c.last_name AS customer_name,
       o.order_date,
       o.status,
       o.total_amount,
       o.notes,
       (SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = o.order_id) AS item_count
   FROM orders o
   JOIN customers c ON c.customer_id = o.customer_id
   ORDER BY o.order_date DESC
   ```

2. **Link Column Configuration**:
   - Column: `ORDER_ID`
   - Link Text: `#ORDER_ID#`
   - Target: `f?p=&APP_ID.:1:&SESSION.:::P1_SELECTED_ORDER:#ORDER_ID#`
   - Link Attributes: `class="select-order-link"`

3. **Report Attributes**:
   - Pagination: Server-side, 15 rows
   - Search Bar: Enabled
   - Highlight: Enable row highlighting on selection

4. **Page Item: P1_SELECTED_ORDER** (Hidden, Value Protected)
   - Source: Null
   - Used to track which order is selected

### Step 4: Configure the Detail Region (Order Items Interactive Grid)

1. Create a **Region**: "Order Items"
   - Type: Interactive Grid
   - Source:
   ```sql
   SELECT
       oi.item_id,
       oi.order_id,
       oi.product_name,
       oi.quantity,
       oi.unit_price,
       oi.line_total
   FROM order_items oi
   WHERE oi.order_id = :P1_SELECTED_ORDER
   ORDER BY oi.item_id
   ```

2. **Interactive Grid Attributes**:
   - **Edit**: Enabled
   - **Toolbar**: Show (Add Row, Save, Reset)
   - **Pagination**: Server-side, 50 rows
   - **Row Actions**: Enable Edit, Delete columns

3. **Column Attributes**:
   - `PRODUCT_NAME`: Text Field, required
   - `QUANTITY`: Number Field, required, min=1, max=999
   - `UNIT_PRICE`: Number Field, required, format: 999G999G990D00
   - `LINE_TOTAL`: Display Only, format: 999G999G990D00, source: `quantity * unit_price`

4. **Dynamic Rendering**: Do not render this region if `P1_SELECTED_ORDER` is null
   - Server-side Condition: `P1_SELECTED_ORDER IS NOT NULL`

### Step 5: Dynamic Actions for Master-Detail Synchronization

**Dynamic Action 1: Select Order from Master**
- Event: **Click** on link column in Orders IR
- Client-side Condition: None

**True Action 1.1: Set Value**
- Set `P1_SELECTED_ORDER` to the clicked ORDER_ID
- Fire on Page Load: No

**True Action 1.2: Refresh Detail Region**
- Action: Refresh
- Affected Element: Region "Order Items"

**True Action 1.3: Refresh Summary Region**
- Action: Refresh
- Affected Element: Region "Order Summary"

**True Action 1.4: Execute JavaScript**
- Code:
  ```javascript
  // Highlight selected row
  $('tr.highlight-row').removeClass('highlight-row');
  $(this.triggeringElement).closest('tr').addClass('highlight-row');

  // Update page title or banner
  $('#P1_SELECTED_ORDER_DISPLAY').text('Order #' + apex.item('P1_SELECTED_ORDER').getValue());
  ```

**Dynamic Action 2: Refresh on Page Load**
- Event: Page Load
- True Action: Execute PL/SQL
- Code (check if selection persisted):
  ```sql
  NULL;
  ```
- Then Refresh detail and summary regions conditionally

### Step 6: Order Summary Region

1. Create a **Region**: "Order Summary"
   - Type: Classic Report
   - Source:
   ```sql
   SELECT
       o.order_id,
       c.first_name || ' ' || c.last_name AS customer,
       o.order_date,
       o.status,
       o.total_amount,
       o.notes,
       COUNT(oi.item_id) AS total_items,
       SUM(oi.line_total) AS computed_total
   FROM orders o
   JOIN customers c ON c.customer_id = o.customer_id
   LEFT JOIN order_items oi ON oi.order_id = o.order_id
   WHERE o.order_id = :P1_SELECTED_ORDER
   GROUP BY o.order_id, c.first_name, c.last_name, o.order_date,
            o.status, o.total_amount, o.notes
   ```

2. **Template**: Use "Value Attribute Pairs" or a custom card template for a compact summary view
3. **Condition**: `P1_SELECTED_ORDER IS NOT NULL`

### Step 7: Inline Editing and Save Process

**Process 1: Save Order Items (IG Save)**
- Type: Interactive Grid — Save Data
- Affected Region: "Order Items"
- Editable Region: order_items
- **Set up primary key**: ITEM_ID

**Process 2: Recalculate Order Total (After IG Save)**
- Type: PL/SQL Code
- Code:
  ```sql
  UPDATE orders o
  SET o.total_amount = (
      SELECT NVL(SUM(oi.line_total), 0)
      FROM order_items oi
      WHERE oi.order_id = o.order_id
  )
  WHERE o.order_id = :P1_SELECTED_ORDER;
  ```
- Success Message: "Order updated successfully"

**Dynamic Action 3: After Line Item Change**
- Event: **After Refresh** on "Order Items" region
- True Action: Execute JavaScript to recalc inline totals:
  ```javascript
  // APEX Interactive Grid handles the line_total virtual column automatically
  // This DA catches after-save to refresh summary
  apex.event.trigger('custom-refresh-summary');
  ```

**Dynamic Action 4: Custom Refresh Listener**
- On Page (Global) — Custom Event: `custom-refresh-summary`
- True Action: Refresh "Order Summary" region

### Step 8: Add Row to Detail

The Interactive Grid toolbar includes an **Add Row** button by default. To customize:

1. In IG toolbar, enable **Insert Row**
2. Set default values for new rows:
   - `ORDER_ID`: `P1_SELECTED_ORDER` (via default or processing)
3. After adding multiple rows, user clicks **Save** to persist all changes at once

### Step 9: Real-Time Total Recalculation

1. Create a **Dynamic Action** on the IG columns `QUANTITY` and `UNIT_PRICE`:
   - Event: **Change**
   - Selection Type: Column(s) in Interactive Grid — `QUANTITY`, `UNIT_PRICE`

2. True Action: Execute JavaScript
   ```javascript
   function recalcLineTotal(model, rowId) {
       var record = model.getRecord(rowId);
       var qty = model.getValue(record, 'QUANTITY');
       var price = model.getValue(record, 'UNIT_PRICE');
       var total = (parseFloat(qty) || 0) * (parseFloat(price) || 0);
       model.setValue(record, 'LINE_TOTAL', total);
   }
   ```

3. This provides client-side recalculation before saving. The server-side virtual column ensures consistency.

### Step 10: Error Handling and Validation

**Validation 1: Quantity > 0 (On Submit of IG)**
- Type: PL/SQL Function Body (Returning Error Message)
- Code:
  ```sql
  DECLARE
      l_count NUMBER;
  BEGIN
      SELECT COUNT(*) INTO l_count
      FROM order_items
      WHERE order_id = :P1_SELECTED_ORDER
        AND quantity <= 0;
      IF l_count > 0 THEN
          RETURN 'Quantity must be greater than zero for all items.';
      END IF;
      RETURN NULL;
  END;
  ```

**Validation 2: Inventory Check (Before Save)**
- Type: PL/SQL Function Body (Returning Error Message)
- Code:
  ```sql
  DECLARE
      l_stock NUMBER;
      l_shortage VARCHAR2(4000);
  BEGIN
      FOR item IN (
          SELECT product_name, quantity
          FROM order_items
          WHERE order_id = :P1_SELECTED_ORDER
      ) LOOP
          SELECT NVL(MAX(stock_quantity), 0) INTO l_stock
          FROM inventory WHERE product_name = item.product_name;

          IF l_stock < item.quantity THEN
              l_shortage := l_shortage || ', ' || item.product_name
                  || ' (ordered: ' || item.quantity || ', available: ' || l_stock || ')';
          END IF;
      END LOOP;

      IF l_shortage IS NOT NULL THEN
          RETURN 'Insufficient inventory for: ' || LTRIM(l_shortage, ', ');
      END IF;
      RETURN NULL;
  END;
  ```

### Step 11: Adding Dynamic Actions for Status Transitions

**Dynamic Action: Update Order Status**
1. Add a **Select List** page item `P1_NEW_STATUS` in the summary region
   - LOV: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
   - Condition: `P1_SELECTED_ORDER IS NOT NULL`

2. Add **Button**: "Update Status" next to the select list

3. Dynamic Action: On Click of "Update Status"
   - True Action: Execute PL/SQL
   ```sql
   UPDATE orders
   SET status = :P1_NEW_STATUS,
       updated_date = SYSDATE
   WHERE order_id = :P1_SELECTED_ORDER;

   -- Check for valid transitions
   DECLARE
       l_old_status VARCHAR2(20);
   BEGIN
       SELECT status INTO l_old_status FROM orders WHERE order_id = :P1_SELECTED_ORDER;
       CASE
           WHEN l_old_status = 'SHIPPED' AND :P1_NEW_STATUS = 'PENDING' THEN
               RAISE_APPLICATION_ERROR(-20001, 'Cannot revert from SHIPPED to PENDING');
           WHEN l_old_status = 'DELIVERED' AND :P1_NEW_STATUS != 'CANCELLED' THEN
               RAISE_APPLICATION_ERROR(-20002, 'Only cancellation allowed after delivery');
           ELSE NULL;
       END CASE;
   END;
   ```
   - Then Refresh: "Orders" IR, "Order Items" IG, "Order Summary" — to reflect new status

### Step 12: Advanced: Tabular Form Alternative Using Classic Report + APEX_ITEM

If Interactive Grid is not available (older APEX), use a Classic Report with `APEX_ITEM` for inline editing:

```sql
SELECT
    oi.item_id,
    oi.order_id,
    APEX_ITEM.TEXT(1, oi.product_name, 50, 200) AS product_name,
    APEX_ITEM.NUMBER(2, oi.quantity, 5) AS quantity,
    APEX_ITEM.NUMBER(3, oi.unit_price, 10, 2) AS unit_price,
    oi.line_total
FROM order_items oi
WHERE oi.order_id = :P1_SELECTED_ORDER;
```

Process the array with:
```sql
BEGIN
    FOR i IN 1..APEX_APPLICATION.G_F01.COUNT LOOP
        UPDATE order_items SET
            product_name = APEX_APPLICATION.G_F01(i),
            quantity = APEX_APPLICATION.G_F02(i),
            unit_price = APEX_APPLICATION.G_F03(i)
        WHERE item_id = APEX_APPLICATION.G_F04(i);
    END LOOP;
END;
```

---

## Best Practices Applied

1. **Master-Detail Design**: Separate regions with shared state via page item
2. **Performance**: Server-side pagination on both regions, indexed foreign keys
3. **User Experience**: No page submission on selection, instant detail refresh
4. **Data Integrity**: Virtual column for line total, validation for negative quantities
5. **Maintainability**: Clear naming (P1_SELECTED_ORDER), modular dynamic actions
6. **Scalability**: With 100K orders, pagination and indexed queries keep performance

## Common Pitfalls to Avoid

1. **Missing ON DELETE CASCADE**: Order items must be deleted when order is deleted
2. **Not refreshing on selection change**: Ensure all dependent regions refresh
3. **IG default values**: New rows may not pick up P1_SELECTED_ORDER automatically — set in Default processing
4. **Race conditions**: Multiple fast clicks may trigger overlapping AJAX calls — debounce if needed
5. **Authorization**: Add row-level security to restrict orders by customer/sales rep

## Extensions for Future Iterations

1. Drag-and-drop reordering of line items
2. Barcode scanning for product lookup
3. PDF invoice generation from order data
4. Email notification on status change
5. Partial shipment tracking with multiple statuses per item
6. Integration with inventory management system via REST
