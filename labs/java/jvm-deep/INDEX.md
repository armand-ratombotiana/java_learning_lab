# Jvm Deep - Micro-Labs

## Overview
This module contains 10 atomic deep-dive micro-labs.

## Lab Index
| # | Lab | Description |
|---|-----|-------------|
| 01 | [Class File Format](./01-class-file-format/) | Magic number, constant pool, field/method/attribute tables, Code attribute, LineNumberTable |
| 02 | [Class Loading](./02-class-loading/) | Loading/linking/initialization, bootstrap/extension/application classloaders, custom classloader |
| 03 | [Bytecode Introduction](./03-bytecode-introduction/) | JVM instruction set, iconst/bi/push/sipush/ldc, aload/astore, invokevirtual/invokespecial/invokeinterface/invokestatic/invokedynamic |
| 04 | [ASM Bytecode](./04-asm-bytecode/) | ASM Core API vs Tree API, ClassReader/ClassWriter/ClassVisitor, MethodVisitor, advice adapter, code weaving |
| 05 | [Method Handles](./05-method-handles/) | MethodHandle vs Reflection benchmarks, invokedynamic bootstrap, method type, call site |
| 06 | [JIT Compilation](./06-jit-compilation/) | C1 vs C2 pipelines, compilation thresholds (10000/100000), tiered compilation levels 0-4, OSR, uncommon trap |
| 07 | [G1 Garbage Collector](./07-gc-1-g1/) | G1 region design, SATB, remembered sets/PRT, evacuation pause, concurrent marking, mixed GC, humongous objects |
| 08 | [ZGC](./08-gc-2-zgc/) | ZGC colored pointers, multi-mapping, load barriers, concurrent compaction, generational ZGC, NUMA-aware allocation |
| 09 | [Shenandoah GC](./09-gc-3-shenandoah/) | Shenandoah Brooks forwarding pointers, concurrent evacuation, update refs, SATB vs Shenandoah |
| 10 | [JVM Tuning](./10-jvm-tuning/) | Heap sizing formulas, survivor ratio, TLAB sizing, code cache, Metaspace, Compressed OOPs, large pages, GC flags |

## How to Use
Each micro-lab is self-contained with 24 markdown files, Java source code,
JUnit 5 tests, and 7 subdirectories for hands-on work.
Start from lab 01 and progress sequentially.