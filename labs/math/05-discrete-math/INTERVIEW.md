# Interview Questions on Discrete Mathematics

## Easy

1. Check if a number is prime.
2. Compute Fibonacci numbers (iterative and recursive).
3. Find GCD using Euclidean algorithm.
4. Count bits set to 1 in an integer.

## Medium

5. Implement the Sieve of Eratosthenes.
6. Generate all subsets of a set (power set).
7. Evaluate a boolean expression from a truth table.
8. Find modular inverse using extended Euclidean algorithm.

## Hard

9. Implement RSA key generation, encryption, and decryption.
10. Solve the "N-Queens" problem using backtracking (constraint satisfaction).
11. Implement a SAT solver using DPLL.
12. Design a bloom filter.

## Java: Modular Inverse

```java
public static int modInverse(int a, int m) {
    int[] result = extendedGCD(a, m);
    int gcd = result[0], x = result[1];
    if (gcd != 1) throw new ArithmeticException("Not invertible");
    return (x % m + m) % m;
}
```

## Java: N-Queens

```java
public static List<int[]> solveNQueens(int n) {
    List<int[]> solutions = new ArrayList<>();
    int[] board = new int[n];
    placeQueens(board, 0, solutions);
    return solutions;
}

private static void placeQueens(int[] board, int row, List<int[]> solutions) {
    if (row == board.length) { solutions.add(board.clone()); return; }
    for (int col = 0; col < board.length; col++) {
        if (isSafe(board, row, col)) {
            board[row] = col;
            placeQueens(board, row + 1, solutions);
        }
    }
}
```
