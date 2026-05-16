# AWS Compute - Theory

## Overview
AWS offers multiple compute services to handle diverse workload requirements from serverless functions to containerized applications.

## 1. AWS Lambda

### What is Lambda?
- Serverless compute service
- Run code without provisioning servers
- Pay only for compute time used

### Key Concepts
- **Function**: Unit of deployment
- **Runtime**: Language environment (Python, Node.js, Java, Go, etc.)
- **Handler**: Entry point for function execution
- **Execution Environment**: Isolated container for function
- **Trigger**: Event that invokes the function

### Invocation Types
- **Synchronous**: Direct call, waits for response
- **Asynchronous**: Event queued, no waiting
- **Event Source Mapping**: Polling-based invocation

### Limits and Best Practices
- Timeout: 15 minutes max
- Memory: 128MB to 10GB
- Concurrent executions: 1000 (default, expandable)
- Cold starts: Initial latency
- Use layers for dependencies

## 2. Amazon ECS (Elastic Container Service)

### What is ECS?
- Container orchestration service
- Manage Docker containers in AWS
- Two launch types: EC2 and Fargate

### Key Components
- **Cluster**: Logical grouping of container instances
- **Task Definition**: Blueprint for containers
- **Task**: Running instance of task definition
- **Service**: Maintains desired task count
- **Container**: Docker container instance

### EC2 vs Fargate
| Feature | EC2 | Fargate |
|---------|-----|---------|
| Server Management | User manages | Serverless |
| Pricing | EC2 instance hours | vCPU/memory used |
| Scaling | Cluster scaling | Automatic |
| Use Case | Custom control | Simplicity |

### Networking Modes
- **Bridge**: Default, docker bridge networking
- **Host**: Skip docker networking
- **AWSVPC**: Each task gets ENI

## 3. Amazon EKS (Elastic Kubernetes Service)

### What is EKS?
- Managed Kubernetes service
- Open-source Kubernetes compatible
- Multi-AZ high availability

### Key Concepts
- **Cluster**: Control plane + worker nodes
- **Control Plane**: Managed by AWS
- **Worker Nodes**: EC2 instances or Fargate
- **kubectl**: Kubernetes CLI
- **Pods**: Smallest deployable unit

### EKS Add-ons
- CoreDNS
- kube-proxy
- VPC CNI
- AWS Load Balancer Controller

### Comparison: ECS vs EKS
| Aspect | ECS | EKS |
|--------|-----|-----|
| Complexity | Lower | Higher |
| Portability | AWS-specific | Kubernetes standard |
| Ecosystem | AWS-native | Large K8s ecosystem |
| Learning Curve | Easier | Steeper |

## Scaling Strategies

### Lambda Scaling
- **Concurrency**: Concurrent executions
- **Provisioned Concurrency**: Pre-warmed instances
- **Reserved Concurrency**: Limit concurrent executions

### ECS/EKS Scaling
- **Horizontal Pod Autoscaler (HPA)**
- **Cluster Autoscaler**
- **Service Autoscaler**

## Use Cases

### Lambda
- API backends
- Image processing
- Real-time file processing
- IoT data ingestion

### ECS
- Microservices
- Batch jobs
- Long-running applications

### EKS
- Kubernetes-native applications
- Hybrid cloud workloads
- ML/AI workloads
- Complex orchestration needs