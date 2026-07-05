package com.learning.backend08;

import io.micronaut.runtime.Micronaut;

/**
 * Micronaut application entry point.
 *
 * Micronaut is a modern, JVM-based framework focused on fast startup
 * and low memory footprint by using compile-time dependency injection
 * (rather than runtime reflection like Spring).
 */
public class Backend08Application {
    public static void main(String[] args) {
        Micronaut.run(Backend08Application.class, args);
        System.out.println("=== Micronaut Lab is running ===");
    }
}
