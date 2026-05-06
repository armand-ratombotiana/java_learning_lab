package com.learning.boot;

import java.util.*;

public class SpringBootAdvancedTraining {

    public static void main(String[] args) {
        System.out.println("=== Spring Boot Advanced Training ===");

        demonstrateActuator();
        demonstrateProfiles();
        demonstrateConfiguration();
        demonstrateDeployment();
    }

    private static void demonstrateActuator() {
        System.out.println("\n--- Spring Boot Actuator ---");

        String[] endpoints = {
            "/actuator - list all endpoints",
            "/actuator/health - application health",
            "/actuator/info - application info",
            "/actuator/beans - all Spring beans",
            "/actuator/conditions - auto-config conditions",
            "/actuator/env - environment properties",
            "/actuator/metrics - application metrics",
            "/actuator/loggers - logger configuration",
            "/actuator/threaddump - thread dump",
            "/actuator/heapdump - heap dump",
            "/actuator/prometheus - Prometheus format"
        };

        for (String e : endpoints) System.out.println("  " + e);

        System.out.println("\nEnable Endpoints:");
        String config = """
            management:
              endpoints:
                web:
                  exposure:
                    include: health,info,metrics,env
                  base-path: /actuator
              endpoint:
                health:
                  show-details: always
            """;
        System.out.println(config);

        System.out.println("\nCustom Health Indicator:");
        String health = """
            @Component
            public class DatabaseHealthIndicator 
                    implements HealthIndicator {
                @Override
                public Health health() {
                    if (isDatabaseUp()) {
                        return Health.up()
                            .withDetail("db", "connected")
                            .build();
                    }
                    return Health.down()
                        .withDetail("db", "disconnected")
                        .build();
                }
            }""";
        System.out.println(health);
    }

    private static void demonstrateProfiles() {
        System.out.println("\n--- Spring Profiles ---");

        String[] profileTypes = {
            "dev - development environment",
            "test - testing environment",
            "staging - staging environment",
            "prod - production environment"
        };

        System.out.println("Common Profiles:");
        for (String p : profileTypes) System.out.println("  " + p);

        System.out.println("\nActivating Profiles:");
        String[] activation = {
            "SPRING_PROFILES_ACTIVE=dev",
            "--spring.profiles.active=prod",
            "application-dev.yml (auto)",
            "@Profile(\"dev\") annotation"
        };

        for (String a : activation) System.out.println("  " + a);

        System.out.println("\nProfile-specific Config:");
        String config = """
            # application.yml (common config)
            spring:
              application:
                name: myapp
            
            # application-dev.yml
            spring:
              config:
                activate:
                  on-profile: dev
              datasource:
                url: jdbc:h2:mem:devdb
                hikari:
                  maximum-pool-size: 5
            
            # application-prod.yml
            spring:
              config:
                activate:
                  on-profile: prod
              datasource:
                url: jdbc:postgresql://prod-server/db
                hikari:
                  maximum-pool-size: 20""";
        System.out.println(config);
    }

    private static void demonstrateConfiguration() {
        System.out.println("\n--- Externalized Configuration ---");

        String[] configSources = {
            "1. Command line arguments",
            "2. OS environment variables",
            "3. Application properties (application.properties)",
            "4. Application YAML (application.yml)",
            "5. @PropertySource annotations",
            "6. RandomValuePropertySource"
        };

        System.out.println("Configuration Priority (high to low):");
        for (String s : configSources) System.out.println("  " + s);

        System.out.println("\n@ConfigurationProperties:");
        String props = """
            @ConfigurationProperties(prefix = "app")
            @Validated
            public class AppProperties {
                
                @NotBlank
                private String name;
                
                @Min(1) @Max(100)
                private int maxConnections;
                
                private List<String> allowedOrigins;
                
                private Map<String, String> settings;
            }""";
        System.out.println(props);

        System.out.println("\nConfiguration Properties:");
        String yaml = """
            app:
              name: my-application
              max-connections: 50
              allowed-origins:
                - https://example.com
                - https://api.example.com
              settings:
                timezone: UTC
                locale: en-US""";
        System.out.println(yaml);
    }

    private static void demonstrateDeployment() {
        System.out.println("\n--- Deployment Options ---");

        String[] deployment = {
            "Executable JAR - java -jar app.jar",
            "Docker Container - multi-stage build",
            "Cloud Platforms - AWS, GCP, Azure",
            "Kubernetes - container orchestration",
            "Cloud Foundry - managed platform",
            "Heroku - simple deployment"
        };

        System.out.println("Deployment Methods:");
        for (String d : deployment) System.out.println("  " + d);

        System.out.println("\nDockerfile Example:");
        String dockerfile = """
            FROM eclipse-temurin:21-jdk-alpine AS build
            WORKDIR /app
            COPY . .
            RUN ./mvnw package -DskipTests
            
            FROM eclipse-temurin:21-jre-alpine
            WORKDIR /app
            COPY --from=build /app/target/*.jar app.jar
            EXPOSE 8080
            ENTRYPOINT ["java", "-jar", "app.jar"]""";
        System.out.println(dockerfile);

        System.out.println("\nBuild Properties:");
        String build = """
            <build>
              <finalName>${project.artifactId}</finalName>
              <plugins>
                <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
                  <configuration>
                    <executable>true</executable>
                  </configuration>
                </plugin>
              </plugins>
            </build>""";
        System.out.println(build);

        System.out.println("\nProduction Checklist:");
        String[] checklist = {
            "Enable security (HTTPS, authentication)",
            "Configure proper logging levels",
            "Set up health checks and monitoring",
            "Use external configuration",
            "Optimize JVM parameters",
            "Enable compression and caching"
        };
        for (String c : checklist) System.out.println("  " + c);
    }
}