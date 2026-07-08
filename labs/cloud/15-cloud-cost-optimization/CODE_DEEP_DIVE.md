# 15 — Cloud Cost Optimization — Code Deep Dive

## 1. Reserved Instance Modeling

### RI Calculator and Analyzer
```java
public record ReservationParams(String instanceType, String region, PaymentOption payment, int termYears) {}

public enum PaymentOption { ALL_UPFRONT, PARTIAL_UPFRONT, NO_UPFRONT }

public class RIProfitabilityCalculator {
    private static final Map<String, Double> onDemandRates = Map.of(
        "t3.medium", 0.0416, "m5.large", 0.096, "c5.xlarge", 0.17, "r5.large", 0.126
    );
    private static final Map<String, Double> riDiscounts = Map.of(
        "ALL_UPFRONT", 0.72, "PARTIAL_UPFRONT", 0.62, "NO_UPFRONT", 0.55
    );

    public record SavingsReport(String instanceType, double monthlyOnDemand, double monthlyRI,
                                 double monthlySavings, double oneYearSavings, double threeYearSavings) {}

    public SavingsReport calculateSavings(String instanceType, int hoursPerMonth,
                                           PaymentOption payment, int termYears) {
        double hourlyOnDemand = onDemandRates.getOrDefault(instanceType, 0.05);
        double monthlyOnDemand = hourlyOnDemand * hoursPerMonth;
        double discountFactor = 1 - riDiscounts.get(payment.name());
        double hourlyRI = hourlyOnDemand * discountFactor;
        double monthlyRI = hourlyRI * hoursPerMonth;
        double monthlySavings = monthlyOnDemand - monthlyRI;
        double termSavings = monthlySavings * 12 * termYears;

        return new SavingsReport(instanceType, monthlyOnDemand, monthlyRI,
            monthlySavings, monthlySavings * 12, termSavings);
    }

    public List<SavingsReport> compareAll(int hoursPerMonth) {
        return onDemandRates.keySet().stream()
            .flatMap(type -> Stream.of(PaymentOption.values())
                .map(payment -> calculateSavings(type, hoursPerMonth, payment, 1)))
            .toList();
    }
}
```

## 2. Rightsizing Engine

### Instance Rightsizing Recommender
```java
public record InstanceMetrics(String instanceId, double avgCpu, double peakCpu,
                               double avgMemory, double peakMemory, double avgNetworkIO) {}

public record RightsizingRecommendation(String instanceId, String currentType,
                                         String recommendedType, double estimatedSavings,
                                         String reason) {}

public class RightsizingEngine {
    private static final Map<String, String> DOWNSIZE_MAP = Map.of(
        "m5.xlarge", "m5.large",
        "m5.large", "m5.medium",
        "c5.xlarge", "c5.large",
        "r5.xlarge", "r5.large"
    );

    public RightsizingRecommendation analyze(InstanceMetrics metrics) {
        String currentType = metrics.instanceId().contains("-") ?
            metrics.instanceId().substring(0, metrics.instanceId().indexOf('-')) : "m5.large";

        if (metrics.avgCpu() < 20 && metrics.avgMemory() < 30) {
            String recommended = DOWNSIZE_MAP.getOrDefault(currentType, "t3.medium");
            double estimatedSavings = estimateSavings(currentType, recommended);
            return new RightsizingRecommendation(metrics.instanceId(), currentType,
                recommended, estimatedSavings,
                "CPU avg " + metrics.avgCpu() + "%, Memory avg " + metrics.avgMemory() + "%");
        }

        if (metrics.peakCpu() > 80 || metrics.peakMemory() > 85) {
            return new RightsizingRecommendation(metrics.instanceId(), currentType,
                currentType, 0, "No downsizing recommended: peak utilization high");
        }

        return new RightsizingRecommendation(metrics.instanceId(), currentType,
            currentType, 0, "Currently right-sized");
    }

    private double estimateSavings(String current, String recommended) {
        Map<String, Double> rates = Map.of(
            "m5.xlarge", 0.192, "m5.large", 0.096, "m5.medium", 0.048,
            "c5.xlarge", 0.17, "c5.large", 0.085,
            "r5.xlarge", 0.252, "r5.large", 0.126
        );
        double currentRate = rates.getOrDefault(current, 0.10);
        double recommendedRate = rates.getOrDefault(recommended, 0.05);
        return (currentRate - recommendedRate) * 730; // monthly savings (730 hours)
    }
}
```

## 3. Spot Instance Manager

### Spot Instance Lifecycle Manager
```java
public class SpotInstanceManager {
    private final Set<String> activeSpotInstances = new ConcurrentHashSet<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public record SpotRequest(String instanceType, double maxPrice, String region) {}

    public String requestSpotInstance(SpotRequest request) {
        String instanceId = "s-" + UUID.randomUUID().toString().substring(0, 8);
        activeSpotInstances.add(instanceId);
        System.out.println("Spot instance " + instanceId + " requested: " + request);

        // Simulate 2-minute interruption warning
        scheduler.schedule(() -> {
            if (Math.random() < 0.1) { // 10% chance of interruption
                handleInterruption(instanceId);
            }
        }, ThreadLocalRandom.current().nextInt(30, 300), TimeUnit.SECONDS);

        return instanceId;
    }

    private void handleInterruption(String instanceId) {
        System.out.println("WARNING: Spot instance " + instanceId + " will be interrupted in 2 minutes");
        checkpointWork(instanceId);
        activeSpotInstances.remove(instanceId);
        requestReplacementInstance(instanceId);
    }

    private void checkpointWork(String instanceId) {
        System.out.println("Checkpointing work from " + instanceId + " to S3...");
        // In production: save state to S3, DynamoDB, or EFS
    }

    private void requestReplacementInstance(String oldInstanceId) {
        System.out.println("Requesting replacement for " + oldInstanceId);
        // In production: launch new spot instance via EC2 Fleet
    }

    public int getActiveCount() { return activeSpotInstances.size(); }
}

// ConcurrentHashSet helper
class ConcurrentHashSet<T> {
    private final Set<T> backingSet = ConcurrentHashMap.newKeySet();
    public boolean add(T e) { return backingSet.add(e); }
    public boolean remove(T e) { return backingSet.remove(e); }
    public int size() { return backingSet.size(); }
}
```
