# CI/CD Exercises

## Exercise 1: Basic GitHub Actions
Create a workflow that runs on push, checks out code, and runs `echo "Hello CI"`.

## Exercise 2: Build and Test
Add npm install and npm test to the workflow. Use caching for node_modules.

## Exercise 3: Matrix Build
Create a matrix strategy testing Node.js 18 and 20.

## Exercise 4: Docker Build
Add Docker image build and push to GitHub Container Registry.

## Exercise 5: Deployment
Add a deploy stage that runs kubectl apply (simulate with echo).

## Exercise 6: Conditional Stages
Add an approval gate for production deployments using environments.

## Exercise 7: Jenkins Pipeline
Write a Jenkinsfile with build, test, and deploy stages using declarative syntax.

## Exercise 8: Security Scanning
Integrate a SAST tool (like CodeQL) into the pipeline.

## Exercise 9: Pipeline Optimization
Refactor a slow pipeline to use caching and parallel jobs. Measure before/after.
