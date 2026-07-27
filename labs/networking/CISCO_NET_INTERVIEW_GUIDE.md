# Cisco — Networking Interview Guide

---

## Role Overview

Cisco is the world's largest networking company. Roles cover enterprise routing/switching, data center (ACI, NX-OS), security (Firepower, TrustSec), SD-WAN, Meraki, and network automation (DNA Center, NSO).

### Key Product Lines You Must Know

| Product Line | Area | Why It Matters |
|-------------|------|----------------|
| Catalyst 9000 Series | Enterprise Switching | Campus switching flagship |
| Nexus 9000 Series | Data Center Switching | VXLAN/EVPN, ACI |
| ISR / ASR Routers | WAN/Service Provider | Routing, MPLS, SD-WAN |
| ACI (APIC) | SDN/Data Center | Policy-driven networking |
| DNA Center | Network Management | Intent-based networking |
| NSO (Network Services Orchestrator) | Automation | Multi-vendor orchestration |
| Meraki | Cloud-managed IT | Simplicity, management |
| Firepower | Security (NGFW/IPS) | Next-gen firewall, Threat Defense |

---

## Interview Rounds (5 total)

### 1. Phone Screen (45 min)
**Focus**: CCNP-level routing/switching knowledge.

Common questions:
- STP/RSTP/MST differences and how they work.
- OSPF areas, LSA types, SPF algorithm.
- BGP path selection (full algorithm).
- VLAN trunking — 802.1Q vs ISL.

### 2. Technical — Routing (60 min)
**Focus**: Deep routing protocol knowledge.

Questions:
- Walk through BGP best path selection step by step.
- OSPF neighbor states: Down, Init, 2-Way, ExStart, Exchange, Loading, Full.
- EIGRP DUAL algorithm and feasible successors.
- Route redistribution between OSPF and EIGRP.
- BGP communities, route reflectors, confederations.

### 3. Technical — Switching & DC (60 min)
**Focus**: Campus and data center switching.

Topics:
- STP: Root bridge election, port states, convergence time.
- RSTP: Edge ports, link types, proposal/agreement.
- VXLAN/EVPN: Overlay/underlay, anycast gateway, Type-2/3 routes.
- StackWise vs VPC vs vPC (Cisco vs Nexus MLAG).
- FHRP: HSRP, VRRP, GLBP comparison.

### 4. Design / Architecture (60 min)
**Focus**: Enterprise network design.

Common scenarios:
- Design a campus network for 10,000 users (3-tier vs spine-leaf).
- Design a data center with redundancy and virtualization.
- Design a WAN connecting 50 branches (SD-WAN vs MPLS).
- QoS design for voice/video on a converged network.

### 5. Automation / Behavioral (45 min)
**Focus**: Python, Ansible, NETCONF/YANG, DNA Center.

Questions:
- How would you automate VLAN provisioning across 500 switches?
- Compare NETCONF/YANG vs RESTCONF vs SNMP.
- Write a Python script using netmiko or napalm to collect interface status.
- Describe a time automation prevented a major outage.

---

## Must-Know Technologies

### Routing Protocol Comparison

| Feature | OSPF | EIGRP | BGP |
|---------|------|-------|-----|
| Type | Link-state | Advanced distance-vector | Path-vector |
| Metric | Cost (bandwidth) | Composite (BW, delay, load, reliability) | Path attributes |
| Scalability | Medium (areas) | Medium | Very high |
| Convergence | Fast (LSA flooding) | Fast (DUAL, feasible successors) | Slower |
| Loop Prevention | SPF tree | DUAL algorithm | AS_PATH loop detection |
| Use Case | Enterprise campus | Cisco-only enterprise | Internet, SP, DC |

### STP (Spanning Tree Protocol) Port States

| State | Purpose | Time to Transition |
|-------|---------|-------------------|
| Blocking | No traffic, no MAC learning | Immediately |
| Listening | Listening for BPDUs (no traffic) | 15 sec |
| Learning | Learning MAC addresses (no traffic) | 15 sec |
| Forwarding | Normal operation | — |

### ACI Key Concepts

| Concept | Description |
|---------|-------------|
| Tenant | Logical isolation (customer/org) |
| VRF | Layer 3 routing domain |
| Bridge Domain | Layer 2 broadcast domain |
| EPG | Endpoint Group (policy-based grouping) |
| Contract | Rules defining EPG-to-EPG communication |
| APIC | Application Policy Infrastructure Controller |

---

## Sample Technical Questions

### Question 1: "BGP Path Selection — full algorithm walkthrough."

**Answer:**
1. **Highest Weight** (local to router)
2. **Highest Local Preference** (AS-wide)
3. **Locally originated** > learned (network/aggregate/redistribute)
4. **Shortest AS_PATH** (not including AS_CONFED_SEQUENCE)
5. **Lowest Origin type**: IGP (i) < EGP (e) < INCOMPLETE (?)
6. **Lowest MED** (multi-exit discriminator)
7. **eBGP > iBGP**
8. **Lowest IGP metric to next-hop**
9. **If both eBGP, oldest path wins** (for stability)
10. **Lowest Router-ID**
11. **Lowest neighbor IP address**

### Question 2: "Design a high-availability campus network."

**Answer Structure:**
1. **Access layer**: Catalyst 9300 stack, STP, access VLANs
2. **Distribution layer**: Catalyst 9500, HSRP for gateway redundancy, routed links
3. **Core layer**: Catalyst 9600, L3 routing (OSPF/EIGRP), ECMP
4. **Redundancy**: Dual homing from access to distribution, VSS/StackWise
5. **Management**: DNA Center, NetFlow, SNMPv3
6. **Security**: 802.1X, MACsec, TrustSec segmentation

---

## Automation at Cisco

### NETCONF/YANG Example

```xml
<rpc message-id="101" xmlns="urn:ietf:params:xml:ns:netconf:base:1.0">
  <get-config>
    <source>
      <running/>
    </source>
    <filter type="subtree">
      <interfaces xmlns="urn:ietf:params:xml:ns:yang:ietf-interfaces"/>
    </filter>
  </get-config>
</rpc>
```

### Python + netmiko Example

```python
from netmiko import ConnectHandler
device = {
    'device_type': 'cisco_ios',
    'host': '192.168.1.1',
    'username': 'admin',
    'password': 'password',
}
conn = ConnectHandler(**device)
output = conn.send_command('show ip interface brief')
print(output)
conn.disconnect()
```

---

## Certification Path

| Level | Certification | Key Topics |
|-------|--------------|------------|
| Associate | CCNA | Basics, routing, switching, security, automation |
| Professional | CCNP Enterprise | Advanced routing (ENARSI), SD-WAN, design (ENSLD) |
| Expert | CCIE Enterprise | Infrastructure, design, lab exam (8 hours) |
| Specialist | CCNP Data Center | ACI, NX-OS, VXLAN/EVPN, MDS |

---

## Key Tips

> "Cisco interviews test CCIE-level knowledge even for non-CCIE roles."
> "Know the 'why' behind protocol behavior — not just the 'how'."
> "They value design trade-off discussions — show you can defend architecture decisions."
> "Automation knowledge is rapidly becoming a requirement, not a bonus."
> "Cisco's networking products are the industry standard — know them better than any other vendor."

---

## Recommended Reading

- CCNP Enterprise Certification Study Guide (ENCOR + ENARSI)
- Routing TCP/IP Volumes 1 and 2 (Jeff Doyle)
- Cisco Press: BGP Design and Implementation
- Cisco Live presentations (download from Cisco website)
- Network Programmability with YANG (Cisco Press)
- Cisco ACI Design Guide (white paper)

---

*"Cisco is the network that connects everything."*
