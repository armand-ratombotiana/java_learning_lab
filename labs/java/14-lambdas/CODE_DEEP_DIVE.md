# Code Deep Dive — Lambdas

## Example: Sorting with Comparator
```java
List<String> names = Arrays.asList("Charlie", "Alice", "Bob");

// Anonymous class
names.sort(new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
});

// Lambda
names.sort((a, b) -> a.compareTo(b));

// Method reference
names.sort(String::compareTo);
```

## Example: Custom Functional Interface
```java
@FunctionalInterface
interface Transformer<T> {
    T transform(T input);
}

public static void main(String[] args) {
    Transformer<String> upper = s -> s.toUpperCase();
    Transformer<String> reverse = s -> new StringBuilder(s).reverse().toString();

    System.out.println(upper.transform("hello"));   // HELLO
    System.out.println(reverse.transform("hello")); // olleh
}
```

## Example: Chaining Functions
```java
Function<String, Integer> parser = Integer::parseInt;
Function<Integer, String> formatter = Object::toString;
Function<String, String> pipeline = parser.andThen(formatter);

System.out.println(pipeline.apply("42")); // "42"
```
