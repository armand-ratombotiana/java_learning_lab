# Step-by-Step: Combinatorics in Java

## Count Permutations with Duplicates

```java
public static long countPermutationsWithDuplicates(String s) {
    int n = s.length();
    Map<Character, Long> freq = s.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    long result = factorial(n);
    for (long f : freq.values())
        result /= factorial((int) f);
    return result;
}
```

## Generate All Subsets (Power Set)

```java
public static List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    int n = nums.length;
    for (int mask = 0; mask < (1 << n); mask++) {
        List<Integer> subset = new ArrayList<>();
        for (int i = 0; i < n; i++)
            if ((mask & (1 << i)) != 0)
                subset.add(nums[i]);
        result.add(subset);
    }
    return result;
}
```

## Catalan Numbers

$$
C_n = \frac{1}{n+1} \binom{2n}{n}
$$

```java
public static long catalan(int n) {
    return binomial(2 * n, n) / (n + 1);
}
```
