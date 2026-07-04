# AWS Networking — Internals

## VPC Internals

### Hyperplane Data Plane
- AWS internally uses "Hyperplane" — distributed data plane for managed services
- NAT Gateway, ALB, NLB, VPC Endpoints, Transit Gateway all use Hyperplane
- Hyperplane workers are horizontally-scaled EC2 instances running custom software
- Traffic enters Hyperplane → packet processing → destination

### Packet Flow Through VPC
```
1. EC2 sends packet via ENA (Elastic Network Adapter)
2. ENA delivers to VPC Switch (distributed virtual switch)
3. VPC Switch evaluates:
   a. Security Group (stateful — caches return path)
   b. Route Table (longest prefix match)
4. If destination is NAT Gateway → packet to Hyperplane worker
5. Hyperplane performs NAT (Source NAT → Elastic IP)
6. Packet to Internet Gateway (IGW is DNS name for a set of AWS routers)
7. Internet Gateway delivers to internet
```

### VPC Flow Logs
- Captures packet metadata (not payload)
- Records: src_ip, dst_ip, src_port, dst_port, protocol, action (ACCEPT/REJECT)
- Published to CloudWatch Logs or S3
- Used for: security analysis, network troubleshooting, compliance

## Route 53 Internals

### DNS Resolution with Route 53 Resolver
```
EC2 in VPC → VPC+2 resolver (e.g., 10.0.0.2) → Route 53 Resolver
  → On-premise DNS (if hybrid) → Route 53 Authoritative → Answer
```

- Route 53 has multiple nameservers per hosted zone (NS record)
- Route 53 authoritative serves from edge locations (low latency)
- Route 53 Resolver (in VPC) handles recursive resolution
- Private hosted zones only resolvable from within VPCs

## CloudFront Internals

### Edge Location Architecture
- 600+ Points of Presence (POPs) in 100+ cities
- Each POP runs CloudFront software on AWS Outposts infrastructure
- POP connects to Regional Origin Cache (larger, more capacity)
- Regional Cache connects to origin (S3, ALB, Custom)
- Cache behavior: TTL-based (Cache-Control, Expires headers)

### Origin Shield
- Additional caching layer (one per region)
- Reduces origin load by consolidating multiple edge requests
- Adds ~10ms latency but reduces origin requests by 80%+

## ALB Internals

### Node Discovery
- ALB has at least 2 nodes per AZ (auto-scaled)
- Each node has an elastic network interface (ENI) in each subnet
- DNS resolves to all ALB node IPs (round-robin)
- Each node handles connections independently
- Node failure → DNS updated, connections drain

### Flow Hashing
- ALB uses flow hashing to route requests to targets
- Same TCP connection → same target (stickiness)
- Additional stickiness: cookie-based (duration configurable)
