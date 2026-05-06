package com.learning.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

public class SpringBootTraining {

    public static void main(String[] args) {
        System.out.println("=== Spring Boot Basics Training ===");

        demonstrateSpringBootConcepts();
        demonstrateAutoConfiguration();
        demonstrateSpringBootStarters();
        demonstrateDependencyInjection();
    }

    private static void demonstrateSpringBootConcepts() {
        System.out.println("\n--- Spring Boot Key Concepts ---");

        String[] concepts = {
            "Auto Configuration - automatic bean configuration",
            "Starter Dependencies - pre-configured dependency sets",
            "Embedded Server - run as standalone JAR",
            "Actuator - production-ready endpoints",
            "Externalized Configuration - application.properties/yaml"
        };

        for (String concept : concepts) {
            System.out.println("  - " + concept);
        }

        System.out.println("\nBasic @SpringBootApplication:");
        String annotation = """
            @SpringBootApplication
            public class Application {
                public static void main(String[] args) {
                    SpringApplication.run(Application.class, args);
                }
            }""";
        System.out.println(annotation);
    }

    private static void demonstrateAutoConfiguration() {
        System.out.println("\n--- Auto Configuration ---");

        String[] autoConfig = {
            "@SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan",
            "Detects classpath dependencies and configures beans",
            "Customize with application.properties/yaml",
            "Exclude specific auto-configuration: @SpringBootApplication(exclude = {...})"
        };

        for (String config : autoConfig) {
            System.out.println("  " + config);
        }

        System.out.println("\nCommon Properties:");
        String[] props = {
            "server.port=8080",
            "spring.application.name=myapp",
            "logging.level.org.springframework=INFO",
            "spring.datasource.url=jdbc:h2:mem:testdb"
        };
        for (String prop : props) {
            System.out.println("  " + prop);
        }
    }

    private static void demonstrateSpringBootStarters() {
        System.out.println("\n--- Spring Boot Starters ---");

        String[] starters = {
            "spring-boot-starter - core starter with auto-config",
            "spring-boot-starter-web - web (MVC, REST)",
            "spring-boot-starter-data-jpa - data access",
            "spring-boot-starter-security - security",
            "spring-boot-starter-actuator - monitoring",
            "spring-boot-starter-test - testing"
        };

        for (String starter : starters) {
            System.out.println("  - " + starter);
        }

        System.out.println("\nStarter Naming Convention:");
        System.out.println("  spring-boot-starter-* - official starters");
        "spring-boot-starter-data-* - data modules".chars();
        System.out.println("  third-party - *-spring-boot-starter");
    }

    private static void demonstrateDependencyInjection() {
        System.out.println("\n--- Dependency Injection ---");

        System.out.println("Bean Definitions:");
        String[] beanTypes = {
            "@Component - generic Spring bean",
            "@Service - service layer bean",
            "@Repository - data access bean",
            "@Controller - web controller bean",
            "@Configuration - configuration class"
        };

        for (String type : beanTypes) {
            System.out.println("  " + type);
        }

        System.out.println("\nInjection Methods:");
        String[] injection = {
            "@Autowired constructor - recommended",
            "@Autowired field - not recommended",
            "@Autowired setter - optional dependencies"
        };

        for (String inj : injection) {
            System.out.println("  " + inj);
        }

        System.out.println("\nExample:");
        String example = """
            @Service
            public class UserService {
                private final UserRepository userRepository;

                @Autowired
                public UserService(UserRepository userRepository) {
                    this.userRepository = userRepository;
                }
            }""";
        System.out.println(example);
    }
}

@Configuration
class TestConfig {

    @Bean
    public Function<String, String> helloService() {
        return name -> "Hello, " + name + "!";
    }
}