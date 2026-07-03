# DevOps Academy — Complete Learning Path

<div align="true">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-12-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Intermediate_to_Expert-purple?style=for-the-badge)

**Master DevOps practices — from containerization to observability and infrastructure as code**

</div>

---

## Overview

The DevOps Academy covers the complete DevOps lifecycle for Java applications. You will learn containerization with Docker, orchestration with Kubernetes, CI/CD pipelines, monitoring and observability (Prometheus, Grafana, Jaeger), and health management with Spring Boot Actuator. Every lab is Java-centric and production-focused.

---

## Curriculum Map

### Level 1: Containerization & Orchestration
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 01 | [Docker Fundamentals](./01-docker/) | Images, containers, Dockerfile, Compose, networks, volumes | 4-5 hrs | Intermediate | [29-docker](../../29-docker/) |
| 02 | [Kubernetes Basics](./02-kubernetes/) | Pods, deployments, services, ConfigMaps, secrets | 5-6 hrs | Advanced | [30-kubernetes](../../30-kubernetes/) |
| 03 | [Kubernetes Advanced](./03-kubernetes-advanced/) | Ingress, Helm, operators, RBAC, auto-scaling | 5-6 hrs | Advanced | [56-k8s](../../56-k8s/) |
| 04 | [Cloud-Native Patterns](./04-cloud-native/) | 12-factor app, service discovery, configuration, resilience | 3-4 hrs | Advanced | [10-cloud-native](../../10-cloud-native/) |

### Level 2: CI/CD & Infrastructure
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 05 | [CI/CD Pipelines](./05-ci-cd/) | GitHub Actions, Jenkins, Maven/Gradle, artifact management | 4-5 hrs | Advanced | [28-ci-cd](../../28-ci-cd/) |
| 06 | [Testcontainers](./06-testcontainers/) | Integration tests with containers, modularity patterns | 3-4 hrs | Intermediate | [42-testcontainers](../../42-testcontainers/) |
| 07 | [WireMock & Service Virtualization](./07-wiremock/) | HTTP stubbing, contract testing, simulated dependencies | 2-3 hrs | Intermediate | [43-wiremock](../../43-wiremock/) |

### Level 3: Observability
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 08 | [Spring Boot Actuator](./08-actuator/) | Health checks, metrics, info, env, custom endpoints | 2-3 hrs | Intermediate | [41-actuator](../../41-actuator/) |
| 09 | [Prometheus & Metrics](./09-prometheus/) | Metrics exposition, custom metrics, alerting, recording rules | 3-4 hrs | Advanced | [38-prometheus](../../38-prometheus/) |
| 10 | [Grafana Dashboards](./10-grafana/) | Dashboards, panels, alerts, data sources, annotations | 3-4 hrs | Advanced | [39-grafana](../../39-grafana/) |
| 11 | [Observability with OpenTelemetry](./11-observability/) | Tracing, logging, metrics correlation, OpenTelemetry SDK | 4-5 hrs | Advanced | [23-observability](../../23-observability/) |
| 12 | [Distributed Tracing with Jaeger](./12-jaeger/) | Trace context, spans, sampling, trace analytics | 3-4 hrs | Advanced | [40-jaeger](../../40-jaeger/) |

**Total estimated time: 41-51 hours**

---

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08 ──→ 09 ──→ 10 ──→ 11 ──→ 12
Docker  K8s    K8s     Cloud   CI/CD   Test    Wire    Actu    Prom    Graf    OTel   Jaeger
               Adv     Nat                     Cont            ather                   Trace
```

Labs 01–04 cover container infrastructure. Labs 05–07 cover automation and testing. Labs 08–12 cover comprehensive observability.

---

## Prerequisites

- Basic Linux command-line skills
- Java application building (Maven/Gradle)
- Understanding of HTTP and REST
- Docker Desktop installed
- Access to a Kubernetes cluster (local Minikube/K3s or cloud)

---

## How to Use This Academy

### For DevOps Engineers
Work through all labs sequentially for a complete DevOps skillset.

### For Backend Developers
Focus on Labs 01–02, 06, 08–10 for essential container and observability skills.

### For Platform Engineers
Pay special attention to Labs 03, 05, 11–12 for advanced platform and tracing capabilities.

---

## Related Academies

- [Cloud Academy](../cloud/) — AWS, cloud services, Terraform
- [Backend Academy](../backend/) — Spring Boot, application deployment
- [Security Academy](../security/) — Secrets management, security scanning
- [Architecture Academy](../architecture/) — Service mesh, resilience patterns

---

## Resources

### Official Documentation
- [Docker Docs](https://docs.docker.com/)
- [Kubernetes Docs](https://kubernetes.io/docs/)
- [Prometheus Docs](https://prometheus.io/docs/)
- [Grafana Docs](https://grafana.com/docs/)
- [OpenTelemetry Docs](https://opentelemetry.io/docs/)
- [Jaeger Docs](https://www.jaegertracing.io/docs/)

### Books
- *The Docker Book* — James Turnbull
- *Kubernetes in Action* — Marko Luksa
- *Site Reliability Engineering* — Google SRE Team
- *The Art of Monitoring* — James Turnbull

### Tools
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Minikube](https://minikube.sigs.k8s.io/)
- [Helm](https://helm.sh/)
- [kind](https://kind.sigs.k8s.io/)

---

<div align="center">

**Operate Excellence. Build Everything.**

</div>
