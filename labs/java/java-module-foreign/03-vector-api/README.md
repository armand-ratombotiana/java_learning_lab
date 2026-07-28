# Lab 03: Vector API (SIMD Operations)

## Overview
The Vector API (incubator in Java 16–21, finalized in Java 22) provides a portable API for expressing vector computations that the JIT compiles to SIMD instructions (AVX, SVE, NEON). This lab covers vector operations, species, and performance considerations.

## Goals
- Use `VectorSpecies` and `Vector` types
- Perform element-wise vector operations
- Apply vector reductions (sum, min, max)
- Write masked vector operations
- Understand performance characteristics vs scalar loops

## Prerequisites
- Java 22+
- Understanding of SIMD concepts
- Familiarity with `jdk.incubator.vector` module
