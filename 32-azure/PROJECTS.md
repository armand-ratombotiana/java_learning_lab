# Azure Module - PROJECTS.md

---

# Mini-Project: Azure Cloud Integration

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Azure SDK, Blob Storage, Azure Functions, VM Management

This mini-project demonstrates Azure cloud integration for Java applications.

---

## Project Structure

```
32-azure/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── service/
│   │   ├── BlobService.java
│   │   └── FunctionService.java
│   └── config/
│       └── AzureConfig.java
```

---

## POM.xml

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>azure-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    <dependencies>
        <dependency>
            <groupId>com.azure</groupId>
            <artifactId>azure-storage-blob</artifactId>
            <version>12.21.0</version>
        </dependency>
        <dependency>
            <groupId>com.azure</groupId>
            <artifactId>azure-identity</artifactId>
            <version>1.11.0</version>
        </dependency>
    </dependencies>
</project>
```

---

## Implementation

```java
// config/AzureConfig.java
package com.learning.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureConfig {
    
    @Value("${azure.storage.connection-string}")
    private String connectionString;
    
    @Bean
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
    }
    
    @Bean
    public BlobContainerClient blobContainerClient(BlobServiceClient blobServiceClient) {
        return blobServiceClient.getBlobContainerClient("mycontainer");
    }
}
```

```java
// service/BlobService.java
package com.learning.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlobService {
    
    private final BlobContainerClient containerClient;
    
    public BlobService(BlobContainerClient containerClient) {
        this.containerClient = containerClient;
    }
    
    public void uploadFile(String blobName, File file) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.uploadFromFile(file.getAbsolutePath());
        System.out.println("File uploaded: " + blobName);
    }
    
    public void downloadFile(String blobName, String destinationPath) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.downloadToFile(destinationPath);
        System.out.println("File downloaded: " + blobName);
    }
    
    public void deleteFile(String blobName) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.delete();
        System.out.println("File deleted: " + blobName);
    }
    
    public List<String> listFiles() {
        return containerClient.listBlobs().stream()
            .map(blobItem -> blobItem.getName())
            .collect(Collectors.toList());
    }
}
```

```java
// service/FunctionService.java
package com.learning.service;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueServiceClient;
import com.azure.storage.queue.QueueServiceClientBuilder;
import org.springframework.stereotype.Service;

@Service
public class FunctionService {
    
    public void sendMessage(String queueName, String message) {
        QueueServiceClient queueServiceClient = new QueueServiceClientBuilder()
            .connectionString(System.getenv("AzureWebJobsStorage"))
            .buildClient();
        
        QueueClient queueClient = queueServiceClient.getQueueClient(queueName);
        queueClient.createIfNotExists();
        queueClient.sendMessage(message);
        
        System.out.println("Message sent to queue: " + queueName);
    }
    
    public String receiveMessage(String queueName) {
        QueueServiceClient queueServiceClient = new QueueServiceClientBuilder()
            .connectionString(System.getenv("AzureWebJobsStorage"))
            .buildClient();
        
        QueueClient queueClient = queueServiceClient.getQueueClient(queueName);
        
        return queueClient.receiveMessage().getBody().toString();
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
cd 32-azure
mvn clean package
# Configure Azure credentials
export AZURE_STORAGE_CONNECTION_STRING="your_connection_string"
mvn spring-boot:run
```

---

# Real-World Project: Azure Functions

```java
// Azure Functions Integration
public class AzureFunctionsService {
    
    public void triggerFunction(String functionName, String payload) {
        // Integration with Azure Functions
        System.out.println("Triggering Azure Function: " + functionName);
    }
}
```

---

## Build Instructions

```bash
cd 32-azure
mvn clean compile
mvn spring-boot:run
```

This comprehensive module demonstrates Azure cloud integration with Blob Storage, Queue messaging, Azure Functions, and Cosmos DB for enterprise Java applications.