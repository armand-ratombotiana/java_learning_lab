# Reflection — AWS Networking

## Self-Assessment

| Skill | Know | Explain | Teach |
|-------|:----:|:-------:|:-----:|
| VPC design and subnet planning | ☐ | ☐ | ☐ |
| Route 53 routing policies | ☐ | ☐ | ☐ |
| CloudFront configuration and optimization | ☐ | ☐ | ☐ |
| ALB/NLB setup and target groups | ☐ | ☐ | ☐ |
| VPC Peering and Transit Gateway | ☐ | ☐ | ☐ |
| Direct Connect and VPN | ☐ | ☐ | ☐ |
| Network security (SG, NACL, WAF) | ☐ | ☐ | ☐ |
| VPC Flow Logs analysis | ☐ | ☐ | ☐ |

## Journal Prompts

1. How does VPC change the way you design network topology for microservices?

2. When would you choose Transit Gateway over VPC Peering?

3. How does CloudFront improve both performance and security for a web application?

4. What's the most important networking lesson you've learned from this lab?

5. Design a VPC architecture for a Java application that must be PCI-DSS compliant.

## Key Takeaways
- VPC is your private data center in AWS — design it right from the start
- Subnets map to tiers: public (web), private (app), isolated (DB)
- Security groups are stateful; NACLs are stateless — use both for defense-in-depth
- Route 53 offers multiple routing policies for traffic management
- CloudFront reduces latency by 60%+ and absorbs DDoS attacks
- Transit Gateway replaces complex VPC peering meshes
- Always plan CIDR ranges to avoid overlaps (peering, hybrid)
- Network is the foundation — everything depends on it
