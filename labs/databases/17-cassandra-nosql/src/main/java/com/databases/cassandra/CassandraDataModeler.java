package com.databases.cassandra;

import java.util.*;

public class CassandraDataModeler {
    private String keyspace;
    private final Map<String, TableDefinition> tables = new LinkedHashMap<>();

    public record ColumnDef(String name, String type, boolean isPartitionKey, boolean isClusteringKey, boolean isStatic) {}
    public record TableDefinition(String name, List<ColumnDef> columns, String comment) {}

    public CassandraDataModeler withKeyspace(String ks) { this.keyspace = ks; return this; }

    public TableDefinition addTable(String name, String comment, ColumnDef... cols) {
        var def = new TableDefinition(name, List.of(cols), comment);
        tables.put(name, def);
        return def;
    }

    public String generateCreateTable(String tableName) {
        var def = tables.get(tableName);
        if (def == null) throw new IllegalArgumentException("Table not found: " + tableName);
        var sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(keyspace).append(".").append(tableName).append(" (\n");

        for (int i = 0; i < def.columns().size(); i++) {
            var col = def.columns().get(i);
            sb.append("  ").append(col.name()).append(" ").append(col.type());
            if (col.isStatic()) sb.append(" static");
            if (i < def.columns().size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("  PRIMARY KEY (");
        var pkCols = def.columns().stream().filter(ColumnDef::isPartitionKey).toList();
        var ckCols = def.columns().stream().filter(ColumnDef::isClusteringKey).toList();

        if (pkCols.size() == 1) {
            sb.append(pkCols.get(0).name());
        } else {
            sb.append("(");
            for (int i = 0; i < pkCols.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(pkCols.get(i).name());
            }
            sb.append(")");
        }

        for (var ck : ckCols) sb.append(", ").append(ck.name());

        sb.append(")\n");
        if (def.comment() != null && !def.comment().isEmpty()) {
            sb.append("WITH comment = '").append(def.comment()).append("';\n");
        } else {
            sb.append(");\n");
        }
        return sb.toString();
    }

    public String generateCreateKeyspace(String keyspace, String strategy, int replicationFactor) {
        return "CREATE KEYSPACE IF NOT EXISTS " + keyspace
            + " WITH replication = {'class': '" + strategy + "', 'replication_factor': " + replicationFactor + "};\n";
    }

    public List<String> getTables() { return new ArrayList<>(tables.keySet()); }
    public TableDefinition getTable(String name) { return tables.get(name); }

    public static CassandraDataModeler createUserModel() {
        var model = new CassandraDataModeler().withKeyspace("myapp");
        model.addTable("users", "User profiles",
            new ColumnDef("user_id", "uuid", true, false, false),
            new ColumnDef("email", "text", false, false, false),
            new ColumnDef("name", "text", false, false, false),
            new ColumnDef("created_at", "timestamp", false, false, false));
        model.addTable("user_events", "User activity events",
            new ColumnDef("user_id", "uuid", true, false, false),
            new ColumnDef("event_time", "timestamp", false, true, false),
            new ColumnDef("event_type", "text", false, false, false),
            new ColumnDef("data", "text", false, false, false));
        return model;
    }
}
