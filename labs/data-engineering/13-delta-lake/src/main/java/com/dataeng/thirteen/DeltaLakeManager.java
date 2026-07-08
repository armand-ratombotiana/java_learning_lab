package com.dataeng.thirteen;

public class DeltaLakeManager {
    private final String basePath;

    public DeltaLakeManager(String basePath) { this.basePath = basePath; }

    public String getOptimizeSql(String tablePath) {
        return "OPTIMIZE delta.`" + tablePath + "`";
    }

    public String getVacuumSql(String tablePath, int retentionHours) {
        return "VACUUM delta.`" + tablePath + "` RETAIN " + retentionHours + " HOURS";
    }

    public String getMergeSql(String targetPath, String sourceTable, String joinKey) {
        return String.format(
            "MERGE INTO delta.`%s` t USING %s s ON t.%s = s.%s " +
            "WHEN MATCHED THEN UPDATE SET * " +
            "WHEN NOT MATCHED THEN INSERT *",
            targetPath, sourceTable, joinKey, joinKey);
    }

    public String getTimeTravelSql(String tablePath, int version) {
        return "SELECT * FROM delta.`" + tablePath + "` VERSION AS OF " + version;
    }

    public String getZOrderSql(String tablePath, String... columns) {
        return "OPTIMIZE delta.`" + tablePath + "` ZORDER BY (" + String.join(", ", columns) + ")";
    }

    public String getHistorySql(String tablePath) {
        return "DESCRIBE HISTORY delta.`" + tablePath + "`";
    }

    public String getChangeDataFeedSql(String tablePath, int startVersion) {
        return "SELECT * FROM table_changes('" + tablePath + "', " + startVersion + ")";
    }
}
