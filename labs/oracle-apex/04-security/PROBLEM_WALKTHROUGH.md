# Problem Walkthrough: Security

## Problem 1: Authentication Scheme Setup — Oracle
### APEX Interview Scenario
"Oracle's customer wants to integrate APEX with Oracle Identity Cloud Service (IDCS). Set up SSO."

### Problem
Configure APEX to use OAuth2/OIDC via Oracle Identity Cloud Service for employee login.

### Solution Walkthrough
1. **Register APEX App in IDCS** — Create confidential app with redirect URIs
2. **Set Authentication Scheme** — In APEX Builder: Shared Components → Authentication Schemes → Create
3. **Choose "OpenID Connect"** — Provider: Oracle Identity Cloud Service
4. **Configure Endpoints** — Issuer, Authorization, Token, JWKS URI from IDCS metadata
5. **Map User Attributes** — Map IDCS claims (`sub`, `email`, `name`) to APEX user attributes
6. **Set Client Secret** — Store in APEX credential store or Vault
7. **Test Login Flow** — Redirect to IDCS, accept, return to APEX
8. **Handle Logout** — Configure RP-initiated logout URL

### Code
```sql
-- Store client credential securely
BEGIN
    APEX_CREDENTIAL.CREATE_CREDENTIAL(
        p_credential_name    => 'IDCS_APEX_CLIENT',
        p_credential_type    => 'OAUTH2_CLIENT_CREDENTIALS',
        p_client_id          => 'your_client_id',
        p_client_secret      => 'your_secret'
    );
END;
/

-- Verify authentication scheme in metadata
SELECT scheme_name, scheme_type, credential_name
FROM APEX_APPLICATION_AUTH
WHERE application_id = 100;
```

### Company Evaluation
- **Oracle**: Deep OIDC/OAuth2 integration, IDCS admin, credential vault
- **Deloitte**: Identity governance, access certification requirements
- **Accenture**: Enterprise SSO rollout across multiple apps

---

## Problem 2: Authorization Schemes & Row-Level Security — Deloitte
### APEX Interview Scenario
"Deloitte's client needs role-based access: Managers can see all rows, employees only their own. No VPD."

### Problem
Implement row-level security in APEX without Virtual Private Database.

### Solution Walkthrough
1. **Table Design** — Add `MANAGER_ID` column to `employees`
2. **Create Authorization Scheme** — "Is Manager": Check `:APP_USER` in manager list
3. **Create Access Control Table** — `APP_USERS(user_name, role, department_id)`
4. **Modified Report Query**:
   ```sql
   SELECT * FROM employees
   WHERE department_id IN (
       SELECT department_id FROM app_users WHERE user_name = :APP_USER AND role = 'MANAGER'
   )
   OR employee_id = (
       SELECT employee_id FROM app_users WHERE user_name = :APP_USER
   )
   ```
5. **Button Visibility** — Set button condition to authorization scheme "Is Manager"
6. **Tab Visibility** — Use authorization for page tabs
7. **Test** — Login as manager vs. employee, verify data visibility

### Code
```sql
-- Authorization PL/SQL Function Body
BEGIN
    FOR c IN (SELECT role FROM app_users WHERE user_name = :APP_USER) LOOP
        IF c.role = 'MANAGER' THEN
            RETURN TRUE;
        END IF;
    END LOOP;
    RETURN FALSE;
END;
/

-- ACL table
CREATE TABLE app_users (
    user_name      VARCHAR2(100) PRIMARY KEY,
    role           VARCHAR2(20) CHECK (role IN ('EMPLOYEE','MANAGER','ADMIN')),
    employee_id    NUMBER REFERENCES employees(employee_id),
    department_id  NUMBER REFERENCES departments(department_id)
);
```

### Company Evaluation
- **Deloitte**: Fine-grained access control for multi-tenant clients
- **Accenture**: Scalable ACL design, centralized authorization management
- **Oracle**: Authorization scheme execution context vs. VPD

---

## Problem 3: SQL Injection Prevention — Accenture
### APEX Interview Scenario
"Accenture found a SQL injection vulnerability in an APEX report with a dynamic WHERE clause."

### Problem
A developer used string concatenation in a report source: `SELECT * FROM products WHERE ' || :P1_FILTER`. Fix it.

### Solution Walkthrough
1. **Identify the Pattern** — Search for concatenation in region source, validation, DA code
2. **Replace with Bind Variables** — Never concatenate user input into SQL
3. **Use APEX_ITEM** — For interactive filters:
   ```sql
   SELECT * FROM products
   WHERE (:P1_SEARCH IS NULL OR product_name LIKE '%' || :P1_SEARCH || '%')
     AND (:P1_CATEGORY IS NULL OR category = :P1_CATEGORY)
   ```
4. **Use DBMS_ASSERT** — Validate any dynamic object names:
   ```sql
   l_table := DBMS_ASSERT.SQL_OBJECT_NAME(:P1_TABLE_NAME);
   ```
5. **Use APEX Data Validation** — Instead of manual SQL in dynamic actions
6. **Penetration Test** — Test with `' OR '1'='1` and other injection patterns

### Code
```sql
-- Unsafe (DON'T DO THIS)
l_sql := 'SELECT * FROM products WHERE ' || :P1_FILTER;

-- Safe with APEX bind variables
SELECT * FROM products
WHERE product_name LIKE '%' || :P1_PRODUCT_NAME || '%'
  AND (:P1_CATEGORY IS NULL OR category = :P1_CATEGORY);

-- Safe dynamic table name
BEGIN
    l_table := DBMS_ASSERT.SQL_OBJECT_NAME(:P1_TABLE);
    EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM ' || l_table INTO l_count;
END;
/
```

### Company Evaluation
- **Accenture**: Security audit procedures, OWASP compliance, remediation documentation
- **Oracle**: DBMS_ASSERT, APEX item protection levels
- **Deloitte**: Security training for development teams

---

## Problem 4: Session & Page Item Protection — Oracle
### APEX Interview Scenario
"At Oracle, a penetration test shows that page items can be manipulated via URL parameters."

### Problem
Users can tamper with page items through the URL (e.g., `?P1_AMOUNT=999999`).

### Solution Walkthrough
1. **Set Item Protection Level** — In Page Designer:
   - "Protected": Item cannot be set via URL
   - "Checksum Required": Requires valid checksum for URL-based sets
2. **Use Session State Protection** — Enable in Security Attributes:
   - "Session State Protection" → "Enabled"
3. **Generate Checksums** — For links:
   ```sql
   APEX_UTIL.PREPARE_URL(
       p_url => 'f?p=100:1:&SESSION.::NO::P1_AMOUNT:100',
       p_checksum_type => 'SESSION'
   )
   ```
4. **Server-side Validation** — Always re-validate sensitive item values on submit
5. **Audit** — Check `APEX_WORKSPACE_ACTIVITY_LOG` for suspicious parameter attempts

### Code
```sql
-- Generate URL with checksum
SELECT APEX_UTIL.PREPARE_URL(
    p_url => 'f?p=&APP_ID.:1:&SESSION.:::P1_ORDER_ID:' || :ORDER_ID,
    p_checksum_type => 'SESSION'
) AS safe_link FROM DUAL;

-- Activity log review
SELECT *
FROM APEX_WORKSPACE_ACTIVITY_LOG
WHERE application_id = 100
  AND page_id = 1
  AND request LIKE '%P1_AMOUNT%'
ORDER BY access_date DESC;
```

### Company Evaluation
- **Oracle**: Session state protection internals, checksum algorithms
- **Accenture**: Penetration testing findings, security hardening checklist
- **Deloitte**: Risk assessment documentation, client recommendations
