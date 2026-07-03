# Debugging: String Handling

## Common Issues

### Issue 1: String Comparison with ==
```java
String s1 = "hello";
String s2 = new String("hello");
System.out.println(s1 == s2);  // false! Different objects
System.out.println(s1.equals(s2));  // true
```
Always use .equals() for string content comparison.

### Issue 2: String Concatenation in Loops
```java
// BAD: O(n^2)
String result = "";
for (String s : items) {
    result += s;
}

// GOOD: O(n)
StringBuilder sb = new StringBuilder();
for (String s : items) {
    sb.append(s);
}
String result = sb.toString();
```

### Issue 3: String.intern() Memory Leak
```java
for (String data : largeDataset) {
    data.intern();  // Strings pile up in the String Pool forever
}
```
Intern only strings with bounded cardinality (e.g., enum-like values, country codes).

### Issue 4: Substring Memory Leak (Java 6)
```java
// Java 6: substring kept reference to whole original char[]
String huge = readHugeFile();  // 100MB
String small = huge.substring(0, 10);  // Keeps 100MB char[] alive!
```
Fixed in Java 7+ where substring copies the array.

## Debugging Tools

### Enable String Deduplication (G1 GC)
```bash
-XX:+UseStringDeduplication  # G1 GC only
# Reduces duplicate String objects to share the same backing char[]
```

### Analyze String Usage
```bash
jmap -histo:live <pid> | findstr java.lang.String
# Shows String and char[] counts and sizes
```

### Tune String Table
```bash
-XX:StringTableSize=1000003  # Prime number for better distribution
-XX:+PrintStringTableStatistics  # Show pool usage at exit
```
