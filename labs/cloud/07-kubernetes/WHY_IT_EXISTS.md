# Why Kubernetes Exists

## The Problem
Running containers is easy (Docker). Running hundreds of containers across multiple servers — with networking, health checks, rolling updates, scaling, secrets, and service discovery — is hard. That's why Kubernetes exists.

## What Kubernetes Provides
- **Orchestration**: Schedule containers across a cluster
- **Self-healing**: Restart failed containers, reschedule on node failure
- **Service discovery**: DNS-based service resolution
- **Load balancing**: Distribute traffic across pods
- **Rolling updates**: Zero-downtime deployments
- **Auto-scaling**: Scale pods based on CPU/memory or custom metrics
- **Storage orchestration**: Attach persistent storage (EBS, EFS)
- **Configuration**: ConfigMaps and Secrets as first-class resources

## Why Kubernetes for Java?
- **Predictable deployments**: Blue-green, canary, rolling updates
- **Resource efficiency**: Bin-pack Java containers across nodes
- **Observability**: Built-in health checks, metrics, logging
- **Platform abstraction**: Same K8s API on AWS (EKS), GCP (GKE), Azure (AKS), on-prem
