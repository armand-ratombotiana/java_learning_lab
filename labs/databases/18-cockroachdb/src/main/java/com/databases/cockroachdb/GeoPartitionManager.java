package com.databases.cockroachdb;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GeoPartitionManager {
    private final Map<String, RegionConfig> regions = new ConcurrentHashMap<>();

    public record RegionConfig(String region, String locality, List<String> tables, int replicationFactor) {}

    public void addRegion(String name, String locality, int rf) {
        regions.put(name, new RegionConfig(name, locality, new ArrayList<>(), rf));
    }

    public void assignTable(String table, String region) {
        var reg = regions.get(region);
        if (reg == null) throw new IllegalArgumentException("Unknown region: " + region);
        var tables = new ArrayList<>(reg.tables());
        tables.add(table);
        regions.put(region, new RegionConfig(reg.region(), reg.locality(), tables, reg.replicationFactor()));
    }

    public String generateAddRegionSQL(String region) {
        var reg = regions.get(region);
        if (reg == null) return "";
        return "ALTER DATABASE " + region + " PRIMARY REGION \"" + reg.locality() + "\";\n";
    }

    public String generatePartitionByRegionSQL(String table, String region, String partitionColumn) {
        return "ALTER TABLE " + table + " PARTITION BY LIST (" + partitionColumn + ") (\n"
            + "  PARTITION " + region + "_pkey VALUES IN ('" + region + "')\n"
            + ");\n";
    }

    public String generatePlacementSQL(String table, String region) {
        return "ALTER TABLE " + table + " PARTITION " + region + "_pkey\n"
            + "  CONFIGURE ZONE USING num_replicas = "
            + regions.get(region).replicationFactor()
            + ", constraints = '{\"+region=" + regions.get(region).locality() + "\":1}';\n";
    }

    public List<String> generateFullGeoSetup(String database) {
        var sql = new ArrayList<String>();
        for (var reg : regions.values()) {
            sql.add("-- Region: " + reg.region());
            sql.add("ALTER DATABASE " + database + " ADD REGION \"" + reg.locality() + "\";");
        }
        for (var reg : regions.values()) {
            for (var table : reg.tables()) {
                sql.add(generatePartitionByRegionSQL(table, reg.region(), "region"));
                sql.add(generatePlacementSQL(table, reg.region()));
            }
        }
        return sql;
    }

    public Set<String> getRegions() { return regions.keySet(); }
    public RegionConfig getRegion(String name) { return regions.get(name); }
}
