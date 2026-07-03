# Module 66: GitOps - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is GitOps, and what are its core principles?
**Answer**:
GitOps is an operational framework that takes DevOps best practices used for application development (version control, collaboration, compliance, and CI/CD) and applies them to infrastructure automation.
**Core Principles**:
1. **Declarative**: The entire system must be described declaratively (e.g., using Kubernetes YAML or Terraform).
2. **Versioned and Immutable**: The desired state is stored in Git. This provides an undeniable audit trail and allows for instant rollbacks by simply reverting a commit.
3. **Pulled Automatically**: Software agents (like ArgoCD) pull the declarations from Git and apply them to the cluster.
4. **Continuously Reconciled**: Software agents continuously observe the cluster state and compare it to the Git state, automatically resolving any drift.

### Q2: Why is separating your CI repository (App Code) from your CD repository (Manifests) considered a best practice?
**Answer**:
If you keep your Kubernetes manifests in the same repository as your Java source code, every time your CI pipeline builds a new Docker image, it must commit the new image tag back to the repository to update the manifest. This Git commit triggers the CI pipeline *again*, creating an infinite build loop. 
Separating them ensures that the CI pipeline only runs when source code changes, and the GitOps controller only deploys when the config repository changes, ensuring a clean separation of concerns and preventing endless loops.

### Q3: How do you handle database passwords or API keys in a GitOps workflow where all configuration must be committed to Git?
**Answer**:
You can never commit plaintext secrets to Git, even if the repository is private. There are two primary solutions:
1. **Sealed Secrets / SOPS**: You encrypt the secret locally using strong asymmetric encryption. You commit the resulting ciphertext to Git. Inside the Kubernetes cluster, a decryption controller uses the private key (which never leaves the cluster) to convert the ciphertext back into a usable Kubernetes Secret.
2. **External Secrets Operator**: The Git repository only stores a generic "pointer" or reference file. Inside the cluster, an operator reads this pointer, securely connects to an external Vault (like AWS Secrets Manager or HashiCorp Vault), fetches the real password, and injects it into the pod.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: The Rollback
**Problem**: An interviewer says, "We just deployed v2.0 of our microservice via Jenkins (Push CD), and it brought down production. In a traditional setup, the ops team is frantically searching through Jenkins logs and running manual `kubectl rollback` commands. If we were using a strict GitOps architecture (Pull CD), how would we handle this rollback?"

**Solution**:
In a GitOps architecture, the cluster state is a perfect mirror of the Git `main` branch. 
To roll back the outage, no developer or operations engineer needs to touch the cluster, execute `kubectl` commands, or log into a CI server.
The developer simply goes to GitHub and clicks **"Revert"** on the bad Pull Request that changed the image tag to `v2.0`, restoring it to `v1.9`.
As soon as the revert commit lands in the `main` branch, the GitOps controller (ArgoCD) instantly detects the change, calculates the diff, and forces the Kubernetes cluster to terminate the `v2.0` pods and spin up the `v1.9` pods, resolving the outage in seconds with a perfect audit trail.