# Problem Walkthrough: HRMS

## Problem 1: Employee Data Migration — Company: Accenture
### EBS Interview Scenario
"You're at Accenture implementing Oracle HRMS for a global bank with 20,000 employees across 15 countries. They are migrating from a legacy SAP HR system. The legacy data has inconsistent employee records — missing managers, invalid national identifiers, and duplicate employee numbers. The go-live date is fixed due to regulatory compliance deadlines."

### The Problem
The legacy system exports employee data as flat files, but the data quality is poor. Only 60% of records pass EBS validation. The key issues include: (1) Supervisor assignments reference employees not in the extract, (2) National identifiers (SSN/SIN/NI) fail format validation per country, (3) Date fields are in mixed formats (MM/DD/YY vs DD-MM-YYYY), (4) Assignment records have overlapping effective dates. The HR team needs all employees loaded with correct hierarchy.

### Solution Walkthrough
- Step 1: Create a pre-validation staging table to capture all source records
- Step 2: Write PL/SQL validation routines for each data domain (person, assignment, supervisor)
- Step 3: Implement a multi-pass load strategy — load people first, then assignments, then supervisors
- Step 4: Handle missing supervisors by creating placeholder generic supervisor records
- Step 5: Normalize date formats and national identifiers using country-specific rules
- Step 6: Use PER_ALL_PEOPLE_F and PER_ALL_ASSIGNMENTS_F APIs for bulk loading
- Step 7: Create a data error report with corrective action tracking
- Step 8: Implement re-validation until 99.9% pass rate achieved

### Code
```sql
-- Staging table for employee data
CREATE TABLE xx_bank_employee_staging (
  source_employee_id    VARCHAR2(20),
  employee_number       VARCHAR2(30),
  first_name            VARCHAR2(150),
  last_name             VARCHAR2(150),
  national_identifier   VARCHAR2(30),
  date_of_birth         VARCHAR2(20),
  hire_date             VARCHAR2(20),
  supervisor_emp_id     VARCHAR2(20),
  assignment_category   VARCHAR2(30),
  location_code         VARCHAR2(20),
  job_code              VARCHAR2(20),
  effective_date        VARCHAR2(20),
  country_code          VARCHAR2(3),
  validation_status     VARCHAR2(20),
  error_message         VARCHAR2(4000),
  processed_flag       VARCHAR2(1) DEFAULT 'N'
);

-- Load person records using HR API
DECLARE
  l_person_id  NUMBER;
  l_assign_id  NUMBER;
BEGIN
  FOR rec IN (
    SELECT * FROM xx_bank_employee_staging
    WHERE validation_status = 'VALID'
    AND processed_flag = 'N'
  ) LOOP
    -- Create person
    l_person_id := hr_person_utility.create_person(
      p_effective_date       => TO_DATE(rec.hire_date, 'YYYY-MM-DD'),
      p_employee_number      => rec.employee_number,
      p_first_name           => rec.first_name,
      p_last_name            => rec.last_name,
      p_national_identifier  => fix_national_id(rec.national_identifier, rec.country_code),
      p_date_of_birth        => TO_DATE(rec.date_of_birth, 'YYYY-MM-DD'),
      p_sex                  => 'U',
      p_per_comment          => 'Migrated from legacy system'
    );
    
    -- Create assignment
    l_assign_id := hr_assignment_api.create_emp_assign(
      p_effective_date             => TO_DATE(rec.effective_date, 'YYYY-MM-DD'),
      p_person_id                  => l_person_id,
      p_assignment_category        => rec.assignment_category,
      p_organization_id           => get_org_id(rec.location_code),
      p_job_id                     => get_job_id(rec.job_code),
      p_supervisor_id             => get_supervisor_id(rec.supervisor_emp_id)
    );
    
    UPDATE xx_bank_employee_staging
    SET processed_flag = 'Y',
        validation_status = 'LOADED'
    WHERE source_employee_id = rec.source_employee_id;
  END LOOP;
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: HRMS APIs — PER_ALL_PEOPLE_F, PER_ALL_ASSIGNMENTS_F, HR_PERSON_UTILITY, HR_ASSIGNMENT_API, effective dating.
- Accenture: Large-scale HR data migration methodology, data quality frameworks, global payroll readiness.
- Deloitte: HR transformation methodology, organizational change management, HR process standardization.
- PwC: Payroll compliance, data privacy (GDPR), employee records audit, SOX for HR controls.
- Amazon: Cloud migration of HR data to AWS, serverless data validation with Lambda, step function orchestration.

---

## Problem 2: Payroll Run Failure — Company: Oracle
### EBS Interview Scenario
"You're at Oracle support for a retail client. Their weekly payroll run failed with "ORA-06502: PL/SQL numeric or value error" during the QuickPay process. It is Wednesday — payroll must be processed by Friday or 5,000 employees will not get paid. The client's payroll team is panicking."

### The Problem
The QuickPay process fails at the element entry calculation step. Investigation reveals that a new "Bonus" element was added by the compensation team with a formula that divides by a value that can be zero. Specifically, the formula references a personal payment method percentage that is null for 200 employees who have not set up direct deposit. The element formula does not handle NULL values.

### Solution Walkthrough
- Step 1: Review the payroll run log for the exact error and element context
- Step 2: Identify the failing element — XX_BONUS_CALC
- Step 3: Analyze the Oracle Fast Formula in PAY_ELEMENT_TYPES_F
- Step 4: Fix the formula to handle NULL divisor with NVL default
- Step 5: Update the formula version using FF_FORMULA_API
- Step 6: Reprocess QuickPay for the affected employees only
- Step 7: Set up element validation to prevent new elements from bypassing formula testing
- Step 8: Create a pre-payroll validation report

### Code
```sql
-- Identify failing elements in payroll run
SELECT pet.element_name,
       pet.reporting_name,
       pif.formula_name,
       pif.formula_text,
       per_assignment_id
FROM   pay_element_types_f pet,
       pay_element_links_f pel,
       pay_input_values_f piv,
       ff_formulas_f pif
WHERE  pet.element_type_id = pel.element_type_id
AND    pel.element_link_id = piv.element_link_id
AND    piv.formula_id = pif.formula_id
AND    pet.element_name = 'XX_BONUS_CALC'
AND    SYSDATE BETWEEN pet.effective_start_date AND pet.effective_end_date;

-- Fix the formula to handle NULL divisor
-- Original: BONUS_AMOUNT = SALARY * BONUS_PCT / DIVISOR
-- Fixed:
/*
DEFAULT FOR DIVISOR IS 1
INPUTS ARE BONUS_PCT, DIVISOR
IF DIVISOR = 0 THEN
  DIVISOR = 1
END IF
BONUS_AMOUNT = SALARY * BONUS_PCT / DIVISOR
RETURN BONUS_AMOUNT
*/

-- Update formula using API
BEGIN
  ff_formula_api.update_formula(
    p_formula_name          => 'XX_BONUS_FORMULA',
    p_formula_type          => 'FF',
    p_description           => 'Fixed NULL divisor handling',
    p_formula_text          => 'DEFAULT FOR DIVISOR IS 1' || CHR(10) ||
                               'INPUTS ARE BONUS_PCT, DIVISOR' || CHR(10) ||
                               'IF DIVISOR = 0 THEN' || CHR(10) ||
                               '  DIVISOR = 1' || CHR(10) ||
                               'END IF' || CHR(10) ||
                               'BONUS_AMOUNT = SALARY * BONUS_PCT / DIVISOR' || CHR(10) ||
                               'RETURN BONUS_AMOUNT',
    p_effective_start_date  => SYSDATE,
    p_business_group_id     => 101
  );
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: Payroll architecture — PAY_ELEMENT_TYPES_F, PAY_RUN_RESULTS, PAY_ASSIGNMENT_ACTIONS, Oracle Fast Formula, QuickPay process flow.
- Deloitte: Payroll implementation methodology, parallel payroll testing, payroll reconciliation controls.
- Accenture: Multi-country payroll, legislative rule groups, third-party payroll integration patterns.
- PwC: Payroll compliance audit, tax withholding controls, SOX for payroll processing, employee records privacy.
- Amazon: Cloud payroll processing, automated regression testing for payroll formulas, disaster recovery runbooks.

---

## Problem 3: Absence Management — Company: PwC
### EBS Interview Scenario
"You're at PwC auditing the HRMS implementation at a manufacturing client. The client's absence management process lacks proper controls — employees can enter sick leave retroactively without approvals, and managers can approve their own time off. The audit team identifies a material weakness in time and labor reporting."

### The Problem
The client configured Oracle HRMS Absence Management without any approval workflow. Employees enter absences directly in Self-Service HR and the absence is automatically approved. There is no validation for insufficient leave balance, no manager approval workflow, and no segregation between time entry and time approval. A test audit found 15 cases where managers approved their own absence requests.

### Solution Walkthrough
- Step 1: Review current absence types and their approval configuration in PER_ABSENCE_ATTENDANCES
- Step 2: Implement approval workflow using Oracle Approvals Management (AME) for absences
- Step 3: Configure SOD rules preventing self-approval
- Step 4: Set up absence balance validation before submission
- Step 5: Implement retroactive absence restriction (>30 days requires HR intervention)
- Step 6: Create audit trail for absence modifications
- Step 7: Configure monthly absence reconciliation report for HR

### Code
```sql
-- Review absence entries without approvals
SELECT paa.person_id,
       ppf.employee_number,
       ppf.full_name,
       paat.name AS absence_type,
       paa.date_start,
       paa.date_end,
       paa.absence_days,
       paa.approved_flag,
       paa.authorizing_person_id,
       auth.employee_number AS authorizer_emp_number
FROM   per_absence_attendances paa,
       per_absence_attendance_types_tl paat,
       per_people_f ppf,
       per_people_f auth
WHERE  paa.absence_attendance_type_id = paat.absence_attendance_type_id
AND    paa.person_id = ppf.person_id
AND    paa.authorizing_person_id = auth.person_id(+)
AND    paa.approved_flag = 'Y'
AND    paa.date_start > SYSDATE - 90
AND    (SYSDATE BETWEEN ppf.effective_start_date AND ppf.effective_end_date)
ORDER  BY paa.date_start DESC;

-- Set up AME approval for absences
BEGIN
  ame_util.create_approval_rule(
    p_rule_name    => 'ABSENCE_APPROVAL_MANAGER',
    p_rule_type    => 'LIST',
    p_item_type    => 'ABSENCE',
    p_condition    => 'ABSENCE_DAYS <= 10',
    p_approver_type => 'SUPERVISOR',
    p_sequence     => 10
  );
  
  ame_util.create_approval_rule(
    p_rule_name    => 'ABSENCE_APPROVAL_HR',
    p_rule_type    => 'LIST',
    p_item_type    => 'ABSENCE',
    p_condition    => 'ABSENCE_DAYS > 10',
    p_approver_type => 'POSITION',
    p_sequence     => 20
  );
  COMMIT;
END;
/
```

### Company Evaluation
- PwC: HR SOX controls, segregation of duties in HRMS, absence management audit, time reporting compliance.
- Oracle: Absence management APIs, AME integration for HR, PER_ABSENCE_ATTENDANCES table, self-service HR workflow.
- Deloitte: HR process design, time and labor policy, absence management best practices.
- Accenture: Global time tracking, union/labor rule compliance, absence accrual rules per country.
- Amazon: Automated absence management with machine learning for pattern detection, compliance monitoring.
