# Interview Questions — Network Security

## Beginner

Q: What is the difference between stateful and stateless firewalls?
A: Stateful tracks connection state; stateless evaluates each packet independently.

Q: What is a WAF and what does it protect against?
A: Web Application Firewall protects against application-layer attacks: SQL injection, XSS, CSRF.

## Intermediate

Q: How does SYN flood DDoS attack work and how to mitigate?
A: Attacker sends SYN packets without completing handshake, exhausting connection table. Mitigation: SYN cookies, rate limiting, proxy.

Q: What are the principles of zero trust networking?
A: Never trust always verify, least privilege, micro-segmentation, continuous authentication, device posture check.

## Advanced

Q: Design a defense-in-depth security architecture for a web application.
A: WAF at edge, DDoS scrubbing, stateful firewall, network segmentation (web/app/db tiers), IPS at perimeter, bastion host for admin.

Q: How would you implement micro-segmentation in a Kubernetes environment?
A: Network policies with fine-grained ingress/egress rules, service mesh mTLS, Cilium for eBPF-based segmentation.
