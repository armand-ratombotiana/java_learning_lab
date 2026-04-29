# Module 13: Generics - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-12 (Core Java, OOP, Collections)  
**Estimated Reading Time**: 75-90 minutes  
**Code Examples**: 160+

---

## 📚 Table of Contents

1. [Introduction to Generics](#introduction)
2. [Generic Classes](#genericclasses)
3. [Generic Methods](#genericmethods)
4. [Bounded Type Parameters](#bounded)
5. [Wildcards](#wildcards)
6. [Type Erasure](#erasure)
7. [Generic Collections](#collections)
8. [Advanced Generics](#advanced)
9. [Best Practices](#bestpractices)

---

## <a name="introduction"></a>1. Introduction to Generics

### What Are Generics?

Generics enable **type-safe** collections and methods by allowing you to specify the type of objects a collection can hold.

### Why Generics Matter

**Benefits**:
- ✅ Type safety at compile time
- ✅ Elimination of casting
- ✅ Better code readability
- ✅ Enables code reuse
- ✅ Catches errors early

**Before Generics**:
```java
List list = new ArrayList();
list.add("Hello");
list.add(42);

String str = (String) list.get(0);  // Casting required
Integer num = (Integer) list.get(1);
```

**After Generics**:
```java
List<String> list = new ArrayList<>();
list.add("Hello");
// list.add(42);  // ❌ Compile error

String str = list.get(0);  // No casting needed
```

---

## <a name="genericclasses"></a>2. Generic Classes

### Basic Generic Class

```java
public class Box<T> {
    private T value;
    
    public void set(T value) {
        this.value = value;
    }
    
    public T get() {
        return value;
    }
}

// Usage
Box<String> stringBox = new Box<>();
stringBox.set("Hello");
String str = stringBox.get();

Box<Integer> intBox = new Box<>();
intBox.set(42);
Integer num = intBox.get();
```

### Multiple Type Parameters

```java
public class Pair<K, V> {
    private K key;
    private V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() {
        return key;
    }
    
    public V getValue() {
        return value;
    }
}

// Usage
Pair<String, Integer> pair = new Pair<>("age", 30);
String key = pair.getKey();
Integer value = pair.getValue();
```

### Generic Inheritance

```java
// Generic parent class
public class Container<T> {
    protected T item;
    
    public void setItem(T item) {
        this.item = item;
    }
    
    public T getItem() {
        return item;
    }
}

// Generic child class
public class SpecialContainer<T> extends Container<T> {
    public void printItem() {
        System.out.println(item);
    }
}

// Concrete child class
public class StringContainer extends Container<String> {
    @Override
    public void setItem(String item) {
        this.item = item.toUpperCase();
    }
}

// Usage
SpecialContainer<Integer> container = new SpecialContainer<>();
container.setItem(42);
System.out.println(container.getItem());

StringContainer stringContainer = new StringContainer();
stringContainer.setItem("hello");
System.out.println(stringContainer.getItem());  // HELLO
```

---

## <a name="genericmethods"></a>3. Generic Methods

### Basic Generic Method

```java
public class Utilities {
    // Generic method
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.println(element);
        }
    }
    
    // Generic method with return type
    public static <T> T getFirst(T[] array) {
        return array.length > 0 ? array[0] : null;
    }
    
    // Generic method with multiple type parameters
    public static <K, V> void printPair(K key, V value) {
        System.out.println(key + " -> " + value);
    }
}

// Usage
Integer[] intArray = {1, 2, 3};
Utilities.printArray(intArray);

String[] strArray = {"a", "b", "c"};
String first = Utilities.getFirst(strArray);

Utilities.printPair("name", "John");
```

### Generic Methods in Generic Classes

```java
public class GenericClass<T> {
    // Instance method using class type parameter
    public void printItem(T item) {
        System.out.println(item);
    }
    
    // Generic method with its own type parameter
    public <U> void printPair(T first, U second) {
        System.out.println(first + " -> " + second);
    }
    
    // Generic method returning generic type
    public <U> Pair<T, U> createPair(U second) {
        return new Pair<>(null, second);
    }
}

// Usage
GenericClass<String> gc = new GenericClass<>();
gc.printItem("Hello");
gc.printPair("name", 30);
Pair<String, Integer> pair = gc.createPair(42);
```

---

## <a name="bounded"></a>4. Bounded Type Parameters

### Upper Bounded Wildcards

```java
// Bounded type parameter - T must be Number or subclass
public class NumberBox<T extends Number> {
    private T value;
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public double doubleValue() {
        return value.doubleValue();
    }
}

// Usage
NumberBox<Integer> intBox = new NumberBox<>();
intBox.setValue(42);
System.out.println(intBox.doubleValue());  // 42.0

// NumberBox<String> strBox = new NumberBox<>();  // ❌ Compile error
```

### Multiple Bounds

```java
// T must implement both Comparable and Serializable
public class ComparableSerializable<T extends Comparable<T> & Serializable> {
    private T value;
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }
}

// Usage
ComparableSerializable<String> cs = new ComparableSerializable<>();
cs.setValue("Hello");
```

### Bounded Generic Methods

```java
public class Utilities {
    // Method that works with any Number
    public static <T extends Number> double sum(T[] array) {
        double sum = 0;
        for (T element : array) {
            sum += element.doubleValue();
        }
        return sum;
    }
    
    // Method that works with Comparable types
    public static <T extends Comparable<T>> T max(T[] array) {
        T max = array[0];
        for (T element : array) {
            if (element.compareTo(max) > 0) {
                max = element;
            }
        }
        return max;
    }
}

// Usage
Integer[] intArray = {1, 2, 3, 4, 5};
System.out.println(Utilities.sum(intArray));  // 15.0

String[] strArray = {"apple", "banana", "cherry"};
System.out.println(Utilities.max(strArray));  // cherry
```

---

## <a name="wildcards"></a>5. Wildcards

### Unbounded Wildcards

```java
public class Utilities {
    // Works with any type
    public static void printList(List<?> list) {
        for (Object element : list) {
            System.out.println(element);
        }
    }
    
    public static int getSize(List<?> list) {
        return list.size();
    }
}

// Usage
List<String> stringList = Arrays.asList("a", "b", "c");
Utilities.printList(stringList);

List<Integer> intList = Arrays.asList(1, 2, 3);
Utilities.printList(intList);
```

### Upper Bounded Wildcards

```java
public class Utilities {
    // Works with Number and its subclasses
    public static double sumNumbers(List<? extends Number> list) {
        double sum = 0;
        for (Number number : list) {
            sum += number.doubleValue();
        }
        return sum;
    }
    
    // Works with Comparable types
    public static <T extends Comparable<? super T>> T max(List<T> list) {
        T max = list.get(0);
        for (T element : list) {
            if (element.compareTo(max) > 0) {
                max = element;
            }
        }
        return max;
    }
}

// Usage
List<Integer> intList = Arrays.asList(1, 2, 3);
System.out.println(Utilities.sumNumbers(intList));

List<Double> doubleList = Arrays.asList(1.5, 2.5, 3.5);
System.out.println(Utilities.sumNumbers(doubleList));
```

### Lower Bounded Wildcards

```java
public class Utilities {
    // Works with Integer and its superclasses
    public static void addNumbers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        list.add(3);
    }
    
    // Producer extends, Consumer super
    public static <T> void copy(List<? extends T> source, List<? super T> dest) {
        for (T element : source) {
            dest.add(element);
        }
    }
}

// Usage
List<Number> numberList = new ArrayList<>();
Utilities.addNumbers(numberList);

List<Integer> source = Arrays.asList(1, 2, 3);
List<Number> dest = new ArrayList<>();
Utilities.copy(source, dest);
```

### PECS Rule

```java
// Producer Extends, Consumer Super
public class Collections {
    // Source is producer (extends)
    public static <T> void copy(List<? extends T> source, List<? super T> dest) {
        for (T element : source) {
            dest.add(element);
        }
    }
    
    // Example: Reading from source
    public static <T> List<T> unmodifiableList(List<? extends T> list) {
        return new ArrayList<>(list);
    }
    
    // Example: Writing to destination
    public static <T> void fill(List<? super T> list, T value) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, value);
        }
    }
}
```

---

## <a name="erasure"></a>6. Type Erasure

### How Type Erasure Works

```java
// At compile time
List<String> stringList = new ArrayList<>();
stringList.add("Hello");
String str = stringList.get(0);

// After type erasure (at runtime)
List stringList = new ArrayList();
stringList.add("Hello");
String str = (String) stringList.get(0);  // Implicit cast added
```

### Implications of Type Erasure

```java
public class TypeErasureExample {
    // ❌ CANNOT DO THIS - Erasure makes them identical
    // public void method(List<String> list) { }
    // public void method(List<Integer> list) { }
    
    // ✅ CAN DO THIS - Different after erasure
    public void method(List<String> list) { }
    public void method(Set<String> set) { }
    
    // ❌ CANNOT CREATE INSTANCES
    // List<String>[] array = new List<String>[10];  // Compile error
    
    // ✅ USE WILDCARD ARRAY
    List<?>[] array = new List[10];
    array[0] = new ArrayList<String>();
    
    // ❌ CANNOT USE instanceof WITH GENERICS
    // if (list instanceof List<String>) { }  // Compile error
    
    // ✅ USE WILDCARD
    if (list instanceof List<?>) { }
}
```

### Bridge Methods

```java
public class BridgeMethodExample {
    // Generic class
    public static class Container<T> {
        public void set(T value) {
            System.out.println("Setting: " + value);
        }
    }
    
    // Concrete subclass
    public static class StringContainer extends Container<String> {
        @Override
        public void set(String value) {
            System.out.println("Setting string: " + value);
        }
    }
    
    // Compiler generates bridge method:
    // public void set(Object value) {
    //     set((String) value);
    // }
}

// Usage
Container<String> container = new StringContainer();
container.set("Hello");  // Calls bridge method, then actual method
```

---

## <a name="collections"></a>7. Generic Collections

### Generic List

```java
public class GenericListExample {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("Hello");
        list.add("World");
        
        for (String str : list) {
            System.out.println(str);
        }
        
        // Type-safe operations
        String first = list.get(0);
        list.set(0, "Hi");
    }
}
```

### Generic Map

```java
public class GenericMapExample {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("age", 30);
        map.put("count", 5);
        
        // Type-safe access
        Integer age = map.get("age");
        
        // Type-safe iteration
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + " -> " + value);
        }
    }
}
```

### Generic Set

```java
public class GenericSetExample {
    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        set.add("apple");
        set.add("banana");
        set.add("cherry");
        
        // Type-safe iteration
        for (String fruit : set) {
            System.out.println(fruit);
        }
        
        // Type-safe operations
        boolean contains = set.contains("apple");
    }
}
```

---

## <a name="advanced"></a>8. Advanced Generics

### Recursive Type Bounds

```java
// T must be comparable to itself
public class Node<T extends Comparable<T>> implements Comparable<Node<T>> {
    private T value;
    
    public Node(T value) {
        this.value = value;
    }
    
    @Override
    public int compareTo(Node<T> other) {
        return value.compareTo(other.value);
    }
}

// Usage
Node<Integer> node1 = new Node<>(10);
Node<Integer> node2 = new Node<>(20);
System.out.println(node1.compareTo(node2));  // -1
```

### Generic Interfaces

```java
// Generic interface
public interface Container<T> {
    void add(T item);
    T get(int index);
    int size();
}

// Implementation
public class ListContainer<T> implements Container<T> {
    private List<T> items = new ArrayList<>();
    
    @Override
    public void add(T item) {
        items.add(item);
    }
    
    @Override
    public T get(int index) {
        return items.get(index);
    }
    
    @Override
    public int size() {
        return items.size();
    }
}

// Usage
Container<String> container = new ListContainer<>();
container.add("Hello");
container.add("World");
System.out.println(container.get(0));
```

### Generic Functional Interfaces

```java
// Generic functional interface
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}

// Usage
Function<Integer, String> intToString = n -> "Number: " + n;
String result = intToString.apply(42);

Function<String, Integer> stringLength = s -> s.length();
Integer length = stringLength.apply("Hello");
```

### Covariance and Contravariance

```java
public class Variance {
    // Covariance: ? extends T (read-only)
    public static void readFromList(List<? extends Number> list) {
        for (Number num : list) {
            System.out.println(num);
        }
    }
    
    // Contravariance: ? super T (write-only)
    public static void writeToList(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        list.add(3);
    }
    
    // Invariance: T (read and write)
    public static void processExactList(List<Integer> list) {
        list.add(1);
        Integer num = list.get(0);
    }
}

// Usage
List<Integer> intList = new ArrayList<>();
readFromList(intList);  // Covariance

List<Number> numberList = new ArrayList<>();
writeToList(numberList);  // Contravariance

processExactList(intList);  // Invariance
```

---

## <a name="bestpractices"></a>9. Best Practices

### Use Generics for Type Safety

```java
// ❌ AVOID - No generics
List list = new ArrayList();
list.add("Hello");
list.add(42);
String str = (String) list.get(0);

// ✅ PREFER - With generics
List<String> list = new ArrayList<>();
list.add("Hello");
// list.add(42);  // Compile error
String str = list.get(0);
```

### Use Bounded Type Parameters

```java
// ❌ AVOID - Too generic
public <T> T max(T[] array) {
    // Can't compare T objects
    return array[0];
}

// ✅ PREFER - Bounded
public <T extends Comparable<T>> T max(T[] array) {
    T max = array[0];
    for (T element : array) {
        if (element.compareTo(max) > 0) {
            max = element;
        }
    }
    return max;
}
```

### Use Wildcards Appropriately

```java
// ❌ AVOID - Unnecessary type parameter
public void printList(List<Object> list) {
    for (Object element : list) {
        System.out.println(element);
    }
}

// ✅ PREFER - Wildcard
public void printList(List<?> list) {
    for (Object element : list) {
        System.out.println(element);
    }
}
```

### Avoid Raw Types

```java
// ❌ AVOID - Raw type
List list = new ArrayList();
list.add("Hello");
String str = (String) list.get(0);

// ✅ PREFER - Generic type
List<String> list = new ArrayList<>();
list.add("Hello");
String str = list.get(0);
```

---

## 🎯 Key Takeaways

1. **Generics** provide type safety at compile time
2. **Type parameters** enable code reuse
3. **Bounded types** restrict type parameters
4. **Wildcards** provide flexibility
5. **Type erasure** happens at runtime
6. **PECS rule** guides wildcard usage
7. **Avoid raw types** for type safety
8. **Use generics** in collections and methods
9. **Understand variance** for proper usage
10. **Test thoroughly** with different types

---

**Module 13 - Generics**  
*Master type-safe programming with generics*