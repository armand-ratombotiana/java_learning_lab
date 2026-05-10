package solution;

import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.lambda.*;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.ec2.*;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.*;

public class AWSSolution {

    private final S3Client s3Client;
    private final LambdaClient lambdaClient;
    private final DynamoDbClient dynamoDbClient;
    private final Ec2Client ec2Client;

    public AWSSolution() {
        this.s3Client = S3Client.builder().build();
        this.lambdaClient = LambdaClient.builder().build();
        this.dynamoDbClient = DynamoDbClient.builder().build();
        this.ec2Client = Ec2Client.builder().build();
    }

    // S3 Operations
    public void uploadFile(String bucket, String key, byte[] data) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
        s3Client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromBytes(data));
    }

    public byte[] downloadFile(String bucket, String key) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
        return s3Client.getObject(request).readAllBytes();
    }

    public List<String> listFiles(String bucket) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
            .bucket(bucket)
            .build();
        return s3Client.listObjectsV2(request).contents().stream()
            .map(S3Object::key)
            .toList();
    }

    public void deleteFile(String bucket, String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
        s3Client.deleteObject(request);
    }

    public String generatePresignedUrl(String bucket, String key) {
        return s3Client.utilities().getUrl(GetUrlRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()).toString();
    }

    // Lambda Operations
    public void createFunction(String functionName, String handler, String runtime) {
        CreateFunctionRequest request = CreateFunctionRequest.builder()
            .functionName(functionName)
            .runtime(Runtime.fromValue(runtime))
            .handler(handler)
            .code(code -> code.zipFile("target/function.zip"))
            .role("arn:aws:iam::123456789012:role/lambda-role")
            .build();
        lambdaClient.createFunction(request);
    }

    public byte[] invokeFunction(String functionName, byte[] payload) {
        InvokeRequest request = InvokeRequest.builder()
            .functionName(functionName)
            .payload(software.amazon.awssdk.core.sync.RequestBody.fromBytes(payload))
            .build();
        return lambdaClient.invoke(request).payload().readAllBytes();
    }

    public void updateFunctionConfig(String functionName, int memory, int timeout) {
        UpdateFunctionConfigurationRequest request = UpdateFunctionConfigurationRequest.builder()
            .functionName(functionName)
            .MemorySize(memory)
            .Timeout(timeout)
            .build();
        lambdaClient.updateFunctionConfiguration(request);
    }

    // DynamoDB Operations
    public void createTable(String tableName) {
        CreateTableRequest request = CreateTableRequest.builder()
            .tableName(tableName)
            .keySchema(KeySchemaElement.builder()
                .attributeName("id")
                .keyType(KeyType.HASH)
                .build())
            .attributeDefinitions(AttributeDefinition.builder()
                .attributeName("id")
                .attributeType(ScalarAttributeType.S)
                .build())
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .build();
        dynamoDbClient.createTable(request);
    }

    public void putItem(String tableName, Map<String, AttributeValue> item) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build();
        dynamoDbClient.putItem(request);
    }

    public Map<String, AttributeValue> getItem(String tableName, String key) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of("id", AttributeValue.builder().s(key).build()))
            .build();
        return dynamoDbClient.getItem(request).hasItem() ? dynamoDbClient.getItem(request).item() : null;
    }

    public List<Map<String, AttributeValue>> queryTable(String tableName, String keyCondition) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression(keyCondition)
            .build();
        return dynamoDbClient.query(request).items();
    }

    // EC2 Operations
    public String createInstance(String instanceType, String amiId) {
        RunInstancesRequest request = RunInstancesRequest.builder()
            .instanceType(instanceType)
            .imageId(amiId)
            .maxCount(1)
            .build();
        return ec2Client.runInstances(request).instances().get(0).instanceId();
    }

    public void startInstance(String instanceId) {
        StartInstancesRequest request = StartInstancesRequest.builder()
            .instanceIds(instanceId)
            .build();
        ec2Client.startInstances(request);
    }

    public void stopInstance(String instanceId) {
        StopInstancesRequest request = StopInstancesRequest.builder()
            .instanceIds(instanceId)
            .build();
        ec2Client.stopInstances(request);
    }

    public void terminateInstance(String instanceId) {
        TerminateInstancesRequest request = TerminateInstancesRequest.builder()
            .instanceIds(instanceId)
            .build();
        ec2Client.terminateInstances(request);
    }

    // SNS for notifications
    public void publishToTopic(String topicArn, String message) {
        // SNS client operations
    }

    // SQS for queueing
    public void sendMessage(String queueUrl, String message) {
        // SQS client operations
    }
}