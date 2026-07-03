# Common Mistakes with String Handling

## Mistake 1: String Concatenation in Loops
```java
// BAD: O(n²)
String s = "";
for (int i = 0; i < 1000; i++) s += i;
// GOOD: O(n)
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) sb.append(i);
```

## Mistake 2: Using == Instead of equals()
```java
String a = new String("hello");
String b = new String("hello");
a == b;         // false (different references)
a.equals(b);    // true (same value)
```

## Mistake 3: Not Compiling Regex Patterns
```java
// BAD: Compiles pattern every time
"text".matches("\\d+");
// GOOD: Cache the pattern
private static final Pattern DIGITS = Pattern.compile("\\d+");
DIGITS.matcher("text").matches();
```

## Mistake 4: Using StringBuffer Instead of StringBuilder
StringBuffer is synchronized; StringBuilder is not. In single-threaded code, StringBuilder is 2-3x faster.

## Mistake 5: Forgetting Text Block Leading Whitespace
Text blocks remove common leading whitespace. Mixing tabs and spaces causes unexpected indentation.

## Mistake 6: Unescaped Backslashes in Regex
In Java strings, `\` is an escape character. For regex `\d`, use `"\\d"`. For literal backslash, use `"\\\\"`.
