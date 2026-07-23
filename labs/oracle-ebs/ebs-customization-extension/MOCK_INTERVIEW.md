# Mock Interview: EBS Customization & Extension (ebs-customization-extension)

**Role:** Oracle EBS Technical Developer  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is the CEMLI approach in EBS customizations?

**Candidate:** CEMLI stands for the five categories of EBS customizations:
- **C — Configuration:** Setting up flexfields, profile options, concurrent programs, workflows using seeded functionality
- **E — Extension:** Adding new functionality using Forms, OA Framework, BI Publisher, or workflows
- **M — Modification:** (Deprecated in modern approach) Modifying base Oracle code
- **L — Localization:** Country-specific requirements (tax, legal reports, localization)
- **I — Integration:** Connecting EBS with external systems via APIs, PL/SQL interfaces, or SOA/OSB

Best practice: Prefer Configuration over Extension. Extend before modifying.

**Interviewer:** What are personalizations in Oracle EBS?

**Candidate:** Personalizations allow users to modify OA Framework page behavior without coding:
- Hide or show regions and items
- Make fields required, read-only, or updatable
- Change prompts, tooltips, and display properties
- Set default values
- Change LOV behavior
- Add or modify validation

Personalizations are stored in `JDR_PATHS` table and can be exported/imported between environments. Access controlled via Functional Administrator responsibility.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you extend OA Framework pages in EBS?

**Candidate:** OA Framework extensions are done through **OA Extension** (JDeveloper-based):
1. **Personalization (low-code):** Change UI properties without coding
2. **Controller Extension:** Create a custom Java class extending the standard controller
3. **View Object Extension:** Extend the view object to add new attributes or queries
4. **Application Module Extension:** Extend the business logic

Steps for controller extension:
1. Find the page's package name and controller class
2. Create a subclass in a custom Java package
3. Override methods (processRequest, processFormData, processFormRequest)
4. Register extension in OA Framework metadata
5. Deploy to `$OA_JAVA_TOP`

**Interviewer:** How do you develop custom Forms in EBS?

**Candidate:** Custom Forms development process:
1. Create form in Oracle Forms Builder (based on underlying EBS tables)
2. Attach EBS libraries: `APPFLDR`, `APPDAYPK`, `APPCORE`, `FNDSQF`
3. Implement EBS standards: menus, toolbars, calendar, flexfields
4. Register form using Application Object Library:
   - Form function: Define the form and parameters
   - Menu: Add form function to a menu
   - Responsibility: Assign menu to responsibility
5. Generate `.fmx` and deploy to server

---

## Round 3: Hard (15-25 minutes)

**Interviewer:** Design a custom module for expense report approval that integrates with EBS Payables. The module must handle mobile approvals, multi-level approval hierarchy, and integration with GL.

**Candidate:** 

**Design overview:**
```
Expense Report Module (Custom OA Framework Page)
        │
  ┌─────┴─────────────────────────────────┐
  │  Workflow: Expense Approval Process   │
  │  ├── Employee submits expense report  │
  │  ├── Manager approves (Level 1)      │
  │  ├── Director approves if > $5000    │
  │  └── Finance approves if > $25000    │
  └─────┬─────────────────────────────────┘
        │
  ┌─────┴─────────────────────────────────┐
  │  Integration Points                   │
  │  ├── AP Invoices (Payables)          │
  │  ├── GL Posting (General Ledger)     │
  │  ├── HR (Employee, Manager, Cost Ctr)│
  │  └── Email Notifications (Workflow)  │
  └───────────────────────────────────────┘
```

**Implementation:**
1. **Custom tables:** `XX_EXPENSE_REPORTS`, `XX_EXPENSE_LINES`, `XX_EXPENSE_AUDIT`
2. **OA Framework pages:** Expense Entry, Expense Approval, Expense Inquiry
3. **Workflow:** Custom workflow "XXEXPAPP" with approval hierarchy steps
4. **Mobile:** Enable OA Framework pages for mobile browsers or build APEX-based mobile interface
5. **Integration to Payables:**
   - After approval, call `AP_IMPORT_UTILITIES_PKG.IMPORT_INVOICE` to create AP invoice
   - Use descriptive flexfields to carry expense reference to AP
6. **GL integration:** Use `GL_INTERFACE` table for posting to General Ledger

**Security:** Custom roles: XX_EMP_EXPENSE_ENTRY, XX_MGR_EXPENSE_APPROVAL, XX_FIN_EXPENSE_RELEASE

**Interviewer:** How would you migrate these customizations between environments?

**Candidate:** Use **FNDLOAD** and **AD** utilities for migration:
```bash
# Download OA Framework component
FNDLOAD apps/apps_pass 0 Y DOWNLOAD $FND_TOP/patch/115/import/aflvmlu.txt \
  XEXP_RPT.ldt - UPLOAD_MODE REPLACE \
  - CUSTOMIZATIONS \
  - "TOP=XXCUSTOM" \
  - "PAGE=XXEXPENSEREPORT"

# Download workflow definition
WFLOAD apps/apps_pass 0 Y DOWNLOAD XXEXPAPP.wft XXEXPAPP

# Download concurrent program
FNDLOAD apps/apps_pass 0 Y DOWNLOAD $FND_TOP/patch/115/import/afcpprog.lct \
  XX_CONC_PROG.ldt PROGRAM APPLICATION_SHORT_NAME="XXCUSTOM" \
  CONCURRENT_PROGRAM_NAME="XX_EXPENSE_REPORT"
```

Use a version control system (Git) for all customizations. Automate migration with Ant or shell scripts. Apply CEMLI standards: each object named with custom prefix (XX_).

---

## Interviewer Feedback

**Strengths:** Deep EBS customization knowledge, practical OA Framework design, good understanding of CEMLI  
**Areas to Improve:** Could discuss ATG (AutoInstall) patching for custom applications  
**Verdict:** Hire

---

*EBS Customization MOCK_INTERVIEW.md — Part of Oracle EBS Academy Interview Preparation*
