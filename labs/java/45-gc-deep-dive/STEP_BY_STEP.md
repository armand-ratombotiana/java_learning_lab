# Step-by-Step: Garbage Collection

## Step 1: Run GcComparisonDemo.java
Execute and observe the GC collector names and statistics. The demo allocates 100 MB and reports collection counts. Run with different GC flags:
```bash
java -XX:+UseG1GC -jar lab45-gc-deep-dive.jar
java -XX:+UseZGC -jar lab45-gc-deep-dive.jar
java -XX:+UseParallelGC -jar lab45-gc-deep-dive.jar
```

## Step 2: Run G1GcDemo.java
Observe the G1 allocation pattern. The demo allocates 50,000 objects with 10% retention. Run with `-XX:+PrintGCDetails` to see G1 region activity: `-Xlog:gc*`.

## Step 3: Run ZGcDemo.java
Observe the ZGC allocation pattern with 20 cycles of short-lived objects. Run with ZGC:
```bash
java -XX:+UseZGC -Xlog:gc* -jar lab45-gc-deep-dive.jar
```
Note the "Concurrent Mark" and "Concurrent Relocate" phases.

## Step 4: Run GcRootExample.java
Observe how GC roots keep objects alive. After `clear()` and `null` assignments, objects become unreachable. Run with `-XX:+PrintGCDetails` to see collection events.

## Step 5: Run GcLoggingExample.java
Observe programmatic GC logging. The listener captures GC events and prints them. This demonstrates how to monitor GC in a live system without parsing log files.

## Step 6: Experiment with GC Logging Flags
```bash
# Basic GC logging
java -Xlog:gc* -jar lab45-gc-deep-dive.jar
# Detailed G1 logging
java -Xlog:gc* -XX:+UseG1GC -jar lab45-gc-deep-dive.jar
# ZGC logging
java -Xlog:gc* -XX:+UseZGC -jar lab45-gc-deep-dive.jar
```

## Step 7: Run GcDeepDiveTest.java
Execute all JUnit tests to verify GC demonstration code works correctly.
