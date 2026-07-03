# Security — DP

- Resource Exhaustion: DP tables with attacker-controlled dimensions can cause OOM
- Cache Timing: DP operations may leak information
- Input Validation: Validate dimensions to prevent excessive allocation
- Integer Overflow: Use long/BigInteger for large problems
