# AWS Module - PROJECTS.md

---

# Mini-Project 1: EC2 and Compute (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: EC2, IAM Roles, Security Groups, Key Pairs

Deploy Java applications on AWS EC2 instances.

---

## Project Structure

```
31-aws/
├── pom.xml
├── src/main/java/com/learning/
│   └── App.java
├── terraform/
│   ├── main.tf
│   ├── variables.tf
│   └── user-data.sh
└── scripts/
    └── deploy-ec2.sh
```

---

## Implementation

```java
// App.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import java.util.HashMap;

@SpringBootApplication
@RestController
public class App {
    
    @Value("${aws.instance-id:local}")
    private String instanceId;
    
    @Value("${aws.region:unknown}")
    private String region;
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @GetMapping("/")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Running on AWS EC2!");
        response.put("instance", instanceId);
        response.put("region", region);
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }
}
```

```hcl
# terraform/main.tf
terraform {
  required_version = ">= 1.0"
  
  backend "s3" {
    bucket = "terraform-state-bucket"
    key    = "ec2-app/terraform.tfstate"
    region = "us-east-1"
  }
}

provider "aws" {
  region = var.aws_region
}

resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true
  
  tags = {
    Name = "main-vpc"
  }
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "${var.aws_region}a"
  map_public_ip_on_launch = true
  
  tags = {
    Name = "public-subnet"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
  
  tags = {
    Name = "main-igw"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }
  
  tags = {
    Name = "public-rt"
  }
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

resource "aws_security_group" "app" {
  name        = "app-sg"
  description = "Security group for app servers"
  vpc_id      = aws_vpc.main.id
  
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = {
    Name = "app-sg"
  }
}

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]
  
  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}

resource "aws_instance" "app" {
  ami           = data.aws_ami.amazon_linux.id
  instance_type = var.instance_type
  subnet_id     = aws_subnet.public.id
  key_name      = var.key_name
  
  vpc_security_group_ids = [aws_security_group.app.id]
  
  user_data = base64encode(templatefile("${path.module}/user-data.sh", {
    jar_url = var.jar_url
  }))
  
  tags = {
    Name = "app-server"
  }
  
  root_block_device {
    volume_size = 20
    volume_type = "gp3"
  }
}

resource "aws_ebs_volume" "data" {
  availability_zone = "${var.aws_region}a"
  size               = 50
  type               = "gp3"
  
  tags = {
    Name = "data-volume"
  }
}

resource "aws_volume_attachment" "data" {
  device_name = "/dev/sdf"
  volume_id   = aws_ebs_volume.data.id
  instance_id = aws_instance.app.id
}

output "public_ip" {
  value = aws_instance.app.public_ip
}

output "instance_id" {
  value = aws_instance.app.id
}
```

```bash
#!/bin/bash
# terraform/user-data.sh
#!/bin/bash
yum update -y
yum install -y java-17-amazon-corretto-devel docker

# Install Docker
service docker start
usermod -a -G docker ec2-user

# Download and run JAR
curl -o /app/app.jar ${jar_url}
chown ec2-user:ec2-user /app/app.jar

# Start application
su - ec2-user -c "java -jar /app/app.jar > /var/log/app.log 2>&1 &"
```

---

## Build Instructions

```bash
cd 31-aws

# Build application
mvn clean package -DskipTests

# Deploy with Terraform
cd terraform
terraform init
terraform plan
terraform apply

# SSH to instance
ssh -i key.pem ec2-user@<public-ip>
```

---

# Mini-Project 2: S3 and Storage (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: S3 Buckets, Multi-part Upload, Presigned URLs, Lifecycle Policies

Implement S3 storage for Java applications.

---

## Project Structure

```
31-aws/
├── src/main/java/com/learning/
│   ├── service/
│   │   └── S3Service.java
│   └── config/
│       └── AwsConfig.java
└── terraform/
    └── s3-bucket.tf
```

---

## Implementation

```java
// config/AwsConfig.java
package com.learning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsConfig {
    
    @Value("${aws.region:us-east-1}")
    private String region;
    
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }
    
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }
}
```

```java
// service/S3Service.java
package com.learning.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {
    
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    
    @Value("${aws.s3.bucket.name:app-bucket}")
    private String bucketName;
    
    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }
    
    public void uploadFile(String key, File file) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("application/octet-stream")
            .build();
        
        s3Client.putObject(request, RequestBody.fromFile(file));
    }
    
    public void uploadWithMetadata(String key, File file, 
                                   java.util.Map<String, String> metadata) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .metadata(metadata)
            .contentType("application/octet-stream")
            .build();
        
        s3Client.putObject(request, RequestBody.fromFile(file));
    }
    
    public void downloadFile(String key, String destination) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        s3Client.getObject(request, new File(destination).toPath());
    }
    
    public List<String> listFiles(String prefix) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
            .bucket(bucketName)
            .prefix(prefix)
            .build();
        
        return s3Client.listObjectsV2(request).contents().stream()
            .map(S3Object::key)
            .collect(Collectors.toList());
    }
    
    public String generatePresignedUrl(String key, Duration expiration) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(expiration)
            .getObjectRequest(r -> r.bucket(bucketName).key(key))
            .build();
        
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
    
    public void deleteFile(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        s3Client.deleteObject(request);
    }
    
    public long getFileSize(String key) {
        HeadObjectRequest request = HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        return s3Client.headObject(request).contentLength();
    }
}
```

---

## Build Instructions

```bash
cd 31-aws

# Build and run
mvn spring-boot:run

# Test S3 operations
curl -X POST http://localhost:8080/api/upload?key=test.pdf
```

---

# Mini-Project 3: Lambda Functions (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: AWS Lambda, API Gateway, Function URLs, Cold Starts

Build serverless Java functions with AWS Lambda.

---

## Project Structure

```
31-aws/
├── lambda/
│   ├── pom.xml
│   ├── src/main/java/com/learning/
│   │   └── LambdaHandler.java
│   └── build.sh
└── terraform/
    └── lambda.tf
```

---

## Implementation

```java
// LambdaHandler.java
package com.learning;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

import java.util.Map;

public class LambdaHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    
    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        String path = event.getRawPath();
        String method = event.getRequestContext().getHttp().getMethod();
        
        context.getLogger().log("Path: " + path + ", Method: " + method);
        
        if (path.equals("/") && method.equals("GET")) {
            return response(200, "{\"message\":\"Hello from Lambda!\"}");
        } else if (path.equals("/health") && method.equals("GET")) {
            return response(200, "{\"status\":\"OK\"}");
        } else if (path.startsWith("/echo/")) {
            String message = path.substring(6);
            return response(200, "{\"echo\":\"" + message + "\"}");
        }
        
        return response(404, "{\"error\":\"Not Found\"}");
    }
    
    private APIGatewayV2HTTPResponse response(int status, String body) {
        return APIGatewayV2HTTPResponse.builder()
            .statusCode(status)
            .headers(Map.of("Content-Type", "application/json"))
            .body(body)
            .build();
    }
}
```

---

# Mini-Project 4: RDS and Databases (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: RDS PostgreSQL, Connection Pooling, Multi-AZ, Read Replicas

Configure RDS database for Java applications.

---

## Project Structure

```
31-aws/
├── src/main/java/com/learning/
│   ├── config/
│   │   └── DatabaseConfig.java
│   └── repository/
│       └── ProductRepository.java
└── terraform/
    └── rds.tf
```

---

## Implementation

```java
// config/DatabaseConfig.java
package com.learning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    
    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);
        config.setMaxLifetime(1200000);
        config.setAutoCommit(true);
        config.setPoolName("AppHikariCP");
        
        return new HikariDataSource(config);
    }
}
```

---

# Real-World Project: AWS Infrastructure (10+ hours)

## Project Overview

**Duration**: 10+ hours  
**Difficulty**: Advanced  
**Concepts Used**: VPC, EKS, RDS, ElastiCache, Route 53, CloudWatch, Infrastructure as Code

Build comprehensive AWS infrastructure for Java applications.

---

## Complete Terraform Implementation

```hcl
# Complete AWS Infrastructure
terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = { source = "hashicorp/aws" }
  }
}

provider "aws" {
  region = "us-east-1"
}

# VPC
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true
  
  tags = { Name = "main-vpc" }
}

# Subnets
resource "aws_subnet" "private" {
  count             = 3
  vpc_id           = aws_vpc.main.id
  cidr_block       = cidrsubnet("10.0.0.0/16", 4, count.index)
  availability_zone = data.aws_availability_zones.available.names[count.index]
  
  tags = { Name = "private-subnet-${count.index}" }
}

# RDS
resource "aws_db_instance" "main" {
  identifier             = "app-db"
  engine                 = "postgres"
  engine_version         = "15.3"
  instance_class         = "db.t3.medium"
  allocated_storage      = 100
  max_allocated_storage  = 500
  storage_encrypted      = true
  multi_az               = true
  
  db_name  = "appdb"
  username = var.db_username
  password = var.db_password
  
  backup_retention_period = 7
  backup_window          = "03:00-04:00"
  maintenance_window     = "mon:04:00-mon:05:00"
  
  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.main.name
  
  tags = { Name = "app-db" }
}

# ElastiCache
resource "aws_elasticache_cluster" "redis" {
  cluster_id           = "app-redis"
  engine              = "redis"
  engine_version      = "7.0"
  node_type           = "cache.t3.micro"
  num_cache_nodes     = 1
  
  parameter_group_name = "default.redis7"
  security_group_ids  = [aws_security_group.redis.id]
  subnet_group_name   = aws_elasticache_subnet_group.main.name
  
  tags = { Name = "app-redis" }
}
```

---

## Build Instructions

```bash
cd 31-aws

# Terraform deployment
cd terraform
terraform init
terraform plan -var-file=prod.tfvars
terraform apply -var-file=prod.tfvars

# View resources
terraform output
```