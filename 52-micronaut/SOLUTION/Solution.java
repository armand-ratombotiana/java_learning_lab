package com.learning.micronaut;

import io.micronaut.runtime.Micronaut;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Inject;

public class MicronautSolution {

    public void runApplication() {
        Micronaut.run(null);
    }

    public void runApplication(Class<?> source) {
        Micronaut.run(source);
    }

    public static class HelloController {
        @Get("/hello")
        public String hello() {
            return "Hello from Micronaut";
        }
    }

    public static class GreetingController {
        @Get("/greet/{name}")
        public String greet(@PathVariable String name) {
            return "Hello, " + name + "!";
        }
    }

    public static class ConfigController {
        @Property(name = "app.message", defaultValue = "Default")
        String message;

        @Get("/config")
        public String getConfig() {
            return message;
        }
    }

    public static class MyService {
        public String process(String input) {
            return "Processed: " + input;
        }
    }

    public static class StartupEvent {
        @EventListener
        public void onStartup(io.micronaut.runtime.Micronaut.StartupEvent event) {
            System.out.println("Application started");
        }
    }

    public static class ShutdownEvent {
        @EventListener
        public void onShutdown(io.micronaut.runtime.Micronaut.ShutdownEvent event) {
            System.out.println("Application shutting down");
        }
    }
}