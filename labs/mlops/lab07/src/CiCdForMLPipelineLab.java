package com.mlops.lab07;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CI/CD for ML Pipelines — Lab 07.
 * <p>
 * Demonstrates CI/CD pipeline concepts for ML workflows.
 * Models pipeline stages with quality gates, generates GitHub Actions
 * workflow YAML, and simulates automated model validation before deployment.
 */
public class CiCdForMLPipelineLab {

    /** Represents a single stage in an ML CI/CD pipeline. */
    static class PipelineStage {
        final String name;
        final List<String> dependencies;
        final Runnable action;
        boolean passed;

        PipelineStage(String name, List<String> dependencies, Runnable action) {
            this.name = name;
            this.dependencies = dependencies;
            this.action = action;
        }

        void execute() {
            System.out.printf("  ▶ Stage: %s%n", name);
            try {
                action.run();
                passed = true;
                System.out.printf("    ✓ %s passed%n", name);
            } catch (Exception e) {
                passed = false;
                System.out.printf("    ✗ %s FAILED: %s%n", name, e.getMessage());
            }
        }
    }

    /** Generates a GitHub Actions workflow YAML for ML pipelines. */
    static String generateGitHubActionsWorkflow() {
        return """
                name: ML Pipeline CI/CD
                
                on:
                  push:
                    branches: [main]
                  pull_request:
                    branches: [main]
                  schedule:
                    - cron: '0 6 * * 1'  # Weekly retrain
                
                jobs:
                  data-validation:
                    runs-on: ubuntu-latest
                    steps:
                      - uses: actions/checkout@v4
                      - name: Validate Data Schema
                        run: java -cp . com.mlops.lab09.DataValidationLab
                
                  training:
                    needs: data-validation
                    runs-on: ubuntu-latest
                    steps:
                      - uses: actions/checkout@v4
                      - name: Setup Java 21
                        uses: actions/setup-java@v4
                        with:
                          java-version: '21'
                          distribution: 'temurin'
                      - name: Train Model
                        run: |
                          javac src/com/mlops/lab07/*.java
                          java com.mlops.lab07.CiCdForMLPipelineLab
                      - name: Upload Model Artifact
                        uses: actions/upload-artifact@v4
                        with:
                          name: model
                          path: model.bin
                
                  evaluation:
                    needs: training
                    runs-on: ubuntu-latest
                    steps:
                      - name: Evaluate Model
                        run: |
                          echo "Evaluating model against champion..."
                          # Compare metrics, run statistical tests
                          echo "Accuracy: 0.947 (champion: 0.935) ✓"
                
                  deploy-staging:
                    needs: evaluation
                    runs-on: ubuntu-latest
                    steps:
                      - name: Deploy to Staging
                        run: echo "Deploying model to staging..."
                
                  integration-test:
                    needs: deploy-staging
                    runs-on: ubuntu-latest
                    steps:
                      - name: Run Integration Tests
                        run: |
                          echo "Running shadow traffic test..."
                          echo "All integration tests passed ✓"
                
                  deploy-production:
                    needs: integration-test
                    runs-on: ubuntu-latest
                    environment: production
                    steps:
                      - name: Deploy to Production
                        run: echo "Deploying model to production..."
                """;
    }

    /** Generates a Jenkinsfile (Declarative Pipeline) for ML workflows. */
    static String generateJenkinsfile() {
        return """
                pipeline {
                    agent any
                    stages {
                        stage('Data Validation') {
                            steps { sh 'java -cp . com.mlops.lab09.DataValidationLab' }
                        }
                        stage('Feature Engineering') {
                            steps { sh 'java -cp . com.mlops.lab04.FeatureStoreLab' }
                        }
                        stage('Model Training') {
                            steps { sh 'java -cp . com.mlops.lab01.MLOpsPipelineOrchestrationLab' }
                        }
                        stage('Model Evaluation') {
                            steps {
                                script {
                                    def accuracy = sh(script: 'echo 0.947', returnStdout: true).trim()
                                    def champion = sh(script: 'echo 0.935', returnStdout: true).trim()
                                    if (accuracy.toDouble() < champion.toDouble()) {
                                        error "Model accuracy ${accuracy} below champion ${champion}"
                                    }
                                }
                            }
                        }
                        stage('Deploy to Staging') {
                            steps { sh 'echo "Deploying..."' }
                        }
                        stage('Integration Tests') {
                            steps { sh 'echo "Running shadow tests..."' }
                        }
                        stage('Deploy to Production') {
                            when { branch 'main' }
                            steps { sh 'echo "Promoting to production..."' }
                        }
                    }
                    post {
                        failure { sh 'echo "Pipeline failed — notifying team"' }
                        success { sh 'echo "Pipeline succeeded"' }
                    }
                }
                """;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("=== CI/CD for ML Pipelines ===\n");

        // Simulate pipeline stages
        List<PipelineStage> stages = List.of(
            new PipelineStage("Code Checkout", List.of(), () -> {
                simulateWork(300);
                if (Math.random() < 0.1) throw new RuntimeException("Git checkout failed");
            }),
            new PipelineStage("Data Validation", List.of("Code Checkout"), () -> {
                simulateWork(500);
                System.out.println("    Schema valid, distributions OK");
            }),
            new PipelineStage("Feature Engineering", List.of("Data Validation"), () -> {
                simulateWork(800);
                System.out.println("    Features computed");
            }),
            new PipelineStage("Model Training", List.of("Feature Engineering"), () -> {
                simulateWork(1200);
                System.out.println("    Model trained with accuracy=0.947");
            }),
            new PipelineStage("Model Evaluation", List.of("Model Training"), () -> {
                simulateWork(400);
                double accuracy = 0.947;
                double champion = 0.935;
                if (accuracy < champion) {
                    throw new RuntimeException(
                            "Accuracy " + accuracy + " < champion " + champion);
                }
                System.out.printf("    ✓ Accuracy %.3f > champion %.3f%n", accuracy, champion);
            }),
            new PipelineStage("Deploy Staging", List.of("Model Evaluation"), () -> {
                simulateWork(300);
                System.out.println("    Model deployed to staging");
            }),
            new PipelineStage("Integration Tests", List.of("Deploy Staging"), () -> {
                simulateWork(600);
                System.out.println("    Shadow test passed (1000 requests, 0 errors)");
            }),
            new PipelineStage("Deploy Production", List.of("Integration Tests"), () -> {
                simulateWork(200);
                System.out.println("    ✓ Model promoted to production via MLflow registry");
            })
        );

        // Build dependency map
        Map<String, PipelineStage> stageMap = stages.stream()
                .collect(Collectors.toMap(s -> s.name, s -> s));

        // Execute in dependency order
        for (PipelineStage stage : stages) {
            boolean depsOk = stage.dependencies.stream()
                    .allMatch(d -> stageMap.get(d).passed);
            if (!depsOk) {
                System.out.printf("  ⧖ Stage: %s — skipped (dependency failed)%n", stage.name);
                continue;
            }
            stage.execute();
            if (!stage.passed) {
                System.out.printf("%nPipeline FAILED at stage: %s%n", stage.name);
                break;
            }
        }

        // Generate workflow files
        System.out.println("\n=== Generated: .github/workflows/ml-pipeline.yml ===");
        String workflow = generateGitHubActionsWorkflow();
        System.out.println(workflow);

        System.out.println("\n=== Generated: Jenkinsfile ===");
        String jenkinsfile = generateJenkinsfile();
        System.out.println(jenkinsfile);

        // Write to files
        Path githubDir = Paths.get(".github", "workflows");
        Files.createDirectories(githubDir);
        Files.writeString(githubDir.resolve("ml-pipeline.yml"), workflow, StandardCharsets.UTF_8);
        Files.writeString(Paths.get("Jenkinsfile"), jenkinsfile, StandardCharsets.UTF_8);
        System.out.println("Workflow files written to .github/workflows/ and Jenkinsfile");
    }

    private static void simulateWork(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
