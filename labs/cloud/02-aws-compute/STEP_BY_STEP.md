# Step-by-Step — Deploy a Java Lambda Function

## Step 1: Create a Maven Project
```xml
<dependencies>
    <dependency>
        <groupId>com.amazonaws</groupId>
        <artifactId>aws-lambda-java-core</artifactId>
        <version>1.2.2</version>
    </dependency>
    <dependency>
        <groupId>com.amazonaws</groupId>
        <artifactId>aws-lambda-java-events</artifactId>
        <version>3.11.2</version>
    </dependency>
</dependencies>
```

## Step 2: Write Lambda Handler
```java
// Handler.java
package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map;

public class Handler implements RequestHandler<Map<String, String>, String> {
    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        String name = event.getOrDefault("name", "World");
        return String.format("Hello, %s!", name);
    }
}
```

## Step 3: Build Deployable Artifact
```powershell
# Build fat JAR
mvn clean package shade:shade

# Verify
ls target/lambda-java-1.0-SNAPSHOT.jar
```

## Step 4: Create Lambda Function
```powershell
aws lambda create-function --function-name java-hello `
    --runtime java17 --role arn:aws:iam::xxx:role/lambda-basic `
    --handler com.example.Handler::handleRequest `
    --zip-file fileb://target/lambda-java-1.0-SNAPSHOT.jar `
    --memory-size 512 --timeout 30
```

## Step 5: Invoke the Function
```powershell
aws lambda invoke --function-name java-hello `
    --payload '{"name":"AWS"}' response.json
cat response.json
# "Hello, AWS!"
```

## Step 6: Add API Gateway Trigger
```powershell
# Create REST API
aws apigateway create-rest-api --name "JavaHelloApi"

# Create resource, method (POST), and integration
# (See AWS docs for full commands)

# Deploy API
aws apigateway create-deployment --rest-api-id xxxxxx `
    --stage-name prod
```

## Step 7: Monitor with CloudWatch
```powershell
# View logs
aws logs tail /aws/lambda/java-hello --since 5m

# View metrics
aws cloudwatch get-metric-statistics --namespace AWS/Lambda `
    --metric-name Duration --statistics Average `
    --start-time 2024-01-01T00:00:00Z --end-time 2024-01-02T00:00:00Z `
    --period 3600
```

## Step 8: Clean Up
```powershell
aws lambda delete-function --function-name java-hello
aws apigateway delete-rest-api --rest-api-id xxxxxx
```
