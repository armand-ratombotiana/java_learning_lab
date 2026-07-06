package com.algo.lab24;

import java.util.*;

/**
 * Round Robin scheduling with a fixed time quantum.
 * Each process runs for at most one quantum before being preempted.
 * Time: O(n * (total_burst / quantum)), Space: O(n)
 */
public class RoundRobinScheduler {

    private RoundRobinScheduler() {}

    public static List<ScheduleResult> schedule(List<Process> processes, int quantum) {
        if (processes == null || processes.isEmpty()) return List.of();
        Map<Integer, Integer> remaining = new HashMap<>();
        for (Process p : processes) remaining.put(p.id(), p.burstTime());
        Queue<Process> queue = new LinkedList<>();
        processes.sort(Comparator.comparingInt(Process::arrivalTime));
        List<ScheduleResult> results = new ArrayList<>();
        int currentTime = 0;
        int idx = 0;
        while (idx < processes.size() || !queue.isEmpty()) {
            while (idx < processes.size() && processes.get(idx).arrivalTime() <= currentTime) {
                queue.add(processes.get(idx));
                idx++;
            }
            if (queue.isEmpty()) {
                currentTime = processes.get(idx).arrivalTime();
                continue;
            }
            Process p = queue.poll();
            int execTime = Math.min(quantum, remaining.get(p.id()));
            int start = currentTime;
            currentTime += execTime;
            remaining.put(p.id(), remaining.get(p.id()) - execTime);
            while (idx < processes.size() && processes.get(idx).arrivalTime() <= currentTime) {
                queue.add(processes.get(idx));
                idx++;
            }
            if (remaining.get(p.id()) > 0) {
                queue.add(p);
            } else {
                results.add(new ScheduleResult(p.id(), start, currentTime, currentTime - p.arrivalTime() - p.burstTime()));
            }
        }
        return results;
    }
}
