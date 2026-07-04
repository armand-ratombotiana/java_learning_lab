# Math Foundation for AWS Storage

## S3 Durability

### 11 9s Explained
```
Durability = 99.999999999%
Annual failure rate = 1 - 0.99999999999 = 1 × 10⁻¹¹

Expected annual object loss:
  For 10¹² objects: 10¹² × 10⁻¹¹ = 10 objects (very conservatively)
  Actual: much lower due to background repair and checksums

Why 11 9s?
  3 AZs × 3 replicas × 2 disks (RAID-1 mirror) = 18 copies
  Probability all 18 fail simultaneously: P(failure)¹⁸
  P(disk failure/year) ≈ 1%
  P(all 18 fail) = (0.01)¹⁸ = 1 × 10⁻³⁶ (far better than 11 9s)
```

## S3 Availability

| Storage Class | Availability SLA | Monthly Downtime |
|--------------|:---------------:|:----------------:|
| Standard | 99.99% | 4.38 minutes |
| Standard-IA | 99.9% | 43.2 minutes |
| One Zone-IA | 99.5% | 3.65 hours |
| Glacier | 99.99% | 4.38 minutes |
| Deep Archive | 99.99% | 4.38 minutes |

## Storage Cost Comparison

### 1 TB Monthly Cost (us-east-1, 2024)
```
S3 Standard:         1024 × $0.023 = $23.55
S3 Standard-IA:      1024 × $0.0125 = $12.80 + retrieval fee
S3 One Zone-IA:      1024 × $0.01 = $10.24
S3 Glacier:          1024 × $0.0036 = $3.69
S3 Deep Archive:     1024 × $0.00099 = $1.01

EBS gp3 (1TB):      1024 × $0.08 = $81.92 (3K IOPS included)
EBS io2 (1TB):      1024 × $0.125 = $128.00 + IOPS cost

EFS (1TB):          1024 × $0.30 = $307.20
EFS IA (1TB):       1024 × $0.016 = $16.38
```

### Lifecycle Cost Example
```
Object uploaded to Standard, accessed daily for 30 days, then monthly for a year,
then yearly for 3 years:
  Standard: 30d × ($0.023/GB/mo) = $0.023/GB
  Standard-IA: 11mo × ($0.0125/GB/mo) = $0.1375/GB
  Glacier: 2yr × ($0.0036/GB/mo) = $0.0864/GB
  Total per GB: ~$0.25

vs. Keeping in Standard for 3 years:
  36mo × $0.023 = $0.828/GB
  Savings: ~70%
```

## EBS IOPS Math

### gp3 IOPS
```
Baseline: 3000 IOPS (included in volume price)
Additional IOPS: $0.005 per provisioned IOPS over 3000
Max: 16000 IOPS

Cost for 10000 IOPS:
  3000 baseline (included)
  7000 additional × $0.005 = $35.00/month (for 1TB volume)
```

### io2 Block Express
```
Max IOPS: 256,000
Cost: $0.065/GB/month + $0.004/provisioned IOPS over 64K
For 1TB with 100K IOPS:
  1000 × $0.065 = $65
  (100000 - 64000) × $0.004 = $144
  Total: $209/month
```

## EFS Throughput Math

### Bursting Mode
```
Throughput = Filesystem Size × 50 MiB/s per TiB
For 100GB: 0.1 × 50 = 5 MiB/s baseline, up to 100 MiB/s burst
Burst credits accumulate when below baseline
```

### Provisioned Mode
```
Flat rate regardless of size:
  1 MiB/s: $6.00/month
  10 MiB/s: $60.00/month
  100 MiB/s: $600.00/month
```
