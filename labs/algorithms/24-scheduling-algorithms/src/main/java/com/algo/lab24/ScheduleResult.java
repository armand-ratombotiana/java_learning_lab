package com.algo.lab24;

/**
 * Result of scheduling a process.
 * Contains process id, start time, finish time, and waiting time.
 */
public record ScheduleResult(int processId, int startTime, int finishTime, int waitingTime) {

    public int turnaroundTime() {
        return finishTime - startTime + waitingTime;
    }

    @Override
    public String toString() {
        return "P" + processId + ": [" + startTime + "-" + finishTime + "] wait=" + waitingTime;
    }
}
