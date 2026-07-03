# ☁️ Cloud Academy

![Status](https://img.shields.io/badge/status-active-success.svg)
![Labs](https://img.shields.io/badge/labs-8-blue)
![Difficulty](https://img.shields.io/badge/difficulty-intermediate-yellow)
![AWS](https://img.shields.io/badge/AWS-orange)
![IaC](https://img.shields.io/badge/IaC-Terraform-purple)

## Overview

The Cloud Academy provides a structured path from AWS fundamentals through advanced container orchestration and Infrastructure as Code. You will learn core cloud services (compute, storage, database, networking), containerization with Docker, orchestration with Kubernetes, and infrastructure provisioning with Terraform. All examples are Java-centric and aligned with real-world cloud architectures.

## Curriculum

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

**Total estimated time: 28 hours**

## How to Use

Each lab contains:
- **THEORY.md** — In-depth concept explanations
- **CODE_DEEP_DIVE.md** — Annotated code walkthroughs
- **EXERCISES.md** — Practice problems with solutions
- **MINI_PROJECT.md** — Guided hands-on project
- **REAL_WORLD_PROJECT.md** — Full production scenario
- **QUIZ.md** — Knowledge check questions
- **FLASHCARDS.md** — Spaced-repetition review cards

Work through labs sequentially. For each lab, read THEORY.md first, then CODE_DEEP_DIVE.md, complete EXERCISES.md, build the MINI_PROJECT, and finish with the REAL_WORLD_PROJECT. Use FLASHCARDS.md and QUIZ.md periodically for reinforcement.

## Prerequisites

- Basic Java proficiency (Java 11+)
- Understanding of HTTP, REST, and networking fundamentals
- Familiarity with command-line interfaces
- An AWS account (free tier sufficient for most labs)
- Docker Desktop installed (for labs 06–08)

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08
Core    Compute  Storage  DB       Network  Docker   K8s      Terraform
```

Labs 01–05 build cloud fundamentals. Lab 06 introduces containers. Lab 07 orchestrates them. Lab 08 codifies everything as infrastructure.

## Related Academies

- [DevOps Academy](../devops) — CI/CD pipelines, monitoring, GitOps
- [Security Academy](../security) — Cloud security, IAM policies, compliance
- [System Design Academy](../system-design) — Scalability, caching, messaging
- [Distributed Systems Academy](../distributed-systems) — Consensus, replication, distributed storage

## Resources

- [AWS Documentation](https://docs.aws.amazon.com)
- [Docker Docs](https://docs.docker.com)
- [Kubernetes Docs](https://kubernetes.io/docs)
- [Terraform Docs](https://developer.hashicorp.com/terraform/docs)
- [AWS Free Tier](https://aws.amazon.com/free)
- [Well-Architected Framework](https://aws.amazon.com/architecture/well-architected)
