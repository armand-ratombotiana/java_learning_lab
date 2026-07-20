package com.capstone.spark;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SparkContext {
    private final String appName;
    private final String master;
    private final Map<String, String> config = new ConcurrentHashMap<>();
    private final AtomicLong jobIdGen = new AtomicLong(0);
    private final AtomicLong rddIdGen = new AtomicLong(0);
    private volatile boolean stopped = false;

    public SparkContext(String appName, String master) {
        this.appName = appName;
        this.master = master;
        config.put("spark.default.parallelism", "2");
        config.put("spark.sql.shuffle.partitions", "2");
    }

    public <T> RDD<T> parallelize(List<T> data) {
        int parallelism = Integer.parseInt(config.getOrDefault("spark.default.parallelism", "2"));
        return new RDD<>(data, parallelism);
    }

    public <T> RDD<T> parallelize(List<T> data, int numSlices) {
        return new RDD<>(data, numSlices);
    }

    public <T> RDD<T> emptyRDD() {
        return new RDD<>(List.of());
    }

    public <T> RDD<T> textFile(String path) {
        return new RDD<>(List.of("line1", "line2", "line3"), 2);
    }

    public long generateRDDId() { return rddIdGen.incrementAndGet(); }

    public long generateJobId() { return jobIdGen.incrementAndGet(); }

    public void setConfig(String key, String value) { config.put(key, value); }

    public String getConfig(String key) { return config.get(key); }

    public String getConfig(String key, String defaultValue) { return config.getOrDefault(key, defaultValue); }

    public String getAppName() { return appName; }

    public String getMaster() { return master; }

    public void stop() { stopped = true; }

    public boolean isStopped() { return stopped; }

    public void clear() { config.clear(); stopped = false; }
}
