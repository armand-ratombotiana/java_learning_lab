# Architecture of Combinatorics

## Combinatorial Structures in Java

```
Collection API
├── List<T>          — ordered, indexed (permutations)
├── Set<T>           — unordered, unique (combinations)
├── Map<K,V>         — associations (pairings)
└── Stream<T>        — combinatorial operations via filter/map/collect
```

## Combinatorial Algorithms

```
Enumeration
├── Permutations     — all orderings
├── Combinations     — all subsets of size k
├── Subsets          — all subsets (power set)
├── Partitions       — all ways to divide
└── Compositions     — ordered partitions
```

## Library Design for Combinatorics

```java
public interface CombinatorialObject {
    long count();
    void generate(Consumer<List<Integer>> consumer);
}

public record Subsets(int n) implements CombinatorialObject {
    public long count() { return 1L << n; }
    public void generate(Consumer<List<Integer>> consumer) {
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> set = new ArrayList<>();
            for (int i = 0; i < n; i++)
                if ((mask & (1 << i)) != 0) set.add(i);
            consumer.accept(set);
        }
    }
}
```
