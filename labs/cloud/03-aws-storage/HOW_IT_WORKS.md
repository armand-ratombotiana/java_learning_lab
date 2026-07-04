# How AWS Storage Works

## S3 PUT Operation Flow

1. **Request arrives** at S3 regional endpoint (e.g., s3.us-east-1.amazonaws.com)
2. **Authentication**: IAM signature v4 verified against caller's credentials
3. **Authorization**: Bucket policy, IAM policy, and ACLs evaluated
4. **Key routing**: Object key hashed to determine partition placement
5. **Storage**: Object written to three devices across at least three AZs
6. **Consistency**: PUT returns 200 OK only after all replicas acknowledge
7. **Notification**: If configured, S3 Event Notification sent to SQS/SNS/Lambda

```java
// S3 multipart upload for large files
S3AsyncClient s3Async = S3AsyncClient.builder()
    .multipartEnabled(true).build();

PutObjectRequest request = PutObjectRequest.builder()
    .bucket("my-app").key("large-file.dat").build();

s3Async.putObject(request, AsyncRequestBody.fromFile(Paths.get("large-file.dat")))
    .thenAccept(response -> System.out.println("ETag: " + response.eTag()));
```

## EBS Volume Lifecycle

1. **Create**: Volume provisioned in specific AZ, resources allocated
2. **Attach**: Attached to EC2 instance in same AZ as NVMe or Xen block device
3. **Format**: mkfs.ext4 /dev/xvdf (first time only)
4. **Mount**: mount /dev/xvdf /data
5. **Snapshot**: Point-in-time backup to S3 (incremental after first)
6. **Detach**: Volume detached from instance (preserves data)
7. **Delete**: Volume deleted (only if DeleteOnTermination = true)

```java
// Create and attach EBS volume
CreateVolumeRequest createReq = CreateVolumeRequest.builder()
    .size(100).volumeType(VolumeType.GP3)
    .availabilityZone("us-east-1a").iops(3000).build();
Volume volume = ec2Client.createVolume(createReq);

AttachVolumeRequest attachReq = AttachVolumeRequest.builder()
    .volumeId(volume.volumeId()).instanceId("i-xxx")
    .device("/dev/xvdf").build();
ec2Client.attachVolume(attachReq);
```

## EFS File System

1. **Create**: File system created in VPC, mount targets in each AZ
2. **Mount**: EC2 instances mount via NFSv4.1: `mount -t nfs4 fs-xxx.efs.us-east-1.amazonaws.com:/ /mnt/efs`
3. **Scale**: As files grow, EFS scales throughput automatically (bursting) or by provisioned setting
4. **Lifecycle**: Files not accessed for 30/90 days auto-move to EFS IA tier
5. **Access**: POSIX permissions via security group or EFS access points

## EBS vs Instance Store
| Feature | EBS | Instance Store |
|---------|-----|----------------|
| Persistence | Survives stop/terminate | Lost on stop/terminate |
| Performance | gp3 16K IOPS, io2 256K | NVMe up to 4M IOPS |
| Use case | DBs, persistent data | Cache, temp, swap |
| Backup | Snapshots to S3 | No snapshot support |
