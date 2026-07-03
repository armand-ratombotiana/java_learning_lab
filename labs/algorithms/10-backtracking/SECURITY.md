# Security — Backtracking

- Exponential backtracking: Attacker can craft inputs causing worst-case search
- Resource exhaustion: Backtracking can consume memory with deep recursion
- Timeout vulnerabilities: Long-running backtracking can be exploited
- Input validation: Limit input size to prevent explosion
- Use iterative deepening DFS for depth-limited search
