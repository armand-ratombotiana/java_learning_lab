# Exercises - Azure

## Exercise 1: Blob Storage Operations
Implement file storage operations:

1. Create a BlobServiceClient with connection string
2. Upload a file to Blob Storage with metadata
3. Download a blob to local file system
4. List all blobs in a container with pagination
5. Set access tier (Hot, Cool, Archive) for blobs

## Exercise 2: Queue Messaging
Implement queue-based messaging:

1. Create QueueClient and ensure queue exists
2. Send messages with visibility timeout
3. Receive and process messages with polling
4. Update message content and extend visibility
5. Implement dead-letter queue handling

## Exercise 3: Azure Functions Integration
Build serverless functions:

1. Create timer-triggered function for scheduled tasks
2. Implement HTTP-triggered function with query params
3. Add Blob trigger for file processing
4. Configure function bindings for input/output
5. Deploy function with Azure CLI

## Exercise 4: Cosmos DB Integration
Implement NoSQL database operations:

1. Create CosmosClient with connection string
2. Create database and container with partition key
3. Insert documents with custom JSON structure
4. Query with SQL-like syntax and pagination
5. Implement change feed processor

## Exercise 5: Key Vault Integration
Securely manage secrets:

1. Create SecretClient using DefaultAzureCredential
2. Store and retrieve secrets with version tracking
3. List all secrets in a vault
4. Implement secret rotation pattern
5. Configure access policies for applications

## Bonus Challenge
Build a multi-service Azure application that:
- Uses Blob Storage for document upload
- Triggers Azure Function on upload
- Stores metadata in Cosmos DB
- Sends notification via Queue Storage
- Uses Key Vault for all connection strings