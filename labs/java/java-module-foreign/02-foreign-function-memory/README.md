# Lab 02: Foreign Function & Memory (FFM) API

## Overview
The Foreign Function & Memory API (FFM, finalized in Java 22) replaces JNI with a pure-Java mechanism for calling native code and managing off-heap memory. This lab covers `MemorySegment`, `Arena`, `Linker`, and foreign function calls.

## Goals
- Allocate and manage off-heap memory with `Arena` and `MemorySegment`
- Call native C functions using `Linker` and `SymbolLookup`
- Work with structs and pointers via `MemoryLayout`
- Understand lifecycle management with `Arena` scopes
- Recognize when to use FFM vs JNI vs JNA

## Prerequisites
- Java 22+
- Basic understanding of C data types and pointers
- Familiarity with `java.lang.foreign` module
