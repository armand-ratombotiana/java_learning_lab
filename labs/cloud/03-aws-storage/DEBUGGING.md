# Debugging — AWS Storage

## S3 Debugging

### Access Denied (403)
```
Troubleshoot:
  1. aws s3api get-bucket-policy --bucket xxx
     Check Principal allows caller
  2. aws iam simulate-principal-policy
     --policy-source-arn arn:aws:iam::xxx:user/user
     --action-names s3:GetObject
  3. Check S3 Block Public Access in bucket permissions tab
  4. If presigned URL: check expiration time, signature v4

CLI debug: aws s3api get-object --bucket xxx --key yyy out.txt --debug
```

### Upload Fails for Large Files
```
Symptom: Connection reset or timeout for >100MB files
Root cause: Single PUT operation, no multipart
Fix:
  - Enable multipart in SDK config
  - Increase TCP keepalive settings
  - Use S3 Transfer Acceleration for cross-region
  - Check upload limits: max 5GB single PUT, 5TB with multipart
```

### Versioning Confusion
```
Symptom: Can't find object, versioning is on
Issue: DELETE creates a delete marker, not actually deleting
Fix: List all versions:
  aws s3api list-object-versions --bucket xxx --prefix key
  Delete specific version:
  aws s3api delete-object --bucket xxx --key yyy --version-id zzz
```

## EBS Debugging

### Volume Stuck in "attaching" State
```
Wait 2-3 minutes
If still stuck: force detach (risk of data loss)
  aws ec2 detach-volume --volume-id vol-xxx --force
  Then re-attach
```

### High EBS Latency
```
Check:
  - aws cloudwatch get-metric-statistics --namespace AWS/EBS
    --metric-name VolumeQueueLength --dimensions Name=VolumeId,Value=vol-xxx
  - Queue length > 1 means IOPS bottleneck
  - Check VolumeWriteBytes / VolumeReadBytes to see throughput

Fix:
  - Increase gp3 IOPS
  - Migrate to io2 for consistent low latency
  - Check if Nitro instance (better EBS performance)
```

### EBS Snapshot Slow
```
Symptom: First snapshot takes hours for large volume
Reason: First snapshot is full copy
Fix:
  - Subsequent snapshots are incremental (faster)
  - Use EBS fast snapshot restore for pre-warmed snapshots
  - Schedule snapshots during low-traffic periods
```

## EFS Debugging

### Mount Hangs
```
Symptom: mount.nfs4 command hangs indefinitely
Checklist:
  - Security group: allow NFS (2049) inbound from EC2 SG
  - NACL allows ephemeral ports (1024-65535)
  - EFS mount target is in the same AZ as EC2
  - DNS resolves: nslookup fs-xxx.efs.us-east-1.amazonaws.com

Fix: force mount with timeout:
  mount -t nfs4 -o nfsvers=4.1,rsize=1048576,wsize=1048576,hard,timeo=600,retrans=2
  fs-xxx.efs.us-east-1.amazonaws.com:/ /mnt/efs
```
