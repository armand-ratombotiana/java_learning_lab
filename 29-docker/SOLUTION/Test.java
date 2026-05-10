package solution;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DockerSolutionTest {

    @Test
    void testMultiStageDockerfile() {
        DockerSolution solution = new DockerSolution();
        String dockerfile = solution.generateMultiStageDockerfile();
        assertNotNull(dockerfile);
        assertTrue(dockerfile.contains("FROM maven"));
        assertTrue(dockerfile.contains("FROM eclipse-temurin"));
        assertTrue(dockerfile.contains("COPY --from=builder"));
        assertTrue(dockerfile.contains("HEALTHCHECK"));
    }

    @Test
    void testDockerCompose() {
        DockerSolution solution = new DockerSolution();
        String compose = solution.generateDockerCompose();
        assertNotNull(compose);
        assertTrue(compose.contains("services:"));
        assertTrue(compose.contains("app:"));
        assertTrue(compose.contains("db:"));
        assertTrue(compose.contains("redis:"));
    }

    @Test
    void testDockerComposeDev() {
        DockerSolution solution = new DockerSolution();
        String compose = solution.generateDockerComposeDev();
        assertNotNull(compose);
        assertTrue(compose.contains("volumes:"));
        assertTrue(compose.contains("JAVA_TOOL_OPTIONS"));
    }

    @Test
    void testDockerIgnore() {
        DockerSolution solution = new DockerSolution();
        String dockerIgnore = solution.generateDockerIgnore();
        assertNotNull(dockerIgnore);
        assertTrue(dockerIgnore.contains("target/"));
    }

    @Test
    void testBuildScript() {
        DockerSolution solution = new DockerSolution();
        String script = solution.generateBuildScript();
        assertNotNull(script);
        assertTrue(script.contains("docker build"));
        assertTrue(script.contains("docker push"));
    }

    @Test
    void testVulnerabilityCheck() {
        DockerSolution solution = new DockerSolution();
        assertTrue(solution.checkVulnerabilities("myapp:latest"));
    }
}