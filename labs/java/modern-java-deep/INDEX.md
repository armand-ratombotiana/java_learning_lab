# Modern Java Deep - Micro-Labs

## Overview
This module contains 5 atomic deep-dive micro-labs.

## Lab Index
| # | Lab | Description |
|---|-----|-------------|
| 01 | [Records Deep Dive](./01-records-deep/) | Record components, canonical/compact/custom constructors, serialization, pattern matching with records, local records |
| 02 | [Sealed Classes](./02-sealed-classes/) | Sealed interface/class, permits clause, exhaustive switch, sealed + records = algebraic data types |
| 03 | [Pattern Matching](./03-pattern-matching/) | Type patterns, record patterns, nested patterns, guarded patterns, switch expression exhaustiveness |
| 04 | [Virtual Threads](./04-virtual-threads/) | Carrier threads, mount/unmount, pinning (synchronized), yield points, thread confinement |
| 05 | [Structured Concurrency 2](./05-structured-concurrency-2/) | StructuredTaskScope internals, ShutdownOnSuccess/Failure, scope hierarchy, JEP 462 |

## How to Use
Each micro-lab is self-contained with 24 markdown files, Java source code,
JUnit 5 tests, and 7 subdirectories for hands-on work.
Start from lab 01 and progress sequentially.