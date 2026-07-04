# Why AWS Compute Exists

## The Shift from Physical to Virtual to Serverless
- **Physical era**: One app per server, long procurement cycles
- **Virtual era (EC2)**: Multiple apps per server, faster provisioning
- **Serverless era (Lambda)**: No servers to manage — just code

AWS Compute exists to offer the **right abstraction level** for every workload. EC2 gives full control, Lambda removes all operational overhead, and ECS/EKS/Fargate sit in between for containerized workloads.

## Why Multiple Compute Options?
- **EC2**: Predictable, long-running, stateful workloads
- **Lambda**: Event-driven, burstable, short-lived functions
- **ECS**: Docker containers without orchestrator complexity
- **EKS**: Kubernetes-native container orchestration
- **Fargate**: Serverless containers — no cluster management
- **Elastic Beanstalk**: PaaS — deploy code, AWS handles everything

## Java Context
Java apps historically ran on EC2 + Tomcat. Modern Java deployments use:
- **Lambda** for microservices (with GraalVM native-image for cold-start)
- **ECS/Fargate** for Spring Boot microservices in Docker containers
- **EKS** for complex microservice mesh architectures
- **Elastic Beanstalk** for quick POCs and simple web apps
