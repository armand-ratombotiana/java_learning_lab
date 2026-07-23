# Oracle APEX — Company Interview Guide

<div align="center">

![Oracle](https://img.shields.io/badge/Oracle-Interviews-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Google](https://img.shields.io/badge/Google-4285F4?style=for-the-badge&logo=google&logoColor=white)
![Microsoft](https://img.shields.io/badge/Microsoft-0078D4?style=for-the-badge&logo=microsoft&logoColor=white)
![Amazon](https://img.shields.io/badge/Amazon-FF9900?style=for-the-badge&logo=amazon&logoColor=white)

**Prepare for Oracle APEX and database-related interviews at top tech companies**

</div>

---

## Table of Contents

1. [Oracle-Specific Interview Process](#oracle-specific-interview-process)
2. [Google/Microsoft/Amazon Interview for APEX Roles](#googlemicrosoftamazon-interview-for-apex-roles)
3. [Apple Interview for Database-Related Roles](#apple-interview-for-database-roles)
4. [LeetCode SQL Problem Mappings](#leetcode-sql-problem-mappings)
5. [Real Interview Experiences](#real-interview-experiences)

---

## Oracle-Specific Interview Process

### Typical 4-5 Round Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| 1 | Recruiter Screen | 30 min | Background, skills, salary expectations |
| 2 | Technical Screen (Hiring Manager) | 45-60 min | APEX fundamentals, project experience, SQL |
| 3 | Technical Deep Dive | 60 min | Architecture, security, performance, ORDS |
| 4 | System Design / Whiteboarding | 60 min | Design an APEX application architecture |
| 5 | Behavioral / Cross-Functional | 45 min | Teamwork, conflict resolution, leadership |

### Common Technical Questions for APEX Roles

**APEX Fundamentals:**
1. Explain the APEX page rendering lifecycle — what processes fire in which order?
2. How does APEX session state management work?
3. Compare Interactive Grid vs Classic Report — when to use each?
4. How do you implement authentication and authorization in APEX?
5. Explain the difference between page-level, application-level, and item-level session state protection.

**Oracle Database:**
1. Explain Oracle architecture: SGA, PGA, background processes
2. How do you optimize a slow APEX page? Walk through your diagnostic process.
3. Explain partitioning, indexing strategies for APEX applications
4. How does Oracle's multitenant architecture work (CDB/PDB)?
5. Explain Oracle Data Guard and its role in APEX high availability

**RESTful Services:**
1. How do you consume external REST APIs from APEX?
2. Explain ORDS architecture and configuration
3. How do you expose APEX data as RESTful services?
4. Describe the Web Source Module feature

**Performance:**
1. How would you tune an APEX page that loads in 10 seconds?
2. Explain APEX caching options and when to use each
3. How do you use APEX's debug mode to diagnose performance issues?

### Behavioral Questions
1. Describe a complex APEX application you built — what challenges did you face?
2. How do you handle conflicts between APEX best practices and business requirements?
3. Tell me about a time you optimized a slow APEX application.

---

## Google/Microsoft/Amazon Interview for APEX Roles

### Google — Technical Solutions Consultant / Cloud Engineer (APEX)

**Interview Process:**
| Round | Focus | Topics |
|-------|-------|--------|
| Phone Screen | General technical skills | SQL, cloud concepts, APIs |
| Technical | APEX + Oracle | APEX architecture, ORDS, security |
| System Design | Database design | Schema design for low-code apps, scalability |
| Googleyness | Behavioral | Leadership, ambiguity handling |

**Sample Questions:**
1. "If you could redesign APEX for Google Cloud, what would you change?"
2. "Design a multi-tenant APEX application for Google Workspace customers."
3. "How would you migrate an on-premise APEX application to Google Cloud?"

### Microsoft — Azure Database / Low-Code Engineer

**Interview Process:**
| Round | Focus | Topics |
|-------|-------|--------|
| Phone Screen | Background + Azure knowledge | Oracle on Azure, Power Platform vs APEX |
| Technical (Azure) | Oracle + Azure Integration | Running APEX on Azure VMs, Azure SQL vs Oracle |
| Technical (APEX) | Deep APEX knowledge | Security, performance, ORDS |
| System Design | Low-code platform design | Compare APEX with Power Platform |
| Behavioral | Microsoft culture | Customer focus, growth mindset |

### Amazon — Database Specialist / Solutions Architect

**Interview Process:**
| Round | Focus | Topics |
|-------|-------|--------|
| Phone Screen | Technical background | APEX experience, AWS knowledge |
| Technical | Oracle on AWS | RDS Oracle, EC2 APEX deployment, DMS migration |
| System Design | Design a low-code platform | From APEX perspective, architecture choices |
| Leadership Principles | Behavioral | Ownership, customer obsession, bias for action |

**Sample Questions:**
1. "Design a high-availability APEX deployment on AWS."
2. "How would you migrate an Oracle APEX application to Amazon Aurora?"
3. "Cost-optimize an APEX application running on AWS."

---

## Apple Interview for Database Roles

### Apple — Database Engineer / Backend Engineer

**Interview Process:**
| Round | Focus | Topics |
|-------|-------|--------|
| Recruiter Screen | Background | Database experience, Apple product knowledge |
| Technical (SQL) | Advanced SQL | Complex queries, PL/SQL, performance tuning |
| Technical (Database) | Database internals | ACID, isolation levels, indexing, partitioning |
| System Design | Scalable database | Design a global database for Apple services |
| Behavioral | Apple culture | Privacy focus, attention to detail |

**Sample Questions:**
1. "Design the database schema for iMessage message storage."
2. "How would you ensure GDPR compliance in a global database?"
3. "Write a query to find the most common words in user feedback."
4. "How do you handle database encryption at rest and in transit?"

---

## LeetCode SQL Problem Mappings

### Easy (APEX Interview Level)
| Problem | Concepts | APEX Relevance |
|---------|----------|----------------|
| 175. Combine Two Tables | JOIN | SQL Workshop basics |
| 176. Second Highest Salary | Subquery, LIMIT | Interactive Grid reports |
| 181. Employees Earning More Than Managers | Self-JOIN | Hierarchical data |
| 182. Duplicate Emails | GROUP BY, HAVING | Data validation |
| 183. Customers Who Never Order | LEFT JOIN, IS NULL | Data analysis |

### Medium (APEX Technical Screen)
| Problem | Concepts | APEX Relevance |
|---------|----------|----------------|
| 177. Nth Highest Salary | Window functions | Report ranking |
| 178. Rank Scores | DENSE_RANK | Leaderboard reports |
| 180. Consecutive Numbers | Self-JOIN, LEAD/LAG | Trend analysis |
| 184. Department Highest Salary | Window functions, JOIN | Department reports |
| 550. Game Play Analysis IV | Aggregation, date functions | User engagement analysis |

### Hard (System Design / Deep Dive)
| Problem | Concepts | APEX Relevance |
|---------|----------|----------------|
| 185. Department Top Three Salaries | Window functions, JOIN | Performance reports |
| 262. Trips and Users | Complex JOINs, date filtering | Data integrity queries |
| 601. Human Traffic of Stadium | Window functions, gaps | Pattern analysis |
| 1097. Game Play Analysis V | CTE, aggregation | Advanced analytics |

---

## Real Interview Experiences

### Oracle APEX Developer (Glassdoor Aggregated)

**Experience #1 — Oracle APEX Engineer (Redwood Shores, 2023)**
- "3 rounds: Technical phone (SQL + APEX fundamentals), System design (design a customer portal in APEX), Behavioral"
- "System design question: Design an employee expense tracking system using APEX"
- "SQL question: Write a recursive CTE to find organizational hierarchy"

**Experience #2 — Senior APEX Developer (Remote, 2024)**
- "4 rounds: Hiring manager (project experience), Technical (APEX deep dive — security, performance), SQL (LeetCode medium), Final (director)"
- "APEX deep dive: Walk through how you would implement row-level security"
- "Performance: A page takes 15 seconds to load — debug it step by step"

**Experience #3 — Oracle APEX Cloud Engineer (OCI, 2024)**
- "1st round: Recruiter discussing OCI and APEX cloud deployment"
- "2nd round: Technical — how does ORDS work, how to deploy APEX on OCI"
- "3rd round: Live coding — build a RESTful service in APEX and integrate with a web source module"
- "4th round: System design — design a multi-region APEX deployment"

### Levels.fyi Insights
- **Average salary for APEX Developer:** $110K-$150K (US)
- **Senior APEX Engineer:** $140K-$180K
- **APEX Architect/Principal:** $170K-$220K
- **Top paying companies:** Oracle, Amazon (AWS RDS team), Microsoft (Azure Oracle team), Google (Cloud SQL)

---

<div align="center">

**"The best way to prepare is practical APEX experience combined with strong SQL fundamentals."**

</div>
