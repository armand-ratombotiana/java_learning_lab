# Container Security — Study Guide

## Core Concepts

### Docker Security
- Run as non-root user (USER directive)
- Use multi-stage builds to minimize image size
- Read-only root filesystem (--read-only)
- Drop all capabilities, add only needed (--cap-drop=ALL)
- Use seccomp profiles to limit syscalls

### Image Security
- Scan images: Trivy, Snyk, Grype
- Use minimal base images (scratch, distroless, alpine)
- Pin exact versions (not :latest)
- Regularly rebuild and scan

### Kubernetes Security
- RBAC: least privilege service accounts
- Pod Security Admission: baseline/restricted
- Network policies: micro-segmentation
- Secrets: encrypt at rest, use external secrets operator

## Implementation Checklist
1. Never run containers as root
2. Scan images in CI/CD pipeline before deployment
3. Enable audit logging at container and cluster level
4. Use admission controllers (OPA/Gatekeeper, Kyverno)
5. Implement network policies to restrict traffic

## Common Pitfalls
- Running containers with --privileged
- Storing secrets in environment variables
- Allowing container escape via mounted Docker socket
- Using default service accounts with cluster-admin
- Not scanning base images regularly
