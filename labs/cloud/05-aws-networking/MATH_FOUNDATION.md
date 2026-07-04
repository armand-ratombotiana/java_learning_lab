# Math Foundation for AWS Networking

## CIDR Math

### VPC Subnet Calculation
```
VPC /16: 10.0.0.0/16 = 65,536 IPs

Subnet size table:
  /24 = 256 IPs  (251 usable — 5 reserved by AWS)
  /23 = 512 IPs  (507 usable)
  /22 = 1024 IPs (1019 usable)
  /21 = 2048 IPs (2043 usable)
  /20 = 4096 IPs (4091 usable)

Reserved IPs per subnet (AWS):
  .0: Network address
  .1: VPC router
  .2: DNS server
  .3: Future use
  .255: Broadcast

Example: 10.0.1.0/24:
  Usable: 10.0.1.4 - 10.0.1.254 (251 addresses)
```

## CloudFront Cost Math

### Data Transfer Pricing
```
US/Europe: $0.085/GB (first 10TB)
Asia:      $0.14/GB (first 10TB)
South America: $0.25/GB (first 10TB)

Requests:
  HTTP: $0.0075/10,000 requests
  HTTPS: $0.01/10,000 requests

Example: 1TB/month, 90% US/Europe, 10% Asia, 5M HTTPS requests
  Transfer: 0.9 × 1024 × $0.085 + 0.1 × 1024 × $0.14
          = $78.34 + $14.34 = $92.68
  Requests: 5,000,000 / 10,000 × $0.01 = $5.00
  Total: $97.68/month
```

## Load Balancer Pricing

```
ALB:
  $0.0225/hour (~$16.43/month)
  $0.008/LCU-hour (LCU = Load Balancer Capacity Unit)
  
  LCU components (use highest):
  - New connections/sec: 25
  - Active connections: 3000
  - Processed bytes: 1GB/hour
  - Rule evaluations: 1000

Example: 100 new connections/sec, 10K active, 500MB/hour processed, 50 rules
  New connections LCU: 100/25 = 4
  Active connections LCU: 10000/3000 = 3.34
  Bytes LCU: 0.5/1 = 0.5
  Rules LCU: 50/1000 = 0.05
  Max: 4 LCU
  Monthly: $16.43 + 4 × 0.008 × 730 = $16.43 + $23.36 = $39.79
```

## Route 53 Latency Math

```
Global latency (approximate):
  US-West → US-East: ~65ms
  US-East → Europe: ~90ms
  US-East → Asia: ~200ms
  US-East → South America: ~120ms
  US-East → Australia: ~170ms

Latency-based routing: Route 53 directs to closest region
Without latency routing: all traffic to us-east-1
  User in Sydney: 170ms RTT
With latency routing: Sydney traffic → ap-southeast-2 (~5ms)
  Improvement: ~165ms reduction
```

## Direct Connect Bandwidth

```
DX speed: 50Mbps, 100Mbps, 200Mbps, 300Mbps, 400Mbps, 500Mbps, 1Gbps, 10Gbps, 100Gbps

Transfer times for 10TB:
  1Gbps: 10 × 8 × 1024 / 1 = 81,920 sec ≈ 22.7 hours
  10Gbps: 2.27 hours
  100Gbps: 13.6 minutes

Monthly cost: 1Gbps = ~$387/month (port + data egress)
vs internet: same data egress via internet = ~$800+ (varies)
```
