package com.learning.lab21;

/**
 * Main entry point for Lab 21: Java 21 Features.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 21: Java 21 Features");
        System.out.println("========================================\n");

        PatternMatchingSwitchExample.showPatternMatchingSwitch();
        System.out.println();
        RecordPatternsExample.showRecordPatterns();
        System.out.println();
        SequencedCollectionsExample.showSequencedCollections();
        System.out.println();
        StringTemplatesExample.showStringFeatures();

        System.out.println("\n========================================");
        System.out.println("   Lab 21 Complete");
        System.out.println("========================================");
    }
}
