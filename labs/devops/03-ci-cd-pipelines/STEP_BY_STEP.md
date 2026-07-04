# Step-by-Step CI/CD Guide

## 1. Create GitHub Actions Workflow
```powershell
mkdir -Path .github/workflows -Force
# Create .github/workflows/ci.yml
```

## 2. Add Build Stage
```yaml
- run: npm ci
- run: npm run build
```

## 3. Add Test Stage
```yaml
- run: npm test
```

## 4. Add Docker Build
```yaml
- name: Build and push Docker
  uses: docker/build-push-action@v5
  with:
    push: true
    tags: myapp:latest
```

## 5. Add Deployment Stage
```yaml
- name: Deploy to Kubernetes
  run: kubectl apply -f k8s/
```

## 6. Set Up Jenkins (Alternative)
```powershell
# Run Jenkins in Docker
docker run -p 8080:8080 -p 50000:50000 jenkins/jenkins:lts
```

## 7. Create Jenkins Pipeline
```groovy
// Jenkinsfile in repository root
pipeline {
    agent any
    stages {
        stage('Build') { steps { sh 'echo Building...' } }
        stage('Test') { steps { sh 'echo Testing...' } }
        stage('Deploy') { steps { sh 'echo Deploying...' } }
    }
}
```

## 8. Test the Pipeline
```powershell
git add . && git commit -m "Add CI/CD pipeline"
git push
# Check Actions tab or Jenkins dashboard
```
