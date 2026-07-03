# Security — Searching

- Binary search on untrusted data: If array is not sorted, results are unpredictable
- Interpolation search DoS: Non-uniform distribution causes O(n)
- Linear search timing: Can leak position through timing side channels
- Index bounds: Always validate computed indices
