# Serverless Functions

## Overview
Serverless computing allows developers to build and run applications without managing infrastructure, scaling automatically.

## Key Features
- Cloud function providers (AWS, GCP, Azure)
- Function as a Service (FaaS)
- Cold start optimization
- Pay-per-invocation pricing
- Event-driven triggers

## Project Structure
```
58-serverless/
  functions-framework/
    src/main/java/com/learning/serverless/functions/ServerlessLab.java
```

## Running
```bash
cd 58-serverless/functions-framework
mvn compile exec:java
```

## Concepts Covered
- Cloud function handlers
- Serverless Framework
- Function deployment
- Trigger types

## Dependencies
- Spring Cloud Functions