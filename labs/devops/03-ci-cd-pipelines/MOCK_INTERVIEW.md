# CI/CD Pipelines MOCK_INTERVIEW.md

## Scenario 1: Pipeline Optimization
Your CI/CD pipeline takes 45 minutes to complete. Developers are frustrated.

**Questions**:
1. How would you identify bottlenecks in the pipeline?
2. What strategies would you use to reduce build time?
3. Explain parallel stages vs sequential stages.
4. How would you implement caching for dependencies?

**Expected approach**: Analyze stage durations, identify slowest stages. Strategies: parallel test execution, dependency caching (Maven/npm/pip), build matrix, incremental builds, skip CI on docs-only changes (`[skip ci]`), use of buildkit for Docker, stage-level `needs` (DAG pipelines).

## Scenario 2: Deployment Strategy
You need to deploy a critical update with zero downtime.

**Questions**:
1. Compare rolling, blue-green, and canary deployments.
2. When would you choose each strategy?
3. How do you implement automated rollback?
4. How do you verify a successful deployment?

**Expected approach**: Rolling (default, gradual), blue-green (traffic switch), canary (percentage-based). Rollback via `kubectl rollout undo`, Git revert + redeploy, or version pinning. Verification: health checks, smoke tests, canary analysis (prometheus metrics, Flagger).

## Scenario 3: Multi-Environment Pipeline
Design a pipeline that deploys to dev, staging, and production.

**Questions**:
1. How would you manage environment-specific configurations?
2. How do you promote artifacts between environments?
3. How do you handle approval gates for production?
4. How do you prevent environment drift?

**Expected approach**: Configuration via environment variables, ConfigMaps, or external config service. Artifact promotion by tag (docker image: `dev-xxx` → `staging-xxx` → `prod-xxx`). Approval gates via manual approval, release management. Drift detection via Terraform plan, Kubernetes diff, regular reconciliation.

## Scenario 4: Security in CI/CD
A critical vulnerability was found in a dependency used by your application.

**Questions**:
1. Where in the pipeline would you add security scanning?
2. What types of security scans are needed?
3. How do you enforce policy (e.g., no critical vulnerabilities)?
4. How do you handle secrets in CI/CD pipelines?

**Expected approach**: SAST (SonarQube, Semgrep), DAST (OWASP ZAP), SCA (Dependency-Check, Snyk), container scanning (Trivy, Anchore), secret scanning (truffleHog, Gitleaks). Policy enforcement via pipeline failure or manual gate. Secrets via CI/CD platform's secret store, external Vault, never hardcoded.

## Scenario 5: Pipeline as Code
Your team has 20 microservices, each with similar pipelines. There's significant duplication.

**Questions**:
1. How would you DRY up your pipelines?
2. Compare shared libraries (Jenkins), templates (GitLab), composite actions (GitHub).
3. How would you version your pipeline definitions?
4. How do you test pipeline changes before merging?

**Expected approach**: Jenkins Shared Library (Groovy vars), GitLab includes (template YAML), GitHub reusable workflows/composite actions. Pipeline templates versioned alongside code. Testing via `act` (GitHub), `gitlab-ci-local`, Jenkins pipeline unit tests.

## Key CI/CD Interview Questions
1. What's the difference between CI and CD? Give concrete examples.
2. Explain GitFlow vs trunk-based development. Which for CI/CD?
3. How do you handle database migrations in a pipeline?
4. What's the difference between a build artifact and a package?
5. Explain semantic versioning in the context of CI/CD.
6. How do you handle feature flags in deployments?
7. What's canary analysis and how do you implement it?
8. Explain the push vs pull model for deployments.
9. How do you handle pipeline failures — stop, notify, auto-retry?
10. What metrics do you track for CI/CD performance?

## Whiteboard Challenge
Design a CI/CD pipeline for a monorepo containing 5 microservices with shared libraries. Each service has its own build, test, and deploy stages. Some services depend on others.

## Follow-up
1. How would you add automated security scanning?
2. How would you handle a failed deployment mid-rollout?
3. What monitoring would you add to the pipeline itself?