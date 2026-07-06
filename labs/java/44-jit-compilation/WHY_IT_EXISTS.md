# Why JIT Compilation Exists

JIT compilation exists because Java needed to bridge two conflicting requirements: portability and performance. Bytecode provides portability (write once, run anywhere), but interpreted bytecode is 10-100x slower than native code. Ahead-of-time (AOT) compilation provides native speed but sacrifices portability and runtime adaptability.

The C1 compiler (client) exists for faster startup. Desktop applications and client-side tools need responsive startup — waiting for a heavyweight compiler to warm up is unacceptable. C1 provides basic optimizations quickly, improving responsiveness within seconds.

The C2 compiler (server) exists for peak performance. Long-running server applications benefit from aggressive optimizations that take milliseconds per method but save microseconds per invocation. C2 performs advanced optimizations like:
- Loop unrolling and vectorization
- Global value numbering
- Dead code elimination
- Lock coarsening and elision
- Conditional constant propagation

Tiered compilation exists to combine these benefits — fast startup from C1 and peak performance from C2. Without tiered compilation, you'd have to choose between fast startup (client compiler only) or peak performance (server compiler only).

Inlining exists because method call overhead is significant (frame setup, parameter passing, return handling). Inlining eliminates this overhead and more importantly exposes the inlined code to the caller's optimization context. A constant that appears in the inlined method can propagate into the caller, enabling constant folding and dead code elimination.

Intrinsics exist because the JIT knows that some library methods (System.arraycopy, Math functions) have highly optimized CPU-specific implementations that are faster than any sequence of bytecode. Mapping these to CPU intrinsics gives Java competitive performance with C/C++ for numerical and systems programming.

Escape analysis exists because heap allocation is expensive (GC overhead, pointer chasing). If the JIT can prove an object doesn't escape the method, it can allocate it on the stack or replace it with registers. This is critical for value objects and short-lived temporaries.
