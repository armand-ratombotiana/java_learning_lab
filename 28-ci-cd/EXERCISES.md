# CI/CD Exercises

## Exercise 1: GitHub Actions Basic Pipeline

### Task
Create a basic GitHub Actions workflow that:
1. Runs on push to main branch
2. Sets up Java 17
3. Builds the project
4. Runs unit tests

### Solution
```yaml
name: Java CI

on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'
      
      - name: Build project
        run: mvn clean package -DskipTests
      
      - name: Run tests
        run: mvn test
```

---

## Exercise 2: Jenkins Declarative Pipeline

### Task
Create a Jenkins pipeline with:
1. Maven build stage
2. Test stage with JUnit reports
3. Docker build and push stage

### Solution
```groovy
pipeline {
    agent any
    
    environment {
        REGISTRY = 'docker.io'
        IMAGE_NAME = 'myapp'
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
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${env.BUILD_NUMBER} ."
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
}
```

---

## Exercise 3: Matrix Build Strategy

### Task
Create a GitHub Actions matrix build that tests on:
- Java 11, 17, 21
- Ubuntu and Windows

### Solution
```yaml
name: Multi-Version Build

on: [push, pull_request]

jobs:
  test:
    strategy:
      matrix:
        java: [11, 17, 21]
        os: [ubuntu-latest, windows-latest]
    
    runs-on: ${{ matrix.os }}
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup JDK ${{ matrix.java }}
        uses: actions/setup-java@v4
        with:
          java-version: ${{ matrix.java }}
          distribution: 'temurin'
      
      - name: Run tests
        run: mvn verify
```

---

## Exercise 4: Environment-Specific Deployment

### Task
Create a pipeline that:
1. Deploys to staging on develop branch push
2. Deploys to production on main branch push
3. Requires manual approval for production

### Solution
```yaml
name: Deploy Pipeline

on:
  push:
    branches: [develop, main]

jobs:
  deploy-staging:
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/develop'
    
    steps:
      - uses: actions/checkout@v4
      - name: Deploy to Staging
        run: ./deploy.sh staging

  deploy-production:
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    environment: production
    needs: deploy-staging
    
    steps:
      - uses: actions/checkout@v4
      - name: Deploy to Production
        run: ./deploy.sh production
```

---

## Exercise 5: Docker Multi-Stage Build

### Task
Create a multi-stage Dockerfile that:
1. Uses Maven to build in builder stage
2. Uses JRE Alpine for minimal runtime image
3. Only copies the final JAR

### Solution
```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn dependency:go-offline -B
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Exercise 6: SonarQube Integration

### Task
Add SonarQube analysis to your pipeline:
1. Run scanner after tests
2. Publish results to SonarQube server
3. Quality gate check

### Solution
```yaml
- name: SonarQube Scan
  uses: sonarsource/sonarqube-scan-action@master
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
    SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}

- name: Quality Gate Wait
  run: |
    sleep 5
    STATUS=$(curl -s -u ${{ secrets.SONAR_TOKEN }}: \
      "${{ secrets.SONAR_HOST_URL }}/api/qualitygates/project_status?projectKey=myapp" \
      | jq -r '.projectStatus.status')
    echo "Quality Gate Status: $STATUS"
    [ "$STATUS" = "OK" ] || exit 1
```

---

## Exercise 7: Caching Dependencies

### Task
Add Maven caching to your GitHub Actions workflow.

### Solution
```yaml
- name: Cache Maven packages
  uses: actions/cache@v4
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
    restore-keys: |
      ${{ runner.os }}-maven-
      ${{ runner.os }}-

- name: Build
  run: mvn clean package
```

---

## Exercise 8: Slack Notifications

### Task
Add Slack notifications to your Jenkins pipeline that:
1. Notifies on build failure
2. Includes build URL and duration

### Solution
```groovy
post {
    failure {
        slackSend(
            color: 'danger',
            channel: '#builds',
            message: """
                Build FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}
                Duration: ${currentBuild.durationString}
                URL: ${env.BUILD_URL}
            """
        )
    }
    success {
        slackSend(
            color: 'good',
            channel: '#builds',
            message: """
                Build SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}
            """
        )
    }
}
```

---

## Exercise 9: Blue-Green Deployment

### Task
Write a deployment script that implements blue-green deployment.

### Solution
```bash
#!/bin/bash

VERSION=$1
ACTIVE_COLOR=$(kubectl get service myapp -o jsonpath='{.spec.selector.version}')

if [ "$ACTIVE_COLOR" = "blue" ]; then
  NEXT_COLOR="green"
else
  NEXT_COLOR="blue"
fi

echo "Deploying version $VERSION as $NEXT_COLOR"

kubectl set image deployment/myapp-$NEXT_COLOR \
  myapp=myapp:$VERSION

kubectl rollout status deployment/myapp-$NEXT_COLOR

# Test new version
curl -sf http://myapp-$NEXT_COLOR/health || exit 1

# Switch traffic
kubectl patch service myapp -p '{"spec":{"selector":{"version":"'"$NEXT_COLOR"'"}}}'

echo "Traffic switched to $NEXT_COLOR"
```

---

## Exercise 10: Artifact Publishing

### Task
Create a pipeline that:
1. Builds the artifact
2. Publishes to GitHub Releases on tag
3. Uploads to S3 storage

### Solution
```yaml
name: Publish Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '17'
      
      - name: Build
        run: mvn clean package -DskipTests
      
      - name: Create Release
        uses: actions/create-release@v1
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          tag_name: ${{ github.ref }}
          release_name: Release ${{ github.ref }}
      
      - name: Upload to S3
        run: |
          aws s3 cp target/app.jar s3://mybucket/releases/
```

---

## Exercise Bonus: Multi-Environment Config

### Task
Create a configuration that supports dev, staging, and production environments with different configurations.

### Solution
```yaml
# environments.yml
environments:
  dev:
    database_url: jdbc:postgresql://dev-db:5432/devdb
    max_connections: 10
    log_level: DEBUG
  
  staging:
    database_url: jdbc:postgresql://staging-db:5432/stagingdb
    max_connections: 50
    log_level: INFO
  
  prod:
    database_url: jdbc:postgresql://prod-db:5432/proddb
    max_connections: 100
    log_level: WARN

# Deploy script
#!/bin/bash
ENV=$1
CONFIG=$(cat environments.yml | yq ".environments.$ENV")

echo "Deploying to $ENV"
echo "$CONFIG" | base64 > config.env

kubectl create configmap app-config --from-file=config.env --dry-run=client -o yaml | kubectl apply -f -