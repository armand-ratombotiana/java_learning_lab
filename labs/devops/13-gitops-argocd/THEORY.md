# GitOps & ArgoCD Theory

## GitOps Principles

### 1. Declarative Configuration
The entire system must be described declaratively. Infrastructure as Code (IaC) tools like Kubernetes YAML, Helm charts, Kustomize overlays, and Terraform are used to define the desired state of the system. Every aspect of the infrastructure — from networking and storage to application configuration and secrets — is codified in version-controlled files. This declarative approach ensures that the system can be reproduced, audited, and reviewed through pull requests.

### 2. Version Controlled and Immutable
Git serves as the single source of truth for both application code and infrastructure configuration. All changes to the system must go through Git, which provides a complete audit trail of who changed what and when. The immutable nature of Git history ensures that every state of the infrastructure is recorded and recoverable. Branches can represent different environments (dev, staging, production), and tags mark specific releases.

### 3. Automated Sync
A GitOps operator continuously reconciles the actual cluster state with the desired state defined in Git. The operator polls the Git repository at configurable intervals and applies any differences it detects. This sync can be automated (auto-sync) or triggered manually depending on the operational requirements. Automated sync ensures that the cluster always reflects the configuration stored in Git.

### 4. Pull-Based Deployment
Unlike traditional CI/CD systems that push changes to the target environment, GitOps uses a pull-based model. The GitOps operator running inside the cluster pulls the desired state from Git and applies it. This model has several advantages: it does not require the CI system to have direct access to the cluster, it works across network boundaries, and it provides better security by eliminating the need for long-lived cluster credentials in external systems.

### 5. Self-Healing
The GitOps operator automatically detects and corrects configuration drift. If someone manually modifies a resource in the cluster using kubectl, the operator will revert that change to match the desired state in Git. This self-healing capability ensures that the cluster remains consistent with the version-controlled configuration and prevents configuration drift that can lead to production incidents.

### 6. Observable State
The GitOps operator provides visibility into the state of the cluster. It reports sync status (synced, out of sync, syncing, failed), health status (healthy, degraded, progressing, suspended), and provides a diff view showing what has drifted. This observability enables operators to understand the current state of the system and take corrective action when needed.

## ArgoCD Architecture

### Core Components

**API Server**: The API server serves the ArgoCD REST API and gRPC interface. It handles authentication, authorization, and all CRUD operations for ArgoCD resources (Applications, ApplicationSets, Projects, Repositories, Clusters). The API server also serves the ArgoCD web UI and supports SSO integration with OIDC providers like Dex, Keycloak, or GitHub.

**Repository Server**: The repository server is responsible for generating Kubernetes manifests from various configuration management tools. It clones Git repositories, processes Helm charts, runs Kustomize overlays, and evaluates Jsonnet templates. The repository server caches repository data to improve performance and reduce load on Git servers. It also handles webhook events from GitHub, GitLab, and Bitbucket to trigger syncs.

**Application Controller**: The application controller is the heart of ArgoCD. It continuously monitors running applications, compares the live state against the desired state specified in the Git repository, and detects OutOfSync state. It takes corrective action for applications configured with auto-sync and self-heal. The controller also invoke resource hooks (PreSync, Sync, PostSync) and manages sync waves.

**Redis Server**: Redis is used as a cache for the ArgoCD components. It stores application state, repository data, and session information. Redis improves performance by reducing the number of API calls to Git repositories and Kubernetes API servers.

**Dex / SSO Provider**: ArgoCD integrates with Dex for authentication. Dex acts as an identity provider proxy that can connect to various backends including LDAP, Active Directory, SAML, GitHub, Google, and OIDC providers. This enables organizations to use their existing identity infrastructure for ArgoCD access control.

### Application Model

An ArgoCD Application represents a deployed instance of a set of Kubernetes manifests. Each Application has:

- **Source**: The Git repository URL, target revision (branch, tag, commit), and path within the repository
- **Destination**: The target Kubernetes cluster and namespace where manifests will be applied
- **Sync Policy**: Controls whether sync is automatic or manual, whether resources are pruned, and whether self-healing is enabled
- **Sync Options**: Advanced options like CreateNamespace, PruneLast, RespectIgnoreDifferences, and SkipDryRunOnMissingResource
- **Health Assessment**: Custom health checks defined in resource customizations

### Project Model

ArgoCD Projects provide logical grouping of Applications and RBAC boundaries. Each Project defines:
- **Source Repositories**: Which Git repositories are allowed
- **Destinations**: Which clusters and namespaces are permitted
- **Roles**: JWT tokens for CI systems with specific permissions
- **Sync Windows**: Time-based restrictions on when syncs can occur
- **Namespace Resource Quotas**: Limits on resources within project namespaces
- **Orphaned Resources**: Monitoring for resources not managed by ArgoCD

## Sync Strategies

### Manual Sync
The operator must manually trigger a sync through the UI, CLI, or API. Manual sync provides full control over when changes are applied.

### Automated Sync with Prune
The operator automatically syncs when it detects differences between Git and the cluster. With prune enabled, resources that exist in the cluster but not in Git are automatically removed. This ensures the cluster exactly matches what is defined in Git.

### Automated Sync without Prune
The operator automatically syncs but does not remove resources that are not in Git. This is useful for environments where some resources are managed outside of GitOps.

### Sync Waves
Resources within an Application can be assigned to sync waves (negative and positive integers). Resources in lower-numbered waves are applied first. This allows managing dependencies, such as creating a namespace before deploying resources into it.

## ApplicationSet

ApplicationSet is a Kubernetes controller that generates ArgoCD Applications from a template. It supports multiple generators:

- **List Generator**: Iterates over a list of key-value pairs
- **Cluster Generator**: Iterates over clusters registered in ArgoCD
- **Git Generator**: Generates applications from directories or files in a Git repository
- **Matrix Generator**: Combines outputs from multiple generators
- **Merge Generator**: Merges parameters from multiple generators
- **SCM Provider Generator**: Uses SCM providers (GitHub, GitLab, Bitbucket) to discover repositories
- **Pull Request Generator**: Creates applications for each pull request

## Multi-Cluster Management

ArgoCD can manage multiple Kubernetes clusters from a single instance. Clusters are registered by providing the cluster's API server URL and credentials. Applications can target any registered cluster, and ApplicationSets can generate applications for all clusters automatically.
