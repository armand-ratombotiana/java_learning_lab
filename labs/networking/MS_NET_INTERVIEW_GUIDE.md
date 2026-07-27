# Microsoft — Azure Networking Interview Guide

---

## Role Overview

Microsoft Azure networking roles focus on virtual networking (VNet), hybrid connectivity (ExpressRoute, VPN), global distribution (Front Door, Traffic Manager), and security (Azure Firewall, NSGs, DDoS Protection). The engineering culture emphasizes growth mindset, customer obsession, and cross-team collaboration.

### Key Azure Networking Services You Must Know

| Service | Purpose | Why It Matters |
|---------|---------|----------------|
| Azure Virtual Network (VNet) | Virtual Private Cloud | Foundation of all Azure networking |
| VNet Peering | Connect VNets | Cross-region connectivity |
| Azure VPN Gateway | Site-to-site + Point-to-site | Hybrid connectivity |
| ExpressRoute | Dedicated private WAN | High-bandwidth, low-latency hybrid |
| Azure Load Balancer | L4 Load Balancing | 100M+ concurrent sessions |
| Application Gateway | L7 Load Balancing / WAF | HTTP/s load balancing |
| Azure Front Door | Global HTTP/s load balancer + CDN | Multi-region traffic management |
| Traffic Manager | DNS-based traffic routing | Global DNS routing policies |
| Azure DNS | Domain hosting | Private DNS, resolver, forwarding |
| Azure Firewall | Stateful firewall as a service | Central security inspection |
| Network Security Groups (NSG) | Distributed firewall | Per-subnet/per-NIC filtering |
| Azure DDoS Protection | DDoS mitigation | L3/4 protection, adaptive tuning |
| Private Link | Private access to PaaS | Private connectivity, data exfiltration prevention |
| Service Endpoints | Private VNet access to Azure services | Free alternative to Private Link |
| Azure Route Server | Route exchange with NVAs | Dynamic routing with VNet appliances |

---

## Interview Rounds (5 total)

### 1. Phone Screen (45 min)
**Focus**: Azure networking fundamentals.

Common questions:
- How does VNet peering work? What are the limitations?
- Compare NSGs and Azure Firewall.
- What is the difference between ExpressRoute and VPN Gateway?
- How does Azure Load Balancer distribute traffic?

### 2. Technical Screen (60 min)
**Focus**: Deeper networking, hands-on architecture.

Topics:
- Design a hub-spoke network with Azure Firewall.
- BGP routing with ExpressRoute (circuit, gateway, BGP session).
- Azure Front Door routing rules and WAF policies.
- Private Link vs Service Endpoints — when to use each.
- Troubleshoot connectivity: NSG flow logs, Azure Monitor, Network Watcher.

### 3. System Design On-site (60 min)
**Focus**: Global, resilient, secure network architecture.

Design questions:
- Design a multi-region active-passive architecture using Traffic Manager + Front Door.
- Design hybrid connectivity for 3 data centers connecting to multiple VNets.
- Design a zero-trust network for a cloud-native app (no public IPs).
- Design for PCI DSS compliance — network segmentation and inspection.

### 4. Deep Dive — Azure Networking Internals (60 min)
**Focus**: How Azure networking works under the hood. For senior roles.

Topics:
- Azure Software Load Balancer (SLB) — Virtual Filtering Platform (VFP)
- VFP flow processing: how flows are created/removed at the host level
- How Accelerated Networking works (SR-IOV)
- Azure SmartNIC architecture
- VNet encryption — how Azure implements encryption in transit

### 5. Behavioral / Growth Mindset (45 min)
**Focus**: Microsoft culture, cross-team collaboration, growth.

Questions:
- "Tell me about a time you had to learn a new networking technology quickly."
- "Describe a project where you collaborated across multiple teams."
- "What's the biggest networking challenge you've solved?"
- "How do you handle ambiguity when requirements aren't clear?"
- Microsoft-specific: "How do you apply growth mindset to your work?"

---

## Must-Know Azure Networking Patterns

### Hub-Spoke Topology

```
               ┌──────────────────────────────────────┐
               │           Hub VNet (10.0.0.0/16)     │
               │  ┌──────────────────────────────────┐ │
               │  │  Azure Firewall (Central Ingress)  │ │
               │  └──────────────────────────────────┘ │
               │  ExpressRoute Gateway / VPN Gateway    │
               └──────────┬───────────────────────────┘
                          │
         ┌────────────────┼────────────────┐
         │                │                │
   ┌─────┴─────┐   ┌─────┴─────┐   ┌─────┴─────┐
   │ Spoke VNet│   │ Spoke VNet│   │ Spoke VNet│
   │ 10.1.0/16 │   │ 10.2.0/16 │   │ 10.3.0/16 │
   └───────────┘   └───────────┘   └───────────┘
```

**Key Design Decisions:**
- Azure Firewall in the hub for all egress traffic inspection
- ExpressRoute Gateway for on-prem connectivity
- VNet peering between hub and spokes
- User-defined routes (UDRs) for forced tunneling
- Private DNS zones in the hub with linking

### ExpressRoute BGP Details

| Parameter | Value / Notes |
|-----------|---------------|
| ASN (Microsoft) | 12076 |
| VLAN ID | Customer-provided (802.1Q) |
| BGP Session | Primary + Secondary (two MSEE routers) |
| BFD | Supported (fast failover ~3 seconds) |
| Prefix Limit | 1000 IPv4 prefixes per BGP session |
| QoS | DSCP marking for voice/video/data |
| FastPath | Bypasses gateway for higher throughput (max 100 Gbps) |

---

## Azure Load Balancer Deep Dive

### SLB (Software Load Balancer)

```
SDN Controller (Network Controller)
         │
         ▼
VFP (Virtual Filtering Platform) — on Hyper-V host
         │
         ▼
Flow Rules (match: 5-tuple, action: forward/NAT/drop)
```

**Distribution Modes:**
1. **5-tuple hash** (src/dst IP, src/dst port, protocol) — default
2. **2-tuple hash** (source IP, destination IP)
3. **Source IP affinity** (session stickiness)

### Azure Front Door vs Traffic Manager

| Feature | Front Door (L7) | Traffic Manager (DNS) |
|---------|-----------------|----------------------|
| Protocol | HTTP/HTTPS | DNS only |
| Routing | Path-based + global | DNS-based |
| TLS | SSL termination + re-encrypt | Not applicable |
| WAF | Integrated | No |
| Caching | Yes (CDN capability) | No |
| Latency-based | Yes (closest backend) | Yes (DNS latency probes) |
| Fast failover | ~3-5 seconds | ~30-60 seconds (DNS TTL) |

---

## Sample Design Question: Multi-region Architecture

### Question: "Design a multi-region active-active application on Azure."

**Answer Structure:**

1. **Regions**: US East 2 + West Europe (paired regions)
2. **Traffic Routing**: Front Door → latency-based routing to nearest region
3. **Per Region**:
   - VNet with 3 subnets (Web, App, DB)
   - Application Gateway (internal, WAF enabled)
   - Azure Load Balancer for internal traffic
4. **Database**: Cosmos DB (multi-region writes) or SQL DB (geo-replication)
5. **Failover**: Front Door health probes → auto-failover per backend
6. **Security**: NSGs, Azure Firewall in hub, Private Link for DB
7. **Monitoring**: Azure Monitor, Network Watcher, Traffic Analytics

---

## Key Tips

> "Microsoft cares about 'why' as much as 'what' — explain your design decisions."
> "Understand VFP (Virtual Filtering Platform) — it's the heart of Azure networking."
> "ExpressRoute BGP details are fair game for senior roles."
> "Private Link vs Service Endpoints is a common comparison question."
> "Growth mindset stories matter — show how you learn from mistakes."
> "Azure-specific terms: VNet, NSG, UDR, ASG, Azure Firewall, WAF, Front Door."

---

## Recommended Reading

- Azure Networking documentation (docs.microsoft.com/en-us/azure/networking/)
- Azure Networking Team Blog
- Microsoft Learn: Networking modules (AZ-700 path)
- "Inside Azure Networking" — Virtual Filtering Platform (VFP) deep dive
- Azure architecture center: Reference architectures (hub-spoke, N-tier)
- AZ-700: Designing and Implementing Microsoft Azure Networking Solutions
- Azure Networking: Learn from Microsoft's Networking Architects (Channel 9 / YouTube)

---

*"Empower every network on the planet to achieve more."* — inspired by Microsoft's mission
