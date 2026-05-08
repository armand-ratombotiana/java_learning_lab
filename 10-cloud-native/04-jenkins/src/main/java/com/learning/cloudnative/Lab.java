package com.learning.cloudnative;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Jenkins CI/CD Concepts ===\n");

        demonstrateArchitecture();
        demonstrateFreestyleJobs();
        demonstratePipelineAsCode();
        demonstrateDeclarativePipeline();
        demonstrateDistributedBuilds();
        demonstrateIntegration();
    }

    private static void demonstrateArchitecture() {
        System.out.println("--- Architecture ---");
        System.out.println("Jenkins Master (Controller):");
        System.out.println("  Web UI + REST API + Scheduling");
        System.out.println("  Manages jobs, plugins, and build queue");
        System.out.println();
        System.out.println("Jenkins Agent (Node):");
        System.out.println("  Executes build jobs");
        System.out.println("  Can be SSH slaves, JNLP agents, or Docker containers");
        System.out.println("  Ephemeral agents (Docker/K8s) for scalability");
        System.out.println();
        System.out.println("Home directory (JENKINS_HOME) = jobs, configs, builds, plugins");
    }

    private static void demonstrateFreestyleJobs() {
        System.out.println("\n--- Freestyle Jobs ---");
        System.out.println("Simplest job type: configure through UI");
        System.out.println("SCM: Git, SVN (branch-based checkout)");
        System.out.println("Build triggers: SCM poll, cron, webhook, upstream");
        System.out.println("Build steps: Execute shell/batch, Maven/Gradle, invoke targets");
        System.out.println("Post-build: archive artifacts, publish reports, notify (email/Slack)");
        System.out.println();
        System.out.println("Limitations: UI-bound, not reproducible, hard to version");
    }

    private static void demonstratePipelineAsCode() {
        System.out.println("\n--- Pipeline as Code ---");
        System.out.println("Jenkinsfile = Pipeline definition stored in SCM (Git)");
        System.out.println("Benefits: versioned, reviewed, reusable, templateable");
        System.out.println();
        System.out.println("Two syntaxes:");
        System.out.println("  1. Declarative (simpler, structured)");
        System.out.println("  2. Scripted (flexible, Groovy-based, uses 'node' block)");
    }

    private static void demonstrateDeclarativePipeline() {
        System.out.println("\n--- Declarative Pipeline Structure ---");
        System.out.println("pipeline {");
        System.out.println("    agent any");
        System.out.println("    stages {");
        System.out.println("        stage('Checkout') { steps { checkout scm } }");
        System.out.println("        stage('Build')    { steps { sh 'mvn clean package' } }");
        System.out.println("        stage('Test')     { steps { sh 'mvn test' } }");
        System.out.println("        stage('Deploy')   { steps { sh 'deploy.sh' } }");
        System.out.println("    }");
        System.out.println("    post {");
        System.out.println("        success { emailext to: 'team@x.com', subject: 'Build OK' }");
        System.out.println("        failure { slackSend message: 'Build FAILED' }");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();
        System.out.println("Directives: when, environment, options, parameters, triggers");
        System.out.println("Parallel execution: stage('Test') { parallel { ... } }");
    }

    private static void demonstrateDistributedBuilds() {
        System.out.println("\n--- Distributed Builds ---");
        System.out.println("1. Static Agents    -> Pre-configured VMs/machines");
        System.out.println("2. Docker Agents    -> On-demand containers per job");
        System.out.println("3. Kubernetes Agents-> Pods as build agents (ephemeral)");
        System.out.println("4. Cloud Agents     -> AWS EC2, Azure VMs on-demand");
        System.out.println();
        System.out.println("Kubernetes plugin: agent { kubernetes { ... } }");
        System.out.println("  -> Spins pod, runs build, terminates pod");
    }

    private static void demonstrateIntegration() {
        System.out.println("\n--- Integration & Plugins ---");
        System.out.println("Blue Ocean -> Modern pipeline UI");
        System.out.println("GitHub/GitLab/Bitbucket integration -> webhook triggers");
        System.out.println("SonarQube  -> Static analysis quality gates");
        System.out.println("Nexus/Artifactory -> Artifact storage");
        System.out.println("Shared Libraries -> Reusable pipeline code across repos");
        System.out.println("Pipeline: milestone() + lock() for ordered deployments");
    }
}
