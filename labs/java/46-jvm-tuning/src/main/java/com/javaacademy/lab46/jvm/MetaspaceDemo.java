package com.javaacademy.lab46.jvm;

import javax.tools.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.*;

/**
 * Generates many classes dynamically to measure metaspace growth.
 * Uses Java Compiler API to compile classes at runtime.
 */
public class MetaspaceDemo {

    private static final List<Class<?>> generatedClasses = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        System.out.println("=== Metaspace Growth Demo ===\n");

        // Get metaspace usage from MemoryPoolMXBean
        var pools = ManagementFactory.getMemoryPoolMXBeans();
        for (var pool : pools) {
            if (pool.getName().contains("Metaspace")) {
                var usage = pool.getUsage();
                System.out.println("Initial Metaspace: " + usage.getUsed() / 1024 + " KB");
            }
        }

        // Generate classes dynamically
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("Java Compiler API not available (run with JDK, not JRE)");
            return;
        }

        int count = 0;
        try {
            for (int i = 0; i < 500; i++) {
                String className = "com.javaacademy.lab46.jvm.Generated_" + i;
                String source = "package com.javaacademy.lab46.jvm;\n"
                    + "public class Generated_" + i + " {\n"
                    + "    public int value() { return " + i + "; }\n"
                    + "}";

                // Compile from string
                var fileManager = compiler.getStandardFileManager(null, null, null);
                var sourceObj = new SimpleJavaFileObject(
                    URI.create("string:///" + className.replace('.', '/') + ".java"),
                    JavaFileObject.Kind.SOURCE) {
                    @Override
                    public CharSequence getCharContent(boolean ignore) { return source; }
                };

                var task = compiler.getTask(null, fileManager, null, null, null,
                    Collections.singletonList(sourceObj));
                task.call();
                fileManager.close();

                Class<?> clazz = Class.forName(className);
                generatedClasses.add(clazz);
                count++;
            }
        } catch (Exception e) {
            System.out.println("Stopped after " + count + " classes: " + e.getMessage());
        }

        System.out.println("\nGenerated " + count + " classes");

        for (var pool : pools) {
            if (pool.getName().contains("Metaspace")) {
                var usage = pool.getUsage();
                System.out.println("Final Metaspace: " + usage.getUsed() / 1024 + " KB");
            }
        }

        System.out.println("\nJVM flags: -XX:MaxMetaspaceSize=256m -XX:MetaspaceSize=64m");
    }
}
