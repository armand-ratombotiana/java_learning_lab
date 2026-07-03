# Mini Project: Advanced Generic Data Pipeline

## Objective
Build a type-safe data processing pipeline. You will implement the Self-Type idiom (CRTP) for a fluent builder hierarchy, use the PECS rule to safely transfer data between collections, and use recursive type bounds to find the maximum element.

## Prerequisites
*   Java 17+

## Step 1: The Self-Type Idiom (Fluent Builder Hierarchy)
We want a base `QueryBuilder` and a specific `UserQueryBuilder`. We must use the Self-Type idiom so methods inherited from the base class return the specific subclass.

```java
// Base Builder using CRTP
abstract class QueryBuilder<SELF extends QueryBuilder<SELF>> {
    protected int limit = 10;

    // The abstract method that subclasses must implement to return 'this'
    protected abstract SELF self();

    public SELF limit(int limit) {
        this.limit = limit;
        return self(); // Returns the specific subclass type
    }
}

// Concrete Subclass
class UserQueryBuilder extends QueryBuilder<UserQueryBuilder> {
    private String role;

    @Override
    protected UserQueryBuilder self() {
        return this;
    }

    public UserQueryBuilder role(String role) {
        this.role = role;
        return self();
    }

    public String build() {
        return "Query: SELECT * FROM Users WHERE role='" + role + "' LIMIT " + limit;
    }
}
```

## Step 2: Recursive Type Bounds
Create a generic utility method to find the maximum element in a collection. The elements must be mutually comparable.

```java
import java.util.Collection;

public class GenericUtils {
    
    // T must implement Comparable, and it must compare to itself (T)
    public static <T extends Comparable<T>> T findMax(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) return null;

        T max = collection.iterator().next();
        for (T item : collection) {
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }
}
```

## Step 3: The PECS Rule (Producer Extends, Consumer Super)
Create a method that copies data from one collection to another. The source is a Producer (`extends`), and the destination is a Consumer (`super`).

```java
import java.util.List;

public class DataTransfer {
    
    // src produces T objects. dest consumes T objects.
    public static <T> void transferData(List<? extends T> src, List<? super T> dest) {
        for (T item : src) {
            // We can safely read from src because we know everything is at least a T.
            // We can safely write to dest because we know it can hold at least a T.
            dest.add(item);
        }
    }
}
```

## Step 4: Execute the Pipeline
```java
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- 1. Testing Self-Type Idiom ---");
        // We can chain limit() (from base class) and role() (from subclass) seamlessly!
        String query = new UserQueryBuilder()
                .limit(50)
                .role("ADMIN")
                .build();
        System.out.println(query);

        System.out.println("\n--- 2. Testing Recursive Bounds ---");
        List<Integer> numbers = List.of(10, 42, 5, 99, 1);
        Integer maxInt = GenericUtils.findMax(numbers);
        System.out.println("Max Number: " + maxInt);
        
        List<String> words = List.of("Apple", "Zebra", "Banana");
        String maxWord = GenericUtils.findMax(words);
        System.out.println("Max Word: " + maxWord);

        System.out.println("\n--- 3. Testing PECS Rule ---");
        // Source is a List of Integers
        List<Integer> intSource = List.of(1, 2, 3);
        
        // Destination is a List of Numbers (Number is a superclass of Integer)
        List<Number> numDest = new ArrayList<>();
        
        // transferData(List<? extends Number>, List<? super Number>)
        DataTransfer.transferData(intSource, numDest);
        
        System.out.println("Transferred Data to Number List: " + numDest);
    }
}
```

## Expected Output
```text
--- 1. Testing Self-Type Idiom ---
Query: SELECT * FROM Users WHERE role='ADMIN' LIMIT 50

--- 2. Testing Recursive Bounds ---
Max Number: 99
Max Word: Zebra

--- 3. Testing PECS Rule ---
Transferred Data to Number List: [1, 2, 3]
```