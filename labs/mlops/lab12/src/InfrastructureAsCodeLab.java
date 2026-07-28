package com.mlops.lab12;

import java.util.*;

/**
 * Infrastructure as Code for ML — Lab 12.
 * <p>
 * Demonstrates IaC concepts for ML infrastructure using Terraform and Pulumi.
 * Generates Terraform HCL configurations programmatically for ML infrastructure:
 * S3 storage, ECS compute, IAM roles, and VPC networking.
 */
public class InfrastructureAsCodeLab {

    /** Generates Terraform HCL configuration for ML infrastructure. */
    static class TerraformConfigGenerator {

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
    }

    public static void main(String[] args) {
        String env = "dev";
        System.out.println("=== Infrastructure as Code for ML (Environment: " + env + ") ===\n");

        // Generate Terraform configurations
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

        // Show Pulumi equivalent as Java code
        System.out.println("\n=== Pulumi Equivalent (Java) ===");
        System.out.println("""
                // Pulumi Java equivalent:
                // Bucket modelsBucket = new Bucket("ml-models",
                //     BucketArgs.builder()
                //         .versioning(new BucketVersioningArgs(true))
                //         .build());
                //
                // EcsCluster cluster = new EcsCluster("ml-cluster");
                // TaskDefinition task = new TaskDefinition("ml-task",
                //     TaskDefinitionArgs.builder()
                //         .cpu("512").memory("1024")
                //         .build());
                """);

        System.out.println("\n=== Deploy Commands ===");
        System.out.println("""
                terraform init
                terraform plan -out plan.tfplan
                terraform apply plan.tfplan
                terraform destroy  # tear down
                """);
    }
}
