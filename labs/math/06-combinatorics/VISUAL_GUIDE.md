# Visual Guide to Combinatorics

## Permutations Tree (n=3)

```
          []
     /    |    \
    1     2     3
   / \   / \   / \
  2   3 1   3 1   2
  |   | |   | |   |
  3   2 3   1 2   1
```

6 permutations of {1,2,3}

## Combinations (n=5, k=3)

```
 1 2 3     1 2 4     1 2 5
 1 3 4     1 3 5     1 4 5
 2 3 4     2 3 5     2 4 5
 3 4 5
```

$\binom{5}{3} = 10$ combinations

## Pascal's Triangle

```
        1  ← row 0
      1   1
    1   2   1
  1   3   3   1
1   4   6   4   1
```

Each entry = $\binom{\text{row}}{\text{column}}$

## Stars and Bars

```
★★|★|★★  →  2,1,2  (6 stars into 3 bins)
 ★|★★★|★  →  1,3,1
 ★★|★★|★  →  2,2,1
```

$\binom{6+3-1}{3-1} = \binom{8}{2} = 28$ distributions
