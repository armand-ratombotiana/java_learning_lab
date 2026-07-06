package com.javaacademy.lab35.serialization;

import java.util.*;

public class SerializationBenchmark {

    private static final int ITERATIONS = 10000;
    private static final int WARMUP = 1000;

    public static void main(String[] args) throws Exception {
        Person person = new Person("Benchmark User", 35, "pwd123", "bench@test.com");
        JsonSerializationExample jsonEx = new JsonSerializationExample();
        ProtobufExample protobuf = new ProtobufExample();
        ProtobufExample.ProtoPerson protoPerson = new ProtobufExample.ProtoPerson("Bench", 35, "b@t.com");

        System.out.println("=== Serialization Benchmark (" + ITERATIONS + " iterations) ===\n");

        benchmark("Java Serializable", () -> {
            byte[] data = SerializationUtil.serialize(person);
            SerializationUtil.deserialize(data);
        });

        benchmark("Jackson JSON", () -> {
            String json = jsonEx.toJson(person);
            jsonEx.fromJson(json, Person.class);
        });

        benchmark("Protobuf (custom)", () -> {
            byte[] data = protobuf.serialize(protoPerson);
            protobuf.deserialize(data);
        });

        System.out.println("\nDone.");
    }

    private static void benchmark(String name, Runnable task) throws Exception {
        for (int i = 0; i < WARMUP; i++) task.run();
        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) task.run();
        long elapsed = System.nanoTime() - start;
        double avg = elapsed / 1_000_000.0 / ITERATIONS;
        System.out.printf("%-20s average: %.3f ms, total: %.2f ms%n", name, avg, elapsed / 1_000_000.0);
    }
}
