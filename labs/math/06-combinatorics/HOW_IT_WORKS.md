# How Combinatorics Works

## Permutations (order matters)

### Without repetition:
$$
P(n, k) = \frac{n!}{(n-k)!} = n \times (n-1) \times \dots \times (n-k+1)
$$

### With repetition:
$$
n^k
$$

## Combinations (order doesn't matter)

### Without repetition:
$$
\binom{n}{k} = \frac{n!}{k!(n-k)!}
$$

### With repetition (stars and bars):
$$
\binom{n + k - 1}{k}
$$

## Binomial Theorem

$$
(x + y)^n = \sum_{k=0}^n \binom{n}{k} x^{n-k} y^k
$$

## In Java: Generate All Permutations

```java
public static void generatePermutations(int[] arr, int start) {
    if (start == arr.length - 1) {
        System.out.println(Arrays.toString(arr));
        return;
    }
    for (int i = start; i < arr.length; i++) {
        swap(arr, start, i);
        generatePermutations(arr, start + 1);
        swap(arr, start, i);
    }
}
```

## In Java: Generate All Combinations

```java
public static void generateCombinations(int n, int k, int start,
                                         List<Integer> current) {
    if (current.size() == k) {
        System.out.println(current);
        return;
    }
    for (int i = start; i <= n; i++) {
        current.add(i);
        generateCombinations(n, k, i + 1, current);
        current.remove(current.size() - 1);
    }
}
```
