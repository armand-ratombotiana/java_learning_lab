# Math Foundation for AWS Fundamentals

## Durability Calculations

### S3 Durability: 11 9s
- Durability = 99.999999999%
- Annual failure rate for one object = 1 − 0.99999999999 = 1 × 10⁻¹¹
- Expected objects lost per year = total_objects × 10⁻¹¹
- With 10 trillion objects: 10¹³ × 10⁻¹¹ = 100 objects/year expected loss

### EBS Durability
- gp3 volumes: 99.8–99.9% durability (annual failure rate 0.1–0.2%)
- io2 Block Express: 99.999% durability
- RAID 0 over two EBS volumes: durability = 1 − (1 − d)²

## Availability Calculations

### EC2 Instance Availability
- Single instance in one AZ: 99.5% (AWS SLA)
- Two instances across 2 AZs: 1 − (1 − 0.995)² = 99.9975%
- Three instances across 3 AZs: 1 − (1 − 0.995)³ = 99.9999875%

### S3 Availability
- S3 Standard: 99.99% availability (monthly uptime ≥ 99.99%)
- Annual allowed downtime: 8760h × (1 − 0.9999) = 0.876h ≈ 53 minutes/year

## Cost Calculations

### EC2 Pricing
```
On-Demand: price × hours_running
Reserved (1yr): price × 8760 × 0.6 (40% discount)
                  = price × 5256
Spot: price × spot_hours × spot_discount (60–90% off)

Break-even: reserved_cost / on_demand_hourly = hours
Example: t3.micro $0.0104/hr on-demand
        1yr reserved upfront $64 = $0.0073/hr
        Break-even: 64 / 0.0104 ≈ 6154 hours (~8.5 months)
```

### S3 Storage Cost
```
Monthly cost = GB_stored × price_per_GB + PUT_count × PUT_price + GET_count × GET_price

Example: 1TB Standard storage + 100K PUT + 1M GET
  Storage: 1024 × $0.023 = $23.55
  PUT:  100,000 × $0.000005 = $0.50
  GET: 1,000,000 × $0.0000004 = $0.40
  Total: $24.45/month
```

## Network Math

### CIDR Calculation
```
VPC: 10.0.0.0/16 (65,536 IPs)
  └── Public subnet: 10.0.1.0/24 (256 - 5 reserved = 251 usable)
  └── Private subnet: 10.0.2.0/24 (251 usable)
  └── DB subnet: 10.0.3.0/24 (251 usable)

Total usable: 753 out of 65,536 in VPC
```

### Availability Zone Math
```
n AZs with each having 99.5% instance availability:
P(at least one running) = 1 − (0.005)ⁿ
       n=1: 99.5%
       n=2: 99.9975%
       n=3: 99.9999875%
```
