# Performance of Date-Time API

## Object Creation Cost
- `LocalDate.now()`: ~15ns (fast due to caching)
- `LocalDate.of()`: ~10ns
- `LocalTime.now()`: ~15ns
- `ZonedDateTime.now()`: ~100ns (timezone lookup)
- `Instant.now()`: ~10ns (System.currentTimeMillis() + System.nanoTime())

## Immutability Benefits
Being immutable, these objects can be freely shared without defensive copies. The caching of common values (years, times) reduces memory pressure.

## Formatting Performance
- `DateTimeFormatter.ofPattern()`: ~1-5μs (pattern parsing)
- `format()` with cached formatter: ~100-500ns
- `SimpleDateFormat.format()`: ~500-2000ns

## Best Practices
1. Cache `DateTimeFormatter` instances (they're thread-safe)
2. Use `Instant` for high-precision timestamps
3. Avoid `ZonedDateTime` in hot loops (timezone lookups are expensive)
4. Use `Clock` for testability and fixed time sources
