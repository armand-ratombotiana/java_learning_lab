# Problem Walkthrough: Upgrade & Migration

## Problem 1: EBS 12.1 to 12.2 Upgrade — Company: Oracle
### EBS Interview Scenario
"You're at Oracle leading an upgrade for a retail client from EBS 12.1.3 to 12.2.10. The client has a 12 TB database, 500 customizations, and requires zero data loss. The go-live window is limited to a 4-day holiday weekend. During pre-upgrade testing, you discover that 30% of customizations use deprecated APIs and the online patching (ADOP) cycle is estimated at 14 hours per patch."

### The Problem
The upgrade must address: (1) Online Patching (ADOP) readiness — custom tables need editioning views, (2) Deprecated APIs — custom code using FND_FILE.PUT_LINE, FND_GLOBAL.APPS_INIT, and old concurrent manager APIs, (3) 4-day downtime window is insufficient for full upgrade + testing, (4) 12 TB database makes cutover risky, (5) Custom schema objects must be editioned for ADOP compatibility.

### Solution Walkthrough
- Step 1: Run the Online Patching Readiness Report to identify all non-editioned objects
- Step 2: Create editioning views for all custom tables using ADOP preparation scripts
- Step 3: Audit all custom PL/SQL for deprecated API usage — replace with FND_LOG, FND_GLOBAL 12.2 equivalents
- Step 4: Implement a phased upgrade approach — upgrade dev/test first, then production
- Step 5: Use ADOP with offline patching cycle for the initial cutover to reduce risk
- Step 6: Implement GoldenGate replication for near-zero downtime cutover
- Step 7: Create a comprehensive fallback plan with database flashback
- Step 8: Perform dress rehearsal three times before production cutover

### Code
```sql
-- Identify non-editioned custom objects
SELECT object_name,
       object_type,
       owner,
       status,
       edition_name
FROM   dba_objects
WHERE  owner LIKE 'XX%'
AND    edition_name IS NULL
AND    object_type IN ('TABLE', 'VIEW', 'PACKAGE', 'PROCEDURE', 'FUNCTION', 'INDEX')
AND    status = 'VALID'
ORDER  BY object_type, object_name;

-- Create editioning view for custom table
-- Step 1: Rename original table
ALTER TABLE xx_custom_invoice_data RENAME TO xx_custom_invoice_data_tb;

-- Step 2: Create editioning view
CREATE OR REPLACE EDITIONING VIEW xx_custom_invoice_data AS
SELECT * FROM xx_custom_invoice_data_tb;

-- Step 3: Create triggers to maintain editioning behavior
CREATE OR REPLACE TRIGGER xx_custom_invoice_data_evt
  INSTEAD OF INSERT OR UPDATE OR DELETE ON xx_custom_invoice_data
  FOR EACH ROW
BEGIN
  IF INSERTING THEN
    INSERT INTO xx_custom_invoice_data_tb VALUES :NEW.*;
  ELSIF UPDATING THEN
    UPDATE xx_custom_invoice_data_tb SET ...;
  ELSIF DELETING THEN
    DELETE FROM xx_custom_invoice_data_tb WHERE ...;
  END IF;
END;
/

-- Replace deprecated FND_FILE.PUT_LINE with FND_LOG
-- BEFORE: FND_FILE.PUT_LINE(FND_FILE.LOG, 'Processing record ' || rec_id);
-- AFTER:
FND_LOG.STRING(FND_LOG.LEVEL_STATEMENT, 'XX_CUSTOM_PKG',
               'Processing record ' || rec_id);
```

### Company Evaluation
- Oracle: ADOP architecture, editioning views, online patching, deprecated API replacement, upgrade methodology.
- Deloitte: Upgrade project management, testing strategy, cutover planning, risk management approach.
- Accenture: Large-scale upgrade factories, automated regression testing, customization remediation patterns.
- PwC: Upgrade SOX compliance, validation of controls post-upgrade, data integrity verification.
- Amazon: Cloud migration with AWS SMS, EBS on EC2 with ADOP optimization, RDS for database tier.

---

## Problem 2: On-Prem to Cloud Migration — Company: Amazon
### EBS Interview Scenario
"You're at Amazon Web Services helping a manufacturing customer migrate their EBS 12.2 environment from on-premises data center to AWS. The customer has 500 concurrent users, a 5 TB database, and requires 99.95% uptime. They want to move from a CAPEX model to OPEX and gain elasticity. The current on-prem infrastructure is at 80% capacity with no room for growth."

### The Problem
The customer's on-prem EBS environment runs on HP-UX with Oracle Database 11g. They want to migrate to AWS (EC2 for app tier, RDS Custom for database). The migration must: (1) Minimize downtime — goal is <2 hours, (2) Support future scaling — app tier auto-scaling based on CPU, (3) Database must support ADOP online patching, (4) Storage costs must be optimized with S3 for archives, (5) Backup strategy must enable point-in-time recovery.

### Solution Walkthrough
- Step 1: Assess current environment — size EC2 instances based on OATM metrics
- Step 2: Set up AWS Direct Connect for low-latency connection
- Step 3: Migrate database using AWS DMS (Database Migration Service) for ongoing replication
- Step 4: Cutover — stop apps, stop replication, promote RDS as primary
- Step 5: Configure Application Tier EC2 with ASG (Auto Scaling Group) + ELB
- Step 6: Set up EBS Cloud Backup Module (optional) or use AWS Backup
- Step 7: Configure CloudWatch monitoring for EBS alerts
- Step 8: Test DR scenario using cross-Region RDS replica

### Code
```sql
-- After migration, update EBS configuration for cloud
-- Configure database connection in apps context file
-- $CONTEXT_FILE: EBS_DB_HOST, EBS_DB_PORT, EBS_DB_SID

-- Update RDS instance as EBS database
BEGIN
  -- Set database link to RDS
  EXECUTE IMMEDIATE 'CREATE DATABASE LINK rds_ebs ' ||
    'CONNECT TO apps IDENTIFIED BY "****" ' ||
    'USING ''(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=ebs-db.xxxxx.rds.amazonaws.com)(PORT=1521))' ||
    '(CONNECT_DATA=(SID=EBSDB)))''';
END;
/

-- Configure EBS for multi-node app tier
-- Verify apps context is correct for ELB
SELECT name, value
FROM   fnd_profile_option_values fpov,
       fnd_profile_options fpo
WHERE  fpov.profile_option_id = fpo.profile_option_id
AND    fpo.profile_option_name IN (
  'APPS_JAVA_AGENT_HOST',
  'APPS_WEB_AGENT_HOST',
  'APPS_FRAMES_AGENT_HOST',
  'ICX_FORMS_LAUNCHER'
);

-- Update for ELB endpoint
BEGIN
  fnd_profile.save('APPS_WEB_AGENT_HOST', 'ebs-elb-123.us-east-1.elb.amazonaws.com', 'SITE');
  fnd_profile.save('APPS_FRAMES_AGENT_HOST', 'ebs-elb-123.us-east-1.elb.amazonaws.com', 'SITE');
  COMMIT;
END;
/
```

### Company Evaluation
- Amazon: AWS migration methodology, DMS, RDS Custom for EBS, EC2 ASG, ELB, CloudWatch, cost optimization.
- Oracle: EBS on AWS reference architecture, OATM sizing, licensing in cloud (ULA/BYOL).
- Deloitte: Cloud TCO analysis, migration business case, risk assessment for cloud migration.
- Accenture: Cloud migration factory, automated migration tools, operating model for cloud-managed EBS.
- PwC: Cloud security controls, compliance in cloud (SOC, HIPAA), data residency considerations.

---

## Problem 3: Database Upgrade — 11g to 19c — Company: Deloitte
### EBS Interview Scenario
"You're at Deloitte managing the database upgrade for EBS from Oracle 11.2.0.4 to 19c (Long-Term Support). The client's database is 8 TB, running on AIX with IBM GPFS storage. The database must be upgraded concurrently with the EBS application patch level update. The client cannot afford more than 8 hours of downtime."

### The Problem
The database upgrade has several challenges: (1) 11.2.0.4 to 19c is a direct upgrade path but requires compatibility checks, (2) The database uses deprecated features (oracle text, advanced replication, materialized views for replication), (3) The 8-hour downtime window must include both database upgrade AND application patching, (4) Custom PL/SQL may have syntax incompatible with 19c, (5) The client uses Oracle RAC with 4 nodes and must maintain RAC configuration.

### Solution Walkthrough
- Step 1: Run pre-upgrade information tool (preupgrd.sql) and fix all identified issues
- Step 2: Address deprecated features — remove advanced replication, migrate materialized views
- Step 3: Use AutoUpgrade tool for parallel upgrade with fallback option
- Step 4: Implement a phased cutover — upgrade standby first, then switchover
- Step 5: Test all custom PL/SQL in 19c compatibility mode — fix syntax issues
- Step 6: Verify optimizer statistics and review SQL plan baselines
- Step 7: Execute the upgrade during planned downtime with full database backup first
- Step 8: Post-upgrade — run database dictionary stats, gather fixed object stats

### Code
```sql
-- Pre-upgrade checks (run on 11g)
-- Execute preupgrd.sql
-- @$ORACLE_HOME/rdbms/admin/preupgrd.sql

-- Check for deprecated database features
SELECT name, version, detected_usages, currently_used
FROM   dba_feature_usage_statistics
WHERE  name IN (
  'Advanced Replication',
  'Materialized View Rewrite',
  'Oracle Text',
  'DBMS_STREAMS',
  'Transportable Tablespaces'
)
AND    currently_used = 'TRUE';

-- Fix PL/SQL for 19c compatibility
-- 19c no longer supports:
--   LONG datatype in PL/SQL (use CLOB)
--   DBMS_SQL.VARIABLE_VALUE with NUMBER (use DBMS_SQL.TO_REFCURSOR)

-- Example fix: Convert LONG to CLOB
-- BEFORE:
--   l_long_data LONG;
--   SELECT notes INTO l_long_data FROM gl_je_headers WHERE ...;
-- AFTER:
DECLARE
  l_clob_data CLOB;
BEGIN
  SELECT TO_CLOB(notes) INTO l_clob_data FROM gl_je_headers WHERE ...;
END;
/

-- Post-upgrade: Gather fixed object stats
EXEC DBMS_STATS.GATHER_FIXED_OBJECTS_STATS;

-- Gather dictionary stats
EXEC DBMS_STATS.GATHER_DATABASE_STATS(
  gather_sys => TRUE,
  options => 'GATHER STALE'
);
```

### Company Evaluation
- Oracle: Database upgrade methodology, AutoUpgrade, pre/post upgrade scripts, deprecated feature management.
- Deloitte: Upgrade project planning, risk assessment, testing strategy, rollback procedures.
- Accenture: Large-database upgrade patterns, RAC migration, storage migration (GPFS to ASM).
- PwC: Database security controls, encryption validation, audit compliance post-upgrade.
- Amazon: Migration to RDS or RDS Custom, zero-downtime upgrades with DMS, Aurora migration path.
