# Debugging — Recursion

## Print Depth Tracing
`java
public static int factorial(int n, String indent) {
    System.out.println(indent + "factorial(" + n + ")");
    if (n <= 1) { System.out.println(indent + "→ 1"); return 1; }
    int result = n * factorial(n - 1, indent + "  ");
    System.out.println(indent + "→ " + result);
    return result;
}
`

## IDE Debugger
- Set breakpoint on recursive call
- Watch call stack in debugger
- Step Into to trace recursion, Step Over to skip
- In IntelliJ: Drop Frame to back out
