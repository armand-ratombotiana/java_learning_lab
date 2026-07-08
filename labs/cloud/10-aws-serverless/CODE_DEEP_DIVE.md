# 10 — AWS Serverless — Code Deep Dive

## 1. Advanced Lambda with Java

### Lambda Handler with SnapStart
```java
package com.cloud.awssrvls;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map;

public class OrderHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final DynamoDbClient dynamoDb;
    private final SnsClient sns;

    // Initialized once during SnapStart snapshot
    public OrderHandler() {
        this.dynamoDb = DynamoDbClient.builder()
            .region(Region.US_EAST_1)
            .build();
        this.sns = SnsClient.create();
    }

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        String orderId = (String) event.get("orderId");
        String userId = (String) event.get("userId");
        double amount = (double) event.get("amount");

        // Save to DynamoDB
        Map<String, AttributeValue> item = Map.of(
            "PK", AttributeValue.fromS("ORDER#" + orderId),
            "SK", AttributeValue.fromS("USER#" + userId),
            "amount", AttributeValue.fromN(String.valueOf(amount)),
            "status", AttributeValue.fromS("CREATED"),
            "ttl", AttributeValue.fromN(String.valueOf(Instant.now().getEpochSecond() + 86400))
        );
        dynamoDb.putItem(PutItemRequest.builder()
            .tableName("orders")
            .item(item)
            .build());

        // Publish confirmation event
        sns.publish(PublishRequest.builder()
            .topicArn(System.getenv("ORDER_TOPIC_ARN"))
            .message("Order " + orderId + " created for $" + amount)
            .subject("Order Confirmation")
            .build());

        return Map.of(
            "statusCode", 201,
            "body", Map.of("orderId", orderId, "status", "CREATED")
        );
    }
}
```

### Lambda Function URL Handler
```java
public class FunctionUrlHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        String httpMethod = (String) event.get("requestContext");
        String path = (String) event.get("rawPath");
        String body = (String) event.get("body");
        return Map.of(
            "statusCode", 200,
            "headers", Map.of("Content-Type", "application/json"),
            "body", "{\"message\":\"Hello from Lambda URL!\", \"path\":\"" + path + "\"}"
        );
    }
}
```

## 2. Step Functions Workflow

### Workflow State Machine Definition (JSON)
```json
{
  "Comment": "Order Processing Workflow",
  "StartAt": "ValidateOrder",
  "States": {
    "ValidateOrder": {
      "Type": "Task",
      "Resource": "arn:aws:lambda:us-east-1:123456789012:function:ValidateOrder",
      "Next": "ProcessPayment",
      "Catch": [{
        "ErrorEquals": ["OrderValidationError"],
        "Next": "NotifyFailure"
      }]
    },
    "ProcessPayment": {
      "Type": "Task",
      "Resource": "arn:aws:states:::lambda:invoke",
      "Parameters": {
        "FunctionName": "arn:aws:lambda:us-east-1:123456789012:function:ProcessPayment",
        "Payload.$": "$"
      },
      "Next": "FulfillOrder",
      "Retry": [{
        "ErrorEquals": ["States.TaskFailed"],
        "IntervalSeconds": 3,
        "MaxAttempts": 2,
        "BackoffRate": 2.0
      }]
    },
    "FulfillOrder": {
      "Type": "Task",
      "Resource": "arn:aws:states:::sns:publish",
      "Parameters": {
        "TopicArn": "arn:aws:sns:us-east-1:123456789012:fulfillment",
        "Message.$": "$"
      },
      "End": true
    },
    "NotifyFailure": {
      "Type": "Task",
      "Resource": "arn:aws:states:::sns:publish",
      "Parameters": {
        "TopicArn": "arn:aws:sns:us-east-1:123456789012:failures",
        "Message.$": "$"
      },
      "End": true
    }
  }
}
```

### Step Functions Client in Java
```java
SfnClient sfn = SfnClient.create();

StartExecutionResponse execution = sfn.startExecution(StartExecutionRequest.builder()
    .stateMachineArn("arn:aws:states:us-east-1:123456789012:stateMachine:OrderProcessor")
    .input("{\"orderId\":\"ORD-123\",\"userId\":\"USR-456\",\"amount\":99.99}")
    .name("exec-" + UUID.randomUUID())
    .build());

System.out.println("Execution ARN: " + execution.executionArn());

// Describe execution status
DescribeExecutionResponse status = sfn.describeExecution(DescribeExecutionRequest.builder()
    .executionArn(execution.executionArn())
    .build());
System.out.println("Status: " + status.status());
```

## 3. EventBridge Event Publishing

### Publishing Custom Events
```java
EventBridgeClient eventBridge = EventBridgeClient.create();

PutEventsResponse response = eventBridge.putEvents(PutEventsRequest.builder()
    .entries(PutEventsRequestEntry.builder()
        .eventBusName("custom-events")
        .source("com.myapp.order")
        .detailType("OrderCreated")
        .detail("""
            {"orderId":"ORD-789","userId":"USR-012","items":[{"sku":"SKU-001","qty":2}]}""")
        .resources("arn:aws:dynamodb:us-east-1:123456789012:table/orders")
        .time(Instant.now())
        .build())
    .build());

System.out.println("Events sent: " + response.entryCount());
```

### Event Pattern Filtering
```java
// Java representation of EventBridge pattern
public class EventPattern {
    private String source;
    private String detailType;
    private Map<String, Object> detail;

    public boolean matches(EventBridgeEvent event) {
        if (source != null && !source.equals(event.source())) return false;
        if (detailType != null && !detailType.equals(event.detailType())) return false;
        if (detail != null) return matchDetail(event.detail());
        return true;
    }
}
```
