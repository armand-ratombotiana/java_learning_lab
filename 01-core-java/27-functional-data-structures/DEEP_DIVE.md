# Deep Dive: Functional Data Structures

## 1. The Mutability Problem
Standard Java collections (`ArrayList`, `HashMap`) are mutable. When you add an element, the collection changes in place. In highly concurrent or complex functional systems, this shared mutable state leads to race conditions, requires heavy synchronization (locking), and makes it difficult to reason about the code.

While Java 9 introduced `List.of()` (Immutable Collections), these are "read-only." If you have a list of 10,000 items and want to add one more, you must copy all 10,000 items into a new list. This is extremely inefficient.

## 2. Persistent Data Structures
Functional programming languages (like Scala, Clojure, or Haskell) solve this using **Persistent Data Structures**. A persistent data structure is immutable, but when "modified," it returns a *new* version of itself while preserving the old version, sharing as much structure as possible between the two.

### The Singly Linked List (Cons List)
The simplest functional data structure is the immutable singly linked list. It consists of two parts:
*   **Head**: The first element.
*   **Tail**: Another list containing the rest of the elements.

```java
// A conceptual functional list
public class FunList<T> {
    private final T head;
    private final FunList<T> tail;

    public FunList(T head, FunList<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    // "Adding" an element doesn't mutate the list. 
    // It creates a new node that points to the existing list!
    public FunList<T> prepend(T element) {
        return new FunList<>(element, this); // O(1) time complexity!
    }
}
```
*   **Structural Sharing**: If `list1` is `[2, 3]`, and you do `list2 = list1.prepend(1)`, `list2` is `[1, 2, 3]`. `list2` simply holds `1` and points to `list1`. No copying of `[2, 3]` is required. Both lists exist simultaneously and safely in memory.

## 3. Advanced Persistent Structures (Tries)
While prepending to a linked list is $O(1)$, accessing the $n$-th element is $O(N)$. To achieve near $O(1)$ performance for lists and maps, functional languages use **Hash Array Mapped Tries (HAMT)** or **Bitmapped Vector Tries**.

*   Instead of a flat array, data is stored in a wide, shallow tree (e.g., a tree where each node has 32 branches).
*   When you "modify" an element, the structure doesn't copy the whole tree. It only copies the nodes on the path from the root down to the modified element (Path Copying).
*   Because the tree is very shallow (a depth of 6 can hold over 1 billion elements), the cost of copying that path is effectively constant time $O(1)$.

*Note: Java does not have these built-in. You must use third-party libraries like **Vavr** or **Eclipse Collections** to use persistent tries in Java.*

## 4. Lazy Evaluation
Functional data structures often employ lazy evaluation. Instead of computing a result immediately, they store the *computation* (a thunk or lambda) and only execute it when the value is actually requested.

### Lazy Sequences (Streams)
Java's `Stream` API is a prime example of lazy evaluation.
```java
Stream<Integer> stream = list.stream()
    .filter(n -> { System.out.println("Filtering " + n); return n % 2 == 0; })
    .map(n -> { System.out.println("Mapping " + n); return n * 2; });

System.out.println("Stream created, but no work done yet.");
// The filter and map lambdas do not execute until a terminal operation is called.
stream.findFirst(); 
```
*   **Short-Circuiting**: Because it's lazy, `findFirst()` will pull elements through the pipeline one by one. As soon as it finds the first match, it stops. It doesn't process the rest of the list, saving massive amounts of CPU time.

## 5. Tail Recursion
Functional data structures rely heavily on recursion for traversal. However, deep recursion causes `StackOverflowError`s in Java.
Functional languages use **Tail Call Optimization (TCO)** to convert recursive calls into loops at the compiler level. 
*   *Limitation*: The Java compiler does **not** currently support TCO. Therefore, deep recursive operations on functional lists in Java must be manually rewritten as loops or handled using complex trampoline patterns.