# AWS Compute - Flashcards

### Lambda
Serverless compute service that runs code in response to events.

### Cold Start
Initial latency when Lambda function is invoked after being idle.

### Execution Environment
Isolated container where Lambda function runs.

### Lambda Layer
ZIP archive containing dependencies, libraries, or custom runtime.

### Lambda Version
Snapshot of function code and configuration at publication time.

### Lambda Alias
Pointer to specific version, enables traffic shifting.

### Provisioned Concurrency
Pre-warmed Lambda instances to eliminate cold starts.

### Dead Letter Queue
Destination for failed event processing after retries.

### AWS Lambda
Serverless functions as a service.

### Amazon ECS
Container orchestration service for Docker.

### Task Definition
Blueprint specifying container configuration for ECS.

### ECS Service
Manages desired task count and scaling.

### Fargate
Serverless compute engine for containers.

### EC2 Launch Type
ECS with user-managed EC2 instances.

### Amazon EKS
Managed Kubernetes service.

### Kubernetes Pod
Smallest deployable unit containing one or more containers.

### kubectl
Kubernetes command-line tool.

### EKS Control Plane
Managed Kubernetes master nodes.

### Node Group
Worker nodes in EKS cluster.

### HPA
Horizontal Pod Autoscaler for Kubernetes.

### Karpenter
Kubernetes node autoscaler for EKS.

### Helm
Package manager for Kubernetes.

### Kubernetes Deployment
Manages pod replicas and updates.

### Kubernetes Service
Exposes pods as network service.

### Ingress
HTTP/HTTPS routing for Kubernetes.

### AWSVPC Mode
ECS networking where each task gets ENI.

### Event Source Mapping
Lambda trigger from stream/queue services.

### Lambda@Edge
Lambda functions at CloudFront edge locations.

### Serverless Application Model
Framework for building serverless applications.

### Container Image
Lightweight, standalone executable package.