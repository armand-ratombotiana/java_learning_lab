# Mock Interview: APEX Security (Lab 05)

**Role:** Oracle APEX Developer (Mid-Level)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What authentication schemes are available in Oracle APEX?

**Candidate:** APEX provides several built-in authentication schemes:
1. **APEX Accounts:** Users stored in APEX internal users table
2. **LDAP Directory:** Integration with LDAP (Active Directory, Oracle Internet Directory)
3. **OpenID Connect:** Modern authentication with OIDC providers (Okta, Azure AD, Google)
4. **OAuth 2.0:** Using OAuth 2.0 client credentials
5. **Oracle Application Express Authentication:** Built-in, no custom code
6. **Custom Authentication:** Build your own authentication using any custom logic
7. **No Authentication (DAD):** For intranet apps where web server handles auth
8. **HTTP Header Variable:** Trusted authentication via reverse proxy headers

For enterprise applications, LDAP or OpenID Connect is most common. Custom authentication is used for legacy integrations.

**Interviewer:** How is authorization different from authentication in APEX?

**Candidate:** Authentication verifies **who** the user is. Authorization verifies **what** the user can do. APEX implements authorization through:
- **Authorization Schemes:** Defined at application level, assigned to pages, regions, items, buttons, or processes
- **Role-based access:** Users have roles (ADMIN, MANAGER, USER) mapped to authorization schemes
- **Access control:** `APEX_UTIL.PUBLIC_CHECK_AUTHORIZATION` for programmatic checks
- **Row-level security:** VPD (Virtual Private Database) for data-level authorization

Example authorization scheme definition:
- Name: `IS_MANAGER`
- Type: `PL/SQL Function Returning Boolean`
- PL/SQL: `RETURN check_user_role(:APP_USER, 'MANAGER');`
- This can be applied to any page, region, or component.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you prevent SQL injection in APEX applications?

**Candidate:** APEX provides multiple layers of protection:

1. **Bind variables:** Always use bind variables (:P1_ITEM) instead of string concatenation
   - Safe: `SELECT * FROM employees WHERE department_id = :P1_DEPT`
   - Unsafe: `SELECT * FROM employees WHERE department_id = '` || :P1_DEPT || `'`

2. **Session State Protection:** Protects page items from tampering:
   - Checksum-based protection on page items
   - Restricts which items can be set from URL parameters
   - Prevents parameter tampering (`f?p=...&P1_ID=99999`)

3. **Item Attributes:** 
   - "Escape Special Characters" = Yes (default for most items)
   - "Strip HTML" = Yes for text fields
   - Input validation using APEX built-in validators

4. **PL/SQL Code:** 
   - Use `DBMS_ASSERT` to validate identifiers
   - Sanitize input using `APEX_UTIL.PURGE_SQL` or custom validation

5. **ORDS:** REST endpoints parameterize SQL automatically when using bind variables

**Interviewer:** How do you implement row-level security (RLS) in APEX?

**Candidate:** Row-level security can be implemented at multiple levels:

**APEX-level RLS:** Using authorization schemes at the data source level
```sql
-- In the SQL source of a report:
SELECT e.* FROM employees e
WHERE EXISTS (SELECT 1 FROM user_org_access uoa 
              WHERE uoa.user_id = :APP_USER 
              AND uoa.organization_id = e.organization_id);
```

**Database-level VPD:** Virtual Private Database policy
```sql
CREATE OR REPLACE FUNCTION rls_employees(p_schema VARCHAR2, p_object VARCHAR2)
RETURN VARCHAR2 AS
BEGIN
    RETURN 'organization_id IN (
        SELECT organization_id FROM user_org_access WHERE user_id = SYS_CONTEXT(''APEX$SESSION'', ''APP_USER'')
    )';
END;

BEGIN
    DBMS_RLS.ADD_POLICY(
        object_name => 'EMPLOYEES',
        policy_name => 'employee_rls',
        policy_function => 'rls_employees',
        statement_types => 'SELECT, INSERT, UPDATE, DELETE'
    );
END;
```

VPD is more secure (enforced at database level, cannot be bypassed even through direct SQL access) but requires DBA privileges to implement. APEX-level RLS is simpler to implement and test.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a comprehensive security framework for an APEX-based banking application.

**Candidate:** 

**Multi-layer security architecture:**

**Layer 1 — Network Security:**
- OCI WAF (Web Application Firewall) in front of APEX
- TLS 1.3 for all connections
- IP whitelisting for admin pages
- DDoS protection

**Layer 2 — Authentication:**
```sql
-- Custom authentication with MFA
CREATE OR REPLACE FUNCTION bank_auth(p_username VARCHAR2, p_password VARCHAR2)
RETURN BOOLEAN AS
    v_user_id users.user_id%TYPE;
BEGIN
    -- Verify password
    SELECT user_id INTO v_user_id FROM users 
    WHERE username = p_username AND password_hash = hash_password(p_password);
    
    -- Check if MFA required (every session for > $10K users)
    IF is_high_value_user(v_user_id) THEN
        -- Store MFA challenge in session
        APEX_UTIL.SET_SESSION_STATE('MFA_REQUIRED', 'Y');
        -- Return partial authentication
        RETURN TRUE;
    END IF;
    
    -- Full authentication for non-high-value users
    APEX_UTIL.SET_SESSION_STATE('MFA_REQUIRED', 'N');
    RETURN TRUE;
EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN FALSE;
END;
```

**Layer 3 — Authorization:**
- Role hierarchy: SYSTEM_ADMIN > COMPLIANCE_OFFICER > BRANCH_MANAGER > TELLER > CUSTOMER
- Pages: Authorized by role using Authorization Schemes
- Data: VPD policies based on branch access, user role, and data sensitivity
- Transactions: `APEX_UTIL.PUBLIC_CHECK_AUTHORIZATION` for withdrawal limits

**Layer 4 — Session Management:**
- Short session timeout (15 minutes for banking, 5 for admin)
- Concurrent session detection (prevent duplicate logins)
- IP binding: Session tied to initial IP address
- Device fingerprinting using JavaScript
- Session invalidation on password change or suspicious activity

**Layer 5 — Audit Logging:**
```sql
CREATE TABLE audit_log (
    log_id NUMBER PRIMARY KEY,
    user_id VARCHAR2(100),
    action VARCHAR2(100),
    table_name VARCHAR2(30),
    record_pk NUMBER,
    old_values CLOB,
    new_values CLOB,
    ip_address VARCHAR2(45),
    session_id NUMBER,
    created_at TIMESTAMP
);

CREATE OR REPLACE TRIGGER trg_account_audit
AFTER UPDATE ON accounts
FOR EACH ROW
BEGIN
    IF :OLD.balance != :NEW.balance THEN
        INSERT INTO audit_log(user_id, action, table_name, record_pk, 
                             old_values, new_values, ip_address, session_id)
        VALUES (NVL(v('APP_USER'), 'SYSTEM'), 'BALANCE_CHANGE', 'ACCOUNTS', 
               :NEW.account_id,
               :OLD.balance, :NEW.balance,
               NVL(OWA_UTIL.GET_CGI_ENV('REMOTE_ADDR'), 'UNKNOWN'),
               NVL(v('APP_SESSION'), 0));
    END IF;
END;
```

**Interviewer:** How do you protect against clickjacking, CSRF, and XSS in APEX?

**Candidate:** 
- **Clickjacking:** APEX by default sends `X-Frame-Options: DENY` or `SAMEORIGIN`. Configure via Application Attributes → Security → Browser Security → Frame Protection.
- **CSRF:** APEX includes CSRF tokens in every state-modifying request. Session state protection adds another layer. For custom processes, use `APEX_UTIL.GET_CSRF_TOKEN` and validate in the process.
- **XSS:** APEX automatically escapes HTML in most display items. Default: "Escape Special Characters" = Yes. For HTML regions or custom rendering, manually validate output using `APEX_ESCAPE.HTML()`. Content Security Policy headers can be added via application-level HTTP headers.

---

## Interviewer Feedback

**Strengths:** Comprehensive security knowledge, practical banking application design, strong VPD understanding  
**Areas to Improve:** Could discuss OAuth 2.0 / OpenID Connect for third-party integration  
**Verdict:** Strong Hire

---

*Lab 05 MOCK_INTERVIEW.md — Part of Oracle APEX Academy Interview Preparation*
