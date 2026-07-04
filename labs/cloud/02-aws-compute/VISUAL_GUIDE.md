# Visual Guide to AWS Compute

## 1. Compute Service Decision Tree

```
Start: Where does your code run?
  │
  ├── Do you want full control over OS and runtime?
  │   └── YES → EC2 (Linux/Windows, SSH access, any software)
  │
  ├── Do you want containers without orchestration complexity?
  │   └── YES → ECS (Fargate = no servers, EC2 = cheaper)
  │
  ├── Do you need Kubernetes compatibility?
  │   └── YES → EKS (standard K8s, managed control plane)
  │
  ├── Is your workload event-driven, short-lived?
  │   └── YES → Lambda (sub-second to 15 min execution)
  │
  ├── Do you just want to upload code and go?
  │   └── YES → Elastic Beanstalk (PaaS for Java)
  │
  └── Is it a batch/CI workload?
      └── YES → Batch (managed HPC, job scheduling)
```

## 2. Lambda Invocation Model

```
                   ┌──────────┐
          ┌───────►│  Warm    │◄────────┐
          │        │  (1-5ms) │         │
          │        └──────────┘         │
          │                             │
          │  5-15 min inactivity        │
          │                             │
     ┌────┴────┐                   ┌────┴────┐
     │  Cold   │──────────────────►│  Warm2  │
     │(200ms-5s)│  Re-initialize    │(warmed) │
     └─────────┘                   └─────────┘

     SnapStart: Cold → 100-200ms (pre-initialized JVM)
```

## 3. ECS Task Placement

```
Cluster: production
  │
  ├── c5.large (us-east-1a) ── Running:
  │   ├── Task: order-service:1 (512 CPU, 1GB RAM)
  │   └── Task: payment-service:3 (256 CPU, 512MB RAM)
  │
  ├── c5.large (us-east-1b) ── Running:
  │   ├── Task: order-service:2 (512 CPU, 1GB RAM)
  │   └── Task: inventory-service:1 (256 CPU, 512MB RAM)
  │
  └── c5.large (us-east-1c) ── Running:
      └── Task: order-service:3 (512 CPU, 1GB RAM)

      Desired: 3 order-service, 1 payment-service, 1 inventory-service
      Strategy: spread across AZs, binpack by CPU
```

## 4. Elastic Beanstalk Architecture

```
Your Code (WAR/JAR/Docker)
       │
       ▼
  Elastic Beanstalk ──► CloudFormation Stack
       │
       ├── Auto Scaling Group ── EC2 instances (Tomcat/Corretto)
       ├── Application Load Balancer (HTTPS, sticky sessions)
       ├── RDS (optional — dev/test tiers)
       └── S3 (deployment artifacts)
```
