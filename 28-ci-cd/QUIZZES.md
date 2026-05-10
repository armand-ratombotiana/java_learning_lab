# CI/CD Quizzes

## Quiz 1: GitHub Actions Basics

**Question 1**: What is the correct syntax to trigger a workflow on push to main branch?
- A) on: push: main
- B) on: [push: main]
- C) on: push: branches: [main]
- D) on: main

**Answer**: C

---

**Question 2**: Which action is used to checkout code in GitHub Actions?
- A) actions/checkout@v4
- B) actions/git@v4
- C) github/checkout@v4
- D) uses: checkout@v4

**Answer**: A

---

**Question 3**: How do you define environment variables in GitHub Actions?
- A) env: VAR: value
- B) environment: VAR: value
- C) env: {VAR: value}
- D) set_env: VAR=value

**Answer**: C

---

**Question 4**: What is a matrix strategy used for?
- A) Running jobs in sequence
- B) Running jobs with different configurations
- C) Creating job dependencies
- D) Defining job resources

**Answer**: B

---

**Question 5**: How do you pass secrets to a GitHub Actions workflow?
- A) secrets: SECRET_NAME
- B) env: ${{ secrets.SECRET_NAME }}
- C) ${{ secrets.SECRET_NAME }}
- D) using: secrets

**Answer**: B

---

## Quiz 2: Jenkins Pipeline

**Question 1**: What are the two types of Jenkins pipelines?
- A) Simple and Complex
- B) Declarative and Scripted
- C) Basic and Advanced
- D) Linear and Parallel

**Answer**: B

---

**Question 2**: Which directive defines the agent in a declarative pipeline?
- A) agent: any
- B) agent { any }
- C) agent: 'any'
- D) runs-on: any

**Answer**: A

---

**Question 3**: How do you define environment variables in Jenkins pipeline?
- A) env: VAR = 'value'
- B) environment { VAR = 'value' }
- C) vars: VAR = 'value'
- D) set: VAR = 'value'

**Answer**: B

---

**Question 4**: Which post block condition runs after failure?
- A) always
- B) failure
- C) success
- D) unstable

**Answer**: B

---

**Question 5**: How do you archive test results in Jenkins?
- A) archiveArtifacts
- B) saveArtifacts
- C) keepArtifacts
- D) storeArtifacts

**Answer**: A

---

## Quiz 3: Docker in CI/CD

**Question 1**: What is the benefit of multi-stage Docker builds?
- A) Faster execution
- B) Smaller final image
- C) Better caching
- D) More secure

**Answer**: B

---

**Question 2**: Which instruction copies files between stages in multi-stage build?
- A) COPY --from
- B) FROM --copy
- C) STAGE COPY
- D) COPY STAGE

**Answer**: A

---

**Question 3**: How do you tag an image during build?
- A) docker tag image:tag
- B) docker build -t image:tag
- C) docker build --tag image:tag
- D) docker build image:tag

**Answer**: B

---

**Question 4**: What is the purpose of .dockerignore file?
- A) Exclude images from build
- B) Exclude files from build context
- C) Ignore build errors
- D) Default ignore rules

**Answer**: B

---

**Question 5**: How do you run a container and remove it after?
- A) docker run --remove
- B) docker run --rm
- C) docker run -d --delete
- D) docker run -r

**Answer**: B

---

## Quiz 4: Deployment Strategies

**Question 1**: What is blue-green deployment?
- A) Deploying to two regions simultaneously
- B) Running two versions side by side
- C) Gradually shifting traffic between versions
- D) Rolling back to previous version

**Answer**: B

---

**Question 2**: What is canary deployment?
- A) Deploying to all users at once
- B) Deploying to a small subset first
- C) Deploying with feature flags
- D) Deploying to staging first

**Answer**: B

---

**Question 3**: Which strategy has zero downtime?
- A) Recreate deployment
- B) Rolling deployment
- C) Blue-green deployment
- D) All of the above

**Answer**: C

---

**Question 4**: How do you implement rolling updates in Kubernetes?
- A) Recreate strategy
- B) RollingUpdate strategy
- C) Blue-green strategy
- D) Canary strategy

**Answer**: B

---

**Question 5**: What is the purpose of health check in deployment?
- A) Monitor performance
- B) Verify application is ready
- C) Check resource usage
- D) Log errors

**Answer**: B

---

## Quiz 5: Best Practices

**Question 1**: Which is NOT a recommended CI/CD best practice?
- A) Fail fast on tests
- B) Use semantic versioning
- C) Commit secrets to repository
- D) Use environment variables

**Answer**: C

---

**Question 2**: What is the purpose of caching in CI/CD?
- A) Store build outputs
- B) Speed up builds
- C) Reduce costs
- D) Improve security

**Answer**: B

---

**Question 3**: When should you use parallel execution in pipelines?
- A) Always
- B) When jobs are independent
- C) Only for tests
- D) Never

**Answer**: B

---

**Question 4**: What is the recommended approach for secrets?
- A) Store in repository
- B) Use environment variables directly
- C) Use secrets management tools
- D) Hardcode in scripts

**Answer**: C

---

**Question 5**: How often should you run pipeline locally?
- A) Never
- B) Only on failures
- C) Regularly before push
- D) Once a week

**Answer**: C

---

## Answer Key

| Quiz | Q1 | Q2 | Q3 | Q4 | Q5 |
|------|-----|-----|-----|-----|-----|
| 1    | C  | A  | C  | B  | B  |
| 2    | B  | A  | B  | B  | A  |
| 3    | B  | A  | B  | B  | B  |
| 4    | B  | B  | C  | B  | B  |
| 5    | C  | B  | B  | C  | C  |