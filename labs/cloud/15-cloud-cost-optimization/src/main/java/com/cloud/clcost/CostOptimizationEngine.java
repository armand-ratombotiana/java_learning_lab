package com.cloud.clcost;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class CostOptimizationEngine {

    public record PricingData(String instanceType, String region, double onDemandHourly,
                               double riHourly, double spotHourly, double savingsPlanHourly) {}

    public static class PricingCatalog {
        private final Map<String, PricingData> catalog = new ConcurrentHashMap<>();

        public PricingCatalog() {
            catalog.put("t3.medium", new PricingData("t3.medium", "us-east-1", 0.0416, 0.0125, 0.0125, 0.0146));
            catalog.put("m5.large", new PricingData("m5.large", "us-east-1", 0.096, 0.0288, 0.0288, 0.0336));
            catalog.put("c5.xlarge", new PricingData("c5.xlarge", "us-east-1", 0.17, 0.051, 0.051, 0.0595));
            catalog.put("r5.large", new PricingData("r5.large", "us-east-1", 0.126, 0.0378, 0.0378, 0.0441));
            catalog.put("m5.xlarge", new PricingData("m5.xlarge", "us-east-1", 0.192, 0.0576, 0.0576, 0.0672));
        }

        public PricingData getPricing(String instanceType) {
            return catalog.getOrDefault(instanceType, new PricingData(instanceType, "us-east-1", 0.10, 0.03, 0.02, 0.035));
        }
    }

    public record CostReport(String instanceType, String pricingModel, double hourlyRate,
                              double monthlyCost, double annualCost, double savingsVsOnDemand) {}

    public List<CostReport> generateReport(String instanceType, int hoursPerMonth) {
        PricingCatalog catalog = new PricingCatalog();
        PricingData data = catalog.getPricing(instanceType);
        return List.of(
            new CostReport(instanceType, "On-Demand", data.onDemandHourly(),
                data.onDemandHourly() * hoursPerMonth, data.onDemandHourly() * hoursPerMonth * 12, 0),
            new CostReport(instanceType, "Reserved Instance", data.riHourly(),
                data.riHourly() * hoursPerMonth, data.riHourly() * hoursPerMonth * 12,
                (data.onDemandHourly() - data.riHourly()) * hoursPerMonth * 12),
            new CostReport(instanceType, "Spot", data.spotHourly(),
                data.spotHourly() * hoursPerMonth, data.spotHourly() * hoursPerMonth * 12,
                (data.onDemandHourly() - data.spotHourly()) * hoursPerMonth * 12),
            new CostReport(instanceType, "Savings Plan", data.savingsPlanHourly(),
                data.savingsPlanHourly() * hoursPerMonth, data.savingsPlanHourly() * hoursPerMonth * 12,
                (data.onDemandHourly() - data.savingsPlanHourly()) * hoursPerMonth * 12)
        );
    }

    public record RightsizingRecommendation(String currentType, String recommendedType,
                                              String reason, double monthlySavings) {}

    public RightsizingRecommendation analyzeRightsizing(String currentType, double avgCpu, double avgMemory) {
        if (avgCpu < 20 && avgMemory < 30) {
            return switch (currentType) {
                case "m5.xlarge" -> new RightsizingRecommendation(currentType, "m5.large", "Low CPU/Memory utilization", 70.08);
                case "m5.large" -> new RightsizingRecommendation(currentType, "m5.medium", "Low CPU/Memory utilization", 35.04);
                case "c5.xlarge" -> new RightsizingRecommendation(currentType, "c5.large", "Low CPU/Memory utilization", 62.05);
                default -> new RightsizingRecommendation(currentType, currentType, "Already optimal", 0);
            };
        }
        return new RightsizingRecommendation(currentType, currentType, "Utilization adequate", 0);
    }

    public static class SpotFleetManager {
        private final Set<String> activeSpotInstances = new ConcurrentHashSet<>();
        private final Random random = new Random();

        public String requestSpotInstance(String instanceType, double maxPrice) {
            String id = "sir-" + UUID.randomUUID().toString().substring(0, 8);
            activeSpotInstances.add(id);
            System.out.println("Spot request " + id + " for " + instanceType + " @ $" + maxPrice + "/hr");
            return id;
        }

        public int checkInterruptions() {
            long interrupted = activeSpotInstances.stream()
                .filter(id -> random.nextDouble() < 0.05)
                .peek(id -> System.out.println("Spot interruption for " + id))
                .count();
            return (int) interrupted;
        }

        public int getActiveCount() { return activeSpotInstances.size(); }
    }

    static class ConcurrentHashSet<T> {
        private final Set<T> backing = ConcurrentHashMap.newKeySet();
        public boolean add(T e) { return backing.add(e); }
        public boolean remove(T e) { return backing.remove(e); }
        public int size() { return backing.size(); }
        public Stream<T> stream() { return backing.stream(); }
    }

    public static void main(String[] args) {
        CostOptimizationEngine engine = new CostOptimizationEngine();
        List<CostReport> reports = engine.generateReport("m5.large", 730);
        reports.forEach(r -> System.out.println(r.pricingModel() + ": $" + String.format("%.2f", r.monthlyCost()) +
            "/mo, saves $" + String.format("%.2f", r.savingsVsOnDemand()) + "/yr"));

        RightsizingRecommendation rec = engine.analyzeRightsizing("m5.xlarge", 15.0, 25.0);
        System.out.println("Rightsizing: " + rec.currentType() + " -> " + rec.recommendedType() +
            " (save $" + String.format("%.2f", rec.monthlySavings()) + "/mo)");

        SpotFleetManager spot = new SpotFleetManager();
        spot.requestSpotInstance("t3.medium", 0.015);
        System.out.println("Active spot instances: " + spot.getActiveCount());
    }
}
