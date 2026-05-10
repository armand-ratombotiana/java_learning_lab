# AWS Deep Dive

## EC2 Patterns

### Launch Template

```json
{
  "LaunchTemplateName": "myapp-template",
  "LaunchTemplateData": {
    "ImageId": "ami-0c55b159cbfafe1f0",
    "InstanceType": "t3.medium",
    "KeyName": "my-key",
    "SecurityGroupIds": ["sg-0123456789abcdef0"],
    "UserData": "#!/bin/bash\nyum update -y\n...",
    "IamInstanceProfile": {
      "Arn": "arn:aws:iam::123456789012:instance-profile/myapp-role"
    },
    "BlockDeviceMappings": [
      {
        "DeviceName": "/dev/sda1",
        "Ebs": {
          "VolumeSize": 30,
          "VolumeType": "gp3",
          "DeleteOnTermination": true
        }
      }
    ]
  }
}
```

### Auto Scaling Group

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
      "Key": "Name",
      "Value": "myapp-instance",
      "PropagateAtLaunch": true
    }
  ]
}
```

## ECS Patterns

### Task Definition

```json
{
  "family": "myapp-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
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
      "environment": [
        {"name": "SPRING_PROFILES", "value": "production"}
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

### ECS Service

```json
{
  "cluster": "myapp-cluster",
  "serviceName": "myapp-service",
  "taskDefinition": "myapp-task:1",
  "desiredCount": 3,
  "launchType": "FARGATE",
  "networkConfiguration": {
    "awsvpcConfiguration": {
      "subnets": ["subnet-12345", "subnet-67890"],
      "securityGroups": ["sg-0123456789abcdef0"],
      "assignPublicIp": "ENABLED"
    }
  },
  "loadBalancers": [
    {
      "targetGroupArn": "arn:aws:elasticloadbalancing:...",
      "containerName": "myapp",
      "containerPort": 8080
    }
  ],
  "deploymentConfiguration": {
    "minimumHealthyPercent": 100,
    "maximumPercent": 200
  }
}
```

## Lambda Patterns

### Function Configuration

```json
{
  "FunctionName": "myapp-processor",
  "Runtime": "java17",
  "Handler": "com.example.Handler::handleRequest",
  "MemorySize": 512,
  "Timeout": 30,
  "Role": "arn:aws:iam::123456789012:role/lambda-role",
  "Environment": {
    "Variables": {
      "TABLE_NAME": "my-table",
      "LOG_LEVEL": "INFO"
    }
  },
  "VPCConfig": {
    "SubnetIds": ["subnet-12345"],
    "SecurityGroupIds": ["sg-0123456789abcdef0"]
  }
}
```

### Lambda with Spring

```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LambdaHandler {
    public static void main(String[] args) {
        SpringApplication.run(LambdaHandler.class, args);
    }
}

@Component
public class RequestHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    
    @Autowired
    private ProductService productService;
    
    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        // Process request
    }
}
```

### Layers

```json
{
  "Content": {
    "ZipFile": "base64-encoded-layer.zip"
  },
  "LayerName": "myapp-layer",
  "CompatibleRuntimes": ["java17", "java21"]
}
```

## S3 Patterns

### Bucket Configuration

```java
import software.amazon.awssdk.services.s3.model.*;

public class S3Service {
    
    public void createBucket(String bucketName) {
        s3Client.createBucket(CreateBucketRequest.builder()
            .bucket(bucketName)
            .build());
    }
    
    public void uploadFile(String bucketName, String key, File file) {
        s3Client.putObject(PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("application/octet-stream")
            .build(), RequestBody.fromFile(file));
    }
    
    public void enableVersioning(String bucketName) {
        s3Client.putBucketVersioning(PutBucketVersioningRequest.builder()
            .bucket(bucketName)
            .versioningConfiguration(VersioningConfiguration.builder()
                .status(BucketVersioningStatus.ENABLED)
                .build())
            .build());
    }
    
    public void setLifecyclePolicy(String bucketName) {
        s3Client.putBucketLifecycleConfiguration(PutBucketLifecycleConfigurationRequest.builder()
            .bucket(bucketName)
            .lifecycleConfiguration(LifecycleConfiguration.builder()
                .rules(Rule.builder()
                    .id("ArchiveOldObjects")
                    .status(EnabledStatus.ENABLED)
                    .filter(Filter.builder()
                        .prefix("logs/")
                        .build())
                    .transitions(
                        Transition.builder()
                            .days(30)
                            .storageClass(StorageClass.GLACIER)
                            .build()
                    )
                    .build())
                .build())
            .build());
    }
}
```

## RDS Patterns

### Database Configuration

```java
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

public class RDSService {
    
    public void createDatabase() {
        CreateDBInstanceRequest request = CreateDBInstanceRequest.builder()
            .dbInstanceIdentifier("myapp-db")
            .engine("postgres")
            .engineVersion("15.3")
            .dbInstanceClass("db.t3.medium")
            .allocatedStorage(100)
            .masterUsername("admin")
            .masterUserPassword("password")
            .vpcSecurityGroupIds("sg-0123456789abcdef0")
            .subnetIds("subnet-12345", "subnet-67890")
            .backupRetentionPeriod(7)
            .deletionProtection(true)
            .multiAz(true)
            .build();
        
        rdsClient.createDBInstance(request);
    }
    
    public void createReadReplica() {
        CreateDBInstanceReadReplicaRequest request = 
            CreateDBInstanceReadReplicaRequest.builder()
                .dbInstanceIdentifier("myapp-db-replica")
                .sourceDBInstanceIdentifier("myapp-db")
                .dbInstanceClass("db.t3.medium")
                .build();
        
        rdsClient.createDBInstanceReadReplica(request);
    }
}
```

## IAM Patterns

### Role Policy

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": "arn:aws:s3:::mybucket/*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "dynamodb:GetItem",
        "dynamodb:PutItem"
      ],
      "Resource": "arn:aws:dynamodb:us-east-1:123456789012:table/myapp"
    },
    {
      "Effect": "Deny",
      "Action": [
        "s3:*"
      ],
      "Resource": "arn:aws:s3:::sensitive-bucket/*",
      "Condition": {
        "Bool": {
          "aws:SecureTransport": "false"
        }
      }
    }
  ]
}
```

### Lambda Execution Role

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:*:*:*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": "arn:aws:s3:::mybucket/*"
    }
  ]
}
```

## VPC Patterns

### VPC Setup

```json
{
  "VpcConfiguration": {
    "CidrBlock": "10.0.0.0/16",
    "SubnetConfigurations": [
      {
        "SubnetType": "Public",
        "CidrBlock": "10.0.1.0/24",
        "AvailabilityZone": "us-east-1a"
      },
      {
        "SubnetType": "Private",
        "CidrBlock": "10.0.2.0/24",
        "AvailabilityZone": "us-east-1a"
      },
      {
        "SubnetType": "Isolated",
        "CidrBlock": "10.0.3.0/24",
        "AvailabilityZone": "us-east-1a"
      }
    ]
  }
}
```

## CDK Patterns

### Infrastructure as Code

```java
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;

public class MyAppStack extends Stack {
    
    public MyAppStack(App scope, String id) {
        super(scope, id);
        
        Vpc vpc = Vpc.Builder.create(this, "MyVpc")
            .maxAzs(2)
            .build();
        
        Cluster cluster = Cluster.Builder.create(this, "MyCluster")
            .vpc(vpc)
            .build();
        
        ApplicationLoadBalancedFargateService.Builder.create(this, "MyService")
            .cluster(cluster)
            .cpu(256)
            .memoryLimitMiB(512)
            .desiredCount(3)
            .taskImageOptions(
                ApplicationLoadBalancedTaskImageOptions.builder()
                    .image(ContainerImage.fromRegistry("myapp:latest"))
                    .build()
            )
            .build();
    }
}
```

## Best Practices

1. **Use IAM roles**: Never use access keys
2. **Enable encryption**: At rest and in transit
3. **Use VPC**: Isolate resources
4. **Enable logging**: CloudWatch, VPC Flow Logs
5. **Use tags**: For cost allocation
6. **Monitor**: Use CloudWatch metrics
7. **Backup**: Enable automated backups
8. **Use CDK**: Infrastructure as code