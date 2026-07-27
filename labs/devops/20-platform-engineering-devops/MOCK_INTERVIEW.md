# Platform Engineering MOCK_INTERVIEW.md

## Scenario 1: Internal Developer Platform (IDP)
Your organization has 200+ engineers struggling with infrastructure complexity.

**Questions**:
1. What's an internal developer platform?
2. How does it differ from a DevOps team?
3. What components make up an IDP?
4. How do you measure platform success?

**Expected approach**: IDP: self-service layer (portal + APIs) that abstracts infrastructure, enabling devs to deploy without deep infra knowledge. Components: CI/CD, IaC, secrets, monitoring, service catalog. Metrics: developer onboarding time, deployment frequency, MTTR, platform adoption %, developer satisfaction (DORA + SPACE).

## Scenario 2: Platform Backstage
Your team wants to adopt Backstage (Spotify) as the developer portal.

**Questions**:
1. What problems does Backstage solve?
2. How do you create Backstage plugins?
3. How do you model software entities?
4. How do you integrate Backstage with existing tools?

**Expected approach**: Backstage provides unified catalog, software templates, tech docs, and plugin ecosystem. Plugins: React components (EntityPage, cards). Software Catalog: YAML metadata files (catalog-info.yaml) in each repo. Integrations: plugins for K8s, ArgoCD, Datadog, PagerDuty, GitHub/GitLab.

## Scenario 3: Golden Paths and Scaffolding
Teams are setting up their projects differently — inconsistency everywhere.

**Questions**:
1. What are golden paths?
2. How do you create a scaffold/template?
3. How do you enforce golden path adoption?
4. How do you update golden paths over time?

**Expected approach**: Golden paths: pre-approved, supported ways to build/deploy services. Templates: Backstage software templates, Cookiecutter, `git clone` starter repos. Enforcement: PR reviews, policy checks, linting, no exceptions without platform team review. Updates: maintain templates, communicate changes, automated migration scripts.

## Scenario 4: Platform as a Product
The platform team needs to treat the platform as a product.

**Questions**:
1. What does "platform as a product" mean?
2. How do you gather user feedback?
3. How do you prioritize features?
4. How do you communicate releases?

**Expected approach**: Treat dev teams as customers. Feedback: surveys, office hours, feedback channels, usage analytics. Prioritization: user story mapping, RICE scoring, aligned with business goals. Communication: changelog, release notes, feature announcements, platform newsletter, demo days.

## Scenario 5: Platform Migration
You're migrating from a traditional DevOps team model to a platform engineering model.

**Questions**:
1. What's the migration strategy?
2. How do you handle existing workflows?
3. How do you get buy-in from dev teams?
4. How do you measure migration success?

**Expected approach**: Incremental: start with one golden path, expand. Don't break existing workflows — offer migration path and support. Buy-in: show concrete value (faster deploys, less toil), involve early adopters. Success: adoption %, feedback, DORA metrics improvement, reduced cognitive load for devs.

## Key Platform Engineering Interview Questions
1. What's the difference between DevOps and Platform Engineering?
2. Explain Team Topologies (stream-aligned, platform, enabling, complicated-subsystem).
3. What's a service catalog and why is it important?
4. How do you design platform APIs?
5. What's the role of a platform engineer?
6. How do you balance platform flexibility vs standardization?
7. What's cognitive load in platform engineering?
8. How does Crossplane enable platform engineering?
9. What's the difference between a portal and a platform?
10. How do you handle platform versioning and upgrades?

## Whiteboard Challenge
Design an Internal Developer Platform for an organization with 10 engineering teams, 50+ microservices, across 3 cloud providers. Include CI/CD, infrastructure provisioning, secrets management, monitoring, and developer portal.

## Follow-up
1. How would you handle platform cost allocation?
2. How would you implement platform SLAs?
3. How would you handle platform team scaling?