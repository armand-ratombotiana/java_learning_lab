# Performance — AWS Fundamentals

## EC2 Performance

### Instance Selection Guide
| Workload | Instance Family | Example | Why |
|----------|----------------|---------|-----|
| Java web app (low traffic) | Burstable | t3.medium | Cost-effective, bursts handle spikes |
| Java web app (high traffic) | General | m6i.large | Consistent performance, no CPU credit exhaustion |
| Batch processing | Compute | c6i.xlarge | More vCPUs for parallel processing |
| In-memory cache | Memory | r6i.large | High RAM per vCPU |
| GPU workloads | GPU | g5.xlarge | ML inference, video encoding |

### CPU Credit Math (t3 instances)
```
CPUCreditBalance accumulates when CPU < baseline
Baseline for t3.micro: 10% of 1 vCPU = 0.1 credits/minute

If CPU = 20% for 5 minutes: spends 5 × 0.2 = 1.0 credit
Unlimited mode: AWS charges $0.05 per vCPU-hour over baseline
```

### EBS Optimization
- **gp3**: Baseline 3000 IOPS (free), up to 16000 IOPS ($0.005/provisioned IOPS)
- **io2 Block Express**: 256K IOPS, 4000 MB/s — for latency-sensitive Java apps
- **EBS Nitro**: Instances with Nitro get max EBS performance
- **RAID 0**: Striping across EBS volumes doubles throughput

## S3 Performance

### Best Practices
- **Prefix optimization**: S3 achieves 3500 PUT/COPY/POST/DELETE or 5500 GET/HEAD per prefix
- **Parallel uploads**: Split large files into 100MB+ parts (multipart upload)
- **Transfer Acceleration**: Use CloudFront edge locations for long-distance uploads
- **S3 Select**: Retrieve only subset of data from CSV/JSON/Parquet files (reduce data transfer)

### SDK Performance
```java
// AWS SDK v2 with CRT S3 client — up to 5x faster
S3AsyncClient s3Async = S3AsyncClient.builder()
    .multipartEnabled(true)
    .checksumValidationEnabled(false) // for throughput, skip if not critical
    .build();
```

## IAM Performance

### Latency Considerations
- IAM policy evaluation adds ~1-5ms per API call
- STS AssumeRole: ~500ms-2s (generates temporary credentials)
- Cache temporary credentials for 15-60 minutes to reduce STS calls
- Use IAM roles (EC2 instance profile) to avoid STS calls entirely

## VPC Performance

### Network Optimization
- **Placement Groups**: Cluster placement (10 Gbps) for tightly-coupled Java apps
- **ENA Express**: Up to 100 Gbps with SRD (Scalable Reliable Datagram)
- **Keep traffic in AZ**: Inter-AZ data costs $0.01/GB; same-AZ is free
- **VPC endpoints**: Access S3/DynamoDB without NAT Gateway (lower latency, no cost)

### Benchmark Example
```java
// Measure S3 upload latency from EC2 in same region
long start = System.nanoTime();
s3Client.putObject(req, Paths.get("data.bin"));
long elapsed = System.nanoTime() - start;
System.out.println("S3 upload: " + elapsed / 1_000_000 + "ms");
```
