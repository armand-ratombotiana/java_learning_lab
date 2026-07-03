# Debugging — Sorting Basics

## Print Debugging
`java
System.out.println(Arrays.toString(arr) + " after pass " + i);
`

## Assertions
`java
assert isSorted(arr) : "Array should be sorted";

public static <T extends Comparable<T>> boolean isSorted(T[] arr) {
    for (int i = 1; i < arr.length; i++)
        if (arr[i].compareTo(arr[i-1]) < 0) return false;
    return true;
}
`

## Visual Debugging
Use JVisualVM or Java Mission Control to profile sorting of 10,000+ elements.
