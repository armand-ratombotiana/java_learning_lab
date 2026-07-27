# Problem Walkthrough: System Administration

## Problem 1: User Account Provisioning — Company: Accenture
### EBS Interview Scenario
"You're at Accenture rolling out EBS to a large retail client with 5,000 employees across 12 countries. The client needs to onboard 500 new warehouse users within 2 weeks. Manual user creation will take 3 months. They ask for a scalable solution."

### The Problem
The client has no automated user provisioning. Current manual process uses Form FNDSCAUS (User Maintenance) — 15 minutes per user. With 500 users, that's 125 hours of data entry just for initial setup. Additionally, each user needs custom responsibilities (Warehouse Operator, Inventory Viewer, Shipping Clerk) and the security rules differ by country due to data privacy laws.

### Solution Walkthrough
- Step 1: Design a user provisioning spreadsheet template mapped to FND_USER columns
- Step 2: Write a PL/SQL bulk user creation script using FND_USER_PKG
- Step 3: Create role-based responsibility sets per job function
- Step 4: Implement data group and security group assignments per country
- Step 5: Schedule as a single concurrent program with error logging
- Step 6: Test rollback capability (savepoint-based)
- Step 7: Document SOD (Segregation of Duties) rules for each role

### Code
```sql
-- Bulk user creation procedure
CREATE OR REPLACE PROCEDURE bulk_create_users (
  p_request_id IN NUMBER
) IS
  CURSOR user_cur IS
    SELECT employee_number, user_name, description,
           email_address, responsibility_key, data_group
    FROM   xx_client_import_users
    WHERE  processed_flag = 'N';
  
  l_user_id NUMBER;
  l_resp_id NUMBER;
  l_error_msg VARCHAR2(2000);
BEGIN
  FOR rec IN user_cur LOOP
    BEGIN
      l_user_id := fnd_user_pkg.createuser(
        x_user_name          => rec.user_name,
        x_owner              => 'CUST',
        x_unencrypted_password => 'TemporaryPass123!',
        x_description        => rec.description,
        x_email_address      => rec.email_address,
        x_employee_id        => TO_NUMBER(rec.employee_number),
        x_password_lifespan_days => 90,
        x_password_date      => SYSDATE
      );
      
      -- Assign responsibility
      l_resp_id := fnd_user_resp_groups_api.insert_assignment(
        x_user_id          => l_user_id,
        x_responsibility_id => get_resp_id(rec.responsibility_key),
        x_effective_date   => SYSDATE,
        x_expiration_date  => NULL
      );
      
      UPDATE xx_client_import_users
      SET processed_flag = 'Y', user_id = l_user_id
      WHERE employee_number = rec.employee_number;
      
    EXCEPTION
      WHEN OTHERS THEN
        l_error_msg := SQLERRM;
        INSERT INTO xx_client_import_errors
          (request_id, employee_number, error_msg, created_date)
        VALUES (p_request_id, rec.employee_number, l_error_msg, SYSDATE);
    END;
  END LOOP;
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: FND_USER_PKG internals, password policies, concurrent manager integration, user validation APIs.
- Accenture: Large-scale rollout patterns, CEMLI components for user provisioning, regional data privacy (GDPR, LGPD, CCPA).
- Deloitte: Implementation methodology — user acceptance testing, training, cutover planning for user provisioning.
- PwC: SOD rule enforcement, privileged access monitoring, user recertification cycle design.
- Amazon: Identity federation with IAM, SSO through OCIAM or Azure AD, Just-In-Time provisioning.

---

## Problem 2: Concurrent Program Failure — Company: Deloitte
### EBS Interview Scenario
"You're at Deloitte supporting a pharmaceutical client during their quarterly close. A critical concurrent program — "GL_POST" — fails every night with the error "ORA-00001: unique constraint (GL.GL_JE_LINES_U1) violated." The finance team is unable to post journals and close the period."

### The Problem
The GL_POST concurrent program runs across multiple legal entities. Due to a bug in a custom feeder program, duplicate journal line numbers are being generated. The GL_JE_LINES_U1 unique constraint on (JE_HEADER_ID, JE_LINE_NUM) is being violated. The client needs immediate fix and a long-term resolution.

### Solution Walkthrough
- Step 1: Identify the failing request and examine the log file for exact ORA error
- Step 2: Query GL_JE_HEADERS and GL_JE_LINES to find duplicate entries
- Step 3: Identify the feeder program that generates the duplicate lines
- Step 4: Quick fix — identify and delete duplicate lines, re-sequence line numbers
- Step 5: Permanent fix — modify the feeder program to use GL_JOURNAL_LINES_S.NEXTVAL properly
- Step 6: Add validation logic to prevent duplicate submission
- Step 7: Set up proactive monitoring for constraint violations

### Code
```sql
-- Find duplicate journal lines
SELECT je_header_id, je_line_num, COUNT(*)
FROM   gl_je_lines
WHERE  je_header_id IN (
  SELECT je_header_id
  FROM   gl_je_headers
  WHERE  period_name = 'DEC-24'
  AND    status = 'U'
)
GROUP  BY je_header_id, je_line_num
HAVING COUNT(*) > 1;

-- Fix duplicate line numbers
DECLARE
  CURSOR fix_cur IS
    SELECT rowid, je_header_id, je_line_num,
           ROW_NUMBER() OVER (
             PARTITION BY je_header_id
             ORDER BY je_line_num, last_update_date
           ) AS new_line_num
    FROM   gl_je_lines
    WHERE  je_header_id IN (
      SELECT je_header_id FROM gl_je_headers
      WHERE period_name = 'DEC-24'
    );
BEGIN
  FOR rec IN fix_cur LOOP
    UPDATE gl_je_lines
    SET je_line_num = rec.new_line_num
    WHERE rowid = rec.rowid;
  END LOOP;
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: Deep knowledge of GL schema, concurrent program execution flow, error handling in PL/SQL feeder programs.
- Deloitte: Crisis management methodology, root cause analysis (RCA), permanent corrective action (PCA) documentation.
- Accenture: CEMLI defect management, change control procedures, regression testing for feeder program fixes.
- PwC: SOX impact assessment — journal integrity, audit trail of corrections, period-close controls.
- Amazon: Automation of period close, monitoring with CloudWatch, runbook automation for common failures.

---

## Problem 3: Profile Option Management — Company: PwC
### EBS Interview Scenario
"You're at PwC auditing an EBS 12.2 implementation at a financial services client. The client's internal audit found that 15 users in AP department have access to both "AP Invoice Validation" and "AP Payment Approval" responsibilities, which violates SOD rules. They ask you to remediate."

### The Problem
The client has conflicting profile options set at the user level that override site-level security restrictions. Additionally, there is no change management for profile option modifications — any administrator can change critical profiles without approval. The audit also found that profile option FND_HIDE_DB_PASSWORD was set to "N" at the site level, exposing database credentials in debug logs.

### Solution Walkthrough
- Step 1: Audit all profile option changes using FND_PROFILE_OPTION_VALUES_HISTORY
- Step 2: Identify users with SOD violations using FND_USER_RESP_GROUPS
- Step 3: Create a profile option change management process using custom workflow
- Step 4: Reset FND_HIDE_DB_PASSWORD to "Y" at all levels
- Step 5: Implement profile option auditing via FND_PROFILE_OPTIONS auditing
- Step 6: Create a custom concurrent program to report profile option drift

### Code
```sql
-- Audit profile option changes
SELECT fpovh.profile_option_name,
       fpovh.user_name,
       fpovh.old_value,
       fpovh.new_value,
       fpovh.change_date,
       fpovh.change_type
FROM   fnd_profile_option_values_history fpovh
WHERE  fpovh.change_date > SYSDATE - 90
ORDER  BY fpovh.change_date DESC;

-- Find users with SOD violations
SELECT urg.user_name,
       urg.responsibility_name,
       LISTAGG(urg.responsibility_name, ', ') 
         WITHIN GROUP (ORDER BY urg.responsibility_name) AS conflicting_resps
FROM   fnd_user_resp_groups urg
WHERE  urg.responsibility_name IN (
  'AP_INVOICE_VALIDATION', 'AP_PAYMENT_APPROVAL'
)
GROUP  BY urg.user_name, urg.responsibility_name
HAVING COUNT(DISTINCT urg.responsibility_name) > 1;

-- Reset critical profile option
BEGIN
  fnd_profile.save(
    'FND_HIDE_DB_PASSWORD',
    'Y',
    'SITE'
  );
  COMMIT;
END;
/
```

### Company Evaluation
- PwC: SOX compliance framework, SOD matrix design, user access recertification, profile option auditing methodology.
- Oracle: Profile option architecture — site, application, responsibility, user levels; inheritance rules; caching behavior.
- Deloitte: Implementation of preventive controls, change management workflows, and access certification automation.
- Accenture: SOD risk analysis templates, role-based access control (RBAC) design for large enterprises.
- Amazon: IAM policy equivalent mapping, automated access reviews, continuous compliance monitoring.
