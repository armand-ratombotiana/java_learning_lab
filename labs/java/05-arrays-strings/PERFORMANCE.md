# Performance — Arrays & Strings

## Array Performance

Arrays are the most memory-efficient data structure (no per-element overhead). Access is O(1) with bounds checking. Bounds checking can be elided by JIT when index is provably within range.

## String Concatenation

- `+` in a single expression: JIT uses `StringBuilder` or `invokedynamic` (efficient)
- `+` in a loop: O(n²) — each iteration creates new String and StringBuilder
- Use explicit `StringBuilder` with initial capacity in loops

## String.substring() (Java 6 vs 7+)

- Java 6: shared char array — O(1) but could keep large array alive
- Java 7+: copies the array — O(n) but prevents memory leaks

## Compact Strings (Java 9+)

- Latin-1 strings use 1 byte per char (half the memory of Java 8)
- UTF-16 strings use 2 bytes per char
- `coder` flag distinguishes — small performance check on each access

## StringBuilder Capacity

Default capacity: 16. When exceeded, array grows to (oldCapacity * 2) + 2 — causes copy. Set initial capacity: `StringBuilder sb = new StringBuilder(expectedLength);`

## Text Blocks

Text blocks are compiled to string constants at compile time — no runtime overhead compared to regular string literals.
