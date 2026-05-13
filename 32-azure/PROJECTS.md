# Azure Module - PROJECTS.md

---

# Mini-Project 1: Azure Compute (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: Azure VMs, VMSS, Azure Container Instances

Deploy Java applications on Azure virtual machines and container instances.

---

## Project Structure

```
32-azure/
├── pom.xml
├── src/main/java/com/learning/
│   └── App.java
├── arm/
│   └── vm-template.json
└── scripts/
    └── deploy-azure.sh
```

---

## Implementation

```java
// App.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import java.util.HashMap;

@SpringBootApplication
@RestController
public class App {
    
    @Value("${azure.vm.name:local}")
    private String vmName;
    
    @Value("${azure.region:unknown}")
    private String region;
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @GetMapping("/")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Running on Azure VM!");
        response.put("vm", vmName);
        response.put("region", region);
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }
}
```

```json
// arm/vm-template.json
{
  "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#",
  "contentVersion": "1.0.0.0",
  "parameters": {
    "vmName": {
      "type": "string",
      "defaultValue": "app-vm"
    },
    "adminUsername": {
      "type": "string"
    },
    "adminPassword": {
      "type": "securestring"
    }
  },
  "resources": [
    {
      "type": "Microsoft.Compute/virtualMachines",
      "apiVersion": "2021-03-01",
      "name": "[parameters('vmName')]",
      "location": "[resourceGroup().location]",
      "properties": {
        "hardwareProfile": {
          "vmSize": "Standard_D2s_v3"
        },
        "osProfile": {
          "computerName": "[parameters('vmName')]",
          "adminUsername": "[parameters('adminUsername')]",
          "adminPassword": "[parameters('adminPassword')]"
        },
        "storageProfile": {
          "imageReference": {
            "publisher": "Canonical",
            "offer": "UbuntuServer",
            "sku": "18.04-LTS",
            "version": "latest"
          },
          "osDisk": {
            "createOption": "FromImage"
          }
        },
        "networkProfile": {
          "networkInterfaces": [
            {
              "id": "[resourceId('Microsoft.Network/networkInterfaces', 'app-nic')]"
            }
          ]
        }
      }
    }
  ]
}
```

---

## Build Instructions

```bash
cd 32-azure
mvn clean package -DskipTests

# Azure deployment
az group create --location eastus --name app-rg
az deployment group create --resource-group app-rg --template-file arm/vm-template.json
```

---

# Mini-Project 2: Azure Storage (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Blob Storage, Azure Functions, Queue Storage

Implement Azure storage solutions for Java applications.

---

## Project Structure

```
32-azure/
├── src/main/java/com/learning/
│   ├── service/
│   │   └── BlobStorageService.java
│   │   └── QueueService.java
│   └── config/
│       └── AzureConfig.java
```

---

## Implementation

```java
// service/BlobStorageService.java
package com.learning.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlobStorageService {
    
    private final BlobContainerClient containerClient;
    
    public BlobStorageService() {
        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
        
        this.containerClient = blobServiceClient.getBlobContainerClient("app-container");
        containerClient.createIfNotExists();
    }
    
    public void uploadFile(String blobName, File file) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.uploadFromFile(file.getAbsolutePath());
    }
    
    public void downloadFile(String blobName, String destination) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.downloadToFile(destination);
    }
    
    public List<String> listBlobs() {
        return containerClient.listBlobs().stream()
            .map(blobItem -> blobItem.getName())
            .collect(Collectors.toList());
    }
    
    public void deleteBlob(String blobName) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.delete();
    }
}
```

---

# Mini-Project 3: Azure Functions (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Azure Functions, Triggers, Bindings, Durable Functions

Build serverless functions with Azure Functions.

---

## Implementation

```java
// Azure Function
package com.learning;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import java.util.Optional;

public class HttpTriggerFunction {
    
    @FunctionName("HttpTrigger")
    public HttpResponseMessage execute(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, 
                     route = "api/{name}") HttpRequestMessage<Optional<String>> request,
        @BindingName("name") String name,
        ExecutionContext context) {
        
        context.getLogger().info("Processing request for: " + name);
        
        if (name == null || name.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Please provide a name")
                .build();
        }
        
        String response = String.format("Hello, %s! Welcome to Azure Functions.", name);
        
        return request.createResponseBuilder(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body(response)
            .build();
    }
}
```

---

# Mini-Project 4: Azure Kubernetes Service (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: AKS, Azure Container Registry, kubectl

Deploy containerized applications on Azure Kubernetes Service.

---

## Implementation

```yaml
# aks-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-aks
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-aks
  template:
    metadata:
      labels:
        app: app-aks
    spec:
      containers:
      - name: app
        image: appaks.azurecr.io/app:1.0.0
        ports:
        - containerPort: 8080
```

---

# Real-World Project: Azure Cloud Architecture (10+ hours)

## Project Overview

**Duration**: 10+ hours  
**Difficulty**: Advanced  
**Concepts Used**: ARM Templates, Azure DevOps, AKS, Azure SQL, Redis Cache

Build production-grade Azure infrastructure.

---

## Complete Implementation

```hcl
# Terraform for Azure
terraform {
  required_version = ">= 1.0"
  required_providers {
    azurerm = { source = "hashicorp/azurerm" }
  }
}

provider "azurerm" {
  features {}
  subscription_id = var.subscription_id
}

resource "azurerm_resource_group" "main" {
  name     = "app-rg"
  location = var.location
}

resource "azurerm_virtual_network" "main" {
  name                = "app-vnet"
  address_space       = ["10.0.0.0/16"]
  resource_group_name = azurerm_resource_group.main.name
  location            = azurerm_resource_group.main.location
}

resource "azurerm_kubernetes_cluster" "main" {
  name                = "app-aks"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  dns_prefix          = "appaks"
  
  default_node_pool {
    name       = "default"
    node_count = 3
    vm_size    = "Standard_D2s_v3"
  }
  
  identity {
    type = "SystemAssigned"
  }
}

resource "azurerm_sql_database" "main" {
  name                = "app-db"
  resource_group_name = azurerm_resource_group.main.location
  server_name         = azurerm_sql_server.main.name
  edition             = "Standard"
  requested_service_objective_name = "S0"
  
  tags = {
    environment = "production"
  }
}
```

---

## Build Instructions

```bash
cd 32-azure

# Build application
mvn clean package

# Azure deployment
az aks create --resource-group app-rg --name app-aks --node-count 3
az aks get-credentials --resource-group app-rg --name app-aks
kubectl apply -f aks-deployment.yaml
```