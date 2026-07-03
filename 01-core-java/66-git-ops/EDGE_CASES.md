# Module 66: GitOps - Edge Cases & Pitfalls

---

## Pitfall 1: Manual Cluster Modifications (Configuration Drift)

### ❌ Wrong
A developer notices a bug in production and quickly runs `kubectl edit deployment my-app` from their terminal to add an environment variable, intending to update the Git repository "later."

### ✅ Correct
In a GitOps environment, "Git is the single source of truth." If you edit the cluster manually, tools like ArgoCD will immediately detect Configuration Drift. Depending on your configuration, ArgoCD will ruthlessly overwrite the developer's manual changes and revert the cluster back to the state defined in Git. All changes must go through a Git Pull Request.

---

## Pitfall 2: CI and CD in the Same Repository

### ❌ Wrong
Storing the Kubernetes YAML manifests in the same repository as the Java source code. When the CI pipeline builds a new Docker image, it commits the new image tag back to the same repo, triggering another CI build in an infinite loop.

### ✅ Correct
Separate your repositories. Have one "App Repo" for the Java source code (CI). When the pipeline finishes, it pushes the image to a registry and commits the new image tag to a separate "Config Repo" (CD). ArgoCD monitors only the Config Repo.

---

## Pitfall 3: Plaintext Secrets in Git

### ❌ Wrong
Base64 encoding a Kubernetes Secret containing a database password and committing it directly to the Git repository. Base64 is not encryption; anyone who can read the repository can decode the password instantly.

### ✅ Correct
Use a tool like Bitnami Sealed Secrets. You encrypt the secret on your local machine using the cluster's public key. You commit the encrypted string (which is perfectly safe) to Git. The Sealed Secrets controller inside the cluster decrypts it.