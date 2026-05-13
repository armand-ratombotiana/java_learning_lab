# Quick Reference: Azure Cloud

<div align="center">

![Module](https://img.shields.io/badge/Module-32-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Azure-green?style=for-the-badge)

**Quick lookup guide for Azure services**

</div>

---

## 📋 Core Services

| Service | Purpose |
|---------|---------|
| **Azure Storage** | Blob, Queue, Table storage |
| **Azure Functions** | Serverless compute |
| **Cosmos DB** | Multi-model NoSQL |
| **Azure Key Vault** | Secrets management |
| **App Service** | Web app hosting |
| **AKS** | Kubernetes service |

---

## 🔑 Key Commands

### Azure CLI
```bash
# Login
az login

# Create resource group
az group create --name mygroup --location eastus

# Storage account
az storage account create --name mystorage --resource-group mygroup
az storage container create --name mycontainer --account-name mystorage

# Upload blob
az storage blob upload --file file.txt --container-name mycontainer --name blob.txt

# Azure Functions
func azure functionapp create --name myfunc --resource-group mygroup
func azure functionapp deploy --name myfunc --source-path ./target
```

---

## 📊 Java SDK Examples

### Blob Storage
```java
BlobServiceClient client = new BlobServiceClientBuilder()
    .connectionString(connString)
    .buildClient();

BlobContainerClient container = client.getBlobContainerClient("mycontainer");
BlobClient blob = container.getBlobClient("file.txt");
blob.uploadFromFile("localfile.txt");
```

### Queue Storage
```java
QueueClient queue = new QueueClientBuilder()
    .connectionString(connString)
    .queueName("myqueue")
    .buildClient();

queue.sendMessage("message content");
queue.receiveMessages().forEach(msg -> {
    System.out.println(msg.getBody());
    queue.deleteMessage(msg.getMessageId(), msg.getPopReceipt());
});
```

### Azure Functions
```java
public class Function {
    @FunctionName("httpTrigger")
    public HttpResponseMessage run(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request) {
        return request.ok("Hello from Azure Functions");
    }
}
```

---

## ☁️ Cosmos DB

### SQL API
```java
CosmosClient client = new CosmosClientBuilder()
    .endpoint("https://mycosmos.documents.azure.com:443/")
    .key("mykey")
    .buildClient();

CosmosDatabase db = client.getDatabase("mydb");
CosmosContainer container = db.getContainer("mycontainer");

// Create
container.createItem(new Item("id", "value"));
// Query
container.queryItems("SELECT * FROM c", new FeedOptions()).iterable().forEachRemaining(System.out::println);
```

---

## 🔐 Key Vault

```java
SecretClient client = new SecretClientBuilder()
    .vaultUrl("https://myvault.vault.azure.net/")
    .credential(new DefaultAzureCredentialBuilder().build())
    .buildClient();

client.setSecret(new SecretProperties().setName("mysecret").setValue("secretvalue"));
String secret = client.getSecret("mysecret").getValue();
```

---

## ✅ Best Practices

- Use Managed Identity for authentication
- Enable soft delete for Key Vault
- Use private endpoints for storage
- Implement proper scaling for Functions

### ❌ DON'T
- Don't commit keys to source control
- Don't expose sensitive endpoints publicly

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>