# WebAssembly with Java

## Overview
WebAssembly (WASM) is a binary instruction format that runs in browsers and standalone runtimes, enabling high-performance code execution.

## Key Features
- Binary format, near-native performance
- Browser and standalone execution
- Language interop (C, Rust, JS, Java)
- TeaVM, CheerpJ, GraalVM WASM

## Project Structure
```
59-webassembly/
  wasm-java/
    src/main/java/com/learning/wasm/WebAssemblyLab.java
```

## Running
```bash
cd 59-webassembly/wasm-java
mvn compile exec:java
```

## Concepts Covered
- WASM compilation (TeaVM)
- Browser WASM execution
- GraalVM WASM support

## Dependencies
- Java WASM runtimes