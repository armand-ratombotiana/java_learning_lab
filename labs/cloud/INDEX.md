# Cloud Academy

![Status](https://img.shields.io/badge/status-active-success.svg)
![Labs](https://img.shields.io/badge/labs-15-blue)
![Difficulty](https://img.shields.io/badge/difficulty-intermediate%2Fadvanced-red)
![AWS](https://img.shields.io/badge/AWS-orange)
![Azure](https://img.shields.io/badge/Azure-blue)
![GCP](https://img.shields.io/badge/GCP-green)
![IaC](https://img.shields.io/badge/IaC-Terraform-purple)

## Overview

The Cloud Academy provides a structured path from AWS fundamentals through advanced container orchestration, Infrastructure as Code, security, serverless, observability, multi-cloud, and cost optimization. You will learn core cloud services (compute, storage, database, networking), containerization with Docker, orchestration with Kubernetes, infrastructure provisioning with Terraform, and advanced multi-cloud strategies across AWS, Azure, and GCP. All examples are Java-centric and aligned with real-world cloud architectures.

## Curriculum

### Level 1: Core Cloud Foundations (Labs 01-08)

| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 01 | [aws-fundamentals](./01-aws-fundamentals) | EC2, S3, IAM, VPC, global infrastructure | 3h | ★☆☆ |
| 02 | [aws-compute](./02-aws-compute) | Lambda, ECS, EKS, Fargate, Batch, Elastic Beanstalk | 3h | ★★☆ |
| 03 | [aws-storage](./03-aws-storage) | S3 classes, EBS, EFS, lifecycle policies, backup | 3h | ★★☆ |
| 04 | [aws-database](./04-aws-database) | RDS, DynamoDB, ElastiCache, Aurora, Redshift | 3h | ★★☆ |
| 05 | [aws-networking](./05-aws-networking) | Route 53, CloudFront, VPC peering, VPN, Direct Connect | 3h | ★★★ |
| 06 | [docker-containers](./06-docker-containers) | Images, containers, Dockerfile, Compose, networking | 4h | ★★☆ |
| 07 | [kubernetes](./07-kubernetes) | Pods, deployments, services, ingress, Helm, RBAC | 5h | ★★★ |
| 08 | [terraform](./08-terraform) | HCL, state, modules, providers, workspaces, IaC best practices | 4h | ★★★ |

### Level 2: Advanced Cloud & Multi-Cloud (Labs 09-15)

| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 09 | [aws-security](./09-aws-security) | IAM policies, KMS encryption, Security Groups, WAF, Shield, GuardDuty | 4h | ★★★ |
| 10 | [aws-serverless](./10-aws-serverless) | Lambda advanced, Step Functions, EventBridge, SAM framework | 4h | ★★★ |
| 11 | [aws-observability](./11-aws-observability) | CloudWatch, X-Ray, Prometheus on AWS, AMP, AMG | 4h | ★★★ |
| 12 | [azure-fundamentals](./12-azure-fundamentals) | Azure VMs, Blob Storage, Azure SQL, AKS, managed identities | 4h | ★★☆ |
| 13 | [gcp-fundamentals](./13-gcp-fundamentals) | GCP Compute Engine, Cloud Storage, Cloud SQL, GKE, IAM | 4h | ★★☆ |
| 14 | [multi-cloud](./14-multi-cloud) | Multi-cloud strategies, Cloudflare, provider abstraction, portability | 5h | ★★★ |
| 15 | [cloud-cost-optimization](./15-cloud-cost-optimization) | Reserved instances, savings plans, rightsizing, spot instances, FinOps | 4h | ★★★ |

**Total estimated time: 56 hours**

## How to Use

Each lab contains:
- **THEORY.md** — In-depth concept explanations
- **CODE_DEEP_DIVE.md** — Annotated code walkthroughs
- **EXERCISES.md** — Practice problems with solutions
- **BENCHMARK/** — JMH performance benchmarks
- **CHALLENGE/** — Advanced coding challenges
- **DIAGRAMS/** — Architecture and sequence diagrams
- **MINI_PROJECT/** — Guided hands-on project
- **REAL_WORLD_PROJECT/** — Full production scenario
- **SOLUTION/** — Exercise solutions
- **TESTS/** — Additional test scenarios
- **QUIZ.md** — Knowledge check questions
- **FLASHCARDS.md** — Spaced-repetition review cards

Work through labs sequentially. For each lab, read THEORY.md first, then CODE_DEEP_DIVE.md, complete EXERCISES.md, build the MINI_PROJECT, and finish with the REAL_WORLD_PROJECT. Use FLASHCARDS.md and QUIZ.md periodically for reinforcement. Level 2 labs build on Level 1 foundations and introduce multi-provider coverage.

## Prerequisites

- Basic Java proficiency (Java 21+)
- Understanding of HTTP, REST, and networking fundamentals
- Familiarity with command-line interfaces
- An AWS account (free tier sufficient for labs 01-11)
- An Azure subscription (free tier for lab 12)
- A GCP project (free tier for lab 13)
- Docker Desktop installed (for labs 06-08)

## Learning Path

### Level 1: Core Cloud Foundations
```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08
Core    Compute  Storage  DB       Network  Docker   K8s      Terraform
```

Labs 01-05 build cloud fundamentals. Lab 06 introduces containers. Lab 07 orchestrates them. Lab 08 codifies everything as infrastructure.

### Level 2: Advanced Cloud & Multi-Cloud
```
09 ──→ 10 ──→ 11 ──→ 12 ──→ 13 ──→ 14 ──→ 15
Sec     Srvls   Obsrv   Azure   GCP     Multi   Cost
```

Labs 09-11 deepen AWS expertise in security, serverless, and observability. Labs 12-13 introduce Azure and GCP. Lab 14 ties everything together with multi-cloud patterns. Lab 15 covers cost optimization and FinOps across all providers.

## Related Academies

- [DevOps Academy](../devops) — CI/CD pipelines, monitoring, GitOps
- [Security Academy](../security) — Cloud security, IAM policies, compliance
- [System Design Academy](../system-design) — Scalability, caching, messaging
- [Distributed Systems Academy](../distributed-systems) — Consensus, replication, distributed storage

## Resources

- [AWS Documentation](https://docs.aws.amazon.com)
- [Azure Documentation](https://docs.microsoft.com/azure)
- [GCP Documentation](https://cloud.google.com/docs)
- [Docker Docs](https://docs.docker.com)
- [Kubernetes Docs](https://kubernetes.io/docs)
- [Terraform Docs](https://developer.hashicorp.com/terraform/docs)
- [Cloudflare Docs](https://developers.cloudflare.com)
- [AWS Free Tier](https://aws.amazon.com/free)
- [Azure Free Account](https://azure.microsoft.com/free)
- [GCP Free Tier](https://cloud.google.com/free)
- [Well-Architected Framework](https://aws.amazon.com/architecture/well-architected)
- [FinOps Foundation](https://www.finops.org)
