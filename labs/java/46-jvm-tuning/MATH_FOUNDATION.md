# Mathematical Foundation of JVM Tuning

## Heap Sizing
The optimal heap size H satisfies: H = L + A × T, where L is the live data set size, A is the allocation rate, and T is the GC interval. Too small: frequent GC. Too large: long pauses.

## Young Generation Tuning
Minor GC frequency f = A / Y, where A is allocation rate and Y is young size. Required throughput = f × pause_time. For A=500 MB/s, Y=1 GB: f = 0.5 Hz, or one minor GC every 2 seconds.

## Code Cache Filling Rate
For a class C with M hot methods, each method generates ~1-10 KB of compiled code. Code cache usage = Σ(MethodSize_i × Compiled_i). A Spring Boot app with 10K methods and 20% hot: ~200MB of compiled code.

## Metaspace Growth
For N classes, each with average metadata size S: Metaspace = N × S × (1 + O), where O is overhead factor (~30% for chunk alignment). N=50,000, S=2KB: ~130 MB Metaspace.

## String Dedup Savings
If N strings have D distinct char[] contents, the memory saved = (N - D) × (header + array_size). For N=1,000,000, D=100,000, array=32 bytes, header=16: saved = 900,000 × 48 = 43 MB.

## Large Page TLB Coverage
With 4KB pages, TLB coverage = TLB_entries × 4KB. With 2MB pages, coverage = TLB_entries × 2MB. A typical L1 TLB has 64 entries: 4KB = 256KB coverage, 2MB = 128MB coverage. For a 16GB heap, 4KB pages need 4 million entries (impossible), 2MB pages need 8,192 entries (fits in TLB).

## GC Overhead with Heap Sizing
GC overhead O = pause_time / interval = (L × k) / (Y / A) where L is live data, k is per-byte processing cost. Doubling Y halves GC frequency but may increase pause time by 1.5x (not linear due to survivor space effects).

## NUMA Penalty
Remote memory access is 1.5-2x slower than local. For a workload with 50% memory accesses to remote NUMA nodes: effective memory bandwidth = local_bandwidth / (1 + 0.5 × penalty_ratio). With 50% remote and 1.5x penalty: 20% throughput loss.
