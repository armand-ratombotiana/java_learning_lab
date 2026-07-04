# Common Mistakes with Arrays

## Off-by-One Errors

```java
// WRONG — loops past last valid index
for (int i = 0; i <= arr.length; i++) {
    System.out.println(arr[i]);  // ArrayIndexOutOfBoundsException
}

// CORRECT
for (int i = 0; i < arr.length; i++) {
    System.out.println(arr[i]);
}
```

## Confusing Length vs Capacity

```java
int[] arr = new int[10];
// arr.length = 10 always

// With ArrayList:
ArrayList<Integer> list = new ArrayList<>(10);
// size (number of elements) = 0
// capacity (internal array length) = 10
```

## Negative Array Size

```java
int size = getSize();
int[] arr = new int[size];  // NegativeArraySizeException if size < 0
```

Always validate: `if (size < 0) throw new IllegalArgumentException(...)`

## Null Array Reference

```java
int[] arr = null;
System.out.println(arr.length);  // NullPointerException
```

## ArrayStoreException with Covariance

```java
Object[] arr = new String[3];
arr[0] = "hello";           // OK
arr[1] = 42;                // ArrayStoreException at runtime
```

## Mutable Keys in Hash-Based Arrays

```java
// If you use an array as a key in a HashMap:
HashMap<int[], String> map = new HashMap<>();
int[] key = {1, 2, 3};
map.put(key, "value");
key[0] = 99;  // Now map.get(key) returns null — hash changed!
```

## Ignoring Array Copy Overhead

```java
// Inefficient — copies the entire array for every insert
List<Integer> list = new ArrayList<>();
for (int i = 0; i < 100000; i++) {
    list.add(0, i);  // O(n) per insert → O(n²) total
}
```

## Assuming toArray() Returns a Specific Type

```java
// This throws ClassCastException at runtime:
String[] arr = (String[]) list.toArray();

// Correct:
String[] arr = list.toArray(new String[0]);
```
