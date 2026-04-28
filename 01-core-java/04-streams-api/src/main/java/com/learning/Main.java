package com.learning;

import com.learning.basic.*;
import com.learning.filtering.*;
import com.learning.transformation.*;
import com.learning.terminal.*;
import com.learning.collectors.*;
import com.learning.optional.*;
import com.learning.parallel.*;
import com.learning.advanced.*;

/**
 * Main entry point for Streams API module.
 * Demonstrates all major stream operations and patterns.
 * Orchestrates demonstrations from 16+ stream demo classes.
 * 
 * @author Java Learning Team
 * @version 2.0
 */
public class Main {
    
    private static final String SECTION_SEPARATOR = "\n" + "=".repeat(70) + "\n";
    private static final String SUBSECTION_SEPARATOR = "\n" + "-".repeat(70) + "\n";
    
    /**
     * Main method to demonstrate all streams concepts comprehensively.
     */
    public static void main(String[] args) {
        printHeader();

        // 1. BASIC STREAM OPERATIONS
        demonstrateBasics();

        // 2. FILTERING OPERATIONS
        demonstrateFiltering();

        // 3. TRANSFORMATION OPERATIONS
        demonstrateTransformations();

        // 4. TERMINAL OPERATIONS
        demonstrateTerminalOps();

        // 5. COLLECTORS & ADVANCED COLLECTION
        demonstrateCollectors();

        // 6. OPTIONAL PATTERNS
        demonstrateOptionals();

        // 7. PARALLEL STREAMS
        demonstrateParallel();

        // 8. ADVANCED PATTERNS
        demonstrateAdvanced();

        // 9. ELITE INTERVIEW TRAINING
        demonstrateEliteTraining();

        printFooter();
    }
    
    /**
     * Demonstrates basic stream creation and characteristics.
     */
    private static void demonstrateBasics() {
        System.out.println(SECTION_SEPARATOR);
        System.out.println("PART 1: BASIC STREAM OPERATIONS");
        System.out.println(SECTION_SEPARATOR);
        
        // Stream Interface Demo
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("1.1 Stream Interface & Creation Methods");
        System.out.println(SUBSECTION_SEPARATOR);
        StreamInterfaceDemo demo1 = new StreamInterfaceDemo();
        demo1.demonstrateStreamCreation();
        demo1.demonstrateStreamLaziness();
        demo1.demonstrateStreamCharacteristics();
        demo1.demonstrateSequentialVsParallel();
        demo1.demonstrateIteratorVsStream();
        demo1.demonstrateStreamReusability();
        demo1.demonstrateTerminalOperations();
        demo1.demonstrativePrimitiveStreams();
        demo1.demonstrateStreamBuilder();
        
        // ArrayList Stream Demo
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("1.2 ArrayList Stream Operations");
        System.out.println(SUBSECTION_SEPARATOR);
        ArrayListStreamDemo demo2 = new ArrayListStreamDemo();
        demo2.demonstrateFilteringAndCollecting();
        demo2.demonstrateStringStream();
        demo2.demonstrateCustomObjectStream();
        demo2.demonstrateDistinctAndSort();
        demo2.demonstrateLimitAndSkip();
        demo2.demonstrateReduction();
        demo2.demonstratePartitioning();
        demo2.demonstrateGrouping();
        demo2.demonstrateMinMax();
        
        // Peek Operations Demo
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("1.3 Peek Operations for Debugging");
        System.out.println(SUBSECTION_SEPARATOR);
        PeekOperationsDemo demo3 = new PeekOperationsDemo();
        demo3.demonstrateBasicPeek();
        demo3.demonstratePeekWithObjects();
        demo3.demonstrateMultiplePeeks();
        demo3.demonstratePeekForSideEffects();
        demo3.demonstrateLazyEvaluation();
    }
    
    /**
     * Demonstrates filtering operations on streams.
     */
    private static void demonstrateFiltering() {
        System.out.println(SECTION_SEPARATOR);
        System.out.println("PART 2: FILTERING OPERATIONS");
        System.out.println(SECTION_SEPARATOR);
        
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("2.1 Filter Operations");
        System.out.println(SUBSECTION_SEPARATOR);
        FilterOperationsDemo demo = new FilterOperationsDemo();
        demo.demonstrateBasicFiltering();
        demo.demonstrateComplexPredicates();
        demo.demonstrateObjectFiltering();
    }
    
    /**
     * Demonstrates transformation operations on streams.
     */
    private static void demonstrateTransformations() {
        System.out.println(SECTION_SEPARATOR);
        System.out.println("PART 3: TRANSFORMATION OPERATIONS");
        System.out.println(SECTION_SEPARATOR);
        
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("3.1 Map Operations");
        System.out.println(SUBSECTION_SEPARATOR);
        MapOperationsDemo demo1 = new MapOperationsDemo();
        demo1.demonstrateBasicMapping();
        demo1.demonstrateStringMapping();
        
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("3.2 FlatMap Operations");
        System.out.println(SUBSECTION_SEPARATOR);
        FlatMapOperationsDemo demo2 = new FlatMapOperationsDemo();
        demo2.demonstrateBasicFlatMap();
        demo2.demonstrateFlatMapWithStrings();
        demo2.demonstrateFlatMapWithObjects();
        demo2.demonstrateFlatMapVsMap();
        demo2.demonstrateFlatMapForCombinations();
        demo2.demonstrateNestedFlatMap();
    }
    
    /**
     * Demonstrates terminal operations on streams.
     */
    private static void demonstrateTerminalOps() {
        System.out.println(SECTION_SEPARATOR);
        System.out.println("PART 4: TERMINAL OPERATIONS");
        System.out.println(SECTION_SEPARATOR);
        
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("4.1 Collect Operations");
        System.out.println(SUBSECTION_SEPARATOR);
        CollectOperationsDemo demo = new CollectOperationsDemo();
        demo.demonstrateCollect();
    }
    
    /**
     * Demonstrates collector patterns and grouping operations.
     */
    private static void demonstrateCollectors() {
        System.out.println(SECTION_SEPARATOR);
        System.out.println("PART 5: COLLECTORS & ADVANCED GROUPING");
        System.out.println(SECTION_SEPARATOR);
        
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("5.1 Basic Collectors");
        System.out.println(SUBSECTION_SEPARATOR);
        CollectorExamplesDemo demo1 = new CollectorExamplesDemo();
        demo1.demonstrateCollectors();
        
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("5.2 Grouping Operations");
        System.out.println(SUBSECTION_SEPARATOR);
        GroupingByDemo demo2 = new GroupingByDemo();
        demo2.demonstrateGroupingBy();
        
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("5.3 Complex Collectors");
        System.out.println(SUBSECTION_SEPARATOR);
        ComplexCollectorsDemo demo3 = new ComplexCollectorsDemo();
        demo3.demonstrateComplexCollectors();
    }
    
    /**
     * Demonstrates Optional patterns and best practices.
     */
    private static void demonstrateOptionals() {
        System.out.println(SECTION_SEPARATOR);
        System.out.println("PART 6: OPTIONAL PATTERNS");
        System.out.println(SECTION_SEPARATOR);
        
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("6.1 Optional Patterns");
        System.out.println(SUBSECTION_SEPARATOR);
        OptionalPatternsDemo demo = new OptionalPatternsDemo();
        demo.demonstrateNullableVsOptional();
        demo.demonstrateOptionalChaining();
        demo.demonstrateOptionalFiltering();
        demo.demonstrateStreamOptionalOperations();
        demo.demonstrateOptionalDefaults();
        demo.demonstrateOptionalTransformations();
    }
    
    /**
     * Demonstrates parallel stream processing.
     */
    private static void demonstrateParallel() {
        System.out.println(SECTION_SEPARATOR);
        System.out.println("PART 7: PARALLEL STREAMS");
        System.out.println(SECTION_SEPARATOR);
        
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("7.1 Parallel Processing");
        System.out.println(SUBSECTION_SEPARATOR);
        ParallelStreamsDemo demo1 = new ParallelStreamsDemo();
        demo1.demonstrateParallelStreams();
        
        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("7.2 Performance Comparison");
        System.out.println(SUBSECTION_SEPARATOR);
        PerformanceComparisonDemo demo2 = new PerformanceComparisonDemo();
        demo2.comparePerformance();
    }
    
    /**
     * Demonstrates advanced stream patterns and techniques.
     */
    private static void demonstrateAdvanced() {
        System.out.println(SECTION_SEPARATOR);
        System.out.println("PART 8: ADVANCED PATTERNS");
        System.out.println(SECTION_SEPARATOR);

        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("8.1 Advanced Stream Patterns");
        System.out.println(SUBSECTION_SEPARATOR);
        if (classExists("com.learning.advanced.AdvancedStreamPatterns")) {
            try {
                AdvancedStreamPatterns demo = new AdvancedStreamPatterns();
                demo.demonstrateAdvancedPatterns();
            } catch (Exception e) {
                System.out.println("Skipping advanced patterns (class not fully implemented)");
            }
        }
    }

    /**
     * Demonstrates elite interview training problems.
     */
    private static void demonstrateEliteTraining() {
        System.out.println(SECTION_SEPARATOR);
        System.out.println("PART 9: ELITE INTERVIEW TRAINING - FAANG PREPARATION");
        System.out.println(SECTION_SEPARATOR);

        System.out.println(SUBSECTION_SEPARATOR);
        System.out.println("9.1 Elite Streams Problems (12 Advanced Problems)");
        System.out.println(SUBSECTION_SEPARATOR);

        try {
            EliteStreamsTraining.demonstrateAll();

            System.out.println("\n" + "=".repeat(70));
            System.out.println("🎓 ELITE TRAINING DEMONSTRATION COMPLETE!");
            System.out.println("=".repeat(70));
            System.out.println("\nYou have mastered:");
            System.out.println("  ✓ 12 advanced stream processing problems");
            System.out.println("  ✓ Top K frequent elements pattern");
            System.out.println("  ✓ Complex grouping and partitioning");
            System.out.println("  ✓ Custom collectors and statistics");
            System.out.println("  ✓ Parallel stream optimization");
            System.out.println("  ✓ Stream pipeline performance tuning");
            System.out.println("\nCompanies covered: Google, Amazon, Meta, Microsoft, Netflix, LinkedIn");
            System.out.println("Difficulty levels: Easy, Medium, Hard");
            System.out.println("=".repeat(70));
        } catch (Exception e) {
            System.out.println("Error running elite training: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper to check if a class exists.
     */
    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Prints the header message.
     */
    private static void printHeader() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║       ELITE STREAMS API LEARNING MODULE v3.0 - FAANG READY          ║");
        System.out.println("║                                                                    ║");
        System.out.println("║  This module covers:                                               ║");
        System.out.println("║  • Stream creation and characteristics                              ║");
        System.out.println("║  • Intermediate operations (filter, map, flatMap, peek)             ║");
        System.out.println("║  • Terminal operations (collect, reduce, forEach, match)            ║");
        System.out.println("║  • Collectors and grouping operations                               ║");
        System.out.println("║  • Optional patterns and best practices                             ║");
        System.out.println("║  • Parallel stream processing                                       ║");
        System.out.println("║  • Advanced patterns and performance optimization                   ║");
        System.out.println("║  • 12 ELITE INTERVIEW PROBLEMS from Google, Amazon, Meta, etc.      ║");
        System.out.println("║                                                                    ║");
        System.out.println("║  Total Classes: 17 demonstration + 15 test classes                  ║");
        System.out.println("║  Test Coverage Target: 80%+ with 198+ test methods                  ║");
        System.out.println("║                                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
    }
    
    /**
     * Prints the footer message.
     */
    private static void printFooter() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    MODULE DEMONSTRATION COMPLETE                    ║");
        System.out.println("║                                                                    ║");
        System.out.println("║  🎓 You have completed:                                             ║");
        System.out.println("║  ✓ How to create streams from various sources                       ║");
        System.out.println("║  ✓ How intermediate operations work (lazy evaluation)                ║");
        System.out.println("║  ✓ How to use terminal operations to produce results                ║");
        System.out.println("║  ✓ Advanced collectors and grouping patterns                        ║");
        System.out.println("║  ✓ Proper Optional usage and patterns                               ║");
        System.out.println("║  ✓ When and how to use parallel streams                             ║");
        System.out.println("║  ✓ 12 Elite interview problems from FAANG companies                 ║");
        System.out.println("║  ✓ Custom collectors and stream optimization                        ║");
        System.out.println("║                                                                    ║");
        System.out.println("║  🚀 Next Steps:                                                     ║");
        System.out.println("║  1. Practice all 12 elite problems on your own                      ║");
        System.out.println("║  2. Review EliteStreamsTrainingTest for edge cases                  ║");
        System.out.println("║  3. Study time/space complexity for each solution                   ║");
        System.out.println("║  4. Prepare for behavioral questions about streams                  ║");
        System.out.println("║  5. Move to Module 05: Concurrency & Multithreading                 ║");
        System.out.println("║                                                                    ║");
        System.out.println("║  You are now ready for FAANG-level Streams API interviews! 🎉       ║");
        System.out.println("║                                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
    }
}
