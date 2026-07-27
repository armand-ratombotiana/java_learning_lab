# Problem Walkthrough: Security

## Problem 1: Segregation of Duties Remediation — Company: PwC
### EBS Interview Scenario
"You're at PwC auditing the EBS implementation at a financial services client. The internal audit team found that 45 users have conflicting responsibilities that violate SOX SOD rules. Specifically, users in AP can create suppliers, approve invoices, and process payments — all without secondary approval. The audit committee demands immediate remediation."

### The Problem
The client has no SOD enforcement in EBS. A single user in the "AP Manager" responsibility can: (1) Create suppliers (PO_VENDOR), (2) Approve invoices (AP_INVOICE_APPROVAL), (3) Create payments (AP_PAYMENT_PROCESS). The standard Oracle SOD solution (EBS SOD) was not implemented. The client needs a remediation plan that identifies conflicts, remediates current violations, and prevents future violations — all without disrupting business operations.

### Solution Walkthrough
- Step 1: Perform SOD risk analysis using Oracle EBS SOD tool or custom query
- Step 2: Identify users with conflicting responsibility assignments
- Step 3: Create a SOD risk matrix mapping functions to conflict categories
- Step 4: Remediate immediate violations — revoke conflicting responsibilities
- Step 5: Implement preventive controls using Oracle EBS SOD module
- Step 6: Configure SOD violation workflow for approval of new responsibility assignments
- Step 7: Implement monthly SOD certification process
- Step 8: Create SOD violation monitoring and reporting concurrent program

### Code
```sql
-- SOD Conflict Analysis Query
SELECT fu.user_name,
       fu.description AS user_full_name,
       frv.responsibility_name,
       frv.responsibility_key,
       frv.attribute1 AS risk_category,
       CASE
         WHEN frv.responsibility_key LIKE '%AP_INVOICE%' AND
              frv.responsibility_key LIKE '%PAYMENT%' THEN 'CRITICAL: Invoice to Payment'
         WHEN frv.responsibility_key LIKE '%VENDOR%' AND
              frv.responsibility_key LIKE '%PAYMENT%' THEN 'CRITICAL: Vendor to Payment'
         WHEN frv.responsibility_key LIKE '%PO%' AND
              frv.responsibility_key LIKE '%RECEIVE%' THEN 'HIGH: PO to Receiving'
         ELSE 'MEDIUM'
       END AS sod_conflict_type
FROM   fnd_user fu,
       fnd_user_resp_groups furi,
       fnd_responsibility_vl frv
WHERE  fu.user_id = furi.user_id
AND    furi.responsibility_id = frv.responsibility_id
AND    fu.end_date IS NULL
AND    frv.responsibility_key IN (
  'AP_INVOICE_VALIDATION',
  'AP_INVOICE_APPROVAL',
  'AP_PAYMENT_PROCESS',
  'PO_VENDOR',
  'PO_PURCHASING',
  'PO_RECEIVING'
)
ORDER  BY fu.user_name, frv.responsibility_name;

-- Revoke conflicting responsibility
DECLARE
  l_user_id NUMBER;
  l_resp_id NUMBER;
BEGIN
  -- Find user and responsibility IDs
  SELECT user_id INTO l_user_id
  FROM fnd_user WHERE user_name = 'JDOE';
  
  SELECT responsibility_id INTO l_resp_id
  FROM fnd_responsibility_vl
  WHERE responsibility_key = 'AP_PAYMENT_PROCESS';
  
  -- End-date the assignment
  fnd_user_resp_groups_api.update_assignment(
    x_user_id           => l_user_id,
    x_responsibility_id => l_resp_id,
    x_effective_date    => SYSDATE,
    x_expiration_date   => SYSDATE  -- End date immediately
  );
  
  COMMIT;
END;
/
```

### Company Evaluation
- PwC: SOX compliance framework, SOD risk matrices, control design, audit methodology, remediation planning.
- Oracle: EBS SOD module, responsibility security, function security, form-level security, data security.
- Deloitte: Internal controls implementation, SOD testing methodology, user access certification process.
- Accenture: Role-based access control design, SOD rule development for large organizations, GRC integration.
- Amazon: IAM policy equivalent in EBS, Just-In-Time access, automated access reviews with CloudWatch.

---

## Problem 2: Data Security — Encrypting PII — Company: Oracle
### EBS Interview Scenario
"You're at Oracle consulting for a healthcare client subject to HIPAA regulations. During a security audit, it was discovered that patient Social Security Numbers and medical record numbers are stored in plain text in EBS tables. The audit report mandates that all PII must be encrypted at rest within 60 days or the client faces $50K/day fines."

### The Problem
The client stores PII in multiple EBS modules — HR (PER_ALL_PEOPLE_F), AP (AP_INVOICES_ALL notes), and custom tables. The database is not using Transparent Data Encryption (TDE) due to performance concerns. The client needs a column-level encryption solution that: (1) Encrypts SSN in PER_ALL_PEOPLE_F, (2) Encrypts medical record numbers in custom tables, (3) Allows authorized programs to decrypt transparently, (4) Maintains index performance for lookup queries, (5) Does not break existing integrations that read these fields.

### Solution Walkthrough
- Step 1: Inventory all tables and columns containing PII
- Step 2: Choose encryption approach — DBMS_CRYPTO with wrapped key management
- Step 3: Create encryption wrapper package with key stored in FND_ENCRYPTED_KEY
- Step 4: Add encrypted columns alongside existing (non-encrypted) columns for migration
- Step 5: Write migration program to encrypt existing data
- Step 6: Update forms and APIs to use encryption/decryption functions
- Step 7: Drop old plaintext columns after verification
- Step 8: Implement key rotation procedure

### Code
```sql
-- Encryption key management package
CREATE OR REPLACE PACKAGE xx_pii_encryption_pkg AS
  FUNCTION encrypt_ssn(p_ssn IN VARCHAR2) RETURN RAW;
  FUNCTION decrypt_ssn(p_encrypted IN RAW) RETURN VARCHAR2;
  FUNCTION hash_ssn(p_ssn IN VARCHAR2) RETURN RAW;  -- For indexing/lookup
END;
/

CREATE OR REPLACE PACKAGE BODY xx_pii_encryption_pkg AS
  g_encryption_key RAW(32);
  g_iv RAW(16);
  
  FUNCTION get_encryption_key RETURN RAW IS
  BEGIN
    -- Retrieve key from secure storage
    SELECT TO_RAW(attribute_value) INTO g_encryption_key
    FROM fnd_encrypted_keys
    WHERE key_name = 'PII_SSN_KEY'
    AND enabled_flag = 'Y';
    
    RETURN g_encryption_key;
  END;
  
  FUNCTION encrypt_ssn(p_ssn IN VARCHAR2) RETURN RAW IS
    l_encrypted RAW(200);
  BEGIN
    l_encrypted := DBMS_CRYPTO.encrypt(
      src => UTL_RAW.CAST_TO_RAW(p_ssn),
      typ => DBMS_CRYPTO.ENCRYPT_AES256 + DBMS_CRYPTO.CHAIN_CBC + DBMS_CRYPTO.PAD_PKCS5,
      key => get_encryption_key()
    );
    RETURN l_encrypted;
  END;
  
  FUNCTION decrypt_ssn(p_encrypted IN RAW) RETURN VARCHAR2 IS
    l_decrypted VARCHAR2(100);
  BEGIN
    l_decrypted := UTL_RAW.CAST_TO_VARCHAR2(
      DBMS_CRYPTO.decrypt(
        src => p_encrypted,
        typ => DBMS_CRYPTO.ENCRYPT_AES256 + DBMS_CRYPTO.CHAIN_CBC + DBMS_CRYPTO.PAD_PKCS5,
        key => get_encryption_key()
      )
    );
    RETURN l_decrypted;
  END;
  
  FUNCTION hash_ssn(p_ssn IN VARCHAR2) RETURN RAW IS
  BEGIN
    RETURN DBMS_CRYPTO.hash(
      src => UTL_RAW.CAST_TO_RAW(p_ssn),
      typ => DBMS_CRYPTO.HASH_SH256
    );
  END;
END;
/

-- Migration: Add encrypted column
ALTER TABLE per_all_people_f ADD (
  encrypted_national_identifier RAW(200),
  national_identifier_hash RAW(32)
);

-- Migrate existing records
UPDATE per_all_people_f
SET encrypted_national_identifier = xx_pii_encryption_pkg.encrypt_ssn(national_identifier),
    national_identifier_hash = xx_pii_encryption_pkg.hash_ssn(national_identifier)
WHERE encrypted_national_identifier IS NULL;

COMMIT;
```

### Company Evaluation
- Oracle: DBMS_CRYPTO, Transparent Data Encryption, FND_ENCRYPTED_KEYS, Oracle Advanced Security options.
- PwC: HIPAA compliance, data privacy framework, encryption standards (AES-256), key management audit.
- Deloitte: Data privacy methodology, GDPR/HIPAA readiness assessment, privacy impact analysis.
- Accenture: Healthcare data security patterns, PII discovery tools, enterprise data protection strategy.
- Amazon: AWS KMS for key management, RDS encryption, S3 server-side encryption, CloudHSM for HSM.

---

## Problem 3: User Access Certification — Company: Deloitte
### EBS Interview Scenario
"You're at Deloitte helping a multinational client prepare for their annual SOX audit. The auditor requires evidence that all EBS user access has been reviewed and certified within the last 90 days. The client has 3,000 active users across 50 responsibilities. Currently, there is no automated access certification process."

### The Problem
The client manually distributes Excel spreadsheets to department managers who must review and certify user access. The process takes 6 weeks and has a 40% non-response rate. The auditor found that 200 terminated employees still have active EBS accounts. The client needs an automated certification workflow integrated with EBS.

### Solution Walkthrough
- Step 1: Design certification campaign data model in custom tables
- Step 2: Build a concurrent program to generate certification lists by manager
- Step 3: Implement certification workflow using Oracle Workflow or AME
- Step 4: Create self-service certification page in OAF (or use Oracle Access Governance)
- Step 5: Configure automatic deactivation of uncertified users after deadline
- Step 6: Generate certification evidence report for auditors
- Step 7: Integrate with HRMS for automatic deactivation on termination

### Code
```sql
-- Certification campaign metadata
CREATE TABLE xx_access_certification (
  campaign_id       NUMBER PRIMARY KEY,
  campaign_name     VARCHAR2(100),
  start_date        DATE,
  end_date          DATE,
  status            VARCHAR2(20),  -- OPEN, IN_PROGRESS, CLOSED
  created_by        NUMBER,
  creation_date     DATE
);

-- Generate certification items for each user-manager pair
CREATE OR REPLACE PROCEDURE generate_certification_items (
  p_campaign_id IN NUMBER
) IS
BEGIN
  INSERT INTO xx_access_cert_items (
    campaign_id,
    user_id,
    user_name,
    user_full_name,
    responsibility_name,
    manager_id,
    manager_name,
    certification_status,
    created_date
  )
  SELECT p_campaign_id,
         fu.user_id,
         fu.user_name,
         ppf.full_name,
         frv.responsibility_name,
         ppf.supervisor_id,
         mgr.full_name,
         'PENDING',
         SYSDATE
  FROM   fnd_user fu,
         per_people_f ppf,
         fnd_user_resp_groups furi,
         fnd_responsibility_vl frv,
         per_people_f mgr
  WHERE  fu.employee_id = ppf.person_id(+)
  AND    fu.user_id = furi.user_id
  AND    furi.responsibility_id = frv.responsibility_id
  AND    ppf.supervisor_id = mgr.person_id(+)
  AND    fu.end_date IS NULL
  AND    furi.end_date IS NULL
  AND    SYSDATE BETWEEN ppf.effective_start_date(+) AND ppf.effective_end_date(+)
  AND    SYSDATE BETWEEN mgr.effective_start_date(+) AND mgr.effective_end_date(+);
  
  COMMIT;
END;
/

-- Auto-deactivate uncertified users
CREATE OR REPLACE PROCEDURE deactivate_uncertified_users (
  p_campaign_id IN NUMBER
) IS
BEGIN
  FOR rec IN (
    SELECT user_id
    FROM   xx_access_cert_items
    WHERE  campaign_id = p_campaign_id
    AND    certification_status = 'PENDING'
    AND    created_date < SYSDATE - 90
  ) LOOP
    fnd_user_pkg.disableuser(
      x_user_id => rec.user_id
    );
    
    UPDATE xx_access_cert_items
    SET certification_status = 'AUTO_DEACTIVATED'
    WHERE user_id = rec.user_id
    AND campaign_id = p_campaign_id;
  END LOOP;
  
  COMMIT;
END;
/
```

### Company Evaluation
- Deloitte: Access certification methodology, SOX compliance automation, identity governance frameworks.
- Oracle: FND_USER APIs, user management, workflow integration, Oracle Identity Governance / Access Governance.
- PwC: Audit evidence standards, certification reporting, control testing methodology.
- Accenture: Enterprise identity management, role mining, automated provisioning/deprovisioning.
- Amazon: IAM access analyzer, automated credential reports, SSO integration with identity providers (Okta, Azure AD).
