package com.dataeng.fourteen;

import java.util.*;

public class IcebergTableManager {
    private final String catalogName;
    private final String database;

    public IcebergTableManager(String catalogName, String database) {
        this.catalogName = catalogName;
        this.database = database;
    }

    public String getCreateTableSql(String tableName, String columns, String partitionSpec) {
        return String.format(
            "CREATE TABLE %s.%s.%s (%s) USING iceberg %s",
            catalogName, database, tableName, columns,
            partitionSpec.isEmpty() ? "" : "PARTITIONED BY (" + partitionSpec + ")");
    }

    public String getPartitionEvolutionSql(String tableName, String newPartitionSpec) {
        return String.format(
            "ALTER TABLE %s.%s.%s SET PARTITION SPEC (%s)",
            catalogName, database, tableName, newPartitionSpec);
    }

    public String getCompactionSql(String tableName) {
        return String.format("CALL %s.system.rewrite_data_files(table => '%s.%s')",
            catalogName, database, tableName);
    }

    public String getExpireSnapshotsSql(String tableName, String olderThan) {
        return String.format("CALL %s.system.expire_snapshots(table => '%s.%s', older_than => '%s')",
            catalogName, database, tableName, olderThan);
    }

    public String getSnapshotSql(String tableName) {
        return String.format("SELECT snapshot_id, committed_at FROM %s.%s.%s.snapshots",
            catalogName, database, tableName);
    }

    public String getRewriteManifestsSql(String tableName) {
        return String.format("CALL %s.system.rewrite_manifests(table => '%s.%s')",
            catalogName, database, tableName);
    }

    public String getRemoveOrphanFilesSql(String tableName) {
        return String.format("CALL %s.system.remove_orphan_files(table => '%s.%s')",
            catalogName, database, tableName);
    }
}
