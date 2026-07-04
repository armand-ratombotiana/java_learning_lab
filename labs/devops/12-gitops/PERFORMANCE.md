# GitOps Performance

## Sync Performance
- **Poll interval**: Default 3m (ArgoCD), 1m (Flux). Shorter = faster detection, more load.
- **Webhook**: GitHub/GitLab webhooks trigger immediate sync (reduces delay to <10s).
- **Large repos**: Cloning large repos takes time; use shallow clones or split repos.
- **Number of apps**: ArgoCD handles 1000+ apps per instance; Flux similarly scalable.

## Optimization Tips
- **Repo splitting**: One repo per team/service reduces clone size and sync time.
- **Helm chart caching**: Cache Helm index for faster source updates.
- **Kustomize remote bases**: Use HTTPS for remote Kustomize bases (avoid git clone).
- **Resource exclusion**: Exclude unnecessary resources from health checks.
- **Sync waves**: Order resource creation to avoid dependency errors.

## ArgoCD Performance
- **API Server**: Stateless; scale horizontally.
- **Application Controller**: Stateful; split via sharding for large deployments.
- **Repo Server**: Stateless; cache Git data in Redis.
- **Redis**: Cache repo data; configure max memory and eviction policy.

## Flux Performance
- **Source Controller**: Stateless; uses shared informers.
- **Reconciliation**: Smarter reconciliation filters reduce unnecessary operations.
- **OCIRepository**: OCI-based artifact storage (faster than Git clone).

## Large-Scale Considerations
- 500+ apps: Consider ArgoCD sharding or Flux multi-instance.
- 5000+ resources: Monitor controller memory and API server load.
- Multi-cluster: Use ApplicationSet (ArgoCD) or Flux per cluster.
- Network latency: Webhook + pull interval tuning for cross-region deployments.
