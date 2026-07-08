# Common Mistakes with Apache Beam

1. Missing Window for Unbounded: Streaming pipelines without windowing cause indefinite state growth or no output
2. Late Data Without AllowedLateness: Late data is silently dropped by default; configure allowedLateness for late arrivals
3. No Trigger Configuration: Default trigger (watermark only) may not meet latency requirements; configure early firings
4. Large State Without Cleanup: Stateful DoFn must clear state periodically; use state TTL or explicit cleanup
5. Wrong Runner Selection: Not all runners support all features (e.g., state, timers, Splittable DoFn); check capability matrix
