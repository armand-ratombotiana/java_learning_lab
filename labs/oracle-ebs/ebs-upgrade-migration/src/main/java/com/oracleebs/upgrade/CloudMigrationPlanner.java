package com.oracleebs.upgrade;

import java.util.*;

public class CloudMigrationPlanner {
    public enum MigrationStrategy { LIFT_AND_SHIFT, RECREATE, HYBRID }
    public enum ResourceType { COMPUTE, BLOCK_VOLUME, FILE_STORAGE, DATABASE }

    public static class CloudResource {
        private final String resourceName;
        private final ResourceType type;
        private final String shape;
        private final int count;
        private final int storageGB;

        public CloudResource(String name, ResourceType type, String shape, int count, int storage) {
            this.resourceName = name;
            this.type = type;
            this.shape = shape;
            this.count = count;
            this.storageGB = storage;
        }

        public String getResourceName() { return resourceName; }
        public ResourceType getType() { return type; }
        public String getShape() { return shape; }
        public int getCount() { return count; }
        public int getStorageGB() { return storageGB; }
    }

    public static class MigrationPlan {
        private final String planName;
        private final MigrationStrategy strategy;
        private final List<CloudResource> resources;
        private final String sourceVersion;
        private final String targetRegion;
        private long estimatedCostMonthly;
        private int estimatedDowntimeHours;

        public MigrationPlan(String name, MigrationStrategy strategy, String source, String region) {
            this.planName = name;
            this.strategy = strategy;
            this.sourceVersion = source;
            this.targetRegion = region;
            this.resources = new ArrayList<>();
        }

        public void addResource(CloudResource r) { resources.add(r); }
        public void setEstimatedCostMonthly(long cost) { estimatedCostMonthly = cost; }
        public void setEstimatedDowntimeHours(int hours) { estimatedDowntimeHours = hours; }
        public String getPlanName() { return planName; }
        public MigrationStrategy getStrategy() { return strategy; }
        public String getSourceVersion() { return sourceVersion; }
        public String getTargetRegion() { return targetRegion; }
        public List<CloudResource> getResources() { return Collections.unmodifiableList(resources); }
        public long getEstimatedCostMonthly() { return estimatedCostMonthly; }
        public int getEstimatedDowntimeHours() { return estimatedDowntimeHours; }
    }

    public MigrationPlan createPlan(String name, MigrationStrategy strategy, String source, String region) {
        MigrationPlan plan = new MigrationPlan(name, strategy, source, region);
        plan.addResource(new CloudResource("EBS Application Server", ResourceType.COMPUTE, "VM.Standard.E4.Flex", 2, 0));
        plan.addResource(new CloudResource("EBS Database Server", ResourceType.COMPUTE, "VM.Standard.E4.Flex", 1, 0));
        plan.addResource(new CloudResource("Application Storage", ResourceType.BLOCK_VOLUME, "Standard", 4, 500));
        plan.addResource(new CloudResource("Database Storage", ResourceType.BLOCK_VOLUME, "High Performance", 8, 2000));
        plan.addResource(new CloudResource("Shared File System", ResourceType.FILE_STORAGE, "NFS", 1, 1024));
        plan.setEstimatedCostMonthly(8500);
        plan.setEstimatedDowntimeHours(strategy == MigrationStrategy.LIFT_AND_SHIFT ? 24 : 48);
        return plan;
    }

    public List<String> getMigrationSteps(MigrationStrategy strategy) {
        return switch (strategy) {
            case LIFT_AND_SHIFT -> List.of(
                "Provision OCI infrastructure",
                "Copy EBS file system to OCI",
                "Import database to OCI",
                "Configure EBS on OCI",
                "Test and validate",
                "Cutover DNS"
            );
            case RECREATE -> List.of(
                "Provision OCI infrastructure",
                "Fresh install EBS on OCI",
                "Migrate data via interfaces",
                "Configure integrations",
                "User acceptance testing",
                "Go live"
            );
            case HYBRID -> List.of(
                "Assess on-premise components",
                "Migrate database to OCI",
                "Keep application tier on-premise",
                "Configure database link",
                "Test hybrid connectivity",
                "Gradual cutover"
            );
        };
    }
}
