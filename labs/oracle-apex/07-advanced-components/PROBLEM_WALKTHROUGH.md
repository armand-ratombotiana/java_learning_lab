# Problem Walkthrough: Advanced Components

## Problem 1: Interactive Grid with Custom Editing — Oracle
### APEX Interview Scenario
"Oracle's client needs an Excel-like editable grid for order entry with validation on each cell."

### Problem
Build an Interactive Grid for order line items with cell validation, calculated columns, and bulk save.

### Solution Walkthrough
1. **Create Interactive Grid Region** — Source: `SELECT * FROM order_items WHERE order_id = :P1_ORDER_ID`
2. **Enable Editing** — Set "Edit" → "Enabled" with "Update, Insert, Delete" allowed
3. **Add Validation** — Toolbar → Add validation:
   - `quantity > 0`
   - `unit_price > 0`
4. **Add Calculated Column** — Line total = `quantity * unit_price` (save or display only)
5. **Add Dynamic Action** — After cell edit, recalculate totals
6. **Configure Primary Key** — Define `order_item_id` as primary key column
7. **Bulk Processing** — Enable "Save All Rows" button (single AJAX call)

### Code
```sql
-- Interactive Grid SQL
SELECT order_item_id, order_id, product_id, product_name,
       quantity, unit_price, (quantity * unit_price) AS line_total
FROM order_items
WHERE order_id = :P1_ORDER_ID
ORDER BY line_item_no;

-- Validation function
CREATE OR REPLACE FUNCTION validate_order_item(
    p_product_id  IN NUMBER,
    p_quantity    IN NUMBER
) RETURN VARCHAR2 AS
    l_stock NUMBER;
BEGIN
    SELECT quantity_on_hand INTO l_stock
    FROM inventory WHERE product_id = p_product_id;

    IF p_quantity > l_stock THEN
        RETURN 'Insufficient stock: only ' || l_stock || ' available.';
    END IF;
    RETURN NULL;
END;
/
```

### Company Evaluation
- **Oracle**: IG architecture, cell-level validation, primary key setup
- **Deloitte**: Data entry UX optimization, bulk edit client demos
- **Accenture**: Complex inline validation rules for enterprise workflows

---

## Problem 2: Oracle JET Charts — Deloitte
### APEX Interview Scenario
"Deloitte's client needs an executive dashboard with multiple chart types: bar, line, pie, and a gauge."

### Problem
Create a dashboard page with four Oracle JET charts driven by one shared date filter.

### Solution Walkthrough
1. **Add Date Filter Item** — Date picker `P2_FISCAL_YEAR`
2. **Bar Chart** — Revenue by month:
   ```sql
   SELECT TO_CHAR(order_date,'MON') AS label, SUM(amount) AS value
   FROM orders WHERE fiscal_year = :P2_FISCAL_YEAR
   GROUP BY TO_CHAR(order_date,'MON')
   ```
3. **Line Chart** — Cumulative revenue trend (SQL with analytic function)
4. **Pie Chart** — Revenue by product category
5. **Gauge Chart** — Target vs. actual (single value)
6. **Synchronized Filter** — DA on date picker: refresh all chart regions
7. **Chart Customization** — Set colors, labels, tooltips in Series attributes

### Code
```sql
-- Bar chart series
SELECT TO_CHAR(order_date, 'MON') AS label, SUM(amount) AS value
FROM orders
WHERE EXTRACT(YEAR FROM order_date) = :P2_FISCAL_YEAR
GROUP BY TO_CHAR(order_date, 'MON'), EXTRACT(MONTH FROM order_date)
ORDER BY EXTRACT(MONTH FROM order_date);

-- Gauge chart series (single value)
SELECT SUM(amount) AS actual, 5000000 AS target
FROM orders
WHERE EXTRACT(YEAR FROM order_date) = :P2_FISCAL_YEAR;
```

### Company Evaluation
- **Deloitte**: Executive-level data visualization, storytelling with data
- **Oracle**: JET chart customization, animation, responsive sizing
- **Accenture**: Drill-down linking from charts to detail reports

---

## Problem 3: APEX Plugins & Dynamic Actions — Accenture
### APEX Interview Scenario
"Accenture needs a custom date picker that shows only business days. No suitable built-in component exists."

### Problem
Create a custom APEX plugin (Item Type) that extends date picker to disable weekends and holidays.

### Solution Walkthrough
1. **Create Plugin** — Shared Components → Plugins → Create "Business Date Picker"
2. **Set Properties** — Standard Item Type, extend from Date Picker
3. **JavaScript Implementation** — Override `beforeShow` and `beforeShowDay`:
   ```javascript
   function( pItem ) {
       return {
           beforeShowDay: function(date) {
               var day = date.getDay();
               if (day === 0 || day === 6) return [false, '', 'Weekend'];
               // Check holiday table via AJAX callback
               var holiday = apex.server.process('CHECK_HOLIDAY', {
                   x01: apex.date.format(date, 'YYYY-MM-DD')
               });
               return holiday ? [false, '', 'Holiday'] : [true, '', ''];
           }
       };
   }
   ```
4. **AJAX Callback** — PL/SQL in plugin render function:
   ```sql
   BEGIN
       SELECT COUNT(*) INTO :P_HOLIDAY_COUNT
       FROM holidays WHERE holiday_date = TO_DATE(:APEX$X01, 'YYYY-MM-DD');
   END;
   ```
5. **Package Plugin** — Export `.sql` file for reuse
6. **Implement in App** — Replace existing date pickers with new plugin

### Code
```sql
-- Holiday table
CREATE TABLE holidays (
    holiday_date DATE PRIMARY KEY,
    description  VARCHAR2(100)
);

-- Insert sample holidays
INSERT INTO holidays VALUES (TO_DATE('2026-01-01','YYYY-MM-DD'), 'New Year');
INSERT INTO holidays VALUES (TO_DATE('2026-12-25','YYYY-MM-DD'), 'Christmas');
COMMIT;
```

### Company Evaluation
- **Accenture**: Custom plugin development, reusability, component libraries
- **Oracle**: Plugin framework, AJAX callbacks, JavaScript API
- **Deloitte**: Rapid development with standard components vs. custom plugins

---

## Problem 4: Oracle Spatial & APEX Maps — Oracle
### APEX Interview Scenario
"Oracle's customer wants to display store locations on an interactive map with clustering."

### Problem
Show 5,000 store locations on an APEX map region with clustering and info windows.

### Solution Walkthrough
1. **Enable Spatial** — Ensure `MDSYS` schema and `SDO_GEOMETRY` available
2. **Store Table** — Add `location SDO_GEOMETRY` column:
   ```sql
   ALTER TABLE stores ADD (location SDO_GEOMETRY);
   ```
3. **Create Map Region** — Page Designer → Region Type: "Map"
4. **Set Map Layer** — Source: SQL query with coordinates:
   ```sql
   SELECT store_id, store_name,
          SDO_UTIL.TO_GEOJSON(location) AS geometry,
          address, city, phone
   FROM stores
   ```
5. **Enable Clustering** — In Map Layer → "Clustering" → "Grid" or "K-means"
6. **Configure Info Window** — HTML expression showing store details
7. **Add Search** — Geocoding search bar for address lookup
8. **Set Zoom** — Initial zoom level, bounds

### Code
```sql
-- Update store location (geocode from address)
UPDATE stores
SET location = SDO_GIS.GEOCODE_AS_GEOMETRY(
    'USA', 'ADDR', NULL, address || ', ' || city || ', ' || state
)
WHERE location IS NULL;

-- Map layer query
SELECT store_id AS id, store_name AS label,
       JSON_OBJECT('city' KEY city, 'phone' KEY phone, 'hours' KEY operating_hours) AS attributes,
       SDO_UTIL.TO_GEOJSON(location) AS geometry
FROM stores
WHERE (:P3_CITY IS NULL OR city = :P3_CITY);
```

### Company Evaluation
- **Oracle**: Spatial integration, GeoJSON format, map layer optimization
- **Accenture**: Geospatial analytics for logistics clients
- **Deloitte**: Interactive map dashboards for retail clients
