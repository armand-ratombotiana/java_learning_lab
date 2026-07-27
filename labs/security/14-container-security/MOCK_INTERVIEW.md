# Mock Interview — Container Security

## Interviewer: Cloud Security Engineer (45 min)

**Q1: How do you secure a container from build through runtime — the full lifecycle?**

Candidate: Build phase — use minimal base images (alpine, distroless, scratch). Distroless images contain only the application and its runtime dependencies — no shell, no package manager, no compilers. Scan images for vulnerabilities with Trivy or Grype during CI. Fail the build if critical or high CVEs are found (with an exception process). Pin base image digests (not tags like :latest — digests are immutable, ensuring reproducible builds). Use multi-stage builds: build stage has all tools (JDK, compilers), final stage has only runtime JRE + the compiled artifact. Set a non-root user with USER 10001. Sign images with Cosign and store signatures in an OCI registry. Registry phase — use a private registry (Harbor, ECR, GCR) with policy enforcement: scan on push, require signatures, block images with critical vulns, allow only approved base images. Deployment phase — set Kubernetes Pod Security Standards to "restricted": runAsNonRoot: true, readOnlyRootFilesystem: true, drop all capabilities, allowPrivilegeEscalation: false, seccompProfile: RuntimeDefault. Set CPU/memory resource limits (prevents DoS). Use network policies to restrict pod-to-pod and pod-to-external communication. Runtime phase — use Falco or Tracee for behavioral monitoring. Detect: shell execution in containers, unexpected network connections, file writes outside allowed paths, privilege escalation attempts.

**Q2: What are the risks of running containers as root and how do you enforce non-root?**

Candidate: Risk 1 — kernel escape: the container shares the host kernel. If a root process within a container exploits a kernel vulnerability, it could gain root access on the host. Risk 2 — reduced isolation: many security mechanisms (capabilities, seccomp, AppArmor) are more permissive for root. Risk 3 — container escape via cgroup: root in a container can write to cgroup notify_on_release to escape. Risk 4 — package installation: root can install software, adding attack surface. Enforce non-root: in Dockerfile, add USER 10001 at the end. In Kubernetes pod spec: securityContext: { runAsNonRoot: true, runAsUser: 10001 }. Use Pod Security Admission with the restricted profile — it rejects pods that don't set runAsNonRoot. Verify that your base image doesn't have a user with UID 0 as the default. For Java applications: there's no issue running as non-root for standard web apps. For containers needing some capabilities, add only the specific ones needed (e.g., SETGID, SETUID), never SYS_ADMIN.

**Q3: How does container isolation differ from virtual machine isolation and what are the security implications?**

Candidate: VMs use a hypervisor (Type 1: KVM, Xen, Hyper-V) that provides hardware-level isolation. Each VM runs its own kernel, has dedicated memory (hardware-enforced by MMU), dedicated devices, and complete hardware virtualization. The attack surface for VM escape is small — a hypervisor exploit is rare (though they exist). Containers use kernel namespaces (pid, net, mnt, uts, ipc, user) and cgroups for resource limiting. Containers share the host kernel. A container escape means exploiting a kernel vulnerability from within a namespace. Linux kernel CVEs (CVE-2022-0492, CVE-2024-1086) demonstrate realistic escape vectors. Containers are lighter (faster startup, more density per host) but weaker isolation. Defense: never run untrusted workloads (multi-tenant SaaS, user-submitted code) in plain containers without additional isolation. For untrusted workloads, use: gVisor (userspace kernel that intercepts syscalls), Kata Containers (lightweight VMs with container interface), or Firecracker (microVMs from AWS Lambda).

**Q4: Design a Kubernetes pod security policy using Pod Security Standards.**

Candidate: Use Pod Security Admission (built-in in Kubernetes 1.23+, replaces deprecated PodSecurityPolicy). Apply at the namespace level with labels:
```yaml
apiVersion: v1
kind: Namespace
metadata:
  labels:
    pod-security.kubernetes.io/enforce: restricted
    pod-security.kubernetes.io/audit: restricted
    pod-security.kubernetes.io/warn: restricted
  name: production
```
For workloads that need exceptions, use baseline or custom. Restricted profile enforces: runAsNonRoot: true, runAsUser: must be set and non-zero, seccompProfile: RuntimeDefault or Localhost, capabilities.drop: ["ALL"], allowPrivilegeEscalation: false, readOnlyRootFilesystem: true. For workloads needing exceptions (log shipping daemons, monitoring agents), use baseline (still secure but allows some flexibility) and document the exception with a justification.

**Q5: How do you detect container escapes or runtime attacks in production?**

Candidate: Falco (CNCF graduated) monitors system calls and container events. Example rules: (1) "Terminal shell in container" — detects exec into a container with /bin/sh or /bin/bash. (2) "Unexpected outbound connection" — detects a container connecting to an unknown external IP. (3) "Write below /etc" — detects configuration file modification (possible backdoor). (4) "Read sensitive file untrusted" — detects reading /etc/shadow from within a container. (5) "Symlink created over sensitive file" — possible container escape pattern. Deploy Falco as a DaemonSet on each node. Falco outputs alerts via gRPC, syslog, or stdout. Forward to the SIEM for correlation with other signals (Kubernetes audit logs, network flow logs). For automated response: Falco events trigger a webhook that runs a Kubernetes Job to investigate or isolate the affected pod (network policy isolation, snapshotting the pod for forensics, or killing and scaling). Additional detection: use Tracee for more advanced eBPF-based runtime detection — detects kernel module loading, user namespace creation, and other suspicious behaviors.
