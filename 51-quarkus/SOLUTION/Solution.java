package com.learning.quarkus;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

public class QuarkusSolution {

    public void runApplication() {
        Quarkus.run();
    }

    public void runApplication(Class<? extends QuarkusApplication> appClass) {
        Quarkus.run(appClass);
    }

    public void stopApplication() {
        Quarkus.stop();
    }

    public boolean isApplicationRunning() {
        return Quarkus.isApplicationRunning();
    }

    public static class HelloResource {
        @GET
        @Path("/hello")
        @Produces(MediaType.TEXT_PLAIN)
        public String hello() {
            return "Hello from Quarkus";
        }
    }

    public static class GreetingResource {
        @Inject
        GreetingService greetingService;

        @GET
        @Path("/greet/{name}")
        public String greet(String name) {
            return greetingService.greet(name);
        }
    }

    public static class GreetingService {
        public String greet(String name) {
            return "Hello, " + name + "!";
        }
    }

    public static class ReactiveResource {
        @GET
        @Path("/reactive")
        public io.smallrye.mutiny.Uni<String> reactive() {
            return io.smallrye.mutiny.Uni.createFrom().item("Reactive!");
        }
    }

    public static class ConfigResource {
        @Inject
        org.eclipse.microprofile.config.Config config;

        @GET
        @Path("/config/{key}")
        public String getConfig(String key) {
            return config.getValue(key, String.class);
        }
    }
}