# Network Security (Deep) — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: Explain the Zero Trust security model. How does it differ from perimeter-based security?

**Expected coverage**: Zero Trust: never trust, always verify. No implicit trust based on network location. Every request authenticated, authorized, and encrypted. Micro-segmentation (least privilege per workload), continuous verification (not just at login), assume breach. Perimeter model: trust inside network, hard outside. Zero Trust requires identity-aware proxies, mTLS, policy enforcement at every hop.

**Q2**: What is mTLS (mutual TLS)? How does it work in microservice communication?

**Expected coverage**: Both client and server present certificates, mutual verification. Each service has identity certificate (SPIFFE identity), CA signs all certificates, sidecar proxy (Envoy) or service mesh handles mTLS transparently, automatic certificate rotation (spiffe-rotation API). Prevents unauthorized services from connecting, provides encryption + authentication in one layer.

**Q3**: Compare WAF, IDS, and IPS. Where would you deploy each?

**Expected coverage**: WAF (Web Application Firewall, L7, HTTP-specific rules: SQL injection, XSS, CSRF, rate limiting), IDS (Intrusion Detection System, passive, monitors traffic for known patterns, alerts, no inline blocking), IPS (Intrusion Prevention System, inline, blocks malicious traffic based on signatures/anomalies). Deployment: WAF at edge (Cloudflare, AWS WAF, Azure WAF), IDS/IPS behind firewall or at network segments for internal threat detection.

## Intermediate (3 questions)

**Q4**: Explain DDoS mitigation layers (L3/L4/L7). What tools and strategies are used at each?

**Expected coverage**: L3/L4 (Network/Transport): SYN flood, UDP amplification, NTP amplification. Mitigation: scrubbing centers, rate limiting, SYN cookies, connection tracking limits, BGP blackholing (RTBH). L7 (Application): HTTP flood, slowloris, DNS query flood. Mitigation: WAF rate limiting, CAPTCHA/challenge pages, behavioral analysis, IP reputation, bot management. Cloudflare/AWS Shield/Azure DDoS Protection for managed mitigation.

**Q5**: What is network segmentation? Compare VLANs, VXLANs, and network security groups for segmentation.

**Expected coverage**: VLAN (802.1Q, L2 segmentation, 4096 VLAN limit, limited to single subnet), VXLAN (overlay, 16M VNIs, L2 over L3 underlay, data center virtualization), NSG (cloud-native, stateful L4 filtering, per-subnet/per-NIC in AWS/Azure, rule evaluation order). Segmentation prevents lateral movement in case of breach, reduces attack surface.

**Q6**: Explain the TLS 1.3 handshake. How does it improve over TLS 1.2?

**Expected coverage**: TLS 1.3 handshake: ClientHello → ServerHello + Certificate + Finished → Client Finished (1-RTT). 0-RTT: ClientHello + Early Data. Improvements over 1.2: Removed static RSA/DH key exchange (only forward secrecy via ECDHE), 1-RTT handshake (was 2), removed weak/obsolete cipher suites, encrypted handshake messages (EncryptedExtensions), PSK resumption for 0-RTT, downgrade protection (TLS 1.2 is performed only if server signals downgrade).

## Advanced (2 questions)

**Q7**: Design a zero-trust network architecture for a 100-microservice application on Kubernetes.

**Expected coverage**: Service mesh (Istio: sidecar Envoy proxies, mTLS between all services, authorization policies), eBPF/Cilium (kernel-level network policies without sidecar, WireGuard encryption), network policies (K8s NetworkPolicy for pod-level restrictions). Identity: SPIFFE/SPIRE for service identity, cert rotation. Observability: mTLS metrics, connection logs, policy audit. Gateways: ingress/Egress gateway for external traffic with mTLS to backend.

**Q8**: Your network was breached. An attacker moved laterally from a web server to a database server. How do you redesign to prevent this?

**Expected coverage**: Micro-segmentation (strict network policies: web can only talk to app on port 8080; app can only talk to DB on port 3306; no direct web→DB), mTLS (DB verifies app identity certificate, rejects all other), zero-trust (no implicit trust based on IP), network flow logs (monitor and alert on abnormal connections), bastion/jump host for admin access (audit logged, MFA), service mesh with authorization policies limiting access by workload identity.
