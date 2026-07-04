# Math Foundation for AWS Database

## DynamoDB Capacity Math

### RCU and WCU Calculation
```
Read:
  1 RCU = 1 strongly consistent read/sec of 4KB item
  Item size = 8KB → 2 RCU per strongly consistent read
  Item size = 4KB → 1 RCU per eventually consistent read (half cost)

Write:
  1 WCU = 1 write/sec of 1KB item
  Item size = 2.5KB → 3 WCU per write (round up)
  Item size = 0.5KB → 1 WCU per write

Throughput for 1000 items/sec, 3KB each:
  RCU = 1000 × ceil(3/4) × 1 = 1000 × 1 = 1000 RCU
  WCU = 1000 × ceil(3/1) = 1000 × 3 = 3000 WCU
```

### On-Demand Pricing
```
DynamoDB on-demand:
  $1.25 per million write request units
  $0.25 per million read request units
  Storage: $0.25/GB/month

Example: 10M writes/month, 50M reads/month, 100GB storage
  Writes: 10,000,000 / 1,000,000 × $1.25 = $12.50
  Reads:  50,000,000 / 1,000,000 × $0.25 = $12.50
  Storage: 100 × $0.25 = $25.00
  Total: $50/month
```

## RDS Pricing Math

### Multi-AZ Cost
```
RDS db.r5.large (2 vCPU, 16GB):
  Single-AZ: $0.24/hr
  Multi-AZ:  $0.48/hr (2x cost)

Annual cost:
  Single-AZ: $0.24 × 8760 = $2,102
  Multi-AZ:  $0.48 × 8760 = $4,204
```

### Reserved Instance Comparison
```
db.r5.large:
  On-Demand:              $0.24/hr
  1yr All Upfront:        $0.15/hr (37% savings)
  3yr All Upfront:        $0.11/hr (54% savings)
```

## Aurora IO Cost

### I/O-Optimized vs Standard
```
Aurora Standard:
  Storage: $0.10/GB/month
  I/O:     $0.20/1M requests

Aurora I/O-Optimized:
  Storage: $0.128/GB/month (28% more)
  I/O:     No charge

Break-even point:
  I/O cost at Standard = I/O price × I/O volume
  I/O-Opt cost = storage premium
  
  100GB, 500M IOs/month:
    Standard: 100×$0.10 + 500×$0.20 = $10 + $100 = $110
    IO-Opt:   100×$0.128 = $12.80 ✓
  
  100GB, 50M IOs/month:
    Standard: 100×$0.10 + 50×$0.20 = $10 + $10 = $20
    IO-Opt:   100×$0.128 = $12.80 ✓ (still cheaper)
```

## ElastiCache Redis Math

### Cluster Sizing
```
Node: cache.r5.large (2 vCPU, 13GB)
Data size: 10GB
Replication factor: 2 (1 primary, 1 replica)
Total needed: 20GB (including overhead)
Nodes required: 2 (13GB each, but overhead reduces to ~10GB usable)

Monthly cost:
  2 × $0.17/hr × 730 = $248.20/month
```
