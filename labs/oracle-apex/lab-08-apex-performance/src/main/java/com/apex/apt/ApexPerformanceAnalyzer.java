package com.apex.apt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.*;

public class ApexPerformanceAnalyzer {
    public record PageMetric(int pageId, String pageName, long renderTimeMs, long dbTimeMs, int regions, int items) {}
    public record QueryMetric(String sql, long elapsedMs, long rowsProcessed, String executionPlan) {}
    public record CacheStats(String name, int hits, int misses, long totalSize) {}

    private final Map<Integer, PageMetric> pageMetrics = new ConcurrentHashMap<>();
    private final List<QueryMetric> queryMetrics = new ArrayList<>();
    private final Map<String, CacheStats> cacheStats = new ConcurrentHashMap<>();

    public void recordPageMetric(PageMetric m) { pageMetrics.put(m.pageId(), m); }

    public PageMetric getPageMetric(int pageId) { return pageMetrics.get(pageId); }

    public PageMetric getSlowestPage() {
        return pageMetrics.values().stream()
            .max(Comparator.comparingLong(PageMetric::renderTimeMs))
            .orElse(null);
    }

    public PageMetric getFastestPage() {
        return pageMetrics.values().stream()
            .min(Comparator.comparingLong(PageMetric::renderTimeMs))
            .orElse(null);
    }

    public double getAverageRenderTime() {
        return pageMetrics.values().stream()
            .mapToLong(PageMetric::renderTimeMs)
            .average().orElse(0);
    }

    public void recordQueryMetric(QueryMetric m) { queryMetrics.add(m); }

    public List<QueryMetric> getSlowQueries(long thresholdMs) {
        return queryMetrics.stream()
            .filter(q -> q.elapsedMs() >= thresholdMs)
            .sorted(Comparator.comparingLong(QueryMetric::elapsedMs).reversed())
            .toList();
    }

    public QueryMetric getWorstQuery() {
        return queryMetrics.stream()
            .max(Comparator.comparingLong(QueryMetric::elapsedMs))
            .orElse(null);
    }

    public void recordCacheStats(CacheStats s) { cacheStats.put(s.name(), s); }

    public CacheStats getCacheStats(String name) { return cacheStats.get(name); }

    public double getCacheHitRatio(String name) {
        var cs = cacheStats.get(name);
        if (cs == null) return 0;
        int total = cs.hits() + cs.misses();
        return total > 0 ? (double) cs.hits() / total * 100 : 0;
    }

    public String generatePerformanceReport() {
        var sb = new StringBuilder();
        sb.append("APEX Performance Report\n=====================\n\n");
        sb.append("Page Metrics:\n");
        sb.append(String.format("  Average render time: %.2f ms\n", getAverageRenderTime()));
        var slowest = getSlowestPage();
        if (slowest != null) sb.append(String.format("  Slowest page: %d (%s) - %d ms\n",
            slowest.pageId(), slowest.pageName(), slowest.renderTimeMs()));
        sb.append("\nQuery Metrics:\n");
        var worst = getWorstQuery();
        if (worst != null) sb.append(String.format("  Worst query: %d ms\n", worst.elapsedMs()));
        sb.append("\nCache Metrics:\n");
        for (var cs : cacheStats.values()) {
            sb.append(String.format("  %s: %.1f%% hit ratio\n", cs.name(), getCacheHitRatio(cs.name())));
        }
        return sb.toString();
    }

    public static ApexPerformanceAnalyzer createSample() {
        var pa = new ApexPerformanceAnalyzer();
        pa.recordPageMetric(new PageMetric(1, "Dashboard", 120, 80, 5, 3));
        pa.recordPageMetric(new PageMetric(2, "Employee Form", 450, 320, 8, 15));
        pa.recordPageMetric(new PageMetric(3, "Report", 890, 650, 3, 2));
        pa.recordQueryMetric(new QueryMetric("SELECT * FROM employees JOIN departments...", 320, 5000, "HASH JOIN"));
        pa.recordQueryMetric(new QueryMetric("SELECT COUNT(*) FROM sales WHERE region = :1", 1200, 1000000, "FULL TABLE SCAN"));
        pa.recordCacheStats(new CacheStats("Region Cache", 950, 50, 1024));
        pa.recordCacheStats(new CacheStats("Query Cache", 500, 100, 512));
        return pa;
    }
}