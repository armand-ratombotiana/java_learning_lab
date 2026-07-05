package com.learning.lab29;

/**
 * Demonstrates module-info.java concepts in code.
 * 
 * module com.learning.lab29 {
 *     exports com.learning.lab29.service;
 *     exports com.learning.lab29;
 * }
 * 
 * In a real module setup, this file would be at the module root.
 * Here we demonstrate the concepts programmatically.
 */
public class ModuleInfoExample {

    public static void showModuleInfo() {
        System.out.println("=== module-info.java Concepts ===");

        Module module = ModuleInfoExample.class.getModule();
        System.out.println("  Module name: " + module.getName());

        System.out.println("  Module is named: " + module.isNamed());
        System.out.println("  Module descriptor: " + module.getDescriptor());

        boolean hasService = module.getDescriptor() != null 
            && !module.getDescriptor().exports().isEmpty();
        System.out.println("  Has exports: " + hasService);

        System.out.println();
        System.out.println("  Typical module-info.java:");
        System.out.println("  module com.learning.lab29 {");
        System.out.println("      exports com.learning.lab29;");
        System.out.println("      exports com.learning.lab29.service;");
        System.out.println("      requires java.base;");
        System.out.println("  }");
    }
}
