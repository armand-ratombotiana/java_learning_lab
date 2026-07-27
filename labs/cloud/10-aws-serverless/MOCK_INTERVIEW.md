# Mock Interview — AWS Serverless

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Architecture
- **Difficulty**: Professional

## Warm-Up (5 min)

Q1: What is the difference between Lambda, API Gateway, and Step Functions?

Q2: Explain the Lambda execution lifecycle (cold start vs warm start). What factors affect cold start time?

## Technical Questions (20 min)

### Question 1: Lambda + API Gateway Design (10 min)
Design a serverless REST API for a note-taking application with CRUD operations. Consider:
- Authentication (Cognito or API Key)
- Rate limiting
- Request validation
- Caching
- Error handling

**How would you structure the API Gateway routes and Lambda functions?**

### Question 2: Step Functions Workflow (10 min)
Design a serverless order processing workflow:
1. Receive order (API Gateway)
2. Validate payment (charge credit card)
3. Check inventory
4. If in stock: ship order, send confirmation email
5. If out of stock: notify customer, offer refund or backorder
6. If payment fails: retry 3 times, then cancel order

**Choose services**: Step Functions, Lambda, SQS, SNS, DynamoDB

## Behavioral Question (10 min)

**Question**: Tell me about a time you migrated from a server-based architecture to serverless. What were the benefits and challenges?

## System Design Whiteboard (10 min)

**Problem**: Design a serverless event-driven data pipeline:
- Ingest 1M events/day from IoT devices
- Process and enrich events
- Store in DynamoDB (real-time queries) and S3 (data lake)
- Alert on anomalies (e.g., temperature > 100°C)
- Dashboard with real-time metrics

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| Lambda | Concurrency, VPC, DLQ, power tuning, layers | Basic function | Simple function only |
| API Gateway | Authorizers, throttling, caching, validation | Basic REST API | Single endpoint |
| Step Functions | Error handling, retry, parallel, wait states | Linear workflow | No error handling |
| Event-Driven | EventBridge, SQS, SNS, Kinesis | Basic S3 trigger | Only synchronous |
| Cold Start | SnapStart, provisioned concurrency, warmers | Aware of the issue | No optimization |

## Sample Solution Outline

### Serverless REST API
- API Gateway REST API with Cognito User Pools authorizer
- Resource: `/notes` with GET, POST; `/notes/{id}` with GET, PUT, DELETE
- Lambda functions per resource or single function with routing
- DynamoDB table: PK = userId, SK = noteId, with GSI on createdAt
- Usage plans for rate limiting (1000 req/s burst, 500 req/s sustained)
- API Gateway caching (300s TTL for GET /notes)
- Request validation in API Gateway for POST/PUT body schema
- Lambda Powertools for Java for structured logging, tracing
- Dead Letter Queue for failed event processing

### Order Processing Step Functions
```
OrderReceived → ValidatePayment → CheckInventory
    │               │                    │
    │               ▼                    ├── InStock → ShipOrder → SendEmail
    │         Payment Failed ─┐           │
    │               │        │           └── OutOfStock → NotifyCustomer
    │               ▼        │                             │
    │          Retry(3x) ────┤                             ├── Refund
    │               │        │                             │
    │          MaxRetries → CancelOrder                    └── Backorder
    │               │
    └───────────────┘
```
- Error handling: `Retry` with exponential backoff, `Catch` with DLQ
- Parallel state for ShipOrder + SendEmail
- Wait state for backorder confirmation timeout

### IoT Data Pipeline
- IoT Core rule → Kinesis Firehose → S3 (raw data)
- Firehose → Lambda (enrichment) → S3 (processed data)
- Firehose → Lambda → DynamoDB (real-time)
- Kinesis Analytics for anomaly detection
- EventBridge for alerting (CloudWatch Alarm on anomaly metric)
- QuickSight for dashboard from S3/Athena
