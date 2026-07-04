# Interview Questions — AWS Networking

## Beginner

**Q1**: What's the difference between a security group and a NACL?

**Q2**: What is Route 53 and what routing policies does it support?

**Q3**: How does CloudFront improve application performance?

## Intermediate

**Q4**: Explain VPC Peering and its limitations.

**Q5**: How does an ALB differ from an NLB?

**Q6**: What is Direct Connect and when would you use it over VPN?

**Q7**: Explain the difference between public and private subnets.

## Advanced

**Q8**: Design a multi-region, multi-VPC architecture for a global Java application.

**Q9**: How does AWS Transit Gateway simplify network management compared to VPC peering?

**Q10**: You're getting 503 errors from your ALB. Walk through the troubleshooting steps.

**Q11**: Design a network architecture that isolates development, staging, and production environments while allowing them to share services (monitoring, CI/CD).

## Sample Answers

**A1**: Security groups are stateful instance-level firewalls (return traffic auto-allowed). NACLs are stateless subnet-level firewalls (both directions need explicit rules). SGs evaluate all rules together; NACLs process in order (first match).

**A2**: Route 53 is a DNS service with: Simple (single record), Weighted (A/B testing), Latency (best region), Geolocation (region by user location), Failover (active-passive DR), Multi-value (up to 8 healthy records).

**A3**: CloudFront caches content at 600+ edge locations worldwide, reducing latency from 100-300ms to 10-50ms. It also provides DDoS protection (Shield), SSL termination at edge, and origin offloading.

**A4**: VPC Peering connects two VPCs privately using AWS internal network. Limitations: no transitive peering (A-B-C: A can't reach C through B), no overlapping CIDR, max 125 peering connections per VPC. For many VPCs, Transit Gateway is better.

**A5**: ALB is Layer 7 (HTTP/HTTPS) — path/host routing, SSL termination, WebSocket, gRPC, sticky sessions. NLB is Layer 4 (TCP/UDP) — ultra-low latency (~0.1ms), static IPs, PrivateLink integration. Use ALB for HTTP apps; NLB for TCP/UDP, VoIP, or extreme latency sensitivity.

## Key Topics for AWS Networking Exam
- VPC design and components (subnets, route tables, IGW, NAT, SG, NACL)
- VPC endpoints (Gateway and Interface endpoints)
- Route 53 routing policies and health checks
- CloudFront behaviors, origins, TTL, invalidations
- ALB vs NLB vs Gateway Load Balancer
- VPN (Site-to-Site and Client VPN)
- Direct Connect (dedicated fiber vs VPN)
- Transit Gateway (hub-and-spoke vs peering)
- AWS Global Accelerator
- VPC Flow Logs and network monitoring
