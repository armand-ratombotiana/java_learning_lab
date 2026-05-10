package com.learning.lab.module32;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 32: Azure SDK Examples ===");

        blobStorageDemo();
        cosmosDBDemo();
        serviceBusDemo();
        appServiceDemo();
        aksDemo();
    }

    static void blobStorageDemo() {
        System.out.println("\n--- Azure Blob Storage ---");
        String connectionString = "DefaultEndpointsProtocol=https;AccountName=myaccount;AccountKey=mykey;EndpointSuffix=core.windows.net";
        System.out.println("Blob Storage Operations:");
        System.out.println("1. Create container: blobServiceClient.createBlobContainer(\"mycontainer\")");
        System.out.println("2. Upload blob: blobClient.upload(OutputStream, length)");
        System.out.println("3. Download: blobClient.download(OutputStream)");
        System.out.println("4. List blobs: containerClient.listBlobs()");
        System.out.println("Code: AzureIdentity credential, BlobServiceClient builder");
    }

    static void cosmosDBDemo() {
        System.out.println("\n--- Azure Cosmos DB ---");
        System.out.println("Cosmos DB Operations:");
        System.out.println("1. Create client: CosmosClientBuilder endpoint, key");
        System.out.println("2. Create database: client.createDatabaseIfNotExists(\"mydb\")");
        System.out.println("3. Create container: database.createContainerIfNotExists(\"mycontainer\", partitionKeyPath)");
        System.out.println("4. CRUD operations: container.createItem(item), container.readItem, container.replaceItem");
        System.out.println("5. Query: container.queryItems(query, options)");
    }

    static void serviceBusDemo() {
        System.out.println("\n--- Azure Service Bus ---");
        System.out.println("Service Bus Operations:");
        System.out.println("1. Create queue: ServiceBusAdminClient.createQueue(\"myqueue\")");
        System.out.println("2. Send: ServiceBusSenderClient.sendMessage(ServiceBusMessage)");
        System.out.println("3. Receive: ServiceBusReceiverClient.receiveMessages()");
        System.out.println("4. Topics: ServiceBusTopicClient, ServiceBusSubscriptionClient");
        System.out.println("5. Sessions: SessionProcessorClient for ordered processing");
    }

    static void appServiceDemo() {
        System.out.println("\n--- Azure App Service ---");
        System.out.println("Azure App Service (Web Apps):");
        System.out.println("1. Deploy: az webapp deployment source config-local-git");
        System.out.println("2. Configure: az webapp config set --linux-fx-version \"JAVA|17-java17\"");
        System.out.println("3. Scale: az webapp up --instance-count 3 --max-elastic 10");
        System.out.println("4. Slots: az webapp deployment slot create --slot staging");
        System.out.println("5. CI/CD: Configure with GitHub Actions, Azure Pipelines");
    }

    static void aksDemo() {
        System.out.println("\n--- Azure Kubernetes Service (AKS) ---");
        System.out.println("AKS Operations:");
        System.out.println("1. Create cluster: az aks create --resource-group rg --name akscluster");
        System.out.println("2. Get credentials: az aks get-credentials --resource-group rg --name akscluster");
        System.out.println("3. Scale: az aks scale --node-count 5 --name akscluster");
        System.out.println("4. Update: az aks upgrade --kubernetes-version 1.28");
        System.out.println("5. Enable addon: az aks enable-addons --addons azure-policy");
    }
}