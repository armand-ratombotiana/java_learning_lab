package com.javaacademy.lab39.buildtools;

public class MultiModuleExample {

    public static class Module {
        private final String name;
        private final String path;
        private final String description;

        public Module(String name, String path, String description) {
            this.name = name;
            this.path = path;
            this.description = description;
        }

        public String getName() { return name; }
        public String getPath() { return path; }
        public String getDescription() { return description; }
    }

    private final Module[] modules = {
        new Module("core", "modules/core", "Core domain model and shared utilities"),
        new Module("api", "modules/api", "REST API controllers and DTOs"),
        new Module("persistence", "modules/persistence", "Database access and repositories"),
        new Module("service", "modules/service", "Business logic and orchestration"),
        new Module("webapp", "modules/webapp", "Web application entry point")
    };

    public Module[] getModules() { return modules; }

    public String generateMavenParentPom() {
        StringBuilder sb = new StringBuilder();
        sb.append("<project>\n");
        sb.append("    <modelVersion>4.0.0</modelVersion>\n");
        sb.append("    <groupId>com.example</groupId>\n");
        sb.append("    <artifactId>multi-module-project</artifactId>\n");
        sb.append("    <version>1.0.0</version>\n");
        sb.append("    <packaging>pom</packaging>\n");
        sb.append("    <modules>\n");
        for (Module m : modules) {
            sb.append("        <module>").append(m.getPath()).append("</module>\n");
        }
        sb.append("    </modules>\n");
        sb.append("</project>\n");
        return sb.toString();
    }

    public String generateSettingsGradle() {
        StringBuilder sb = new StringBuilder();
        sb.append("rootProject.name = 'multi-module-project'\n\n");
        for (Module m : modules) {
            String safeName = m.getName().substring(0, 1).toUpperCase() + m.getName().substring(1);
            sb.append("include '").append(m.getName()).append("'\n");
        }
        return sb.toString();
    }

    public String describeModuleDependencies() {
        return """
            Module dependency graph:
            webapp -> service -> persistence
                       |
                       v
                      api -> core
            """;
    }
}
