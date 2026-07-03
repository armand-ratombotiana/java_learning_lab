# Deep Dive: Composition Patterns

## 1. Favor Composition Over Inheritance
"Favor composition over inheritance" is a fundamental principle of Object-Oriented Design. Inheritance creates a rigid, static "is-a" relationship (e.g., a `Car` *is a* `Vehicle`). Composition creates a flexible, dynamic "has-a" relationship (e.g., a `Car` *has an* `Engine`).

### Why Inheritance Fails at Scale:
*   **The Fragile Base Class Problem**: Changes to a superclass can inadvertently break subclasses.
*   **Class Explosion**: If you have `Window`, `ScrollingWindow`, `BorderedWindow`, and `ScrollingBorderedWindow`, the class hierarchy grows exponentially with new features.
*   **Static Nature**: You cannot change an object's superclass at runtime.

Composition solves this by assembling behaviors at runtime by injecting dependencies (objects) into other objects.

## 2. The Decorator Pattern
The Decorator pattern attaches additional responsibilities to an object dynamically. It provides a flexible alternative to subclassing for extending functionality.

### Key Characteristics:
*   The Decorator implements the same interface as the object it decorates.
*   The Decorator holds a reference to an instance of that interface.
*   Calls are delegated to the wrapped object, but the Decorator can add behavior before or after the delegation.

### Java I/O Example:
Java's `java.io` package is the classic example of the Decorator pattern.
```java
// InputStream is the base component
InputStream is = new FileInputStream("data.txt");

// BufferedInputStream decorates it with caching
InputStream bis = new BufferedInputStream(is);

// DataInputStream decorates it with data parsing
DataInputStream dis = new DataInputStream(bis);
```

## 3. The Composite Pattern
The Composite pattern allows you to compose objects into tree structures to represent part-whole hierarchies. It lets clients treat individual objects and compositions of objects uniformly.

### Structure:
*   **Component**: An interface representing both primitive objects and their containers.
*   **Leaf**: A primitive object that has no children (e.g., a `File`).
*   **Composite**: An object that contains children (e.g., a `Directory`). It implements the Component interface by delegating the operations to its children.

```java
public interface FileSystemNode {
    int getSize();
}

public class File implements FileSystemNode {
    private int size;
    public File(int size) { this.size = size; }
    public int getSize() { return size; }
}

public class Directory implements FileSystemNode {
    private List<FileSystemNode> children = new ArrayList<>();
    public void add(FileSystemNode node) { children.add(node); }
    
    public int getSize() {
        return children.stream().mapToInt(FileSystemNode::getSize).sum();
    }
}
```

## 4. Delegation Patterns
Delegation is a technique where an object expresses certain behavior to the outside, but in reality, it delegates responsibility for implementing that behavior to an associated object.

### The Forwarding Pattern (Wrapper)
A Wrapper class implements an interface and forwards all method calls to an underlying instance of that interface. Guava's `ForwardingList` or `ForwardingSet` are prime examples. They allow you to easily "decorate" a collection without having to manually write boilerplate code to forward the 20+ methods of the `List` interface.

```java
// Example using Guava's ForwardingList to create an observable list
public class ObservableList<E> extends ForwardingList<E> {
    private final List<E> delegate;

    public ObservableList(List<E> delegate) { this.delegate = delegate; }
    
    @Override protected List<E> delegate() { return delegate; }

    @Override public boolean add(E element) {
        System.out.println("Added: " + element);
        return super.add(element); // Forwards to the delegate
    }
}
```

## 5. Composition vs. Proxies
While structurally similar (both wrap an object and implement its interface), the intent differs:
*   **Decorator/Composition**: Adds *behavior* (e.g., adding a scrollbar to a window). The client is aware of the decoration.
*   **Proxy**: Controls *access* (e.g., lazy loading, security). The client is usually unaware of the proxy.