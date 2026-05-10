package com.learning.lab.module29;

import java.io.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 29: Docker Configuration ===");

        dockerfileDemo();
        dockerComposeDemo();
        multiStageBuildDemo();
        volumeDemo();
        networkingDemo();
        bestPracticesDemo();
    }

    static void dockerfileDemo() {
        System.out.println("\n--- Dockerfile Example ---");
        String dockerfile = "FROM eclipse-temurin:17-jdk-alpine\n" +
                "WORKDIR /app\n" +
                "COPY target/*.jar app.jar\n" +
                "EXPOSE 8080\n" +
                "ENV JAVA_OPTS=\"-Xmx512m\"\n" +
                "ENTRYPOINT [\"sh\", \"-c\", \"java $JAVA_OPTS -jar app.jar\"]";
        System.out.println(dockerfile);
    }

    static void dockerComposeDemo() {
        System.out.println("\n--- Docker Compose Example ---");
        String compose = "version: '3.8'\n" +
                "services:\n" +
                "  app:\n" +
                "    build: .\n" +
                "    ports:\n" +
                "      - \"8080:8080\"\n" +
                "    environment:\n" +
                "      - DB_HOST=postgres\n" +
                "    depends_on:\n" +
                "      - postgres\n" +
                "  postgres:\n" +
                "    image: postgres:15\n" +
                "    environment:\n" +
                "      - POSTGRES_DB=appdb\n" +
                "      - POSTGRES_USER=user\n" +
                "      - POSTGRES_PASSWORD=pass\n" +
                "    volumes:\n" +
                "      - pgdata:/var/lib/postgresql/data\n" +
                "volumes:\n" +
                "  pgdata:";
        System.out.println(compose);
    }

    static void multiStageBuildDemo() {
        System.out.println("\n--- Multi-Stage Build ---");
        String multiStage = "FROM maven:3.9-eclipse-temurin-17 AS builder\n" +
                "COPY pom.xml .\n" +
                "COPY src ./src\n" +
                "RUN mvn clean package -DskipTests\n" +
                "\n" +
                "FROM eclipse-temurin:17-jre-alpine\n" +
                "WORKDIR /app\n" +
                "COPY --from=builder /app/target/*.jar app.jar\n" +
                "EXPOSE 8080\n" +
                "ENTRYPOINT [\"java\", \"-jar\", \"app.jar\"]";
        System.out.println(multiStage);
    }

    static void volumeDemo() {
        System.out.println("\n--- Docker Volumes ---");
        System.out.println("Named volume: docker volume create mydata");
        System.out.println("Mount: docker run -v mydata:/app/data myimage");
        System.out.println("Bind mount: docker run -v /host/path:/container/path myimage");
        System.out.println("tmpfs: docker run --tmpfs /app/data myimage");
    }

    static void networkingDemo() {
        System.out.println("\n--- Docker Networking ---");
        System.out.println("Create network: docker network create mynet");
        System.out.println("Run in network: docker run --network mynet myimage");
        System.out.println("DNS resolution: containers can reference each other by service name");
        System.out.println("Port mapping: -p host:container");
    }

    static void bestPracticesDemo() {
        System.out.println("\n--- Docker Best Practices ---");
        System.out.println("1. Use official base images");
        System.out.println("2. Minimize layers: combine RUN, COPY, RUN");
        System.out.println("3. Use .dockerignore to exclude files");
        System.out.println("4. Use multi-stage builds for smaller images");
        System.out.println("5. Don't run as root");
        System.out.println("6. Set healthchecks");
        System.out.println("7. Use tags, not latest");
        System.out.println("8. Use labels for metadata");
    }
}