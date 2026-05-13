# Quick Reference: AWS Cloud Patterns

<div align="center">

![Module](https://img.shields.io/badge/Module-31-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-AWS-green?style=for-the-badge)

**Quick lookup guide for AWS services**

</div>

---

## 📋 Core Services

| Service | Purpose |
|---------|---------|
| **EC2** | Virtual servers |
| **Lambda** | Serverless functions |
| **ECS/EKS** | Container orchestration |
| **S3** | Object storage |
| **RDS** | Managed databases |
| **IAM** | Access management |
| **VPC** | Virtual network |

---

## 🔑 Key Commands

### AWS CLI
```bash
# Configure
aws configure

# S3 operations
aws s3 cp file.txt s3://bucket/
aws s3 ls s3://bucket/

# EC2
aws ec2 describe-instances
aws ec2 run-instances --image-id ami-xxx --instance-type t3.micro

# Lambda
aws lambda update-function-code --function-name myfunc --zip-file fileb://func.zip

# Elastic Beanstalk
aws elasticbeanstalk create-application-version --application-name myapp --version v1 --source-bundle S3Bucket=bucket,S3Key=app.zip
```

---

## 📊 Java SDK Examples

### S3 Upload
```java
AmazonS3 s3 = AmazonS3ClientBuilder.standard()
    .withRegion("us-east-1")
    .build();

s3.putObject("bucket", "key", new File("file.txt"));
```

### Lambda Invocation
```java
AWSSLambda client = AWSSLambdaClientBuilder.standard()
    .withRegion("us-east-1")
    .build();

InvokeRequest request = new InvokeRequest()
    .withFunctionName("myFunction")
    .withPayload("{\"input\": \"data\"}");

InvokeResult result = client.invoke(request);
```

### ECS Task Definition
```java
RegisterTaskDefinitionRequest request = new RegisterTaskDefinitionRequest()
    .withFamily("my-task")
    .withContainerDefinitions(new ContainerDefinition()
        .withName("app")
        .withImage("myapp:latest")
        .withMemory(512)
        .withCpu(256));
```

---

## ☁️ Serverless (Lambda)

### Handler
```java
public class MyHandler implements RequestHandler<Map<String,String>, String> {
    @Override
    public String handleRequest(Map<String,String> event, Context context) {
        context.getLogger().log("Received: " + event);
        return "Success";
    }
}
```

### Spring Cloud Function
```java
@Bean
public Function<String, String> uppercase() {
    return s -> s.toUpperCase();
}
```

---

## 📦 Deployment

### Elastic Beanstalk
```bash
# Create application
aws elasticbeanstalk create-application --application-name myapp

# Deploy
eb init -p java
eb create production
eb deploy
```

### ECS Fargate
```bash
# Create cluster
aws ecs create-cluster --cluster-name mycluster

# Register task
aws ecs register-task-definition --cli-input-json file://task.json

# Run service
aws ecs create-service --cluster mycluster --service-name myservice --task-definition mytask:1
```

---

## 🔒 IAM Policy

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": [
      "s3:GetObject",
      "s3:PutObject"
    ],
    "Resource": "arn:aws:s3:::bucket/*"
  }]
}
```

---

## ✅ Best Practices

- Use IAM roles instead of access keys
- Enable encryption at rest
- Use VPC for network isolation
- Implement auto-scaling

### ❌ DON'T
- Don't store credentials in code
- Don't expose public endpoints without security

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>