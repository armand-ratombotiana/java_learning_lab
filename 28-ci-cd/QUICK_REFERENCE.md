# Quick Reference: CI/CD Pipelines

<div align="center">

![Module](https://img.shields.io/badge/Module-28-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-CI/CD-green?style=for-the-badge)

**Quick lookup guide for CI/CD pipelines**

</div>

---

## 📋 Core Concepts

| Concept | Description |
|---------|-------------|
| **CI** | Continuous Integration - automated builds on code changes |
| **CD** | Continuous Delivery/Deployment - automated release process |
| **Pipeline** | Sequence of stages for building/testing/deploying |
| **Artifact** | Built package ready for deployment |

---

## 🔑 Key Commands

### Maven Build
```bash
# Clean build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Run specific profile
mvn clean verify -Pintegration

# Build with Docker
mvn spring-boot:build-image
```

### Docker
```bash
# Build image
docker build -t myapp:latest .

# Run container
docker run -p 8080:8080 myapp:latest

# Push to registry
docker push myregistry/myapp:latest

# Multi-stage build
docker build -t myapp:latest -f Dockerfile.multi .
```

---

## 📊 GitHub Actions

```yaml
name: Build and Deploy
on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '17'
      - name: Build
        run: mvn clean package
      - name: Test
        run: mvn verify
      - name: Build Docker
        run: docker build -t ${{ secrets.REGISTRY }}/app:${{ github.sha }} .
```

### Matrix Strategy
```yaml
jobs:
  test:
    strategy:
      matrix:
        java: [11, 17, 21]
        os: [ubuntu-latest, windows-latest]
```

---

## 📊 Jenkins Pipeline

```groovy
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn verify'
            }
        }
        stage('Deploy') {
            when { branch 'main' }
            steps {
                sh 'docker build -t myapp .'
                sh 'docker push myapp'
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
```

---

## 🚀 Deployment Strategies

### Blue-Green
```yaml
# Traffic switch after validation
- deploy to green environment
- run smoke tests
- switch traffic: update load balancer
- keep blue for rollback
```

### Canary
```yaml
# Gradual rollout
- deploy to 10% of servers
- monitor metrics
- increase traffic gradually
- full rollout or rollback
```

### Rolling
```yaml
# Sequential replacement
- take one instance offline
- deploy new version
- bring back online
- repeat for all instances
```

---

## ✅ Best Practices

- Use IaC (Terraform/CloudFormation) for infrastructure
- Implement quality gates (tests, security scans)
- Use secrets management (GitHub secrets, Jenkins credentials)
- Monitor pipeline metrics

### ❌ DON'T
- Don't hardcode credentials in pipeline scripts
- Don't skip test stages for speed

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>