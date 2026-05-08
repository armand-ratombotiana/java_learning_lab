package com.learning.cloudnative;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Docker Concepts ===\n");

        demonstrateImagesAndContainers();
        demonstrateDockerfile();
        demonstrateDockerNetworking();
        demonstrateVolumes();
        demonstrateDockerCompose();
        demonstrateBestPractices();
    }

    private static void demonstrateImagesAndContainers() {
        System.out.println("--- Images vs Containers ---");
        System.out.println("Image     = Read-only template (layered filesystem)");
        System.out.println("Container = Runnable instance of an image (with writable layer)");
        System.out.println();
        System.out.println("Image layers (union filesystem):");
        System.out.println("  Base OS layer (e.g., alpine:3.18)");
        System.out.println("  + JDK layer (e.g., openjdk:21-slim)");
        System.out.println("  + Application JAR layer");
        System.out.println("  + Config layer");
        System.out.println("  = Final image (each layer is cached)");
    }

    private static void demonstrateDockerfile() {
        System.out.println("\n--- Dockerfile Instructions ---");
        System.out.println("FROM       -> Base image (e.g., eclipse-temurin:21-jre)");
        System.out.println("WORKDIR    -> Set working directory");
        System.out.println("COPY/ADD   -> Copy files into image");
        System.out.println("RUN        -> Execute commands during build");
        System.out.println("EXPOSE     -> Document port (does NOT publish)");
        System.out.println("ENV        -> Set environment variables");
        System.out.println("CMD/ENTRYPOINT -> Default command on container start");
        System.out.println("HEALTHCHECK    -> Check container health status");
        System.out.println();
        System.out.println("Multi-stage builds: compile in stage-1, copy artifact to slim stage-2");
    }

    private static void demonstrateDockerNetworking() {
        System.out.println("\n--- Networking ---");
        System.out.println("Bridge    -> Default network (containers communicate via IP)");
        System.out.println("Host      -> Container uses host network directly");
        System.out.println("Overlay   -> Multi-host networking (Swarm)");
        System.out.println("Macvlan   -> Assign MAC address to container");
        System.out.println("None      -> No networking");
        System.out.println();
        System.out.println("DNS resolution: containers reach each other by service name on custom bridge");
    }

    private static void demonstrateVolumes() {
        System.out.println("\n--- Storage ---");
        System.out.println("Volume     -> Managed by Docker, stored in /var/lib/docker/volumes/");
        System.out.println("Bind Mount -> Direct host path mapping (e.g., ./data:/app/data)");
        System.out.println("tmpfs      -> In-memory storage (fast, ephemeral)");
        System.out.println();
        System.out.println("Use volumes for persistent DB data, bind mounts for dev hot-reload");
    }

    private static void demonstrateDockerCompose() {
        System.out.println("\n--- Docker Compose ---");
        System.out.println("Define multi-container apps in YAML");
        System.out.println("services:  -> One entry per container");
        System.out.println("networks:  -> Custom networks");
        System.out.println("volumes:   -> Named volumes");
        System.out.println("depends_on -> Startup order (not readiness)");
        System.out.println();
        System.out.println("Example: app (Spring Boot) + db (PostgreSQL) + redis (cache)");
    }

    private static void demonstrateBestPractices() {
        System.out.println("\n--- Best Practices ---");
        System.out.println("1. Use specific tags (not 'latest')");
        System.out.println("2. Minimize layers (combine RUN commands)");
        System.out.println("3. Use .dockerignore to exclude files");
        System.out.println("4. Don't run as root (USER appuser)");
        System.out.println("5. Use multi-stage for smaller images");
        System.out.println("6. Scan images for vulnerabilities (docker scan / Trivy)");
        System.out.println("7. Use health checks in production");
        System.out.println("8. Set resource limits (--memory, --cpus)");
    }
}
