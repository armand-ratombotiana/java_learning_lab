# Mock Interview: APEX Advanced Worksheets (Lab 07)

**Role:** Oracle APEX Developer (Mid-Level)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are Advanced Worksheets in APEX and what types are available?

**Candidate:** Advanced Worksheets are specialized region types that provide interactive data visualization and exploration beyond standard reports. APEX includes:
- **Faceted Search:** Multi-faceted browsing with filter counts and dynamic refinement
- **Tree:** Hierarchical data display (org charts, categories)
- **Calendar:** Date-based event display
- **Map:** Geospatial data visualization (Oracle Maps)
- **Chart:** Various chart types (bar, line, pie, scatter, bubble) using Oracle JET
- **Card:** Card-based layout (product listings, user profiles)
- **Media:** Image/video gallery
- **Smart Filters:** Context-aware filter bar

**Interviewer:** How does Faceted Search work and when would you use it?

**Candidate:** Faceted Search provides a user-driven filtering experience on data sets. Users drill down through categories (facets) to narrow results. Each facet shows a count of matching records. When a facet value is selected, other facets update their counts dynamically.

Implementation:
- **Data source:** SQL query returning the base data
- **Facets:** Defined for specific columns (text, number, date, boolean)
- **Display:** A sidebar or top-bar with facet selections; results region updates on selection
- **Configuration:** Facet type, display type (radio, checkbox, range), sort order

Best for: e-commerce product browsing, search result refinement, data exploration tools.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you create a hierarchical Tree region? Give an example.

**Candidate:** A Tree region displays hierarchical data. Configuration:
1. Create a new region > Type = Tree
2. Configure the SQL query with hierarchical columns:
```sql
SELECT 
    department_id AS id,
    parent_department_id AS parent_id,
    department_name AS label,
    'fa fa-folder' AS icon,
    manager_id AS value,
    'Department' AS description,
    'p101' as target_page,
    department_id as target_param_value
FROM departments
START WITH parent_department_id IS NULL
CONNECT BY PRIOR department_id = parent_department_id
ORDER SIBLINGS BY department_name
```

3. Map columns: Node ID, Parent ID, Label, Icon, Tooltip
4. Configure default expansion level and node selection behavior

**Example use case:** Company organizational chart, product category tree, document folder structure.

**Interviewer:** Explain how the Calendar component works with date-based data.

**Candidate:** The Calendar region displays events on a date grid (day, week, month views). Configuration:
1. Create region > Type = Calendar
2. Configure SQL source:
```sql
SELECT 
    project_id AS id,
    project_name AS title,
    start_date AS start_date,
    end_date AS end_date,
    'planned' AS event_type,
    status AS description
FROM projects
```
3. Column mappings: ID, Title, Start Date, End Date, Type (determines color), URL (on-click navigation)
4. Calendar views: Month, Week, Day, Agenda
5. Drag-and-drop rescheduling (update event dates via calendar)
6. Filters using page items: `WHERE status = :P1_STATUS`

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Using APEX's advanced components, design a product catalog with faceted search, a product comparison tool, and a "recently viewed" history.

**Candidate:** 

**Page design:**
```
Page: Product Catalog (PAGE 300)
│
├── Region: Search & Filters (Faceted Search)
│   ├── Facet: Category (Select List) — from CATEGORIES table
│   ├── Facet: Price Range (Range) — min/max price
│   ├── Facet: Brand (Checkbox Group) — from BRANDS table
│   ├── Facet: Rating (Stars/Slider) — min rating filter
│   ├── Facet: In Stock (Switch) — boolean filter
│   └── Facet: Tags (Text) — keyword search
│
├── Region: Results Count (Static Content)
│   └── Display total matched products, clear all filters link
│
├── Region: Product Results (Card Region)
│   └── Card design:
│       ├── Media: Product Image (BLOB)
│       ├── Title: Product Name
│       ├── Subtitle: Brand + SKU
│       ├── Body: Description excerpt (truncated)
│       ├── Badge: Price + Discount badge
│       └── Actions:
│           ├── "View Details" — opens modal page P301
│           ├── "Add to Cart" — AJAX call adds to shopping cart
│           └── "Compare" — adds to comparison list (session state array)
│
├── Region: Recently Viewed (Tree or List Region)
│   └── Source: APEX collection storing last 10 viewed products
│       └── Managed by: On page P301 (detail) load, add product to collection
│
├── Region: Product Comparison (Interactive Grid or Static)
│   └── Shows side-by-side comparison of selected products
│   └── Hidden by default, shown when 2+ products in comparison list
│
└── Dynamic Actions:
    ├── AJAX Call: "Add to Cart"
    │   └── Process: Add to shopping cart session state + refresh cart badge
    ├── AJAX Call: "Compare Toggle"
    │   └── Process: Add/remove from comparison session array
    └── Refresh on Filter Change: Faceted Search auto-refreshes results
```

**Recently Viewed implementation using APEX Collections:**
```sql
-- On product detail page load:
PROCEDURE add_to_recently_viewed(p_product_id NUMBER) IS
BEGIN
    -- Remove duplicate if exists
    APEX_COLLECTION.DELETE_MEMBER(
        p_collection_name => 'RECENTLY_VIEWED',
        p_seq => (SELECT seq_id FROM APEX_COLLECTIONS 
                  WHERE collection_name = 'RECENTLY_VIEWED' 
                  AND c001 = p_product_id)
    );
    
    -- Add to beginning
    APEX_COLLECTION.ADD_MEMBER(
        p_collection_name => 'RECENTLY_VIEWED',
        p_c001 => p_product_id,
        p_c002 => SYSTIMESTAMP
    );
    
    -- Trim to 10 items
    APEX_COLLECTION.TRUNCATE(
        p_collection_name => 'RECENTLY_VIEWED',
        p_max_size => 10
    );
END;
```

**Performance considerations:**
- Faceted Search: Use function-based indexes on commonly filtered columns
- Card images: Store small thumbnails in the database, full images on CDN
- Faceted Search: Use materialized views for large datasets (>50K products)
- Comparison table: Load only selected product details via AJAX, not all at once

---

## Interviewer Feedback

**Strengths:** Strong APEX component knowledge, practical faceted search design, creative use of collections  
**Areas to Improve:** Could discuss Smart Filters vs Faceted Search differences  
**Verdict:** Hire

---

*Lab 07 MOCK_INTERVIEW.md — Part of Oracle APEX Academy Interview Preparation*
