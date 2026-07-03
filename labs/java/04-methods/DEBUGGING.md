# Debugging Methods

## "Missing Return Statement"

If the compiler says `missing return statement`:
1. Check all code paths — every branch must return
2. Check if there's an if-else where both branches don't return
3. Check if there's a try-catch that doesn't return in all paths
4. Check if you accidentally wrote `void` instead of the actual return type

## StackOverflowError

Infinite recursion:
1. Check base case — does it ever become true?
2. Check recursive step — does it move toward base case?
3. Use stack trace to see the recursion path
4. Add depth counter: `private static int depth = 0;`

## Method Not Found / Wrong Method Called

1. Check method signature (params, return type)
2. Check if method is `private` or in a different package
3. Check overload resolution — is the right overload being called?
4. Use `@Override` to confirm method actually overrides a parent

## Debugging Techniques

- Set breakpoints in method entry and return
- Use "Step Into" (F7) to enter method, "Step Over" (F8) to execute line
- Watch parameters and local variables in debugger
- Use `Thread.currentThread().getStackTrace()` to see call chain
- Log method entry/exit with parameters for complex flows
