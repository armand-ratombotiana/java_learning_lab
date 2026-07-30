# Interview: Zero Trust

## Q1: Conceptual Understanding
**Q**: Explain the core principles of Zero Trust architecture.
**A**: 1) Never trust, always verify — every access request is authenticated and authorized regardless of location. 2) Assume breach — design for the worst case with micro-segmentation and encryption. 3) Least privilege — grant minimal access needed for the task, use JIT elevations.

## Q2: Implementation
**Q**: How do you implement micro-segmentation in Kubernetes?
**A**: Use Kubernetes Network Policies to restrict pod-to-pod communication by namespace, pod labels, and ports. For service mesh segmentation, use Istio AuthorizationPolicy with mTLS. Apply default-deny ingress/egress and allow only needed flows.

## Q3: System Design
**Q**: Design a Zero Trust access control system for a cloud-native application.
**A**: PDP/PA/PEP architecture: PDP evaluates policies based on user identity, device posture, location, and risk score. PEP (API gateway or sidecar proxy) enforces decisions. PA issues short-lived tokens. Continuous telemetry feeds back to PDP for adaptive policies.

## Coding Challenge
Implement a risk-based access control engine that evaluates user identity, device posture, and behavioral signals.
