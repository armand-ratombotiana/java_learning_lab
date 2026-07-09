# APEX Architecture

## Overview
Oracle APEX is a low-code development platform that runs entirely in the Oracle Database. The APEX engine is stored as PL/SQL packages in the database and uses the database as both the application repository and the runtime engine.

## Three-Layer Architecture
### Layer 1: Web Browser (Client)
The browser sends HTTP requests and renders HTML/CSS/JavaScript responses. APEX uses Universal Theme which is based on Bootstrap 5 and Oracle JET.

### Layer 2: ORDS (Oracle REST Data Services)
ORDS bridges HTTP and the database. It translates HTTP requests into database calls and returns rendered pages. ORDS handles:
- Session management
- Authentication
- Static file serving
- REST API endpoints

### Layer 3: Oracle Database (APEX Engine)
The APEX engine in the database handles:
- Page rendering (generating HTML)
- Page processing (accepting submitted data)
- Session state management
- Application metadata storage
- Security and authorization

## Request Processing
### Page Rendering (GET request)
1. Browser requests `f?p=100:1:SESSION_ID`
2. ORDS translates to `wwv_flow.show` PL/SQL call
3. APEX engine reads metadata from `APEX_APPLICATIONS` tables
4. Engine resolves page components (regions, items, buttons)
5. Engine runs page- and region-level computations
6. Engine renders each region type (SQL, PL/SQL, static)
7. HTML is generated and returned via ORDS

### Page Processing (POST request)
1. Browser submits page with `f?p=100:1:SESSION_ID:::REQUEST_VALUE`
2. ORDS calls `wwv_flow.accept`
3. APEX engine processes in order:
   - Page-level computations
   - Page validation
   - Page processes
   - Page branches

## Authentication vs Authorization
Authentication verifies identity (who you are). Authorization determines access (what you can do). APEX authentication happens before page rendering. Authorization can be checked at page, region, item, or button level.

## Shared Components
Shared components are app-level objects shared across pages:
- Application items
- Application processes
- Lists of values (LOV)
- Breadcrumbs
- Navigation (lists, menus)
- Static files (CSS, JS)
- Build options
- Security attributes
- Globalization settings

## Page Zero
Page zero is a global page whose components appear on every page. Use for:
- Global navigation bar
- Common JavaScript includes
- Global dynamic actions
- Common CSS
- Page-level header/footer

## Universal Theme
The default theme (Universal Theme 42) provides:
- Responsive design (mobile-first)
- 14 template types (page, region, report, label, button, etc.)
- CSS utility classes
- Theme roller for custom styling
- Accessible design patterns

## Template Types
- Page templates: chrome around the page (header, body, footer)
- Region templates: chrome around regions (title bar, borders)
- Report templates: table rows (alternating, highlight)
- Label templates: label positioning for items (left, right, above)
- Button templates: button styles (text, icon, hot)
- List templates: navigation rendering patterns
- Breadcrumb templates: breadcrumb display
- Popup LOV templates: lookup dialog styling
- Calendar templates: calendar component
- Chart templates: chart wrapper
