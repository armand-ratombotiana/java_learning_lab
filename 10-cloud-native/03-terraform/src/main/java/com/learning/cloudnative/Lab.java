package com.learning.cloudnative;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Terraform / Infrastructure as Code Concepts ===\n");

        demonstrateCoreConcepts();
        demonstrateHCLStructure();
        demonstrateStateManagement();
        demonstrateModules();
        demonstrateProvisioners();
        demonstrateWorkflow();
    }

    private static void demonstrateCoreConcepts() {
        System.out.println("--- Core Concepts ---");
        System.out.println("IaC = Manage infrastructure via declarative configuration files");
        System.out.println("Terraform = HashiCorp's open-source provisioning tool");
        System.out.println();
        System.out.println("Providers: AWS, Azure, GCP, Kubernetes, Helm, etc.");
        System.out.println("Resources: cloud components (vm, bucket, db, etc.)");
        System.out.println("Data Sources: read existing infrastructure attributes");
        System.out.println("Variables: input params, Outputs: exposed values");
    }

    private static void demonstrateHCLStructure() {
        System.out.println("\n--- HCL Configuration ---");
        System.out.println("terraform {");
        System.out.println("  required_providers {");
        System.out.println("    aws = { source = \"hashicorp/aws\", version = \"~> 5.0\" }");
        System.out.println("  }");
        System.out.println("}");
        System.out.println();
        System.out.println("resource \"aws_s3_bucket\" \"data\" {");
        System.out.println("  bucket = \"my-app-data-bucket\"");
        System.out.println("  tags   = { Name = \"DataBucket\" }");
        System.out.println("}");
        System.out.println();
        System.out.println("variable \"region\" { default = \"us-east-1\" }");
        System.out.println("output \"bucket_arn\" { value = aws_s3_bucket.data.arn }");
    }

    private static void demonstrateStateManagement() {
        System.out.println("\n--- State Management ---");
        System.out.println("terraform.tfstate = JSON mapping of real-world resources");
        System.out.println("Terraform compares state with config to determine changes");
        System.out.println();
        System.out.println("Backends for state storage:");
        System.out.println("  Local   -> terraform.tfstate file (single-user)");
        System.out.println("  Remote  -> S3, GCS, Azure Storage, Terraform Cloud");
        System.out.println("  State Locking -> DynamoDB (S3), prevents concurrent modifications");
        System.out.println();
        System.out.println("Sensitive data in state -> use encryption at rest + access controls");
    }

    private static void demonstrateModules() {
        System.out.println("\n--- Modules ---");
        System.out.println("Modules = Reusable, composable infrastructure packages");
        System.out.println("module \"vpc\" {");
        System.out.println("  source  = \"terraform-aws-modules/vpc/aws\"");
        System.out.println("  version = \"5.0.0\"");
        System.out.println("  name    = \"my-vpc\"");
        System.out.println("  cidr    = \"10.0.0.0/16\"");
        System.out.println("}");
        System.out.println();
        System.out.println("Sources: local path, registry, GitHub, S3, HTTP");
        System.out.println("Root module = current working directory");
    }

    private static void demonstrateProvisioners() {
        System.out.println("\n--- Provisioners (Use as Last Resort) ---");
        System.out.println("file -> Copy files to resource");
        System.out.println("remote-exec -> Run commands on resource");
        System.out.println("local-exec  -> Run commands on local machine");
        System.out.println();
        System.out.println("Better alternatives:");
        System.out.println("  User data / cloud-init scripts");
        System.out.println("  Packer for pre-baked AMIs");
        System.out.println("  Configuration management (Ansible, Chef)");
    }

    private static void demonstrateWorkflow() {
        System.out.println("\n--- Terraform Workflow ---");
        System.out.println("1. terraform init      -> Initialize backend & providers");
        System.out.println("2. terraform fmt        -> Format code");
        System.out.println("3. terraform validate   -> Validate syntax");
        System.out.println("4. terraform plan       -> Show execution plan");
        System.out.println("5. terraform apply      -> Execute changes");
        System.out.println("6. terraform destroy    -> Tear down resources");
        System.out.println();
        System.out.println("Workspaces  -> Manage multiple environments (dev/staging/prod)");
        System.out.println("Terraform Cloud -> Remote execution, policy as code (Sentinel)");
    }
}
