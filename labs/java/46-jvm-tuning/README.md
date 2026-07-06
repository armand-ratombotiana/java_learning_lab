# JVM Tuning & Optimization — Overview

This lab explores JVM tuning: heap sizing (-Xms, -Xmx, -Xmn, -XX:NewRatio), code cache, Metaspace, large pages, NUMA, compiler tuning, string deduplication, and -XX flag analysis.

## Learning Objectives
- Understand heap sizing and its impact on GC and throughput
- Measure code cache usage and its effect on JIT compilation
- Monitor Metaspace growth with dynamic class loading
- Evaluate string deduplication memory savings
- Use ManagementFactory and RuntimeMXBean to inspect JVM configuration

## Prerequisites
- Java 21+
- Completion of GC Deep Dive (Lab 45) recommended
- Understanding of JIT compilation (Lab 44) helpful

## Files in This Lab
- Java sources: `src/main/java/com/javaacademy/lab46/jvm/`
- Tests: `src/test/java/com/javaacademy/lab46/jvm/`
- 24 documentation .md files covering JVM tuning theory and practice
