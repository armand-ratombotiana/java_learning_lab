package com.prod.solutions.memoryleak;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simulated heap dump analysis tool.
 * In production, use Eclipse MAT or jhat to analyze real .hprof files.
 * This class demonstrates what to look for when analyzing a heap dump
 * for ThreadLocal-related memory leaks.
 */
public class HeapDumpAnalyzer {

    static class LeakSuspect {
        final String className;
        final long retainedHeap;
        final String retainingThread;
        final String referenceChain;

        LeakSuspect(String className, long retainedHeap, String retainingThread, String referenceChain) {
            this.className = className;
            this.retainedHeap = retainedHeap;
            this.retainingThread = retainingThread;
            this.referenceChain = referenceChain;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Simulated Heap Dump Analysis ===");
        System.out.println("Analysis time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        LeakSuspect[] suspects = {
            new LeakSuspect(
                "java.lang.ThreadLocal$ThreadLocalMap$Entry",
                2_147_483_648L, // 2GB
                "http-nio-8080-exec-10",
                "ThreadLocal.ThreadLocalMap.Entry → SecurityContext → URLClassLoader"
            ),
            new LeakSuspect(
                "java.net.URLClassLoader",
                1_073_741_824L, // 1GB
                "http-nio-8080-exec-5",
                "ThreadLocal.ThreadLocalMap.Entry → SessionContext → URLClassLoader"
            ),
            new LeakSuspect(
                "com.netflix.zuul.context.SecurityContext",
                536_870_912L, // 512MB
                "http-nio-8080-exec-3",
                "ThreadLocal.ThreadLocalMap.Entry → SecurityContext"
            )
        };

        System.out.println("\n--- Leak Suspects Report ---");
        for (int i = 0; i < suspects.length; i++) {
            LeakSuspect s = suspects[i];
            System.out.printf("%nSuspect %d: %s%n", i + 1, s.className);
            System.out.printf("  Retained Heap: %d bytes (%.2f MB)%n", s.retainedHeap, s.retainedHeap / (1024.0 * 1024));
            System.out.printf("  Retaining Thread: %s%n", s.retainingThread);
            System.out.printf("  Reference Chain: %s%n", s.referenceChain);
        }

        System.out.println("\n--- Diagnosis ---");
        System.out.println("Pattern: ThreadLocal values not removed in thread-pooled environment");
        System.out.println("Root cause: Missing threadLocal.remove() in finally block");
        System.out.println("Fix: Add try-finally with threadLocal.remove() in request lifecycle");
        System.out.println("Defensive fix: Wrap ThreadLocal values in WeakReference");

        // Show reference chain analysis
        System.out.println("\n--- Reference Chain Analysis ---");
        System.out.println("GC Root (Thread)");
        System.out.println("  └── ThreadLocal.ThreadLocalMap");
        System.out.println("       └── Entry (key=ThreadLocal, value=SecurityContext)");
        System.out.println("            └── SecurityContext.classLoader");
        System.out.println("                 └── URLClassLoader (retains all loaded classes)");
        System.out.println("                      └── Metaspace (~2GB of class metadata)");
        System.out.println("\n  ↑ This chain prevents GC of ALL classes loaded by URLClassLoader");
    }
}
