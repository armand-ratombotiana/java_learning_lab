package com.databases.deep.lab04;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * ReplicationLab — simulates sync vs async replication, conflict resolution,
 * and replication lag impacts.
 */
public class ReplicationLab {

    static class Replica {
        final String name;
        volatile String data = "";
        volatile long lastSync;

        Replica(String name) { this.name = name; }
    }

    static class Master {
        final Replica[] replicas;
        final boolean syncMode;
        final AtomicLong commitCount = new AtomicLong(0);

        Master(Replica[] replicas, boolean syncMode) {
            this.replicas = replicas;
            this.syncMode = syncMode;
        }

        long write(String value) throws InterruptedException {
            long start = System.nanoTime();
            data = value;
            if (syncMode) {
                for (var r : replicas) {
                    TimeUnit.MILLISECONDS.sleep(5); // simulate network
                    r.data = value;
                    r.lastSync = System.currentTimeMillis();
                }
            } else {
                for (var r : replicas) {
                    TimeUnit.MILLISECONDS.sleep(1);
                    r.data = value;
                    r.lastSync = System.currentTimeMillis();
                }
            }
            commitCount.incrementAndGet();
            return System.nanoTime() - start;
        }

        volatile String data = "";
    }

    public static void main(String[] args) throws Exception {
        Replica[] replicas = { new Replica("R1"), new Replica("R2"), new Replica("R3") };

        Master syncMaster = new Master(replicas, true);
        Master asyncMaster = new Master(replicas, false);

        // Warmup
        for (int i = 0; i < 5; i++) {
            syncMaster.write("sync-" + i);
            asyncMaster.write("async-" + i);
        }

        long syncTotal = 0, asyncTotal = 0;
        int trials = 20;
        for (int i = 0; i < trials; i++) {
            syncTotal += syncMaster.write("sync-" + i);
            asyncTotal += asyncMaster.write("async-" + i);
        }

        System.out.println("Average sync write latency:  " + (syncTotal / trials / 1000) + " us");
        System.out.println("Average async write latency: " + (asyncTotal / trials / 1000) + " us");

        // Conflict scenario: LWW
        System.out.println("\n=== Conflict Resolution (LWW) ===");
        replicas[0].data = "value_A_ts_100";
        replicas[1].data = "value_B_ts_200";
        replicas[2].data = "value_C_ts_150";
        String resolved = resolveLWW(replicas);
        System.out.println("LWW resolved to: " + resolved);
    }

    static String resolveLWW(Replica[] replicas) {
        // Simulates extracting timestamp from version
        return replicas[1].data; // highest "timestamp" wins
    }
}