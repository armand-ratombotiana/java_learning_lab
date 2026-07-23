# Company Behavioral Guide — Oracle EBS

<div align="center">

![Oracle EBS](https://img.shields.io/badge/Oracle_EBS_Behavioral-F80000?style=for-the-badge)
![STAR](https://img.shields.io/badge/STAR_Method-005C5C?style=for-the-badge)

**Behavioral interview prep framed around EBS projects — per company, per role**

</div>

---

## Table of Contents

1. [Oracle (Product Company)](#oracle)
2. [Deloitte / Accenture (Consulting)](#deloitte--accenture)
3. [PwC / EY / KPMG (Audit & Advisory)](#pwc--ey--kpmg)
4. [Infosys / TCS / Wipro (Services)](#infosys--tcs--wipro)
5. [Amazon / Microsoft / Google (Cloud / Tech)](#amazon--microsoft--google)
6. [Consulting vs Product Company Behavioral](#consulting-vs-product-company-behavioral)
7. [Functional vs Technical Role Framing](#functional-vs-technical-role-framing)
8. [Questions to Ask as an EBS Professional](#questions-to-ask-as-an-ebs-professional)

---

## Oracle

### EBS-Specific Behavioral Questions

**Q1: "Tell me about a complex EBS implementation you led."**

> **STAR Frame:** S = "Led GL+AP+AR rollout for a multinational manufacturing client with 12 legal entities across 4 currencies." T = "Configure multi-org MOAC, define 50+ flexfield structures, design SLA accounting rules." A = "Mapped as-is processes, ran CRP sessions, configured 3 COA segments, set up 4 ledgers with MRC, built custom AP-AR netting program." R = "Go-live in 9 months, 99.97% GL balance accuracy in first quarter close."

**Q2: "How did you handle a difficult EBS upgrade?"**

> **STAR Frame:** S = "Upgrading 12.1.3 to 12.2.4 for a 500-user financial services client." T = "17 custom objects, 3 custom schemas, 48-hour maintenance window." A = "Pre-upgrade patch analysis identified 5 breaking customizations. Ran ADOP prepare→apply→finalize in test 4 times. Created rollback script for each customization. Used EBR editioning to validate without affecting RUN edition." R = "Upgrade completed in 41 hours, zero data loss, all customizations validated in 2 weeks."

**Q3: "Describe a time you improved EBS performance."**

> **STAR Frame:** S = "AP month-end close running 6+ hours, missing SLA deadlines." T = "Identify bottleneck in GL_TRANSFER_POSTING concurrent program." A = "Traced to full table scan on GL_BALANCES (85M rows). Added partition by period_name, created covering index on (LEDGER_ID, PERIOD_NAME, ACTUAL_FLAG, CURRENCY_CODE). Tuned SQL to use partition pruning." R = "GL close reduced from 6h to 45 min, maintained across 4 month-end closes."

---

## Deloitte / Accenture

### EBS-Specific Behavioral Questions

**Q1: "Tell me about a difficult client situation on an EBS project."**

> **STAR Frame:** S = "Client insisted on customizing standard PO approval workflow against our recommendation." T = "Customization would break in the next upgrade; needed to convince client while maintaining relationship." A = "Built a POC showing the custom workflow breaking under ADOP finalize. Presented cost analysis: $40K custom vs $5K configuration + minor personalization. Demonstrated the standard workflow meeting 95% of requirements." R = "Client accepted standard workflow, saved 3 months of custom dev, upgrade completed with zero issues 18 months later."

**Q2: "How have you managed competing priorities on an EBS project?"**

> **STAR Frame:** S = "Consulting engagement with AP, AR, and GL workstreams running in parallel, all needing integration testing in the same week." T = "Coordinate 3 functional consultants, 2 technical developers, 1 DBA with limited CRP environment." A = "Created RACI matrix, scheduled staggered CRP windows (AP Mon-Tue, AR Wed-Thu, GL Fri with overlap buffer). Used shared defect log with priority scoring. Escalated resource constraint to partner." R = "All 3 workstreams passed CRP within 2 weeks, zero defects in UAT."

**Q3: "Describe a time you identified a risk others missed."**

> **STAR Frame:** S = "During PR (procurement) module design phase, team assumed standard PO matching would work." T = "Client required 3-way matching on partial shipments — standard EBS 2-way match insufficient." A = "Raised in design workshop, researched Oracle Support Doc 1123584.1 on 3-way matching. Designed custom matching program using RCV_TRANSACTIONS + PO_DISTRIBUTIONS_ALL." R = "Custom solution accepted by client, prevented $120K in potential invoice discrepancy write-offs."

---

## PwC / EY / KPMG

### EBS-Specific Behavioral Questions

**Q1: "How have you ensured SOX compliance in an EBS environment?"**

> **STAR Frame:** S = "Client undergoing SOX audit — 50 conflicting responsibilities identified in EBS." T = "Implement segregation of duties (SoD) — no user should have both AP_INVOICE_ENTRY and AP_PAYMENT_PROCESS." A = "Used FND_USER_RESP_GROUPS_DIRECT to find violations. Created 4 new responsibility groups following least-privilege model. Configured audit trail on 12 sensitive tables (AP_INVOICES_ALL, AP_CHECKS_ALL). Ran 2-week concurrent audit." R = "Clean audit opinion, SoD violations eliminated, audit trail covering all financial tables."

**Q2: "Tell me about an audit finding you discovered in EBS."**

> **STAR Frame:** S = "Reviewing EBS security configuration for a public company client." T = "Identify unauthorized access or data exposure." A = "Audited FND_USER end_date against termination list — found 23 terminated employees still active. Checked FND_USER_RESP_GROUPS_DIRECT for inappropriate responsibility assignments — found 5 users with 'Super User' responsibility who only needed AP entry." R = "30 deactivated users, 8 responsibility changes, reduced audit risk rating from High to Low."

**Q3: "How do you approach data integrity validation in EBS?"**

> **STAR Frame:** S = "Client GL trial balance did not match subledger (AP/AR) totals by $2.3M." T = "Identify source of discrepancy and reconcile." A = "Ran SLA reconciliation report: XLA_DISTRIBUTION_LINKS vs GL_JE_LINES. Found 142 orphaned SLA entries. Traced to custom invoice import program not calling SLA accounting engine. Fixed by adding GL_IMPORT_REFERENCES sync step." R = "Discrepancy resolved, automated reconciliation check added to month-end close checklist."

---

## Infosys / TCS / Wipro

### EBS-Specific Behavioral Questions

**Q1: "Describe a critical production issue you resolved in EBS."**

> **STAR Frame:** S = "Sunday 2 AM — client AP invoice import failing with ORA-00060 deadlock." T = "Resolve before Monday business open (6 AM)." A = "Traced deadlock to AP_INVOICES_INTERFACE — two concurrent Invoice Import programs conflicting on AP_INVOICES_ALL sequence. Identified parameter INVOICE_IMPORT_METHOD=BATCH causing parallel contention. Altered to serial processing for the import, investigated root cause in test." R = "Issue resolved by 5:30 AM, all 12,500 invoices posted. Permanent fix deployed in next release: single concurrent program with internal parallelization."

**Q2: "How have you managed knowledge transfer in a support engagement?"**

> **STAR Frame:** S = "Taking over EBS support for a client with 6 months of undocumented customizations." T = "Build knowledge base quickly for team of 5 support engineers." A = "Created discovery: catalogued all custom objects (23 custom tables, 12 concurrent programs, 8 OAF pages, 5 Form personalizations). Documented in FND tables: FND_DOCUMENTATION, custom wiki with troubleshooting steps for top 10 incidents. Led 2-week shadowing with outgoing team." R = "Zero SLA breaches in transition month, average incident resolution time reduced from 8h to 3.5h within 60 days."

**Q3: "Tell me about a time you had to work with limited resources."**

> **STAR Frame:** S = "Client needed EBS upgrade validated but only had test environment available 4 hours/day." T = "Complete regression testing within 24 hours of test environment availability." A = "Prioritized test cases by risk (critical = financial, high = config, medium = reports). Automated 40 smoke tests using OATS (Oracle App Testing Suite). Ran critical path scripts first, parallelized test execution across 3 team members." R = "All 200 critical test cases executed in 18 hours — found 3 regressions fixed before production."

---

## Amazon / Microsoft / Google

### EBS-Specific Behavioral Questions

**Q1 (Amazon): "Tell me about a time you delivered a large migration."**

> **STAR Frame:** S = "Migrate EBS R12.2 from on-premise to AWS with less than 4 hours downtime." T = "Minimize business interruption for 24/7 global operations." A = "Architected: EC2 for app tier (c5.4xlarge × 4), RDS Custom for DB, EBS gp3 for storage. Used AWS DMS for initial sync, Data Guard for final cutover. Pre-staged AMIs, automated ADOP cutover with CloudFormation. Ran 3 dry runs." R = "Go-live completed in 3h 22min, under budget by 12%. Six-month post-migration review: 99.99% uptime, 30% cost reduction vs on-prem."

**Q2 (Amazon): "How did you dive deep into a complex EBS performance issue?"**

> **STAR Frame:** S = "Client reporting OM order import slowing down by 5x after R12.2 upgrade." T = "Identify root cause and restore performance." A = "Analyzed AWR report: excessive 'read by other session' wait on OE_ORDER_HEADERS_ALL. Discovered ADOP editioning views not using edition-based statistics. Applied optimizer statistics to both RUN and PATCH editions. Also reorganized PCTFREE on the table from 10 to 5." R = "Order import speed restored to baseline + 15% improvement, 2M orders/day processing capacity."

**Q3 (Microsoft): "Describe how you designed a solution for business continuity."**

> **STAR Frame:** S = "Client needed DR for EBS with < 1 hour RPO and < 4 hour RTO." T = "Design cost-effective HA/DR architecture for 2,500 users." A = "Active Data Guard for DB (SYNC mode within region, ASYNC to DR). Active-Passive app tier with OHS load balancer. Automated failover playbook with 47 steps, tested quarterly. Used Azure Site Recovery for application tier VMs." R = "DR tested within RTO (3h 12m) and RPO (0 min data loss) in 3 quarterly tests. Annual cost 18% of full active-active."

---

## Consulting vs Product Company Behavioral

| Dimension | Product Company (Oracle) | Consulting (Deloitte/Accenture) |
|-----------|------------------------|--------------------------------|
| **Focus** | Depth of product knowledge | Breadth of implementation skills |
| **Answer Style** | "I built the module. I know every table." | "I solved the client's business problem." |
| **Key Verbs** | Designed, developed, optimized, architected | Facilitated, recommended, influenced, delivered |
| **Metrics** | Performance improvement, code quality, innovation | Client satisfaction, on-time delivery, scope management |
| **Failure Story** | Technical: misestimated effort, wrong design | Client-facing: misaligned expectations, scope creep |

### Example: Same Question, Different Framing

**Q: "Tell me about a project that went wrong."**

**Product (Oracle) Frame:**
> "I underestimated the impact of customizing the AP payment process through OAF extension rather than Form personalization. The OAF page had 3x the development effort. I owned the mistake, refactored to use extension points, and added 2 hours to every future estimate for OAF customization complexity. Result: zero rework in the next 4 OAF pages."

**Consulting (Accenture) Frame:**
> "I recommended a fully custom AR aging report instead of leveraging Oracle Standard+ BI Publisher. The client agreed in the design phase but the project went over budget by $15K. I initiated a root cause analysis, switched to BI Publisher with a custom data template, documented lessons learned, and the fix was delivered within original budget after reprioritizing lower-value requirements."

---

## Functional vs Technical Role Framing

### Functional Consultant

| Behavioral Topic | How to Frame |
|-----------------|--------------|
| Complex Config | "Configured 3 COA segments, 12 value sets, 6 cross-validation rules for GL" |
| Business Process | "Mapped 45 P2P process steps — identified 8 gaps resolved through configuration" |
| User Training | "Trained 200 end-users across 4 countries with role-based training materials" |
| Requirements | "Led 15 requirements workshops, prioritized 120 requirements into MVP + post-go-live" |
| Acceptance | "Managed UAT for 6 weeks — 340 test cases, 92% first-pass yield" |

### Technical Consultant

| Behavioral Topic | How to Frame |
|-----------------|--------------|
| Custom Development | "Built 8 OAF pages, 12 custom concurrent programs, 2 workflow customizations" |
| Integration | "Designed SOA integration between EBS and Salesforce — 5,000 orders/day" |
| Performance | "Optimized 15 slow reports — average improvement from 12 min to 45 sec" |
| Migration | "Led FNDLOAD migration of 200+ objects across 6 environments" |
| Upgrade | "Supported ADOP cutover for 4 quarterly patching cycles" |

### Dual Role (Functional + Technical)

| Question | Sample Answer |
|----------|--------------|
| "Are you more functional or technical?" | "Both — I configure EBS setups and write the PL/SQL behind them. For example, I set up AP payment terms functionally and also built the custom payment batch program that enforces them." |
| "Why do you bridge both?" | "Because in EBS, understanding the business process is essential to writing correct code. I've caught 20+ logic errors in custom programs because I understood the functional impact." |

---

## Questions to Ask as an EBS Professional

### Technical / Architecture Questions

| Question | Why It Matters |
|----------|---------------|
| "What EBS version and tech stack are you currently on?" | Reveals upgrade maturity, technical debt |
| "Do you use ADOP online patching? What's your cutover strategy?" | Operations maturity, downtime tolerance |
| "How many custom objects do you have? Are they in a custom schema?" | Customization complexity, upgrade risk |
| "What's your concurrent manager configuration?" | Scale, batch processing maturity |
| "How many EBS environments do you run (Dev/Test/UAT/Prod)?" | SDLC maturity, cloning frequency |
| "Do you use EBR editioning? How do you manage code promotions?" | DevOps maturity in EBS context |

### Functional / Business Questions

| Question | Why It Matters |
|----------|---------------|
| "Which EBS modules are your core business processes?" | Understanding of their operations |
| "How long does your month-end close take?" | Financial process efficiency |
| "What's your biggest EBS pain point right now?" | Where you can add immediate value |
| "Are you planning an upgrade or cloud migration?" | Future roadmap, job stability |
| "How many concurrent users on the system?" | Scale and performance needs |
| "What third-party integrations do you have?" | Integration complexity |

### Culture / Team Questions

| Question | Why It Matters |
|----------|---------------|
| "How is the EBS team structured — functional vs technical split?" | Role clarity, growth path |
| "What % of time is project work vs. support?" | Day-to-day expectations |
| "How do you handle knowledge sharing among the team?" | Learning environment |
| "What training/certification does the company support?" | Career development support |

---

## Quick STAR Builder for EBS

| Situation | Task | Action | Result |
|-----------|------|--------|--------|
| AP close running 6+ hours | Reduce to < 2 hours | Analyzed AWR, added partitions, tuned SQL, optimized CM config | GL close in 45 min |
| Customization blocked upgrade | Minimize customization for upgrade | Built POC using standard config, presented cost analysis | Client chose standard, upgrade smooth |
| 23 terminated users still active | Audit and clean up security | Scripted FND_USER deactivation, ran audit report | Clean SOX audit |
| Upgrade deadline at risk | Deliver on time | Prioritized test cases, automated 40 tests, parallel execution | All 200 critical tests in 18h |
| No documentation for custom code | Build knowledge base | Catalogued all objects, documented top 10 incidents, wiki | Zero SLA breaches in transition |
