package com.cloud.deep.lab02;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class CloudCostOptimizer {

    public enum PricingModel { ON_DEMAND, RESERVED_1YR, RESERVED_3YR, SPOT, RESERVED_1YR_PARTIAL, RESERVED_3YR_ALL }

    public record CostEstimate(double hourly, double monthly, double yearly, PricingModel model, double savingsVsOnDemand) {}

    public record InstanceType(String name, double vcpu, double memoryGb, double onDemandHourly) {}

    public record Workload(double avgCpu, double avgMemory, double peakCpu, double peakMemory, double hoursPerMonth) {}

    public record RightsizingRecommendation(String currentType, String recommendedType, double estimatedSavingsMonthly, double confidence) {}

    public static final List<InstanceType> INSTANCE_CATALOG = List.of(
        new InstanceType("t3.medium", 2, 4, 0.0416),
        new InstanceType("t3.large", 2, 8, 0.0832),
        new InstanceType("t3.xlarge", 4, 16, 0.1664),
        new InstanceType("m5.large", 2, 8, 0.096),
        new InstanceType("m5.xlarge", 4, 16, 0.192),
        new InstanceType("m5.2xlarge", 8, 32, 0.384),
        new InstanceType("c5.large", 2, 4, 0.085),
        new InstanceType("c5.xlarge", 4, 8, 0.17),
        new InstanceType("r5.large", 2, 16, 0.126),
        new InstanceType("r5.xlarge", 4, 32, 0.252)
    );

    public static CostEstimate calculateCost(InstanceType type, PricingModel model) {
        double onDemandHourly = type.onDemandHourly();
        double hourly = switch (model) {
            case ON_DEMAND -> onDemandHourly;
            case RESERVED_1YR -> onDemandHourly * 0.60;
            case RESERVED_3YR -> onDemandHourly * 0.40;
            case RESERVED_1YR_PARTIAL -> onDemandHourly * 0.65;
            case RESERVED_3YR_ALL -> onDemandHourly * 0.35;
            case SPOT -> onDemandHourly * (0.3 + Math.random() * 0.4);
        };
        double monthly = hourly * 730;
        double yearly = monthly * 12;
        double savings = (onDemandHourly - hourly) / onDemandHourly * 100;
        return new CostEstimate(hourly, monthly, yearly, model, savings);
    }

    public static RightsizingRecommendation recommendRightsizing(InstanceType current, Workload workload) {
        double cpuUtil = workload.avgCpu() / current.vcpu() * 100;
        double memUtil = workload.avgMemory() / current.memoryGb() * 100;
        if (cpuUtil < 20 && memUtil < 20) {
            var smaller = INSTANCE_CATALOG.stream()
                .filter(i -> i.vcpu() < current.vcpu() && i.memoryGb() >= workload.peakMemory())
                .min(Comparator.comparingDouble(InstanceType::onDemandHourly));
            if (smaller.isPresent()) {
                double savings = (current.onDemandHourly() - smaller.get().onDemandHourly()) * workload.hoursPerMonth();
                return new RightsizingRecommendation(current.name(), smaller.get().name(), savings, 0.85);
            }
        }
        return new RightsizingRecommendation(current.name(), current.name(), 0, 1.0);
    }

    public static class SpotFleet {
        private final List<InstanceType> types;
        private final Random rand = new Random();

        public SpotFleet(List<InstanceType> types) { this.types = types; }

        public InstanceType requestInstance() {
            double r = rand.nextDouble();
            if (r < 0.3 && types.size() > 1) {
                return types.get(rand.nextInt(types.size()));
            }
            return types.get(0);
        }
    }

    public static void main(String[] args) {
        var instance = INSTANCE_CATALOG.stream().filter(i -> i.name().equals("m5.xlarge")).findFirst().orElseThrow();
        System.out.println("=== Pricing Comparison for " + instance.name() + " ===");
        for (var model : PricingModel.values()) {
            var cost = calculateCost(instance, model);
            System.out.printf("%-20s $%.4f/hr  $%.2f/mo  (%.0f%% savings)%n", model, cost.hourly(), cost.monthly(), cost.savingsVsOnDemand());
        }

        var workload = new Workload(1.2, 4.5, 3.8, 10, 730);
        var rec = recommendRightsizing(instance, workload);
        System.out.printf("%nRightsizing: %s -> %s (save $%.2f/mo)%.0f%% confidence%n",
            rec.currentType(), rec.recommendedType(), rec.estimatedSavingsMonthly(), rec.confidence() * 100);

        var fleet = new SpotFleet(List.of(
            INSTANCE_CATALOG.get(0), INSTANCE_CATALOG.get(2), INSTANCE_CATALOG.get(4)));
        System.out.println("\nSpot fleet requests:");
        for (int i = 0; i < 5; i++) {
            var inst = fleet.requestInstance();
            var cost = calculateCost(inst, PricingModel.SPOT);
            System.out.printf("  %s @ $%.4f/hr%n", inst.name(), cost.hourly());
        }
    }
}
