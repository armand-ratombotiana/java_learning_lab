package com.learning.webassembly;

import java.util.*;
import java.util.function.*;

public class Lab {

    static class WasmModule {
        private final String name;
        private final Map<String, Function<List<Number>, Number>> functions = new LinkedHashMap<>();
        private final Map<String, Number> globals = new LinkedHashMap<>();
        private final Map<String, byte[]> memories = new LinkedHashMap<>();

        WasmModule(String name) { this.name = name; }

        WasmModule addFunction(String name, Function<List<Number>, Number> fn) {
            functions.put(name, fn);
            return this;
        }

        WasmModule addGlobal(String name, Number value) {
            globals.put(name, value);
            return this;
        }

        Number call(String functionName, Number... args) {
            var fn = functions.get(functionName);
            if (fn == null) throw new IllegalArgumentException("Function not found: " + functionName);
            return fn.apply(List.of(args));
        }

        void setMemory(String name, byte[] data) { memories.put(name, data); }
        byte[] getMemory(String name) { return memories.get(name); }
        Number getGlobal(String name) { return globals.get(name); }

        void printExports() {
            System.out.println("  Exports from " + name + ":");
            functions.keySet().forEach(f -> System.out.println("    func " + f + "(...) -> number"));
            globals.keySet().forEach(g -> System.out.println("    global " + g + " = " + globals.get(g)));
            memories.keySet().forEach(m -> System.out.println("    memory " + m + " (" + memories.get(m).length + " bytes)"));
        }
    }

    public static void main(String[] args) {
        System.out.println("=== WebAssembly Lab ===\n");

        wasmModuleModel();
        compilerTargets();
        wasiInterface();
        javaToWasm();
        useCases();
    }

    static void wasmModuleModel() {
        System.out.println("--- WebAssembly Module Model ---");
        var math = new WasmModule("math-utils")
            .addGlobal("PI", 3.14159)
            .addFunction("add", args -> args.get(0).intValue() + args.get(1).intValue())
            .addFunction("mul", args -> args.get(0).intValue() * args.get(1).intValue())
            .addFunction("factorial", args -> {
                int n = args.get(0).intValue();
                long r = 1;
                for (int i = 2; i <= n; i++) r *= i;
                return r;
            });

        math.setMemory("linear", new byte[65536]);
        math.printExports();

        System.out.println("\n  Execution:");
        System.out.println("    add(3, 4) = " + math.call("add", 3, 4));
        System.out.println("    mul(5, 6) = " + math.call("mul", 5, 6));
        System.out.println("    factorial(10) = " + math.call("factorial", 10));

        System.out.println("""
  Wasm is a stack-based virtual machine:
  i32.const 3
  i32.const 4
  i32.add            -> stack: [7]
  local.get/set, global.get/set, memory.load/store
    """);
    }

    static void compilerTargets() {
        System.out.println("\n--- Compiler Targets ---");
        System.out.println("""
  Languages that compile to WebAssembly:

  C/C++:     Emscripten (LLVM backend) - most mature
  Rust:      wasm-pack, wasm-bindgen - excellent support
  Go:        Go 1.21+ supports wasm (js/wasm, wasip1/wasm)
  Kotlin:    Kotlin/Wasm (experimental, targeting WasmGC)
  Java:      TeaVM, JWebAssembly (bytecode -> wasm)
  .NET:      Blazor WebAssembly (Mono runtime)
  AssemblyScript: TypeScript-like syntax

  Wasm binary format:
  Magic: \\0asm
  Version: 1
  Sections: Type, Function, Code, Memory, Export, Import...
    """);
    }

    static void wasiInterface() {
        System.out.println("\n--- WASI (WebAssembly System Interface) ---");
        System.out.println("""
  WASI provides OS-like capabilities to Wasm modules:
  - File I/O (open, read, write, close)
  - Environment variables
  - Clock / time
  - Random numbers
  - Networking (future: wasi-sockets)

  WASI layers:
  wasi-libc:     POSIX-like API for C programs
  wasi-nn:       Neural network inference
  wasi-crypto:   Cryptographic operations
  wasi-http:     HTTP requests/responses

  Runtime support:
  Wasmtime, Wasmer, WAMR, WasmEdge
  Node.js: experimental wasi support
  Browser: WASI polyfill available

  Example: wasirun my-module.wasm arg1 arg2
    """);
    }

    static void javaToWasm() {
        System.out.println("\n--- Java to WebAssembly ---");
        System.out.println("""
  Approaches:

  1. TeaVM: Java bytecode -> JavaScript/Wasm
     - Static compilation (no reflection)
     - Supports subset of Java standard library
     - Good for small to medium apps

  2. JWebAssembly: bytecode -> .wasm
     - Direct translation
     - Garbage collection via wasm-gc

  3. CheerpJ: Java applet -> Wasm (legacy migration)
     - Full JVM in Wasm (heavy)
     - AOT + JIT compilation

  4. GraalVM Wasm: run Wasm modules inside JVM
     - WasmEngine, Interop API
     - Call Wasm functions from Java

  Challenges:
  - GC: WasmGC extension required for Java heap
  - Exception handling: Wasm exception handling proposal
  - Threading: Wasm threads proposal
  - Reflection: limited in AOT-compiled Java
    """);
    }

    static void useCases() {
        System.out.println("\n--- WebAssembly Use Cases ---");
        System.out.println("""
  In the browser:
  - Video/image editing (Figma, Adobe)
  - Games (Unity, Unreal Engine)
  - Real-time communication (Zoom, WebRTC codecs)
  - 3D rendering (CAD, modeling)

  On the server:
  - Edge computing (Fastly Compute@Edge, Cloudflare Workers)
  - Plugin systems (extensible sandboxed code)
  - Serverless functions (Fermyon Spin)
  - Database UDFs (user-defined functions in SQLite)

  In IoT / embedded:
  - Plug-in systems for constrained devices
  - Safe code execution (sandboxed by default)
  - Cross-platform binary portability

  Benefits over containers:
  - Faster startup (nanoseconds vs milliseconds)
  - Smaller binary (KB vs MB)
  - Stronger sandbox (no syscalls without WASI)
    """);
    }
}
