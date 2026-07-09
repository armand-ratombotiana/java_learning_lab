package com.databases.plsqladv;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class AdvancedPlSqlAnalyser {

    public record PipelinedFunc(String name, String returnType, List<String> params, String query) {}
    public record ScheduleJob(String jobName, String program, String schedule, boolean enabled) {}
    public record VpdPolicy(String schema, String table, String policy, String function, List<String> statements) {}
    public record ResultCacheEntry(String key, Object value, long timestamp, long ttl) {}

    private final Map<String, PipelinedFunc> pipelinedFuncs = new ConcurrentHashMap<>();
    private final Map<String, ScheduleJob> schedulerJobs = new ConcurrentHashMap<>();
    private final Map<String, VpdPolicy> vpdPolicies = new ConcurrentHashMap<>();
    private final Map<String, ResultCacheEntry> resultCache = new ConcurrentHashMap<>();
    private final Set<String> editionViews = new ConcurrentSkipListSet<>();

    public void registerPipelinedFunc(PipelinedFunc f) { pipelinedFuncs.put(f.name(), f); }

    public List<Map<String, Object>> simulatePipelinedExecution(String funcName, int rowsPerChunk, int maxRows) {
        var func = pipelinedFuncs.get(funcName);
        if (func == null) throw new IllegalArgumentException("Function not found: " + funcName);
        var result = new ArrayList<Map<String, Object>>();
        int processed = 0;
        while (processed < maxRows) {
            int chunkSize = Math.min(rowsPerChunk, maxRows - processed);
            for (int i = 0; i < chunkSize; i++) {
                var row = new HashMap<String, Object>();
                row.put("rowNum", processed + i + 1);
                row.put("data", "Pipelined from " + funcName + " row " + (processed + i + 1));
                result.add(row);
            }
            processed += chunkSize;
            if (chunkSize < rowsPerChunk) break;
        }
        return result;
    }

    public String scheduleJob(ScheduleJob job) {
        schedulerJobs.put(job.jobName(), job);
        return "Job " + job.jobName() + " scheduled: " + (job.enabled() ? "ENABLED" : "DISABLED");
    }

    public void addVpdPolicy(VpdPolicy policy) { vpdPolicies.put(policy.policy(), policy); }

    public String generateVpdPredicate(String schema, String table) {
        return vpdPolicies.values().stream()
            .filter(p -> p.schema().equals(schema) && p.table().equals(table))
            .findFirst()
            .map(p -> p.function() + "()")
            .orElse("1=1");
    }

    public Object cacheResult(String key, Supplier<Object> supplier, long ttlMs) {
        var entry = resultCache.get(key);
        if (entry != null && System.currentTimeMillis() - entry.timestamp() < entry.ttl()) {
            return entry.value();
        }
        var value = supplier.get();
        resultCache.put(key, new ResultCacheEntry(key, value, System.currentTimeMillis(), ttlMs));
        return value;
    }

    public void invalidateCache(String key) { resultCache.remove(key); }
    public void clearCache() { resultCache.clear(); }
    public int cacheSize() { return resultCache.size(); }

    public void registerEditionView(String viewName) { editionViews.add(viewName); }

    public List<String> getEditionViews() { return List.copyOf(editionViews); }

    public List<Map<String, Object>> simulateAQ(String queue, List<String> messages) {
        var result = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < messages.size(); i++) {
            var entry = new HashMap<String, Object>();
            entry.put("queue", queue);
            entry.put("messageId", i + 1);
            entry.put("payload", messages.get(i));
            entry.put("enqueued", System.currentTimeMillis());
            result.add(entry);
        }
        return result;
    }

    public static AdvancedPlSqlAnalyser createSample() {
        var a = new AdvancedPlSqlAnalyser();
        a.registerPipelinedFunc(new PipelinedFunc("get_employees_pipe", "SYS.ODCINUMBERLIST",
            List.of("p_dept_id NUMBER"), "SELECT emp_id FROM employees WHERE dept_id = p_dept_id"));
        a.addVpdPolicy(new VpdPolicy("HR", "EMPLOYEES", "emp_sec_policy",
            "emp_sec_function", List.of("SELECT", "UPDATE")));
        a.registerEditionView("V_EMP_SALARY");
        a.registerEditionView("V_DEPT_SUMMARY");
        return a;
    }
}