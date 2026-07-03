# Common Mistakes with Records

## Mistake 1: Forgetting That Records Are Shallowly Immutable

```java
// BAD: The list reference is final, but the list contents can change
record ShoppingCart(List<String> items) {}

var cart = new ShoppingCart(new ArrayList<>());
cart.items().add("apple");  // This works! The list is mutated
System.out.println(cart);   // ShoppingCart[items=[apple]]

// GOOD: Defensive copy in compact constructor
record SafeCart(List<String> items) {
    public SafeCart {
        items = List.copyOf(items);  // Creates immutable copy
    }
}

var safe = new SafeCart(new ArrayList<>());
// safe.items().add("apple");  // UnsupportedOperationException
```

## Mistake 2: Exposing Mutable Arrays

```java
// BAD: Array reference is final but array contents are mutable
record Data(byte[] contents) {}

var data = new Data(new byte[]{1, 2, 3});
data.contents()[0] = 99;  // Mutates the internal array!

// GOOD: Defensive copy for arrays
record SafeData(byte[] contents) {
    public SafeData {
        contents = contents.clone();  // Defensive copy
    }
    
    @Override
    public byte[] contents() {
        return contents.clone();  // Return copy in accessor
    }
}
```

## Mistake 3: Using Records with ORM Frameworks

```java
// BAD: JPA/Hibernate typically need no-arg constructors and proxies
@Entity
public record User(@Id Long id, String name) {}  // Won't work!

// GOOD: Use traditional class for JPA entities
@Entity
public class User {
    @Id private Long id;
    private String name;
    // ...
}
```

Records are value objects, not entities. They work well as DTOs and query results but not as JPA entities.

## Mistake 4: Overlooking Component Order in equals/hashCode

```java
record Pair(int a, int b) {}

var p1 = new Pair(1, 2);
var p2 = new Pair(2, 1);
System.out.println(p1.equals(p2));  // false — components differ in order
```

This is correct behavior but can be surprising if you think of the record as an unordered bag of values.

## Mistake 5: Adding Mutating Methods

```java
// BAD: Mutable methods on an immutable record
record Counter(int value) {
    public void increment() {
        // Can't modify this.value — it's final
        // Need to return a new instance instead
    }
}

// GOOD: Return new instance with updated value
record ImmutableCounter(int value) {
    public ImmutableCounter increment() {
        return new ImmutableCounter(value + 1);
    }
}
```

## Mistake 6: Ignoring Inheritance Restrictions

```java
// BAD: Records cannot extend other classes
record Base(int id) {}
record Derived(int id, String name) extends Base(id) {}  // COMPILER ERROR!

// GOOD: Records implement interfaces
interface Identifiable { int id(); }
record User(int id, String name) implements Identifiable {}
```

## Mistake 7: Expecting Records to Work with Reflection-Based Frameworks

Some frameworks (older versions of Spring, MyBatis, etc.) rely on JavaBeans conventions including setter methods. Records don't have setters, and their accessors use the JavaBeans convention only if explicitly annotated.

```java
record User(String name) {}
// user.getName() — does NOT exist (it's user.name())
```

## Mistake 8: Assuming Local Records Can Be Used Outside Their Scope

```java
public Supplier<Record> badExample() {
    record Local(int x) {}
    return () -> new Local(5);  // OK — record is still in scope
}  // Local record type is not accessible after this point

// Trying to use Local outside the method → compile error
```

## Mistake 9: Using Records for Frequently Changing State

Records are immutable. If you need to update values frequently, each update creates a new object. This can cause memory pressure if done excessively in hot loops:

```java
// BAD: Creating many short-lived records in a loop
record MutableStylePoint(int x, int y) {}

MutableStylePoint p = new MutableStylePoint(0, 0);
for (int i = 0; i < 1000000; i++) {
    p = new MutableStylePoint(p.x() + 1, p.y() + 1);  // 1M allocations
}

// GOOD: Use a mutable local variable
int x = 0, y = 0;
for (int i = 0; i < 1000000; i++) {
    x++; y++;
}  // 0 allocations
```

## Mistake 10: Forgetting to Handle Null Components

```java
// BAD: NullPointerException risk
record User(String name, String email) {}

var user = new User("Alice", null);
System.out.println(user.toString());  // Works (null printed as "null")
System.out.println(user.equals(new User("Alice", null)));  // Works
// But: user.email().length() → NullPointerException

// GOOD: Validate in compact constructor
record SafeUser(String name, String email) {
    public SafeUser {
        Objects.requireNonNull(name);
        Objects.requireNonNull(email);
    }
}
```
