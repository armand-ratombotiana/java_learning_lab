package solution;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CICDSolutionTest {

    @Test
    void testJenkinsfileGeneration() {
        CICDSolution solution = new CICDSolution();
        String jenkinsfile = solution.generateJenkinsfile();
        assertNotNull(jenkinsfile);
        assertTrue(jenkinsfile.contains("pipeline"));
        assertTrue(jenkinsfile.contains("mvn clean package"));
    }

    @Test
    void testGitHubActionsWorkflow() {
        CICDSolution solution = new CICDSolution();
        String workflow = solution.generateGitHubActionsWorkflow();
        assertNotNull(workflow);
        assertTrue(workflow.contains("jobs:"));
        assertTrue(workflow.contains("mvn test"));
    }

    @Test
    void testGitLabCI() {
        CICDSolution solution = new CICDSolution();
        String gitlabCI = solution.generateGitLabCI();
        assertNotNull(gitlabCI);
        assertTrue(gitlabCI.contains("stages:"));
        assertTrue(gitlabCI.contains("mvn clean package"));
    }

    @Test
    void testDockerfile() {
        CICDSolution solution = new CICDSolution();
        String dockerfile = solution.generateDockerfile();
        assertNotNull(dockerfile);
        assertTrue(dockerfile.contains("FROM maven"));
        assertTrue(dockerfile.contains("mvn clean package"));
    }

    @Test
    void testMavenSettings() {
        CICDSolution solution = new CICDSolution();
        String settings = solution.generateMavenSettings();
        assertNotNull(settings);
        assertTrue(settings.contains("<settings>"));
    }

    @Test
    void testPipelineConfig() {
        CICDSolution.PipelineConfig config = new CICDSolution.PipelineConfig();
        CICDSolution.Stage stage = new CICDSolution.Stage("Build");
        CICDSolution.Job job = new CICDSolution.Job("build-job")
            .withImage("maven:17")
            .withCommands("mvn package");
        stage.addJob(job);
        config.stages.add(stage);

        assertEquals(1, config.stages.size());
        assertEquals("Build", config.stages.get(0).name);
    }
}