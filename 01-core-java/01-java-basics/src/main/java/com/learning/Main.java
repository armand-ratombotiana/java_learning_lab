package com.learning;

/**
 * Main class demonstrating Java Basics concepts.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Main {
    
    /**
     * Main entry point for the application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("=== Java Basics Module ===\n");
        
        // Variables Demo
        System.out.println("1. Variables Demo:");
        Variables.demonstrateVariables();
        
        // Data Types Demo
        System.out.println("\n2. Data Types Demo:");
        DataTypes.demonstrateDataTypes();
        
        // Operators Demo
        System.out.println("\n3. Operators Demo:");
        Operators.demonstrateOperators();
        
        // Control Flow Demo
        System.out.println("\n4. Control Flow Demo:");
        ControlFlow.demonstrateControlFlow();
        
        // Arrays Demo
        System.out.println("\n5. Arrays Demo:");
        ArraysDemo.demonstrateArrays();
        
        // Strings Demo
        System.out.println("\n6. Strings Demo:");
        StringsDemo.demonstrateStrings();
        
        // Methods Demo
        System.out.println("\n7. Methods Demo:");
        MethodsDemo.demonstrateMethods();
        
        // Exceptions Demo
        System.out.println("\n8. Exceptions Demo:");
        ExceptionsDemo.demonstrateExceptions();
        
        // Elite Exercises (Interview Preparation)
        System.out.println("\n9. Elite Interview Exercises:");
        EliteExercises.demonstrateEliteExercises();

        // Elite Training (Hands-on Coding Exercises)
        System.out.println("\n10. Elite Training - Practical Exercises:");
        EliteTraining.demonstrateEliteTraining();

        System.out.println("\n\n=== Java Basics Module Complete ===");
        System.out.println("All demonstrations executed successfully!");
        System.out.println("\n🎓 You've completed:");
        System.out.println("   • Core Java Basics Concepts");
        System.out.println("   • 18+ Interview Questions with Solutions");
        System.out.println("   • 13+ Hands-on Coding Exercises");
        System.out.println("   • FAANG-level Interview Preparation");
        System.out.println("\n🚀 Next: Practice Module 02 - OOP Concepts!");
    }
}