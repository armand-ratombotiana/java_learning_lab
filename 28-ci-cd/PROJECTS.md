# CI/CD Module - PROJECTS.md

---

# Mini-Project: CI/CD Pipeline Setup

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Jenkins Pipeline, GitHub Actions, Maven Deployment, Docker Build

This mini-project demonstrates CI/CD pipeline configuration.

---

## Project Structure

```
28-ci-cd/
├── pom.xml
├── src/main/java/com/learning/
│   └── Main.java
├── Jenkinsfile
├── .github/
│   └── workflows/
│       └── maven.yml
└── docker/
    └── Dockerfile
```

---

## POM.xml

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>ci-cd-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
</project>
```

---

## Implementation

```groovy
// Jenkinsfile
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Docker Build') {
            steps {
                sh 'docker build -t myapp:latest .'
            }
        }
    }
}
```

```yaml
# .github/workflows/maven.yml
name: Java CI

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build
        run: mvn clean package
      - name: Test
        run: mvn test
```

```dockerfile
# docker/Dockerfile
FROM openjdk:17-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```java
// Main.java
package com.learning;

public class Main {
    public static void main(String[] args) {
        System.out.println("CI/CD Demo Application");
    }
}
```

---

## Build Instructions

```bash
cd 28-ci-cd
mvn clean package
docker build -t myapp:latest .
```

---

# Real-World Project: Multi-Stage Pipeline

```groovy
// Advanced Jenkinsfile with stages
stages {
    stage('Deploy to Staging') {
        when { branch 'develop' }
        steps { sh './deploy.sh staging' }
    }
    stage('Deploy to Production') {
        when { branch 'main' }
        steps { sh './deploy.sh production' }
    }
}
```

---

## Build Instructions

```bash
cd 28-ci-cd
mvn clean compile
mvn package
docker build -f docker/Dockerfile -t myapp:latest .
```