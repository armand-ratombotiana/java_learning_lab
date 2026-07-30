# Zero Trust — Study Guide

## Core Concepts

### Zero Trust Pillars
1. **Verify explicitly** — authenticate and authorize based on all available data
2. **Use least privilege access** — JIT/JEA, risk-based adaptive policies
3. **Assume breach** — segment access, encrypt all traffic, monitor continuously

### Micro-Segmentation
- **Network segmentation**: firewalls, VPCs, security groups, network policies
- **Workload segmentation**: service mesh (Istio), sidecar proxies
- **Identity segmentation**: user roles, device identity, workload identity

### Continuous Verification
- **Device posture**: OS version, patch level, disk encryption, EDR status
- **User behavior**: location, time, device, access patterns (UEBA)
- **Risk scoring**: low → allow, medium → step-up MFA, high → block

### NIST Zero Trust Architecture (SP 800-207)
- Policy Decision Point (PDP) — makes access decisions
- Policy Enforcement Point (PEP) — enforces decisions
- Policy Administrator (PA) — generates session tokens

## Implementation Checklist
1. Identity is the new perimeter — start with strong identity
2. Map data flows before designing segmentation
3. Phase in Zero Trust incrementally (monitor → isolate → verify)
4. Use telemetry for continuous improvement

## Common Pitfalls
- Trying to implement Zero Trust overnight (it's a journey)
- Neglecting to encrypt internal traffic
- Relying on network location as a trust factor
- Not having visibility before implementing controls
