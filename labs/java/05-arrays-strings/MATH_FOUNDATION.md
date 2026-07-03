# Math Foundation for Arrays & Strings

## Indexing

Arrays use zero-based indexing: `array[0]` is the first element, `array[n-1]` is the last.

## Multi-dimensional Indexing

A 2D array `matrix[row][col]` uses:
- `row`: 0 to rows-1
- `col`: 0 to cols-1

The total number of elements = rows × cols.

## String Length vs Index

`"hello".length()` = 5, valid indices are 0-4. `charAt(5)` throws StringIndexOutOfBoundsException.

No specific math foundation required beyond basic counting and arithmetic.
