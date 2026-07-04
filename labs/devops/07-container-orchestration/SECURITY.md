# Container Orchestration Security

## Pod Security
- **Security context**: runAsNonRoot, readOnlyRootFilesystem, capabilities drop.
- **Pod Security Admission**: Enforce baseline/restricted policies at namespace level.
- **Seccomp profiles**: Limit system calls available to containers.
- **AppArmor/SELinux**: Mandatory access control profiles per pod.

## Network Security
- **Network policies**: Restrict pod-to-pod communication.
- **mTLS**: Service mesh for encrypted inter-service communication.
- **Egress controls**: Restrict outbound traffic from pods.

## Resource Security
- **Resource quotas**: Prevent resource exhaustion attacks.
- **Limit ranges**: Default resource constraints prevent unbounded resource consumption.
- **Priority classes**: Prevent low-priority pods from starving critical services.

## Deployment Security
- **Image verification**: Verify signatures before deployment.
- **Admission controllers**: Gatekeeper/Kyverno for deployment policy enforcement.
- **SBOM scanning**: Check deployment manifests for vulnerable container images.

## Compliance
- **Audit logging**: Enable Kubernetes audit log for all API requests.
- **Pod security standards**: Map to compliance frameworks (NIST, CIS, SOC2).
- **Immutable infrastructure**: No SSH to nodes; all changes via declarative config.
