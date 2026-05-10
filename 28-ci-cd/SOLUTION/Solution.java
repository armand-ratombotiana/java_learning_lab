package solution;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CICDSolution {

    public static class PipelineConfig {
        public String name;
        public List<Stage> stages = new ArrayList<>();

        public static PipelineConfig fromYaml(String yaml) {
            return new PipelineConfig();
        }
    }

    public static class Stage {
        public String name;
        public List<Job> jobs = new ArrayList<>();
        public boolean enabled = true;

        public Stage(String name) {
            this.name = name;
        }

        public Stage addJob(Job job) {
            this.jobs.add(job);
            return this;
        }
    }

    public static class Job {
        public String name;
        public String image;
        public List<String> commands = new ArrayList<>();
        public Map<String, String> env = new HashMap<>();
        public List<String> caches = new ArrayList<>();

        public Job(String name) {
            this.name = name;
        }

        public Job withImage(String image) {
            this.image = image;
            return this;
        }

        public Job withCommands(String... commands) {
            this.commands.addAll(Arrays.asList(commands));
            return this;
        }

        public Job withEnv(String key, String value) {
            this.env.put(key, value);
            return this;
        }

        public Job withCache(String path) {
            this.caches.add(path);
            return this;
        }
    }

    // Jenkins Pipeline
    public String generateJenkinsfile() {
        return """
            pipeline {
                agent any
                environment {
                    DOCKER_REGISTRY = 'docker.io'
                }
                stages {
                    stage('Build') {
                        steps {
                            sh 'mvn clean package'
                        }
                    }
                    stage('Test') {
                        steps {
                            sh 'mvn test'
                        }
                        post {
                            always {
                                junit 'target/surefire-reports/*.xml'
                            }
                        }
                    }
                    stage('Docker Build') {
                        steps {
                            sh 'docker build -t myapp:${BUILD_NUMBER} .'
                        }
                    }
                    stage('Deploy') {
                        when { branch 'main' }
                        steps {
                            sh 'kubectl apply -f k8s/'
                        }
                    }
                }
                post {
                    always {
                        cleanWs()
                    }
                    success {
                        echo 'Pipeline succeeded!'
                    }
                    failure {
                        echo 'Pipeline failed!'
                    }
                }
            }
            """;
    }

    // GitHub Actions
    public String generateGitHubActionsWorkflow() {
        return """
            name: CI/CD Pipeline

            on:
              push:
                branches: [ main, develop ]
              pull_request:
                branches: [ main ]

            jobs:
              build:
                runs-on: ubuntu-latest
                steps:
                  - uses: actions/checkout@v4
                  - name: Set up JDK
                    uses: actions/setup-java@v4
                    with:
                      java-version: '17'
                      distribution: 'temulus'
                      cache: maven
                  - name: Build
                    run: mvn clean package -DskipTests
                  - name: Test
                    run: mvn test
                  - name: Upload test results
                    if: always()
                    uses: actions/upload-artifact@v4
                    with:
                      name: test-results
                      path: target/surefire-reports/

              docker:
                runs-on: ubuntu-latest
                needs: build
                steps:
                  - uses: actions/checkout@v4
                  - name: Build Docker image
                    run: docker build -t myapp:${{ github.sha }} .
                  - name: Push to registry
                    run: |
                      echo "${{ secrets.DOCKER_TOKEN }}" | docker login -u ${{ secrets.DOCKER_USER }} --password-stdin
                      docker push myapp:${{ github.sha }}

              deploy:
                runs-on: ubuntu-latest
                needs: docker
                if: github.ref == 'refs/heads/main'
                steps:
                  - name: Deploy to Kubernetes
                    run: |
                      echo "${{ secrets.KUBECONFIG }}" > kubeconfig
                      kubectl apply -f k8s/ --kubeconfig=kubeconfig
            """;
    }

    // GitLab CI
    public String generateGitLabCI() {
        return """
            stages:
              - build
              - test
              - docker
              - deploy

            variables:
              MAVEN_OPTS: "-Dmaven.repo.local=.m2/repository"

            build:
              stage: build
              image: maven:3.9-eclipse-temurin-17
              script:
                - mvn clean package -DskipTests
              artifacts:
                paths:
                  - target/*.jar

            test:
              stage: test
              image: maven:3.9-eclipse-temurin-17
              script:
                - mvn test
              coverage: '/Coverage: \\d+/'
              artifacts:
                reports:
                  junit: target/surefire-reports/*.xml

            docker:
              stage: docker
              image: docker:24
              services:
                - docker:24-dind
              script:
                - docker build -t myapp:$CI_COMMIT_SHA .
                - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
                - docker push myapp:$CI_COMMIT_SHA

            deploy:
              stage: deploy
              image: bitnami/kubectl:latest
              script:
                - kubectl apply -f k8s/
              only:
                - main
            """;
    }

    // Maven wrapper for build
    public String generateMavenSettings() {
        return """
            <settings>
              <servers>
                <server>
                  <id>nexus</id>
                  <username>${env.NEXUS_USER}</username>
                  <password>${env.NEXUS_PASS}</password>
                </server>
              </servers>
              <mirrors>
                <mirror>
                  <id>nexus</id>
                  <mirrorOf>*</mirrorOf>
                  <url>https://nexus.example.com/repository/maven-public/</url>
                </mirror>
              </mirrors>
            </settings>
            """;
    }

    // Docker build with multi-stage
    public String generateDockerfile() {
        return """
            # Build stage
            FROM maven:3.9-eclipse-temurin-17 AS build
            WORKDIR /app
            COPY pom.xml .
            COPY src ./src
            RUN mvn clean package -DskipTests

            # Runtime stage
            FROM eclipse-temurin:17-jre-alpine
            WORKDIR /app
            COPY --from=build /app/target/*.jar app.jar
            EXPOSE 8080
            ENV JAVA_OPTS="-Xmx512m -Xms256m"
            ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
            """;
    }
}