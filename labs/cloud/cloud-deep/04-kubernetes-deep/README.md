# Lab 04 — Kubernetes Deep

## Overview
Deep dive into Kubernetes internals: pod lifecycle, scheduling, controllers, operators, custom resources, admission webhooks, and network policies.

## Prerequisites
- Java 21+ development environment
- Docker and Kubernetes fundamentals
- Basic understanding of container orchestration

## What You Will Learn
- Model pod lifecycle phases in Java
- Implement a custom scheduler algorithm
- Build Kubernetes controllers and operators
- Define custom resource definitions (CRDs)
- Create admission webhooks for policy enforcement
- Design network policy rules

## Topics Covered
| Topic | Description |
|-------|-------------|
| Pod Lifecycle | Pending, Running, Succeeded, Failed, CrashLoopBackOff |
| Scheduling | Predicates, priorities, affinity, taints, tolerations |
| Controllers | ReplicaSet, Deployment, StatefulSet, DaemonSet controllers |
| Operators | CRDs, reconciliation loops, controller-runtime patterns |
| Admission Webhooks | Mutating, validating, webhook configuration |
| Network Policies | Ingress/egress rules, pod selectors, namespace isolation |

## Java Package
`com.cloud.deep.lab04`
