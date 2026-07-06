package com.algo.lab24;

import java.util.*;

/**
 * Shortest Job First (SJF) non-preemptive scheduling.
 * Selects the process with the shortest burst time among available processes.
 * Time: O(n^2), Space: O(n)
 */
public class SJFScheduler {

    private SJFScheduler() {}

    public static List<ScheduleResult> schedule(List<Process> processes) {
        if (processes == null || processes.isEmpty()) return List.of();
        List<Process> remaining = new ArrayList<>(processes);
        List<ScheduleResult> results = new ArrayList<>();
        int currentTime = 0;
        while (!remaining.isEmpty()) {
            List<Process> available = new ArrayList<>();
            for (Process p : remaining) {
                if (p.arrivalTime() <= currentTime) available.add(p);
            }
            if (available.isEmpty()) {
                Process next = remaining.get(0);
                for (Process p : remaining) {
                    if (p.arrivalTime() < next.arrivalTime()) next = p;
                }
                currentTime = next.arrivalTime();
                available.add(next);
            }
            Process selected = available.get(0);
            for (Process p : available) {
                if (p.burstTime() < selected.burstTime()) selected = p;
            }
            int finish = currentTime + selected.burstTime();
            results.add(new ScheduleResult(selected.id(), currentTime, finish, currentTime - selected.arrivalTime()));
            currentTime = finish;
            remaining.remove(selected);
        }
        return results;
    }
}
