# Module 63: WebAssembly (Wasm) - Mini Project

**Project Name**: Polyglot Wasm Plugin Host  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Demonstrate the power of WebAssembly as a polyglot plugin system. You will write a Java application that hosts a WebAssembly runtime, loads a pre-compiled `.wasm` file (simulating logic written in Rust or C), and executes it safely.

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Create a Maven project.
   - Include a Java WebAssembly runtime dependency. For this project, use `Chicory` or an embedded runtime like `wasmtime-java`.

2. **The Wasm Module (Mock)**:
   - Since we are focusing on Java, you will not be required to write Rust/C++. Assume you have been given a file named `calculator.wasm`.
   - *Note*: You can use WABT (WebAssembly Binary Toolkit) to compile a simple `.wat` (WebAssembly Text format) file into `.wasm` for testing.
   - The module exports a function `int add(int a, int b)`.

3. **The Java Host**:
   - Create a `WasmPluginManager` class.
   - Load the `calculator.wasm` file from the resources directory into the Wasm Engine.
   - Instantiate the Wasm module.
   - Lookup the exported `add` function.

4. **Execution**:
   - Invoke the `add` function from Java, passing two integers.
   - Print the result returned from the Wasm module.

---

## 💡 Solution Blueprint

*(Using the `wasmtime-java` library as an example)*

1. **Dependency**:
   ```xml
   <dependency>
       <groupId>io.github.kawamuray.wasmtime</groupId>
       <artifactId>wasmtime-java</artifactId>
       <version>0.10.0</version>
   </dependency>
   ```

2. **The Implementation**:
   ```java
   import io.github.kawamuray.wasmtime.*;

   public class WasmHost {
       public static void main(String[] args) throws Exception {
           // 1. Create a Wasmtime Engine and Store (sandbox)
           try (Store<Void> store = Store.withoutData();
                Engine engine = store.engine()) {

               // 2. Load the WebAssembly binary from disk
               byte[] wasmBytes = Files.readAllBytes(Paths.get("calculator.wasm"));
               Module module = new Module(engine, wasmBytes);

               // 3. Instantiate the module inside the sandbox
               Instance instance = new Instance(store, module, Collections.emptyList());

               // 4. Find the exported function
               Func addFunction = instance.getFunc(store, "add").get();

               // 5. Execute the Wasm function from Java!
               Val[] argsWasm = { Val.fromI32(5), Val.fromI32(7) };
               Val[] results = addFunction.call(store, argsWasm);

               System.out.println("Result from Wasm: " + results[0].i32());
           }
       }
   }
   ```