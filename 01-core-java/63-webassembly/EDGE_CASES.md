# Module 63: WebAssembly (Wasm) - Edge Cases & Pitfalls

---

## Pitfall 1: Expecting Full JVM Capability in Wasm

### ❌ Wrong
Writing a massive Enterprise Java application using Spring Boot, JPA, and Threading, and expecting to easily compile it down to a `.wasm` file using current tools (like TeaVM) without any code changes.

### ✅ Correct
Wasm currently lacks native, mature support for multithreading (though Wasm threads are evolving) and garbage collection (WasmGC is still relatively new). Compiling Java to Wasm works best for isolated, pure-computation functions or basic logic. Heavy enterprise frameworks relying on reflection and massive I/O are not yet suitable for direct Wasm compilation.

---

## Pitfall 2: The "WASI" Missing Link

### ❌ Wrong
Compiling a C++ or Rust program to Wasm that writes files to the disk, hosting that `.wasm` module in your Java application, and wondering why it crashes with permission errors.

### ✅ Correct
Wasm modules are completely sandboxed by default and have no idea what a file system or a network socket is. To allow a Wasm module to access the OS, the host runtime must explicitly grant capabilities via **WASI (WebAssembly System Interface)**. Without WASI, the module can only compute math and return numbers.

---

## Pitfall 3: Serialization Overhead

### ❌ Wrong
Passing millions of complex JSON objects back and forth between the Java Host and the Guest Wasm module multiple times per second.

### ✅ Correct
Wasm only natively understands four numeric types (integers and floats). To pass a String or a complex object, it must be serialized, written to the Wasm module's linear memory, and then deserialized inside the module. This serialization overhead can completely destroy any performance benefits gained by using Wasm. Limit boundary crossings and keep data exchange to compact, binary representations when possible.