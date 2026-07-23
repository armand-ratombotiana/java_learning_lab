# Mock Interview: EBS HRMS (ebs-hrms)

**Role:** Oracle EBS HRMS Functional Consultant  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What modules are part of Oracle EBS HRMS?

**Candidate:** Oracle EBS HRMS (Human Resources Management System) includes:
- **Oracle HR:** Core HR (person records, organization structures, positions)
- **Oracle Payroll:** Payroll processing, tax calculation, statutory reporting
- **Oracle Time & Labor (OTL):** Time capture, absence management, overtime
- **Oracle Work Structures:** Organization hierarchy, job families, grades
- **Oracle Talent Management:** Recruiting, performance management, succession planning
- **Oracle Learning Management (OLL):** Training administration, certifications
- **Oracle Absence Management:** Absence plans, entitlements, balances
- **Oracle Self-Service HR (SSHR):** Employee and manager self-service via web

**Interviewer:** Explain the relationship between Legal Employer, Business Group, Organization, and Position.

**Candidate:** 
- **Business Group:** Highest level in HRMS hierarchy, represents the enterprise. Contains all employees.
- **Legal Employer:** Legal entity with tax ID, responsible for employment contracts and payroll
- **Organization:** Internal department/division within the Business Group
- **Position:** Defined role within an organization (e.g., "Senior Java Developer, IT Department")
- **Job:** Generic classification (e.g., "Software Engineer") — can span multiple positions
- **Assignment:** The employee-organization-position relationship. An employee holds an assignment to a specific position in a specific organization.

The hierarchy: Business Group → Legal Employer → Organization → Department → Position → Assignment (Employee)

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does Oracle Payroll process a payroll run?

**Candidate:** Payroll processing flow:
1. **Pre-payroll verification:** Check element entry, ensure no missing information
2. **QuickPay / Payroll Run:**
   - Read employee assignments and element entries
   - Calculate earnings (salary, overtime, commission)
   - Calculate deductions (tax, insurance, pension)
   - Apply statutory rules (country-specific)
   - Generate payroll results
3. **RetroPay:** If retroactive changes exist, recalculate historical periods
4. **Costing:** Distribute payroll costs to GL accounts
5. **Payment processing:** Generate bank files, checks, or Payslips
6. **GL Transfer:** Post payroll costs to General Ledger
7. **Reporting:** Generate statutory reports, payslips, payroll registers

Configurable elements: User-defined earnings, deductions, and benefits. Calculate using formula (FastFormula) or database item.

**Interviewer:** How do you manage absence in EBS HRMS?

**Candidate:** Oracle Absence Management:
1. **Absence Plans:** Define plan types (vacation, sick, personal), entitlement formulas, and carryover rules
2. **Accrual formulas:** Calculate entitlement based on tenure, grade, or custom rules
3. **Employee absences:** Self-service entry via SSHR
4. **Absence balances:** Track accrued, taken, and remaining entitlement
5. **Integration with Payroll:** Automatically deduct absence from pay when plan is unpaid
6. **Absence reporting:** Absence analysis, compliance reporting (FMLA, ADA)

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a UK-specific payroll system that handles multiple pay frequencies (monthly, weekly), auto-enrollment pension (Workplace Pension), statutory payments (SSP, SMP), and year-end reporting (RTI).

**Candidate:** 

**System design:**

1. **Elements set up for UK payroll:**
```sql
-- Earnings
PAY_BASIC_SALARY    (Monthly, Weekly)  
PAY_OVERTIME        (Freely configurable)
PAY_COMMISSION      (Periodic)

-- Statutory Payments
SMP                 (Statutory Maternity Pay)
SSP                 (Statutory Sick Pay)
SPP                 (Shared Parental Pay)

-- Deductions
UK_INCOME_TAX       (HMRC tax codes)
UK_NATIONAL_INSURANCE (Employee and Employer NI)
PENSION_AE          (Auto-enrolment workplace pension)
```

2. **Tax calculation:** Use Oracle Payroll's HMRC-recognized FastFormula for:
   - Tax code validation (L, T, BR, D0, etc.)
   - Cumulative vs week1/month1 basis
   - NI category (A, B, C, J, M, Z, etc.)
   - Student loan deductions (Plan 1, Plan 2, Postgraduate)

3. **Auto-enrolment pension:** 
   - Qualifying earnings calculation (£6,240 - £50,270 threshold for 2024/25)
   - Minimum contributions: 3% employer, 5% employee (total 8%)
   - Opt-out processing and re-enrolment every 3 years
   - NEST (National Employment Savings Trust) interface via BACS file

4. **RTI (Real-Time Information):**
   - FPS (Full Payment Submission): Send to HMRC on or before each pay day
   - EPS (Employer Payment Summary): Claim statutory payments, report adjustments
   - Year-end final submission
   - Integration: Oracle Payroll generates RTI-ready XML files

5. **Payroll schedule:**
   - Weekly payroll: Every Friday
   - Monthly payroll: Last working day
   - Off-cycle: Unplanned payments on any day

6. **BACS payment file:**
   - Generate BACS Standard 18 (or new ISO 20022 format)
   - Include payment date, amount, sort code, account number
   - BACS submission via BACSTEL-IP or BACS Service User

**Interviewer:** How do you handle multiple assignments for a single employee?

**Candidate:** In EBS, an employee can have multiple assignments (e.g., primary job + secondary role):
1. **Primary assignment:** Main employment, payroll, cost allocation
2. **Secondary assignments:** Additional roles (e.g., Health & Safety Officer), may have different cost centers

Processing:
- Each assignment can have its own element entries and payroll distribution
- Payroll processes each assignment separately
- Consolidated pay: Multiple assignment payslips combined into one payment
- Assignment status tracked independently (active, terminated, suspension)

---

## Interviewer Feedback

**Strengths:** Strong HRMS module knowledge, detailed UK payroll design, practical statutory processing  
**Areas to Improve:** Could discuss Oracle HCM Cloud integration with EBS HRMS  
**Verdict:** Hire

---

*EBS HRMS MOCK_INTERVIEW.md — Part of Oracle EBS Academy Interview Preparation*
