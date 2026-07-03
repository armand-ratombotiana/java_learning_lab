# Debugging — Backtracking

## Print Search Tree
`java
private static int depth = 0;
void log(String msg) {
    System.out.println("  ".repeat(depth) + msg);
}
// Before choice: depth++; log("Trying " + choice);
// After backtrack: depth--; log("Backtrack from " + choice);
`

## Visualization
For N-Queens, print the board at each solution candidate to verify correctness.
