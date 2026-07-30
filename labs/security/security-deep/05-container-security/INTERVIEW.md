# Interview: Container Security

## Q1: Conceptual Understanding
**Q**: What are the key security risks with containers and how do you mitigate them?
**A**: 1) Image vulnerabilities (scan with Trivy, use minimal base). 2) Config drift (immutable infrastructure). 3) Privilege escalation (drop capabilities, non-root user). 4) Cross-container attacks (seccomp, AppArmor). 5) Supply chain (image signing, SBOM).

## Q2: Implementation
**Q**: How do you secure a Kubernetes cluster in production?
**A**: Enable RBAC with least privilege, use Pod Security Admission (restricted profile), deploy network policies for micro-segmentation, encrypt secrets at rest, use OPA/Gatekeeper for policy enforcement, enable audit logging, run CIS benchmark.

## Q3: System Design
**Q**: Design a secure container image build and deployment pipeline.
**A**: Developer pushes code → CI builds image → SAST & image scan → sign with Cosign → store in private registry → CD deploys with admission controller verifying signature and policy → runtime monitoring with Falco + audit logging.

## Coding Challenge
Write a Java utility that validates a Dockerfile for security best practices (non-root user, no latest tag, etc.).
