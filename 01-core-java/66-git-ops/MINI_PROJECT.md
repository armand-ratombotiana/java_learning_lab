# Module 66: GitOps & Declarative Infrastructure - Mini Project

**Project Name**: ArgoCD Automated Synchronization Pipeline  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Simulate a production-grade GitOps workflow. Set up ArgoCD in a local Kubernetes cluster, connect it to a GitHub repository containing Kubernetes YAML manifests, and observe automated synchronization and drift reconciliation.

## 📝 Requirements

### Core Features

1. **The Config Repository**:
   - Create a new, public GitHub repository named `gitops-config`.
   - Add a simple Kubernetes `Deployment` YAML for an Nginx web server.
   - Add a Kubernetes `Service` YAML exposing the deployment.

2. **Cluster Setup (Minikube)**:
   - Start a local Kubernetes cluster (e.g., Minikube or Docker Desktop).
   - Install ArgoCD into the cluster using their official installation manifests.
   - Forward the ArgoCD API server port so you can access the UI on `localhost:8080`.
   - Log into the ArgoCD UI using the default `admin` credentials (extract the auto-generated password from the K8s Secret).

3. **Application Registration**:
   - In the ArgoCD UI (or via the ArgoCD CLI), create a new "Application".
   - Point the Source to your `gitops-config` GitHub repository URL.
   - Point the Destination to your local cluster (`https://kubernetes.default.svc`) and the `default` namespace.
   - Enable "Auto-Sync" and "Self-Heal".

4. **The GitOps Experiment**:
   - Commit and push a change to your GitHub repo (e.g., scale the `Deployment` replicas from 1 to 3).
   - Watch the ArgoCD UI. It should detect the change, mark the app as "OutOfSync", and automatically apply the new YAML, bringing the replicas to 3.

5. **The Drift Reconciliation Experiment**:
   - Open your terminal and manually sabotage the cluster: run `kubectl scale deployment nginx --replicas=0`.
   - Watch the ArgoCD UI. Because "Self-Heal" is enabled, ArgoCD will detect the configuration drift and immediately overwrite your manual command, restoring the replicas back to 3.

---

## 💡 Solution Blueprint

1. **ArgoCD Installation (CLI)**:
   ```bash
   kubectl create namespace argocd
   kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
   kubectl port-forward svc/argocd-server -n argocd 8080:443
   ```

2. **ArgoCD Application Definition (`app.yaml`)**:
   Instead of using the UI, you can define the ArgoCD App declaratively in Kubernetes.
   ```yaml
   apiVersion: argoproj.io/v1alpha1
   kind: Application
   metadata:
     name: my-nginx-app
     namespace: argocd
   spec:
     project: default
     source:
       repoURL: 'https://github.com/yourusername/gitops-config.git'
       targetRevision: HEAD
       path: .
     destination:
       server: 'https://kubernetes.default.svc'
       namespace: default
     syncPolicy:
       automated:
         prune: true
         selfHeal: true
   ```