# Mock Interview: APEX Page Builder (Lab 02)

**Role:** Oracle APEX Developer (Junior)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are the main components of the APEX Page Designer?

**Candidate:** Page Designer is the primary development interface for building APEX pages. Its main components:
- **Rendering tab:** Page structure — regions, items, buttons, page processes
- **Processing tab:** Page processing — computations, validations, processes, branches
- **Page Shared Components tab:** Application-level shared elements
- **Property Editor (right pane):** Configure the selected component
- **Gallery (center):** Drag-and-drop component palette
- **Layout/Component views:** Switch between tree view and visual layout

The Page Designer replaced the older Component View interface as of APEX 5.0+.

**Interviewer:** What types of regions does APEX support and when would you use each?

**Candidate:** 
- **Static Content:** Custom HTML content — for headers, static text, embedded HTML
- **Classic Report:** Read-only tabular data — for simple data display, printable reports
- **Interactive Report:** Filterable, sortable data grid with user personalization — for ad-hoc analysis
- **Interactive Grid:** Editable spreadsheet-like data grid — for data entry, CRUD operations
- **Form:** Single record display/edit — for detail views, data entry forms
- **Chart:** Chart visualizations (based on Oracle JET) — for dashboards
- **Map:** Location data visualization — for geospatial data
- **Tree:** Hierarchical data display — for organizational charts
- **PL/SQL Dynamic Content:** Custom-rendered regions using PL/SQL — for complex custom outputs
- **List:** Navigation or content lists
- **Breadcrumb:** Page hierarchy navigation

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Explain APEX items and their types. How do you configure item attributes?

**Candidate:** APEX items are form elements that capture or display data. Common types:
- **Text Field:** Single-line text input
- **Textarea:** Multi-line text input
- **Number Field:** Numeric input with validation
- **Select List:** Drop-down list (static values or SQL query)
- **Radio Group:** Multiple choice, single selection
- **Checkbox:** Multiple choice, multiple selection
- **Date Picker:** Date selection
- **Switch:** Toggle on/off
- **Hidden:** Stored in session but not displayed
- **Display Only:** Read-only value display
- **File Upload:** File upload with BLOB storage

Item configuration includes:
- **Source:** Where the item gets its value (database column, session state, static)
- **Default:** Default value (static, SQL, PL/SQL, item reference)
- **Formatting:** CSS classes, templates, icon prefix/suffix
- **Validation:** Required, format mask, min/max values, custom validation
- **Security:** Session state protection, escape special characters
- **Advanced:** Read-only conditions, authorization, help text

**Interviewer:** How do you implement master-detail relationships in APEX?

**Candidate:** Master-detail (one-to-many) can be implemented several ways:

1. **Interactive Grid Master-Detail:** Create two Interactive Grid regions on same page. Detail grid has a `master column` that filters based on selected row in master grid. Configured through the Master-Detail attribute of the detail grid.

2. **Form + Interactive Grid:** Master region is a form showing the parent record. Detail region is an Interactive Grid or Classic Report on the child table. Pass the master's primary key as a bind variable in the detail SQL: `WHERE department_id = :P1_DEPT_ID`

3. **Modal Dialog:** Master page shows a list. "View Details" button opens a modal dialog page that receives the master key via `item` or `clear cache` URL parameters.

4. **Tabular Form (Legacy):** Older approach using tabular forms — replaced by Interactive Grid in modern APEX.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design an employee management page with the following requirements: search by department, display employee list, edit employee details inline, and save changes.

**Candidate:** 

**Page design:**
```
Page: Employee Management (PAGE 100)
│
├── Region: Search Filters (Static Content)
│   ├── Item: P100_DEPARTMENT (Select List, LOV from DEPARTMENTS table)
│   ├── Item: P100_EMPLOYEE_NAME (Text Field, search/filter)
│   └── Button: SEARCH (actions: refresh employee grid)
│
├── Region: Employees (Interactive Grid)
│   └── Source SQL: 
│       SELECT employee_id, first_name, last_name, email, phone_number,
│              hire_date, job_id, salary, department_id, manager_id
│       FROM employees
│       WHERE (:P100_DEPARTMENT IS NULL OR department_id = :P100_DEPARTMENT)
│       AND (:P100_EMPLOYEE_NAME IS NULL OR 
│            UPPER(first_name || ' ' || last_name) LIKE UPPER('%' || :P100_EMPLOYEE_NAME || '%'))
│
├── Process: Save Employees (Interactive Grid - Automatic Row Processing, DML)
│
└── Dynamic Action: SEARCH Click
    └── True Action: Refresh [Employees Interactive Grid]
```

**Key configurations:**
- Interactive Grid: Editable = Yes, Allowed Operations = Insert, Update, Delete
- Search button: Set to "Redirect and Set Value" to submit and refresh
- Department select list: Include "All Departments" option with NULL value
- Interactive Grid has built-in row validation, error display, and change tracking

**Advanced features to add:**
- Add a "Reset" button that clears search fields and refreshes grid
- Add an "Export to CSV" button using Interactive Grid built-in export
- Implement row highlighting for recently modified rows
- Add inline validation (salary > 0, email format, hire_date not in future)

**Interviewer:** How would you add a "View Employee Details" modal dialog that shows when clicking a row in the grid?

**Candidate:**
1. Create a new page (P101) with page type = Modal Dialog
2. Add items to display employee details (Display Only items)
3. Add a "Close" button with action = "Cancel Dialog"
4. In the Interactive Grid, add a link column or a button that opens the modal:
   - Link target: `f?p=&APP_ID.:101:&SESSION.:::&DEBUG.::P101_EMPLOYEE_ID:EMPLOYEE_ID`
   - Link attributes: `data-modal="true"` class for modal behavior
5. Alternatively, use Dynamic Action: "Single Row Click" on the Interactive Grid → "Redirect" to PAGE 101 with P101_EMPLOYEE_ID = selected row's EMPLOYEE_ID

---

## Interviewer Feedback

**Strengths:** Good page design understanding, practical search/filter implementation, clear master-detail knowledge  
**Areas to Improve:** Could discuss APEX 23.x Universal Theme capabilities  
**Verdict:** Hire

---

*Lab 02 MOCK_INTERVIEW.md — Part of Oracle APEX Academy Interview Preparation*
