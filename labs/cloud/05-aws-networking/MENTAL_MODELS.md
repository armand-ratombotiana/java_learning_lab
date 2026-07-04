# Mental Models for AWS Networking

## 1. The VPC as a Private Data Center

```
Your Physical DC:              AWS VPC:
┌─────────────────────┐       ┌─────────────────────┐
│ Router              │       │ Route Table         │
│ Firewall            │       │ NACL                │
│ Rack 1 (Web)        │       │ Public Subnet       │
│ Rack 2 (App)        │       │ Private Subnet (App)│
│ Rack 3 (DB)         │       │ Private Subnet (DB) │
│ Internet Line       │       │ Internet Gateway    │
│ VPN to Branch       │       │ VPN Gateway         │
│ MPLS to HQ          │       │ Direct Connect      │
└─────────────────────┘       └─────────────────────┘

Every physical component maps to an AWS networking service.
```

## 2. The DNS Hierarchy

```
Root (.) ──► TLD (.com, .org) ──► Domain (example.com)
                                       │
                                  Route 53 hosted zone
                                       │
                            ┌──────────┼──────────┐
                            │          │          │
                      www.example  api.example  db.example
                       │              │            │
                   CloudFront       ALB         RDS
```

## 3. The CDN Cache Layers

```
User request: mysite.com/image.jpg
  ┌────────────────────────────────────────┐
  │  1. Browser cache (HTTP cache headers) │  ── Hit: instant
  ├────────────────────────────────────────┤
  │  2. CloudFront Edge location (POP)     │  ── Hit: ~20ms
  ├────────────────────────────────────────┤
  │  3. CloudFront Regional cache          │  ── Hit: ~50ms
  ├────────────────────────────────────────┤
  │  4. Origin (S3, ALB, Custom)           │  ── Miss: ~200ms
  └────────────────────────────────────────┘
```

## 4. The Hub-and-Spoke (Transit Gateway)

```
              ┌──────────────┐
              │  On-Premise  │
              │  DC          │
              └──────┬───────┘
                     │ Direct Connect / VPN
                     │
              ┌──────▼───────┐
              │  Transit      │
              │  Gateway      │────── Route table (central)
              └──────┬───────┘
                     │
     ┌───────────────┼───────────────┐
     │               │               │
┌────▼────┐    ┌─────▼────┐    ┌─────▼────┐
│VPC Prod  │    │VPC Stage │    │VPC Shared│
│(App)     │    │(Test)    │    │(Logs,    │
│          │    │          │    │ Monitoring│
└──────────┘    └──────────┘    └──────────┘
```
