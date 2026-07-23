# NeetCode Roadmap — Oracle EBS Interview

<div align="center">

![Oracle EBS](https://img.shields.io/badge/Oracle_EBS_NeetCode-F80000?style=for-the-badge)
![Roadmap](https://img.shields.io/badge/Roadmap-Study_Plan-005C5C?style=for-the-badge)

**LeetCode SQL map + technical topics + 4-week study plan for EBS roles**

</div>

---

## Table of Contents

1. [LeetCode SQL Problems Relevant to EBS](#leetcode-sql-problems-relevant-to-ebs)
2. [Technical Topics Map](#technical-topics-map)
3. [4-Week Study Plan](#4-week-study-plan)
4. [Company-Specific Focus](#company-specific-focus)
5. [Additional Resources](#additional-resources)

---

## LeetCode SQL Problems Relevant to EBS

### Lab 01: EBS Architecture

| Problem | Topic | EBS Relevance |
|---------|-------|---------------|
| LC 175 | Combine Two Tables | Admin node + DB service joining |
| LC 177 | Nth Highest Salary | Ranking concurrent manager priority |
| LC 178 | Rank Scores | CM service ranking by load |
| LC 197 | Rising Temperature | Edition-based redefinition versioning |

### Lab 02: System Setup & Configuration

| Problem | Topic | EBS Relevance |
|---------|-------|---------------|
| LC 176 | Second Highest Salary | Flexfield value set hierarchy |
| LC 182 | Duplicate Emails | Profile option validation |
| LC 183 | Customers Who Never Order | Organizations without setup |
| LC 196 | Delete Duplicate Emails | Dedup flexfield values |
| LC 627 | Swap Salary | Profile option value swapping |

### Lab 03: Financials (GL, AP, AR)

| Problem | Topic | EBS Relevance |
|---------|-------|---------------|
| LC 569 | Median Employee Salary | GL median balance by period |
| LC 571 | Find Median Given Frequency | Financial median calculation |
| LC 579 | Find Cumulative Salary | GL running balance |
| LC 585 | Investments in 2016 | Insurance / asset investment entries |
| LC 601 | Human Traffic | High-volume period detection |
| LC 1174 | Immediate Food Delivery | Payment schedule aging |
| LC 1193 | Monthly Transactions | AP/AR monthly reconciliation |
| LC 1204 | Last Person to Board | Payment run cutoff logic |
| LC 1321 | Restaurant Growth | SLA cumulative balances |
| LC 1384 | Total Sales Amount by Year | GL annual rollforward |
| LC 1393 | Capital Gain/Loss | Fixed asset gain/loss |
| LC 1440 | Evaluate Boolean Expression | Intercompany eliminations |
| LC 1890 | Latest Login | Latest period close date |

### Lab 04: Supply Chain (INV, PO, OM)

| Problem | Topic | EBS Relevance |
|---------|-------|---------------|
| LC 534 | Game Play Analysis III | ATP cumulative available (SUM OVER) |
| LC 550 | Game Play Analysis IV | Order fulfillment rate |
| LC 1045 | Customers Who Bought All | Cross-selling item analysis |
| LC 1070 | Product Sales Analysis III | Item date-range sales |
| LC 1501 | Countries to Invest | Sourcing / country compliance |
| LC 1596 | Most Frequently Ordered | Fast-moving inventory |
| LC 1613 | Lost Customers | Lost customers AR aging |
| LC 1699 | Number of Calls | Inter-org transfers |
| LC 1741 | Total Time Spent | Shipping time tracking |
| LC 1789 | Primary Department | Default warehouse assignment |

### Lab 05: Manufacturing (WIP, MRP, BOM)

| Problem | Topic | EBS Relevance |
|---------|-------|---------------|
| LC 570 | Managers with 5 Reports | BOM top assemblies >5 components |
| LC 596 | Classes > 5 Students | Component count in assembly |
| LC 626 | Exchange Seats | WIP operation sequence reordering |
| LC 1270 | All People Report to Manager | BOM multi-level explosion (CTE) |
| LC 1322 | Ads Performance | Manufacturing yield / scrap rate |
| LC 1445 | Apples and Oranges | Finished vs WIP variance |
| LC 1468 | Calculate Salaries | Shop floor labor costing |

### Lab 06: HRMS

| Problem | Topic | EBS Relevance |
|---------|-------|---------------|
| LC 181 | Employees Earning More | Manager vs subordinate comp |
| LC 184 | Department Highest Salary | Dept-wise max element entry |
| LC 615 | Avg Salary by Department | Payroll run results by dept |
| LC 618 | Student Geo Report | Location-based HR reporting |
| LC 1141 | User Activity Past 30 Days | HR self-service activity tracking |
| LC 1142 | User Activity Past 30 Days II | HR compliance horizon analysis |
| LC 1978 | Left Employees | Terminated employees audit |

### Lab 07: Technical Architecture (OAF, BC4J)

| Problem | Topic | EBS Relevance |
|---------|-------|---------------|
| LC 232 | Queue with Stacks | OAF workflow queue pattern |
| LC 353 | Design Snake Game | Validation in Entity Object |
| LC 380 | Insert/Delete/Random | FND API design patterns |
| LC 460 | LFU Cache | Concurrent request caching |
| LC 146 | LRU Cache | MDS cache / metadata cache |
| LC 173 | BST Iterator | BC4J RowSet iterator pattern |

### Lab 08: Customization & Extensions (CEMLI)

| Problem | Topic | EBS Relevance |
|---------|-------|---------------|
| LC 208 | Trie (Prefix Tree) | Flexfield value hierarchy |
| LC 355 | Design Twitter | WF notification / approval chain |
| LC 362 | Design Hit Counter | AME approval rule frequencies |
| LC 631 | Design Excel Sum | Personalization formula engine |

### Lab 09: Security

| Problem | Topic | EBS Relevance |
|---------|-------|---------------|
| LC 262 | Trips and Users | Access audit trail analysis |
| LC 608 | Tree Node | Menu → Function → Form hierarchy |
| LC 1127 | User Purchase Platform | Login activity by responsibility |
| LC 1555 | Bank Account Summary | Financial security / audit view |
| LC 1939 | Active Request Users | Active session / request tracking |

### Lab 10: Upgrade & Migration

| Problem | Topic | EBS Relevance |
|---------|-------|---------------|
| LC 185 | Department Top 3 | Pre-upgrade compatibility ranking |
| LC 1972 | First/Last Call | Edition state change tracking |
| LC 2051 | Category of Each Category | Data migration categorization |

---

## Technical Topics Map

### SQL and PL/SQL (NeetCode SQL + Oracle-Specific)

| NeetCode Topic | EBS Extension | Study Resource |
|----------------|---------------|----------------|
| SELECT + WHERE | FND tables multi-org scope | NeetCode SQL 01 |
| JOINs | EBS _ALL + TL + FK chains | NeetCode SQL 04-05 |
| Aggregation (GROUP BY) | GL/AP period close reporting | NeetCode SQL 06 |
| Window Functions (RANK, DENSE_RANK) | Concurrent manager priority queue | NeetCode SQL 07 |
| CTE / Recursive | BOM explosion / MRP netting | NeetCode SQL 08 |
| Subqueries (correlated) | MOAC VPD / FND_MOBS pattern | NeetCode SQL 09 |
| Date/Time Functions | GL period range, effective dates | NeetCode SQL 10 |
| CASE / Pivot | Flexfield segment decode | NeetCode SQL 11 |
| Performance Tuning | Explain plan, index strategy | "SQL Tuning" by Dan Tow |

### Java / OAF (NeetCode Java + EBS)

| NeetCode Topic | EBS Pattern | Lab |
|----------------|-------------|-----|
| OOP (Inheritance) | OAF BC4J: EntityImpl, VOImpl subclass | Lab 07 |
| Design Patterns | Controller pattern, AM → VO delegation | Lab 07 |
| Collections | ViewRowSet iteration, ArrayList for bulk | Lab 07-08 |
| Exception Handling | OAException, JtR12Process error handling | Lab 07-08 |
| File I/O | FND_FILE (output/log), UTL_FILE in PL/SQL | Lab 07-08 |

### BI Publisher / XML (Reporting)

| Topic | EBS Specific | Prep |
|-------|-------------|------|
| RTF Templates | EBS data model + report triggers | Oracle Docs |
| XML Data Structure | EBS concurrent program output XML | rtf2pdf |
| Bursting | EBS delivery options (email/FTP/print) | BI Pub Admin |
| Excel/PDF Output | EBS concurrent program style templates | BI Pub Dev |
| Sub-templates | EBS standard sub-template reuse | Oracle Support |

### Workflow

| Topic | EBS Specific | Prep |
|-------|-------------|------|
| WF Builder | Item type, process, activity, function | OWF Dev Guide |
| Notification | Approver routing, role resolution | WF Admin Guide |
| Business Event | Event-subscription model | BES documentation |
| Workflow Java | WF Java APIs (WF_ENGINE) | OWF Dev Guide |

---

## 4-Week EBS Interview Study Plan

### Week 1: Foundation + SQL

| Day | Morning (2 hrs) | Afternoon (2 hrs) | Evening (1 hr) |
|-----|----------------|-------------------|----------------|
| Mon | EBS Architecture (multi-tier, EBR) | NeetCode SQL: SELECT + Basic JOIN | Review: Lab 01 ARCHITECTURE.md |
| Tue | EBS File System (APPL_TOP, INST_TOP) | NeetCode SQL: JOINs + Self JOIN | Practice: LC 175, 181 |  |
| Wed | Concurrent Manager architecture | NeetCode SQL: Aggregation + GROUP BY | Practice: LC 596, 1179 |  |
| Thu | MOAC / VPD / Multi-Org | NeetCode SQL: Window Functions (RANK) | Practice: LC 178, 184, 185 |  |
| Fri | ADOP / EBR deep dive | NeetCode SQL: Date functions | Practice: LC 197, 1321 |  |
| Sat | Lab 01-02 full review | LC practice (5 Easy + 3 Medium) | Write: ARCHITECTURE TL;DR |  |
| Sun | Quiz Lab 01-02 | Rest / Review weak areas | Plan Week 2 |  |

### Week 2: Functional Modules + Advanced SQL

| Day | Morning (2 hrs) | Afternoon (2 hrs) | Evening (1 hr) |
|-----|----------------|-------------------|----------------|
| Mon | GL Cycle (R2R) + Key Tables | NeetCode SQL: CTE + Recursive | Practice: LC 1270, 570 |
| Tue | AP/P2P Cycle + Key Tables | NeetCode SQL: Subqueries | Practice: LC 183, 262 |
| Wed | AR/O2C Cycle + Key Tables | NeetCode SQL: CASE + Pivot | Practice: LC 626, 627 |
| Thu | INV + PO Core | EBS Performance Tuning: Indexes | Practice: LC 534, 1393 |
| Fri | OM + Pricing + ATP | EBS Performance: Partitioning | Practice: LC 1045, 1070 |
| Sat | Lab 03-04 full review | LC practice (5 Medium + 2 Hard) | Write: FINANCIALS TL;DR |
| Sun | Quiz Lab 03-04 | Review weak SQL patterns | Plan Week 3 |

### Week 3: Technical Deep Dive + Customization

| Day | Morning (2 hrs) | Afternoon (2 hrs) | Evening (1 hr) |
|-----|----------------|-------------------|----------------|
| Mon | OAF Architecture + BC4J | NeetCode Java: OOP, Inheritance | Lab 07: OAF patterns |
| Tue | OAF Controller + VO patterns | NeetCode Java: Design Patterns | Custom controller example |
| Wed | Workflow Builder + Notifications | BI Publisher: RTF templates | Lab 08: Workflow patterns |
| Thu | AME approval rules | XML Data Structure + Bursting | Lab 08: CEMLI patterns |
| Fri | Form Personalization | BI Publisher: Sub-templates | Lab 08: Extensions |
| Sat | Lab 05-06-07-08 full review | LC practice (3 Hard SQL + OOP) | Write: TECHNICAL TL;DR |
| Sun | Quiz Lab 05-08 | Mock interview (technical) | Plan Week 4 |

### Week 4: System Design + Behavioral + Mock

| Day | Morning (2 hrs) | Afternoon (2 hrs) | Evening (1 hr) |
|-----|----------------|-------------------|----------------|
| Mon | Upgrade: 12.1→12.2, ADOP cycles | System Design: HA + DR for EBS | Practice: 12.2 upgrade flow |
| Tue | Cloud migration: OCI/Azure/AWS | System Design: Integration (SOA/EDI) | Practice: Cloud arch diagrams |
| Wed | Security: User mgmt, audit, VPD | System Design: Scalability | Practice: Security architecture |
| Thu | Behavioral: STAR preparation | Company research (target company) | Prepare: "Tell me about yourself" |
| Fri | Full mock interview (tech) | Full mock interview (behavioral) | Review weak spots |
| Sat | Lab 09-10 full review | Final LC SQL speed run (10 min each) | Write: MIGRATION TL;DR |
| Sun | REST / Final review | Light review | **Interview Day Prep** |

---

## Company-Specific Focus

### Oracle (Product Company — Deep Product Knowledge)

| Area | Focus | Weight |
|------|-------|--------|
| EBS Architecture | Multi-tier, EBR, ADOP, RAC | 30% |
| Functional Modules | GL, AP, AR deep; O2C/P2P flows | 25% |
| Technical Depth | Forms, OAF, BC4J, Reports | 20% |
| SQL/PLSQL | EBS-specific schema queries | 15% |
| Behavioral | Product ownership, collaboration | 10% |

**LeetCode Priority:** LC 175-185, window function problems, OAF design patterns

---

### Deloitte / Accenture (Consulting — Functional + Technical)

| Area | Focus | Weight |
|------|-------|--------|
| EBS Implementation | Full lifecycle, client handling | 30% |
| Functional + Technical | Broad module knowledge, APIs | 25% |
| Solution Architecture | Fit-gap analysis, design | 20% |
| SQL / Data Migration | FNDLOAD, interfaces | 15% |
| Behavioral | Client management, team leading | 10% |

**LeetCode Priority:** LC 176-178-184, aggregation + JOIN problems (client reporting)

---

### Amazon (Cloud Migration Focus)

| Area | Focus | Weight |
|------|-------|--------|
| Cloud Architecture | EBS on AWS, migration planning | 35% |
| Scalability/Performance | RAC on EC2, DR strategies | 25% |
| Integration | API, messaging, S3 | 20% |
| SQL Performance | Tuning, partitioning | 15% |
| Leadership Principles | Ownership, dive deep, deliver | 10% |

**LeetCode Priority:** LC 197, 262, 601 (large-scale data problems), partitioning patterns

---

### Microsoft / Google (Cloud Platform Focus)

| Area | Focus | Weight |
|------|-------|--------|
| Cloud Migration | EBS on Azure/GCP, lift-shift | 35% |
| Database | Oracle vs SQL/Cloud SQL | 25% |
| Integration | Azure Logic Apps / GCP Workflows | 20% |
| Security | SSO, OAuth, Azure AD | 10% |
| Behavioral | Cross-team, ambiguity handling | 10% |

**LeetCode Priority:** LC 197, 570, 579, 1321 — complex window functions + CTE

---

### PwC / EY / KPMG (Audit/Compliance)

| Area | Focus | Weight |
|------|-------|--------|
| Security & Audit | SoD, VPD, audit trails, SOX | 35% |
| Financials Deep | GL close, consolidation, reporting | 25% |
| Data Integrity | Reconciliation, controls | 20% |
| SQL for Audit | Audit trail queries, access review | 15% |
| Behavioral | Professional skepticism, ethics | 10% |

**LeetCode Priority:** LC 182, 183, 196, 608 — security/audit pattern problems

---

### Infosys / TCS / Wipro (Services)

| Area | Focus | Weight |
|------|-------|--------|
| EBS Maintenance | ADOP patching, upgrade support | 30% |
| Custom Development | CEMLI, OAF, Forms extensions | 25% |
| Production Support | Incident management, break-fix | 20% |
| SQL / PLSQL | Tuning, debugging | 15% |
| Behavioral | Teamwork, client communication | 10% |

**LeetCode Priority:** LC 197, 1321, 180, 262 — production issue patterns + date-range SQL

---

## Additional Resources

| Resource | Type | Link |
|----------|------|------|
| NeetCode SQL Roadmap | Course | neetcode.io/sql |
| Oracle EBS R12.2 Docs | Documentation | docs.oracle.com |
| Oracle Support ADOP Guide | Doc 1585985.1 | support.oracle.com |
| Oracle EBS Performance Tuning | Whitepaper | Oracle Support |
| LeetCode SQL Problem Set | Practice | leetcode.com/problemset/database |
| Oracle EBS Developer's Guide | Guide | Part E22956-04 |
| Oracle Workflow Guide | Guide | Part E17133-04 |
| Oracle BI Publisher Dev Guide | Guide | Oracle Enterprise Docs |
| Oracle University EBS Training | Training | education.oracle.com |
| Exam: 1Z0-536 (EBS R12.2) | Certification | Oracle Certification |
