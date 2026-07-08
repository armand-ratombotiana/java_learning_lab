package com.devops.thirteen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

class ApplicationSetGeneratorTest {
    private ApplicationSetGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ApplicationSetGenerator("test-appset", ApplicationSetGenerator.GeneratorType.LIST);
    }

    @Test
    @DisplayName("Should generate applications from list")
    void testGenerateFromList() {
        List<Map<String, String>> elements = List.of(
            Map.of("cluster", "us-east-1", "environment", "prod"),
            Map.of("cluster", "us-west-2", "environment", "staging"));
        generator.generateFromList(elements);
        List<ArgoCDApplication> apps = generator.generateApplications(
            "https://github.com/org/repo.git", "k8s/overlays", "main", "default");
        assertEquals(2, apps.size());
        assertEquals("test-appset-us-east-1", apps.get(0).getName());
        assertEquals("test-appset-us-west-2", apps.get(1).getName());
    }

    @Test
    @DisplayName("Should generate from Git directory")
    void testGenerateFromGit() {
        generator.generateFromGit("https://github.com/org/repo.git", "k8s/overlays/prod");
        List<ArgoCDApplication> apps = generator.generateApplications(
            "https://github.com/org/repo.git", "k8s/overlays", "main", "default");
        assertFalse(apps.isEmpty());
    }

    @Test
    @DisplayName("Should generate from cluster list")
    void testGenerateFromClusters() {
        List<String> clusters = List.of(
            "https://kubernetes.us-east-1.example.com",
            "https://kubernetes.eu-west-1.example.com");
        generator.generateFromClusters(clusters);
        List<ArgoCDApplication> apps = generator.generateApplications(
            "https://github.com/org/repo.git", "k8s/overlays", "main", "default");
        assertEquals(2, apps.size());
    }

    @Test
    @DisplayName("Should generate and sync generated applications")
    void testGenerateAndSync() {
        List<Map<String, String>> elements = List.of(
            Map.of("cluster", "dev", "environment", "dev"));
        generator.generateFromList(elements);
        List<ArgoCDApplication> apps = generator.generateApplications(
            "https://github.com/org/repo.git", "k8s/overlays", "main", "default");
        for (ArgoCDApplication app : apps) {
            assertTrue(app.sync());
        }
    }
}
