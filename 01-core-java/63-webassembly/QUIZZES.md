# Module 63: WebAssembly (Wasm) - Quizzes

---

## Q1: The Sandbox Environment
What is the default security posture of a WebAssembly module?

A) It has full root access to the host machine.
B) It has access to the file system but not the network.
C) It is completely sandboxed in a memory-safe environment and has zero access to the host's OS, file system, or network unless explicitly granted by the host runtime via WASI.
D) It relies on Java's SecurityManager.

**Answer**: C
**Explanation**: Security is a fundamental design pillar of WebAssembly. A `.wasm` file cannot execute arbitrary code on the host machine, making it extremely safe for executing untrusted third-party plugins.

---

## Q2: WASI
What does WASI stand for, and what problem does it solve?

A) WebAssembly System Interface. It provides a standardized way for Wasm modules to safely interact with the host operating system (e.g., file I/O, clocks, random number generators) outside of a web browser.
B) Web Application Security Integration. It provides a firewall for Wasm.
C) Wide Area System Interface. It connects Wasm modules across the internet.
D) WebAssembly Standard Interpreter. It executes the code.

**Answer**: A
**Explanation**: Since Wasm was originally built for the browser (which has no access to the local file system), backend Wasm needed a safe standard to request OS resources. WASI fulfills this role using a capability-based security model.

---

## Q3: Docker vs WebAssembly
Why are some cloud architects predicting that WebAssembly might eventually replace or heavily complement Docker containers for Serverless deployments?

A) Because Wasm runs directly in the Linux Kernel.
B) Because Wasm modules are fully compiled OS images.
C) Because Wasm modules are vastly smaller (often kilobytes instead of megabytes), start up in microseconds (no OS to boot), and are completely architecture-neutral (the same binary runs on ARM and x86).
D) Because Wasm uses Kubernetes natively.

**Answer**: C
**Explanation**: Docker containers must bundle a subset of an Operating System and are tied to a specific CPU architecture. WebAssembly binaries are pure bytecode, incredibly tiny, and perfectly portable, solving the cold-start and size problems of traditional containers.