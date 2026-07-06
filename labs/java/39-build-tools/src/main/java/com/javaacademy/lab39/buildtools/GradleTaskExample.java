package com.javaacademy.lab39.buildtools;

import java.util.*;

public class GradleTaskExample {

    public static class GradleTask {
        private final String name;
        private final String type;
        private final String description;
        private final List<String> dependsOn = new ArrayList<>();
        private final Map<String, Object> extensions = new HashMap<>();

        public GradleTask(String name, String type, String description) {
            this.name = name;
            this.type = type;
            this.description = description;
        }

        public GradleTask dependsOn(String... tasks) {
            dependsOn.addAll(Arrays.asList(tasks));
            return this;
        }

        public GradleTask extension(String key, Object value) {
            extensions.put(key, value);
            return this;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public List<String> getDependsOn() { return dependsOn; }
        public Map<String, Object> getExtensions() { return extensions; }
    }

    private final List<GradleTask> tasks = new ArrayList<>();

    public GradleTaskExample() {
        tasks.add(new GradleTask("clean", "Delete", "Deletes build directory")
            .extension("deleteRoot", "build"));

        tasks.add(new GradleTask("compileJava", "JavaCompile", "Compiles Java sources")
            .dependsOn("clean")
            .extension("sourceCompatibility", "21")
            .extension("targetCompatibility", "21"));

        tasks.add(new GradleTask("test", "Test", "Runs JUnit tests")
            .dependsOn("compileJava", "processResources")
            .extension("useJUnitPlatform", true));

        tasks.add(new GradleTask("jar", "Jar", "Creates JAR file")
            .dependsOn("compileJava"));
    }

    public List<GradleTask> getTasks() { return tasks; }

    public String generateBuildGradleSnippet() {
        StringBuilder sb = new StringBuilder();
        sb.append("plugins {\n");
        sb.append("    id 'java'\n");
        sb.append("    id 'application'\n");
        sb.append("}\n\n");
        sb.append("repositories {\n");
        sb.append("    mavenCentral()\n");
        sb.append("}\n\n");
        sb.append("dependencies {\n");
        sb.append("    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'\n");
        sb.append("}\n\n");
        sb.append("java {\n");
        sb.append("    sourceCompatibility = JavaVersion.VERSION_21\n");
        sb.append("    targetCompatibility = JavaVersion.VERSION_21\n");
        sb.append("}\n\n");
        sb.append("test {\n");
        sb.append("    useJUnitPlatform()\n");
        sb.append("}\n");
        return sb.toString();
    }

    public List<String> getTaskDag(String taskName) {
        List<String> dag = new ArrayList<>();
        buildDag(taskName, dag, new HashSet<>());
        return dag;
    }

    private void buildDag(String taskName, List<String> dag, Set<String> visited) {
        if (visited.contains(taskName)) return;
        visited.add(taskName);
        for (GradleTask task : tasks) {
            if (task.getName().equals(taskName)) {
                for (String dep : task.getDependsOn()) {
                    buildDag(dep, dag, visited);
                }
                dag.add(taskName);
            }
        }
    }

    public String describeTaskTree() {
        StringBuilder sb = new StringBuilder();
        for (GradleTask task : tasks) {
            sb.append(task.getName()).append(" (").append(task.getType()).append(")\n");
            for (String dep : task.getDependsOn()) {
                sb.append("  - depends on: ").append(dep).append("\n");
            }
        }
        return sb.toString();
    }
}
