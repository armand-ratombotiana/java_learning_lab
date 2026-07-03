# Common Mistakes — Functional Programming

## 1. Using Mutable State in Streams
```java
List<Integer> result = new ArrayList<>();
stream.filter(x -> x > 5).forEach(result::add); // Not thread-safe, not pure
```
**Fix:** Use `collect()`.

## 2. Returning null from Methods Declared as Optional
```java
public Optional<String> find() {
    return null; // Breaks the monad contract
}
```
**Fix:** Return `Optional.empty()`.

## 3. Calling `get()` Without Checking `isPresent()`
```java
String s = optional.get(); // NoSuchElementException if empty
```
**Fix:** Use `orElse()`, `orElseGet()`, or `ifPresent()`.

## 4. Using Optional for Fields / Parameters
```java
public class Person {
    private Optional<String> name; // Not serializable, not recommended
}
```
**Fix:** Use simple null checks or a default value.

## 5. Side Effects in map()
```java
.map(x -> { System.out.println(x); return x * 2; }) // Impure
```
**Fix:** Use `peek()` for debugging (remove in production).

## 6. Confusing Composition Order with `andThen` / `compose`
- `f.andThen(g)` → apply f then g
- `f.compose(g)` → apply g then f
