# How CI/CD Works

## GitHub Actions Workflow
```yaml
name: CI/CD Pipeline
on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: npm ci
      - run: npm test
      - run: npm run build

  deploy:
    needs: build
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: npm ci
      - run: npm run deploy
```

## Jenkins Declarative Pipeline
```groovy
pipeline {
    agent any
    stages {
        stage('Build') {
            steps { sh 'mvn clean package' }
        }
        stage('Test') {
            steps { sh 'mvn test' }
        }
        stage('Deploy') {
            when { branch 'main' }
            steps { sh 'kubectl apply -f k8s/' }
        }
    }
}
```

## Webhook Flow
```
Developer pushes → Git webhook → CI server triggers → Pipeline runs
    → Status reported back to PR → Deploy if all green
```
