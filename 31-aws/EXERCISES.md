# AWS Exercises

## Exercise 1: S3 File Operations

### Task
Create a service that uploads and downloads files from S3 with versioning.

### Solution
```java
@Service
public class S3FileService {
    
    private final S3Client s3Client;
    private final String bucketName = "myapp-files";
    
    public String uploadFile(String key, File file) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("application/octet-stream")
            .build();
        
        s3Client.putObject(request, RequestBody.fromFile(file));
        return key;
    }
    
    public byte[] downloadFile(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        return s3Client.getObject(request).readAllBytes();
    }
    
    public List<S3Object> listFiles() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
            .bucket(bucketName)
            .build();
        
        return s3Client.listObjectsV2(request).contents();
    }
}
```

---

## Exercise 2: Lambda Function

### Task
Create a Lambda function that processes API Gateway requests.

### Solution
```java
public class LambdaHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    
    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        String method = event.getRequestContext().getHttp().getMethod();
        
        if ("GET".equals(method)) {
            return handleGet(event);
        } else if ("POST".equals(method)) {
            return handlePost(event);
        }
        
        return APIGatewayV2HTTPResponse.builder()
            .statusCode(405)
            .body("Method not allowed")
            .build();
    }
    
    private APIGatewayV2HTTPResponse handleGet(APIGatewayV2HTTPEvent event) {
        return APIGatewayV2HTTPResponse.builder()
            .statusCode(200)
            .body("{\"message\":\"Hello from Lambda\"}")
            .headers(Map.of("Content-Type", "application/json"))
            .build();
    }
}
```

---

## Exercise 3: EC2 Auto Scaling

### Task
Create an Auto Scaling Group with launch template.

### Solution
```json
{
  "AutoScalingGroupName": "myapp-asg",
  "MinSize": 2,
  "MaxSize": 10,
  "DesiredCapacity": 3,
  "VPCZoneIdentifier": "subnet-12345,subnet-67890",
  "LaunchTemplate": {
    "LaunchTemplateId": "lt-1234567890abcdef0",
    "Version": "$Latest"
  },
  "TargetGroupARNs": ["arn:aws:elasticloadbalancing:..."],
  "Tags": [
    {
      "Key": "Environment",
      "Value": "production",
      "PropagateAtLaunch": true
    }
  ]
}
```

---

## Exercise 4: ECS Task Definition

### Task
Create an ECS task definition with logging.

### Solution
```json
{
  "family": "myapp-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "myapp",
      "image": "123456789012.dkr.ecr.us-east-1.amazonaws.com/myapp:latest",
      "essential": true,
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/myapp",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

---

## Exercise 5: RDS Database

### Task
Create an RDS PostgreSQL instance with backup enabled.

### Solution
```java
public void createDatabase() {
    CreateDBInstanceRequest request = CreateDBInstanceRequest.builder()
        .dbInstanceIdentifier("myapp-db")
        .engine("postgres")
        .engineVersion("15.3")
        .dbInstanceClass("db.t3.medium")
        .allocatedStorage(100)
        .storageType("gp3")
        .masterUsername("admin")
        .masterUserPassword("SecurePassword123!")
        .vpcSecurityGroupIds("sg-0123456789abcdef0")
        .subnetIds("subnet-12345", "subnet-67890", "subnet-abcde")
        .backupRetentionPeriod(7)
        .deletionProtection(true)
        .multiAz(false)
        .publiclyAccessible(false)
        .build();
    
    rdsClient.createDBInstance(request);
}
```

---

## Exercise 6: IAM Policy

### Task
Create an IAM policy for S3 and DynamoDB access.

### Solution
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::mybucket",
        "arn:aws:s3:::mybucket/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "dynamodb:GetItem",
        "dynamodb:PutItem",
        "dynamodb:Query"
      ],
      "Resource": "arn:aws:dynamodb:us-east-1:123456789012:table/myapp"
    },
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:*:*:*"
    }
  ]
}
```

---

## Exercise 7: CloudWatch Alarm

### Task
Create a CloudWatch alarm for Lambda errors.

### Solution
```json
{
  "AlarmName": "myapp-lambda-errors",
  "AlarmDescription": "Alarm when Lambda function has errors",
  "MetricName": "Errors",
  "Namespace": "AWS/Lambda",
  "Statistic": "Sum",
  "Period": 300,
  "EvaluationPeriods": 2,
  "Threshold": 5,
  "ComparisonOperator": "GreaterThanThreshold",
  "TreatMissingData": "notBreaching",
  "Dimensions": [
    {
      "Name": "FunctionName",
      "Value": "myapp-processor"
    }
  ],
  "AlarmActions": [
    "arn:aws:sns:us-east-1:123456789012:myapp-alerts"
  ]
}
```

---

## Exercise 8: CDK Stack

### Task
Create a CDK stack with VPC and ECS cluster.

### Solution
```java
public class MyAppStack extends Stack {
    
    public MyAppStack(App scope, String id, StackProps props) {
        super(scope, id, props);
        
        Vpc vpc = Vpc.Builder.create(this, "MyVpc")
            .maxAzs(2)
            .natGateways(1)
            .build();
        
        Cluster cluster = Cluster.Builder.create(this, "MyCluster")
            .vpc(vpc)
            .build();
        
        ApplicationLoadBalancedFargateService.Builder.create(this, "MyService")
            .cluster(cluster)
            .cpu(256)
            .memoryLimitMiB(512)
            .desiredCount(2)
            .taskImageOptions(
                ApplicationLoadBalancedTaskImageOptions.builder()
                    .image(ContainerImage.fromRegistry("myapp:latest"))
                    .environment(Map.of(
                        "SPRING_PROFILES", "production"
                    ))
                    .build()
            )
            .publicLoadBalancer(true)
            .build();
    }
}
```

---

## Exercise 9: DynamoDB Operations

### Task
Create a DynamoDB service for CRUD operations.

### Solution
```java
@Service
public class DynamoDBService {
    
    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "myapp-items";
    
    public void putItem(Item item) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(Map.of(
                "id", AttributeValue.builder().s(item.getId()).build(),
                "name", AttributeValue.builder().s(item.getName()).build(),
                "createdAt", AttributeValue.builder().s(item.getCreatedAt()).build()
            ))
            .build();
        
        dynamoDbClient.putItem(request);
    }
    
    public Optional<Item> getItem(String id) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of("id", AttributeValue.builder().s(id).build()))
            .build();
        
        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();
        
        if (item == null || item.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(mapToItem(item));
    }
    
    public void deleteItem(String id) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of("id", AttributeValue.builder().s(id).build()))
            .build();
        
        dynamoDbClient.deleteItem(request);
    }
    
    private Item mapToItem(Map<String, AttributeValue> item) {
        return new Item(
            item.get("id").s(),
            item.get("name").s(),
            item.get("createdAt").s()
        );
    }
}
```

---

## Exercise 10: Secrets Manager

### Task
Use Secrets Manager for database credentials.

### Solution
```java
public class SecretsManagerService {
    
    private final SecretsManagerClient secretsClient;
    
    public DatabaseCredentials getCredentials(String secretName) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();
        
        GetSecretValueResponse response = secretsClient.getSecretValue(request);
        
        Map<String, String> secret = new ObjectMapper().readValue(
            response.secretString(), 
            new TypeReference<Map<String, String>>() {}
        );
        
        return new DatabaseCredentials(
            secret.get("username"),
            secret.get("password"),
            secret.get("host"),
            Integer.parseInt(secret.get("port"))
        );
    }
}
```

```yaml
# CloudFormation for secret
Resources:
  DatabaseSecret:
    Type: AWS::SecretsManager::Secret
    Properties:
      Name: myapp/db-credentials
      GenerateSecretString:
        SecretStringTemplate: '{"username": "admin"}'
        GenerateSecretKey: password
```