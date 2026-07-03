# Mini Project: Building a Functional Cons List

## Objective
Build a purely functional, immutable Singly Linked List (often called a Cons List in Lisp/Scala). You will implement methods to prepend elements, reverse the list, and use a functional `foldLeft` (reduce) operation to process the data without mutating any state.

## Prerequisites
*   Java 17+

## Step 1: Define the Functional List Interface
Create a sealed interface to represent the two possible states of a functional list: Empty (`Nil`) or Non-Empty (`Cons`).

```java
public sealed interface FunList<T> permits Nil, Cons {
    
    // Factory method for an empty list
    static <T> FunList<T> empty() {
        return new Nil<>();
    }

    // Factory method for a single element
    static <T> FunList<T> of(T element) {
        return new Cons<>(element, empty());
    }

    // Core operations
    boolean isEmpty();
    T head();
    FunList<T> tail();
    
    // Functional Operations
    FunList<T> prepend(T element);
    FunList<T> reverse();
    
    // Fold (Reduce) operation
    <U> U foldLeft(U identity, java.util.function.BiFunction<U, T, U> accumulator);
}
```

## Step 2: Implement the Empty List (`Nil`)
The `Nil` class represents the end of the list. Operations on it usually return itself or throw an exception.

```java
import java.util.NoSuchElementException;

public final class Nil<T> implements FunList<T> {
    
    @Override public boolean isEmpty() { return true; }
    
    @Override public T head() { throw new NoSuchElementException("head of empty list"); }
    
    @Override public FunList<T> tail() { throw new NoSuchElementException("tail of empty list"); }

    @Override public FunList<T> prepend(T element) {
        return new Cons<>(element, this);
    }

    @Override public FunList<T> reverse() {
        return this; // Reversing empty is empty
    }

    @Override public <U> U foldLeft(U identity, java.util.function.BiFunction<U, T, U> accumulator) {
        return identity; // Base case for recursion/folding
    }

    @Override public String toString() { return "Nil"; }
}
```

## Step 3: Implement the Non-Empty List (`Cons`)
The `Cons` (Construct) class holds a value and a pointer to the rest of the list. Notice that all fields are `private final` and there are no setters.

```java
public final class Cons<T> implements FunList<T> {
    private final T head;
    private final FunList<T> tail;

    public Cons(T head, FunList<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override public boolean isEmpty() { return false; }
    @Override public T head() { return head; }
    @Override public FunList<T> tail() { return tail; }

    @Override
    public FunList<T> prepend(T element) {
        // O(1) time complexity. Structural sharing!
        return new Cons<>(element, this);
    }

    @Override
    public FunList<T> reverse() {
        // We use foldLeft to build a new list in reverse order
        return foldLeft(FunList.empty(), (acc, element) -> acc.prepend(element));
    }

    @Override
    public <U> U foldLeft(U identity, java.util.function.BiFunction<U, T, U> accumulator) {
        // Note: In Java, deep recursion can cause StackOverflow. 
        // For a production library, this should be written as a while loop.
        // We use recursion here for functional purity demonstration.
        U newIdentity = accumulator.apply(identity, head);
        return tail.foldLeft(newIdentity, accumulator);
    }

    @Override
    public String toString() {
        return head + " :: " + tail.toString();
    }
}
```

## Step 4: Test the Functional List
Demonstrate structural sharing and the `foldLeft` operation.

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 1. Building the List ---");
        // Build list: 1 :: 2 :: 3 :: Nil
        FunList<Integer> list1 = FunList.<Integer>empty()
                .prepend(3)
                .prepend(2)
                .prepend(1);
        
        System.out.println("List 1: " + list1);

        System.out.println("\n--- 2. Structural Sharing ---");
        // list2 shares the [2 :: 3 :: Nil] part of list1
        FunList<Integer> list2 = list1.tail().prepend(99);
        
        System.out.println("List 1: " + list1); // Unchanged!
        System.out.println("List 2: " + list2);

        System.out.println("\n--- 3. Reverse ---");
        FunList<Integer> reversed = list1.reverse();
        System.out.println("Reversed: " + reversed);

        System.out.println("\n--- 4. FoldLeft (Sum) ---");
        // Sum all elements: 0 + 1 + 2 + 3
        int sum = list1.foldLeft(0, Integer::sum);
        System.out.println("Sum of List 1: " + sum);
        
        System.out.println("\n--- 5. FoldLeft (String Concat) ---");
        String joined = list1.foldLeft("", (acc, val) -> acc + val + "-");
        System.out.println("Joined: " + joined);
    }
}
```

## Expected Output
```text
--- 1. Building the List ---
List 1: 1 :: 2 :: 3 :: Nil

--- 2. Structural Sharing ---
List 1: 1 :: 2 :: 3 :: Nil
List 2: 99 :: 2 :: 3 :: Nil

--- 3. Reverse ---
Reversed: 3 :: 2 :: 1 :: Nil

--- 4. FoldLeft (Sum) ---
Sum of List 1: 6

--- 5. FoldLeft (String Concat) ---
Joined: 1-2-3-
```