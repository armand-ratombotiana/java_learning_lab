package com.ds03;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.EmptyStackException;
import java.util.NoSuchElementException;

public class StackQueueTest {

    private StackArray<Integer> stackArray;
    private StackLinkedList<String> stackLinked;
    private QueueArray<Integer> queueArray;
    private QueueLinkedList<String> queueLinked;
    private DequeArray<Integer> deque;
    private CircularQueue<Integer> circQueue;
    private PriorityQueueMin<Integer> prioQ;

    @BeforeEach
    void setUp() {
        stackArray = new StackArray<>();
        stackLinked = new StackLinkedList<>();
        queueArray = new QueueArray<>();
        queueLinked = new QueueLinkedList<>();
        deque = new DequeArray<>();
        circQueue = new CircularQueue<>(3);
        prioQ = new PriorityQueueMin<>();
    }

    @Test
    void stackArrayLifo() {
        stackArray.push(1); stackArray.push(2); stackArray.push(3);
        assertEquals(3, stackArray.pop());
        assertEquals(2, stackArray.pop());
        assertEquals(1, stackArray.pop());
        assertTrue(stackArray.isEmpty());
    }

    @Test
    void stackArrayPeek() {
        stackArray.push(10);
        assertEquals(10, stackArray.peek());
        assertEquals(1, stackArray.size());
    }

    @Test
    void stackArrayEmptyThrows() {
        assertThrows(EmptyStackException.class, () -> stackArray.pop());
        assertThrows(EmptyStackException.class, () -> stackArray.peek());
    }

    @Test
    void stackLinkedListLifo() {
        stackLinked.push("a"); stackLinked.push("b"); stackLinked.push("c");
        assertEquals("c", stackLinked.pop());
        assertEquals("b", stackLinked.pop());
        assertEquals("a", stackLinked.pop());
    }

    @Test
    void queueArrayFifo() {
        queueArray.enqueue(10); queueArray.enqueue(20); queueArray.enqueue(30);
        assertEquals(10, queueArray.dequeue());
        assertEquals(20, queueArray.dequeue());
        assertEquals(30, queueArray.dequeue());
    }

    @Test
    void queueArrayDynamicResize() {
        for (int i = 0; i < 100; i++) queueArray.enqueue(i);
        assertEquals(100, queueArray.size());
        for (int i = 0; i < 100; i++) assertEquals(i, queueArray.dequeue());
    }

    @Test
    void queueLinkedListFifo() {
        queueLinked.enqueue("x"); queueLinked.enqueue("y"); queueLinked.enqueue("z");
        assertEquals("x", queueLinked.dequeue());
        assertEquals("y", queueLinked.dequeue());
        assertEquals("z", queueLinked.dequeue());
    }

    @Test
    void dequeAddRemoveBothEnds() {
        deque.addLast(2); deque.addLast(3);
        deque.addFirst(1); deque.addLast(4);
        assertEquals(1, deque.removeFirst());
        assertEquals(4, deque.removeLast());
        assertEquals(2, deque.removeFirst());
        assertEquals(3, deque.removeLast());
    }

    @Test
    void dequeGetFirstLast() {
        deque.addLast(10); deque.addFirst(5);
        assertEquals(5, deque.getFirst());
        assertEquals(10, deque.getLast());
    }

    @Test
    void dequeEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> deque.removeFirst());
        assertThrows(NoSuchElementException.class, () -> deque.removeLast());
    }

    @Test
    void circularQueueFixedCapacity() {
        assertTrue(circQueue.enqueue(1));
        assertTrue(circQueue.enqueue(2));
        assertTrue(circQueue.enqueue(3));
        assertFalse(circQueue.enqueue(4));
        assertTrue(circQueue.isFull());
    }

    @Test
    void circularQueueWrapAround() {
        circQueue.enqueue(1); circQueue.enqueue(2); circQueue.enqueue(3);
        circQueue.dequeue(); circQueue.dequeue();
        circQueue.enqueue(4); circQueue.enqueue(5);
        assertEquals(3, circQueue.dequeue());
        assertEquals(4, circQueue.dequeue());
        assertEquals(5, circQueue.dequeue());
    }

    @Test
    void priorityQueueMinOrder() {
        prioQ.offer(30); prioQ.offer(10); prioQ.offer(50); prioQ.offer(20);
        assertEquals(10, prioQ.poll());
        assertEquals(20, prioQ.poll());
        assertEquals(30, prioQ.poll());
        assertEquals(50, prioQ.poll());
        assertTrue(prioQ.isEmpty());
    }

    @Test
    void priorityQueuePeek() {
        prioQ.offer(5);
        assertEquals(5, prioQ.peek());
        assertEquals(1, prioQ.size());
    }
}
