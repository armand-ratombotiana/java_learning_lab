# Helm Security

## Chart Security
- **Verify chart provenance**: `helm verify chart.tgz` with PGP signature.
- **Scan charts**: Use Trivy or Snyk to scan chart dependencies for vulnerabilities.
- **Sign charts**: Use `helm package --sign` with GPG key.
- **OCI trust**: Use OCI registries with content signing (cosign, notation).

## Deployment Security
- **Never hardcode secrets**: Use `--set` with CI secrets, or external secrets operator.
- **RBAC**: Create namespace-scoped ServiceAccounts, not cluster-admin.
- **Namespace isolation**: Install charts in dedicated namespaces.
- **Pod security**: Define securityContext in templates for non-root operation.
- **Network policies**: Include NetworkPolicy templates in charts.

## Repository Security
- **HTTPS only**: Use HTTPS repositories to prevent MITM attacks.
- **Index validation**: Verify index.yaml integrity from repo.
- **Private registries**: Use OCI with authentication for private charts.
- **Vulnerability scanning**: Continuously scan indexed charts.

## Supply Chain Security
- **Software Bill of Materials (SBOM)**: Generate SBOM for chart dependencies.
- **SLSA compliance**: Build charts in CI with provenance attestations.
- **Dependency pinning**: Lock subchart versions with Chart.lock.
