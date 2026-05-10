# CI/CD Deep Dive

## GitHub Actions Deep Dive

### Basic Workflow Structure

```yaml
name: Java CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

env:
  JAVA_VERSION: '17'
  MAVEN_OPTS: -Xmx2048m

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
          cache: 'maven'
      
      - name: Build with Maven
        run: mvn clean package -DskipTests
      
      - name: Run tests
        run: mvn verify
      
      - name: Upload artifacts
        uses: actions/upload-artifact@v4
        with:
          name: build-artifacts
          path: target/*.jar
```

### Matrix Builds

```yaml
jobs:
  test:
    strategy:
      fail-fast: false
      matrix:
        java: ['11', '17', '21']
        os: ['ubuntu-latest', 'windows-latest']
        exclude:
          - java: '21'
            os: 'windows-latest'
    
    runs-on: ${{ matrix.os }}
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup JDK ${{ matrix.java }}
        uses: actions/setup-java@v4
        with:
          java-version: ${{ matrix.java }}
      
      - name: Run tests
        run: mvn verify
```

### Caching Dependencies

```yaml
steps:
  - uses: actions/checkout@v4
  
  - name: Cache Maven packages
    uses: actions/cache@v4
    with:
      path: ~/.m2/repository
      key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
      restore-keys: |
        ${{ runner.os }}-maven-
  
  - name: Build project
    run: mvn clean package
```

### Conditional Steps

```yaml
steps:
  - name: Checkout
    uses: actions/checkout@v4
  
  - name: Build
    run: mvn clean package
  
  - name: Deploy to Staging
    if: github.ref == 'refs/heads/develop'
    run: ./deploy.sh staging
  
  - name: Deploy to Production
    if: github.ref == 'refs/heads/main' && github.event_name == 'push'
    run: ./deploy.sh production
```

### Environment Secrets

```yaml
jobs:
  deploy:
    runs-on: ubuntu-latest
    environment: production
    
    steps:
      - name: Deploy
        run: |
          echo "Deploying to ${{ secrets.API_URL }}"
          curl -X POST ${{ secrets.API_URL }} \
            -H "Authorization: Bearer ${{ secrets.API_TOKEN }}"
```

### Reusable Workflows

```yaml
# .github/workflows/build.yml
name: Reusable Build Workflow

on:
  workflow_call:
    inputs:
      java-version:
        type: string
        default: '17'
    secrets:
      token:
        required: true

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ inputs.java-version }}
      
      - name: Build
        run: mvn clean package
```

---

## Jenkins Pipeline Deep Dive

### Declarative Pipeline

```groovy
pipeline {
    agent any
    
    environment {
        REGISTRY = 'docker.io'
        IMAGE_NAME = 'myapp'
        DOCKER_CREDS = credentials('docker-hub')
    }
    
    options {
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            parallel {
                stage('Compile') {
                    steps {
                        sh 'mvn clean compile'
                    }
                }
                
                stage('Unit Tests') {
                    steps {
                        sh 'mvn test'
                    }
                }
            }
        }
        
        stage('Integration Tests') {
            steps {
                sh 'mvn verify -Pintegration'
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
        
        stage('Build Docker') {
            steps {
                sh """
                    docker build -t ${IMAGE_NAME}:${env.BUILD_NUMBER} .
                    docker tag ${IMAGE_NAME}:${env.BUILD_NUMBER} ${IMAGE_NAME}:latest
                """
            }
        }
        
        stage('Push to Registry') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker push ${IMAGE_NAME}:${env.BUILD_NUMBER}
                        docker push ${IMAGE_NAME}:latest
                    """
                }
            }
        }
        
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                sh './deploy.sh production'
            }
        }
    }
    
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
    }
}
```

### Scripted Pipeline

```groovy
node {
    try {
        stage('Checkout') {
            checkout scm
        }
        
        stage('Build') {
            sh 'mvn clean package -DskipTests'
        }
        
        stage('Test') {
            def testResults = 'target/surefire-reports/*.xml'
            junit testResults
            
            if (fileExists('target/site/jacoco/index.html')) {
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'Code Coverage Report'
                ])
            }
        }
        
        stage('SonarQube') {
            withSonarQubeEnv('sonar-server') {
                sh 'mvn sonar:sonar -Dsonar.branch=${env.BRANCH_NAME}'
            }
        }
        
        if (env.BRANCH_NAME == 'main') {
            stage('Deploy') {
                sh './deploy.sh production'
            }
        }
    } catch (Exception e) {
        currentBuild.result = 'FAILURE'
        throw e
    } finally {
        cleanWs()
    }
}
```

### Shared Libraries

```groovy
// vars/buildJava.groovy
def call(String version = '17') {
    pipeline {
        agent any
        stages {
            stage('Setup') {
                steps {
                    sh "java -version"
                }
            }
        }
    }
}

// Jenkinsfile
@Library('shared-lib') _

buildJava('17')
```

### Agent Selection

```groovy
pipeline {
    agent {
        label 'docker && linux'
    }
    
    stages {
        stage('Build') {
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-17'
                    reuseNode true
                }
            }
            steps {
                sh 'mvn package'
            }
        }
    }
}
```

---

## Deployment Strategies

### Blue-Green Deployment

```yaml
# docker-compose.blue.yml
services:
  app-blue:
    image: myapp:blue
    ports:
      - "8080:8080"
    environment:
      - ACTIVE_PROFILE=blue

# docker-compose.green.yml
services:
  app-green:
    image: myapp:green
    ports:
      - "8081:8080"
    environment:
      - ACTIVE_PROFILE=green
```

```bash
# Deploy Blue
docker-compose -f docker-compose.blue.yml up -d

# Test Blue
curl http://localhost:8080/health

# Switch traffic to Blue
docker-compose -f docker-compose.green.yml stop
docker-compose -f docker-compose.blue.yml up
```

### Canary Deployment

```yaml
# Kubernetes canary deployment
apiVersion: v1
kind: Service
metadata:
  name: myapp
spec:
  selector:
    app: myapp
  ports:
  - port: 80
    targetPort: 8080
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-v1
spec:
  replicas: 10
  selector:
    matchLabels:
      version: v1
  template:
    spec:
      containers:
      - name: myapp
        image: myapp:v1
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-v2
spec:
  replicas: 2
  selector:
    matchLabels:
      version: v2
  template:
    spec:
      containers:
      - name: myapp
        image: myapp:v2
```

### Rolling Update

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  replicas: 5
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    spec:
      containers:
      - name: myapp
        image: myapp:v2
```

---

## Pipeline Quality Gates

### SonarQube Integration

```yaml
- name: SonarQube Scan
  uses: sonarsource/sonarqube-scan-action@master
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
    SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}

- name: Quality Gate Check
  run: |
    while true; do
      STATUS=$(curl -s -u ${{ secrets.SONAR_TOKEN }}: \
        "${{ secrets.SONAR_HOST_URL }}/api/qualitygates/project_status?projectKey=myapp" \
        | jq -r '.projectStatus.status')
      
      if [ "$STATUS" == "OK" ]; then
        echo "Quality Gate passed"
        break
      elif [ "$STATUS" == "ERROR" ]; then
        echo "Quality Gate failed"
        exit 1
      fi
      sleep 5
    done
```

### Security Scanning

```yaml
# Dependency Check
- name: OWASP Dependency Check
  run: mvn org.owasp:dependency-check-maven:check

# Container Scanning
- name: Trivy Scan
  uses: aquasecurity/trivy-action@master
  with:
    scan-type: 'fs'
    severity: 'CRITICAL,HIGH'
    exit-code: '1'
```

---

## Monitoring and Troubleshooting

### Pipeline Metrics

```groovy
pipeline {
    agent any
    
    options {
        timestamps()
    }
    
    stages {
        stage('Build') {
            steps {
                echo "Starting build at ${new Date().format('yyyy-MM-dd HH:mm:ss')}"
                sh 'mvn clean package'
                echo "Build finished at ${new Date().format('yyyy-MM-dd HH:mm:ss')}"
            }
        }
    }
    
    post {
        always {
            echo "Total duration: ${currentBuild.durationString}"
        }
    }
}
```

### Slack Notifications

```groovy
post {
    always {
        slackSend(
            channel: '#builds',
            color: currentBuild.result == 'SUCCESS' ? 'good' : 'danger',
            message: """
                Build ${env.JOB_NAME} #${env.BUILD_NUMBER}
                Status: ${currentBuild.result}
                Duration: ${currentBuild.durationString}
                URL: ${env.BUILD_URL}
            """
        )
    }
}
```

---

## Best Practices

1. **Use Configurable Parameters**: Avoid hardcoded values
2. **Parallelize When Possible**: Run independent stages in parallel
3. **Implement Proper Caching**: Cache dependencies and build artifacts
4. **Use Quality Gates**: Enforce code quality before deployment
5. **Secure Secrets**: Never commit secrets in code
6. **Implement Proper Logging**: Log all important steps
7. **Use Agents Wisely**: Choose appropriate agents for different jobs
8. **Implement Rollback**: Have rollback strategies ready