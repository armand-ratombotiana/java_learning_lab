package com.databases.plsqladv;

import java.util.*;
import java.util.concurrent.*;

public class PlSqlProfiler {

    public record ProfileSample(String unit, String lineText, int lineNum, long elapsedNs, long calls) {}
    public record ProfilerResult(Map<String, List<ProfileSample>> byUnit, long totalTimeNs) {}

    private final Map<String, List<ProfileSample>> samples = new ConcurrentHashMap<>();
    private final Map<String, Long> timers = new ConcurrentHashMap<>();
    private volatile boolean running = false;

    public void start() { running = true; }

    public void stop() { running = false; }

    public void startTimer(String timerName) {
        timers.put(timerName, System.nanoTime());
    }

    public long stopTimer(String timerName) {
        var start = timers.remove(timerName);
        if (start == null) return 0;
        return System.nanoTime() - start;
    }

    public void recordSample(String unit, String text, int lineNum, long elapsedNs, long calls) {
        samples.computeIfAbsent(unit, k -> new CopyOnWriteArrayList<>())
            .add(new ProfileSample(unit, text, lineNum, elapsedNs, calls));
    }

    public ProfilerResult getResult() {
        long total = samples.values().stream()
            .flatMap(List::stream)
            .mapToLong(ProfileSample::elapsedNs)
            .sum();
        return new ProfilerResult(new HashMap<>(samples), total);
    }

    public List<ProfileSample> getSamples(String unit) {
        return samples.getOrDefault(unit, List.of());
    }

    public Map<String, Long> getUnitTotals() {
        var totals = new HashMap<String, Long>();
        samples.forEach((unit, list) ->
            totals.put(unit, list.stream().mapToLong(ProfileSample::elapsedNs).sum()));
        return totals;
    }

    public void clear() { samples.clear(); }

    public String generateReport() {
        var sb = new StringBuilder();
        sb.append("PL/SQL Profiler Report\n");
        sb.append("======================\n\n");
        var totals = getUnitTotals();
        long grandTotal = totals.values().stream().mapToLong(Long::longValue).sum();
        var sorted = totals.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .toList();
        for (var entry : sorted) {
            double pct = grandTotal > 0 ? (entry.getValue() * 100.0 / grandTotal) : 0;
            sb.append(String.format("%-30s %12d ns (%5.1f%%)\n",
                entry.getKey(), entry.getValue(), pct));
        }
        sb.append("\nDetailed samples:\n");
        samples.forEach((unit, list) -> {
            sb.append("\n--- ").append(unit).append(" ---\n");
            list.stream()
                .sorted(Comparator.comparingLong(ProfileSample::elapsedNs).reversed())
                .limit(10)
                .forEach(s -> sb.append(String.format("  Line %4d (%s): %12d ns, %5d calls\n",
                    s.lineNum(), s.lineText().length() > 40 ? s.lineText().substring(0, 40) : s.lineText(),
                    s.elapsedNs(), s.calls())));
        });
        return sb.toString();
    }

    public static PlSqlProfiler createWithSampleData() {
        var p = new PlSqlProfiler();
        p.recordSample("EMP_PKG.GET_SALARY", "SELECT salary INTO v_sal FROM employees WHERE emp_id = v_id", 42, 250000, 100);
        p.recordSample("EMP_PKG.GET_SALARY", "RETURN v_sal", 45, 5000, 100);
        p.recordSample("EMP_PKG.CALC_BONUS", "v_bonus := v_salary * 0.1", 78, 15000, 50);
        p.recordSample("EMP_PKG.CALC_BONUS", "IF v_years > 10 THEN", 80, 8000, 50);
        p.recordSample("HR_TRIGGER.VALIDATE_SAL", "RAISE_APPLICATION_ERROR", 15, 12000, 5);
        return p;
    }
}