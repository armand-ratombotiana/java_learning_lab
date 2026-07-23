package com.algorithms.graph;

import java.util.*;

/**
 * LeetCode 207: Course Schedule
 * https://leetcode.com/problems/course-schedule/
 *
 * Determine if all courses can be finished given prerequisites.
 * Solved using topological sort (Kahn's algorithm).
 *
 * Time Complexity: O(V + E)
 * Space Complexity: O(V + E)
 */
public class CourseSchedule {

    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] indegree = new int[numCourses];
        for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());

        for (int[] p : prerequisites) {
            graph.get(p[1]).add(p[0]);
            indegree[p[0]]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) queue.offer(i);
        }

        int processed = 0;
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            processed++;
            for (int next : graph.get(cur)) {
                if (--indegree[next] == 0) queue.offer(next);
            }
        }
        return processed == numCourses;
    }

    public static void main(String[] args) {
        CourseSchedule cs = new CourseSchedule();
        System.out.println("Test 1: " + cs.canFinish(2, new int[][] { { 1, 0 } }) + " (expected: true)");
        System.out.println("Test 2: " + cs.canFinish(2, new int[][] { { 1, 0 }, { 0, 1 } }) + " (expected: false)");
        System.out.println("Test 3: " + cs.canFinish(5, new int[][] { { 0, 1 }, { 1, 2 }, { 3, 4 } }) + " (expected: true)");
        System.out.println("Test 4: " + cs.canFinish(1, new int[][] {}) + " (expected: true)");
    }
}
