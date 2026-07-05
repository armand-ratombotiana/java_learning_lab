package com.cloud.storage;

import java.util.*;

public class StorageClasses {

    public static class StorageClassInfo {
        public final StorageClassesEnum name;
        public final double costPerGb;
        public final double retrievalCostPerGb;
        public final long retrievalTimeMs;
        public final String description;

        public StorageClassInfo(StorageClassesEnum name, double costPerGb,
                                double retrievalCost, long retrievalTimeMs, String desc) {
            this.name = name;
            this.costPerGb = costPerGb;
            this.retrievalCostPerGb = retrievalCost;
            this.retrievalTimeMs = retrievalTimeMs;
            this.description = desc;
        }

        @Override
        public String toString() {
            return String.format("%-20s $%.4f/GB/mo | retrieve: $%.4f/GB | %s",
                name, costPerGb, retrievalCostPerGb, description);
        }
    }

    public enum StorageClassesEnum {
        STANDARD, STANDARD_IA, INTELLIGENT_TIERING, ONE_ZONE_IA, GLACIER, DEEP_ARCHIVE
    }

    public static class StorageClassSelector {
        private final Map<StorageClassesEnum, StorageClassInfo> classes = new LinkedHashMap<>();

        public StorageClassSelector() {
            classes.put(StorageClassesEnum.STANDARD,
                new StorageClassInfo(StorageClassesEnum.STANDARD, 0.023, 0.0, 0, "Frequent access, low latency"));
            classes.put(StorageClassesEnum.STANDARD_IA,
                new StorageClassInfo(StorageClassesEnum.STANDARD_IA, 0.0125, 0.01, 0, "Infrequent access"));
            classes.put(StorageClassesEnum.INTELLIGENT_TIERING,
                new StorageClassInfo(StorageClassesEnum.INTELLIGENT_TIERING, 0.023, 0.0, 0, "Auto-cost optimization"));
            classes.put(StorageClassesEnum.ONE_ZONE_IA,
                new StorageClassInfo(StorageClassesEnum.ONE_ZONE_IA, 0.01, 0.01, 0, "Single AZ, infrequent"));
            classes.put(StorageClassesEnum.GLACIER,
                new StorageClassInfo(StorageClassesEnum.GLACIER, 0.0036, 0.03, 300_000, "Archive, 1-5 min retrieval"));
            classes.put(StorageClassesEnum.DEEP_ARCHIVE,
                new StorageClassInfo(StorageClassesEnum.DEEP_ARCHIVE, 0.00099, 0.05, 720_000, "Long-term archive, 12h retr"));
        }

        public StorageClassInfo getInfo(StorageClassesEnum sc) { return classes.get(sc); }

        public StorageClassesEnum recommend(long objectSizeBytes, int accessFrequency,
                                             boolean durabilityRequired) {
            double sizeMb = objectSizeBytes / (1024.0 * 1024.0);

            if (accessFrequency > 30) return StorageClassesEnum.STANDARD;
            if (accessFrequency > 5) return StorageClassesEnum.STANDARD_IA;
            if (accessFrequency > 1) return StorageClassesEnum.INTELLIGENT_TIERING;
            if (!durabilityRequired) return StorageClassesEnum.ONE_ZONE_IA;
            if (sizeMb > 100) return StorageClassesEnum.GLACIER;
            return StorageClassesEnum.DEEP_ARCHIVE;
        }

        public void printAll() {
            System.out.println("=== S3 Storage Classes ===");
            classes.values().forEach(System.out::println);
        }
    }

    public static void main(String[] args) {
        StorageClassSelector selector = new StorageClassSelector();
        selector.printAll();

        System.out.println("\n=== Recommendations ===");
        System.out.println("Frequent access log: " + selector.recommend(1_000_000, 100, true));
        System.out.println("Monthly backup: " + selector.recommend(500_000_000, 1, true));
        System.out.println("Temp cache file: " + selector.recommend(50_000, 20, false));
        System.out.println("Regulatory archive: " + selector.recommend(2_000_000_000L, 0, true));
    }
}
