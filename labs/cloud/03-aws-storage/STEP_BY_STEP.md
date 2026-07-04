# Step-by-Step — S3 Lifecycle Policy for Log Archival

## Step 1: Create S3 Bucket with Versioning
```powershell
# Create bucket
aws s3 mb s3://java-app-logs-prod --region us-east-1

# Enable versioning
aws s3api put-bucket-versioning --bucket java-app-logs-prod `
    --versioning-configuration Status=Enabled
```

## Step 2: Apply Lifecycle Policy
```json
// lifecycle.json
{
  "Rules": [
    {
      "Id": "LogLifecycle",
      "Status": "Enabled",
      "Filter": {"Prefix": "logs/"},
      "Transitions": [
        {"Days": 30, "StorageClass": "STANDARD_IA"},
        {"Days": 90, "StorageClass": "GLACIER"},
        {"Days": 365, "StorageClass": "DEEP_ARCHIVE"}
      ],
      "Expiration": {
        "Days": 2555,
        "ExpiredObjectDeleteMarker": true
      },
      "NoncurrentVersionTransitions": [
        {"NoncurrentDays": 7, "StorageClass": "STANDARD_IA"}
      ],
      "NoncurrentVersionExpiration": {"NoncurrentDays": 90}
    }
  ]
}
```

```powershell
aws s3api put-bucket-lifecycle-configuration `
    --bucket java-app-logs-prod `
    --lifecycle-configuration file://lifecycle.json
```

## Step 3: Write Java Log Uploader
```java
// LogUploader.java
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LogUploader {
    private final S3Client s3;
    private final String bucket;

    public LogUploader(String bucket) {
        this.s3 = S3Client.create();
        this.bucket = bucket;
    }

    public void uploadLog(Path logFile) {
        String key = "logs/" + LocalDate.now()
            .format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" +
            logFile.getFileName();
        s3.putObject(PutObjectRequest.builder()
                .bucket(bucket).key(key)
                .storageClass(StorageClass.STANDARD)
                .build(), logFile);
    }

    public static void main(String[] args) {
        new LogUploader("java-app-logs-prod")
            .uploadLog(Paths.get("application.log"));
    }
}
```

## Step 4: Schedule Upload with Cron
```bash
# crontab — upload logs hourly
0 * * * * /usr/bin/java -jar /opt/log-uploader.jar /var/log/app/access.log
```

## Step 5: Verify Lifecycle
```powershell
# Check current storage class
aws s3api list-objects --bucket java-app-logs-prod --prefix logs/ `
    --query "Contents[].{Key:Key, StorageClass:StorageClass}"

# Check lifecycle rules
aws s3api get-bucket-lifecycle-configuration --bucket java-app-logs-prod
```

## Step 6: Retrieve from Glacier
```powershell
# Initiate restore
aws s3api restore-object --bucket java-app-logs-prod `
    --key logs/2024/01/01/access.log `
    --restore-request '{"Days":7,"GlacierJobParameters":{"Tier":"Expedited"}}'

# Check restore status
aws s3api head-object --bucket java-app-logs-prod `
    --key logs/2024/01/01/access.log `
    --query Restore
```
