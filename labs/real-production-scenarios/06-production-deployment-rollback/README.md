# Lab 06: Production Deployment Rollback — Bad Deployment Takes Down 25% of Nodes

## Situation Overview

**Severity**: SEV1 (Critical Customer Impact)  
**Classification**: P0 Incident — Multiple Nodes Unavailable  
**Duration**: 47 minutes from first alert to full recovery  
**Scope**: 25% of production nodes (12 of 48) serving customer traffic  
**Users Impacted**: ~340,000 active sessions dropped, 12% error rate on API endpoints  
**Platform**: Microsoft Azure Kubernetes Service (AKS) with Azure DevOps Pipelines

### Incident Summary

A routine deployment of a minor feature enhancement (user profile preference caching) triggered a cascading failure across 25% of the production fleet. The deployment passed all CI/CD pipeline gates including unit tests, integration tests, and a 15-minute canary analysis window. Within 3 minutes of full rollout (100% traffic shift), unhealthy nodes began serving HTTP 500 errors due to an unhandled `NullPointerException` introduced in the new caching code path.

The incident exposed critical gaps in the deployment pipeline: insufficient canary duration, absence of feature flag gating for risky code paths, and lack of automated rollback triggers based on error rate thresholds. Engineering teams from three squads were paged within 10 minutes. Rollback was initiated at T+14 minutes but took an additional 33 minutes to fully drain traffic from affected nodes due to connection draining configuration issues.

### Infrastructure Context

The deployment pipeline uses Azure DevOps for build and release management, with multi-stage YAML pipelines managing promotion across dev, staging, canary, and production environments. Production consists of 48 nodes spread across 3 Azure regions (East US, West Europe, Southeast Asia) behind Azure Front Door with traffic manager profiles for geo-routing. Each node runs a Spring Boot 3.x application packaged as a Docker container deployed to AKS.

The deployment strategy was configured as a rolling update with a `maxSurge` of 25% and `maxUnavailable` of 25%. The team had recently migrated from manual deployment approvals to a fully automated pipeline with integrated canary analysis powered by a homegrown metrics comparison tool built on Azure Monitor and Application Insights.

### Why This Lab Matters

Real-world deployment incidents are the most common cause of production outages in cloud-native environments. Industry data from the Google SRE book ("Site Reliability Engineering", Chapter 14 — Managing Incidents) indicates that approximately 70% of all production incidents are triggered by changes — either code deployments, configuration changes, or infrastructure modifications. Netflix's "Chaos Engineering" philosophy (as documented in their Tech Blog post "The Netflix Simian Army") explicitly targets deployment resilience as a core system property.

This lab simulates a realistic Azure DevOps deployment failure scenario. You will:
1. Analyze the deployment pipeline configuration to identify the root cause
2. Implement proper null-checking and defensive programming in the failing code path
3. Configure feature flags with LaunchDarkly to gate high-risk features
4. Implement automated rollback triggers based on error rate monitoring
5. Design a blue-green deployment strategy with progressive exposure

The skills practiced in this lab directly translate to real-world Site Reliability Engineering (SRE) and platform engineering roles at Microsoft, Netflix, and Google.

### Key Engineering Concepts

- **Blue-Green Deployment**: Maintaining two identical production environments (blue and green) with traffic switched atomically at the load balancer level. This provides instant rollback capability by redirecting traffic back to the previous environment.
- **Canary Analysis**: Gradually routing a small percentage of production traffic to a new deployment while comparing key metrics (error rate, latency, throughput) against a baseline. Automated canary analysis uses statistical methods to detect metric regression.
- **Feature Flags (LaunchDarkly)**: Runtime toggles that control feature availability without redeployment. High-risk features should be wrapped in feature flags with kill-switch capability — allowing operations teams to disable problematic features instantly without rolling back the entire deployment.
- **Automated Rollback**: Pipeline-level automation that continuously monitors deployment health metrics and automatically triggers rollback when predefined thresholds are breached (e.g., error rate > 1% for more than 2 consecutive minutes).
- **Connection Draining**: The practice of allowing in-flight requests to complete before removing a node from service. Misconfigured draining windows significantly increase rollback time.

### References

- Google SRE Book — Chapter 14: "Managing Incidents", Chapter 20: "Load Balancing and Connection Draining"
- Microsoft Azure Documentation: "Blue-Green Deployment in AKS" (https://learn.microsoft.com/en-us/azure/aks/blue-green-deployment)
- Netflix Tech Blog: "The Netflix Simian Army" (https://netflixtechblog.com/the-netflix-simian-army-16e57fbab116)
- Netflix Tech Blog: "Canary Analysis with Automated Rollback" (https://netflixtechblog.com/canary-analysis-and-automated-rollback-at-netflix-8e4b6ef6e93e)
- AWS re:Invent 2021 — "Deployment Strategies at Amazon" (Channy Yun, AWS Developer Advocacy)
- LaunchDarkly Documentation: "Feature Flag Best Practices for High-Risk Deployments"
- Google SRE Book — Chapter 8: "Release Engineering"
- Microsoft Azure Well-Architected Framework: "Deployment and Testing" pillar

### Severity Assessment Criteria

```
SEV1 (Critical)  — Complete service unavailability for all users
SEV2 (High)      — Partial service degradation affecting subset of users
SEV3 (Medium)    — Functional impairment with workaround available
SEV4 (Low)       — Cosmetic issues or non-critical bugs
P0 (Immediate)   — Customer-facing outage, all hands on deck
P1 (High)        — Significant impact, dedicated team required
P2 (Medium)      — Moderate impact, fix within business hours
P3 (Low)         — Minor impact, fix within sprint
```

This incident qualified as SEV1/P0 because:
- More than 20% of nodes were unhealthy
- Customer-facing errors exceeded 10%
- Over 300,000 active sessions were abruptly terminated
- Revenue impact exceeded $50,000 per hour of downtime
- Data integrity risk due to incomplete write operations

## Organizational Context

### Team Structure
The incident involved 3 squads across the engineering organization:
- **SRE Team** (4 engineers): Owned the deployment pipeline, monitoring, and incident response. Responsible for runtime health, capacity management, and production infrastructure.
- **Platform Team** (6 engineers): Owned the Azure DevOps pipeline configuration, container orchestration (AKS), and feature flag infrastructure (LaunchDarkly). Maintained the CI/CD toolchain and deployment standards.
- **Feature Team (User Profile)** (5 engineers): Owned the application code being deployed — the `user-preference-cache` feature. Responsible for testing, code review, and feature flag integration.

### Reporting Structure
The SRE team reported to the Director of Platform Engineering. The feature team reported to the Director of Product Engineering. Both directors reported to the VP of Engineering. This organizational separation contributed to the incident because the feature team had full autonomy over their deployment pipeline without SRE oversight for this particular rollout.

### Incident Response Structure
Upon SEV1 declaration, the incident response followed a modified version of the Google SRE incident command structure:
- **Incident Commander (IC)**: Assigned to the SRE primary — responsible for coordinating response, communication, and decision-making
- **Operations Lead**: Managed the technical response — monitoring dashboards, executing rollback, verifying recovery
- **Communications Lead**: Managed internal and external status updates
- **Feature Team Lead**: Provided technical expertise on the failing code path
- **Scribe**: Documented the timeline and actions in real-time

### Engineering Culture and Blameless Post-Mortem
Following the incident, a blameless post-mortem was conducted in accordance with Google SRE practices (Chapter 15: "Postmortem Culture: Learning from Failure"). The post-mortem focused on identifying systemic failures rather than individual mistakes. Key outcomes were:
- The developer who introduced the null-pointer bug was explicitly not blamed — the focus was on why the pipeline allowed the bug to reach production
- The code reviewer was not held responsible — the focus was on why the review process missed the edge case
- The SRE team was not criticized for the slow rollback — the focus was on why connection draining was misconfigured

This culture of blamelessness is essential for maintaining psychological safety in engineering organizations and is a cornerstone of SRE practice at Google, Netflix, and Microsoft.

## Technical Deep Dive: The Failing Code Path

The incident was triggered by a null return from the `DownstreamPreferenceClient.getPreferences()` method. Understanding why this method returned null requires examining the downstream service's architecture.

The downstream preference service used a two-level caching architecture:
1. **L1 Cache**: In-memory Caffeine cache (local to each service instance) with a 60-second TTL
2. **L2 Cache**: Redis cluster with a 300-second TTL

The race condition occurred because:
1. Under high load (peak traffic at 14:00 UTC), multiple threads in the downstream service triggered L1 cache evictions concurrently
2. These threads simultaneously attempted to refresh from L2 cache
3. When the L2 cache also had stale or missing entries, all threads attempted to query the database
4. A race in the concurrent cache refresh logic caused some threads to return null instead of waiting for the refresh to complete

The race condition was in a rarely-exercised code path — it required specific timing of cache evictions, high concurrency, and L2 cache misses. This is why it had never been caught in testing or previous production runs.

The fix required not just a null check in the calling code (which would have prevented the incident), but also a proper fix in the downstream service's concurrent cache refresh logic to handle the race condition.

## Comparison with Industry Incidents

### Netflix — Canary Analysis Incident (2014)
Netflix's "Canary Analysis and Automated Rollback at Netflix" blog post describes a similar incident where a bad deployment passed canary analysis but caused failures at full rollout. Netflix's solution included:
- Statistical hypothesis testing for canary analysis (Student's t-test on error rates)
- Automated rollback triggers based on metric regression
- Progressive exposure with 1% → 5% → 25% → 50% → 100% traffic shifts

Our incident mirrored this pattern exactly. Our canary analysis only compared mean error rates without statistical significance testing. Netflix's approach would have detected the issue with higher confidence.

### Google SRE — Deployments on Google Cloud (2019)
Google's internal deployment tooling (as described in the SRE book) uses:
- Mandatory feature flags for all new code paths
- Automated rollback with 5-minute SLA
- Error budget tracking for deployment health
- Deployment freeze when error budget is depleted

Our organization lacked all four of these mechanisms, directly contributing to the severity of the incident.

### Microsoft Azure — Safe Deployment Practices (2022)
Microsoft's Azure DevOps documentation on safe deployment practices recommends:
- Progressive exposure using deployment slots (blue-green)
- Traffic splitting at the load balancer level
- Automated rollback triggers based on Azure Monitor metric alerts
- Feature flag integration with Azure App Configuration

Our deployment used Kubernetes rolling updates instead of blue-green, which meant we could not atomically switch traffic between versions. This increased our rollback time from seconds to minutes.

## Expanded Incident Response Analysis

### Communication Failure Points
During the incident, several communication gaps were identified:
1. The initial warning alert at T+3 minutes (error rate > 1%) was sent to a distribution list that was not actively monitored during business hours
2. The PagerDuty escalation policy had a 5-minute delay before paging the secondary SRE
3. The feature team lead was not automatically paged — they joined the bridge 3 minutes after being manually called by the SRE
4. There was no established communication channel for status updates — the team used a mix of Slack, bridge calls, and JIRA comments

### Decision-Making Analysis
The decision to roll back was delayed by approximately 3 minutes because:
1. The SRE primary was verifying that the issue was indeed caused by the new deployment (checking deployment versions, comparing metrics)
2. The SRE wanted to confirm with the feature team lead before initiating rollback
3. There was concern about the rollback itself causing additional issues (the rollback mechanism had not been tested in 6 months)

In hindsight, the decision should have been made faster. The guideline now is: "When in doubt, roll back first, investigate second."

## Training and Preparedness

### Incident Response Drills
Following this incident, the organization implemented quarterly incident response drills that simulate deployment failures. Key scenarios practiced:
1. Bad deployment requiring rollback
2. Database connection pool exhaustion
3. Cache stampede / database overload
4. Security incident / compromised credentials
5. Cascading microservice failure

### Tabletop Exercises
Monthly tabletop exercises review hypothetical incidents with cross-functional teams. Recent exercises include:
- "Feature flag accidentally enables beta feature for all users"
- "Canary deployment causes 5% error rate on critical endpoint"
- "Rollback mechanism fails — pods stuck in CrashLoopBackOff"

### Documentation And Runbooks
All deployment-related runbooks have been updated to include:
1. Step-by-step rollback procedures with screenshots
2. Connection draining verification commands
3. Feature flag kill-switch procedures
4. Canary analysis failure decision tree
5. Error budget status checks

## Expanded Severity Assessment

### Business Impact Quantification
The financial impact of this SEV1 incident was calculated as:
- **Direct Revenue Loss**: $52,300 (47 minutes at ~$67,000/hour revenue)
- **Customer Trust Impact**: Estimated at $120,000-200,000 in long-term churn
- **Engineering Time Cost**: $28,000 (12 engineers × 47 minutes + post-mortem)
- **Regulatory/Compliance**: Not applicable (no PII exposure confirmed)
- **Total Estimated Impact**: $200,000-280,000

### SLA/SLO Impact
The service had a monthly availability SLO of 99.95% (21.6 minutes of allowed downtime). This single incident consumed 47 minutes of downtime — more than double the monthly error budget. This triggered an SLO breach review and required re-baselining the SLO target for the next quarter.

### Customer Impact Analysis
Of the ~340,000 affected sessions:
- 210,000 were shopping cart sessions (abandoned carts due to errors)
- 85,000 were user profile edit sessions (changes may not have been saved)
- 45,000 were API integration sessions (partner services received error responses)
- 15% of affected users opened support tickets within the first hour
- Customer satisfaction scores dropped 12 points during the incident window

## Continual Improvement

### Post-Incident Review Cadence
1. **Immediate Review** (within 24 hours): Initial timeline, containment effectiveness, communication quality
2. **Root Cause Analysis** (within 72 hours): Full 5 Whys analysis, contributing factors
3. **Action Item Tracking** (weekly): Progress review of all prevention measures
4. **Quarterly Review**: Pattern analysis across all incidents, systemic improvements

### Metrics To Track
The following metrics are now tracked monthly to assess deployment safety:
1. **Deployment Failure Rate**: Percentage of deployments that require rollback
2. **Mean Time to Rollback (MTTR)**: Average time from rollback decision to full recovery
3. **Canary Analysis Coverage**: Percentage of deployments with automated canary analysis
4. **Feature Flag Adoption**: Percentage of new features wrapped in feature flags
5. **Error Budget Consumption**: Monthly error budget usage trend

### References Added
- Google SRE Book — Chapter 15: "Postmortem Culture: Learning from Failure"
- Netflix Tech Blog: "The Netflix Simian Army"
- Netflix Tech Blog: "Canary Analysis and Automated Rollback at Netflix"
- Microsoft Learn: "Safe Deployment Practices in Azure DevOps"
- AWS re:Invent 2019 — DOP004: "Deploying Safely at Amazon"
- AWS re:Invent 2021 — DOP205: "Progressive Deployment at Amazon"
- LaunchDarkly Blog: "Feature Flag Best Practices for Production Deployments"
- Chaos Engineering: "Principles of Chaos Engineering" (Principles of Chaos book)
- Azure Well-Architected Framework: "Operational Excellence Pillar"

## Expanded Learning Resources

### Recommended Reading for Deeper Understanding

**Books**:
- "Site Reliability Engineering" by Betsy Beyer et al. (Google SRE Book) — Chapters 8, 13, 14, 15
- "The DevOps Handbook" by Gene Kim et al. — Deployment patterns and practices
- "Accelerate" by Nicole Forsgren et al. — Metrics for deployment performance
- "Chaos Engineering" by Casey Rosenthal et al. — Principles of chaos engineering
- "Building Microservices" by Sam Newman — Deployment strategies for microservices

**Courses**:
- Microsoft Learn: "Deploy and manage containers with Azure Kubernetes Service"
- LaunchDarkly Academy: "Feature Flag Fundamentals"
- Coursera: "Site Reliability Engineering: Measuring and Managing Reliability" (Google Cloud)

**Videos**:
- AWS re:Invent 2021: "Deploying Safely at Amazon" (Channy Yun)
- Microsoft Build 2023: "Zero-Downtime Deployment with Azure DevOps"
- Google SRE Live: "Incident Response Best Practices"

### Glossary of Key Terms

| Term | Definition |
|------|-----------|
| Blue-Green Deployment | Two identical environments, traffic switched atomically between them |
| Canary Deployment | Gradual traffic shift to new version while monitoring metrics |
| Rolling Update | Gradual replacement of pods/nodes with new version |
| Feature Flag | Runtime toggle for enabling/disabling features without deployment |
| Kill Switch | Feature flag that immediately disables a feature across all users |
| Connection Draining | Allowing in-flight requests to complete before removing node from service |
| Canary Analysis | Automated comparison of metrics between old and new versions |
| Error Budget | Allowable downtime calculated from SLO (e.g., 99.9% SLO = 0.1% error budget) |
| Mean Time to Detection (MTTD) | Average time from fault to detection |
| Mean Time to Recovery (MTTR) | Average time from detection to full recovery |
| Progressive Exposure | Gradually increasing traffic percentage to a new deployment |
| Automated Rollback | System-initiated revert to previous version when health metrics degrade |

### Practical Exercise Scenarios

**Scenario 1: Rollback Drill**
Your team has deployed a new version of the user preference service to a canary environment. After 30 minutes at 5% traffic, you notice:
- Error rate: 0.5% (baseline: 0.1%)
- p95 latency: 210ms (baseline: 140ms)
- Throughput: 95% of baseline

Is this a successful canary? Should you roll back? What additional information would you need?

**Scenario 2: Feature Flag Failover**
Your team deployed a new recommendation engine behind a feature flag. Users report seeing incorrect recommendations. The feature flag is named `recommendation-engine-v2`. Walk through the steps to disable this feature via the kill switch.

**Scenario 3: Pipeline Configuration**
You are asked to configure a deployment pipeline for a new service. Design the YAML pipeline stages with:
1. Build and test stage
2. Dev deployment
3. Staging deployment with load test
4. Canary deployment (2% for 30 minutes)
5. Progressive rollout (25% → 50% → 75% → 100%)
6. Automated rollback trigger at 1% error rate

### Incident Commander Checklist Expanded

The incident commander should verify during each phase:

**During Detection**:
- Has the alert been confirmed as a genuine incident?
- What is the severity based on current metrics?
- How many users/customers are affected?
- Is there a known cause (recent deployment, configuration change)?

**During Containment**:
- Has rollback been initiated if this is a deployment issue?
- Has the feature flag been disabled?
- Is the error rate declining?
- Are affected nodes recovering?

**During Recovery**:
- Are all nodes healthy?
- Is traffic fully restored?
- Are error rates back to baseline?
- Has the root cause been identified?

**During Post-Mortem**:
- Has the full timeline been documented?
- Have all contributing factors been identified?
- Are action items created with owners?
- Has the incident report been shared with the team?
