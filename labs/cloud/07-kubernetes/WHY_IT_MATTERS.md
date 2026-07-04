# Why Kubernetes Matters

## Business Impact
- **Portability**: Same workloads run on any Kubernetes cluster (cloud or on-prem)
- **Velocity**: Ship features faster with automated rollouts and rollbacks
- **Reliability**: Self-healing infrastructure reduces pager rotation burden
- **Efficiency**: 3-5x better resource utilization compared to direct EC2 placement

## Technical Impact
- **Declarative config**: YAML manifests = infrastructure as code for containers
- **Extensibility**: CRDs (Custom Resource Definitions) extend K8s API
- **Ecosystem**: Helm charts, Operators, Service Mesh (Istio), GitOps (ArgoCD)
- **Maturity**: 15+ years of production battle-testing (Borg → Kubernetes)

## For Java Developers
- Spring Boot + Kubernetes = natural fit (actuator health checks = liveness/readiness probes)
- ConfigMaps = externalized Spring configuration
- Secrets = sensitive values (don't put in application.properties)
- StatefulSets for stateful Java apps (Kafka, Cassandra, ZooKeeper)
- Helm charts to package and deploy Java microservices
