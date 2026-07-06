# Garbage Collection Deep Dive — Overview

This lab explores Java garbage collection internals: collector algorithms (Serial, Parallel, G1, ZGC, Shenandoah), GC phases, G1 remembered sets/SATB, ZGC colored pointers/load barriers, and GC tuning and log analysis.

## Learning Objectives
- Compare all JVM GC collectors and their performance characteristics
- Understand G1 region-based allocation, remembered sets, and SATB marking
- Explain ZGC colored pointers and load barriers for concurrent compaction
- Identify GC roots and their role in reachability analysis
- Programmatically access GC metrics via ManagementFactory

## Prerequisites
- Java 21+
- Understanding of memory allocation and heap structure
- Familiarity with the JVM memory model

## Files in This Lab
- Java sources: `src/main/java/com/javaacademy/lab45/gc/`
- Tests: `src/test/java/com/javaacademy/lab45/gc/`
- 24 documentation .md files covering GC theory and practice
