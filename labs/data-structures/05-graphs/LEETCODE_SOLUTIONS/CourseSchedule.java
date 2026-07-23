package com.leetcode.graphs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * LeetCode 207: Course Schedule
 * https://leetcode.com/problems/course-schedule/
 *
 * There are numCourses courses labeled from 0 to numCourses - 1.
 * Determine if all courses can be finished given prerequisites.
 *
 * Time Complexity: O(V + E)
 * Space Complexity: O(V + E)
 */
public class CourseSchedule {

    /**
     * Approach 1 (Optimal): Topological Sort (Kahn's Algorithm / BFS)
     * Build adjacency list and indegree array. Process courses with indegree 0.
     * Time: O(V + E), Space: O(V + E)
     */
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] indegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());

        for (int[] prereq : prerequisites) {
            int course = prereq[0];
            int prereqCourse = prereq[1];
            graph.get(prereqCourse).add(course);
            indegree[course]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) queue.offer(i);
        }

        int processed = 0;
        while (!queue.isEmpty()) {
            int current = queue.poll();
            processed++;
            for (int neighbor : graph.get(current)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) queue.offer(neighbor);
            }
        }

        return processed == numCourses;
    }

    /**
     * Approach 2: DFS with cycle detection
     * Use 3 states: 0=unvisited, 1=visiting, 2=visited.
     * Time: O(V + E), Space: O(V + E)
     */

    public static void main(String[] args) {
        CourseSchedule cs = new CourseSchedule();

        System.out.println("Test 1: " + cs.canFinish(2, new int[][] { { 1, 0 } }) + " (expected: true)");
        System.out.println("Test 2: " + cs.canFinish(2, new int[][] { { 1, 0 }, { 0, 1 } }) + " (expected: false)");
        System.out.println("Test 3: " + cs.canFinish(5, new int[][] { { 1, 0 }, { 2, 1 }, { 3, 2 }, { 4, 3 } }) + " (expected: true)");
        System.out.println("Test 4: " + cs.canFinish(1, new int[][] {}) + " (expected: true)");
        System.out.println("Test 5: " + cs.canFinish(3, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 0 } }) + " (expected: false)");
    }
}
