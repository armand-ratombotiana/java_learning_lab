# GUIDE — Cloud Cost Optimization

## Step 1: Pricing Model Abstraction
```java
public enum PricingModel { ON_DEMAND, RESERVED_1YR, RESERVED_3YR, SPOT }
public record CostEstimate(double hourly, double monthly, double yearly, PricingModel model) {}
```

## Step 2: Reserved Instance Analyzer
- Calculate break-even between RI and on-demand
- Compare partial vs full upfront payment
- Evaluate convertible RI flexibility premiums

## Step 3: Spot Instance Manager
- Model spot price history with moving averages
- Implement interruption detection and fallback to on-demand
- Build spot fleet diversification strategy

## Step 4: Auto-Scaling Cost Simulator
```java
ScalingPolicy policy = new ScalingPolicy(cpuTarget, minSize, maxSize, coolDownSeconds);
AutoScaler scaler = new AutoScaler(policy, cloudCompute);
scaler.simulate(workloadPattern);
```

## Step 5: Rightsizing Engine
- Collect utilization metrics (CPU, memory, network)
- Match workload profiles to optimal instance families
- Generate savings recommendations with confidence scores

## Step 6: Exercises
1. Implement a FinOps dashboard that aggregates cost by team tag
2. Build a spot instance bid price optimizer using historical pricing
3. Create a reserved instance purchase recommendation engine
