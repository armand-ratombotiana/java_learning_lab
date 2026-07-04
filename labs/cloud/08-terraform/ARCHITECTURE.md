# Architecture — Terraform

## Multi-Environment Infrastructure

```
┌──────────────────────────────────────────────────────────┐
│  Single Terraform repository                              │
├──────────────────────────────────────────────────────────┤
│                                                           │
│  envs/dev/terraform.tfvars:                               │
│    environment = "dev"                                    │
│    instance_type = "t3.micro"                             │
│    min_size = 1                                           │
│    max_size = 2                                           │
│                                                           │
│  envs/staging/terraform.tfvars:                           │
│    environment = "staging"                                │
│    instance_type = "t3.small"                             │
│    min_size = 2                                           │
│    max_size = 4                                           │
│                                                           │
│  envs/prod/terraform.tfvars:                              │
│    environment = "prod"                                   │
│    instance_type = "t3.medium"                            │
│    min_size = 3                                           │
│    max_size = 20                                          │
│                                                           │
│  Backend configs:                                          │
│    backend/dev.hcl   → bucket: tf-state-dev               │
│    backend/prod.hcl  → bucket: tf-state-prod              │
├──────────────────────────────────────────────────────────┤
│                                                           │
│  Modules:                                                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐│
│  │ VPC      │  │ Compute  │  │ Database │  │ Monitoring││
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘│
└──────────────────────────────────────────────────────────┘
```

## Microservice Infrastructure Pattern

```
module "orders-service" {
  source        = "./modules/java-microservice"
  service_name  = "orders"
  environment   = var.environment
  vpc_id        = module.vpc.vpc_id
  subnet_ids    = module.vpc.private_subnet_ids
  instance_type = var.instance_type
  app_port      = 8080
  health_check  = "/actuator/health"
  min_capacity  = 2
  max_capacity  = 10
  cpu_target    = 70
}

module "users-service" {
  source        = "./modules/java-microservice"
  service_name  = "users"
  environment   = var.environment
  vpc_id        = module.vpc.vpc_id
  subnet_ids    = module.vpc.private_subnet_ids
  instance_type = var.instance_type
  app_port      = 8080
  health_check  = "/actuator/health"
  min_capacity  = 2
  max_capacity  = 5
  cpu_target    = 70
}

# Shared resources
module "shared-rds" {
  source      = "./modules/rds"
  environment = var.environment
  vpc_id      = module.vpc.vpc_id
  subnet_ids  = module.vpc.database_subnet_ids
  db_name     = "shared"
  engine      = "aurora-mysql"
}
```

## Terraform + Docker + ECS

```
# Build → Push → Deploy with Terraform

# 1. Build Docker image (CI/CD)
docker build -t app:${VERSION} .
docker push ${ECR_REPO}:${VERSION}

# 2. Terraform reads latest image tag
data "aws_ecr_image" "latest" {
  repository_name = "java-app"
  image_tag       = var.app_version
}

# 3. Terraform creates/updates ECS service
resource "aws_ecs_service" "app" {
  name            = "java-app-${var.environment}"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = var.app_count
  
  deployment_controller {
    type = "ECS"
  }
}

# 4. Rolling update triggered by new task definition
resource "aws_ecs_task_definition" "app" {
  family = "java-app"
  container_definitions = jsonencode([
    {
      name  = "app"
      image = "${var.ecr_repo}:${var.app_version}"
      // ...
    }
  ])
}
```

## GitOps with Terraform Cloud

```
GitHub Repo
├── terraform/
│   ├── environments/
│   ├── modules/
│   └── main.tf
├── app/
│   ├── src/
│   └── Dockerfile
└── .github/workflows/
    └── terraform.yml

Workflow:
  1. PR to main → GitHub Actions runs terraform plan
  2. Plan posted as PR comment
  3. Merge → GitHub Actions runs terraform apply
  4. AWS infrastructure updated
  5. App deployment triggered (new ECS task definition)
```
