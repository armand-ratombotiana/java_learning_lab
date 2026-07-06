package com.algo.lab24;

/**
 * Represents a process for scheduling algorithms.
 * Contains id, arrival time, burst time, priority, and deadline.
 */
public record Process(int id, int arrivalTime, int burstTime, int priority, int deadline) {

    public Process(int id, int arrivalTime, int burstTime) {
        this(id, arrivalTime, burstTime, 0, Integer.MAX_VALUE);
    }

    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this(id, arrivalTime, burstTime, priority, Integer.MAX_VALUE);
    }
}
