# Module 65: Incident Response & SRE - Mini Project

**Project Name**: The Blameless Post-Mortem Exercise  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2 hours

---

## 🎯 Objective
Simulate the resolution of a critical production outage and author a professional, blameless Post-Mortem document using the "5 Whys" root-cause analysis technique.

## 📝 Requirements

### Core Features (Simulated Scenario)
You are the Incident Commander for a fictional E-Commerce company. At 14:00 on Black Friday, the entire checkout API started returning HTTP 500 errors. Customers could not buy items for 45 minutes.

**The Facts Discovered During the Incident**:
- The checkout API connects to a legacy Inventory Database.
- A junior developer ran a database migration script manually on the production database instead of the staging database.
- The script locked the `inventory` table for 45 minutes while attempting to add a new column.
- Connection pools on the API Gateway immediately filled up waiting for the database to respond, crashing the gateway.
- At 14:45, the DBA killed the migration script process, and the system instantly recovered.

### The Task: Author the Post-Mortem Document
Create a Markdown document (`POST_MORTEM.md`) structured strictly as follows:

1. **Executive Summary**: 2-3 sentences explaining what happened, the duration, and the impact (estimated revenue lost).
2. **Timeline**: A minute-by-minute breakdown of the incident (Detection, Triage, Mitigation, Resolution).
3. **Root Cause (The 5 Whys)**:
   - Perform a deep dive to find the *true* systemic failure. Do NOT stop at "The developer ran the wrong script."
   - *Example*: Why did the API go down? Because the connection pool exhausted. Why? Because the DB was locked. Why? Because a script was run in prod. Why was it run in prod manually? Because our CI/CD pipeline doesn't support automated migrations yet. Why does the dev have raw access to prod? Because we haven't implemented IAM role-based access control.
4. **Action Items (Preventative Measures)**:
   - List 3 concrete engineering tasks to ensure this exact scenario can never happen again. (e.g., "Implement Liquibase for automated migrations," "Revoke manual write access to Prod DB," "Implement Circuit Breaker on the Checkout API to prevent connection pool exhaustion").

---

## 💡 Solution Blueprint (Action Items Excerpt)

**Action Items**:
- [ ] **Infrastructure**: Revoke all direct human write access to the Production Database clusters. Implement read-only temporary credentials via Vault. (Owner: DevOps Team)
- [ ] **Code**: Implement `Resilience4j` Circuit Breakers on the API Gateway to fast-fail requests if the Inventory DB latency exceeds 2000ms, preventing global thread exhaustion. (Owner: Backend Team)
- [ ] **Process**: Migrate all raw SQL scripts to Flyway. Force all schema changes to be executed exclusively through the automated Jenkins CI/CD pipeline. (Owner: Database Team)