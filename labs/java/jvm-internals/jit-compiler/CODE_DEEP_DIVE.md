# JIT Compiler Code Deep Dive

This lab demonstrates how to write code that triggers the JIT's most powerful optimization: **Method Inlining**, and how to verify it using JVM diagnostic flags.

## 💻 Verifying Inlining with JVM Flags

To see the JIT in action, we need to run a method enough times to make it "Hot".

```java file="labs/java/jvm-internals/jit-compiler/SOLUTION/InliningDemo.java"
package java.jvminternals.jit;

/**
 * Demonstrates JIT Method Inlining.
 * 
 * Run with:
 * java -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining -XX:+PrintCompilation InliningDemo
 */
public class InliningDemo {

    private static int add(int a, int b) {
        return a + b;
    }

    private static int compute(int iterations) {
        int sum = 0;
        for (int i = 0; i < iterations; i++) {
            // The JIT will likely inline this small method
            sum = add(sum, i);
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("Warming up the JVM...");

        // Run enough times to trigger C2 compilation (usually 10,000+)
        for (int i = 0; i < 20_000; i++) {
            compute(1000);
        }

        System.out.println("Warmup complete. Check the console logs for 'inlined' messages.");
    }
}
```

## 🔍 How to Read the Logs
When you run the command above, look for lines in the output like this:

`   123   45       4       java.jvminternals.jit.InliningDemo::compute (24 bytes)`

Then, look for the diagnostic inlining block:
```text
@ 12   java.jvminternals.jit.InliningDemo::add (4 bytes)   inline (hot)
```
- `@ 12`: The bytecode offset inside `compute` where the call to `add` was found.
- `inline (hot)`: Confirms that the JIT successfully deleted the method call and replaced it with the logic of `add`.

## 🚀 Performance Impact
Inlining is the "gateway" optimization. Once `add` is inlined into `compute`, the JIT can see the entire loop as one unit. It can then apply **Loop Unrolling** or **Auto-Vectorization** (using SIMD instructions) to make the calculation even faster. Without inlining, those optimizations would be impossible because the method call acts as a "black box" to the compiler.