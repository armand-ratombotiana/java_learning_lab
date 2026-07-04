# Debugging — AWS Fundamentals

## EC2 Debugging

### Instance Won't Start
```
Symptom: state = "pending" indefinitely
Check:   aws ec2 describe-instance-status --instance-ids i-xxx
  - Insufficient capacity: try different AZ or instance type
  - EBS volume limit exceeded: check volume count/GB limit
  - AMI not found: verify AMI ID exists in this region
```

### Cannot SSH
```
Symptom: "Connection timed out" or "Connection refused"
Checklist:
  1. Security group allows port 22 from your IP
     aws ec2 describe-security-groups --group-ids sg-xxx
  2. Instance has public IP (or use bastion)
  3. Key pair matches: you have the .pem file, correct permissions
  4. NACL allows inbound ephemeral ports (1024-65535)
  5. Route table has 0.0.0.0/0 → IGW for public subnet
```

### High CPU / Memory
```
Diagnose:
  ssh -i key.pem ec2-user@<ip>
  top -bn1
  free -m
  df -h
  dmesg | tail -20

Common causes:
  - Insufficient instance size (t3.micro for high-traffic Java app)
  - Memory leak in Java (use jstat, jmap, jstack)
  - CPU credits exhausted (t2/t3 burstable — check CPUCreditBalance)
```

## S3 Debugging

### Access Denied
```
Symptom: 403 AccessDenied on PUT/GET
Debug:
  1. Check bucket policy: Principal allows your IAM user/role
  2. Check IAM policy: Action s3:PutObject allowed
  3. Check S3 Block Public Access settings
  4. If presigned URL: verify expiration and signature v4
  5. If cross-account: verify bucket policy allows external principal

CLI: aws s3api put-object --bucket my-bucket --key test.txt --body test.txt
     --debug  (shows exact request/response)
```

### Slow Uploads
```
Symptom: Large files upload slowly
Fix:
  - Enable multipart upload for >100MB files
  - Use S3 Transfer Acceleration for cross-region
  - Check network bandwidth: iperf3
  - Use AWS SDK v2 with CRT-based S3 client (parallel multipart)
```

## IAM Debugging

### Unexpected Deny
```
Symptom: API call returns AccessDenied, but policies look correct
Debug:
  1. Check if an explicit Deny exists (SCP, identity, resource)
  2. Check if service control policy (SCP) at organization level blocks it
  3. Use IAM Policy Simulator:
     aws iam simulate-principal-policy
         --policy-source-arn arn:aws:iam::123456789012:user/admin
         --action-names ec2:RunInstances
  4. Check session policies (assume-role with policy ARN parameter)
```

## VPC Debugging

### No Internet Access from Private Subnet
```
Symptom: EC2 in private subnet can't ping google.com
Checklist:
  1. NAT Gateway is running in public subnet
  2. Private subnet route table: 0.0.0.0/0 → nat-xxxxx
  3. NAT Gateway has Elastic IP
  4. Public subnet route table: 0.0.0.0/0 → igw-xxxxx
  5. NACL allows outbound traffic (port 80, 443, ephemeral)

Test: aws ec2 describe-nat-gateways --nat-gateway-ids nat-xxx
```

### Connectivity Issues Across AZs
```
Symptom: Traffic between AZs is slow or drops
  - Check VPC Flow Logs for dropped packets
  - Verify security groups allow cross-AZ traffic
  - Check inter-AZ data transfer costs (~$0.01/GB)
  - Keep data-local within AZ when possible
```
