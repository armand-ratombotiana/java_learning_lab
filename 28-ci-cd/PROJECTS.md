# CI/CD Module - PROJECTS.md

---

# Mini-Project 1: CI Pipeline (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: GitHub Actions, Maven Build, JUnit Tests, Build Artifacts

Build a complete CI pipeline using GitHub Actions for a Java application.

---

## Project Structure

```
28-ci-cd/
├── pom.xml
├── src/main/java/com/learning/
│   └── App.java
├── src/test/java/com/learning/
│   └── AppTest.java
├── .github/
│   └── workflows/
│       └── ci.yml
└── Jenkinsfile
```

---

## POM.xml

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>ci-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.2</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Implementation

```java
// App.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    public String greet(String name) {
        return "Hello, " + name + "!";
    }
    
    public int add(int a, int b) {
        return a + b;
    }
    
    public boolean isEven(int number) {
        return number % 2 == 0;
    }
    
    public String getStatus() {
        return "Application is running";
    }
}
```

```java
// AppTest.java
package com.learning;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    
    @Test
    void testGreet() {
        App app = new App();
        assertEquals("Hello, World!", app.greet("World"));
    }
    
    @Test
    void testAdd() {
        App app = new App();
        assertEquals(5, app.add(2, 3));
        assertEquals(0, app.add(-1, 1));
    }
    
    @Test
    void testIsEven() {
        App app = new App();
        assertTrue(app.isEven(4));
        assertFalse(app.isEven(5));
        assertTrue(app.isEven(0));
    }
    
    @Test
    void testGetStatus() {
        App app = new App();
        assertEquals("Application is running", app.getStatus());
    }
}
```

```yaml
# .github/workflows/ci.yml
name: CI Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  JAVA_VERSION: '17'
  MAVEN_VERSION: '3.9.5'

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: maven
      
      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-m2-
      
      - name: Build with Maven
        run: mvn clean compile
      
      - name: Run tests
        run: mvn test
      
      - name: Generate test report
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: test-results
          path: target/surefire-reports/
      
      - name: Package application
        run: mvn package -DskipTests
      
      - name: Upload artifact
        uses: actions/upload-artifact@v3
        with:
          name: application-jar
          path: target/*.jar

  code-quality:
    runs-on: ubuntu-latest
    needs: build
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
      
      - name: Checkstyle
        run: mvn checkstyle:check
      
      - name: Spotbugs
        run: mvn spotbugs:check
      
      - name: PMD Analysis
        run: mvn pmd:check

  security-scan:
    runs-on: ubuntu-latest
    needs: build
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Run OWASP Dependency Check
        run: mvn org.owasp:dependency-check-maven:check
      
      - name: Upload security report
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: security-report
          path: target/dependency-check-report.html
```

---

## Build Instructions

```bash
cd 28-ci-cd
mvn clean compile
mvn test
mvn package -DskipTests
```

---

# Mini-Project 2: CD Pipeline (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Deployment, Environment Promotion, Approval Gates

Build a CD pipeline with multiple environments and promotion logic.

---

## Project Structure

```
28-ci-cd/
├── .github/
│   └── workflows/
│       ├── build.yml
│       ├── deploy-dev.yml
│       ├── deploy-staging.yml
│       └── deploy-prod.yml
├── deployment/
│   ├── dev/
│   │   └── deployment.yaml
│   ├── staging/
│   │   └── deployment.yaml
│   └── prod/
│       └── deployment.yaml
└── scripts/
    ├── deploy.sh
    └── health-check.sh
```

---

## Implementation

```yaml
# .github/workflows/build.yml
name: Build and Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    outputs:
      version: ${{ steps.version.outputs.version }}
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Build application
        run: mvn clean package -DskipTests
      
      - name: Set version
        id: version
        run: echo "version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)" >> $GITHUB_OUTPUT
      
      - name: Upload JAR artifact
        uses: actions/upload-artifact@v3
        with:
          name: app-jar
          path: target/*.jar
      
      - name: Upload Dockerfiles
        uses: actions/upload-artifact@v3
        with:
          name: dockerfiles
          path: |
            Dockerfile
            Dockerfile.multistage
      
      - name: Upload deployment configs
        uses: actions/upload-artifact@v3
        with:
          name: deployment-configs
          path: deployment/
```

```yaml
# .github/workflows/deploy-dev.yml
name: Deploy to Development

on:
  workflow_run:
    workflows: [Build and Test]
    types: [completed]
    branches: [develop]

jobs:
  deploy-dev:
    runs-on: ubuntu-latest
    if: ${{ github.event.workflow_run.conclusion == 'success' }}
    environment: development
    
    steps:
      - name: Download artifacts
        uses: actions/download-artifact@v3
        with:
          name: deployment-configs
          path: deployment/
      
      - name: Configure kubectl
        run: |
          echo "${{ secrets.KUBE_CONFIG_DEV }}" > kubeconfig
          export KUBECONFIG=kubeconfig
      
      - name: Deploy to Dev
        run: |
          kubectl apply -f deployment/dev/
          kubectl rollout status deployment/app-dev --timeout=300s
      
      - name: Run health check
        run: |
          sleep 10
          curl -f http://app-dev.example.com/health || exit 1
```

```yaml
# .github/workflows/deploy-staging.yml
name: Deploy to Staging

on:
  workflow_dispatch:
    inputs:
      version:
        description: 'Version to deploy'
        required: true

jobs:
  deploy-staging:
    runs-on: ubuntu-latest
    environment: staging
    
    steps:
      - name: Download artifacts
        uses: actions/download-artifact@v3
        with:
          name: deployment-configs
          path: deployment/
      
      - name: Configure kubectl
        run: |
          echo "${{ secrets.KUBE_CONFIG_STAGING }}" > kubeconfig
      
      - name: Deploy to Staging
        run: |
          kubectl set image deployment/app-staging app=app:${{ github.event.inputs.version }}
          kubectl rollout status deployment/app-staging --timeout=300s
      
      - name: Run integration tests
        run: |
          mvn verify -Dspring.profiles=staging
      
      - name: Notify Slack on success
        if: success()
        run: |
          curl -X POST -H 'Content-type: application/json' \
            --data '{"text":"Successfully deployed version ${{ github.event.inputs.version }} to staging"}' \
            ${{ secrets.SLACK_WEBHOOK }}
```

```yaml
# .github/workflows/deploy-prod.yml
name: Deploy to Production

on:
  workflow_dispatch:
    inputs:
      version:
        description: 'Version to deploy'
        required: true

jobs:
  approve-deployment:
    runs-on: ubuntu-latest
    environment: production-approval
    outputs:
      approved: ${{ steps.approval.outputs.approved }}
    
    steps:
      - id: approval
        run: echo "approved=true" >> $GITHUB_OUTPUT

  deploy-production:
    needs: approve-deployment
    runs-on: ubuntu-latest
    environment: production
    
    steps:
      - name: Download artifacts
        uses: actions/download-artifact@v3
        with:
          name: deployment-configs
          path: deployment/
      
      - name: Configure kubectl
        run: |
          echo "${{ secrets.KUBE_CONFIG_PROD }}" > kubeconfig
      
      - name: Blue-Green Deployment
        run: |
          kubectl apply -f deployment/prod/blue/
          kubectl set image deployment/app-blue app=app:${{ github.event.inputs.version }}
          kubectl rollout status deployment/app-blue --timeout=300s
      
      - name: Run smoke tests
        run: |
          curl -f http://app-blue.example.com/health
          curl -f http://app-blue.example.com/api/v1/test
      
      - name: Switch traffic (if green healthy)
        run: |
          kubectl patch service app-service -p '{"spec":{"selector":{"version":"blue"}}}'
      
      - name: Cleanup old version
        run: |
          kubectl delete deployment app-green --grace-period=30
```

```bash
# scripts/deploy.sh
#!/bin/bash

set -e

ENVIRONMENT=$1
VERSION=$2

if [ -z "$ENVIRONMENT" ] || [ -z "$VERSION" ]; then
    echo "Usage: ./deploy.sh <environment> <version>"
    exit 1
fi

echo "Deploying version $VERSION to $ENVIRONMENT"

case $ENVIRONMENT in
    dev)
        kubectl config use-context dev
        kubectl set image deployment/app-dev app=myapp:$VERSION
        kubectl rollout status deployment/app-dev
        ;;
    staging)
        kubectl config use-context staging
        kubectl set image deployment/app-staging app=myapp:$VERSION
        kubectl rollout status deployment/app-staging
        ;;
    prod)
        echo "Production deployment requires approval"
        kubectl config use-context prod
        kubectl set image deployment/app-prod app=myapp:$VERSION
        kubectl rollout status deployment/app-prod
        ;;
    *)
        echo "Unknown environment: $ENVIRONMENT"
        exit 1
        ;;
esac

echo "Deployment to $ENVIRONMENT completed successfully"
```

```bash
# scripts/health-check.sh
#!/bin/bash

URL=$1
MAX_RETRIES=30
RETRY_INTERVAL=2

echo "Checking health of $URL"

for i in $(seq 1 $MAX_RETRIES); do
    if curl -sf "$URL/health" > /dev/null 2>&1; then
        echo "Health check passed"
        exit 0
    fi
    echo "Attempt $i/$MAX_RETRIES: Health check failed, retrying..."
    sleep $RETRY_INTERVAL
done

echo "Health check failed after $MAX_RETRIES attempts"
exit 1
```

---

## Build Instructions

```bash
cd 28-ci-cd
chmod +x scripts/deploy.sh
chmod +x scripts/health-check.sh
./scripts/deploy.sh dev 1.0.0
```

---

# Mini-Project 3: Pipeline as Code (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Jenkins Pipeline, Groovy DSL, Shared Libraries

Build Jenkins pipeline using Pipeline-as-Code with shared libraries.

---

## Project Structure

```
28-ci-cd/
├── Jenkinsfile
├── Jenkinsfile.dev
├── vars/
│   ├── buildJava.groovy
│   ├── runTests.groovy
│   ├── deploy.groovy
│   └── notify.groovy
└── src/
    └── org/
        └── learning/
            └── PipelineUtils.groovy
```

---

## Implementation

```groovy
// Jenkinsfile
@Library('shared-pipeline-library@main') _

pipeline {
    agent any
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }
    
    parameters {
        string(name: 'VERSION', defaultValue: '', description: 'Version to deploy')
        choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'prod'], description: 'Target environment')
        booleanParam(name: 'RUN_INTEGRATION_TESTS', defaultValue: true, description: 'Run integration tests')
    }
    
    environment {
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_IMAGE = 'myapp'
    }
    
    stages {
        stage('Initialize') {
            steps {
                script {
                    env.BUILD_VERSION = params.VERSION ?: "${env.BUILD_NUMBER}"
                    echo "Starting build for version: ${env.BUILD_VERSION}"
                }
            }
        }
        
        stage('Build') {
            steps {
                script {
                    buildJava {
                        javaVersion = '17'
                        mavenGoals = 'clean package'
                    }
                }
            }
        }
        
        stage('Test') {
            steps {
                script {
                    runTests {
                        testType = 'unit'
                        failBuild = true
                    }
                }
            }
            
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    publishHTML(target: [
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'Coverage Report'
                    ])
                }
            }
        }
        
        stage('Security Scan') {
            steps {
                sh 'mvn dependency:tree -DoutputFile=dependency-tree.txt'
                sh 'mvn org.owasp:dependency-check-maven:check'
            }
            
            post {
                always {
                    archiveArtifacts artifacts: 'target/dependency-check-report.html', allowEmptyArchive: true
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    def dockerImage = docker.build("${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${env.BUILD_VERSION}")
                    dockerImage.push()
                    dockerImage.push('latest')
                }
            }
        }
        
        stage('Deploy') {
            when {
                expression { params.ENVIRONMENT != 'prod' || currentBuild.result == 'SUCCESS' }
            }
            steps {
                script {
                    deploy {
                        environment = params.ENVIRONMENT
                        version = env.BUILD_VERSION
                        namespace = "app-${params.ENVIRONMENT}"
                    }
                }
            }
        }
        
        stage('Integration Tests') {
            when {
                expression { params.RUN_INTEGRATION_TESTS }
            }
            steps {
                script {
                    sh """
                        mvn verify -Pintegration-tests \
                            -Dbase.url=http://app-${params.ENVIRONMENT}.example.com
                    """
                }
            }
        }
    }
    
    post {
        success {
            script {
                notify {
                    channel = 'build-notifications'
                    message = "Build ${env.BUILD_NUMBER} completed successfully"
                }
            }
        }
        failure {
            script {
                notify {
                    channel = 'build-alerts'
                    message = "Build ${env.BUILD_NUMBER} failed"
                }
            }
        }
        always {
            cleanWs()
        }
    }
}
```

```groovy
// vars/buildJava.groovy
def call(Map config = [:]) {
    def javaVersion = config.javaVersion ?: '17'
    def mavenGoals = config.mavenGoals ?: 'clean package'
    
    echo "Building with Java ${javaVersion}"
    
    sh """
        java -version
        mvn ${mavenGoals} \
            -Djava.version=${javaVersion} \
            -DskipTests=false \
            -Dmaven.test.failure.ignore=false
    """
    
    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
}
```

```groovy
// vars/runTests.groovy
def call(Map config = [:]) {
    def testType = config.testType ?: 'all'
    def failBuild = config.failBuild ?: true
    
    echo "Running ${testType} tests"
    
    def testGoals = 'test'
    if (testType == 'unit') {
        testGoals = 'test'
    } else if (testType == 'integration') {
        testGoals = 'verify -Pintegration-tests'
    } else if (testType == 'performance') {
        testGoals = 'verify -Pperformance-tests'
    }
    
    def testCommand = "mvn ${testGoals}"
    
    if (!failBuild) {
        testCommand += " -Dmaven.test.failure.ignore=true"
    }
    
    sh testCommand
    
    junit 'target/surefire-reports/*.xml'
    junit 'target/failsafe-reports/*.xml'
}
```

```groovy
// vars/deploy.groovy
def call(Map config = [:]) {
    def environment = config.environment
    def version = config.version
    def namespace = config.namespace ?: "default"
    
    echo "Deploying to ${environment} in namespace ${namespace}"
    
    withCredentials([file(credentialsId: "kubeconfig-${environment}", variable: 'KUBECONFIG')]) {
        sh """
            kubectl config get-contexts
            kubectl config use-context ${environment}
            
            kubectl set image deployment/app app=myapp:${version} -n ${namespace}
            kubectl rollout status deployment/app -n ${namespace} --timeout=300s
            
            kubectl get pods -n ${namespace}
            kubectl get services -n ${namespace}
        """
    }
    
    echo "Deployment to ${environment} completed"
}
```

```groovy
// vars/notify.groovy
def call(Map config = [:]) {
    def channel = config.channel ?: 'default'
    def message = config.message ?: 'Build notification'
    
    echo "Sending notification to ${channel}: ${message}"
    
    switch(channel) {
        case 'slack':
            slackSend(color: 'good', message: message)
            break
        case 'email':
            emailext(
                subject: "Jenkins Build: ${env.JOB_NAME}",
                body: message,
                to: 'team@example.com'
            )
            break
        case 'teams':
            echo "Teams notification: ${message}"
            break
        default:
            echo "Unknown channel: ${channel}"
    }
}
```

```groovy
// src/org/learning/PipelineUtils.groovy
package org.learning

class PipelineUtils implements Serializable {
    
    def script
    
    PipelineUtils(script) {
        this.script = script
    }
    
    def getVersionFromPom() {
        def pom = script.readMavenPom()
        return pom.version
    }
    
    def setBuildMetadata() {
        script.env.BUILD_TIMESTAMP = new Date().format("yyyy-MM-dd HH:mm:ss")
        script.env.BUILD_USER = script.sh(script: 'git log -1 --pretty=%an', returnStdout: true).trim()
        script.env.GIT_COMMIT_SHORT = script.sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
    }
    
    def verifyVersion(String version) {
        if (!version.matches(/^\d+\.\d+\.\d+$/)) {
            script.error "Invalid version format: ${version}. Expected semantic version (e.g., 1.0.0)"
            return false
        }
        return true
    }
    
    def createDockerTag(String version) {
        return "v${version}"
    }
    
    def rollbackDeployment(String environment) {
        script.sh "kubectl rollout undo deployment/app -n ${environment}"
    }
}
```

---

## Build Instructions

```bash
cd 28-ci-cd
# Run in Jenkins
jenkins-cli build ci-demo -p VERSION=1.0.0 -p ENVIRONMENT=dev
```

---

# Mini-Project 4: Deployment Strategies (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Blue-Green, Canary, Rolling Update, A/B Testing

Implement various deployment strategies for zero-downtime releases.

---

## Project Structure

```
28-ci-cd/
├── deployment/
│   ├── blue-green/
│   │   ├── blue-deployment.yaml
│   │   ├── green-deployment.yaml
│   │   └── service.yaml
│   ├── canary/
│   │   ├── canary-deployment.yaml
│   │   └── stable-deployment.yaml
│   └── rolling/
│       └── deployment.yaml
└── scripts/
    ├── blue-green-switch.sh
    └── canary-analysis.sh
```

---

## Implementation

```yaml
# deployment/blue-green/blue-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-blue
  namespace: production
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
  selector:
    matchLabels:
      app: myapp
      version: blue
  template:
    metadata:
      labels:
        app: myapp
        version: blue
    spec:
      containers:
      - name: app
        image: myapp:1.0.0
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

```yaml
# deployment/blue-green/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: app-service
  namespace: production
spec:
  selector:
    app: myapp
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

```yaml
# deployment/canary/canary-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-canary
  namespace: production
spec:
  replicas: 1
  selector:
    matchLabels:
      app: myapp
      version: canary
  template:
    metadata:
      labels:
        app: myapp
        version: canary
    spec:
      containers:
      - name: app
        image: myapp:2.0.0
        ports:
        - containerPort: 8080
```

```yaml
# deployment/canary/stable-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-stable
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
      version: stable
  template:
    metadata:
      labels:
        app: myapp
        version: stable
    spec:
      containers:
      - name: app
        image: myapp:1.0.0
        ports:
        - containerPort: 8080
```

```yaml
# deployment/rolling/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-rolling
  namespace: production
spec:
  replicas: 4
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
      - name: app
        image: myapp:2.0.0
        ports:
        - containerPort: 8080
        env:
        - name: VERSION
          value: "2.0.0"
```

```bash
#!/bin/bash
# blue-green-switch.sh

set -e

echo "Starting Blue-Green Deployment Switch"

# Get current active color
CURRENT_COLOR=$(kubectl get service app-service -n production -o jsonpath='{.spec.selector.version}')

if [ "$CURRENT_COLOR" = "blue" ]; then
    NEW_COLOR="green"
    OLD_COLOR="blue"
else
    NEW_COLOR="blue"
    OLD_COLOR="green"
fi

echo "Current: $CURRENT_COLOR -> New: $NEW_COLOR"

# Deploy new version
kubectl set image deployment/app-${NEW_COLOR} app=myapp:$1 -n production
kubectl rollout status deployment/app-${NEW_COLOR} -n production --timeout=300s

# Run smoke tests
echo "Running smoke tests..."
curl -sf http://app-${NEW_COLOR}.example.com/health || exit 1

# Switch traffic
echo "Switching traffic to $NEW_COLOR..."
kubectl patch service app-service -n production -p '{"spec":{"selector":{"version":"'${NEW_COLOR}'"}}}'

echo "Traffic switched to $NEW_COLOR"

# Wait for traffic to stabilize
sleep 30

# Verify traffic
echo "Verifying traffic..."
kubectl get pods -n production -l version=$NEW_COLOR

echo "Blue-Green deployment completed successfully"
```

```bash
#!/bin/bash
# canary-analysis.sh

set -e

echo "Starting Canary Analysis"

VERSION=$1
METRICS_DURATION=${2:-300}

# Deploy canary
echo "Deploying canary version: $VERSION"
kubectl apply -f deployment/canary/canary-deployment.yaml
kubectl set image deployment/app-canary app=myapp:$VERSION -n production
kubectl rollout status deployment/app-canary -n production --timeout=300s

# Wait for metrics collection
echo "Collecting metrics for $METRICS_DURATION seconds..."

ERROR_RATE=0
RESPONSE_TIME=0

for i in $(seq 1 $((METRICS_DURATION / 10))); do
    ERROR_RATE=$(curl -s http://app-canary.example.com/metrics | grep "error_rate" || echo "0")
    RESPONSE_TIME=$(curl -s http://app-canary.example.com/metrics | grep "response_time" || echo "0")
    echo "Error Rate: $ERROR_RATE, Response Time: $RESPONSE_TIME"
    sleep 10
done

# Decision logic
if (( $(echo "$ERROR_RATE < 0.01" | bc -l) )) && (( $(echo "$RESPONSE_TIME < 500" | bc -l) )); then
    echo "Canary passed - promoting to 100%"
    # Update stable deployment
    kubectl set image deployment/app-stable app=myapp:$VERSION -n production
    kubectl scale deployment app-stable --replicas=3 -n production
    # Delete canary
    kubectl delete deployment app-canary -n production
    echo "Promoted to stable"
else
    echo "Canary failed - rolling back"
    kubectl delete deployment app-canary -n production
    echo "Rolled back"
fi
```

---

## Build Instructions

```bash
cd 28-ci-cd
chmod +x scripts/blue-green-switch.sh
chmod +x scripts/canary-analysis.sh
./scripts/blue-green-switch.sh 2.0.0
./scripts/canary-analysis.sh 2.0.0 300
```

---

# Real-World Project: CI/CD Pipeline (10+ hours)

## Project Overview

**Duration**: 10+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Multi-stage Pipeline, Artifact Management, Automated Testing, Infrastructure as Code

Build a complete enterprise CI/CD pipeline with all best practices.

---

## Project Structure

```
28-ci-cd/
├── .github/
│   └── workflows/
│       ├── ci.yml
│       ├── cd.yml
│       └── release.yml
├── Jenkinsfile
├── deployment/
│   ├── base/
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   └── configmap.yaml
│   ├── dev/
│   │   └── kustomization.yaml
│   ├── staging/
│   │   └── kustomization.yaml
│   └── prod/
│       └── kustomization.yaml
├── terraform/
│   ├── main.tf
│   ├── variables.tf
│   └── outputs.tf
├── ansible/
│   ├── playbook.yml
│   └── roles/
│       └── app/
│           └── tasks/
├── scripts/
│   ├── build.sh
│   ├── test.sh
│   ├── deploy.sh
│   ├── rollback.sh
│   └── health-check.sh
└── pom.xml
```

---

## Complete Implementation

```groovy
// Jenkinsfile - Complete Enterprise Pipeline
@Library('shared-lib@v1.0') _

pipeline {
    agent { label 'docker' }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 45, unit: 'MINUTES')
        timestamps()
        ansiColor('xterm')
    }
    
    parameters {
        choice(name: 'ACTION', choices: ['build', 'deploy', 'test', 'clean'], description: 'Pipeline action')
        string(name: 'VERSION', description: 'Version to deploy (optional)')
        choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'prod'], description: 'Target environment')
        booleanParam(name: 'RUN_SECURITY_SCAN', defaultValue: true, description: 'Run security scans')
        booleanParam(name: 'RUN_PERFORMANCE_TESTS', defaultValue: false, description: 'Run performance tests')
    }
    
    environment {
        APP_NAME = 'enterprise-app'
        DOCKER_REGISTRY = 'registry.example.com'
        SONAR_URL = 'http://sonarqube:9000'
        NEXUS_URL = 'http://nexus:8081'
    }
    
    stages {
        stage('Initialize') {
            steps {
                script {
                    env.BUILD_VERSION = getVersion()
                    env.BUILD_TIMESTAMP = getTimestamp()
                    env.GIT_BRANCH = getBranchName()
                    env.GIT_COMMIT = getCommitHash()
                    
                    echo "=== Build Information ==="
                    echo "Version: ${env.BUILD_VERSION}"
                    echo "Branch: ${env.GIT_BRANCH}"
                    echo "Commit: ${env.GIT_COMMIT}"
                    echo "Timestamp: ${env.BUILD_TIMESTAMP}"
                }
            }
        }
        
        stage('Code Checkout') {
            steps {
                checkout scm
                sh 'git rev-parse HEAD > commit.txt'
            }
        }
        
        stage('Build Application') {
            parallel {
                stage('Build JAR') {
                    steps {
                        sh 'mvn clean package -DskipTests'
                        archiveArtifacts 'target/*.jar'
                    }
                }
                
                stage('Build Docker Image') {
                    steps {
                        script {
                            def imageTag = "${env.DOCKER_REGISTRY}/${env.APP_NAME}:${env.BUILD_VERSION}"
                            def dockerImage = docker.build(imageTag)
                            dockerImage.push()
                            dockerImage.push('latest')
                            
                            env.DOCKER_IMAGE = imageTag
                        }
                    }
                }
            }
        }
        
        stage('Static Code Analysis') {
            steps {
                sh 'mvn sonar:sonar -Dsonar.host.url=${SONAR_URL}'
            }
            
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/sonar',
                        reportFiles: 'index.html',
                        reportName: 'SonarQube Report'
                    ])
                }
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test -Dtest=*Test'
            }
            
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    publishHTML([
                        allowMissing: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'Code Coverage Report'
                    ])
                }
            }
        }
        
        stage('Security Scan') {
            when {
                expression { params.RUN_SECURITY_SCAN }
            }
            steps {
                parallel(
                    'OWASP': { sh 'mvn org.owasp:dependency-check-maven:check' },
                    'SpotBugs': { sh 'mvn spotbugs:check' },
                    'PMD': { sh 'mvn pmd:check' }
                )
            }
        }
        
        stage('Integration Tests') {
            steps {
                sh 'mvn verify -Pintegration-tests'
            }
            
            post {
                always {
                    junit 'target/failsafe-reports/*.xml'
                }
            }
        }
        
        stage('Performance Tests') {
            when {
                expression { params.RUN_PERFORMANCE_TESTS }
            }
            steps {
                sh 'mvn verify -Pperformance-tests'
            }
            
            post {
                always {
                    archiveArtifacts 'target/jmeter-reports/**/*.html'
                }
            }
        }
        
        stage('Artifact Publishing') {
            steps {
                sh """
                    curl -u admin:admin123 -X PUT \
                        "${NEXUS_URL}/repository/maven-releases/${env.APP_NAME}-${env.BUILD_VERSION}.jar" \
                        -T target/*.jar
                """
            }
        }
        
        stage('Deploy to Environment') {
            when {
                expression { params.ACTION == 'deploy' }
            }
            steps {
                script {
                    switch(params.ENVIRONMENT) {
                        case 'dev':
                            deployToDev()
                            break
                        case 'staging':
                            deployToStaging()
                            break
                        case 'prod':
                            input message: 'Approve production deployment?', ok: 'Approve'
                            deployToProd()
                            break
                    }
                }
            }
        }
        
        stage('Smoke Tests') {
            when {
                expression { params.ACTION == 'deploy' }
            }
            steps {
                script {
                    smokeTests(params.ENVIRONMENT)
                }
            }
        }
        
        stage('Release') {
            when {
                expression { params.ACTION == 'build' && env.GIT_BRANCH == 'main' }
            }
            steps {
                sh 'git tag v${env.BUILD_VERSION}'
                sh 'git push origin v${env.BUILD_VERSION}'
            }
        }
    }
    
    post {
        success {
            slackSend(
                color: 'good',
                message: "Build ${env.BUILD_NUMBER} completed successfully - ${env.BUILD_VERSION}"
            )
        }
        failure {
            slackSend(
                color: 'danger',
                message: "Build ${env.BUILD_NUMBER} failed - ${env.BUILD_VERSION}"
            )
        }
        always {
            cleanWs()
        }
    }
}

def getVersion() {
    if (params.VERSION) {
        return params.VERSION
    }
    def pom = readMavenPom()
    return "${pom.version}-build.${env.BUILD_NUMBER}"
}

def getTimestamp() {
    return new Date().format('yyyy-MM-dd HH:mm:ss')
}

def getBranchName() {
    return sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
}

def getCommitHash() {
    return sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
}

def deployToDev() {
    sh """
        kubectl set image deployment/${APP_NAME} \
            app=${DOCKER_REGISTRY}/${APP_NAME}:${env.BUILD_VERSION} \
            -n dev
        kubectl rollout status deployment/${APP_NAME} -n dev --timeout=180s
    """
}

def deployToStaging() {
    sh """
        kubectl set image deployment/${APP_NAME} \
            app=${DOCKER_REGISTRY}/${APP_NAME}:${env.BUILD_VERSION} \
            -n staging
        kubectl rollout status deployment/${APP_NAME} -n staging --timeout=300s
        kubectl apply -f deployment/istio/virtual-service-staging.yaml
    """
}

def deployToProd() {
    sh """
        kubectl apply -f deployment/prod/
        kubectl set image deployment/${APP_NAME} \
            app=${DOCKER_REGISTRY}/${APP_NAME}:${env.BUILD_VERSION} \
            -n production
        kubectl rollout status deployment/${APP_NAME} -n production --timeout=300s
        kubectl apply -f deployment/istio/virtual-service-prod.yaml
    """
}

def smokeTests(String environment) {
    def url = "http://${APP_NAME}.${environment}.example.com"
    sh """
        curl -sf "${url}/health" || exit 1
        curl -sf "${url}/api/v1/status" || exit 1
        echo "Smoke tests passed for ${environment}"
    """
}
```

---

## Build Instructions

```bash
cd 28-ci-cd

# Local build
mvn clean package

# Build Docker
docker build -t myapp:1.0.0 .

# Deploy to environment
./scripts/deploy.sh dev 1.0.0

# View pipeline status
jenkins-cli get-job enterprise-app/lastBuild/consoleText
```

---

## Pipeline Best Practices

### 1. Version Management
```bash
# Semantic versioning
VERSION=$(node -p "require('./package.json').version")
TAG="v${VERSION}-${BUILD_NUMBER}"
```

### 2. Environment Configuration
```bash
# Use environment-specific configs
export ENV=staging
mvn spring-boot:run -Dspring.profiles.active=${ENV}
```

### 3. Security Best Practices
```yaml
# GitHub Actions secrets
secrets:
  DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
  DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
```