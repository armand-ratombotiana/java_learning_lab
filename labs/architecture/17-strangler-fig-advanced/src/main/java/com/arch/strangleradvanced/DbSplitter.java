package com.arch.strangleradvanced;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DbSplitter {
    public enum DbType { LEGACY, NEW }
    public enum MigrationState { NOT_STARTED, MIGRATING, DUAL_WRITE, VERIFIED, DECOMMISSIONED }

    private final Map<String, DbType> routingTable = new ConcurrentHashMap<>();
    private final Map<String, MigrationState> migrationStates = new ConcurrentHashMap<>();
    private final Map<String, Consumer<Map<String, Object>>> legacyWrites = new ConcurrentHashMap<>();
    private final Map<String, Consumer<Map<String, Object>>> newWrites = new ConcurrentHashMap<>();
    private final List<DataRecord> pendingSync = new CopyOnWriteArrayList<>();

    public void registerTable(String tableName, Consumer<Map<String, Object>> legacyWrite, Consumer<Map<String, Object>> newWrite) {
        legacyWrites.put(tableName, legacyWrite);
        newWrites.put(tableName, newWrite);
        routingTable.put(tableName, DbType.LEGACY);
        migrationStates.put(tableName, MigrationState.NOT_STARTED);
    }

    public void write(String tableName, Map<String, Object> data) {
        DbType target = routingTable.getOrDefault(tableName, DbType.LEGACY);
        MigrationState state = migrationStates.get(tableName);
        if (state == MigrationState.DUAL_WRITE || state == MigrationState.MIGRATING) {
            legacyWrites.get(tableName).accept(data);
            newWrites.get(tableName).accept(data);
            pendingSync.add(new DataRecord(tableName, data, System.currentTimeMillis()));
        } else if (target == DbType.NEW) {
            newWrites.get(tableName).accept(data);
        } else {
            legacyWrites.get(tableName).accept(data);
        }
    }

    public void startMigration(String tableName) {
        migrationStates.put(tableName, MigrationState.MIGRATING);
        routingTable.put(tableName, DbType.NEW);
    }

    public void enableDualWrite(String tableName) {
        migrationStates.put(tableName, MigrationState.DUAL_WRITE);
    }

    public void verifyAndComplete(String tableName) {
        migrationStates.put(tableName, MigrationState.VERIFIED);
    }

    public void decommissionLegacy(String tableName) {
        migrationStates.put(tableName, MigrationState.DECOMMISSIONED);
        routingTable.put(tableName, DbType.NEW);
    }

    public MigrationState getState(String tableName) { return migrationStates.get(tableName); }
    public List<DataRecord> getPendingSync() { return List.copyOf(pendingSync); }
}

record DataRecord(String table, Map<String, Object> data, long timestamp) {}
