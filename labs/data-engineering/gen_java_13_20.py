#!/usr/bin/env python3
"""Add Java source files and tests to labs 13-20."""
import os, pathlib

BASE = r"C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering"

def write(path, content):
    pathlib.Path(os.path.dirname(path)).mkdir(parents=True, exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content.strip() + "\n")
    print(f"  {os.path.basename(path)}")

# ===== LAB 13: DELTA LAKE =====
pkg = "com.dataeng.thirteen"
sp = os.path.join(BASE, "13-delta-lake", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "13-delta-lake", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "DeltaLakeManager.java"), """package com.dataeng.thirteen;

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
}""")

write(os.path.join(sp, "DeltaOptimizer.java"), """package com.dataeng.thirteen;

import java.util.*;

public class DeltaOptimizer {
    private final long targetFileSize; // bytes

    public DeltaOptimizer(long targetFileSize) { this.targetFileSize = targetFileSize; }

    public int calculateRequiredFiles(long totalDataSize) {
        return (int) Math.max(1, Math.ceil((double) totalDataSize / targetFileSize));
    }

    public double estimateCompactionRatio(int currentFiles, long totalDataSize) {
        int optimalFiles = calculateRequiredFiles(totalDataSize);
        return (double) currentFiles / Math.max(1, optimalFiles);
    }

    public String[] recommendZOrderColumns(Map<String, Integer> columnCardinality, Set<String> frequentFilterColumns) {
        return frequentFilterColumns.stream()
            .filter(col -> columnCardinality.getOrDefault(col, 0) > 1000)
            .sorted((a, b) -> columnCardinality.getOrDefault(b, 0) - columnCardinality.getOrDefault(a, 0))
            .limit(3)
            .toArray(String[]::new);
    }

    public int calculateOptimalPartitions(long totalSize, long avgRecordSize) {
        long totalRecords = totalSize / Math.max(1, avgRecordSize);
        int partitions = (int) (totalRecords / 100_000);
        return Math.max(1, Math.min(partitions, 1024));
    }

    public String generateOptimizePlan(String tablePath, long totalSize, int fileCount) {
        double ratio = estimateCompactionRatio(fileCount, totalSize);
        if (ratio < 1.5) return "No optimization needed (ratio: " + String.format("%.1f", ratio) + ")";
        return "Optimize " + tablePath + ": " + fileCount + " files -> "
            + calculateRequiredFiles(totalSize) + " files (ratio: " + String.format("%.1f", ratio) + "x)";
    }
}""")

write(os.path.join(tp, "DeltaLakeManagerTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DeltaLakeManagerTest {
    private final DeltaLakeManager dm = new DeltaLakeManager("/data/lake");

    @Test
    void testOptimizeSql() {
        String sql = dm.getOptimizeSql("/data/lake/events");
        assertTrue(sql.contains("OPTIMIZE"));
        assertTrue(sql.contains("events"));
    }

    @Test
    void testVacuumSql() {
        String sql = dm.getVacuumSql("/data/lake/events", 168);
        assertTrue(sql.contains("VACUUM"));
        assertTrue(sql.contains("168"));
    }

    @Test
    void testMergeSql() {
        String sql = dm.getMergeSql("/data/lake/events", "updates", "id");
        assertTrue(sql.contains("MERGE"));
        assertTrue(sql.contains("MATCHED"));
        assertTrue(sql.contains("id"));
    }

    @Test
    void testTimeTravelSql() {
        String sql = dm.getTimeTravelSql("/data/lake/events", 25);
        assertTrue(sql.contains("VERSION AS OF 25"));
    }

    @Test
    void testZOrderSql() {
        String sql = dm.getZOrderSql("/data/lake/events", "customer_id", "event_date");
        assertTrue(sql.contains("ZORDER BY"));
        assertTrue(sql.contains("customer_id"));
        assertTrue(sql.contains("event_date"));
    }

    @Test
    void testChangeDataFeedSql() {
        String sql = dm.getChangeDataFeedSql("/data/lake/events", 0);
        assertTrue(sql.contains("table_changes"));
    }
}""")

write(os.path.join(tp, "DeltaOptimizerTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class DeltaOptimizerTest {
    private final DeltaOptimizer opt = new DeltaOptimizer(256 * 1024 * 1024);

    @Test
    void testRequiredFiles() {
        assertEquals(4, opt.calculateRequiredFiles(1024L * 1024 * 1024));
    }

    @Test
    void testCompactionRatio() {
        double ratio = opt.estimateCompactionRatio(100, 1024L * 1024 * 1024);
        assertTrue(ratio > 20);
    }

    @Test
    void testZOrderRecommendation() {
        var cardinality = Map.of("id", 1_000_000, "status", 5, "date", 365);
        var filters = Set.of("id", "date", "status");
        String[] cols = opt.recommendZOrderColumns(cardinality, filters);
        assertTrue(cols.length > 0);
        assertEquals("id", cols[0]);
    }

    @Test
    void testOptimizePlan() {
        String plan = opt.generateOptimizePlan("/data/table", 1024L * 1024 * 1024, 1000);
        assertTrue(plan.contains("Optimize"));
        assertTrue(plan.contains("ratio"));
    }
}""")

# ===== LAB 14: ICEBERG =====
pkg = "com.dataeng.fourteen"
sp = os.path.join(BASE, "14-apache-iceberg", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "14-apache-iceberg", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "IcebergTableManager.java"), """package com.dataeng.fourteen;

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
}""")

write(os.path.join(sp, "IcebergMaintenance.java"), """package com.dataeng.fourteen;

public class IcebergMaintenance {
    private final IcebergTableManager tableManager;

    public IcebergMaintenance(IcebergTableManager tableManager) {
        this.tableManager = tableManager;
    }

    public String buildMaintenancePlan(String tableName, long totalSize, int fileCount) {
        StringBuilder plan = new StringBuilder();
        plan.append("Maintenance plan for ").append(tableName).append(":\\n");

        if (fileCount > 100 || totalSize / Math.max(1, fileCount) < 64 * 1024 * 1024) {
            plan.append("- Run compaction: ").append(tableManager.getCompactionSql(tableName)).append("\\n");
        }

        plan.append("- Expire old snapshots: ").append(tableManager.getExpireSnapshotsSql(tableName, "2024-01-01")).append("\\n");
        plan.append("- Rewrite manifests: ").append(tableManager.getRewriteManifestsSql(tableName)).append("\\n");
        plan.append("- Remove orphan files: ").append(tableManager.getRemoveOrphanFilesSql(tableName)).append("\\n");

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
}""")

write(os.path.join(tp, "IcebergTableManagerTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class IcebergTableManagerTest {
    private final IcebergTableManager mgr = new IcebergTableManager("iceberg", "analytics");

    @Test
    void testCreateTableSql() {
        String sql = mgr.getCreateTableSql("events", "id BIGINT, ts TIMESTAMP", "days(ts)");
        assertTrue(sql.contains("CREATE TABLE"));
        assertTrue(sql.contains("iceberg.analytics.events"));
        assertTrue(sql.contains("PARTITIONED BY"));
    }

    @Test
    void testPartitionEvolutionSql() {
        String sql = mgr.getPartitionEvolutionSql("events", "months(ts)");
        assertTrue(sql.contains("SET PARTITION SPEC"));
    }

    @Test
    void testCompactionSql() {
        String sql = mgr.getCompactionSql("events");
        assertTrue(sql.contains("rewrite_data_files"));
    }

    @Test
    void testExpireSnapshotsSql() {
        String sql = mgr.getExpireSnapshotsSql("events", "2024-01-01");
        assertTrue(sql.contains("expire_snapshots"));
    }
}""")

write(os.path.join(tp, "IcebergMaintenanceTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class IcebergMaintenanceTest {
    private final IcebergTableManager mgr = new IcebergTableManager("iceberg", "analytics");
    private final IcebergMaintenance maint = new IcebergMaintenance(mgr);

    @Test
    void testNeedsCompaction() {
        assertTrue(maint.needsCompaction(100, 1024L * 1024 * 1024));
        assertFalse(maint.needsCompaction(10, 1024L * 1024 * 1024 * 10));
    }

    @Test
    void testOptimalFiles() {
        assertEquals(4, maint.estimateOptimalFiles(1024L * 1024 * 1024, 256 * 1024 * 1024));
    }

    @Test
    void testBuildPlan() {
        String plan = maint.buildMaintenancePlan("events", 1024L * 1024 * 1024, 500);
        assertTrue(plan.contains("compaction"));
        assertTrue(plan.contains("expire_snapshots"));
    }
}""")

# ===== LAB 15: DATA QUALITY =====
pkg = "com.dataeng.fifteen"
sp = os.path.join(BASE, "15-data-quality", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "15-data-quality", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "QualityCheckEngine.java"), """package com.dataeng.fifteen;

import java.util.*;
import java.util.stream.*;

public class QualityCheckEngine {
    private final List<QualityRule> rules = new ArrayList<>();
    private final AlertService alertService;

    public QualityCheckEngine(AlertService alertService) { this.alertService = alertService; }

    public void addRule(QualityRule rule) { rules.add(rule); }

    public QualityReport execute(String datasetName, List<Map<String, Object>> data) {
        QualityReport report = new QualityReport(datasetName, System.currentTimeMillis());
        for (QualityRule rule : rules) {
            RuleResult result = rule.evaluate(data);
            report.addResult(result);
            if (!result.passed() && rule.severity() >= 8) {
                alertService.sendAlert(datasetName, result);
            }
        }
        return report;
    }

    public record QualityReport(String datasetName, long timestamp, List<RuleResult> results) {
        public QualityReport(String datasetName, long timestamp) {
            this(datasetName, timestamp, new ArrayList<>());
        }
        public void addResult(RuleResult r) { results.add(r); }
        public double overallScore() {
            return results.stream().mapToDouble(RuleResult::score).average().orElse(0);
        }
        public boolean allPassed() { return results.stream().allMatch(RuleResult::passed); }
    }

    public record RuleResult(String ruleName, boolean passed, double score, String details) {}
    public record QualityRule(String name, int severity, java.util.function.Function<List<Map<String, Object>>, RuleResult> check) {
        public RuleResult evaluate(List<Map<String, Object>> data) { return check.apply(data); }
    }

    public interface AlertService {
        void sendAlert(String dataset, RuleResult result);
    }

    public static QualityRule notNullRule(String columnName, int severity) {
        return new QualityRule("not_null_" + columnName, severity, data -> {
            long total = data.size();
            long nulls = data.stream().filter(row -> row.get(columnName) == null).count();
            double nullRate = total > 0 ? (double) nulls / total : 0;
            return new RuleResult("not_null_" + columnName, nullRate < 0.05, (1 - nullRate) * 100,
                nulls + "/" + total + " nulls in " + columnName);
        });
    }

    public static QualityRule uniqueRule(String columnName, int severity) {
        return new QualityRule("unique_" + columnName, severity, data -> {
            long total = data.size();
            long distinct = data.stream().map(row -> row.get(columnName)).distinct().count();
            double uniqueRate = total > 0 ? (double) distinct / total : 0;
            return new RuleResult("unique_" + columnName, uniqueRate > 0.99, uniqueRate * 100,
                distinct + "/" + total + " distinct in " + columnName);
        });
    }

    public static QualityRule rangeRule(String columnName, double min, double max, int severity) {
        return new QualityRule("range_" + columnName, severity, data -> {
            long violations = data.stream()
                .map(row -> row.get(columnName))
                .filter(v -> v instanceof Number)
                .map(v -> ((Number) v).doubleValue())
                .filter(v -> v < min || v > max)
                .count();
            long total = data.size();
            double passRate = total > 0 ? 1 - (double) violations / total : 0;
            return new RuleResult("range_" + columnName, violations == 0, passRate * 100,
                violations + " values outside [" + min + ", " + max + "]");
        });
    }
}""")

write(os.path.join(sp, "SchemaValidator.java"), """package com.dataeng.fifteen;

import java.util.*;

public class SchemaValidator {
    public record SchemaDiff(List<String> newColumns, List<String> missingColumns,
                              List<String> typeChanges, boolean hasBreakingChanges) {}

    public record ColumnDef(String name, String type, boolean nullable) {}

    public SchemaDiff compare(List<ColumnDef> expected, List<ColumnDef> actual) {
        Map<String, ColumnDef> expectedMap = new HashMap<>();
        Map<String, ColumnDef> actualMap = new HashMap<>();
        for (ColumnDef c : expected) expectedMap.put(c.name(), c);
        for (ColumnDef c : actual) actualMap.put(c.name(), c);

        List<String> newCols = new ArrayList<>();
        List<String> missingCols = new ArrayList<>();
        List<String> typeChanges = new ArrayList<>();

        for (String name : actualMap.keySet()) {
            if (!expectedMap.containsKey(name)) {
                newCols.add(name);
            }
        }

        for (String name : expectedMap.keySet()) {
            if (!actualMap.containsKey(name)) {
                missingCols.add(name);
            } else if (!expectedMap.get(name).type().equals(actualMap.get(name).type())) {
                typeChanges.add(name + ": " + expectedMap.get(name).type() + " -> " + actualMap.get(name).type());
            }
        }

        boolean breaking = !missingCols.isEmpty() || !typeChanges.isEmpty();
        return new SchemaDiff(newCols, missingCols, typeChanges, breaking);
    }

    public String classifyChange(SchemaDiff diff) {
        if (diff.hasBreakingChanges()) return "BREAKING";
        if (!diff.newColumns().isEmpty()) return "EVOLUTION";
        return "NO_CHANGE";
    }
}""")

write(os.path.join(tp, "QualityCheckEngineTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class QualityCheckEngineTest {
    private final QualityCheckEngine engine = new QualityCheckEngine((ds, r) -> {});

    @Test
    void testNotNullRule() {
        var rule = QualityCheckEngine.notNullRule("id", 5);
        var data = List.of(Map.of("id", 1), Map.of("id", 2), Map.of("id", null));
        var result = rule.evaluate(data);
        assertFalse(result.passed());
    }

    @Test
    void testUniqueRule() {
        var rule = QualityCheckEngine.uniqueRule("id", 5);
        var data = List.of(Map.of("id", 1), Map.of("id", 2), Map.of("id", 2));
        var result = rule.evaluate(data);
        assertFalse(result.passed());
    }

    @Test
    void testRangeRule() {
        var rule = QualityCheckEngine.rangeRule("age", 0, 120, 5);
        var data = List.of(Map.of("age", 25), Map.of("age", 30), Map.of("age", 200));
        var result = rule.evaluate(data);
        assertFalse(result.passed());
    }

    @Test
    void testAllPass() {
        var rule = QualityCheckEngine.notNullRule("name", 5);
        var data = List.of(Map.of("name", "Alice"), Map.of("name", "Bob"));
        var result = rule.evaluate(data);
        assertTrue(result.passed());
    }

    @Test
    void testReportScore() {
        engine.addRule(QualityCheckEngine.notNullRule("name", 5));
        var data = List.of(Map.of("name", "Alice"), Map.of("name", "Bob"));
        var report = engine.execute("test", data);
        assertTrue(report.allPassed());
        assertEquals(100.0, report.overallScore());
    }
}""")

write(os.path.join(tp, "SchemaValidatorTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class SchemaValidatorTest {
    private final SchemaValidator validator = new SchemaValidator();

    @Test
    void testIdenticalSchemas() {
        var expected = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var actual = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var diff = validator.compare(expected, actual);
        assertFalse(diff.hasBreakingChanges());
        assertTrue(diff.newColumns().isEmpty());
        assertTrue(diff.missingColumns().isEmpty());
    }

    @Test
    void testNewColumn() {
        var expected = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var actual = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false),
            new SchemaValidator.ColumnDef("name", "STRING", true));
        var diff = validator.compare(expected, actual);
        assertFalse(diff.hasBreakingChanges());
        assertEquals(1, diff.newColumns().size());
    }

    @Test
    void testMissingColumn() {
        var expected = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false),
            new SchemaValidator.ColumnDef("name", "STRING", true));
        var actual = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var diff = validator.compare(expected, actual);
        assertTrue(diff.hasBreakingChanges());
        assertEquals(1, diff.missingColumns().size());
    }

    @Test
    void testTypeChange() {
        var expected = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var actual = List.of(new SchemaValidator.ColumnDef("id", "STRING", false));
        var diff = validator.compare(expected, actual);
        assertTrue(diff.hasBreakingChanges());
        assertEquals(1, diff.typeChanges().size());
    }

    @Test
    void testClassification() {
        var expected = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var actual = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false),
            new SchemaValidator.ColumnDef("name", "STRING", true));
        var diff = validator.compare(expected, actual);
        assertEquals("EVOLUTION", validator.classifyChange(diff));
    }
}""")

# ===== LAB 16: DATA CATALOG =====
pkg = "com.dataeng.sixteen"
sp = os.path.join(BASE, "16-data-catalog", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "16-data-catalog", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "MetadataIngestionClient.java"), """package com.dataeng.sixteen;

import java.net.http.*;
import java.net.URI;
import java.util.*;

public class MetadataIngestionClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String catalogUrl;
    private final String apiKey;

    public MetadataIngestionClient(String catalogUrl, String apiKey) {
        this.catalogUrl = catalogUrl;
        this.apiKey = apiKey;
    }

    public record ColumnMetadata(String name, String type, String description) {}
    public record DatasetMetadata(String database, String schema, String table,
                                   List<ColumnMetadata> columns, String description, String source) {}

    public boolean ingestDataset(DatasetMetadata metadata) {
        try {
            String json = toJson(metadata);
            var request = HttpRequest.newBuilder()
                .uri(URI.create(catalogUrl + "/api/v1/datasets"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            System.err.println("Ingestion failed: " + e.getMessage());
            return false;
        }
    }

    public boolean submitLineage(String json) {
        try {
            var request = HttpRequest.newBuilder()
                .uri(URI.create(catalogUrl + "/api/v1/lineage"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("Lineage submission failed: " + e.getMessage());
            return false;
        }
    }

    private String toJson(DatasetMetadata m) {
        return String.format(
            "{\"database\":\"%s\",\"schema\":\"%s\",\"table\":\"%s\",\"columns\":%s,\"description\":\"%s\",\"source\":\"%s\"}",
            m.database(), m.schema(), m.table(), columnsToJson(m.columns()), m.description(), m.source());
    }

    private String columnsToJson(List<ColumnMetadata> cols) {
        var sb = new StringBuilder("[");
        for (int i = 0; i < cols.size(); i++) {
            var c = cols.get(i);
            sb.append(String.format("{\"name\":\"%s\",\"type\":\"%s\",\"description\":\"%s\"}", c.name(), c.type(), c.description()));
            if (i < cols.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}""")

write(os.path.join(sp, "LineageBuilder.java"), """package com.dataeng.sixteen;

import java.util.*;

public class LineageBuilder {
    private final List<String> sources = new ArrayList<>();
    private final List<String> transformations = new ArrayList<>();
    private final List<String> targets = new ArrayList<>();

    public LineageBuilder addSource(String source) { sources.add(source); return this; }
    public LineageBuilder addTransformation(String transformation) { transformations.add(transformation); return this; }
    public LineageBuilder addTarget(String target) { targets.add(target); return this; }

    public String build() {
        var sb = new StringBuilder();
        sb.append("{\"sources\":[");
        sb.append(String.join(",", sources.stream().map(s -> "\\"" + s + "\\"").toList()));
        sb.append("],\"transformations\":[");
        sb.append(String.join(",", transformations.stream().map(t -> "\\"" + t + "\\"").toList()));
        sb.append("],\"targets\":[");
        sb.append(String.join(",", targets.stream().map(t -> "\\"" + t + "\\"").toList()));
        sb.append("]}");
        return sb.toString();
    }

    public boolean isValid() {
        return !sources.isEmpty() && !targets.isEmpty();
    }
}""")

write(os.path.join(tp, "MetadataIngestionClientTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class MetadataIngestionClientTest {
    @Test
    void testDatasetMetadataCreation() {
        var cols = List.of(
            new MetadataIngestionClient.ColumnMetadata("id", "BIGINT", "Primary key"),
            new MetadataIngestionClient.ColumnMetadata("name", "STRING", "Customer name"));
        var meta = new MetadataIngestionClient.DatasetMetadata("db", "public", "customers", cols, "Customer table", "ETL");
        assertEquals("db", meta.database());
        assertEquals("customers", meta.table());
        assertEquals(2, meta.columns().size());
    }

    @Test
    void testClientCreation() {
        var client = new MetadataIngestionClient("http://localhost:8585", "test-key");
        assertNotNull(client);
    }
}""")

write(os.path.join(tp, "LineageBuilderTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class LineageBuilderTest {
    @Test
    void testBuildLineage() {
        var lb = new LineageBuilder()
            .addSource("orders_db.public.orders")
            .addTransformation("dbt_etl_job")
            .addTarget("analytics.fact_orders");
        assertTrue(lb.isValid());
        String json = lb.build();
        assertTrue(json.contains("orders_db.public.orders"));
        assertTrue(json.contains("analytics.fact_orders"));
    }

    @Test
    void testInvalidLineage() {
        var lb = new LineageBuilder();
        assertFalse(lb.isValid());
    }

    @Test
    void testMultipleSources() {
        var lb = new LineageBuilder()
            .addSource("db1.orders")
            .addSource("db2.customers")
            .addTransformation("etl_join")
            .addTarget("analytics.orders_with_customers");
        String json = lb.build();
        assertTrue(json.contains("db1.orders"));
        assertTrue(json.contains("db2.customers"));
    }
}""")

# ===== LAB 17: FEATURE STORE =====
pkg = "com.dataeng.seventeen"
sp = os.path.join(BASE, "17-real-time-feature-store", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "17-real-time-feature-store", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "FeatureClient.java"), """package com.dataeng.seventeen;

import java.net.http.*;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

public class FeatureClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String feastUrl;
    private final Map<String, CachedValue> cache = new ConcurrentHashMap<>();

    public FeatureClient(String feastUrl) { this.feastUrl = feastUrl; }

    public record FeatureValue(String name, Object value, long timestamp) {}
    public record CachedValue(FeatureValue value, long expiry) {}

    public Map<String, FeatureValue> getOnlineFeatures(String featureView, Map<String, String> entities, List<String> features) {
        Map<String, FeatureValue> result = new HashMap<>();
        List<String> uncached = new ArrayList<>();

        for (String feature : features) {
            String cacheKey = featureView + ":" + entities + ":" + feature;
            CachedValue cached = cache.get(cacheKey);
            if (cached != null && cached.expiry() > System.currentTimeMillis()) {
                result.put(feature, cached.value());
            } else {
                uncached.add(feature);
            }
        }

        if (!uncached.isEmpty()) {
            try {
                String json = buildRequestJson(featureView, entities, uncached);
                var request = HttpRequest.newBuilder()
                    .uri(URI.create(feastUrl + "/get-online-features"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
                var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    // Parse response and update cache (simplified)
                    for (String f : uncached) {
                        FeatureValue fv = new FeatureValue(f, response.body(), System.currentTimeMillis());
                        cache.put(featureView + ":" + entities + ":" + f, new CachedValue(fv, System.currentTimeMillis() + 5000));
                        result.put(f, fv);
                    }
                }
            } catch (Exception e) {
                System.err.println("Feature serving failed: " + e.getMessage());
            }
        }

        return result;
    }

    private String buildRequestJson(String featureView, Map<String, String> entities, List<String> features) {
        var sb = new StringBuilder();
        sb.append("{\"feature_service\":\"").append(featureView).append("\",");
        sb.append("\"entities\":{");
        var entityParts = entities.entrySet().stream()
            .map(e -> "\\"" + e.getKey() + "\\":\\"" + e.getValue() + "\\"")
            .toList();
        sb.append(String.join(",", entityParts));
        sb.append("},\"features\":[");
        sb.append(String.join(",", features.stream().map(f -> "\\"" + f + "\\"").toList()));
        sb.append("]}");
        return sb.toString();
    }

    public void clearCache() { cache.clear(); }
    public int cacheSize() { return cache.size(); }
}""")

write(os.path.join(sp, "PointInTimeJoin.java"), """package com.dataeng.seventeen;

import java.util.*;
import java.util.stream.*;

public class PointInTimeJoin {

    public record Label(String entityKey, long timestamp, Object value) {}
    public record Feature(String entityKey, long timestamp, String name, Object value) {}

    public static List<Map<String, Object>> execute(List<Label> labels, List<Feature> features) {
        Map<String, List<Feature>> featuresByEntity = features.stream()
            .collect(Collectors.groupingBy(Feature::entityKey));

        return labels.stream().map(label -> {
            List<Feature> entityFeatures = featuresByEntity.getOrDefault(label.entityKey(), List.of());
            Map<String, Object> result = new HashMap<>();
            result.put("entity_key", label.entityKey());
            result.put("label_timestamp", label.timestamp());
            result.put("label_value", label.value());

            Map<String, List<Feature>> featuresByName = entityFeatures.stream()
                .filter(f -> f.timestamp() <= label.timestamp())
                .collect(Collectors.groupingBy(Feature::name));

            for (var entry : featuresByName.entrySet()) {
                var latest = entry.getValue().stream()
                    .max(Comparator.comparingLong(Feature::timestamp));
                latest.ifPresent(f -> result.put(entry.getKey(), f.value()));
            }

            return result;
        }).collect(Collectors.toList());
    }

    public static void validateNoLeakage(List<Label> labels, List<Feature> features) {
        for (Label label : labels) {
            for (Feature feature : features) {
                if (feature.entityKey().equals(label.entityKey())
                    && feature.timestamp() > label.timestamp()) {
                    throw new IllegalStateException(
                        "Data leakage detected: feature " + feature.name()
                        + " for entity " + feature.entityKey()
                        + " has timestamp " + feature.timestamp()
                        + " > label timestamp " + label.timestamp());
                }
            }
        }
    }
}""")

write(os.path.join(tp, "FeatureClientTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FeatureClientTest {
    @Test
    void testClientCreation() {
        var client = new FeatureClient("http://localhost:6566");
        assertNotNull(client);
    }

    @Test
    void testCacheEmptyInitially() {
        var client = new FeatureClient("http://localhost:6566");
        assertEquals(0, client.cacheSize());
    }

    @Test
    void testClearCache() {
        var client = new FeatureClient("http://localhost:6566");
        client.clearCache();
        assertEquals(0, client.cacheSize());
    }
}""")

write(os.path.join(tp, "PointInTimeJoinTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class PointInTimeJoinTest {
    @Test
    void testSimpleJoin() {
        var labels = List.of(
            new PointInTimeJoin.Label("user1", 1000L, "click"));
        var features = List.of(
            new PointInTimeJoin.Feature("user1", 500L, "total_orders", 10));
        var result = PointInTimeJoin.execute(labels, features);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).get("total_orders"));
    }

    @Test
    void testFutureFeatureExcluded() {
        var labels = List.of(
            new PointInTimeJoin.Label("user1", 1000L, "click"));
        var features = List.of(
            new PointInTimeJoin.Feature("user1", 1500L, "total_orders", 20));
        var result = PointInTimeJoin.execute(labels, features);
        assertNull(result.get(0).get("total_orders"));
    }

    @Test
    void testLatestFeatureUsed() {
        var labels = List.of(
            new PointInTimeJoin.Label("user1", 1000L, "click"));
        var features = List.of(
            new PointInTimeJoin.Feature("user1", 300L, "total_orders", 5),
            new PointInTimeJoin.Feature("user1", 800L, "total_orders", 10));
        var result = PointInTimeJoin.execute(labels, features);
        assertEquals(10, result.get(0).get("total_orders"));
    }

    @Test
    void testLeakageDetection() {
        var labels = List.of(
            new PointInTimeJoin.Label("user1", 1000L, "click"));
        var features = List.of(
            new PointInTimeJoin.Feature("user1", 1500L, "future_feat", 99));
        assertThrows(IllegalStateException.class,
            () -> PointInTimeJoin.validateNoLeakage(labels, features));
    }
}""")

# ===== LAB 18: DATA OBSERVABILITY =====
pkg = "com.dataeng.eighteen"
sp = os.path.join(BASE, "18-data-observability", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "18-data-observability", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "AnomalyDetector.java"), """package com.dataeng.eighteen;

import java.util.*;

public class AnomalyDetector {

    public record VolumeAnomaly(String metric, double currentValue, double expectedValue,
                                  double deviation, boolean isAnomaly, String severity) {}

    public record FreshnessAnomaly(String table, long currentLagMs, long expectedLagMs,
                                    boolean isAnomaly, String severity) {}

    public VolumeAnomaly detectVolumeAnomaly(double current, List<Double> history) {
        double mean = history.stream().mapToDouble(v -> v).average().orElse(current);
        double stddev = Math.sqrt(history.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0));
        double zScore = stddev > 0 ? Math.abs(current - mean) / stddev : 0;
        double deviation = mean > 0 ? (current - mean) / mean : 0;

        String severity;
        boolean isAnomaly;
        if (zScore > 4) { isAnomaly = true; severity = "CRITICAL"; }
        else if (zScore > 3) { isAnomaly = true; severity = "HIGH"; }
        else if (zScore > 2) { isAnomaly = true; severity = "WARNING"; }
        else { isAnomaly = false; severity = "OK"; }

        return new VolumeAnomaly("row_count", current, mean, deviation, isAnomaly, severity);
    }

    public FreshnessAnomaly detectFreshnessAnomaly(long currentLagMs, List<Long> history, long slaMs) {
        double avgLag = history.stream().mapToLong(v -> v).average().orElse(currentLagMs);
        boolean isAnomaly = currentLagMs > slaMs && currentLagMs > avgLag * 2;
        String severity = isAnomaly ? (currentLagMs > slaMs * 3 ? "CRITICAL" : "HIGH") : "OK";
        return new FreshnessAnomaly("table", currentLagMs, (long) avgLag, isAnomaly, severity);
    }

    public Map<String, Object> detectDistributionDrift(double[] currentHistogram, double[] expectedHistogram) {
        double ks = computeKsStatistic(currentHistogram, expectedHistogram);
        var result = new HashMap<String, Object>();
        result.put("ks_statistic", ks);
        result.put("drift_detected", ks > 0.1);
        result.put("severity", ks > 0.2 ? "HIGH" : ks > 0.1 ? "WARNING" : "OK");
        return result;
    }

    private double computeKsStatistic(double[] a, double[] b) {
        double[] cdfA = new double[a.length];
        double[] cdfB = new double[b.length];
        cdfA[0] = a[0]; cdfB[0] = b[0];
        for (int i = 1; i < a.length; i++) {
            cdfA[i] = cdfA[i-1] + a[i];
            cdfB[i] = cdfB[i-1] + b[i];
        }
        double maxDiff = 0;
        for (int i = 0; i < a.length; i++) {
            maxDiff = Math.max(maxDiff, Math.abs(cdfA[i] - cdfB[i]));
        }
        return maxDiff;
    }
}""")

write(os.path.join(sp, "DataProfiler.java"), """package com.dataeng.eighteen;

import java.util.*;
import java.util.stream.*;

public class DataProfiler {

    public record ProfileResult(String columnName, long rowCount, long nullCount,
                                 long distinctCount, double min, double max, double mean,
                                 double stddev, Map<String, Long> topValues) {
        public double nullRate() { return rowCount > 0 ? (double) nullCount / rowCount : 0; }
        public double distinctRate() { return rowCount > 0 ? (double) distinctCount / rowCount : 0; }
    }

    public ProfileResult profileNumericColumn(List<Number> values, String columnName) {
        long rowCount = values.size();
        long nullCount = values.stream().filter(Objects::isNull).count();
        List<Double> nonNull = values.stream().filter(Objects::nonNull).map(Number::doubleValue).toList();
        long distinctCount = nonNull.stream().distinct().count();
        double min = nonNull.stream().mapToDouble(v -> v).min().orElse(0);
        double max = nonNull.stream().mapToDouble(v -> v).max().orElse(0);
        double mean = nonNull.stream().mapToDouble(v -> v).average().orElse(0);
        double variance = nonNull.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0);
        double stddev = Math.sqrt(variance);

        Map<String, Long> topValues = nonNull.stream()
            .limit(1000)
            .collect(Collectors.groupingBy(Object::toString, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b) -> a, LinkedHashMap::new));

        return new ProfileResult(columnName, rowCount, nullCount, distinctCount, min, max, mean, stddev, topValues);
    }

    public ProfileResult profileStringColumn(List<String> values, String columnName) {
        long rowCount = values.size();
        long nullCount = values.stream().filter(Objects::isNull).count();
        long distinctCount = values.stream().filter(Objects::nonNull).distinct().count();

        Map<String, Long> topValues = values.stream().filter(Objects::nonNull)
            .collect(Collectors.groupingBy(v -> v, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b) -> a, LinkedHashMap::new));

        return new ProfileResult(columnName, rowCount, nullCount, distinctCount, 0, 0, 0, 0, topValues);
    }
}""")

write(os.path.join(tp, "AnomalyDetectorTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class AnomalyDetectorTest {
    private final AnomalyDetector detector = new AnomalyDetector();

    @Test
    void testNormalVolume() {
        var history = List.of(100.0, 102.0, 98.0, 101.0, 99.0);
        var result = detector.detectVolumeAnomaly(100, history);
        assertFalse(result.isAnomaly());
    }

    @Test
    void testSpikeVolume() {
        var history = List.of(100.0, 102.0, 98.0, 101.0, 99.0);
        var result = detector.detectVolumeAnomaly(500, history);
        assertTrue(result.isAnomaly());
        assertEquals("CRITICAL", result.severity());
    }

    @Test
    void testFreshnessNormal() {
        var history = List.of(1000L, 1200L, 1100L);
        var result = detector.detectFreshnessAnomaly(1500, history, 5000);
        assertFalse(result.isAnomaly());
    }

    @Test
    void testFreshnessBreach() {
        var history = List.of(1000L, 1200L, 1100L);
        var result = detector.detectFreshnessAnomaly(30000, history, 5000);
        assertTrue(result.isAnomaly());
    }

    @Test
    void testDistributionDrift() {
        var current = new double[]{0.5, 0.3, 0.2};
        var expected = new double[]{0.5, 0.3, 0.2};
        var result = detector.detectDistributionDrift(current, expected);
        assertFalse((boolean) result.get("drift_detected"));
    }

    @Test
    void testDistributionDriftDetected() {
        var current = new double[]{0.9, 0.05, 0.05};
        var expected = new double[]{0.3, 0.3, 0.4};
        var result = detector.detectDistributionDrift(current, expected);
        assertTrue((boolean) result.get("drift_detected"));
    }
}""")

write(os.path.join(tp, "DataProfilerTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class DataProfilerTest {
    private final DataProfiler profiler = new DataProfiler();

    @Test
    void testNumericProfile() {
        var values = List.of(1, 2, 3, 4, 5);
        var result = profiler.profileNumericColumn(values, "age");
        assertEquals(5, result.rowCount());
        assertEquals(3.0, result.mean());
        assertEquals(1.0, result.min());
        assertEquals(5.0, result.max());
    }

    @Test
    void testNullHandling() {
        var values = Arrays.asList(1, null, 3, null, 5);
        var result = profiler.profileNumericColumn(values, "age");
        assertEquals(5, result.rowCount());
        assertEquals(2, result.nullCount());
        assertEquals(0.4, result.nullRate());
    }

    @Test
    void testStringProfile() {
        var values = List.of("a", "b", "a", "c", "b", "a");
        var result = profiler.profileStringColumn(values, "category");
        assertEquals(3, result.distinctCount());
        assertTrue(result.topValues().containsKey("a"));
        assertTrue(result.topValues().get("a") >= 3);
    }
}""")

# ===== LAB 19: DATA GOVERNANCE =====
pkg = "com.dataeng.nineteen"
sp = os.path.join(BASE, "19-data-governance", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "19-data-governance", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "PiiDetector.java"), """package com.dataeng.nineteen;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class PiiDetector {
    private static final Map<String, Pattern> PATTERNS = new LinkedHashMap<>();
    static {
        PATTERNS.put("EMAIL", Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"));
        PATTERNS.put("SSN", Pattern.compile("^\\\\d{3}-\\\\d{2}-\\\\d{4}$"));
        PATTERNS.put("PHONE", Pattern.compile("^\\\\+?1?[-.\\\\s]?\\\\(?\\\\d{3}\\\\)?[-.\\\\s]?\\\\d{3}[-.\\\\s]?\\\\d{4}$"));
        PATTERNS.put("CREDIT_CARD", Pattern.compile("^\\\\d{4}[- ]?\\\\d{4}[- ]?\\\\d{4}[- ]?\\\\d{4}$"));
        PATTERNS.put("ZIP_CODE", Pattern.compile("^\\\\d{5}(-\\\\d{4})?$"));
        PATTERNS.put("IP_ADDRESS", Pattern.compile("^\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}$"));
    }

    private static final Set<String> COLUMN_NAME_PATTERNS = Set.of(
        "email", "e-mail", "ssn", "social", "phone", "telephone", "mobile",
        "credit_card", "cc_number", "card_number", "password", "secret",
        "passport", "driver_license", "bank_account", "routing_number");

    public record PiiResult(String piiType, double confidence, String matchedPattern) {}

    public PiiResult analyzeColumn(String columnName, List<String> sampleValues) {
        String colLower = columnName.toLowerCase().replaceAll("[^a-z0-9]", "");

        if (COLUMN_NAME_PATTERNS.contains(colLower)) {
            double valueConfidence = 0.8;
            for (var entry : PATTERNS.entrySet()) {
                if (entry.getKey().equals("EMAIL") && colLower.contains("email")) valueConfidence = 0.95;
                if (entry.getKey().equals("SSN") && colLower.contains("ssn")) valueConfidence = 0.95;
            }
            return new PiiResult(detectTypeFromName(colLower), valueConfidence, "column_name");
        }

        if (sampleValues == null || sampleValues.isEmpty()) {
            return new PiiResult("UNKNOWN", 0, "no_data");
        }

        Map<String, Long> matches = new HashMap<>();
        for (String value : sampleValues) {
            if (value == null) continue;
            for (var entry : PATTERNS.entrySet()) {
                if (entry.getValue().matcher(value).matches()) {
                    matches.merge(entry.getKey(), 1L, Long::sum);
                }
            }
        }

        if (matches.isEmpty()) return new PiiResult("UNKNOWN", 0, "no_match");

        String bestType = matches.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey).orElse("UNKNOWN");

        double matchRate = (double) matches.get(bestType) / Math.max(1, sampleValues.size());
        return new PiiResult(bestType, matchRate, "value_pattern");
    }

    private String detectTypeFromName(String name) {
        if (name.contains("email")) return "EMAIL";
        if (name.contains("ssn")) return "SSN";
        if (name.contains("phone") || name.contains("mobile") || name.contains("telephone")) return "PHONE";
        if (name.contains("credit") || name.contains("card_number") || name.contains("cc_")) return "CREDIT_CARD";
        if (name.contains("password") || name.contains("secret")) return "PASSWORD";
        if (name.contains("passport")) return "PASSPORT";
        if (name.contains("bank") || name.contains("routing")) return "BANK_ACCOUNT";
        return "PII";
    }
}""")

write(os.path.join(sp, "DataMasker.java"), """package com.dataeng.nineteen;

import java.util.*;

public class DataMasker {
    public enum MaskingType { EMAIL, SSN, PHONE, CREDIT_CARD, GENERAL, PASSTHROUGH }

    public String mask(String value, MaskingType type) {
        if (value == null || value.isEmpty()) return value;
        return switch (type) {
            case EMAIL -> maskEmail(value);
            case SSN -> maskSSN(value);
            case PHONE -> maskPhone(value);
            case CREDIT_CARD -> maskCreditCard(value);
            case GENERAL -> maskGeneral(value);
            case PASSTHROUGH -> value;
        };
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex < 2) return email;
        return email.charAt(0) + "***" + email.substring(atIndex - 1);
    }

    private String maskSSN(String ssn) {
        if (ssn.length() < 4) return "***-**-****";
        return "***-**-" + ssn.substring(Math.max(0, ssn.length() - 4));
    }

    private String maskPhone(String phone) {
        String cleaned = phone.replaceAll("[^\\\\d]", "");
        if (cleaned.length() < 4) return phone;
        String last4 = cleaned.substring(cleaned.length() - 4);
        return "***-***-" + last4;
    }

    private String maskCreditCard(String cc) {
        String cleaned = cc.replaceAll("[^\\\\d]", "");
        if (cleaned.length() < 4) return cc;
        String last4 = cleaned.substring(cleaned.length() - 4);
        return "****-****-****-" + last4;
    }

    private String maskGeneral(String value) {
        if (value.length() <= 4) return "****";
        return value.substring(0, 1) + "***" + value.substring(value.length() - 1);
    }

    public String maskByRole(String value, String role, MaskingType type) {
        return switch (role) {
            case "admin" -> value;
            case "analyst" -> type == MaskingType.PASSTHROUGH ? value : mask(value, type);
            case "viewer" -> mask(value, type);
            default -> mask(value, MaskingType.GENERAL);
        };
    }
}""")

write(os.path.join(tp, "PiiDetectorTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class PiiDetectorTest {
    private final PiiDetector detector = new PiiDetector();

    @Test
    void testDetectEmail() {
        var result = detector.analyzeColumn("email", List.of("user@example.com", "test@test.org"));
        assertNotEquals("UNKNOWN", result.piiType());
        assertTrue(result.confidence() > 0.5);
    }

    @Test
    void testDetectSSN() {
        var result = detector.analyzeColumn("ssn", List.of("123-45-6789", "987-65-4321"));
        assertEquals("SSN", result.piiType());
    }

    @Test
    void testDetectPhone() {
        var result = detector.analyzeColumn("phone", List.of("+1-555-123-4567", "555-123-4567"));
        assertEquals("PHONE", result.piiType());
    }

    @Test
    void testColumnNameDetection() {
        var result = detector.analyzeColumn("user_email_address", List.of());
        assertEquals("EMAIL", result.piiType());
    }

    @Test
    void testNoPii() {
        var result = detector.analyzeColumn("product_name", List.of("Widget", "Gadget"));
        assertEquals("UNKNOWN", result.piiType());
    }
}""")

write(os.path.join(tp, "DataMaskerTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DataMaskerTest {
    private final DataMasker masker = new DataMasker();

    @Test
    void testMaskEmail() {
        String masked = masker.mask("john.doe@example.com", DataMasker.MaskingType.EMAIL);
        assertEquals("j***.doe@example.com", masked);
    }

    @Test
    void testMaskSSN() {
        String masked = masker.mask("123-45-6789", DataMasker.MaskingType.SSN);
        assertTrue(masked.contains("6789"));
        assertFalse(masked.contains("123"));
    }

    @Test
    void testMaskPhone() {
        String masked = masker.mask("555-123-4567", DataMasker.MaskingType.PHONE);
        assertEquals("***-***-4567", masked);
    }

    @Test
    void testMaskCreditCard() {
        String masked = masker.mask("1234-5678-9012-3456", DataMasker.MaskingType.CREDIT_CARD);
        assertEquals("****-****-****-3456", masked);
    }

    @Test
    void testMaskNull() {
        assertNull(masker.mask(null, DataMasker.MaskingType.GENERAL));
    }

    @Test
    void testMaskByRole() {
        String value = "user@example.com";
        assertEquals(value, masker.maskByRole(value, "admin", DataMasker.MaskingType.EMAIL));
        assertNotEquals(value, masker.maskByRole(value, "viewer", DataMasker.MaskingType.EMAIL));
    }
}""")

# ===== LAB 20: STREAMING ANALYTICS =====
pkg = "com.dataeng.twenty"
sp = os.path.join(BASE, "20-streaming-analytics", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "20-streaming-analytics", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "StreamingAnalyticsJob.java"), '''package com.dataeng.twenty;

public class StreamingAnalyticsJob {
    private final String jobName;
    private final String sourceTopic;
    private final String sinkTable;
    private final String query;

    public StreamingAnalyticsJob(String jobName, String sourceTopic, String sinkTable, String query) {
        this.jobName = jobName;
        this.sourceTopic = sourceTopic;
        this.sinkTable = sinkTable;
        this.query = query;
    }

    public String getFlinkSqlStatement() {
        return String.format(
            "CREATE TABLE source (event_id BIGINT, event_type STRING, " +
            "value DOUBLE, event_ts TIMESTAMP(3), " +
            "WATERMARK FOR event_ts AS event_ts - INTERVAL '5' SECOND) WITH (" +
            "'connector' = 'kafka', 'topic' = '%s', " +
            "'properties.bootstrap.servers' = 'localhost:9092', 'format' = 'json'); " +
            "CREATE TABLE sink (window_start TIMESTAMP(3), metric STRING, " +
            "count_value BIGINT, avg_value DOUBLE, " +
            "PRIMARY KEY (window_start, metric) NOT ENFORCED) WITH (" +
            "'connector' = 'jdbc', 'url' = 'jdbc:postgresql://localhost:5432/analytics', " +
            "'table-name' = '%s'); INSERT INTO sink %s;",
            sourceTopic, sinkTable, query);
    }

    public String getTumblingWindowQuery(String windowSize) {
        return String.format(
            "SELECT TUMBLE_START(event_ts, INTERVAL '%s') AS window_start, " +
            "event_type AS metric, COUNT(*) AS count_value, AVG(value) AS avg_value " +
            "FROM source GROUP BY TUMBLE(event_ts, INTERVAL '%s'), event_type",
            windowSize, windowSize);
    }

    public String getSlidingWindowQuery(String windowSize, String slideSize) {
        return String.format(
            "SELECT HOP_START(event_ts, INTERVAL '%s', INTERVAL '%s') AS window_start, " +
            "event_type AS metric, COUNT(*) AS count_value, AVG(value) AS avg_value " +
            "FROM source GROUP BY HOP(event_ts, INTERVAL '%s', INTERVAL '%s'), event_type",
            slideSize, windowSize, slideSize, windowSize);
    }

    public String getSessionWindowQuery(String gapSize) {
        return String.format(
            "SELECT SESSION_START(event_ts, INTERVAL '%s') AS window_start, " +
            "event_type AS metric, COUNT(*) AS count_value, AVG(value) AS avg_value " +
            "FROM source GROUP BY SESSION(event_ts, INTERVAL '%s'), event_type",
            gapSize, gapSize);
    }

    public String getJobName() { return jobName; }
}''')

write(os.path.join(sp, "DashboardDataService.java"), """package com.dataeng.twenty;

import java.net.http.*;
import java.net.URI;
import java.util.*;

public class DashboardDataService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String databaseUrl;

    public DashboardDataService(String databaseUrl) { this.databaseUrl = databaseUrl; }

    public record MetricSnapshot(String windowStart, String metric, long count, double avg) {}

    public List<MetricSnapshot> getLatestMetrics(int limit) {
        try {
            var request = HttpRequest.newBuilder()
                .uri(URI.create(databaseUrl + "/api/metrics?limit=" + limit))
                .GET()
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parseMetrics(response.body());
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch metrics: " + e.getMessage());
        }
        return List.of();
    }

    private List<MetricSnapshot> parseMetrics(String json) {
        // Simplified JSON parsing for demonstration
        List<MetricSnapshot> metrics = new ArrayList<>();
        if (json != null && !json.isEmpty()) {
            metrics.add(new MetricSnapshot("now", "revenue", 100, 250.0));
            metrics.add(new MetricSnapshot("now", "users", 50, 1.5));
        }
        return metrics;
    }

    public boolean checkLatency(long maxAllowedLagMs) {
        try {
            var request = HttpRequest.newBuilder()
                .uri(URI.create(databaseUrl + "/api/health"))
                .GET()
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}""")

write(os.path.join(tp, "StreamingAnalyticsJobTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class StreamingAnalyticsJobTest {
    @Test
    void testJobCreation() {
        var job = new StreamingAnalyticsJob("revenue", "orders", "revenue_metrics", "");
        assertEquals("revenue", job.getJobName());
    }

    @Test
    void testTumblingWindowQuery() {
        var job = new StreamingAnalyticsJob("test", "events", "sink", "");
        String query = job.getTumblingWindowQuery("1 MINUTE");
        assertTrue(query.contains("TUMBLE_START"));
        assertTrue(query.contains("1 MINUTE"));
        assertTrue(query.contains("GROUP BY"));
    }

    @Test
    void testSlidingWindowQuery() {
        var job = new StreamingAnalyticsJob("test", "events", "sink", "");
        String query = job.getSlidingWindowQuery("10 MINUTE", "5 MINUTE");
        assertTrue(query.contains("HOP_START"));
        assertTrue(query.contains("10 MINUTE"));
    }

    @Test
    void testSessionWindowQuery() {
        var job = new StreamingAnalyticsJob("test", "events", "sink", "");
        String query = job.getSessionWindowQuery("30 MINUTE");
        assertTrue(query.contains("SESSION_START"));
    }

    @Test
    void testFlinkSqlStatement() {
        var job = new StreamingAnalyticsJob("test", "input-topic", "output_table",
            "SELECT COUNT(*) FROM source");
        String sql = job.getFlinkSqlStatement();
        assertTrue(sql.contains("input-topic"));
        assertTrue(sql.contains("output_table"));
        assertTrue(sql.contains("WATERMARK"));
    }
}""")

write(os.path.join(tp, "DashboardDataServiceTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DashboardDataServiceTest {
    @Test
    void testServiceCreation() {
        var svc = new DashboardDataService("http://localhost:8080");
        assertNotNull(svc);
    }

    @Test
    void testMetricSnapshot() {
        var snapshot = new DashboardDataService.MetricSnapshot("2024-01-01", "revenue", 100, 250.0);
        assertEquals("revenue", snapshot.metric());
        assertEquals(100, snapshot.count());
        assertEquals(250.0, snapshot.avg());
    }

    @Test
    void testHealthCheck() {
        var svc = new DashboardDataService("http://localhost:8080");
        boolean healthy = svc.checkLatency(5000);
        // Will be false since no server running, but shouldn't throw
        assertFalse(healthy);
    }
}""")

print("Java sources and tests added for labs 13-20!")
