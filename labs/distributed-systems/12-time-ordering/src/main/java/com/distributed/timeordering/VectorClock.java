package com.distributed.timeordering;

import java.util.Arrays;
import java.util.Objects;

public class VectorClock implements EventClock<int[]> {
    private final int[] vector;
    private final int processId;

    public VectorClock(int numProcesses, int processId) {
        if (processId < 0 || processId >= numProcesses) {
            throw new IllegalArgumentException("Invalid process ID: " + processId);
        }
        this.vector = new int[numProcesses];
        this.processId = processId;
    }

    @Override
    public synchronized int[] tick() {
        vector[processId]++;
        return copyOf(vector);
    }

    @Override
    public synchronized int[] send() {
        vector[processId]++;
        return copyOf(vector);
    }

    @Override
    public synchronized void receive(int[] other, long currentTimeMillis) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = Math.max(vector[i], other[i]);
        }
        vector[processId]++;
    }

    @Override
    public synchronized int[] getValue() {
        return copyOf(vector);
    }

    public synchronized boolean happensBefore(VectorClock other) {
        int[] v1 = this.vector;
        int[] v2 = other.vector;
        boolean lessOrEqual = true;
        boolean notEqual = false;
        for (int i = 0; i < v1.length; i++) {
            if (v1[i] > v2[i]) lessOrEqual = false;
            if (v1[i] != v2[i]) notEqual = true;
        }
        return lessOrEqual && notEqual;
    }

    public synchronized boolean isConcurrent(VectorClock other) {
        return !happensBefore(other) && !other.happensBefore(this) && !this.equals(other);
    }

    private int[] copyOf(int[] source) {
        return Arrays.copyOf(source, source.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VectorClock that)) return false;
        return Arrays.equals(vector, that.vector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(vector), processId);
    }

    @Override
    public String toString() {
        return "VectorClock{process=" + processId + ", vector=" + Arrays.toString(vector) + "}";
    }
}
