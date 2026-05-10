# Serverless Solution

## Overview
This module covers Lambda, functions, and triggers.

## Key Features

### Lambda Function
- Creating functions
- Setting runtime
- Configuring handler
- Setting role

### Invocation
- Creating invoke requests
- Parsing responses
- Handling events

### Configuration
- Environment variables
- Timeout settings
- Memory allocation

### Triggers
- S3 event triggers
- API Gateway triggers
- CloudWatch triggers

### API Gateway Response
- Creating HTTP responses
- Status codes
- Response bodies

## Usage

```java
ServerlessSolution solution = new ServerlessSolution();

// Create function
CreateFunctionRequest req = solution.createFunction("my-function", "java11", "com.example.Handler", "role-arn");
req = solution.withTimeout(req, 30);
req = solution.withMemory(req, 512);

// Invoke function
InvokeRequest invokeReq = solution.createInvokeRequest("my-function", "{\"input\":\"data\"}");

// Create API response
Map<String, Object> response = solution.createApiGatewayResponse(200, "{\"result\":\"ok\"}");
```

## Dependencies
- AWS Lambda SDK
- JUnit 5
- Mockito