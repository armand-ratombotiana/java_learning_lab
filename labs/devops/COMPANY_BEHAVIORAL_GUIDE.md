# DevOps Behavioral Interview Guide — Company-Specific

> DevOps behavioral questions, STAR stories, and cultural expectations for top tech companies.

---

## Table of Contents
1. STAR Framework
2. DevOps Behavioral Themes
3. Company-Specific Behavioral Expectations
4. Sample STAR Stories (DevOps)
5. Mapping Stories to Leadership Principles
6. Outage Response Stories
7. Automation Stories
8. Migration Stories
9. On-Call Stories
10. Blameless Culture Stories
11. SLO Negotiation Stories
12. Incident Command Stories

---

## 1. STAR Framework

**Situation** — Set the context. When? What team? What was the problem?
**Task** — What needed to be done? What was your role?
**Action** — What specific steps did you take? I, not "we."
**Result** — What happened? Quantified impact. What did you learn?

### DevOps STAR Template

```
S: Our team managed 3 legacy data centers with manual deployment processes.
   Deployments took 4 hours with a 30% failure rate.
T: I needed to automate deployments to reduce time and failure rate.
A: I designed and implemented a CI/CD pipeline using Jenkins, Docker, and
   Ansible. Created blue-green deployment strategy. Wrote automated rollback.
R: Deploy time reduced from 4 hours to 15 minutes. Failure rate dropped to 2%.
   Team adoption reached 100% within 2 months.
```

---

## 2. DevOps Behavioral Themes

| Theme | Why It Matters | Sample Question |
|-------|----------------|-----------------|
| Outage Response | DevOps must handle incidents calmly | "Tell me about a time you resolved a critical production outage." |
| Automation | DevOps is about reducing toil | "Describe a manual process you automated." |
| Migration | Changing infrastructure without downtime | "Tell me about a complex migration you led." |
| On-Call Experience | DevOps must sustain operations | "Describe your on-call experience." |
| Incident Command | Leading through incidents | "Tell me about a time you led an incident response." |
| Blameless Culture | Learning from failures | "Tell me about a postmortem you conducted." |
| SLO Negotiation | Balancing reliability and velocity | "Tell me about a time you negotiated an SLO." |
| Stakeholder Management | Communicating with non-technical audiences | "How do you explain downtime to executives?" |
| Cross-Team Collaboration | DevOps works across teams | "Tell me about a cross-functional project you led." |

---

## 3. Company-Specific Behavioral Expectations

### Google (Googleyness)
- **Values**: Ambiguity, leadership without authority, collaboration.
- **Key themes**: Handling ambiguity, failing forward, influencing without authority.
- **Sample**: "Tell me about a time you had to make a decision without complete information."
- **Expectation**: Structured, data-driven responses with humility.

### Meta (Ownership)
- **Values**: Move fast, own your outcomes, be direct.
- **Key themes**: Owner's mindset, proactive problem-solving, direct feedback.
- **Sample**: "Tell me about a time you saw a problem and fixed it without being asked."
- **Expectation**: High energy, bias for action, measurable results.

### Amazon (Leadership Principles)
- **Values**: 16 LPs — Customer Obsession, Ownership, Dive Deep, etc.
- **Key themes**: Every story MUST tie back to one or more LPs.
- **Sample**: "Tell me about a time you went above and beyond for a customer."
- **Expectation**: STAR format, explicit LP tie-in, quantified results. See section 5.

### Netflix (Freedom & Responsibility)
- **Values**: Candor, stunning colleagues, context not control.
- **Key themes**: Candid feedback, ownership, high standards.
- **Sample**: "Tell me about a time you gave a teammate hard feedback."
- **Expectation**: No STAR — direct, honest conversation. Thinkers, not scripts.

### Microsoft (Growth Mindset)
- **Values**: Learn from failures, seek feedback, customer focus.
- **Key themes**: Learning from mistakes, continuous improvement, mentorship.
- **Sample**: "Tell me about a time you failed and what you learned."
- **Expectation**: Self-aware, reflective, humble.

### GitHub (Collaboration)
- **Values**: Optimize for the reader, default to open, remote-first.
- **Key themes**: Async communication, open source contribution, empathy.
- **Sample**: "How do you communicate asynchronously in a remote team?"
- **Expectation**: Thoughtful, clear, inclusive.

### GitLab (CREDIT)
- **Values**: Collaboration, Results, Efficiency, Diversity, Iteration, Transparency.
- **Key themes**: Handbook-first, iteration, transparency.
- **Sample**: "Describe a time you shipped a minimum viable change."
- **Expectation**: Specific reference to GitLab values and handbook.

### HashiCorp (Principles)
- **Values**: Autonomy, transparency, collaboration, humility.
- **Key themes**: Self-direction, open communication, team player.
- **Sample**: "Tell me about a time you owned a project from start to finish."
- **Expectation**: Self-starter, humble, collaborative.

### Datadog (High Agency)
- **Values**: Customer-centric, high agency, ownership.
- **Key themes**: Taking initiative, customer empathy, solving hard problems.
- **Sample**: "Tell me about a time you went above and beyond for a customer."
- **Expectation**: Proactive, personal accountability.

### New Relic (Radical Transparency)
- **Values**: Transparency, customer obsession, intellectual honesty.
- **Key themes**: Sharing bad news early, data-driven decisions.
- **Sample**: "Tell me about a time you shared negative data with a stakeholder."
- **Expectation**: Honesty, openness, specific examples.

### Splunk (Data-Driven)
- **Values**: Data-driven, innovation, customer-first.
- **Key themes**: Using data to make decisions, solving customer problems.
- **Sample**: "Tell me about a time you used data to drive a difficult decision."
- **Expectation**: Data analysis examples, customer empathy.

### PagerDuty (Blameless Culture)
- **Values**: Customer first, blameless, bias for action.
- **Key themes**: Incident response, continuous improvement.
- **Sample**: "Tell me about a time you conducted a blameless postmortem."
- **Expectation**: Learning from failure, system thinking.

---

## 4. Sample STAR Stories (DevOps)

### Story 1: Outage Response
```
S: At 3 AM, our production Kubernetes cluster went down. All pods were
   in CrashLoopBackOff. The team was panicking. Customer-facing apps were down.
T: As the on-call SRE, I needed to diagnose the issue and restore service
   as quickly as possible.
A: I checked Grafana — saw a sudden spike in API server latency. Checked
   etcd — high disk latency. Ran 'etcdctl endpoint status' — one node was
   unresponsive. I cordoned the node, drained pods, replaced the etcd member.
   Service restored in 22 minutes. Later confirmed a disk failure.
R: Service restored in 22 minutes (P50 for SEV1 = 30 min). Root caused to
   disk failure. Implemented etcd monitoring alerts. No recurrence.
   Incident postmortem written, shared company-wide.
```

### Story 2: Automation
```
S: Our deployment process required 5 engineers to manually run scripts
   across 200 servers. Each deployment took 4 hours with frequent errors.
T: I wanted to automate the entire deployment process so one engineer
   could deploy reliably in under 30 minutes.
A: I designed a CI/CD pipeline with Jenkins, Ansible, and Docker.
   Created immutable AMIs with Packer. Wrote Ansible playbooks for
   configuration. Set up blue-green deployment with automated rollback.
   Documented the workflow and trained the team.
R: Deployment time reduced from 4 hours to 12 minutes. Failure rate dropped
   from 30% to <1%. Team scaled from 2 releases per month to 10+.
   Recognized with a company innovation award.
```

### Story 3: Migration
```
S: We needed to migrate 50 microservices from a monolith to Kubernetes
   with zero downtime. 200+ engineers, 5 teams, tight 6-month deadline.
T: I was the lead SRE for the migration. I needed to design the migration
   strategy and ensure zero downtime.
A: I designed a strangler fig pattern: each microservice was extracted one
   at a time. Used Istio for traffic splitting — 1% traffic to new service,
   monitor, gradually increase. Created Terraform modules for consistent
   infrastructure. Automated canary analysis with Prometheus + Flagger.
R: All 50 services migrated in 5 months. Zero production incidents.
   Improved deployment frequency from weekly to daily. Platform team re-used
   our Terraform modules company-wide.
```

### Story 4: On-Call Experience
```
S: Our on-call rotation was burning out engineers. 12-hour shifts, 50+
   alerts per night, most were noise. Team morale was low.
T: I needed to reduce on-call toil and improve alert quality.
A: I analyzed alert data — 80% of alerts were not actionable. Deleted
   30 unused alert rules. Set up alert grouping and silencing. Created
   runbooks for top 10 alerts. Reduced on-call shift from 12h to 8h by
   adding a follow-the-sun rotation. Monitored burn rate.
R: Alert volume dropped 80%. On-call satisfaction score went from 2/10
   to 8/10. Hire and retention improved. Pattern adopted by other teams.
```

### Story 5: SLO Negotiation
```
S: Product team wanted 99.999% uptime for a new service. Cost would be
   3x current infrastructure budget. I needed to negotiate a realistic SLO.
T: I had to educate the product team on SLO trade-offs and agree on a
   target that balanced reliability with cost and velocity.
A: I presented data: current service had 99.9% uptime with $10K/month.
   99.999% would require multi-region active-active + 5x redundancy =
   $30K/month. Proposed 99.95% with error budget policy. Showed that
   99.95% meets business needs for the next 12 months. Documented SLO,
   SLIs, and error budget in a team charter.
R: Agreed on 99.95% SLO. Error budget policy helped team ship faster.
   Cost stayed at $12K/month. Product team was happy with the trade-off
   analysis. Became the template for new service SLO negotiations.
```

### Story 6: Blameless Postmortem
```
S: A misconfigured firewall caused a 2-hour outage. Team was blaming
   the engineer who made the change. Morale was dropping.
T: As the SRE lead, I needed to conduct a blameless postmortem and
   prevent recurrence without blaming individuals.
A: I scheduled the postmortem within 24 hours. Opened with: "This is
   a system failure, not a person failure." Used timeline reconstruction
   to identify all contributing factors. Root cause: lack of change review
   process and insufficient test coverage for firewall changes. Created
   5 action items: change review workflow, automated firewall testing,
   staged rollout, alert on config drift, and a runbook. Assigned owners.
R: Action items completed in 2 weeks. No similar outage in 18 months.
   Team adopted blameless postmortem culture. The engineer who made the
   change became a contributor to the new change review process.
```

### Story 7: Incident Command
```
S: A cascading failure across 3 services affected millions of users.
   Multiple teams were debugging independently. Chaos.
T: I stepped in as Incident Commander to coordinate the response.
A: Declared SEV-1, set up a war room channel, assigned roles:
   - Comms Lead: stakeholder updates every 15 min
   - Ops Lead: coordinated mitigation steps
   - SME per affected service
   I maintained the timeline, tracked action items, prevented parallel
   debugging. Once mitigated, I drove the postmortem.
R: Incident mitigated in 45 minutes (complexity would have taken 2+ hours
   without coordination). Postmortem revealed 3 systemic issues.
   Implemented distributed tracing and circuit breakers. No recurrence.
```

### Story 8: Scaling Infrastructure
```
S: Our user base grew 5x in 3 months. Database queries went from 50ms
   to 2 seconds. Site was becoming unusable.
T: I needed to scale the infrastructure to handle the growth without
   downtime.
A: Analyzed performance data — database was the bottleneck.
   Implemented read replicas, connection pooling (PgBouncer), and
   caching with Redis (cache-aside pattern). Added auto-scaling for
   application tier. Used Terraform to manage infrastructure as code
   for consistent scaling.
R: Query latency dropped from 2s to 30ms. System handled 5x growth
   without issues. Infrastructure costs increased only 2x due to
   right-sizing. Team used the Terraform modules for new services.
```

---

## 5. Mapping Stories to Amazon Leadership Principles

| LP | Story That Fits |
|----|-----------------|
| Customer Obsession | Outage resolved quickly, customer communication |
| Ownership | Migration project, owned end-to-end |
| Invent and Simplify | Automation story |
| Are Right, A Lot | SLO negotiation, data-driven decision |
| Learn and Be Curious | Learning Terraform/K8s for migration |
| Hire and Develop the Best | Mentoring on-call engineers, postmortem training |
| Insist on the Highest Standards | Alert quality improvement, runbooks |
| Think Big | Multi-region architecture proposal |
| Bias for Action | 3 AM outage response |
| Frugality | SLO negotiation, right-sizing |
| Earn Trust | Blameless postmortem, transparent communication |
| Dive Deep | etcd disk failure debugging |
| Have Backbone; Disagree and Commit | SLO disagreement with product team |
| Deliver Results | Completed migration in 5 months |
| Strive to be Earth's Best Employer | Improved on-call experience |
| Success and Scale Bring Broad Responsibility | Security automation, user data protection |

---

## 6. Outage Response Stories

### Template
```
S: When/where did the outage occur? What was the impact?
T: What was your role (IC, incident commander, SME)?
A: Detection → Triage → Mitigation → Communication → Postmortem.
R: Time to resolution. Lessons learned. Prevention measures.
```

### Practice Scenarios
1. **Database outage**: Replication lag → failover → data loss risk.
2. **Network partition**: Cloud provider AZ failure → multi-region failover.
3. **Deployment failure**: Bad code pushed → rollback → fix → re-deploy.
4. **Certificate expiration**: TLS cert expired → traffic blocked.
5. **Resource exhaustion**: Cluster ran out of IPs → new pods pending.
6. **Security incident**: Breach detected → contain → rotate secrets.
7. **Dependency failure**: Upstream API deprecation → service degraded.
8. **Configuration drift**: Manual change → inconsistency → outage.

### Key Phrases for Outage Stories
- "Followed incident management playbook."
- "Declared SEV-1 and established command structure."
- "Mitigated by rolling back the last deployment."
- "Root cause was... implemented permanent fix."
- "Postmortem resulted in 5 action items."
- "Paged the right SMEs immediately."
- "Kept stakeholders informed every 15 minutes."

---

## 7. Automation Stories

### Template
```
S: What was the manual/tedious/repetitive task?
T: What was the impact (time, errors, morale)?
A: What did you automate? What tools? What was the approach?
R: Quantified improvement (time, error rate, team satisfaction).
```

### Practice Scenarios
1. **Deployment**: Manual → CI/CD pipeline.
2. **Testing**: Manual QA → automated regression tests.
3. **Infrastructure**: Click-ops → Terraform/Pulumi.
4. **Configuration**: Manual SSH → Ansible/Puppet.
5. **Monitoring**: Manual checks → Prometheus alerts.
6. **Backups**: Manual → automated scheduled + verified.
7. **Compliance**: Manual audits → policy as code (OPA).
8. **Reporting**: Manual → Grafana dashboards + automated reports.

### Key Phrases for Automation Stories
- "Wrote a script to automate..."
- "Designed a CI/CD pipeline that..."
- "Reduced toil by automating..."
- "Created Terraform modules to standardize..."
- "Implemented infrastructure as code for..."
- "Set up monitoring as code with..."

---

## 8. Migration Stories

### Template
```
S: What was being migrated (DC → cloud, monolith → microservices, VM → K8s)?
T: What was the complexity?  Why was a migration needed?
A: Migration strategy (strangler fig, big bang, parallel run). Tools used.
R: Completed on time? Zero downtime? Lessons learned?
```

### Practice Scenarios
1. **Data center to cloud migrate**: Physical servers → AWS/GCP/Azure.
2. **Monolith to microservices**: Breaking a monolith into services.
3. **VM to containers**: Traditional VMs → Docker/Kubernetes.
4. **Database migration**: Oracle → PostgreSQL, on-prem → RDS.
5. **CI/CD migration**: Jenkins → GitHub Actions/GitLab CI.
6. **Config management migration**: Puppet → Ansible, Chef → Terraform.
7. **Service mesh adoption**: Adding Istio to existing K8s cluster.
8. **Multi-cloud expansion**: Single cloud → multi-cloud.

### Key Phrases for Migration Stories
- "Designed a strangler fig pattern..."
- "Used feature flags to phase the migration."
- "Implemented blue-green deployment for zero-downtime."
- "Ran both systems in parallel for 2 weeks."
- "Created rollback plan before starting."
- "Added monitoring and dashboards before migration."

---

## 9. On-Call Stories

### Template
```
S: Describe the on-call rotation (schedule, team size, volume).
T: What was the challenge (burnout, noise, lack of runbooks)?
A: What systemic improvements did you make?
R: Improved on-call experience. Quantified improvement.
```

### Practice Scenarios
1. **Alert fatigue**: Reduced noise, consolidated alerts.
2. **Burnout**: Changed rotation, added follow-the-sun.
3. **Missing runbooks**: Created runbooks for top incidents.
4. **Insufficient coverage**: Hired, rebalanced team.
5. **Escalation issues**: Reworked escalation policies.
6. **Tooling**: PagerDuty configuration, on-call scheduling.

### Key Phrases for On-Call Stories
- "Reduced alert volume by X%."
- "Created runbooks for the top 10 alert types."
- "Implemented follow-the-sun rotation."
- "Improved MTTR by X%."
- "Added auto-remediation for common issues."
- "On-call satisfaction improved from X to Y."

---

## 10. Blameless Culture Stories

### Template
```
S: What incident or mistake occurred?
T: How did the team initially react?
A: How did you promote blamelessness? What systems changes were made?
R: Cultural impact. Recurrence prevention.
```

### Practice Scenarios
1. **Postmortem leadership**: Conducted first blameless postmortem.
2. **Cultural shift**: Changed from blame to learning culture.
3. **Process improvement**: Introduced change review, gradual rollout.
4. **Tooling**: Implemented feature flags, canary deployments.
5. **Training**: Team training on blameless practices.

### Key Phrases for Blameless Culture
- "Assumed good intent — focused on system, not person."
- "The postmortem opened with 'This is a system failure.'"
- "Created 3-5 actionable items to prevent recurrence."
- "Shared the postmortem company-wide."
- "Changed our deployment process from big bang to gradual rollout."
- "Introduced error budgets to balance reliability and velocity."

---

## 11. SLO Negotiation Stories

### Template
```
S: What service? What were the stakeholders asking for?
T: What was the conflict (cost vs reliability, velocity vs stability)?
A: How did you negotiate? What data did you present?
R: Agreed SLO. How did it impact the team/product?
```

### Practice Scenarios
1. **Product vs SRE**: Product wants 99.999%, SRE says 99.9%.
2. **Error budget policy**: Introducing error budget for the first time.
3. **New service**: Defining SLIs and SLOs for a brand new service.
4. **SLO violation**: SLO was violated — what now?
5. **Multi-service SLOs**: Complex dependencies, composite SLOs.

### Key Phrases for SLO Stories
- "Presented cost-reliability trade-off analysis."
- "Proposed 99.9% SLO with error budget of X minutes/month."
- "Defined SLIs based on the four golden signals."
- "Agreed on SLO that balanced customer needs with engineering cost."
- "Error budget policy gave team permission to ship faster."
- "Used burn rate alerts to detect SLO violations early."

---

## 12. Incident Command Stories

### Template
```
S: What was the incident (scale, impact, complexity)?
T: Why did the incident need command structure?
A: What roles did you establish? How did you coordinate?
R: Faster resolution. Better outcomes. Systemic fixes.
```

### Practice Scenarios
1. **Cascading failure**: Multiple services, multiple teams.
2. **Large-scale outage**: Regional cloud provider failure.
3. **Security incident**: Breach response, containment.
4. **Data loss**: Database corruption, recovery.
5. **Third-party dependency**: Critical vendor outage.

### Incident Command Roles
- **Incident Commander (IC)**: Coordinates, does not debug.
- **Operations Lead**: Executes mitigation steps.
- **Communications Lead**: Handles stakeholder updates.
- **SME**: Deep technical investigation.
- **Scribe**: Maintains timeline.

### Key Phrases for Incident Command Stories
- "I stepped in as Incident Commander."
- "Assigned clear roles: Ops Lead, Comms Lead, SMEs."
- "Set up a war room in Slack/Zoom."
- "Maintained compressed timeline."
- "Prevented parallel debugging."
- "Kept stakeholders informed every 15 minutes."
- "After mitigation, drove the blameless postmortem."

---

## Quick Reference: Story Bank

| Story Theme | Situation | Task | Action | Result |
|-------------|-----------|------|--------|--------|
| Outage | 3AM K8s crash | Restore service | Cordoned node, replaced etcd | 22 min MTTR |
| Automation | 4h manual deploy | Automate CI/CD | Jenkins + Docker + Ansible | 12 min, 30%→1% failure |
| Migration | 50 services to K8s | Zero-downtime migration | Strangler fig + Istio | 5 months, zero incidents |
| On-Call | 50 alerts/night | Reduce noise | Deleted 30 rules, runbooks | 80% reduction |
| SLO Negotiation | 99.999% request | Negotiate realistic SLO | Cost analysis, error budget | 99.95% agreed |
| Blameless | Firewall outage | Blameless postmortem | System-focused, 5 actions | No recurrence |
| Incident Command | Cascading failure | Coordinate response | Assigned roles, timeline | 45 min vs 2h+ |
| Scaling | 5x user growth | Scale infrastructure | Read replicas, Redis cache, ASG | 50ms→30ms latency |

---

## Behavioral Question Cheatsheet

### Common Questions
1. "Tell me about yourself." → 2-min summary: past, present, future.
2. "Why do you want to work here?" → Company research + your values.
3. "What is your greatest weakness?" → Real weakness + improvement plan.
4. "Tell me about a time you failed." → Honest, vulnerable, learned.
5. "Tell me about a time you had a conflict." → Both sides, resolution.
6. "How do you handle stress?" → Systems, not heroics.
7. "What's the most complex technical problem you solved?" → Architecture.
8. "Where do you see yourself in 5 years?" → Growth + contribution.
9. "Tell me about a time you disagreed with a decision." → Professional.
10. "Describe a time you influenced without authority." → Key SRE skill.

### DevOps-Specific Questions
1. "How do you handle a production outage?"
2. "Describe your ideal CI/CD pipeline."
3. "How do you approach monitoring and alerting?"
4. "Tell me about a time you improved reliability."
5. "What's your incident response philosophy?"
6. "How do you balance shipping fast vs being reliable?"
7. "Describe a time you reduced toil."
8. "How do you keep up with new DevOps technologies?"
9. "What's your experience with infrastructure as code?"
10. "How do you handle a missed SLO?"

---

_End of COMPANY_BEHAVIORAL_GUIDE.md_