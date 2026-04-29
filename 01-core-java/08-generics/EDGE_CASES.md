# Module 13: Generics - Edge Cases & Pitfalls

**Critical Pitfalls**: 16  
**Prevention Strategies**: 16  
**Real-World Scenarios**: 12

---

## 🚨 Critical Pitfalls & Prevention

### 1. Raw Types and Type Safety Loss

**❌ PITFALL**:
```java
// Using raw type
List list = new ArrayList();
list.add("Hello");
list.add(42);

// No compile-time checking
String str = (String) list.get(0);  // Works
Integer num = (Integer) list.get(1);  // Works
// Integer num = (Integer) list.get(0);  // ❌ Runtime error!
```

**✅ PREVENTION**:
```java
// Use generic type
List<String> list = new ArrayList<>();
list.add("Hello");
// list.add(42);  // ❌ Compile error - caught early

String str = list.get(0);  // No casting needed
// Integer num = (Integer) list.get(1);  // ❌ Compile error
```

**Why It Matters**: Raw types lose type safety and require casting, leading to runtime errors.

---

### 2. Type Erasure and instanceof

**❌ PITFALL**:
```java
// Cannot use instanceof with generics
List<String> stringList = new ArrayList<>();
List<Integer> intList = new ArrayList<>();

// ❌ Compile error
// if (stringList instanceof List<String>) { }

// ❌ Runtime error - both are List at runtime
if (stringList instanceof List) {
    // Both stringList and intList match
}
```

**✅ PREVENTION**:
```java
// Use wildcard with instanceof
List<String> stringList = new ArrayList<>();

// ✅ Correct
if (stringList instanceof List<?>) {
    System.out.println("It's a list");
}

// Or use a helper method
public static <T> boolean isList(Object obj, Class<T> type) {
    if (!(obj instanceof List)) {
        return false;
    }
    List<?> list = (List<?>) obj;
    return !list.isEmpty() && type.isInstance(list.get(0));
}
```

**Why It Matters**: Type erasure removes generic information at runtime, making instanceof checks impossible.

---

### 3. Generic Array Creation

**❌ PITFALL**:
```java
// ❌ Cannot create generic arrays
// List<String>[] array = new List<String>[10];  // Compile error

// Workaround that's unsafe
List[] array = new List[10];
array[0] = new ArrayList<String>();
array[1] = new ArrayList<Integer>();  // ❌ Type mismatch at runtime
```

**✅ PREVENTION**:
```java
// Option 1: Use wildcard array
List<?>[] array = new List[10];
array[0] = new ArrayList<String>();
array[1] = new ArrayList<Integer>();  // ✅ Safe

// Option 2: Use List of arrays
List<List<String>> listOfLists = new ArrayList<>();
listOfLists.add(new ArrayList<String>());

// Option 3: Use List instead of array
List<List<String>> lists = new ArrayList<>();
lists.add(new ArrayList<String>());
```

**Why It Matters**: Generic arrays are unsafe due to type erasure.

---

### 4. Unchecked Warnings

**❌ PITFALL**:
```java
// Unchecked cast warning
List list = new ArrayList();
List<String> stringList = (List<String>) list;  // ⚠️ Unchecked warning

// Unchecked assignment warning
List<String> list2 = new ArrayList();  // ⚠️ Unchecked warning
```

**✅ PREVENTION**:
```java
// Option 1: Use proper generics
List<String> stringList = new ArrayList<>();

// Option 2: Suppress warning if necessary
@SuppressWarnings("unchecked")
List<String> stringList = (List<String>) (List) list;

// Option 3: Use wildcard
List<?> unknownList = list;
```

**Why It Matters**: Unchecked warnings indicate potential type safety issues.

---

### 5. Wildcard Limitations

**❌ PITFALL**:
```java
// Cannot write to wildcard list
List<?> list = new ArrayList<String>();
// list.add("Hello");  // ❌ Compile error

// Cannot read specific type
Object obj = list.get(0);  // Returns Object, not String
```

**✅ PREVENTION**:
```java
// Option 1: Use specific type
List<String> list = new ArrayList<>();
list.add("Hello");
String str = list.get(0);

// Option 2: Use wildcard capture
public static <T> void addToList(List<? extends T> list, T element) {
    // Can work with T
}

// Option 3: Use lower bounded wildcard for writing
List<? super String> list = new ArrayList<Object>();
list.add("Hello");  // ✅ Can write
```

**Why It Matters**: Wildcards have limitations on reading and writing.

---

### 6. Type Parameter Shadowing

**❌ PITFALL**:
```java
public class Container<T> {
    public <T> void method(T param) {  // ❌ Shadows class type parameter
        // T here refers to method parameter, not class parameter
    }
}

// Confusing usage
Container<String> container = new Container<>();
container.method(42);  // ✅ Works but confusing
```

**✅ PREVENTION**:
```java
// Option 1: Use different name
public class Container<T> {
    public <U> void method(U param) {  // ✅ Clear distinction
        // U is method parameter, T is class parameter
    }
}

// Option 2: Use class type parameter
public class Container<T> {
    public void method(T param) {  // ✅ Uses class type parameter
    }
}
```

**Why It Matters**: Type parameter shadowing causes confusion and bugs.

---

### 7. Bounded Type Parameter Misuse

**❌ PITFALL**:
```java
// Too restrictive bound
public <T extends String> void process(T param) {
    // T can only be String, so why use generic?
}

// Too loose bound
public <T> T max(T[] array) {
    // Cannot compare T objects
    return array[0];
}
```

**✅ PREVENTION**:
```java
// Option 1: Remove unnecessary generic
public void process(String param) {
    // No need for generic if only String
}

// Option 2: Add proper bound
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

**Why It Matters**: Improper bounds reduce code reusability or cause compilation errors.

---

### 8. Invariance Issues

**❌ PITFALL**:
```java
// Cannot assign List<String> to List<Object>
List<String> stringList = new ArrayList<>();
// List<Object> objectList = stringList;  // ❌ Compile error

// Trying to work around with raw type
List objectList = stringList;  // ⚠️ Unchecked warning
objectList.add(42);  // ❌ Runtime error when accessing as String
```

**✅ PREVENTION**:
```java
// Option 1: Use wildcard
List<? extends Object> list = new ArrayList<String>();

// Option 2: Use specific type
List<String> stringList = new ArrayList<>();

// Option 3: Use lower bounded wildcard
List<? super String> list = new ArrayList<Object>();
```

**Why It Matters**: Generics are invariant to maintain type safety.

---

### 9. Covariance Misunderstanding

**❌ PITFALL**:
```java
// Trying to use covariance incorrectly
List<? extends Number> list = new ArrayList<Integer>();
// list.add(1);  // ❌ Compile error - cannot write

// Trying to read as specific type
Number num = list.get(0);  // ✅ Works
// Integer num = list.get(0);  // ❌ Compile error
```

**✅ PREVENTION**:
```java
// Correct covariance usage
List<? extends Number> list = new ArrayList<Integer>();

// Read as upper bound
Number num = list.get(0);  // ✅ Correct

// For writing, use specific type
List<Integer> intList = new ArrayList<>();
intList.add(1);  // ✅ Correct
```

**Why It Matters**: Covariance allows reading but not writing.

---

### 10. Contravariance Misunderstanding

**❌ PITFALL**:
```java
// Trying to use contravariance incorrectly
List<? super Integer> list = new ArrayList<Number>();
// Integer num = list.get(0);  // ❌ Compile error - returns Object

// Trying to write wrong type
// list.add(1.5);  // ❌ Compile error
```

**✅ PREVENTION**:
```java
// Correct contravariance usage
List<? super Integer> list = new ArrayList<Number>();

// Write Integer
list.add(1);  // ✅ Correct

// Read as Object
Object obj = list.get(0);  // ✅ Correct
```

**Why It Matters**: Contravariance allows writing but reading as Object.

---

### 11. Generic Method Type Inference Failure

**❌ PITFALL**:
```java
public static <T> T getValue(T value) {
    return value;
}

// Type inference fails
// String str = getValue(42);  // ❌ Type mismatch

// Explicit type parameter needed
String str = getValue("Hello");  // ✅ Works
```

**✅ PREVENTION**:
```java
// Option 1: Provide explicit type parameter
String str = GenericClass.<String>getValue("Hello");

// Option 2: Ensure argument matches expected type
String str = getValue("Hello");  // ✅ Type inference works

// Option 3: Use bounded type parameter
public static <T extends Number> T getNumber(T value) {
    return value;
}
```

**Why It Matters**: Type inference can fail with complex generics.

---

### 12. Recursive Type Bound Misuse

**❌ PITFALL**:
```java
// Incorrect recursive bound
public <T extends Comparable> T max(T[] array) {
    // T is Comparable but not necessarily to itself
    return array[0];
}

// Correct but confusing
public <T extends Comparable<T>> T max(T[] array) {
    // T is comparable to itself
    return array[0];
}
```

**✅ PREVENTION**:
```java
// Clear recursive bound
public <T extends Comparable<T>> T max(T[] array) {
    T max = array[0];
    for (T element : array) {
        if (element.compareTo(max) > 0) {
            max = element;
        }
    }
    return max;
}

// Or use wildcard
public <T extends Comparable<? super T>> T max(T[] array) {
    T max = array[0];
    for (T element : array) {
        if (element.compareTo(max) > 0) {
            max = element;
        }
    }
    return max;
}
```

**Why It Matters**: Recursive bounds ensure proper type relationships.

---

### 13-16: Additional Pitfalls

**13. Generic Exception Catching**: Cannot catch generic exceptions
**14. Generic Static Members**: Cannot use type parameters in static members
**15. Type Erasure and Reflection**: Generic information lost at runtime
**16. Bridge Method Confusion**: Compiler-generated methods can be confusing

---

## 📋 Prevention Checklist

- ✅ Always use generic types, avoid raw types
- ✅ Use bounded type parameters when needed
- ✅ Understand PECS rule for wildcards
- ✅ Don't use instanceof with generics
- ✅ Use wildcard arrays instead of generic arrays
- ✅ Suppress unchecked warnings only when necessary
- ✅ Avoid type parameter shadowing
- ✅ Use proper bounds for type parameters
- ✅ Understand invariance, covariance, contravariance
- ✅ Test generic code with different types
- ✅ Document generic type constraints
- ✅ Use explicit type parameters when needed
- ✅ Avoid mixing raw and generic types
- ✅ Use helper methods for complex generics
- ✅ Keep generics simple and readable

---

**Module 13 - Generics Edge Cases**  
*Master the pitfalls and prevention strategies*