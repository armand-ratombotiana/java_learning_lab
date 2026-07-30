# Lab 05 — Infrastructure as Code Deep

## Overview
Deep dive into IaC tooling: Terraform state management, modules, workspaces, remote backends, Pulumi, CloudFormation, and CDK.

## Prerequisites
- Java 21+ development environment
- Basic IaC knowledge
- Understanding of cloud provisioning concepts

## What You Will Learn
- Model Terraform state, resources, and data sources in Java
- Implement module composition and versioning patterns
- Build workspace management for multi-environment IaC
- Design remote backend abstractions (S3, Azure Storage, GCS)
- Implement Pulumi-style infrastructure in Java
- Create CloudFormation template generation and CDK constructs

## Topics Covered
| Topic | Description |
|-------|-------------|
| State Management | State file, locking, drift detection, state migration |
| Modules | Input/output variables, module composition, registry |
| Workspaces | Multi-environment isolation, workspace switching |
| Remote Backends | S3, DynamoDB, Azure Storage, GCS, Terraform Cloud |
| Pulumi | Infrastructure as real code, automation API |
| CloudFormation | Templates, stacks, change sets, stack sets |
| CDK | Constructs, synthesis, custom resources |

## Java Package
`com.cloud.deep.lab05`
