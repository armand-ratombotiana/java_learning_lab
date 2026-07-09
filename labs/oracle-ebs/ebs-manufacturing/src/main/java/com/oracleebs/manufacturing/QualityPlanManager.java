package com.oracleebs.manufacturing;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QualityPlanManager {
    public enum SpecificationLimit { LOWER, UPPER, NOMINAL }
    public enum CollectionResult { PASS, FAIL, WARNING }

    public static class QualityPlan {
        private final String planId;
        private final String itemCode;
        private final int sampleSize;
        private final Map<String, DoubleSpecification> specifications;
        private final List<QualityResult> results;

        public QualityPlan(String planId, String item, int sample) {
            this.planId = planId;
            this.itemCode = item;
            this.sampleSize = sample;
            this.specifications = new LinkedHashMap<>();
            this.results = new ArrayList<>();
        }

        public String getPlanId() { return planId; }
        public String getItemCode() { return itemCode; }
        public int getSampleSize() { return sampleSize; }
        public Map<String, DoubleSpecification> getSpecifications() { return Collections.unmodifiableMap(specifications); }
        public List<QualityResult> getResults() { return Collections.unmodifiableList(results); }

        public void addSpec(String name, double lower, double upper, double nominal) {
            specifications.put(name, new DoubleSpecification(name, lower, upper, nominal));
        }

        public void addResult(QualityResult r) { results.add(r); }
    }

    public record DoubleSpecification(String name, double lower, double upper, double nominal) {}
    public record QualityResult(String specName, double measuredValue, CollectionResult result, String note) {}

    private final Map<String, QualityPlan> plans;

    public QualityPlanManager() {
        this.plans = new ConcurrentHashMap<>();
    }

    public QualityPlan createPlan(String planId, String item, int sample) {
        QualityPlan plan = new QualityPlan(planId, item, sample);
        plans.put(planId, plan);
        return plan;
    }

    public CollectionResult measure(QualityPlan plan, String specName, double value) {
        var spec = plan.getSpecifications().get(specName);
        if (spec == null) throw new IllegalArgumentException("Spec not found: " + specName);
        CollectionResult result;
        if (value < spec.lower() || value > spec.upper()) {
            result = CollectionResult.FAIL;
        } else if (Math.abs(value - spec.nominal()) > (spec.upper() - spec.lower()) * 0.25) {
            result = CollectionResult.WARNING;
        } else {
            result = CollectionResult.PASS;
        }
        plan.addResult(new QualityResult(specName, value, result, "Measured: " + value));
        return result;
    }

    public QualityPlan getPlan(String planId) {
        return plans.get(planId);
    }

    public static QualityPlanManager createDefault() {
        QualityPlanManager mgr = new QualityPlanManager();
        var plan = mgr.createPlan("QPLAN001", "FIN001", 10);
        plan.addSpec("Length", 9.5, 10.5, 10.0);
        plan.addSpec("Width", 4.8, 5.2, 5.0);
        plan.addSpec("Weight", 49.0, 51.0, 50.0);
        return mgr;
    }
}
