# Mathematical Foundation — I/O & NIO

## I/O Model Comparison
| Model | Threads per I/O | Complexity | Throughput |
|-------|-----------------|-----------|------------|
| Blocking | 1:1 | Low | Low |
| Non-blocking (selector) | 1:N | Medium | Medium |
| Async (CompletableFuture) | 1:N | High | Medium |
| Virtual threads | 1:1 (app) | Low | High |

## Buffer Size Tuning
`optimum buffer size ≈ block size of storage device (4 KB for SSD, 512 bytes for HDD)`
Typical sweet spot: 8 KB - 64 KB.

## Amdahl's Law Applied
File I/O is often the bottleneck. If 90% of time is I/O, parallelising the 10% CPU gains at most 11%.

## Zero-Copy Savings
Traditional: `file → kernel buffer → user buffer → kernel buffer → NIC` (4 copies)
Zero-copy: `file → kernel buffer → NIC` (2 copies, or 1 with DMA)
