# Problem Walkthrough: Getting Started with Oracle APEX

## Problem 1: Workspace Provisioning — Oracle
### APEX Interview Scenario
"How would you set up an APEX workspace for a new enterprise application team at Oracle?"

### The Problem
You need to provision a workspace for a team of 12 developers building a customer-facing order management application. The workspace must support multiple environments, version control integration, and role-based access.

### Solution Walkthrough
1. **Request Workspace Creation** — Use APEX Administration Service or REST API
2. **Assign Schema** — Create a dedicated database schema (e.g., `ORDERS_DEV`)
3. **Configure Storage** — Set tablespace quotas, default tablespace for APEX objects
4. **Set Workspace Attributes** — Name, description, session timeout (60 min), maximum sessions per schema (50)
5. **Create Users** — Add developers with `APEX_DEVELOPER` role, read-only users with `APEX_VIEWER`
6. **Enable REST** — Enable ORDS for the workspace schema
7. **Version Control** — Configure export-on-save to Git repository
8. **Verify** — Login as each developer, test app creation

### Code
```sql
BEGIN
    APEX_INSTANCE_ADMIN.ADD_WORKSPACE(
        p_workspace   => 'ORDERS_DEV',
        p_schema      => 'ORDERS_DEV',
        p_allow_app_parsing => 'Y'
    );
END;
/

-- Create workspace users
BEGIN
    APEX_UTIL.CREATE_USER(
        p_user_name       => 'DEV_LEAD',
        p_web_password    => 'TemporaryPass123!',
        p_developer_privs => 'ADMIN',
        p_default_schema  => 'ORDERS_DEV',
        p_allow_app_building_yn => 'Y'
    );
END;
/
```

### Company Evaluation
- **Oracle**: Must understand workspace isolation, internal metadata tables (`APEX_WORKSPACES`, `APEX_DEVELOPERS`), and instance-level administration
- **Deloitte**: Focus on governance processes, workspace request forms, audit trails for compliance
- **Accenture**: Large-scale automation — script workspace creation with SQLcl, integrate with Terraform

---

## Problem 2: First APEX Application from Scratch — Deloitte
### APEX Interview Scenario
"A Deloitte client needs a quick prototype to track project expenses. Build it in APEX in one day."

### The Problem
Create a single-page expense tracker app: add, edit, delete expenses with categories and date filters. No authentication (public app).

### Solution Walkthrough
1. **Create Application** — From APEX Builder, choose "Create App" → "New Application"
2. **Page 1: Interactive Report** — Based on query:
   ```sql
   SELECT expense_id, category, amount, expense_date, description
   FROM expenses
   ```
3. **Add Form** — Use "Create Page" → "Form" linked to the Interactive Report
4. **Add Search** — Set page search item for category/date
5. **Set Navigation** — Homepage with list of reports
6. **Test** — Run app, insert sample data
7. **Export** — Export application `.sql` and `fXXX.sql` for client

### Code
```sql
CREATE TABLE expenses (
    expense_id    NUMBER PRIMARY KEY,
    category      VARCHAR2(50) NOT NULL,
    amount        NUMBER(10,2) NOT NULL,
    expense_date  DATE NOT NULL,
    description   VARCHAR2(500)
);

CREATE SEQUENCE expense_seq START WITH 1 INCREMENT BY 1;

-- APEX automatic row fetch uses this process
```

### Company Evaluation
- **Deloitte**: Time-boxed prototyping, clear requirements gathering, demo-ready in hours
- **Accenture**: Focus on reusability — create shared component templates
- **Oracle**: Emphasize APEX low-code value proposition

---

## Problem 3: Application Export & Deployment — Accenture
### APEX Interview Scenario
"Accenture is deploying an APEX application across 50 client instances. Automate the export and import process."

### The Problem
You have an APEX app `f100.sql` in version control. You need to deploy it to DEV, TEST, and PROD with different connection strings and workspace credentials.

### Solution Walkthrough
1. **Export from DEV**:
   ```sql
   BEGIN
       APEX_EXPORT.EXPORT_APPLICATION(
           p_application_id => 100,
           p_export_format  => 'SQL'
       );
   END;
   /
   ```
2. **Store in Git** — Commit `f100.sql` with version tag
3. **Install in TEST** — Use SQLcl: `apex install f100.sql`
4. **Configure Application Settings** — After import, run:
   ```sql
   BEGIN
       APEX_APPLICATION_INSTALL.SET_APPLICATION_ITEM(
           p_item_name  => 'G_DB_CONNECTION',
           p_item_value => 'test_connection_string'
       );
   END;
   /
   ```
5. **Validate** — Check parsing schema, authorization schemes
6. **Deploy to PROD** — Repeat steps 3-5 with PROD values
7. **Rollback Plan** — Keep previous export in VCS for immediate revert

### Code
```sql
-- Install via SQLcl (command line)
-- > apex install f100.sql -workspace MY_WS -appuser ADMIN -password ****

-- Post-install configuration
BEGIN
    APEX_UTIL.SET_SECURITY_GROUP_ID(
        p_security_group_id => APEX_UTIL.FIND_SECURITY_GROUP_ID('PROD_WS')
    );
    APEX_APPLICATION_INSTALL.GENERATE_NEW_APPLICATION_ID;
END;
/
```

### Company Evaluation
- **Accenture**: Automation, CI/CD pipeline integration, repeatability across environments
- **Deloitte**: Client handover documentation, runbook creation
- **Oracle**: Understand export internals, workspace ID remapping

---

## Problem 4: Session State Management — Oracle
### APEX Interview Scenario
"At Oracle, a customer reports losing session state between page submissions. Diagnose and fix."

### The Problem
A wizard-based form loses entered data when the user navigates back. The application uses page-level items stored in session state.

### Solution Walkthrough
1. **Verify Session Timeout** — Check `APEX_SESSION` timeout setting (default 60 min)
2. **Check Page Item Attributes** — Ensure items have "Store value in session state" = `Yes`
3. **Examine Branch Logic** — Wizard pages should use "Submit" not "Redirect"
4. **Use Session State Viewer** — Debug via `?p=101:1:::::SESSION:&SESSION.`
5. **Fix Cache Issue** — Set page caching to "No Cache" for wizard pages
6. **Add Persistent Items** — Use application-level items for cross-page data

### Code
```sql
-- Check session state for current user
SELECT *
FROM APEX_WORKSPACE_SESSIONS
WHERE workspace = 'MY_WS'
  AND session_id = :APP_SESSION;

-- Force session state persistence
BEGIN
    APEX_UTIL.SET_SESSION_STATE(
        p_item_name  => 'P1_CUSTOMER_ID',
        p_item_value => :P1_CUSTOMER_ID
    );
END;
/
```

### Company Evaluation
- **Oracle**: Deep understanding of session architecture, caching models, AJAX callback state
- **Deloitte**: Clear client communication, reproduction steps
- **Accenture**: Root cause analysis documentation for support handoff
