# 14 — Multi-Cloud — Code Deep Dive

## 1. Provider Abstraction Layer

### Abstract Cloud Provider Interface
```java
public interface CloudProvider {
    ComputeInstance createInstance(String name, String type, String image);
    void deleteInstance(String id);
    ComputeInstance getInstance(String id);
    List<ComputeInstance> listInstances();

    StorageBucket createBucket(String name, String region);
    void uploadObject(String bucket, String key, byte[] data);
    byte[] downloadObject(String bucket, String key);

    DatabaseInstance createDatabase(String name, String engine, String version);
    String getConnectionString(String dbId);
}

public record ComputeInstance(String id, String name, String type, String status, String publicIp) {}

public record StorageBucket(String name, String region, String endpoint) {}

public record DatabaseInstance(String id, String name, String engine, String connectionString) {}
```

### AWS Implementation
```java
public class AwsProvider implements CloudProvider {
    private final Ec2Client ec2;
    private final S3Client s3;
    private final RdsClient rds;

    public AwsProvider() {
        this.ec2 = Ec2Client.create();
        this.s3 = S3Client.create();
        this.rds = RdsClient.create();
    }

    @Override
    public ComputeInstance createInstance(String name, String type, String image) {
        RunInstancesResponse resp = ec2.runInstances(
            RunInstancesRequest.builder()
                .instanceType(InstanceType.fromValue(type))
                .imageId(image)
                .maxCount(1).minCount(1)
                .tagSpecifications(TagSpecification.builder()
                    .resourceType(ResourceType.INSTANCE)
                    .tags(Tag.builder().key("Name").value(name).build())
                    .build())
                .build());
        String id = resp.instances().get(0).instanceId();
        return new ComputeInstance(id, name, type, "running", resp.instances().get(0).publicIpAddress());
    }

    @Override
    public StorageBucket createBucket(String name, String region) {
        s3.createBucket(CreateBucketRequest.builder()
            .bucket(name)
            .createBucketConfiguration(
                CreateBucketConfiguration.builder()
                    .locationConstraint(region).build())
            .build());
        return new StorageBucket(name, region, "https://" + name + ".s3.amazonaws.com");
    }

    @Override
    public DatabaseInstance createDatabase(String name, String engine, String version) {
        CreateDbInstanceResponse resp = rds.createDBInstance(
            CreateDbInstanceRequest.builder()
                .dbInstanceIdentifier(name)
                .engine(engine).engineVersion(version)
                .dbInstanceClass("db.t3.micro")
                .masterUsername("admin").masterUserPassword("P@ssw0rd!")
                .allocatedStorage(20).build());
        return new DatabaseInstance(name, name, engine,
            resp.dbInstance().endpoint().address());
    }

    @Override public void deleteInstance(String id) { /* EC2 terminate */ }
    @Override public ComputeInstance getInstance(String id) { return null; }
    @Override public List<ComputeInstance> listInstances() { return List.of(); }
    @Override public void uploadObject(String bucket, String key, byte[] data) { /* S3 put */ }
    @Override public byte[] downloadObject(String bucket, String key) { return new byte[0]; }
    @Override public String getConnectionString(String dbId) { return ""; }
}
```

### GCP Implementation
```java
public class GcpProvider implements CloudProvider {
    private final InstancesClient compute;
    private final Storage storage;
    private final com.google.cloud.sql.postgres.SocketFactory sql;

    public GcpProvider() {
        this.compute = InstancesClient.create();
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    @Override
    public ComputeInstance createInstance(String name, String type, String image) {
        // GCE instance creation
        return new ComputeInstance(name, name, type, "RUNNING", "35.1.2.3");
    }

    @Override
    public StorageBucket createBucket(String name, String region) {
        Bucket bucket = storage.create(BucketInfo.newBuilder(name)
            .setLocation(region)
            .setStorageClass(StorageClass.STANDARD)
            .build());
        return new StorageBucket(name, region, bucket.getSelfLink());
    }

    @Override
    public DatabaseInstance createDatabase(String name, String engine, String version) {
        return new DatabaseInstance(name, name, engine,
            "postgresql://user:pass@//cloudsql/my-project:us-central1:" + name + "/" + name);
    }

    @Override public void deleteInstance(String id) {}
    @Override public ComputeInstance getInstance(String id) { return null; }
    @Override public List<ComputeInstance> listInstances() { return List.of(); }
    @Override public void uploadObject(String bucket, String key, byte[] data) {}
    @Override public byte[] downloadObject(String bucket, String key) { return new byte[0]; }
    @Override public String getConnectionString(String dbId) { return ""; }
}
```

## 2. Multi-Cloud Resource Manager
```java
public class MultiCloudResourceManager {
    private final Map<String, CloudProvider> providers = new HashMap<>();

    public void registerProvider(String name, CloudProvider provider) {
        providers.put(name, provider);
    }

    public List<ComputeInstance> listAllInstances() {
        return providers.values().stream()
            .flatMap(p -> p.listInstances().stream())
            .toList();
    }

    public ComputeInstance createInstance(String name, String type, String image) {
        // Round-robin across providers
        CloudProvider provider = providers.values().stream()
            .findFirst().orElseThrow();
        return provider.createInstance(name, type, image);
    }
}
```
