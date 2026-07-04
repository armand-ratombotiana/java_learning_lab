# Interview Questions — AWS Compute

## Beginner

**Q1**: What is the difference between EC2 and Lambda?

**Q2**: Explain the difference between ECS and EKS.

**Q3**: What is a Lambda cold start and why does it affect Java?

**Q4**: How does Auto Scaling work with an ALB?

## Intermediate

**Q5**: How would you design a Java microservice on Lambda and handle database connections?

**Q6**: Compare Fargate vs EC2 launch type for ECS. When would you use each?

**Q7**: What is SnapStart and how does it improve Java Lambda performance?

**Q8**: Explain how Lambda scales. What are the concurrency limits?

## Advanced

**Q9**: Design a multi-tenant Java application on Lambda. How do you isolate tenants?

**Q10**: Your ECS task with a Java Spring Boot app keeps getting OOM-killed. Debug.

**Q11**: How does AWS Nitro offload networking and storage from the hypervisor?

**Q12**: You need to migrate a 20-year-old Java Servlet monolith on EC2 to serverless. Walk through the strategy.

## Sample Answers

**A1**: EC2 is virtual servers (you manage OS, runtime, scaling). Lambda is functions-as-a-service (AWS manages everything, code runs up to 15 min, scales per invocation).

**A2**: ECS is AWS-native Docker orchestration (simpler, deeper AWS integration). EKS is managed Kubernetes (portable, standard K8s API, larger ecosystem).

**A3**: Cold start is the delay when Lambda initializes a new execution environment. Java is affected because JVM startup, class loading, and Spring DI initialization take 2-8 seconds. Mitigations: SnapStart, Provisioned Concurrency, GraalVM native-image.

**A4**: Auto Scaling Group maintains instance count. ALB health check determines instance health. Scale-out policy adds instances when request count per target exceeds threshold. Scale-in removes low-utilization instances. Cooldown prevents rapid fluctuations.

## Key Topics for AWS Developer Exam
- Lambda triggers (S3, SQS, API Gateway, DynamoDB Streams)
- Lambda versions, aliases, and canary deployments
- ECS task definitions, services, and auto scaling
- EKS pods, deployments, and services (K8s fundamentals)
- Elastic Beanstalk deployment modes (all at once, rolling, immutable)
- Auto Scaling policies (target tracking, step, simple)
- Spot Instance best practices and interruption handling
