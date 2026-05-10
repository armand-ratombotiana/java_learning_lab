# Azure Solution

## Concepts Covered

### Azure Blob Storage
- Blob upload/download
- Container operations
- SAS tokens for secure access
- Blob lifecycle management

### Azure Functions
- Blob Trigger - process new blobs
- Timer Trigger - scheduled tasks
- HTTP Trigger - REST endpoints
- Durable Functions for workflows

### Azure Cosmos DB
- Database and container creation
- CRUD operations
- Partition key management
- SQL query support

### Azure Service Bus
- Queue messaging
- Topic subscriptions

## Dependencies

```xml
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-storage-blob</artifactId>
    <version>12.22.0</version>
</dependency>
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-cosmos</artifactId>
    <version>4.47.0</version>
</dependency>
<dependency>
    <groupId>com.azure.functions</groupId>
    <artifactId>azure-functions-java-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration

```yaml
azure:
  storage:
    connection-string: ${AZURE_STORAGE_CONNECTION_STRING}
  cosmos:
    endpoint: ${AZURE_COSMOS_ENDPOINT}
    key: ${AZURE_COSMOS_KEY}
```

## Running Tests

```bash
mvn test -Dtest=AzureSolutionTest
```