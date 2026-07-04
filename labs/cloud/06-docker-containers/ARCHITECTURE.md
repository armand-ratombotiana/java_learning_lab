# Architecture — Docker

## Microservice Architecture with Docker Compose

```
                            ┌──────────────┐
                            │  API Gateway  │
                            │  (Nginx)      │
                            └──────┬───────┘
                                   │
              ┌────────────────────┼────────────────────┐
              │                    │                    │
         ┌────▼────┐         ┌─────▼─────┐        ┌─────▼─────┐
         │ Service  │         │ Service   │        │ Service   │
         │ A        │         │ B         │        │ C         │
         │(Spring)  │         │(Spring)   │        │(Spring)   │
         │ 8081     │         │ 8082      │        │ 8083      │
         └────┬────┘         └─────┬─────┘        └─────┬─────┘
              │                    │                    │
              └────────────────────┼────────────────────┘
                                   │
                            ┌──────▼───────┐
                            │  Shared Data  │
                            │  PostgreSQL   │
                            └──────────────┘

Services communicate via REST/gRPC over Docker network
Each service: own image, own scaling, own lifecycle
```

## CI/CD Pipeline with Docker

```
Developer commits code
       │
       ▼
┌──────────────────┐
│  CI Server        │
│  (GitHub Actions) │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Build Image      │
│  docker build     │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Test Container   │
│  docker compose   │
│  up + integration │
│  tests            │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Push to Registry │
│  docker push      │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Deploy           │
│  docker compose   │
│  pull + up        │
└──────────────────┘
```

## Hybrid: Docker + AWS ECS

```
Locally: docker compose up (dev)
Cloud:   docker compose up (ECS context)

# Create ECS context
docker context create ecs my-ecs-context
docker --context my-ecs-context compose up

# Deploys to ECS Fargate:
# - CloudFormation stack
# - ALB + Target Groups
# - Fargate tasks
# - CloudWatch logs
# - Service Discovery
```

## Docker Swarm (Production Clustering)

```
Manager node:         Worker nodes:
┌────────────┐       ┌────────────┐ ┌────────────┐
│ Manager    │──────►│ Worker 1   │ │ Worker 2   │
│ ┌────────┐ │       │ ┌────────┐ │ │ ┌────────┐ │
│ │ API     │ │       │ │nginx   │ │ │ │app     │ │
│ │ Sched   │ │       │ └────────┘ │ │ └────────┘ │
│ │ Raft    │ │       │ ┌────────┐ │ │ ┌────────┐ │
│ └────────┘ │       │ │app     │ │ │ │app     │ │
└────────────┘       │ └────────┘ │ │ └────────┘ │
                     └────────────┘ └────────────┘
Overlay network: ingress (routing mesh, port 80 exposed on all nodes)
```
