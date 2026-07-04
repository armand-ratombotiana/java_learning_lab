# Refactoring — AWS Fundamentals

## 1. From Monolithic EC2 to Auto Scaling + ALB

### Before
```
One EC2 instance running Tomcat
├── Direct public IP
├── No redundancy
└── Manual scaling
```

### After
```
Auto Scaling Group (min=2, max=10)
├── Launch template with AMI
├── Attached to ALB
├── Health checks on /health
└── Scaling policies (CPU > 70%)
```

### Java Code Changes
```java
// Before: hardcoded instance address
String dbUrl = "jdbc:mysql://10.0.1.5:3306/mydb";

// After: use RDS endpoint via environment variable
String dbUrl = System.getenv("DB_URL");
// Passed via launch template user-data or Parameter Store
```

## 2. From IAM Users to IAM Roles

### Before
```java
// Hardcoded IAM user credentials
StaticCredentialsProvider.create(
    AccessKeyId("AKIA..."),
    SecretAccessKey("...")
);
```

### After
```java
// EC2 instance profile — automatic credentials from metadata
S3Client s3 = S3Client.builder()
    .credentialsProvider(InstanceProfileCredentialsProvider.create())
    .region(Region.US_EAST_1)
    .build();
```
EC2 automatically rotates temporary credentials; no secrets in code.

## 3. From S3 Direct Upload to Presigned URLs

### Before
```java
// Server uploads file directly — needs full S3 write access
s3.putObject(PutObjectRequest.builder()
    .bucket("uploads").key("file.pdf").build(), body);
```

### After
```java
// Server generates presigned URL — client uploads directly
PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(
    r -> r.signatureDuration(Duration.ofMinutes(5))
        .putObjectRequest(PutObjectRequest.builder()
            .bucket("uploads").key("file.pdf").build())
);
// Return URL to client — 5 minute window, S3 handles upload
```

## 4. From Single-AZ to Multi-AZ

### Before
```
┌─────────────────┐
│    us-east-1a    │
│  ┌─────────┐    │
│  │ App + DB │    │
│  └─────────┘    │
│ Single point of │
│ failure         │
└─────────────────┘
```

### After
```
AZ-a ──┐
  App  │
       ├── ALB ── Internet
AZ-b ──┘
  App
       RDS Multi-AZ (standby in AZ-b)
```
