# Module 66: GitOps & Declarative Infrastructure - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-65 (especially Cloud/DevOps and Kubernetes)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to GitOps](#intro)
2. [Push vs Pull Deployments](#push-pull)
3. [ArgoCD & Flux (GitOps Controllers)](#tools)
4. [Declarative Infrastructure (Kubernetes & Helm)](#declarative)
5. [Managing Secrets in GitOps](#secrets)

---

## 1. Introduction to GitOps <a name="intro"></a>
GitOps is a set of practices that uses Git as the single source of truth for declarative infrastructure and applications. Instead of running `kubectl apply` manually, developers merge a YAML change into the `main` branch, and the infrastructure automatically synchronizes itself to match the state of the Git repository.

---

## 2. Push vs Pull Deployments <a name="push-pull"></a>
- **Push-based (Traditional CI/CD)**: A CI tool (like Jenkins or GitHub Actions) builds the code and then pushes the new Docker image directly into the Kubernetes cluster. *Problem*: The CI server requires admin credentials to the production cluster, creating a massive security vulnerability.
- **Pull-based (GitOps)**: The CI tool simply builds the image and updates the Git repository. A GitOps Controller (living *inside* the cluster) constantly polls the Git repository. When it detects a change, it pulls the new configuration and applies it. The cluster doesn't expose its credentials to the outside world.

---

## 3. ArgoCD & Flux (GitOps Controllers) <a name="tools"></a>
ArgoCD and Flux are the industry standards for Kubernetes GitOps.
- They continuously monitor the target Git repository.
- If the live cluster state deviates from the Git state (e.g., someone manually deleted a pod), the controller detects "Configuration Drift" and immediately restores the cluster to match Git.

---

## 4. Declarative Infrastructure (Kubernetes & Helm) <a name="declarative"></a>
GitOps requires the infrastructure to be declarative, not imperative. You don't script *how* to deploy (e.g., "start container A, wait 5 seconds, start container B"). Instead, you declare the desired state (e.g., "I want 3 replicas of Container A") using YAML or Helm charts, and the system figures out how to achieve it.

---

## 5. Managing Secrets in GitOps <a name="secrets"></a>
You cannot store raw database passwords in a public or even private Git repository.
- **Sealed Secrets**: Encrypt secrets locally using a public key. Store the encrypted string in Git. A controller inside Kubernetes uses the private key to decrypt it into a native Kubernetes Secret.
- **External Secret Operators**: Store secrets in AWS Secrets Manager or HashiCorp Vault. The GitOps repo only contains a reference mapping, and the operator fetches the actual secret at runtime.