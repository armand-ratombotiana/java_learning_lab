# Problem Walkthrough: Employee Lifecycle Management from Hire to Termination

## Problem Statement

**Design and implement a complete employee lifecycle management system in Oracle EBS HRMS R12.2 that automates the end-to-end journey from recruiting and hiring through active employment, development, promotions, and termination.**

The client is a global financial services firm with 25,000 employees across 18 countries. They currently use disparate systems for recruiting (external ATS), HR (EBS HRMS), payroll (ADP), and offboarding (manual spreadsheets), with no integration between them. Each employee lifecycle event requires 3-5 days of manual data entry across multiple systems. The CHRO demands a unified, automated lifecycle management system that eliminates duplicate data entry, enforces compliance with local labor laws, and provides a complete audit trail from hire to alumni status.

### Business Requirements
- Single system of record for all employee lifecycle events
- Automated triggers: hire → onboarding → payroll setup → benefits enrollment
- Compliance with 18 countries' labor laws (termination notice periods, final pay calculations)
- Lifecycle event audit trail with 7-year retention
- Seamless integration with external payroll provider (ADP)
- Manager self-service for promotions, transfers, and terminations
- Complete offboarding with exit checklist automation

### Technical Constraints
- Oracle EBS R12.2 HRMS (HR, Payroll, SSHR)
- 25,000 active employees, 8,000 alumni/year (33% turnover)
- 18 legislative data groups (US, UK, DE, FR, JP, SG, AU, BR, IN, etc.)
- Integration with ADP Global View for payroll processing
- Integration with Active Directory for identity management
- 7-year data retention policy for terminated employees

---

## Solution Architecture

```
                  ┌─────────────────────────────┐
                  │    Employee Lifecycle Core    │
                  └─────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
┌──────────────────┐ ┌───────────────┐ ┌──────────────────┐
│   Hire-to-Retire  │ │  Active Mgmt  │ │  Exit-to-Alumni  │
│   Lifecycle       │ │  Lifecycle    │ │  Lifecycle       │
├──────────────────┤ ├───────────────┤ ├──────────────────┤
│ • Offer to Hire  │ │ • Promotions  │ │ • Resignation    │
│ • Onboarding     │ │ • Transfers   │ │ • Termination    │
│ • Payroll Setup  │ │ • Compensation│ │ • Clearance      │
│ • Benefits Enroll│ │ • Performance │ │ • Final Pay      │
│ • IT Provisioning│ │ • Development │ │ • Alumni Status  │
└──────────────────┘ └───────────────┘ └──────────────────┘
         │                    │                    │
         └────────────────────┼────────────────────┘
                              ▼
              ┌─────────────────────────────┐
              │     Integration Layer        │
              ├─────────────────────────────┤
              │ ADP Payroll │ AD │ Benefits │
              └─────────────────────────────┘
```

### Step 1: Hire-to-Retire Lifecycle Foundation

Create the core lifecycle management tables:

```sql
-- Lifecycle event type configuration
CREATE TABLE xx_hrms_lifecycle_events (
  event_code          VARCHAR2(30) PRIMARY KEY,
  event_name          VARCHAR2(100) NOT NULL,
  event_category      VARCHAR2(30) NOT NULL,  -- HIRE, PROMOTION, TRANSFER, TERMINATION, etc.
  requires_approval   VARCHAR2(1) DEFAULT 'Y',
  effective_dating    VARCHAR2(1) DEFAULT 'Y',  -- Uses EBS effective dating
  notification_template VARCHAR2(100),
  enabled_flag        VARCHAR2(1) DEFAULT 'Y',
  created_by          NUMBER,
  creation_date       DATE DEFAULT SYSDATE
);

-- Lifecycle checklist templates
CREATE TABLE xx_hrms_checklist_templates (
  template_id         NUMBER PRIMARY KEY,
  template_name       VARCHAR2(100) NOT NULL,
  event_code          VARCHAR2(30) NOT NULL REFERENCES xx_hrms_lifecycle_events(event_code),
  sequence_num        NUMBER NOT NULL,
  task_name           VARCHAR2(200) NOT NULL,
  assigned_role       VARCHAR2(50),          -- HR, IT, MANAGER, PAYROLL, FACILITIES
  sla_hours           NUMBER DEFAULT 24,
  mandatory_flag      VARCHAR2(1) DEFAULT 'Y',
  created_by          NUMBER,
  creation_date       DATE DEFAULT SYSDATE
);

-- Lifecycle instance (per employee per event)
CREATE TABLE xx_hrms_lifecycle_instances (
  instance_id         NUMBER PRIMARY KEY,
  person_id           NUMBER NOT NULL,
  event_code          VARCHAR2(30) NOT NULL,
  event_date          DATE NOT NULL,
  effective_date      DATE,
  status              VARCHAR2(20) DEFAULT 'PENDING',  -- PENDING, IN_PROGRESS, COMPLETED, CANCELLED
  initiated_by        NUMBER,
  initiated_date      DATE DEFAULT SYSDATE,
  completed_by        NUMBER,
  completed_date      DATE,
  notes               VARCHAR2(2000),
  attribute_category  VARCHAR2(30),
  attribute1-15       VARCHAR2(150)
);

-- Lifecycle task execution tracking
CREATE TABLE xx_hrms_lifecycle_tasks (
  task_id             NUMBER PRIMARY KEY,
  instance_id         NUMBER NOT NULL REFERENCES xx_hrms_lifecycle_instances(instance_id),
  template_id         NUMBER NOT NULL REFERENCES xx_hrms_checklist_templates(template_id),
  task_name           VARCHAR2(200) NOT NULL,
  assigned_to         NUMBER,                -- Person ID of assignee
  assigned_role       VARCHAR2(50),
  status              VARCHAR2(20) DEFAULT 'PENDING',
  started_date        DATE,
  completed_date      DATE,
  completion_note     VARCHAR2(2000),
  created_by          NUMBER,
  creation_date       DATE DEFAULT SYSDATE
);

-- Sequences
CREATE SEQUENCE xx_hrms_lifecycle_instances_s;
CREATE SEQUENCE xx_hrms_lifecycle_tasks_s;
CREATE SEQUENCE xx_hrms_checklist_templates_s;
```

### Step 2: New Hire Process

Implement the complete new hire workflow from offer acceptance to active employee:

```sql
CREATE OR REPLACE PACKAGE xx_hrms_new_hire_pkg AS

  TYPE hire_employee_rec IS RECORD (
    person_id             NUMBER,
    employee_number       VARCHAR2(30),
    first_name            VARCHAR2(150),
    last_name             VARCHAR2(150),
    date_of_birth         DATE,
    hire_date             DATE,
    email_address         VARCHAR2(240),
    national_identifier   VARCHAR2(30),
    business_group_id     NUMBER,
    legal_employer_id     NUMBER,
    organization_id       NUMBER,
    position_id           NUMBER,
    job_id                NUMBER,
    grade_id              NUMBER,
    location_id           NUMBER,
    supervisor_id         NUMBER,
    salary                NUMBER,
    currency_code         VARCHAR2(15),
    pay_basis             VARCHAR2(30),
    frequency             VARCHAR2(30)
  );

  PROCEDURE create_new_hire_lifecycle(
    p_hire_rec        IN hire_employee_rec,
    p_lifecycle_id    OUT NUMBER
  );

  PROCEDURE process_onboarding_step(
    p_instance_id     NUMBER,
    p_task_id         NUMBER,
    p_completed_by    NUMBER
  );

  PROCEDURE finalize_hire(
    p_instance_id     NUMBER,
    p_completed_by    NUMBER,
    p_person_id       OUT NUMBER
  );

END xx_hrms_new_hire_pkg;
/

CREATE OR REPLACE PACKAGE BODY xx_hrms_new_hire_pkg AS

  PROCEDURE create_new_hire_lifecycle(
    p_hire_rec        IN hire_employee_rec,
    p_lifecycle_id    OUT NUMBER
  ) IS
    l_instance_id NUMBER;
    l_person_id   NUMBER;
  BEGIN
    -- Generate temporary person number
    l_person_id := hr_person_utility.create_person(
      p_effective_date       => p_hire_rec.hire_date,
      p_employee_number      => p_hire_rec.employee_number,
      p_first_name           => p_hire_rec.first_name,
      p_last_name            => p_hire_rec.last_name,
      p_date_of_birth        => p_hire_rec.date_of_birth,
      p_national_identifier  => p_hire_rec.national_identifier,
      p_email_address        => p_hire_rec.email_address,
      p_sex                  => 'U',
      p_per_comment          => 'New hire - lifecycle initiated'
    );

    -- Create lifecycle instance
    l_instance_id := xx_hrms_lifecycle_instances_s.NEXTVAL;

    INSERT INTO xx_hrms_lifecycle_instances (
      instance_id, person_id, event_code, event_date,
      effective_date, status, initiated_by, initiated_date
    ) VALUES (
      l_instance_id, l_person_id, 'HIRE',
      p_hire_rec.hire_date, p_hire_rec.hire_date,
      'IN_PROGRESS', p_hire_rec.supervisor_id, SYSDATE
    );

    -- Create onboarding tasks from template
    INSERT INTO xx_hrms_lifecycle_tasks (
      task_id, instance_id, template_id, task_name,
      assigned_role, status, created_by, creation_date
    )
    SELECT xx_hrms_lifecycle_tasks_s.NEXTVAL,
           l_instance_id,
           template_id,
           task_name,
           assigned_role,
           'PENDING',
           p_hire_rec.supervisor_id,
           SYSDATE
    FROM   xx_hrms_checklist_templates
    WHERE  event_code = 'HIRE'
    ORDER  BY sequence_num;

    -- Create HRMS assignment (pending — finalized on completion)
    hr_assignment_api.create_emp_assign(
      p_effective_date       => p_hire_rec.hire_date,
      p_person_id            => l_person_id,
      p_assignment_category  => 'E',
      p_organization_id      => p_hire_rec.organization_id,
      p_position_id          => p_hire_rec.position_id,
      p_job_id              => p_hire_rec.job_id,
      p_grade_id             => p_hire_rec.grade_id,
      p_location_id          => p_hire_rec.location_id,
      p_supervisor_id        => p_hire_rec.supervisor_id
    );

    -- Create salary record
    hr_salary_api.create_salary(
      p_person_id        => l_person_id,
      p_proposed_salary  => p_hire_rec.salary,
      p_currency_code    => p_hire_rec.currency_code,
      p_pay_basis        => p_hire_rec.pay_basis,
      p_frequency        => p_hire_rec.frequency,
      p_effective_date   => p_hire_rec.hire_date
    );

    COMMIT;

    p_lifecycle_id := l_instance_id;

    DBMS_OUTPUT.PUT_LINE('New hire lifecycle created: ' || l_instance_id
      || ' for person: ' || l_person_id);
  END create_new_hire_lifecycle;

  PROCEDURE process_onboarding_step(
    p_instance_id     NUMBER,
    p_task_id         NUMBER,
    p_completed_by    NUMBER
  ) IS
  BEGIN
    UPDATE xx_hrms_lifecycle_tasks
    SET status = 'COMPLETED',
        completed_date = SYSDATE,
        completed_by = p_completed_by,
        assigned_to = p_completed_by
    WHERE task_id = p_task_id
    AND instance_id = p_instance_id;

    COMMIT;
  END process_onboarding_step;

  PROCEDURE finalize_hire(
    p_instance_id     NUMBER,
    p_completed_by    NUMBER,
    p_person_id       OUT NUMBER
  ) IS
    l_person_id   NUMBER;
    l_task_id     NUMBER;
    l_all_done    VARCHAR2(1) := 'Y';
  BEGIN
    -- Get person ID
    SELECT person_id INTO l_person_id
    FROM   xx_hrms_lifecycle_instances
    WHERE  instance_id = p_instance_id;

    -- Check all mandatory tasks are complete
    FOR rec IN (
      SELECT task_id
      FROM   xx_hrms_lifecycle_tasks t,
             xx_hrms_checklist_templates ct
      WHERE  t.template_id = ct.template_id
      AND    t.instance_id = p_instance_id
      AND    ct.mandatory_flag = 'Y'
      AND    t.status != 'COMPLETED'
    ) LOOP
      l_all_done := 'N';
    END LOOP;

    IF l_all_done = 'Y' THEN
      -- Update lifecycle status
      UPDATE xx_hrms_lifecycle_instances
      SET status = 'COMPLETED',
          completed_by = p_completed_by,
          completed_date = SYSDATE
      WHERE instance_id = p_instance_id;

      -- Activate employee assignment (effective dated)
      hr_assignment_api.update_assignment(
        p_person_id        => l_person_id,
        p_effective_date   => SYSDATE,
        p_assignment_status => 'ACTIVE'
      );

      COMMIT;
      p_person_id := l_person_id;

      DBMS_OUTPUT.PUT_LINE('Hire finalized for person: ' || l_person_id);
    ELSE
      DBMS_OUTPUT.PUT_LINE('Cannot finalize — mandatory tasks incomplete');
      p_person_id := NULL;
    END IF;
  END finalize_hire;

END xx_hrms_new_hire_pkg;
/
```

### Step 3: Employee Promotion/Transfer Lifecycle

```sql
CREATE OR REPLACE PACKAGE xx_hrms_movement_pkg AS

  PROCEDURE initiate_promotion(
    p_person_id         NUMBER,
    p_new_job_id        NUMBER,
    p_new_grade_id      NUMBER,
    p_new_salary        NUMBER,
    p_effective_date    DATE,
    p_requested_by      NUMBER,
    p_movement_id       OUT NUMBER
  );

  PROCEDURE initiate_transfer(
    p_person_id         NUMBER,
    p_new_organization_id NUMBER,
    p_new_location_id   NUMBER,
    p_effective_date    DATE,
    p_requested_by      NUMBER,
    p_movement_id       OUT NUMBER
  );

  PROCEDURE approve_movement(
    p_movement_id       NUMBER,
    p_approved_by       NUMBER
  );

  PROCEDURE execute_movement(
    p_movement_id       NUMBER
  );

END xx_hrms_movement_pkg;
/

CREATE OR REPLACE PACKAGE BODY xx_hrms_movement_pkg AS

  PROCEDURE initiate_promotion(
    p_person_id         NUMBER,
    p_new_job_id        NUMBER,
    p_new_grade_id      NUMBER,
    p_new_salary        NUMBER,
    p_effective_date    DATE,
    p_requested_by      NUMBER,
    p_movement_id       OUT NUMBER
  ) IS
    l_instance_id NUMBER;
    l_current_job NUMBER;
    l_current_salary NUMBER;
  BEGIN
    -- Get current job and salary
    SELECT job_id, proposed_salary
    INTO   l_current_job, l_current_salary
    FROM   per_all_assignments_f paf,
           per_all_people_f pap,
           pay_proposed_salary_records ppsr
    WHERE  paf.person_id = p_person_id
    AND    pap.person_id = paf.person_id
    AND    ppsr.assignment_id = paf.assignment_id
    AND    SYSDATE BETWEEN paf.effective_start_date AND paf.effective_end_date
    AND    ROWNUM = 1;

    -- Create lifecycle instance
    l_instance_id := xx_hrms_lifecycle_instances_s.NEXTVAL;

    INSERT INTO xx_hrms_lifecycle_instances (
      instance_id, person_id, event_code, event_date,
      effective_date, status, initiated_by, initiated_date
    ) VALUES (
      l_instance_id, p_person_id, 'PROMOTION',
      p_effective_date, p_effective_date,
      'PENDING_APPROVAL', p_requested_by, SYSDATE
    );

    -- Store movement details
    INSERT INTO xx_hrms_movement_details (
      movement_id, instance_id, person_id,
      previous_job_id, new_job_id,
      previous_grade_id, new_grade_id,
      previous_salary, new_salary,
      effective_date, approval_status
    ) VALUES (
      xx_hrms_movement_details_s.NEXTVAL, l_instance_id, p_person_id,
      l_current_job, p_new_job_id,
      NULL, p_new_grade_id,
      l_current_salary, p_new_salary,
      p_effective_date, 'PENDING_APPROVAL'
    ) RETURNING movement_id INTO p_movement_id;

    COMMIT;
  END initiate_promotion;

  PROCEDURE approve_movement(
    p_movement_id       NUMBER,
    p_approved_by       NUMBER
  ) IS
  BEGIN
    UPDATE xx_hrms_movement_details
    SET approval_status = 'APPROVED',
        approved_by = p_approved_by,
        approval_date = SYSDATE
    WHERE movement_id = p_movement_id;

    -- Update lifecycle
    UPDATE xx_hrms_lifecycle_instances
    SET status = 'IN_PROGRESS',
        notes = 'Approved by ' || p_approved_by
    WHERE instance_id = (
      SELECT instance_id
      FROM   xx_hrms_movement_details
      WHERE  movement_id = p_movement_id
    );

    COMMIT;
  END approve_movement;

  PROCEDURE execute_movement(
    p_movement_id       NUMBER
  ) IS
    l_rec xx_hrms_movement_details%ROWTYPE;
    l_person_id NUMBER;
  BEGIN
    SELECT * INTO l_rec
    FROM   xx_hrms_movement_details
    WHERE  movement_id = p_movement_id;

    IF l_rec.approval_status != 'APPROVED' THEN
      RAISE_APPLICATION_ERROR(-20001, 'Movement not yet approved');
    END IF;

    -- Update assignment with new job and grade (effective-dated)
    FOR assign_rec IN (
      SELECT assignment_id
      FROM   per_all_assignments_f
      WHERE  person_id = l_rec.person_id
      AND    SYSDATE BETWEEN effective_start_date AND effective_end_date
    ) LOOP
      hr_assignment_api.update_assignment(
        p_assign_id        => assign_rec.assignment_id,
        p_effective_date   => l_rec.effective_date,
        p_job_id           => l_rec.new_job_id,
        p_grade_id         => l_rec.new_grade_id
      );
    END LOOP;

    -- Update salary
    FOR assign_rec IN (
      SELECT assignment_id
      FROM   per_all_assignments_f
      WHERE  person_id = l_rec.person_id
    ) LOOP
      hr_salary_api.update_salary(
        p_assignment_id    => assign_rec.assignment_id,
        p_proposed_salary  => l_rec.new_salary,
        p_effective_date   => l_rec.effective_date
      );
    END LOOP;

    -- Update lifecycle complete
    UPDATE xx_hrms_lifecycle_instances
    SET status = 'COMPLETED',
        completed_date = SYSDATE
    WHERE instance_id = l_rec.instance_id;

    COMMIT;
  END execute_movement;

END xx_hrms_movement_pkg;
/
```

### Step 4: Performance Management Lifecycle Integration

Link performance reviews to lifecycle events for promotions and compensation:

```sql
CREATE OR REPLACE PACKAGE xx_hrms_performance_pkg AS

  PROCEDURE create_performance_review(
    p_person_id       NUMBER,
    p_review_period   VARCHAR2,  -- 'Q1_2026', 'ANNUAL_2025'
    p_reviewer_id     NUMBER,
    p_review_id       OUT NUMBER
  );

  PROCEDURE finalize_review(
    p_review_id       NUMBER,
    p_rating          NUMBER,    -- 1-5 scale
    p_comments        VARCHAR2
  );

  PROCEDURE process_review_outcome(
    p_review_id       NUMBER,
    p_promotion_recommended VARCHAR2 DEFAULT 'N',
    p_salary_adjustment_pct NUMBER DEFAULT 0
  );

END xx_hrms_performance_pkg;
/

CREATE OR REPLACE PACKAGE BODY xx_hrms_performance_pkg AS

  PROCEDURE create_performance_review(
    p_person_id       NUMBER,
    p_review_period   VARCHAR2,
    p_reviewer_id     NUMBER,
    p_review_id       OUT NUMBER
  ) IS
  BEGIN
    SELECT xx_hrms_review_s.NEXTVAL INTO p_review_id FROM DUAL;

    INSERT INTO xx_hrms_performance_reviews (
      review_id, person_id, review_period,
      reviewer_id, review_status, created_date
    ) VALUES (
      p_review_id, p_person_id, p_review_period,
      p_reviewer_id, 'PENDING', SYSDATE
    );

    INSERT INTO xx_hrms_lifecycle_instances (
      instance_id, person_id, event_code, event_date,
      effective_date, status, initiated_by, initiated_date
    ) VALUES (
      xx_hrms_lifecycle_instances_s.NEXTVAL,
      p_person_id, 'PERFORMANCE_REVIEW',
      SYSDATE, SYSDATE, 'IN_PROGRESS',
      p_reviewer_id, SYSDATE
    );

    COMMIT;
  END create_performance_review;

  PROCEDURE finalize_review(
    p_review_id       NUMBER,
    p_rating          NUMBER,
    p_comments        VARCHAR2
  ) IS
  BEGIN
    UPDATE xx_hrms_performance_reviews
    SET overall_rating = p_rating,
        reviewer_comments = p_comments,
        review_status = 'COMPLETED',
        completed_date = SYSDATE
    WHERE review_id = p_review_id;

    COMMIT;
  END finalize_review;

  PROCEDURE process_review_outcome(
    p_review_id       NUMBER,
    p_promotion_recommended VARCHAR2 DEFAULT 'N',
    p_salary_adjustment_pct NUMBER DEFAULT 0
  ) IS
    l_person_id    NUMBER;
    l_current_sal  NUMBER;
    l_new_salary   NUMBER;
    l_instance_id  NUMBER;
  BEGIN
    SELECT person_id INTO l_person_id
    FROM   xx_hrms_performance_reviews
    WHERE  review_id = p_review_id;

    IF p_salary_adjustment_pct > 0 THEN
      -- Calculate new salary
      SELECT NVL(MAX(proposed_salary), 0)
      INTO   l_current_sal
      FROM   pay_proposed_salary_records ppsr,
             per_all_assignments_f paf
      WHERE  ppsr.assignment_id = paf.assignment_id
      AND    paf.person_id = l_person_id
      AND    SYSDATE BETWEEN paf.effective_start_date AND paf.effective_end_date;

      l_new_salary := l_current_sal * (1 + p_salary_adjustment_pct / 100);

      -- Create lifecycle event for salary change
      l_instance_id := xx_hrms_lifecycle_instances_s.NEXTVAL;

      INSERT INTO xx_hrms_lifecycle_instances (
        instance_id, person_id, event_code, event_date,
        effective_date, status, notes
      ) VALUES (
        l_instance_id, l_person_id, 'SALARY_ADJUSTMENT',
        SYSDATE, SYSDATE, 'COMPLETED',
        'Salary adjustment ' || p_salary_adjustment_pct || '% based on review ' || p_review_id
      );
    END IF;

    IF p_promotion_recommended = 'Y' THEN
      DBMS_OUTPUT.PUT_LINE('Promotion recommended for person: ' || l_person_id
        || ' — initiate promotion workflow');
    END IF;

    COMMIT;
  END process_review_outcome;

END xx_hrms_performance_pkg;
/
```

### Step 5: Termination Lifecycle

Implement compliant termination processing:

```sql
CREATE OR REPLACE PACKAGE xx_hrms_termination_pkg AS

  TYPE termination_reason_rec IS RECORD (
    reason_code       VARCHAR2(30),
    notice_period_days NUMBER,
    final_pay_rules   VARCHAR2(200),
    cobra_eligible    VARCHAR2(1),
    rehire_eligible   VARCHAR2(1)
  );

  PROCEDURE initiate_termination(
    p_person_id           NUMBER,
    p_termination_date    DATE,
    p_termination_reason  VARCHAR2,
    p_last_working_date   DATE,
    p_initiated_by        NUMBER,
    p_instance_id         OUT NUMBER
  );

  PROCEDURE process_exit_checklist(
    p_instance_id     NUMBER,
    p_task_code       VARCHAR2,
    p_completed_by    NUMBER,
    p_completion_note VARCHAR2
  );

  PROCEDURE calculate_final_pay(
    p_instance_id     NUMBER,
    p_final_pay_amount OUT NUMBER
  );

  PROCEDURE execute_termination(
    p_instance_id     NUMBER,
    p_completed_by    NUMBER
  );

END xx_hrms_termination_pkg;
/

CREATE OR REPLACE PACKAGE BODY xx_hrms_termination_pkg AS

  -- Country-specific termination rules
  FUNCTION get_termination_rules(
    p_legislative_code VARCHAR2,
    p_reason_code      VARCHAR2
  ) RETURN termination_reason_rec IS
    l_rec termination_reason_rec;
  BEGIN
    -- US Rules
    IF p_legislative_code = 'US' THEN
      IF p_reason_code = 'RESIGNATION' THEN
        l_rec.notice_period_days := 0;     -- At-will employment
        l_rec.final_pay_rules := 'NEXT_SCHEDULED_PAY_DATE';
        l_rec.cobra_eligible := 'Y';
        l_rec.rehire_eligible := 'Y';
      ELSIF p_reason_code = 'TERMINATION_FOR_CAUSE' THEN
        l_rec.notice_period_days := 0;
        l_rec.final_pay_rules := 'IMMEDIATE';
        l_rec.cobra_eligible := 'Y';
        l_rec.rehire_eligible := 'N';
      ELSIF p_reason_code = 'LAYOFF' THEN
        l_rec.notice_period_days := 60;    -- WARN Act
        l_rec.final_pay_rules := 'SEVERANCE_CALCULATION';
        l_rec.cobra_eligible := 'Y';
        l_rec.rehire_eligible := 'Y';
      END IF;

    -- UK Rules
    ELSIF p_legislative_code = 'GB' THEN
      IF p_reason_code = 'RESIGNATION' THEN
        l_rec.notice_period_days := 28;    -- 1 week to 1 month
        l_rec.final_pay_rules := 'ACCRUED_HOLIDAY_CALCULATION';
        l_rec.cobra_eligible := 'N';
        l_rec.rehire_eligible := 'Y';
      ELSIF p_reason_code = 'REDUNDANCY' THEN
        l_rec.notice_period_days := 90;    -- Statutory redundancy
        l_rec.final_pay_rules := 'REDUNDANCY_PAY_CALCULATION';
        l_rec.cobra_eligible := 'N';
        l_rec.rehire_eligible := 'Y';
      END IF;

    -- Germany
    ELSIF p_legislative_code = 'DE' THEN
      IF p_reason_code = 'RESIGNATION' THEN
        l_rec.notice_period_days := 28;
        l_rec.final_pay_rules := 'STATUTORY_NOTICE';
        l_rec.cobra_eligible := 'N';
        l_rec.rehire_eligible := 'Y';
      ELSIF p_reason_code = 'TERMINATION_BY_EMPLOYER' THEN
        l_rec.notice_period_days := 180;   -- Up to 6 months protected
        l_rec.final_pay_rules := 'SEVERANCE_LEGAL';
        l_rec.cobra_eligible := 'N';
        l_rec.rehire_eligible := 'N';
      END IF;

    -- Default
    ELSE
      l_rec.notice_period_days := 30;
      l_rec.final_pay_rules := 'STANDARD';
      l_rec.cobra_eligible := 'N';
      l_rec.rehire_eligible := 'Y';
    END IF;

    RETURN l_rec;
  END get_termination_rules;

  PROCEDURE initiate_termination(
    p_person_id           NUMBER,
    p_termination_date    DATE,
    p_termination_reason  VARCHAR2,
    p_last_working_date   DATE,
    p_initiated_by        NUMBER,
    p_instance_id         OUT NUMBER
  ) IS
    l_instance_id NUMBER;
    l_legislative_code VARCHAR2(30);
  BEGIN
    -- Get employee's legislative code
    SELECT bg.legislative_code
    INTO   l_legislative_code
    FROM   per_all_assignments_f paf,
           per_business_groups bg
    WHERE  paf.person_id = p_person_id
    AND    paf.business_group_id = bg.business_group_id
    AND    SYSDATE BETWEEN paf.effective_start_date AND paf.effective_end_date
    AND    ROWNUM = 1;

    -- Create lifecycle instance
    l_instance_id := xx_hrms_lifecycle_instances_s.NEXTVAL;

    INSERT INTO xx_hrms_lifecycle_instances (
      instance_id, person_id, event_code, event_date,
      effective_date, status, initiated_by, initiated_date, notes
    ) VALUES (
      l_instance_id, p_person_id, 'TERMINATION',
      p_termination_date, p_termination_date,
      'PENDING_EXIT_CHECKLIST', p_initiated_by, SYSDATE,
      'Reason: ' || p_termination_reason
      || ', Last working day: ' || TO_CHAR(p_last_working_date, 'YYYY-MM-DD')
    );

    -- Create exit checklist from termination templates
    INSERT INTO xx_hrms_lifecycle_tasks (
      task_id, instance_id, template_id, task_name,
      assigned_role, status, created_by, creation_date
    )
    SELECT xx_hrms_lifecycle_tasks_s.NEXTVAL,
           l_instance_id, template_id, task_name,
           assigned_role, 'PENDING',
           p_initiated_by, SYSDATE
    FROM   xx_hrms_checklist_templates
    WHERE  event_code = 'TERMINATION'
    ORDER  BY sequence_num;

    -- Insert country-specific compliance tasks
    IF l_legislative_code = 'DE' THEN
      INSERT INTO xx_hrms_lifecycle_tasks (
        task_id, instance_id, template_id, task_name,
        assigned_role, status
      ) VALUES (
        xx_hrms_lifecycle_tasks_s.NEXTVAL, l_instance_id,
        NULL, 'Works Council notification - 14 day consultation period',
        'HR', 'PENDING'
      );
    ELSIF l_legislative_code = 'GB' THEN
      INSERT INTO xx_hrms_lifecycle_tasks (
        task_id, instance_id, template_id, task_name,
        assigned_role, status
      ) VALUES (
        xx_hrms_lifecycle_tasks_s.NEXTVAL, l_instance_id,
        NULL, 'P45 preparation and HMRC notification',
        'PAYROLL', 'PENDING'
      );
    END IF;

    -- Record termination details
    INSERT INTO xx_hrms_termination_details (
      termination_id, instance_id, person_id,
      termination_date, last_working_date, termination_reason,
      legislative_code, notice_period_days,
      final_pay_method, cobra_eligible, rehire_eligible
    ) VALUES (
      xx_hrms_termination_details_s.NEXTVAL, l_instance_id, p_person_id,
      p_termination_date, p_last_working_date, p_termination_reason,
      l_legislative_code,
      get_termination_rules(l_legislative_code, p_termination_reason).notice_period_days,
      get_termination_rules(l_legislative_code, p_termination_reason).final_pay_rules,
      get_termination_rules(l_legislative_code, p_termination_reason).cobra_eligible,
      get_termination_rules(l_legislative_code, p_termination_reason).rehire_eligible
    );

    COMMIT;
    p_instance_id := l_instance_id;

    DBMS_OUTPUT.PUT_LINE('Termination initiated: ' || l_instance_id
      || ' for person ' || p_person_id);
  END initiate_termination;

  PROCEDURE process_exit_checklist(
    p_instance_id     NUMBER,
    p_task_code       VARCHAR2,
    p_completed_by    NUMBER,
    p_completion_note VARCHAR2
  ) IS
  BEGIN
    UPDATE xx_hrms_lifecycle_tasks
    SET status = 'COMPLETED',
        assigned_to = p_completed_by,
        completed_date = SYSDATE,
        completion_note = p_completion_note
    WHERE instance_id = p_instance_id
    AND task_name = p_task_code;

    COMMIT;
  END process_exit_checklist;

  PROCEDURE execute_termination(
    p_instance_id     NUMBER,
    p_completed_by    NUMBER
  ) IS
    l_person_id         NUMBER;
    l_termination_date  DATE;
    l_all_clear         VARCHAR2(1) := 'Y';
  BEGIN
    -- Verify all exit checklist tasks are complete
    FOR rec IN (
      SELECT task_id, task_name
      FROM   xx_hrms_lifecycle_tasks
      WHERE  instance_id = p_instance_id
      AND    status != 'COMPLETED'
    ) LOOP
      l_all_clear := 'N';
      DBMS_OUTPUT.PUT_LINE('Task not completed: ' || rec.task_name);
    END LOOP;

    IF l_all_clear = 'N' THEN
      RAISE_APPLICATION_ERROR(-20002, 'Cannot terminate — exit checklist incomplete');
    END IF;

    -- Get person and date
    SELECT person_id, event_date
    INTO   l_person_id, l_termination_date
    FROM   xx_hrms_lifecycle_instances
    WHERE  instance_id = p_instance_id;

    -- Perform the actual termination in EBS HRMS
    hr_assignment_api.terminate_assignment(
      p_person_id        => l_person_id,
      p_effective_date   => l_termination_date,
      p_termination_type => 'VOLUNTARY',
      p_reason_code      => (
        SELECT termination_reason
        FROM   xx_hrms_termination_details
        WHERE  instance_id = p_instance_id
      )
    );

    -- Update lifecycle status
    UPDATE xx_hrms_lifecycle_instances
    SET status = 'COMPLETED',
        completed_by = p_completed_by,
        completed_date = SYSDATE
    WHERE instance_id = p_instance_id;

    -- Archive employee records for 7-year retention
    INSERT INTO xx_hrms_alumni_records (
      person_id, termination_date, archive_date,
      data_retention_until, archived_by
    ) VALUES (
      l_person_id, l_termination_date, SYSDATE,
      ADD_MONTHS(SYSDATE, 84),  -- 7 years
      p_completed_by
    );

    COMMIT;

    DBMS_OUTPUT.PUT_LINE('Termination executed for person: ' || l_person_id);
  END execute_termination;

END xx_hrms_termination_pkg;
/
```

### Step 6: Lifecycle Dashboard and Reporting

```sql
CREATE OR REPLACE VIEW xx_hrms_lifecycle_dashboard AS
SELECT
  li.instance_id,
  pap.employee_number,
  pap.full_name,
  pap.email_address,
  bg.name AS business_group,
  li.event_code,
  le.event_name,
  li.event_date,
  li.status,
  li.initiated_date,
  li.completed_date,
  CASE
    WHEN li.status = 'PENDING' THEN
      ROUND((SYSDATE - li.initiated_date) * 24, 1)
    WHEN li.status = 'IN_PROGRESS' OR li.status LIKE 'PENDING%' THEN
      ROUND((SYSDATE - li.initiated_date) * 24, 1)
    ELSE
      ROUND((li.completed_date - li.initiated_date) * 24, 1)
  END AS hours_in_status,
  (SELECT COUNT(*)
   FROM   xx_hrms_lifecycle_tasks t
   WHERE  t.instance_id = li.instance_id
   AND    t.status = 'COMPLETED') || '/' ||
  (SELECT COUNT(*)
   FROM   xx_hrms_lifecycle_tasks t
   WHERE  t.instance_id = li.instance_id) AS task_progress,
  (SELECT ROUND(AVG(CASE WHEN t.status = 'COMPLETED'
           THEN (t.completed_date - li.initiated_date) * 24
           ELSE NULL END), 1)
   FROM   xx_hrms_lifecycle_tasks t
   WHERE  t.instance_id = li.instance_id) AS avg_task_completion_hours,
  li.notes
FROM   xx_hrms_lifecycle_instances li,
       per_all_people_f pap,
       per_business_groups bg
WHERE  li.person_id = pap.person_id
AND    pap.business_group_id = bg.business_group_id(+)
AND    SYSDATE BETWEEN pap.effective_start_date AND pap.effective_end_date
AND    li.event_date > SYSDATE - 90;
```

---

## Best Practices

### Lifecycle Event Design
1. **Event-driven architecture**: Each lifecycle change should fire events that trigger workflows (onboarding, IT provisioning, payroll updates) rather than relying on batch processes
2. **Effective dating**: Always use EBS effective dating for lifecycle changes — never directly update current records without maintaining history
3. **Checklist automation**: Define mandatory vs. optional checklist items; mandatory items block lifecycle completion to ensure compliance
4. **SLA tracking**: Assign SLA hours per checklist item; generate alerts when SLAs are breached (e.g., IT provisioning > 24 hours)

### Compliance by Country
1. **Legislative rules table**: Maintain a configuration table for country-specific termination rules (notice periods, final pay calculations, statutory forms)
2. **Notice period calculation**: Automatically calculate notice periods based on tenure, role, and local labor law; prevent termination dates earlier than notice + last working day
3. **Document retention**: Archived employee records must be retained per local law (7 years US, 6 years UK, 10 years DE) with purge automation
4. **Data privacy**: Anonymize alumni records in reporting after retention period; never expose PII in lifecycle dashboards

### Integration Patterns
1. **AD de-provisioning**: Trigger AD account disable on termination_date — not on notification date; use a scheduled job that runs daily at 2am
2. **Payroll cutover**: Final pay must be calculated including accrued but unused vacation, severance, and any outstanding expenses
3. **Benefits termination**: COBRA notification must be sent within 14 days of termination (US); automate via workflow notification
4. **Asset recovery**: IT asset checklist must be completed before final pay can be released

### Common Pitfalls
1. **Direct UPDATE on HR tables**: Never UPDATE per_all_people_f directly — always use HR APIs for effective-dated changes
2. **Missing exit interviews**: 30% of wrongful termination claims succeed due to lack of documentation — make exit interviews mandatory
3. **Delayed offboarding**: 40% of data breaches involve former employees — automate access revocation on termination_date
4. **Incomplete audit trail**: Every lifecycle event must capture who, what, when, and why — use xla_ae_headers-style audit for HR changes

## Key Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| Time-to-hire | < 30 days | Offer accept to first working day |
| Onboarding completion | < 5 business days | Checklist completion time |
| Promotion cycle time | < 14 days | Initiation to effective date |
| Transfer cycle time | < 10 days | Initiation to effective date |
| Exit checklist completion | < 3 days after LWD | Task completion rate |
| Final pay accuracy | 100% | Error rate in final pay |
| Compliance (Germany) | Works Council notification > 14 days before termination | Notice period compliance |
| Employee data accuracy | > 95% | Audit-driven data quality score |
