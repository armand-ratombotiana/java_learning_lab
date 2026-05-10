package com.learning.lab.module51;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 51: Quarkus Lab ===\n");

        System.out.println("1. Quarkus Core Features:");
        System.out.println("   - Supersonic Subatomic Java");
        System.out.println("   - Hot reload in dev mode: mvn quarkus:dev");
        System.out.println("   - Live coding enabled");

        System.out.println("\n2. Native Image Build:");
        System.out.println("   - GraalVM native image: mvn package -Pnative");
        System.out.println("   - Container native: mvn package -Pnative -Dquarkus.native.container-build=true");
        System.out.println("   - Sub-second startup times");
        System.out.println("   - Memory-efficient");

        System.out.println("\n3. Extensions:");
        System.out.println("   - RESTEasy Reactive: JAX-RS with reactive support");
        System.out.println("   - Hibernate ORM with Panache");
        System.out.println("   - REST Data with Panache");
        System.out.println("   - Reactive MySQL client");
        System.out.println("   - Scheduler");
        System.out.println("   - Health checks");

        System.out.println("\n4. Dev Services:");
        System.out.println("   - Automatic container provisioning");
        System.out.println("   - Dev mode database auto-start");
        System.out.println("   - No config needed for dev");

        System.out.println("\n5. Config Management:");
        System.out.println("   - application.properties / application.yaml");
        System.out.println("   - Profile-specific: application-dev.properties");
        System.out.println("   - Environment variables override");

        System.out.println("\n6. Testing:");
        System.out.println("   - QuarkusTest annotations");
        System.out.println("   - @QuarkusTestResource for services");
        System.out.println("   - DevServices for automatic test containers");

        System.out.println("\n7. Build Tool Integration:");
        System.out.println("   - Maven plugin: io.quarkus.platform");
        System.out.println("   - Gradle support with quarkus plugin");

        System.out.println("\n=== Quarkus Lab Complete ===");
    }
}