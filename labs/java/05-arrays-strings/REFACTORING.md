# Refactoring Arrays & Strings

## Replace Manual Array Copy with Library Method

Before: `for (int i = 0; i < src.length; i++) { dest[i] = src[i]; }`
After: `System.arraycopy(src, 0, dest, 0, src.length);` or `Arrays.copyOf(src, src.length);`

## Use StringBuilder for Concatenation in Loops

Before: `String s = ""; for (int i = 0; i < 1000; i++) { s += i; }`
After: `StringBuilder sb = new StringBuilder(); for (int i = 0; i < 1000; i++) { sb.append(i); } String s = sb.toString();`

## Replace StringBuffer with StringBuilder

If not used across threads: `new StringBuffer()` → `new StringBuilder()`.

## Use Text Blocks for Multi-Line Strings

Before: `String json = "{\n  \"name\": \"Alice\"\n}";`
After: `String json = """ { "name": "Alice" } """;`

## Use String::isEmpty Instead of length() Check

Before: `if (s.length() == 0)`
After: `if (s.isEmpty())`

## Use String::isBlank (Java 11+)

Before: `if (s.trim().isEmpty())`
After: `if (s.isBlank())`

## Use String::repeat (Java 11+)

Before: `String dashes = ""; for (int i = 0; i < n; i++) dashes += "-";`
After: `String dashes = "-".repeat(n);`
