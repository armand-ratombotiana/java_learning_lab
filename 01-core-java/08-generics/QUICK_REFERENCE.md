# Module 13: Generics - Quick Reference

**Quick Lookup Guide**  
**Syntax Cheat Sheet**  
**Decision Trees**  
**Interview Q&A**

---

## 🔍 Syntax Quick Reference

### Generic Class Declaration
```java
public class Box<T> { }                    // Single type parameter
public class Pair<K, V> { }                // Multiple type parameters
public class Triple<A, B, C> { }           // Three type parameters
```

### Generic Method Declaration
```java
public <T> void method(T param) { }        // Generic method
public <T> T method(T param) { }           // Generic return type
public <K, V> void method(K k, V v) { }    // Multiple type parameters
```

### Bounded Type Parameters
```java
<T extends Number>                         // Upper bound
<T extends Comparable<T>>                  // Recursive bound
<T extends Number & Serializable>          // Multiple bounds
<T extends Comparable<? super T>>          // Complex bound
```

### Wildcards
```java
List<?>                                    // Unbounded wildcard
List<? extends Number>                     // Upper bounded wildcard
List<? super Integer>                      // Lower bounded wildcard
```

### Generic Inheritance
```java
class Child<T> extends Parent<T> { }       // Generic inheritance
class Child extends Parent<String> { }     // Concrete inheritance
class Child<T> implements Interface<T> { } // Generic interface
```

---

## 📋 Common Patterns

### Pattern 1: Generic Container
```java
public class Container<T> {
    private T value;
    public void set(T value) { this.value = value; }
    public T get() { return value; }
}

// Usage
Container<String> container = new Container<>();
container.set("Hello");
String value = container.get();
```

### Pattern 2: Generic Pair
```java
public class Pair<K, V> {
    private K key;
    private V value;
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    public K getKey() { return key; }
    public V getValue() { return value; }
}

// Usage
Pair<String, Integer> pair = new Pair<>("age", 30);
```

### Pattern 3: Generic Method
```java
public static <T> void printArray(T[] array) {
    for (T element : array) {
        System.out.println(element);
    }
}

// Usage
Integer[] intArray = {1, 2, 3};
printArray(intArray);
```

### Pattern 4: Bounded Generic Method
```java
public static <T extends Comparable<T>> T max(T[] array) {
    T max = array[0];
    for (T element : array) {
        if (element.compareTo(max) > 0) {
            max = element;
        }
    }
    return max;
}

// Usage
Integer[] intArray = {1, 5, 3};
Integer max = max(intArray);
```

### Pattern 5: Wildcard Method
```java
public static void printList(List<?> list) {
    for (Object element : list) {
        System.out.println(element);
    }
}

// Usage
List<String> stringList = Arrays.asList("a", "b");
printList(stringList);
```

### Pattern 6: PECS Pattern
```java
// Producer (extends) - read from source
public static <T> void copy(List<? extends T> source, List<? super T> dest) {
    for (T element : source) {
        dest.add(element);
    }
}

// Usage
List<Integer> source = Arrays.asList(1, 2, 3);
List<Number> dest = new ArrayList<>();
copy(source, dest);
```

---

## 🎯 Decision Trees

### When to Use Generics?

```
Do you have a collection or method that works with multiple types?
├─ YES → Use generics
│   ├─ Single type? → Use <T>
│   ├─ Multiple types? → Use <K, V> or <A, B, C>
│   └─ Need constraints? → Use bounded types
└─ NO → Don't use generics
```

### Which Wildcard to Use?

```
Do you need to read from the collection?
├─ YES → Use <? extends T>
│   └─ Can read as T, cannot write
└─ NO → Do you need to write?
    ├─ YES → Use <? super T>
    │   └─ Can write T, read as Object
    └─ NO → Use <?> (unbounded)
```

### Generic Class or Method?

```
Is the type parameter used in the class?
├─ YES → Use generic class <T>
└─ NO → Use generic method <T>
    └─ Is it used in multiple methods?
        ├─ YES → Consider generic class
        └─ NO → Use generic method
```

### Bounded or Unbounded?

```
Do you need to call methods on the type parameter?
├─ YES → Use bounded type
│   ├─ Comparable? → <T extends Comparable<T>>
│   ├─ Number? → <T extends Number>
│   └─ Custom? → <T extends MyInterface>
└─ NO → Use unbounded <T>
```

---

## 📊 Type Parameter Naming Conventions

| Convention | Usage | Example |
|-----------|-------|---------|
| T | Type | `<T>` |
| K | Key | `<K, V>` |
| V | Value | `<K, V>` |
| E | Element | `<E>` |
| N | Number | `<N extends Number>` |
| U, V, W | Additional types | `<T, U, V>` |

---

## 🚫 What NOT to Do

| ❌ Don't | ✅ Do |
|---------|------|
| `List list = new ArrayList();` | `List<String> list = new ArrayList<>();` |
| `List<String>[] array = new List[10];` | `List<?>[] array = new List[10];` |
| `if (list instanceof List<String>)` | `if (list instanceof List<?>)` |
| `public void method(List<Object> list)` | `public void method(List<?> list)` |
| `<T extends A, B>` | `<T extends A & B>` |
| `List<String> list = (List<String>) rawList;` | Use proper generics from start |

---

## 🔧 Common Operations

### Creating Generic Instances
```java
List<String> list = new ArrayList<>();
Map<String, Integer> map = new HashMap<>();
Set<String> set = new HashSet<>();
Queue<Integer> queue = new LinkedList<>();
```

### Iterating Generic Collections
```java
for (String element : list) { }
for (Map.Entry<String, Integer> entry : map.entrySet()) { }
for (String element : set) { }
```

### Type-Safe Operations
```java
list.add("Hello");           // Type-safe
String str = list.get(0);    // No casting
list.remove("Hello");        // Type-safe
```

### Generic Method Calls
```java
String result = Utilities.<String>getFirst(array);
Integer max = Utilities.<Integer>max(intArray);
```

---

## 🎓 Interview Q&A

### Q1: What are generics?
**A**: Generics enable type-safe collections and methods by allowing you to specify the type of objects at compile time.

### Q2: Why use generics?
**A**: Type safety, elimination of casting, better code readability, and early error detection.

### Q3: What is type erasure?
**A**: Generic type information is removed at runtime and replaced with Object or the upper bound.

### Q4: Can you create a generic array?
**A**: No, use `List<T>` or `List<?>[]` instead due to type erasure.

### Q5: What is PECS?
**A**: Producer Extends, Consumer Super - use `? extends` for reading, `? super` for writing.

### Q6: What is a bounded type parameter?
**A**: A type parameter with constraints, e.g., `<T extends Number>` restricts T to Number and subclasses.

### Q7: What is a wildcard?
**A**: A placeholder for an unknown type, e.g., `List<?>` represents a list of unknown type.

### Q8: Can you use instanceof with generics?
**A**: No, use `instanceof List<?>` instead of `instanceof List<String>`.

### Q9: What is a recursive type bound?
**A**: A bound where the type parameter references itself, e.g., `<T extends Comparable<T>>`.

### Q10: How do you handle unchecked warnings?
**A**: Use `@SuppressWarnings("unchecked")` only when necessary, or refactor to avoid the warning.

---

## 🔗 Type Parameter Relationships

```
Object
├─ Number
│  ├─ Integer
│  ├─ Double
│  └─ Long
├─ String
├─ Comparable<T>
└─ Serializable
```

---

## 📈 Complexity Guide

| Concept | Difficulty | Time to Learn |
|---------|-----------|---------------|
| Basic generics | Easy | 30 min |
| Generic classes | Easy | 30 min |
| Generic methods | Easy | 30 min |
| Bounded types | Medium | 45 min |
| Wildcards | Medium | 60 min |
| Type erasure | Medium | 45 min |
| Advanced patterns | Hard | 90 min |
| Variance | Hard | 60 min |

---

## 🎯 Common Mistakes

1. **Using raw types** - Always use generic types
2. **Mixing raw and generic** - Be consistent
3. **Incorrect wildcard usage** - Remember PECS
4. **Type parameter shadowing** - Use different names
5. **Unchecked casts** - Avoid when possible
6. **Generic arrays** - Use List instead
7. **instanceof with generics** - Use wildcards
8. **Forgetting bounds** - Add constraints when needed

---

## 📚 Related Topics

- Collections Framework (Module 03)
- Lambda Expressions (Module 10)
- Reflection (Advanced)
- Type Inference (Java 8+)
- Functional Interfaces (Module 10)

---

**Module 13 - Generics Quick Reference**  
*Your quick lookup guide for generics*