package com.javaacademy.lab39.buildtools;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class BuildToolsTest {

    @Test @DisplayName("Maven lifecycle phases ordered correctly")
    void mavenLifecycle() {
        MavenPluginExample maven = new MavenPluginExample();
        String lifecycle = maven.describeLifecycle();
        assertTrue(lifecycle.contains("validate"));
        assertTrue(lifecycle.contains("compile"));
        assertTrue(lifecycle.contains("test"));
        assertTrue(lifecycle.contains("package"));
        assertTrue(lifecycle.contains("deploy"));
    }

    @Test @DisplayName("Maven plugin mojos defined")
    void mavenMojos() {
        MavenPluginExample maven = new MavenPluginExample();
        assertEquals(3, maven.getMojos().size());
    }

    @Test @DisplayName("Gradle task DAG resolution")
    void gradleTaskDag() {
        GradleTaskExample gradle = new GradleTaskExample();
        var dag = gradle.getTaskDag("test");
        assertTrue(dag.contains("compileJava"));
        assertTrue(dag.contains("test"));
    }

    @Test @DisplayName("Gradle build script generation")
    void gradleBuildScript() {
        GradleTaskExample gradle = new GradleTaskExample();
        String script = gradle.generateBuildGradleSnippet();
        assertTrue(script.contains("junit-jupiter"));
        assertTrue(script.contains("useJUnitPlatform"));
    }

    @Test @DisplayName("Dependency scopes correctly classified")
    void dependencyScopes() {
        DependencyExample deps = new DependencyExample();
        assertEquals(2, deps.getDependenciesByScope(DependencyExample.Scope.TEST).size());
        assertEquals(1, deps.getDependenciesByScope(DependencyExample.Scope.PROVIDED).size());
    }

    @Test @DisplayName("Dependency conflict resolution uses highest version")
    void dependencyConflict() {
        DependencyExample deps = new DependencyExample();
        var v1 = new DependencyExample.Dependency("a", "b", "2.0", DependencyExample.Scope.IMPLEMENTATION, true);
        var v2 = new DependencyExample.Dependency("a", "b", "3.0", DependencyExample.Scope.IMPLEMENTATION, true);
        var resolved = deps.resolveConflict(v1, v2);
        assertEquals("3.0", resolved.getVersion());
    }

    @Test @DisplayName("Multi-module project structure")
    void multiModule() {
        MultiModuleExample mm = new MultiModuleExample();
        assertEquals(5, mm.getModules().length);
        assertTrue(mm.generateMavenParentPom().contains("modules/core"));
        assertTrue(mm.generateSettingsGradle().contains("core"));
    }

    @Test @DisplayName("Phase descriptions are complete")
    void phaseDescriptions() {
        MavenPluginExample maven = new MavenPluginExample();
        assertTrue(maven.getPhaseDescription("compile").contains("Compile"));
        assertTrue(maven.getPhaseDescription("test").contains("test"));
        assertTrue(maven.getPhaseDescription("deploy").contains("remote"));
    }

    @Test @DisplayName("Gradle task tree generation")
    void gradleTaskTree() {
        GradleTaskExample gradle = new GradleTaskExample();
        String tree = gradle.describeTaskTree();
        assertTrue(tree.contains("compileJava"));
        assertTrue(tree.contains("JavaCompile"));
    }
}
