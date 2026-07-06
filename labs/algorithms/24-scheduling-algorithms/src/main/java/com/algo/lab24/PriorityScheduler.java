package com.algo.lab24;

import java.util.*;

/**
 * Priority preemptive scheduling.
 * Higher priority (lower number) processes preempt lower priority ones.
 * Time: O(n^2), Space: O(n)
 */
public class PriorityScheduler {

    private PriorityScheduler() {}

    public static List<ScheduleResult> schedule(List<Process> processes) {
        if (processes == null || processes.isEmpty()) return List.of();
        Map<Integer, Integer> remaining = new HashMap<>();
        Map<Integer, Integer> priorities = new HashMap<>();
        for (Process p : processes) {
            remaining.put(p.id(), p.burstTime());
            priorities.put(p.id(), p.priority());
        }
        List<ScheduleResult> results = new ArrayList<>();
        List<Process> sorted = new ArrayList<>(processes);
        sorted.sort(Comparator.comparingInt(Process::arrivalTime));
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();
        int lastId = -1;
        int lastStart = 0;
        while (completed < n) {
            Process selected = null;
            for (Process p : sorted) {
                if (remaining.get(p.id()) > 0 && p.arrivalTime() <= currentTime) {
                    if (selected == null || p.priority() < selected.priority()) {
                        selected = p;
                    }
                }
            }
            if (selected == null) {
                currentTime++;
                continue;
            }
            if (selected.id() != lastId && lastId != -1) {
                results.add(new ScheduleResult(lastId, lastStart, currentTime, 0));
            }
            if (selected.id() != lastId) {
                lastId = selected.id();
                lastStart = currentTime;
            }
            remaining.put(selected.id(), remaining.get(selected.id()) - 1);
            currentTime++;
            if (remaining.get(selected.id()) == 0) {
                results.add(new ScheduleResult(selected.id(), lastStart, currentTime, currentTime - selected.arrivalTime() - selected.burstTime()));
                completed++;
                lastId = -1;
            }
        }
        return results;
    }
}
