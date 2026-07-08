# 13 — GCP Fundamentals — Code Deep Dive

## 1. Compute Engine Instances

### Creating and Managing GCE Instances
```java
import com.google.cloud.compute.v1.*;
import com.google.cloud.compute.v1.InstancesClient;

InstancesClient instances = InstancesClient.create();

String project = "my-project";
String zone = "us-central1-a";
String instanceName = "java-instance";

// Create instance
Instance instance = Instance.newBuilder()
    .setName(instanceName)
    .setMachineType("zones/" + zone + "/machineTypes/e2-standard-2")
    .addNetworkInterfaces(NetworkInterface.newBuilder()
        .setName("global/networks/default")
        .addAccessConfigs(AccessConfig.newBuilder()
            .setType(AccessConfig.Type.ONE_TO_ONE_NAT)
            .setName("External NAT"))
        .build())
    .addDisks(AttachedDisk.newBuilder()
        .setBoot(true)
        .setInitializeParams(AttachedDiskInitializeParams.newBuilder()
            .setSourceImage("projects/debian-cloud/global/images/family/debian-12")
            .build())
        .build())
    .build();

InsertInstanceRequest insertRequest = InsertInstanceRequest.newBuilder()
    .setProject(project)
    .setZone(zone)
    .setInstanceResource(instance)
    .build();

instances.insertAsync(insertRequest);

// List instances
InstancesClient.ListPagedResponse response = instances.list(
    ListInstancesRequest.newBuilder().setProject(project).setZone(zone).build());
response.iterateAll().forEach(i ->
    System.out.println(i.getName() + " - " + i.getStatus()));
```

## 2. Cloud Storage Operations

### Working with GCP Cloud Storage
```java
import com.google.cloud.storage.*;

Storage storage = StorageOptions.getDefaultInstance().getService();

// Create bucket
Bucket bucket = storage.create(BucketInfo.newBuilder("my-java-bucket")
    .setStorageClass(StorageClass.STANDARD)
    .setLocation("US-CENTRAL1")
    .build());

// Upload object
BlobId blobId = BlobId.of("my-java-bucket", "hello.txt");
BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
Blob blob = storage.create(blobInfo, "Hello GCP Cloud Storage!".getBytes());

// Download object
byte[] content = storage.readAllBytes("my-java-bucket", "hello.txt");
System.out.println(new String(content));

// List objects
storage.list("my-java-bucket").iterateAll().forEach(b ->
    System.out.println(b.getName() + " (" + b.getSize() + " bytes)"));
```

## 3. Cloud SQL Connection

### Connecting to Cloud SQL with Java
```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class CloudSqlConnector {
    public DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql:///ordersdb");
        config.setUsername("dbuser");
        config.setPassword(System.getenv("DB_PASSWORD"));
        config.addDataSourceProperty("socketFactory",
            "com.google.cloud.sql.postgres.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance",
            "my-project:us-central1:my-instance");
        config.addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        return new HikariDataSource(config);
    }
}
```

## 4. GKE Deployment

### Connecting to GKE and Deploying Workloads
```java
// Using Kubernetes client with GKE credentials
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;

ApiClient client = Config.defaultClient();
AppsV1Api appsApi = new AppsV1Api(client);

V1Deployment deployment = new V1Deployment()
    .apiVersion("apps/v1")
    .kind("Deployment")
    .metadata(new V1ObjectMeta().name("java-app"))
    .spec(new V1DeploymentSpec()
        .replicas(3)
        .selector(new V1LabelSelector()
            .putMatchLabelsItem("app", "java-app"))
        .template(new V1PodTemplateSpec()
            .metadata(new V1ObjectMeta()
                .putLabelsItem("app", "java-app"))
            .spec(new V1PodSpec()
                .addContainersItem(new V1Container()
                    .name("java-app")
                    .image("gcr.io/my-project/java-app:latest")
                    .addPortsItem(new V1ContainerPort().containerPort(8080))))));

appsApi.createNamespacedDeployment("default", deployment, null, null, null, null);
System.out.println("Deployment created");
```
