# Reflection — AWS Compute

## Self-Assessment

| Skill | Know | Explain | Teach |
|-------|:----:|:-------:|:-----:|
| Lambda function creation and invocation | ☐ | ☐ | ☐ |
| Handling Java cold starts with SnapStart | ☐ | ☐ | ☐ |
| ECS task definition and service configuration | ☐ | ☐ | ☐ |
| Fargate vs EC2 launch type | ☐ | ☐ | ☐ |
| EKS pod, deployment, service concepts | ☐ | ☐ | ☐ |
| Elastic Beanstalk deployment workflows | ☐ | ☐ | ☐ |
| Auto Scaling policies and cooldowns | ☐ | ☐ | ☐ |
| Lambda security (execution role, VPC, secrets) | ☐ | ☐ | ☐ |
| Spot Instance strategy for EC2 | ☐ | ☐ | ☐ |

## Journal Prompts

1. Which compute model (EC2, Lambda, ECS, EKS) feels most natural for your next Java project?

2. How does Lambda's per-millisecond billing change how you think about application cost?

3. What concerns do you have about Java on Lambda? How would you address them?

4. Why would you choose Fargate over EC2 for containers? What's the trade-off?

5. How would you migrate an existing Java Spring Boot app from EC2 to ECS without downtime?

## Key Takeaways
- Compute spectrum: EC2 (full control) → ECS/EKS (containers) → Fargate (serverless containers) → Lambda (functions)
- Java on Lambda needs SnapStart, Provisioned Concurrency, or GraalVM for good cold-start performance
- ECS is simpler than EKS for AWS-native teams; EKS is better for multi-cloud or K8s expertise
- Elastic Beanstalk is ideal for quick deployments but limited for complex architectures
- Always design for failure: multi-AZ, health checks, graceful shutdowns
