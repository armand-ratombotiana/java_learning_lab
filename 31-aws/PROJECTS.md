# AWS Module - PROJECTS.md

---

# Mini-Project: AWS Integration

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: AWS SDK, S3 Bucket Operations, EC2 Management, Lambda Functions

This mini-project demonstrates AWS integration for Java applications.

---

## Project Structure

```
31-aws/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── service/
│   │   ├── S3Service.java
│   │   └── LambdaService.java
│   └── config/
│       └── AwsConfig.java
```

---

## POM.xml

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>aws-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    <dependencies>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>s3</artifactId>
            <version>2.21.0</version>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>lambda</artifactId>
            <version>2.21.0</version>
        </dependency>
    </dependencies>
</project>
```

---

## Implementation

```java
// config/AwsConfig.java
package com.learning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {
    
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }
}
```

```java
// service/S3Service.java
package com.learning.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;

@Service
public class S3Service {
    
    private final S3Client s3Client;
    
    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
    public void uploadFile(String bucketName, String key, File file) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        s3Client.putObject(request, RequestBody.fromFile(file));
        System.out.println("File uploaded: " + key);
    }
    
    public void downloadFile(String bucketName, String key, String destination) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        s3Client.getObject(request, new File(destination).toPath());
        System.out.println("File downloaded: " + key);
    }
    
    public void listFiles(String bucketName) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
            .bucket(bucketName)
            .build();
        
        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        
        System.out.println("Files in bucket:");
        response.contents().forEach(obj -> 
            System.out.println("  - " + obj.key()));
    }
    
    public void deleteFile(String bucketName, String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        s3Client.deleteObject(request);
        System.out.println("File deleted: " + key);
    }
}
```

```java
// service/LambdaService.java
package com.learning.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

import java.nio.ByteBuffer;

@Service
public class LambdaService {
    
    private final LambdaClient lambdaClient;
    
    public LambdaService(LambdaClient lambdaClient) {
        this.lambdaClient = lambdaClient;
    }
    
    public String invokeFunction(String functionName, String payload) {
        InvokeRequest request = InvokeRequest.builder()
            .functionName(functionName)
            .payload(ByteBuffer.wrap(payload.getBytes()))
            .build();
        
        InvokeResponse response = lambdaClient.invoke(request);
        
        String result = response.payload().asUtf8String();
        System.out.println("Lambda response: " + result);
        
        return result;
    }
}
```

```java
// Main.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Build Instructions

```bash
cd 31-aws
mvn clean package
# Configure AWS credentials before running
export AWS_ACCESS_KEY_ID=your_key
export AWS_SECRET_ACCESS_KEY=your_secret
mvn spring-boot:run
```

---

# Real-World Project: EC2 Management

```java
// Advanced EC2 Service
@Service
public class EC2Service {
    
    public void listInstances() {
        Ec2Client ec2 = Ec2Client.create();
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
        ec2.describeInstances(request);
    }
}
```

---

## Build Instructions

```bash
cd 31-aws
mvn clean compile
mvn spring-boot:run
```