package com.javaacademy.lab51.advancedconcurrency;

import java.util.concurrent.Phaser;

/**
 * Demonstrates Phaser for phased computations among variable numbers
 * of threads. Phaser supports dynamic party registration and phased
 * barriers, making it ideal for multi-phase algorithms like
 * iterative solvers or map-reduce stages.
 */
public class PhaserExample {

    private static final int WORKERS = 4;
    private static final int PHASES = 3;

    public static void main(String[] args) {
        var phaser = new Phaser(1); // register main thread
        var results = new int[WORKERS][PHASES];

        for (int w = 0; w < WORKERS; w++) {
            int workerId = w;
            phaser.register();
            Thread.ofVirtual().name("worker-" + w).start(() -> {
                for (int phase = 0; phase < PHASES; phase++) {
                    // Phase computation
                    int result = (workerId + 1) * (phase + 1) * 10;
                    results[workerId][phase] = result;
                    System.out.println("Worker " + workerId + " phase " + phase + " = " + result);

                    // Wait for all workers at phase barrier
                    phaser.arriveAndAwaitAdvance();
                }
                phaser.arriveAndDeregister();
            });
        }

        // Main thread also participates in phases
        for (int phase = 0; phase < PHASES; phase++) {
            System.out.println("--- Main waiting at phase " + phase + " ---");
            phaser.arriveAndAwaitAdvance();
        }

        phaser.arriveAndDeregister();
        System.out.println("Phaser demo complete.");
    }
}
