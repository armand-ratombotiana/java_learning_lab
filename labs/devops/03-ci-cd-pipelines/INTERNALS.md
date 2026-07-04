# CI/CD Internals

## GitHub Actions Architecture
- **Workflow**: YAML file in `.github/workflows/` defining automation.
- **Runner**: Environment that executes jobs (GitHub-hosted Linux/Windows/macOS or self-hosted).
- **Job**: Set of steps running on the same runner (sequential).
- **Step**: Individual task (shell command or action).
- **Action**: Reusable unit of code (Docker container or JavaScript).
- **Event**: Trigger for workflow (push, PR, schedule, webhook).

## Jenkins Architecture
- **Master (Controller)**: Web UI, API, job scheduling, build queue.
- **Agent (Worker)**: Executes build jobs (JNLP/SSH connection).
- **Pipeline**: Series of stages defined in Jenkinsfile (Declarative or Scripted).
- **Plugin**: Extends Jenkins functionality (500+ plugins available).

## Pipeline Execution Model
```
Source → Build → Test → Package → Publish → Deploy
  ↑                                           ↓
  └─────────── Feedback loop (pass/fail) ──────┘
```

## Artifact Storage
- Docker images: Container registry (ECR, GCR, Docker Hub)
- JAR/WAR: Artifact repository (Nexus, Artifactory)
- npm packages: Package registry (npm, GitHub Packages)
