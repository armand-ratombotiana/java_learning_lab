# Jaeger Solution

## Overview
This module covers distributed tracing with Jaeger.

## Key Features

### Tracing
- Span creation
- Parent-child relationships
- Context propagation

### Integration
- OpenTracing API
- B3 propagation
- Baggage propagation

### Best Practices
- Naming conventions
- Span granularity
- Sampling strategies

## Usage

```java
JaegerSolution solution = new JaegerSolution("my-service");

// Start span
Span span = solution.startSpan("operation");

// Add tags
span.setTag("component", "http");
span.log("Operation completed");

// Set baggage
solution.setBaggageItem(span, "user-id", "123");

// Finish
span.finish();
```

## Dependencies
- Jaeger client
- OpenTracing API
- JUnit 5 for testing