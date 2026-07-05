package com.learning.backend10;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Quarkus application entry point.
 *
 * Quarkus is a Kubernetes-native Java framework tailored for GraalVM
 * and OpenJDK HotSpot, with fast startup and low memory usage.
 *
 * Quarkus supports both JAX-RS (@Path, @GET) and Spring Web annotations.
 */
@QuarkusMain
public class Backend10Application {
    public static void main(String[] args) {
        Quarkus.run(args);
        System.out.println("=== Quarkus Lab is running ===");
    }
}
