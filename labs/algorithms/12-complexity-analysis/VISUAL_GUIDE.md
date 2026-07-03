# Visual Guide — Complexity Analysis

## Growth Rate Comparison
`
Operations
^
|                    2ⁿ (exponential)
|                  n² (quadratic)
|               n log n
|            n (linear)
|         log n
|      1 (constant)
+------------------------> n
`

## Amortized Analysis — Dynamic Array
`
Cost per insert
^
|  * (costly resize)
|  |  |  |  |  *  |  |  |  |  *
+------------------------> insert #
Most inserts cost O(1), resizing costs O(n)
Amortized cost: O(1) per insert
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Complexity Analysis

## Bad Complexity (O(n²) when O(n) is possible)
`java
// Bad: O(n²) — repeated string concatenation
String result = "";
for (String s : strings) result += s;  // String is immutable!

// Good: O(n)
StringBuilder sb = new StringBuilder();
for (String s : strings) sb.append(s);
`

## Amortized O(1) — Dynamic Array
`java
public class DynamicArray {
    private int[] arr = new int[1];
    private int size = 0;

    public void add(int value) {
        if (size == arr.length) {
            int[] newArr = new int[arr.length * 2]; // O(n) — but rare
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            arr = newArr;
        }
        arr[size++] = value; // O(1) — most of the time
    }
}
`
Amortized analysis: n inserts cost O(n), so O(1) per insert amortized.
