# Why AWS Networking Exists

## The Problem
Applications need reliable, secure, and performant network connectivity. Traditional networking requires: router/switch configuration, firewall management, DNS administration, WAN links, and CDN contracts. AWS Networking services abstract these into API-managed, highly available services.

## Why These Services?
- **Route 53**: DNS + health checks + routing policies — the internet's phonebook
- **CloudFront**: CDN for low-latency content delivery + DDoS protection (AWS Shield)
- **VPC Peering**: Connect VPCs privately (no internet, no VPN)
- **Transit Gateway**: Hub-and-spoke network topology for many VPCs/on-prem
- **Direct Connect**: Dedicated fiber from on-premise to AWS (bypass internet)
- **VPN**: Encrypted tunnels over the internet for hybrid cloud

## Java Context
- Route 53: DNS resolution for Java microservices, weighted routing for canary deployments
- CloudFront: Static asset distribution with S3 origin for Java web apps
- VPC Peering: Connect microservice VPCs (services, databases)
- ALB/NLB: Layer 7 / Layer 4 load balancing for Java app instances
