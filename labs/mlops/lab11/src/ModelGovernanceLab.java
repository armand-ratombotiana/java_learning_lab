package com.mlops.lab11;

import java.time.Instant;
import java.util.*;

/**
 * Model Governance & Compliance — Lab 11.
 * <p>
 * Demonstrates model governance concepts: model cards, bias detection,
 * audit trails, and compliance reporting. Implements fairness metrics
 * (demographic parity, equal opportunity, disparate impact).
 */
public class ModelGovernanceLab {

    /** Model Card — standard documentation for ML models. */
    static class ModelCard {
        final String modelName;
        final String version;
        final String modelType;
        final String framework;
        final Instant createdDate;
        final String intendedUse;
        final List<String> limitations;
        final String trainingDataDescription;
        final Map<String, Map<String, Double>> perGroupMetrics;
        final Map<String, Double> fairnessMetrics;

        ModelCard(String modelName, String version, String modelType,
                   String framework, String intendedUse) {
            this.modelName = modelName;
            this.version = version;
            this.modelType = modelType;
            this.framework = framework;
            this.createdDate = Instant.now();
            this.intendedUse = intendedUse;
            this.limitations = new ArrayList<>();
            this.trainingDataDescription = "";
            this.perGroupMetrics = new LinkedHashMap<>();
            this.fairnessMetrics = new LinkedHashMap<>();
        }

        void addLimitation(String limitation) { limitations.add(limitation); }

        void addGroupMetrics(String group, double accuracy, double precision, double recall, double f1) {
            Map<String, Double> m = new LinkedHashMap<>();
            m.put("accuracy", accuracy);
            m.put("precision", precision);
            m.put("recall", recall);
            m.put("f1", f1);
            m.put("sample_size", 1000.0);
            perGroupMetrics.put(group, m);
        }

        void print() {
            System.out.printf("""
                    Model Card: %s v%s
                    ================================
                    Type:      %s
                    Framework: %s
                    Created:   %s
                    Intended Use: %s
                    
                    Limitations:
                    """, modelName, version, modelType, framework,
                    createdDate.toString().substring(0, 19), intendedUse);
            limitations.forEach(l -> System.out.println("  - " + l));
            System.out.println("\nPer-Group Performance:");
            for (Map.Entry<String, Map<String, Double>> entry : perGroupMetrics.entrySet()) {
                System.out.printf("  %s: %s%n", entry.getKey(), entry.getValue());
            }
            if (!fairnessMetrics.isEmpty()) {
                System.out.println("\nFairness Metrics:");
                fairnessMetrics.forEach((k, v) -> System.out.printf("  %s: %.4f%n", k, v));
            }
        }
    }

    /** Bias detection using fairness metrics. */
    static class BiasDetector {

        /** Demographic parity: difference in positive prediction rates between groups. */
        static double demographicParityDifference(List<Boolean> predictionsGroupA,
                                                    List<Boolean> predictionsGroupB) {
            double rateA = positiveRate(predictionsGroupA);
            double rateB = positiveRate(predictionsGroupB);
            return Math.abs(rateA - rateB);
        }

        /** Equal opportunity: difference in true positive rates. */
        static double equalOpportunityDifference(List<Boolean> predictionsGroupA,
                                                   List<Boolean> actualGroupA,
                                                   List<Boolean> predictionsGroupB,
                                                   List<Boolean> actualGroupB) {
            double tprA = truePositiveRate(predictionsGroupA, actualGroupA);
            double tprB = truePositiveRate(predictionsGroupB, actualGroupB);
            return Math.abs(tprA - tprB);
        }

        /** Disparate impact ratio (80% rule). */
        static double disparateImpactRatio(List<Boolean> predictionsGroupA,
                                             List<Boolean> predictionsGroupB) {
            double rateA = positiveRate(predictionsGroupA);
            double rateB = positiveRate(predictionsGroupB);
            double min = Math.min(rateA, rateB);
            double max = Math.max(rateA, rateB);
            return max == 0 ? 1.0 : min / max;
        }

        private static double positiveRate(List<Boolean> predictions) {
            if (predictions.isEmpty()) return 0.0;
            return (double) predictions.stream().filter(p -> p).count() / predictions.size();
        }

        private static double truePositiveRate(List<Boolean> predictions, List<Boolean> actual) {
            long tp = 0, actualPositive = 0;
            for (int i = 0; i < predictions.size(); i++) {
                if (actual.get(i)) {
                    actualPositive++;
                    if (predictions.get(i)) tp++;
                }
            }
            return actualPositive == 0 ? 0.0 : (double) tp / actualPositive;
        }
    }

    /** Audit trail entry for model governance. */
    static class AuditEntry {
        final String action;
        final String modelName;
        final String version;
        final String user;
        final String details;
        final Instant timestamp;
        final String previousHash;
        String hash;

        AuditEntry(String action, String modelName, String version,
                    String user, String details, String previousHash) {
            this.action = action;
            this.modelName = modelName;
            this.version = version;
            this.user = user;
            this.details = details;
            this.timestamp = Instant.now();
            this.previousHash = previousHash;
            this.hash = computeHash();
        }

        private String computeHash() {
            String input = action + modelName + version + user + details + timestamp + previousHash;
            return Integer.toHexString(input.hashCode());
        }

        @Override
        public String toString() {
            return String.format("[%s] %s | model=%s v%s | user=%s | detail=%s | hash=%s",
                    timestamp.toString().substring(0, 19), action, modelName, version, user, details, hash);
        }
    }

    static class AuditTrail {
        private final List<AuditEntry> entries = new ArrayList<>();
        private String lastHash = "0";

        void log(String action, String modelName, String version, String user, String details) {
            AuditEntry entry = new AuditEntry(action, modelName, version, user, details, lastHash);
            entries.add(entry);
            lastHash = entry.hash;
        }

        void printAll() {
            System.out.println("Audit Trail:");
            entries.forEach(e -> System.out.println("  " + e));
        }

        boolean verifyIntegrity() {
            String previous = "0";
            for (AuditEntry entry : entries) {
                String expected = Integer.toHexString(
                        (entry.action + entry.modelName + entry.version + entry.user
                                + entry.details + entry.timestamp + previous).hashCode());
                if (!entry.hash.equals(expected)) return false;
                previous = entry.hash;
            }
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Model Governance & Compliance ===\n");

        // PART 1: Model Card
        ModelCard card = new ModelCard("loan_approval", "2.1.0",
                "Gradient Boosted Tree", "XGBoost",
                "Automated loan approval decisions for personal loans up to $50,000");
        card.addLimitation("Not validated for commercial loans");
        card.addLimitation("Training data skewed towards urban populations");
        card.addLimitation("Model performance degrades for applicants with thin credit files");
        card.addGroupMetrics("Overall", 0.923, 0.91, 0.89, 0.90);
        card.addGroupMetrics("Group A (Majority)", 0.935, 0.93, 0.91, 0.92);
        card.addGroupMetrics("Group B (Protected)", 0.885, 0.86, 0.84, 0.85);
        card.print();

        // PART 2: Bias Detection
        System.out.println("\n--- Bias Detection ---");
        Random rng = new Random(42);
        List<Boolean> predA = new ArrayList<>();
        List<Boolean> actualA = new ArrayList<>();
        List<Boolean> predB = new ArrayList<>();
        List<Boolean> actualB = new ArrayList<>();

        // Simulate predictions for two groups
        for (int i = 0; i < 1000; i++) {
            actualA.add(rng.nextDouble() < 0.3);
            predA.add(rng.nextDouble() < (actualA.get(i) ? 0.92 : 0.08));
            actualB.add(rng.nextDouble() < 0.3);
            predB.add(rng.nextDouble() < (actualB.get(i) ? 0.85 : 0.15));
        }

        double dpDiff = BiasDetector.demographicParityDifference(predA, predB);
        double eodDiff = BiasDetector.equalOpportunityDifference(predA, actualA, predB, actualB);
        double diRatio = BiasDetector.disparateImpactRatio(predA, predB);

        System.out.printf("Demographic Parity Difference: %.4f %s%n",
                dpDiff, dpDiff < 0.1 ? "✓" : "⚠ EXCEEDS THRESHOLD");
        System.out.printf("Equal Opportunity Difference:  %.4f %s%n",
                eodDiff, eodDiff < 0.1 ? "✓" : "⚠ EXCEEDS THRESHOLD");
        System.out.printf("Disparate Impact Ratio:        %.4f %s%n",
                diRatio, diRatio >= 0.8 ? "✓ (satisfies 80%% rule)" : "⚠ (violates 80%% rule)");

        // PART 3: Audit Trail
        System.out.println("\n--- Audit Trail ---");
        AuditTrail audit = new AuditTrail();
        audit.log("REGISTER", "loan_approval", "1.0.0", "ds_lead", "Initial model registration");
        audit.log("PROMOTE", "loan_approval", "1.0.0", "ml_eng", "Promoted to staging");
        audit.log("VALIDATE", "loan_approval", "1.0.0", "qa_eng", "Validation passed");
        audit.log("PROMOTE", "loan_approval", "1.0.0", "ml_eng", "Promoted to production");
        audit.log("REGISTER", "loan_approval", "2.0.0", "ds_lead", "Updated training data");
        audit.log("PROMOTE", "loan_approval", "2.1.0", "ml_eng", "Bias mitigation applied");
        audit.printAll();
        System.out.printf("Audit trail integrity: %s%n", audit.verifyIntegrity() ? "INTACT ✓" : "TAMPERED ✗");
    }
}
