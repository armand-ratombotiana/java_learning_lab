# Interview Questions — Cloud Networking

## Beginner

Q: What is a VPC and what are its core components?
A: Virtual Private Cloud, logically isolated network with subnets, route tables, gateways, and security groups.

Q: What's the difference between a public and private subnet?
A: Public subnet has a route to an Internet Gateway; private subnet does not.

## Intermediate

Q: How does VPC peering work and what are its limitations?
A: Peering connects two VPCs via private IP. Limitations: no transitive routing, no overlapping CIDRs, no edge-to-edge routing.

Q: What is a Transit Gateway and when would you use it?
A: Central hub for connecting VPCs, VPN, Direct Connect. Use for multi-VPC, multi-region, and hybrid connectivity.

## Advanced

Q: Design a multi-region, multi-VPC network architecture for a global application.
A: Hub-spoke per region with Transit Gateway, inter-region peering or Transit Gateway inter-region, Direct Connect to on-prem, DNS-based global routing.

Q: How does Private Link work and when should you use it instead of VPC peering?
A: Private Link exposes services via VPC endpoints, no peering required, no CIDR overlap issues, cross-account/region connectivity.
