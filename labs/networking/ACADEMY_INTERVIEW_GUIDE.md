# Networking Academy — Company-Specific Interview Preparation Guide

> 400+ lines covering Google, Amazon, Microsoft, Cloudflare, Cisco, Arista, and general cloud networking roles.

---

## Table of Contents

1. Google (Network Engineer)
2. Amazon (VPC / CloudFront / Route53)
3. Microsoft (Azure Networking)
4. Cloudflare
5. Cisco
6. Arista
7. General Cloud Networking Roles

---

## 1. Google — Network Engineer

### Interview Rounds

| Round | Focus | Duration |
|-------|-------|----------|
| Phone Screen | OSI model, TCP congestion control, BGP fundamentals | 45 min |
| Coding | Network flow algorithms, socket programming, Go/Python | 45 min |
| System Design | Data center fabric, BGP peering, CDN architecture | 60 min |
| Network Deep Dive | MPLS, SDN, load balancing, packet walkthrough | 60 min |
| Googleyness | Behavioral, outage response, cross-team collaboration | 45 min |

### Protocol Knowledge Depth Expected

- **TCP/IP**: Full stack — from ARP to HTTP/2. Must explain congestion control (CUBIC, BBR), slow start, fast retransmit, selective ACKs.
- **BGP**: Path selection algorithm, route reflection, communities, AS path prepending, local preference.
- **DNS**: Anycast deployment, DNSSEC, query resolution flow, TTL strategies.
- **HTTP**: QUIC, HTTP/3, prioritization, head-of-line blocking, stream multiplexing.
- **Load Balancing**: Maglev (consistent hashing), IPVS, L4 vs L7, health check design.
- **SDN**: OpenFlow, Google's Espresso, B4, Andromeda.

### Key Topics to Master

- Google's Jupiter data center fabric (Clos topology)
- gRPC vs HTTP/2 for internal services
- BBR congestion control algorithm
- Espresso (peering edge) and B4 (WAN SDN)
- Anycast for frontend services

### Sample Questions

1. Walk through DNS resolution for www.google.com from a user's browser.
2. Design a load balancer for a global service with 99.99% uptime.
3. Explain how QUIC solves head-of-line blocking in HTTP/2.
4. What happens when a BGP session goes down at a peering edge?
5. How does Google's Maglev load balancer achieve 10M+ connections per second?

---

## 2. Amazon — VPC / CloudFront / Route53 / Network Engineer

### Interview Rounds

| Round | Focus | Duration |
|-------|-------|----------|
| Online Assessment | Network troubleshooting scenarios, IP math | 90 min |
| Phone Screen | AWS networking services, VPC design | 60 min |
| On-site System Design | Multi-region VPC, transit gateway, CloudFront | 60 min |
| Leadership Principles | Customer obsession, ownership in outages | 60 min |
| Bar Raiser | Scalability, security, trade-off decisions | 60 min |

### Protocol Knowledge Depth Expected

- **AWS Networking**: VPC, subnets, route tables, security groups, NACLs, VPC peering, transit gateways, Direct Connect, VPN, PrivateLink.
- **Route53**: DNS routing policies (simple, weighted, latency, failover, geolocation, geoproximity), alias records, health checks.
- **CloudFront**: Edge locations, origin shield, WAF, field-level encryption, signed URLs, cache behavior.
- **ELB/ALB/NLB**: Cross-zone load balancing, sticky sessions, slow start, connection draining.
- **TCP/IP**: IPv4/IPv6 dual stack, NAT gateways, bastion hosts, DHCP options sets.
- **BGP**: When using Direct Connect, BGP session requirements, ASN, VLAN, prefix advertisement.

### Key Topics to Master

- Designing a VPC for high availability across AZs
- CloudFront with Lambda@Edge for request transformation
- Route53 routing policies and failover strategies
- Direct Connect vs VPN vs internet for hybrid networking
- Security group vs NACL (stateful vs stateless)

### Sample Questions

1. Design a multi-region architecture with active-active traffic via Route53.
2. A web app suddenly stops responding. Walk through debugging using AWS tools.
3. How would you migrate on-premises traffic to AWS using Direct Connect?
4. Compare security groups and NACLs. When would you use each?
5. How does CloudFront handle cache misses? Design an origin failover strategy.

---

## 3. Microsoft — Azure Networking

### Interview Rounds

| Round | Focus | Duration |
|-------|-------|----------|
| Phone Screen | Azure virtual networking, DNS, load balancing | 45 min |
| Technical Screen | Network security, VPN gateway, ExpressRoute | 60 min |
| System Design | Hub-spoke topology, Azure Firewall, traffic manager | 60 min |
| Behavioral | Growth mindset, dealing with ambiguous problems | 45 min |
| Deep Dive | SDN, Azure Load Balancer internals | 60 min |

### Protocol Knowledge Depth Expected

- **Azure Networking**: VNet, subnets, VNet peering, Azure DNS, Private DNS, Azure Firewall, NSGs, ASGs, Azure DDoS Protection.
- **Load Balancing**: Azure Load Balancer (L4), Application Gateway (L7), Traffic Manager (DNS), Front Door (global).
- **VPN/ExpressRoute**: Site-to-site VPN, point-to-site, ExpressRoute circuits, ExpressRoute Gateway, FastPath.
- **BGP**: BGP communities in ExpressRoute, Azure route server, route propagation.
- **SDN**: Azure Software Load Balancer (SLB), Virtual Filtering Platform (VFP), Azure SmartNIC.

### Key Topics to Master

- Hub-spoke network design with Azure Firewall as central inspection point
- ExpressRoute with BGP, QoS, and FastPath performance
- Azure Load Balancer distribution modes (5-tuple hash, source IP affinity)
- Azure Front Door with WAF policies and custom domain
- Private Link and service endpoints for PaaS services

### Sample Questions

1. Design a hub-spoke network for a multi-region deployment on Azure.
2. A user on a VPN can't reach resources in an Azure VNet. Walk through debugging.
3. Compare Azure Load Balancer (Standard SKU) vs Application Gateway.
4. How does Azure DDoS Protection detect and mitigate attacks?
5. Design a globally distributed app using Azure Traffic Manager and Front Door.

---

## 4. Cloudflare

### Interview Rounds

| Round | Focus | Duration |
|-------|-------|----------|
| Phone Screen | Anycast, CDN, DNS, DDoS mitigation | 45 min |
| Technical | System design involving edge networking | 60 min |
| Protocol | HTTP/2, HTTP/3, QUIC, TLS 1.3, Argo | 60 min |
| Systems | Performance optimization, caching strategy | 60 min |
| Team Fit | Dogfooding, customer-first approach | 45 min |

### Protocol Knowledge Depth Expected

- **Anycast**: How Cloudflare uses anycast for CDN, DNS, and DDoS absorption.
- **HTTP/3 + QUIC**: Full knowledge of QUIC handshake, 0-RTT, connection migration, stream multiplexing.
- **TLS**: TLS 1.3 handshake, certificate chains, ECH (Encrypted Client Hello), OCSP stapling.
- **DNS**: DNSSEC, CNAME flattening, zone apex, authoritative vs recursive DNS.
- **DDoS**: L3/4/7 DDoS mitigation, rate limiting, WAF rules, bot management.
- **CDN**: Cache keys, purge strategies, Argo Smart Routing, edge caching vs origin caching.

### Key Topics to Master

- Cloudflare's edge network architecture (global network with data centers in 310+ cities)
- How anycast routing works for DDoS absorption
- Workers and edge computing at the network level
- Argo Smart Routing and Tiered Cache
- The Cloudflare Global Anycast Network in detail

### Sample Questions

1. How does anycast help with DDoS mitigation?
2. Walk through a TLS 1.3 handshake with Cloudflare as reverse proxy.
3. Design a caching strategy for a dynamic API fronted by Cloudflare.
4. How would you reduce latency between a global user base and an origin server?
5. Explain the differences between Cloudflare's L3/4 vs L7 DDoS protections.

---

## 5. Cisco

### Interview Rounds

| Round | Focus | Duration |
|-------|-------|----------|
| Phone Screen | Routing protocols, switching, VLANs | 45 min |
| Technical | BGP, OSPF, EIGRP deep dive, troubleshooting | 60 min |
| Design | Enterprise network design, campus fabric, SD-Access | 60 min |
| Automation | Python, Ansible, NETCONF, YANG, REST APIs | 60 min |
| C-suite/Manager | Leadership, strategy, customer engagement | 45 min |

### Protocol Knowledge Depth Expected

- **Routing**: OSPF (LSA types, areas, SPF), EIGRP (DUAL, feasible successors), BGP (path attributes, route reflectors, confederations).
- **Switching**: STP, RSTP, MST, VLAN trunking (802.1Q), VTP, EtherChannel, LACP.
- **SDN**: Cisco ACI, APIC-EM, SD-Access, SDA fabric, LISP, VXLAN.
- **Automation**: NETCONF/YANG, RESTCONF, Cisco NSO, Ansible for network automation.
- **Security**: ACLs, zone-based firewalls, IPSec, DMVPN, GET VPN, TrustSec.

### Key Topics to Master

- Classic 3-tier vs spine-leaf (Clos) topology design
- BGP best path selection algorithm (step-by-step)
- MPLS VPNs (L3VPN, L2VPN, VPLS)
- Cisco ACI (Application Centric Infrastructure) concepts
- DNA Center and intent-based networking

### Sample Questions

1. Walk through the BGP best path selection algorithm in order.
2. A user in VLAN 10 can't reach VLAN 20. Walk through troubleshooting.
3. Design a campus network with redundancy using HSRP/VRRP.
4. Compare OSPF and EIGRP for an enterprise deployment.
5. How would you automate network configuration across 1000 Cisco switches?

---

## 6. Arista

### Interview Rounds

| Round | Focus | Duration |
|-------|-------|----------|
| Phone Screen | Data center networking, EOS, routing | 45 min |
| Technical | BGP in data center, VXLAN, EVPN | 60 min |
| System Design | Leaf-spine fabric design, MLAG, overlay | 60 min |
| Customer/Field | Network automation, troubleshooting, consulting | 60 min |
| Behavioral | Arista culture, customer-driven innovation | 45 min |

### Protocol Knowledge Depth Expected

- **EOS**: Arista Extensible Operating System, SysDB, multi-process architecture, ASIC abstraction.
- **BGP in DC**: BGP unnumbered, BGP as the routing protocol for underlay + overlay.
- **VXLAN/EVPN**: VXLAN encapsulation, BGP EVPN address family, Type-2/Type-3 routes, anycast gateway.
- **MLAG**: Arista MLAG (Multi-chassis Link Aggregation), active-active L2.
- **Leaf-Spine**: Clos fabric designs, ECMP, flowlet switching, congestion management.
- **Automation**: eAPI, CloudVision, Terraform, Ansible, Go, Python SDK.

### Key Topics to Master

- How Arista's EOS architecture differs from Cisco IOS
- VXLAN/EVPN for data center network virtualization
- Arista's CloudVision as a management plane
- BGP unnumbered in the data center (RFC 5549)
- Flowlet switching for better ECMP load balancing

### Sample Questions

1. Design a leaf-spine data center fabric using Arista switches.
2. Walk through EVPN Type-2 and Type-3 route dissemination.
3. How does MLAG work? What are the failure scenarios?
4. Compare flowlet switching vs flow-based ECMP.
5. How would you automate Arista switch provisioning with CloudVision?

---

## 7. Cloud Networking Roles (General)

### Interview Rounds

| Round | Focus | Duration |
|-------|-------|----------|
| Screen | Cloud networking fundamentals, any CSP | 45 min |
| System Design | Multi-cloud networking, transit/routing | 60 min |
| Security | NSGs, Firewalls, PrivateLink, zero trust | 60 min |
| Troubleshooting | Connectivity, latency, packet loss deep dive | 60 min |
| Leadership | Architecture decisions, incident response | 45 min |

### Protocol Knowledge Depth Expected

- **Multi-Cloud**: Cross-cloud connectivity, VPN between AWS/Azure/GCP, dedicated interconnects.
- **Virtual Networking**: VPC, VNet, VPC peering, transit gateway, cross-cloud routing.
- **Service Mesh**: Istio, Envoy, sidecar proxy, mTLS, traffic splitting, circuit breaking.
- **Security**: Zero-trust networking, micro-segmentation, Cilium/eBPF, network policies.
- **Container Networking**: CNI plugins (Calico, Flannel, Cilium, AWS VPC CNI), overlay networks, service mesh.
- **Observability**: Network monitoring, Flow Logs (AWS), NSG Flow Logs (Azure), VPC Flow Logs (GCP), NetFlow, sFlow.

### Key Topics to Master

- Cloud-agnostic VPC/VNet design patterns
- Kubernetes networking (CNI, Service, Ingress, Egress, Network Policies)
- Service mesh comparison (Istio, Linkerd, Consul)
- eBPF and Cilium for next-gen networking
- Observability with distributed tracing and flow logs

### Sample Questions

1. Design a multi-cloud network connecting AWS and GCP with fault tolerance.
2. What is the difference between a CNI plugin and a service mesh?
3. How does eBPF improve Kubernetes networking performance?
4. Troubleshoot a latency issue between two pods in different Kubernetes nodes.
5. Design a zero-trust networking model for a microservices application.

---

## Quick Reference: Protocol Depth by Company

| Protocol/Area | Google | Amazon | Microsoft | Cloudflare | Cisco | Arista |
|---------------|--------|--------|-----------|------------|-------|--------|
| TCP/IP (deep) | ★★★ | ★★ | ★★ | ★★★ | ★★★ | ★★★ |
| BGP | ★★★ | ★★ | ★★ | ★★★ | ★★★ | ★★★ |
| HTTP/2 + HTTP/3/QUIC | ★★★ | ★ | ★ | ★★★ | ★ | ★ |
| DNS (anycast) | ★★★ | ★★★ | ★★ | ★★★ | ★ | ★ |
| Load Balancing | ★★★ | ★★★ | ★★★ | ★★ | ★ | ★ |
| SDN | ★★★ | ★★ | ★★★ | ★ | ★★ | ★★ |
| DDoS Mitigation | ★★ | ★ | ★ | ★★★ | ★★ | ★ |
| VXLAN/EVPN | ★ | ★ | ★ | ★ | ★★ | ★★★ |
| Automation | ★★ | ★★ | ★★ | ★★ | ★★★ | ★★★ |
| Service Mesh | ★★ | ★ | ★ | ★★ | ★ | ★ |

---

## Preparation Checklist

- [ ] Master TCP/IP congestion control algorithms
- [ ] Build a mental model of BGP best path selection
- [ ] Practice designing a multi-region network architecture
- [ ] Understand anycast routing in depth
- [ ] Learn QUIC handshake and HTTP/3 frame structure
- [ ] Practice network troubleshooting walkthroughs aloud
- [ ] Review cloud provider-specific networking services
- [ ] Study load balancing algorithms (consistent hashing, Maglev)
- [ ] Understand CDN caching strategies and origin shielding
- [ ] Prepare outage response stories using STAR method

---

*"The network is the computer." — John Gage*  
*Last updated: 2026*
