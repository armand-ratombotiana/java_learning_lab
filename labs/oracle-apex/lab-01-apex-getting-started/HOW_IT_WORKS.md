# How APEX Works

## Creating a Workspace
A workspace is a logical container for applications, users, and data. To create one:
1. Log into APEX Administration Services
2. Click "Create Workspace"
3. Enter workspace name, schema, and admin credentials
4. Assign storage quota
5. Workspace is provisioned with a unique ID

## Building an Application
1. From App Builder, click "Create"
2. Choose: From Scratch, From a File (CSV/JSON/XML), Create App (wizard)
3. Select pages: Dashboard, Reports, Forms, Calendar, etc.
4. Choose theme (Universal Theme 42)
5. Set authentication scheme
6. Click "Create Application"

## Page Components
### Regions
Regions are the basic layout containers. Types:
- **Static Content**: HTML content, text, images
- **Classic Report**: SQL query results rendered as a table
- **Interactive Report**: user-customizable report with actions
- **Interactive Grid**: editable data grid
- **Chart**: Oracle JET charts (bar, line, pie, area, etc.)
- **PL/SQL Dynamic**: regions rendered by PL/SQL code
- **List**: navigation lists
- **Tree**: hierarchical data display
- **Calendar**: date-based events

### Items
Items are form controls for user input:
- Text Field: single-line text entry
- Textarea: multi-line text
- Number Field: numeric input
- Date Picker: calendar date selection
- Select List: drop-down options
- Popup LOV: lookup dialog
- Radio Group: mutually exclusive options
- Checkbox: boolean or multi-select
- File Browse: file upload
- Hidden: invisible page-level value

### Buttons
Buttons trigger actions. Each button has:
- Position: region position, region button, page-level
- Template: text, icon, hot, danger
- Action: submit page, redirect, JavaScript, dynamic action

## Dynamic Actions
Dynamic actions implement client-side behavior without JavaScript coding:
1. Define event (click, change, page load, etc.)
2. Set selection type (region, item, jQuery selector)
3. Define true action (set value, hide, show, refresh, PL/SQL, etc.)
4. Optionally define false action
5. Set condition for action execution

## Page Processes
Processes execute server-side code during page processing:
- **PL/SQL**: execute anonymous PL/SQL
- **Execute Code**: simplified PL/SQL
- **Close Dialog**: for modal page dialogs
- **Download**: file download process
- **Import**: data import process
- **Web Service**: call external REST/SOAP services

## Validations
Validations ensure data quality before processing:
- **Item**: check item values (NOT NULL, numeric, etc.)
- **SQL**: validate using SQL EXISTS query
- **PL/SQL**: PL/SQL function returning boolean
- **Function Body**: JavaScript expression
- **Regular Expression**: pattern matching

## Branches
Branches redirect users after page processing:
- **Unconditional**: always redirect
- **Conditional**: redirect based on expression
- **On Submit**: after process success
- **On Delete**: after delete process
- **On Error**: redirect on validation failure

## Computations
Computations set item values at specific points:
- **SQL**: set from SQL query
- **PL/SQL**: set from PL/SQL
- **Static**: set a constant value
- **Item**: copy from another item
- **Function Body**: expression result

## Session State Management
APEX manages session state automatically. Each user gets a session with items stored in `APEX_SESSION_STATE`. Items persist across pages. Session can be cleared with `FSP` or APEX_CLEAR_CACHE.
