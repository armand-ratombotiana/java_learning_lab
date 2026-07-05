package com.learning.lab20;

/**
 * Main entry point for Lab 20: Reflection.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("   Lab 20: Reflection");
        System.out.println("========================================\n");

        ClassForNameExample.showReflectionBasics();
        System.out.println();
        MethodInvokeExample.showInvocation();
        System.out.println();
        DynamicProxyExample.showDynamicProxy();

        System.out.println("\n========================================");
        System.out.println("   Lab 20 Complete");
        System.out.println("========================================");
    }
}
