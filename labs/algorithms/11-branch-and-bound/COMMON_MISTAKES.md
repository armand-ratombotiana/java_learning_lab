# Common Mistakes

- Bound not optimistic enough — prunes optimal solutions
- Bound too loose — no pruning, degenerates to brute force
- Using depth-first when best-first would prune more
- Not updating best solution early — many branches don't get pruned
- Incorrect bound computation for remaining capacity
- Not handling integer vs fractional bound properly
- Priority queue grows too large (memory issues)
