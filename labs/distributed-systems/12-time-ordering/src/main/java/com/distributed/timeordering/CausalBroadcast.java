package com.distributed.timeordering;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class CausalBroadcast {
    private final int processId;
    private final int numProcesses;
    private final VectorClock clock;
    private final Map<Integer, Queue<Message>> buffer = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public record Message(int senderId, int[] vectorClock, String content) {}

    public CausalBroadcast(int numProcesses, int processId) {
        this.numProcesses = numProcesses;
        this.processId = processId;
        this.clock = new VectorClock(numProcesses, processId);
        for (int i = 0; i < numProcesses; i++) {
            buffer.put(i, new ConcurrentLinkedQueue<>());
        }
    }

    public Message broadcast(String content) {
        int[] vc = clock.send();
        return new Message(processId, vc, content);
    }

    public List<Message> receive(Message message) {
        List<Message> deliverable = new ArrayList<>();
        lock.lock();
        try {
            clock.receive(message.vectorClock(), System.currentTimeMillis());
            buffer.get(message.senderId()).add(message);
            boolean delivered;
            do {
                delivered = false;
                for (int i = 0; i < numProcesses; i++) {
                    Queue<Message> q = buffer.get(i);
                    Message next = q.peek();
                    if (next != null && canDeliver(next)) {
                        deliverable.add(q.poll());
                        delivered = true;
                    }
                }
            } while (delivered);
        } finally {
            lock.unlock();
        }
        return deliverable;
    }

    private boolean canDeliver(Message msg) {
        int[] msgVc = msg.vectorClock();
        int[] localVc = clock.getValue();
        if (msgVc[msg.senderId()] != localVc[msg.senderId()] + 1) {
            return false;
        }
        for (int i = 0; i < numProcesses; i++) {
            if (i != msg.senderId() && msgVc[i] > localVc[i]) {
                return false;
            }
        }
        return true;
    }

    public VectorClock getClock() { return clock; }
    public int getProcessId() { return processId; }
}
