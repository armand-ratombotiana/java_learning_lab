# Architecture — AWS Compute

## Lambda + API Gateway Microservice

```
                          ┌──────────────┐
                          │ Route 53     │
                          │ api.example. │
                          │ com          │
                          └──────┬───────┘
                                 │
                          ┌──────▼───────┐
                          │ CloudFront   │
                          │ CDN + WAF    │
                          └──────┬───────┘
                                 │
                          ┌──────▼───────┐
                          │ API Gateway  │
                          │ REST/HTTP    │
                          └──────┬───────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
         ┌────▼────┐       ┌────▼────┐        ┌────▼────┐
         │ Lambda   │       │ Lambda  │        │ Lambda  │
         │ Users    │       │ Orders  │        │ Payments│
         │(Java 17) │       │(Java 17)│        │(Java 17)│
         └────┬────┘       └────┬────┘        └────┬────┘
              │                  │                  │
              └──────────────────┼──────────────────┘
                                 │
                          ┌──────▼───────┐
                          │ DynamoDB     │
                          │ (Tables)     │
                          └──────────────┘
```

## ECS Fargate Service Mesh

```
                          ┌──────────────┐
                          │   ALB (HTTPS)│
                          └──────┬───────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
         ┌────▼────┐       ┌────▼────┐        ┌────▼────┐
         │Fargate  │       │Fargate  │        │Fargate  │
         │Frontend │       │  API    │        │  Auth   │
         │React+S  │       │Spring   │        │Spring   │
         │pring    │       │  Boot   │        │  Boot   │
         └─────────┘       └────┬────┘        └─────────┘
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
         ┌────▼────┐      ┌────▼────┐       ┌────▼────┐
         │Fargate  │      │Fargate  │       │Fargate  │
         │ Orders  │      │Inventory│       │Payments │
         │Spring   │      │Spring   │       │Spring   │
         └────┬────┘      └─────────┘       └────┬────┘
              │                                   │
         ┌────▼────┐                        ┌────▼────┐
         │ Aurora  │                        │ Stripe  │
         │ (RDS)   │                        │ (External)│
         └─────────┘                        └─────────┘

Service Discovery: AWS Cloud Map
Service Mesh: App Mesh (Envoy sidecar)
```

## Hybrid Architecture — EC2 + Lambda + ECS

```
┌──────────────────────────────────────────────────────┐
│                 AWS Cloud                            │
│                                                      │
│  ┌──────────────────────────────────────────────┐   │
│  │   Elastic Beanstalk (EC2 + Tomcat)           │   │
│  │   Legacy Spring MVC app                      │   │
│  └──────────────────────────────────────────────┘   │
│                                                      │
│  ┌──────────────────────────────────────────────┐   │
│  │   ECS Fargate (New microservices)            │   │
│  │   ┌─────────┐ ┌─────────┐ ┌─────────┐       │   │
│  │   │Orders   │ │Payments │ │Users    │       │   │
│  │   └─────────┘ └─────────┘ └─────────┘       │   │
│  └──────────────────────────────────────────────┘   │
│                                                      │
│  ┌──────────────────────────────────────────────┐   │
│  │   Lambda (Event-driven processing)           │   │
│  │   ┌─────────┐ ┌─────────┐                    │   │
│  │   │Resize   │ │Notify   │                    │   │
│  │   │Images   │ │Users    │                    │   │
│  │   └─────────┘ └─────────┘                    │   │
│  └──────────────────────────────────────────────┘   │
│                                                      │
│  RDS, ElastiCache, SQS, S3 (Shared data layer)      │
└──────────────────────────────────────────────────────┘
```
