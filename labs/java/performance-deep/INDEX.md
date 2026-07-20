# Performance Deep - Micro-Labs

## Overview
This module contains 5 atomic deep-dive micro-labs.

## Lab Index
| # | Lab | Description |
|---|-----|-------------|
| 01 | [Profiling with async-profiler](./01-profiling-async/) | async-profiler CPU/wall-clock/allocation profiling, flame graph generation, frame folding, perf_events |
| 02 | [JMH Benchmarks](./02-jmh-benchmarks/) | JMH annotation-based/fluent API, warmup/measurement iterations, Blackhole, fork/thread params, profilers |
| 03 | [JFR Streaming](./03-jfr-streaming/) | JFR custom events, jdk.jfr.Event, streaming API, FlightRecorderMXBean, JMC template XML |
| 04 | [Memory Leaks](./04-memory-leaks/) | ClassLoader leak, ThreadLocal leak, String intern, direct buffer, finalizer, lambda-capturing-this, GC root analysis |
| 05 | [GC Log Analysis](./05-gc-log-analysis/) | GC log flags (-Xlog:gc*), G1/ZGC/Shenandoah log parsing, pause time analysis, allocation rate, promotion |

## How to Use
Each micro-lab is self-contained with 24 markdown files, Java source code,
JUnit 5 tests, and 7 subdirectories for hands-on work.
Start from lab 01 and progress sequentially.