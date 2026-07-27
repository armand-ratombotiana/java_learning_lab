# Helm MOCK_INTERVIEW.md

## Scenario 1: Chart Design
You need to package your application as a Helm chart for deployment to Kubernetes.

**Questions**:
1. What's the directory structure of a Helm chart?
2. How do you make the chart configurable for multiple environments?
3. How do you handle dependencies on other charts (e.g., PostgreSQL)?
4. How do you test a Helm chart before deployment?

**Expected approach**: Standard chart structure: Chart.yaml, values.yaml, templates/, charts/, templates/NOTES.txt, templates/_helpers.tpl. Environment config via different values files (values-dev.yaml, values-prod.yaml). Dependencies via requirements.yaml / Chart.yaml dependencies field. Testing via `helm lint`, `helm template`, `helm test`, chart-testing (ct) tool.

## Scenario 2: Upgrade Failure
A `helm upgrade` fails mid-way. Some resources were created, some weren't.

**Questions**:
1. How does Helm handle upgrades and rollbacks?
2. How do you recover from a failed upgrade?
3. What's `helm rollback` and how does it work?
4. How do you prevent destructive upgrades?

**Expected approach**: Helm creates a release, stores history in secrets/configmaps. Failed upgrade means partial state. `helm rollback RELEASE REVISION` to go back. `helm history` to see revisions. Prevention: install with `--atomic` (rollback on failure), `--wait` (wait for ready), hooks for pre/post upgrade.

## Scenario 3: Templating Complexity
Your Helm templates are becoming complex with many conditionals and nested loops.

**Questions**:
1. How do you manage template complexity?
2. What's the use of `_helpers.tpl`? Show an example.
3. How do you use `range` and `if` effectively in templates?
4. When should you create subcharts vs a single chart?

**Expected approach**: Extract named templates to `_helpers.tpl`. Use `include` for reusable blocks. Keep templates simple — logic in `values.yaml` structure, not templates. Subcharts for independent components (DB, cache). Parent chart for application + dependencies.

## Scenario 4: Helm Security
You need to ensure Helm charts are secure and follow best practices.

**Questions**:
1. How do you handle secrets in Helm?
2. What's the Helm security model?
3. How do you sign and verify charts?
4. How do you enforce pod security in Helm charts?

**Expected approach**: Secrets: `values.yaml` encrypted via SOPS + Helm Secrets plugin, or Sealed Secrets, or Vault sidecar. Security: least-privilege RBAC, `securityContext` in templates, read-only root filesystem, drop capabilities. Signing: `helm gpg` / `helm sign`, verification with provenance files.

## Scenario 5: Helm in GitOps
You're using ArgoCD with Helm charts.

**Questions**:
1. How does ArgoCD handle Helm charts?
2. How do you configure ArgoCD to use custom values?
3. How do Helm hooks interact with ArgoCD sync waves?
4. What's the difference between ArgoCD managing a Helm chart vs `helm install`?

**Expected approach**: ArgoCD parses Helm charts, renders templates, and applies them. Custom values via `spec.source.helm.values` or `valuesFiles`. Sync waves map to Helm hooks (pre-install, post-install). ArgoCD doesn't use Helm release management — it directly applies manifests.

## Key Helm Interview Questions
1. Explain the Helm architecture (Tiller removed in v3).
2. What's a release in Helm?
3. How does Helm store release history?
4. Explain template functions: `required`, `default`, `toYaml`, `tpl`.
5. What are Helm hooks? Give examples of pre/post install hooks.
6. How do you handle chart versioning?
7. Explain the difference between library charts and application charts.
8. How do you debug a Helm template?
9. What's the `helm test` feature?
10. How do you migrate from Helm v2 to v3?

## Whiteboard Challenge
Design a Helm chart for a 3-tier web application (React frontend, Node API, PostgreSQL database). Include environment-specific configuration, secrets management, health checks, and horizontal pod autoscaling.

## Follow-up
1. How would you package and distribute this chart?
2. How would you version the chart alongside the application?
3. How would you integrate this with a CI/CD pipeline?