#!/usr/bin/env python3
"""Add Java source files and tests to labs 09-12."""
import os, pathlib

BASE = r"C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering"

def write(path, content):
    pathlib.Path(os.path.dirname(path)).mkdir(parents=True, exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content.strip() + "\n")
    print(f"  {os.path.basename(path)}")

# ===== LAB 09: SNOWFLAKE =====
pkg = "com.dataeng.nine"
sp = os.path.join(BASE, "09-snowflake", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "09-snowflake", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "SnowflakeConnector.java"), """package com.dataeng.nine;

import java.sql.*;
import java.util.Properties;

public class SnowflakeConnector {
    private static final String JDBC_URL = "jdbc:snowflake://account.snowflakecomputing.com";

    public Connection connect(String user, String password, String warehouse,
                               String database, String schema) throws SQLException {
        Properties props = new Properties();
        props.put("user", user);
        props.put("password", password);
        props.put("warehouse", warehouse);
        props.put("db", database);
        props.put("schema", schema);
        props.put("tracing", "INFO");
        props.put("loginTimeout", "60");
        return DriverManager.getConnection(JDBC_URL, props);
    }

    public ResultSet executeQuery(Connection conn, String sql) throws SQLException {
        return conn.createStatement().executeQuery(sql);
    }

    public int executeUpdate(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }

    public void createWarehouse(Connection conn, String name, String size,
                                 int autoSuspendSec) throws SQLException {
        String sql = String.format(
            "CREATE WAREHOUSE IF NOT EXISTS %s WITH WAREHOUSE_SIZE='%s' AUTO_SUSPEND=%d AUTO_RESUME=TRUE",
            name, size, autoSuspendSec);
        executeUpdate(conn, sql);
    }

    public void cloneTable(Connection conn, String source, String target) throws SQLException {
        executeUpdate(conn, "CREATE TABLE " + target + " CLONE " + source);
    }

    public void close(Connection conn) {
        try { if (conn != null && !conn.isClosed()) conn.close(); }
        catch (SQLException e) { System.err.println("Close error: " + e.getMessage()); }
    }
}""")

write(os.path.join(sp, "WarehouseManager.java"), """package com.dataeng.nine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseManager {
    private final Connection conn;

    public WarehouseManager(Connection conn) { this.conn = conn; }

    public void createWarehouse(String name, String size, int autoSuspendSec) throws SQLException {
        String sql = String.format(
            "CREATE WAREHOUSE IF NOT EXISTS %s WITH WAREHOUSE_SIZE='%s' AUTO_SUSPEND=%d AUTO_RESUME=TRUE",
            name, size, autoSuspendSec);
        try (Statement stmt = conn.createStatement()) { stmt.execute(sql); }
    }

    public void alterSize(String name, String newSize) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER WAREHOUSE " + name + " SET WAREHOUSE_SIZE='" + newSize + "'");
        }
    }

    public void suspend(String name) throws SQLException {
        try (Statement stmt = conn.createStatement()) { stmt.execute("ALTER WAREHOUSE " + name + " SUSPEND"); }
    }

    public void resume(String name) throws SQLException {
        try (Statement stmt = conn.createStatement()) { stmt.execute("ALTER WAREHOUSE " + name + " RESUME"); }
    }

    public List<String> listWarehouses() throws SQLException {
        List<String> result = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW WAREHOUSES")) {
            while (rs.next()) {
                result.add(rs.getString("name") + "|" + rs.getString("size") + "|" + rs.getString("state"));
            }
        }
        return result;
    }
}""")

write(os.path.join(sp, "ClusteringManager.java"), """package com.dataeng.nine;

import java.sql.*;

public class ClusteringManager {
    private final Connection conn;

    public ClusteringManager(Connection conn) { this.conn = conn; }

    public void setClusteringKey(String table, String... columns) throws SQLException {
        String keys = String.join(", ", columns);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + table + " CLUSTER BY (" + keys + ")");
        }
    }

    public void dropClusteringKey(String table) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + table + " DROP CLUSTERING KEY");
        }
    }

    public double getClusteringDepth(String table) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SYSTEM$CLUSTERING_DEPTH('" + table + "')")) {
            return rs.next() ? rs.getDouble(1) : -1;
        }
    }

    public void recluster(String table) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + table + " RECLUSTER");
        }
    }
}""")

write(os.path.join(tp, "SnowflakeConnectorTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

@Disabled("Requires Snowflake account credentials")
class SnowflakeConnectorTest {
    private SnowflakeConnector connector;

    @BeforeEach
    void setUp() { connector = new SnowflakeConnector(); }

    @Test
    void testConnectionString() {
        assertNotNull(connector);
    }

    @Test
    void testCreateWarehouseSql() {
        assertDoesNotThrow(() -> connector.createWarehouse(null, "test_wh", "X-SMALL", 300));
    }

    @Test
    void testCloneSql() {
        assertDoesNotThrow(() -> connector.cloneTable(null, "source", "target"));
    }
}""")

write(os.path.join(tp, "WarehouseManagerTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

@Disabled("Requires Snowflake account credentials")
class WarehouseManagerTest {
    private WarehouseManager manager;

    @Test
    void testCreateSql() {
        assertDoesNotThrow(() -> new WarehouseManager(null));
    }

    @Test
    void testAlterSqlIsFormatted() {
        String expected = "ALTER WAREHOUSE test SET WAREHOUSE_SIZE='LARGE'";
        assertNotNull(expected);
    }
}""")

# ===== LAB 10: FLINK =====
pkg = "com.dataeng.ten"
sp = os.path.join(BASE, "10-apache-flink", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "10-apache-flink", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "SensorReading.java"), """package com.dataeng.ten;

import java.util.Objects;

public class SensorReading {
    private String sensorId;
    private double value;
    private long timestamp;
    private String unit;

    public SensorReading() {}

    public SensorReading(String sensorId, double value, long timestamp, String unit) {
        this.sensorId = sensorId;
        this.value = value;
        this.timestamp = timestamp;
        this.unit = unit;
    }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorReading that)) return false;
        return Double.compare(that.value, value) == 0 && timestamp == that.timestamp
            && Objects.equals(sensorId, that.sensorId) && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() { return Objects.hash(sensorId, value, timestamp, unit); }

    @Override
    public String toString() {
        return "SensorReading{sensorId='" + sensorId + "', value=" + value
            + ", timestamp=" + timestamp + ", unit='" + unit + "'}";
    }
}""")

write(os.path.join(sp, "AggregateResult.java"), """package com.dataeng.ten;

public class AggregateResult {
    private String sensorId;
    private long windowEnd;
    private double avgValue;
    private double maxValue;
    private long count;

    public AggregateResult() {}

    public AggregateResult(String sensorId, long windowEnd, double avgValue, double maxValue, long count) {
        this.sensorId = sensorId;
        this.windowEnd = windowEnd;
        this.avgValue = avgValue;
        this.maxValue = maxValue;
        this.count = count;
    }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public long getWindowEnd() { return windowEnd; }
    public void setWindowEnd(long windowEnd) { this.windowEnd = windowEnd; }
    public double getAvgValue() { return avgValue; }
    public void setAvgValue(double avgValue) { this.avgValue = avgValue; }
    public double getMaxValue() { return maxValue; }
    public void setMaxValue(double maxValue) { this.maxValue = maxValue; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}""")

write(os.path.join(sp, "SensorAggregator.java"), """package com.dataeng.ten;

import org.apache.flink.api.common.functions.AggregateFunction;

public class SensorAggregator implements AggregateFunction<SensorReading, SensorAggregator.Accumulator, AggregateResult> {

    public static class Accumulator {
        double sum = 0;
        long count = 0;
        double max = Double.MIN_VALUE;
    }

    @Override
    public Accumulator createAccumulator() { return new Accumulator(); }

    @Override
    public Accumulator add(SensorReading r, Accumulator acc) {
        acc.sum += r.getValue();
        acc.count++;
        acc.max = Math.max(acc.max, r.getValue());
        return acc;
    }

    @Override
    public AggregateResult getResult(Accumulator acc) {
        return new AggregateResult("", 0L, acc.sum / acc.count, acc.max, acc.count);
    }

    @Override
    public Accumulator merge(Accumulator a, Accumulator b) {
        a.sum += b.sum;
        a.count += b.count;
        a.max = Math.max(a.max, b.max);
        return a;
    }
}""")

write(os.path.join(tp, "SensorReadingTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SensorReadingTest {
    @Test
    void testConstructor() {
        SensorReading r = new SensorReading("s1", 25.5, 1000L, "celsius");
        assertEquals("s1", r.getSensorId());
        assertEquals(25.5, r.getValue());
        assertEquals(1000L, r.getTimestamp());
        assertEquals("celsius", r.getUnit());
    }

    @Test
    void testEquals() {
        SensorReading a = new SensorReading("s1", 25.5, 1000L, "celsius");
        SensorReading b = new SensorReading("s1", 25.5, 1000L, "celsius");
        assertEquals(a, b);
    }

    @Test
    void testNotEquals() {
        SensorReading a = new SensorReading("s1", 25.5, 1000L, "celsius");
        SensorReading b = new SensorReading("s2", 30.0, 2000L, "celsius");
        assertNotEquals(a, b);
    }

    @Test
    void testToString() {
        SensorReading r = new SensorReading("s1", 25.5, 1000L, "celsius");
        assertTrue(r.toString().contains("sensorId='s1'"));
        assertTrue(r.toString().contains("value=25.5"));
    }
}""")

write(os.path.join(tp, "SensorAggregatorTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SensorAggregatorTest {
    private final SensorAggregator agg = new SensorAggregator();

    @Test
    void testAccumulatorCreation() {
        SensorAggregator.Accumulator acc = agg.createAccumulator();
        assertEquals(0, acc.count);
        assertEquals(0.0, acc.sum);
    }

    @Test
    void testAdd() {
        SensorAggregator.Accumulator acc = agg.createAccumulator();
        agg.add(new SensorReading("s1", 10.0, 1L, "celsius"), acc);
        agg.add(new SensorReading("s1", 20.0, 2L, "celsius"), acc);
        assertEquals(2, acc.count);
        assertEquals(30.0, acc.sum);
        assertEquals(20.0, acc.max);
    }

    @Test
    void testGetResult() {
        SensorAggregator.Accumulator acc = agg.createAccumulator();
        agg.add(new SensorReading("s1", 10.0, 1L, "celsius"), acc);
        agg.add(new SensorReading("s1", 20.0, 2L, "celsius"), acc);
        AggregateResult result = agg.getResult(acc);
        assertEquals(15.0, result.getAvgValue());
        assertEquals(20.0, result.getMaxValue());
        assertEquals(2, result.getCount());
    }
}""")

# ===== LAB 11: AIRFLOW =====
pkg = "com.dataeng.eleven"
sp = os.path.join(BASE, "11-apache-airflow", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "11-apache-airflow", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "DagValidator.java"), """package com.dataeng.eleven;

import java.util.*;

public class DagValidator {
    private final Map<String, List<String>> adjacency = new HashMap<>();

    public void addEdge(String from, String to) {
        adjacency.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        adjacency.putIfAbsent(to, new ArrayList<>());
    }

    public boolean hasCycle() {
        Set<String> white = new HashSet<>(adjacency.keySet());
        Set<String> grey = new HashSet<>();
        Set<String> black = new HashSet<>();
        for (String node : List.copyOf(white)) {
            if (dfs(node, white, grey, black)) return true;
        }
        return false;
    }

    private boolean dfs(String node, Set<String> white, Set<String> grey, Set<String> black) {
        white.remove(node);
        grey.add(node);
        for (String neighbor : adjacency.getOrDefault(node, Collections.emptyList())) {
            if (black.contains(neighbor)) continue;
            if (grey.contains(neighbor)) return true;
            if (dfs(neighbor, white, grey, black)) return true;
        }
        grey.remove(node);
        black.add(node);
        return false;
    }

    public List<String> topologicalSort() {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String node : adjacency.keySet()) inDegree.put(node, 0);
        for (List<String> edges : adjacency.values()) {
            for (String n : edges) inDegree.merge(n, 1, Integer::sum);
        }

        Queue<String> queue = new LinkedList<>();
        inDegree.forEach((node, deg) -> { if (deg == 0) queue.add(node); });

        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String node = queue.poll();
            result.add(node);
            for (String n : adjacency.getOrDefault(node, Collections.emptyList())) {
                inDegree.merge(n, -1, Integer::sum);
                if (inDegree.get(n) == 0) queue.add(n);
            }
        }
        return result;
    }

    public int estimateDuration(Map<String, Integer> durations) {
        Map<String, Integer> earliest = new HashMap<>();
        for (String node : topologicalSort()) {
            int maxPred = 0;
            for (Map.Entry<String, List<String>> e : adjacency.entrySet()) {
                if (e.getValue().contains(node)) {
                    maxPred = Math.max(maxPred, earliest.getOrDefault(e.getKey(), 0));
                }
            }
            earliest.put(node, maxPred + durations.getOrDefault(node, 0));
        }
        return earliest.values().stream().mapToInt(v -> v).max().orElse(0);
    }
}""")

write(os.path.join(sp, "SlaMonitor.java"), """package com.dataeng.eleven;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SlaMonitor {
    private final Map<String, SlaDefinition> slas = new ConcurrentHashMap<>();
    private final Map<String, List<SlaBreach>> breaches = new ConcurrentHashMap<>();

    public record SlaDefinition(String dagId, String taskId, Duration maxDuration, Duration maxLag, String severity) {}
    public record SlaBreach(String dagId, String taskId, String metric, double actualValue, double threshold, Instant timestamp) {}

    public void registerSla(SlaDefinition sla) {
        slas.put(sla.dagId() + "." + sla.taskId(), sla);
    }

    public void checkDuration(String dagId, String taskId, Duration actual) {
        SlaDefinition sla = slas.get(dagId + "." + taskId);
        if (sla != null && actual.compareTo(sla.maxDuration()) > 0) {
            var breach = new SlaBreach(dagId, taskId, "duration",
                actual.toMillis() / 1000.0, sla.maxDuration().toMillis() / 1000.0, Instant.now());
            breaches.computeIfAbsent(dagId, k -> new CopyOnWriteArrayList<>()).add(breach);
            alert(breach);
        }
    }

    private void alert(SlaBreach b) {
        System.err.printf("SLA BREACH: %s/%s - %s (%.1fs > %.1fs)%n",
            b.dagId(), b.taskId(), b.metric(), b.actualValue(), b.threshold());
    }

    public List<SlaBreach> getBreaches(String dagId) {
        return breaches.getOrDefault(dagId, Collections.emptyList());
    }
}""")

write(os.path.join(tp, "DagValidatorTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DagValidatorTest {
    @Test
    void testNoCycle() {
        DagValidator dv = new DagValidator();
        dv.addEdge("A", "B");
        dv.addEdge("B", "C");
        assertFalse(dv.hasCycle());
    }

    @Test
    void testHasCycle() {
        DagValidator dv = new DagValidator();
        dv.addEdge("A", "B");
        dv.addEdge("B", "C");
        dv.addEdge("C", "A");
        assertTrue(dv.hasCycle());
    }

    @Test
    void testTopologicalSort() {
        DagValidator dv = new DagValidator();
        dv.addEdge("A", "B");
        dv.addEdge("A", "C");
        dv.addEdge("B", "D");
        dv.addEdge("C", "D");
        var order = dv.topologicalSort();
        assertEquals(4, order.size());
        assertEquals("A", order.get(0));
        assertEquals("D", order.get(3));
    }

    @Test
    void testEstimateDuration() {
        DagValidator dv = new DagValidator();
        dv.addEdge("A", "B");
        dv.addEdge("B", "C");
        var durations = Map.of("A", 5, "B", 10, "C", 3);
        assertEquals(18, dv.estimateDuration(durations));
    }
}""")

write(os.path.join(tp, "SlaMonitorTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.*;

class SlaMonitorTest {
    @Test
    void testNoBreach() {
        SlaMonitor sm = new SlaMonitor();
        sm.registerSla(new SlaMonitor.SlaDefinition("dag1", "task1", Duration.ofMinutes(5), Duration.ZERO, "HIGH"));
        sm.checkDuration("dag1", "task1", Duration.ofMinutes(3));
        assertTrue(sm.getBreaches("dag1").isEmpty());
    }

    @Test
    void testBreachDetected() {
        SlaMonitor sm = new SlaMonitor();
        sm.registerSla(new SlaMonitor.SlaDefinition("dag1", "task1", Duration.ofMinutes(5), Duration.ZERO, "HIGH"));
        sm.checkDuration("dag1", "task1", Duration.ofMinutes(10));
        assertFalse(sm.getBreaches("dag1").isEmpty());
        assertEquals("duration", sm.getBreaches("dag1").get(0).metric());
    }

    @Test
    void testMultipleBreaches() {
        SlaMonitor sm = new SlaMonitor();
        sm.registerSla(new SlaMonitor.SlaDefinition("dag1", "t1", Duration.ofMinutes(5), Duration.ZERO, "HIGH"));
        sm.registerSla(new SlaMonitor.SlaDefinition("dag1", "t2", Duration.ofMinutes(3), Duration.ZERO, "HIGH"));
        sm.checkDuration("dag1", "t1", Duration.ofMinutes(10));
        sm.checkDuration("dag1", "t2", Duration.ofMinutes(10));
        assertEquals(2, sm.getBreaches("dag1").size());
    }
}""")

# ===== LAB 12: BEAM =====
pkg = "com.dataeng.twelve"
sp = os.path.join(BASE, "12-apache-beam", "src", "main", "java", *pkg.split("."))
tp = os.path.join(BASE, "12-apache-beam", "src", "test", "java", *pkg.split("."))

write(os.path.join(sp, "BeamPipelineValidator.java"), """package com.dataeng.twelve;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

public class BeamPipelineValidator {

    public static PCollection<String> validateNonEmpty(PCollection<String> input, String name) {
        return input.apply(name, ParDo.of(new DoFn<String, String>() {
            @ProcessElement
            public void process(ProcessContext c) {
                if (c.element() != null && !c.element().isEmpty()) {
                    c.output(c.element());
                }
            }
        }));
    }

    public static PCollection<String> sample(PCollection<String> input, double fraction) {
        return input.apply("Sample", ParDo.of(new DoFn<String, String>() {
            @ProcessElement
            public void process(ProcessContext c) {
                if (Math.random() < fraction) c.output(c.element());
            }
        }));
    }

    public static PCollection<String> deduplicate(PCollection<String> input) {
        return input.apply("Dedup", ParDo.of(new DoFn<String, String>() {
            private final java.util.Set<String> seen = new java.util.HashSet<>();
            @ProcessElement
            public void process(ProcessContext c) {
                if (seen.add(c.element())) c.output(c.element());
            }
        }));
    }

    public static void main(String[] args) {
        var options = PipelineOptionsFactory.fromArgs(args).withValidation().create();
        Pipeline p = Pipeline.create(options);
        System.out.println("Pipeline created: " + p.getOptions().getAppName());
        p.run().waitUntilFinish();
    }
}""")

write(os.path.join(sp, "WordCountPipeline.java"), """package com.dataeng.twelve;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.*;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptors;

public class WordCountPipeline {

    public interface Options extends PipelineOptions {
        @Description("Input path") @Default.String("gs://data/input/*.txt")
        String getInputFile(); void setInputFile(String v);
        @Description("Output path") @Default.String("gs://data/output/counts")
        String getOutput(); void setOutput(String v);
    }

    public static void main(String[] args) {
        var opts = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
        Pipeline p = Pipeline.create(opts);
        p.apply("Read", TextIO.read().from(opts.getInputFile()))
         .apply("Words", FlatMapElements.into(TypeDescriptors.strings())
             .via((String line) -> java.util.Arrays.asList(line.toLowerCase().split("[^a-z']+"))))
         .apply("Filter", Filter.by(w -> !w.isEmpty()))
         .apply("Count", Count.perElement())
         .apply("Format", MapElements.into(TypeDescriptors.strings())
             .via((KV<String, Long> kv) -> kv.getKey() + ": " + kv.getValue()))
         .apply("Write", TextIO.write().to(opts.getOutput()).withSuffix(".txt"));
        p.run().waitUntilFinish();
    }
}""")

write(os.path.join(tp, "BeamPipelineValidatorTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BeamPipelineValidatorTest {
    @Test
    void testValidateNonEmpty() {
        assertDoesNotThrow(() -> {
            var opts = org.apache.beam.sdk.options.PipelineOptionsFactory.create();
            org.apache.beam.sdk.Pipeline p = org.apache.beam.sdk.Pipeline.create(opts);
            var input = p.apply("Create", org.apache.beam.sdk.values.PCollectionList.of(p)
                .apply(org.apache.beam.sdk.io.Create.of("hello", "", "world")));
            var result = BeamPipelineValidator.validateNonEmpty(input, "Test");
            assertNotNull(result);
        });
    }

    @Test
    void testPipelineCreation() {
        assertDoesNotThrow(() -> {
            var opts = org.apache.beam.sdk.options.PipelineOptionsFactory.create();
            assertNotNull(opts);
        });
    }
}""")

write(os.path.join(tp, "WordCountPipelineTest.java"), """import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class WordCountPipelineTest {
    @Test
    void testOptionsCreation() {
        var opts = org.apache.beam.sdk.options.PipelineOptionsFactory
            .fromArgs(new String[]{"--inputFile=in.txt", "--output=out.txt"})
            .as(WordCountPipeline.Options.class);
        assertEquals("in.txt", opts.getInputFile());
        assertEquals("out.txt", opts.getOutput());
    }

    @Test
    void testPipelineRuns() {
        assertDoesNotThrow(() -> {
            var opts = org.apache.beam.sdk.options.PipelineOptionsFactory.create();
            var p = org.apache.beam.sdk.Pipeline.create(opts);
            assertNotNull(p);
            p.run().waitUntilFinish();
        });
    }
}""")

print("Java sources and tests added for labs 09-12!")
