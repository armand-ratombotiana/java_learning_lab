# Common Mistakes in JVM Tuning

## Mistake 1: Setting Xms Much Smaller Than Xmx
The heap grows on demand from `-Xms` to `-Xmx`. Growth requires expensive OS calls (mmap, page faults) and can cause unexpected latency spikes. Set `-Xms = -Xmx` for consistent performance.

## Mistake 2: Ignoring Young Generation Sizing
Default `NewRatio=2` means old gen is 2× young. For high allocation applications, the young generation may be too small, causing frequent minor GC. Experiment with `-Xmn` or `-XX:NewRatio`.

## Mistake 3: Code Cache Oversight
Default 240 MB code cache is adequate for most applications. But with complex frameworks (Spring, Hibernate, JAXB), the code cache can fill. Monitor with `-XX:+PrintCodeCache`. Increase with `-XX:ReservedCodeCacheSize`.

## Mistake 4: Unlimited Metaspace
Without `-XX:MaxMetaspaceSize`, Metaspace can grow until native memory is exhausted. Set a reasonable limit (e.g., 256 MB) based on the application's class footprint.

## Mistake 5: Using Large Pages Without Verification
`-XX:+UseLargePages` fails silently if the OS is not configured for huge pages. Verify with `-XX:+PrintFlagsFinal`. Check `/proc/meminfo` (Linux) for HugePages_Total.

## Mistake 6: Tuning Without Measurement
Changing flags without measuring their impact is guesswork. Always measure:
- Before and after GC pause times
- Throughput (requests/second)
- Memory footprint
- CPU usage

## Mistake 7: Copying Tuning from Other Applications
Each application has a unique allocation profile, live data set, and latency sensitivity. What works for a Spring Boot web service won't work for a Spark data processing job.

## Mistake 8: Not Using -XX:+AlwaysPreTouch
Without `-XX:+AlwaysPreTouch`, the OS lazily commits pages as they're accessed. This causes minor page faults during warmup. Pre-touch commits all pages at startup, eliminating runtime page faults.

## Mistake 9: Overriding Compiler Flags
`-XX:-TieredCompilation` disables C1, which delays C2 compilation (longer warmup). `-XX:CompileThreshold=100000` delays all compilation. Measure the warmup impact before changing compiler flags.

## Mistake 10: Enabling All Optimizations
More flags don't always improve performance. `-XX:+AlwaysCompileLoopMethods`, `-XX:+UseFMA`, and aggressive inlining can increase compilation time and code cache usage without corresponding throughput gains.
