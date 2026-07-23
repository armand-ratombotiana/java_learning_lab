# Oracle E-Business Suite — Company Interview Guide

<div align="center">

![Oracle](https://img.shields.io/badge/Oracle_EBS-Interviews-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![R12.2](https://img.shields.io/badge/R12.2-005C5C?style=for-the-badge)

**Oracle EBS interview prep — process, questions, and real experiences**

</div>

---

## Table of Contents

1. [Oracle-Specific Interview Process](#oracle-specific-interview-process)
2. [Google/Microsoft/Amazon Interview for EBS Roles](#googlemicrosoftamazon-interview-for-ebs-roles)
3. [Apple Interview for ERP/Database Roles](#apple-interview-for-erpdatabase-roles)
4. [Real Interview Experiences](#real-interview-experiences)

---

## Oracle-Specific Interview Process

### Typical 4-5 Round Process for EBS Roles

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| 1 | Recruiter Screen | 30 min | Background, EBS experience, availability |
| 2 | Technical Screen | 45-60 min | EBS architecture, functional modules, SQL |
| 3 | Technical Deep Dive | 60 min | ADOP, patching, upgrades, customization |
| 4 | System Design / Architecture | 60 min | Design an EBS solution for business requirement |
| 5 | Manager/Behavioral | 45 min | Project experience, leadership, conflict resolution |

### Common Technical Questions

**EBS Architecture:**
1. Explain the EBS R12.2 multi-tier architecture (Desktop, Apps, Database tiers).
2. How does Edition-Based Redefinition (EBR) work and why is it crucial for online patching?
3. Describe the concurrent manager architecture and how Internal Monitor Manager coordinates processing.
4. Explain Multi-Org Access Control (MOAC) Using Virtual Private Database (VPD).

**ADOP and Patching:**
1. Walk through the complete ADOP cycle: prepare, apply, finalize, cutover, cleanup.
2. How do you handle ADOP cutover conflicts?
3. What's the difference between ADOP and the older AD Patch utility?
4. How do you apply quarterly CPU patches using ADOP?

**R12.1 to R12.2 Upgrade:**
1. What are the prerequisites for upgrading from R12.1 to R12.2?
2. How has the file system structure changed from R12.1 to R12.2?
3. What technology stack changes are involved (Forms, OAF, Java)?

**Functional Modules:**
1. Explain the General Ledger cycle in EBS (Journal Entry → Post → Revalue → Consolidate → Close).
2. How does the Order to Cash (O2C) cycle work in EBS?
3. Explain the Procure to Pay (P2P) lifecycle.

**Security:**
1. How do you implement segregation of duties in EBS?
2. Explain EBS user management and role-based access control.
3. How does EBS handle auditing and compliance?

---

## Google/Microsoft/Amazon Interview for EBS Roles

### Google — Technical Program Manager (ERP/Oracle)

**Process:**
| Round | Focus |
|-------|-------|
| Recruiter | ERP background, system design experiences |
| Technical | EBS integration with cloud systems |
| System Design | Scaling Oracle workloads on Google Cloud |
| Program Management | Cross-functional team leadership |

**Sample Questions:**
1. "How would you migrate EBS to Google Cloud Compute Engine?"
2. "Design a backup and disaster recovery strategy for EBS on GCP."
3. "How do you handle the network latency challenges of running EBS database on Cloud SQL?"

### Microsoft — Dynamics 365 Migration Consultant

**Process:**
| Round | Focus |
|-------|-------|
| Recruiter | EBS experience, Dynamics 365 knowledge |
| Technical | Oracle to SQL Server migration, integration patterns |
| Architecture | Cloud migration strategy |
| Behavioral | Customer focus, growth mindset |

**Sample Questions:**
1. "How would you plan a migration from Oracle EBS to Dynamics 365?"
2. "What data migration strategy would you use for 10+ years of EBS financial data?"
3. "How do you handle integration between EBS and Azure Logic Apps?"

### Amazon — ERP Manager / Database Specialist

**Process:**
| Round | Focus |
|-------|-------|
| Recruiter | EBS management experience, AWS knowledge |
| Technical | EBS architecture, SQL, performance tuning |
| Leadership | Amazon Leadership Principles |
| Bar Raiser | Leadership + technical depth |

**Sample Questions:**
1. "Design a highly available EBS deployment on AWS."
2. "How would you cost-optimize an EBS environment running on EC2?"
3. "If EBS has a performance issue impacting month-end close, how do you triage?"

---

## Apple Interview for ERP/Database Roles

### Apple — Enterprise Systems Engineer

**Interview Process:**
| Round | Focus | Topics |
|-------|-------|--------|
| Recruiter | ERP background | EBS, Oracle Database |
| Technical Screen | EBS + Database | EBS architecture, SQL optimization |
| System Design | Enterprise systems | Global ERP deployment |
| Apple Values | Behavioral | Privacy, quality, attention to detail |

**Sample Questions:**
1. "Design a global EBS deployment that handles 50+ legal entities across 30 countries."
2. "How would you ensure data privacy compliance (GDPR, CCPA) in an EBS system?"
3. "Describe the optimal EBS database configuration for a production system with 5000+ concurrent users."

---

## Real Interview Experiences

### Oracle EBS Technical Lead (Glassdoor Aggregated)

**Experience #1 — Oracle EBS Architect (2023)**
- "5 round process: Technical phone (ADOP cycle, EBS architecture), System design (multi-org architecture), Functional (GL/AP/AR), Architecture review (design a customization), Behavioral"
- "System design: Design a new EBS module for expense management"
- "ADOP: Asked to walk through the complete cutover process and what to do if it fails"

**Experience #2 — Oracle EBS Functional Consultant (2024)**
- "3 rounds: 2 technical + 1 functional"
- "Focus on Order to Cash (O2C) and Procure to Pay (P2P) cycles"
- "SQL: Write queries to identify orders stuck in workflow"
- "Integration: How do you integrate EBS with a third-party logistics provider?"

**Experience #3 — Senior EBS DBA (2023)**
- "4 rounds: Deep ADOP knowledge, R12.2 upgrade experience, database performance, leadership"
- "R12.2 upgrade: Walk through the entire upgrade plan from R12.1.3 to R12.2.10+"
- "Performance: A concurrent request takes 6 hours instead of 1 — troubleshoot"
- "Cloning: Best practices for EBS cloning"

### Levels.fyi Insights
- **Average EBS Developer/Analyst Salary:** $95K-$130K
- **Senior EBS Functional Consultant:** $120K-$160K
- **EBS Technical Architect/Lead:** $140K-$185K
- **EBS DBA:** $110K-$155K
- **Top paying companies:** Oracle (highest), Deloitte, Accenture, PwC, KPMG

---

<div align="center">

**"EBS expertise is a niche but well-compensated skill — demonstrate deep knowledge of ADOP, upgrades, and functional cycles."**

</div>
