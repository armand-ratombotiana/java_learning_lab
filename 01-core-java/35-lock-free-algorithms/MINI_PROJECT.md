# Mini Project: Lock-Free Stack (Treiber Stack)

## Objective
Build a thread-safe, lock-free Stack (LIFO) using `AtomicReference` and a Compare-And-Swap (CAS) spin-loop. Then, write a multi-threaded test to prove it does not drop data under high contention.

## Prerequisites
*   Java 17+

## Step 1: The Node Class
Create a simple immutable Node to hold the data and the pointer to the next node.

```java
public class Node<T> {
    public final T item;
    public final Node<T> next;

    public Node(T item, Node<T> next) {
        this.item = item;
        this.next = next;
    }
}
```

## Step 2: The Lock-Free Stack
Implement the `push` and `pop` methods using the Treiber Stack algorithm.

```java
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack<T> {
    // The only mutable state is the pointer to the head of the stack
    private final AtomicReference<Node<T>> head = new AtomicReference<>(null);

    public void push(T item) {
        Node<T> newHead;
        Node<T> oldHead;
        
        do {
            // 1. Read current state
            oldHead = head.get();
            
            // 2. Calculate new state (new node points to the current head)
            newHead = new Node<>(item, oldHead);
            
            // 3. Attempt to swap. If head is still oldHead, make it newHead.
            // If it fails, loop and try again.
        } while (!head.compareAndSet(oldHead, newHead));
    }

    public T pop() {
        Node<T> oldHead;
        Node<T> newHead;
        
        do {
            // 1. Read current state
            oldHead = head.get();
            
            // If stack is empty, return null
            if (oldHead == null) {
                return null; 
            }
            
            // 2. Calculate new state (the new head will be the 2nd element)
            newHead = oldHead.next;
            
            // 3. Attempt to swap. If head is still oldHead, make it newHead.
        } while (!head.compareAndSet(oldHead, newHead));
        
        return oldHead.item;
    }
    
    // Optional: A non-atomic size method for debugging
    public int approximateSize() {
        int count = 0;
        Node<T> current = head.get();
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }
}
```

## Step 3: The Contention Test
We will spawn multiple threads to furiously push and pop items. We will track exactly how many items were pushed and popped to ensure no data is lost or duplicated.

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 10_000;
        
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        AtomicInteger totalPushed = new AtomicInteger(0);
        AtomicInteger totalPopped = new AtomicInteger(0);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        System.out.println("--- Starting Lock-Free Stack Test ---");
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        // Push an item
                        stack.push(j);
                        totalPushed.incrementAndGet();
                        
                        // Occasionally pop an item
                        if (j % 2 == 0) {
                            Integer popped = stack.pop();
                            if (popped != null) {
                                totalPopped.incrementAndGet();
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to finish
        latch.await();
        executor.shutdown();

        // Calculate expected final size
        int expectedRemaining = totalPushed.get() - totalPopped.get();
        int actualRemaining = stack.approximateSize();

        System.out.println("Total Pushed    : " + totalPushed.get());
        System.out.println("Total Popped    : " + totalPopped.get());
        System.out.println("Expected in Stack: " + expectedRemaining);
        System.out.println("Actual in Stack  : " + actualRemaining);
        
        if (expectedRemaining == actualRemaining) {
            System.out.println("\nSUCCESS! No data lost or corrupted under high contention.");
        } else {
            System.err.println("\nFAILURE! Data mismatch.");
        }
    }
}
```

## Expected Output
```text
--- Starting Lock-Free Stack Test ---
Total Pushed    : 100000
Total Popped    : 50000
Expected in Stack: 50000
Actual in Stack  : 50000

SUCCESS! No data lost or corrupted under high contention.
```