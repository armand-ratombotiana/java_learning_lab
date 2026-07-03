# Debugging Arrays & Strings

## ArrayIndexOutOfBoundsException

When you see this:
1. Check the index value — is it less than array length?
2. Check loop bounds: `for (i = 0; i <= arr.length; i++)` should be `i < arr.length`
3. Check if the array is empty: `arr.length == 0`
4. Check if you're accessing the correct index variable

## NullPointerException with Arrays/Strings

1. Check if array reference is null before accessing `.length` or elements
2. Check if array element is null before calling methods on it
3. Use `Arrays.toString(arr)` to print array contents for inspection
4. Add null checks: `if (arr != null && arr.length > 0)`

## String Comparison Issues

When string comparison doesn't work as expected:
1. Check if using `==` instead of `.equals()` — most common bug
2. Check case sensitivity — use `.equalsIgnoreCase()` for case-insensitive
3. Check whitespace — use `.trim()` before comparison
4. Check for null before calling `.equals()`

## Visualizing Arrays in Debugger

- IntelliJ: arrays show with inline elements; use "View as" → "Array"
- Eclipse: arrays expandable in Variables view
- VS Code: hover over array variable to see elements
- Add `Arrays.toString()` / `Arrays.deepToString()` for print debugging
