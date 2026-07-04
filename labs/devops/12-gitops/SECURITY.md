# GitOps Security

## Repository Security
- **Branch protection**: Require PR reviews, status checks, signed commits.
- **Signed commits**: GPG/SSH signed commits for integrity verification.
- **Secrets in Git**: Never store secrets in Git; use External Secrets Operator (SOPS, Vault, SealedSecrets).
- **Repo access**: Least-privilege Git access per team/service.

## Operator Security
- **RBAC**: Least-privilege roles for GitOps operator.
- **Service accounts**: Dedicated SA for each application/team namespace.
- **Network policies**: Restrict operator egress to Git only.
- **Pull model**: Operator pulls config; no CI/CD credentials in cluster.

## ArgoCD Security
- **SSO/OIDC**: Authenticate users via GitHub, GitLab, Okta, Azure AD.
- **RBAC**: Project-scoped roles (admin, developer, viewer).
- **Projects**: Isolate applications by team/environment.
- **Repositories**: Credentials stored encrypted in ArgoCD secrets.

## Flux Security
- **SSO/OIDC**: Via Kubernetes API (OIDC integration).
- **Multi-tenancy**: Tenants in separate namespaces with RBAC restrictions.
- **Notification controller**: Webhook secrets, filtered events.
- **Image automation**: Signed image updates via cosign.

## Supply Chain Security
- **COSIGN**: Sign container images; verify before deployment.
- **SBOM**: Generate and verify software bill of materials.
- **Policy enforcement**: Kyverno/OPA for admission control.
- **Git verification**: Verify commit signatures for automated sync.
