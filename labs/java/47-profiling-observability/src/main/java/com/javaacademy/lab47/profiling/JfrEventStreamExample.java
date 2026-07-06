package com.javaacademy.lab47.profiling;

import jdk.jfr.Event;
import jdk.jfr.EventType;
import jdk.jfr.consumer.RecordingStream;
import jdk.jfr.Label;
import jdk.jfr.Name;
import java.time.Duration;

/**
 * Demonstrates programmatic JFR event subscription using RecordingStream.
 * Shows both custom JFR events and how to subscribe to built-in events
 * like GC, CPU, and lock events.
 */
public class JfrEventStreamExample {

    @Name("com.javaacademy.lab47.CustomWorkEvent")
    @Label("Custom Work Event")
    static class CustomWorkEvent extends Event {
        @Label("workId")
        int workId;

        @Label("duration")
        long durationMs;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread consumer = new Thread(() -> {
            try (var rs = new RecordingStream()) {
                rs.enable("com.javaacademy.lab47.CustomWorkEvent").withPeriod(Duration.ofSeconds(1));
                rs.enable("jdk.GCHeapSummary").withPeriod(Duration.ofSeconds(2));
                rs.onEvent("com.javaacademy.lab47.CustomWorkEvent", ev -> {
                    System.out.println("Custom event: workId=" + ev.getInt("workId")
                        + " durationMs=" + ev.getLong("durationMs"));
                });
                rs.onEvent("jdk.GCHeapSummary", ev -> {
                    System.out.println("GC Heap: used=" + ev.getLong("heapUsed")
                        + " total=" + ev.getLong("heapTotal"));
                });
                rs.start();
            }
        }, "jfr-consumer");
        consumer.setDaemon(true);
        consumer.start();

        for (int i = 0; i < 5; i++) {
            var ev = new CustomWorkEvent();
            ev.workId = i;
            ev.durationMs = 100 + (long)(Math.random() * 200);
            ev.begin();
            Thread.sleep(ev.durationMs);
            ev.commit();
        }

        Thread.sleep(2000);
        System.out.println("JFR streaming example completed.");
    }
}
