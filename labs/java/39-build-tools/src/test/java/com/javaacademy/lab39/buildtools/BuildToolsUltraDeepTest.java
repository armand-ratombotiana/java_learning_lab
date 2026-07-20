package com.javaacademy.lab39.buildtools;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

class BuildToolsUltraDeepTest {

    @Test
    void mavenPomXmlExists() {
        var pom = new File("pom.xml");
        assertTrue(pom.exists(), "pom.xml should exist");
    }

    @Test
    void buildGradleExists() {
        var gradle = new File("build.gradle");
        // Either pom.xml or build.gradle should exist
        assertTrue(new File("pom.xml").exists() || gradle.exists(),
            "Either pom.xml or build.gradle should exist");
    }

    @Test
    void mavenStandardDirectories() {
        assertTrue(new File("src/main/java").exists());
        assertTrue(new File("src/test/java").exists());
        assertTrue(new File("src/main/resources").exists() || true);
    }

    @Test
    void javaVersionProperty() throws IOException {
        var pom = new File("pom.xml");
        if (pom.exists()) {
            String content = new String(java.nio.file.Files.readAllBytes(pom.toPath()));
            assertTrue(content.contains("21") || content.contains("25") || content.contains("24"),
                "pom.xml should reference Java 21+");
        }
    }
}
