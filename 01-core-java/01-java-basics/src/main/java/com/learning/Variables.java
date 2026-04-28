package com.learning;

/**
 * Demonstrates Java variable concepts including declaration, initialization,
 * and scope.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Variables {
    
    // Instance variables
    private int instanceVar = 100;
    private String instanceString = "Instance Variable";
    
    // Static/Class variables
    private static int staticVar = 200;
    private static final String CONSTANT = "CONSTANT_VALUE";
    
    /**
     * Demonstrates various types of variables in Java.
     */
    public static void demonstrateVariables() {
        // Local variables
        int localVar = 10;
        String localString = "Local Variable";
        
        System.out.println("Local Variable: " + localVar);
        System.out.println("Local String: " + localString);
        System.out.println("Static Variable: " + staticVar);
        System.out.println("Constant: " + CONSTANT);
        
        // Variable scope demonstration
        demonstrateScope();
        
        // Variable naming conventions
        demonstrateNamingConventions();
    }
    
    /**
     * Demonstrates variable scope in Java.
     */
    private static void demonstrateScope() {
        int outerScope = 1;
        
        if (true) {
            int innerScope = 2;
            System.out.println("Inner scope can access outer: " + outerScope);
            System.out.println("Inner scope variable: " + innerScope);
        }
        
        // innerScope is not accessible here
        System.out.println("Outer scope variable: " + outerScope);
    }
    
    /**
     * Demonstrates Java naming conventions for variables.
     */
    private static void demonstrateNamingConventions() {
        // camelCase for variables
        int studentAge = 20;
        String firstName = "John";
        
        // UPPER_CASE for constants
        final double PI = 3.14159;
        final int MAX_SIZE = 100;
        
        System.out.println("Student Age: " + studentAge);
        System.out.println("First Name: " + firstName);
        System.out.println("PI: " + PI);
        System.out.println("Max Size: " + MAX_SIZE);
    }
    
    /**
     * Gets the instance variable value.
     * 
     * @return the instance variable value
     */
    public int getInstanceVar() {
        return instanceVar;
    }
    
    /**
     * Sets the instance variable value.
     * 
     * @param instanceVar the new value
     */
    public void setInstanceVar(int instanceVar) {
        this.instanceVar = instanceVar;
    }
    
    /**
     * Gets the instance string value.
     * 
     * @return the instance string value
     */
    public String getInstanceString() {
        return instanceString;
    }
    
    /**
     * Gets the static variable value.
     * 
     * @return the static variable value
     */
    public static int getStaticVar() {
        return staticVar;
    }
    
    /**
     * Gets the constant value.
     * 
     * @return the constant value
     */
    public static String getConstant() {
        return CONSTANT;
    }
}