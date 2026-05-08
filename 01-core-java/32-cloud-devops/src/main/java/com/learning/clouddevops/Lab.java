package com.learning.clouddevops;

import java.util.*;
import java.util.concurrent.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Cloud & DevOps Lab ===\n");

        cloudServices();
        containerization();
        ciCdPipeline();
        infrastructureAsCode();
        deploymentStrategies();
    }

    static void cloudServices() {
        System.out.println("--- Cloud Service Models ---");

        for (var m : List.of(
            "IaaS: VMs, networks, storage (AWS EC2, GCE, Azure VM)",
            "PaaS: Platform manages runtime (Heroku, App Engine, Elastic Beanstalk)",
            "SaaS: Fully managed (Gmail, Salesforce, Slack)",
            "FaaS: Serverless functions (Lambda, Cloud Functions, Azure Functions)",
            "CaaS: Container orchestration (EKS, GKE, AKS)"))
            System.out.println("  " + m);

        System.out.println("\n  Shared Responsibility:");
        System.out.println("""
            On-Prem: App+Runtime+OS+Infra = You
            IaaS:    App+Runtime+OS = You, Infra = Provider
            PaaS:    App = You, Runtime+OS+Infra = Provider
            SaaS:    Everything = Provider""");
    }

    static void containerization() {
        System.out.println("\n--- Containerization ---");

        System.out.println("  Dockerfile:");
        for (var l : List.of("FROM eclipse-temurin:21-jre", "WORKDIR /app",
                "COPY target/app.jar app.jar", "EXPOSE 8080", "USER 1000",
                "HEALTHCHECK --interval=30s CMD curl -f http://localhost:8080/actuator/health",
                "ENTRYPOINT [\"java\",\"-jar\",\"app.jar\"]"))
            System.out.println("  " + l);

        System.out.println("\n  Docker Compose:");
        System.out.println("""
            services:
              app: build: .  ports: ["8080:8080"]
              db:  image: postgres:16  volumes: - pgdata:/var/lib/postgresql/data
              redis: image: redis:7-alpine
            volumes: { pgdata: }""");

        System.out.println("\n  Best practices: small base image, layer caching, multi-stage builds, non-root, health checks");
    }

    static void ciCdPipeline() {
        System.out.println("\n--- CI/CD Pipeline ---");

        System.out.println("  Stages:");
        for (var s : List.of("Code Checkout (Git)", "Static Analysis (SonarQube)",
                "Unit Tests (JUnit + Maven)", "Integration Tests (Testcontainers)",
                "Build & Package (Maven)", "Docker Image (Buildx)",
                "Push to Registry (Docker Hub)", "Deploy to Staging (K8s)"))
            System.out.println("  " + s);

        System.out.println("\n  GitHub Actions workflow:");
        System.out.println("""
            name: Java CI/CD
            on: { push: { branches: [main] } }
            jobs:
              build:
                runs-on: ubuntu-latest
                steps:
                  - uses: actions/checkout@v4
                  - uses: actions/setup-java@v4
                    with: { java-version: '21', distribution: 'temurin' }
                  - run: mvn clean package
                  - run: mvn verify""");
    }

    static void infrastructureAsCode() {
        System.out.println("\n--- Infrastructure as Code ---");

        for (var t : List.of("Terraform: declarative, cloud-agnostic",
                "CloudFormation: AWS-native (YAML/JSON)",
                "Ansible: imperative, configuration management",
                "Pulumi: real programming languages (Java/TS/Python)"))
            System.out.println("  " + t);

        System.out.println("\n  Immutable vs mutable:");
        System.out.println("  Mutable: update in-place (Ansible, Chef)");
        System.out.println("  Immutable: recreate from image (Terraform, Packer)");

        System.out.println("\n  Terraform example:");
        System.out.println("""
            resource "aws_instance" "app" {
              ami = "ami-0c55b159cbfafe1f0"
              instance_type = "t3.medium"
              tags = { Name = "app-server" }
            }""");
    }

    static void deploymentStrategies() {
        System.out.println("\n--- Deployment Strategies ---");

        for (var s : List.of("Rolling Update: gradual replacement, zero downtime",
                "Blue/Green: switch traffic between two identical envs",
                "Canary: route small traffic % to new version",
                "A/B Testing: route based on user segments",
                "Recreate: shut down old, start new (full downtime)",
                "Shadow: run new version in parallel, mirror traffic"))
            System.out.println("  " + s);

        System.out.println("\n  Kubernetes rolling update:");
        System.out.println("""
            strategy:
              type: RollingUpdate
              rollingUpdate: { maxUnavailable: 1, maxSurge: 1 }""");

        System.out.println("\n  Observability pillars:");
        for (var p : List.of("Logging (ELK, Loki)", "Metrics (Prometheus, Grafana)",
                "Tracing (Jaeger, OpenTelemetry)", "Alerting (Alertmanager, PagerDuty)"))
            System.out.println("  " + p);

        System.out.println("\n  Golden Signals: Latency, Traffic, Errors, Saturation");
        System.out.println("  SLO/SLI/SLA: 99.9% uptime, error budget ~3.6h/month");
    }
}
