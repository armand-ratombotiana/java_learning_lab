package com.learning.cdc;

import java.util.*;

public class CDCSolution {

    public static class CDCConfig {
        private String connectorClass;
        private String databaseHost;
        private int databasePort;
        private String databaseName;
        private String tableName;
        private Map<String, String> properties;

        public CDCConfig(String connectorClass) {
            this.connectorClass = connectorClass;
            this.properties = new HashMap<>();
        }

        public CDCConfig database(String host, int port, String name) {
            this.databaseHost = host;
            this.databasePort = port;
            this.databaseName = name;
            return this;
        }

        public CDCConfig table(String table) {
            this.tableName = table;
            return this;
        }

        public CDCConfig property(String key, String value) {
            properties.put(key, value);
            return this;
        }

        public String getConnectorClass() { return connectorClass; }
        public String getDatabaseHost() { return databaseHost; }
        public int getDatabasePort() { return databasePort; }
        public String getDatabaseName() { return databaseName; }
        public String getTableName() { return tableName; }
        public Map<String, String> getProperties() { return properties; }
    }

    public CDCConfig createConfig(String connectorClass) {
        return new CDCConfig(connectorClass);
    }

    public CDCConfig mysqlConfig() {
        return createConfig("io.debezium.connector.mysql.MySqlConnector")
            .property("database.hostname", "localhost")
            .property("database.port", "3306");
    }

    public CDCConfig postgresConfig() {
        return createConfig("io.debezium.connector.postgresql.PostgresConnector")
            .property("database.hostname", "localhost")
            .property("database.port", "5432");
    }

    public CDCConfig mongodbConfig() {
        return createConfig("io.debezium.connector.mongodb.MongoDbConnector")
            .property("mongodb.hosts", "localhost:27017");
    }

    public static class CDCEvent {
        private String operation;
        private String table;
        private Map<String, Object> before;
        private Map<String, Object> after;
        private long timestamp;

        public CDCEvent(String operation, String table) {
            this.operation = operation;
            this.table = table;
            this.before = new HashMap<>();
            this.after = new HashMap<>();
        }

        public void setBefore(Map<String, Object> before) {
            this.before = before;
        }

        public void setAfter(Map<String, Object> after) {
            this.after = after;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getOperation() { return operation; }
        public String getTable() { return table; }
        public Map<String, Object> getBefore() { return before; }
        public Map<String, Object> getAfter() { return after; }
        public long getTimestamp() { return timestamp; }
    }

    public CDCEvent createEvent(String operation, String table) {
        return new CDCEvent(operation, table);
    }

    public CDCEvent createInsertEvent(String table, Map<String, Object> data) {
        CDCEvent event = createEvent("insert", table);
        event.setAfter(data);
        event.setTimestamp(System.currentTimeMillis());
        return event;
    }

    public CDCEvent createUpdateEvent(String table, Map<String, Object> before, Map<String, Object> after) {
        CDCEvent event = createEvent("update", table);
        event.setBefore(before);
        event.setAfter(after);
        event.setTimestamp(System.currentTimeMillis());
        return event;
    }

    public CDCEvent createDeleteEvent(String table, Map<String, Object> data) {
        CDCEvent event = createEvent("delete", table);
        event.setBefore(data);
        event.setTimestamp(System.currentTimeMillis());
        return event;
    }

    public static class CDCProcessor {
        private List<Consumer<CDCEvent>> handlers;

        public CDCProcessor() {
            this.handlers = new ArrayList<>();
        }

        public CDCProcessor onInsert(Consumer<CDCEvent> handler) {
            handlers.add(event -> {
                if ("insert".equals(event.getOperation())) {
                    handler.accept(event);
                }
            });
            return this;
        }

        public CDCProcessor onUpdate(Consumer<CDCEvent> handler) {
            handlers.add(event -> {
                if ("update".equals(event.getOperation())) {
                    handler.accept(event);
                }
            });
            return this;
        }

        public CDCProcessor onDelete(Consumer<CDCEvent> handler) {
            handlers.add(event -> {
                if ("delete".equals(event.getOperation())) {
                    handler.accept(event);
                }
            });
            return this;
        }

        public void process(CDCEvent event) {
            for (Consumer<CDCEvent> handler : handlers) {
                handler.accept(event);
            }
        }
    }

    public CDCProcessor createProcessor() {
        return new CDCProcessor();
    }

    public static class DebeziumConnector {
        private CDCConfig config;

        public DebeziumConnector(CDCConfig config) {
            this.config = config;
        }

        public void start() {}
        public void stop() {}
        public String getStatus() { return "running"; }
    }

    public DebeziumConnector createConnector(CDCConfig config) {
        return new DebeziumConnector(config);
    }

    public static class SnapshotMode {
        private String mode;

        public SnapshotMode(String mode) {
            this.mode = mode;
        }

        public String getMode() { return mode; }
    }

    public SnapshotMode initial() {
        return new SnapshotMode("initial");
    }

    public SnapshotMode schemaOnly() {
        return new SnapshotMode("schema_only");
    }

    public SnapshotMode always() {
        return new SnapshotMode("always");
    }

    public SnapshotMode never() {
        return new SnapshotMode("never");
    }

    public CDCConfig withSnapshotMode(CDCConfig config, SnapshotMode mode) {
        config.property("snapshot.mode", mode.getMode());
        return config;
    }
}