# Helm Advanced MOCK_INTERVIEW.md

## Scenario 1: Advanced Templating Patterns
Your Helm chart needs to generate complex Kubernetes resources based on dynamic values.

**Questions**:
1. How do you use `range` to iterate over lists and maps?
2. How do you use `tpl` for dynamic string evaluation?
3. How do you create conditional resource generation?
4. How do you handle type conversion in templates?

**Expected approach**: `range .Values.services` for iteration. `tpl` for rendering values that contain template strings. Conditional: `{{- if .Values.ingress.enabled }}`. Type conversion via `toString`, `toInt`, `fromYaml`, `toYaml`. Use `required` to fail on missing values.

## Scenario 2: Chart Testing
You need to ensure Helm chart quality before releasing.

**Questions**:
1. How do you lint Helm charts?
2. How do you test chart deployment?
3. How do you test chart upgrades?
4. How do you test with different value sets?

**Expected approach**: `helm lint`, `helm template`, `helm unittest` plugin. Chart-testing (ct) tool for integration testing. Upgrade testing via `helm upgrade --install --dry-run`. Multiple value files tested via CI matrix. `helm test` for post-deployment verification.

## Scenario 3: Helm Repository Management
Your team has 20+ charts. You need to manage them efficiently.

**Questions**:
1. How do you host a Helm repository?
2. How do you version charts?
3. How do you sign and verify charts?
4. How do you manage chart dependencies?

**Expected approach**: Host via GitHub Pages, S3, GCS, Azure Storage, or ChartMuseum. Versioning: SemVer2. Signing: `helm gpg sign`, `helm gpg verify`. Dependencies via `Chart.yaml` with version constraints. Use `helm dependency update` and lock file.

## Scenario 4: Helm + OCI Registries
You want to store Helm charts in an OCI-compliant container registry.

**Questions**:
1. How do you push charts to an OCI registry?
2. How do you reference OCI charts in ArgoCD/Flux?
3. What are the benefits over traditional chart repos?
4. How do you handle authentication?

**Expected approach**: `helm package`, `helm push chart.tgz oci://registry/repo`. OCI charts use `oci://` prefix in sources. Benefits: same registry as container images, fine-grained IAM, geo-replication. Auth via `helm registry login`, cloud provider IAM (ECR, ACR, GAR).

## Scenario 5: Helm + Kustomize Integration
When would you use both Helm and Kustomize together?

**Questions**:
1. How do you render Helm charts and post-process with Kustomize?
2. How does ArgoCD support both?
3. What's the order of operations?
4. What are common patterns?

**Expected approach**: ArgoCD: source as Helm chart, post-render with Kustomize. Flux: HelmRelease with Kustomize post-render. Order: Helm renders templates → Kustomize applies patches/transformers → applied to cluster. Patterns: Helm for upstream charts, Kustomize for site-specific overlays.

## Key Advanced Helm Interview Questions
1. Explain Helm's template engine under the hood (Go templates, Sprig functions).
2. How do you write library charts?
3. How do you handle cross-chart dependencies?
4. What's the Helm hooks system and weight ordering?
5. How do you debug complex templates?
6. Explain the `lookup` function in Helm templates.
7. How do you handle CRDs in Helm?
8. What's the best practice for chart naming conventions?
9. How do you handle chart testing for multiple K8s versions?
10. Explain the Helm plugin system.

## Whiteboard Challenge
Design a library chart that provides common infrastructure patterns (service, deployment, ingress, HPA) for 30+ microservices. Include testing, CI/CD, and OCI registry hosting.

## Follow-up
1. How would you handle chart upgrades with breaking changes?
2. How would you implement custom Helm plugins?
3. How would you migrate from Helm v2 to v3?