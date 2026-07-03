# Security — Recursion

- Stack Overflow DoS: Deep recursion on untrusted input can crash JVM
- Resource Exhaustion: Exponential-time recursion can be exploited
- Thread Starvation: Recursion on shared thread pool can block tasks
- Input Validation: Validate input size before recursion
