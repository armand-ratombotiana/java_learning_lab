# Oracle APEX — Certification Guide

<div align="center">

![Oracle](https://img.shields.io/badge/Oracle_APEX-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Certification](https://img.shields.io/badge/Certification-OCP-005C5C?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs_ Covered-8-blue?style=for-the-badge)

**Earn your Oracle APEX certification — roadmap, resources, and practice**

</div>

---

## Table of Contents

1. [Oracle APEX Certification Paths](#oracle-apex-certification-paths)
2. [Exam Objectives Mapped to Labs](#exam-objectives-mapped-to-labs)
3. [Sample Certification Questions](#sample-certification-questions)
4. [Study Resources](#study-resources)
5. [How to Get Certified: Steps and Costs](#how-to-get-certified-steps-and-costs)

---

## Oracle APEX Certification Paths

### Available Certifications

| Certification | Exam | Level | Target |
|--------------|------|-------|--------|
| **Oracle APEX Cloud Developer Professional** | 1Z0-770 | Professional | APEX developers building cloud-native apps |
| **Oracle APEX Cloud Developer Certified Specialist** | 1Z0-771 | Specialist | Advanced APEX specialization |
| **Oracle APEX Developer Professional (Legacy)** | 1Z0-748 | Professional | Classic APEX certification (retiring) |

### Recommended Path

```
Oracle APEX Cloud Developer Professional (1Z0-770)
    └── Oracle APEX Cloud Developer Certified Specialist (1Z0-771)
```

The Cloud Developer path is the current, modern certification track. It focuses on:
- Low-code application development with APEX
- APEX on Oracle Cloud Infrastructure (OCI)
- RESTful services integration
- Modern APEX 23.x features

---

## Exam Objectives Mapped to Labs

### Exam 1Z0-770: Oracle APEX Cloud Developer Professional

| Domain | % | Mapped Labs | Key Topics |
|--------|---|-------------|------------|
| Workspace and Application Administration | 15% | Lab 01 | Workspace management, application creation, version control |
| Page Design and Regions | 20% | Lab 02, Lab 04 | Page builder, regions (static, interactive grid, classic report) |
| Data Sources and SQL Workshop | 20% | Lab 03 | SQL Workshop, Object Browser, data loading, REST data sources |
| Interactive Grid and Reports | 15% | Lab 04 | Interactive Grid, master-detail, dynamic actions |
| Security and Session Management | 15% | Lab 05 | Authentication, authorization, session state, CSRF protection |
| RESTful Services and Integration | 10% | Lab 06 | RESTful services, ORDS, web source modules |
| Advanced Worksheets and Dynamic Actions | 5% | Lab 07, Lab 08 | Dynamic actions, JavaScript API, performance optimization |

### Detailed Mapping

**Lab 01 — APEX Getting Started** (Covers 15% of exam)
- Workspace creation and management
- Application builder overview
- Application types (Desktop, Mobile, Responsive)
- Theme selection and application settings
- Version control and export/import

**Lab 02 — Page Builder** (Covers 12% of exam)
- Component view vs Page Designer
- Regions (Static, HTML, Classic Report)
- Items (Text Field, Select List, Radio Group, Checkbox)
- Buttons and dynamic actions
- Page processing (processes, computations, validations)

**Lab 03 — SQL Workshop** (Covers 8% of exam)
- Object Browser (tables, views, indexes)
- SQL Commands (running queries)
- SQL Scripts (creating and running scripts)
- Data Workshop (data loading, data generation, data export)

**Lab 04 — Interactive Grid** (Covers 15% of exam)
- Interactive Grid attributes
- Columns, filters, sorting, grouping
- Master-detail (one-to-many relationships)
- Editable Interactive Grid
- Aggregation and control break

**Lab 05 — Security** (Covers 15% of exam)
- Authentication schemes (APEX Accounts, LDAP, OAuth2, Custom)
- Authorization schemes (role-based, access control)
- Session state protection (page, item, application-level)
- SQL injection prevention in APEX
- CSRF protection

**Lab 06 — RESTful Services** (Covers 10% of exam)
- ORDS (Oracle REST Data Services) configuration
- RESTful service modules and templates
- Web source modules (consuming external REST APIs)
- JSON parsing and generation
- Swagger/OpenAPI support

**Lab 07 — Advanced Worksheets** (Covers 5% of exam)
- Faceted search
- Tree regions
- Calendar region
- Form region customization
- Dynamic actions with JavaScript

**Lab 08 — Performance** (Covers 5% of exam)
- APEX performance tuning
- Database query optimization
- Caching strategies
- CDN integration for static files
- Debug mode and performance analysis

---

## Sample Certification Questions

### Question 1 (Easy)
Which APEX component provides a spreadsheet-like interface for editing tabular data?

A. Classic Report
B. Interactive Grid ✓
C. Interactive Report
D. Faceted Search

**Explanation:** Interactive Grid provides the most spreadsheet-like experience, allowing inline editing of tabular data with built-in sorting, filtering, and grouping.

### Question 2 (Medium)
You need to secure an APEX application page so that only users with the "MANAGER" role can view employee salary information. Which APEX feature should you use?

A. Authentication Scheme
B. Authorization Scheme ✓
C. Session State Protection
D. Data Security

**Explanation:** Authorization schemes control access to pages, components, or processes based on user roles. Authentication schemes verify identity, not permissions.

### Question 3 (Hard)
An Interactive Grid shows a list of orders. Users report it's slow when viewing orders with more than 10 line items. What is the most effective optimization?

A. Add pagination to the Interactive Grid
B. Optimize the underlying query with appropriate indexes ✓
C. Reduce the number of columns displayed
D. Enable Interactive Grid caching

**Explanation:** The root cause is likely a database query performance issue. Optimizing the SQL with proper indexes addresses the root cause, while pagination and column reduction are cosmetic fixes.

### Question 4 (Scenario)
An APEX application needs to display data from both Oracle Database and an external Salesforce CRM system. How would you design this?

**Suggested Answer:**
1. Create a REST data source (Web Source Module) connected to Salesforce API
2. Use SQL Workshop to query Oracle tables
3. Create APEX pages with regions from both data sources
4. Optionally use APEX SQL Workshop to create views joining Oracle data with REST-enabled data
5. ORDS can expose Oracle data as REST endpoints consumed by Salesforce

### Question 5 (Advanced)
You need to implement row-level security in an APEX application so that managers can only see their direct reports' data. Describe the implementation.

**Suggested Answer:**
1. Create a security attribute in the session (e.g., `MANAGER_ID`)
2. In the application's authentication process, store the manager's hierarchy
3. Implement a Virtual Private Database (VPD) or row-level security function:
```sql
CREATE FUNCTION hr_authorization(p_schema VARCHAR2, p_object VARCHAR2)
RETURN VARCHAR2 AS
BEGIN
    RETURN 'manager_id IN (SELECT employee_id FROM employees START WITH manager_id = SYS_CONTEXT(''APEX$SESSION'', ''APP_USER''))';
END;
```
4. Apply the VPD policy to tables containing sensitive employee data
5. Alternatively, use APEX's Authorization Schemes at the page/region level with data filtering in queries

---

## Study Resources

### Official Training
| Resource | URL | Cost |
|----------|-----|------|
| Oracle APEX Cloud Developer Professional (1Z0-770) | Oracle University | $250 (exam voucher) |
| APEX Office Hours | apex.oracle.com/community | Free |
| Oracle Live SQL | livesql.oracle.com | Free |
| Oracle APEX Developer Guide | docs.oracle.com | Free |

### Recommended Books
- *Oracle APEX Best Practices* — Learn from expert APEX developers
- *Oracle APEX 23.x Cookbook* — Practical recipes for common tasks
- *Pro Oracle APEX* — Comprehensive guide for professional developers

### Practice Platforms
| Platform | Purpose |
|----------|---------|
| apex.oracle.com | Free APEX workspace for practice |
| Oracle Live SQL | Test SQL queries online |
| SQL Fiddle | Quick SQL syntax testing |

### Community
- [APEX Community on Oracle Forums](https://community.oracle.com/tech/developers/categories/apex)
- [APEX on Stack Overflow](https://stackoverflow.com/questions/tagged/oracle-apex)
- [APEX Blog on Medium](https://medium.com/tag/oracle-apex)

---

## How to Get Certified: Steps and Costs

### Step-by-Step Process

| Step | Action | Time | Cost |
|------|--------|------|------|
| 1 | Complete all 8 APEX labs | 2-4 weeks | $0 |
| 2 | Review exam objectives | 1 day | $0 |
| 3 | Take Oracle University training (optional) | 5 days | $3,000 (classroom) or $200 (online) |
| 4 | Practice with sample questions | 1-2 weeks | $0 |
| 5 | Schedule exam via Pearson VUE | 1 day | $0 to schedule |
| 6 | Take certification exam | 2 hours | $250 (exam voucher) |
| 7 | Receive digital badge | 1-2 weeks | $0 |

### Total Cost
- **Minimum:** $250 (exam only)
- **With training:** $3,250+
- **Bundle (exam prep + voucher):** $350-400

### Exam Details
- **Duration:** 120 minutes
- **Questions:** ~60-70 multiple choice
- **Passing score:** ~65%
- **Delivery:** Pearson VUE (online proctored available)
- **Validity:** 2 years (renewable)
- **Languages:** English, Japanese

---

<div align="center">

**Get certified, demonstrate your APEX expertise, and advance your career.**

</div>
