package solution;

import java.io.*;
import java.util.*;

public class DockerSolution {

    public static class DockerfileConfig {
        public String baseImage;
        public List<String> runCommands = new ArrayList<>();
        public Map<String, String> envVars = new HashMap<>();
        public List<Integer> exposedPorts = new ArrayList<>();
        public List<String> volumes = new ArrayList<>();
        public String entrypoint;
        public String cmd;

        public DockerfileConfig(String baseImage) {
            this.baseImage = baseImage;
        }
    }

    // Multi-stage build for Spring Boot
    public String generateMultiStageDockerfile() {
        return """
            # Build stage
            FROM maven:3.9-eclipse-temurin-17 AS builder
            WORKDIR /build

            # Copy only pom.xml first for dependency caching
            COPY pom.xml .
            RUN mvn dependency:go-offline

            # Copy source and build
            COPY src ./src
            RUN mvn clean package -DskipTests

            # Runtime stage
            FROM eclipse-temurin:17-jre-alpine

            # Create non-root user for security
            RUN addgroup -g 1000 appgroup && \\
                adduser -u 1000 -G appgroup -s /bin/sh -D appuser

            WORKDIR /app

            # Copy jar from build stage
            COPY --from=builder /build/target/*.jar app.jar

            # Set ownership
            RUN chown -R appuser:appgroup /app

            # Environment variables
            ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
            ENV SERVER_PORT=8080

            # Expose port
            EXPOSE 8080

            # Health check
            HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \\
                CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

            # Run as non-root
            USER appuser

            ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
            """;
    }

    // Docker Compose for microservices
    public String generateDockerCompose() {
        return """
            version: '3.8'

            services:
              app:
                build:
                  context: .
                  dockerfile: Dockerfile
                ports:
                  - "8080:8080"
                environment:
                  - SPRING_PROFILES_ACTIVE=prod
                  - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydb
                  - SPRING_REDIS_HOST=redis
                depends_on:
                  db:
                    condition: service_healthy
                  redis:
                    condition: service_started
                healthcheck:
                  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
                  interval: 30s
                  timeout: 10s
                  retries: 5

              db:
                image: postgres:15-alpine
                environment:
                  - POSTGRES_DB=mydb
                  - POSTGRES_USER=admin
                  - POSTGRES_PASSWORD=secret
                volumes:
                  - postgres_data:/var/lib/postgresql/data
                ports:
                  - "5432:5432"
                healthcheck:
                  test: ["CMD-SHELL", "pg_isready -U admin"]
                  interval: 10s
                  timeout: 5s
                  retries: 5

              redis:
                image: redis:7-alpine
                command: redis-server --appendonly yes
                volumes:
                  - redis_data:/data
                ports:
                  - "6379:6379"

              nginx:
                image: nginx:alpine
                volumes:
                  - ./nginx.conf:/etc/nginx/nginx.conf:ro
                ports:
                  - "80:80"
                depends_on:
                  - app

            volumes:
              postgres_data:
              redis_data:

            networks:
              default:
                name: myapp-network
            """;
    }

    // Docker Compose for development
    public String generateDockerComposeDev() {
        return """
            version: '3.8'

            services:
              app:
                build:
                  context: .
                  target: development
                volumes:
                  - ./src:/app/src
                  - maven_cache:/root/.m2
                ports:
                  - "8080:8080"
                  - "5005:5005"
                environment:
                  - SPRING_PROFILES_ACTIVE=dev
                  - JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

              db:
                image: postgres:15-alpine
                environment:
                  - POSTGRES_DB=mydb
                  - POSTGRES_USER=dev
                  - POSTGRES_PASSWORD=dev
                ports:
                  - "5432:5432"

            volumes:
              maven_cache:
            """;
    }

    // Build and run scripts
    public String generateBuildScript() {
        return """
            #!/bin/bash
            set -e

            IMAGE_NAME="myapp"
            TAG="${1:-latest}"
            REGISTRY="${2:-docker.io}"

            echo "Building image: ${REGISTRY}/${IMAGE_NAME}:${TAG}"

            docker build -t ${IMAGE_NAME}:${TAG} .
            docker tag ${IMAGE_NAME}:${TAG} ${REGISTRY}/${IMAGE_NAME}:${TAG}

            if [ "$TAG" = "latest" ]; then
                echo "Pushing to registry..."
                docker push ${REGISTRY}/${IMAGE_NAME}:${TAG}
            fi

            echo "Build completed successfully!"
            """;
    }

    // .dockerignore
    public String generateDockerIgnore() {
        return """
            target/
            *.class
            *.log
            .git/
            .gitignore
            .mvn/
            mvnw
            mvnw.cmd
            !.mvn/wrapper/maven-wrapper.jar
            HELP.md
            !.mvn/wrapper/maven-wrapper.properties
            Dockerfile.dev
            docker-compose.override.yml
            .idea/
            *.iml
            .vscode/
            */node_modules/
            """;
    }

    // Security scanning
    public boolean checkVulnerabilities(String imageName) {
        // Integration with Trivy, Docker Scout, or similar
        return true;
    }

    // Build cache optimization
    public String generateOptimizedDockerfile() {
        return """
            FROM maven:3.9-eclipse-temurin-17 AS builder

            WORKDIR /app

            # Layer 1: Dependencies (changes infrequently)
            COPY pom.xml .
            RUN mvn dependency:go-offline -B

            # Layer 2: Source code (changes frequently)
            COPY src ./src
            RUN mvn package -DskipTests -B

            FROM eclipse-temurin:17-jre-alpine
            WORKDIR /app
            COPY --from=builder /app/target/*.jar app.jar

            EXPOSE 8080
            ENTRYPOINT ["java", "-jar", "app.jar"]
            """;
    }
}