package com.learning.graalvm;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {
    // Simulated reflection configuration for GraalVM
    @interface ReflectionHint { boolean value() default true; }

    static class NativeImageConfig {
        final Set<String> reflectionClasses = ConcurrentHashMap.newKeySet();
        final Set<String> resourcePatterns = ConcurrentHashMap.newKeySet();
        final Set<String> serializationClasses = ConcurrentHashMap.newKeySet();

        NativeImageConfig addReflection(Class<?> clazz) {
            reflectionClasses.add(clazz.getName());
            return this;
        }

        NativeImageConfig addResource(String pattern) {
            resourcePatterns.add(pattern);
            return this;
        }

        NativeImageConfig addSerialization(Class<?> clazz) {
            serializationClasses.add(clazz.getName());
            return this;
        }

        void generateConfig() {
            System.out.println("  // reflect-config.json");
            reflectionClasses.forEach(c -> System.out.println("  { \"name\": \"" + c + "\", \"allDeclaredConstructors\": true, \"allDeclaredMethods\": true }"));
            System.out.println("  // resource-config.json");
            resourcePatterns.forEach(p -> System.out.println("  { \"pattern\": \"" + p + "\" }"));
            System.out.println("  // serialization-config.json");
            serializationClasses.forEach(c -> System.out.println("  { \"name\": \"" + c + "\" }"));
        }
    }

    // Feature detection
    static class ImageBuildInfo {
        static boolean isNativeImage() {
            try {
                Class.forName("org.graalvm.nativeimage.ImageInfo");
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        static boolean isBuildTime() {
            try {
                var clazz = Class.forName("org.graalvm.nativeimage.ImageInfo");
                return (boolean) clazz.getMethod("inImageBuildtimeCode").invoke(null);
            } catch (Exception e) {
                return false;
            }
        }
    }

    static class RuntimeInitialization {
        final List<String> buildTimeInit = new ArrayList<>();
        final List<String> runTimeInit = new ArrayList<>();

        void initializeAtBuildTime(String className) { buildTimeInit.add(className); }
        void initializeAtRunTime(String className) { runTimeInit.add(className); }

        void report() {
            System.out.println("\n  // Initialization");
            buildTimeInit.forEach(c -> System.out.println("  " + c + " -> build-time"));
            runTimeInit.forEach(c -> System.out.println("  " + c + " -> run-time"));
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== GraalVM Native Image Concepts ===\n");

        // AOT compilation
        System.out.println("--- Ahead-of-Time (AOT) Compilation ---");
        System.out.println("  Source -> Native executable (no JIT warmup needed)");
        System.out.println("  Faster startup, lower memory, smaller footprint");
        System.out.println("  isNativeImage(): " + ImageBuildInfo.isNativeImage());

        // Reflection config
        System.out.println("\n--- Reflection Configuration ---");
        var config = new NativeImageConfig();
        config.addReflection(java.util.Date.class);
        config.addReflection(java.math.BigDecimal.class);
        config.addReflection(java.util.UUID.class);
        config.addResource("application\\.yml");
        config.addResource("messages\\.properties");
        config.addSerialization(java.util.HashMap.class);
        config.generateConfig();

        // Runtime vs Build-time init
        System.out.println("\n--- Runtime Initialization ---");
        var init = new RuntimeInitialization();
        init.initializeAtBuildTime("com.learning.graalvm.Lab");
        init.initializeAtRunTime("java.util.concurrent.ThreadPoolExecutor");
        init.report();

        // Proxy configuration
        System.out.println("\n--- Dynamic Proxy ---");
        System.out.println("  // proxy-config.json");
        System.out.println("  { \"interfaces\": [ \"java.util.function.Function\", \"java.io.Serializable\" ] }");

        // Class metadata
        System.out.println("\n--- Class Metadata Reachability ---");
        var classes = List.of(
            "java.lang.String", "java.util.HashMap", "java.lang.Thread",
            "java.sql.Connection", "javax.sql.DataSource"
        );
        for (var name : classes) {
            try {
                var clazz = Class.forName(name);
                System.out.println("  " + name + " -> " + (clazz.getDeclaredMethods().length > 0 ? "reachable" : "unreachable"));
            } catch (ClassNotFoundException e) {
                System.out.println("  " + name + " -> UNREACHABLE (missing)");
            }
        }

        // Resource bundle
        System.out.println("\n--- Resource Bundles ---");
        System.out.println("  // resource-config.json entry:");
        System.out.println("  { \"bundles\": [ { \"name\": \"com.example.i18n.messages\" } ] }");

        // Native image build
        System.out.println("\n--- Build Process ---");
        System.out.println("  $ native-image -jar app.jar --no-fallback");
        System.out.println("  $ native-image -jar app.jar --initialize-at-build-time=com.example");
        System.out.println("  $ native-image -jar app.jar -H:ReflectionConfigurationFiles=reflect-config.json");

        // Comparison
        System.out.println("\n--- JVM vs Native Image ---");
        System.out.println("  Metric           | JVM        | Native Image");
        System.out.println("  Startup time     | ~3s        | ~30ms");
        System.out.println("  Memory (heap)    | ~150MB     | ~20MB");
        System.out.println("  Peak performance | Excellent  | Good (no JIT)");
        System.out.println("  Build time       | ~5s        | ~3min");
    }
}
