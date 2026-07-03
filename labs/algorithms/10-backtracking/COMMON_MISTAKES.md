# Common Mistakes

- Forgetting to backtrack (undo choice) — leads to incorrect state
- Not pruning early enough — wastes time on invalid branches
- Modifying shared state without copy — references cause aliasing bugs
- Missing base case — infinite recursion
- Checking constraints after full assignment — should prune earlier
- Not handling duplicate elements in input
- Exceeding recursion limit on large problems
