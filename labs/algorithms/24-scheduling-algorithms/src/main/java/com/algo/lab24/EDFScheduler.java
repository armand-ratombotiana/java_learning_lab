package com.algo.lab24;

import java.util.*;

/**
 * Earliest Deadline First (EDF) scheduling.
 * Schedules processes based on their deadlines; preemptive.
 * Time: O(n^2), Space: O(n)
 */
public class EDFScheduler {

    private EDFScheduler() {}

    public static List<ScheduleResult> schedule(List<Process> processes) {
        if (processes == null || processes.isEmpty()) return List.of();
        Map<Integer, Integer> remaining = new HashMap<>();
        Map<Integer, Integer> deadlines = new HashMap<>();
        for (Process p : processes) {
            remaining.put(p.id(), p.burstTime());
            deadlines.put(p.id(), p.deadline());
        }
        List<ScheduleResult> results = new ArrayList<>();
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();
        int lastId = -1;
        int lastStart = 0;
        while (completed < n) {
            Process selected = null;
            for (Process p : processes) {
                if (remaining.get(p.id()) > 0 && p.arrivalTime() <= currentTime) {
                    if (selected == null || p.deadline() < selected.deadline()) {
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
