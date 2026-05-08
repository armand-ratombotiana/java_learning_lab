package com.learning.buildtools;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Build Tools Lab ===");

        mavenLifecycle();
        dependencyManagement();
        gradleConcepts();
        buildScripting();
        ciPipeline();
    }

    static void mavenLifecycle() {
        System.out.println("\n--- Maven Build Lifecycle ---");

        class Phase {
            String name, description;
            Phase(String n, String d) { name = n; description = d; }
        }

        List<Phase> phases = List.of(
            new Phase("validate", "Validate project is correct"),
            new Phase("compile", "Compile source code"),
            new Phase("test-compile", "Compile test sources"),
            new Phase("test", "Run tests with surefire"),
            new Phase("package", "Package as JAR/WAR"),
            new Phase("verify", "Run integration tests"),
            new Phase("install", "Install to local repo (~/.m2)"),
            new Phase("deploy", "Deploy to remote repo")
        );

        System.out.println("Default lifecycle phases (in order):");
        for (int i = 0; i < phases.size(); i++) {
            System.out.printf("  %d. %-14s %s%n", i + 1, phases.get(i).name, phases.get(i).description);
        }

        System.out.println("\nCommon commands:");
        Map<String, String> cmds = new LinkedHashMap<>();
        cmds.put("mvn clean compile", "Compile only");
        cmds.put("mvn clean test", "Compile + test");
        cmds.put("mvn clean package -DskipTests", "Package without tests");
        cmds.put("mvn dependency:tree", "Show dependency tree");
        cmds.put("mvn help:effective-pom", "Show resolved POM");
        cmds.put("mvn versions:display-dependency-updates", "Check updates");
        cmds.forEach((cmd, desc) -> System.out.printf("  %-45s %s%n", cmd, desc));
    }

    static void dependencyManagement() {
        System.out.println("\n--- Dependency Management ---");

        record Dep(String groupId, String artifactId, String version, String scope) {}

        List<Dep> deps = List.of(
            new Dep("org.junit.jupiter", "junit-jupiter", "5.11.0", "test"),
            new Dep("org.mockito", "mockito-core", "5.12.0", "test"),
            new Dep("com.h2database", "h2", "2.2.224", "runtime"),
            new Dep("ch.qos.logback", "logback-classic", "1.5.6", "compile"),
            new Dep("com.fasterxml.jackson.core", "jackson-databind", "2.17.1", "compile")
        );

        System.out.println("Sample Maven dependencies:");
        deps.forEach(d -> System.out.printf(
            "  <dependency>%n    <groupId>%s</groupId>%n    <artifactId>%s</artifactId>%n    <version>%s</version>%n    <scope>%s</scope>%n  </dependency>%n",
            d.groupId(), d.artifactId(), d.version(), d.scope()));

        System.out.println("\nTransitive dependency resolution:");
        Set<String> resolved = new LinkedHashSet<>();
        resolved.add("jackson-databind 2.17.1");
        resolved.add("  └─ jackson-core 2.17.1 (transitive)");
        resolved.add("  └─ jackson-annotations 2.17.1 (transitive)");
        resolved.add("logback-classic 1.5.6");
        resolved.add("  └─ slf4j-api 2.0.13 (transitive)");
        resolved.forEach(System.out::println);
    }

    static void gradleConcepts() {
        System.out.println("\n--- Gradle Concepts ---");

        System.out.println("Gradle build script (build.gradle.kts):");
        String kotlinDsl = """
            plugins {
                java
                id("org.springframework.boot") version "3.3.0"
            }

            repositories {
                mavenCentral()
            }

            dependencies {
                implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
                implementation("org.springframework.boot:spring-boot-starter-web")
                testImplementation("org.junit.jupiter:junit-jupiter")
            }

            tasks.test {
                useJUnitPlatform()
            }
            """;
        System.out.println(kotlinDsl);

        System.out.println("Key Gradle tasks:");
        String[][] tasks = {
            {"build", "Full build + test"},
            {"clean", "Delete build directory"},
            {"bootRun", "Run Spring Boot app"},
            {"test --tests *Integration*", "Run specific tests"},
            {"dependencies", "Show dependency tree"},
            {"build --scan", "Build with scan report"}
        };
        for (String[] t : tasks) System.out.printf("  gradle %-30s %s%n", t[0], t[1]);

        System.out.println("\nMaven vs Gradle comparison:");
        String[][] comparison = {
            {"Build file", "pom.xml (XML)", "build.gradle (Groovy/Kotlin)"},
            {"Performance", "Incremental (limited)", "Incremental + build cache"},
            {"Configuration", "XML verbose", "DSL concise"},
            {"Dependencies", "Centralized in POM", "Per-source-set"},
            {"Flexibility", "Plugin-based", "Script + plugin-based"}
        };
        for (String[] row : comparison) System.out.printf("  %-20s %-20s %s%n", row[0], row[1], row[2]);
    }

    static class Task {
        String name;
        Runnable action;
        List<String> dependsOn = new ArrayList<>();
        Task(String n, Runnable a) { name = n; action = a; }
    }

    static void buildScripting() {
        System.out.println("\n--- Build Automation Scripting ---");

        Map<String, Task> tasks = new LinkedHashMap<>();
        tasks.put("clean", new Task("clean", () -> System.out.println("  Deleting build/ directory")));
        tasks.put("compile", new Task("compile", () -> System.out.println("  Compiling src/ -> build/classes")));
        tasks.put("testCompile", new Task("testCompile", () -> System.out.println("  Compiling test/ -> build/test-classes")));
        tasks.put("test", new Task("test", () -> System.out.println("  Running tests... All passed!")));
        tasks.put("package", new Task("package", () -> System.out.println("  Creating app.jar")));

        tasks.get("compile").dependsOn.add("clean");
        tasks.get("testCompile").dependsOn.add("compile");
        tasks.get("test").dependsOn.add("testCompile");
        tasks.get("package").dependsOn.add("test");

        System.out.println("Running 'package' task (simulated DAG execution):");
        Set<String> executed = new HashSet<>();
        runTask("package", tasks, executed);
    }

    static void runTask(String name, Map<String, Task> tasks, Set<String> executed) {
        Task t = tasks.get(name);
        if (t == null || executed.contains(name)) return;
        for (String dep : t.dependsOn) runTask(dep, tasks, executed);
        t.action.run();
        executed.add(name);
    }

    static void ciPipeline() {
        System.out.println("\n--- CI/CD Pipeline Stages ---");

        record Stage(String name, boolean success) {}
        List<Stage> stages = List.of(
            new Stage("Checkout", true),
            new Stage("Lint/Static analysis", true),
            new Stage("Unit tests", true),
            new Stage("Integration tests", true),
            new Stage("Package", true),
            new Stage("Deploy to staging", true),
            new Stage("Smoke tests", false)  // fails
        );

        System.out.println("Simulated CI pipeline execution:");
        for (Stage s : stages) {
            String icon = s.success() ? "PASS" : "FAIL";
            System.out.printf("  [%s] %s%n", icon, s.name());
            if (!s.success()) {
                System.out.println("  -> Pipeline aborted at: " + s.name());
                System.out.println("  -> Developer notified. Fix and re-push.");
                break;
            }
        }

        System.out.println("\nCI/CD tools ecosystem:");
        Map<String, String> tools = new LinkedHashMap<>();
        tools.put("Jenkins", "Extensible CI/CD server");
        tools.put("GitHub Actions", "Integrated CI/CD with workflow YAML");
        tools.put("GitLab CI", "Built-in CI/CD with .gitlab-ci.yml");
        tools.put("CircleCI", "Cloud-native CI platform");
        tools.put("SonarQube", "Continuous code quality inspection");
        tools.forEach((t, d) -> System.out.printf("  %-20s %s%n", t, d));
    }
}
