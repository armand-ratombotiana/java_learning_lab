# Math Foundation for AWS Compute

## Lambda Pricing Math

### Cost Formula
```
Total cost = Request cost + Duration cost + Provisioned Concurrency cost

Request cost:
  = number_of_requests × $0.20 per 1M requests
  = N × 0.00000020

Duration cost:
  = total_compute_seconds × allocated_memory / 1024 × $0.0000166667 per GB-second

Example: 10M requests, 256MB, 200ms average
  Requests: 10,000,000 × $0.00000020 = $2.00
  Duration: 10,000,000 × 0.2 × (256/1024) × $0.0000166667
          = 2,000,000 × 0.25 × $0.0000166667
          = 500,000 × $0.0000166667
          = $8.33
  Total: $10.33/month
```

### Cold Start Overhead
```
Total latency = cold_start_probability × cold_start_time + warm_latency

With SnapStart (Java):
  Cold start: ~200ms (vs ~5000ms without)
  Warm: ~5ms

Cold start rate varies by memory:
  128MB: ~2-5s cold start
  512MB: ~800-1500ms
  1024MB: ~400-800ms
  3008MB: ~200-500ms
```

## EC2 Reserved Instance Break-Even

```
On-Demand: $0.096/hr (m5.large)
1yr Standard Reserved (partial upfront): $0.061/hr (36% savings)
3yr Standard Reserved (partial upfront): $0.046/hr (52% savings)

Break-even:
  If you run > 4 months continuously → Reserved is cheaper
  If you run < 4 months → On-Demand is cheaper (no commitment)

Spot: up to 90% off
  Spot price varies: typically $0.0096-$0.0288/hr for m5.large
  Use for: batch jobs, stateless apps, CI/CD runners
```

## Auto Scaling Math

### Target Tracking Policy
```
Desired capacity = max(1, ceiling(current_load / target_per_instance))

CPU target = 50%
Current CPU = 75% across 4 instances
Equivalent_instances = 4 × (75/50) = 6 → desired = 6
```

### Step Scaling
```
Scale out: +2 instances when CPU > 70% for 3 minutes
Scale in:  -1 instance when CPU < 30% for 10 minutes

Cooldown: 300 seconds between scaling activities
```

## ECS/Fargate CPU Allocation

```
CPU units: 1 vCPU = 1024 CPU units

Throttling:
  If task uses > allocated CPU for sustained period → throttled
  Burst allowed up to 2× allocated for short periods (100ms-10s)

Memory:
  Task memory = hard limit (OOM killer if exceeded)
  Java heap should be ≤ 70% of container memory
  Container memory = 512MB → Xmx = 358MB
```
