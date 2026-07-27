# Amazon — Networking Interview Guide

---

## Role Overview

Amazon networking roles span AWS (VPC, CloudFront, Route53, ELB) and retail operations. The focus is on cloud-native design, operational excellence (knowing AWS services deeply), and troubleshooting under ambiguity.

### Key Services You Must Know

| Service | Category | Why It Matters |
|---------|----------|----------------|
| Amazon VPC | Virtual Network | Foundation of all AWS networking |
| Route53 | DNS | Global routing policies, health checks |
| CloudFront | CDN | Edge caching, origin shield, Lambda@Edge |
| ELB/ALB/NLB | Load Balancing | L4 and L7 distribution, sticky sessions |
| Transit Gateway | Hub Routing | Connecting VPCs and on-prem |
| Direct Connect | Hybrid | Dedicated on-prem to AWS connection |
| VPN (S2S/Client) | Hybrid | Encrypted tunnels |
| PrivateLink | Service Access | Private VPC-to-service connectivity |
| Network Firewall | Security | Stateful inspection, traffic filtering |
| AWS Global Accelerator | Traffic | Anycast routing for global apps |

---

## Interview Rounds (5-6 total)

### 1. Online Assessment (90 min)
**Focus**: Networking troubleshooting scenarios, IP math, design.

Types of questions:
- Subnet calculation and IP allocation problems
- VPC design scenarios
- Troubleshooting connectivity between subnets
- Route table analysis

### 2. Phone Screen (60 min)
**Focus**: AWS networking fundamentals.

Common questions:
- Design a VPC with public/private subnets across 3 AZs.
- How does Route53 routing policy (latency, geolocation, failover) work?
- Compare ALB vs NLB vs CloudFront.
- Walk through a CloudFront cache miss and how it's resolved.

### 3. System Design On-site (60 min)
**Focus**: Multi-region, scalable, highly available architectures.

Common design questions:
- Design a multi-region active-active web app using Route53.
- Design a hybrid network connecting 3 data centers to AWS.
- Design a globally distributed microservices network with service mesh.
- Design a VPC architecture for PCI DSS compliance.

### 4. Leadership Principles (60 min)
**Focus**: Amazon's 16 Leadership Principles applied to networking.

Prepare stories for:
- **Customer Obsession**: "A customer's workload was impacted by a network change. What did you do?"
- **Ownership**: "You notice a recurring network issue no one owns. What do you do?"
- **Dive Deep**: "How did you debug a complex network problem?"
- **Bias for Action**: "You find a security vulnerability in the network design. What's your immediate response?"
- **Deliver Results**: "A network migration is behind schedule. What do you do?"

### 5. Bar Raiser (60 min)
**Focus**: High-bar assessment of all dimensions. Most senior interviewer.

Questions:
- "Tell me about the most technically complex network problem you've solved."
- "What's the biggest mistake you made in network design and what did you learn?"
- "How would you design a zero-trust network on AWS?"

### 6. Technical Deep Dive (optional)

For senior roles:
- BGP with Direct Connect (ASN, VLAN, BFD, prefix advertisement)
- DNS resolution flow through Route53
- CloudFront signed URLs and origin access identity

---

## Must-Know AWS Networking

### VPC Design Patterns

```
Pattern 1: Single VPC with public/private subnets
  └── Internet Gateway → Public Subnet (Web) → NAT → Private Subnet (App/DB)

Pattern 2: Multi-VPC with Transit Gateway
  └── VPC A ─┐
              ├── Transit Gateway ── Direct Connect → On-prem
  VPC B ─┘

Pattern 3: VPC Lattice (service-to-service)
  └── Service Network → Service Associations → VPC attachments
```

### Route53 Routing Policies

| Policy | How It Works | Best For |
|--------|-------------|----------|
| Simple | One record, one answer | Single server |
| Weighted | Distribute % based on weight | Canary deployments |
| Latency | Route to lowest latency | Global user base |
| Failover | Primary + secondary health check | Disaster recovery |
| Geolocation | Route based on user location | Content restrictions |
| Geoproximity | Route based on geographic distance | Traffic steering with bias |
| Multi-value | Return healthy records | Simple LB with health checks |

### ELB Comparison

| Feature | ALB (L7) | NLB (L4) | CLB (Legacy) |
|---------|----------|----------|--------------|
| Protocol | HTTP/HTTPS/gRPC | TCP/UDP/TLS | HTTP/TCP/SSL |
| Target Type | IP, Instance, Lambda | IP, Instance, ALB | Instance |
| Stickiness | Cookie-based | Source IP | Cookie-based |
| WAF | Yes | No | Yes |
| Static IP | No | Yes (per AZ) | No |
| Price | $0.0225/hr + LCU | $0.0063/hr + LCU | Higher |
| Connection | Up to 50K req/s | 100M req/s (TLS) | Moderate |

---

## Sample Answer: VPC Design Question

### Question: "Design a VPC for a three-tier web application."

**Answer Structure:**
1. **Availability**: 3 AZs (us-east-1a, 1b, 1c)
2. **Subnets**: 
   - Public: Web tier (behind ALB)
   - Private: App tier (auto-scaling)
   - Private: DB tier (RDS Multi-AZ)
3. **Routing**:
   - Public: Internet Gateway → direct route
   - Private: NAT Gateway (one per AZ for HA)
4. **Security**:
   - Security Groups: Web (80/443 from 0.0.0.0/0), App (from Web SG), DB (from App SG, port 3306)
   - NACL: Ephemeral ports open, deny all inbound except expected
5. **Cost Consideration**: Use VPC endpoints for S3/DynamoDB to avoid NAT data transfer costs

---

## Key Tips

> "Know the difference between stateful (Security Groups) and stateless (NACLs) — this is ALWAYS asked."
> "Design for failure: never assume a single AZ, NAT Gateway, or Direct Connect will work."
> "Cost matters to Amazon — consider data transfer costs in your designs."
> "VPC Flow Logs are your best tool for debugging connectivity issues."
> "Understand CloudFront origin shield and origin failover deeply."

---

## Recommended Reading

- AWS VPC Documentation (practical, hands-on)
- AWS re:Invent networking sessions (YouTube)
- AWS Networking Cookbook
- "AWS VPC Deep Dive" blog series by AWS Networking team
- Route53 and CloudFront FAQs
- Direct Connect User Guide (BGP, VLAN, prefix limits)

---

*"Own the network, own the customer experience."*
