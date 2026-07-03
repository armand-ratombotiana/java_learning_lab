# Security — Data Types

## Avoid float/double for Sensitive Values

Floating-point imprecision can cause security issues in financial calculations, comparison logic, and cryptographic operations. Always use `BigDecimal` for monetary values.

## Null Wrapper Vulnerability

Unboxing null wrappers causes NullPointerException — can be used for denial of service if user input controls object state.

## Integer Overflow

Overflow can bypass validation checks: `if (x + y > LIMIT)` can be bypassed if x + y overflows. Use `Math.addExact()`, `Math.multiplyExact()` for security-critical arithmetic.

## String for Secrets

Strings are immutable and stay in memory until GC. For passwords, use `char[]` and clear after use. For keys, use `SecretKey` or `KeyStore`.

## Deserialization

Deserializing objects of unknown types is dangerous. Validate before deserializing, or use alternative serialization mechanisms.
