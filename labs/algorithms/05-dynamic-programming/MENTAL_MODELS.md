# Mental Models

## The Staircase
To reach step n, come from n-1 or n-2. Ways to reach n = ways(n-1) + ways(n-2).

## The Fibonacci Tree
Naive recursion = tree with repeated nodes. Memoization prunes the tree.

## The Table Builder
Fill a spreadsheet where each cell depends on previously computed cells. Start from top-left (base cases) toward bottom-right (target).

## Optimal Substructure
"If I know the best subproblem solutions, I can combine them for the larger problem."
