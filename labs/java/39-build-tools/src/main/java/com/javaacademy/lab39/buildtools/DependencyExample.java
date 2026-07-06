package com.javaacademy.lab39.buildtools;

import java.util.*;

public class DependencyExample {

    public enum Scope { COMPILE, IMPLEMENTATION, TEST, RUNTIME, PROVIDED }

    public static class Dependency {
        private final String groupId;
        private final String artifactId;
        private final String version;
        private final Scope scope;
        private final List<String> exclusions = new ArrayList<>();
        private final boolean transitive;

        public Dependency(String groupId, String artifactId, String version, Scope scope, boolean transitive) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
            this.scope = scope;
            this.transitive = transitive;
        }

        public String getMavenCoordinate() {
            return groupId + ":" + artifactId + ":" + version + ":" + scope.name().toLowerCase();
        }

        public Dependency addExclusion(String coord) { exclusions.add(coord); return this; }

        public String getGroupId() { return groupId; }
        public String getArtifactId() { return artifactId; }
        public String getVersion() { return version; }
        public Scope getScope() { return scope; }
        public List<String> getExclusions() { return exclusions; }
        public boolean isTransitive() { return transitive; }
    }

    private final List<Dependency> dependencies = new ArrayList<>();

    public DependencyExample() {
        dependencies.add(new Dependency("org.junit.jupiter", "junit-jupiter-api", "5.10.2", Scope.TEST, true));
        dependencies.add(new Dependency("org.mockito", "mockito-core", "5.10.0", Scope.TEST, true)
            .addExclusion("org.hamcrest:hamcrest-core"));
        dependencies.add(new Dependency("com.fasterxml.jackson.core", "jackson-databind", "2.16.1", Scope.IMPLEMENTATION, true));
        dependencies.add(new Dependency("ch.qos.logback", "logback-classic", "1.4.14", Scope.IMPLEMENTATION, true));
        dependencies.add(new Dependency("org.projectlombok", "lombok", "1.18.30", Scope.PROVIDED, false));
    }

    public List<Dependency> getDependencies() { return dependencies; }
    public List<Dependency> getDependenciesByScope(Scope scope) {
        return dependencies.stream().filter(d -> d.getScope() == scope).toList();
    }

    public Dependency resolveConflict(Dependency a, Dependency b) {
        return a.getVersion().compareTo(b.getVersion()) >= 0 ? a : b;
    }

    public String generateDependencyTree() {
        StringBuilder sb = new StringBuilder();
        buildTree(sb, "", dependencies, new HashSet<>());
        return sb.toString();
    }

    private void buildTree(StringBuilder sb, String prefix, List<Dependency> deps, Set<String> seen) {
        for (int i = 0; i < deps.size(); i++) {
            Dependency dep = deps.get(i);
            boolean last = i == deps.size() - 1;
            sb.append(prefix).append(last ? "└── " : "├── ").append(dep.getMavenCoordinate());
            sb.append(dep.isTransitive() ? "" : " (direct)").append("\n");
            seen.add(dep.getGroupId() + ":" + dep.getArtifactId());
        }
    }
}
