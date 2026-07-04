# Architecture — AWS Networking

## Global Multi-Region Architecture

```
                         ┌─────────────────────────────────┐
                         │  Route 53 (Latency-based)       │
                         │  failover.example.com            │
                         └────────────┬────────────────────┘
                                      │
              ┌───────────────────────┼───────────────────────┐
              │                       │                       │
         ┌────▼────┐            ┌─────▼────┐           ┌─────▼────┐
         │ CloudFront│            │CloudFront │           │CloudFront │
         │ us-east   │            │eu-west    │           │ap-southeast│
         └────┬────┘            └─────┬────┘           └─────┬────┘
              │                       │                       │
         ┌────▼────┐            ┌─────▼────┐           ┌─────▼────┐
         │ ALB     │            │ ALB      │           │ ALB      │
         │ us-east │            │ eu-west  │           │ ap-southeast
         └────┬────┘            └─────┬────┘           └─────┬────┘
              │                       │                       │
         ┌────▼────┐            ┌─────▼────┐           ┌─────▼────┐
         │ ECS     │            │ ECS      │           │ ECS      │
         │ Fargate │            │ Fargate  │           │ Fargate  │
         └────┬────┘            └─────┬────┘           └─────┬────┘
              │                       │                       │
         ┌────▼────┐            ┌─────▼────┐           ┌─────▼────┐
         │ Aurora  │            │ Aurora   │           │ Aurora   │
         │ (local) │◄───────────│ (global) │──────────►│ (local)  │
         └─────────┘            └──────────┘           └──────────┘
                               Primary region
```

## Hub-and-Spoke Network Topology

```
                         ┌──────────────────────────────┐
                         │  AWS Transit Gateway          │
                         │  Route table:                 │
                         │  Spoke → TGW attachment       │
                         │  TGW → Spoke + On-prem        │
                         └────────────┬─────────────────┘
                                      │
        ┌─────────────────────────────┼─────────────────────────────┐
        │                             │                             │
   ┌────▼────┐                  ┌─────▼────┐                  ┌─────▼────┐
   │ VPC Prod│                  │ VPC Stage│                  │ VPC Shared│
   │ (App)   │                  │ (Test)   │                  │ (Logs,    │
   │ 10.0.0.0│                  │ 10.1.0.0/16             │ Monitoring)│
   └─────────┘                  └──────────┘                  │ 10.2.0.0/16
                                                              └─────┬────┘
                                                                    │
                                                              ┌─────▼────┐
                                                              │Direct     │
                                                              │Connect/VPN│
                                                              │ (On-prem) │
                                                              └──────────┘
Benefits:
  - Centralized inspection: all traffic can route through security VPC
  - No peering mesh: TGW manages N:1 connections
  - Easy to add/remove VPCs: single attachment
```

## Zero Trust Network Architecture

```
                   ┌─────────────────────────────────────────┐
                   │  Internet                               │
                   └────────────┬────────────────────────────┘
                                │
                    ┌───────────▼───────────┐
                    │  CloudFront + WAF     │
                    │  + Shield Advanced    │
                    └───────────┬───────────┘
                                │
                    ┌───────────▼───────────┐
                    │  ALB (Internal)       │
                    │  mTLS with client cert│
                    └───────────┬───────────┘
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
         ┌────▼────┐      ┌────▼────┐      ┌─────▼────┐
         │ Service A│      │Service B│      │ Service C │
         │(App)     │      │(App)    │      │ (App)     │
         └────┬────┘      └────┬────┘      └─────┬────┘
              │                 │                 │
         ┌────▼─────────────────▼─────────────────▼────┐
         │  App Mesh / VPC Lattice                      │
         │  Service-to-service mTLS, RBAC, observability│
         └──────────────────────────────────────────────┘

         No VPC peering — all service communication via service mesh
         No internet access — VPC endpoints for AWS services
         Every request authenticated and authorized
```
