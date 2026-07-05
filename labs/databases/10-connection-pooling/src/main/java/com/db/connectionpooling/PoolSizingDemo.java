package com.db.connectionpooling;

/**
 * Demonstrates connection pool sizing theory and calculations.
 *
 * The "magic formula" from HikariCP documentation:
 *   pool_size = Tn * (Cm - 1) + 1
 *   where Tn = number of cores, Cm = max concurrent tasks per core
 *
 * Simplified rule of thumb:
 *   pool_size = (core_count * 2) + effective_spindle_count
 */
public class PoolSizingDemo {

    record PoolRecommendation(
        int cores, String workload, int recommendedSize, String reasoning
    ) {}

    static PoolRecommendation[] recommendations() {
        return new PoolRecommendation[] {
            new PoolRecommendation(4, "Web API (I/O bound)", 10,
                "4 cores × (3 concurrent I/O - 1) + 1 = 9, rounded to 10"),
            new PoolRecommendation(8, "Web API (I/O bound)", 18,
                "8 cores × (3 concurrent I/O - 1) + 1 = 17, rounded to 18"),
            new PoolRecommendation(16, "Batch processing", 10,
                "CPU-bound: more connections don't help; pool = cores + 1 for overhead"),
            new PoolRecommendation(4, "Heavy reporting", 15,
                "Long-running queries need more connections in flight"),
            new PoolRecommendation(2, "Microservice (low traffic)", 5,
                "Smaller footprint: 2-5 connections is often sufficient")
        };
    }

    static void printRecommendations() {
        System.out.println("=== Connection Pool Sizing Recommendations ===\n");
        System.out.printf("%-6s %-20s %-5s %s%n", "Cores", "Workload", "Pool", "Reasoning");
        System.out.println("-".repeat(90));
        for (var r : recommendations()) {
            System.out.printf("%-6d %-20s %-5d %s%n",
                r.cores(), r.workload(), r.recommendedSize(), r.reasoning());
        }
    }

    /**
     * Calculates a recommended pool size using the HikariCP formula.
     */
    static int calculatePoolSize(int cores, int concurrentTasksPerCore) {
        return cores * (concurrentTasksPerCore - 1) + 1;
    }

    public static void main(String[] args) {
        printRecommendations();

        System.out.println("\n=== Formula: pool_size = cores * (tasks_per_core - 1) + 1 ===\n");

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("  Available processors: " + cores);

        System.out.println("\n  Workload scenarios:");
        System.out.printf("    Light I/O (2 tasks/core):  pool=%d%n",
            calculatePoolSize(cores, 2));
        System.out.printf("    Medium I/O (3 tasks/core): pool=%d%n",
            calculatePoolSize(cores, 3));
        System.out.printf("    Heavy I/O (5 tasks/core):  pool=%d%n",
            calculatePoolSize(cores, 5));

        System.out.println("\n=== Key Guidelines ===");
        System.out.println("""
            1. Start with:    pool = cores * 2
            2. Monitor:       active vs idle connections
            3. If active pool is always saturated → increase pool
            4. If connections are mostly idle → decrease pool
            5. Avoid:         pool > 200 (context switching overhead)
            6. For SSDs:      fewer connections needed (fast I/O)
            7. For HDDs:      more connections to hide latency
            
            Benchmark your specific workload — there is no universal best size!
            """);
    }
}
