# 10 - Cloud Native Development

Cloud native application development and deployment. Covers Docker (images, containers, Dockerfile, networking, volumes, Compose), Kubernetes (pods, deployments, services, configmaps), CI/CD pipelines, Terraform infrastructure as code, Jenkins automation, and Helm packaging.

## Prerequisites

- Java 11+
- Maven 3.x
- Docker Desktop
- Kubernetes cluster (Minikube or Docker Desktop)

## Key Concepts

- Docker: images vs containers, layered filesystem, Dockerfile instructions, multi-stage builds, networking, volumes, Docker Compose
- Kubernetes: pods, deployments, services, ConfigMaps, Secrets, Ingress, persistent volumes
- CI/CD: pipeline stages, build, test, deploy automation
- Terraform: infrastructure as code, declarative resource management, providers
- Jenkins: pipeline as code, agents, multibranch pipelines
- Helm: charts, templates, releases, values

## Module Structure

- `01-docker/` - Docker containerization
- `02-kubernetes/` - Kubernetes orchestration
- `03-cicd/` - CI/CD pipelines
- `03-terraform/` - Terraform infrastructure
- `04-jenkins/` - Jenkins automation
- `05-helm/` - Helm package management

## Learning Objectives

- Containerize Java applications with Docker
- Deploy and manage containers on Kubernetes
- Build automated CI/CD pipelines

## Estimated Time

- 6-10 hours across all submodules

## How to Build

```bash
cd 10-cloud-native
mvn clean package
```

Run Docker lab:

```bash
cd 01-docker
mvn compile exec:java -Dexec.mainClass="com.learning.cloudnative.Lab"
```
