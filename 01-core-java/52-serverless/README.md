# 52 - Serverless

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Status](https://img.shields.io/badge/Status-Complete-success?style=for-the-badge)

**Function-as-a-Service and cloud-native serverless computing**

</div>

---

## Overview

This module covers serverless computing concepts, AWS Lambda implementation, event-driven architecture, and cold start optimization.

### Learning Objectives

- Understand FaaS (Function-as-a-Service) paradigm
- Implement Lambda functions with Java
- Handle event-driven architectures
- Optimize cold start performance

---

## Topics Covered

### Lambda Functions
- Handler signature: `handle(Request, Context)`
- Context objects: requestId, memory, timeout, region
- AWS Lambda SDK integration

### Event Sources
- API Gateway (HTTP requests)
- S3, SQS, DynamoDB Streams, Kinesis
- EventBridge, CloudWatch events

### Cold Start
- Container initialization (500ms-3s)
- JVM startup, class loading
- Mitigations: SnapStart, provisioned concurrency, GraalVM

### Cloud Providers
- AWS Lambda: Java 21, SnapStart, 10GB memory
- Azure Functions: Java 17, Premium plan
- Google Cloud Functions: 1st/2nd gen

---

## Running the Module

```bash
cd 01-core-java/52-serverless
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.serverless.Lab"
```

---

## Key Concepts

| Concept | Description |
|---------|-------------|
| Cold Start | First invocation after idle |
| Warm Start | Container reuse |
| Stateless Design | No local state, use external stores |
| Idempotency | Safe to retry |

---

## Best Practices

- Minimize package size (use GraalVM)
- Lazy initialize heavy resources
- Use structured JSON logging
- Implement dead-letter queues

---

<div align="center">

[Exercises](./EXERCISES.md) | [Pedagogic Guide](./PEDAGOGIC_GUIDE.md)

</div>