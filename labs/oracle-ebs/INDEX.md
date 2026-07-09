# Oracle E-Business Suite (EBS) Academy

## Overview
This academy provides a comprehensive, hands-on curriculum for mastering Oracle E-Business Suite R12.2. From core architecture to upgrade migration, each lab blends theoretical foundations with practical Java implementations that simulate real EBS behaviors. The academy is designed for Java developers, Oracle EBS technical consultants, functional consultants, DBAs, and architects who want to build deep expertise in the world's most widely deployed enterprise application suite.

## Curriculum — 10 Labs

| # | Lab | Focus Area | Java Package |
|---|-----|------------|-------------|
| 01 | ebs-architecture | Multi-tier architecture, file system, editioning | com.oracleebs.architecture |
| 02 | ebs-setup-config | Rapid Install, MOAC, flexfields, profiles | com.oracleebs.setup |
| 03 | ebs-financials | GL, AP, AR, SLA, payment processing | com.oracleebs.financials |
| 04 | ebs-supply-chain | INV, PO, OM, pricing, ATP | com.oracleebs.scm |
| 05 | ebs-manufacturing | WIP, MRP, BOM, quality | com.oracleebs.manufacturing |
| 06 | ebs-hrms | HRMS, payroll, talent, absence | com.oracleebs.hrms |
| 07 | ebs-technical-architecture | OAF, BC4J, concurrent programs | com.oracleebs.technical |
| 08 | ebs-customization-extension | CEMLI, WF Builder, AME | com.oracleebs.customization |
| 09 | ebs-security-controls | VPD, audit, user management | com.oracleebs.security |
| 10 | ebs-upgrade-migration | ADOP, cloud migration, pre-req checks | com.oracleebs.upgrade |

## Learning Path

**Prerequisites**: Basic Java (SE 8+), SQL, understanding of relational databases. No prior EBS experience required.

1. **Foundation** → Lab 01 (Architecture) + Lab 02 (Setup/Config)
2. **Functional Core** → Lab 03 (Financials) + Lab 04 (Supply Chain) + Lab 05 (Manufacturing)
3. **People & Platform** → Lab 06 (HRMS)
4. **Technical Deep-Dive** → Lab 07 (Technical Architecture)
5. **Customization & Security** → Lab 08 (Customization) + Lab 09 (Security)
6. **Maintenance & Migration** → Lab 10 (Upgrade)

Each lab contains 24 markdown documents following a standard template, Java source files with JUnit 5 tests, and project directories for benchmarks, challenges, diagrams, mini-projects, and real-world projects.

## Related Academies
- **Databases Academy** — Oracle Database architecture, SQL tuning, PL/SQL
- **Backend Academy** — Java EE, Spring Boot, REST API design
- **Security Academy** — OAuth2, SAML, encryption, identity management
- **DevOps Academy** — CI/CD, containerization, cloud migration

## Resources
- Oracle EBS R12.2 Documentation Library (docs.oracle.com)
- Oracle Support Doc ID 1585985.1 (ADOP Reference)
- Oracle EBS Developer's Guide (Part No. E22956-04)
- Oracle Workflow Developer's Guide (Part No. E17133-04)
- Oracle EBS System Administrator's Guide (Part No. E22943-04)

## Lab File Structure (per lab)

```
<lab>/
├── BENCHMARK/
├── CHALLENGE/
├── DIAGRAMS/
│   └── .gitkeep
├── MINI_PROJECT/
├── REAL_WORLD_PROJECT/
├── SOLUTION/
├── TESTS/
├── src/
│   ├── main/java/com/oracleebs/<module>/
│   │   ├── <Java sources>
│   └── test/java/com/oracleebs/<module>/
│       ├── <JUnit 5 tests>
├── 24 documentation files:
│   ├── README.md
│   ├── THEORY.md
│   ├── MATH_FOUNDATION.md
│   ├── CODE_DEEP_DIVE.md
│   ├── EXERCISES.md
│   ├── QUIZ.md
│   ├── ARCHITECTURE.md
│   ├── SECURITY.md
│   ├── PERFORMANCE.md
│   ├── REFACTORING.md
│   ├── DEBUGGING.md
│   ├── COMMON_MISTAKES.md
│   ├── STEP_BY_STEP.md
│   ├── VISUAL_GUIDE.md
│   ├── INTERNALS.md
│   ├── HOW_IT_WORKS.md
│   ├── MENTAL_MODELS.md
│   ├── HISTORY.md
│   ├── WHY_IT_MATTERS.md
│   ├── WHY_IT_EXISTS.md
│   ├── REFERENCES.md
│   ├── REFLECTION.md
│   ├── INTERVIEW.md
│   └── FLASHCARDS.md
```

## Total Files
- 241 markdown files (INDEX.md + 10 labs × 24 files)
- 30 Java source files (10 labs × 3 sources)
- 20 JUnit 5 test files (10 labs × 2 tests)
- 10 .gitkeep marker files
- 70 subdirectory markers (BENCHMARK, CHALLENGE, DIAGRAMS, MINI_PROJECT, REAL_WORLD_PROJECT, SOLUTION, TESTS per lab)