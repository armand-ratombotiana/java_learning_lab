# Performance — AWS Compute

## Lambda Performance

### Memory vs CPU Allocation
| Memory | vCPU | Cold Start (Java) |
|--------|------|-------------------|
| 128MB | ~0.07 | 3-8s |
| 256MB | ~0.14 | 2-4s |
| 512MB | ~0.28 | 1-2s |
| 1024MB | ~0.57 | 500-800ms |
| 2048MB | ~1.14 | 300-500ms |
| 3008MB | ~1.71 | 200-400ms |

### SnapStart Performance
```java
// Enable SnapStart
// AWS Console → Lambda → SnapStart → "PublishedVersions"

// Before SnapStart
// Init: 4500ms (JVM start, class loading, DI initialization)
// Invoke: 50ms

// After SnapStart
// Restore: 200ms (restore snapshot, not full init)
// Invoke: 50ms
```

### Best Practices
- **Provisioned Concurrency**: Pre-warm N concurrent environments (for steady-state traffic)
- **Reserved Concurrency**: Limit max concurrent executions (prevent downstream throttling)
- **Burst**: 500-3000/min initial burst, then 500/min additional
- **Power Tune**: Use AWS Lambda Power Tuning tool (Step Function) to find optimal memory

## ECS/Fargate Performance

### CPU Allocation
```
Task CPU: 256 (.25 vCPU) → 1024 (1 vCPU) → 4096 (4 vCPU)
Java heap: Xmx = 70% of container memory (leave 30% for JVM overhead)

Example: container memory = 2048MB
  Xmx = 1433MB (-Xmx1433m)
  JVM overhead ~30% = 615MB
  Total = 2048MB (safe)
```

### Network Performance
- Fargate: up to 25 Gbps (for 4 vCPU+ tasks)
- ECS on EC2: up to 100 Gbps (with ENA + placement group)
- Use awsvpc network mode (each task gets its own ENI)

## EC2 Performance

### Burstable Instances (t3/t4g)
```
CPU Credit System:
- Earn: 1 CPU credit = 1 vCPU at 100% for 1 minute
- Baseline: t3.micro = 10% → 0.1 credits/minute → 144/day
- Burst: CPU > 10% spends credits
- Unlimited: $0.05/vCPU-hour when credits exhausted

Monitoring: aws cloudwatch get-metric-statistics
  --metric-name CPUCreditBalance --namespace AWS/EC2
```

### Nitro Performance
```java
// EBS-optimized by default on Nitro instances
// Max EBS throughput per instance type:
// m5.large: 3 Gbps EBS bandwidth
// m5.xlarge: 4.75 Gbps
// m5.2xlarge: 4.75 Gbps (same but more IOPS)
```
