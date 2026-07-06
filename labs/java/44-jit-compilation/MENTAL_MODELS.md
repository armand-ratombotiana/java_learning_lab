# Mental Models for JIT Compilation

## JIT as a Personal Trainer
Think of the JIT as a personal trainer. At first (interpreted), you're doing basic exercises with poor form. After practice (C1 compilation), you do the exercises correctly. With more training (profiling), the trainer understands your patterns. At peak fitness (C2 compilation), the trainer designs a highly optimized routine just for you.

## Tiered Compilation as an Airport Security
The interpreter is like the initial security check (everyone goes through). C1 (with profiling) is like TSA PreCheck — faster processing while collecting data. C2 is like CLEAR — fully optimized, minimal stops. You progress from general security to CLEAR based on your travel frequency (method invocation count).

## Inlining as Copy-Paste for Functions
Inlining is like copy-pasting a function's code at every call site instead of jumping to it. If you have `add(a,b)` called 1000 times, the JIT replaces each `call add` with `mov eax, a; add eax, b` — eliminating the call/ret overhead and enabling further optimization within the caller's context.

## Intrinsics as Special VIP Lanes
Intrinsics are VIP lanes at the airport. When the JIT sees `System.arraycopy`, it doesn't go through normal bytecode handling. Instead, it takes a specialized path that's directly mapped to a CPU instruction (like a dedicated AVX lane for memory copying).

## Escape Analysis as a Magician's Hat Trick
Escape analysis is the JIT checking whether anyone outside the method can see an object. If the object is created in a method and never leaves (no return, no assignment to global), it's like a magician's coin trick — the coin appears and disappears within the illusion. The JIT keeps the coin in its hand (registers) instead of putting it in a vault (heap).

## Deoptimization as a Falling Safety Net
Deoptimization is the JIT realizing its assumptions were wrong and falling back to safety. The JIT bets that a call site only receives type A. If type B appears, the JIT loses its bet and must revert to the interpreter (safe but slow). Like a circus performer whose safety net catches them when a trick fails.

## Compilation Threshold as a Frequent Flyer Program
The CompileThreshold is like a frequent flyer status: after enough flights (method invocations), you get upgraded to C1 (premium economy), then C2 (first class). The tier progression is automatic based on usage.
