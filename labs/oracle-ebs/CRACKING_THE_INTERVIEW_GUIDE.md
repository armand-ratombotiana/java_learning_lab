# Oracle E-Business Suite — Cracking the Interview Guide

<div align="center">

![Oracle EBS](https://img.shields.io/badge/Oracle_EBS_Interview-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![R12.2](https://img.shields.io/badge/R12.2-005C5C?style=for-the-badge)
![Certification](https://img.shields.io/badge/Certification-OCP-004d99?style=for-the-badge)

**Comprehensive interview preparation for Oracle E-Business Suite (EBS) roles**

</div>

---

## Table of Contents

1. [Oracle Interview Process for EBS Roles](#oracle-interview-process-for-ebs-roles)
2. [EBS Certification (R12.2)](#ebs-certification-r122)
3. [Financials Interview Questions (AP, AR, GL)](#financials-interview-questions-ap-ar-gl)
4. [Supply Chain Interview Questions](#supply-chain-interview-questions)
5. [Technical Interview Questions (Forms, Reports, Workflow)](#technical-interview-questions-forms-reports-workflow)
6. [Upgrade/Migration Scenarios](#upgrademigration-scenarios)
7. [30/60 Day Study Plan](#3060-day-study-plan)
8. [Resources](#resources)

---

## Oracle Interview Process for EBS Roles

### Typical 4-5 Round Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| 1 | Recruiter Screen | 30 min | Background, EBS experience, availability, salary expectations |
| 2 | Technical Screen (Hiring Manager) | 45-60 min | EBS architecture, functional modules, SQL proficiency |
| 3 | Technical Deep Dive | 60 min | ADOP, patching, upgrades, customization, Forms/OAF |
| 4 | System Design / Architecture | 60 min | Design an EBS solution for a complex business requirement |
| 5 | Manager/Behavioral | 45 min | Project experience, team leadership, conflict resolution |

### What Oracle Looks For by Role

**Functional Consultant:**
- Deep knowledge of EBS modules (Financials, SCM, HRMS)
- Understanding of business processes (O2C, P2P, Record-to-Report)
- Configuration expertise (flexfields, profiles, concurrent programs)
- Functional flows and setups
- Integration across modules

**Technical Consultant:**
- Oracle Forms and Reports development
- OAF (Oracle Application Framework)
- Workflow Builder and AME
- SQL, PL/SQL performance tuning
- Concurrent program development
- XML Publisher/BIP reports

**EBS DBA:**
- ADOP patching cycle expertise
- Cloning and backup/recovery
- Performance tuning (database + apps tier)
- Upgrade planning and execution
- Multi-node architecture
- EBR (Edition-Based Redefinition)

**Solution Architect:**
- Deep cross-module knowledge
- Integration patterns (EBS ↔ third-party)
- Upgrade/migration strategy
- Solution design documentation
- System landscape design

---

## EBS Certification (R12.2)

### Available Certifications

| Certification | Exam Code | Level | Focus |
|--------------|-----------|-------|-------|
| Oracle EBS R12.2 Install/Patch/Upgrade | 1Z0-544 | Professional | System administration, patching, upgrade |
| Oracle EBS R12: Inventory and Order Management | 1Z0-243 | Professional | Supply chain, order management |
| Oracle EBS R12: Financials | 1Z0-221 | Professional | GL, AP, AR, Cash Management |
| Oracle EBS R12: HRMS | 1Z0-222 | Professional | HR, Payroll, Self-service |
| Oracle EBS R12: Manufacturing | 1Z0-242 | Professional | Manufacturing, WIP, BOM |
| Oracle EBS R12: Supply Chain Management | 1Z0-244 | Professional | SCM, Purchasing, Inventory |

### Certification Path Recommendation

```
1. Start with R12.2 Install/Patch/Upgrade (1Z0-544)
   └── Foundation for all EBS technical roles
2. Choose Functional Track:
   ├── Financials (1Z0-221) — Most common
   ├── SCM (1Z0-244)
   ├── Manufacturing (1Z0-242)
   └── HRMS (1Z0-222)
3. Advanced: Multiple certifications for architect roles
```

### Key Exam Topics for 1Z0-544

| Domain | Weight | Key Topics |
|--------|--------|------------|
| Architecture & Installation | 20% | Multi-tier architecture, Rapid Install, cloning |
| Online Patching (ADOP) | 25% | ADOP cycle, cutover, edition-based redefinition |
| System Administration | 20% | Concurrent manager, profile options, printers, auditing |
| Maintenance & Patching | 20% | AD patching, OPatch, bundled patches, patch analysis |
| Upgrade & Migration | 15% | R12.1 to R12.2 upgrade, data migration, testing |

---

## Financials Interview Questions (AP, AR, GL)

### General Ledger (GL)

**Basic Questions:**
1. Explain the Record-to-Report (R2R) cycle in EBS.
2. What is a set of books (SOB) and how is it structured in R12?
3. Explain the difference between balancing segment, cost center segment, and management segment.
4. What is cross-validation rules in GL? How do you set them up?
5. Explain the GL journal posting process: journal entry → journal import → posting → reporting.

**Advanced Questions:**
1. How does GL currency translation work? Explain the translation process with different conversion types (Corporate, Average, Spot).
2. Explain GL consolidation: how do you consolidate multiple sets of books?
3. What are the new GL features in R12.2? (Advanced Global Intercompany System, Subledger Accounting)
4. How does SLA (Subledger Accounting) work in R12? How is it different from R11i?
5. Design a solution for multi-currency, multi-GAAP reporting in EBS.

### Accounts Payable (AP)

**Basic Questions:**
1. Explain the Procure-to-Pay (P2P) cycle in EBS.
2. What are the different types of invoices in AP? (Standard, Prepayment, Debit Memo, Credit Memo, Mixed)
3. Explain the 3-way matching process (PO ↔ Receipt ↔ Invoice).
4. How do you handle holds on invoices? What are common hold types?
5. What is the difference between a payment batch and a single payment?

**Advanced Questions:**
1. How does AP handle withholding tax? Explain the setup.
2. Explain the AP terms and discount processing logic.
3. How does AP accrual reversal work at period-end?
4. Design an AP invoice approval workflow using Oracle Workflow.
5. How would you troubleshoot a payment batch that is failing to create payments?

### Accounts Receivable (AR)

**Basic Questions:**
1. Explain the Order-to-Cash (O2C) cycle in EBS.
2. What are transaction types in AR? (Invoice, Debit Memo, Credit Memo, Chargeback)
3. Explain the difference between Standard, Repeating, Guaranteed, and Invoice-Less Receipts.
4. How does AR aging work? How do you set up aging buckets?
5. Explain the AutoInvoice process and its key tables.

**Advanced Questions:**
1. How does AR handle multi-currency receipts and receipt write-offs?
2. Explain AR dunning letters and collection workflow.
3. How do you handle third-party payments and cross-currency receipts?
4. Design a solution for electronic invoicing compliance (e.g., Mexico CFDI, Brazil NF-e).
5. How does Revenue Management integration work in R12?

---

## Supply Chain Interview Questions

### Order Management (OM)

**Basic Questions:**
1. Explain the Order-to-Cash (O2C) flow in detail.
2. What is ATP (Available to Promise)? How does it work?
3. Explain order types, transaction types, and workflow in OM.
4. What are sales orders, and how do they flow through Release Rules?
5. Explain the difference between Pick Release and Ship Confirm.

**Advanced Questions:**
1. How does OM handle back-to-back orders (configured to order)?
2. Explain drop-shipment and its setup in OM.
3. Design a solution for complex pricing rules using Oracle Pricing Engine.
4. How do you handle order hold resolution workflow?
5. Explain OM integration with Advanced Supply Chain Planning (ASCP).

### Purchasing (PO)

**Basic Questions:**
1. Explain the Procure-to-Pay (P2P) process in detail.
2. What are the different types of purchase orders? (Standard, Planned, Blanket, Contract)
3. Explain requisition approval workflow.
4. What is the difference between RFQ and quotation?
5. Explain receiving process: receipt, inspection, put-away.

**Advanced Questions:**
1. How does ASL (Approved Supplier List) work in R12?
2. Explain consignment purchasing and its setup.
3. Design a procurement solution for a multinational corporation with centralized procurement.
4. How does sourcing rules work with Global Order Promising (GOP)?
5. Explain the relationship between PO, Receiving, and AP.

### Inventory (INV)

**Basic Questions:**
1. What are the different types of inventory? (On-hand, Reserved, In-transit, Intransit)
2. Explain cycle counting vs physical inventory.
3. What are item categories, category sets, and cross-reference?
4. Explain subinventory and locator control.
5. What is ATP in inventory context?

**Advanced Questions:**
1. How does MSCA (Material Stock Check Application) work?
2. Explain min-max planning, reorder point planning, and Kanban replenishment.
3. Design an inventory management solution for a global warehouse network.
4. How does lot control, serial control, and revision control work?
5. Explain the relationship between Inventory, WMS, and Shipping.

---

## Technical Interview Questions (Forms, Reports, Workflow)

### Oracle Forms

**Basic Questions:**
1. Explain Oracle Forms architecture: Forms Runtime, Forms Builder, Forms Listener.
2. What are the different trigger levels in Oracle Forms? (Block-level, Record-level, Item-level)
3. Explain WHEN-VALIDATE-ITEM, WHEN-VALIDATE-RECORD, and WHEN-BUTTON-PRESSED triggers.
4. How do you handle master-detail relationships in Forms?
5. What is the difference between PRE-QUERY and POST-QUERY triggers?

**Advanced Questions:**
1. How does Forms integration with EBS work? Explain the EBS Forms standards.
2. How do you create a custom Form that integrates with EBS menus and responsibilities?
3. Explain how Forms utilizes Oracle OA Framework libraries.
4. How do you debug a Forms session? (Forms Runtime Debug, trace files)
5. How does Forms personalization work in R12?

### Oracle Reports (BI Publisher / XML Publisher)

**Basic Questions:**
1. What is the difference between Oracle Reports and BI Publisher (XML Publisher)?
2. Explain the report development process: data model, layout, triggers.
3. What are the different layout types in BI Publisher? (RTF, PDF, Excel, EText)
4. Explain BI Publisher burst functionality.
5. How do you handle multi-currency and multi-language reports?

**Advanced Questions:**
1. Design a BI Publisher report that handles complex financial statements (P&L, Balance Sheet).
2. How does BI Publisher integrate with EBS concurrent programs?
3. Explain the BI Publisher bursting engine and its configuration.
4. How would you migrate reports from Oracle Reports 6i to BI Publisher?
5. Compare BI Publisher with OBIEE for EBS reporting.

### Oracle Workflow

**Basic Questions:**
1. Explain Oracle Workflow architecture: Items, Processes, Activities, Notifications.
2. What are the different types of activities in Workflow Builder? (Function, Notification, Wait, Block)
3. Explain the Workflow Engine and its background process.
4. How does workflow deferral and timeout work?
5. How do you monitor and troubleshoot workflows?

**Advanced Questions:**
1. Design a custom approval workflow for purchase orders with dynamic routing based on amount, category, and geography.
2. How does Oracle Workflow integrate with Business Event System (BES) and AME?
3. Explain how to implement complex routing rules (FYI, FYA, sequential, parallel, voting).
4. How do you handle workflow background engine failures and stuck workflows?
5. Explain the Workflow Directory Service (WDS) and its configuration.

### OAF (Oracle Application Framework)

**Basic Questions:**
1. Explain OAF architecture: BC4J (Business Components for Java), Model-View-Controller.
2. What are the key OAF components? (VO, EO, AM, Page, Region)
3. Explain Entity Object vs View Object.
4. How do you create a simple OAF page?
5. What is the difference between OA Framework and Oracle Forms?

**Advanced Questions:**
1. Design a custom OAF page that extends an existing EBS page using personalization.
2. How does OAF handle security and responsibilities?
3. Explain OAF globalization (NLS) and Flexfield integration.
4. How do you handle OAF performance tuning? (VO caching, query optimization)
5. Explain the OAF page lifecycle: process request, process form data, validate, process request again.

---

## Upgrade/Migration Scenarios

### R12.1 to R12.2 Upgrade

**Pre-Upgrade Checklist:**
| Task | Details |
|------|---------|
| System health check | Validate database, apps tier, concurrent managers |
| Backup strategy | Full backup before any upgrade steps |
| Test plan | Create comprehensive test scenarios |
| Regression testing | All customizations must be tested |
| Cutover plan | Define downtime window, rollback strategy |
| Training | Team training on ADOP, EBR |

**Key Upgrade Steps:**
1. **Technology Stack Upgrade:** Database 19c, WebLogic 12c, Java 8
2. **R12.2 Installation:** Run Rapid Install for new tech stack
3. **ADOP Preparation:** Initialize ADOP, enable edition-based redefinition
4. **ADOP Cutover:** Final sync between old and new edition
5. **Post-Upgrade Testing:** Functional + performance testing

**Common Upgrade Challenges:**
| Challenge | Solution |
|-----------|----------|
| Forms customization compatibility | Recompile Forms with R12.2 frmcmp |
| OAF page incompatibility | Test and update BC4J components |
| ADOP cutover failures | Check edition conflicts, review logs |
| Performance degradation | Re-gather statistics, tune new parameters |
| Custom schema objects | Validate objects in new edition |

### Cloud Migration Scenarios

**EBS to OCI:**
| Migration Type | Approach | Downtime |
|----------------|----------|----------|
| Lift and Shift | Move EBS from on-prem to OCI without changes | Minimal |
| Rehost | Reinstall EBS on OCI, migrate data | Moderate |
| Rearchitect | Refactor for cloud-native (limited EBS changes) | High |
| Replace | Migrate to Oracle Cloud SaaS (Fusion) | High effort |

**EBS to Cloud SaaS (Oracle Fusion):**
- Data migration: EBS to Fusion using Oracle Integration Cloud (OIC)
- Functional gap analysis: identify EBS features not in Fusion
- Coexistence: run EBS + Fusion during transition
- Cutover: phased module-by-module migration

---

## 30/60 Day Study Plan

### 30-Day Intensive Plan

| Week | Focus | Daily Time | Activities |
|------|-------|------------|------------|
| 1 | EBS Architecture + ADOP | 3 hrs | Multi-tier architecture, EBR, ADOP cycle |
| 2 | Financials Deep Dive | 3 hrs | GL, AP, AR, SLA, month-end close |
| 3 | Technical Skills | 3 hrs | Forms, Reports, OAF, Workflow, SQL |
| 4 | Upgrade + Interview Prep | 4 hrs | R12.2 upgrade scenarios, mock interviews |

**Daily Schedule:**
- **Hour 1:** Architecture or functional module study
- **Hour 2:** Technical practice (Forms/OAF/SQL)
- **Hour 3:** Interview questions review
- **Weekend:** Mock interview + upgrade scenario walkthrough

### 60-Day Balanced Plan

| Weeks | Focus | Deliverable |
|-------|-------|-------------|
| 1-2 | EBS Foundation | Architecture, ADOP, installation, cloning |
| 3-4 | Financials Deep Dive | GL cycle, AP/AR processes, SLA, period close |
| 5-6 | Supply Chain + Technical | O2C, P2P, Forms development, Reports, Workflow |
| 7-8 | Upgrade + Interview Prep | R12.2 upgrade plan, mock interviews, certification prep |

### Key Resources for Study

| Topic | Resource |
|-------|----------|
| Architecture | Oracle EBS R12.2 Concepts Guide (Doc ID 1578704.1) |
| ADOP | Oracle EBS Patching Procedures (Doc ID 1585985.1) |
| Financials | Oracle Financials Implementation Guide |
| Supply Chain | Oracle SCM Implementation Guide |
| Forms | Oracle Forms Developer's Guide for EBS |
| Reports | BI Publisher Report Developer's Guide |
| Workflow | Oracle Workflow Developer's Guide |
| Upgrade | Oracle EBS Upgrade Guide: R12.1 to R12.2 |

---

## Resources

### My Oracle Support (MOS) Key Notes
| Doc ID | Title |
|--------|-------|
| 1523648.1 | Oracle EBS R12.2 Upgrade Guide |
| 1578704.1 | ADOP Best Practices |
| 1315862.1 | EBS 12.2 Patching and Maintenance |
| 1594274.1 | EBS R12.2 Installation Checklist |
| 1077858.1 | EBS R12.2 File System Structure |
| 1346329.1 | EBS Cloning Best Practices |
| 396100.1 | EBS Performance Tuning Guide |

### Recommended Books
| Title | Author | Focus |
|-------|--------|-------|
| Oracle EBS R12.2 Installation and Administration | Syed Zaheer | Installation, architecture |
| Oracle EBS Financials R12 | Anil K. Jain | Financial modules |
| Oracle EBS R12.x Supply Chain | Brett Scoggins | SCM modules |
| Oracle EBS R12.2 Upgrade Guide | Oracle Press | Upgrade scenarios |

### Training Platforms
| Platform | Type | Cost |
|----------|------|------|
| Oracle University | Instructor-led, self-paced | $200-$3,000 |
| My Oracle Support | Documentation, whitepapers | Subscription |
| Oracle Learning Explorer | Free courses | Free |
| Udemy | EBS courses | $10-$200 |

### Interview Preparation Resources
- Glassdoor: Search "Oracle EBS Consultant" interview reviews
- LinkedIn: Connect with EBS professionals for informational interviews
- Oracle Forums: Engage in EBS technical discussions
- Stack Overflow: oracle-ebs tag for technical Q&A

---

<div align="center">

**"EBS interviews test depth — know one module deeply and understand how all modules connect."**

---

[Back to Top](#oracle-e-business-suite--cracking-the-interview-guide)

</div>
