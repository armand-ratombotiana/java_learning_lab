package com.ds03;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== StackArray Demo ===");
        StackArray<Integer> sa = new StackArray<>();
        for (int i = 1; i <= 5; i++) sa.push(i);
        System.out.println("Stack: " + sa);
        System.out.println("Peek: " + sa.peek());
        System.out.println("Pop: " + sa.pop());
        System.out.println("After pop: " + sa);
        System.out.println("Size: " + sa.size());

        System.out.println("\n=== StackLinkedList Demo ===");
        StackLinkedList<String> sl = new StackLinkedList<>();
        sl.push("one"); sl.push("two"); sl.push("three");
        System.out.println("Stack: " + sl);
        System.out.println("Pop: " + sl.pop());
        System.out.println("After pop: " + sl);

        System.out.println("\n=== QueueArray Demo ===");
        QueueArray<Integer> qa = new QueueArray<>();
        for (int i = 10; i <= 50; i += 10) qa.enqueue(i);
        System.out.println("Queue: " + qa);
        System.out.println("Dequeue: " + qa.dequeue());
        System.out.println("After dequeue: " + qa);
        qa.enqueue(60);
        System.out.println("After enqueue 60: " + qa);

        System.out.println("\n=== QueueLinkedList Demo ===");
        QueueLinkedList<String> ql = new QueueLinkedList<>();
        ql.enqueue("a"); ql.enqueue("b"); ql.enqueue("c");
        System.out.println("Queue: " + ql);
        System.out.println("Dequeue: " + ql.dequeue());

        System.out.println("\n=== DequeArray Demo ===");
        DequeArray<Integer> dq = new DequeArray<>();
        dq.addLast(2); dq.addLast(3);
        dq.addFirst(1); dq.addLast(4);
        System.out.println("Deque: " + dq);
        System.out.println("Remove first: " + dq.removeFirst());
        System.out.println("Remove last: " + dq.removeLast());
        System.out.println("After removes: " + dq);

        System.out.println("\n=== CircularQueue Demo ===");
        CircularQueue<Integer> cq = new CircularQueue<>(5);
        for (int i = 1; i <= 5; i++) cq.enqueue(i);
        System.out.println("Full: " + cq.isFull() + ", Queue: " + cq);
        System.out.println("Dequeue: " + cq.dequeue());
        cq.enqueue(6);
        System.out.println("After dequeue+enqueue 6: " + cq);

        System.out.println("\n=== PriorityQueueMin Demo ===");
        PriorityQueueMin<Integer> pq = new PriorityQueueMin<>();
        pq.offer(30); pq.offer(10); pq.offer(50); pq.offer(20);
        System.out.println("Queue: " + pq);
        System.out.println("Poll (min): " + pq.poll());
        System.out.println("Poll (min): " + pq.poll());
        System.out.println("After polls: " + pq);
    }
}
