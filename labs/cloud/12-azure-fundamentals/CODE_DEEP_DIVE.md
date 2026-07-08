# 12 — Azure Fundamentals — Code Deep Dive

## 1. Azure VM Management

### Creating and Managing VMs with Java SDK
```java
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.*;

AzureResourceManager azure = AzureResourceManager.authenticate(credential)
    .withDefaultSubscription();

VirtualMachine vm = azure.virtualMachines().define("myJavaVM")
    .withRegion(Region.US_EAST)
    .withNewResourceGroup("java-lab-rg")
    .withNewPrimaryNetwork("10.0.0.0/16")
    .withPrimaryPrivateIPAddressDynamic()
    .withoutPrimaryPublicIPAddress()
    .withLatestLinuxImage("Canonical", "UbuntuServer", "22.04-LTS")
    .withRootUsername("azureuser")
    .withRootPassword("P@ssw0rd1234!")
    .withSize(VirtualMachineSizeTypes.STANDARD_D2S_V3)
    .create();

System.out.println("VM created: " + vm.id());
System.out.println("VM state: " + vm.powerState());
```

### VM Lifecycle Operations
```java
// Start VM
vm.start();

// Stop VM
vm.powerOff();

// Restart VM
vm.restart();

// Deallocate (stop billing)
vm.deallocate();

// Get VM status
VirtualMachineInstanceView status = vm.instanceView();
status.statuses().forEach(s ->
    System.out.println(s.code() + ": " + s.displayStatus()));
```

## 2. Azure Blob Storage

### Upload and Download Blobs
```java
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;

String connectionString = "DefaultEndpointsProtocol=https;AccountName=...";
BlobServiceClient blobService = new BlobServiceClientBuilder()
    .connectionString(connectionString)
    .buildClient();

BlobContainerClient container = blobService.createBlobContainer("mycontainer");

// Upload
BlobClient blob = container.getBlobClient("data.txt");
blob.upload(new ByteArrayInputStream("Hello Azure!".getBytes()), true);

// Download
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
blob.download(outputStream);
System.out.println(new String(outputStream.toByteArray()));

// List blobs
container.listBlobs().forEach(b ->
    System.out.println("Blob: " + b.getName() + ", Size: " + b.getProperties().getContentLength()));
```

## 3. Azure SQL Database

### Connecting to Azure SQL from Java
```java
import java.sql.*;

String connectionUrl = "jdbc:sqlserver://my-server.database.windows.net:1433;"
    + "database=my-db;"
    + "user=my-user@my-server;"
    + "password=****;"
    + "encrypt=true;"
    + "trustServerCertificate=false;"
    + "hostNameInCertificate=*.database.windows.net;"
    + "loginTimeout=30;";

try (Connection conn = DriverManager.getConnection(connectionUrl)) {
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT TOP 10 * FROM Orders");
    while (rs.next()) {
        System.out.println(rs.getString("OrderId") + ": " + rs.getDouble("Amount"));
    }
}
```

## 4. AKS Deployment

### Deploying to AKS with Java SDK
```java
import com.azure.resourcemanager.containerservice.models.*;

KubernetesCluster aks = azure.kubernetesClusters().define("myAKSCluster")
    .withRegion(Region.US_EAST)
    .withExistingResourceGroup("java-lab-rg")
    .withDefaultVersion()
    .withSystemAssignedManagedServiceIdentity()
    .withDnsPrefix("java-aks-lab")
    .defineAgentPool("agentpool")
        .withVirtualMachineSize(ContainerServiceVMSizeTypes.STANDARD_D2S_V3)
        .withAgentPoolVirtualMachineCount(3)
        .withAgentPoolMode(AgentPoolMode.SYSTEM)
        .attach()
    .create();

String kubeConfig = aks.adminKubeConfigContent();
System.out.println("AKS cluster created. Kubeconfig length: " + kubeConfig.length());
```
