# Security — Sorting Basics

- **Timing Attacks**: Constant-time comparison may be needed for sensitive data
- **Data Integrity**: Unstable sorts can reorder equal elements, affecting audit trails
- **Resource Exhaustion**: O(n²) on attacker-controlled large inputs can cause DoS
- **Comparator Injection**: Ensure user-controlled comparators are well-behaved
