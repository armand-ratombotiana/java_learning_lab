package com.learning.lab.module31;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ExampleTest {

    @Test
    @DisplayName("AWS S3 bucket can be created")
    void testS3BucketCreation() {
        S3Bucket bucket = new S3Bucket("my-app-bucket");
        bucket.setRegion("us-east-1");
        assertEquals("my-app-bucket", bucket.getName());
    }

    @Test
    @DisplayName("AWS S3 object can be uploaded")
    void testS3ObjectUpload() {
        S3Bucket bucket = new S3Bucket("my-bucket");
        S3Object object = new S3Object("documents/report.pdf", 1024);
        bucket.putObject(object);
        assertNotNull(object);
    }

    @Test
    @DisplayName("AWS EC2 instance can be launched")
    void testEC2Instance() {
        EC2Instance instance = new EC2Instance("i-1234567890abcdef0");
        instance.setInstanceType("t3.micro");
        instance.setAmiId("ami-0c55b159cbfafe1f0");
        assertEquals("t3.micro", instance.getInstanceType());
    }

    @Test
    @DisplayName("AWS RDS database can be configured")
    void testRDSInstance() {
        RDSInstance db = new RDSInstance("mydb-instance");
        db.setEngine("postgres");
        db.setEngineVersion("13.7");
        db.setAllocatedStorage(100);
        assertEquals("postgres", db.getEngine());
    }

    @Test
    @DisplayName("AWS Lambda function can be created")
    void testLambdaFunction() {
        LambdaFunction function = new LambdaFunction("my-function");
        function.setRuntime("java11");
        function.setHandler("com.example.Handler");
        assertEquals("java11", function.getRuntime());
    }

    @Test
    @DisplayName("AWS SNS topic can be created")
    void testSNSTopic() {
        SNSTopic topic = new SNSTopic("notifications-topic");
        topic.addSubscription("email", "admin@example.com");
        assertNotNull(topic);
    }

    @Test
    @DisplayName("AWS SQS queue can be configured")
    void testSQSQueue() {
        SQSQueue queue = new SQSQueue("messages-queue");
        queue.setVisibilityTimeout(30);
        queue.setDelaySeconds(5);
        assertEquals(30, queue.getVisibilityTimeout());
    }

    @Test
    @DisplayName("AWS IAM role can be created")
    void testIAMRole() {
        IAMRole role = new IAMRole("ec2-role");
        role.addPolicy("AmazonS3ReadOnlyAccess");
        role.addPolicy("CloudWatchLogsFullAccess");
        assertEquals(2, role.getPolicies().size());
    }

    @Test
    @DisplayName("AWS DynamoDB table can be defined")
    void testDynamoDBTable() {
        DynamoDBTable table = new DynamoDBTable("users-table");
        table.setPartitionKey("userId", "S");
        table.setSortKey("timestamp", "N");
        assertEquals("S", table.getPartitionKeyType());
    }
}

class S3Bucket {
    private final String name;
    private String region;
    private java.util.List<S3Object> objects = new java.util.ArrayList<>();

    public S3Bucket(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void putObject(S3Object object) {
        objects.add(object);
    }
}

class S3Object {
    private final String key;
    private final long size;

    public S3Object(String key, long size) {
        this.key = key;
        this.size = size;
    }

    public String getKey() {
        return key;
    }

    public long getSize() {
        return size;
    }
}

class EC2Instance {
    private final String instanceId;
    private String instanceType;
    private String amiId;

    public EC2Instance(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public void setAmiId(String amiId) {
        this.amiId = amiId;
    }
}

class RDSInstance {
    private final String instanceId;
    private String engine;
    private String engineVersion;
    private int allocatedStorage;

    public RDSInstance(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public void setAllocatedStorage(int storage) {
        this.allocatedStorage = storage;
    }
}

class LambdaFunction {
    private final String functionName;
    private String runtime;
    private String handler;

    public LambdaFunction(String functionName) {
        this.functionName = functionName;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }
}

class SNSTopic {
    private final String topicName;
    private java.util.Map<String, String> subscriptions = new java.util.HashMap<>();

    public SNSTopic(String topicName) {
        this.topicName = topicName;
    }

    public void addSubscription(String protocol, String endpoint) {
        subscriptions.put(protocol, endpoint);
    }
}

class SQSQueue {
    private final String queueName;
    private int visibilityTimeout;
    private int delaySeconds;

    public SQSQueue(String queueName) {
        this.queueName = queueName;
    }

    public int getVisibilityTimeout() {
        return visibilityTimeout;
    }

    public void setVisibilityTimeout(int visibilityTimeout) {
        this.visibilityTimeout = visibilityTimeout;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }
}

class IAMRole {
    private final String roleName;
    private java.util.List<String> policies = new java.util.ArrayList<>();

    public IAMRole(String roleName) {
        this.roleName = roleName;
    }

    public void addPolicy(String policy) {
        policies.add(policy);
    }

    public java.util.List<String> getPolicies() {
        return policies;
    }
}

class DynamoDBTable {
    private final String tableName;
    private String partitionKeyName;
    private String partitionKeyType;
    private String sortKeyName;
    private String sortKeyType;

    public DynamoDBTable(String tableName) {
        this.tableName = tableName;
    }

    public void setPartitionKey(String name, String type) {
        this.partitionKeyName = name;
        this.partitionKeyType = type;
    }

    public void setSortKey(String name, String type) {
        this.sortKeyName = name;
        this.sortKeyType = type;
    }

    public String getPartitionKeyType() {
        return partitionKeyType;
    }
}