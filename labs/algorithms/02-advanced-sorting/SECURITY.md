# Security — Advanced Sorting

- Quick Sort DoS: Attacker creates input causing O(n²). Mitigation: random pivot or Introsort
- Timing Side Channels: Comparison timing may leak information
- Comparator Injection: Malicious comparator could cause infinite loops
- Memory Exhaustion: Merge Sort O(n) space for large arrays
