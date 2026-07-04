# Refactoring — AWS Networking

## 1. From Single-AZ to Multi-AZ VPC

### Before
```
Single AZ: us-east-1a
├── Public subnet (10.0.1.0/24)
├── Private subnet (10.0.2.0/24)
└── DB subnet (10.0.3.0/24)
Single point of failure at AZ level
```

### After
```
Three AZs: us-east-1a, 1b, 1c
├── Public subnets: 10.0.1.0/24, 10.0.101.0/24, 10.0.201.0/24
├── Private subnets: 10.0.2.0/24, 10.0.102.0/24, 10.0.202.0/24
├── DB subnets: 10.0.3.0/24, 10.0.103.0/24, 10.0.203.0/24
├── NAT Gateway per AZ (HA)
└── ALB cross-zone LB enabled
```

## 2. From Classic Load Balancer to ALB

### Before (CLB)
```
One CLB, one target group, all instances
No path-based routing, no host-based routing
HTTP/1.1 only, no gRPC or WebSocket
```

### After (ALB)
```
One ALB, multiple target groups
Path-based: /api/* → API target group, /static/* → static target group
Host-based: api.example.com → API, www.example.com → web
HTTP/2, WebSocket, gRPC support
```

## 3. From Direct Internet to CloudFront + WAF

### Before
```
Users ──► ALB (public) ──► EC2
Direct internet access, no DDoS protection
Origin IP exposed
```

### After
```
Users ──► CloudFront ──► WAF ──► ALB (internal) ──► EC2
└── Caching at edge (reduces origin load 60%+)
└── DDoS mitigation (AWS Shield)
└── WAF rules: SQL injection, XSS, IP blocking
└── ALB restricted to CloudFront IPs only
```

## 4. From VPC Peering to Transit Gateway

### Before
```
3 VPCs → 3 peer connections (fully meshed)
  Prod ──── Peer ──── Stage
    │                    │
  Peer                Peer
    │                    │
  Shared ──── Peer ──── Dev

6 connections for 4 VPCs; manual route table entries
```

### After
```
            ┌──────────┐
            │  Transit  │
            │  Gateway  │
            └────┬─────┘
     ┌───────────┼───────────┐
     │           │           │
  ┌──▼──┐   ┌───▼───┐   ┌───▼───┐
  │Prod │   │Stage  │   │Shared │
  │ VPC │   │ VPC   │   │ VPC   │
  └─────┘   └───────┘   └───────┘

1 TGW, 3 attachments; centralized routing
```
