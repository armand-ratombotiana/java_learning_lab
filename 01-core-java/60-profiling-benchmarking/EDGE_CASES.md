# Edge Cases & Pitfalls: Profiling & Benchmarking

Benchmarking Java code is fraught with compiler optimizations that can entirely invalidate your results. If you don't account for these, you will optimize the wrong code.

## 1. Dead Code Elimination (DCE) Trap
*   **The Scenario**: You write a JMH benchmark to test the speed of `Math.log(x)`.
    ```java
    @Benchmark
    public void testMath() {
        double x = Math.PI;
        Math.log(x); // Result is not used!
    }
    ```
*   **The Pitfall**: The JIT compiler realizes that the result of `Math.log(x)` is never used, and the method has no side effects. It will literally delete the method call from the compiled machine code. JMH will report that the method takes 0 nanoseconds to execute. You have successfully benchmarked the speed of doing absolutely nothing.
*   **Mitigation**: You MUST consume the result of your computations. Either return the value from the `@Benchmark` method, or pass it into a `Blackhole` object provided by JMH (`blackhole.consume(Math.log(x))`).

## 2. Constant Folding
*   **The Scenario**: You try to fix the DCE trap above by returning the value.
    ```java
    @Benchmark
    public double testMath() {
        return Math.log(Math.PI); 
    }
    ```
*   **The Pitfall**: Because `Math.PI` is a constant, the JIT compiler knows the result of `Math.log(Math.PI)` will *always* be the same. It calculates the answer once during compilation and replaces the method call with the hardcoded result (e.g., `return 1.144;`). You are now benchmarking the speed of returning a constant, not the speed of the math operation.
*   **Mitigation**: Use `@State` variables to feed data into your benchmark. The JIT cannot assume the state variable will remain constant across invocations, forcing it to actually execute the math operation.

## 3. Coordinated Omission (Load Testing)
*   **The Scenario**: You use a load testing tool (like JMeter or Gatling) to hit your server with 1,000 requests per second. The server pauses for a 5-second Garbage Collection cycle.
*   **The Pitfall**: During that 5-second pause, the server stops responding. Your load testing tool, waiting for responses before sending the next batch, *also* pauses. It stops sending 1,000 req/sec. When the server recovers, the tool reports that the 99th percentile latency is fine, because it completely failed to measure the requests that *should* have been sent during the 5-second blackout. This masks massive latency spikes.
*   **Mitigation**: Use load testing tools designed to prevent Coordinated Omission (like `wrk2` or specialized configurations in Gatling) that generate load at a fixed rate regardless of whether the server is responding.

## 4. Profiler Overhead (The Observer Effect)
*   **The Scenario**: You attach a traditional sampling profiler (like VisualVM) to a production server to find a CPU bottleneck.
*   **The Pitfall**: Traditional profilers often require the JVM to reach a "safepoint" to collect a stack trace. If you sample the stacks 1,000 times a second, you are constantly freezing the JVM to take a picture of it. The act of observing the system drastically degrades its performance, and the resulting profile might show that the JVM is spending most of its time managing the profiler itself!
*   **Mitigation**: Use low-overhead, asynchronous profilers like **JFR (JDK Flight Recorder)** or **async-profiler**. These tools collect data without requiring global JVM safepoints, keeping overhead below 1%.

## 5. Loop Unrolling Illusions
*   **The Scenario**: You write a microbenchmark with a `for` loop inside the `@Benchmark` method to "do more work" per invocation.
    ```java
    @Benchmark
    public int testLoop() {
        int sum = 0;
        for (int i=0; i<10; i++) sum += i;
        return sum;
    }
    ```
*   **The Pitfall**: The JIT compiler will unroll small loops. It will rewrite the bytecode to `sum = 0 + 1 + 2 + 3... + 9;`. This drastically changes the execution profile compared to a loop of 10,000 items. 
*   **Mitigation**: Let JMH handle the looping. Write your benchmark to perform exactly *one* operation, and let JMH invoke that method millions of times.