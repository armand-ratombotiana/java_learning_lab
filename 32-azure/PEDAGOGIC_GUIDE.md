# Pedagogic Guide - Azure

## Learning Path

### Phase 1: Core Concepts
1. Azure resource groups and subscriptions
2. Azure Storage account types
3. Azure SDK authentication methods
4. Resource naming conventions

### Phase 2: Storage Services
1. Blob Storage for unstructured data
2. Queue Storage for messaging
3. Table Storage for NoSQL
4. File shares for SMB access

### Phase 3: Compute Services
1. Azure Functions - event-driven serverless
2. App Service for web applications
3. Azure Spring Apps for Java microservices
4. Container instances

### Phase 4: Data Services
1. Cosmos DB for multi-model NoSQL
2. Azure SQL Database for relational
3. Azure Cache for Redis
4. Data Factory for ETL

### Phase 5: Security & Identity
1. Azure Active Directory
2. Managed identities
3. Azure Key Vault integration
4. Role-based access control (RBAC)

## Interview Topics

| Topic | Description |
|-------|-------------|
| Blob Storage | Hot vs Cool vs Archive tiers |
| Consistency | Eventual vs Session vs Strong |
| Scaling | Cosmos DB RU allocation |
| Serverless | Function triggers and bindings |
| Security | Shared keys vs RBAC vs OAuth |

## Azure vs AWS Comparison

| Service | Azure | AWS |
|---------|-------|-----|
| Compute | Azure Functions | Lambda |
| Storage | Blob Storage | S3 |
| NoSQL | Cosmos DB | DynamoDB |
| Messaging | Queue Storage | SQS |
| Cache | Azure Cache | ElastiCache |

## SDK Authentication

```java
DefaultAzureCredential credential = new DefaultAzureCredentialBuilder()
    .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
    .build();
```

## Next Steps
- Explore Azure Event Grid for event-driven architecture
- Learn Azure Service Bus for enterprise messaging
- Study Azure Monitor for observability