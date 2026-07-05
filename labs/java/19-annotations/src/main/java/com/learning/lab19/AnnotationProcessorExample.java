package com.learning.lab19;

import java.lang.reflect.*;

/**
 * Demonstrates processing custom annotations at runtime using reflection.
 */
public class AnnotationProcessorExample {

    public static void showAnnotationProcessing() {
        System.out.println("=== Annotation Processing ===");

        processAnnotations(new ServiceClass());
        processAnnotations(new UtilityClass());
    }

    static void processAnnotations(Object obj) {
        Class<?> clazz = obj.getClass();
        System.out.println("Processing: " + clazz.getSimpleName());

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Loggable.class)) {
                System.out.println("  @Loggable method detected: " + method.getName());
                try {
                    method.invoke(obj);
                } catch (Exception e) {
                    System.out.println("  Error invoking: " + e.getMessage());
                }
            }
        }
    }
}

class ServiceClass {
    @Loggable
    public void serve() {
        System.out.println("    Service executed");
    }

    public void helper() {
        System.out.println("    Helper (not logged)");
    }
}

class UtilityClass {
    @Loggable
    public void format() {
        System.out.println("    Format executed");
    }

    @Loggable
    public void validate() {
        System.out.println("    Validate executed");
    }
}
