package com.algo.lab24;

import java.util.*;

/**
 * First-Come, First-Served (FCFS) scheduling algorithm.
 * Processes are executed in order of arrival.
 * Time: O(n log n) for sorting, O(n) for scheduling, Space: O(n)
 */
public class FCFSScheduler {

    private FCFSScheduler() {}

    public static List<ScheduleResult> schedule(List<Process> processes) {
        if (processes == null || processes.isEmpty()) return List.of();
        List<Process> sorted = new ArrayList<>(processes);
        sorted.sort(Comparator.comparingInt(Process::arrivalTime));
        List<ScheduleResult> results = new ArrayList<>();
        int currentTime = 0;
        for (Process p : sorted) {
            int start = Math.max(currentTime, p.arrivalTime());
            int finish = start + p.burstTime();
            results.add(new ScheduleResult(p.id(), start, finish, finish - p.arrivalTime() - p.burstTime()));
            currentTime = finish;
        }
        return results;
    }
}
