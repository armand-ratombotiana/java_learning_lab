# Module 63: WebAssembly (Wasm) - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What makes WebAssembly a compelling alternative to Docker for running third-party code on a server?
**Answer**:
1. **Security**: Docker containers share the host's OS kernel. A kernel vulnerability can lead to a container escape. WebAssembly is a completely self-contained, CPU-agnostic bytecode format running in a highly restricted software sandbox. It fundamentally cannot access memory outside its assigned array, making it much safer for running untrusted code.
2. **Speed & Size**: Docker requires bundling an entire OS file system (even Alpine is a few MBs) and takes tens of milliseconds to start. A `.wasm` file is often just a few kilobytes of pure compiled logic and starts in microseconds.
3. **Portability**: A Docker image is compiled for a specific architecture (e.g., `amd64` vs `arm64`). A `.wasm` binary is architecture-neutral; the Wasm runtime handles the JIT compilation to the underlying hardware.

### Q2: How does a Wasm module interact with the outside world if it is fully sandboxed?
**Answer**:
A Wasm module can only perform pure mathematical computations on numbers. It cannot open files, make network requests, or even print to the console on its own.
To interact with the outside world, the **Host Runtime** (e.g., a Java application or Wasmtime) must explicitly inject "Host Functions" into the Wasm module when it is instantiated. The Wasm module calls these imported functions, and the Host executes the actual OS-level work on its behalf. The WASI (WebAssembly System Interface) standard standardizes these host functions for common OS tasks.

### Q3: What is "Linear Memory" in WebAssembly?
**Answer**:
WebAssembly does not have a garbage-collected heap or complex object pointers like Java. It uses "Linear Memory," which is simply a single, contiguous array of raw bytes.
When the host (Java) wants to pass a complex object (like a String) to Wasm, it must encode that String into UTF-8 bytes, copy those bytes into a specific index of the Wasm module's Linear Memory array, and then pass an integer (the index pointer) and another integer (the length) to the Wasm function.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: The Serialization Bottleneck
**Problem**: An interviewer poses this scenario: "We decided to rewrite a heavy Java mathematical calculation in Rust, compile it to WebAssembly, and call it from our Java backend using a Wasm runtime. The calculation takes a massive JSON string representing a million data points. However, our performance benchmarks show that the Java+Wasm approach is actually 5x *slower* than pure Java. What went wrong?"

**Solution**:
The problem is the **Serialization and Boundary Crossing Overhead**.
To pass the massive JSON string to Wasm, the Java host must:
1. Allocate memory inside the Wasm Linear Memory.
2. Encode the Java String to UTF-8 bytes.
3. Copy the bytes into the Wasm memory.
4. Execute the Wasm function.
5. The Wasm (Rust) code must then deserialize that JSON string back into Rust structs.
This massive dual-serialization cost completely eclipses the performance gains of the math calculation.
**The Fix**: Never pass large JSON strings across the Wasm boundary. Instead, format the data into a dense, binary format (like Protocol Buffers or FlatBuffers) or use shared memory arrays where the Host and Guest agree on the byte layout beforehand, eliminating serialization entirely.