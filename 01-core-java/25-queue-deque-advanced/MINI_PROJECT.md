# Mini Project: Multi-Tiered Task Scheduler

## Objective
Build a task scheduling system that demonstrates the difference between standard Queues, Deques, and PriorityQueues. The system will handle urgent tasks (PriorityQueue), standard tasks (FIFO Queue), and allow for "undo/redo" operations (Deque as a Stack).

## Prerequisites
*   Java 17+

## Step 1: Define the Task Class
Create a task that implements `Comparable` so it can be sorted by priority.

```java
public record Task(int id, String description, int priority) implements Comparable<Task> {
    @Override
    public int compareTo(Task other) {
        // Higher priority number means it should be processed FIRST.
        // We reverse the natural order by comparing 'other' to 'this'.
        return Integer.compare(other.priority, this.priority);
    }
}
```

## Step 2: Build the Scheduler
The scheduler manages three different collections to demonstrate different processing rules.

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;

public class TaskScheduler {
    // 1. Priority Queue for urgent tasks (Min-Heap based on compareTo)
    private final Queue<Task> urgentTasks = new PriorityQueue<>();
    
    // 2. Standard Queue for normal tasks (FIFO)
    private final Queue<Task> standardTasks = new ArrayDeque<>();
    
    // 3. Deque used as a Stack for completed tasks (LIFO) to support "Undo"
    private final Deque<Task> completedTasks = new ArrayDeque<>();

    public void addTask(Task task) {
        if (task.priority() >= 10) {
            urgentTasks.offer(task);
            System.out.println("Added URGENT task: " + task.description());
        } else {
            standardTasks.offer(task);
            System.out.println("Added standard task: " + task.description());
        }
    }

    public void processNextTask() {
        Task taskToProcess = null;

        // Urgent tasks always take precedence
        if (!urgentTasks.isEmpty()) {
            taskToProcess = urgentTasks.poll();
        } else if (!standardTasks.isEmpty()) {
            taskToProcess = standardTasks.poll();
        }

        if (taskToProcess != null) {
            System.out.println("Processing: [" + taskToProcess.priority() + "] " + taskToProcess.description());
            // Push to the stack (adds to the head of the Deque)
            completedTasks.push(taskToProcess); 
        } else {
            System.out.println("No tasks to process.");
        }
    }

    public void undoLastTask() {
        if (!completedTasks.isEmpty()) {
            // Pop from the stack (removes from the head of the Deque)
            Task undoneTask = completedTasks.pop();
            System.out.println("Undoing task: " + undoneTask.description());
            // Put it back in the appropriate queue
            addTask(undoneTask);
        } else {
            System.out.println("Nothing to undo.");
        }
    }
    
    public void printUrgentQueueInternals() {
        System.out.println("\n--- Urgent Queue Iterator (Heap Order, NOT Sorted Order) ---");
        for (Task t : urgentTasks) {
            System.out.println(t.priority() + " : " + t.description());
        }
    }
}
```

## Step 3: Test the Scheduler
Create a `Main` class to feed tasks into the scheduler and observe the processing order.

```java
public class Main {
    public static void main(String[] args) {
        TaskScheduler scheduler = new TaskScheduler();

        System.out.println("--- Adding Tasks ---");
        scheduler.addTask(new Task(1, "Write email", 1));
        scheduler.addTask(new Task(2, "Fix production bug", 100));
        scheduler.addTask(new Task(3, "Update documentation", 5));
        scheduler.addTask(new Task(4, "Restart crashed server", 50));
        scheduler.addTask(new Task(5, "Reply to boss", 10));

        // Demonstrate that the iterator doesn't print in sorted order
        scheduler.printUrgentQueueInternals();

        System.out.println("\n--- Processing Tasks ---");
        // Should process 100, then 50, then 10, then 1 (FIFO), then 3 (FIFO)
        scheduler.processNextTask(); // 100
        scheduler.processNextTask(); // 50
        
        System.out.println("\n--- Undoing ---");
        scheduler.undoLastTask(); // Undoes 50 and puts it back in urgent queue
        
        System.out.println("\n--- Processing Remaining ---");
        scheduler.processNextTask(); // 50 (it's back!)
        scheduler.processNextTask(); // 10
        scheduler.processNextTask(); // 1
        scheduler.processNextTask(); // 3
    }
}
```

## Expected Output
```text
--- Adding Tasks ---
Added standard task: Write email
Added URGENT task: Fix production bug
Added standard task: Update documentation
Added URGENT task: Restart crashed server
Added URGENT task: Reply to boss

--- Urgent Queue Iterator (Heap Order, NOT Sorted Order) ---
100 : Fix production bug
50 : Restart crashed server
10 : Reply to boss

--- Processing Tasks ---
Processing: [100] Fix production bug
Processing: [50] Restart crashed server

--- Undoing ---
Undoing task: Restart crashed server
Added URGENT task: Restart crashed server

--- Processing Remaining ---
Processing: [50] Restart crashed server
Processing: [10] Reply to boss
Processing: [1] Write email
Processing: [5] Update documentation
```