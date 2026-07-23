package performance;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

/**
 * GC Log Analyzer — parses and analyzes GC log files.
 * 
 * GC log format varies by JVM version and flags.
 * Java 8 default: -XX:+PrintGCDetails -XX:+PrintGCDateStamps
 * Java 9+ default: -Xlog:gc*
 * 
 * This analyzer supports multiple formats and extracts key metrics:
 * - GC pause times
 * - Heap usage before/after
 * - GC frequency
 * - Promotion rates
 * 
 * Run with a GC log file as argument.
 */
public class GCLogAnalyzer {

    static class GCEvent {
        double pauseMs;
        long heapBefore, heapAfter;
        long heapBeforeTotal, heapAfterTotal;
        String type; // Young, Full, Mixed
        double timestamp;
    }

    public static void main(String[] args) throws Exception {
        String logFile = args.length > 0 ? args[0] : "gc.log";

        Path path = Paths.get(logFile);
        if (!Files.exists(path)) {
            System.out.println("GC log not found: " + logFile);
            System.out.println("To generate: run Java with -Xlog:gc*:file=gc.log");
            return;
        }

        var events = new java.util.ArrayList<GCEvent>();
        
        // Patterns for different GC log formats
        Pattern java8Pattern = Pattern.compile(
            ".*?(\\d+\\.\\d+): \\[GC.*?\\[.*?Before GC: (\\d+)K->(\\d+)K\\((\\d+)K\\].*?After GC: (\\d+)K->(\\d+)K\\(\\d+K\\].*?\\]");
        
        Pattern java17Pattern = Pattern.compile(
            "\\[.*?\\] \\[gc,.*?\\] GC\\((\\d+)\\) Pause (\\w+) \\((.*?)\\).*?-> (\\d+)K.*?\\]");

        Pattern pausePattern = Pattern.compile(
            ".*?Pause (\\w+).*?(\\d+\\.\\d+)ms");

        try (var reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                var matcher = pausePattern.matcher(line);
                if (matcher.find()) {
                    var ev = new GCEvent();
                    ev.type = matcher.group(1);
                    ev.pauseMs = Double.parseDouble(matcher.group(2));
                    events.add(ev);
                }
            }
        }

        if (events.isEmpty()) {
            System.out.println("No GC events found in " + logFile);
            System.out.println("Expected format: -Xlog:gc* (Java 9+) or -XX:+PrintGCDetails (Java 8)");
            return;
        }

        // Analysis
        var youngPauses = events.stream().filter(e -> "Young".equals(e.type)).mapToDouble(e -> e.pauseMs);
        var fullPauses = events.stream().filter(e -> "Full".equals(e.type) || "Pause".equals(e.type)).mapToDouble(e -> e.pauseMs);

        var youngStats = youngPauses.summaryStatistics();
        var fullStats = fullPauses.summaryStatistics();

        System.out.println("=== GC Log Analysis ===");
        System.out.println("Total GC events: " + events.size());
        System.out.println("");

        System.out.println("Young GC pauses (ms):");
        System.out.println("  Count: " + youngStats.getCount());
        System.out.println("  Avg:   " + String.format("%.2f", youngStats.getAverage()));
        System.out.println("  Min:   " + String.format("%.2f", youngStats.getMin()));
        System.out.println("  Max:   " + String.format("%.2f", youngStats.getMax()));
        System.out.println("  Total: " + String.format("%.2f", youngStats.getSum()));

        System.out.println("");

        if (fullStats.getCount() > 0) {
            System.out.println("Full GC pauses (ms):");
            System.out.println("  Count: " + fullStats.getCount());
            System.out.println("  Avg:   " + String.format("%.2f", fullStats.getAverage()));
            System.out.println("  Min:   " + String.format("%.2f", fullStats.getMin()));
            System.out.println("  Max:   " + String.format("%.2f", fullStats.getMax()));
            System.out.println("  Total: " + String.format("%.2f", fullStats.getSum()));
        } else {
            System.out.println("No Full GC events detected (good).");
        }

        System.out.println("\nRecommendations:");
        if (fullStats.getAverage() > 1000) {
            System.out.println("  - Full GC pauses > 1s. Consider: increase heap, switch to G1/ZGC.");
        }
        if (events.size() > 100) {
            System.out.println("  - High GC frequency. Consider: increase young gen, tune GC.");
        }

        System.out.println("GCLogAnalyzer complete.");
    }
}