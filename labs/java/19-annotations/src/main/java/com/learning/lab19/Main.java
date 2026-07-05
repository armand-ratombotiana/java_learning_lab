package com.learning.lab19;

/**
 * Main entry point for Lab 19: Annotations.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 19: Annotations");
        System.out.println("========================================\n");

        BuiltInAnnotationsExample.showBuiltInAnnotations();
        System.out.println();
        CustomAnnotationExample.showCustomAnnotations();
        System.out.println();
        AnnotationProcessorExample.showAnnotationProcessing();

        System.out.println("\n========================================");
        System.out.println("   Lab 19 Complete");
        System.out.println("========================================");
    }
}
