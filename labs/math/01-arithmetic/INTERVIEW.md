# Interview Questions on Arithmetic

## Easy

1. Reverse an integer without converting to String.
2. Check if a number is a palindrome.
3. Find the GCD of two numbers.
4. Count set bits in an integer (popcount).

## Medium

5. Implement `myPow(x, n)` in $O(\log n)$.
6. String multiplication without BigInteger.
7. Find the single non-duplicate element in an array where every other element appears twice (XOR trick).
8. Trapping rain water — height array arithmetic.

## Hard

9. Implement division without `/`, `*`, or `%`.
10. Find $k$th smallest in multiplication table.
11. Solve "Maximum Points You Can Obtain from Cards" using sliding window / prefix sums.

## Java Implementation: Reverse Integer

```java
public int reverse(int x) {
    int result = 0;
    while (x != 0) {
        int digit = x % 10;
        x /= 10;
        if (result > Integer.MAX_VALUE / 10 ||
            result < Integer.MIN_VALUE / 10) return 0;
        result = result * 10 + digit;
    }
    return result;
}
```
