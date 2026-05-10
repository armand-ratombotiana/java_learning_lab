# 32 - Azure Learning Module

## Overview
Azure is Microsoft's cloud computing platform. This module covers Azure services integration with Java applications including Blob Storage, Azure Functions, Cosmos DB, and more.

## Module Structure
- `azure-demo/` - Spring Boot with Azure SDK implementation

## Technology Stack
- Spring Boot 3.x
- Azure SDK for Java
- Azure Identity
- Azure Storage Blob
- Maven

## Prerequisites
- Azure account with subscription
- Azure Storage account (for Blob examples)
- Azure Functions runtime (optional)
- Environment variables configured

## Key Features
- Blob Storage for file storage
- Queue Storage for messaging
- Azure Functions serverless
- Cosmos DB NoSQL integration
- Azure Active Directory authentication
- Resource management

## Build & Run
```bash
cd azure-demo
mvn clean install
mvn spring-boot:run
```

## Default Configuration
- Storage connection via environment variable
- Default container: `mycontainer`

## Related Modules
- 31-mongodb (NoSQL comparison)
- 34-rabbitmq (messaging comparison)

## Azure Services Covered
1. **Azure Storage** - Blob, Queue, Table
2. **Azure Functions** - Serverless compute
3. **Azure Cosmos DB** - Multi-model database
4. **Azure Key Vault** - Secrets management