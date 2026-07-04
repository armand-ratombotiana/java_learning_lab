# Cloud Deployment Architecture

## AWS Architecture (Microservices)
```
┌─────────────────────────────────────────────────────────────┐
│                         AWS Cloud                            │
│                                                              │
│  CloudFront ─────────────────────────────────────────────┐  │
│     │                                                     │  │
│     ▼                                                     │  │
│  ALB (Application Load Balancer)                          │  │
│     │                                                     │  │
│  ┌──┴─────────────────────────────────────────────────┐  │  │
│  │                EKS Cluster                          │  │  │
│  │  Service A ──▶ Service B ──▶ Service C             │  │  │
│  │  (pod: 3)      (pod: 5)      (pod: 2)              │  │  │
│  │     │              │              │                 │  │  │
│  └─────┼──────────────┼──────────────┼─────────────────┘  │  │
│        │              │              │                     │  │
│        ▼              ▼              ▼                     │  │
│  ┌─────────┐  ┌──────────┐  ┌──────────┐                 │  │
│  │ RDS     │  │ElastiCache│  │ SQS      │                 │  │
│  │(DB)     │  │(Redis)    │  │(Queue)   │                 │  │
│  └─────────┘  └──────────┘  └──────────┘                 │  │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │  │
│  │ S3 (static assets, backups, logs)                  │    │  │
│  └────────────────────────────────────────────────────┘    │  │
└──────────────────────────────────────────────────────────────┘
```

## Multi-Cloud/Hybrid Considerations
- **Portable workloads**: Containers + Kubernetes = portable across clouds.
- **Networking**: VPN/Direct Connect between on-premises and cloud.
- **Identity federation**: SSO across clouds and on-premises (OIDC, SAML).
- **Data gravity**: Keep compute close to data; minimize cross-cloud data transfer.
