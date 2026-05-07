package com.learning.cloud;

public class DockerLab {

    public static void main(String[] args) {
        System.out.println("=== Docker Learning Lab ===\n");

        demonstrateDockerfile();
        demonstrateDockerCommands();
        demonstrateDockerCompose();
    }

    private static void demonstrateDockerfile() {
        System.out.println("--- Dockerfile Structure ---");
        System.out.println("""
        # Base image
        FROM openjdk:21-slim

        # Set working directory
        WORKDIR /app

        # Copy files
        COPY target/*.jar app.jar

        # Expose port
        EXPOSE 8080

        # Run application
        CMD ["java", "-jar", "app.jar"]
        """);
    }

    private static void demonstrateDockerCommands() {
        System.out.println("\n--- Essential Docker Commands ---");
        System.out.println("docker build -t myapp .");
        System.out.println("docker run -p 8080:8080 myapp");
        System.out.println("docker ps -a");
        System.out.println("docker logs <container-id>");
        System.out.println("docker exec -it <container-id> bash");
        System.out.println("docker-compose up -d");
    }

    private static void demonstrateDockerCompose() {
        System.out.println("\n--- Docker Compose Example ---");
        System.out.println("""
        version: '3.8'
        services:
          app:
            build: .
            ports:
              - "8080:8080"
            environment:
              - SPRING_PROFILES_ACTIVE=prod
            depends_on:
              - db
          db:
            image: postgres:15
            environment:
              - POSTGRES_PASSWORD=secret
        """);
    }
}