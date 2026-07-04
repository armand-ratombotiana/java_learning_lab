# Flashcards — AWS Networking

## Q: What is the difference between a Security Group and a NACL?
**A**: SG = stateful instance-level firewall; NACL = stateless subnet-level firewall

## Q: What does Route 53's latency-based routing do?
**A**: Routes users to the AWS region with lowest latency for their location

## Q: What is the difference between ALB and NLB?
**A**: ALB = Layer 7 (HTTP/HTTPS); NLB = Layer 4 (TCP/UDP)

## Q: What is CloudFront?
**A**: AWS's Content Delivery Network (CDN) — caches content at 600+ edge locations

## Q: What is a VPC peering connection?
**A**: Direct private connection between two VPCs (no transitive routing)

## Q: What is Transit Gateway?
**A**: Hub-and-spoke network hub connecting many VPCs and on-premise networks

## Q: What is Direct Connect?
**A**: Dedicated fiber connection from on-premise data center to AWS

## Q: What is AWS WAF?
**A**: Web Application Firewall — blocks SQL injection, XSS, bad bots at edge

## Q: What does a Route 53 health check do?
**A**: Monitors endpoint health and removes unhealthy targets from DNS responses

## Q: What is AWS Global Accelerator?
**A**: Improves global app performance by routing traffic over AWS's global network

## Q: What are VPC Flow Logs?
**A**: Capture metadata (not payload) of all network traffic in a VPC

## Q: What is the default VPC?
**A**: Automatically created VPC in each region (1 public subnet per AZ, IGW attached)

## Q: What is an Internet Gateway?
**A**: Horizontally-scaled redundant component allowing VPC-to-internet communication

## Q: What is a NAT Gateway?
**A**: Allows private subnet instances to access internet (outbound only)

## Q: What is AWS PrivateLink?
**A**: Securely expose services across VPCs without peering, internet, or NAT

## Q: What is VPC endpoint?
**A**: Private connection to AWS services (S3, DynamoDB) without internet gateway

## Q: What is the difference between an A record and a CNAME?
**A**: A record maps domain to IP; CNAME maps domain to another domain

## Q: What is an Origin Access Control (OAC)?
**A**: Restricts S3 bucket access to only CloudFront (replaces OAI)

## Q: What is AWS Shield?
**A**: Managed DDoS protection service (Standard = free; Advanced = $3000/month)

## Q: What is the difference between Site-to-Site VPN and Client VPN?
**A**: Site-to-Site connects entire networks; Client VPN connects individual users
