package com.dataeng.fourteen;

public class IcebergMaintenance {
    private final IcebergTableManager tableManager;

    public IcebergMaintenance(IcebergTableManager tableManager) {
        this.tableManager = tableManager;
    }

    public String buildMaintenancePlan(String tableName, long totalSize, int fileCount) {
        StringBuilder plan = new StringBuilder();
        plan.append("Maintenance plan for ").append(tableName).append(":\n");

        if (fileCount > 100 || totalSize / Math.max(1, fileCount) < 64 * 1024 * 1024) {
            plan.append("- Run compaction: ").append(tableManager.getCompactionSql(tableName)).append("\n");
        }

        plan.append("- Expire old snapshots: ").append(tableManager.getExpireSnapshotsSql(tableName, "2024-01-01")).append("\n");
        plan.append("- Rewrite manifests: ").append(tableManager.getRewriteManifestsSql(tableName)).append("\n");
        plan.append("- Remove orphan files: ").append(tableManager.getRemoveOrphanFilesSql(tableName)).append("\n");

        return plan.toString();
    }

    public boolean needsCompaction(int fileCount, long totalSize) {
        if (fileCount < 10) return false;
        long avgFileSize = totalSize / Math.max(1, fileCount);
        return avgFileSize < 64 * 1024 * 1024 || fileCount > 1000;
    }

    public int estimateOptimalFiles(long totalSize, long targetFileSize) {
        return (int) Math.max(1, Math.ceil((double) totalSize / targetFileSize));
    }
}
