package com.learning.wasm;

public class WebAssemblyLab {

    public static void main(String[] args) {
        System.out.println("=== WebAssembly with Java Lab ===\n");

        System.out.println("1. WASM Concept:");
        System.out.println("   - WebAssembly is a binary instruction format");
        System.out.println("   - Runs in browsers and standalone runtimes");
        System.out.println("   - Compiled from C/C++, Rust, AssemblyScript, etc.");

        System.out.println("\n2. Java WASM Runtimes:");
        System.out.println("   - TeaVM: Java bytecode to WebAssembly compiler");
        System.out.println("   - CheerpJ: JVM in the browser via WASM");
        System.out.println("   - GraalVM: Polyglot with WASM component");

        System.out.println("\n3. Example:");
        System.out.println("   Factorial(5) = " + factorial(5));
        System.out.println("   Fibonacci(10) = " + fibonacci(10));

        System.out.println("\n=== WebAssembly Lab Complete ===");
    }

    static int factorial(int n) { return (n <= 1) ? 1 : n * factorial(n - 1); }
    static int fibonacci(int n) {
        if (n <= 1) return n;
        int a = 0, b = 1;
        for (int i = 2; i <= n; i++) { int t = a + b; a = b; b = t; }
        return b;
    }
}