# Mental Models for AWS Fundamentals

## 1. The Data Center in a Box
Think of a VPC as your own private data center inside AWS. Subnets are server rows, security groups are rack-level firewalls, route tables are network switches, and the internet gateway is your building's internet connection.

```
┌───────────────────────── VPC ─────────────────────────┐
│  ┌───────────┐    ┌───────────┐    ┌───────────┐      │
│  │ Public    │    │ Private   │    │ Private   │      │
│  │ Subnet    │    │ Subnet A  │    │ Subnet B  │      │
│  │ (Web)     │    │ (App)     │    │ (DB)      │      │
│  └─────┬─────┘    └─────┬─────┘    └─────┬─────┘      │
│        │               │               │             │
│   ┌────▼────┐     ┌────▼────┐     ┌────▼────┐        │
│   │ EC2     │     │ EC2     │     │ RDS     │        │
│   │ (Nginx) │     │ (Tomcat)│     │ (MySQL) │        │
│   └─────────┘     └─────────┘     └─────────┘        │
└──────────────────────────────────────────────────────┘
```

## 2. The Onion Model of Security
IAM is layered like an onion. Each layer adds protection:
- **Outer**: IAM organization policies (SCPs)
- **Middle**: IAM user/group policies
- **Inner**: Resource-based policies (bucket policies, trust policies)
- **Core**: Security groups (instance-level) + NACLs (subnet-level)

## 3. The Utility Meter
AWS pricing is like electricity billing:
- **Compute**: Pay per CPU-hour (like kWh)
- **Storage**: Pay per GB-month (like water usage)
- **Network**: Pay per GB transferred (like metered gas)
- Reserved instances = fixed-rate plan; Spot = real-time pricing

## 4. The LEGO Brick Model
AWS services are LEGO bricks. EC2, S3, IAM, VPC are the simplest 2×4 bricks. Everything else — Lambda, RDS, ELB — is a more complex brick that connects to these basics. Master the 2×4 bricks first.

## 5. The Shared Responsibility Spectrum
Security is shared like renting an apartment:
- **Landlord (AWS)**: Building structure, locks on doors, fire suppression
- **Tenant (You)**: Locking your door, securing valuables, not leaving windows open
The more managed the service (S3 > EC2), the more AWS handles.
