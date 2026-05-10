# 30 - Kubernetes Patterns

## Module Overview

This module covers Kubernetes deployment patterns, orchestration strategies, and best practices for managing containerized Java applications at scale.

## Learning Objectives

- Deploy applications to Kubernetes
- Configure pods, services, and deployments
- Implement security and resource management
- Use Helm for package management
- Monitor and troubleshoot K8s applications

## Topics Covered

| Topic | Description |
|-------|-------------|
| Pods | Pod configuration, lifecycle |
| Deployments | ReplicaSets, rolling updates |
| Services | ClusterIP, NodePort, LoadBalancer |
| ConfigMaps | Configuration management |
| Secrets | Sensitive data management |
| Ingress | HTTP routing |
| Helm | Package management |

## Prerequisites

- Docker knowledge
- kubectl CLI
- Minikube or kind for local testing

## Quick Start

```bash
# Deploy application
kubectl apply -f deployment.yaml

# View pods
kubectl get pods

# Check logs
kubectl logs -f deployment/myapp

# Scale application
kubectl scale deployment/myapp --replicas=3
```

## Module Structure

```
30-kubernetes/
├── README.md              # This file
├── DEEP_DIVE.md           # In-depth technical content
├── EDGE_CASES.md          # Edge cases and error handling
├── EXERCISES.md           # Practice exercises
├── PEDAGOGIC_GUIDE.md     # Teaching guide
├── PROJECTS.md            # Mini and real-world projects
└── QUIZZES.md             # Assessment questions
```