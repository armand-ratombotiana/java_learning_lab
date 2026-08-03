# Problem Walkthrough: Infrastructure as Code for ML

## Problem 1: Model-Serving Platform as Generated Terraform — Company: Pinterest

### Interview Scenario

> **Interviewer**: "Our model-serving platform is provisioned by hand — five HCL files that have drifted between dev and prod, and the last incident was a security group someone deleted. We want a Java generator that produces the Terraform config for the whole environment: provider with remote state, three versioned S3 buckets, IAM, VPC with a security group, and a Fargate service for the model server. The demo should generate the `dev` environment and print every section; the transcript must come from the compiled run."
>
> **Candidate**: "I'll mirror the generator from the lab, run it, and lock the exact transcript — including the quirks that a careful reviewer would flag in a PR."

### The Problem

1. Generate a `provider "aws"` block with the S3 backend for state and the DynamoDB lock table.
2. Generate three S3 buckets (`models`, `data`, `experiments`) for the `dev` environment — each with versioning enabled and a 90-day noncurrent-version lifecycle.
3. Generate the IAM execution role and policy for ECS.
4. Generate the VPC module plus a model-serving security group (ingress 8080 from the VPC CIDR, open egress).
5. Generate the ECS cluster, task definition, and Fargate service for a fraud-detector model, wired to the VPC subnets and security group.
6. Every generated string must match the compiled run exactly — the Expected Output is a golden file.

### Solution Walkthrough

1. **Define the generator as pure functions.** Each `generateXxx` method takes the few parameters that vary (name, environment, cpu, memory) and returns one HCL block as a Java text block with format specifiers — `String.format("""...""", name, env, ...)`. The output is deterministic: no randomness, no timestamps, no ordering dependence — which is the property that makes the transcript a testable golden file.
2. **Parameterize environment through every resource.** `mlops-%s-%s` threads `name` and `environment` into bucket names, cluster names, task families, and services — so `dev` and `prod` are the same code with different arguments, and cross-resource references stay consistent by construction (`aws_s3_bucket.%s.id`).
3. **Stamp governance onto storage.** `generateS3Bucket` always emits three blocks: the bucket with `Name`/`Environment`/`ManagedBy` tags, the versioning block with `status = "Enabled"`, and the lifecycle block expiring noncurrent versions after 90 days. Three buckets (`models`, `data`, `experiments`) are concatenated in `main` with blank-line separators.
4. **Emit least-privilege IAM for the task.** The role's trust policy allows `sts:AssumeRole` only for `ecs-tasks.amazonaws.com`; the attached policy grants S3 Get/Put and CloudWatch Logs — the exact calls a model server makes. The `Resource = ["*"]` scope is a simplification to flag in review, not a pattern to copy into prod.
5. **Compose the network from a module and a rule.** `generateVpc` pulls the community VPC module (two AZs, private/public subnets, NAT), and `generateSecurityGroup` adds the model API ingress — 8080/tcp from `10.0.0.0/16` — with open egress, referenced by `module.vpc.vpc_id`.
6. **Build the serving path.** `generateEcsFargateService` emits the cluster, the task definition (Fargate compatibility, `cpu = "512"`, `memory = "1024"`, container with `ENVIRONMENT` and `MODEL_THRESHOLD=0.85` env vars, awslogs config), and the service with `desired_count = 1` for dev — 3 for prod via the environment conditional.
7. **Know the two bugs before a reviewer finds them.** The image uses the literal placeholder `ACCOUNT_ID` — a real pipeline needs a variable or account data source — and the service references `aws_security_group.fraud-detector_sg.id`, a resource the generator never declares (only `model-server_sg` exists), so `terraform plan` would fail with "Reference to undeclared resource". Both are deliberate lab quirks; step 8's transcript shows them exactly as generated.
8. **Verify against the compiled run.** The Expected Output below is the full stdout of the walkthrough class on this repo's JDK — every HCL block, blank line, and placeholder reproduced from the actual run.

### Code

```java
package com.mlops.lab12;

import java.util.*;

public class InfrastructureAsCodeWalkthrough {

    static class TerraformConfigGenerator {

        static String generateProvider(String region) {
            return String.format("""
                    provider "aws" {
                      region = "%s"
                    }
                    
                    terraform {
                      backend "s3" {
                        bucket = "mlops-terraform-state"
                        key    = "ml-platform/terraform.tfstate"
                        region = "us-east-1"
                        dynamodb_table = "mlops-terraform-locks"
                      }
                    }
                    """, region);
        }

        static String generateS3Bucket(String name, String environment) {
            return String.format("""
                    resource "aws_s3_bucket" "%s" {
                      bucket = "mlops-%s-%s"
                      tags = {
                        Name        = "mlops-%s-%s"
                        Environment = "%s"
                        ManagedBy   = "terraform"
                      }
                    }
                    
                    resource "aws_s3_bucket_versioning" "%s_versioning" {
                      bucket = aws_s3_bucket.%s.id
                      versioning_configuration {
                        status = "Enabled"
                      }
                    }
                    
                    resource "aws_s3_bucket_lifecycle_configuration" "%s_lifecycle" {
                      bucket = aws_s3_bucket.%s.id
                      rule {
                        id     = "expire-old-versions"
                        status = "Enabled"
                        noncurrent_version_expiration {
                          noncurrent_days = 90
                        }
                      }
                    }
                    """, name, name, environment, name, environment, environment,
                    name, name, name, name);
        }

        static String generateIamRole(String name) {
            return String.format("""
                    resource "aws_iam_role" "%s_exec" {
                      name = "mlops-%s-exec-role"
                      assume_role_policy = jsonencode({
                        Version = "2012-10-17"
                        Statement = [{
                          Action = "sts:AssumeRole"
                          Effect = "Allow"
                          Principal = { Service = "ecs-tasks.amazonaws.com" }
                        }]
                      })
                    }
                    
                    resource "aws_iam_role_policy" "%s_exec_policy" {
                      name = "mlops-%s-exec-policy"
                      role = aws_iam_role.%s_exec.id
                      policy = jsonencode({
                        Version = "2012-10-17"
                        Statement = [
                          {
                            Effect = "Allow"
                            Action = [
                              "s3:GetObject", "s3:PutObject",
                              "logs:CreateLogStream", "logs:PutLogEvents"
                            ]
                            Resource = ["*"]
                          }
                        ]
                      })
                    }
                    """, name, name, name, name, name);
        }

        static String generateVpc() {
            return """
                    module "vpc" {
                      source = "terraform-aws-modules/vpc/aws"
                      name = "mlops-vpc"
                      cidr = "10.0.0.0/16"
                      azs             = ["us-east-1a", "us-east-1b"]
                      private_subnets = ["10.0.1.0/24", "10.0.2.0/24"]
                      public_subnets  = ["10.0.101.0/24", "10.0.102.0/24"]
                      enable_nat_gateway = true
                      enable_vpn_gateway = false
                      tags = { Name = "mlops-vpc", ManagedBy = "terraform" }
                    }
                    """;
        }

        static String generateSecurityGroup(String name) {
            return String.format("""
                    resource "aws_security_group" "%s_sg" {
                      name        = "mlops-%s-model-sg"
                      description = "Security group for ML model serving"
                      vpc_id      = module.vpc.vpc_id
                    
                      ingress {
                        description = "Model API"
                        from_port   = 8080
                        to_port     = 8080
                        protocol    = "tcp"
                        cidr_blocks = ["10.0.0.0/16"]
                      }
                    
                      egress {
                        from_port   = 0
                        to_port     = 0
                        protocol    = "-1"
                        cidr_blocks = ["0.0.0.0/0"]
                      }
                    }
                    """, name, name);
        }

        static String generateEcsFargateService(String name, String environment, int cpu, int memory) {
            return String.format("""
                    resource "aws_ecs_cluster" "%s_cluster" {
                      name = "mlops-%s-%s-cluster"
                    }
                    
                    resource "aws_ecs_task_definition" "%s_task" {
                      family                   = "mlops-%s-%s-task"
                      network_mode            = "awsvpc"
                      requires_compatibilities = ["FARGATE"]
                      cpu                     = "%d"
                      memory                  = "%d"
                      execution_role_arn      = aws_iam_role.%s_exec.arn
                      container_definitions = jsonencode([
                        {
                          name  = "model-server"
                          image = "%s.dkr.ecr.us-east-1.amazonaws.com/mlops-model:latest"
                          portMappings = [{ containerPort = 8080 }]
                          environment = [
                            { name = "ENVIRONMENT", value = "%s" },
                            { name = "MODEL_THRESHOLD", value = "0.85" }
                          ]
                          logConfiguration = {
                            logDriver = "awslogs"
                            options = {
                              awslogs-group = "/ecs/mlops-%s-%s"
                              awslogs-region = "us-east-1"
                            }
                          }
                        }
                      ])
                    }
                    
                    resource "aws_ecs_service" "%s_service" {
                      name            = "mlops-%s-%s-service"
                      cluster         = aws_ecs_cluster.%s_cluster.id
                      task_definition = aws_ecs_task_definition.%s_task.arn
                      desired_count   = %s
                      launch_type     = "FARGATE"
                      network_configuration {
                        subnets         = module.vpc.private_subnets
                        security_groups = [aws_security_group.%s_sg.id]
                      }
                    }
                    """, name, name, environment, name, name, environment,
                    cpu, memory, name, "ACCOUNT_ID", environment, name, environment,
                    name, name, environment, name, name,
                    environment.equals("prod") ? "3" : "1", name);
        }
    }

    public static void main(String[] args) {
        String env = "dev";
        System.out.println("=== Infrastructure as Code for ML (Environment: " + env + ") ===\n");

        System.out.println("--- main.tf ---");
        String provider = TerraformConfigGenerator.generateProvider("us-east-1");
        System.out.println(provider);

        System.out.println("--- s3.tf ---");
        String s3 = TerraformConfigGenerator.generateS3Bucket("models", env);
        s3 += "\n" + TerraformConfigGenerator.generateS3Bucket("data", env);
        s3 += "\n" + TerraformConfigGenerator.generateS3Bucket("experiments", env);
        System.out.println(s3);

        System.out.println("--- iam.tf ---");
        String iam = TerraformConfigGenerator.generateIamRole("model-server");
        System.out.println(iam);

        System.out.println("--- vpc.tf ---");
        String vpc = TerraformConfigGenerator.generateVpc();
        vpc += "\n" + TerraformConfigGenerator.generateSecurityGroup("model-server");
        System.out.println(vpc);

        System.out.println("--- ecs.tf ---");
        String ecs = TerraformConfigGenerator.generateEcsFargateService(
                "fraud-detector", env, 512, 1024);
        System.out.println(ecs);
    }
}
```

### Expected Output

```
=== Infrastructure as Code for ML (Environment: dev) ===

--- main.tf ---
provider "aws" {
  region = "us-east-1"
}

terraform {
  backend "s3" {
    bucket = "mlops-terraform-state"
    key    = "ml-platform/terraform.tfstate"
    region = "us-east-1"
    dynamodb_table = "mlops-terraform-locks"
  }
}

--- s3.tf ---
resource "aws_s3_bucket" "models" {
  bucket = "mlops-models-dev"
  tags = {
    Name        = "mlops-models-dev"
    Environment = "dev"
    ManagedBy   = "terraform"
  }
}

resource "aws_s3_bucket_versioning" "models_versioning" {
  bucket = aws_s3_bucket.models.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "models_lifecycle" {
  bucket = aws_s3_bucket.models.id
  rule {
    id     = "expire-old-versions"
    status = "Enabled"
    noncurrent_version_expiration {
      noncurrent_days = 90
    }
  }
}

resource "aws_s3_bucket" "data" {
  bucket = "mlops-data-dev"
  tags = {
    Name        = "mlops-data-dev"
    Environment = "dev"
    ManagedBy   = "terraform"
  }
}

resource "aws_s3_bucket_versioning" "data_versioning" {
  bucket = aws_s3_bucket.data.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "data_lifecycle" {
  bucket = aws_s3_bucket.data.id
  rule {
    id     = "expire-old-versions"
    status = "Enabled"
    noncurrent_version_expiration {
      noncurrent_days = 90
    }
  }
}

resource "aws_s3_bucket" "experiments" {
  bucket = "mlops-experiments-dev"
  tags = {
    Name        = "mlops-experiments-dev"
    Environment = "dev"
    ManagedBy   = "terraform"
  }
}

resource "aws_s3_bucket_versioning" "experiments_versioning" {
  bucket = aws_s3_bucket.experiments.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "experiments_lifecycle" {
  bucket = aws_s3_bucket.experiments.id
  rule {
    id     = "expire-old-versions"
    status = "Enabled"
    noncurrent_version_expiration {
      noncurrent_days = 90
    }
  }
}

--- iam.tf ---
resource "aws_iam_role" "model-server_exec" {
  name = "mlops-model-server-exec-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy" "model-server_exec_policy" {
  name = "mlops-model-server-exec-policy"
  role = aws_iam_role.model-server_exec.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject", "s3:PutObject",
          "logs:CreateLogStream", "logs:PutLogEvents"
        ]
        Resource = ["*"]
      }
    ]
  })
}

--- vpc.tf ---
module "vpc" {
  source = "terraform-aws-modules/vpc/aws"
  name = "mlops-vpc"
  cidr = "10.0.0.0/16"
  azs             = ["us-east-1a", "us-east-1b"]
  private_subnets = ["10.0.1.0/24", "10.0.2.0/24"]
  public_subnets  = ["10.0.101.0/24", "10.0.102.0/24"]
  enable_nat_gateway = true
  enable_vpn_gateway = false
  tags = { Name = "mlops-vpc", ManagedBy = "terraform" }
}

resource "aws_security_group" "model-server_sg" {
  name        = "mlops-model-server-model-sg"
  description = "Security group for ML model serving"
  vpc_id      = module.vpc.vpc_id

  ingress {
    description = "Model API"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["10.0.0.0/16"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

--- ecs.tf ---
resource "aws_ecs_cluster" "fraud-detector_cluster" {
  name = "mlops-fraud-detector-dev-cluster"
}

resource "aws_ecs_task_definition" "fraud-detector_task" {
  family                   = "mlops-fraud-detector-dev-task"
  network_mode            = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                     = "512"
  memory                  = "1024"
  execution_role_arn      = aws_iam_role.fraud-detector_exec.arn
  container_definitions = jsonencode([
    {
      name  = "model-server"
      image = "ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/mlops-model:latest"
      portMappings = [{ containerPort = 8080 }]
      environment = [
        { name = "ENVIRONMENT", value = "dev" },
        { name = "MODEL_THRESHOLD", value = "0.85" }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group = "/ecs/mlops-fraud-detector-dev"
          awslogs-region = "us-east-1"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "fraud-detector_service" {
  name            = "mlops-fraud-detector-dev-service"
  cluster         = aws_ecs_cluster.fraud-detector_cluster.id
  task_definition = aws_ecs_task_definition.fraud-detector_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets         = module.vpc.private_subnets
    security_groups = [aws_security_group.fraud-detector_sg.id]
  }
}
```

*(Reviewer notes, as generated: the `ACCOUNT_ID` placeholder must be substituted via variable or account data source before apply; the service's `aws_security_group.fraud-detector_sg` reference is undeclared — the generator emits only `model-server_sg` — so `terraform plan` would fail until the service generator takes the SG resource name as a parameter; and `Resource = ["*"]` in the IAM policy should be scoped to the three `mlops-*` buckets. The transcript keeps the lab's output faithful, quirks included.)*

## Problem 2: Environment Promotion Without Drift — Company: Gusto

### The Problem

The platform runs `dev`, `staging`, and `prod` from the same generator. A production incident reveals `prod`'s model bucket has no lifecycle rule — someone edited the config by hand last quarter. How do you detect and prevent this?

### Solution Walkthrough

1. **Make the state the source of truth.** Add the S3+DynamoDB backend to prod (the generator already emits it in `generateProvider`) and run `terraform plan` — drift between the declared lifecycle rule and the live bucket shows up immediately as a planned change.
2. **Re-apply the generator output to heal the drift.** The plan will show the missing lifecycle rule as an addition; applying restores the 90-day expiration, proving that the environment converges to the declared definition.
3. **Remove the hand-edit path.** The incident happened because editing HCL directly is possible; CI/CD from Lab 07 runs `terraform plan` on every PR and blocks merges that change generated output without a golden-file update — the same check as Problem 1's transcript.
4. **Prevent recurrence with the inventory tag.** Every generated resource carries `ManagedBy = "terraform"`; the response plan includes an audit job that lists untagged resources in each environment, flagging anything Terraform didn't create — manual infrastructure now triggers an alert instead of an incident.

## Problem 3: Golden-File Testing the Generator — Company: Zapier

### The Problem

The config generator produces the environment definition; a refactor reorders parameters and silently changes the generated HCL, breaking a deployment at plan time. Design a test that fails on any unintended output change.

### Solution Walkthrough

1. **Freeze the current output as a golden file.** The Problem 1 Expected Output is exactly that: the captured stdout becomes a test fixture; the test runs the generator and diffs against it — any output change is a red test, forcing an explicit, reviewed update of the golden file.
2. **Make the test assert more than text.** Structural assertions complement the golden file: every `aws_s3_bucket_versioning` block is preceded by a bucket with a `status = "Enabled"` line; every `aws_*` resource referenced inside another block is declared somewhere in the config (the check that catches `fraud-detector_sg`); and `mlops-%s-%s` naming is consistent across cluster, task, and service.
3. **Parameterize the fixtures.** One golden file per environment (dev, prod) catches environment-specific regressions — e.g. `desired_count` flipping from 3 to 1 in prod — because the golden files are generated from the same code path with different arguments.
4. **Gate the pipeline on the tests.** The PR pipeline runs the generator tests before `terraform validate`; a refactor that passes tests but changes config is still caught by the golden diff — and the diff itself is the review artifact, because for infrastructure, a config change *is* the deployment decision.
