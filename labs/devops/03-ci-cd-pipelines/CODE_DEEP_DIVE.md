# CI/CD Code Deep Dive

## GitHub Actions Complete Pipeline
```yaml
name: Full CI/CD

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
      - run: npm ci
      - run: npm run lint
      - run: npm test -- --coverage
      - run: npm run build
      - uses: actions/upload-artifact@v4
        with:
          name: build-output
          path: dist/

  docker-build-push:
    needs: build-and-test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      - run: |
          docker build -t $REGISTRY/$IMAGE_NAME:${{ github.sha }} .
          docker tag $REGISTRY/$IMAGE_NAME:${{ github.sha }} \
                    $REGISTRY/$IMAGE_NAME:latest
          docker push --all-tags $REGISTRY/$IMAGE_NAME

  deploy-staging:
    needs: docker-build-push
    runs-on: ubuntu-latest
    environment: staging
    steps:
      - run: |
          echo "Deploying ${{ github.sha }} to staging..."
          # kubectl set image deployment/app app=$REGISTRY/$IMAGE_NAME:${{ github.sha }}
```

## Jenkins Declarative Pipeline
```groovy
pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'ghcr.io'
        IMAGE_NAME = "${DOCKER_REGISTRY}/myorg/myapp"
    }
    stages {
        stage('Checkout') { steps { checkout scm } }
        stage('Build') { steps { sh 'mvn clean package' } }
        stage('Test') {
            steps { sh 'mvn test' }
            post { failure { slackSend(color: 'danger', message: "Tests failed!") } }
        }
        stage('Docker Build') {
            steps { sh 'docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .' }
        }
        stage('Deploy Staging') {
            when { branch 'main' }
            steps { sh 'kubectl set image deployment/app app=${IMAGE_NAME}:${BUILD_NUMBER}' }
        }
    }
    post {
        always { cleanWs() }
    }
}
```
