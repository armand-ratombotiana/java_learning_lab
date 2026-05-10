# 31 - AWS Cloud Patterns

## Module Overview

This module covers AWS cloud services and patterns for deploying Java applications, including compute, storage, databases, and serverless architectures.

## Learning Objectives

- Deploy Java applications to AWS
- Use AWS services for storage and databases
- Implement serverless patterns with Lambda
- Configure AWS networking
- Apply security best practices

## Topics Covered

| Topic | Description |
|-------|-------------|
| EC2 | Virtual servers |
| ECS/EKS | Container orchestration |
| Lambda | Serverless functions |
| S3 | Object storage |
| RDS | Managed databases |
| IAM | Security and access |

## Prerequisites

- AWS account
- Basic cloud knowledge
- Java development experience

## Quick Start

```bash
# Configure AWS CLI
aws configure

# Deploy application
aws elasticbeanstalk create-application-version ...

# Deploy with CDK
cdk deploy
```

## Module Structure

```
31-aws/
├── README.md              # This file
├── DEEP_DIVE.md           # In-depth technical content
├── EDGE_CASES.md          # Edge cases and error handling
├── EXERCISES.md           # Practice exercises
├── PEDAGOGIC_GUIDE.md     # Teaching guide
├── PROJECTS.md            # Mini and real-world projects
└── QUIZZES.md             # Assessment questions
```