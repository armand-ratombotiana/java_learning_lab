# Oracle APEX — Interview Cheatsheet

<div align="center">

![APEX](https://img.shields.io/badge/Oracle_APEX-00758F?style=for-the-badge)
![Cheatsheet](https://img.shields.io/badge/Cheatsheet-FF6F00?style=for-the-badge)

**Quick reference for Oracle APEX interviews — components, security, performance, dynamic actions**

</div>

---

## APEX Components Cheat Sheet

### Page Types
| Page Type | Use Case | Key Features |
|-----------|----------|--------------|
| Blank Page | Custom layout | Full control, no predefined structure |
| Report (with Form) | Master-detail | List then edit/view detail |
| Report (Interactive) | Data exploration | Filter, sort, group, chart |
| Report (Classic) | Simple data display | Formatted HTML reports |
| Form | Data entry | Automatic DML, validation |
| Faceted Search | Filter-based navigation | Multiple search facets |
| Calendar | Date-based scheduling | Day/week/month views |
| Tree | Hierarchical data | Expandable tree nodes |
| Chart | Data visualization | JET chart integration |
| Map | Geospatial display | Oracle Maps / GeoJSON |
| Login | Authentication | Built-in auth support |

### Region Types
| Region Type | Best For |
|-------------|----------|
| Static Content | HTML, text, images |
| Classic Report | SQL queries as report |
| Interactive Report | User-driven data exploration |
| Interactive Grid | Editable tabular data |
| Chart | Visual data representation |
| Calendar | Date-based views |
| Tree | Hierarchical navigation |
| Form | Data entry forms |
| List | Navigation lists |
| PL/SQL Dynamic | Dynamic content generation |

### Items (Page Components)
| Item Type | Data Type | Use Case |
|-----------|-----------|----------|
| Text Field | VARCHAR2 | Single-line text input |
| Textarea | VARCHAR2/CLOB | Multi-line text |
| Number Field | NUMBER | Numeric input |
| Date Picker | DATE | Date selection |
| Select List | VARCHAR2 | Dropdown single select |
| Popup LOV | VARCHAR2 | Searchable list |
| Checkbox | VARCHAR2 | Yes/no, multi-select |
| Radio Group | VARCHAR2 | Single choice, few options |
| File Browse | BLOB | File upload |
| Hidden | VARCHAR2 | Pass values without display |
| Display Only | VARCHAR2 | Read-only display |

### Dynamic Actions
| Event | Trigger | Action |
|-------|---------|--------|
| Change | Item value changes | Set Value, Refresh, Show, Hide |
| Click | Button/region click | Submit, Execute PL/SQL, Execute JavaScript |
| Key Release | Text input | Execute JavaScript, Validate |
| Page Load | Page renders | Execute PL/SQL, Set Value |
| Row Initialization | IG row loads | Set Value, Execute JavaScript |
| Dialog Closed | Modal returns | Refresh, Set Value |
| Before Page Submit | Submit triggered | Validate, Execute JavaScript |

---

## Security Best Practices

### Authentication Schemes
| Scheme | Use Case | Implementation |
|--------|----------|----------------|
| APEX Accounts | Simple internal apps | Built-in user repository |
| LDAP | Enterprise directory | Directory authentication |
| OAuth2 (Google/Azure) | SSO integration | Authorization code flow |
| OpenID Connect | Federated identity | ID token validation |
| SAML | Enterprise SSO | SAML assertion |
| Custom | Any custom logic | PL/SQL function returning boolean |
| HTTP Header | Reverse proxy auth | Header-based authentication |

### Authorization Schemes
| Scheme Type | Syntax | Example |
|-------------|--------|---------|
| PL/SQL Function | `RETURN BOOLEAN` | `RETURN :APP_USER IN (SELECT username FROM managers);` |
| Access Control List | User/role assignment | `ADMIN, MANAGER, USER` roles |
| SQL Exists | `RETURN BOOLEAN` via SQL | Check role table |
| Role-based | `APEX_UTIL.USER_ROLES` | Predefined roles |

### Session State Protection
```
Levels of Protection:
Unrestricted < Checksum < Checksum + Session < Restricted

Application Level  → All items
Page Level         → Items on target page
Item Level         → Specific items
```

### SQL Injection Prevention
```sql
-- ❌ Vulnerable: string concatenation
SELECT * FROM emp WHERE dept = ''' || :P_DEPT || '''';

-- ✅ Safe: bind variables
SELECT * FROM emp WHERE dept = :P_DEPT;

-- ✅ Safe: DBMS_ASSERT for dynamic SQL
l_query := 'SELECT * FROM emp WHERE dept = ' || DBMS_ASSERT.ENQUOTE_LITERAL(:P_DEPT);
```

### CSRF Protection
- APEX includes CSRF tokens in all generated forms by default
- Use `APEX_PAGE.GET_URL` for page links to include CSRF token
- Enable "Session State Protection" for additional CSRF prevention
- Custom AJAX calls must include CSRF token via `apex.server.process`

### Common Security Checklist
| Item | Check |
|------|-------|
| Authentication enabled | Required for all production apps |
| Authorization on pages/regions | Apply to sensitive components |
| Session state protection | Enable for sensitive items |
| SQL uses bind variables | Never concatenate user input |
| File upload validation | Restrict file types, sizes |
| XSS escaping | Use APEX_ESCAPE.HTML_ATTRIBUTE |
| HTTPS enforced | Redirect HTTP to HTTPS |
| Audit logging enabled | Log all data changes |

---

## Performance Tuning Tips

### SQL Optimization
```sql
-- ❌ Slow: SELECT *, no filters
SELECT * FROM orders;

-- ✅ Fast: Specific columns, filters, pagination
SELECT order_id, customer_id, order_date, status
FROM orders
WHERE status = :P_STATUS
  AND order_date BETWEEN :P_START_DATE AND :P_END_DATE
ORDER BY order_date DESC;
```

### APEX-Specific Performance Tips
| Tip | Impact | Effort |
|-----|--------|--------|
| Enable region caching | High | Low |
| Use pagination (max rows) | High | Low |
| Optimize report queries | High | Medium |
| Reduce page items | Medium | Low |
| Use bind variables | High | Low |
| Enable query result cache | Medium | Low |
| Minimize custom CSS/JS | Medium | Medium |
| Use CDN for static files | Low | Low |

### Caching Strategies
| Cache Type | Configuration | Invalidates When |
|------------|--------------|------------------|
| Page Cache | Page attributes | TTL expires, or manual |
| Region Cache | Region attributes | TTL expires, or manual |
| Query Result Cache | SQL hint `/*+ RESULT_CACHE */` | Data changes |
| Collection Cache | APEX collection | Session ends |

### Debug Mode Quick Reference
```
Add ?p_debug=YES to URL
Log Levels: FATAL > ERROR > WARN > INFO > DEBUG

Key Timings to Watch:
- "Render Page [X ms]" — Total page render time
- "Execute SQL [X ms]" — Database query time
- "Process Page [X ms]" — Page processing time
- "Ajax Callback [X ms]" — Dynamic action time
```

---

## Dynamic Actions Reference

### Action Types
| Action | Purpose | Example |
|--------|---------|---------|
| Set Value | Set item value | `:P1_TOTAL := :P1_QTY * :P1_PRICE` |
| Refresh | Refresh region | Re-render region content |
| Submit | Submit page | Trigger page processing |
| Execute JavaScript | Run JS code | Client-side logic |
| Execute PL/SQL | Run PL/SQL | Server-side logic |
| Show/Hide | Toggle visibility | Conditional display |
| Enable/Disable | Toggle enablement | Conditional interaction |
| Focus | Set focus | After validation error |
| Add Class | Add CSS class | Visual changes |
| Remove Class | Remove CSS class | Visual changes |

### Client-Side Condition Examples
```javascript
// Item value check
apex.item("P1_STATUS").getValue() === "ACTIVE"

// Element visibility
$("#P1_DETAILS").is(":visible")

// Numeric comparison
Number(apex.item("P1_AMOUNT").getValue()) > 1000
```

### AJAX Callback Process
```sql
-- Server-side PL/SQL for AJAX callback
DECLARE
    v_result VARCHAR2(4000);
BEGIN
    -- Access apex_application.g_x01 through g_x10
    v_result := get_employee_name(apex_application.g_x01);
    :P1_RESULT := v_result;
END;
```

---

## Authorization Schemes

### Common Authorization Patterns
```sql
-- 1. Role-based
RETURN APEX_UTIL.USER_HAS_ROLE('ADMIN') = 'YES';

-- 2. Group membership
RETURN :APP_USER IN (
    SELECT username FROM user_groups WHERE group_name = 'MANAGERS'
);

-- 3. Ownership check
RETURN EXISTS (
    SELECT 1 FROM projects
    WHERE project_id = :P101_PROJECT_ID
    AND created_by = :APP_USER
);

-- 4. Function-based
RETURN security_pkg.is_authorized(:APP_USER, 'VIEW_SALARY');
```

### Predefined Authorization Schemes
| Scheme | Check |
|--------|-------|
| IS_ADMIN | User has ADMIN role |
| IS_DEVELOPER | User is APEX developer |
| IS_USER | Authenticated user |
| NOT_ADMIN | User does not have ADMIN role |

---

<div align="center">

**"APEX interviews test your ability to build secure, performant applications — know your tools deeply."**

---

[Back to Top](#oracle-apex--interview-cheatsheet)

</div>
