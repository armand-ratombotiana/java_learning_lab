# Kubernetes Security

## Cluster Security
- **RBAC**: Least-privilege ServiceAccounts; avoid cluster-admin for apps.
- **Network Policies**: Default-deny ingress/egress; allow only necessary traffic.
- **Pod Security Standards (PSS)**: Enforce privileged, baseline, or restricted policies.
- **Admission Controllers**: Use OPA/Gatekeeper or Kyverno for policy enforcement.
- **etcd encryption**: Enable encryption at rest; limit direct access to etcd.

## Image Security
- Use private registry with image scanning (Trivy, Clair).
- Sign images with cosign; verify before deployment.
- Avoid `latest` tag; use digest (`image@sha256:...`) for immutable images.

## Runtime Security
- **seccomp**: Restrict system calls from containers.
- **AppArmor/SELinux**: Mandatory access control profiles.
- **Read-only rootfs**: Run with `readOnlyRootFilesystem: true`.
- **Drop capabilities**: `securityContext.capabilities.drop: ["ALL"]`.

## Authentication & Authorization
- OIDC integration for user authentication.
- mTLS via Service Mesh (Istio, Linkerd).
- Short-lived tokens for ServiceAccounts with bound service account tokens.
