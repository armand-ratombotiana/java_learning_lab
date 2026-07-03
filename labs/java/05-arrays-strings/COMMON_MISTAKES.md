# Common Mistakes — Arrays & Strings

1. **Off-by-one errors**: `for (int i = 0; i <= arr.length; i++)` — ArrayIndexOutOfBoundsException.
2. **Comparing arrays with equals**: `arr1.equals(arr2)` uses Object.equals (reference). Use `Arrays.equals()`.
3. **Using `==` for strings**: `s == "hello"` compares references. Use `s.equals("hello")`.
4. **StringBuilder instead of String concatenation in loops**: Using `+` in loop is O(n²).
5. **Forgetting String immutability**: `s.toUpperCase()` returns new String, doesn't modify s.
6. **Null array/string access**: Calling `.length` or methods on null reference.
7. **Array index out of bounds**: `arr[arr.length]` — valid indices are 0 to length-1.
8. **Inefficient string concatenation**: `String s = ""; for (x : list) s += x;` — use StringBuilder.
9. **Wrong array copy**: `arr2 = arr1` copies reference, not content. Use `Arrays.copyOf()`.
10. **StringBuilder capacity**: Default 16; frequent resizing hurts performance. Set initial capacity.
11. **Multi-dimensional array assumption**: `int[3][4]` is an array of arrays, not a rectangular block.
12. **StringBuffer vs StringBuilder**: Using StringBuffer (synchronized) in single-threaded code.
