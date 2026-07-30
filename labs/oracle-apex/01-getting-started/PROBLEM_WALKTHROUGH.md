# Problem Walkthrough: Build a CRUD Application with Interactive Report and Form

## Problem Statement

You are an APEX developer at a consulting firm. A client needs a department expense tracking application that allows managers to:
1. View all expenses in an Interactive Report with sorting, filtering, and searching
2. Add new expense records via a form
3. Edit existing expense records
4. Delete expense records with confirmation
5. View expense summaries by category and month

The client has no existing application and needs a fully functional prototype delivered within a week. The application must be built entirely in Oracle APEX with a single database schema.

### Technical Requirements
- Oracle Database 19c or later
- Oracle APEX 23.2 or later
- Single workspace with one schema
- No external integrations
- Responsive design for desktop and tablet
- Row-level security (users can only see their own department's expenses)

### Success Criteria
- Interactive Report loads within 2 seconds on 10,000 records
- Form validation prevents duplicate entries
- Delete requires confirmation dialog
- Expenses can be filtered by date range and category
- Summary charts show at-a-glance spending patterns

---

## Step-by-Step Walkthrough

### Step 1: Database Objects

Create the underlying tables, sequences, and indexes for the expense tracking application.

```sql
-- Core expense table
CREATE TABLE expenses (
    expense_id      NUMBER PRIMARY KEY,
    department_id   NUMBER NOT NULL,
    employee_id     NUMBER NOT NULL,
    category        VARCHAR2(50) NOT NULL,
    amount          NUMBER(12,2) NOT NULL,
    expense_date    DATE NOT NULL,
    description     VARCHAR2(500),
    status          VARCHAR2(20) DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    created_by      VARCHAR2(100),
    created_date    DATE DEFAULT SYSDATE,
    updated_by      VARCHAR2(100),
    updated_date    DATE
);

-- Department reference table
CREATE TABLE departments (
    department_id   NUMBER PRIMARY KEY,
    department_name VARCHAR2(100) NOT NULL,
    manager_email   VARCHAR2(100)
);

-- Sequence for expense IDs
CREATE SEQUENCE expense_seq START WITH 1000 INCREMENT BY 1;

-- Indexes for report performance
CREATE INDEX idx_expenses_date ON expenses(expense_date);
CREATE INDEX idx_expenses_dept ON expenses(department_id);
CREATE INDEX idx_expenses_category ON expenses(category);
CREATE INDEX idx_expenses_status ON expenses(status);

-- Sample data
INSERT INTO departments VALUES (10, 'Engineering', 'eng-mgr@company.com');
INSERT INTO departments VALUES (20, 'Marketing', 'mkt-mgr@company.com');
INSERT INTO departments VALUES (30, 'Sales', 'sales-mgr@company.com');
INSERT INTO departments VALUES (40, 'Operations', 'ops-mgr@company.com');

INSERT INTO expenses VALUES (expense_seq.NEXTVAL, 10, 101, 'Travel', 450.00,
    SYSDATE - 5, 'Client meeting - NYC', 'APPROVED', 'JSMITH', SYSDATE - 5, NULL, NULL);
INSERT INTO expenses VALUES (expense_seq.NEXTVAL, 10, 101, 'Office Supplies', 89.50,
    SYSDATE - 3, 'Printer toner and paper', 'APPROVED', 'JSMITH', SYSDATE - 3, NULL, NULL);
INSERT INTO expenses VALUES (expense_seq.NEXTVAL, 20, 201, 'Marketing', 1200.00,
    SYSDATE - 7, 'Trade show booth materials', 'PENDING', 'JDOE', SYSDATE - 7, NULL, NULL);
INSERT INTO expenses VALUES (expense_seq.NEXTVAL, 20, 201, 'Travel', 320.75,
    SYSDATE - 2, 'Conference registration', 'PENDING', 'JDOE', SYSDATE - 2, NULL, NULL);
INSERT INTO expenses VALUES (expense_seq.NEXTVAL, 30, 301, 'Entertainment', 156.00,
    SYSDATE - 1, 'Client dinner', 'APPROVED', 'BROWN', SYSDATE - 1, NULL, NULL);
INSERT INTO expenses VALUES (expense_seq.NEXTVAL, 30, 301, 'Travel', 890.00,
    SYSDATE - 14, 'Flight to Chicago', 'REJECTED', 'BROWN', SYSDATE - 14, NULL, NULL);
INSERT INTO expenses VALUES (expense_seq.NEXTVAL, 40, 401, 'Software', 299.00,
    SYSDATE - 4, 'Annual license renewal', 'PENDING', 'WILSON', SYSDATE - 4, NULL, NULL);
INSERT INTO expenses VALUES (expense_seq.NEXTVAL, 10, 102, 'Training', 1500.00,
    SYSDATE - 30, 'AWS certification course', 'APPROVED', 'NGUYEN', SYSDATE - 30, NULL, NULL);

COMMIT;
```

### Step 2: Create the Application

1. Navigate to App Builder → Create → New Application
2. Name: **Expense Tracker**
3. Set: **Schema** = your workspace schema
4. Add Pages:
   - Check **Interactive Report** for table `EXPENSES`
   - Check **Form** for table `EXPENSES` (linked to report)
   - Check **Interactive Report** for table `DEPARTMENTS`
   - Add a blank page: **Dashboard**

5. Set Application Attributes:
   - **Application Items**: `G_DEPT_ID` (for row-level security)
   - **Authorization Scheme**: "Is Manager" — PL/SQL function body:
     ```sql
     DECLARE
         l_count NUMBER;
     BEGIN
         SELECT COUNT(*) INTO l_count
         FROM departments
         WHERE manager_email = :APP_USER;
         RETURN l_count > 0;
     END;
     ```
   - **Authentication**: Oracle APEX Accounts (for development)

6. Theme: **Vita — Slider** (or **Redwood** if APEX 23.2+)

### Step 3: Configure the Interactive Report (Page 1)

Set the **Region Source** to the following SQL query:

```sql
SELECT
    e.expense_id,
    e.expense_date,
    e.category,
    e.amount,
    e.description,
    e.status,
    d.department_name,
    e.created_by,
    e.created_date
FROM expenses e
JOIN departments d ON d.department_id = e.department_id
WHERE e.department_id = NVL(:G_DEPT_ID, e.department_id)
ORDER BY e.expense_date DESC;
```

**Report Attributes**:
- **Search Bar**: Enable (position: top)
- **Actions Menu**: Enable with all default actions
- **Download**: Enable CSV, XLSX, PDF
- **Pagination**: Server-side, 25 rows
- **Link Column**: Set the Expense ID column as a link to Page 2 (Form)
  - Target: `f?p=&APP_ID.:2:&SESSION.:::P2_EXPENSE_ID:#EXPENSE_ID#`
- **Column Formatting**:
  - Amount: Format mask `FMT_L` (999G999G999G999G990D00)
  - Status: Use a **Badge List of Values**: PENDING=yellow, APPROVED=green, REJECTED=red
  - Expense Date: Format mask `DD-MON-YYYY`

**Filters**:
- Add a page item `P1_DATE_FROM` (Date Picker) in a Static Content region above the report
- Add a page item `P1_DATE_TO` (Date Picker)
- Modify the report WHERE clause:
  ```sql
  AND e.expense_date BETWEEN NVL(:P1_DATE_FROM, e.expense_date)
                         AND NVL(:P1_DATE_TO, e.expense_date)
  ```

**Dynamic Action**: On Change of `P1_DATE_FROM` or `P1_DATE_TO`:
- Event: Change
- Action: Refresh the Interactive Report region

### Step 4: Configure the Form (Page 2)

1. Set **Page Mode**: Modal Dialog
2. Set **Form Region Source**: `SELECT * FROM expenses WHERE expense_id = :P2_EXPENSE_ID`

3. **Form Items** (adjust order and layout):

| Item | Type | Source Column | Required | Notes |
|------|------|---------------|----------|-------|
| P2_EXPENSE_ID | Hidden | EXPENSE_ID | — | Primary key |
| P2_DEPARTMENT_ID | Select List | DEPARTMENT_ID | Yes | LOV: SELECT department_id, department_name FROM departments |
| P2_EMPLOYEE_ID | Number Field | EMPLOYEE_ID | Yes | |
| P2_CATEGORY | Select List | CATEGORY | Yes | Static LOV: Travel, Office Supplies, Marketing, Entertainment, Software, Training, Other |
| P2_AMOUNT | Number Field | AMOUNT | Yes | Format mask: 999G999G990D00 |
| P2_EXPENSE_DATE | Date Picker | EXPENSE_DATE | Yes | |
| P2_DESCRIPTION | Textarea | DESCRIPTION | No | Rows: 4 |
| P2_STATUS | Select List | STATUS | Yes | Static LOV: PENDING, APPROVED, REJECTED |
| P2_CREATED_BY | Display Only | CREATED_BY | — | Read-only |
| P2_CREATED_DATE | Display Only | CREATED_DATE | — | Read-only |

4. **Automatic Row Fetch**: Process `Fetch Row from EXPENSES` — runs on page load, condition: `P2_EXPENSE_ID IS NOT NULL`

5. **Process: Save** (After Submit):
   - Type: Automatic Row Processing (DML)
   - Table Name: `EXPENSES`
   - Target Type: Single Row — `UPDATE` if `P2_EXPENSE_ID` is not null, else `INSERT`
   - Set `CREATED_BY` to `:APP_USER`
   - Set `CREATED_DATE` to `SYSDATE`

6. **Buttons**:
   - **SAVE**: Submit page
   - **DELETE**: Create a button with action "Delete" — requires confirmation
     - Confirmation message: "Are you sure you want to delete this expense?"
   - **CANCEL**: Redirect to Page 1 (or close dialog)

7. **Validation: Duplicate Check** (Before Processing):
   - Type: PL/SQL Function Body (Returning Boolean)
   - Validation Point: On Submit — Before Processing
   - Code:
     ```sql
     DECLARE
         l_count NUMBER;
     BEGIN
         SELECT COUNT(*) INTO l_count
         FROM expenses
         WHERE category = :P2_CATEGORY
           AND amount = :P2_AMOUNT
           AND expense_date = :P2_EXPENSE_DATE
           AND description = :P2_DESCRIPTION
           AND (:P2_EXPENSE_ID IS NULL OR expense_id != :P2_EXPENSE_ID);
         RETURN l_count = 0;
     END;
     ```
   - Error Message: "A matching expense record already exists."

### Step 5: Add Dashboard Page (Page 3)

Create summary visualizations for the expense data.

**Region 1: Summary Statistics (Classic Report)**
Source:
```sql
SELECT
    COUNT(*) AS total_expenses,
    ROUND(SUM(amount), 2) AS total_amount,
    ROUND(AVG(amount), 2) AS avg_amount,
    MAX(amount) AS largest_expense,
    MIN(expense_date) AS oldest_expense,
    MAX(expense_date) AS newest_expense
FROM expenses
WHERE department_id = NVL(:G_DEPT_ID, department_id);
```

**Region 2: Expenses by Category (Pie Chart)**
Source:
```sql
SELECT category AS label, SUM(amount) AS value
FROM expenses
WHERE department_id = NVL(:G_DEPT_ID, department_id)
GROUP BY category
ORDER BY value DESC;
```

**Region 3: Monthly Spending Trend (Bar Chart)**
Source:
```sql
SELECT TO_CHAR(expense_date, 'YYYY-MM') AS label,
       SUM(amount) AS value
FROM expenses
WHERE department_id = NVL(:G_DEPT_ID, department_id)
GROUP BY TO_CHAR(expense_date, 'YYYY-MM')
ORDER BY label;
```

### Step 6: Navigation Setup

1. **Navigation Menu**: List-based (default)
2. Create entries:
   - **Dashboard** → Page 3
   - **Expenses** → Page 1
   - **Departments** → Page for departments IR
3. Set **Authorization** on menu entries — only "Is Manager" can see Departments

### Step 7: Row-Level Security

1. Create an Application Item: `G_DEPT_ID`
2. Create an Application Computation:
   - Point: After Authentication
   - Code:
     ```sql
     SELECT department_id
     INTO :G_DEPT_ID
     FROM departments
     WHERE manager_email = :APP_USER;
     ```
3. Modify all report queries to include `WHERE department_id = NVL(:G_DEPT_ID, department_id)`

### Step 8: Testing and Validation

1. **Login as different users**:
   - JSMITH (Engineering manager) — should see only Engineering expenses
   - JDOE (Marketing manager) — should see only Marketing expenses
   - Create a new expense, verify it appears in the report
   - Edit an expense, verify changes persist
   - Delete an expense, verify confirmation dialog

2. **Test report features**:
   - Sort by amount column
   - Filter by category using the Actions menu
   - Download as CSV
   - Use date range filters

3. **Test form validation**:
   - Try submitting with missing required fields
   - Try submitting a duplicate entry

### Step 9: Performance Optimization

1. Add server-side pagination (already configured)
2. Reduce initial data load:
   ```sql
   -- Add ROWNUM guard for initial load (APEX handles this)
   ```
3. Consider a materialized view for the dashboard:
   ```sql
   CREATE MATERIALIZED VIEW mv_expense_summary
   REFRESH COMPLETE ON DEMAND
   AS
   SELECT department_id, category,
          TO_CHAR(expense_date, 'YYYY-MM') AS month,
          COUNT(*) AS count, SUM(amount) AS total
   FROM expenses
   GROUP BY department_id, category, TO_CHAR(expense_date, 'YYYY-MM');
   ```

---

## Best Practices Applied

1. **Database Design**: Normalized schema with proper indexes, constraints, and sequences
2. **Security**: Row-level security via application item and WHERE clause filtering
3. **UX**: Modal dialog for forms, confirmation for destructive actions, responsive layout
4. **Performance**: Server-side pagination, indexed columns, efficient queries
5. **Maintainability**: Separated concerns (report, form, dashboard pages)
6. **Validation**: Both client-side (required attributes) and server-side (duplicate check)

## Common Pitfalls to Avoid

1. **Missing bind variables**: Always use `:P1_ITEM` syntax, never string concatenation
2. **Security by obscurity**: Row-level security via WHERE clause must be in every query
3. **Unvalidated deletes**: Always add confirmation for DELETE operations
4. **Slow reports**: Ensure indexes exist on filtered/ordered columns
5. **Broken links**: Test link columns thoroughly after any page ID changes

## Extensions for Future Iterations

1. Email notifications when an expense is submitted/approved
2. Approval workflow with dynamic actions
3. File attachments for receipts (APEX_FILE_MANAGER)
4. Budget tracking with alerts when approaching limits
5. REST API for mobile expense submission
6. Audit logging with APPROVAL_HISTORY table
