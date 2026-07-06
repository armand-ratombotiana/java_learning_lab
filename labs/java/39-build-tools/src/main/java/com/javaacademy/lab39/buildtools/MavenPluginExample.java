package com.javaacademy.lab39.buildtools;

import java.io.*;
import java.util.*;

public class MavenPluginExample {

    public static class MojoDescriptor {
        private final String goal;
        private final String phase;
        private final String description;
        private final Map<String, String> parameters = new HashMap<>();

        public MojoDescriptor(String goal, String phase, String description) {
            this.goal = goal;
            this.phase = phase;
            this.description = description;
        }

        public MojoDescriptor addParameter(String name, String type) {
            parameters.put(name, type);
            return this;
        }

        public String getGoal() { return goal; }
        public String getPhase() { return phase; }
        public String getDescription() { return description; }
        public Map<String, String> getParameters() { return parameters; }
    }

    private final List<MojoDescriptor> mojos = new ArrayList<>();

    public MavenPluginExample() {
        mojos.add(new MojoDescriptor("compile", "compile", "Compiles source code")
            .addParameter("sourceDir", "File")
            .addParameter("targetDir", "File")
            .addParameter("release", "String"));

        mojos.add(new MojoDescriptor("test", "test", "Runs unit tests")
            .addParameter("testClassesDir", "File")
            .addParameter("includes", "String[]")
            .addParameter("excludes", "String[]"));

        mojos.add(new MojoDescriptor("package", "package", "Packages compiled classes")
            .addParameter("finalName", "String")
            .addParameter("classifier", "String"));
    }

    public List<MojoDescriptor> getMojos() { return mojos; }

    public String generatePomSnippet() {
        StringBuilder sb = new StringBuilder();
        sb.append("<plugin>\n");
        sb.append("    <groupId>com.example</groupId>\n");
        sb.append("    <artifactId>example-maven-plugin</artifactId>\n");
        sb.append("    <version>1.0.0</version>\n");
        sb.append("    <executions>\n");
        sb.append("        <execution>\n");
        sb.append("            <goals>\n");
        for (MojoDescriptor mojo : mojos) {
            sb.append("                <goal>").append(mojo.getGoal()).append("</goal>\n");
        }
        sb.append("            </goals>\n");
        sb.append("            <phase>compile</phase>\n");
        sb.append("        </execution>\n");
        sb.append("    </executions>\n");
        sb.append("</plugin>\n");
        return sb.toString();
    }

    public String describeLifecycle() {
        return String.join(" -> ", "validate", "compile", "test", "package", "verify", "install", "deploy");
    }

    public String getPhaseDescription(String phase) {
        return switch (phase) {
            case "validate" -> "Validate project is correct and all necessary information is available";
            case "compile" -> "Compile source code";
            case "test" -> "Test compiled source code using unit test framework";
            case "package" -> "Package compiled code in distributable format (JAR, WAR, etc.)";
            case "verify" -> "Run integration tests to verify package quality";
            case "install" -> "Install package into local repository";
            case "deploy" -> "Copy package to remote repository for sharing";
            default -> "Unknown phase: " + phase;
        };
    }
}
