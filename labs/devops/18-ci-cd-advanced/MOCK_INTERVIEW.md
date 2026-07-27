# CI/CD Advanced MOCK_INTERVIEW.md

## Scenario 1: Advanced Pipeline Patterns
Your pipeline needs to handle complex scenarios: conditional execution, manual approvals, and rollbacks.

**Questions**:
1. How do you implement conditional stages?
2. How do you add manual approval gates?
3. How do you implement automatic rollback on failure?
4. How do you handle pipeline timeouts and retries?

**Expected approach**: Conditional: rules/changes, if statements. Gates: environment protection rules (GitHub), manual approvals (GitLab), input step (Jenkins). Rollback: git revert + redeploy, `kubectl rollout undo`, Terraform state rollback. Retries: `retry` parameter, backoff strategy.

## Scenario 2: Monorepo Pipeline
Your monorepo has 10 services. CI should only build changed services.

**Questions**:
1. How do you detect which services changed?
2. How do you only build and test changed services?
3. How do you handle shared library changes?
4. How do you parallelize builds?

**Expected approach**: Path-based triggers (paths, paths-ignore, changes). Detect changes: `git diff`, `CI_COMMIT_BEFORE_SHA`. Shared libraries: build all services when shared lib changes. Parallel: matrix strategy, DAG (needs).

## Scenario 3: Database Migrations in CI/CD
You need to run database migrations as part of the deployment pipeline.

**Questions**:
1. How do you manage database schema changes?
2. How do you handle rollback of migrations?
3. How do you test migrations?
4. How do you migrate without downtime?

**Expected approach**: Migration tool: Flyway, Liquibase, Alembic. Forward-only migrations (no rollback — new migration to revert). Test: run migration on staging, verify. Zero-downtime: expand-migrate-contract pattern, backwards-compatible schema changes, multi-phase migrations.

## Scenario 4: Pipeline Security
Your pipeline needs to be secure against supply chain attacks.

**Questions**:
1. How do you verify dependencies?
2. How do you implement SLSA compliance?
3. How do you sign artifacts?
4. How do you enforce pipeline security policies?

**Expected approach**: Dependency verification: checksums, lock files, `npm audit`/`pip audit`. SLSA: levels 1-4 (provenance, hermetic builds, reproducible). Signing: cosign for container images, GPG for packages. Policies: branch protection, required checks, signed commits.

## Scenario 5: Multi-Platform CI/CD
Your application needs to build and test on Linux, Windows, and macOS.

**Questions**:
1. How do you set up multi-platform builds?
2. How do you handle platform-specific dependencies?
3. How do you test on all platforms?
4. How do you use build matrices?

**Expected approach**: Matrix strategy (os: [ubuntu, windows, macos]). Platform-specific: conditional steps, `if: runner.os == 'Windows'`. Docker for consistent environment. Testing: parallel per platform, cross-platform integration tests.

## Key Advanced CI/CD Interview Questions
1. Explain the concept of CI/CD maturity levels.
2. How do you implement feature flags in deployment?
3. What's the difference between continuous delivery and continuous deployment?
4. How do you handle A/B testing in CI/CD?
5. What's the release train model?
6. How do you implement progressive delivery?
7. Explain canary analysis in CI/CD.
8. How do you handle multi-region deployment?
9. What's the blue-green deployment pattern in detail?
10. How do you handle artifact versioning and promotion?

## Whiteboard Challenge
Design an advanced CI/CD platform for an organization with:
- 50+ microservices in a monorepo
- Multi-cloud deployment (AWS + GCP)
- Database migrations
- Security scanning (SAST, DAST, SCA)
- Canary deployments with automated rollback
- Compliance (SLSA Level 3+)

## Follow-up
1. How would you implement deployment-freeze windows?
2. How would you handle incident-driven hotfixes?
3. How would you measure CI/CD effectiveness (DORA metrics)?