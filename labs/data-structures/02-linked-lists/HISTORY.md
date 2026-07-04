# History of Linked Lists

## Origins (1950s)

Linked lists were invented by **Allen Newell, Cliff Shaw, and Herbert Simon** around 1955–1956 for the **Information Processing Language (IPL)**, used in artificial intelligence research and the Logic Theorist. IPL was one of the first list-processing languages.

## Lisp (1958)

John McCarthy created **Lisp** (LISt Processing) at MIT, making the linked list the central data structure. Lisp's `cons` cells (a `car` and `cdr` pair) are singly linked list nodes:

```lisp
(cons 1 (cons 2 (cons 3 nil)))  ; the list (1 2 3)
```

McCarthy's insight was that code and data could both be represented as linked lists.

## The C Era (1970s)

Dennis Ritchie's C brought linked lists into systems programming. Without generics, C programmers used `void*` or struct-specific implementations:

```c
struct node {
    void *data;
    struct node *next;
};
```

Doubly linked lists became standard in operating system kernels (process lists, free memory lists).

## Java (1995–present)

- **Java 1.0** (1996): `java.util.LinkedList` — doubly linked list with `List` interface
- **Java 1.2** (1998): Collections Framework — LinkedList retrofitted to `List` and `Queue`
- **Java 6** (2006): `Deque` interface added; LinkedList implements it
- **Java 8** (2014): HashMap chaining switches from linked lists to trees (TREEIFY_THRESHOLD = 8)

## Modern Developments

- **Lock-free linked lists** (1996, Michael & Scott): concurrent linked lists without mutexes
- **Skip lists** (1990, William Pugh): linked list with multiple levels for O(log n) search
- **Radio and ref counting algorithms** use linked lists of live objects for GC
