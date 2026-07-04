# Terraform Exercises

## Exercise 1: Hello Terraform
Create a local_file resource that writes "Hello Terraform" to a file. Initialize, plan, and apply.

## Exercise 2: Variables
Add variables for filename and content. Create a `terraform.tfvars` file. Use `terraform plan` to verify.

## Exercise 3: AWS EC2 (if AWS available)
Create a VPC, security group, and EC2 instance. Use `terraform apply`.

## Exercise 4: Remote State
Configure S3 backend for remote state with DynamoDB locking.

## Exercise 5: Modules
Create a module for VPC/network. Use it in root configuration with different CIDR blocks.

## Exercise 6: Data Sources
Use `data "aws_ami"` to find the latest Amazon Linux 2 AMI and launch an instance.

## Exercise 7: Loops and Conditionals
Use `count` or `for_each` to create multiple similar resources. Use `condition` for conditional creation.

## Exercise 8: Outputs
Define outputs for resource attributes. Use `terraform output` to display them.

## Exercise 9: Multi-Environment
Create directory structure for dev/staging/prod with different variable values per environment.
