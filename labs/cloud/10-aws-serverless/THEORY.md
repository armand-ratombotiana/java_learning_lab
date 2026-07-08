# 09 — AWS Serverless — Theory

## Overview
AWS Serverless computing enables building and running applications without managing servers. This lab covers advanced Lambda, Step Functions, EventBridge, and the SAM framework.

## 1. AWS Lambda Advanced

### Execution Environment Lifecycle
```
Cold Start -> Init (extension init, runtime init, function init) -> Invoke -> Shutdown
                |                                                         |
                +-------- Warm Start (reused environment) ----------------+
```

### Cold Start Optimization
- **Lambda SnapStart**: Pre-initialize execution environment and snapshot the memory state
- **Reserved Concurrency**: Keep N environments warm (costs for idle time)
- **Provisioned Concurrency**: Pre-initialize environments for predictable latency
- **Runtime**: Java cold starts are ~3-10s; SnapStart reduces to ~200ms
- **Classpath**: Minimize JAR size with Lambda layers for shared dependencies

### Lambda Limits
| Resource | Limit |
|----------|-------|
| Memory | 128 MB - 10,240 MB |
| Ephemeral storage | 512 MB - 10,240 MB |
| Execution timeout | 15 minutes |
| Payload size | 6 MB (sync), 256 KB (async) |
| /tmp directory | 512 MB - 10,240 MB |
| Concurrent executions | 1,000 (soft, can be increased) |

### Event Sources
- **Synchronous**: API Gateway, ELB, Cognito, Lex, Alexa
- **Asynchronous**: S3, SNS, EventBridge, SES, CloudFormation
- **Stream-based**: Kinesis, DynamoDB Streams, SQS

### Dead Letter Queues (DLQ)
When async invocation fails after retries (2 retries by default), the event is sent to a DLQ (SQS or SNS) for further processing.

### Lambda@Edge
Run Lambda functions at CloudFront edge locations for low-latency response to viewer requests. Use cases include A/B testing, user authentication, SEO optimization.

## 2. Step Functions

### Workflow Types
- **Standard Workflows**: Long-running (up to 1 year), exactly-once execution
- **Express Workflows**: Short-lived (up to 5 minutes), at-least-once execution

### States
- **Task**: Single unit of work (Lambda, Activity, service integration)
- **Choice**: Branching logic based on input
- **Wait**: Delay execution for specified time
- **Parallel**: Execute branches concurrently
- **Map**: Iterate over items in an array
- **Pass**: Transform input to output
- **Fail/Succeed**: Terminate execution

### Error Handling
- **Retry**: Automatic retry with exponential backoff
- **Catсh**: Handle specific errors with fallback paths
- **Timeout**: Configure max execution time per state or workflow

### Service Integrations
Step Functions integrates with over 200 AWS services using optimized API calls. Common integrations include Lambda, ECS, DynamoDB, SNS, SQS, Batch, Glue, and SageMaker.

## 3. EventBridge

### Event Bus Types
- **Default Bus**: Receives events from AWS services in the account
- **Custom Bus**: Receives events from custom applications
- **Partner Bus**: Receives events from SaaS partners (Datadog, Zendesk, PagerDuty)

### Event Pattern Matching
EventBridge uses event patterns (similar to IAM policies) to match incoming events. Patterns can match on:
- Source (aws.ec2, custom.myapp)
- Event type (EC2 Instance State-change Notification)
- Detail fields (instanceId, state)
- Numeric, string, and IP address matching

### Pipes
A simpler alternative to EventBridge rules for point-to-point integrations between sources and targets. Supports filtering, enrichment, and transformation.

### Schema Registry
Discover and manage event schemas. Generate Java classes from schemas for type-safe event processing.

## 4. SAM (Serverless Application Model)

### SAM Template Structure
```yaml
AWSTemplateFormatVersion: "2010-09-09"
Transform: AWS::Serverless-2016-10-31
Resources:
  MyFunction:
    Type: AWS::Serverless::Function
    Properties:
      CodeUri: target/my-app.jar
      Handler: com.example.Handler::handleRequest
      Runtime: java21
      Events:
        ApiEvent:
          Type: Api
          Properties:
            Path: /hello
            Method: GET
```

### SAM CLI Commands
- `sam build`: Build locally using provided build system (Maven, Gradle)
- `sam local invoke`: Invoke Lambda locally for testing
- `sam local start-api`: Run API Gateway locally
- `sam deploy`: Package and deploy to AWS
- `sam sync`: Auto-sync local changes to AWS

### SAM Policy Templates
Pre-built policy statements for common use cases:
- `S3CrudPolicy`: Read/write access to an S3 bucket
- `DynamoDBCrudPolicy`: CRUD operations on DynamoDB table
- `SQSPollerPolicy`: Poll messages from SQS queue

## 5. Event-Driven Architecture Patterns

### Saga Pattern
Distributed transaction pattern using Step Functions or EventBridge:
- Each service publishes events after completing its work
- Next service in the saga subscribes to the event
- Compensation events roll back on failure

### Fan-Out Pattern
SNS + SQS pattern for parallel processing:
```
Event -> SNS Topic -> SQS Queue A -> Consumer A
                   -> SQS Queue B -> Consumer B
                   -> SQS Queue C -> Consumer C
```

### CQRS with Event Sourcing
- Commands: API Gateway -> Lambda -> DynamoDB
- Events: DynamoDB Streams -> EventBridge -> Other services
- Queries: API Gateway -> Lambda -> Read-optimized database

## Key Takeaways
1. Lambda cold starts can be mitigated with SnapStart and Provisioned Concurrency
2. Step Functions orchestrate complex workflows with built-in error handling
3. EventBridge enables event-driven architectures across AWS and SaaS
4. SAM simplifies serverless application packaging and deployment
5. Design for failure with DLQs, retries, and idempotency
