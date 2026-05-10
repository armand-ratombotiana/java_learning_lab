# AWS Solution

## Concepts Covered

### S3 (Simple Storage Service)
- File upload/download
- Bucket operations
- Presigned URLs
- Lifecycle policies

### Lambda
- Function creation and deployment
- Invoking functions with payloads
- Memory and timeout configuration
- Layers for dependencies

### DynamoDB
- Table creation
- Put/Get operations
- Query and scan operations
- GSI (Global Secondary Index)

### EC2
- Instance creation
- Start/stop/terminate
- Security groups
- Key pairs

## Dependencies

```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.21.0</version>
</dependency>
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>lambda</artifactId>
    <version>2.21.0</version>
</dependency>
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>dynamodb</artifactId>
    <version>2.21.0</version>
</dependency>
```

## Configuration

```yaml
aws:
  region: us-east-1
  accessKeyId: ${AWS_ACCESS_KEY_ID}
  secretAccessKey: ${AWS_SECRET_ACCESS_KEY}
```

## Running Tests

```bash
mvn test -Dtest=AWSSolutionTest
```