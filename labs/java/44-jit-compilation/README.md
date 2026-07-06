# JIT Compilation Deep Dive — Overview

This lab explores Just-In-Time (JIT) compilation in the JVM: tiered compilation (C1/C2), method inlining, intrinsics, escape analysis, scalar replacement, and deoptimization.

## Learning Objectives
- Understand C1/C2 tiered compilation and compilation thresholds
- Explain how the JIT inlines methods (MaxInlineSize, InlineFrequency)
- Identify JVM intrinsics (System.arraycopy, Math functions)
- Analyze escape analysis and scalar replacement effects
- Trigger and observe deoptimization from polymorphic call sites

## Prerequisites
- Java 21+
- Basic understanding of bytecode (Lab 43 is recommended)
- Familiarity with JVM command-line flags

## Files in This Lab
- Java sources: `src/main/java/com/javaacademy/lab44/jit/`
- Tests: `src/test/java/com/javaacademy/lab44/jit/`
- 24 documentation .md files covering JIT compilation in depth
