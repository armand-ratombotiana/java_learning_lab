package com.learning.cloud;

import java.util.*;

public class CloudDevOpsTraining {

    public static void main(String[] args) {
        System.out.println("=== Cloud and DevOps Training ===");

        demonstrateCloudProviders();
        demonstrateCICD();
        demonstrateKubernetes();
        demonstrateInfrastructureAsCode();
    }

    private static void demonstrateCloudProviders() {
        System.out.println("\n--- Cloud Providers ---");

        System.out.println("AWS Services:");
        Map<String, String> aws = new LinkedHashMap<>();
        aws.put("EC2", "Virtual servers");
        aws.put("Lambda", "Serverless functions");
        aws.put("RDS", "Managed databases");
        aws.put("S3", "Object storage");
        aws.put("EKS", "Kubernetes service");
        aws.put("ECS", "Container service");
        aws.put("CloudWatch", "Monitoring");
        aws.put("IAM", "Access management");
        aws.forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));

        System.out.println("\nGCP Services:");
        Map<String, String> gcp = new LinkedHashMap<>();
        gcp.put("Compute Engine", "Virtual machines");
        gcp.put("Cloud Run", "Container hosting");
        gcp.put("Cloud SQL", "Managed databases");
        gcp.put("Cloud Storage", "Object storage");
        gcp.put("GKE", "Kubernetes engine");
        gcp.put("Cloud Monitoring", "Observability");
        gcp.forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));

        System.out.println("\nAzure Services:");
        Map<String, String> azure = new LinkedHashMap<>();
        azure.put("Azure VMs", "Virtual machines");
        azure.put("Azure Functions", "Serverless");
        azure.put("Azure SQL", "Managed databases");
        azure.put("Azure Blob", "Object storage");
        azure.put("AKS", "Kubernetes service");
        azure.put("Azure DevOps", "CI/CD platform");
        azure.forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));

        System.out.println("\nCloud Comparison:");
        String[] comparison = {
            "AWS: Most services, largest market share",
            "GCP: Strong in data analytics, ML",
            "Azure: Best for Microsoft integration"
        };
        for (String c : comparison) System.out.println("  " + c);
    }

    private static void demonstrateCICD() {
        System.out.println("\n--- CI/CD Pipelines ---");

        System.out.println("CI/CD Stages:");
        String[] stages = {
            "1. Source - code push to VCS",
            "2. Build - compile and package",
            "3. Test - unit, integration tests",
            "4. Analyze - code quality, security",
            "5. Staging - deploy to test env",
            "6. Production - deploy to prod"
        };
        for (String s : stages) System.out.println("  " + s);

        System.out.println("\nCI/CD Tools:");
        Map<String, String> tools = new LinkedHashMap<>();
        tools.put("Jenkins", "Open-source automation server");
        tools.put("GitHub Actions", "GitHub native CI/CD");
        tools.put("GitLab CI", "GitLab integrated CI/CD");
        tools.put("CircleCI", "Cloud-native CI/CD");
        tools.put("Travis CI", "GitHub CI service");
        tools.put("Azure DevOps", "Microsoft CI/CD");
        tools.forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));

        System.out.println("\nGitHub Actions Example:");
        String githubActions = """
            name: Java CI
            
            on:
              push:
                branches: [main]
              pull_request:
                branches: [main]
            
            jobs:
              build:
                runs-on: ubuntu-latest
                steps:
                  - uses: actions/checkout@v3
                  - name: Set up JDK 21
                    uses: actions/setup-java@v3
                    with:
                      java-version: '21'
                  - name: Build with Maven
                    run: mvn clean package
                  - name: Run tests
                    run: mvn test""";
        System.out.println(githubActions);

        System.out.println("\nDeployment Strategies:");
        String[] strategies = {
            "Blue-Green - two identical environments",
            "Canary - gradual rollout percentage",
            "Rolling - incremental updates",
            "Feature Flags - toggle features"
        };
        for (String s : strategies) System.out.println("  - " + s);
    }

    private static void demonstrateKubernetes() {
        System.out.println("\n--- Kubernetes (K8s) ---");

        System.out.println("K8s Architecture:");
        String[] arch = {
            "Master Node - control plane (API server, etcd, scheduler)",
            "Worker Nodes - run containers (kubelet, kube-proxy)",
            "Pods - smallest deployable unit",
            "Services - stable network endpoint",
            "Deployments - declarative updates"
        };
        for (String a : arch) System.out.println("  " + a);

        System.out.println("\nK8s Resources:");
        String[] resources = {
            "Pod - container(s) definition",
            "Service - network exposure",
            "Deployment - app deployment",
            "StatefulSet - stateful apps",
            "ConfigMap - configuration",
            "Secret - sensitive data",
            "Ingress - HTTP routing",
            "PersistentVolume - storage"
        };
        for (String r : resources) System.out.println("  - " + r);

        System.out.println("\nK8s YAML Example:");
        String k8s = """
            apiVersion: apps/v1
            kind: Deployment
            metadata:
              name: myapp
            spec:
              replicas: 3
              selector:
                matchLabels:
                  app: myapp
              template:
                metadata:
                  labels:
                    app: myapp
                spec:
                  containers:
                  - name: myapp
                    image: myapp:1.0
                    ports:
                    - containerPort: 8080
                    resources:
                      limits:
                        memory: "512Mi"
                        cpu: "500m\"""";
        System.out.println(k8s);

        System.out.println("\nK8s Commands:");
        String[] commands = {
            "kubectl get pods - list pods",
            "kubectl apply -f deploy.yaml - apply config",
            "kubectl logs -f pod-name - view logs",
            "kubectl exec -it pod-name -- bash - shell access",
            "kubectl describe pod pod-name - pod details"
        };
        for (String c : commands) System.out.println("  " + c);
    }

    private static void demonstrateInfrastructureAsCode() {
        System.out.println("\n--- Infrastructure as Code (IaC) ---");

        System.out.println("IaC Tools:");
        Map<String, String> tools = new LinkedHashMap<>();
        tools.put("Terraform", "Cloud-agnostic, HCL");
        tools.put("AWS CDK", "AWS native, code-based");
        tools.put("Pulumi", "Real languages");
        tools.put("CloudFormation", "AWS JSON/YAML");
        tools.put("ARM Templates", "Azure JSON");
        tools.forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));

        System.out.println("\nTerraform Example:");
        String tf = """
            provider "aws" {
              region = "us-east-1"
            }
            
            resource "aws_instance" "web" {
              ami           = "ami-12345"
              instance_type = "t3.micro"
              tags = {
                Name = "web-server"
              }
            }
            
            resource "aws_s3_bucket" "data" {
              bucket = "my-data-bucket"
            }""";
        System.out.println(tf);

        System.out.println("\nDevOps Best Practices:");
        String[] practices = {
            "Infrastructure as Code",
            "Automated testing and deployment",
            "Immutable infrastructure",
            "Monitoring and alerting",
            "Log aggregation",
            "Security scanning in pipeline",
            "Immutable deployments",
            "Configuration management"
        };
        for (String p : practices) System.out.println("  - " + p);

        System.out.println("\nMonitoring Stack:");
        String[] monitoring = {
            "Prometheus - metrics collection",
            "Grafana - visualization",
            "ELK Stack - log aggregation",
            "Jaeger - distributed tracing"
        };
        for (String m : monitoring) System.out.println("  - " + m);
    }
}