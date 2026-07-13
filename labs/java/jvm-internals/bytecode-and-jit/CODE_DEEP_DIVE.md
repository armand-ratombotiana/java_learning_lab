# Bytecode and JIT Code Deep Dive

This lab demonstrates how to write code that triggers the JIT compiler, and how to use JVM flags to observe the compilation process in real-time.

## 💻 Simulating JIT Warmup

```java file="labs/java/jvm-internals/bytecode-and-jit/SOLUTION/JitWarmupDemo.java"
package java.jvminternals.jit;

/**
 * Demonstrates JIT Compilation and Method Inlining.
 * 
 * Run with the following JVM flags to observe JIT activity:
 * java -XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining JitWarmupDemo
 */
public class JitWarmupDemo {

    // A simple method that will be heavily inlined
    private static int add(int a, int b) {
        return a + b;
    }

    // A method that does some work, which will become a "Hot Spot"
    private static int compute(int iterations) {
        int sum = 0;
        for (int i = 0; i < iterations; i++) {
            // The JIT will inline the add() method here to avoid method call overhead
            sum = add(sum, i); 
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("Starting JIT Warmup Demonstration...");
        
        long startTime = System.nanoTime();

        // Phase 1: The Interpreter (Cold Code)
        // We run the method a few times. The JVM interprets the bytecode.
        for (int i = 0; i < 100; i++) {
            compute(1000);
        }
        long phase1Time = System.nanoTime() - startTime;
        System.out.printf("Phase 1 (Interpreted) took: %,d ns%n", phase1Time);

        // Phase 2: Triggering C1 and C2 Compilation (Warming Up)
        // We run the method 20,000 times. This crosses the JVM's compilation thresholds.
        // Tier 3 (C1) usually triggers around 2,000 invocations.
        // Tier 4 (C2) usually triggers around 10,000 invocations.
        startTime = System.nanoTime();
        for (int i = 0; i < 20_000; i++) {
            compute(1000);
        }
        long phase2Time = System.nanoTime() - startTime;
        System.out.printf("Phase 2 (Warming Up) took:  %,d ns%n", phase2Time);

        // Phase 3: Executing Highly Optimized Machine Code (Hot Code)
        // The method is now fully compiled by C2.
        startTime = System.nanoTime();
        for (int i = 0; i < 20_000; i++) {
            compute(1000);
        }
        long phase3Time = System.nanoTime() - startTime;
        System.out.printf("Phase 3 (Optimized) took:   %,d ns%n", phase3Time);
        
        System.out.println("\nNotice how Phase 3 is significantly faster than Phase 2, despite doing the exact same amount of work!");
    }
}
```

## 🔍 Key Takeaways
1. **The Warmup Penalty**: If you run this code, you will see that Phase 2 takes significantly longer per iteration than Phase 3. This is because during Phase 2, the JVM is spending CPU cycles *compiling* the code in the background, stealing resources from the application. Once Phase 3 hits, the compilation is done, and the code runs at native speed.
2. **Analyzing the Logs**: If you run the code with `-XX:+PrintCompilation`, you will see output like this:
   `   123   45       3       java.jvminternals.jit.JitWarmupDemo::compute (24 bytes)`
   `   125   48       4       java.jvminternals.jit.JitWarmupDemo::compute (24 bytes)`
   - The `3` indicates the C1 compiler (Tier 3) compiled the method.
   - A few milliseconds later, the `4` indicates the C2 compiler (Tier 4) took over and applied maximum optimization.
3. **Inlining Logs**: If you add `-XX:+PrintInlining`, you will see a log entry stating that `JitWarmupDemo::add` was `inlined` into `compute`. The JVM completely deleted the `add` method call and replaced it with a simple native addition instruction inside the loop.