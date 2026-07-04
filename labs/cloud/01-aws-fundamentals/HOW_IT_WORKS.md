# How AWS Fundamentals Works

## EC2 — Elastic Compute Cloud

### Lifecycle
1. **AMI selection**: Choose an OS/template (Amazon Linux, Ubuntu, Windows)
2. **Instance type**: Pick CPU/RAM/GPU (t3.micro, c5.large, m5.xlarge)
3. **Key pair**: SSH key injected at launch for access
4. **Network placement**: VPC, subnet, security group, public IP
5. **Storage**: EBS volumes attached as block devices
6. **Launch**: Instance boots from AMI, user data script runs
7. **Runtime**: Instance runs until stopped or terminated
8. **Billing**: Per-second billing while running (not stopped/terminated)

```java
// AWS SDK v2 — creating an EC2 instance
RunInstancesRequest request = RunInstancesRequest.builder()
    .imageId("ami-0c55b159cbfafe1f0")
    .instanceType(InstanceType.T3_MICRO)
    .keyName("my-key-pair")
    .securityGroupIds("sg-12345")
    .subnetId("subnet-abc")
    .maxCount(1).minCount(1)
    .build();
ec2Client.runInstances(request);
```

## S3 — Simple Storage Service

### Data Model
- **Buckets**: Global namespace, region-specific storage
- **Objects**: Key-value store (key = path, value = data)
- **Version ID**: Distinguishes object versions when versioning is on

### Request Flow
1. Client computes signature v4 (HMAC-SHA256)
2. Request goes to S3 REST endpoint
3. S3 authenticates via IAM/bucket policy
4. S3 routes to correct partition using key hash
5. Object stored on three devices across AZs (99.999999999% durability)

```java
PutObjectRequest request = PutObjectRequest.builder()
    .bucket("my-java-app-bucket")
    .key("config/application.properties")
    .build();
s3Client.putObject(request, Paths.get("application.properties"));
```

## IAM — Identity and Access Management

### Policy Evaluation
1. Request context (principal, action, resource, conditions)
2. AWS evaluates all applicable policies (identity-based + resource-based)
3. Default: **Deny** (explicit allow required)
4. Explicit deny overrides any allow
5. SCPs (organization policies) can restrict even root user

```json
{
  "Effect": "Allow",
  "Action": ["s3:GetObject", "s3:ListBucket"],
  "Resource": ["arn:aws:s3:::my-app-bucket", "arn:aws:s3:::my-app-bucket/*"]
}
```

## VPC — Virtual Private Cloud

### Traffic Flow
1. Instance sends packet to its subnet's route table
2. Route table matches destination CIDR
3. If local → delivered directly; if internet → internet gateway (IGW)
4. Security group evaluates stateful inbound/outbound rules
5. NACL evaluates stateless inbound/outbound rules
6. Packet reaches destination or is dropped
