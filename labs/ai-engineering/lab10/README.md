# Lab 10: AI Deployment & CI/CD

## Learning Objectives
- Implement blue-green and canary deployment strategies
- Build traffic routing and weight-based release management
- Design rollback mechanisms for failed deployments
- Create a CI/CD pipeline for model versioning and deployment

## Concepts Covered
- **Blue-Green Deployment**: Running two environments with instant switch
- **Canary Releases**: Gradual traffic shifting to new versions
- **Rollback**: Rapid reversion to previous deployment
- **Traffic Routing**: Weight-based request distribution
- **CI/CD Pipeline**: Build, test, deploy stages for ML models

## Setup
```bash
cd lab10
javac src/com/aiengineering/lab10/AiDeploymentAndCiCdDemo.java
java com.aiengineering.lab10.AiDeploymentAndCiCdDemo
```

## Key Takeaways
- Canary releases limit blast radius of bad deployments
- Automated rollback is critical for production reliability
- CI/CD for ML requires model validation in the pipeline
