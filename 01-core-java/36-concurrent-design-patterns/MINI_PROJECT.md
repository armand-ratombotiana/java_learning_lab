# Mini Project: Log Processing Pipeline

## Objective
Build a robust, multi-threaded log processing pipeline. It will demonstrate the **Producer-Consumer** pattern with a bounded buffer for backpressure, the **Poison Pill** pattern for clean shutdown, and the **Balking** pattern to prevent redundant saves.

## Prerequisites
*   Java 17+

## Step 1: Define the Domain and the Poison Pill
Create a `LogMessage` record. We will define a special constant to act as our Poison Pill.

```java
public record LogMessage(String level, String message) {
    // The Poison Pill: A unique instance that consumers will recognize
    public static final LogMessage POISON_PILL = new LogMessage("POISON", "PILL");
}
```

## Step 2: The Balking File Saver
Create a component that saves logs in batches. It uses the Balking pattern to avoid unnecessary disk I/O if the batch is empty.

```java
import java.util.ArrayList;
import java.util.List;

public class BatchSaver {
    private final List<LogMessage> batch = new ArrayList<>();
    private final Object lock = new Object();

    public void addLog(LogMessage log) {
        synchronized (lock) {
            batch.add(log);
        }
    }

    public void flush() {
        List<LogMessage> toSave;
        synchronized (lock) {
            // THE BALKING PATTERN: If nothing to do, return immediately
            if (batch.isEmpty()) {
                System.out.println("Saver: Balking (Nothing to save)");
                return; 
            }
            // Copy the data and clear the buffer quickly while holding the lock
            toSave = new ArrayList<>(batch);
            batch.clear();
        }

        // Perform the slow I/O operation OUTSIDE the synchronized block
        System.out.println("Saver: Writing " + toSave.size() + " logs to disk...");
        try { Thread.sleep(500); } catch (InterruptedException e) {} // Simulate I/O
    }
}
```

## Step 3: The Consumer (Processor)
The Consumer takes messages from the queue. If it sees the Poison Pill, it shuts down.

```java
import java.util.concurrent.BlockingQueue;

public class LogConsumer implements Runnable {
    private final BlockingQueue<LogMessage> queue;
    private final BatchSaver saver;

    public LogConsumer(BlockingQueue<LogMessage> queue, BatchSaver saver) {
        this.queue = queue;
        this.saver = saver;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Blocks until a message is available
                LogMessage msg = queue.take();

                // THE POISON PILL PATTERN: Check for the termination signal
                if (msg == LogMessage.POISON_PILL) {
                    System.out.println("Consumer: Poison Pill received. Shutting down.");
                    saver.flush(); // Ensure final logs are saved
                    break; // Exit the while loop, terminating the thread
                }

                // Process normal message
                saver.addLog(msg);
                
                // Flush every 5 logs
                if (Math.random() > 0.8) {
                    saver.flush();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Consumer interrupted!");
        }
    }
}
```

## Step 4: The Producer
The Producer generates logs and pushes them to the queue.

```java
import java.util.concurrent.BlockingQueue;

public class LogProducer implements Runnable {
    private final BlockingQueue<LogMessage> queue;
    private final int numLogs;

    public LogProducer(BlockingQueue<LogMessage> queue, int numLogs) {
        this.queue = queue;
        this.numLogs = numLogs;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= numLogs; i++) {
                LogMessage msg = new LogMessage("INFO", "Log entry " + i);
                
                // Blocks if the queue is full (Backpressure)
                queue.put(msg); 
                System.out.println("Producer: Sent " + msg.message());
                
                Thread.sleep(100); // Simulate work
            }
            
            // Send the Poison Pill to signal completion
            System.out.println("Producer: Sending Poison Pill...");
            queue.put(LogMessage.POISON_PILL);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

## Step 5: Execute the Pipeline
```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // BOUNDED BUFFER: Capacity of 10 forces backpressure if consumer is slow
        BlockingQueue<LogMessage> queue = new ArrayBlockingQueue<>(10);
        BatchSaver saver = new BatchSaver();

        Thread producerThread = new Thread(new LogProducer(queue, 15));
        Thread consumerThread = new Thread(new LogConsumer(queue, saver));

        System.out.println("--- Starting Log Pipeline ---");
        consumerThread.start();
        producerThread.start();

        // Wait for both to finish cleanly
        producerThread.join();
        consumerThread.join();
        
        System.out.println("--- Pipeline Terminated Cleanly ---");
    }
}
```

## Expected Output
Notice how the Saver balks when empty, and how the Consumer cleanly terminates when the Poison Pill arrives.
```text
--- Starting Log Pipeline ---
Producer: Sent Log entry 1
Producer: Sent Log entry 2
Saver: Balking (Nothing to save)
Producer: Sent Log entry 3
Saver: Writing 3 logs to disk...
Producer: Sent Log entry 4
...
Producer: Sending Poison Pill...
Consumer: Poison Pill received. Shutting down.
Saver: Writing 2 logs to disk...
--- Pipeline Terminated Cleanly ---
```