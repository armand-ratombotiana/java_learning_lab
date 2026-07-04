# Refactoring Discrete Math Code

## Extract Set Operations

```java
// BEFORE
List<Integer> union = new ArrayList<>(listA);
for (int x : listB) if (!union.contains(x)) union.add(x);

// AFTER
Set<Integer> union = new HashSet<>(setA);
union.addAll(setB);
```

## Replace Magic Numbers with Named Constants

```java
// BEFORE
int hash = 7;
hash = 31 * hash + field1.hashCode();
hash = 31 * hash + field2.hashCode();

// AFTER
private static final int HASH_SEED = 7;
private static final int HASH_MULTIPLIER = 31;

public int hashCode() {
    int result = HASH_SEED;
    result = HASH_MULTIPLIER * result + field1.hashCode();
    result = HASH_MULTIPLIER * result + field2.hashCode();
    return result;
}
```

## Use Streams for Set Operations

```java
// BEFORE
Set<Integer> intersection = new HashSet<>(setA);
intersection.retainAll(setB);

// AFTER
Set<Integer> intersection = setA.stream()
    .filter(setB::contains)
    .collect(Collectors.toSet());
```
