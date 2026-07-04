# GitOps Exercises

## Exercise 1: Install ArgoCD
Install ArgoCD on a Kubernetes cluster. Access the UI via port-forward.

## Exercise 2: Deploy via ArgoCD
Create a Git repository with a simple nginx deployment. Create an ArgoCD Application pointing to it. Sync manually.

## Exercise 3: Auto-Sync
Enable auto-sync with prune and selfHeal. Make a manual change with kubectl and watch ArgoCD correct it.

## Exercise 4: Helm with ArgoCD
Create an ArgoCD Application that deploys a Helm chart from a Git repo or Helm repository.

## Exercise 5: ApplicationSet
Create an ApplicationSet that deploys to multiple clusters (or multiple namespaces).

## Exercise 6: Install Flux
Install Flux on a Kubernetes cluster using the Flux CLI.

## Exercise 7: Flux GitRepository
Create a GitRepository source and Kustomization. Deploy an application.

## Exercise 8: Flux HelmRelease
Create a HelmRepository source and HelmRelease. Deploy a Helm chart.

## Exercise 9: Webhook Setup
Configure GitHub webhooks for ArgoCD or Flux for instant sync.

## Exercise 10: GitOps CI Pipeline
Set up a GitHub Actions CI pipeline that builds an image and updates a GitOps config repo.
