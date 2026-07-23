# Mock Interview: EBS Upgrade & Migration (ebs-upgrade-migration)

**Role:** Oracle EBS Upgrade Specialist / DBA  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are the main considerations for upgrading from EBS R12.1 to R12.2?

**Candidate:** Key considerations:
1. **Technology stack upgrade:** R12.1 uses Oracle Application Server (OC4J); R12.2 uses WebLogic
2. **File system reorganization:** R12.2 introduces two-file system structure (run/patch)
3. **ADOP replaces AD Patch:** Online patching requires Edition-Based Redefinition (EBR)
4. **Database compatibility:** R12.2 requires minimum Oracle Database 12.2, ideally 19c
5. **Forms/OAF upgrade:** Forms 10g → Forms 12c, OAF version changes
6. **Customizations:** All customizations must be revalidated for EBR compatibility
7. **Downtime estimation:** Cutover window of 8-24 hours depending on data size

**Interviewer:** What is the difference between a technical upgrade and a functional upgrade?

**Candidate:** 
- **Technical upgrade:** Focuses on the technology stack — database, forms, OAF, WebLogic. No changes to business functionality. Primary goal: get on supported platform with minimal functional impact.
- **Functional upgrade:** Involves business process changes, new features in the upgraded EBS version. May include re-implementation of certain modules, new functionality adoption.

Most organizations do a technical upgrade first (to get off unsupported versions), then a separate functional upgrade later.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Walk through the R12.1 to R12.2 upgrade process.

**Candidate:** High-level upgrade steps:

**Phase 1 — Pre-upgrade (4-8 weeks):**
1. System health check (ORA-600 errors, invalid objects, space)
2. Identify and resolve customizations
3. Apply R12.1 latest patchset
4. Clone production → build test upgrade environment
5. Test upgrade in test environment (3-4 iterations)

**Phase 2 — Technology stack:**
1. Upgrade database to 19c (while running R12.1.3 apps tier)
2. Install new R12.2 apps tier software
3. Run Rapid Install for R12.2 technology stack

**Phase 3 — Database upgrade:**
1. Run `adpreclone.pl` on R12.1 apps tier
2. Run `addbmerge.sql` for data migration
3. Run AutoConfig
4. Validate schema counts and object counts

**Phase 4 — ADOP initialization:**
1. Initialize ADOP with edition-based redefinition
2. Validate EBR setup
3. Run ADOP prepare cycle

**Phase 5 — Post-upgrade:**
1. Apply latest AD/TXK patches
2. Recompile invalid objects
3. Functional testing (all critical cycles)
4. Performance benchmark comparison
5. User acceptance testing (UAT)

---

## Round 3: Hard (15-25 minutes)

**Interviewer:** You're upgrading a 5TB EBS R12.1.3 database to R12.2.10. The upgrade must complete within a 48-hour weekend window. Design the plan.

**Candidate:** 

**Weekend upgrade plan:**

**Friday 6 PM — Start:**

```
Fri 18:00 — SYSTEM/SYS no-login trigger enabled
Fri 18:30 — Kill all EBS sessions (apps tier shutdown)
Fri 19:00 — Begin database pre-upgrade checks
    - `preupgrd.sql` analysis
    - `DBMS_PREUP.ENABLE_EXCEPTION`
    - Tablespace space check
    - INVALID object report
Fri 20:00 — Begin database upgrade (R12.1.3 11gR2 → 19c)
    - Time estimate: 6-8 hours for 5TB
    - Parallel processing at 8 threads
    - Monitor alert log for errors
Sat 04:00 — Database upgrade complete
Sat 05:00 — Post-upgrade DB steps
    - `catbundle.sql`
    - `utlrp.sql` (recompile invalid objects)
    - `utluptabstats.sql` (update optimizer stats)
    - `postupgrd.sql` analysis
```

**Saturday 10 AM — Apps tier:**

```
Sat 10:00 — Install R12.2.10 apps tier
    - Rapid Install (multi-node, 4 hours)
    - WebLogic domain creation
    - OHS configuration
Sat 14:00 — Run addbmerge (data migration)
    - Time estimate: 3-5 hours for 5TB
Sat 19:00 — Apps tier configuration
    - Run AutoConfig (all nodes)
    - Validate concurrent manager startup
Sat 22:00 — ADOP initialization
    - `adop phase=prepare -initialize`
    - Time: 1-2 hours
```

**Sunday 12 AM — Validation:**

```
Sun 00:00 — ADOP cutover
    - Brief downtime (< 30 min)
Sun 01:00 — Functional validation
    - GL Period Open → Create Journal → Post → Report
    - AP Invoice Entry → Validate → Pay
    - AR Transaction → Receipt → Apply
Sun 06:00 — Performance benchmark
    - Compare query response times with R12.1
    - Run concurrent requests
    - Validate forms performance
Sun 12:00 — UAT environment ready
Sun 16:00 — Issue resolution
Sun 22:00 — Sign-off
```

**Risk mitigation:**
- Full RMAN backup before starting
- File system snapshot before apps tier changes
- Rollback plan: Restore from backup (adds 12 hours)
- Dedicated DBA team (2 people) monitoring throughout
- Oracle Support ticket pre-opened for critical issues

**Interviewer:** What are the most common issues during EBS upgrades?

**Candidate:** Top 10 issues:
1. **Invalid objects after upgrade:** Caused by missing packages, compilation errors
2. **ADOP cutover failures:** Long-running transactions blocking EBR cutover
3. **Forms incompatibility:** Custom Forms built on older version not compatible with Forms 12c
4. **WebLogic configuration:** HTTP session replication, OHS mod_plsql configuration
5. **Customization conflicts:** Custom code accessing EBS tables in ways not compatible with EBR
6. **Performance regression:** Missing optimizer statistics, plan changes
7. **Oracle Net connectivity:** Connection string format changes
8. **Java security issues:** Forms applet signing certificates, JRE version compatibility
9. **Profile option migration:** Some profiles renamed or deprecated in R12.2
10. **Thread stuck issues:** WebLogic threads blocked by long-running PL/SQL calls

**Rollback strategy:**
- Database: RMAN restore from pre-upgrade backup
- Apps tier: Revert to R12.1 cloned APPL_TOP
- Time: ~12 hours total rollback
- Trigger: If functional validation finds critical business process failure

---

## Interviewer Feedback

**Strengths:** Excellent upgrade planning, practical timeline, strong risk mitigation  
**Areas to Improve:** Could discuss zero-downtime upgrade approaches using multiple application servers  
**Verdict:** Strong Hire

---

*EBS Upgrade & Migration MOCK_INTERVIEW.md — Part of Oracle EBS Academy Interview Preparation*
