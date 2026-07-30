# GUIDE — Optional Deep

## Step 1: Creating Optional
```java
Optional.of(value);       // throws if null
Optional.ofNullable(value); // empty if null
Optional.empty();
```

## Step 2: Safe Chaining with flatMap
```java
Optional<Address> addr = person.flatMap(Person::getAddress);
Optional<String> city = addr.flatMap(Address::getCity);
```

## Step 3: Optional to Stream
```java
Stream<String> stream = optional.stream();  // 0 or 1 element
```

## Step 4: orElseThrow
```java
String value = optional.orElseThrow();      // NoSuchElementException
```

## Step 5: Primitive Optionals
```java
OptionalInt opt = OptionalInt.of(42);
int v = opt.orElseThrow();
```

## Step 6: Exercises
1. Chain three optional‑returning methods safely
2. Convert a list of `Optional<T>` to `List<T>` (non‑empty)
3. Use `OptionalLong` to represent a nullable `long`
