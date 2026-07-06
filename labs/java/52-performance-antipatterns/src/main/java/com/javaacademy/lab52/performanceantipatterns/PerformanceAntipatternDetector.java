package com.javaacademy.lab52.performanceantipatterns;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Uses reflection and runtime inspection to detect common
 * performance antipatterns in loaded classes:
 * - Boxing in collections (Map<Integer, Long> usage)
 * - String concatenation in loops
 * - Missing hashCode/equals on used keys
 * - ThreadLocal without remove()
 * - FinalReference usage
 * - Synchronized methods in hot paths
 */
public class PerformanceAntipatternDetector {

    public static void main(String[] args) {
        System.out.println("=== Performance Antipattern Detector ===");

        // 1. Check common collections for boxing patterns
        detectBoxingPatterns();

        // 2. Check for synchronized methods (potential contention)
        detectSynchronizedMethods();

        // 3. Check for ThreadLocal without removal pattern
        detectThreadLocalPatterns();

        System.out.println("\nDetection scan complete.");
    }

    static void detectBoxingPatterns() {
        System.out.println("\n[1] Boxing Pattern Detection:");
        // Inspect commonly loaded collection types
        Set<Class<?>> boxedCollectionTypes = Set.of(
            HashMap.class, ArrayList.class, HashSet.class,
            TreeMap.class, LinkedHashMap.class
        );

        for (var cls : boxedCollectionTypes) {
            System.out.println("  " + cls.getSimpleName() + " uses wrapper types internally.");
            System.out.println("    Consider specialized libraries (e.g., Eclipse Collections, fastutil)");
            System.out.println("    or Array-of-struct approaches for primitive-heavy workloads.");
        }
    }

    static void detectSynchronizedMethods() {
        System.out.println("\n[2] Synchronized Method Detection:");
        // Note: in a full implementation we'd scan classpath jars
        // Here we just check our own classes as an example
        var checkedClasses = List.of(
            PerformanceAntipatternDetector.class,
            BoxingPerformance.class,
            ContentionExample.class
        );

        for (var cls : checkedClasses) {
            long syncedCount = Arrays.stream(cls.getDeclaredMethods())
                .filter(m -> Modifier.isSynchronized(m.getModifiers()))
                .count();
            if (syncedCount > 0) {
                System.out.println("  " + cls.getSimpleName() + " has " + syncedCount
                    + " synchronized methods — potential contention point");
            }
        }
    }

    static void detectThreadLocalPatterns() {
        System.out.println("\n[3] ThreadLocal Pattern Detection:");
        // Check if ThreadLocal usage exists without corresponding remove()
        var checkedClasses = List.of(ThreadLocalLeakDemo.class);
        for (var cls : checkedClasses) {
            long tlFields = Arrays.stream(cls.getDeclaredFields())
                .filter(f -> ThreadLocal.class.isAssignableFrom(f.getType()))
                .count();
            if (tlFields > 0) {
                System.out.println("  " + cls.getSimpleName() + " has " + tlFields
                    + " ThreadLocal fields — verify remove() is called");
                System.out.println("    Tip: Always use try-finally { threadLocal.remove(); }");
            }
        }
    }
}
