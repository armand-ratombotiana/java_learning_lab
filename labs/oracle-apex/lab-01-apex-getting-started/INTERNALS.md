# APEX Engine Processing

## Page Rendering Phase
When a user navigates to an APEX page, the rendering phase executes:
1. **Session Verification**: validate session ID, check expiration
2. **Authentication**: verify user is authenticated
3. **Authorization**: check page-level authorization
4. **Computation**: execute before-header computations
5. **Region Processing**: for each region:
   - Check region authorization
   - Execute region source (SQL query, PL/SQL, static)
   - Render region using template
6. **Item Processing**: render items with current session state
7. **Button Processing**: render buttons
8. **Dynamic Actions**: initialize client-side behaviors
9. **Navigation**: build breadcrumb and navigation bar

## Page Processing Phase
When a user submits a page, the processing phase executes:
1. **Session Verification**: validate session
2. **Authentication/Authorization**: re-verify
3. **Accept Input**: read submitted values into session state
4. **Computation**: before-submit computations
5. **Validation**: execute all validations
6. **Process**: execute page processes in order
7. **Branch**: execute branch logic

## Component Hierarchy
```
Application
├── Pages
│   ├── Page 0 (Global)
│   └── Pages 1..N
│       ├── Regions
│       │   ├── Items
│       │   ├── Buttons
│       │   └── Dynamic Actions
│       ├── Page Items
│       ├── Page Buttons
│       ├── Page Processes
│       ├── Page Branches
│       ├── Page Validations
│       └── Page Computations
├── Shared Components
│   ├── Application Items
│   ├── Application Processes
│   ├── Lists of Values
│   ├── Navigation
│   ├── Security
│   └── Files
```

## APEX Dictionary Views
Query application metadata:
```sql
-- Application list
SELECT application_id, application_name, application_date_format
FROM apex_applications WHERE availability_status != 'Unavailable';

-- Page list
SELECT application_id, page_id, page_name, page_group, page_function
FROM apex_application_pages WHERE application_id = 100;

-- Regions on a page
SELECT page_id, region_name, region_source_type, source
FROM apex_application_page_regions WHERE page_id = 1;

-- Page items
SELECT page_id, item_name, display_as, source_type
FROM apex_application_page_items WHERE page_id = 1;

-- Dynamic actions
SELECT page_id, da_name, da_event, da_condition_type
FROM apex_application_page_da WHERE page_id = 1;
```
