# Mock Interview: APEX Getting Started (Lab 01)

**Role:** Oracle APEX Developer (Junior/Mid)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is Oracle APEX and what are its key components?

**Candidate:** Oracle APEX (Application Express) is a low-code development platform that runs on Oracle Database. It allows building enterprise web applications declaratively through a browser interface, with minimal coding. Key components include:
- **SQL Workshop:** Database object management (tables, views, indexes, data loading)
- **App Builder:** Page Designer, Component View, shared components
- **Page Designer:** Visual IDE for building pages with regions, items, buttons, dynamic actions
- **Shared Components:** Authentication schemes, lists of values, authorization schemes, application items

APEX applications are metadata-driven — the APEX engine stores page definitions as metadata in Oracle tables and renders them on-the-fly as HTML pages.

**Interviewer:** Explain the APEX workspace concept.

**Candidate:** A workspace is a virtual private database within an APEX instance. Each workspace has:
- Its own schema(s) — database objects
- Its own applications
- Its own users (developers and end-users)
- Isolation from other workspaces

Developers use workspaces to build applications. Each workspace maps to one or more database schemas. APEX administrators manage workspaces through the APEX Administration interface. Workspaces can be provisioned manually or through self-service registration.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Walk through the APEX page rendering lifecycle from URL request to HTML response.

**Candidate:** When a user requests an APEX page, the following happens:

1. **URL is parsed** by the APEX engine — extracts application ID, page ID, session ID, and request
2. **Session validation** — validates or creates the user session in the APEX session table
3. **Authentication check** — verifies the user is authenticated; redirects to login if needed
4. **Authorization check** — verifies user has access to the requested page
5. **Page processing** — runs page-level and region-level processes in the correct order:
   - Before header (application-level computation/process)
   - After submit (compute, process, validations)
   - Before footer
6. **Page rendering** — generates HTML:
   - Reads page definition from APEX metadata tables
   - Renders page template (header, body, footer)
   - Renders each region in order (with region templates)
   - Renders page items and buttons
   - Applies theme CSS and JavaScript
7. **Response sent** — full HTML page is sent to browser

**Interviewer:** How does APEX session state management work?

**Candidate:** APEX stores session state in the database (`APEX$_SESSION_STATE` and related tables). Each session has:
- A unique session ID stored as a cookie in the browser
- Item values (page-level and application-level) stored in session state
- `:P1_NAME` notation references page item values; `:APP_USER` references the current user
- Session state is persisted across page calls within the same session

Key behaviors:
- Item values are loaded from session state during page rendering
- Submitted item values update session state during page processing
- Session state can be cleared using `clear_cache`, FSP (Page Submit Processing), or by deleting the session
- `APP_SESSION` is the current session ID
- Session timeout is configurable (default: 60 minutes idle)

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design an APEX application for a multi-tenant SaaS platform where each customer has their own data. Consider architecture, security, and deployment.

**Candidate:** 

**Architecture options:**

**Option A — Shared schema, row-level security:**
- Single schema, all customer data in same tables
- VPD (Virtual Private Database) policy on each table to filter by customer_id
- Session context stores the customer ID
- Most cost-effective, but challenging data isolation

**Option B — Schema per tenant:**
- Each customer gets their own database schema
- Application parses schema from URL/environment
- Clean data isolation
- More complex deployment and management

**Option C — Workspace per tenant:**
- Each customer gets their own APEX workspace
- Maximum isolation
- Highest operational overhead

**Recommendation:** Option A for cost efficiency with strong VPD security:

```sql
-- VPD policy
CREATE OR REPLACE FUNCTION tenant_vpd(p_schema VARCHAR2, p_object VARCHAR2)
RETURN VARCHAR2 AS
BEGIN
    RETURN 'customer_id = SYS_CONTEXT(''APEX$SESSION'', ''CUSTOMER_ID'')';
END;

BEGIN
    DBMS_RLS.add_policy(
        object_name => 'ORDERS',
        policy_name => 'tenant_isolation',
        policy_function => 'tenant_vpd',
        statement_types => 'SELECT, INSERT, UPDATE, DELETE'
    );
END;
```

**Deployment:** OCI with load-balanced APEX instances, shared Oracle Database (CDB/PDB architecture). Each tenant's data in a separate PDB for stronger isolation if needed.

**Interviewer:** What are the considerations for deploying APEX on Oracle Cloud Infrastructure?

**Candidate:** OCI deployment options:
1. **APEX on OCI Autonomous Database (ADB):** Most managed — automatic scaling, backups, patching
2. **APEX on OCI Compute:** Full control — custom configurations, but more maintenance
3. **APEX Application Deployment (containerized):** Docker container with ORDS + APEX

Considerations:
- **Network:** Use private subnets for database, public subnets for APEX/ORDS via load balancer
- **Scaling:** ADB auto-scales; Compute instances use instance pools + load balancer
- **High availability:** Multi-AD (Availability Domain) deployment, Data Guard for database
- **Backup:** Automatic backups for ADB; custom for Compute
- **Monitoring:** OCI Monitoring + Logging for APEX and ORDS
- **Cost:** ADB is cost-effective for production; Compute allows BYOL

---

## Interviewer Feedback

**Strengths:** Good understanding of APEX architecture, practical multi-tenant design, OCI deployment knowledge  
**Areas to Improve:** Could discuss APEX 23.x new features (Redwood theme, improved REST support)  
**Verdict:** Hire

---

*Lab 01 MOCK_INTERVIEW.md — Part of Oracle APEX Academy Interview Preparation*
