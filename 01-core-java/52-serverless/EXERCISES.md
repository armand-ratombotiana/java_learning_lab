# Exercises: Serverless

---

## Easy Exercises (1-5)

### Exercise 1: Hello Lambda
Create a simple Lambda handler that returns "Hello, {name}!".

### Exercise 2: API Gateway Integration
Handle query parameters and return JSON responses.

### Exercise 3: S3 Event Handler
Process S3 object created events (print bucket/key).

### Exercise 4: Environment Variables
Read configuration from environment variables.

### Exercise 5: Simple Calculator
Implement add, subtract, multiply via query params.

---

## Medium Exercises (6-10)

### Exercise 6: Cold Start Logging
Add timing to measure cold vs warm invocation times.

### Exercise 7: Dead Letter Queue
Implement DLQ for failed processing.

### Exercise 8: Idempotent Handler
Design handler that handles duplicate events safely.

### Exercise 9: DynamoDB Integration
Read/write to DynamoDB table from Lambda.

### Exercise 10: Layer Creation
Create and use a Lambda layer for shared code.

---

## Hard Exercises (11-15)

### Exercise 11: Spring Boot on Lambda
Convert Spring Boot app to Lambda with Spring Cloud Function.

### Exercise 12: Custom Runtime
Build a custom Lambda runtime from scratch.

### Exercise 13: Container Image
Deploy Lambda as container image (ECR).

### Exercise 14: VPC Configuration
Configure Lambda in VPC with private subnet access.

### Exercise 15: Cost Optimization
Implement memory/timeout tuning for cost savings.

---

## Solutions Summary

| # | Exercise | Difficulty | Key Concepts |
|---|----------|------------|---------------|
| 1 | Hello Lambda | Easy | handler, context |
| 2 | API Gateway | Easy | request/response |
| 3 | S3 Events | Easy | event parsing |
| 4 | Env Variables | Easy | configuration |
| 5 | Calculator | Easy | business logic |
| 6 | Cold Start | Medium | timing, logging |
| 7 | DLQ | Medium | error handling |
| 8 | Idempotency | Medium | deduplication |
| 9 | DynamoDB | Medium | SDK, CRUD |
| 10 | Lambda Layers | Medium | code reuse |
| 11 | Spring Boot | Hard | framework |
| 12 | Custom Runtime | Hard | bootstrap |
| 13 | Container | Hard | ECR, images |
| 14 | VPC | Hard | networking |
| 15 | Cost | Hard | optimization |

---

<div align="center">

[Back to README](./README.md) | [Pedagogic Guide](./PEDAGOGIC_GUIDE.md)

</div>