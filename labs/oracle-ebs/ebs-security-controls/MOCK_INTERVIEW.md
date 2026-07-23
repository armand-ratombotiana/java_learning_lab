# Mock Interview: EBS Security & Controls (ebs-security-controls)

**Role:** Oracle EBS Security Administrator  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** How does user management work in Oracle EBS?

**Candidate:** EBS user management is handled through the Application Object Library (AOL):
1. **User creation:** Define user with username, password, employee association
2. **Password policies:** Complexity, expiration, history
3. **Responsibility assignment:** User is assigned one or more responsibilities (functions/modules they can access)
4. **Menu hierarchy:** Each responsibility contains a menu tree of functions and sub-menus
5. **Profile options:** Control application behavior per user, responsibility, or site level

The user model: User → Responsibilities → Menus → Functions → Forms/Pages

**Interviewer:** What is Segregation of Duties (SoD) and how do you enforce it in EBS?

**Candidate:** Segregation of Duties prevents a single user from having conflicting responsibilities that could enable fraud. Examples:
- User cannot have both AP Invoice Entry and AP Payment Approval
- User cannot create a PO and also receive goods against that PO
- User cannot create a journal entry and also approve it

**Enforcement in EBS:**
1. **Oracle Advanced Controls:** Pre-built SoD rules and conflict detection
2. **Manual review:** Periodic access certification by managers
3. **Custom validation:** Pre-request validation programs check for conflicts
4. **Role-based access control (RBAC):** Define roles with mutually exclusive functions

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you audit access and changes in EBS?

**Candidate:** EBS provides several auditing mechanisms:

1. **User Audit:** Track user logins, form accesses, concurrent requests (`FND_LOGINS`, `FND_SIGNON_AUDIT`)
2. **Application Audit:** Schema-level auditing via Oracle Database Fine-Grained Auditing (FGA)
3. **Business Event Auditing:** Workflow event audit trails
4. **Value Set Security:** Control access to specific account ranges
5. **Database triggers:** Custom audit triggers on sensitive tables

```sql
-- Enable audit for sensitive queries (e.g., supplier bank accounts)
BEGIN
    DBMS_FGA.ADD_POLICY(
        object_schema => 'AP',
        object_name => 'AP_BANK_ACCOUNTS_ALL',
        policy_name => 'audit_bank_access',
        audit_condition => 'USER NOT IN (''SYSTEM'',''AP_ADMIN'')',
        audit_column => 'BANK_ACCOUNT_NUM',
        statement_types => 'SELECT, UPDATE'
    );
END;
```

**Interviewer:** How does EBS handle data encryption at rest?

**Candidate:** EBS supports:
1. **Transparent Data Encryption (TDE):** Oracle Database feature encrypts data files, tablespaces
2. **Column-level encryption:** For PII columns (credit cards, bank accounts) using `DBMS_CRYPTO`
3. **Wallet-based key management:** Oracle Wallet stores encryption keys
4. **Network encryption:** Oracle Net Services with native encryption or SSL/TLS

For credit card data (PCI DSS compliance), EBS uses **Payment Card Industry (PCI) compliant tokenization**: actual card numbers are replaced with tokens in the database.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a security framework for EBS that meets SOX (Sarbanes-Oxley) compliance requirements for a multinational company.

**Candidate:** 

**Multi-layer SOX compliance framework:**

**Layer 1 — Access Controls:**
- **User Access Certification (quarterly):** Managers review and certify their direct reports' access
- **Role-Based Access Control:** Pre-defined roles matching job functions
- **Segregation of Duties Matrix:** 100+ conflict rules covering financial cycles (O2C, P2P, GL, F&A)
- **Emergency Access:** FireFighter accounts with full monitoring, auto-expiry

**Layer 2 — Change Management:**
```sql
-- All DDL changes tracked via Oracle Database audit
CREATE OR REPLACE TRIGGER trg_ddl_audit
AFTER DDL ON DATABASE
BEGIN
    INSERT INTO aud_ddl_log(user, timestamp, object_type, object_name, sql_text)
    VALUES (SYS_CONTEXT('USERENV','SESSION_USER'), SYSTIMESTAMP,
            ORA_DICT_OBJ_TYPE, ORA_DICT_OBJ_NAME, ORA_SQL_TXT);
END;
```
- All customizations pass through CEMLI review board
- Changes evaluated by security impact

**Layer 3 — Monitoring & Detection:**
- **Oracle Advanced Security:** Real-time alerting on suspicious activities
- **SIEM integration:** EBS audit logs fed to Splunk or OCI Logging
- **Pattern detection:** Flag unusual login times, high-value transactions, bulk data exports
- **Privileged user monitoring:** All DBA and admin actions logged

**Layer 4 — Data Protection:**
- **TDE enabled** for all tablespaces containing financial data
- **Data masking:** Non-production environments mask sensitive data
- **Key management:** HSM (Hardware Security Module) for encryption keys
- **Backup encryption:** RMAN backup encryption

**Layer 5 — Compliance Reporting:**
- Out-of-box SOX reports: User access, SoD violations, key reports run
- Custom SOX dashboard in EBS or OBIEE
- Quarterly access certification automation
- Audit trail retention: 7 years (as required)

**Interviewer:** How do you manage Oracle EBS users when they change roles or leave the company?

**Candidate:** **User Lifecycle Management:**
1. **New hire:** Automated user creation via HRMS → user provisioning to EBS
2. **Role change:** Automated responsibility update based on job code change in HR
3. **Leave of absence:** Suspend user account (disable but don't delete)
4. **Termination:** Automated user deactivation, transfer open transactions to manager
5. **Rehire:** Reactivate previous user record

Integration: Use Oracle Identity Management (OIM/OAM) for user lifecycle automation across the enterprise. Or use AD integration with EBS SSO.

---

## Interviewer Feedback

**Strengths:** Excellent security knowledge, practical SOX framework design, strong user lifecycle management  
**Areas to Improve:** Could discuss Oracle Database Vault for separation of DBA duties  
**Verdict:** Strong Hire

---

*EBS Security Controls MOCK_INTERVIEW.md — Part of Oracle EBS Academy Interview Preparation*
