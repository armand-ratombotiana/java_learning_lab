package com.javaacademy.lab43.classloading;

/**
 * Prints the full class loader hierarchy for the current class.
 * Shows Bootstrap (null), Platform, and Application class loaders.
 */
public class ClassLoaderHierarchy {

    public static void main(String[] args) {
        System.out.println("=== ClassLoader Hierarchy ===\n");

        ClassLoader cl = ClassLoaderHierarchy.class.getClassLoader();
        while (cl != null) {
            String name = cl.getName();
            System.out.println("ClassLoader: " + name + " (type: " + cl.getClass().getName() + ")");
            // List loaded modules
            cl = cl.getParent();
        }
        System.out.println("Bootstrap ClassLoader (null - native, C++ implementation)");

        System.out.println("\nPlatform ClassLoader: " + ClassLoader.getPlatformClassLoader().getName());
    }
}
