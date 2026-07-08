# Platform Engineering — Theory

## 1. Introduction
Platform engineering is the discipline of designing, building, and maintaining internal developer platforms (IDPs) that provide self-service capabilities, golden paths, and standardized infrastructure for development teams.

## 2. Problem Context
As organizations scale, development teams face cognitive load from infrastructure complexity, inconsistent tooling and processes, slow onboarding, compliance and governance challenges, fragmented observability, and configuration drift across environments.

## 3. Internal Developer Platform (IDP)

### 3.1 Definition
An IDP is a cohesive set of tools, services, and automation that development teams use to build, deploy, and operate services with minimal cognitive load.

### 3.2 Key Capabilities
Self-service provisioning, automated CI/CD pipelines, standardized deployment templates, built-in observability, security and compliance guardrails, and documentation automation.

## 4. Backstage — Spotify's Developer Portal

### 4.1 Software Catalog
Centralized registry of services, libraries, websites, and ML models with ownership tracking, dependency mapping, lifecycle tracking, and automated discovery.

### 4.2 Software Templates
Golden path templates with pre-configured CI/CD pipelines, standardized project structures, built-in quality checks, and compliance defaults.

### 4.3 TechDocs
Documentation-as-code integrated into the portal with markdown-based docs, automated build and publish, cross-document search, and versioned documentation.

## 5. Golden Paths

### 5.1 Definition
Golden paths are the recommended, well-supported approaches for common development tasks. They reduce decision fatigue and ensure consistency.

### 5.2 Characteristics
Opinionated but not restrictive, documented with step-by-step guidance, automated through platform tooling, regularly updated, and measurable.

## 6. Self-Service Capabilities

### 6.1 Infrastructure as Code
Declarative configuration with Terraform or Pulumi, environment templates with parameters, automated approval workflows, and cost estimation.

### 6.2 CI/CD Automation
Standardized build pipelines, automated testing and quality gates, deployment strategies, and rollback automation.

## 7. Platform Team Topologies

### 7.1 Enabling Team
Works with stream-aligned teams to adopt platform capabilities.

### 7.2 Platform Team
Owns and operates the internal developer platform.

### 7.3 Stream-Aligned Team
Focuses on business value delivery using the platform.

## 8. Measuring Platform Success
Developer onboarding time reduction, deployment frequency increase, MTTR reduction, change failure rate improvement, developer satisfaction scores, and platform adoption metrics.
