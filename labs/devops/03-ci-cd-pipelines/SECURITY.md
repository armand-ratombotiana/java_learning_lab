# CI/CD Security

## Secrets Management
- Use repository secrets (GitHub) or credential plugins (Jenkins).
- Never log or print secrets in pipeline output.
- Rotate secrets regularly.
- Use OIDC for cloud provider authentication (no static keys).

## Supply Chain Security
- **Dependency scanning**: npm audit, Dependabot, Snyk.
- **SBOM generation**: Generate software bill of materials per build.
- **Image signing**: Sign Docker images with cosign.
- **SLSA Framework**: Apply Supply-chain Levels for Software Artifacts.

## Pipeline Security
- **Principle of least privilege**: Scoped tokens for CI jobs.
- **Isolated runners**: Self-hosted runners should be ephemeral.
- **Approval gates**: Require manual approval for production deployments.
- **Branch protection**: Require status checks before merging.

## Common Threats
- Poisoned pipeline execution (injecting malicious code via PR)
- Exposed secrets in build logs
- Dependency confusion attacks
- Compromised base images
- Unauthorized access to production via pipeline tokens
