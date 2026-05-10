package com.learning.lab.module31;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.sqs.*;
import software.amazon.awssdk.services.sqs.model.*;

import java.net.URI;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 31: AWS SDK Examples ===");

        s3Demo();
        dynamoDBDemo();
        sqsDemo();
        lambdaDemo();
        ec2Demo();
    }

    static void s3Demo() {
        System.out.println("\n--- Amazon S3 ---");
        AwsBasicCredentials credentials = AwsBasicCredentials.create("accessKey", "secretKey");
        S3Client s3 = S3Client.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1)
                .build();

        s3.createBucket(CreateBucketRequest.builder().bucket("my-bucket").build());
        s3.putObject(PutObjectRequest.builder()
                .bucket("my-bucket")
                .key("file.txt")
                .build(), software.amazon.awssdk.core.sync.RequestBody.fromString("Hello S3"));

        GetObjectResponse response = s3.getObject(GetObjectRequest.builder()
                .bucket("my-bucket")
                .key("file.txt")
                .build(), software.amazon.awssdk.core.sync.ResponseTransformer.toBytes());

        System.out.println("S3 Object Content: " + new String(response.asByteArray()));
    }

    static void dynamoDBDemo() {
        System.out.println("\n--- Amazon DynamoDB ---");
        AwsBasicCredentials credentials = AwsBasicCredentials.create("accessKey", "secretKey");
        DynamoDbClient ddb = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1)
                .build();

        ddb.createTable(CreateTableRequest.builder()
                .tableName("Users")
                .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                .attributeDefinitions(AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build());

        ddb.putItem(PutItemRequest.builder()
                .tableName("Users")
                .item(Map.of("id", AttributeValue.builder().s("1").build(),
                             "name", AttributeValue.builder().s("John").build()))
                .build());

        GetItemResponse item = ddb.getItem(GetItemRequest.builder()
                .tableName("Users")
                .key(Map.of("id", AttributeValue.builder().s("1").build()))
                .build());

        System.out.println("DynamoDB Item: " + item.item());
    }

    static void sqsDemo() {
        System.out.println("\n--- Amazon SQS ---");
        AwsBasicCredentials credentials = AwsBasicCredentials.create("accessKey", "secretKey");
        SqsClient sqs = SqsClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1)
                .build();

        String queueUrl = sqs.createQueue(CreateQueueRequest.builder().queueName("my-queue").build()).queueUrl();
        sqs.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody("Hello SQS")
                .build());

        ReceiveMessageResponse messages = sqs.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .build());

        System.out.println("Received: " + messages.messages().size() + " messages");
    }

    static void lambdaDemo() {
        System.out.println("\n--- AWS Lambda ---");
        System.out.println("AWS Lambda function deployment:");
        System.out.println("1. Package: mvn clean package");
        System.out.println("2. Upload: aws lambda update-function-code --function-name myFunc --zip-file fileb://target/function.zip");
        System.out.println("3. Invoke: aws lambda invoke --function-name myFunc --payload '{\"key\":\"value\"}' response.json");
        System.out.println("Handler: com.example.LambdaHandler::handleRequest");
    }

    static void ec2Demo() {
        System.out.println("\n--- AWS EC2 ---");
        System.out.println("EC2 Instance operations:");
        System.out.println("1. Create: aws ec2 run-instances --image-id ami-xxx --instance-type t3.micro");
        System.out.println("2. List: aws ec2 describe-instances");
        System.out.println("3. Start: aws ec2 start-instances --instance-ids i-xxx");
        System.out.println("4. Stop: aws ec2 stop-instances --instance-ids i-xxx");
        System.out.println("5. Terminate: aws ec2 terminate-instances --instance-ids i-xxx");
    }
}