# Common Mistakes — Recursion

- No base case — StackOverflowError
- Base case never reached — parameters don't converge
- Forgetting to return — missing return statement
- Modifying shared state — mutable objects cause subtle bugs
- Stack overflow — >10,000 calls; use -Xss or iteration
- Exponential complexity — naive Fibonacci; use memoization
- Not trusting recursion — trying to trace all calls instead of assuming subproblem solved
