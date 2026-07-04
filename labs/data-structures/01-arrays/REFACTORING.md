# Refactoring Arrays

## Replace Raw Array with ArrayList

```java
// Before
String[] names = new String[10];
int count = 0;
names[count++] = "Alice";
names[count++] = "Bob";

// After
List<String> names = new ArrayList<>();
names.add("Alice");
names.add("Bob");
```

## Use Arrays Utilities

```java
// Before
int[] copy = new int[source.length];
for (int i = 0; i < source.length; i++) copy[i] = source[i];

// After
int[] copy = Arrays.copyOf(source, source.length);
```

## Convert Between Array and List

```java
int[] arr = {1, 2, 3};
List<Integer> list = Arrays.stream(arr).boxed().toList();

// Back to array
int[] back = list.stream().mapToInt(i -> i).toArray();
```

## Extract Array Operations into Methods

```java
// Before — inline
int[] temp = new int[list.size()];
for (int i = 0; i < list.size(); i++) temp[i] = list.get(i);

// After
private int[] toIntArray(List<Integer> list) {
    return list.stream().mapToInt(i -> i).toArray();
}
```

## Prefer Enhanced For-Loop

```java
// Before
for (int i = 0; i < arr.length; i++) {
    process(arr[i]);
}

// After (when index not needed)
for (int value : arr) {
    process(value);
}
```

## Use Streams for Transformations

```java
// Before
int[] result = new int[arr.length];
for (int i = 0; i < arr.length; i++) {
    result[i] = arr[i] * 2;
}

// After
int[] result = Arrays.stream(arr).map(x -> x * 2).toArray();
```
