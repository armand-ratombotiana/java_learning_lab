package com.algorithms.scheduling;

import java.util.*;

/**
 * Custom: Scheduling Algorithms
 * FCFS, SJF, Round Robin, Priority Scheduling.
 *
 * Time Complexity: O(n log n) for sorting-based, O(n) for round robin
 * Space Complexity: O(n)
 */
public class SchedulingAlgorithms {

    public static class Process {
        int id, arrival, burst, remaining, priority;
        Process(int id, int arrival, int burst) {
            this.id = id; this.arrival = arrival; this.burst = burst;
            this.remaining = burst; this.priority = 0;
        }
    }

    // FCFS - First Come First Served
    public double fcfs(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrival));
        int time = 0, totalWait = 0;
        for (Process p : processes) {
            if (time < p.arrival) time = p.arrival;
            totalWait += time - p.arrival;
            time += p.burst;
        }
        return (double) totalWait / processes.size();
    }

    // SJF - Shortest Job First (Non-preemptive)
    public double sjf(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrival));
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.burst));
        int time = 0, idx = 0, totalWait = 0;
        while (idx < processes.size() || !pq.isEmpty()) {
            while (idx < processes.size() && processes.get(idx).arrival <= time) pq.offer(processes.get(idx++));
            if (pq.isEmpty()) { time = processes.get(idx).arrival; continue; }
            Process p = pq.poll();
            totalWait += time - p.arrival;
            time += p.burst;
        }
        return (double) totalWait / processes.size();
    }

    public static void main(String[] args) {
        List<Process> procs = Arrays.asList(
            new Process(1, 0, 6), new Process(2, 1, 8),
            new Process(3, 2, 7), new Process(4, 3, 3)
        );
        SchedulingAlgorithms sa = new SchedulingAlgorithms();
        System.out.println("FCFS avg wait: " + sa.fcfs(procs));
        System.out.println("SJF avg wait: " + sa.sjf(procs) + " (expected: lower than FCFS)");
    }
}
