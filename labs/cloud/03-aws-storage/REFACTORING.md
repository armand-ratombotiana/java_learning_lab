# Refactoring — AWS Storage

## 1. From EBS to S3 for Application Logs

### Before
```java
// Log files written to EBS volume attached to single EC2
FileWriter fw = new FileWriter("/var/log/app/events.log");
fw.write(event.toString());
fw.close();
// Problem: fills up EBS, no centralized access, lost if instance terminated
```

### After
```java
// Logs written to S3 (centralized, durable, lifecycle-managed)
s3Client.putObject(PutObjectRequest.builder()
    .bucket("app-logs").key("events/2024/01/01/" + UUID.randomUUID() + ".log")
    .build(), new String(event.toString()));
```

## 2. From Direct Upload to Presigned URL

### Before
```java
// App server receives file, uploads to S3 (needs full S3 write permissions)
byte[] fileContent = request.getPart("file").getInputStream().readAllBytes();
s3Client.putObject(PutObjectRequest.builder()
    .bucket("uploads").key(fileId).build(), fileContent);
```

### After
```java
// Generate presigned URL, client uploads directly (no S3 permissions on app server)
PresignedPutObjectRequest presigned = presigner.presignPutObject(r -> r
    .signatureDuration(Duration.ofMinutes(10))
    .putObjectRequest(PutObjectRequest.builder()
        .bucket("uploads").key(fileId).build()));
return presigned.url().toString();
```

## 3. From EBS to EFS for Shared Content

### Before
```
Each EC2 instance has its own EBS volume with uploaded images
Image uploaded to instance A → not visible on instance B
Requires file sync, rsync, or NFS setup
```

### After
```
All EC2 instances mount same EFS file system
Image uploaded → visible on all instances immediately
No sync needed, POSIX permissions for access control
```

## 4. From Single S3 Class to Lifecycle-Managed Tiers

### Before
```
All objects in S3 Standard: $23.55/TB/month
No automation, all objects pay the same regardless of access pattern
```

### After
```
Lifecycle policy:
  Days 0-30:  S3 Standard (frequent access)
  Days 31-90: S3 Standard-IA ($0.0125/GB — 46% savings)
  Days 91-365: S3 Glacier ($0.0036/GB — 84% savings)
  After 365:  S3 Deep Archive ($0.001/GB — 96% savings)
```
