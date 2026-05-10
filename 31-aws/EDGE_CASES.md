# AWS Edge Cases

## Lambda Issues

### Cold Start Performance

```java
// Solution: Increase memory for faster initialization
// Best practice: Keep function warm with provisioned concurrency

public class WarmUpHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    
    private static final ProductService productService;
    
    static {
        productService = new ProductService(); // Initialize once
    }
    
    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        // Use initialized service
    }
}
```

### Timeout Issues

```json
{
  "FunctionName": "my-processor",
  "Timeout": 300,  // Increase from default 3s
  "MemorySize": 1024  // More memory = more CPU
}
```

### VPC Issues

```json
{
  "VpcConfig": {
    "SubnetIds": ["subnet-12345", "subnet-67890"],  // Must have 2 subnets
    "SecurityGroupIds": ["sg-0123456789abcdef0"]
  }
}
```

---

## S3 Issues

### Large File Upload

```java
public void uploadLargeFile(String bucketName, String key, File file) {
    // Use multipart upload for files > 5MB
    Upload upload = TransferManager.builder()
        .s3Client(s3Client)
        .build()
        .upload(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build(),
            file
        );
    
    upload.waitForCompletion();
}
```

### Bucket Policy Errors

```json
{
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::mybucket/*"
    }
  ]
}
```

### Cross-Region Access

```java
// Use region-specific client
S3Client client = S3Client.builder()
    .region(Region.US_EAST_1)
    .build();
```

---

## RDS Issues

### Connection Pool Exhaustion

```yaml
# Use connection pooling
# hikari.properties
maximumPoolSize=20
minimumIdle=5
idleTimeout=300000
connectionTimeout=20000
```

### Failover Issues

```java
// Use read replicas for read-heavy workloads
String jdbcUrl = "jdbc:mysql://master.db:3306/db?" +
    "connectTimeout=30&socketTimeout=30" +
    "&autoReconnect=true&failOverReadOnly=false";
```

### Backup Failures

```java
// Enable point-in-time recovery
rdsClient.enableDBInstanceAutomatedBackups(
    EnableDBInstanceAutomatedBackupsRequest.builder()
        .dbInstanceArn(instanceArn)
        .build()
);
```

---

## EC2 Issues

### Instance Limits

```bash
# Request limit increase
aws support create-case \
  --subject "EC2 Instance Limit Increase" \
  --service-code "amazon-ec2" \
  --category "EC2 Service" \
  --severity "2" \
  --description "Need increase for t3.micro limit"
```

### Key Pair Issues

```bash
# Recreate key pair
aws ec2 create-key-pair --key-name my-key --region us-east-1
```

### SSH Access

```bash
# Fix permissions
chmod 400 my-key.pem

# Connect
ssh -i my-key.pem ec2-user@public-ip
```

---

## Networking Issues

### Security Group Rules

```json
{
  "IpPermissions": [
    {
      "IpProtocol": "tcp",
      "FromPort": 80,
      "ToPort": 80,
      "IpRanges": [{"CidrIp": "0.0.0.0/0"}]
    }
  ]
}
```

### Route Table Issues

```json
{
  "RouteTableId": "rtb-12345",
  "Routes": [
    {
      "DestinationCidrBlock": "10.0.0.0/16",
      "GatewayId": "local"
    },
    {
      "DestinationCidrBlock": "0.0.0.0/0",
      "NatGatewayId": "nat-12345"
    }
  ]
}
```

---

## IAM Issues

### Permission Errors

```json
{
  "Effect": "Allow",
  "Action": ["s3:GetObject"],
  "Resource": "arn:aws:s3:::bucket/*"
}
```

### Role Assumptions

```java
// Assume role
StsClient sts = StsClient.builder().region(Region.US_EAST_1).build();

AssumeRoleRequest request = AssumeRoleRequest.builder()
    .roleArn("arn:aws:iam::123456789012:role/MyRole")
    .roleSessionName("session1")
    .build();

AssumeRoleResponse response = sts.assumeRole(request);
Credentials credentials = response.credentials();
```

---

## Best Practices

1. **Use proper timeout settings**
2. **Implement retry logic with exponential backoff**
3. **Use connection pooling for databases**
4. **Configure proper security groups**
5. **Enable logging for all services**
6. **Use parameterized configurations**
7. **Implement proper error handling**
8. **Monitor with CloudWatch**