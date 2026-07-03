# Collections — Self-Assessment Quiz

## Question 1

Which List implementation provides O(1) `get(int index)`?

A) LinkedList
B) ArrayList
C) Both
D) Neither

<details><summary>Answer</summary>B — ArrayList provides O(1) random access; LinkedList requires O(n) traversal.</details>

## Question 2

What happens when you add a duplicate element to a HashSet?

A) Exception is thrown
B) The add() returns false
C) The old element is replaced
D) Both elements are stored

<details><summary>Answer</summary>B — `add()` returns `false` if element already exists.</details>

## Question 3

Which Map implementation maintains keys in insertion order?

A) HashMap
B) TreeMap
C) LinkedHashMap
D) ConcurrentHashMap

<details><summary>Answer</summary>C — LinkedHashMap maintains a doubly-linked list of entries for iteration ordering.</details>

## Question 4

What is the default initial capacity of HashMap?

A) 8
B) 16
C) 32
D) 64

<details><summary>Answer</summary>B — Default initial capacity is 16 (power of 2).</details>

## Question 5

Which of the following throws ConcurrentModificationException?

A) Modifying a ConcurrentHashMap during iteration
B) Modifying an ArrayList via iterator.remove() during iteration
C) Modifying an ArrayList via list.remove() during for-each iteration
D) Modifying a CopyOnWriteArrayList during iteration

<details><summary>Answer</summary>C — Modifying an ArrayList directly during for-each (which uses an iterator) throws CME. ConcurrentHashMap and CopyOnWriteArrayList are safe. Iterator.remove() is the correct way.</details>

## Question 6

What is the time complexity of `contains()` on a HashSet?

A) O(1) average
B) O(log n)
C) O(n)
D) O(n²)

<details><summary>Answer</summary>A — HashSet provides O(1) average-time `contains()` via hash table lookup.</details>

## Question 7

Which collection should you use for a FIFO queue?

A) Stack
B) ArrayList
C) ArrayDeque
D) TreeSet

<details><summary>Answer</summary>C — ArrayDeque is the best general-purpose Queue/Deque implementation.</details>

## Question 8

What does `Collections.unmodifiableList()` return?

A) A new immutable list with copied data
B) A view of the original list that throws on mutation
C) A synchronized list
D) A list with all elements sorted

<details><summary>Answer</summary>B — It returns a view/wrapper; modifications to the backing list are still visible through the wrapper.</details>

## Question 9

Which interface do LinkedHashSet and TreeSet both implement?

A) List
B) Queue
C) Set
D) Deque

<details><summary>Answer</summary>C — Both implement Set. LinkedHashSet extends HashSet, TreeSet implements NavigableSet.</details>

## Question 10

What is the purpose of `Map.computeIfAbsent()`?

A) Compute a value only if the key is absent, atomically
B) Remove entries where the value is absent
C) Compute all absent values in the map
D) Replace values that are null

<details><summary>Answer</summary>A — `computeIfAbsent()` atomically checks if key is absent, computes the value, and puts it if absent.</details>
