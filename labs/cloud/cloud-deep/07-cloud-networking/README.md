# Lab 07 — Cloud Networking

## Overview
Deep dive into cloud networking: VPC design, subnets, NAT, VPC peering, transit gateway, VPN, Direct Connect, and Private Link.

## Prerequisites
- Java 21+ development environment
- Basic networking concepts (CIDR, routing, NAT)
- Cloud infrastructure familiarity

## What You Will Learn
- Design VPC topologies with public/private subnets and availability zones
- Implement NAT gateway and internet gateway routing
- Model VPC peering and transitive routing via transit gateway
- Build VPN and Direct Connect abstractions
- Create Private Link / VPC Endpoint service models

## Topics Covered
| Topic | Description |
|-------|-------------|
| VPC Design | CIDR planning, subnet sizing, multi-AZ |
| NAT | NAT gateways, NAT instances, outbound connectivity |
| Peering | VPC peering connections, route table entries |
| Transit Gateway | Hub-spoke topology, route propagation |
| VPN | Site-to-site VPN, BGP routing, tunnel redundancy |
| Direct Connect | Dedicated connections, VLANs, BFD |
| Private Link | VPC endpoints, endpoint services, security |

## Java Package
`com.cloud.deep.lab07`
