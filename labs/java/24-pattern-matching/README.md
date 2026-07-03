# Lab 24: Pattern Matching in Java

## Overview

Pattern matching is a transformative feature in modern Java that allows you to test whether a value has a certain shape or type, extract data from it, and bind variables — all in one concise step. Starting with `instanceof` pattern matching in Java 16 and evolving through switch expressions in Java 17-21, pattern matching fundamentally changes how Java developers write conditional logic.

### Key Concepts Covered

- Pattern matching for `instanceof` (Java 16)
- Switch expressions with type patterns (Java 17 preview, 21 finalized)
- Record patterns and nested deconstruction (Java 21)
- Guarded patterns with `when` clauses
- Pattern dominance and exhaustiveness
- Combining sealed classes with pattern matching

### Prerequisites

- Java 21+ JDK recommended
- Understanding of records (Lab 22) and sealed classes (Lab 23)
- Familiarity with traditional switch statements and instanceof

### What You'll Learn

By the end of this lab, you'll be able to use pattern matching to write safer, more expressive, and more concise code. You'll understand how to combine type patterns, record patterns, guards, and sealed hierarchies for exhaustive data processing.

Let's explore pattern matching in depth.
