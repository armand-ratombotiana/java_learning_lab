# Networking Roles — Interview Process Guide

> 300+ lines comparing Network Engineer, SRE, and Cloud Network Architect interview processes.

---

## Table of Contents

1. Network Engineer Interviews
2. Site Reliability Engineer (SRE) — Networking Focus
3. Cloud Network Architect Interviews
4. Role Comparison Matrix
5. Resume and Experience Mapping

---

## 1. Network Engineer Interviews

### Typical Process

| Step | Description | Duration | Preparation |
|------|-------------|----------|-------------|
| Recruiter Screen | Background check, location, salary expectations | 20 min | Have a one-paragraph summary of your networking experience |
| Technical Phone | Fundamentals: OSI model, TCP/UDP, routing protocols | 45-60 min | Review CCNA/CCNP level material |
| Coding Round | Network programming (socket, tcpdump parsing) | 45 min | Practice Python/Go socket programming |
| Deep Dive Protocol | Expert-level BGP/OSPF/IS-IS/VXLAN | 60 min | Know protocol details inside and out |
| System Design | Design a network for a specific requirement | 60-90 min | Draw leaf-spine topologies, MPLS, VXLAN |
| Whiteboard / Diagram | Troubleshooting scenario, packet walkthrough | 45 min | Be ready to trace a packet end-to-end |
| Hiring Committee | Final feedback aggregation | — | It's out of your hands |

### Key Focus Areas

- **CCNP/JNCIP level knowledge**: Routing protocols, switching, MPLS, VPN, QoS
- **Hands-on configuration**: Show config examples for BGP, OSPF, VLANs
- **Automation**: Ansible, Python, NETCONF, YANG, Jinja2 templates
- **Troubleshooting methodology**: Show a repeatable, logical approach
- **Vendor specifics**: Cisco IOS XE/NX-OS, Arista EOS, Juniper JunOS

### Scoring Criteria

| Criterion | Weight | Description |
|-----------|--------|-------------|
| Protocol Knowledge | 30% | Depth of understanding, not just memorization |
| Problem Solving | 25% | Structured approach to network problems |
| Hands-on Experience | 20% | Real deployment stories with measurable outcomes |
| Design Thinking | 15% | Trade-off analysis, scalability considerations |
| Communication | 10% | Clear explanation of complex networking concepts |

### Sample Interview Questions

1. "Walk me through what happens when you type google.com in a browser."
2. "A user can't reach a server. How do you troubleshoot?"
3. "Design a BGP peering strategy for a multi-homed enterprise."
4. "How would you migrate 100 routers to a new OS without downtime?"
5. "Compare OSPF and EIGRP for a 500-site VPN deployment."

---

## 2. Site Reliability Engineer (SRE) — Networking Focus

### Typical Process

| Step | Description | Duration | Preparation |
|------|-------------|----------|-------------|
| Recruiter Screen | SRE role specifics, on-call expectations | 20 min | Understand on-call rotations and incident management |
| Coding Round | Algorithms, data structures, concurrency | 45-60 min | Practice medium-level LeetCode (Python/Go) |
| System Design | Design a highly reliable network system | 60 min | Focus on reliability: redundancy, failover, health checks |
| Networking Deep Dive | TCP, DNS, HTTP, load balancing, CDN | 60 min | Narrower focus than NE but deeper on infra side |
| Debugging | Real incident scenarios, RCA, mitigation | 45 min | Practice postmortem writing and timeline reconstruction |
| Behavioral | Team collaboration, incident response, blamelessness | 45 min | Prepare SRE-specific stories |
| On-site Presentation | Deep dive into a past incident or project | 60 min | Build a 20-minute slide deck on a networking problem |

### Key Focus Areas

- **Reliability engineering**: SLA, SLO, SLI, error budgets, alerting
- **Incident response**: On-call best practices, escalation paths, postmortems
- **Automation at scale**: Infrastructure as code (Terraform, CloudFormation), config management
- **Monitoring**: Prometheus, Grafana, NetFlow, sFlow, latency histograms
- **Capacity planning**: Bandwidth forecasting, headroom management, traffic engineering
- **Programming**: Go or Python for tools, automation, and tooling

### Scoring Criteria

| Criterion | Weight | Description |
|-----------|--------|-------------|
| Reliability Mindset | 25% | Thinking about failure modes, redundancy, graceful degradation |
| Coding Ability | 20% | Production-quality code, networking tools experience |
| Incident Handling | 20% | Rapid triage, clear communication, blameless postmortems |
| System Design | 20% | Reliable, scalable, observable network systems |
| Data-driven Decision | 15% | Using metrics to drive operational improvements |

### Sample Interview Questions

1. "Design a global load balancer with 99.999% uptime."
2. "You get paged at 3 AM for high latency on a critical service. Walk through your runbook."
3. "How would you measure and improve DNS resolution time across regions?"
4. "Design a monitoring system that detects BGP route hijacking."
5. "Your CDN is failing health checks. How do you diagnose and mitigate?"

---

## 3. Cloud Network Architect Interviews

### Typical Process

| Step | Description | Duration | Preparation |
|------|-------------|----------|-------------|
| Recruiter Screen | High-level experience, certifications | 20 min | Articulate your cloud networking specialization |
| Cloud Design | Multi-region, multi-cloud, hybrid networking | 60 min | Know reference architectures for AWS/Azure/GCP |
| Security Deep Dive | Network segmentation, zero-trust, encryption | 60 min | Understand security groups, NACLs, firewalls, WAF |
| Infrastructure as Code | Terraform for networking, CI/CD for infra | 45 min | Write some Terraform for VPCs on the spot |
| Leadership | Decision-making, trade-offs, stakeholder management | 45 min | Show influence without authority |
| Case Study | Architect a solution for a complex business problem | 90 min | Combine networking, security, and cost considerations |

### Key Focus Areas

- **Cloud networking**: VPC, VPN, Direct Connect, Transit Gateway, Cloud WAN, CDN
- **Hybrid networking**: Connecting on-prem to cloud with redundancy
- **Network security**: WAF, DDoS protection, SSL/TLS termination, identity-aware proxy
- **Automation**: Terraform, CloudFormation, CDK, Pulumi
- **Cost optimization**: Data transfer costs, NAT gateway pricing, CloudFront vs direct connection
- **Migration**: Lift-and-shift, re-platform, re-architect networking strategies

### Scoring Criteria

| Criterion | Weight | Description |
|-----------|--------|-------------|
| Cloud Architecture | 30% | Deep knowledge of at least one major cloud provider's networking |
| Security | 20% | Zero-trust, compliance, encryption in transit/at rest |
| Automation | 15% | Infrastructure as code, GitOps for networking |
| Cost & Scale | 15% | Understanding of cloud networking cost drivers |
| Leadership | 10% | Driving architectural decisions across teams |
| Communication | 10% | Clear articulation of complex architectures to non-experts |

### Sample Interview Questions

1. "Design a multi-region active-active architecture on AWS."
2. "How would you connect 5 data centers to 2 cloud providers?"
3. "Design a zero-trust network for a 200-microservice application."
4. "Your cloud egress costs are growing 30% month-over-month. What do you do?"
5. "Migrate a 500-server on-prem application to AWS with minimal downtime."

---

## 4. Role Comparison Matrix

| Dimension | Network Engineer | SRE (Networking) | Cloud Network Architect |
|-----------|-----------------|------------------|------------------------|
| **Primary Focus** | Packet-level networking | Reliability at scale | Cloud-native architecture |
| **Depth vs Breadth** | Deep (protocols, hardware) | Moderate depth, broad infra | Broad (cloud, hybrid, security) |
| **Coding Required** | Scripting (Python, Bash) | Production engineering (Go, Python) | IaC (Terraform, CDK) |
| **On-call** | Occasional (network incidents) | Regular (rotations) | Rare (advisory) |
| **Hardware Knowledge** | High (routers, switches, optics) | Low | Low |
| **Cloud Expertise** | Moderate | Moderate | High |
| **Security Focus** | ACLs, VPN, firewall policies | TLS, mTLS, DDoS | Zero-trust, WAF, security groups |
| **Certifications** | CCNP, JNCIP, CCIE | CKAD, AWS SA, GCP NE | AWS SA Pro, Azure NE, Google NE |
| **Typical Title Progression** | NE > Sr. NE > Network Architect | SRE > Staff SRE > Principal SRE | Cloud Architect > Principal Architect |
| **Leverage in Interview** | Protocol expertise, troubleshooting | Reliability mindset, automation | Architectural breadth, cost awareness |

### Salary Band Guidance (US-based, approximate)

| Experience | Network Engineer | SRE | Cloud Network Architect |
|------------|-----------------|-----|----------------------|
| 0-3 years | $80k-$120k | $110k-$150k | $100k-$130k |
| 3-7 years | $110k-$160k | $140k-$190k | $140k-$180k |
| 7+ years | $150k-$210k | $180k-$260k | $180k-$280k |

*Salaries vary significantly by location, company, and total compensation structure.*

---

## 5. Resume and Experience Mapping

### For Network Engineer Roles

**What to emphasize:**
- Number of routers/switches managed, protocol deployments led
- Complex troubleshooting cases with clear outcomes
- Automation scripts that saved time/reduced errors
- Peak traffic handled (Gbps, connections per second)
- BGP peering relationships, ASNs, IXPs
- Certifications (CCNP/CCIE, JNCIP/JNCIE)

**Sample resume bullet:**
> "Designed and deployed BGP-based DCI interconnect across 4 data centers supporting 400 Gbps of traffic; reduced failover time from 30s to under 3s using BFD and route summarization."

### For SRE (Networking Focus)

**What to emphasize:**
- Incident response metrics (MTTR, MTTD) and improvements
- Monitoring/observability systems built or maintained
- Automation reducing toil (measured in hours saved/month)
- Capacity planning and traffic engineering
- Infrastructure as code (Terraform, K8s, Helm)
- Programming languages (Go, Python, Rust)

**Sample resume bullet:**
> "Reduced network-incident MTTR from 45 min to 12 min by building an automated packet capture and analysis pipeline using Go and sFlow."

### For Cloud Network Architect Roles

**What to emphasize:**
- Cloud migration projects (size, timeline, complexity)
- Multi-region/multi-cloud architectures designed
- Security architecture decisions (compliance, zero-trust)
- Cost optimization initiatives (data transfer, NAT, CDN)
- IaC codebases (Terraform modules, CloudFormation stacks)
- Architecture review leadership across teams

**Sample resume bullet:**
> "Architected a multi-region AWS network for a fintech SaaS company supporting PCI DSS compliance; reduced month-over-month data transfer costs by 40% through strategic CloudFront and Direct Connect optimization."

---

## Practice Schedule (4-week prep)

| Week | Focus | Activities |
|------|-------|------------|
| 1 | Foundation | Review TCP/IP, OSI, routing protocols; practice 3 troubleshooting walkthroughs |
| 2 | Deep Dive | Master BGP path selection, TCP congestion control; build a lab in EVE-NG/GNS3 |
| 3 | System Design | Design 5 network architectures; practice whiteboarding with recording |
| 4 | Mock Interviews | Do 3+ mock interviews with peer feedback; focus on communication |

### Daily Routine

- Morning: 1 networking question (30 min)
- Afternoon: Review one protocol in depth (45 min)
- Evening: Mock whiteboard session (30 min)

---

*"There is no 'perfect' network — only trade-offs you can explain."*
